# Cadastro Mercado Pago no resgate — 07/09/2026

## Referência e implementação

Comparados `JogosConfirmadosPremioMercadoPagoActivity.callWebserviceResgatarPremio`/`redirectMp`, `RedirectNetwork.checkRedirect`/`enviaRegistrarMp`, `DialogUtils.dialogSim` e o contrato `POST apostas/{id}/resgatar-premio`. O resgate legado usa o e-mail digitado, não uma nova consulta de perfil. A obtenção do token continua no serviço `GET usuarios/gerar-token-cadastro-mercado-pago`.

O domínio agora diferencia `PrizeRedemptionResult.Redeemed` de `RegistrationRequired`. Assim, o sucesso da consulta ao backend não implica que o prêmio foi resgatado quando o resultado pede cadastro. O estado SUCCESS e o aviso de Teimosinha são exclusivos de `Redeemed`.

O transporte preserva metadados de erro HTTP sem guardar o request, o corpo bruto ou a exceção original. O parser é limitado a 65.536 bytes, fecha o corpo e não engole falha de leitura/cancelamento. `PrizeRedemptionEnvelopeDto` também impede que um corpo com código de erro/redirect em HTTP de sucesso seja interpretado como resgate concluído. Esse bloqueio de sucesso contraditório é uma proteção explícita, não reprodução do callback legado que ignorava esses metadados.

No repository, `018003` sem redirect só vira solicitação de cadastro no canal Mercado Pago. HTTP 401 e 500 não passam por essa conversão, preservando sua prioridade no handler legado. Mensagem vazia/ausente usa o fallback de operação e HTTP 408 preserva `Tempo de resposta esgotou`. Pix nunca recebe um sucesso ou solicitação de cadastro Mercado Pago a partir desse erro.

## Tela e retorno

- O diálogo Compose usa título Atenção, mensagem do backend e Sim/Não. O formulário e o e-mail permanecem na tela; não é mostrado o estado de resgate concluído.
- `Não` apenas dispensa o diálogo. `Sim` consome a decisão por identidade, captura o e-mail daquela tentativa e entrega-o ao coordenador de cadastro já injetado no host. Não consulta o perfil novamente.
- Enquanto há diálogo ou cadastro em andamento, ficam bloqueados novo resgate, mudança de canal, retorno interno, consulta/cancelamento Pix, geração de QR, downloads e recarga do detalhe. O envio também marca loading antes de agendar a corrotina para impedir POSTs duplicados por cliques consecutivos.
- Loading, falha de token e abertura do navegador reutilizam o fluxo documentado em `MERCADO_PAGO_REGISTRATION_AUDIT.md`. O host conecta as features; `retail` não depende diretamente de `payment` nem do Android/browser no domínio.
- O próprio POST de resgate Mercado Pago passou a usar o loading institucional em diálogo de tela cheia não cancelável, conforme `LoadingViewLoterias.show`; a camada visual anterior não bloqueava a interação. O componente tem atraso de 500 ms, fade de 200 ms e rotação de 1.200 ms. A composição/animação ainda exige comparação no APK.
- O retorno ou a dispensa de erro apenas libera o formulário. Não aprova resgate, não baixa comprovante automaticamente e não repete o POST. Nova tentativa exige ação explícita do usuário. A garantia é do fluxo com ViewModels vivos, não de idempotência/reconciliação entre processos.

O destino externo continua fixo em HTTPS e o token é codificado como parâmetro. O contrato envia token na URL; histórico/referrer/validade/uso único externos não foram certificados. Não há afirmação de conformidade OWASP integral.

## Mocks e qualidade

`PRIZE_MP_REGISTRATION_REQUIRED` e `PRIZE_MP_REGISTRATION_TOKEN_FAILURE` iniciam em `retail/prize-mercado-pago`. A falha negocial é específica do resgate com `meioPagamento=1`; Pix e preparação mantêm suas respostas próprias. O segundo cenário falha também ao obter o token. Em mock, o navegador é representado por diálogo local e nenhuma operação externa real é realizada.

Foram acrescentados testes de confirmação negativa/positiva, e-mail preservado, callbacks duplicados/obsoletos, cliques repetidos, pausa, retorno sem reenvio, rejeição de cadastro inesperado no Pix, HTTP 303/400/404/408/422/503, prioridade de 401/500/redirects, envelopes inválidos e sucesso de resgate com códigos MI053/MIO53. A cadeia de integração usa Retrofit real com respostas interceptadas em memória, sem sockets.

A ampliação da medição para todo o `RemoteRetailRepository`, data source e funções auxiliares encontrou inicialmente 155/176 linhas cobertas (88,07%). Foram adicionadas regressões dos mapeamentos das onze modalidades numéricas/esportivas suportadas pelo parser, canal agência e payload de aviso desconhecido; isso não certifica automaticamente modalidades desconhecidas nem todos os estados especiais visuais.

O gate de `:core:data` deixou de excluir classes internas/corrotinas e DTOs, ganhou relatório XML/HTML e falha se faltarem classes ou dados de execução. Exige mais de 95% das linhas do módulo. A aprovação desse gate completo **ainda precisa de execução pelo Gradle**; não é inferida do relatório isolado de um repository. `:feature:retail` inclui também o novo estado de consentimento; a rede inclui parser/metadados/DTOs. O ProGuard preserva explicitamente `BackendErrorMetadata`, lido por reflexão.

## Evidência executada

Artefatos da revisão em `build/verification/prize-mp-registration-20260907/`:

- **411 testes JVM aprovados**: 326 de domínio/estados/rotas/regras, 28 de rede, 50 de data, 4 de preferências e 3 de navegação mock do host.
- Compilação isolada de UI Compose, integração do host e composition root concluída, incluindo o loading modal do POST. Permanece a depreciação preexistente de `LocalClipboardManager` no pagamento.
- ViewModels/estados/regras selecionados de `retail`, incluindo corrotinas e consentimento: **361/370 linhas (97,57%)**. `PrizeRedemptionViewModel.kt`: **223/228** nesse escopo, não incluindo a renderização em `PrizeRedemptionRoute.kt`.
- `RemoteRetailRepository`, `RetrofitRetailRemoteDataSource` e funções auxiliares do arquivo: **176/176 linhas**. Domínio completo compilado de `retail`: **112/112**. Novo parser/metadados/envelope de resgate: **25/25**.
- Relatórios: `feature-retail-coverage.xml`, `retail-repository-coverage.xml`, `retail-domain-coverage.xml`, `prize-transport-coverage.xml`. Os testes de pagamento anteriores também permanecem aprovados; a cobertura selecionada de sua lógica é **456/461 (98,92%)**.

Esses percentuais não representam o app inteiro. `./gradlew :core:domain:test :core:network:test :core:data:test :feature:retail:testDebugUnitTest --offline` voltou a falhar antes das tarefas ao abrir `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`, com `Operation not permitted`. Não houve contorno das restrições. A compilação isolada usa dependências em cache e não executa Hilt/KSP, R8, empacotamento nem callbacks Android. Não há APK ou captura pareada atual desta revisão.

## Pendências

Revisão posterior: `PRIZE_HTTP_ERROR_PARITY_AUDIT.md` acrescenta alertas 400/401/500 e redirects do POST de resgate. As métricas acima são desta revisão original; os demais endpoints e o serviço de token ainda não receberam todos esses handlers.

Atualização seguinte: [Erros da consulta do token](REGISTRATION_TOKEN_ERROR_AUDIT.md) também migrou os handlers HTTP do token, incluindo novos consentimentos e retorno sem repetir resgate. Os limites abaixo registram a revisão original; os demais endpoints e a homologação integral ainda estão pendentes.

- Homologar diálogos/loading, e-mail, navegação, navegador indisponível, retomada/rotação, acessibilidade e cancelamento no APK, com capturas pareadas do legado.
- Completar os outros handlers de erro/redirect de resgate e do serviço de token. A prioridade de 401/500 não significa que seus destinos e mensagens completos já estejam implementados: erros não tratados especificamente ainda usam a falha genérica do resgate.
- Confirmar reconciliação após morte do processo, política de repetição manual de operações inconclusivas e garantias externas do token.
- Validar Hilt/KSP, R8, todos os flavors/Release, matriz integral e cobertura global do aplicativo acima de 95%.
