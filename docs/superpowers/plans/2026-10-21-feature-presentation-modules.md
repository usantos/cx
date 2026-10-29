# Feature Presentation Modules Implementation Plan

> **For agentic workers:** Execute inline with superpowers:executing-plans; request one independent review after integration.

**Goal:** Give all fourteen features their own `:feature:<name>:presentation` module beside Data/Domain, keeping the project compilable.

**Architecture:** The former feature Android module becomes its presentation child. Move its whole src tree and build.gradle, preserving Kotlin packages, Android namespace, resources, manifests, Hilt/KSP, variants and tests. Parent feature folders become Gradle grouping projects without plugins/sources; dependencies target the real presentation child.

**Tech Stack:** Existing Groovy Gradle, AGP9.2.1, Kotlin2.4.10, Java17, Compose/Hilt/KSP.

**Spec:** User request in this chat, 21/10/2026: separate kotlin+java into a module in every feature at the same level as data/domain and keep compilation working.

## Global Constraints

- Preserve existing uncommitted work; execute in the active checkout without reset or new build-logic.
- Preserve runtime behavior, namespaces and resource identifiers; do not rename package declarations.
- Move production, unit/instrumentation tests and resources together; no cross-module srcDirs aliases or forwarding modules.
- Update build/verification scripts and active documentation to the new physical locations.
- Preserve backend real, Home Legado and network inspector disabled when assembling the validation APK.

## Review Focus

- App and cross-feature project dependencies must target presentation, never empty parents.
- Resource/manifest/coverage/test locations must move with their Gradle owner.
- Architecture guards must still scan presentation sources and their fixtures after relocation.
- Old IDE module models must be replaced by a successful Gradle sync.
- Source file hashes before/after must match except intentional path updates in tooling.

## Tasks

- [x] Inventory fourteen feature roots, project/path references and baseline architecture checks; capture source hashes.
- [x] Move build.gradle/src into presentation per feature, update settings/dependency/task references and functional paths.
- [x] Update architecture guard discovery/fixtures; run Python guard tests and compare against baseline gate output.
- [x] Compile APK and all main/unit/androidTest sources; run relocated unit suites and request independent review.
- [x] Sync Android Studio, verify child modules/import/compilation and summarize results.

## Rulings and execution

- Active checkout is required: it contains the user's ongoing migration and many untracked production sources; a fresh worktree would omit the intended baseline.
- `di` and `navigation` move with `presentation`, as requested for all content shown under kotlin+java; Data/Domain/API/Transport stay in their existing modules.
- `marketplace-filter` also gets presentation despite not having Data/Domain children, because the request covers every feature.

- Source conservation: 1,235 files preserved. Only three instrumentation sources changed text to rename two pre-existing test helpers rejected by the existing Legacy-type gate (`NativeHomeOracle`, `HomeParityUiTest`); production sources/resources match the captured hashes.
- Guard regression: watched 36 tests fail after relocation, updated module discovery and fixture owners, then all 108 tests passed (107 existing + one parameterized aggregation guard).
- Ruling: rename the two test helpers — the module gate already rejected their former names before this move; keeping tests compilable and the gate active avoids carrying that pre-existing failure. No production API changes.

## Initial validation results (before follow-up fixes)

- `:app:assemblePrdDebug -PnetworkLogging=false -PmockBackend=false -PhomeV2=false`: passed, 777 tasks (40s).
- Temporary aggregate `validatePresentationMoveCompile`: passed; compiles main/unit/androidTest sources in every Android library, JVM classes/testClasses, and APP PRD debug sources/tests.
- All unit tasks attempted with `--continue`: 1,737 cases across 34 modules/298 suites; 1,736 passed, one pre-existing assertion failure in APP `HomeCarouselTest` (`pool button follows backend flag modality availability and card state`, line95, closed contest visibility). The production rule matches the pre-move hash; no behavior changed here.
- `verifyFeatureModules`: passed, including configured ownership and acyclic dependency graph. Python policy tests: 108 passed; standalone feature ownership check passed.
- Existing broader gate failures retained: `verifyCleanArchitecture` rejects a `retrofit2.` reference in ResultsRepositoryImpl (Data was not moved/edited); `verifyComposeMigration` rejects four resources already present before the move (`legacy_brand_trevo.xml`, Home `legacy_home_pool_timer.xml`, `legacy_home_result_trevo.xml`, `home_special_contests.xml`). These do not prevent source compilation/APK assembly; no unrelated UI/repository rules were changed.
- Independent read-only review found one missed active inventory, `docs/LEGACY_DRAWABLE_RESOURCES.txt`: corrected all 147 moved resource paths, verified zero missing files, review otherwise clear.

- Android Studio: Gradle sync completed, Account expanded with API/Data/Domain/Presentation/Transport siblings. `Build → Compile All Sources` completed at17:49 on21/10/2026 with63 warnings and no errors, including relocated Android test sources.
- Ruling: report the one functional test assertion and two broader gate failures as existing limitations; their source/resources are unchanged by relocation. Changing business behavior or adding unrelated resource exemptions would exceed this structural task.

## Follow-up: resolve all pending checks

User requested on 21/10/2026 that the Home test and both pending checks also pass.

- Confirmed the pool-button rule in the original `PrincipalActivity.java` lines981–1004: configured pool sales and modality availability control visibility independently of the selected contest's open/closed state; INSTANT/COMBO remain excluded. Updated the stale APP assertion and added the COMBO assertion. Home production behavior is preserved.
- Moved Retrofit-specific shield failure translation into `ResultsRemoteDataSourceImpl`. Only HTTP failures become IO failures; ResultsRepository retains the optional-shield fallback through its existing IO handling. New tests cover HTTP cause preservation, original IO identity, cancellation propagation and programming-error propagation. Watched HTTP test fail before the fix and pass afterward.
- Audited and explicitly registered the three missing vector resources. Kept the two campaign strings in Android resources, with an exact-file/exact-name gate accepting only the two leaf strings; no blanket XML exemption. Added four negative fixtures (layout root, nested layout, extra label, duplicate label), restoring original bytes in finally.
- The added CaixaDS check exposed literal colors in eight Home sources. Replaced them with tokens preserving all exact ARGB values; introduced `OctoberRoseTooltip` and `SportsResultSelection` in CaixaDS for the two distinct semantic roles. This resolves the check without weakening it or changing the visual appearance.
- Complete Gradle validation passed: all main/unit/androidTest sources compile; all unit-test tasks passed (1,741 cases, 299 suites, 34 modules containing tests, zero failures/errors/skips); `verifyFeatureModules`, `verifyCleanArchitecture`, `verifyComposeMigration`, `verifyCaixaDS` passed; PRD debug APK assembled with `networkLogging=false`, `mockBackend=false`, `homeV2=false`. Log: `/tmp/simlo-pending-all-green.log` (1m38s, 1,319 actionable tasks).
- Python policy tests: 108 passed. Independent review of boundary behavior, test expectations, resource gate restrictions and token substitutions reported no findings.

- Real Gradle guardrail regression completed: all 67 cases passed, including the four new campaign-label cases, general XML-layout rejection, literal-color rejection, manifest/FileProvider restrictions and final valid-state checks. All fixture files restored. Evidence: `build/verification/compose-guardrails/results.json`, `/tmp/simlo-pending-guardrails.log`.
- Final status: all pending checks resolved; no failures in the complete unit/compilation/APK/architecture/CaixaDS validation performed for this task.
