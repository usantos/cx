# ReduxKotlin in all features

User explicitly requests expanding the validated Support pilot to all 14 features. Preserve MVVM, Clean Architecture, SOLID/KISS/DRY, headers, baseline/config thresholds, API/session/cart/persistence behavior and all existing effects. No commit/push. Work in current dirty migration checkout with disjoint agent ownership.

## Architecture and shared contract
- Extract reusable bridge into core:presentation/state/ReduxStateStore.kt. Official JVM core/threadsafe 0.6.2 via catalog, excluding transitive multiplatform core as established by pilot. No Domain/Data dependency on Redux, no new module/toolchain/DevTools/logging.
- API: `inline fun <State, reified Action : Any> reduxStateStore(initialState: State, crossinline reducer: (State, Action) -> State): ReduxStateStore<State, Action>`.
- Store exposes `val state: StateFlow<State>`, `val value: State`, `dispatch(Action)`, idempotent `close()`. It is NOT MutableStateFlow and has NO update, setter, transformation/lambda action or generic replace operation. Redux owns writes; subscription alone publishes the private Flow projection.
- Each stateful ViewModel creates its typed store with `reduxStateStore<State, Action>(initial, ::reduceXState).also { addCloseable(it) }`; helpers/coordinators share that same typed store. Views keep read-only StateFlow contracts. Multiple independent states in a VM retain separate typed stores.
- Named immutable semantic actions and pure reducers in each feature's presentation/state/*Reducer.kt. Reducers own state transitions; effects, requests, persistence and one-shot channels stay in ViewModels/coordinators/Domain/Data as appropriate. Do not substitute a fake Redux generic Update/Replace/Transform action.
- Effects-only AccountStatusViewModel needs no dummy store. UI-local focus/animation state, event queues and repository/domain observation Flows are not application UiState and remain in their correct owners.
- Architecture policy permits pure reducers in approved presentation/state owners of all 14 features, rejects reducers in Views/other packages, and preserves effect/purity checks. Enforce stateful ViewModel ownership and forbid new writable UiState flows outside the shared bridge.

## Ownership
Root: shared bridge/tests, core:presentation Gradle, architecture policy/tests, App tests integration, Support migration to shared bridge, verification, documentation/PDF and Samsung.
Group A: account, onboarding, notifications, results, responsible-gaming.
Group B: betting, cart, payment.
Group C: home, marketplace, marketplace-filter, retail, tickets.
Agents may edit only their feature presentation sources/tests and feature Gradle dependency if required. No parallel Gradle executions. Wait for baseline/GO before implementation. Existing behavior tests establish before/after refactor contract; add reducer tests where meaningful.

## Sequence
1. Shared bridge failing tests and existing presentation baseline. Inventory per-group state/effect/coordinator boundaries.
2. Green shared bridge and publish stable API. Parallel feature migrations; all state writes become named dispatches, coordinator store types updated, lifecycles closed. Update existing fixtures to typed stores without weakening assertions.
3. Compile/test all presentation modules + App and bridge; resolve integration failures sequentially. Pure reducers cannot invoke repositories/usecases/clock/network or execute side effects. Review reducers and residual mutable UiState flows.
4. Run Lint, Detekt, focused formatting and architecture gates; preserve protected hashes. Independent final review, fix important findings with regression evidence. Build/install same PRD APK and exercise representative public flows on Samsung.
5. Update architecture/MVVM/Redux docs and branded PDF to full coverage with actual inventory and measured scope. Render and inspect final artifact. Historical global coverage/format debt and previous P2s remain explicit unless genuinely resolved.

## Execution record
Cart was transferred completely from Group B to A; final B ownership is Betting/Payment. Retail constructor follow-up updated its AndroidTest callers and extracted cohesive input/receipt usecase groups without suppressions. The final verification command includes Retail AndroidTest compilation. The complete matrix passed: 2,145 scoped unit tests, no failures/errors/skips, PRD build, Lint/Detekt and all architecture gates. Focused formatting: 240 changed Kotlin files. Python policy: 173 + 11 hook tests. Device evidence and final document QA live under build/verification/redux-all-20261027/.
