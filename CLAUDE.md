# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Roguelette is a roguelike roulette builder game built with libGDX (Java). Players pick a character,
start with ~$100 and build to $1,000,000 across 8 stages by playing roulette while buying items that
customize the wheel, add passive effects, or grant one-time abilities. Bosses gate stages 2/4/6/8;
an opt-in "Casino Curses" difficulty ladder (8 levels) provides the roguelite climb, backed by a
persistent player profile.

## Build & Run Commands

```bash
# Run the game
./gradlew lwjgl3:run

# Build executable JAR (output: lwjgl3/build/libs/Roguelette-1.0.0.jar)
./gradlew lwjgl3:jar

# Platform-specific JARs (smaller size)
./gradlew lwjgl3:jarWin     # Windows
./gradlew lwjgl3:jarMac     # macOS
./gradlew lwjgl3:jarLinux   # Linux

# Build with bundled JDK (using construo plugin)
./gradlew construo:buildWinX64
./gradlew construo:buildMacM1
./gradlew construo:buildMacX64
./gradlew construo:buildLinuxX64

# Run tests
./gradlew test

# Clean build
./gradlew clean
```

## Architecture

### Module Structure
- `core/` - Platform-independent game logic (all gameplay code lives here)
- `lwjgl3/` - Desktop platform launcher using LWJGL3 backend
- `assets/` - Game resources (music, icons, logos, character portraits)

### Key Packages (in `core/src/main/java/de/mario/roguelette/`)

| Package | Purpose |
|---------|---------|
| `screens/` | libGDX Screens (MainMenu, CharacterSelect, Game, GameOver, YouWin) |
| `wheel/` | Wheel and Segment classes (incl. `DevilSegment`) with effect system |
| `betting/` | Bet types and betting logic (BetType implementations define payouts) |
| `items/` | Shop system with three item categories + `LegendaryPool` (boss rewards) |
| `events/` | Game event layer: `GameEventListener` hooks + `LandingContext`/`BetResolution`/`SpinContext` |
| `balls/` | `Ball` — a ball in a spin (tint + per-ball `payoutFactor`) |
| `characters/` | Run-start characters (Balatro-style; passive listeners + signature ball) |
| `boss/` | Boss encounters gating stages 2/4/6/8 (debuff listeners + wheel mutation) |
| `curses/` | Casino Curses: `Curse` + the 8-level `CurseLevels` ladder |
| `config/` | `RunConfig` — every run knob (curve, prices, rounds, budgets), built at run start |
| `profile/` | Persistent player profile (`ProfileManager`, `~/.roguelette/profile.json`) |
| `render/` | All rendering components implementing `Renderable` interface |
| `animator/` | Wheel and ball animation logic |
| `util/` | Managers (BetManager, MusicManager, PendingChanceManager, FontManager) |

### Core Classes

- **RougeletteGame** - Main Game class; owns screens, music, fonts and the `ProfileManager`
- **GameState** - State stack (DEFAULT, SPINNING, DELETE/CHANCE_SEGMENT_SELECTING, SHOW_CRYSTAL_BALL,
  SHOP_OPEN, BOSS_INTRO/FIGHT/REWARD) + progression, event dispatch, boss encounters, curse wiring
- **RunConfig** - all run tunables in one instance (stage targets/rounds, price curve, shop knobs,
  starting balance). Built via `RunConfig.baseline(character)` in `GameScreen.show()`; curses modify
  it via setters before the run is constructed. **The balance table lives here.**
- **GameScreen** - Main gameplay orchestration; assembles the whole run in `show()`
- **Wheel/Segment** - Wheel contains Segments; `NumberSegment` (0-36), Joker segments, `DevilSegment`
  (never wins, never removable). `Segment.isUnremovable()`/`isRecolorable()` must be honoured at
  every mutation point (Segment Remover, boss destroy effect, recolour fortunes)
- **Player** - Balance (long), inventory, hand, character
- **Shop/Inventory** - Item management; 5 fortune slots, 5 chance slots (Deep Pockets adds more —
  `Inventory.getChanceMaxSize()` is computed live)
- **ProfileManager/Profile** - versioned JSON persistence: run stats, per-character wins and
  `highestCurseBeaten` (gates curse-level selection). All persistence goes through here.

### Item Hierarchy

```
ShopItem (abstract)          # getCost(Player) applies player-side discounts (Bargain Hunter)
├── FortuneShopItem - Passive effects (permanent during run)
├── ChanceShopItem - Consumable one-time effects → PendingChanceShopItem (active tracking)
└── SegmentShopItem - Wheel modifications (add/delete segments)
```

Rarity (COMMON/UNCOMMON/RARE) drives weighted shop draws + a min-stage gate; rarity is assigned
centrally in `RandomItemGenerator`. LEGENDARY items live only in `LegendaryPool` (boss rewards,
pick 1 of 3). Duplicates are allowed; snowbally fortunes stack **sub-linearly** via the primary-copy
pattern (`Inventory.countFortunes` + `isPrimaryFortune`; see PaintItBlack/ScarletSurge/PhoenixFeather).

### Rendering Pattern

All visual components implement `Renderable` interface with:
- `render()` - Draw the component
- `contains(x, y)` - Hit detection for mouse interaction

### Event Layer

Persistent game objects implement `GameEventListener` (package `events/`). `GameState` aggregates
them in order — character passives, **curse listeners**, fortunes, active chances, boss debuffs —
and dispatches at well-defined points:
- `onSpinStart` / `onBallLanded(LandingContext)` — `LandingContext` is mutable, so listeners can
  override where a ball lands (Ricochet, Freeze, Magnet Ball). Landing is decided at selection time,
  before the spin animates; `onBallLanded` fires once per ball.
- `onPrepareSpin(SpinContext)` — the spin's ball list is assembled (Double Ball, Twin Ball add
  extras). Each ball lands and pays out independently.
- `onResolveBet(BetResolution)` — items contribute `addBase`/`multiplyTotal` (win) or `addRefund`
  (loss). Refunds can be vetoed order-independently (`suppressRefunds`, All or Nothing) or scaled
  (`multiplyRefund`, Frayed Nets curse). `Bet.getPayout` builds a `BetResolution` per ball.
- `onTurnChange` — per-turn upkeep (duration ticking, wheel mutation, interest, rent, ...).

### Payout Formula

```
win:  amount × (base_multiplier + Σ base adds) × segment_multiplier × Π total muls × ball_payout_factor
      (summed across balls; a winning bet never returns less than its stake)
loss: amount × min(1, Σ refund fractions × refund factor)   (0 if suppressed; counted once per spin)
```

### Progression & Casino Curses

8 stages, ~3.2×/stage: $150 → $500 → $1,500 → $5,000 → $16,000 → $55,000 → $200,000 → $1,000,000,
flat 4 rounds per stage — all driven by **`RunConfig`** (no longer hardcoded in GameState). Prices
scale with the target curve (`getPriceMultiplier` ≈ prev target / 100). Bosses gate stages 2/4/6/8
(gain-X%-in-3-spins fights; reward = 1-of-3 legendaries); the $1M win only triggers after the final
boss. Design intent: each stage ≈ "engineer one big hit" — a big-hit gambling climb, not a +EV grind
(see `.claude/tools/EconSim.java`, diagnostic only).

**Casino Curses** (opt-in, per-character unlock): 8 strictly additive levels — each level = previous
+ exactly one curse. Mains at levels 1/4/7 (Devil's Mark: a `DevilSegment` on the wheel; Devil's
Harvest: +1 per beaten boss; Borrowed Against Time: −1 round/stage); other levels add one random
tier-gated sub-curse (`CurseLevels.rollForLevel`). Curses act via `RunConfig` modifiers, event
listeners, and/or one-time run setup. Level select: UP/DOWN on the character-select screen.

## Dependencies

- libGDX 1.13.1
- gdx-controllers 2.2.3
- Gradle 8.14.1
- construo plugin 1.7.1 (bundled JDK packaging)

## Session Notes & Screenshots

Development session notes are in `.claude/notes/`; screenshots in `.claude/screenshots/`
(YYYY-MM-DD-HH-MM.png). Item icons are generated via PowerShell System.Drawing scripts in
`.claude/art/` (1024×1024, transparent background, glyph style).

**Latest sessions (2026-07-01, two arcs, all pushed):**
- `2026-07-01-items-wave1-session.md` — **Items Wave 1**: 7 fortunes + 6 chances + Twin Ball
  legendary + Crystal Ball buff (never lies). Pools now 13 fortunes / 13 chances / 5 legendaries.
  Infra: per-ball payout factor, refund suppression, central price-discount hook, free-restock flag,
  dynamic chance slots, borrowed-time round skip.
- `2026-07-01-casino-curses-session.md` — **Casino Curses M0–M3**: persistence layer, RunConfig
  extraction, the additive curse ladder + level select, Devil's Segment + unremovable/recolorable
  audit.

Earlier: `2026-06-28-balance-baseline-session.md` (balance baseline), `2026-06-27-boss-system-session.md`,
`2026-06-26-economy-balance-session.md`, `2026-06-22-character-select-session.md`,
`2026-06-21-freeze-chance-session.md`.

**Living design docs (`.claude/design/`, read before working on these features):**
`ascension-mode.md` (Casino Curses — M0–M3 shipped, M4/M5 open), `graphics-atmosphere.md`
(tech-demo → atmosphere overhaul, not started), `new-items.md` (Items Wave 1 — complete, watchlist
inside).

## Current State / Next Steps

### 0. Immediate: PLAYTEST (nothing since the 2026-06-28 baseline has been played!)
One clean run + one curse-level-1 run. Watchlists: Rent Collector stage-1 power ($37/round vs $150
goal), Phoenix Feather gate feel, Magnet Ball pull strength, Bargain Hunter −50% value,
Overcharge×Freeze combo, Twin Ball vs boss goals; curse level-1 feel, Impatient Bosses (3→2 spins),
ladder pacing.

### 1. Then, in rough order
1. **Curse polish**: in-run display of active curses (reuse boss-card style), more segment curses
   (multiplier decay, rebounce, leech, trap, phantom — `ascension-mode.md` §4.3/§5), M5 meta
   (framing/story, run summary). Seeded draws need a run-seed system first.
2. **More bosses** (roster is 4 for 4 slots — no variety): Freezer (disables non-joker segments),
   Shadow Double (shadow ball whose wins LOSE money, via `onPrepareSpin`). Boss portraits later.
3. **A 6th legendary** (pool is 5 with OFFER_SIZE 3; ideas parked in `new-items.md` §3).
4. **More items / Wave 2** when pools feel thin — remaining backlog: Ghost Ball, Heavy Ball
   (cheap `onBallLanded` ball-mods), segment-type items (deliberately deferred to curse waves),
   character-specific balls (per-ball payout bias infra EXISTS now — `Ball.payoutFactor`).
5. **Major graphics/atmosphere update** (`graphics-atmosphere.md`) — art direction first, then SFX,
   segment-selection highlight (real UX gap: Freeze/Remover have no on-wheel highlight), VFX, ball
   feel, transitions, boss portraits, HUD, layout. Expected multi-session; LAST.

### 2. Meta-progression (foundation now exists)
- **Shipped**: characters (Gambler/Count/Professor — `characters/`, backlog in memory
  `character-ball-ideas`), persistent profile, Casino Curses ladder with per-character unlocks.
- **Open**: achievements (profile is ready for it), item/character unlocks, cosmetic themes,
  game modes (daily challenge needs seeded runs; endless needs a big-number money type — `long`
  caps at ~9.2e18).
