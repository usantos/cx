# Remove translucent screen navigation

User direction: use legacy-style direct screen changes, without transparency, on Samsung; preserve headers and protected baselines. Execute inline in existing migration checkout; no commit/push.

Cause: navigation-compose 2.10.0 DefaultNavTransitions.android.kt defaults to 700ms fadeIn/fadeOut. Predictive Back has independent fade/scale defaults. AppNavHost leaves all unspecified. No feature graph overrides transitions.

Design: shared app navigation host wrapper with explicit EnterTransition.None/ExitTransition.None for forward, pop and predictive pop. Reuse all graph registration and startup gates. Do not modify screen contents, loading behavior, system activity animation settings, headers or baselines. The wrapper also allows a small actual-navigation regression graph to use the production policy.

Plan:
- [x] Add a navigation regression that pauses the Compose clock and proves the outgoing screen is not retained through a fade in forward and Back navigation. Observe failure with current default behavior.
- [x] Explicitly disable the six host transition callbacks in the shared wrapper; wire AppNavHost to it. Confirm regression and full app suite, lint, Detekt, formatting and protected hashes.
- [x] Update same Samsung PRD app and record opening/Back of Results, modalities, Cart and another public screen. Inspect short video frames, preserve all shortcuts and collect fresh final review.

Ruling: preserve deliberately dirty checkout and avoid commits per repository policy. No new dependencies or baselines.


Evidence: opening/Back pixel tests at 64ms both failed with mixed colors under AndroidX defaults, passed with explicit None callbacks. Full PRD app suite: 365 tests, 0 failures/errors/skipped. PRD build/lint/Detekt/formatting and architecture passed. 37 baseline/config hashes and both protected header file hashes unchanged.

Final review: clean (wrapper/integration/regression scope); reviewer explicitly notes no claim about exact first frame or actual predictive gesture testing. Six callbacks are configured; Samsung validation uses the existing three-button navigation.

Diagnostic test ruling: a predictive-dispatcher test did not distinguish defaults from None in Robolectric, so it was discarded rather than treated as evidence. The final two pixel regressions cover real forward and pop transitions; gesture configuration is reviewed statically.

Samsung final: 8/8 opening/Back actions passed through normal visitor startup. Video frames confirm the old cross-fade and opaque replacement. Cart 4 bets/R$19 retained. See build/verification/legacy-transitions-20261026/report.md. No remaining required work.
