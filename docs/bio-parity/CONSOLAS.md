# Consolas for the native app

The approved web reference remains unchanged. Its monospaced text uses Windows Consolas.

Android now has a resource-font integration ready for Consolas Regular and Bold. When both resources are present, the timer, dates, counters and eyebrow labels use that family, and the former fallback letter-spacing correction is removed. Without both files, the existing Android fallback remains active.

Microsoft's font-family page lists no standalone download. Its Windows font license does not grant app redistribution rights. The official vendor offers a separate App license for Android embedding:

- https://learn.microsoft.com/en-us/typography/font-list/consolas
- https://learn.microsoft.com/en-us/typography/fonts/font-faq
- https://www.myfonts.com/collections/consolas-font-microsoft-corporation

The app-licensed package has not been supplied or purchased in this session. No unlicensed font binaries have been added to the project.

Once that package is available locally, run:

```powershell
./docs/bio-parity/import-consolas.ps1 -FontDirectory 'D:/Program Files/AndroidCLI/state/font-packages/consolas-app'
```

The importer accepts the original consola.ttf/consolab.ttf names or Consolas-Regular.ttf/Consolas-Bold.ttf. It validates the family without installing it in Windows, copies the original font bytes to the Android font resources, verifies their hashes, and adds resource-shrinker keep rules. Then rebuild the APK and compare fresh Compose previews. All project writes remain in E:/Workspace.
