# Extração das apostas favoritas

Separar coordenação, lista, filtros, inclusão, seletores e componentes por modalidade, preservando ViewModel e comportamento. Continuar no checkout existente sem commits/worktrees; aplicar os perfis VoltAgent já autorizados na análise prévia e revisão final.

## Organização e invariantes

- FavoriteBetsRoute conserva a API pública, coleta lifecycle, showFilter sem chave e composição/ordem dos diálogos de filtro, exclusão, inclusão, sucesso e erro. Ações ligadas ao ViewModel atual são passadas diretamente, sem remember.
- FavoriteBetsContent recebe o Scaffold e a prioridade privateDataEnabled/loading/vazio/lista. Estado, retorno, ajuda, criação, filtro, inclusão e exclusão chegam por parâmetros. O ramo de visitante conserva o cabeçalho sem filtro efetivo; não suprime diálogos da raiz.
- FavoriteBetsList recebe a lista, item, vazio e cabeçalho. Preserva favorite.id, cabeçalho por comparação com o item anterior, ordem e callbacks com o FavoriteBet original.
- FavoriteBetsFilterDialog recebe ModalityFilterDialog. Preserva pending por remember(selected), cancelamento e aplicação explícita.
- FavoriteBetsAddDialog recebe AddFavoriteOptionsDialog com estado e cinco callbacks: cancelar, confirmar, tipo, recorrência e espelho. Preserva showContest, showRecurring e choiceOpened sem chaves, a ordem/distinctBy dos concursos e o mesmo cálculo de singleNormalRecurring. Não adicionar key(candidate/VM) nem resetar estados. Normal único com recorrências inicia habilitado; demais casos exigem abrir o heading de tipo.
- FavoriteBetsSelectors recebe heading e wheel, com corpos e callbacks intactos. Não corrigir capturas/efeitos internos do design system durante a extração.
- FavoriteBetsModalityComponents recebe grid compartilhado, trapézio, Super Sete e constante interna favoriteLegacyTextStyle. Mantém exclusão de Loteca por nome, números, estilos, arte de campanha e cache remember(resource, resources, configuration, context.theme).

Seis arquivos novos de produção; somente FavoriteBetsRoute existente muda. ViewModel, regras, domínio, design system, recursos, dependências e gates permanecem intactos.

## Verificação

Referência e hashes de fontes/recursos/configurações capturados. Check do módulo, cobertura configurada, release e três gates como referência. Quatro testes novos pela rota pública com ViewModel real: filtro aplicado/cancelado e criação; normal/recorrência/espelho/sucesso; especial com troca de VM e conteúdo privado desabilitado enquanto diálogo persiste; exclusão cancelada/falha e agrupamento contíguo. Executar contra original e extração. Reexecutar o teste existente do visitante no app.

Comparar corpos revertendo apenas extração/bindings/visibilidade, além dos hashes. Final: check/lint/cobertura, release, quatro Android de coordenação, um Android de visitante, JVM do app, APK PRD debug e nove gates. Revisão independente somente de leitura. Restaurar pacotes/tela e encerrar emulador dedicado.
