# Home legacy composition extraction

> Execute inline using superpowers:executing-plans; preserve the dirty checkout and do not commit/push (SPEC19.11).

**Goal:** Separate composition, top bar, carousel, cards/actions, shortcuts, campaign and cart summary without changing geometry, animation, callbacks or behavior.
**Spec:** SPEC.md §§2,3.1,6,7,12–17 and the current user request.
**Architecture:** Keep HomeLegacyContent's declared signature, presentation/action data classes, Scaffold/BoxWithConstraints, empty-list return, key(size), rememberPagerState, LaunchedEffect(requestedModalityId), selected lottery and width calculation in the original owner. Extract stateless Composables; BoxScope receivers preserve alignment and emit no extra layout containers. Preserve draw order quick-access → campaign → carousel-offset Box; inside carousel preserve pager → conditional card actions → cart summary.
**Tech:** Existing Compose/Kotlin/JUnit/Android tests; no dependencies or coverage/gate changes.

## Files
- Modify app/.../HomeLegacyContent.kt: composition and original data classes/API only.
- Add HomeLegacyTopBar.kt: original Column/Row/menu/notifications/ribbon/clover.
- Add HomeLegacyCarousel.kt: original offset Box/HorizontalPager with same scrolling/size/padding/spacing and footer gate.
- Add HomeLegacyCards.kt: existing LotteryCard invocation/scale and original pool/bet action boxes, texts/colors/modifiers.
- Add HomeLegacyQuickAccess.kt: BoxScope AnimatedVisibility with original timing/easing/translation and callbacks.
- Add HomeLegacyCampaign.kt: BoxScope AnimatedVisibility with original campaign panel/timing.
- Add HomeLegacyCartSummary.kt: BoxScope visibility and existing CaixaDSCartSummary, same parent and alignment.
- Add HomeLegacyContentUiTest.kt: selected modality survives presentation recomposition; card/cart callbacks/bounds and expanded footer visibility.
- Extend HomeScreenCoordinationUiTest.kt with controlled classic quick/campaign animation frame captures; preserve existing six cases/oracles.
- Keep HomeRoute, HomeScreen, existing LotteryCard, mappers, ViewModels/UiStates/StateFlows, Domain/Data, resources/manifests/Gradle/security unchanged.

## Review focus
1. Remember/key/LaunchedEffect stay under identical empty-list/key conditions; requested modality and recomposition keep pager selection.
2. Parent BoxScope, sibling draw order, modifier chains/dimensions/insets and center padding remain identical; no wrapper layout changes constraints.
3. Quick/campaign enter/exit delays, duration, easing and translation and external route animations stay identical.
4. Expanded result/quick/campaign/preparing/open state keep scroll and footer/click gates; callbacks retain exact page/selected lottery.
5. Cart visibility follows original total rule, remains inside offset Box and beneath the same footer gate; no new state/scope/data access.

## Tasks
- [x] Snapshot all source/APIs; run App/Home JVM and targeted Android on original with new characterizations; collect baseline captures/bounds/animation frames.
- [x] Extract six stateless files, compare literal bodies/state/effect/modifier/animation tokens and declared APIs; run same Android/JVM suite and compare all captures/bounds to baseline (no rebaselining or tolerances added).
- [x] Restore default build flags/identity for AppPrdDebug assemble/JVM/lint, Home check/cov and ten existing gates; one independent final read-only review, material findings RED→GREEN, update five reports.

## Verification and constraints
Use owned temporary SimloHomeLegacy20261019/5560, API36/1280×2856/480dpi/font1.0, same before/after. Targeted app Android uses an init script identity solely for validation and -PhomeV2=false to execute classic branch; never persist these settings, and restore default build flags for final gates. Target classes HomeScreenCoordinationUiTest,HomeDrawerUiTest,HomeLegacyContentUiTest. Existing capture test writes six settled scenes; new tests capture cart/selection and eight frozen-clock intermediate frames. Compare independently recorded before/after PNG pixels and measured bounds; differences require diagnosis, not adjusted baselines. Evidence build/verification/home-legacy-20261019/. No global/private homologation claims.
