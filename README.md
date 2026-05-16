<div align="center">

# Etsume

**A manga reader and anime player for Android.**

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Build](https://github.com/harshtries-code/etsume/actions/workflows/build_push.yml/badge.svg)](https://github.com/harshtries-code/etsume/actions/workflows/build_push.yml)
[![Latest Release](https://img.shields.io/github/v/release/harshtries-code/etsume?include_prereleases&label=latest)](https://github.com/harshtries-code/etsume/releases)

</div>

---

## What is Etsume?

Etsume is an open-source Android application for reading manga and watching anime from extension-based sources you configure yourself.

It is built on a **Mihon / Aniyomi** derived base and is currently undergoing a full product, branding, and UX rewrite to become its own independent app.

> ⚠️ **Early Development** — Etsume is in active development. APIs, features, and UI surfaces may change significantly between releases.

---

## Extensions

Etsume does not come with content sources built in — you install them through **extension repos**.
Etsume is fully compatible with standard community extension repos. To get started:

1. Open Etsume → **More** → **Settings** → **Browse**
2. Tap **Manga Extension Repos** or **Anime Extension Repos**
3. Tap **Add** and paste a repo URL
4. Restart the app — new sources will appear in the Browse tab

### Known Extension Repos

> ⚠️ These are third-party community repos. Etsume does not host, control, or endorse any of them.

**Manga:**
- Keiyoushi
- Yūzōnō
- Kavita
- Suwayomi

**Anime:**
- Yūzōnō
- Secozzi
- Claudemirovsky
- hollow

You can find more repos and setup guides on community wikis. Always use repos from sources you trust.

---

## Features

- 📚 **Manga reader** — multiple viewer modes (left-to-right, right-to-left, vertical, webtoon)
- 🎬 **Anime player** — hardware-accelerated playback via mpv and FFmpeg
- 🔍 **Browse** — community extension sources for both manga and anime
- 📥 **Downloads** — offline reading and viewing support
- 🔖 **Library** — manage your collection with custom categories
- 💬 **Comments** *(coming soon)* — discussion surfaces per entry

---

## Roadmap

- 🖥️ **Desktop app** — coming soon
- 💬 **Comments** — in-app discussion surfaces per title
- 🎨 **Custom reader/player UI** — redesigned floating bars and controls
- 👤 **Profile & tracking redesign** — unified tracking dashboard

## Download

Get the latest APK from [**GitHub Releases**](https://github.com/harshtries-code/etsume/releases).

| Variant | Description |
|---------|-------------|
| `etsume-arm64-v8a-*.apk` | Most modern Android phones (recommended) |
| `etsume-armeabi-v7a-*.apk` | Older 32-bit ARM devices |
| `etsume-x86_64-*.apk` | x86_64 emulators / Chromebooks |
| `etsume-x86-*.apk` | Legacy x86 devices |
| `etsume-universal-*.apk` | All architectures in one file (larger size) |

**Minimum Android version:** Android 8.0 (API 26)

---

## Building from Source

### Requirements

- **Android Studio** Hedgehog (2023.1.1) or newer  
  *or* a working Android SDK + **JDK 17** environment
- A device or emulator running Android 8.0+

### Quick start

```powershell
# Debug build
.\gradlew.bat :app:assembleDebug

# Install debug build on a connected device
.\gradlew.bat :app:installDebug
```

Debug APK output:
- `app/build/outputs/apk/debug/app-arm64-v8a-debug.apk`
- `app/build/outputs/apk/debug/app-universal-debug.apk`

### Signed release build (optional)

1. Copy `keystore.properties.example` → `keystore.properties`
2. Fill in your keystore path and credentials
3. Run:
   ```powershell
   .\gradlew.bat :app:assembleRelease
   ```

The app builds and installs fine without a keystore (it just won't be signed for release distribution).

---

## Project Structure

```
etsume/
├── app/                    # Main Android application module
├── core/                   # Core utilities and shared logic
│   ├── common/
│   └── archive/
├── data/                   # Data layer (repositories, database, preferences)
├── domain/                 # Domain layer (use cases, models, interactors)
├── presentation-core/      # Shared Jetpack Compose UI components
├── presentation-widget/    # Android home screen widget
├── source-api/             # Extension source API definitions
├── source-local/           # Local file source implementation
├── i18n/                   # App string resources
└── i18n-aniyomi/           # Inherited anime-side string resources
```

---

## Contributing

Contributions are welcome and appreciated!  
Please read **[CONTRIBUTING.md](CONTRIBUTING.md)** before submitting anything.

- 🐛 **Bug reports** → [Open an issue](https://github.com/harshtries-code/etsume/issues/new?template=bug_report.md)
- 💡 **Feature requests** → [Start a discussion](https://github.com/harshtries-code/etsume/discussions)
- 🔧 **Pull requests** → Target the `main` branch; one concern per PR

---

## Project Notes

- Etsume does **not** bundle, host, or distribute any media content.
- Source, tracker, reader, and player behavior depends entirely on the extensions and integrations you configure inside the app.
- Etsume inherits the Apache 2.0 license from its upstream projects and intentionally removes upstream community and release metadata that no longer represents Etsume directly.

---

## Credits

Etsume stands on the shoulders of:

| Project | Role |
|---------|------|
| [Mihon](https://github.com/mihonapp/mihon) | Manga reader foundation |
| [Aniyomi](https://github.com/aniyomiorg/aniyomi) | Anime player integration |
| [Tachiyomi](https://github.com/tachiyomiorg/tachiyomi) | Original project lineage |

---

## License

Licensed under the [Apache License 2.0](LICENSE).
