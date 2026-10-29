# Seven compliance corrections implementation plan

> **For agentic workers:** Use superpowers:executing-plans or independent agents for the disjoint tasks below.

**Goal:** Correct the seven audited SPEC.md gaps without changing public interfaces, StateFlows, persistence contracts or existing successful user flows.

**Architecture:** Keep existing facades. Introduce internal suspend boundaries for platform I/O and QR generation; move visitor cart policies to Domain while keeping database atomicity in Preferences.

**Tech Stack:** Existing Kotlin, coroutines, Compose, Hilt, SQLite, JUnit and Android instrumentation; no new dependencies.

**Spec:** /Users/ubiracysantos/Projects/simlo-app-android/SPEC.md

## Constraints
- Preserve APIs, StateFlows, protocol, saved PDF names/bytes, QR geometry, schema and transaction boundaries.
- Propagate CancellationException; ordinary transport failures keep their existing fallback.
- No blocking I/O or heavy bitmap generation on Main; use structured concurrency.
- Retain all existing verification gates and thresholds.
- Work in the existing checkout containing the preceding uncommitted migration; do not commit, reset or replace that work.

## Review focus
- Cancellation must not emit a false receipt failure or empty shields success.
- Failed/cancelled writes must not cache/open incomplete PDFs.
- Changing QR value must not publish an old generation; QR remains 640px rendered at 200dp.
- Visitor cart duplicate comparisons, Loteca price precedence, mirror links and monotonic IDs remain compatible.
- Lifecycle STOPPED must suspend state collection and resume with current data.

## Tasks
- [x] 1. Receipts: reproduce cancellation with tests; preserve public BetDetailReceiptEffect/rememberPrizeReceiptManager; inject internal dispatchers/operations, perform writes on IO, retain main-thread viewer launch. Exercise success, failure, cancellation and no viewer.
- [x] 2. Parameters/lifecycle: test suspended shields cancellation and non-cancellation fallback in core:data, then rethrow cancellation. Replace only ViewModel StateFlow collection in NotificationsRoute and ResponsibleGamingRoute with collectAsStateWithLifecycle and verify lifecycle transitions.
- [x] 3. Visitor cart: characterize quantity/open contest/max capacity/prices/duplicates/mirrors with Domain tests; extract policy into core:domain and persistence graph/mapper into core:preferences without API/schema changes. Run VisitorCartStorageTest on Android.
- [x] 4. QR: reproduce Main-thread generation problem with a worker-boundary test, extract cancellable generator using Default, load with value-keyed Compose effect, preserve exact pixels/dimensions/semantics. Verify stale result cancellation and decoding with tests.
- [x] 5. Integrate: review only delta against saved baseline, run module JVM suites, relevant Android tests, lint, migration gate and applicable coverage checks; document fresh results and limitations.

## Commands
Use ./gradlew with JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home. Coordinate Gradle runs through the root agent to avoid concurrent writes to outputs. Test core:domain/core:data and the six affected Android modules; use connectedDebugAndroidTest on a designated emulator for targeted storage/receipt/lifecycle/QR scenarios. Never modify installed reference apps.

## Execution ledger (19/10/2026)

- Original Android PDF test failed on actual main-thread provider execution; final boundary and original receipt interactions pass.
- Both shield cancellation regressions failed before correction; explicit and in-flight cancellation now propagate. Normal IOException fallback is preserved.
- Both real routes failed the stopped-lifecycle test before correction; only their StateFlow collection changed.
- QR generator tests first failed on missing worker seam; pixel generation now executes off Main. Removing value ownership reproduced stale image replacement (red replacing blue); restored ownership passes Android.
- Original coroutine exception identity assertions were corrected to account for stacktrace-recovery copies while checking the original cancellation in the cause chain.
- One Android validation attempt was interrupted during harness investigation and discarded; a clean repeat completed all 27 retail cases, with the existing profile-dependent coordinate test skipped.
- Initial full JVM matrix: 754/754; one additional production-default QR encoder pixel test added for final check execution.
- Read-only independent review found no actionable regressions in all seven corrections.
- Ruling: continue in the existing dirty checkout because it contains the preceding authorized migration. Full source baseline saved before edits; no commit/reset/branch operations.
- Ruling: existing missing-viewer behavior and pending picker state across recreation remain unchanged; scope is the seven audited corrections.
- Ruling: coverage class filters only expand for new collaborators and the visitor repository; original patterns and minimums remain unchanged.

- Final: 755 JVM, 108 Android + 5 skips, 63 contract proofs, 7 module checks and 10 gates pass; independent review clear. Documentation updated in the five mandatory reports.
