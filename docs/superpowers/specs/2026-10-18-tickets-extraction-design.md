# Extração de listas, detalhes, filtros e consulta manual de bilhetes

Refatorar a fonte local de 811 linhas de `TicketsDataRoutes.kt`, mantendo o ViewModel e comportamento. Referência salva em `build/verification/tickets-refactor-20261018/TicketsDataRoutes.before.kt`; preservar todo o trabalho anterior da conversa.

## Organização

- `TicketsDataRoutes.kt` mantém as cinco funções públicas e defaults, coleta de estado, efeitos de carregamento/retorno, navegação e avisos. `TicketResultDataRoute` já delega a apresentação existente e permanece com o mesmo corpo.
- `ConfirmedBetsContent.kt` / `PurchasesContent.kt`: Scaffolds e listas atuais, estado e ações. A seleção de período da lista continua selecionando e aplicando nessa ordem; filtros de diálogo continuam apenas selecionando até aplicar.
- `PurchaseDetailContent.kt`: layout e seleção das linhas do detalhe. Avisos/ações de status, favoritos e navegação continuam coordenados pela rota.
- `TicketsBetFilters.kt`, `PurchaseFilters.kt`, `TicketsFilterComponents.kt`, `PurchaseDialogs.kt`: filtros e diálogos atuais, com callbacks tipados; helpers compartilhados no mesmo módulo.
- `ManualTicketContent.kt`: formulário, campo e máscaras atuais. Validade do código e efeito de onResult permanecem na rota.
- `PurchasePixContent.kt`: painel, estado do contador/cópia e os dois efeitos existentes; funções de prazo/pending Pix mantidas sem alterações.
- `TicketsDataComponents.kt`: estados de loading/erro, cartões e helpers existentes, inclusive auxiliares atualmente sem uso.

## Contratos

Preservar ViewModel/estado compartilhado, sem novos owners. Carregamento inicial continua com chaves(viewModel,privateDataEnabled) e guard privado/loaded originais. Paginação continua dentro do item lazy, condicionada a canLoadMore/isLoadingMore e chave purchases.size; não antecipar carregamento na rota.

Detalhe continua carregando por purchase.id, com o mesmo fallback de compra e filtro/predicado de linhas. NavigationResumeEffect, consumo dos redirects/avisos Pix, pausa/retorno, destinos e ordem dos diálogos permanecem iguais. Não mudar guards de favorito/overwrite/DAM.

PendingPurchasePix mantém remember por id/expiração, atualização de 1s, polling 20s somente após cópia e enquanto há prazo; não criar novo timer/clock. Consulta manual mantém o limite existente, máscaras, retailer opcional, carregamento/erro e efeito keyed por loaded/bets/ticketResult, inclusive resultado vazio bem-sucedido. Não adicionar consumo de evento, validação ou limpeza de estado.

Todos os componentes novos recebem estado/callbacks sem ViewModel. Binding referencia o ViewModel atual a cada composição, sem remember de closures. Preservar layout, recursos, estilos, textos, semântica, paginação, prioridades e comportamento de cancelar filtros existente. Sem mudanças de domínio, recursos, dependências, gates ou KMP.

## Verificação

Baseline JVM/gates e 18 casos Android controlados:3 lista confirmada,6 status de compra,4 recibos/prazo e 5 novos casos de coordenação (flags privadas das duas listas, cancelar/aplicar filtro, consulta manual vazia e erro). Rodar os novos casos antes da extração pela API pública. Comparar assinaturas/corpos/efeitos/callbacks estruturalmente contra a referência. Executar tickets check/cobertura/lint, compile release, app PRD debug build/unit e nove gates. Uma revisão independente final. Usar somente AVD dedicado 5560, salvar/restaurar configurações/pacotes exatos e encerrar o AVD. Sem commit.
