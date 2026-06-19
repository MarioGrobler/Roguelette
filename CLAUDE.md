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

### Payout Formula

```
amount × (base_multiplier + chance_base) × segment_multiplier × fortune_total
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
