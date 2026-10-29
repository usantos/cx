# Tickets history responsibility extraction

> Execute inline with superpowers:executing-plans. Preserve the shared dirty checkout and do not commit/push (SPEC19.11).

**Goal:** Separate coordination, bets, purchases, filters, pagination and failure recovery while keeping ViewModel, public interfaces, StateFlow and behavior unchanged.
**Architecture:** TicketsHistoryCoordinator remains a compatible facade. Six internal collaborators share the original MutableStateFlow and scope/dispatchers; Bets handles config/modalities/history; Purchases handles month catalog/reload; Pagination handles first/append queries and merge flags; BetFilters/PurchaseFilters handle editing/catalog UI; Recovery retains the two retry choices. Keep the injected failure callback from the existing ViewModel.
**Spec:** SPEC.md §§2,3.1,4,6,8,9,12–17 and current user request.
**Tech:** Existing Kotlin/coroutines/JUnit/Compose/JaCoCo; no dependencies added.

## Boundaries and files
- Modify TicketsHistoryCoordinator.kt only in feature/tickets production: same constructor/methods/Unit returns; delegates and construction.
- Add TicketsHistoryBets.kt: loadBets/applyBetFilters/loadBetsWith; preserve each session check and config/modalities call order.
- Add TicketsHistoryPurchases.kt: loadPurchases/reloadPurchases; same month-selection behavior and call into Pagination.
- Add TicketsHistoryPagination.kt: loadMorePurchases/loadPurchasesWith; same in-coroutine filter read, offset, size10, dedup/order, flags and session fences. Private page constant moves here unchanged.
- Add TicketsHistoryBetFilters.kt: visibility, selection/toggles/default clearing; callback to bets apply at the same moment.
- Add TicketsHistoryPurchaseFilters.kt: month/method/status editing, filter catalog requests and apply/clear; callback to purchases reload at the same moment. Preserve the existing checks, without adding/removing session guards as part of extraction.
- Add TicketsHistoryRecovery.kt: same catalog-dependent retry decisions using the existing Bets/Purchases instances.
- Update existing TicketsViewModelTest.kt before extraction with three public behavioral characterizations.
- Add six coverage patterns in feature/tickets/build.gradle; preserve all existing patterns, connected inputs and minimum0.95 exactly.
- ViewModel/UiState/Domain/Data/UI/resources/Hilt/global Gradle remain byte-identical to baseline.

## Review focus
1. Session expiry before/after catalog/page suspension must prevent the same private requests/publications as before.
2. Append failure must retain accumulated rows and retry the same offset; success deduplicates by ID in original order and uses received size10 for continuation.
3. Apply/reload/pagination read current filters inside the original coroutine, not an earlier captured snapshot.
4. Retry chooses config/month initialization only when the corresponding original state is missing; failure messages/flags use the same callback.
5. Filter catalog order, status reset, default/fallback periods/months, no new coroutine/scope/state owner and cancellation propagation remain identical.

## Tasks
- [x] Snapshot files/APIs; add three characterizations (late append after expiry, cached-catalog retry, queued filter reads) and run Tickets/Domain JVM on original. Expected GREEN for existing behavior; do not invent a functional change for RED.
- [x] Extract six collaborators, add their coverage patterns, and run all Tickets JVM again. Expected GREEN with same API and original ViewModel source.
- [x] Run Tickets check including all Android/coverage/lint, Domain check, AppPrdDebug assemble/JVM/lint and ten original gates. Preserve native-font reference and report conditional geometry skips without weakening tests.
- [x] One fresh read-only review per executing-plans; material findings require regression RED→GREEN. Update five root reports with actual results and scope/limits; keep changes uncommitted.

## Execution
Root owns edits/Gradle/owned temporary emulator5562. Existing emulator5580 (cxguard-api-be60130ac53c) is unrelated and must not be mutated. Use newly created SimloTicketsHistory20261019 with local legacy reference installed; existing SimloComposeGates holds a modern package and must not be overwritten. Evidence under build/verification/tickets-history-20261019/; compare current round snapshots, not HEAD, to preserve prior authorized work. No private login/transactions, no gates/oracles weakened, no broad unrelated refactor.

Completed: final-validation.log PASS4m2s;391JVM,41Android passed/2conditional geometry skips; Tickets97.50%, history236/236lines. AppPrdDebug assemble/JVM/lint and ten gates pass. Fresh review no actionable findings. Source/API and additive coverage checks pass. No commit/push; temporary AVD cleanup follows.
