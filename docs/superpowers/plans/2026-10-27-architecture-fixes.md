# Correção dos dois desvios arquiteturais Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans for integration; independent implementation tasks use superpowers:dispatching-parallel-agents.

**Goal:** Correct ARQ-01 ForceUpdate ownership and ARQ-02 retained receipt platform/mutable-state coupling, then update documentation and the same Samsung app.

**Architecture:** App composes Onboarding Presentation/ViewModel, Domain contracts/usecases and Data Firebase adapter. Retail owns immutable receipt-delivery state/actions in Presentation through ReduxKotlin; Android URI conversion, launchers and document-provider access remain in Route. Retained jobs and pending bytes keep their current lifecycle without putting bytes into Android saved state.

**Tech Stack:** Existing Kotlin, MVVM, ReduxKotlin 0.6.2/JVM17, Compose, Firebase Remote Config and current tests/gates.

**Spec:** User request and docs/architecture/REVIEW_20261027.md (ARQ-01 and ARQ-02).

## Global Constraints
- Preserve every header and the existing baseline/config hashes; no threshold, suppression or baseline expansion.
- Preserve activated force-update state on errors, coroutine cancellation, observation disposal, gate order and public UI copy.
- Preserve receipt rotation, process recreation metadata, one-time delivery, retry/cancel, IO dispatcher and cancellation rules.
- No real bet, payment, reservation, credentials or device-protection changes. Install same PRD package without clearing data; retain the current cart (user later confirmed adding one bet: 5/R$22).
- Preserve current dirty checkout; no commit/push or unrelated cleanup. Root alone runs Gradle, sequentially.

## Review Focus
- Activated required update survives refresh failure; observation and cancellation do not clear it.
- Concurrent/resume refreshes do not duplicate active requests; observation removed on clear.
- Rotation during provider write completes exactly once without retaining an Activity.
- Process recreation retries only the identified receipt/destination, with no byte array in saved state.
- Write failures/cancellation cannot publish stale completion; retry/cancel and viewer failure preserve the original receipt.

## Tasks
- [x] ARQ-01: reproduce structural gap in policy tests; migrate ForceUpdate model/state to Onboarding Presentation (typed local Redux store), contract/operations to Domain, Firebase client to Data. App keeps composition and platform store-opening UI. Migrate existing behavior tests without weakening assertions; add meaningful lifecycle/error regressions where missing.
- [x] ARQ-02: reproduce structural gap; replace ViewModel reference to Route helper and writable Compose state with immutable typed Redux delivery state/events and VM-owned coordinator. Keep Uri/ContentResolver/Intent/launchers at Route boundary. Preserve and compile all existing receipt unit/UI/rotation/IO tests; add state/claim/retry regressions.
- [x] Integrate: strengthen architectural gates and negative tests for runtime App ViewModels and indirect presentation/route coupling. Run baseline/RED/GREEN, allUnitTests, focused formatting, Detekt, Lint, architecture gates, PRD assembly and affected AndroidTest compilation; fix actual findings without expanding baselines.
- [x] Review final ownership, lifecycle and protected hashes; install same APK on Samsung and exercise public startup/navigation with cart preservation. Document exact scope/limitations.
- [x] Close both findings in architecture docs, update Redux/MVVM contracts and branded guide with current evidence. Render/inspect final PDF and deliver screenshots/results.

## Execution ledger

- Work continued in the existing codex/migracao-compose checkout to preserve the current uncommitted migration; no commit, push or baseline/header changes. Independent owners had disjoint feature scopes; root alone ran Gradle.
- RED: old ForceUpdate callback after clear changed REQUIRED to READY; old Retail VM retained Route helper. New policy assertions also failed before guard implementation.
- Implementations moved ForceUpdate through feature DI/Presentation/Domain/Data and receipt delivery to pure coordinator + Redux metadata state. App direct Data dependency was rejected and removed; no policy exception was introduced.
- Fresh independent review approved the scoped implementation without fix findings. Integration then preserved two old test read paths via state.value, corrected a demonstrated recovery-failure regression, added three named test constants, a Firebase Common catalog alias and equivalent toUri conversion requested by Lint.
- No live Firebase flag or financial backend operation is changed for testing. Runtime evidence is recorded separately from static review and JVM/Robolectric results.

Final: all tasks complete. 3,223 unit /17 Android emulator /26 Samsung final checks passed; Lint, Detekt, architecture, affected AndroidTest compilation and PRD build passed. Protected hashes preserved. Final branded PDF254pages rendered and inspected; no blank/clipped pages. Cart5/R$22 preserved as confirmed by user.
