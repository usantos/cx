# Independent feature modules

## Intent and constraints

The user authorized completing the supplied MVVM/Clean feature architecture, beginning with account/cart/betting cycles and eliminating compilation aggregation. Preserve public Kotlin/JVM contracts, existing StateFlow owners, behavior, wire formats, persistence, security, coverage thresholds and every previous workspace change. SPEC.md remains binding. No commits, staging, pushes, root clean or publication. Private authentication, signing and physical-reference limitations remain external.

## Module boundaries

Each feature owns real Gradle modules and ordinary src/main roots. Its presentation module owns presentation, navigation and feature DI. Pure domain and remote-data child modules preserve JVM-only compilation/testing; Android persistence belongs to the feature's Android boundary. This is the supplied layered architecture with explicit compile boundaries, rather than the former core source-directory aggregation.

Small feature-owned API modules contain only contracts needed to break cycles, with the same packages, class names, constructors and members. Cart's API contains the existing cart value types; account's API contains the existing session identity and identity provider. Account domain consumes cart values; betting domain consumes cart values/account domain; cart domain consumes account/betting domain. Neither domain depends on another feature's UI or data implementation. No duplicated cart/session models, substitute authentication or generic god module.

All domain source roots leave core/domain; all feature remote sources leave core/data; all feature Android adapters leave core/preferences; Home presentation leaves app compilation. Core retains genuinely shared service/configuration and infrastructure. Shared transport facades keep their public contracts while domain-specific adapters and DI are owned by features. Public legacy packages can remain as explicit compatibility surfaces; canonical layer subpackages provide focused implementation/model/repository/usecase organization without deleting old callable APIs.

## Remaining architecture work

Feature navigation exposes typed dependency/action contracts; app supplies application configuration and cross-feature callbacks and assembles the root NavHost. Feature bindings live in feature di packages; only environment/platform composition stays in app. Home receives runtime flags and integration callbacks; its resource dependencies become feature resources without changing bytes or geometry.

Network retains shared clients/interceptors/security/configuration. Domain-specific APIs/DTOs/adapters have feature ownership, with compatibility facades where public API preservation requires them. Local database/preferences code keeps its exact schema, keys and commit/apply semantics while moving to explicit database/datastore infrastructure boundaries; no unrequested Room/DataStore data migration. Shared testing replaces truly equivalent test helper duplication. Convention plugins centralize library/feature/Compose policy; existing version catalog and version floors remain.

## Validation

Snapshot the actual dirty checkout, compiled public class contracts and source bodies before mutation. Prove acyclic Gradle/domain graphs, unique source ownership and absence of external source-root compilation. First demonstrate the new requirements fail on current aggregation/cycles; then make the guards pass. Pure moves use literal body comparisons plus existing characterization, not invented functional failures. Behavior changes require meaningful failing regression tests first.

Serialize Gradle. Validate domains independently before data/presentation; move existing tests to their actual owning modules and preserve assertions/fixtures. Coverage migrates with its class/execution ownership and retains original thresholds/patterns. Run all feature JVM tests, affected instrumented suites, real negative gate fixtures, full modernCheck/releaseCheck for three flavors and a fresh independent review. Preserve physical-oracle failures and external limitations, never adjust production coordinates or tolerances to hide them. Record actual commands/results and remove only resources created by this round.

## Alternatives and choice

Keeping core aggregation does not satisfy the request. Renaming shared public models to a new common model package breaks JVM contracts. Explicit feature-owned API/domain/data modules preserve those contracts and make dependencies independently enforceable, at the cost of more small Gradle modules. The user-supplied single-module feature example does not prohibit these stricter boundaries; no empty layers/reducers/storage technologies are created merely to imitate sample filenames.
