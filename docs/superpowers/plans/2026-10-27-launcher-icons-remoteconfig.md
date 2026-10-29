# Launcher icons Remote Config Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans for integration; superpowers:dispatching-parallel-agents for independent asset and outstanding Loteca work. Root alone runs Gradle and operates Samsung.

**Goal:** Deliver the five requested Remote Config launcher presets and retain the four results shortcuts.
**Architecture:** Typed Domain preset/read port, Firebase adapter in Onboarding Data, ViewModel/Redux state in Onboarding Presentation, Android alias/shortcut effects in App runtime. Apply once at safe startup after existing configuration activation.
**Tech Stack:** Kotlin, ReduxKotlin, Firebase Remote Config, activity-alias, PackageManager, ShortcutManager.
**Spec:** docs/superpowers/specs/2026-10-27-launcher-icons-remoteconfig-design.md

## Global Constraints
- No headers, baseline/configuration limits, user data, commits, pushes or test APK on Samsung changed.
- Same main app package on Samsung RXGL10CDZ0W; root alone runs sequential Gradle.
- Native five values: default, outubro_rosa, setembro_amarelo, novembro_azul, agosto_lilas.
- Preserve four shortcut IDs, labels, icons and target routes. MainActivity stays enabled.

## Review Focus
- Unknown/blank Remote Config values must return the default preset.
- Failed/throttled shortcut publication must not strand aliases or erase shortcuts.
- API24–32 sequential changes must never disable every launcher; API33+ changes are atomic.
- Repeated startup with the same preset must not republish unnecessarily or interrupt a running flow.
- Existing intents and pinned results shortcuts must retain the stable MainActivity target.

### Task 1: Campaign assets
- [x] Copy legacy October Rosa resources with provenance; generate yellow, blue and lilac variants preserving logo pixels.
- [x] Create resources ic_launcher_outubro_rosa, ic_launcher_setembro_amarelo, ic_launcher_novembro_azul, ic_launcher_agosto_lilas.
- [x] Render five previews side by side and check dimensions, transparency and preserved brand colors.

### Task 2: Typed selection and startup state
- [x] RED tests for all five values, normalization and missing/unknown fallback.
- [x] Add LauncherIconPreset, LauncherIconConfigurationRepository and GetLauncherIconPresetUseCase in Onboarding Domain.
- [x] Reuse named Firebase app via Data helper; add read-only activated value adapter without another network fetch.
- [x] Add LauncherIconViewModel with pure Redux state and completion outcome; factory in feature DI.

### Task 3: Native aliases and shortcut ownership
- [x] RED tests for one active alias, stable MainActivity, idempotence, rate limit and rollback.
- [x] Add five manifest aliases and native controller; preserve default resource and stable explicit intents.
- [x] Update shortcut owner to active alias; retain all four IDs/icons/routes and check publication return value.
- [x] Integrate safe startup application and DEBUG-only input preview, with normal startup guards retained.

### Task 4: Verification and documentation
- [x] Focused GREEN, complete affected-module tests, App integration, Lint, Detekt, architecture and formatting.
- [x] Check header/baseline hashes; install same main APK without clearing data.
- [x] Samsung: five swaps, return default, one launcher, menu of four shortcuts and correct destinations with closed/open app.
- [x] Update parameter guide, evidence matrix and professional Loterias CAIXA PDF; clearly distinguish local preview from cloud publication.
