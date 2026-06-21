# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Roguelette is a roguelike roulette builder game built with libGDX (Java). Players start with $100 and build to $1,000,000 by playing roulette while purchasing items to customize the wheel, add passive effects, or activate one-time abilities.

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
- `assets/` - Game resources (music, icons, logos)

### Key Packages (in `core/src/main/java/de/mario/roguelette/`)

| Package | Purpose |
|---------|---------|
| `screens/` | libGDX Screen implementations (MainMenu, Game, GameOver, YouWin) |
| `wheel/` | Wheel and Segment classes with effect system |
| `betting/` | Bet types and betting logic (BetType enum defines payouts) |
| `items/` | Shop system with three item categories |
| `events/` | Game event layer: `GameEventListener` hooks + `LandingContext`/`BetResolution`/`SpinContext` |
| `balls/` | `Ball` — a ball in a spin (tint now; future per-ball/player behaviour) |
| `render/` | All rendering components implementing `Renderable` interface |
| `animator/` | Wheel and ball animation logic |
| `util/` | Managers (BetManager, MusicManager, PendingChanceManager) |

### Core Classes

- **RougeletteGame** - Main Game class, manages screens and music
- **GameState** - State stack manager with states: DEFAULT, SPINNING, DELETE_SEGMENT_SELECTING, CHANCE_SEGMENT_SELECTING, SHOW_CRYSTAL_BALL, SHOP_OPEN
- **GameScreen** - Main gameplay orchestration
- **Wheel/Segment** - Wheel contains Segments; NumberSegment for 0-36, JokerSegments for custom positions
- **Player** - Balance, inventory, hand (current bets)
- **Shop/Inventory** - Item management with 5-slot inventory limit

### Item Hierarchy

```
ShopItem (abstract)
├── FortuneShopItem - Passive effects (permanent during run)
├── ChanceShopItem - Consumable one-time effects → PendingChanceShopItem (active tracking)
└── SegmentShopItem - Wheel modifications (add/delete segments)
```

### Rendering Pattern

All visual components implement `Renderable` interface with:
- `render()` - Draw the component
- `contains(x, y)` - Hit detection for mouse interaction

### Event Layer

Persistent game objects (owned `FortuneShopItem`s, active `PendingChanceShopItem`s) implement
`GameEventListener` (package `events/`). `GameState` aggregates them (fortunes first, then
chances) and dispatches at well-defined points instead of hard-wiring effects into payout/spin
code:
- `onSpinStart` / `onBallLanded(LandingContext)` — `LandingContext` is mutable, so items can
  override where the ball lands (Ricochet, Freeze, ball modifiers). The landing is decided at
  selection time, before the spin animates to it; `onBallLanded` fires once per ball.
- `onPrepareSpin(SpinContext)` — fired before landings are rolled while the spin's ball list is
  assembled. Seeded with the player's default ball (`Ball.defaultBall()`); listeners add more
  (Double Ball). Each ball lands and pays out independently. Multi-ball spins are driven by
  `GameScreen.startSpin()` + `WheelRenderer.spinBalls(...)`.
- `onResolveBet(BetResolution)` — items contribute `addBase`/`multiplyTotal` (win) or
  `addRefund` (loss). `Bet.getPayout(List<Segment>)` builds a `BetResolution` per ball and
  dispatches; winnings sum across balls, refunds count once.
- `onTurnChange` — per-turn upkeep (duration ticking, wheel mutation, interest, ...).

### Payout Formula

```
amount × (base_multiplier + Σ listener base adds) × segment_multiplier × Π listener total muls   (win)
amount × min(1, Σ listener refund fractions)                                                      (loss)
```

### Progression System

6 stages with escalating goals: $500 → $2,000 → $8,000 → $40,000 → $200,000 → $1,000,000
- Rounds per stage increase (3→5)
- Prices scale 10× per stage via `getPriceMultiplier()`

## Dependencies

- libGDX 1.13.1
- gdx-controllers 2.2.3
- Gradle 8.14.1
- construo plugin 1.7.1 (bundled JDK packaging)

## Session Notes & Screenshots

Development session notes are stored in `.claude/notes/` with detailed summaries of work done.
Screenshots documenting progress are in `.claude/screenshots/` with timestamp filenames (YYYY-MM-DD-HH-MM.png).

**Latest session:** `2026-06-21-freeze-chance-session.md` - Committed the event layer
(`00a65ef`), then added Freeze chance, three fortunes (Interest, Comeback Kid, Streak Bonus),
the **multi-ball spin system** + Double Ball, and visual polish (transparent icons, grey ball,
themed fortune borders). All committed (`b0d158e`) **and pushed**. Verified via a headless payout
test + a live two-ball spin (both temp harnesses removed). Earlier: the event-layer session
`2026-06-20-event-layer-and-chances-session.md` and graphics overhaul
`2026-06-20-graphics-polish-session.md`.

## Current State / Next Ideas

### 1. More Items

**Chances (single-use actives):**
- Reroll: refresh shop inventory — NOT DONE (needs a shop-refresh hook; renderer coupling)
- ✅ Insurance: refund bet on loss — DONE (`InsuranceChance`)
- ✅ Freeze: lock a segment as guaranteed landing — DONE (`FreezeChance`; WheelSelectChance + onBallLanded)
- ✅ Lucky Seven: landing on a 7 pays triple — DONE (`LuckySevenChance`)
- ✅ Ricochet: if ball lands on 0, bounce to random non-zero — DONE (`RicochetChance`)
- ✅ Double Ball: next spin plays with two balls, both pay out — DONE (`DoubleBallChance`)

**Fortunes (passives):**
- ✅ Interest: earn % of balance between rounds — DONE (`InterestFortune`, onTurnChange)
- ✅ Streak Bonus: consecutive winning spins increase multiplier — DONE (`StreakBonusFortune`, stateful)
- ✅ Comeback Kid: bonus multiplier when balance is low — DONE (`ComebackKidFortune`, scales off stage goal)
- Bargain Hunter: shop discount — NOT DONE
- Deep Pockets: extra inventory slots — NOT DONE

**Segments:**
- Multiplier segments (2x, 3x on landing)
- Wild segments (count as both red and black)
- Multi-number segments (cover 2-3 adjacent numbers)
- Trap segments (risk/reward - high multiplier but lose extra on miss)

**Ball Modifiers (multi-ball infra DONE — `balls/Ball`, `events/SpinContext`, `onPrepareSpin`):**
- ✅ Double Ball: two outcomes per spin, both pay out — DONE (`DoubleBallChance`)
- Magnetic Ball: bias toward certain colors — NOT DONE (cheap `onBallLanded` item)
- Ghost Ball: phases through first segment, lands on second — NOT DONE (`onBallLanded`)
- Heavy Ball: tends toward adjacent segments after bounce — NOT DONE (`onBallLanded`)
- Ball "player select" characters (default ball, red ball that pays more on red, ...): the spin
  is seeded with `Ball.defaultBall()` in `GameScreen.startSpin()` (TODO there); per-ball payout
  bias would hang off `balls/Ball`. NOT DONE.

### 2. Graphics Polish

**Completed (2026-06-20):**
- FreeType fonts (Montserrat Bold) via FontManager
- Background gradient (radial vignette)
- RoundedRects: shadows, gradients, highlights
- Wheel segments: radial gradients, purple for BOTH
- Multiplier badges: rounded pill shape, subtle colors
- Wheel: outer rim gradient, center hub gradient, ball shadow
- Betting cells: vertical gradients
- Chips: shadows, subtle center gradient
- Fortune items: shadows, gradients

**Remaining:**
- Fix wheel center hub / segment overlap issue
- Particle effects: win celebrations, ball trails
- Shaders: glow effects, screen transitions
- Animated items in shop/inventory
- Sound design (no sound effects currently)

### 3. Meta-Progression (Roguelite Elements)

**Characters (Balatro-style):**
- High Roller: starts $500, higher stage goals
- Lucky: starts with a random Fortune
- Collector: 7 inventory slots instead of 5
- Risk Taker: all payouts ±50% variance

**Unlocks:**
- New items unlocked by achievements
- Characters unlocked by completing runs
- Cosmetic wheel/chip themes

**Achievements:**
- Win on specific numbers (hit 17 three times)
- Reach balance milestones ($10k, $100k, $1M)
- Win X rounds in a row
- Complete run with specific item combos
- Complete run without using shop

**Game Modes:**
- Daily Challenge: seeded run, leaderboard
- Endless: no win condition, how far can you go?
- Challenge Runs: modifiers (no fortunes, double prices, etc.)
