# In-Room Dining — Android TV app (WebView wrapper)

Wraps https://alphanicholson.com/qrmenu/ as a native Android TV app
(Leanback launcher, fullscreen, D-pad navigation, screen never dims).

## Build the APK (one-time setup ~15 min, then 1 minute per build)

1. Install **Android Studio** (free): https://developer.android.com/studio
   Runs on macOS 12 Monterey. During first launch accept the default SDK install.
2. **File > Open…** and pick this `qrmenu-tv` folder. Wait for "Gradle sync finished".
   (If asked to upgrade Gradle/AGP, accept.)
3. **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
   Click **locate** in the notification → `app/build/outputs/apk/debug/app-debug.apk`.
   The debug APK installs fine on any TV via sideloading.

   For a signed release APK instead: **Build > Generate Signed Bundle / APK > APK**,
   create a keystore when prompted (keep it — you need the same one for updates).

## Install on the Android TV

Option A — USB stick: copy the APK, open it on the TV with a file manager
(e.g. "File Commander" / "X-plore" from the TV Play Store). Allow "Unknown sources"
when prompted (Settings > Security & restrictions / Apps > Unknown sources).

Option B — over Wi-Fi with "Send files to TV" (phone + TV app) or
`adb connect <tv-ip>` then `adb install app-debug.apk` (enable Developer options
> USB/Network debugging on the TV first).

## Customise

| What                 | Where                                                     |
|----------------------|-----------------------------------------------------------|
| URL                  | `START_URL` in `app/src/main/java/.../MainActivity.java`  |
| App name on TV       | `app/src/main/res/values/strings.xml`                     |
| TV home banner       | `app/src/main/res/drawable/tv_banner.png` (320×180)       |
| Icon                 | `app/src/main/res/mipmap-xxhdpi/ic_launcher.png`          |
| Package id / version | `app/build.gradle.kts` (bump `versionCode` for updates)   |

## Notes for the TV experience

- Remote arrows scroll the page and move focus between links; OK/Enter clicks.
  Make sure the web menu's clickable elements are real `<a>`/`<button>` so they
  receive D-pad focus (add `:focus` styles in the site CSS for a visible highlight).
- BACK goes back inside the menu; on the first page it exits to the TV home.
- If the site is unreachable the app shows a branded "temporarily unavailable"
  screen and retries every 15 s.
- `tel:` links do nothing on a TV (no dialer) — consider showing the number as text.
