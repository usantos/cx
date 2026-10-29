# Extração da consulta de resultados

Separar ResultsRoute.kt em coordenação, modalidades, pesquisa, detalhes, premiações e mapeadores. Preservar API pública, ViewModel e comportamento, trabalhando no checkout existente e mantendo todo o trabalho anterior.

## Organização

- ResultsRoute.kt conserva ResultsRoute e ResultsScreen com assinaturas, defaults, coleta lifecycle, efeito por showDetail e retorno condicionado à seleção idênticos.
- ResultModalities.kt recebe ResultModality, officialModalities, a ordem visual original, ResultModalityScreen e ResultModalityCard.
- ResultContestSearch.kt recebe o botão de alternância e o campo de pesquisa, com parâmetros e callbacks atuais. Visibilidade, FocusRequester, teclado e efeitos permanecem no detalhe, com as chaves originais e no mesmo ramo.
- ResultDetailScreen.kt recebe ResultDetailScreen e o rodapé de navegação. Preserva guardas dos concursos anterior/seguinte, erro de entrada, loading/erro/resultado/vazio e cabeçalho da modalidade.
- ResultContent.kt recebe ResultContent e NumberRows: números, esportes, segundo sorteio, trevos e data. Mantém ordem, estilos e geometria.
- ResultPayouts.kt recebe somente o painel de premiações e arrecadação. Os estilos e espaçamentos calculados no conteúdo são passados sem recalcular ou mudar valores; DrawResultSupplement continua com includeContestSummary=false.
- ResultMappers.kt recebe modalityName e contestButtonLabel, mantendo fallback, spans e textos.

Somente ResultsRoute.kt muda entre os arquivos Kotlin existentes, além dos novos testes. Os helpers existentes ResultFormatting e DrawResultSupplement, ViewModels, recursos, dependências e gates ficam intactos. Seis arquivos novos de produção, sem novas abstrações de estado ou mudanças nas chaves dos efeitos.

## Verificação

Guardar referência de código e hashes. Executar check do módulo, compilação release e gates de arquitetura, Compose e design system antes da extração. Caracterizar pela rota pública os retornos, a pesquisa com erro e carregamento e a troca de callbacks; executar os testes contra o código original e depois contra a extração junto do teste de paridade existente. Comparar corpos, APIs, efeitos e hashes. Concluir check, lint, cobertura configurada, compilação release, testes JVM do app, APK PRD debug, nove gates e revisão independente somente de leitura. Restaurar e encerrar o emulador dedicado.
