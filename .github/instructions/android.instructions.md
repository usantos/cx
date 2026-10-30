---
applyTo: "app/**,core/**,feature/**,sample/**"
---
# Android, arquitetura e interface

Leia [APP_ARCHITECTURE](../../docs/APP_ARCHITECTURE.md), [REDUX_MVVM](../../docs/REDUX_MVVM.md) e [DATA_ACCESS_BY_FEATURE](../../docs/DATA_ACCESS_BY_FEATURE.md). Confirme classes/contratos reais antes de criar novas abstrações.

- Preserve MVVM/Clean/ReduxKotlin: negócio em Domain, adaptadores em Data, estado/efeitos no ViewModel, reducers determinísticos. Compose e navegação não chamam repositories, serviços ou preferências diretamente.
- Stores seguem o ciclo de vida do ViewModel. Cancele trabalhos corretamente, preserve CancellationException e rejeite respostas de sessões/gerações antigas. Uma escrita mantém seu proprietário até concluir; não repetir operações financeiras automaticamente.
- Hilt compõe dependências; não criar service locator. Domain/Data não dependem de Redux. Não criar ViewModel ou UseCase vazio só por convenção.
- Reutilize CaixaDS, recursos, tokens, temas e formatação existentes. Não editar `core/design-system/src/main/java/br/gov/caixa/loterias/core/designsystem/components/CaixaDSScreenComponents.kt` nem `feature/cart/presentation/src/main/java/br/gov/caixa/loterias/feature/cart/presentation/components/CurrentCartScaffold.kt`.
- Novos controles devem expor ID estável com os helpers existentes `automationId`/`uiId`. Preserve IDs antigos via `legacyAutomationId`, TalkBack, rótulos, papel e estado. Não usar CPF/nome/token no ID, nem renumerar os IDs publicados. Em listas, documente o escopo do contêiner.
- Atualize os inventários/seletores Robot quando o contrato mudar; siga [IDS_ANDROID](../../automation/robot-certificacao/docs/IDS_ANDROID.md). O gate estático não prova presença na árvore nativa.
- `MainActivity` é a entrada estável do app; aliases passam pela tarefa temporária `LauncherEntryActivity`. Use a configuração compartilhada `SIMLO App`; não configurar execução pelo alias de campanha `DefaultLauncher`, que pode estar desabilitado.
- Para endpoints, preserve método, headers, parâmetros, DTO, nullabilidade e tratamento negocial. Identidade local não comprova autenticação. Nunca registrar dados sensíveis.
- Teste regras, transições de estado, concorrência e falhas relevantes no módulo alterado. Rode `verifyCleanArchitecture` e `verifyAutomationIds`; confirme o build da variante correta. Não alterar baselines.
