# App accessibility and complete architecture audit Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:executing-plans for root integration; superpowers:dispatching-parallel-agents for independent owners.

**Goal:** Implement the accepted native accessibility criteria across App/Core/all14 features and correct all confirmed architecture findings, preserving headers/baselines, then update the branded guide and Samsung.

**Architecture:** Keep MVVM and typed local Redux stores; business rules in Domain, effects/lifecycle in ViewModels/coordinators, platform/interaction in Views and Data adapters. Accessibility primitives stay in CaixaDS; feature-specific meaning remains in its View. No global state or new module.

**Tech Stack:** Existing Kotlin/Compose/BOM, ReduxKotlin0.6.2 JVM17, Robolectric/Android instrumented tests; official Compose accessibility test framework only if required/compatible.

**Spec:** docs/superpowers/specs/2026-10-27-app-accessibility-architecture-design.md. Builder check by DS auditor found no blocking conflict. User confirmed WCAG2.2AA + Android criteria. Proceed within the user's explicit implementation authorization.

## Global Constraints
- Preserve header visual/style/normal-scale geometry; protected CurrentCartScaffold and CaixaDSScreenComponents hashes EXACT. Natural user font scaling remains enabled.
- No baseline/suppression/exclusion expansion, threshold reduction, mass formatting, commit/push, unrelated cleanup or financial operation.
- Preserve source checkout and all existing meaningful assertions; body contrast and adaptive font-scale behavior may have stronger new expectations, not weaker old tests.
- Root alone runs Gradle sequentially. Agent ownership disjoint; no phone operations by agents.
- Measure current Samsung cart and settings before installation/testing, restore temporary settings, install same main APK without clear-data. Controlled tests replace real authentication/financial flows.

## Review Focus
- Filled/empty/error/password/read-only inputs keep a stable name, editing actions and privacy, without duplicate speech targets.
- Click/keyboard/accessibility activation of a choice emits exactly once, retains selection state and respects disabled status.
- FontScale2/short viewport retains content and reachable actions; no inverse scaling or clipped meaningful results, no header style rewrite.
- Canceled/stale Home/Support/notification requests never overwrite current owner/session/selection; stores/jobs close correctly.
- Extracted pricing/eligibility/expiry rules preserve money/time behavior, exact-price missing cases and inclusive server deadlines; no duplicate domain policy.

## Tasks and ownership

### 1. Baseline, source audit and criteria (root + read-only auditors)
- [x] Source-before fingerprint equals previously verified4272-file checkout; retain previous3223-test baseline as history, not new execution.
- [x] Shared DS and all14 feature audits, inventories and confirmed findings with evidence under build/verification/a11y-full-audit-20261027/.
- [x] Complete App/Core/graph/DI/lifecycle/platform boundary audit; record actual findings separately from accepted platform adapters and theoretical risks.
- [x] Design, primary references, accepted criteria and builder review; no further approval stall for already authorized reversible implementation.

### 2. Shared accessibility contracts (DS agent)
Files: bounded list in ds-audit.md (15 shared component/theme files, resources and new focused tests); protected headers excluded.
- [x] Add compile-ready behavioral RED tests for actual current controls, errors, links, large text and contrast. Root runs focused three JVM classes before product edits.
- [x] Remove font scaling locks, associate input labels/errors, expose one selected/toggled owner, fix duplicate actions, scrollable dialogs/actions, wheel operation/state and local body contrast.
- [x] Publish public `AnnotatedString.withCaixaDSAccessibleLinks(onClick:(String)->Unit):AnnotatedString` in core.designsystem.text, preserving text/spans and delegating confirmation/trust policy to callback. Use LinkAnnotation.Clickable. New HTML external handling must reject unsafe schemes.
- [x] Test native WebView scaling/semantics; implement explicit zoom only with demonstrated need. Do not replace document subtree with one contentDescription.
- [x] Green focused DS tests and all existing DS unit/callback/1x layout contracts, scoped format/Detekt; freeze before integration.

### 3. Features A accessibility + architectural correction (agent A)
Scope: Account, Betting, Cart, Home, Marketplace, Filter, Notifications; all own layers/tests. Root/DS files excluded.
- [x] RED semantic/interaction tests for consent+terms, combo identity, notification switch/expand state, betting Lotogol/Rapid/modality choices, cart/home scaling, marketplace quantities/filter state; one explicit accessibility matrix entry for every scoped feature.
- [x] Domain price/selection/expiry tests and behavior baseline; extract duplicated rules into existing Domain owners, preserve intentional missing-price behavior and server timezone/deadline semantics.
- [x] Move Home results cache/request/error ownership into Home ViewModel/Redux with request identity and lifecycle; remove foreign ViewModel orchestration from the View. Review expiry refresh scheduling and move effect ownership when it contains application orchestration; pure UI countdown stays View.
- [x] Notification preferences mutation/reload ordering: delayed snapshot RED, then serialize/guard authoritative publication without stale overwrite or dropped updates.
- [x] Fix feature-local semantics/layout using shared primitives/helper; preserve old callbacks/assertions and protected Cart scaffold. Contextual names and system-scale support, not decorative descriptions on all images.
- [x] Green all scoped unit/UI tests; freeze manifest. Native App/DS/Retail compilation is recorded in whole-project integration.

### 4. Features B accessibility + architectural correction (agent B)
Scope: Onboarding, Payment, ResponsibleGaming, Results, Retail, Support, Tickets; all own layers/tests. Root/DS files excluded.
- [x] RED tests for unlabelled form/Pix choices/search fields, tab state, meaningful SuperSete/Loteca/award/result grouping, accessible links/keyboard; explicit matrix for all7 features.
- [x] Support stale tab/section response RED using delayed completions; cancel/key requests and reduce only current results, including non-cooperative cancellation.
- [x] Move Pix pending policy out of Tickets Screen into the appropriate pure Domain rule and update VM-owned coordinators; preserve expiry semantics.
- [x] Fix feature meaning/forms/links/large-font results using shared DS/helper; keep trusted link validation/confirmation at existing border.
- [x] Green scoped unit/UI/recreation/IO contracts; freeze manifest. Native App/DS/Retail compilation is recorded in integration.

### 5. App/Core architecture, guardrails and whole-project integration (root)
- [x] Finish audit of all68 module ownership/dependencies and App/Core concrete runtime boundaries; add RED architectural regressions for confirmed owner violations (not arithmetic heuristics that ban legitimate UI formatting).
- [x] Correct remaining confirmed root-owned findings, preserve existing platform/DI composition roles; strengthen generic boundaries without new exception/baseline growth.
- [x] Run per-owner RED/GREEN sequentially; fix actual compilation/behavior problems without weakening tests. Keep source/evidence ledger current.
- [x] Full allUnitTests, all affected AndroidTest compilation, focused Ktlint, Detekt/Lint/gates, main PRD build; verify frozen source and all protected hashes.
- [x] Independent fresh review of final architecture/a11y implementation and acceptance matrix; one regression-backed fix pass for important issues, no repeated review loop.

### 6. Runtime acceptance and delivery (root)
- [x] Automate controlled real screens/components across all14 features for names/roles/states, interaction and relevant large-font/keyboard cases; instrument shared WebView/DOM/provider and receipt/recreation paths natively on emulator. Separate Robolectric from native execution and Samsung public flows in the matrix.
- [x] Samsung normal startup/public navigation, current cart preserved; test official accessibility services available, font-size adaptation and focus/keyboard. If consent/unlock is needed, ask only after APK is concrete; never bypass device protection or financial gates.
- [x] Report full feature matrix with static/automated/instrumented/Samsung/limitations separated; do not call source scans or tree assertions a formal WCAG/TalkBack certification.
- [x] Update architecture/MVVM/Redux docs, accessibility guide and CAIXA-template PDF with actual current results. Render all pages, inspect detail/contact sheets/links, request final artifact opening, update Samsung and deliver.

## Rulings and ledger
- Existing dirty codex branch retained to preserve authorized migration; no worktree/reset/commit introduced.
- Baseline/config hashes and header source hashes are immutable. Accessible BODY text colors may change locally as required by contrast; this is not permission to repaint headers.
- Default Material semantics and automatic expanded touch targets are considered before declaring defects. Explicit shared UI composition across Presentation modules is not automatically a Clean Architecture violation; foreign ViewModel/business orchestration is.
- External SDK/browser/server content cannot be silently claimed remediated; test/adapt owned borders and record concrete limits. No live Firebase configuration/financial/auth mutation for tests.

## Encerramento verificado

PDF final: 258 páginas renderizadas e inspecionadas; 152 marcadores com destinos válidos e 24 links de sumário. Sem páginas vazias ou conteúdo fora dos limites. SHA-256 c8b6b0b5636b7571c4b2d8c940e870a43f43e513b1156fb83d84aa6a0f310d98. Abertura solicitada ao Codex; retorno da interface: queued.

3.325 unitários,19 instrumentados e26+2+1 verificações Samsung aprovados;0falhas/erros/ignorados nas seleções finais. Fontes4328/SHA584730664fe9a11e6c52c625ce7ae94dd1c99fc808370ded37d635f921456a38. Detalhes, histórico RED e limites em docs/technical-guide/verification.json e build/verification/a11y-full-audit-20261027/. Sem commit/push, baseline ampliada ou alteração de headers.
