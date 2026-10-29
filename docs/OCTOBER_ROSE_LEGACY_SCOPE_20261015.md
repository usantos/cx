# Outubro Rosa — legacy scope audit (2026-10-15)

Read-only audit of `/Users/ubiracysantos/Projects/cx/app/src/main`; destination references are relative to `/Users/ubiracysantos/Projects/simlo-app-android`. This file records the source contract, not implementation completion. No legacy source was changed.

## Activation contracts

- **G** = backend `vigenciaMesOutubroRosa` only. `controllers/SplashScreenActivity.java:170` stores it; `utils/EspecialUtils.java:112-113` reads it.
- **M** = **G AND** backend `vigenciaOutubroRosaMegaSena`. Stored at `SplashScreenActivity.java:171`, combined at `EspecialUtils.java:120-121`.
- **M+Mega** = M and the item's actual modality is Mega-Sena. Explicit special cases add `modalidade == MEGA_SENA`. Shared style `utils/EstiloModalidadeMKP.java:343-346` applies its campaign override in the Mega default branch; other declared lottery cases have independent unchanged styles.
- Backend flags determine activation; there is no local October date check in these legacy methods.
- Do not interpret unknown IDs, missing catalog items, `COMBO`, `BOLAO`, or an unrelated lottery as Mega merely because a general style lookup has a green fallback. In the Compose DS, establish Mega identity before applying a campaign override. Simulations use Mega ID 2; support normalized `MEGA_SENA`, `MEGASENA`, `MEGA-SENA`, `Mega-Sena` names.

## Exact semantic colors

`res/values/colors.xml:303-305` and `utils/EstiloModalidadeMKP.java:375-401`:

| Legacy style role | Campaign value | Meaning |
|---|---|---|
| `corClara` | `#F4ADBC` | light/header/betting-selection fill |
| `corEscura` | `#EE3E7F` | dark/body/action fill and outlines |
| `corLetraLista` | `#EE3E7F` | titles/amounts/list accents on white by default |
| `corItemDezena` | `#EE3E7F` | betting number mark/accent |
| `corFonteFundoClaro` | `#9C005C` | text/icons on light pink |
| `corFonteFundoEscuro` | `#FFFFFF` | text/icons on dark pink |
| `alteraOutubroRosaPorTela()` | only `corLetraLista = #9C005C` | only old cart and confirmed-bet list text overrides |

The three pink colors are separate roles. In particular, the DS token currently named `LegacyOctoberRoseDark` is the burgundy text color, not the legacy `corEscura` surface.

## Screen and route matrix

| Legacy screen / component source | Flag | Required visual scope | Compose destination |
|---|---|---|---|
| Home global toolbar and drawer: `controllers/PrincipalActivity.java:225-227,250-251,358-368`; `view/adapter/baseadapter/SideMenuExpandleAdapter.java:79-80,134-148,300-303` | G | Pink hamburger, quick-access clover, campaign ribbon action; drawer user/name/CAIXA copy/arrow/logout/group+child icons and text secondary; drawer background secondary. Campaign informational panel uses watermark and secondary emphasis. | `app/.../HomeScreen.kt`, `HomeDrawer.kt`, `HomeDrawerStyle.kt`; `core/design-system/.../CaixaDSCampaignTheme.kt` |
| Shake row/tooltip: `utils/shake/ApostaShakeMenuController.java:82-86`; `SideMenuExpandleAdapter.java:134-148` | G | Title, icon, info, tooltip bubble/pointer secondary; custom pink switch thumb/track. Checked and unchecked have distinct assets. | `app/.../ShakeMenuRow.kt`, DS drawer/tooltip/switch components |
| Home Mega card: `view/holder/HomeViewHolder.java:154-168,225-238,272-273,327-328,421-422`; `PrincipalActivity.java:1117-1160` | M+Mega | Light title, burgundy text, dark result/CTA panels, white dark-surface copy, campaign clover/trapeze; prize/clock/calendar default list accent secondary; bolão button white 10dp rounded with secondary outline. | `app/.../HomeScreen.kt` (`HomeLotteryUi`, style mapping, cards, expanded result), `HomeV2Screen.kt`; `feature/home` assets |
| Home drawn result: `view/custom/ModalidadeSorteioResultadoView.java:58-60`, `ModalidadeSorteioItemUmConcursoResultadoView.java:72-92` | M+Mega | Wrapper white text; unshaped drawn number text explicitly white for pink Mega, overriding burgundy content-on-light; divider remains content-on-light. | `HomeScreen.kt` expanded result and corresponding shared result widgets |
| Lottery selectors (marketplace, favorites, results, Rapidão, bet filter): `ListaModalidadesAdapter.java:129-145,169-172`; `FavoritasActivity.java:445-459`; `SelecaoModalidadesResultados.java:94-101`; `FiltroApostasAdapter.java:446-458`; `ConfiguracaoRapidaoFragment4.java:132-145`, `ConfiguracaoRapidaoFragment8.java:141-151` | M+Mega | Light chip/selection fill, campaign clover light/dark variants; marketplace selected Mega uses `trevo_megasena_outubro_rosa_ativado`; selector foreground uses correct content/list role. Unselected gray remains unchanged. | `MarketplaceFilterScreen.kt`, `FavoriteBetsRoute.kt`, `ResultsRoute.kt`, `RapidConfigurationRoute.kt`, `BettingCatalogRoute.kt`; `CaixaDSLegacyLotteryClover.kt` |
| Marketplace carousel/list: `view/holder/BolaoCarrosselHolder.java:140-181`; `BolaoHolder.java:262-277` | M+Mega | Unfiltered carousel header explicitly dark secondary instead of light; retailer-filtered normal card keeps light header plus campaign trapeze+clover. Details outline/text secondary, add/quantity controls dark with white content. | `feature/marketplace/.../MarketplaceCarousel.kt`, `MarketplaceRoute.kt`; centralized DS palette/assets |
| Marketplace detail/game list: `view/activity/DetalhesBolaoActivity.java:156-164,372-410`; `BarraTituloBolaoFragment.java:120-141`; `JogoBolaoHolder.java:95-105` | M+Mega | Light/dark surfaces and matching foregrounds; prize and amount-in-words explicitly burgundy; dark action panel white; campaign trapeze for normal and special headers; special contest art retained. | `MarketplaceDetailRoute.kt`, marketplace game widgets and DS style |
| Betting simulation/new volante: `controllers/SimularApostaActivity.java:240,751-777`; `view/features/volantenovo/activity/SimulaActivity.java:824-853,1598-1600`; `SubBarraModalidadeFragment.java:103-111`; `BarraTituloSimulacaoFragment.java:131-140`; `CabecalhoSimulacaoFragment.java:120-130` | M+Mega | Headers/backgrounds light, lower/action sections dark, text and icons burgundy/white by surface, number selection item secondary; specials maintain their own art overlay. | `BettingGameRoute.kt`, `BettingRoute.kt`, betting number/mirror components, `SupportScreen.kt` modality help |
| Add-to-cart confirmation: `ConfirmadoInclusaoCarrinhoActivity.java:253-284`; `ConfirmadoInclusaoBolaoCarrinhoActivity.java:92-111` | M+Mega | Light background with burgundy confirmation copy; dark timer/action elements and white content. Legacy changes local status to dark modality, but user now explicitly requires blue status matching Home header. | `feature/cart/.../CartAdditionRoute.kt` |
| Old cart / confirmed bet list: `ApostaCarrinhoHolder.java:119-148,192-197,234,447`; `ApostaConfirmadaHolder.java:107-137` | M+Mega | Cart modality/contest/amount and strip accent become burgundy via `alteraOutubroRosaPorTela`; dark card panels/white labels remain independent. Confirmed list stripe is secondary, assigned **before** burgundy override; modality/strip copy burgundy. | `LegacyTicketLists.kt`, `ConfirmedBetContent.kt`, old/local cart review content |
| New cart: `novo/features/carrinho/ApostaViewHolder.kt:68,238-245`; `BolaoViewHolder.kt:58-67` | M+Mega | Only modality title inherits `corClara` = light pink; normal gray numbers, money, contest, expiry, delete styling unchanged. Do not apply old-cart burgundy override indiscriminately to this layout. | `CurrentCartPresentation.kt`, `CurrentCartRoute.kt`; `CaixaDSCurrentCartColors.kt` |
| Favorite bets / favorite carts: `FavoritaHolder.java:111-122`; `DetalhesCarrinhoFavoritoHolder.java:52-79`; `FavoritasRapidaoRecyclerAdapter.java:58-59`; `CarrinhoApostasListViewRapidaoAdapter.java:91-96` | M+Mega | Favorite card light header/burgundy text plus campaign trapeze+clover; favorite cart details default list secondary; Rapidão line accents/selection light and dark roles. | `FavoriteBetsRoute.kt`, `FavoriteCartsRoute.kt`, `RapidListRoute.kt` |
| Purchase list and details: `CompraHolder.java:62-63,108-116,143-168`; `ComprasDezenasView.java:305-306,339-340`; `DetalheMinhasApostasActivity.java:214-237,441-503,645`; `DetalheMinhasApostasAdapter.java:88,164-174` | M+Mega | Purchase title light+burgundy; body dark; purchase status and type strip explicitly white. Detail toolbar light/burgundy, body dark/white, prize and receipt actions use list/dark/white as in source. Surpresinha fills inherit light; numbered grids receive typed color config. | `LegacyTicketLists.kt`, `LegacyPurchaseDetailContent.kt`, `BetDetailRoute.kt` |
| Confirmed bet details/result/prize: `ApostaConfirmadaDetalhesActivity.java:194-202,338-363,753-763`; `ApostaConfirmadaBolaoDetalhesFragment.java:113-115,181-182,296-319,396`; `ApostaConfirmadaNumeroDetalhesFragment.java:116-117,157,243-252`; both `controllers/ResultadoApostaConfirmadaActivity.java:98-125` and `view/activity/ResultadoApostaConfirmadaActivity.java:84-111`; `PremiacaoAdapter.java:49,87,102` | M+Mega | Light header/burgundy icons+copy; dark body/white content; prize summary light+burgundy; campaign clover/trapeze; selected/unselected number results use color DTOs, not a global foreground override. | `ConfirmedBetContent.kt`, `BetDetailRoute.kt`, `ConfirmedBetResultRoute.kt`, `LegacyTicketResult.kt`, `PrizeRedemptionRoute.kt` |
| Public results and scanned ticket result: `VisualizarResultadosActivity.java:268-271,355,402-403`; `ResultadoLerBilhetesActivity.java:261,282-301,404-407`; `NumerosResultadosAdapter.java:89`; `ResultadoGenericoFragment.java:65`; `AccordionView.java:95-100` | M+Mega | Lottery selectors and result/prize panels use campaign role colors; default result bubbles white with secondary numbers; generic drawn result text inherited from typed role/config; accordion dark/white. | `ResultsRoute.kt`, `TicketGameChoiceRoute.kt`, `TicketScannerRoute.kt`, `LegacyTicketResult.kt` |
| Combos with Mega components: `ListaComboHolder.java:51-55`; `DetalheComboHolder.java:42-76`; `DetalheComboActivity.java:134-136`; `ApostaCarrinhoComboHolder.java:103`; `JogosComboHolder.java:51-57` | M+Mega item | Only Mega subitems switch: campaign clover/banner/detail watermark, secondary list text. `JogosComboHolder` uses light pink for Mega item modality/contest/value. Composite COMBO overall surface remains its own purple. | Marketplace combo list/detail, cart combo components, favorite-cart items; shared DS style plus combo art |
| Launcher icon: `SplashScreenActivity.java:207-220`, `AndroidManifest.xml:578-588` | G | Separate Android OS launcher alias `.alias.LauncherOutubroRosa`, `@mipmap/ic_launcher_rosa` + round; not Home toolbar ribbon or clover. | No campaign launcher alias/resource found in current Compose manifest/resources at audit time. Native launcher behavior requires explicit Android component implementation; a DS recolor alone cannot provide it. |

The requested removal of a pink Home campaign banner applies to an always-visible banner. The legacy campaign ribbon action opens an informational panel; that panel and action are separate from the removed banner. Status-bar blue matching the Home header is a new explicit requirement and takes precedence over legacy per-modality status changes.

## Drawable roles and availability

All source drawable paths below are under legacy `res/drawable/`. Byte sizes are source sizes, useful for identifying exact asset provenance. XML color-reference substitutions change bytes without changing geometry.

| Asset | Source bytes | Role | Existing Compose path at audit time |
|---|---:|---|---|
| `background_trapeze_megasena_outubro_rosa` | 871 | Two-tone normal header trapeze | `feature/home/src/main/res/drawable/legacy_home_background_trapeze_megasena_outubro_rosa.xml` (597 bytes, adapted) |
| `mega_sena_trapezio_outubro_rosa` | 303 | Special header trapeze | Missing |
| `btn_rounded_rosa_outlined` | 416 | Marketplace detail button; 4dp corners, secondary 1dp stroke, no explicit solid | Missing |
| `btn_rounded_outubro_rosa_outlined` | 463 | Home bolão button; white solid, 10dp corners, secondary 1dp stroke | Missing |
| `ic_tag_bolao_outubro_rosa` | 4908 | Bolão filled tag (style role; many usages now commented) | Missing |
| `ic_tag_bolao_rosa_outline` | 4908 | Bolão outline tag (style role; many usages now commented) | Missing |
| `trevo_megasena_outubro_rosa_fundo_branco` | 1270 | White-background clover; primary+secondary lobes | Missing |
| `trevo_megasena_outubro_rosa_fundo_escuro` | 1224 | Dark-background clover; primary+white lobes | `feature/home/src/main/res/drawable/legacy_home_trevo_megasena_outubro_rosa_fundo_escuro.xml` (1210 bytes, adapted) |
| `trevo_megasena_outubro_rosa_ativado` | 1240 | Marketplace selected modality clover | Missing |
| `tarja_trevo_mega_outubro_rosa_combos` | 699 | Combo list clover banner | Missing |
| `trevo_mega_sena_detalhe_combo_outubro_rosa` | 2870 | Combo detail watermark | Missing |
| `icon_trevo_outubro_rosa` | 1305 | Home quick-access icon | `app/src/main/res/drawable/legacy_home_icon_trevo_outubro_rosa.xml` (1340 bytes, adapted) |
| `ic_outubro_rosa` | 6875 | Home campaign ribbon action | `app/src/main/res/drawable/legacy_home_outubro_rosa.png` (6875 bytes, identical) |
| `marca_dagua_outubro_rosa` | 10343 | Home expanded campaign panel watermark | `app/src/main/res/drawable/legacy_home_marca_dagua_outubro_rosa.png` (10343 bytes, identical) |
| `shake_switch_thumb_rosa` | 250 | Pink switch state selector | Missing |
| `shake_switch_thumb_off_rosa` | 503 | Pink off-thumb | Missing |
| `shake_switch_thumb_on_rosa` | 537 | Pink on-thumb | Missing |
| `shake_switch_track_rosa` | 768 | Pink track checked/unchecked | Missing |

`preencheOutubroRosa()` deliberately retains Mega da Virada art (`mega_virada_background`, `mega_virada_novo`, `mega_virada_simples`, `mega_virada_dupla`, `mega_virada_quadrado`, `cabecalho_mega_da_virada_detalhe_aposta`, `rodape_mega_da_virada_inferior`). Mega 30 overlay is applied after style construction (`EstiloModalidadeMKP.java:73-81`), so campaign colors must not erase selected special-contest art.

Legacy launcher assets are `res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher_rosa.webp` and `_round.webp`, with adaptive XML in `mipmap-anydpi-v26`. They are distinct from the UI image assets above.

## Indirect styling and exclusion rules

1. `model/config/LotteryGridStyleConfig.java:25-118` creates typed `CellStyle(textColor,borderColor,backgroundColor)` for normal/intersection cells. Circular Mega grids use white text+border on secondary fill normally and secondary text on white fill at intersections. Rectangular/no-shape result cells remain white on transparent backgrounds. Do not recolor all numbers burgundy.
2. `model/model/ApostaConfirmadaDetalheModel.java:49-56` creates `ConfiguracaoNumeros(secondary,white)` for non-Timemania; number adapters/fragments inherit this even without importing the campaign helper. `ComprasDezenasView.java:305-306` creates a distinct config from list/content roles.
3. `TipoAposta` DTOs capture clover drawable refs, selected/unselected background, selected foreground, and gray unselected colors in selectors. Updating only visible call-site text misses these propagated styles.
4. `view/config/DezenaConfig` and `ShapeConfig` receive shared style colors from `SimulaActivity`, result models, purchase views, and detail fragments. Keep semantic source colors attached to their original foreground/fill roles.
5. Fixed non-Mega consumers remain unchanged despite importing `EstiloModalidadeMKP`: `SuperSeteResultadoLayout`, `ModalidadeSorteioItemDoisConcursosResultadoView`, `ModalidadeSorteioLotecaResultadoView`, `ModalidadeSorteioLotogolResultadoView`, `ModalideSorteioItemTimeManiaResultadoView`, `ResultadoTimemaniaDiaDeSorteFragment`, `ApostaConfirmadaPartidaDetalhesFragment`, `PartidaViewDetalhesResultado`, `LotecaActivity`, and +Milionária-specific trevo paths. COMBO overall style must stay purple.
6. Existing separate Compose maps are migration holes: `MarketplaceCarousel.kt` palette, `MarketplaceDetailRoute.kt:236` hardcoded Mega light, `CurrentCartPresentation.kt:275-285`, `ResultsRoute.kt:75` static official modality colors, `TicketGameChoiceRoute.kt:172`, `RapidConfigurationRoute.kt:523`, `BettingGameRoute.kt:808`, and fixed vector map in `CaixaDSLegacyLotteryClover.kt`. Central DS propagation alone is insufficient until these consumers resolve semantic style too.

## Non-screen consumers explicitly classified

These references are part of the configuration/rendering pipeline, not extra user-facing campaign screens:

| Consumer | Classification / retained responsibility |
|---|---|
| Legacy `ParametrosConfiguraveisDTO.java:61-64,234-239` | Backend field deserialization and accessors only; no rendering. |
| Legacy `ConfiguracoesEnum.java:22-23` | Preference key catalog only. |
| Legacy `EspecialUtils.java:112-121` | G/M flag evaluation only. |
| Legacy `SplashScreenActivity.java:170-171` | Configuration persistence; its separate launcher behavior at :207 is listed in the route matrix. |
| Legacy `IconeAppEnum.java:7`, `IconAliasManager` | Native launcher metadata/component management, not Compose content. |
| Legacy `EstiloModalidadeMKP.java` | Shared role/asset resolver; not an additional screen. |
| Legacy `LotteryGridStyleConfig.java`, `ApostaConfirmadaDetalheModel.java`, `ResultadoApostaConfirmadaViewModel.java` | Propagate typed colors/shape contracts to result/detail rendering; accounted for in those route rows. |
| Compose `core/network/ApiModels.kt`, `core/data/CommemorativeCampaignMapping.kt`, `core/domain/.../CommemorativeCampaign.kt` | Backend/domain configuration transport and activation policy; no UI surfaces to recolor. |
| Compose `core/network/MockBackendInterceptor.kt` and campaign fixture/tests | Developer mock scenarios and verification only; no independent end-user campaign screen. |

## Complete direct style-consumer index

The following inventory includes every Java/Kotlin file containing `EstiloModalidadeMKP` (excluding the definition itself); it intentionally includes fixed non-Mega consumers so that exclusions are reviewable. Paths are relative to legacy `java/br/gov/caixa/loterias/apostas/`. Getter names map directly to roles above. References list the first three non-commented uses per role; an ellipsis means further uses in that file. Kotlin property syntax is included.

| Consumer | Role references |
|---|---|
| `controllers/ApostaConfirmadaDetalhesActivity.java` | `CorFonteFundoClaro`:194,195,196…; `CorFonteFundoEscuro`:199,200,201…; `CorEscura`:338,339,340…; `CorClara`:355,358,361… |
| `controllers/ConfirmadoInclusaoBolaoCarrinhoActivity.java` | `CorEscura`:92,102,103; `CorClara`:95; `CorFonteFundoClaro`:98,99,100… |
| `controllers/ConfirmadoInclusaoCarrinhoActivity.java` | `CorEscura`:167,204,205…; `CorClara`:176,186,194…; `CorFonteFundoClaro`:253,259; `CorFonteFundoEscuro`:265,284 |
| `controllers/DetalheComboActivity.java` | `TrevoFundoClaro`:135; `CorLetraLista`:136 |
| `controllers/DetalheMinhasApostasActivity.java` | `CorClara`:214,645; `CorFonteFundoClaro`:223,232,237…; `CorEscura`:443,445,476…; `CorFonteFundoEscuro`:447,449,450…; `CorLetraLista`:463,467,471 |
| `controllers/FavoritasActivity.java` | `TrevoFundoClaro`:448,453,459; `TrevoFundoEscuro`:448,453,459; `CorClara`:448,453,459; `CorLetraLista`:454 |
| `controllers/PrincipalActivity.java` | `CorFonteFundoEscuro`:1121; `CorEscura`:1122 |
| `controllers/ResultadoApostaConfirmadaActivity.java` | `CorEscura`:98,125; `CorFonteFundoEscuro`:99,99; `CorClara`:104,121,122…; `Trapezio`:107,112; `TrevoFundoEscuro`:108,113; `CorFonteFundoClaro`:118,119,120… |
| `controllers/ResultadoLerBilhetesActivity.java` | `CorFonteFundoClaro`:261,294,295…; `CorClara`:282; `CorEscura`:284,285,407; `Trapezio`:286; `TrevoFundoEscuro`:287 |
| `controllers/SelecaoModalidadesResultados.java` | `TrevoFundoEscuro`:98,101; `CorClara`:98,101; `CorFonteFundoClaro`:98,101; `TrevoFundoClaro`:101 |
| `controllers/SimularApostaActivity.java` | `CorClara`:240,751; `CorFonteFundoEscuro`:344,346,352…; `CorFonteFundoClaro`:684,766,777…; `CorEscura`:752; `CorLetraLista`:765 |
| `controllers/VisualizarResultadosActivity.java` | `CorFonteFundoEscuro`:149,268; `CorClara`:271; `CorEscura`:271; `Trapezio`:271; `TrevoFundoEscuro`:402; `CorFonteFundoClaro`:403 |
| `model/config/LotteryGridStyleConfig.java` | `CorFonteFundoEscuro`:29,30,35…; `CorEscura`:31,34,47…; `CorClara`:78,79,83… |
| `model/model/ApostaConfirmadaDetalheModel.java` | `CorFonteFundoClaro`:53,64,65…; `CorEscura`:55,69,71; `CorClara`:63 |
| `novo/features/carrinho/ApostaViewHolder.kt` | `CorClara`:245 |
| `novo/features/carrinho/BolaoViewHolder.kt` | `CorClara`:61 |
| `view/activity/BolaoActivity.java` | `CorClara`:312; `CorEscura`:313 |
| `view/activity/DetalhesBolaoActivity.java` | `CorEscura`:156,372,410; `CorFonteFundoClaro`:160,161; `CorLetraLista`:163,164,378…; `CorFonteFundoEscuro`:410 |
| `view/activity/ResultadoApostaConfirmadaActivity.java` | `CorEscura`:84; `CorFonteFundoEscuro`:85,85; `CorClara`:90,107,108; `Trapezio`:93,97; `TrevoFundoEscuro`:94,98; `CorFonteFundoClaro`:102,103,104… |
| `view/adapter/baseadapter/FiltroApostasAdapter.java` | `TrevoFundoClaro`:449,454,458; `TrevoFundoEscuro`:449,454,458; `CorClara`:449,454,458; `CorLetraLista`:455 |
| `view/adapter/recyclerview/CarrinhoApostasListViewRapidaoAdapter.java` | `CorLetraLista`:91,93,95; `CorEscura`:96 |
| `view/adapter/recyclerview/DetalheMinhasApostasAdapter.java` | `CorFonteFundoEscuro`:88,164,169…; `TrevoFundoClaro`:239 |
| `view/adapter/recyclerview/FavoritasRapidaoRecyclerAdapter.java` | `CorClara`:59 |
| `view/adapter/recyclerview/ListaModalidadesAdapter.java` | `CorClara`:129; `TrevoFundoEscuro`:138; `CorFonteFundoEscuro`:140,170; `CorEscura`:142,172; `CorFonteFundoClaro`:144; `TrevoFundoClaro`:169 |
| `view/adapter/recyclerview/NumerosResultadosAdapter.java` | `CorLetraLista`:89 |
| `view/adapter/recyclerview/PremiacaoAdapter.java` | `CorFonteFundoClaro`:49,87,102… |
| `view/adapter/recyclerview/TrevosResultadosAdapter.java` | `CorClara`:55 |
| `view/custom/AccordionView.java` | `CorFonteFundoEscuro`:97,98; `CorEscura`:100 |
| `view/custom/ComprasDezenasView.java` | `CorLetraLista`:305; `CorFonteFundoClaro`:306,381,383; `CorClara`:340,379; `CorEscura`:385,387; `CorFonteFundoEscuro`:386 |
| `view/custom/ModalidadeSorteioItemDoisConcursosResultadoView.java` | `CorFonteFundoClaro`:67,78,83 |
| `view/custom/ModalidadeSorteioItemUmConcursoResultadoView.java` | `CorFonteFundoClaro`:74,92; `CorFonteFundoEscuro`:90 |
| `view/custom/ModalidadeSorteioLotecaResultadoView.java` | `CorClara`:62 |
| `view/custom/ModalidadeSorteioLotogolResultadoView.java` | `CorClara`:63 |
| `view/custom/ModalidadeSorteioResultadoView.java` | `CorFonteFundoEscuro`:60 |
| `view/custom/ModalideSorteioItemTimeManiaResultadoView.java` | `CorFonteFundoEscuro`:76,80,86…; `CorFonteFundoClaro`:93,108 |
| `view/custom/PartidaViewDetalhesResultado.java` | `CorFonteFundoClaro`:195; `CorFonteFundoEscuro`:197 |
| `view/custom/SuperSeteResultadoLayout.java` | `CorClara`:61; `CorFonteFundoEscuro`:68 |
| `view/features/volantenovo/activity/LotecaActivity.java` | `CorClara`:610,614,824; `CorFonteFundoClaro`:612,1099; `CorItemDezena`:1098; `CorFonteFundoEscuro`:1100 |
| `view/features/volantenovo/activity/SimulaActivity.java` | `CorFonteFundoClaro`:671,826,847…; `CorClara`:824,828,853…; `CorItemDezena`:1598; `CorFonteFundoEscuro`:1600 |
| `view/fragment/ApostaConfirmadaBolaoDetalhesFragment.java` | `CorFonteFundoClaro`:113,114,115…; `CorEscura`:181,296,319…; `CorFonteFundoEscuro`:182,205,296…; `CorClara`:396 |
| `view/fragment/ApostaConfirmadaNumeroDetalhesFragment.java` | `CorFonteFundoClaro`:116,117,157…; `CorClara`:157; `CorFonteFundoEscuro`:243,245,252…; `CorEscura`:252,322 |
| `view/fragment/ApostaConfirmadaPartidaDetalhesFragment.java` | `CorFonteFundoClaro`:109,110,111 |
| `view/fragment/BarraTituloBolaoFragment.java` | `TrevoFundoEscuro`:120; `CorFonteFundoClaro`:121,122,123…; `CorFonteFundoEscuro`:124,125,128; `TrapezioEspecial`:139,139; `Trapezio`:141 |
| `view/fragment/BarraTituloComboFragment.java` | `CorFonteFundoClaro`:87,88 |
| `view/fragment/BarraTituloSimulacaoFragment.java` | `CorFonteFundoClaro`:131,132,133… |
| `view/fragment/CabecalhoSimulacaoFragment.java` | `CorFonteFundoClaro`:120,121,122… |
| `view/fragment/ConfiguracaoRapidaoFragment4.java` | `TrevoFundoClaro`:137,145; `TrevoFundoEscuro`:137,145; `CorClara`:137,145; `CorFonteFundoClaro`:137,145 |
| `view/fragment/ConfiguracaoRapidaoFragment8.java` | `TrevoFundoClaro`:148,151; `TrevoFundoEscuro`:148,151; `CorClara`:148,151; `CorFonteFundoClaro`:148,151 |
| `view/fragment/ResultadoTimemaniaDiaDeSorteFragment.java` | `CorFonteFundoEscuro`:75,77 |
| `view/fragment/SubBarraModalidadeFragment.java` | `CorFonteFundoClaro`:103,104,105…; `CorClara`:111 |
| `view/holder/ApostaCarrinhoComboHolder.java` | `CorLetraLista`:103 |
| `view/holder/ApostaCarrinhoHolder.java` | `CorLetraLista`:134,135,136…; `CorEscura`:143,234; `CorFonteFundoEscuro`:192,193,195… |
| `view/holder/ApostaConfirmadaHolder.java` | `CorClara`:109; `CorLetraLista`:112,116,137 |
| `view/holder/BolaoCarrosselHolder.java` | `CorClara`:140,168; `CorEscura`:141,166,178…; `CorLetraLista`:142; `CorFonteFundoClaro`:143; `CorFonteFundoEscuro`:144; `Trapezio`:157; `TrevoFundoEscuro`:158; `ShapeBtnDetalhes`:173 |
| `view/holder/BolaoHolder.java` | `CorLetraLista`:262,270; `CorEscura`:268,277,357…; `CorFonteFundoEscuro`:269; `ShapeBtnDetalhes`:273 |
| `view/holder/CompraHolder.java` | `CorFonteFundoClaro`:63,111,146…; `CorFonteFundoEscuro`:109,144,144; `CorClara`:114; `CorEscura`:116,144,146 |
| `view/holder/DetalheComboHolder.java` | `CorLetraLista`:44,49,73; `TrevoFundoDetalheCombo`:76 |
| `view/holder/DetalhesCarrinhoFavoritoHolder.java` | `CorLetraLista`:71,79,106 |
| `view/holder/FavoritaHolder.java` | `CorFonteFundoClaro`:118; `CorClara`:120; `TrevoFundoEscuro`:121; `Trapezio`:122 |
| `view/holder/HomeViewHolder.java` | `CorFonteFundoClaro`:158; `CorEscura`:163,164,187…; `TrevoFundoEscuro`:166,189,234; `Trapezio`:167,190,235; `CorClara`:168,191,236…; `CorFonteFundoEscuro`:226,228,602…; `CorLetraLista`:237,238,272… |
| `view/holder/JogoBolaoHolder.java` | `CorFonteFundoClaro`:95,96,97… |
| `view/holder/JogosComboHolder.java` | `CorLetraLista`:51,52,53; `CorClara`:55,56,57 |
| `view/holder/ListaComboHolder.java` | `TrevoFundoEscuro`:53; `TarjaEspecialTrevoCombo`:54; `CorLetraLista`:55 |
| `viewModel/ResultadoApostaConfirmadaViewModel.java` | Style construction/exposure; downstream consumes it. |

Total direct style consumers: **64** (not all are eligible for pink). Also trace the direct G consumers and generic result/number fragments identified above.
