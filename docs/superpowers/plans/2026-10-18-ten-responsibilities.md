# Ten Responsibilities Refactoring Plan

> **For agentic workers:** use superpowers:executing-plans to implement this plan inline. Preserve the existing checkout, all prior work and SPEC.md. No commits or pushes.

**Goal:** Refactor the ten agreed files without changing public interfaces, StateFlows, state transitions, payloads, navigation or visual geometry.

**Architecture:** Keep existing public facades and state ownership. Extract internal collaborators by responsibility, sharing the original state and ViewModel coroutine scope. Move Compose functions and DTO declarations literally within their packages. Preserve Retrofit builder mutation and interceptor order.

**Tech stack:** Kotlin, Compose, Hilt, StateFlow, OkHttp, Retrofit, Gson, JUnit, JaCoCo, Android instrumentation.

**Spec:** `/Users/ubiracysantos/Projects/simlo-app-android/SPEC.md`.

## Global constraints

No dependency upgrades, gate reductions, test-oracle edits, public API changes, new state owners, scopes or cancellation policy changes. Characterization tests must pass before and after extraction. Check compiled APIs and literal moved bodies/declarations. Work remains uncommitted as required by SPEC.md. Evidence: `build/verification/ten-responsibilities-20261018/`.

## Task 1: ViewModels

RapidConfiguration: loading, amount limits/form, favorites, numbers, submission, formatting. Login: authorization, biometric preferences/fallback, exchange/access decisions, visitor and acknowledgment. ResponsibleGaming: assessment, suspension, social transfers and errors, retaining effect queue ownership. Notifications: loading, projection, mutations, preferences, effects/dialogs; preserve projection revision and single-consumer queue.

Keep constructors, public methods/return types, UiStates, effects and actions. Pass the original MutableStateFlow and viewModelScope to collaborators. Preserve init order and operation snapshot timing. Add extracted collaborators to existing coverage patterns, keeping thresholds unchanged.

Validation: existing JVM suites before and after, compiled API comparison, targeted UI suites and coverage gates. Expected: all pass; no changes to method bodies beyond explicit receiver delegation.

## Task 2: Compose and navigation

PrizeOptions: composition, current contest, result header, awards, channel rows. PrizePix: composition, actions, key selection/tabs/field, confirmation, buttons. Tickets graph: detail/history/purchases/scanner/manual registration; preserve registration order, routes, keys, owners, arguments, remember keys and LaunchedEffect placement.

Validation: compile all callers; compare moved functions/route blocks; retail and app navigation instrumentation. Expected: same rendering and callbacks, navigation state restoration and private-session boundaries pass.

## Task 3: Network

NetworkClientFactory: public facade, client families, Retrofit/interceptors/transport and pinning. ApiModels: unchanged DTO declarations grouped by domain. MockBackendInterceptor: interception, options/scenarios, ordered response resolution, response routing/errors and domain fixtures.

Validation: network + data JVM tests, compiled DTO/factory/mock APIs and literal DTO/JSON comparisons, network coverage with original thresholds, security gates. Expected: identical serialization and HTTP policies.

## Task 4: Verification and documentation

Run affected module checks, ten architecture/security gates, PrdDebug APK build, relevant Android tests. Request one independent read-only review of this round against snapshots, then resolve actionable regressions. Update five migration documents and validation report with real counts and limitations. Check changes against initial inventory; preserve unrelated work and native oracle packages. Stop only the emulator created for this round.

## Review focus

Coroutine cancellation and launch timing; stale session guards; StateFlow identity and event queue ownership; biometric fallback/blocked-account cleanup; notification revision retention; registration order and ViewModel owners/keys; all Compose modifiers/callbacks; Gson annotations/defaults; JSON whitespace; interceptor ordering and one-shot retry policies. Do not edit existing tests, gates, resources, manifests or native oracle packages to make validation pass.
