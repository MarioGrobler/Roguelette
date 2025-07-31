# Roguelette

A roguelike roulette builder.

**Disclaimer:** This is a fun project. I don't know whether this will ever be finished. 

This work is licensed under a [Creative Commons Attribution-NonCommercial 4.0 International License](https://creativecommons.org/licenses/by-nc/4.0/).
In short terms: if you use it please give appropriate credit and do not make money with this.

This is a [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).
This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.
Fore more information see below.

## Core Idea

A roguelike roulette builder. Start with 100\$ and play roulette to obtain 1000000\$. Cheat if necessary.
Play roulette to maximize your money. Replace numbers and colors on the roulette wheel, add new numbers or make them even more valuable. Play several rounds to customize the roulette wheel as you need it.

### The roulette wheel
Start with a classic (European) roulette wheel containing the numbers from 0 to 36. However, as the game progresses, you will modify the wheel. Change colors of existing numbers? No problem? Remove numbers as a whole? Why not. Add new numbers, potentially with inherent multipliers? Let's go. Add jokers? Sure.

### Consumable items
To make the fun even more fun, there are several items to turn the odds. Here are some ideas for consumable items (that is, items that are consumed upon use)
- color all numbers black for this round
- choose a number, replace every number in the same column with the chosen one
- chose a number, add a striking multiplier to the chosen number (and some adjacent ones)
- get a second ball
- fortune teller knows the next hit
- ...

### Permanent items
Permanent items are for the duration of a run with passive effects. Here are some ideas:
- the multiplier for black and even are x10 instead of x2
- every round a random number is chosen. if the ball lands on this number, double your money.
- get a random consumable every time a prime number is hit
- ...

### Different balls
This can be seen as a kind of character select. Every run with a special ball has a twist.
- Standard ball. No modifications.
- Black ball. Increase all winnings achieved by black numbers by 20%, reduce all winnings achieved by red by 10%
- Double Vision. Get a second ball, reduce all income by 45%
- ...

### Shop
Between rounds (depending on what a "round" is going to be) there is a shop where you can buy items are modify the roulette wheel, as described above

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
