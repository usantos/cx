# External links, receipt recovery and global validation implementation plan

> **For agentic workers:** Apply the approved scoped design, execute independent implementation tasks and request an independent final review.

**Goal:** Report external-opening errors, recover pending PDFs across recreation, preserve public APIs/StateFlows, and execute the complete existing gates plus the five conditional visual cases and available real integration checks.
**Architecture:** Retain current routes and ViewModels. Use narrow internal platform-opening helpers and retained receipt state without storing Activity/Context or PDF bytes in saved-state Bundles. Reuse existing test fixtures and lifecycle owners.
**Tech Stack:** Existing Kotlin, Compose, coroutines, AndroidX ViewModel/activity results, JUnit and instrumentation; no new dependencies.
**Spec:** /Users/ubiracysantos/Projects/simlo-app-android/SPEC.md

## Constraints
- Preserve public contracts, StateFlows, successful navigation and exact URLs/PDF names/bytes/URI flags.
- Propagate cancellation, perform file I/O off Main, show failures honestly, prevent duplicate writes/opening after disposal.
- Preserve authentication/security, pinning and gate thresholds; no weakening assertions/oracles or removing tests to pass.
- Preserve all preceding dirty-worktree changes; no commits/pushes/resets.
- Keep real integration evidence separate from fixture/emulator evidence; never present unavailable services/hardware as homologated.

## Review focus
- Browser/viewer absent and SecurityException must produce user feedback without false success.
- Retry must use the original URL/receipt and not relaunch on every recomposition.
- Recreation while picker is open must preserve receipt ownership and deliver exactly once.
- Recreation during blocking write must not cache/open a partial file or double write.
- Process restoration must use only small metadata in saved state; no cross-user leaks or Context retention.

## Tasks
- [x] 1. Links: add failing actual-route opening tests for AccountRoute and HomeRoute; implement localized feedback/retry preserving public APIs and success layout.
- [x] 2. Receipts: add failing recreation and viewer-failure tests; retain pending bytes appropriately, save small metadata if needed, restore picker results/cache, preserve cancellation/IO behavior.
- [x] 3. Visual cases: identify exact five profile predicates and installed reference artifacts; run unchanged assertions at 1080x2408/450dpi with required typography. Investigate failures against originals; preserve profile afterward.
- [x] 4. Global validation: run modernCheck and releaseCheck (Prd/Tqs/Automacao), without exclusions or mocks. Resolve introduced failures; document genuine external/historical failures with evidence.
- [ ] 5. Real homologation: inventory connected devices, browser/test-session availability, environment contracts and documented test resources without exposing secrets; exercise authorized real flows when prerequisites exist. Request only missing required human action/access and keep independent work moving.
- [x] 6. Final review, public contract and source delta checks; update five mandatory reports with exact evidence and remaining limitations.

## Execution
Root owns Gradle/device/profile operations and documentation. Implementers do not run simultaneous Gradle/ADB commands. JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home. Logs and baseline are in build/verification/links-receipts-global-20261019/.

## Verified outcome
- Links/receipts: RED/GREEN regressions, 17 receipt Android +68 Retail JVM +10 link Android passed; independent review has no remaining findings.
- modernCheck PASS,14m15s:10 gates,24 module checks; reports1910 JVM and193 Android (190 passed,3 conditional skips), with valid Gradle caching.
- releaseCheck PASS,12m53s:Prd/Tqs/Automacao; identities/flags/hashes verified; all APKs unsigned because distribution signing resources are unavailable.
- Five unchanged conditional visual methods ran at1080x2408/450dpi:3 pass,2 fail. Prepared captures identify +80px top-inset displacement; original null remains separate from prepared purchase800. No production/golden compensation. Physical matching profile remains unavailable.
- Task5: local inventory and public TLS/metadata probes completed; real OAuth/biometrics/SSO/queue/private providers remain blocked by unavailable account/device/configuration, confirmed by user. No real transaction or authenticated flow was performed.
- Mandatory reports updated, public contracts/source scope verified; owned emulator processes stopped, only new temporary AVD deleted. No commit/push.
