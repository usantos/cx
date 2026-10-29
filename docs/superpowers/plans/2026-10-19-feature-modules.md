# Independent Feature Modules Implementation Plan

> Execute inline with superpowers:executing-plans. A fresh independent review follows implementation. SPEC19.11 prohibits automatic commits/pushes.

**Goal:** complete feature-owned MVVM/Clean compilation and remove account/cart/betting cycles while preserving public contracts, StateFlows and runtime behavior.
**Architecture:** ordinary feature-owned API/domain/data/presentation modules; core compiles only its own sources. App assembles navigation and supplies environment/platform integration.
**Tech Stack:** existing Kotlin, Compose, Hilt, Gradle, JUnit, JaCoCo and version catalog; Java17, compile/target37 and current SDK floors.
**Spec:** docs/superpowers/specs/2026-10-19-feature-modules-design.md; SPEC.md; user's supplied architecture.

## Global constraints

Preserve the dirty checkout and all previous work. No clean/reset/stage/commit/push/publication. Preserve package/class/method contracts, StateFlows, payloads, persisted bytes/schema/keys, security, default identities/flags and all coverage minima. All Gradle runs serialized. Never weaken physical oracles or replace missing private resources with fabricated evidence.

## Review focus

1. Shared cart and session identity classes must have one compiled owner and unchanged JVM descriptors, equality and defaults.
2. Cross-module internal helpers must remain correctly resolved; no duplicate classes or hidden implementation dependencies.
3. Coverage/tests must follow the moved class owners without losing assertions, class patterns or execution data.
4. Home flags/resources, saveable state, animations, callbacks and native clock/insets must survive app-boundary removal.
5. Hilt scopes/session-bound network clients and navigation side-effect/cancellation ordering must remain unchanged.

## Tasks

- [x] 1. Capture source/hash/public-API baselines under build/verification/feature-modules-20261019. Add structural regression checks for acyclic independent modules/no source aggregation and observe failures on the original graph. Run the current JVM/build baseline before moving sources.
- [x] 2. Extract unchanged cart model values to feature/cart/api and session identity/provider to feature/account/api. Move all feature domain roots to feature/<name>/domain/src/main/kotlin and register JVM modules with explicit dependency lists. Remove core/domain external srcDirs, update consumers and move domain tests to real owners. Compile/test domains independently; compare class contracts and prove acyclic graph.
- [x] 3. Move feature remote sources to their data modules' src/main roots and Android persistence to feature Android roots. Remove core/data and core/preferences source aggregation. Split the shared Retrofit adapter into feature adapters with the old public integration facade at the composition boundary. Move existing data/preferences tests to real owners, resolve only necessary helper visibility, preserve payloads and coverage. Compile/test each dependency group.
- [x] 4. Organize domain model/repository/usecase and data remote/dto/mapper/repository contracts with public compatibility surfaces. Feature-owned transport facades keep public API while shared networking remains core. Prove public API/source/payload equivalence and independent data/domain compilation.
- [x] 5. Remove Home host aggregation: compile all Home presentation in its own module, inject runtime flags/integration callbacks, copy identical required resources and retain original public composition APIs. Existing Home/App tests and exact visual characterization must pass.
- [x] 6. Move feature bindings and navigation contracts into each feature's di/navigation packages. Keep app's environment composition and root NavHost. Preserve old callable integration facades, Hilt scopes, route names/arguments, state owners and action ordering. Compile/test application and affected UI after each group.
- [x] 7. Centralize equivalent testing helpers in core/testing; expose existing database/preference infrastructure through database/datastore boundaries without changing storage technology/data. Introduce build-logic convention plugins for Android library/feature/Compose and pure JVM policy; keep version catalog and current build policies.
- [x] 8. Extend real architecture/format/UI/security/coverage gates to every ordinary module root. Add real negative fixtures proving domain Android/data leaks, source aggregation, dependency cycles and feature app dependencies are rejected. All previous checks and coverage minima retained.
- [x] 9. Run full modernCheck/releaseCheck all three flavors and relevant Android/visual characterization. Fresh whole-scope independent review, fix material findings with regressions; update ARCHITECTURE and migration audit/plan/progress/validation with actual ownership/results/limits. Compare public contracts and protected resources; clean up only this round's temporary resources. No commit/push.

## Execution decisions

The user's explicit implementation request and existing authorization allow continuous reversible implementation; skill handoffs do not add an approval pause. Preserve the existing codex/migracao-compose checkout because a new worktree would omit authorized uncommitted migration work. Use own snapshots/evidence and a plan-specific ledger. Source-root aggregation is not an acceptable final fallback.

Final validation correction: the first full matrix exposed missing AppAuth redirect placeholders in the new Cart/Home/Retail test APKs and NewApi at unchanged java.util.Base64 calls in Retail/Tickets Data. The existing desugar2.0.3 artifact has no Base64; official2.0.4 changelog adds it. A single catalog patch update2.0.3→2.0.4 is justified instead of suppressing lint, raising minSdk or replacing payload encoders. Same existing redirect scheme supplied to the three library test manifests. Remove13unusedSupportmapperimports/fourDataedges confirmed independently. Source bodies/public APIs unchanged. This narrowly justified dependency update supersedes the initial no-version-change implementation assumption.

Final results: modernCheck PASS16m50s2418tasks; releaseCheck(all3minifiedflavors)PASS5m30s1539tasks.2228reportedtestcases0failures0errors3conditionaldefaultskips(validreuse/repeatedflavors).All5physicalcasesexplicitlyexecuted0skips:3pass,2knowncoordinatefailures880/960and720/800.Nooraclechange.Freshindependentreviewandfocusedcorrectionsreviewnofindings.Public1027classes/facadeszerobreaks;37ViewModels/StateFlowsunchanged;392Domainbodiesretained.Homecheckpoint18/18equal;postnav17/18(onebuttonripple);unchangedanimationrepeat8/8baselineequal.Defaultdebugidentity/flagrestored,publicproofrepeated.OnlyownAVDdeleted;other3preserved.Externalphysical/private/signinglimitsremain,not100%SPEChomology.
