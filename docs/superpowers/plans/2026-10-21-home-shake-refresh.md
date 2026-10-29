# Home Shake and Refresh Implementation Plan

> **For agentic workers:** Execute inline with superpowers:executing-plans; preserve unrelated working-tree changes.

**Goal:** On tutorial skip/completion, open the home drawer and Shake tooltip; improve October Rose contrast and switch state colors; refresh Home V2 initially and through pull-to-refresh.

**Architecture:** Reuse the persisted pending-tooltip flag to open the drawer. Keep loading in existing ViewModels and cache initial results across navigation. Use Material3 pull-to-refresh around the existing scrollable home.

**Tech Stack:** Kotlin, Compose Material3, Navigation, coroutine StateFlow.

**Spec:** User request in this chat, October 21, 2026.

## Constraints
- Keep Chucker disabled in the installed debug APK.
- Do not reset preferences, sessions or unrelated working-tree changes.
- Preserve explicit retry actions and session/cart consistency.
- Manual refresh must preserve visible cards while requests run.

## Review Focus
- Skip and completion both persist pending guidance; consume it only when displayed.
- Closing the drawer must not cause repeated automatic openings.
- Disabled and enabled Shake states remain distinguishable in October Rose.
- Returning from another destination must not reload modalities/results.
- Repeated pull gestures must not create duplicate requests.

## Tasks
1. Add regression coverage for pending guidance, switch colors, and refresh/reentry.
2. Open the drawer once for pending guidance; keep tooltip effect responsive to pending changes.
3. Introduce October Rose tooltip/switch colors without changing other campaign palettes.
4. Remove V2 lifecycle modal refresh; use cached results on entry and forced refresh on pull.
5. Add Material3 pull-to-refresh without replacing loaded content by skeletons.
6. Run affected unit/UI tests, review changes, assemble and verify on connected Android device.

## Execution notes
- Initial regression tests failed for duplicate parameter loads on reentry, missing pull gesture, pending guidance changes, disabled switch colors and failed-result retries.
- First implementation passed Home/Results unit tests and eight Home UI tests.
- User clarified that build-logic had been intentionally deleted and requested compilation without it. Ruling: inline its equivalent configuration in each owning module; remove the composite build and custom plugin declarations. Preserve Java 17, compileSdk 37, module minSdk values, desugaring, Compose, dependencies and coverage settings.
- In-memory preference fixtures use Main.immediate, matching existing HomePreferencesFixtures; production storage retains IO dispatchers.
- The completion integration test exposed Coil restarting a reused GIF painter during composition. Keying the painter by the page image isolated request ownership; both skip/completion tests subsequently passed.
- Final verification: PRD Debug assembled without build-logic; Home and Results unit suites and all ten Home instrumented tests passed. APK installed on the connected Samsung; visual inspection there was unavailable while its PIN lock was active.
