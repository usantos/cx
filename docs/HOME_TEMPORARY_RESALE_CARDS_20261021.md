# Home temporary resale cards

Immutable source: ROSA `dca5d9d9bcce3e888aee75d5e2b59d5f1d7ac80b`, `EncerramentoUtil.verificaSeCriaCardTemporario`, `criarCardTemporario`, `upInsParametroJogo`, and `ModalidadeDisponivelCota`.

The legacy app retains a resale-only contest even when individual betting parameters omit it or advance to another contest. It requires the pool-sales flag, parses the resale deadline, accepts the deadline inclusively, and checks the complete modality/type/contest identity. If that identity is missing it replaces an existing entry with the same modality/type, appends a missing NORMAL card, or prepends a missing special card.

The Home mapper now reproduces this projection without mutating domain betting parameters. It accepts an explicit `now: Instant` for deterministic boundary tests. Existing matching games retain their prize, draw metadata and normal presentation. Temporary cards use real availability metadata, individual sales closed, the legacy `01/01/2025 12:00:00` closing marker, and the real resale deadline. This marker is the source's temporary-card behavior, not a simulated server response.

`ModalidadeDisponivelCota` supplies exact wire fields `modalidadeDetalhada`, `estimativa`, and `dataHoraSorteio`. The transport/domain/data mapper now preserves them with compatible defaults. It does not add new API calls, substitute responses or modify stored cart/betting data.

The pool CTA no longer requires `HomeLottery.open`: availability and the pool-sales flag determine its visibility, as in the source, allowing resale while individual betting is closed.

Regression tests added:

- `HomeTemporaryPoolCardsTest`: real metadata on replacement, preserving matching contest, special-first/normal-last insertion, configured special banners, flag suppression, invalid/missing/expired metadata, inclusive deadline boundary, and resale CTA for closed individual sales.
- `PoolAvailabilityMetadataTest`: exact original serialized fields reach the domain; omitted metadata remains absent.

This worker did not run Gradle or adb. Parent integration owns compilation, test execution, native visual comparison and expiry refresh coordination. Source inspection and added tests alone do not establish rendered parity.
