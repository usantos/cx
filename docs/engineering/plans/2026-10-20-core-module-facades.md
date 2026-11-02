# Retire redundant CORE modules

User request: inspect remaining CORE modules and remove forwarding-only structures while preserving behavior and tests. Execute inline in the existing dirty checkout; no commit, push, worktree switch or manipulation of the installed reference app.

- [x] Inventory all 14 declared CORE modules, sources, Gradle consumers and coverage responsibilities.
- [x] Inline UserPreferences.named at its five feature-owned adapters; preserve context, keys, modes and commit/apply.
- [x] Move EnvironmentPreferences and its existing commit-failure/retry test to app/environment; preserve bodies and persistence contract.
- [x] Remove core:datastore and move the core:preferences coverage gate into gradle/persistence-coverage.gradle at root, preserving class selectors, execution suites and minimum 0.9501; remove only eliminated classes and NO-SOURCE module inputs.
- [x] Compile TQS Debug/Release, run all app flavor unit suites, affected Data unit/instrumented suites, architecture/format gates, persistence and domain coverage gates, and inspect results.
- [x] Audit the isolated patch, update current architecture documentation and record evidence and validation limits.

Review focus: commit(false) must not acknowledge durability; exact preference file names/modes and context lifetime; preserve Android corruption handlers and transaction behavior; keep every real coverage input and test dependency; remove all active references to retired modules. Existing behavioral tests cover these contracts; this is a mechanical relocation/inline refactor, with no new functionality.
