# Final APP, CORE and Feature Coherence Implementation Plan

> **For agentic workers:** Execute this authorized review and cleanup in the current session. Reviewers inspect independent scopes without mutations; the primary agent applies changes and runs Gradle serially.

**Goal:** Eliminate evidenced redundant or incoherent code and dependencies across APP, CORE and all 14 features, preserving active behavior.

**Architecture:** APP owns composition and platform entry points; CORE owns shared infrastructure/contracts; features own their domain, data, presentation, navigation and DI. Retire compatibility surfaces that only forward to existing owners and migrate their remaining consumers. Preserve active HTTP transport contracts, serialization, session rules and platform boundaries.

**Tech Stack:** Kotlin, Android, Compose, Hilt/KSP, Gradle, Python architecture gates, JUnit and instrumented Android tests.

**Spec:** User request in this chat: final complete review of APP, CORE and every feature, eliminating remaining redundancy or incoherence.

## Global Constraints

- Preserve the existing dirty checkout; compare changes against the task-start snapshot recorded in `/tmp/final-coherence-snapshot-path-20261020`.
- Do not change payloads, URLs, routes, persistence keys, authorization/session behavior, Compose geometry, Hilt scopes or coverage thresholds.
- Do not remove ports, transport projections, platform adapters or test fixtures that still have an active responsibility.
- No source/build/test edits while Gradle is running. One Gradle invocation at a time; use the existing emulator.
- No commit, push or publication.

## Review Focus

- Compatibility overloads used only by compatibility tests must not survive as artificial production consumers.
- Hilt bindings and manifest/reflection entry points can be active without ordinary Kotlin references.
- Removing exported Gradle dependencies must not hide an actual consumer's missing direct dependency.
- Similar DTOs/repositories may represent distinct transport contracts, identity guards or runtime scopes.
- Removing dead composables must not change active navigation, Compose structure or visual regression oracles.

### Task 1: Review all owners and establish concrete findings

- [x] Review APP/CORE, feature Domain/Data/DI, and feature UI/navigation independently in read-only passes.
- [x] Inventory source references, manifests, tests, module dependencies, compatibility inventories and current architecture documentation.
- [x] Record each actionable finding with its effective owner, consumers and minimal correction; reject speculative deletions.

### Task 2: Apply minimal cleanup and prevent reintroduction

**Files:** Confirmed APP facade files in `config/app-compatibility.json`, corresponding consumer imports/overloads, `scripts/check-app-architecture.py` and `scripts/test_app_architecture.py`; confirmed dead feature declarations and Gradle dependencies; `ARCHITECTURE.md` and current architecture documentation.

- [x] Add failing architecture-policy cases for retired compatibility paths before tightening their gate.
- [x] Remove confirmed dead forwarding surfaces, migrate real consumers, and retire only their inventory/test assertions.
- [x] Remove confirmed unused declarations and dependencies, preserving every active boundary and contract.
- [x] Run policy tests and targeted compilation/tests; independently review the task-specific patch against its snapshot.

### Task 3: Verify and record final state

- [x] Run real negative gate fixtures serially and confirm restoration.
- [x] Run `ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain` after all source edits.
- [x] Inspect exit status, all failures, test skips, coverage reports and release results; fix actual failures before completion.
- [x] Update `MIGRATION_VALIDATION.md` and task evidence with final results, scope and remaining verification limits.
