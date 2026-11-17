# UI design verification

For UI changes, compare fresh Android Studio Compose previews directly with the approved web design in `E:/Workspace/mamxanh-redesign` before declaring the work complete. If the layouts are visibly different, continue refining and rendering until they are similar. A successful build alone does not satisfy this requirement.

Render the real composables. Do not crop components out of a screen image or use emulator screenshots as substitutes for Compose previews.

Keep task files, downloads, build caches, and temporary files on E: or the existing tools under D:. Do not install tools or create temporary files on C:.

# Memory and responsiveness

Always monitor memory pressure and computer responsiveness during Android Studio, Compose preview, Gradle, Kotlin compiler, and emulator work. Check available RAM and task-owned process memory before starting heavy work, periodically during sustained work, and immediately if the computer starts lagging or Android Studio reports heap exhaustion. Treat lag as a reason to inspect memory pressure; do not assume every slowdown is a heap overflow.

When memory pressure causes lag or heap errors, pause additional heavy work and release resources promptly. Stop unnecessary task-owned emulators, preview/render jobs, browser instances, duplicate builds, and idle Gradle/Kotlin daemons. Run builds, emulator tests, and large preview batches sequentially when memory is constrained. Save work before gracefully closing or restarting a task-owned Android Studio instance to release its heap.

Only stop processes confirmed to belong to this task. Preserve the user's applications, active work, and unsaved changes; do not indiscriminately kill Java or Android Studio processes. Do not increase heap limits automatically: first remove unnecessary workloads and check available system memory. Resume heavy work only after memory and responsiveness recover, then rerun any interrupted required checks. Release unnecessary task-owned processes when the task is finished.

If RAM is healthy but the computer or tools remain slow, inspect disk activity and queue length before starting more work. E: has shown severe latency during concurrent Studio, emulator, Gradle, and artifact-copy operations. Keep these workloads sequential while storage is congested. Preserve existing caches and user AVD data; any temporary tool/cache relocation must stay under the permitted D:/Program Files location or E:, with originals retained and copied artifacts verified.

# GitHub checkpoints

Periodically commit and push coherent, validated code checkpoints during ongoing work. Upload the matching verified APK as a GitHub release asset and provide the code and APK links. Use the authorized working branch, preserve unrelated work, and describe any verification limits. Keep APK binaries out of Git history.
