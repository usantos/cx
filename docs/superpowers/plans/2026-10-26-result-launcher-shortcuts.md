# Result Launcher Shortcuts Implementation Plan

> **For agentic workers:** Execute inline with superpowers:executing-plans. Preserve this deliberately dirty checkout; SPEC §19.11 prohibits automatic commits/pushes.

**Goal:** Four native launcher shortcuts open Mega-Sena, Lotofácil, Loteca and all public results.
**Architecture:** Platform publisher and pending request adapter in app/runtime/shortcuts; Compose navigation dispatch in app/navigation/shortcuts; minimal hooks in MainActivity and AppNavHost. Existing startup gates and result screens are reused.
**Tech Stack:** Kotlin, Android ShortcutManager (API25+), Compose, Navigation, Robolectric/JUnit.
**Spec:** docs/superpowers/specs/2026-10-26-result-launcher-shortcuts-design.md

## Constraints
- Stay in the current checkout to preserve the uncommitted migration and prior fixes. No commit/push, new dependencies or baseline/header changes.
- Use actual context.packageName for every intent; retain minSdk29 and handle unavailable/locked shortcut services without crashing startup.
- Allow only the four public destinations. Preserve TLS/security/update/queue gates and all session/visitor data.

## Review focus
- The app already has minSdk29; native shortcuts must respect device capacity and rate limiting.
- Unknown action/ID cannot select a private or arbitrary route.
- Saved pending requests survive recreation before startup; consumed requests do not replay from the activity's original intent.
- Repeated warm shortcuts refresh results without accumulating results entries or replacing the screen underneath.
- Cold Back goes to Home; warm Back restores the existing destination; public results do not require login.

## Tasks
- [x] Add failing publisher/parser/request-state tests in app/src/test/java/br/gov/caixa/loterias/apostas/ResultLauncherShortcutsTest.kt. Implement app/runtime/shortcuts/ResultLauncherShortcuts.kt and ResultShortcutRequests.kt plus strings_result_shortcuts.xml; confirm GREEN.
- [x] Add failing navigation tests in ResultShortcutNavigationTest.kt for cold/warm/repeated/consumed requests. Implement app/navigation/shortcuts/ResultShortcutNavigation.kt, connect MainActivity onCreate/onNewIntent/onSaveInstanceState and AppNavHost after graph creation. Confirm GREEN.
- [x] Run focused/full relevant JVM tests, architecture, analyzers and app lint/build for supported flavors. Install the same PRD app, verify the four launcher actions and destination selection on Samsung, capture evidence, obtain a fresh read-only review and confirm protected hashes.

Execution ruling: Use native dynamic publication to avoid hard-coded PRD package names in shortcut XML. Keep the existing app icon and visible labels requested by the user. Android platform display limits are addressed by the selected fourth action, Todos os resultados.

Ruling: app/build.gradle sets minSdk29, so no obsolete API24 compatibility path or dependency is introduced. Native API25 functionality is available on every supported device.


## Execution evidence
- Publisher/parser/state: 8 regression tests observed RED, then GREEN. Final suite includes the startup replacement regression (9 tests).
- Navigation: 3 tests observed RED, then GREEN; covers cold public Home stack, warm Cart return and refreshed/replaced result entries.
- Final review: fixed replacement shortcut losing cold-start intent while startup gates are pending. `aSecondShortcutDuringStartupKeepsThePublicHomeBackStack` observed assertion RED→GREEN; full PRD app suite 363/363, 0 skipped.
- Full PRD build, lint, Detekt and app architecture passed. Ktlint passed for changed files. All 37 protected baseline/config hashes match the existing snapshot.
- No commit or push: preserve existing migration checkout as requested by repository policy. No header changes, dependencies or baseline regeneration.

- Samsung final: four native menu actions × cold/warm = 8/8 passed, 0 failures, 0 skipped; cold Back confirmed Home; warm PID unchanged. Evidence: build/verification/result-shortcuts-20261026/report.md. Alternate TQS flavor 9/9 passed with actual .hmp package.
