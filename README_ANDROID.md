# Mầm Xanh Android

Native Android application for the Mầm Xanh (MamXanh) Daily Quest & Living Tree Garden proposal.

## Current implementation

- Kotlin, Jetpack Compose, and Material 3.
- MVVM-style state holder using AndroidX ViewModel and StateFlow.
- Offline local quest state stored with SharedPreferences.
- Daily quest categories, completion rewards, a 150-coin daily earning cap, and Daily Clear bonus.
- Initial Quests, Garden, and Store navigation.

## Open in Android Studio

Open this repository as a Gradle project and select the `:app` run configuration.

Requirements: JDK 17, Android SDK 36, Android Gradle Plugin 8.13.2, Kotlin 2.2.20.

## Next milestones

1. Persist garden ownership, tree height, growth timestamps, and inventory.
2. Implement seed selection and purchases.
3. Implement growth thresholds (10 cm / 50 cm / 120 cm), daily growth cap, and slower post-maturity growth.
4. Add timed, hold, and time-window quest confirmations.
5. Add seasonal visuals, reminders, and statistics.

All user data is intended to remain on-device. No account or backend is planned for the initial product.