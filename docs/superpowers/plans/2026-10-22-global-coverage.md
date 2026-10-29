# Global production coverage implementation plan

> **For agentic workers:** Use superpowers:executing-plans for the shared measurement/runtime work; independent module tests use superpowers:dispatching-parallel-agents. Execute the user's requested changes in this session.

**Goal:** More than 95% actual weighted JaCoCo LINE coverage across all production modules.

**Architecture:** Keep the same canonical production variants, generated-code exclusions and eligible handwritten classes. Extend local Android unit tests with Robolectric and assertion-driven Compose/component/state scenarios; preserve the independent device suites. Aggregate only fresh unit-test execution data and compare exact counters.

**Tech Stack:** Gradle 9.4.1, AGP 9.2.1, Kotlin 2.4.10, Java 21, JaCoCo 0.8.13, JUnit 4, Robolectric 4.17.

**Spec:** User request in this chat: global coverage greater than 95%; existing measurement contract in docs/TEST_COVERAGE.md and gradle/aggregate-test-reports.gradle.

## Global constraints

- No handwritten UI, business rules, persistence, network or navigation exclusions.
- No synthetic probe manipulation or execution-only tests without behavioral assertions.
- Existing production behavior, baseline files and SDK security remain intact.
- No real bets, payments, reservations, external messages, commits or pushes.
- All module test execution is coordinated centrally; workers edit disjoint test folders.

## Review focus

- Initial/error/visitor/success UI states must render appropriate content and callbacks.
- Queue/session restoration, invalid data, cancellation and encryption failures must preserve safe state.
- Local UI runners must not invoke real backend/hardware or silently mask test failures.
- Coverage must contain all modules, including modules lacking tests, with no stale execution data.
- Exactly 95% must fail the new strictly-greater-than requirement; rounded display cannot admit a failure.

## Tasks

- [x] Re-run canonical verifyCoverage to record a fresh starting point: 42.39%, 17782/41949 lines, 65 modules.
- [x] Configure reproducible local Android/Compose test support with Robolectric 4.17. Reuse explicitly listed existing behavior tests under the local runner; retain their assertions and original device sources.
- [x] Add missing DS component/state/accessibility/interaction tests in core/design-system/src/test.
- [x] Add missing betting/tickets screen states and callbacks in their presentation/src/test trees.
- [x] Add core session/security/queue and data/persistence failure/recovery tests in scoped src/test trees.
- [x] Cover remaining presentation modules after inspecting the refreshed missed-line inventory.
- [x] Make scripts/verify-jacoco-coverage.py reject exactly 95%, retain weighted/module-sum/freshness checks, and extend scripts/test_jacoco_coverage.py.
- [x] Run all selected unit suites, regenerate all module and aggregate XML/HTML, then run qualityGate; repair failing scenarios and remaining real gaps until the strict threshold passes.
- [x] Record measured counts, test totals, commands and validation limits in docs/TEST_COVERAGE.md and quality results. Report success only from the final command output.

## Production defects exposed by the new scenarios

- Firebase Remote Config settings return a successful `Task<Void>` with a null result. The await helper now checks task success independently of the nullable value; cancelled/failed tasks retain the trusted fallback.
- Combo detail now loads current betting parameters before enabling inclusion. Failed/reloading parameters clear stale selections and prevent submission; the confirmation uses the configured minimum instead of zero.

These are behavioral fixes covered by regression cases, not changes made to reduce the coverage denominator.

## Final verification

`qualityGate --continue --console=plain -PhomeV2=false -PmockBackend=false -PnetworkLogging=false` passed on 2026-10-22 with JDK 21. Fresh weighted production LINE coverage: 39956/41961 = 95.22%, 65 modules; 2959 tests in 577 suites, no failures/errors/skips. Ktlint, Detekt and Android Lint passed. The separate Python script suite passed all 161 tests. Canonical summaries and HTML/XML artifacts are under build/reports; measured results and local/device limits are documented in docs/TEST_COVERAGE.md.
