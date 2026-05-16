# Contributing to Etsume

Thank you for taking the time to contribute! 🎉  
Please read this guide before opening issues or pull requests.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Features](#suggesting-features)
- [Development Setup](#development-setup)
- [Submitting a Pull Request](#submitting-a-pull-request)
- [Code Style](#code-style)
- [Commit Messages](#commit-messages)

---

## Code of Conduct

By participating in this project you agree to abide by the [Code of Conduct](CODE_OF_CONDUCT.md).

---

## Reporting Bugs

Before filing a bug report:
1. Search existing [issues](https://github.com/harshtries-code/etsume/issues) to see if it's already reported.
2. Reproduce the bug on the **latest release** first.

When opening a bug report, use the [bug report template](.github/ISSUE_TEMPLATE/bug_report.md) and fill in every section — especially the steps to reproduce and the device/OS info.

---

## Suggesting Features

Open a [Discussion](https://github.com/harshtries-code/etsume/discussions) under the **Ideas** category.  
Feature requests as GitHub Issues will be closed and redirected to Discussions.

---

## Development Setup

1. **Fork** the repository and clone your fork.
2. Open the project in **Android Studio Hedgehog** (2023.1.1) or newer.
3. Let Gradle sync complete.
4. Build and run on a device or emulator (Android 8.0+):

```powershell
.\gradlew.bat :app:installDebug
```

---

## Submitting a Pull Request

1. Create a branch from `main`:
   ```bash
   git checkout -b fix/describe-your-fix
   ```
2. Make your changes. Keep PRs **focused** — one bug fix or feature per PR.
3. Make sure the code compiles and tests pass:
   ```powershell
   .\gradlew.bat spotlessCheck
   .\gradlew.bat testDebugUnitTest
   ```
4. Push your branch and open a PR against `main`.
5. Fill in the pull request template completely.

PRs that do not follow the template or that mix multiple unrelated changes will be asked to split or reformat.

---

## Code Style

Etsume uses [Spotless](https://github.com/diffplug/spotless) with ktlint for Kotlin formatting.

Check formatting before pushing:
```powershell
.\gradlew.bat spotlessCheck
```

Auto-fix formatting issues:
```powershell
.\gradlew.bat spotlessApply
```

General guidelines:
- Write in **Kotlin** for all new Android code.
- Use **Jetpack Compose** for new UI screens.
- Follow existing module boundaries (UI in `presentation`, business logic in `domain`, data access in `data`).
- Do not add new dependencies without discussion.

---

## Commit Messages

Use the [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>(<scope>): <short description>

[optional body]

[optional footer]
```

Common types: `feat`, `fix`, `refactor`, `chore`, `docs`, `test`, `style`

Examples:
```
feat(reader): add continuous scroll mode
fix(player): resolve crash on subtitle track switch
chore(deps): update coil to 2.6.0
docs: update build instructions in README
```
