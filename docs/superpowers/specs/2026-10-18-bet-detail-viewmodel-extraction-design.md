# Extração de BetDetailViewModel

Pedido: separar coordenação, detalhes, comprovantes, favoritos, repetição e mapeadores mantendo interface pública, StateFlow e comportamento. SPEC.md permanece aplicável.

O ViewModel é a fachada/coordenador, mantém construtor, init, estado e fila privados com StateFlow/Flow públicos. Quatro colaboradores internos recebem as mesmas referências, viewModelScope/dispatcher e use cases: BetDetailDetailsCoordinator, BetDetailReceiptCoordinator, BetDetailFavoriteCoordinator e BetDetailRepeatCoordinator. Favorite mantém o rascunho validado pendente. Tipos públicos realocados no mesmo package em BetDetailUiState.kt. BetDetailMappers.kt contém regras de mapeamento existentes e a montagem literal do BetDraft extraída de submitRepeat.

Manter instantes das guardas/loading, hidratação positiva vs preview, optional use cases/fallbacks, resultados públicos sem bloquear ações, canal/nome/bytes do PDF, fila cancelável, consentimento por snapshot validado, opções/conversão de concursos, quantidade/colunas/teimosinha/espelho/complementos. Não adicionar locks, deduplicação, retries, normalizações ou resets.

Validar original e refatorado com mesmas caracterizações; comparar contrato/corpos/constructor mapping; check completo Tickets (JVM+Android), cobertura incluindo fachada/colaboradores/mappers/tipos sem retirar includes anteriores nem baixar0.95; lint/gates/compilação PrdDebug; revisão independente. UI/DI/rede/contratos não mudam; nenhum commit/push.
