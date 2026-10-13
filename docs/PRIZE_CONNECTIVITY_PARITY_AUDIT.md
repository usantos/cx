# Resgate e cadastro — conexão e abertura de login

## Referência

`RedirectNetwork.checkRedirect` diferencia falha sem resposta HTTP de erro negocial. Primeiro verifica `AppUtils.isNetworkAvailable`; quando há conexão, `MensagensNetwork.setMensagem` distingue timeout de falha de conexão/SSL. `connectKeycloak` consulta a conexão novamente antes de iniciar login.

Os textos abaixo foram conferidos nos recursos compilados do APK legado `app-prd-release.apk` com `aapt2 dump resources`:

- `seminternet`: “Parece que sua internet está com alguma instabilidade. Verifique sua conexão e tente novamente.”
- `erro_408`: “Tempo de resposta esgotou”.
- `connectionServerErro`: “Falha na tentativa de conexão com o servidor”.

## Correção

O `RemoteRetailRepository` agora preserva falhas de transporte do POST de resgate (Pix/Mercado Pago) e do GET do token Mercado Pago como decisões tipadas. Timeout direto ou encapsulado continua distinto de falha de conexão. Exceções SSL/pinagem não são classificadas como falta de internet. A causa técnica, URLs e credenciais não atravessam a fronteira para a apresentação. Cancelamento de corrotina continua sendo propagado, sem alerta ou nova chamada.

O mapeador percorre causas por identidade, com limite de 32 elementos, para não travar diante de ciclos ou cadeias anormais. A classificação padrão de `IOException` é conexão; não existe interpretação de texto de exceção.

`NetworkAvailability` é injetado nos ViewModels de resgate e cadastro. Ao receber uma falha de transporte, a apresentação consulta a conexão atual: desconectado usa o texto de internet; conectado ou desconhecido mantém a distinção timeout/conexão. O aviso é um diálogo CaixaDS com Entendi, sem código/data inventados. O loading termina, mas canal, formulário e contexto da operação são preservados; dispensar o aviso não repete chamadas.

Ao consumir um redirect para login ou o Sim do alerta 401, ambos os ViewModels consultam a conexão naquele momento. Se ela foi perdida, substituem a decisão pelo aviso de internet e não navegam. A confirmação antiga fica inválida. Outros destinos não fazem essa checagem. Restabelecer a conexão não inicia login, consulta token ou repete POST automaticamente: é necessária nova ação explícita.

`ServiceConnectionRules` compartilha mensagens e política na apresentação, usando interfaces de `core:common` e tipos de Domain. Não importa Android ou Retrofit. O host fornece a mesma dependência de conectividade já usada pelo startup. O gate dos avisos compartilhados também inclui as novas regras.

## Mocks e regressão

`PRIZE_RESPONSE_TIMEOUT` e `PRIZE_CONNECTION_FAILURE` mantêm a preparação e falham somente no resgate. `MP_TOKEN_RESPONSE_TIMEOUT` e `MP_TOKEN_CONNECTION_FAILURE` primeiro exigem cadastro Mercado Pago e só falham no token; Pix mantém seu fluxo. Todos iniciam em `retail/prize-mercado-pago` e não realizam conexões externas.

A matriz das 106 operações foi separada em `MockBackendFixturesTest`, independente do teste com servidor/socket de `MockBackendInterceptorTest`. Cada cenário continua percorrendo as operações: exige JSON válido ou a falha de transporte explicitamente esperada naquele endpoint. Os novos cenários não foram simplesmente excluídos da matriz. O teste de delegação com MockWebServer permanece no projeto e não é considerado aprovado por essa execução isolada.

O teste de proteção de hosts foi ampliado para as sete fábricas, incluindo compra e resgate. O gate de rede agora exige entradas de classes/execução não vazias e cobertura maior que 95%; a execução integral via Gradle continua pendente.

## Evidência executada

Artefatos em `build/verification/prize-connectivity-parity-20260907/`:

- **468 testes JVM aprovados**: 350 de domínio/estados/rotas/regras, 50 de rede, 61 de data, 4 de preferências e 3 do host. A suíte isolada foi ampliada com a matriz de fixtures e testes já existentes de fábrica, providers e pinagem; esse total não representa todos os testes do aplicativo.
- Compilação isolada de **todos os fontes de `core:network`**, além dos fontes selecionados de domínio/data, Compose, host e composition root concluída. Permanece a depreciação preexistente de `LocalClipboardManager` no pagamento.
- Lógica selecionada de `retail`: **391/400 (97,75%)**; pagamento: **511/516 (99,03%)**, incluindo **100/100** no arquivo do coordenador/estado/consentimento de cadastro.
- Avisos compartilhados: **37/37**, incluindo as novas regras de conexão **8/8**. Repository/data source de resgate/cadastro e mapeador: **234/234** (arquivo do repository **228/228**, mapeador **6/6**).
- Domínio de `retail`: **115/115**; decisões de serviço: **9/9**; domínio de pagamento selecionado: **78/78**.
- Política de clientes: **156/156** (`NetworkClientFactory` **132/132**, `PinnedHostInterceptor` **13/13**, interceptor de corpo não reutilizável **11/11**). Isso não mede todos os DTOs/classes de rede nem comprova handshakes reais.
- Transporte selecionado de resgate: **40/40**; compra: **57/57**; repository/data source de pagamento: **236/239 (98,74%)**.
- Relatórios: `feature-retail-coverage.xml`, `feature-payment-coverage.xml`, `core-presentation-coverage.xml`, `retail-repository-coverage.xml`, `retail-domain-coverage.xml`, `service-domain-coverage.xml`, `core-domain-payment-coverage.xml`, `network-client-policy-coverage.xml` e os relatórios de transporte/pagamento. São escopos parciais, **não cobertura global**.

## Limites de homologação

- `UNKNOWN` significa que o dispositivo não forneceu uma informação confiável, não que esteja offline. Nessa situação o login não é bloqueado por uma afirmação falsa de falta de internet; a autorização ainda pode falhar no navegador. É uma distinção explícita do modelo moderno de conectividade.
- A revisão cobre transporte do POST de resgate, GET do token e checagem antes dos destinos de login desses coordenadores. Não conclui todos os erros de detalhes, QR, consulta/cancelamento Pix, comprovantes, falhas de parsing ou perfil. Suas regras próprias ainda precisam de auditoria.
- Não houve desativação de pinagem, inclusão de hosts/pins, reenvio automático de transações ou criação de Activity/Fragment/XML.
- Testes com exceções simuladas não comprovam TLS, troca real de rede, callbacks Android, retorno OAuth contextual, reconciliação após morte do processo ou igualdade visual.
- `./gradlew :core:data:test :core:presentation:testDebugUnitTest :feature:retail:testDebugUnitTest :feature:payment:testDebugUnitTest --offline` continua falhando antes das tarefas por `Operation not permitted` no lock do wrapper em `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`. A restrição não foi contornada.
- O objetivo integral permanece aberto: todos os flavors/Release, matriz visual completa e cobertura global maior que 95% ainda não estão comprovados.
