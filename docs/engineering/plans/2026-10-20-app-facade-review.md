# Remove remaining redundant APP forwards

User request: review APP and remove redundant facades/forwards while preserving behavior, tests and coverage thresholds. Work inline in existing dirty checkout. Snapshot is recorded in build/verification/app-facade-review-20261020/manifest.json. No commit/push or default instrumentation against the installed reference application ID.

- [x] Review all main/debug APP sources, DI, manifests and direct consumers.
- [x] Add architecture regression cases for the retired queue/route functions and includes-only Hilt module; observe failure before tightening the guard.
- [x] Replace queue composable and two route forwards with existing CORE functions; migrate existing tests without changing assertions.
- [x] Remove two forwarding session properties and use account-owned values directly. Merge AppConfigurationDataModule includes into existing Singleton AppInitializationBindings; delete includes-only module.
- [x] Pass policies, architecture/format/composition gates, APP unit suites in all flavors, TQS Debug/Release and Android-test compilation. Run the existing queue recomposition test using isolated app ID only. Preserve all coverage configs byte for byte; run relevant coverage gates.
- [x] Independently review isolated patch against snapshot; record outcomes and verification limits.

Keep actual state/lifecycle/ownership: campaign ViewModel factory/theme composition, startup/retry policies, process environment durability, once-only network initialization, foreground route eligibility, typed DI aggregates and manifest Android entries. Exact route strings, back-stack options, handlers, keys, Hilt scopes/providers, test expectations and coverage class/input selectors must remain unchanged. No source/build/test edits during Gradle.
