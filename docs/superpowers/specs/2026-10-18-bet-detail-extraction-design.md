# Bet detail — extração preservando comportamento

Manter BetDetailViewModel, modelos, regras, NativeBetDetailContent, BetReceiptFiles, recursos, dependências e gates. Manter APIs/defaults públicos de BetDetailDataRoute, BetDetailRoute e BetRepeatActionsOverlay. Nenhuma instalação global, commit ou mudança de checkout.

## Fronteiras

- BetDetailRoute.kt: coleta de estado, redirect consumido antes de navegar, early return quando detalhes indisponíveis, bindings e ordem dos overlays.
- BetDetailScreen.kt: API pública BetDetailRoute, composição/provider de texto, scaffold/lista/painel e estado rememberSaveable da Teimosinha com mesmas chaves. Helpers para indisponibilidade e loading sem nós adicionais.
- BetDetailReceipts.kt: efeito com chave viewModel, LocalContext e coleta de receipts, escrita IO/open/falha idênticos. Chamado somente após early return, como antes. BetReceiptFiles permanece intacto.
- BetDetailFavorites.kt: editor e overwrite, mesmas condições/callbacks. Editor continua dentro do provider; overwrite depois do overlay de repetição.
- BetRepeatActions.kt: API pública BetRepeatActionsOverlay, coleta/efeito condicional e loading. Não alterar diferença dos textos de falha desta API e DataRoute.
- BetDetailDialogs.kt: falha do carregamento, Teimosinha e diálogos de repetição (erro, sucesso, seleção, conversão), na mesma ordem; callback de carrinho depois de consumir confirmação.

Sem novos estados, effects, caches, keys ou wrappers de layout. Helpers podem depender do mesmo ViewModel para mover bindings sem um novo modelo de ações; componentes existentes ficam intactos.

## Verificação

Caracterização verde antes/depois é apropriada à extração de comportamento existente; não fabricar um RED. Acrescentar casos de UI contra as APIs públicas: falha/retry e callbacks com aposta hidratada; favorita com cancelamento do editor e overwrite confirmado; seleção e conversão de repetição, consumo antes de callback; redirect consumido antes de navegar sem duplicação; PDF inválido após troca de VM. Casos existentes cobrem footer/recibo/modalidades e restauração Teimosinha.

Executar check/cobertura/lint/release do módulo, todos testes Android do módulo (relatar assumptions de referência externa/perfil físico), testes JVM app PRD debug e compilações PRD debug/release e nove gates. Reconstruir tokens/corpos originais, conferir hashes e revisão independente somente leitura. Cobertura configurada exclui a UI extraída e BetDetailViewModel; não ampliar a alegação.
