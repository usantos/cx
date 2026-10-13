# Última notificação da Home — Compose e tratamento de leitura

## Referência e comportamento

Revisão do checkout legado `simlo-app-android-legacy-app-backup-20260903`:

- `PrincipalActivity.java:547–606`: pagamento não identificado tem prioridade; a notificação
  aguarda a Activity estar RESUMED. Fechar dispensa o popup antes de chamar o serviço.
- `NotificacaoUtils.java` e `nova_popup_notificacao.xml`: imagem base64/default, título e
  conteúdo como texto simples, Fechar centralizado quando não há link; Abrir Link não fecha
  o popup nem confirma leitura.
- `DadosUsuarioBO.java:278`: PUT `historico-notificacoes/{idNotificacao}/visualiza`, sem payload,
  autenticado. Sem ID não há chamada nem remoção da notificação da sessão.
- `RedirectNetwork.checkRedirect`: falhas da leitura não reabrem o popup. Redirect tem precedência
  sobre status HTTP; 401 solicita login, termos exigem reconhecimento, 400 diferencia regras
  negociais, 500 distingue CPF/mensagem/falha genérica. Erros de conexão usam a conectividade atual.

`HomeNotificationPopup` está conectado à Home e usa apenas Compose. Mantém margens laterais de
50 dp, altura máxima de 70% da janela, imagem de 200 dp, fundo branco/arredondamento de 10 dp,
borda de 0,2 dp, backdrop `#4D000000`, fonte Futura e cores do XML. Texto longo tem rolagem.
Não aceita dispensa por Voltar/toque externo. Ainda é necessária captura pareada para comprovar
geometria da janela, métricas de texto, botões e comportamento com fonte ampliada.

O ícone `ic_imagem_notificacao` foi convertido para `CaixaDSNotificationPlaceholder`, preservando
os sete paths/cores e viewport de 308 × 308, sem XML de drawable em runtime. Está no catálogo CaixaDS.

## Camadas e proteção da sessão

`HomeViewModel` recebe `ReadLatestNotificationUseCase`, observação da sessão, dispatchers,
relógio local e conectividade por injeção. `ReadLatestNotification` captura a identidade vinculada
à instância da notificação e só limpa a mesma instância após `NotificationReadResult.Confirmed`.
`Rejected(ServiceFailure)` preserva a notificação na sessão, mas o popup continua dispensado na Home.

`SessionTokenStore` implementa `LatestNotificationSession` na mesma instância Hilt de tokens/conteúdo.
A limpeza usa CAS e não sobrescreve carrinho, aviso de pagamento, sessão ou notificação substituídos.
Fechamento concorrente da UI também usa CAS antes de iniciar o PUT. Respostas antigas não publicam
avisos na nova sessão; efeitos de navegação são consumidos pela referência da instância, uma vez.

`RemoteLatestNotificationReadRepository` usa o cliente dedicado `NotificationReadApiProvider`.
O cliente recebe a sessão capturada via tag, compartilha a política de pins e os logs redigidos,
e desativa redirects/retries automáticos com corpo one-shot. A rotação dos pins substitui esse
cliente junto com os outros clientes de backend. Não há fallback para bearer de outra sessão.

O tratamento de `RedirectNetwork` foi extraído de `RemoteRetailRepository` para
`ServiceFailureMapper`, reutilizado por leitura, resgate e token Mercado Pago. Não existe dependência
de Home para retail/payment. `core:presentation` formata os avisos com relógio injetado; o host resolve
`ServiceRedirect` para rotas. Aceitar aviso/retornar da rota não repete o PUT.

## Situações restauradas

- HTTP 401 sem redirect: Sim/Não para login; Sim consulta novamente a conectividade.
- Redirect de login, cadastro, indisponibilidade, carrinho ou carrinhos favoritos: destino tipado.
- Redirect de termos: texto legado e Entendi antes da navegação.
- Redirect reconhecido sem ação no handler legado: mantém Home, sem inventar sucesso de leitura.
- HTTP 400, códigos 011010/011011/011012: Apostar mais/Entendi. Em 404 não usa essa ação especial.
- HTTP 500: CPF (2067), mensagem tipo 1 ou falha genérica; diagnóstico inclui código/data local
  quando previsto pelo handler. Timeout e conexão são distintos, com mensagem de offline quando aplicável.
- Confirmação 200 sem código/redirect/erros ou 204: limpa apenas a notificação capturada.
- Cancelamento de corrotina propaga; payload inválido não confirma leitura.

## Mocks

`post_login_notification` apresenta a notificação comum. Os cenários abaixo também retornam essa
notificação no cadastro e alteram **somente** o endpoint de leitura:

- `notification_read_login_required`
- `notification_read_terms_required`
- `notification_read_cart_redirect`
- `notification_read_business_rule`
- `notification_read_server_message`
- `notification_read_timeout`
- `notification_read_connection_failure`

Eles não fabricam identidade OIDC. A publicação na Home exige sessão autenticada ou fixture de UI.
O teste do repository usa os clientes de cadastro/leitura com esses mocks e verifica uma única tentativa.

## Limites e divergências explícitas

- Imagens são decodificadas fora da thread de UI, com limites de entrada/dimensões e downsampling.
  Imagens inválidas usam o placeholder; o legado podia apresentar imagem vazia para determinados
  conteúdos base64 inválidos. A regra defensiva não equivale a homologação visual de todos os formatos.
- Links aceitam somente HTTP/HTTPS absolutos, sem credenciais embutidas. Falha de abertura apresenta
  aviso e mantém o popup; o legado apenas registrava a exceção. Não executar esquemas arbitrários.
- O código 018003 no handler legado tenta converter a Activity para a tela Mercado Pago, o que não
  é válido na Home. Aqui falha com aviso, sem iniciar cadastro financeiro inesperado. Esse retorno
  no endpoint de leitura precisa de confirmação contratual; não é declarado como paridade total.
- A antiga cadeia `interceptError`/renovação automática de credenciais, respostas atípicas de sucesso,
  morte do processo e reentrada completa ainda precisam de homologação. Não há retry automático novo.
- A lista histórica de notificações tem repository separado e ainda precisa da auditoria integral.
- O pós-login ainda não transfere as apostas locais. Esta entrega não resolve essa lacuna.
- Sem aprovação de cobertura global >95%, todos os flavors/Release ou matriz completa de telas.

## Validação

A compilação/JUnit/JaCoCo isolados desta revisão ficam em
`build/verification/home-notification-errors-20260907/`. Os testes instrumentados compilados não
foram executados no Android. Compilação isolada não valida Hilt/KSP, recursos, D8/R8, APK ou Release.

Execução final concluída com código 0:

- **643 testes JVM aprovados**: 433 de lógica/estado, 64 de network, 114 de data,
  29 de preferences e três de navegação mock.
- Domain de conta/sessão/leitura selecionado: **65/65 linhas**; leitura propriamente dita, 14/14.
- Repositories/mapeador selecionados: **152/152 linhas**, incluindo 16/16 do repository de leitura
  e 33/33 do tratamento de serviço compartilhado. Auth/store/parser selecionados: 153/153.
- HomeViewModel/UiState/efeito de navegação e política de link: **216/218 linhas (99,08%)**.
- DTO/provider de leitura: 9/9. São escopos selecionados, não cobertura global do aplicativo.
- Fontes afetados de Compose/CaixaDS/host/DI compilados isoladamente; **32 testes Android somente
  compilados**, incluindo três de popup/imagem. Nenhum executado em dispositivo nesta rodada.
- Comparação estrutural aprovou os sete pares path/cor do vetor contra o XML legado.
- `git diff --check` aprovado; somente avisos históricos de normalização CRLF nos arquivos Gradle.

`./gradlew :core:domain:test :core:data:test :feature:home:check --offline` falhou antes das tarefas:
`gradle-9.4.1-bin.zip.lck (Operation not permitted)`. Não foi contornada a restrição de escrita.
