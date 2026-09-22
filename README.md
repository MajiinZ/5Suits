
MajinZ, Connected








































































Readme · MD
<div align="center">
🃏 5 Suits
A five-suited rummy card game — one Kotlin Multiplatform engine, one Compose Multiplatform UI, three platforms.

Kotlin Compose Multiplatform Platforms License

Draw, build books and runs, and dodge deadwood across eleven escalating rounds — with a fifth suit, ★ Stars, and a wild rank that rotates every round.

</div>
🎴 About
5 Suits is a rummy variant played with a custom 116-card deck: the standard four suits plus a fifth, ★ Stars, each ranked 3 through King (two copies of every card) and six jokers.

🔺 Eleven rounds, growing hands — Round 1 deals 3 cards; each round adds one, up to 13 cards in round 11.
🃏 A rotating wild card — Jokers are always wild, and the rank matching the current round's hand size is wild too: 3s in round 1, 4s in round 2, and so on up to Kings.
📚 Books and runs — A book is three or more cards of equal rank in any suits; a run is three or more consecutive ranks in a single suit.
🚪 Go out automatically — As soon as every card in your hand fits a book or a run, you go out and everyone else gets one final turn.
🏆 Lowest score wins — Unused cards count against you: face value for number cards, 11/12/13 for Jack/Queen/King, 20 for a wild-rank card, and 50 for a joker. Lowest total across all eleven rounds takes the game.
The app supports 2–4 players, either against computer opponents or passing a single device around the table. All play is local and offline, with the game saved automatically so a match can be picked up later. ✋📱

✨ Features
🧠 Five-suit, 11-round rummy engine with automatic meld detection (books and runs) via an exact bitmask solver
🃏 Rotating wild rank and joker rules, scoring, and "going out" / final-turn logic
🤖 Computer opponents that draw and discard using the same meld solver as the player
💾 Local game persistence, so an in-progress match survives an app restart
🌗 Light and dark ("evening mode") themes, with a single white-label theme object for reskinning
📲 Shared UI and game logic across Android, iOS, and Desktop from one Compose Multiplatform codebase
🧱 Tech Stack
Layer	Technology
Shared logic	Kotlin Multiplatform — game engine, state, and persistence contracts across all targets
UI	Compose Multiplatform with Material 3 — shared UI for Android, Desktop, and iOS
DI	Koin
Async	Kotlinx Coroutines — game transitions and bot turns
Persistence	Kotlinx Serialization — JSON-encoded save state
State	AndroidX Lifecycle ViewModel (Compose Multiplatform artifact)
📁 Project Structure
5Suits/
├── androidApp/                # 🤖 Android application shell (manifest, launcher activity, icons)
├── composeApp/                # 📦 Shared Kotlin Multiplatform module
│   └── src/
│       ├── commonMain/        # 🧠 Shared game engine, UI, DI, and navigation
│       │   └── kotlin/com/fivesuits/app/
│       │       ├── game/          # GameEngine, MeldSolver, GameModels (deck, cards, state)
│       │       ├── presentation/  # GameViewModel and UI state
│       │       ├── data/          # GameRepository + LocalStore contract
│       │       ├── ui/            # Compose screens, components, and theme (WhiteLabel)
│       │       ├── navigation/    # Screen definitions
│       │       └── App.kt         # App entry point and screen composition
│       ├── androidMain/       # 🤖 Android-specific LocalStore (SharedPreferences)
│       ├── desktopMain/       # 🖥️ Desktop entry point (Main.kt) and file-based LocalStore
│       ├── iosMain/           # 🍎 iOS-specific LocalStore (NSUserDefaults) and view controller bridge
│       └── commonTest/        # ✅ Unit tests for the engine, meld solver, and serialization
├── iosApp/                    # 🍎 Xcode project wrapping the shared framework
├── gradle/                    # Version catalog and Gradle wrapper
└── build.gradle.kts, settings.gradle.kts
🚀 Getting Started
✅ Prerequisites
JDK 17 or newer
Android Studio (Koala or newer recommended) for Android development, or IntelliJ IDEA for Desktop-only work
Xcode 15+ and a Mac, for building the iOS app
Kotlin, Gradle, and the Android SDK are managed automatically through the Gradle wrapper and version catalog (gradle/libs.versions.toml)
📥 Clone the repository
bash
git clone https://github.com/MajiinZ/5Suits.git
cd 5Suits
🤖 Run on Android
Open the project in Android Studio and run the androidApp configuration on an emulator or device, or from the command line:

bash
./gradlew :androidApp:installDebug
🖥️ Run on Desktop
bash
./gradlew :composeApp:run
🍎 Run on iOS
Open iosApp/iosApp.xcodeproj in Xcode, select a simulator or device, and run. Xcode builds the shared composeApp Kotlin framework as part of the standard build.

🧪 Run the tests
The shared game engine, meld solver, and state serialization are covered by unit tests in commonTest:

bash
./gradlew :composeApp:allTests
⚙️ How It Works
The game is modeled as an immutable GameState that a single GameEngine transitions between (deal, draw, discard, go out, score, next round) — every transition returns a new state rather than mutating the old one, which keeps the engine easy to test and safe to persist mid-turn.

Meld detection is handled by MeldSolver, which searches every disjoint combination of books and runs in a hand using bitmask dynamic programming to find the grouping that minimizes leftover (deadwood) points — the same solver powers both the player's live hand analysis and the computer opponents' discard choices. 🧮

📄 License
No license has been specified for this project yet. All rights are reserved by the author unless a license file is added.

👤 Author
Built by MajiinZ 🛠️



