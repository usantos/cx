# Extração de PrizeRedemptionViewModel

Pedido: separar coordenação, canais de resgate, temporizador, comprovantes, avisos e redirecionamentos sem alterar interface pública, StateFlow ou comportamento. SPEC.md continua aplicável.

O ViewModel mantém construtor, métodos públicos e seus retornos, ownership de MutableStateFlow/StateFlow e fila de comprovantes. Colaboradores internos recebem esse estado e o mesmo viewModelScope/dispatcher; nenhum escopo ou dependência externa novo.

Divisão: Coordinator para detalhes/concurso/entrada em aposta; Channels para lotérica/agência, Pix e Mercado Pago; Submission para protocolo comum de resgate; Timer para Job/contador; Receipts para downloads/fila; Notices para avisos/cadastro/bloqueios; Redirects para consumo e conectividade. Tipos públicos de estado são realocados no mesmo package. O facade continua selecionando canais e coordenando Back/retorno às opções.

Preservar pontos de execução das guardas/loading, identidade dos avisos, conectividade no clique e na resposta, relógio local, consumo único, nomes/bytes/ordem dos PDFs e cancelamento estruturado. Não adicionar serialização, retries, deduplicação, resets ou alterações de timers que não existam no original.

Validação: caracterizações no original e depois da extração; corpos normalizados e assinaturas comparados ao snapshot; check completo do módulo com cobertura ampliada (mínimo 0.9501 preservado), lint, compilação do app e gates arquiteturais/Compose/formatação. UI/serviços de plataforma não serão modificados.
