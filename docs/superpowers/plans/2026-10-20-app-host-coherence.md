# App Host Coherence Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task, then use one fresh reviewer for the complete change. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make `app` a thin Android host that owns global composition, initialization and navigation while each feature owns its navigation dependencies and CORE/feature modules own implementation.

**Architecture:** Keep the existing 13 public `*NavigationDependencies` contracts and graph functions, but provide their implementations and Hilt bindings in the owning feature modules. Runtime flags and endpoint/version values are supplied through a small immutable host-configuration contract; Compose callbacks are implemented in their owning features. Replace active use of the 115-property `AppContainer` with typed feature navigation inputs plus narrow host initialization/session/environment inputs; retain the exact legacy facade required by the existing compatibility inventory. Remove direct app-to-feature-Data edges after verifying Hilt aggregation through feature modules.

**Tech Stack:** Kotlin, Android Gradle Plugin, Jetpack Compose Navigation, Hilt/KSP, Python architecture gates, existing Gradle verification tasks.

**Spec:** `docs/superpowers/specs/2026-10-20-app-host-coherence-design.md`; governing requirements in `SPEC.md`.

## Global Constraints

- Preserve public Kotlin/JVM contracts that the compatibility inventories require; do not create duplicate bindings.
- Preserve ViewModel constructors, StateFlows, routes, saved state, payloads, persistence, security, scopes, callback ordering and flavor behavior.
- Keep Domain independent of Android/Compose/Data/Network/Auth implementations.
- Keep feature implementation and bindings in the owning feature; keep shared infrastructure in CORE.
- Preserve all existing checkout work; do not reset, clean, commit, push or publish.
- Serialize Gradle executions and do not edit source/build inputs while a validation task is running.
- Do not claim physical/private homologation from local tests.

## Review Focus

- A Hilt feature graph fails to aggregate when its `:data` dependency stops being a direct `app` dependency; the app's real flavor compile must exercise all installed modules.
- An old public `AppContainer` constructor/getter or app DI factory changes JVM shape; compare compiled public contracts and maintain only enumerated forwarding facades.
- A navigation dependency implementation changes a feature's scope, owner, initial state or callback order; compare each graph's existing route and ViewModel construction.
- Host runtime initialization moves earlier/later when `AppContainer` stops being injected; characterize invocation count and ordering before moving it.
- Session identity becomes stale during async completion/logout/restart after dependency separation; preserve the current lease guard and run the existing navigation/session tests.

---

### Task 1: Characterize the current host and enforce the target boundary

**Files:**
- Modify: `scripts/test_app_architecture.py`
- Modify: `scripts/check-app-architecture.py`
- Read/record: `app/src/main/java/br/gov/caixa/loterias/apostas/AppContainer.kt`, `navigation/AppNavHost.kt`, `composition/AppContent.kt`, `MainActivity.kt`, `app/build.gradle`
- Test: `scripts/test_app_architecture.py`

**Interfaces:**
- Consumes: current source ownership and the accepted app-host spec.
- Produces: structural tests that reject active `AppContainer` wiring, direct `app` dependencies on `feature:*:data`, feature use-case/repository bindings in app composition, and unregistered root-package implementation; accepts only exact compatibility facades.

- [ ] Add failing gate cases `test_active_app_container_reference_is_rejected`, `test_app_cannot_depend_on_feature_data`, `test_app_di_cannot_bind_feature_repository_or_usecase`, and `test_compatibility_facade_must_forward_to_registered_owner`.
- [ ] Run `python3 scripts/test_app_architecture.py`; confirm the new cases fail against the current host.
- [ ] Extend `scripts/check-app-architecture.py` to implement those rules using the existing compatibility inventories and Gradle dependency declarations.
- [ ] Run `python3 scripts/test_app_architecture.py` and `python3 scripts/check-app-architecture.py`; expect all policy tests to pass and the project gate to remain red only for the not-yet-migrated production sources.
- [ ] Record current public API signatures and Hilt binding/scoping inventory under `build/verification/app-host-coherence/` without staging or modifying existing evidence.

### Task 2: Make navigation dependency implementations feature-owned

**Files:**
- Create: `feature/account/src/main/java/br/gov/caixa/loterias/feature/account/navigation/AccountNavigationDependenciesAdapter.kt`, `AccountSessionDependencies.kt`, `AccountSessionDependenciesAdapter.kt`, `AccountEnvironmentDependencies.kt`, `AccountEnvironmentDependenciesAdapter.kt`.
- Create: `feature/betting/src/main/java/br/gov/caixa/loterias/feature/betting/navigation/BettingNavigationDependenciesAdapter.kt`.
- Create: `feature/cart/src/main/java/br/gov/caixa/loterias/feature/cart/navigation/CartNavigationDependenciesAdapter.kt`.
- Create: `feature/home/src/main/java/br/gov/caixa/loterias/feature/home/navigation/HomeNavigationDependenciesAdapter.kt`.
- Create: `feature/marketplace/src/main/java/br/gov/caixa/loterias/feature/marketplace/navigation/MarketplaceNavigationDependenciesAdapter.kt`.
- Create: `feature/notifications/src/main/java/br/gov/caixa/loterias/feature/notifications/navigation/NotificationsNavigationDependenciesAdapter.kt`.
- Create: `feature/onboarding/src/main/java/br/gov/caixa/loterias/feature/onboarding/navigation/OnboardingNavigationDependenciesAdapter.kt`.
- Create: `feature/payment/src/main/java/br/gov/caixa/loterias/feature/payment/navigation/PaymentNavigationDependenciesAdapter.kt`.
- Create: `feature/responsible-gaming/src/main/java/br/gov/caixa/loterias/feature/responsiblegaming/navigation/ResponsibleGamingNavigationDependenciesAdapter.kt`.
- Create: `feature/results/src/main/java/br/gov/caixa/loterias/feature/results/navigation/ResultsNavigationDependenciesAdapter.kt`.
- Create: `feature/retail/src/main/java/br/gov/caixa/loterias/feature/retail/navigation/RetailNavigationDependenciesAdapter.kt`.
- Create: `feature/support/src/main/java/br/gov/caixa/loterias/feature/support/navigation/SupportNavigationDependenciesAdapter.kt`.
- Create: `feature/tickets/src/main/java/br/gov/caixa/loterias/feature/tickets/navigation/TicketsNavigationDependenciesAdapter.kt`.
- Create: Account and Onboarding presentation composable files for the feature-owned slots listed below.
- Create: `core/navigation/src/main/java/br/gov/caixa/loterias/core/navigation/HostFeatureConfiguration.kt`.
- Create: `app/src/main/java/br/gov/caixa/loterias/apostas/di/HostFeatureConfigurationModule.kt`.
- Create Account-owned `AccountSessionDependencies` and `AccountEnvironmentDependencies` contracts/adapters for account operations currently read by host session/environment code.
- Modify: the owning feature's existing `di/*Module.kt` files for bindings.
- Move the feature-owned Compose slots currently forwarded by app (`LoginEnvironmentSelector`, `StartupStatusDestination`, `SimulationRecoveryPanel`) to Account/Onboarding presentation and update their feature adapter implementation.
- Test: feature module compilation and existing navigation/ViewModel tests.

**Interfaces:**
- Consumes: existing `*NavigationDependencies` interfaces and their current properties unchanged.
- Produces: one Hilt binding from each existing interface to an adapter in its owning feature; adapter constructor parameters use only already-bound Domain operations, CORE contracts and `HostFeatureConfiguration` for `appVersionName`, SSO endpoint/client/referrer, environment visibility/label and mock flags. Account also owns the session/environment contracts consumed by global host coordinators. Compose callbacks delegate to presentation in their owning features.

- [ ] Define `HostFeatureConfiguration(appVersionName: String, accountReferrerUri: String, ssoAccountUrl: String, ssoClientId: String, environmentSelectorVisible: Boolean, homeNavigationEnvironmentLabel: String?, mockBackendEnabled: Boolean)` in `core:navigation`; bind its values from the existing `BuildConfig`, `EnvironmentSettings` and effective environment in app DI.
- [ ] Verify each flavor's bound configuration by compiling `prdDebug`, `tqsDebug` and `automacaoDebug`; do not reimplement URL/flag values in feature code.
- [ ] Define Account-owned `AccountSessionDependencies` for authentication, logout/cleanup, completion verification, authenticated identity and dispatchers; define `AccountEnvironmentDependencies` for persisted environment settings and the existing `ChangeEnvironment` operation. Bind both to adapters in Account DI.
- [ ] For each of the 13 contracts, add a compile-oriented architecture fixture asserting the adapter is in the feature that declares the interface and that its constructor exposes no `AppContainer` dependency.
- [ ] Run `python3 scripts/test_app_architecture.py` and the feature architecture tests; confirm new ownership checks fail before adapters are added.
- [ ] Implement each adapter as a constructor-injected immutable holder; preserve all current values and defaults, read build-variant values only from `HostFeatureConfiguration`, and move the `visualConfirmedBet` fixture delegation into the Tickets adapter that already depends on Tickets Data.
- [ ] Move `LoginEnvironmentSelector` into Account presentation and `StartupStatusDestination`/`SimulationRecoveryPanel` into Onboarding presentation; keep their old app FQNs as registered forwarding facades only where the compiled API inventory requires them.
- [ ] Add one `@Binds` binding per interface to its existing feature Hilt module, retaining the current scope (unscoped unless the current behavior specifies otherwise) and adding no duplicate provider.
- [ ] Compile each feature's existing main source set and run its navigation/ViewModel unit tests; expect no changes to ViewModel constructors or StateFlows.

### Task 3: Replace the app's all-use-case navigation parameter with typed feature inputs

**Files:**
- Create: `app/src/main/java/br/gov/caixa/loterias/apostas/navigation/AppFeatureNavigationDependencies.kt`
- Modify: `app/src/main/java/br/gov/caixa/loterias/apostas/navigation/AppNavHost.kt`
- Modify: `app/src/main/java/br/gov/caixa/loterias/apostas/LoteriasApp.kt`
- Modify: `app/src/main/java/br/gov/caixa/loterias/apostas/MainActivity.kt`
- Modify: app navigation tests and `config/app-compatibility.json` only for exact retained facades.

**Interfaces:**
- Consumes: the 13 feature-owned navigation contracts from Task 2.
- Produces: injectable `AppFeatureNavigationDependencies` with one typed property per feature contract; `AppNavHost` and `LoteriasApp` consume it instead of `AppContainer` for feature graph registration.

- [ ] Extend the app architecture test to assert `AppFeatureNavigationDependencies` has exactly these properties: `account: AccountNavigationDependencies`, `betting: BettingNavigationDependencies`, `cart: CartNavigationDependencies`, `home: HomeNavigationDependencies`, `marketplace: MarketplaceNavigationDependencies`, `notifications: NotificationsNavigationDependencies`, `onboarding: OnboardingNavigationDependencies`, `payment: PaymentNavigationDependencies`, `responsibleGaming: ResponsibleGamingNavigationDependencies`, `results: ResultsNavigationDependencies`, `retail: RetailNavigationDependencies`, `support: SupportNavigationDependencies`, `tickets: TicketsNavigationDependencies`; assert default route `account/login` and confirm failure while `AppNavHost` still accepts `AppContainer`.
- [ ] Run the focused app navigation test; confirm it fails while `AppNavHost` still requires `AppContainer`.
- [ ] Add the typed Hilt-constructible `AppFeatureNavigationDependencies` and change only graph wiring in `AppNavHost`; keep route registration order, arguments, `HomeSection` contents and session callbacks unchanged.
- [ ] Inject the typed navigation input at the Activity boundary and thread it through `AppContent`/`LoteriasApp` without passing a use-case bag.
- [ ] Run the app navigation tests and `:app:compileTqsDebugKotlin`; expect the same graph list and successful Hilt aggregation.

### Task 4: Separate host initialization, global session and environment inputs

**Files:**
- Create or modify focused host contracts in `app/src/main/java/br/gov/caixa/loterias/apostas/{initialization,navigation/session,environment,composition,di}/`.
- Modify: `MainActivity.kt`, `composition/AppContent.kt`, `navigation/session/AppSessionCoordinator.kt`, `navigation/action/AppNavigationActions.kt`, `initialization/startup/*`, `theme/ApplicationCampaignTheme.kt`, `runtime/AppHostRuntime.kt`, `FilaBraGate.kt`, `AppContainer.kt`.
- Test: focused app startup/session/environment tests and existing feature session tests.

**Interfaces:**
- Consumes: host-only platform/core operations that were previously read through `AppContainer`.
- Produces: `AppInitializationDependencies(networkAvailability: NetworkAvailability, networkSecurity: PrepareNetworkSecurityUseCase, isVersionSupported: IsVersionSupportedUseCase, marketplaceFilterParameters: GetMarketplaceFilterParametersUseCase, dispatchers: CoroutineDispatchers)`, `AppSessionDependencies(account: AccountSessionDependencies, foregroundSession: ForegroundSessionAccess, foregroundBackground: ForegroundSessionBackgroundState, sessionIdentityChanges: Flow<AuthenticatedSessionIdentity?>, localClock: Clock)`, and `AppCampaignDependencies(configuration: AppConfigurationRepository, networkSecurity: PrepareNetworkSecurityUseCase, homeFeatureConfiguration: GetHomeFeatureConfigurationUseCase, dispatchers: CoroutineDispatchers)`. Account-owned `AccountEnvironmentDependencies(settings: EnvironmentSettings, change: ChangeEnvironment)` replaces environment operations read from the old container. `AccountSessionDependencies` contains the existing authentication, `EndSessionGateway`, `SignOutSessionUseCase`, `ClearLocalSessionDataUseCase`, `ResumeLocalSessionCleanupUseCase`, `VerifyPostLoginAccessUseCase`, authenticated identity provider and dispatchers. Startup's `shouldShowIntroduction` comes from Onboarding navigation dependencies. Active app wiring no longer reads feature use cases through a universal container.

- [ ] Add characterization test `AppHostRuntimeInitializationTest` for `initialize()` call count/order, plus preserve the existing `EnvironmentSelectionTest`, `EnvironmentCommitConfirmationTest`, `ForegroundSessionLifecycleObserverTest`, `HostViewModelsTest` and `FilaBraQueueCoordinationTest` assertions.
- [ ] Run those tests against the current wiring and record their passing outcomes before moving accessors.
- [ ] Move `EnvironmentSettings` and `ProcessEnvironmentSettings` implementation to Account Data, retaining old FQNs through the owner-compatibility inventory; remove `AppHostRuntime`'s direct `PersistEnvironmentChange`/`RecoverEnvironmentChange` construction and consume Account's bound `ChangeEnvironment`.
- [ ] Replace host use of AppContainer with the exact typed contracts in this task's Interfaces block; move environment selection, startup destination/status and recovery-panel Compose ownership to Account/Onboarding as specified in Task 2.
- [ ] Keep network-client initialization in the same Activity-injection order using CORE Network's existing `NetworkClientCoordinator`; keep the initialization call once per existing host lifetime.
- [ ] Keep the visual fixture behind the existing debug/mock checks and delegate its confirmed-bet fixture to the Tickets feature-owned adapter.
- [ ] Run app startup/session/environment tests and existing Account/Onboarding/navigation tests; expect same event order and cancellation behavior.

### Task 5: Retire active `AppContainer` wiring and remove redundant Data edges

**Files:**
- Modify: `app/src/main/java/br/gov/caixa/loterias/apostas/AppContainer.kt`
- Modify: `app/build.gradle`
- Move: public `app/di/*Module.kt` compatibility facades to their owning feature/CORE source modules, preserving old JVM package names in `config/host-public-compatibility.json`.
- Move: `app/src/test/java/br/gov/caixa/loterias/apostas/MockConfirmedBetFixturesTest.kt` to Tickets Data tests and the environment tests that directly consume Account Data to Account tests.
- Modify: `config/app-compatibility.json` and `config/host-public-compatibility.json`
- Modify: `scripts/check-app-architecture.py` and `scripts/test_app_architecture.py`

**Interfaces:**
- Consumes: Tasks 2–4 feature-owned navigation bindings and focused host dependency contracts.
- Produces: no active production caller or Hilt injection of `AppContainer`; because `config/app-compatibility.json` currently records its public constructor/getters, the old type remains as a deprecated compatibility facade with the identical constructor/getter ABI, no `@Inject`, no route callers, and no independent business/runtime implementation. `app` directly depends on feature entry modules and required CORE modules, not on feature Data modules.

- [ ] Add a source/API test proving the legacy facade has no `@Inject`, no route/runtime caller, and preserves its constructor/getter descriptors; its environment/runtime methods forward to the host runtime owner.
- [ ] Run the focused host architecture tests and confirm they reject active `AppContainer` use and each direct Data edge.
- [ ] Remove active constructor injection and navigation-interface aggregation from `AppContainer`; retain only the compatibility constructor/getter descriptors, delegate runtime behavior to `AppHostRuntime`, and update the exact inventory reason to say compatibility-only and inactive.
- [ ] Move app-owned public DI wrappers (`AccountModule`, `BettingModule`, `CartModule`, `ConfigurationModule`, `LatestNotificationModule`, `MarketplaceModule`, `NetworkModule`, `NotificationsModule`, `OnboardingModule`, `PaymentModule`, `ResponsibleGamingModule`, `ResultsModule`, `RetailModule`, `SessionModule`, `SessionLogoutModule`, `SupportModule`, `TicketsModule`, `UnreadNotificationsModule`) into the existing feature/CORE owners, retaining old FQNs as exact binary-compatible methods; keep their actual providers only once in the owner.
- [ ] Move remaining active providers out of `app/di` to existing owners: network client/provider bindings to Core Network, `AppConfigurationBindings` to Core Data, push bindings to Notifications, tickets/betting bindings to their features, session/auth bindings to Core Auth/Security, and lifecycle bindings to Core Auth/Navigation; leave only host platform defaults, BuildConfig values and graph composition bindings in app.
- [ ] Remove direct Account/Tickets Data use from app production and tests (including visual fixture and environment selection); retain feature module dependencies so Hilt sees the owning providers transitively.
- [ ] Remove the 13 direct `implementation project(':feature:<name>:data')` declarations from `app/build.gradle`; retain feature entry modules and CORE dependencies needed by host source.
- [ ] Run `:app:compileTqsDebugKotlin` and `:app:compileTqsReleaseKotlin`; if Hilt aggregation fails, fix ownership/transitivity in the owning feature module without restoring app→Data edges or duplicating bindings.

### Task 6: Close package ownership, compatibility inventory and global validation

**Files:**
- Modify: `scripts/check-app-architecture.py`, `scripts/test_app_architecture.py`
- Modify: `config/app-compatibility.json`, `config/host-public-compatibility.json` only for verified symbols
- Modify: `ARCHITECTURE.md`, `MIGRATION_PLAN.md`, and the app section of `MIGRATION_VALIDATION.md`
- Move remaining app source files only where the owner is clear from the app-host spec; keep exact old-package forwarding facades where required.

**Interfaces:**
- Consumes: completed host and feature wiring from Tasks 1–5.
- Produces: an enforceable app ownership gate, accurate compatibility inventory and reproducible validation record.

- [ ] Add negative fixtures for all acceptance-rule violations in the spec plus positive fixtures for the root composer and documented compatibility facade.
- [ ] Run `python3 scripts/test_app_architecture.py`, `python3 scripts/check-app-architecture.py`, `python3 scripts/test_feature_architecture.py`, `python3 scripts/check-feature-architecture.py`, and `./gradlew verifyCleanArchitecture verifyFeatureModules`.
- [ ] Compare public JVM signatures against the Task 1 snapshot; repair only missing public forwarding surfaces and rerun the comparison.
- [ ] Run `./gradlew modernCheck releaseCheck`; these aggregate tasks cover `prd`, `tqs` and `automacao`. Also run app unit tests and the relevant existing startup/session/navigation Android tests, serially.
- [ ] Update the architecture/migration records with actual outcomes and still-pending physical/private validation; do not claim external homologation or visual checks that were unavailable.

---

## Plan self-review

- **Spec coverage:** Tasks 1 and 6 enforce the package/gate requirements; Tasks 2 and 3 establish feature-owned graph composition; Tasks 4 and 5 remove active host aggregation and direct Data edges; Tasks 6 validates public compatibility and behavior.
- **Step scan:** Every task has a failing/characterization check before implementation and a concrete check after. No task changes route bodies or ViewModel StateFlow contracts.
- **Type consistency:** Feature interface types remain those already present in `feature/*/navigation`; the only new aggregate is a host-only typed registry of those interfaces. Host services are divided by existing initialization/session/environment use.
- **Review focus:** Hilt transitivity, binary compatibility, navigation identity/ordering and initialization timing are attached to Tasks 1–6 as compile/API/behavior checks.
- **Proportion:** The tasks map directly to the six measurable acceptance criteria in the approved spec. No feature screen or Domain rewrite is included.

## Execution constraints

The existing `MIGRATION_PLAN.md` and approved spec prohibit commits/pushes and require preserving the shared dirty checkout. Follow that instruction over the generic frequent-commit workflow. Implement inline in this checkout only after plan review/approval; do not use parallel agents or run Gradle concurrently.

## Execution record — 20/10/2026

Implementation, architecture gates, and the complete global build matrix are finished. `modernCheck releaseCheck` passed for Prd, Tqs, and Automacao; the final log is `/tmp/global-checks-final-5.log`. The app/feature Python gates passed (29 and 11 policy tests), as did `verifyCleanArchitecture` and `verifyFeatureModules`. OAuth test-manifest placeholders were added to Notifications and Tickets; no coverage threshold was reduced. Follow-up characterization tests restored Notifications coverage to 95.80% and passed the existing Support 95% gate.

The only acceptance evidence that cannot be completed from this checkout is compiled ABI comparison: the pre-change snapshot at `build/verification/app-host-coherence/public-and-hilt-baseline.txt` is source-level only and says it does not replace compiled signatures. The compatibility FQNs and recorded declarations compile at their owners, but exact prior bytecode descriptors have not been compared. Physical/private homologation and the dedicated visual runner also remain outside this plan run, as recorded in `MIGRATION_VALIDATION.md`.
