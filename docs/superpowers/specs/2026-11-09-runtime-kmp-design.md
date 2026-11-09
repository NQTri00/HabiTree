# Runtime recovery and KMP migration

The requested end state is a responsive emulator app, completion of the existing MVI and simple-mics redesign plans, Koin dependency injection, Ktor networking, and a Kotlin Multiplatform project.

Preserve saved SQLite data, MVI intents/state/effects, game economy and all existing catalog behavior. The new stack request supersedes the old dependency-free constraint. Retain the previous six-screen/three-dialog acceptance scope and 90% visual target; do not declare similarity from a successful build.

Default migration target, pending optional user steering: share business/data code between Android and iOS, retaining the existing Android Compose UI. Shared code must be used by the Android app, not merely an unused scaffold. Platform persistence remains behind a common contract; move portable models/rules/repository orchestration to commonMain and provide platform adapters. Declare iOS device/simulator frameworks; Windows cannot verify Xcode linkage or iOS runtime and that limitation must remain explicit.

Use Koin for application-lifetime storage/repository/client ownership and lifecycle-owned ViewModels. Use a common Ktor client with Android and Darwin engines, bounded timeouts and cancellation. No backend URL/spec has been supplied: provide configurable networking without inventing remote endpoints or uploading local game data.

Runtime acceptance requires a preserved-data launch, navigation through all destinations and dialogs, no new ANR/crash, reachable controls on 360×640 dp and large text, and before/after evidence. No new automated test infrastructure per the existing user preference; use builds, previews and manual emulator checks. Commit and push verified checkpoints.
