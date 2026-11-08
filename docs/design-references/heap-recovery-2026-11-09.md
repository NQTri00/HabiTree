# Studio heap recovery — 2026-11-09

## Observed state
- Current Studio PID: 9888; previous failing PID14068 is no longer the active instance.
- Machine physical RAM: 16,450,400 KB; free physical RAM approximately3.2GB during verification.
- Effective JVM maximum heap: 4,294,967,296 bytes (4GiB).
- Existing persisted user VM options: C:/Users/AP/AppData/Roaming/Google/AndroidStudio2026.2.2/studio64.exe.vmoptions contains -Xmx4096m.
- The distribution also supplies -Xmx2048m, but the later user -Xmx4096m takes precedence. VM.flags confirms the effective4GiB limit. No heap-setting edit was needed.
- Prior idea.log records repeated LowMemoryWatcher signals with gc overloaded=true and resource cache clearing; these are evidence of the earlier failure, not a claim of a current4GiB OOM.

## Recovery and verification
Used the bundled JDK jcmd GC.class_histogram to collect unreachable objects and inspect live allocations. Heap use decreased from3,696,230KB to2,343,529KB. Largest live allocation category was byte arrays (1,457,762,888bytes); this alone does not identify the retaining owner or prove a leak.

Studio inspection of GardenArtwork.kt completed in6.5s (formatting warnings only).
GardenCareActionPreview rendered successfully in16.3s.
MamXanhAppContentHomePreview rendered successfully in9.1s.
Heap after these renders:2,963,619KB of4,194,304KB, approximately1.17GiB headroom.

The current heap-pressure episode is recovered. This verifies current responsiveness under inspection and full-screen rendering; it does not prove there can be no long-session retention issue.

## Operating constraints
Keep4GiB on this16GiB machine. Do not increase to8GiB while only~3GiB physical RAM is free.
Render previews sequentially. Avoid large atlas renders and avoid dumping base64 image contents into conversation output.
If memory pressure recurs, collect current heap data and inspect retained owners before increasing memory again. Garbage collection is diagnostic/recovery, not proof of a permanent leak fix.
No IDE restart, cache deletion, Gradle setting change, or application-code modification was performed for this recovery.
