# Extração dos carrinhos favoritos

Separar FavoriteCartsRoute.kt atual de502linhas, preservando as duas APIs públicas, FavoriteCartsViewModel e comportamento. Mesmo checkout/trabalho anterior; sem commits/worktrees, mudanças de domínio/VM/recursos/dependências/gates ou correções funcionais.

## Organização e contratos

- FavoriteCartsRoute.kt mantém FavoriteCartsRoute/FavoriteCartDetailRoute, assinaturas/defaults inclusive onHelp opcional da lista (continua sem uso), collectAsStateWithLifecycle, efeitos imported/cartDeleted e BackHandler do detalhe. ConsumeImported antes de onCartReady; confirmação/decisões/diálogos continuam coordenados pelas rotas. Bindings atuais sem remember.
- FavoriteCartsContent.kt recebe state/onBack/onOpen/privateDataEnabled/FavoriteCartsActions(load,toggleCart,requestImport,requestCartRemoval). Scaffold e precedência loading/error/privateflag/empty/list, keyid e callbacks exatos.
- FavoriteCartDetailContent.kt recebe state/onBack/onHelp/onReload/onRequestBetRemoval. Mesmo Scaffold/toolbarhelp/lazyheader/keyid/conteúdo. BackHandler/efeito de remoção permanecem na rota.
- FavoriteCartListItem.kt: FavoriteCartRow, ações raster/vetor e resumo. Mesma medição do nome, layout, animações100ms, estado/loading e callbacks; função vetorial auxiliar não utilizada é preservada.
- FavoriteCartBetCard.kt: cartão de aposta/detail, cores, SuperSete em colunas somente nãoSurpresinha, números/complementos/trevos e delete. Sem inventar novos campos ou mudar ordem/guardas.
- FavoriteCartComponents.kt: heading e loading/error/retry compartilhados.
- FavoriteCartDialogs.kt: confirm e seleção especial, com o mesmo remember sem chave e selected=-1, opçõesAmbas/regular/especial, confirmar somente selected>=0 e dismissoriginal.
- FavoriteCartFormatters.kt: favoriteTextStyle e favoriteNameSize originais (private→internal) mais seis funções internal extraídas dos textos inline existentes: favoriteSummaryModalityName, favoriteDetailModalityName, favoriteBetSummaryText, favoriteBetComplement, favoriteBetNumbers, favoriteBetClovers. Manter diferençaTimemania lista/detalhe, +milionária, ordem/número100→00/padding, contagens dobackend/Surpresinha, separadores/espacos, nulidade de complementos. Medição conserva LocalDensity/rememberTextMeasurer/remember(name,density,measurer), máximo14dp convertido para sp, largura150dp, mínimo8sp/decremento1.

## Verificação

Snapshots/hashes de todos Kotlin anteriores. BaselineJVM/trêsgates e featurecheck integral (todosAndroid/lint/cobertura95.01% todasclasses). Seis testes JVM para os seis formatadores com outputs literais independentes, REDunresolvedhelpers antes da implementação e GREEN depois. Casos novosAndroid pela rota pública/VM real/usecases locais para privateflag sem alterar cargas, importconsume antes callback e repetição confirmada, seleção especial sem opção/cancel/reset, trocaVM/callbacks, detalhe retry/help/back e exclusão da última aposta. Rodar seleção de favoritos existente+nova antes de extração; no final rodar TODOAndroid do módulo comcheck/cobertura sem filtros. Comparar cada declaração/corpo reconstituído com fonteoriginal revertendo somente extração/bindings/formatters/visibilidade. Preservar hashes VM/outrosKotlin. PRDdebugAPK/appunit, releasecompile e novegatesoffline. Uma revisão independente somente leitura. Restaurar e pararAVDdedicado.
