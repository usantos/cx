# Bet detail, receipts and rules implementation plan

> **For agentic workers:** Use superpowers:executing-plans for detail/receipts; independent Home/rules tasks are delegated with dispatching-parallel-agents. Verify each change before integration.

**Goal:** Match the installed legacy bet-detail screen, restore PDF receipts across modalities, display real game rules, and toggle Home's expansion label.
**Architecture:** Preserve existing repositories and use cases. Honor the native Disable-Crypto receipt contract; use an Android content URI for direct PDF opening. Port the native detail layout and keep existing callbacks. Share canonical modality help and consume actual contest metadata.
**Tech Stack:** Kotlin, Compose, OkHttp/Retrofit, Android FileProvider, JVM and Compose instrumentation tests.
**Spec:** User screenshots and requests in this chat; canonical native XML/controller under /Users/ubiracysantos/Projects/cx-filabra-offline/app/src/main.

## Global constraints
- Preserve preexisting dirty changes and real account/cart data; no betting/payment mutations.
- Keep Chucker debug-only and current network redaction.
- All modality paths share receipt transport; special numeric, column, clover and sports selections retain their data.

## Review focus
- Disable-Crypto must preserve unencoded receipt URLs while ordinary APIs remain encoded.
- Paid-prize receipts choose the prize endpoint; regular bets choose the bet endpoint.
- Missing PDF handlers and file failures must present an error rather than silently do nothing.
- Scroll content and fixed footer must remain reachable without overlap on small displays.
- Rules and contest metadata must follow selected modality; expanded Home labels must toggle both directions.

## Task 1: Receipt transport and opening
- [x] Add failing wire-contract tests for both receipt endpoints.
- [x] Honor Disable-Crypto in SilceTransportInterceptor; verify existing transport tests.
- [x] Replace save-first UI with cached FileProvider PDF opening, handling failures visibly using the installed PDF viewer.
- [x] Exercise receipt downloads and intents for all modality fixtures, plus invalid response/no-handler cases.

## Task 2: Native detail parity
- [x] Capture a native fixture with the same Lotofácil data and inspect XML/layout bounds.
- [x] Match toolbar, header, number marks, native badges, receipt action and 172dp footer.
- [x] Verify geometry, scrolling and modality-specific selection data through Compose tests and paired captures.

## Task 3: Rules and Home
- [x] Replace static lorem/dates with shared canonical rules and real contest metadata; unit-test all modalities.
- [x] Toggle Home label to Ver menos when expanded; retain other Ver todas links.
- [x] Integrate host wiring and run combined checks.

## Verification
- [x] JVM suites for network, data, tickets, support, Home and touched shared modules.
- [x] Tickets UI tests and paired native/Compose screenshots; APK assembly and targeted live PDF generation.
- [x] Fresh review, install real debug APK preserving Chucker, report runtime limitations explicitly.

## Verified outcome (2026-10-15)
- 650 JVM tests passed across network, data, tickets, support, betting, presentation and app.
- 12 tickets instrumentation tests and 4 app instrumentation tests passed on dedicated emulator-5558. The paired capture test also passed with settled Lotofácil draw data.
- Network (95.01%), tickets (95%) and support (95%) configured coverage gates passed.
- Fresh detail review corrected Super Sete positional hits, per-contest Teimosinha prize status, Lotogol scores and fallback result navigation.
- Paired native/Compose captures: build/verification/bet-detail-parity-20261015/{native-detail-initial,compose-detail-initial,compose-detail-receipt}.png.
- Installed real PRD debug build with Chucker on emulator-5556 (Compose PRD (validacao)). Real authenticated Lotofácil list/details returned 200; receipt returned 200 and opened in Android PDF viewer.
- Live Home expanded label displayed Ver menos. Rules displayed actual Lotofácil content and current contest metadata without lorem ipsum.
- Receipt fix requires both plain URL and legacy Cookie security=false / Subcanal 3 / JSON headers. Native ServiceConnection mutates a shared header map before its direct StringRequest clones it; initial plain URL alone returned 403. Added a failing wire regression then preserved those headers, obtaining live 200.
- Live account contains Lotofácil bets; other modality receipt actions and special selections were verified with fixtures, not live purchased bets. Prize-payment actions were not exercised.
