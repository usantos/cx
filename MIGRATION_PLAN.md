# Plano de execução — SPEC de modernização

Autoridade: SPEC.md; auditoria: MIGRATION_AUDIT.md. Execução explicitamente autorizada pelo usuário, em etapas pequenas no checkout atual, sem commits/pushes. Prioridade: preservar comportamento antes de idealizações arquiteturais.

1. [x] Auditoria/snapshot e baseline completo modernCheck/releaseCheck; novas caracterizações contra originais.
2. [x] Separar as seis rotas:coordenação, Screen, componentes e adaptadores Android; manter estados/chaves/callbacks/contratos.
3. [x] Decompor as três ViewModels em colaboradores coesos mantendo fachada pública, StateFlow privado e use cases; testar cancelamento/ordem/respostas antigas.
4. [x] Separar mapeadores/adaptadores/orquestração dos dois repositories sem alterar DTOs, wire/cache/rotas/erros.
5. [x] Consolidar CaixaDSLegacyComponents em arquivos por responsabilidade, APIs/corpos/defaults idênticos; previews relevantes.
6. [x] Migrar os três testes Java restantes para Kotlin com mesmas assertivas; centralizar versões de teste sem atualização indiscriminada.
7. [x] Aplicar refinamentos de conformidade comprovadamente necessários pela auditoria; preservar segurança e comportamento, sem inventar integrações.
8. [x] Compilar/testar checkpoints e integrar testes/cobertura dos novos colaboradores sem reduzir gates.
9. [x] Revisão independente e matriz completa modernCheck/releaseCheck; UI/críticos em emulador, análise estática e equivalência.
10. [x] Atualizar MIGRATION_PROGRESS.md, ARCHITECTURE.md e relatório de validação final com métricas reais, pendências e limites.

## Fronteiras de trabalho

UI controla só as seis rotas e novos vizinhos/testes Android. Estado controla só as três ViewModels/vizinhos e testes JVM. Data controla só os dois repositories/vizinhos e testes JVM. Agente principal controla DS, Java de teste, gradle/cobertura/DI/documentos/verificação. Não executar Gradles/dispositivos concorrentes nem tocar arquivos da outra frente.

## Critérios transversais

Sem novas fontes fictícias em produção, sem alteração de protocolo de autenticação/pinning/RASP, sem excluir código usado ou enfraquecer gates. Domain permanece sem Android/Compose/DTOs. Dependências são injetadas e Screens emitem callbacks. Tipos de erro/estados existentes preservados quando adequados. Novos bugs comprovados exigem teste de reprodução antes da correção; extrações literais usam caracterização verde antes/depois.


## Continuação — sete arquivos por responsabilidade (18/10/2026)

Plano detalhado: docs/superpowers/plans/2026-10-18-seven-responsibilities.md. Pedido explicitamente autorizado; execução inline no checkout atual, sem commits/pushes.

1. [x] Capturar snapshots/hashes/APIs; executar baseline JVM e caracterizações Android originais.
2. [x] Separar TicketsPayloadMapper e RemoteCartRepository por operação/tipo de payload, mantendo entradas e falhas.
3. [x] Separar BettingGameSelection por seleção/modalidade/quantidade/reset, com avisos compartilhados.
4. [x] Separar FavoriteCartsViewModel por carga/cache, remoção, importação e apresentação de falhas.
5. [x] Separar PaymentViewModel por métodos, cartões, pré-compra, Pix, avisos e redirecionamentos.
6. [x] Separar FilaBraGate por inicialização, fila, mock, componentes, WebView e broadcasts, preservando efeitos.
7. [x] Separar AppAuthAuthenticationGateway por autorização/biometria/renovação/tokens/logout, mantendo monitor e stores.
8. [x] Concluir checks/testes/cobertura/lint/build/gates, revisão independente e registro final desta rodada.

Os mínimos e inputs de cobertura permanecem; includes Betting/Payment recebem somente adições para os colaboradores. Os novos tipos de estado continuam no mesmo package público. As divergências geométricas históricas de perfis diferentes permanecem documentadas, sem alterar oráculos para aprovar refatorações.

## Continuação — dez arquivos por responsabilidade (18/10/2026)

Plano detalhado: `docs/superpowers/plans/2026-10-18-ten-responsibilities.md`. Execução inline autorizada no checkout existente, sem commits/pushes.

1. [x] Capturar snapshots/hashes, 877 testes JVM de referência e APIs compiladas.
2. [x] Separar os quatro ViewModels por coordenação/estado/carregamento/formulário/decisões, mantendo os owners originais.
3. [x] Separar telas de opções/Pix e registros do grafo Tickets com corpos literais.
4. [x] Separar famílias de clientes, Retrofit, DTOs por domínio e cenários/resolução/fixtures de mock.
5. [x] Ampliar filtros de cobertura de forma aditiva e validar cinco novas caracterizações nas fachadas originais e finais.
6. [x] Executar checks/lint/gates/APK, instrumentação por frente, equivalência e revisão independente.
7. [x] Atualizar auditoria, progresso, arquitetura e relatório de validação.

882 testes JVM aprovados (baseline 877; cinco novas caracterizações aprovadas também com as fachadas originais); 65 testes Android aprovados, um teste condicional ignorado, zero falhas/erros; 352 comparações de fonte/API aprovadas. Revisão independente sem achados. Checks/cobertura/lint dos sete módulos envolvidos, lint PrdDebug do app, dez gates e APK PrdDebug aprovados.

A matriz global modernCheck/releaseCheck histórica não foi repetida integralmente; esta execução valida o escopo acima.

## Sete ajustes concretos de conformidade — 19/10/2026

Concluída a etapa dos sete ajustes concretos: testes de regressão antes das correções reproduziram I/O na Main, cancelamentos absorvidos e coleta abaixo de STARTED. A implementação preservou APIs/StateFlows e separou as políticas do carrinho da persistência. Plano executado: docs/superpowers/plans/2026-10-18-seven-compliance.md.

Próximas etapas: decidir o destino da caixa de notificações legada com base em contrato real; priorizar refinamentos estruturais por benefício comprovado; completar a homologação global de integrações e perfis visuais. Esta entrega não declara a SPEC inteira encerrada.

## Encerramento dos links/comprovantes e validação global — 19/10/2026

1. Correções de abertura e recuperação: concluídas localmente, preservando interfaces/StateFlows; testes de falha e recriação aprovados e revisão independente encerrada sem achados.
2. Matriz global: concluída. modernCheck e releaseCheck completos aprovados, sem exclusões, backend simulado ou override da identidade/flags. Resultados efetivos na última seção de MIGRATION_VALIDATION.md; APKs release unsigned por falta de recursos privados de assinatura.
3. Visuais: os cinco casos condicionais foram executados sem alterar asserts/goldens. Três passaram; duas coordenadas absolutas permanecem reprovadas no Pixel emulado. Repetir no perfil Samsung da referência com os mesmos insets/firmware, ou obter referência equivalente formalmente verificada; preservar os oráculos enquanto isso. O deslocamento de 80px do inset não deve virar compensação de layout em produção.
4. Homologação real: bloqueada externamente pela indisponibilidade confirmada de conta autorizada e dispositivo físico biométrico. Recursos locais existentes foram inspecionados. Quando disponíveis, validar OAuth/callback/refresh/logout, biometria, SSO, fila/broadcast/WebView e provedores privados por ambiente, com configuração de pinning aprovada. Não substituir por mocks nem relaxar segurança.
5. Distribuição: assinatura conferida nos três artefatos release; todos unsigned. Os arquivos privados de assinatura não estavam disponíveis no inventário local. APK gerado sem assinatura comprova build, não distribuição/homologação. Nenhum commit/push/publicação faz parte desta rodada.

A SPEC.md não está encerrada a 100% enquanto os casos físicos, a homologação real e as decisões funcionais pendentes não tiverem evidência suficiente. Decisões funcionais fora deste escopo, como a origem real das mensagens legadas, continuam conforme as pendências anteriores.

## Etapa incremental: central de mensagens — 19/10/2026

A referência confirmada pelo usuário é o legado. Extrair catálogo, operações locais e preferências sem alterar sua semântica; separar os bindings Hilt de contador e popup, preservando os sete métodos públicos de NotificationsModule como pontos de compatibilidade. A caixa continua sem singleton e cada destino obtém uma instância nova. Os dois bindings singleton da leitura autenticada mantêm seus escopos.

Caracterizar o provider real pela factory antes e depois, comparar o catálogo literal e as assinaturas JVM, executar os checks de Data/Domain/Notifications, os dez testes Android de Notifications, os dez gates existentes e assemble/JVM/lint dos três flavors debug. Não mudar oráculos ou limites de cobertura. Não substituir a central por endpoints incompletos nem agregar FCM sem contrato. Homologação privada, assinatura release e os dois casos visuais já reprovados permanecem fora do resultado desta extração.

## Extração do checkout do carrinho — 19/10/2026

Plano detalhado: docs/superpowers/plans/2026-10-19-cart-checkout.md. Execução inline no checkout existente, preservando trabalho anterior e sem commits/pushes.

- Capturar baseline de fontes/APIs e caracterizar suspensão de permissão/cancelamento nas etapas seguintes no coordenador original.
- Manter coordenação/guardas de entrada na fachada; separar localização, permissões, compra recente, validação, avisos e redirecionamentos.
- Preservar a fronteira de cancelamento/finally, callbacks consumidos uma vez, consentimento vinculado ao carrinho, relógios, ordem das chamadas e as mesmas instâncias de estado/escopo/dispatchers.
- Executar Cart check completo (JVM, Android, lint, cobertura mínima original), Domain check, assemble/JVM/lint App PrdDebug e dez gates; revisão independente e comparação de APIs/fontes.

A mudança não redesenha regras de negócio, rede, persistência, UI, reserva de cotas ou autenticação. Evidências atuais em build/verification/cart-checkout-20261019/ e resultados finais em MIGRATION_VALIDATION.md. As pendências globais e a origem demonstrativa da central de mensagens permanecem documentadas nas etapas anteriores.

## Extração do histórico Tickets — 19/10/2026

Plano: docs/superpowers/plans/2026-10-19-tickets-history.md. Execução inline no checkout atual, com snapshots próprios para preservar trabalho anterior e sem commits/pushes.

- Caracterizar primeiro, sobre o original, append após expirar sessão, retry com catálogos existentes e leitura enfileirada dos filtros.
- Manter a fachada e separar TicketsHistoryBets, TicketsHistoryPurchases, TicketsHistoryPagination, TicketsHistoryBetFilters, TicketsHistoryPurchaseFilters e TicketsHistoryRecovery.
- Preservar as mesmas verificações de sessão, ordem de catálogo/página, tamanho 10, offset, deduplicação/ordem, flags, callback de erro e limites de coroutine/cancelamento.
- Adicionar os seis colaboradores à cobertura, mantendo integralmente padrões, entradas conectadas e mínimo 0.95; executar Tickets/Domain checks, AppPrdDebug assemble/JVM/lint e dez gates.
- Revisão independente e relatórios de validação; preservar limites globais anteriores.

A instrumentação usa somente o AVD temporário SimloTicketsHistory 20261019/emulator 5562 com APK legado local para fontes/tema, sem substituir a instalação moderna do AVD existente. O emulador 5580 é alheio à tarefa e não é usado. O serial é selecionado explicitamente no comando. Evidências em build/verification/tickets-history-20261019/.

## Extração da Home clássica — 19/10/2026

Plano executável em docs/superpowers/plans/2026-10-19-home-legacy-content.md. Preservar o checkout compartilhado e comparar snapshots próprios, sem commit/push. Caracterizar geometria/callbacks, seleção após recomposição e quadros intermediários das animações sobre o original antes de extrair. Manter composição/estado no dono atual e separar barra superior, carrossel, cartões, atalhos, campanha e resumo em funções sem estado.

Comparar corpos/assinaturas declaradas e capturas exatas antes/depois, sem máscaras/tolerâncias. Executar a Home clássica com identidade/flag somente de comando; restaurar configuração padrão para Home check, App PrdDebug assemble/JVM/lint e dez gates. Revisão independente e resultados reais em MIGRATION_VALIDATION.md. A etapa não encerra as pendências globais de assinatura, testes físicos, mensagens demonstrativas e homologação privada.

## Refatoração MVVM/Clean de todas as features —19/10/2026

Escopo autorizado:14features, seus pacotes Presentation/Domain/Data, estados/efeitos separados de ViewModels e correção dos acoplamentos conhecidos de Home e conta. Design:docs/superpowers/specs/2026-10-19-feature-mvvm-design.md. Plano:docs/superpowers/plans/2026-10-19-feature-mvvm.md. Execução incremental inline, snapshots próprios, sem commits/pushes.

Ordem: baseline/gate negativo → piloto Support → features simples → Betting/Cart/Marketplace → Payment/Retail/Tickets → Account/Home e seams de plataforma → modernCheck/releaseCheck completos e revisão independente. Cada grupo deve compilar e passar seus testes antes da próxima migração. Preservar wire/persistência/segurança e a origem legada autorizada das mensagens; não atribuir homologação privada aos testes locais.

Etapa MVVM concluída: todos os grupos e as sete extrações implementados e revisados; modernCheck/releaseCheck integrais padrão aprovados nos três flavors. Instrumentação complementar concluída, sem alterar oráculos: divergências450dpi de Loteca/colunas/viewport passaram em480dpi, e dois prompts físicos reproduziram a falha exata no código anterior. Dos cinco visuais condicionais, três passaram e duas coordenadas físicas continuam pendentes. As limitações de aparelho/conta/assinatura e os detalhes efetivos estão na última seção de MIGRATION_VALIDATION.md. Não há implementação desta rodada aguardando aprovação; a homologação externa não foi substituída por testes locais.

## Plano atual — módulos independentes por feature, 19/10/2026

Referência detalhada: `docs/superpowers/plans/2026-10-19-feature-modules.md`; arquitetura atual em `ARCHITECTURE.md`. Os planos anteriores são registros históricos, não a localização atual dos fontes.

- [x] Capturar baseline e contratos; verificar os ciclos/roots anteriores.
- [x] Cortar ciclos com contratos mínimos e compilar Domain/API separadamente.
- [x] Compilar Data/Platform nas features e dividir o adapter Retrofit.
- [x] Organizar Domain e transporte/Data com compatibilidade pública.
- [x] Remover a compilação da Home pelo app, preservando recursos/flags/composição.
- [x] Migrar bindings e grafos por feature, mantendo NavHost e sessão no app.
- [x] Centralizar handles de persistência, auxiliares de teste e convenções.
- [x] Verificar grafo configurado e dez fixtures negativas reais dos gates.
- [x] Executar matriz global atual, caracterização visual final, revisão independente e registro dos resultados/limites: modernCheck/releaseCheck aprovados; cinco visuais executados, com duas divergências físicas anteriores documentadas.

Não criar camadas/reducers/APIs vazios nem alterar armazenamento, backend, sessão, StateFlows, flags ou oráculos para ajustar a estrutura. Os módulos de compatibilidade e aliases são deliberados para preservar nomes públicos. A agregação admitida é de relatórios; cada fonte de produção tem um único módulo compilador.


## Continuação — estrutura APP → features → CORE (19/10/2026)

A orientação final do usuário define a propriedade física dos módulos, não só pastas por camada dentro do app. Atualizados os donos: app como host/composição/navegação, estado e adaptadores nas features e serviços compartilhados nos módulos CORE. Foram mantidos os FQNs públicos existentes com donos explícitos e sem duplicar bindings. Plano de execução e evidências desta continuação: `docs/superpowers/plans/2026-10-19-app-mvvm.md` e `build/verification/app-mvvm-20261019`. Resultado global fica condicionado à conclusão da matriz atual; homologação física/privada segue fora da disponibilidade local.

## Host APP tipado e sem agregação Data — 20/10/2026

Plano: `docs/superpowers/plans/2026-10-20-app-host-coherence.md`; especificação aprovada: `docs/superpowers/specs/2026-10-20-app-host-coherence-design.md`.

- [x] Caracterizar as fronteiras e instalar os gates APP e Feature.
- [x] Transferir adaptadores/bindings dos 13 grafos para suas features e declarar configuração tipada do host.
- [x] Substituir o parâmetro universal no NavHost por dependências de navegação tipadas e separar inicialização/sessão.
- [x] Remover injeção ativa de `AppContainer` e dependências de produção `app → feature:*:data`.
- [x] Corrigir placeholders OAuth para os manifests instrumentados de Notifications e Tickets e cobrir as fachadas/notificações e ações do ViewModel que os gates exercitam.
- [x] Executar `modernCheck`, `releaseCheck`, gates Python e verificações de ownership com os três flavors.

Resultado: ambos os gates globais passaram juntos em 20m42s; evidência detalhada, testes/skips e limites estão em `MIGRATION_VALIDATION.md`. A comparação binária pré/pós dos descritores públicos não foi possível: o snapshot preservado antes das alterações é inventário de fonte e declara expressamente não substituir o dump compilado. Os FQNs e as declarações compatíveis inventariadas foram mantidos, e os tipos de compatibilidade compilaram em seus novos owners. Não se declara verificação byte a byte da ABI antiga.

## Limpeza de compatibilidade de CORE — 20/10/2026

Solicitação do usuário: aplicar a mesma limpeza das fachadas inativas do APP à camada CORE. Consolidar os consumidores nos donos existentes de ambiente, rede, segurança, sessão e apresentação; remover wrappers e aliases desnecessários e suas entradas de inventário. A retirada dos contratos públicos históricos desses tipos substitui sua preservação nas etapas anteriores. Conservar contratos de transporte ativos, regras, persistência, escopos e testes existentes. O módulo de cobertura de persistência deixa de ser dependência de produção do app; seus limiares permanecem intactos. Reforçar os gates e validar CORE e seus consumidores antes de concluir, sem commit/push.

## Limpeza de compatibilidade de todas as features — 20/10/2026

Solicitação do usuário: aplicar às 14 features a mesma limpeza concluída em APP e CORE. Consolidar os consumidores nos modelos/contratos/DTOs reais e nos casos de uso já implementados; remover os aliases e wrappers públicos redundantes. Retirar fachadas antigas de DI, factory e store, realocando módulos instalados e adaptadores ativos para os donos existentes. Manter os tipos e payloads de transporte ativos, regras, persistência, estado e escopos. A retirada dos contratos de compatibilidade correspondentes substitui sua preservação nas etapas anteriores. Validar com testes de política, fixtures Gradle negativas restauráveis e matriz global `modernCheck releaseCheck`, sem reduzir gates nem criar commit/push.


## Revisão final transversal de APP, CORE e features — 20/10/2026

Pedido explícito: revisar integralmente os donos e eliminar redundâncias/incoerências restantes. A revisão cobre APP, todos os CORE, as 14 features, seus 13 Domain/Data, três APIs, nove Transports, navegação, DI, dependências e documentação atual. Revisores independentes trabalham somente em leitura; a aplicação das correções e Gradle ficam serializados neste checkout, preservado por snapshot anterior à rodada.

Foram aposentadas as dez fachadas APP restantes, overload de navegação e wrappers sem consumidor. A configuração de ambiente passa a ser injetada diretamente por seu dono efetivo; Firebase conserva as quatro opções originais. Helpers visuais e a interface Mapper sem uso foram retirados. O teste de commit durável acompanha EnvironmentPreferences no Datastore. Dependências herdadas, imports sem uso e os três shims de plugins foram limpos; módulos usam diretamente os mesmos plugins, sem troca de versões. Gson/coroutines de produção ficam nos consumidores reais. Guardas redundantes comprovadas pelo compilador foram simplificadas, preservando as guardas necessárias.

As duas declarações de Marketplace Filter usadas por import alias foram restauradas integralmente após a revisão detectar esse vínculo. Nenhum componente ativo é tratado como órfão apenas por contagem de nomes. Gates de compatibilidade agora rejeitam fachadas aposentadas mesmo com arquivo renomeado; inventário APP conserva somente entradas Android. Não mudar payloads, URLs, rotas, persistência, regras de sessão, escopos, geometria nem limiares. Sem commit, push ou publicação. A validação final e seus limites ficam em MIGRATION_VALIDATION.md.
