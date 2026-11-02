# ReduxKotlin + MVVM - Support pilot

User selected one feature first, expansion later. Use Support (Ajuda/Dúvidas, terms, privacy and game help) as pilot; preserve other features, all headers, session data, public navigation, API contracts and baselines. Work inline in current dirty migration checkout; no commit/push.

## Design
- ReduxKotlin 0.6.2 stable, `redux-kotlin-threadsafe-jvm`, version catalog managed. Only Support Presentation receives the dependency. Domain/Data remain framework-independent.
- SupportStateStore belongs to Support Presentation and is owned by SupportViewModel. Typed immutable actions feed a pure SupportReducer. Existing SupportAction UI contract is retained via typed mutation actions for UI/load events plus game-help metadata and acceptance failure.
- Store is sole writable state authority. Private StateFlow projection is updated only by its subscription; Views still collect read-only StateFlow. ViewModel coordinates Domain use cases and emits one-shot terms-accepted events outside Redux. Lifecycle clearing unsubscribes the projection.
- Stable threading implementation serializes IO/UI dispatches. No global Singleton store, automatic state persistence, DevTools, state logging, replay of remote operations, or reducer side effects.
- Keep initialization normalization for TERMS_READ_ONLY and all existing action semantics, including acceptance guards and game-help dates.
- Existing reducer prohibition is superseded by the explicit user request ONLY for Support's pure state/SupportReducer.kt. Extend the architecture gate/tests for that exact pilot owner and purity; do not weaken other module boundaries.

## Implementation
1. Add failing reducer/store tests covering immutable transitions, initial normalization, read-only projection, concurrent dispatch and unsubscribe. Add failing gate tests for scoped reducer permission and rejection of effects/other owners.
2. Add catalog dependency, typed state store/reducer and migrate SupportViewModel updates into dispatches while preserving effect orchestration. Run new tests plus complete Support and App regression suites.
3. Run Detekt, focused formatting, Lint, architecture checks and app build. Preserve baseline hashes. Obtain independent review of state source/lifecycle/threading and effect ownership.
4. Update MVVM contract/architecture and branded guide to describe implemented pilot, version, flow and explicit remaining migration. Update measured verification manifests; regenerate/render/inspect final PDF. Install/verify normal Help/Back on Samsung if connected.

Rulings: stable 0.6.2 instead of the 1.0 alpha line; no additional product modules or generic adapter until a second feature demonstrates reuse. Existing two architecture P2 findings remain separate, not silently marked fixed by the pilot. Ktlint global debt remains documented, without broad formatting or increased baselines.

Compatibility ruling: official Android AAR 0.6.2 contains classfile65/JVM21 and inline compilation failed against the existing JVM17 target. Use official JVM artifacts 0.6.2 (classfile61/JVM17), with the same platform-independent API, rather than raising the project target or taking a pre-release. Verified published classfiles for core and threadsafe.

Legacy correspondence: reducers are custom Java under viewModel/reducer, consumed by Simula/LotecaViewModel with UiState + LiveData. No org.reduxkotlin dependency was found. Preserve that ownership/separation, with typed ReduxKotlin store and read-only StateFlow in the pilot. See build/verification/redux-mvvm-20261027/legacy-pattern.md.

Dependency metadata ruling: threadsafe-jvm alone selected Android core transitively via Gradle module metadata. Explicit core-jvm plus a targeted exclusion of the multiplatform core from the threadsafe dependency resolves only the two official JVM17 artifacts. dependency-insight-final.log verifies actual selected runtime modules.


## Completed
- Legacy MVVM/reducer/UiState pattern inspected and recorded. Support pilot implemented with actual stable ReduxKotlin typed thread-safe store; only Support Presentation depends on it.
- Reducer/store tests: 9 assertion failures RED, then GREEN. Native library unsubscribe double-close failure reproduced by regression, fixed with idempotent close guard. Full Support 36 + App PRD 367 = 403 passed, 0 failures/errors/skips.
- Architecture policy: scoped permission and purity checks; 63 focused tests, 170 Python suite tests + 11 hook tests passed. No baseline expansion or broad permission.
- Final official JVM core/threadsafe artifacts selected explicitly; hashes and classfile61/JVM17 confirmed. verified-final.log: full build, Lint, Detekt and architecture passed. Changed Kotlin formatting passed.
- Independent code review clean. Existing late response behavior and pre-existing two architecture P2 findings remain documented, without claiming they were repaired by Redux.
- Samsung final APK installed; six FAQ actions verified against real UI, 0 failures/skips; 4 bets/R$19 retained. No real terms acceptance or financial write.
- Guide regenerated using CAIXA template, 249 pages, 22 TOC links, rendered and inspected. Native photo labels explicitly identify RECOLHIDO/EXPANDIDO. Historical coverage and global format debt remain scoped.
- 37 protected baseline/config hashes + 2 header hashes unchanged. No commit/push.
