# Extração de HomeViewModel

Pedido: separar coordenação, carregamento, notificações, carrinho, repasses sociais e decisões de entrada. Manter interface pública, StateFlow e comportamento atual. SPEC.md permanece aplicável.

A fachada preserva construtor, métodos/retornos, MutableStateFlow privado/StateFlow público e as três referências AtomicLong compartilhadas de revisão. Os flags de carregamento de parâmetros e repasses ficam nos respectivos colaboradores internos. Todos recebem o mesmo viewModelScope/dispatcher e dependências existentes, sem novos escopos.

HomeSessionCoordinator contém a observação da sessão: invalida contadores na ordem original, atualiza o mesmo estado e chama refreshUnreadNotificationCount depois. HomeLoadingCoordinator mantém carregamento sequencial de parâmetros/features/combos e finally. HomeNotificationsCoordinator mantém identidade/CAS/versões, avisos/links/destinos e contagem de não lidas. HomeCartCoordinator mantém a versão capturada dentro da coroutine. HomeSocialTransfersCoordinator mantém deduplicação/cache/retry. HomeEntryCoordinator mantém o bloqueio compartilhado das três decisões. Tipos públicos HomeUiState/HomeNotificationRedirect são realocados no mesmo package.

Não acrescentar guards, cancelamentos, normalizações, retries, reset de estado ou mudar instantes de consulta de conectividade/relógio. Sem alterar UI, navegação, Hilt, integrações, persistência ou dependências.

Aceite: caracterizações verdes no original e após extração; corpos/contratos comparados ao snapshot; check com cobertura JVM+Android real, incluindo colaboradores/coroutines sem reduzir mínimo 0.9501; lint/gates/compilação do app; testes de coordenação Home clássico e V2/restauração com default HOME_V2=true restaurado. Nenhum commit/push.
