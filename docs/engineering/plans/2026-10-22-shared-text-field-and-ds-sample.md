# Shared DS fields and runnable CAIXA catalog

User approved the shared-field design in chat and requested a runnable component-list sample in the same task. Work continues in the existing checkout to preserve the current uncommitted migration and verified coverage work.

## Contracts

- One generic CaixaDSTextField entry point with OUTLINED (existing default), LABELLED (existing external-label preview), and LEGACY variants. Preserve account 52dp row, 4dp label gap, 16sp font, disabled fill/check, focus/error colors, masked placeholder and BRL cents watcher/caret behavior.
- CaixaDSAccountField and CaixaDSLabelledField remain source-compatible forwarding wrappers. Account edit and registration consume the shared entry point directly. Business validation/network/session actions stay outside DS.
- All app/feature BasicTextField inputs use the DS primitive with equivalent String/TextFieldValue overloads and unchanged defaults, IME, selection, styling and decorations. Ownership verification rejects imports/aliases/wildcards/qualified calls that bypass DS.
- Independent :sample:design-system Android app: searchable component list, details with actual previews, usage snippets and parameters, CAIXA palette, all user-facing strings in resources, no login/backend/INTERNET permission. Includes its production classes in aggregate coverage.
- No real bets, payments or reservations; no source exclusions, baseline regeneration, probes manipulation or threshold reduction.

## Work

- [x] Inspect DS and Legacy XML/currency watcher, add field contract tests first and observe missing-API failure.
- [x] Implement shared variants/primitive, forwarding wrappers and migrate account/other consumers.
- [x] Run DS/account regression suites and fix actual differences against the original Legacy contract.
- [x] Build sample catalog with 121 documented interactive APIs, 10 passing catalog tests and emulator checks of list, field variants and local HTML.
- [x] Extend and exercise DS ownership guard (10 fixtures rejected/accepted correctly, baseline restored).
- [x] Review changes and run full qualityGate: 2,980 tests / 586 suites passed, fresh global LINE coverage 95.48% (42,452/44,460), 66 modules including the sample.
- [x] Record measurements, limits, APK/report locations and usage docs in TEST_COVERAGE.md, CAIXA_DS.md and the sample README.
