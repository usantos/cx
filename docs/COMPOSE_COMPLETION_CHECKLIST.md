# Migração integral Kotlin / Compose — acompanhamento ativo

Atualização visual de 15/10: tutorial de Bolão conferido com captura nativa, insets das ações/seletores de modalidades restaurados e largura dos modais flutuantes centralizada no DS. Evidências e limites: [relatório](VISUAL_ADJUSTMENTS_TUTORIAL_MODALITIES_MODALS_20261015.md).

Objetivo: substituir integralmente a implementação legada por Kotlin e Jetpack Compose,
com MVVM, casos de uso, repositories, DataSources e Hilt, mantendo layout e comportamento
da referência em `/Users/ubiracysantos/Projects/legado/simlo-app-android`.
A nova mensagem de sessão expirada solicitada pelo produto permanece como exceção deliberada. Também são deliberados os ajustes solicitados na Home v2: exibir concurso no card com registro encerrado e textos brancos em Dia de Sorte.

> Atualização pareada de 15/10/2026: [Bolão e Filtros frente ao CX](MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md). São 20 pares no mesmo AVD/dados, com Outubro Rosa ligada/desligada, e correções centralizadas no DS. `modernCheck releaseCheck` passou na árvore integrada: 1.699 testes JVM, 146 testes Android nos escopos executados e três Releases minificados. O bloqueio de captura das Activities internas foi resolvido pelo runner de instrumentação. A equivalência global de 100% continua pendente nos fluxos e casos históricos indicados abaixo.

Atualização da execução: [CaixaDS, gates e validação integrada de 14/10/2026](DS_CONSOLIDATION_20261014.md). A tabela de capturas abaixo é histórica; os dois testes raster preexistentes e as demais pendências visuais/de serviços continuam abertos.

## Critérios de conclusão

- [x] Fontes de produção em Kotlin, sem Activities/Fragments de feature ou layouts XML legados.
- [ ] Todos os fluxos e componentes do inventário auditados contra os fontes legados.
- [ ] Layouts e estados comparados no mesmo dispositivo, dados, fonte e insets.
- [ ] Ausência de formulários provisórios, ações vazias e sucesso fictício nas rotas de produção.
- [ ] Fluxos de cadastro, autenticação, biometria, sessão, carrinho, compra e resgate integrados.
- [ ] Upgrade dos dados locais e transferência do carrinho validados sem perda/duplicação.
- [x] Build, lint, testes e cobertura dos escopos exigidos, gates de arquitetura e três Releases aprovados (`modernCheck releaseCheck --offline --continue`, revalidado em 15/10/2026). A suíte Android adicional registra separadamente dois testes raster pendentes.
- [ ] Flavors debug/release e integrações externas homologados com evidência.

## Inventário de evidências visuais existentes

A tabela preserva o histórico, com atualização pareada de Bolão/Filtros nesta rodada. As demais referências não homologam a árvore atual. SSIM é apenas uma
medida auxiliar; não comprova equivalência funcional ou visual integral.

| Destino | Última captura encontrada | Situação | SSIM histórico |
|---|---|---|---|
| `account/change-login` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.605595 |
| `account/login` | `build/visual-audit/legacy-compose-login-v3/report.tsv` | compared | 0.950456 |
| `account/personal-data` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | legacy-network-error |  |
| `account/personal-data-confirmation` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.956668 |
| `account/register` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.887081 |
| `account/register-confirmation` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | legacy-network-error |  |
| `account/token` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.975639 |
| `betting/combo-detail` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.920107 |
| `betting/combos` | `build/visual-audit/legacy-compose-current-v2/report.tsv` | legacy-network-error |  |
| `betting/favorites` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.663002 |
| `betting/pool-detail` | `build/visual-audit/legacy-compose-fixtures-v4/report.tsv` | compared | 0.802603 |
| `betting/pool-view` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.917001 |
| `betting/pools` | `build/verification/marketplace-paired-20261015/paired-metrics.json` | 10 pares / 2 campanhas; carrossel, lista, expansão, favoritas e concurso | 0,970–0,993 |
| `betting/rapid` | `build/visual-audit/legacy-compose-current-v2/report.tsv` | compared | 0.996693 |
| `betting/rapid-list` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.919255 |
| `betting/simulation` | `build/visual-audit/legacy-compose-fixtures-v2/report.tsv` | compared | 0.762002 |
| `betting/tutorial` | `build/visual-audit/legacy-compose-fixtures-v2/report.tsv` | compared | 0.869390 |
| `betting/tutorial-plus` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.834130 |
| `betting/tutorial-super-seven` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.829874 |
| `cart/current` | `build/visual-audit/legacy-compose-current-v2/report.tsv` | compared | 0.010647 |
| `cart/favorite-carts` | `build/visual-audit/legacy-compose-current-v2/report.tsv` | compared | 0.947500 |
| `cart/favorite-detail` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.678104 |
| `cart/included` | `build/visual-audit/legacy-compose-fixtures-v2/report.tsv` | compared | 0.809409 |
| `cart/pool-included` | `build/visual-audit/legacy-compose-pool-pix-v4/report.tsv` | compared | 0.905378 |
| `home` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.777599 |
| `marketplace/filter` | `build/verification/marketplace-paired-20261015/paired-metrics.json` | 10 pares / 2 campanhas; inicial, rodapé, diálogo e seleção | 0,985–0,989 |
| `notifications/messages` | `build/visual-audit/legacy-compose-notifications-v4/report.tsv` | compared | 0.886868 |
| `notifications/preferences` | `build/visual-audit/legacy-compose-notification-preferences-v6/report.tsv` | compared | 0.816112 |
| `onboarding/age` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.942634 |
| `onboarding/introduction` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.965776 |
| `onboarding/splash` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | legacy-network-error |  |
| `onboarding/tutorial` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.943629 |
| `onboarding/unavailable` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.942801 |
| `onboarding/update` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.951846 |
| `payment/add-card` | `build/visual-audit/legacy-compose-fixtures-v2/report.tsv` | compared | 0.859879 |
| `payment/card-preview` | `build/visual-audit/legacy-compose-fixtures-v2/report.tsv` | compared | 0.922734 |
| `payment/cards` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.636063 |
| `payment/confirm-card` | `build/visual-audit/legacy-compose-fixtures-v2/report.tsv` | compared | 0.880121 |
| `payment/confirmation` | `build/visual-audit/legacy-compose-payment-confirmation-v4/report.tsv` | legacy-network-error |  |
| `payment/cvv` | `build/visual-audit/legacy-compose-fixtures-v2/report.tsv` | compared | 0.871605 |
| `payment/method` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.898773 |
| `payment/pix-copy` | `build/visual-audit/legacy-compose-pix-v8/report.tsv` | compared | 0.858421 |
| `payment/pix-guidance` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.837065 |
| `responsible/form` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.886252 |
| `responsible/social` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | redirected |  |
| `responsible/start` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.922479 |
| `responsible/suspension` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | compared | 0.865942 |
| `results/confirmed-bet` | `build/visual-audit/legacy-compose-confirmed-bet-v6/report.tsv` | compared | 0.893049 |
| `results/detail` | `build/visual-audit/legacy-compose-support-results-v1/report.tsv` | compared | 0.927662 |
| `results/modalities` | `ausente` | pendente |  |
| `retail/location` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.953599 |
| `retail/prize-agency` | `build/visual-audit/legacy-compose-prize-agency-v6/report.tsv` | compared | 0.937225 |
| `retail/prize-confirmation` | `build/visual-audit/parity-script-debug/report.tsv` | redirected |  |
| `retail/prize-guidance` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.792764 |
| `retail/prize-lottery` | `build/visual-audit/legacy-compose-prize-lottery-v5/report.tsv` | requires-fixture |  |
| `retail/prize-mercado-pago` | `build/visual-audit/legacy-compose-prize-mp-v4/report.tsv` | compared | 0.865449 |
| `retail/prize-pix` | `build/visual-audit/legacy-compose-fixtures-v2/report.tsv` | compared | 0.719386 |
| `retail/search` | `build/visual-audit/legacy-compose-current-v2/report.tsv` | compared | 0.000091 |
| `support` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.704126 |
| `support/game-help` | `build/visual-audit/legacy-compose-support-v4/report.tsv` | compared | 0.598037 |
| `support/terms` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | legacy-network-error |  |
| `support/terms-acceptance` | `build/visual-audit/legacy-compose-audit-account-support-v1/report.tsv` | legacy-network-error |  |
| `tickets/bet-detail` | `build/visual-audit/legacy-compose-bet-detail-v12/report.tsv` | redirected |  |
| `tickets/manual-code` | `build/visual-audit/legacy-compose-manual-ticket-v2/report.tsv` | compared | 0.968241 |
| `tickets/my-bets` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.624995 |
| `tickets/purchase-detail` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.904761 |
| `tickets/purchases` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.691962 |
| `tickets/read` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.905146 |
| `tickets/read-game-choice` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.701574 |
| `tickets/read-result` | `build/visual-audit/legacy-compose-current-v1/report.tsv` | compared | 0.928581 |
