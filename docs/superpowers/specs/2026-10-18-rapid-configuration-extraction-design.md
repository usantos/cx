# Extração do formulário Rapidão

Separar a rota atual de541 linhas em coordenação, sincronização do pager, etapas, resumo e formatadores. Continuar no checkout existente, preservar alterações anteriores, não criar commit/worktree ou alterar ViewModel, domínio, recursos, dependências e gates.

## Arquivos e contratos

- RapidConfigurationRoute.kt: API pública inalterada; collectAsStateWithLifecycle, rememberPagerState(initialPage=state.page), summaryVisited com remember sem chaves, efeito por state.page, sincronização, efeito saved e diálogos na ordem original. Bindings ao VM atual sem remember.
- RapidConfigurationContent.kt: Scaffold/loading/HorizontalPager com a mesma ordem0..6 e callbacks por RapidConfigurationActions. Salvar só aparece após visitar resumo; mantém canSave/loading. Repetir exatamente configurações visuais e textos.
- RapidConfigurationPager.kt: RapidPageIndicator internal, RapidPagerSynchronization internal e RAPID_PAGE_COUNT internal const7. Preservar navigating por remember(pager), rememberUpdatedState(onSettled), chaves dos dois efeitos e finally/distinctUntilChanged; dots não recebem páginas intermediárias das animações.
- RapidConfigurationIntroPages.kt: welcome, prêmio e wheel. Manter sugestões0/20/40/60/80/100 milhões, normalização0..100, wrap e callbacks.
- RapidConfigurationAmountPage.kt: controles e diálogo com seus três remember originais, presets/limites/retry/parser/erros, sem mover estado para a rota ou VM.
- RapidConfigurationSelectionPages.kt: modalidades, números e favoritos (auxiliar existente não ligado ao pager); manter seleção/limpeza/100células, erro/loading/empty de favoritos e mapeamento de cor.
- RapidConfigurationComponents.kt: RapidPreset, RapidNumberCells, RapidSectionHeader compartilhados. Visibilidade internal somente quando necessário entre arquivos.
- RapidConfigurationSummaryPage.kt: resumo/cartões/números; editar envia1/2/3/4/5 e números permanecem ordenados. Não adicionar resumo de favoritos.
- RapidConfigurationFormatters.kt: rapidFavoriteNumbers private→internal; corpo exato, padding de números, separador por colunas e complementos. formatBetAmount compartilhado existente e formatRapidAmount privado do VM permanecem inalterados.

## Verificação

Salvar snapshot/hashes de Kotlin atuais. Baseline JVM/três gates; três contratos unitários do formatter observam RED por acesso private antes de extração. Caracterizar rota pública com VM real/usecases locais: salvar invisível antes do resumo e persistente após, edição de resumo, seleção e aviso de conflito, limites/custom diálogo e retry, save/desabilitado e back, troca de VM. Pager existente e troca de callback durante efeito sem reiniciar. Rodar seleção Android antes/depois; toda suite JVM da feature e app PRD, check/lint/cobertura configurada95.01%, release compile, PRD debug build e nove gates offline. Comparar todos os corpos originais revertendo somente extração/bindings/visibilidade. Uma revisão independente somente leitura. Restaurar AVD dedicado e registrar evidência.
