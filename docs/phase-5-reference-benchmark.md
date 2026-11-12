# Phase 5 reference verification protocol

Use a separate .verification.phasefive package; never seed/reset the player's main profile. Record commit, debug/release variant, device model, API, physical/logical resolution, density and font scale.

1. Cold start: force-stop only the verification package, run adb shell am start -W, repeat five times. Record TotalTime and median. This is an Activity launch proxy, not proof of end-to-end Home interactivity. Target <=3 s on representative physical lower-end hardware requires additional measurement.
2. Completion: use real SQLite fixture with 1,000 occurrences and >=100 care events; time the transaction using monotonic System.nanoTime. Record emulator timings separately from UI update latency and production performance budgets.
3. Memory: warm Home/tree route, sample adb shell dumpsys meminfo, enter/leave tree 20 times with pauses for UI settlement, sample again. Compare heap after GC/idle; inspect trend, not one noisy total-PSS number. Any growing trend requires profiling before a release claim.
4. Scroll: full catalog/garden ranges and settings at normal and 130% font / 320dp width. Check no lost actions or clipped progress. Keep >=48dp touch targets.
5. Data: export fixture, inspect preview, cancel without changes, restore explicitly, recover safety, cancel and confirm clear only in QA profile. Reject corrupt/truncated files. Validate transaction rollback and snapshot idempotence in automated SQLite tests.
6. Accessibility: inspect semantic labels and disabled states; exercise TalkBack traversal and physical keyboard on release devices. Code inspection is not a completed screen-reader usability study.
7. Lifecycle/offline: activity recreation, background/foreground, force-stop/relaunch, denied notifications and airplane mode. Never use main profile for destructive QA.

Use full-content targeted Compose previews sequentially and stop Gradle daemons before rendering to avoid competing heaps. Emulator snapshots must not be represented as measurements across all phones.

## Recorded local evidence — 2026-11-12
Candidate: 0.2.0 / build 2, debug-derived verification package; Small_Phone AVD, Android 17/API 37, 720×1280 physical / density 320, font 1.0. QA profile contained one static succulent and no completed tasks; scale testing used a separate instrumented SQLite fixture.

Five repeat cold Activity launches (am start -W, force-stop before each): 2120, 1694, 2431, 1907, 1889 ms; median 1907 ms. These are launch proxies, not a measurement of Home interactivity. Initial launches immediately after APK installation took 12363 and 10385 ms; do not conceal these outliers or generalize the repeat median to fresh installs/physical hardware.
The 1,000-occurrence/100-care-event fixture measured the completion transaction at 4 ms and 1 ms in two test runs. This excludes Compose commit-to-visible latency.

Twenty tree/Home entry/exit cycles were checked after every input. All samples used the same process; no restart was used to hide growth. Values below are KB from dumpsys meminfo:

| Completed cycles | Total PSS | Java heap PSS | Native heap PSS |
|---:|---:|---:|---:|
| 0 | 178083 | 17372 | 67564 |
| 5 | 178178 | 16244 | 68416 |
| 10 | 195023 | 15704 | 85220 |
| 15 | 214961 | 17668 | 103172 |
| 20 | 186221 | 18004 | 73936 |

After idle, total PSS fell to 161685 KB, native heap PSS to 50144 KB, Java heap PSS to 17384 KB; one Activity and 12 Views remained. Mid-run allocation growth was reclaimed rather than continuing monotonically. This bounded run found no accumulating activities or sustained memory rise; it is not proof against all leaks or a physical-device frame-rate benchmark.

Runtime QA: actual SAF export to Downloads, reopen/validate summary (one tree), cancel, confirmed replacement, safety-copy availability, success counts and Back-to-Settings/Home all checked. With notifications revoked and airplane mode enabled, Home/statistics/Data remained usable at a 320dp window and font 1.3; two slow scrolls reached the last diagnostic action. Physical size, font 1.0 and airplane mode off were restored afterward.
Targeted full-content previews were rendered sequentially. The narrow 130% Vietnamese restore document needed a 1500dp preview to expose its final action, which ends around 1245dp; runtime remains naturally scrollable.

