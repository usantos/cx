# Extração do carrinho atual

Separar `CurrentCartRoute.kt` (498 linhas) em coordenação, Scaffold, conteúdo, itens, resumo, diálogos e formatadores, preservando o ViewModel e o comportamento. Continuar no checkout existente, preservando o trabalho anterior, sem criar commits ou worktrees.

## Organização e invariantes

- `CurrentCartRoute.kt` mantém a API pública, a coleta com lifecycle, o retorno, os avisos de serviço, os diálogos de localização e os efeitos de pagamento. O consumo dos estados continua antes da navegação. A rota conserva as decisões e a ordem dos diálogos, com a janela bloqueadora de carregamento por último. Os callbacks usam o ViewModel atual, sem `remember`.
- O coordenador privado `CartContent` permanece no mesmo ramo condicional do Scaffold. Preserva `remember(viewModel)`, `LaunchedEffect(hasExpiringShare)`, a consulta ao relógio, o refresh e o intervalo de um segundo. Mantém as chaves e a captura originais, inclusive durante a substituição do ViewModel.
- `CurrentCartScaffold.kt` recebe estado, carrinho, callbacks de navegação, quatro ações e um slot de conteúdo. Preserva as prioridades, as condições de habilitação, os estilos e as dimensões.
- `CurrentCartContent.kt` recebe estado, padding, itens, horário e callbacks. Mantém a tela vazia, o cabeçalho, os avisos, as seções, as chaves da lista e os identificadores dos cliques. Inclui `CartSection` e `CartError`.
- `CartItemComponents.kt` recebe `CartRow` e seus auxiliares. Os layouts ativos de `CurrentCartPresentation.kt` permanecem com os mesmos corpos e algoritmos.
- `CurrentCartSummary.kt` recebe `CurrentCartFooter`, mantendo assinatura, corpo e contador por `items.size`. A constante `CurrentCartText` passa a ser interna para continuar compartilhada com a apresentação.
- `CurrentCartDialogs.kt` contém `FavoriteNameDialog`, com estado e callbacks, e `ConfirmCartDialog`. As decisões de exibição permanecem na rota.
- `CurrentCartFormatters.kt` reúne seções, rótulos, cores, moeda, estilo de texto e nome de modalidade. Preserva corpos, valores padrão, locale e espaços, mudando somente a visibilidade necessária ao compartilhamento.

Somente dois arquivos Kotlin existentes mudam: `CurrentCartRoute.kt` e `CurrentCartPresentation.kt`. São criados seis arquivos de produção. ViewModels, regras, recursos, dependências, `CartLocationDialogs` e gates permanecem intactos.

## Verificação

Guardar cópias dos dois arquivos e hashes dos arquivos Kotlin anteriores. Executar a referência de 96 testes JVM, três gates e o check completo do carrinho: 59 testes Android, lint e cobertura de todas as classes com mínimo de 95,01%.

Adicionar cinco contratos JVM para rótulos, seções, moeda, modalidade e estilo. Confirmar a falha inicial por acesso privado antes da extração e a aprovação depois dela. Adicionar três testes Android pela rota pública com ViewModel real: troca de ViewModel e ações, consumo antes da navegação e atualização do relógio das reservas. Executá-los contra o código original junto aos 16 testes existentes de checkout e bloqueio.

Após a extração, executar os 62 testes Android sem filtro, os testes JVM, lint, cobertura, compilação release, APK PRD debug, testes JVM do app e nove gates offline. Comparar os corpos originais revertendo apenas a extração e os bindings. Concluir uma revisão independente somente de leitura e restaurar e encerrar o emulador dedicado.
