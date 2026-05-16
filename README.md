<div align="center">

# Etsume

**An open-source manga reader and anime player for Android.**

An independent, privacy-focused alternative to Tachiyomi and Aniyomi — rebuilt from the ground up with a premium experience in mind.

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Build](https://github.com/harshtries-code/etsume/actions/workflows/build_push.yml/badge.svg)](https://github.com/harshtries-code/etsume/actions/workflows/build_push.yml)
[![Latest Release](https://img.shields.io/github/v/release/harshtries-code/etsume?include_prereleases&label=latest)](https://github.com/harshtries-code/etsume/releases)

</div>

---

## What is Etsume?

Etsume is an open-source Android app for reading manga and watching anime from community extension sources you configure yourself. No ads, no bundled content, no tracking.

It is built on a **Mihon / Aniyomi** derived base and is undergoing a full product and UX rewrite to become its own independent application with a distinct, premium design language.

> ⚠️ **Early Development** — APIs, features, and UI surfaces are actively changing between releases.

---

## Features

- 📚 **Manga reader** — left-to-right, right-to-left, vertical scroll, and continuous webtoon modes
- 🎬 **Anime player** — hardware-accelerated playback via mpv and FFmpeg
- 🔍 **Browse** — community extension sources for manga and anime
- 📥 **Downloads** — offline reading and viewing support
- 🔖 **Library** — custom categories and collection management
- 💬 **Comments** *(coming soon)* — per-title discussion surfaces

---

## Extensions

Etsume does not bundle any content sources. You install them through **extension repos** that you choose and trust.

**Setup:**
1. Open Etsume → **More** → **Settings** → **Browse**
2. Tap **Manga Extension Repos** or **Anime Extension Repos**
3. Tap **Add** and paste a repo URL
4. Restart the app — new sources appear in the Browse tab

### Known Community Repos

> ⚠️ These are third-party repos. Etsume does not host, control, or endorse any of them.

| Type | Repos |
|------|-------|
| **Manga** | Keiyoushi, Yūzōnō, Kavita, Suwayomi |
| **Anime** | Yūzōnō, Secozzi, Claudemirovsky, hollow |

More repos and setup guides can be found on community wikis.

---

## Download

Get the latest APK from [**GitHub Releases**](https://github.com/harshtries-code/etsume/releases).

| Variant | Description |
|---------|-------------|
| `etsume-arm64-v8a-*.apk` | Most modern Android phones — **recommended** |
| `etsume-armeabi-v7a-*.apk` | Older 32-bit ARM devices |
| `etsume-x86_64-*.apk` | x86_64 emulators and Chromebooks |
| `etsume-x86-*.apk` | Legacy x86 devices |
| `etsume-universal-*.apk` | All architectures in one file (larger size) |

**Minimum:** Android 8.0 (API 26)

---

## Roadmap

- 🖥️ **Desktop app** — cross-platform client coming soon
- 💬 **In-app comments** — native discussion surfaces per title
- 🎨 **Custom reader/player UI** — redesigned floating controls and bars
- 👤 **Tracking redesign** — unified profile and media tracking dashboard

---

## Building from Source

### Requirements

- **Android Studio** Hedgehog (2023.1.1) or newer, or a working Android SDK with **JDK 17**
- A device or emulator running Android 8.0+

### Quick Start

```powershell
# Debug build
.\gradlew.bat :app:assembleDebug

# Install directly to connected device
.\gradlew.bat :app:installDebug
```

**Debug APK output:**
- `app/build/outputs/apk/debug/app-arm64-v8a-debug.apk`
- `app/build/outputs/apk/debug/app-universal-debug.apk`

### Signed Release Build (optional)

1. Copy `keystore.properties.example` → `keystore.properties`
2. Fill in your keystore path and credentials
3. Run:
   ```powershell
   .\gradlew.bat :app:assembleRelease
   ```

The app builds and installs without a keystore — it just won't be signed for release distribution.

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
├── presentation-core/      # Shared UI components
├── presentation-widget/    # Android home screen widget
├── source-api/             # Extension source API definitions
├── source-local/           # Local file source implementation
├── i18n/                   # App string resources
└── i18n-aniyomi/           # Inherited anime-side string resources
```

---

## Contributing

Contributions are welcome. Please read **[CONTRIBUTING.md](CONTRIBUTING.md)** before submitting.

- 🐛 **Bug reports** → [Open an issue](https://github.com/harshtries-code/etsume/issues/new?template=bug_report.md)
- 💡 **Feature requests** → [Start a discussion](https://github.com/harshtries-code/etsume/discussions)
- 🔧 **Pull requests** → Target the `main` branch; one concern per PR

---

## Notes

- Etsume does not bundle, host, or distribute any media content.
- Reader, player, and tracker behavior depend entirely on the extensions and integrations you configure.
- Etsume inherits the Apache 2.0 license from its upstream projects and intentionally removes upstream community metadata that no longer represents Etsume directly.

---

## Credits

| Project | Role |
|---------|------|
| [Mihon](https://github.com/mihonapp/mihon) | Manga reader foundation |
| [Aniyomi](https://github.com/aniyomiorg/aniyomi) | Anime player integration |
| [Tachiyomi](https://github.com/tachiyomiorg/tachiyomi) | Original project lineage |

---

## License

Licensed under the [Apache License 2.0](LICENSE).
