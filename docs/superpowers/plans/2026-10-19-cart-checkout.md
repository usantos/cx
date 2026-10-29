# Cart checkout responsibility extraction

> Execute inline with superpowers:executing-plans. Preserve the existing dirty checkout and do not commit/push (SPEC19.11).

**Goal:** Separate checkout coordination, location, permissions, recent purchase, validation, notices and redirects while preserving public interfaces, StateFlows and behavior.
**Architecture:** CartCheckoutCoordinator remains the compatible facade. Six internal collaborators share the original MutableStateFlow and injected scope/dispatchers. Location owns the pending account and its try/catch/finally boundary, including downstream recent-purchase/validation calls. Recent purchase owns its captured cart/account continuation. No new StateFlow, scope, interface, use case, persistence or API.
**Spec:** SPEC.md §§2,3.1,4,6,8,9,12–17 and the current user request.
**Tech:** Existing Kotlin, coroutines, Hilt host, JUnit, Compose Android and JaCoCo; no dependencies added.

## Files and contracts
- Modify feature/cart/.../CartCheckoutCoordinator.kt: keep constructor, methods and Unit/Boolean returns; account/expiry refresh and entry guards remain here.
- Create CartCheckoutLocation.kt: pending account, country check, retry/block/dismiss and the same cancellation/finally boundary.
- Create CartCheckoutPermissions.kt: consent, permission recording/launch consumption/result, settings and launch failures; callbacks to location block/retry.
- Create CartCheckoutRecentPurchase.kt: current-total history check, review and continuation; callbacks to validation and facade retry.
- Create CartCheckoutValidation.kt: login gate, validation result/technical failure and ready state.
- Create CartCheckoutNotices.kt: map rejection with original clock/network, identity-based dismiss/accept.
- Create CartCheckoutRedirects.kt: consume payment/account/redirect events exactly once with original reference checks.
- Update CartViewModelTest.kt before extraction: two characterizations for suspended permission recording and cancellation in downstream history/validation.
- Keep CartViewModel, CartUiState, Routes/UI, Domain/Data/network, manifests, resources, Gradle patterns/minima unchanged.

## Review focus
1. Preserve guard order: account → expired reservation refresh → location → recent purchase → login/validation, including empty carts and refresh failure.
2. Preserve cancellation from recent-purchase/validation under the location try/catch/finally, and the different direct recent-purchase continuation path.
3. Permission recording must finish before publishing Android action; repeated or irrelevant callbacks must not duplicate checkout or permission requests.
4. Recent-purchase consent applies to the captured cart only; changed cart restarts all entry gates.
5. Notice/redirect identity and consumption, dynamic connectivity, payment-ready flags and expiration-removal loading remain identical.

## Tasks
- [x] Capture baseline files and APIs; add the two characterizations and run all Cart JVM tests against the original coordinator. Expected: GREEN; pure extraction introduces no new functional requirement requiring a RED behavior change.
- [x] Extract the six collaborators and wire the compatible facade without a second state owner or dependency cycle; run all Cart JVM tests again. Expected: GREEN.
- [x] Run Cart check (JVM+all Android+lint+coverage≥95.01%), Domain tests, App PrdDebug JVM/lint/assemble and ten existing gates. Verify APIs and all protected files against this round's baseline.
- [x] Obtain one fresh read-only final review per executing-plans, address material findings with regression RED→GREEN, document exact results in five root reports and leave changes in the current checkout.

## Execution rulings
Use current codex/migracao-compose checkout: previous work is deliberately uncommitted. Compare to build/verification/cart-checkout-20261019/baseline, not HEAD. Root alone owns edits, Gradle and emulator. Characterizations run on the original before literal extraction; preserve any preexisting behavior outside the authorized scope. No weakening tests, coverage inputs/patterns/minima or gates. External/private homologation and known visual profile limits remain unchanged.

Completed: validation.log PASS5m18s (Cart/Domain checks,65Android,422JVM,AppPrdDebug assemble/lint,ten gates). Cart97.36%, checkout209/209lines; thresholds unchanged. Fresh independent review: no actionable findings. Protected source/API comparison passes. No commit/push; global/private limits unchanged.
