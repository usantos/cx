# Classic Home special banner audit

Immutable legacy oracle: ROSA release commit `dca5d9d9bcce3e888aee75d5e2b59d5f1d7ac80b`, inspected with `git show` in `/Users/ubiracysantos/Projects/legado/simlo-app-android`.

`HomeViewHolder.bind` hides the normal `titulo` and shows `cl_especiais` only for `ESPECIAL` contests with a positive `EstiloModalidadeMKP.getImagemEspecialCarrossel`. Without artwork it falls back to the ordinary header. Instant and Combo return before this branch.

| Modality/state | Source carousel artwork | Implementation |
| --- | --- | --- |
| Mega-Sena ESPECIAL | `mega_virada_novo.png` | Exact release PNG copied to scoped Home resource |
| Quina ESPECIAL | `quina_sao_joao_novo.png` | Exact release PNG copied |
| Dupla-Sena ESPECIAL | `dupla_pascoa.png` | Exact release PNG copied |
| Lotofácil ESPECIAL | `lotofacil_independencia.png` | Exact release PNG copied |
| Loteca ESPECIAL | `loteca_copa.png` | Exact release PNG copied |
| Other modality ESPECIAL | No carousel artwork | Retain standard banner |
| NORMAL / Instant / Combo | Standard header | Existing branch preserved unchanged |
| Mega 30 anos | `cabecalho_mega_trinta_anos_carrossel`, configured two-line text | Exact backend key `concursoMega30Anos` now reaches Home configuration. Override requires matching contest number + Mega-Sena + ESPECIAL. Original fixed two-line resource `mega-sena\n30 anos` and exact release artwork copied. |
| Loteca dos Pais | `cabecalho_loteca_pais_carrossel_alta`, configured two-line text | Exact backend key `concursoLotecaPais` now reaches Home configuration. Override requires matching contest number + Loteca + ESPECIAL. Original fixed two-line resource `loteca\ndos pais` and exact release artwork copied. |

Geometry from `card_modalidade.xml`: 71dp panel, artwork fitXY, label start/end margins 90dp/10dp, white Futura bold, vertically centered, uniform autosizing 8..18sp in 1sp steps. Compose uses `BasicText` with `TextAutoSize.StepBased`; current Compose API was verified against installed foundation 1.12 sources. The existing mapper already supplies the server's `specialName` for ESPECIAL, and the label uses the source's Portuguese lowercase transformation. Configured special overrides use optional contest numbers added to the existing network DTO and `HomeFeatureConfiguration.specialContests`; presentation selects the enum only for an exact number/modality/type match. Absent metadata leaves standard special-contest artwork selected. `SplashScreenActivity` proves the two-line titles originate from fixed string resources rather than backend title fields; no invented serialized names or name heuristics were added.

## Validation scope

Added configuration serialization/repository tests (`HomeSpecialContestsConfigurationTest`), matching/nonmatching/absent metadata mapper tests (`HomeSpecialContestsMappingTest`), and `HomeSpecialBannerUiTest`: resource state matrix, panel/label dimensions, short-title maximum font size, long-title overflow and standard-header fallback. No Gradle/device commands were executed by this worker. Source-derived dimensions and byte-identical artwork do **not** establish rendered pixel parity; parent integration compares against its native HomeHolder oracle and records execution results.

Asset SHA-256 (all copied directly from the immutable git blob):

- Mega: `dc315a1660b415db9d8613db2bc6a443125643acce11ca88a7676febf0f7e249`
- Quina: `84f031234f4be761d06a11387da3295c9398c64683e256b1481fad4f729f69c3`
- Dupla: `3927b90d63a1f9f613a57c588095389dd279b45308ebc2dc5e4a597fe0c7cd10`
- Lotofácil: `c73e762e5108df28e187d28fe2ec253faaf0f405d84bcbd6347a947d4ae6e2ab`
- Loteca: `47e5fae70f996230a03c592092f68244be2d4482c0cb474d9d80f568ccdbcc4f`


## Configuration provenance

Release `ParametrosConfiguraveisDTO` declares `@SerializedName("concursoMega30Anos")` and `@SerializedName("concursoLotecaPais")`. Release `SplashScreenActivity` stores these numbers in `ARG_30_ANOS_VALOR` / `ARG_LOTECA_PAIS_VALOR`, and stores `R.string.mega_sena_30_anos_duas_linhas` / `R.string.loteca_pais_duas_linhas` in their corresponding preferences. `EspecialUtils` requires the configured number, modality and special contest type. The new data/domain/presentation mapping preserves those three selection conditions without reading preferences from UI.
