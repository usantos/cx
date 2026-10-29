# Arquitetura do Loterias CAIXA

Para saber **quais classes chamar para obter dados**, consulte [Acesso aos dados por funcionalidade](DATA_ACCESS_BY_FEATURE.md): identidade e cadastro, apostas confirmadas, carrinho, resultados, configurações e catálogo de contratos por feature.

## Revisão arquitetural de 28/10/2026

A auditoria atual e sua matriz de resultados estão em [FULL_AUDIT_20261028.md](architecture/FULL_AUDIT_20261028.md). O inventário distingue 68 módulos compiláveis dos 17 agrupadores Gradle. As 14 features possuem 46 ViewModels, 45 com estado Redux, e 72 arquivos de reducers; AccountStatus permanece dedicado a efeitos. As seções datadas abaixo são históricas.

Navigation compõe ViewModels e traduz resultados em destinos. Leitura/gravação de preferências de Betting e decisão de entrada após recuperação de startup pertencem a ViewModels de Presentation, com operações Domain, estado Redux, retry e invalidação por geração/lifecycle. A composição não executa repositories nem inicia coroutines de aplicação.

DI constrói implementações: concursos especiais de Marketplace pertencem a Domain; a resolução do município usa uma porta estreita Account Domain e adaptadores Data. Mantêm-se um GET de perfil, a rejeição de município ausente e a revalidação da identidade, sem consulta adicional ao catálogo de municípios.

Uma operação de escrita reivindica seu proprietário antes de agendar I/O. Jobs privados controlam efeitos pendentes; o estado Redux descreve a UI. Conclusões de CEP, recargas de perfil ou consultas de questionário não liberam outro envio nem substituem o resultado da operação ativa. Confirmações de inclusão são consumidas antes da navegação. Cancelamento de Combo preserva a intenção durável, sem reenvio automático.

As regras de preço e duplicidade de favoritos ficam em Betting Domain. Textos são capturados em Actions semânticas antes do dispatch; templates conservam um locale explícito. Reducers não consultam catálogo ou idioma global. A política lexical é complementada por regressões de concorrência, replay e navegação; sua aprovação isolada não prova pureza transitiva ou correção de todos os fluxos.


## Histórico — acessibilidade e auditoria integral de 27/10/2026

A rodada atual mantém MVVM, Clean Architecture e ReduxKotlin nas 14 features. Acessibilidade e matriz de aceitação estão em [ACCESSIBILITY.md](ACCESSIBILITY.md). Os oito desvios confirmados desta revisão e as fronteiras App/Core estão em [FULL_AUDIT_20261027.md](architecture/FULL_AUDIT_20261027.md). As seções de validação das rodadas anteriores abaixo conservam seu escopo histórico; resultados atuais e limites pertencem ao manifesto `docs/technical-guide/verification.json`.

Home passou a possuir resultados/cache/jobs em seu ViewModel/Redux, sem orquestrar ResultsViewModel na View. Preço/elegibilidade e expiração passaram para Domain. Notifications serializa leitura inicial e mutações com releitura. Tickets usa política Pix de Domain. Support rejeita respostas fora da geração ativa. O resumo compartilhado de carrinho usa DestinationSnapshotViewModel de Core Presentation, com reducer tipado e dono fechado ao sair do destino.

A View mantém semântica, plataforma e interação. Campos conservam nome/erro e ações de edição; seleção publica papel/estado; links delegam a política existente; texto acompanha o sistema e WebView adapta zoom mantendo DOM. Headers e baselines continuam protegidos. Não se convertem repositórios, protocolo de SDK ou metadados de navegação em stores artificiais.


Atualização de 27/10/2026: [ReduxKotlin + MVVM nas 14 features](REDUX_MVVM.md), por solicitação do usuário. Stores locais ao ViewModel usam a ponte compartilhada de Core Presentation; reducers puros ficam em presentation/state de cada feature. Efeitos continuam no ViewModel e regras em Domain; Domain/Data não dependem de Redux.

Correção de ARQ-01/ARQ-02: atualização obrigatória pertence a Onboarding Presentation/Domain/Data, criada pela DI da feature. App não possui ViewModel/cliente Remote Config nem dependência de produção direta em Data. A entrega de comprovantes Retail possui coordenador sem plataforma, bytes privados e metadados Redux; URI, launchers e provedor ficam em Route. Escopo e evidências em [architecture/REVIEW_20261027.md](architecture/REVIEW_20261027.md).

Revisão estática de 27/10/2026: [resultado, dois desvios P2 e limites dos gates](architecture/REVIEW_20261027.md). As regras abaixo descrevem o contrato pretendido; gates aprovados não eliminam os desvios documentados.

## Visão geral

O aplicativo usa MVVM e Clean Architecture por feature. APP compõe o grafo Hilt, inicializa a plataforma e hospeda a navegação. Presentation/ViewModels consomem operações de Domain; Data e DI de cada feature ligam seus adaptadores e contratos HTTP. Dependências entre features usam contratos explícitos e formam um grafo acíclico, verificado pelos gates.

```text
:app (Application, MainActivity, NavHost, composição e inicialização)
 ├─ :feature:<nome>:presentation (Compose, ViewModels, navegação e DI)
 │   ├─ :feature:<nome>:domain (JVM: modelos, portas e casos de uso)
 │   ├─ :feature:<nome>:data (repositories, persistência e adaptadores)
 │   ├─ :feature:{account,cart,marketplace}:api (contratos mínimos)
 │   └─ :feature:<nome>:transport (DTOs estáveis, dez domínios HTTP)
 ├─ :core:common / :core:domain (primitivas e contratos compartilhados)
 ├─ :core:data / :core:network / :core:persistence (clientes e persistência compartilhada)
 ├─ :core:auth / :core:key-management / :core:security / :core:queue
 ├─ :core:design-system / :core:presentation / :core:navigation (Android/Compose)
 └─ :core:testing (somente testes)
```

A árvore de 27/10/2026, conforme `settings.gradle`, contém 67 módulos do produto e o aplicativo independente `:sample:design-system` (68 módulos ao todo). As pastas `feature/<nome>` são agrupadores. Todas as 14 features têm `presentation`; Marketplace Filter reutiliza domínio existente e não cria módulos Domain/Data vazios. Persistência Android pertence aos adaptadores Data; ambiente é persistido em `app/environment/persistence/EnvironmentPreferences`. Não existem módulos `core:preferences`, `core:database` ou `core:datastore`.

A fila usa `QueueActivity`, entrada de segurança não exportada e sem launcher, para impedir que Intents externos escolham a página exibida. O host de navegação de produto continua sendo `MainActivity`; Core Queue fornece integração e política da fila. O sample tem seu próprio `CatalogActivity` e depende apenas do DS e de bibliotecas de UI; não inicializa Hilt, autenticação, FilaBRA ou backend. Ele não altera as entradas do aplicativo de produção e participa dos testes, do Lint e da cobertura agregada.


As APIs mínimas de Account, Cart e Marketplace cortam ciclos. A tarefa raiz `verifyPersistenceCoverage` agrega a verificação histórica de persistência, sem módulo Android intermediário. As fachadas e aliases antigos foram retirados; apenas os contratos de transporte ativos e entradas Android inventariadas conservam os nomes históricos. A organização consolidada e os resultados estão em `../ARCHITECTURE.md` e `../MIGRATION_VALIDATION.md`.

## Fronteiras revisadas — 22/10/2026

`AndroidPushRuntime` fica em Notifications `presentation/di`, composição autorizada para executar `RegisterPushDevice` e coordenar login, lifecycle e rotação de token. Data conserva os adaptadores Firebase, notificações e store. A coordenação de Domain captura a identidade antes do token assíncrono e rejeita a sessão substituída. Falha de registro continua elegível para retry no próximo resume.

A Home injeta `SelectHomeResaleOffersUseCase`, que seleciona em Domain somente as ofertas de revenda elegíveis com `ServerTimeSource`. O estado e o mapper recebem `resaleOffers`; o mapper não lê a hora local. Retail mantém a política de país no domínio e o geocoder Android em Data. Account invalida a consulta de CEP ao editar o valor, usando identidade e geração de requisição para impedir publicação tardia de endereço ou erro.

A revisão de cobertura identificou arquivos `build/jacoco/testDebugUnitTest.exec` antigos enquanto AGP 9 gravava a execução atual em `build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec`. Os relatórios agora obtêm, de forma tardia, o `destinationFile` da `JacocoTaskExtension` da tarefa `Test` efetiva, inclusive `app:testPrdDebugUnitTest`, sem unir o caminho Android obsoleto. As coletas instrumentadas `.ec`, produtores, seletores de classes, exclusões e mínimos permanecem iguais. Relatórios históricos que leram dados antigos não comprovam cobertura das classes atuais; a nova matriz precisa confirmar os resultados com os destinos corrigidos.

Os testes e percentuais das seções datadas posteriores são registros históricos. Esta revisão inclui testes focados; não reapresenta as aprovações históricas como uma nova execução Gradle.

## Regras de dependência

O carrinho preserva a projeção plana do backend separadamente dos cards agrupados. Data calcula
o resumo sem duplicar valores; Domain expõe `ShoppingCart.betCount`, e Home observa o snapshot
da sessão ou o retorno de consulta. `CaixaDSCartSummary` recebe somente valor/contagem/callback,
sem dependência de Domain ou Data. É compartilhado pela Home e pela tela do carrinho.
Visibilidade de limpar/favoritar continua em regras da feature. Ver `CART_SUMMARY_PARITY_AUDIT.md`
para contrato, fallback explicitamente limitado e pendências de upload/homologação.
`CaixaDSCartActions` também é puramente visual: o ViewModel coordena os gates e chama o caso
de uso de validação, inclusive para carrinho vazio. Valor mínimo/quantidade não viram autorização
ou bloqueio local antes do serviço. O handler genérico do validador ainda precisa preservar
avisos/redirects tipados; ver `CART_CHECKOUT_ACTION_AUDIT.md` para essa pendência.

`NotificationViewModelFactory` em `feature:notifications/di` recebe um `Provider<NotificationRepository>` não singleton
e entrega os sete casos de uso sobre uma mesma instância a cada destino. A central legada é local:
`LocalNotificationRepository` não tem callback de backend e não compartilha IDs com o popup da Home.
Os switches dessa tela também não representam inscrição remota. Ver `NOTIFICATION_INBOX_PARITY_AUDIT.md`.

`AuthenticatedSessionContentRepository` pertence a Domain e é implementado pelo mesmo
`SessionTokenStore` do Hilt. Conteúdo, token, tentativa e identidade fazem parte de um único
estado imutável; publicação usa CAS, e observação expõe somente conteúdo da sessão ativa.
O repository de pós-login publica o snapshot já convertido. A Home recebe apenas o caso de
uso de observação, não DTOs/credenciais; usa CaixaDS para o aviso prioritário de pagamento.
O popup consome `ReadLatestNotificationUseCase`; `LatestNotificationSession` captura/limpa a
notificação por identidade e CAS no mesmo store. `NotificationReadResult` preserva rejeições
tipadas sem confirmar leitura. O cliente Retrofit dedicado compartilha pins, logs redigidos e a
sessão capturada, sem retries automáticos. `ServiceFailureMapper` em Data e as regras de aviso
em Presentation são compartilhados com resgate/token, sem dependência entre features.
O host resolve os efeitos de navegação, e relógio/conectividade são injetados na Home.
Transferência local e homologação integral permanecem pendentes; ver
`POST_LOGIN_CONTENT_PARITY_AUDIT.md` e `HOME_NOTIFICATION_POPUP_AUDIT.md`.

`RetrofitPostLoginAccountDataSource` separa o futuro envio das apostas locais das consultas comuns
de cadastro. A porta `AccountVerificationSessionBinding` é montada por Hilt com `tokenFor` e
`isCurrent` sobre a mesma identidade capturada. O cliente dedicado em network recebe essa
referência via tag, não conhece auth/SQLite e não confirma/apaga lotes. Seu provider é atualizado
junto com os pins pelo `NetworkClientCoordinator`, sem fallback para outro cliente. O coordenador de pós-login consome esse caminho
por `PostLoginAccountRepository`, capturando a identidade antes do GET de bloqueio e revalidando
após GET/POST. O mapeador de acesso é compartilhado com as consultas comuns, sem duplicar regras.
A lista local ainda não é enviada; ver `ACCOUNT_VERIFICATION_TRANSPORT_AUDIT.md` e
`POST_LOGIN_SESSION_INTEGRATION_AUDIT.md`.

`AuthenticatedSessionIdentityProvider` pertence a Domain e é implementado pelo singleton
`SessionTokenStore` em auth. O gateway valida a identidade do Token Endpoint antes de publicar
token/hash juntos. A referência capturada é invalidada por login/logout/substituição;
`tokenFor` não troca uma credencial por outra sessão. A mesma chave persistente reconhece o
proprietário de um recibo, mas não recria uma referência ativa. Ver
`AUTHENTICATED_SESSION_IDENTITY_AUDIT.md` para o perfil OIDC aceito e limites de validação.

O snapshot de apostas locais pertence a Domain (`LocalCartRepository`), a leitura SQLite
somente leitura fica em `feature:cart:data`, junto ao mapeamento do lote para DTO.
`core:network` recebe um contrato tipado sem serializar entidades de domínio nem IDs locais.
`CartTransferRepository` tem provider singleton Hilt e implementação transacional de recibo
e limpeza seletiva em Cart Data. O coordenador ainda precisa derivar proprietário verificado,
ligar o transporte/retorno e apresentar revisão/retomada; não há envio no pós-login até essa integração.
O protocolo local está em `LEGACY_CART_TRANSFER_RECEIPT_AUDIT.md` e não copia o `deleteAll()` legado.
Ver `LEGACY_CART_TRANSFER_AUDIT.md` para o contrato, preservação do banco e pendências.

`core:auth` mantém um registro transitório das tentativas OAuth e um ticket de publicação no `SessionTokenStore`. O bridge AppAuth cria a requisição por tentativa e usa os parâmetros locais na troca; o store invalida tickets no logout/cancelamento/substituição para que callbacks antigos não restaurem tokens. O contrato exige cancelamento explícito pela apresentação; ver `AUTH_ATTEMPT_LIFECYCLE_AUDIT.md`.

`AuthenticationGateway` e `EndSessionGateway` são portas servidas pela mesma instância AppAuth singleton no Hilt. O ID token é associado atomicamente à sessão aceita e extraído no logout, sem usar access token como hint. `EndSessionRegistry` guarda requisição/state, enquanto `LogoutViewModel` recebe apenas identidade opaca e resultado tipado. O launcher Compose fica fora do NavHost para sobreviver à limpeza da pilha autenticada. Menu, autossuspensão e falha pós-login compartilham esse coordenador. Falha local, cancelamento externo, resultado não confirmado e simulação são estados distintos. A integração não substitui a homologação do retorno SSO, recuperação persistente ou limpeza coordenada dos demais caches; ver `SSO_END_SESSION_AUDIT.md`.

Ao sair do modo anônimo, `AppSessionCoordinator` consulta a identidade atual e retorna ao login limpando a pilha e as continuações de navegação, sem iniciar logout SILCE, limpeza de dados de conta ou EndSession SSO. A ausência de uma sessão autenticada não é uma falha de logout. Com uma identidade presente, o coordenador mantém o fluxo de `LogoutViewModel`, incluindo a confirmação remota e a proteção contra respostas de uma sessão substituída.

Entre a invalidação em memória e EndSession, `LogoutViewModel` executa `ClearLocalSessionDataUseCase` no dispatcher I/O. Sua porta `LocalSessionDataRepository` é implementada em `feature:account:data` para limpar somente a identidade/biometria e flag de login herdadas, preservando schema e dados não relacionados. Falha em qualquer armazenamento bloqueia a continuação com retry explícito, sem repetir autorização/logout local. Os acessos Android permanecem fora de Domain e das telas; ver `LOCAL_SESSION_CLEANUP_AUDIT.md` para a transação, limites de persistência/cancelamento e validação pendente.

O mesmo repository singleton implementa `LocalSessionCleanupRecoveryRepository`. Um marcador de operação em SharedPreferences é confirmado antes da limpeza e desativado somente depois de concluí-la; um mutex serializa as duas portas no processo. `ResumeLocalSessionCleanupUseCase` retorna NONE_PENDING/RECOVERED, sem Android. `SessionRecoveryViewModel` controla checagem, falha, reconhecimento e liberação. `MainActivity` compõe `SessionRecoveryGate` antes de todos os caminhos de entrada, mantendo o Splash durante a checagem e sem iniciar/repetir SSO. O protocolo e seus limites de durabilidade/homologação estão em `SESSION_CLEANUP_RECOVERY_AUDIT.md`.

`AccountAccess.status` preserva a situação de conta recebida no cadastro, com UNKNOWN separado de ausência/ACTIVE. A situação passa pelo resultado de pós-login sem dependência de DTO nas features. O ViewModel mantém o aviso de bloqueio parcial como decisão a reconhecer antes da navegação, sem transformá-lo em falha da consulta ou logout. A revisão não cria uma política global de autorização baseada apenas nesse campo; ver `POST_LOGIN_PARTIAL_BLOCK_AUDIT.md`.

O pós-login possui `VerifyPostLoginAccessUseCase`, que consulta `BlockedAccountVerificationRepository` antes de `PostLoginAccountRepository`, mantendo a identidade capturada. Hilt injeta o transporte dedicado de cadastro e o provider legado do check de bloqueio. A distinção de etapas é tipada no domínio; o ViewModel decide o aviso e o reconhecimento, sem conhecer endpoint/DTO. Carrinho e entrada em apostas mantêm `VerifyAccountAccessUseCase`, sem esse orquestrador de pós-login. A implementação não implica homologação de logout SSO ou de todos os estados da sessão; ver `POST_LOGIN_SESSION_INTEGRATION_AUDIT.md`.

O cadastro Mercado Pago usa uma porta própria `MercadoPagoRegistrationRepository`; a obtenção do token foi removida de `PrizeRedemptionRepository`, evitando impor cadastro a implementações de resgate. A resposta tipada diferencia token disponível de decisão de serviço, e somente o caso de uso transforma um token válido em URL fixa e codificada. `core:domain/service` contém avisos/redirects independentes de HTTP/Android usados pelo handler geral de resgate e token. As regras de compra assíncrona permanecem separadas, pois seu tratamento 400/404/401 difere do handler geral.

`core:presentation` depende de Domain e compartilha `ServiceNoticeUiState`, textos, ações e formatação diagnóstica. Os consumidores de resgate importam diretamente `ServiceNotice` e `ServiceRedirect` de CORE Domain, sem aliases nem dependência de `feature:payment` para `feature:retail`. O coordenador de cadastro recebe `Clock` e casos de uso por injeção; somente o host traduz destinos tipados em navegação. Falha no token não aprova compra/resgate, não forma URL externa e não repete POST. Um novo 018003 cria outro consentimento com identidade própria; o retorno de navegação apenas libera a pausa do fluxo de origem.

Falhas de transporte do token e do POST de resgate carregam `ServiceConnectionFailure`, não exceções da biblioteca ou textos técnicos. O data source/repository mantém HTTP negocial separado de `IOException`; o mapeador identifica timeout com uma busca limitada nas causas. `ServiceConnectionRules`, na apresentação, combina esse resultado com a informação atual de `NetworkAvailability` injetada. A mesma dependência é consultada no consumo do destino de login, sem bloquear outras navegações nem reexecutar transações após reconexão. Ver `PRIZE_CONNECTIVITY_PARITY_AUDIT.md` para a distinção de conectividade desconhecida e os limites ainda pendentes.

No resultado premiado, `PrizeRedemptionViewModel` recebe `GetBettingParametersUseCase` para o resumo do concurso atual e `PrepareBetEntryUseCase` para iniciar outra aposta. A política de entrada fica em `core:domain`: revalida acesso, suspensão, janela de revenda após encerramento individual e disponibilidade e devolve uma decisão tipada. Somente `:app` traduz essa decisão em navegação e mantém a Home abaixo do simulador. os bindings de Betting fornecem o caso de uso às dependências de navegação da feature; o Compose não acessa repositories nem Retrofit.

`PrizeRedemptionRoute` coordena estado, efeitos, seleção inicial, navegação e avisos. As telas de opções, lotérica, agência, Pix, Mercado Pago e sucesso ficam em arquivos próprios e recebem callbacks. `rememberPrizeReceiptManager` mantém a integração Android dos comprovantes: coleta de recibos, seletor de documento, escrita e abertura do PDF. O cache de URIs por nome e o recibo pendente permanecem na memória da composição; cancelar o seletor limpa o pendente, e abrir um comprovante já salvo evita outro download. Não há nova persistência de arquivos ou permissões nesta extração.

`BettingGameRoute` mantém os estados locais, efeitos, bloqueios do shake, diálogos e decisões de navegação do simulador. Os componentes de entrada manual/Surpresinha, trevos, Super Sete, mês de sorte, Lotogol, grade, cabeçalho e rodapé recebem estado e callbacks ligados ao ViewModel atual por `BettingGameActions`. Loteca e Timemania continuam usando os componentes esportivos existentes; a seleção de time entra no conteúdo manual/Surpresinha por um slot Compose. `BettingGameFormatters` reúne os rótulos de recorrência, títulos e data do cabeçalho. O ViewModel e suas regras permanecem inalterados.

`ResponsibleGamingRoute` mantém a coleta de estado e efeitos, o estilo de texto herdado, a escolha entre as quatro rotas e o callback opcional de suspensão. As telas inicial e de formulário da autoavaliação, a suspensão e os repasses sociais ficam em arquivos próprios, recebendo o estado e as ações existentes. Os componentes compartilhados e os blocos/categorias de repasses ficam separados; `ResponsibleGamingFormatters` reúne as funções de ano, moeda e valores sociais. O ViewModel, as regras, as condições de acesso e os corpos dos layouts e formatadores permanecem inalterados.

`MarketplaceFilterScreen` mantém o estado de abertura dos diálogos e as decisões sobre opções, seleção, conflitos e confirmação. `MarketplaceFilterForm` organiza o Scaffold, as seções, os avisos e o rodapé; modalidades, busca de lotéricas, campos numéricos e seletores ficam em componentes próprios, recebendo estado e ações existentes. `MarketplaceNumberDialogs` preserva o rascunho local, as chaves de remember, o bloqueio de números do outro filtro e a confirmação ordenada. O ViewModel, o reducer, os mapeadores, os estados e o comportamento do formulário permanecem inalterados.

`PaymentDataRoute` mantém coleta, redirects/avisos, tratamento de retorno, aprovação e diálogos. `PaymentDataContent` organiza o Scaffold e a prioridade dos estados; métodos, cartões salvos e painel Pix recebem estado e callbacks ligados ao ViewModel atual por `PaymentDataActions`, sem guardar bindings em remember. A expansão dos métodos permanece local à sua apresentação; contador e polling continuam no painel Pix com as mesmas chaves e condições. `PaymentDataFormatters` reúne o título anotado, a moeda e o cálculo de prazo existentes. O ViewModel, as regras e a restauração da cobrança permanecem inalterados.

`RapidConfigurationRoute` mantém coleta, estado do pager, visita ao resumo, efeitos de navegação/salvamento e avisos. `RapidConfigurationContent` organiza o Scaffold e as sete etapas com callbacks ligados ao ViewModel atual por `RapidConfigurationActions`, sem guardar bindings em remember. `RapidConfigurationPager` preserva a sincronização entre indicadores e swipes, com as mesmas chaves e callback atualizado. Introdução/prêmio, valores, seleções e resumo ficam em arquivos próprios, usando componentes compartilhados; o diálogo de valor mantém seu estado local. `RapidConfigurationFormatters` reúne a formatação existente de apostas favoritas; o componente auxiliar de favoritos continua fora das sete etapas. O ViewModel, as regras, os textos e o comportamento permanecem inalterados.

`NotificationsRoute` mantém a coleta do estado, a escolha das duas rotas e o efeito Android de compartilhamento, com as mesmas chaves, MIME, texto e chooser. Mensagens, item expandido, ações de deslizar, preferências, diálogos e mapeadores ficam em arquivos próprios; os componentes recebem estado e callbacks ligados ao ViewModel atual por `NotificationMessageActions` ou parâmetros diretos, sem guardar bindings em remember. O scroll horizontal e seu fechamento antes da expansão continuam locais ao item; os switches e as chaves lazy permanecem iguais. Confirmações, avisos e erros mantêm a ordem e os callbacks originais. O ViewModel, as regras e o gate de cobertura de todas as classes, incluindo os testes Android, permanecem inalterados.

`FavoriteCartsRoute` e `FavoriteCartDetailRoute` mantêm a coleta do estado, o consumo da importação antes da navegação, o retorno após excluir o carrinho e a coordenação dos diálogos; o detalhe conserva seu BackHandler. Lista e detalhes recebem estado e callbacks ligados ao ViewModel atual por `FavoriteCartsActions` ou parâmetros diretos, sem guardar bindings em remember. Itens, cartões de aposta, estados compartilhados e diálogos ficam em arquivos próprios. `FavoriteCartFormatters` reúne os textos existentes, o estilo e a medição do nome, preservando densidade, cache, contagens, Surpresinha, complementos e a diferença de rótulo da Timemania. O ViewModel, as chaves lazy, as animações e o gate de cobertura de todas as classes permanecem inalterados.

`PaymentTerminalRoute` mantém a coleta lifecycle, o coletor de efeitos ligado ao ViewModel e a escolha entre processamento, falha e orientação Pix. As telas ficam em arquivos próprios; a orientação recebe os callbacks atuais de retorno, aceite e conclusão local, sem depender do ViewModel. Confirmação e sucesso continuam compartilhando a apresentação de processamento e os destinos existentes. `PaymentCardComponents` preserva as APIs e a renderização dos componentes usados pelo fluxo de cartão; `PaymentFormatters` reúne o estilo e os spans de ênfase originais. O ViewModel, as regras, as chaves dos efeitos e o comportamento permanecem inalterados.

`CurrentCartRoute` mantém a coleta lifecycle, o retorno, os avisos e decisões de pagamento, o consumo dos estados antes da navegação e a ordem dos diálogos, com o bloqueio de carregamento por último. O coordenador privado `CartContent` permanece no mesmo ramo do Scaffold e conserva o relógio, o refresh e as chaves originais das reservas. Scaffold e conteúdo visual recebem estado e callbacks atuais, sem guardar bindings em remember. Itens compartilhados, resumo, diálogos e formatadores ficam em arquivos próprios; o resumo mantém a contagem de itens e a apresentação ativa dos itens preserva seus algoritmos de layout. O ViewModel, as regras e o gate de cobertura de todas as classes permanecem inalterados.

`ResultsRoute` e `ResultsScreen` preservam as APIs públicas, a coleta lifecycle, a seleção inicial por showDetail e o retorno condicionado à modalidade. Modalidades, pesquisa, detalhes, números dos sorteios, premiações e mapeadores ficam em arquivos próprios. O detalhe conserva a visibilidade da pesquisa, o FocusRequester, o teclado e os efeitos com as chaves originais; campo e botão recebem valores e callbacks atuais. A pesquisa fecha quando o efeito observa carregamento, preservando também a resposta imediata que não expõe essa transição. Limites de concursos, ordem dos estados, estilos e geometria permanecem iguais. O painel de premiações recebe os estilos e espaçamentos já calculados e continua omitindo o resumo da Home no suplemento. O ViewModel, os helpers compartilhados e os gates permanecem inalterados.

`SupportScreen` conserva a API pública, o loading global e a seleção dos modos, com retornos antecipados para termos somente leitura e ajuda por modalidade. `SupportContent` compõe Scaffold, título, aceite e prioridade dos estados; abas, FAQ, respostas, links, ajuda e formatadores ficam em arquivos próprios. `FaqAnswer` conserva contexto, pendência por resposta e cache das anotações com as chaves originais, dentro da pergunta expandida. O builder dos links mantém a validação existente; a confirmação limpa a pendência antes de resolver e abrir o Intent. FAQ textual anotado, termos em WebView e regras por HTML anotado continuam com renderizadores distintos. Larguras das abas, estilos, metadados e exceções visuais por modalidade permanecem iguais. ViewModel, reducer, rota, recursos, dependências e gates não mudam.

`FavoriteBetsRoute` mantém a coleta lifecycle, a abertura do filtro e a ordem dos diálogos de filtro, exclusão, inclusão, sucesso e erro. Conteúdo, lista, filtros, inclusão, seletores e componentes por modalidade ficam em arquivos próprios e recebem callbacks atuais, sem bindings memorizados. A lista recebe o padding original do Scaffold, conserva chaves por id e cabeçalhos por mudança de modalidade entre itens consecutivos. A seleção provisória do filtro continua ligada ao valor selecionado; os estados locais da inclusão permanecem sem chave, inclusive durante troca de candidato ou ViewModel no mesmo ramo. Habilitação inicial, ordem e valores dos concursos e recorrências, espelho, arte de campanha e cache do Canvas são preservados. O ramo de visitante continua substituindo apenas o conteúdo principal. ViewModel, regras, recursos, design system e gates permanecem inalterados.

`MarketplaceRoute` preserva as APIs, a coleta lifecycle, a revisão de retorno dos filhos e o consumo da confirmação antes da navegação, além da ordem dos diálogos e do loading bloqueador por último. Tela, abas, paginação, bolões, lotéricas favoritas, cartões e formatadores ficam em arquivos próprios. O conteúdo de bolões conserva o owner do listState e chama o efeito de paginação incondicionalmente antes de escolher loading, erro, vazio ou lista. As chaves, a captura do estado e o callback atualizado do efeito permanecem iguais. A lista mantém o carrossel, os códigos dos itens, o erro de página adicional com retry e as condições do rodapé. Combinações de retailerView, abas, cabeçalho, toolbar e carrinho permanecem literais. Preços, locale, truncamentos e diferenças atuais entre carrossel e cartão são preservados. ViewModels, contratos, recursos, design system e gates não mudam.

`HomeV2Screen` conserva `HomeV2Content`, os temas e a identidade da apresentação. `HomeV2Presentation` mantém o contrato de ações e a ordem persistida; `HomeV2Composition` reúne os estados e a composição da tela. `HomeV2Reordering` preserva arraste, auto scroll e acessibilidade; `HomeV2Carousel`, `HomeV2Cards`, `HomeV2Navigation` e `HomeV2Loading` concentram as partes visuais. `HomeV2VisualMappers` resolve modalidades, textos, cores e ícones, mantendo as campanhas lidas durante a composição. A ordem personalizada continua restrita à grade, e os callbacks continuam recebendo as ações atualizadas.

`OnboardingRoute` mantém a coleta de estado com lifecycle, os efeitos de navegação e a seleção das telas. `OnboardingIntroduction` reúne as páginas e o pager; `OnboardingPagerSynchronization` observa a página com as mesmas chaves e capturas. `OnboardingTutorial` conserva o checkbox e as ações de iniciar/pular; `OnboardingAge` reúne o diálogo de confirmação e a tela de restrição etária. Os botões ficam em `OnboardingComponents`. O `OnboardingViewModel`, a persistência das preferências, a tipografia e os layouts permanecem inalterados.

`LoginRoute` conserva a coleta de estado, o retorno do lifecycle, a autorização automática e a navegação. `LoginAuthorization` coordena o launcher do navegador e o fallback; `LoginBiometrics` mantém o challenge salvo, o launcher de credenciais e o ciclo de vida do prompt. `LoginScreen` concentra a composição e o efeito das barras do sistema; `LoginDialogs` conserva a prioridade dos avisos, e `LoginComponents` reúne os botões. Os helpers são chamados incondicionalmente, preservando os ciclos de vida de autorização e cancelamento; `LoginViewModel` mantém o estado Redux e os gateways executam os protocolos de autenticação.

`CartAdditionRoute` conserva a coleta de estado, a atualização da reserva no `ON_RESUME`, a seleção por tipo e os handlers de voltar. `CartAdditionPoolScreen` e `CartAdditionBetScreen` separam as confirmações de bolão e aposta/combo; cabeçalho, rodapé e desenhos/botão ficam em `CartAdditionHeader`, `CartAdditionFooter` e `CartAdditionComponents`. `CartAdditionHelp` resolve os mesmos ids de modalidade sem alterar o argumento original, e `CartAdditionFormatters` preserva os textos de modalidade e valores em `BigDecimal`. O `CartAdditionViewModel`, o relógio do servidor, as regras de expiração e os formatadores compartilhados de tempo/data permanecem inalterados.

`MarketplaceDetailRoute` mantém a coleta de estado, o refresh no lifecycle e o consumo da confirmação antes de navegar. `MarketplaceDetailScreen` conserva a lista e o painel fixo; cabeçalho, dados da lotérica, grades, compra e diálogos ficam em arquivos próprios. `MarketplaceDetailComponents` compartilha o estilo de texto, divisores, prêmio e informações do bolão sem adicionar nós de layout. ViewModel, contratos, limites de cotas, formatação, campanhas e ordem dos diálogos permanecem inalterados.

O CTA de aposta individual do carrossel também usa essa política por `HomeViewModel`. `BetEntryRequest` mantém modalidade, tipo e número do concurso selecionado durante a revalidação; o acesso pelo resultado histórico continua solicitando apenas a modalidade para obter o concurso atual. A Home publica estado de espera, alerta ou decisão consumível uma vez. O host não reposiciona o carrossel quando a origem já é a Home. `BettingSimulationRoute` transporta tipo e número validados até `BettingGameViewModel`, que exige o mesmo concurso na segunda consulta e nas tentativas seguintes. Rotas genéricas sem esse argumento continuam selecionando o concurso atual; a reedição de aposta histórica não exige o número antigo. Bolões, Combo, Instantânea, Rapidão e demais entradas ainda exigem auditoria própria, assim como a homologação visual/interativa.

O simulador preserva o tipo Normal/Especial no `BetDraft` enviado ao repository. Falha na atualização de parâmetros invalida o jogo anterior para envio, e um concurso encerrado não é enviado ao carrinho. A inclusão é uma operação única por vez; o estado de espera é publicado antes da corrotina, e sucesso desabilita novas inclusões até iniciar outra entrada. O payload mantém o contrato existente do backend; o número da rota protege a seleção local, sem inventar um novo campo na API.

O CTA de bolão do carrossel usa `PreparePoolEntryUseCase`, também injetado por Hilt. A verificação de acesso/suspensão é compartilhada com o caso individual por `verifyBettingAccount`, mas o fechamento individual não bloqueia bolões. A escolha do tutorial consulta `OnboardingRepository.isTutorialDismissed(POOL)`, que lê a chave histórica `BOLAO_TUTORIAL`. A decisão tipada carrega modalidade/tipo e a necessidade de tutorial; `:app` mantém esses dados em `PoolTutorialRoute` e restaura o filtro ao concluir. O tutorial é removido da pilha ao abrir o Marketplace; fechar/pular apenas retorna. Ver `POOL_ENTRY_PARITY_AUDIT.md` para escopo e pendências.

Combo, Instantânea e o atalho de Rapidão da Home usam `PrepareHomeBettingActionUseCase`, com a mesma verificação de conta/suspensão. O domínio resolve a disponibilidade e o destino; somente o host abre o navegador ou navega. O Rapidão lê `RAPIDAO_KEY` para escolher configuração/lista, sem gravar a preferência durante a entrada. A trava de decisão é comum aos CTAs de apostas da Home.

`HomeRoute` é a entrada da tela, responsável pelos estados salvos, lifecycle, drawer, recuperação, decisões de navegação e prioridade dos avisos. `HomeContent` recebe estado de apresentação e callbacks; os cartões, resultados e atalhos ficam em componentes próprios. `HomeLotteryMapper` converte parâmetros e campanhas para o modelo visual `HomeLottery`, e `HomeCarousel` reúne os cálculos puros do pager. A entrada da apresentação V2 continua em `HomeV2Screen`. Os estados do coordenador permanecem acima do retorno de recuperação; preferências e carregamento específicos da V2 permanecem dentro desse ramo.

`HomeDrawerMenu`, em `:feature:home`, define identidades estáveis, ordem, filhos e visibilidade do menu legado. Recebe `HomeFeatureConfiguration`; `consultaMinhasApostas` é mapeado em `:core:data`, com fallback `true`, sem interpretação de DTO no Compose. `HomeDrawerNavigation`, em `:app`, traduz ações em destinos e contratos de confirmação externos. Expansão e diálogo pendente são estados de apresentação salvos com `rememberSaveable`. Nome vem de `CurrentUserIdentityProvider` injetado; versão vem de `BuildConfig`. `TestEnvironment` resolve o indicador não produtivo pelo endpoint efetivo. A seleção persistida de ambientes e a homologação visual/interativa continuam pendentes, conforme `ENVIRONMENT_PARITY_AUDIT.md` e `HOME_NAVIGATION_PARITY_AUDIT.md`.

`ServerTimeSource` é a porta temporal em `core:common`. `MonotonicServerClock` ancora a hora em memória e avança pelo tempo decorrido; o Hilt injeta `SystemClock.elapsedRealtime` no host, sem importar Android no domínio. Antes da primeira sincronização usa a hora local, como o legado. Os clientes Retrofit CAIXA recebem um Gson com `ServerTimeAdapterFactory`, que observa `dataHoraServidor` depois da conversão normal do envelope; os clientes externos de pagamento não recebem esse observador. Respostas com timestamps inválidos, iguais ou anteriores à última referência aceita não substituem essa referência. Esse relógio serve às regras de negócio, não à validação criptográfica ou à expiração do manifesto de pins.

A janela de revenda exige `habilitaRevendaBolaoApp`, modalidade, tipo e número de concurso correspondentes, além dos horários de fechamento individual e da revenda. Ambos os limites são inclusivos. `isBolaoHabilitado` era apenas o nome da preferência local legada, preenchida por `ParametrosConfiguraveisDTO.bolaoHabilitado`, cujo `SerializedName` é `habilitaRevendaBolaoApp`; não é outro parâmetro remoto.

| Camada | Responsabilidade | Não conhece |
|---|---|---|
| Common | resultado, erros, dispatchers e utilidades puras | UI e infraestrutura concreta |
| Domain | entidades, casos de uso, contratos e regras puras | Android, Compose, Retrofit |
| Data | data sources, repositories locais/remotos e mapeamento | telas, navegação e execução de UseCases |
| Network | Retrofit, OkHttp, envelopes e DTOs | regra de apresentação |
| Auth/Queue | adapters de integrações de plataforma/fornecedor | telas de feature |
| Persistência em Data | implementação local dos contratos da feature | navegação e regra visual |
| Presentation | metadados de tela e componentes de apresentação compartilhados | backend concreto e estado específico de feature |
| Feature | Compose, estado e ViewModel | implementação de rede |
| App | Hilt, startup, segurança de rede e navegação | regra visual ou de negócio |

## Módulos

### Core

- `:core:common`: `AppResult`, erros normalizados, dispatchers e contratos utilitários.
- `:core:domain`: configuração, ambiente, push e contratos de avisos/redirects compartilhados. Regras específicas pertencem aos domínios das features.
- `:core:data`: interfaces de data source, adapter Retrofit, implementações de repository e mapeadores.
- `:core:network`: Retrofit/OkHttp, HTTPS, bearer token e contratos JSON.
- `:core:key-management`: validação criptográfica e seleção Remote Config/cache/fallback dos pins SPKI.
- `:core:auth`: OIDC Authorization Code com PKCE usando AppAuth.
- Persistência local: em `:feature:<nome>:data`, mantendo schemas e chaves; as preferências de ambiente ficam no APP.
- `:core:queue`: adapter do FilaBRA e parser testável dos eventos do console.
- `:core:design-system`: CaixaDS, com tokens semânticos, tema claro/escuro, ícones e componentes institucionais Compose.
- `:core:presentation`: metadados imutáveis dos destinos compartilhados; estados, ações, efeitos e ViewModels permanecem no módulo proprietário da funcionalidade.
- `:core:navigation`: biblioteca Android/Compose para destinos, estado e contexto entre features, dependente de contratos Domain.
- `:core:security`: integrações de segurança/Topaz e inspeção Android.
- `:core:testing`: fakes e utilitários usados somente em testes.

### Features

`home`, `onboarding`, `account`, `betting`, `marketplace-filter`, `marketplace`, `cart`, `payment`, `tickets`, `results`, `responsible-gaming`, `notifications`, `retail` e `support` possuem módulos `presentation` Android Library separados. Domain/API são módulos JVM; Data é Android; Transport mantém DTOs sem clientes ou UI.

## Fluxos críticos

```text
HomeRoute
  -> HomeViewModel (StateFlow)
  -> GetBettingParameters / GetHomeFeatureConfiguration / GetBetCombos
  -> GetSocialTransfers / GetCurrentCart
  -> BettingRepository / ResponsibleGamingRepository (interfaces de Domain)
  -> Remote repositories + domain-owned Retrofit adapters
  -> GET /publico/parametros-simulacao, /parametros/configuraveis-app,
     /logado/combos, /repassometro e /logado/carrinhos
```

O carrossel é infinito e não usa bullets. As modalidades, concurso, estimativa, datas, valor mínimo e disponibilidade vêm dos parâmetros remotos. O card de Combo exige a chave `comboApostasHabilitado` e ao menos uma opção válida em `/logado/combos`; Instantânea preserva o card próprio e abre o portal externo. “Compre seu bolão” exige `habilitaRevendaBolaoApp` e a modalidade selecionada em `modalidadesDisponiveisCotas`; a navegação envia `idModalidade` e `tipoConcurso` ao Marketplace. O somador inferior reflete o carrinho autenticado e permanece oculto quando vazio.

```text
MainActivity
  -> FilaBRA gate
  -> atualização e validação dos pins TLS
  -> validação de versão
  -> preload /parametros/configuraveis-app
  -> ShouldShowIntroduction (preferência local compatível com `INTRODUCAO_KEY`)
  -> NavHost Compose
```

```text
IntroductionScreen
  -> OnboardingViewModel (StateFlow + Effects)
  -> CompleteIntroductionUseCase
  -> OnboardingRepository (interface de Domain)
  -> SharedPreferencesOnboardingRepository
```

```text
MarketplaceFilterScreen
  -> MarketplaceFilterViewModel
  -> GetMarketplaceFilterParametersUseCase
  -> AppConfigurationRepository (interface de Domain)
  -> AppConfigurationRemoteDataSource
  -> RetrofitAppConfigurationRemoteDataSource
  -> GET /parametros/configuraveis-app
  -> quantidadeAposta / quantidadeDezena
```

```text
MarketplaceScreen
  -> MarketplaceViewModel (StateFlow + Actions)
MarketplaceDetailScreen
  -> MarketplaceDetailViewModel (StateFlow + Actions)
  -> SearchMarketplaceUseCase / GetMarketplaceDetailUseCase
  -> AddMarketplacePoolToCartUseCase
  -> MarketplaceRepository / MarketplaceDetailRepository /
     MarketplacePurchaseRepository (interfaces de Domain)
  -> RemoteMarketplaceRepository
  -> MarketplaceRemoteDataSource
  -> RetrofitMarketplaceRemoteDataSource
  -> GET /boloes/recuperar-boloes-disponiveis
  -> GET /boloes/detalhar-bolao?idBolao=...
  -> POST /logado/carrinhos/incluir-aposta
```

A inclusão de cota usa o mesmo corpo `IdentificaoDeUmBolao` do legado. A UI bloqueia nova inclusão durante a chamada e só abre a confirmação quando o carrinho retornado contém a reserva do mesmo bolão com `dataHoraExpiracaoReserva`. O contador da confirmação usa essa expiração do backend no fuso `America/Sao_Paulo`, inclusive no formato legado sem offset.

```text
ResponsibleGamingScreen
  -> ResponsibleGamingViewModel (StateFlow + Actions/Effects)
  -> GetSelfAssessmentQuestions / SubmitSelfAssessment / SuspendAccount / GetSocialTransfers
  -> ResponsibleGamingRepository (interface de Domain)
  -> RemoteResponsibleGamingRepository
  -> ResponsibleGamingRemoteDataSource
  -> RetrofitResponsibleGamingRemoteDataSource
  -> GET/POST /autoavaliacoes/*, GET /repassometro e GET/POST /usuarios/*
```

```text
LoginScreen / RegistrationScreen / PersonalDataScreen
  -> LoginViewModel / RegistrationViewModel / PersonalDataViewModel
  -> VerifyAccountAccess / RegisterAccount / GetAccountProfile / UpdatePersonalData
  -> AccountRepository (interface de Domain)
  -> RemoteAccountRepository
  -> AccountRemoteDataSource
  -> RetrofitAccountRemoteDataSource
  -> POST /logado/usuarios/verificar-cadastro
  -> GET/POST/PUT /usuarios e GET /municipios|bairros
```

```text
CurrentCartScreen
  -> CartViewModel (StateFlow + confirmações de exclusão)
  -> GetCurrentCart / RemoveCartItem / ClearCart
  -> CartRepository (interface de Domain)
  -> RemoteCartRepository
  -> CartRemoteDataSource
  -> RetrofitCartRemoteDataSource
  -> GET /logado/carrinhos
  -> DELETE /logado/carrinhos/apostas|combos/{id}
  -> POST /logado/carrinhos/limpar
```

```text
FavoriteCartsScreen / FavoriteCartDetailScreen
  -> FavoriteCartsViewModel
  -> Get/Delete FavoriteCart, DeleteFavoriteCartBet, AddFavoriteCartToCurrent
  -> FavoriteCartRepository (interface de Domain)
  -> RemoteFavoriteCartRepository
  -> GET/DELETE /logado/carrinhos-favoritos/*
  -> POST /logado/carrinhos/incluir-carrinho-favorito/{id}
```

```text
ConfirmedBetsScreen / PurchasesScreen
  -> TicketsViewModel (StateFlow)
  -> casos de uso de consultas e catálogos de filtro
  -> TicketsRepository (interface de Domain)
  -> RemoteTicketsRepository / RetrofitTicketsRemoteDataSource
  -> GET /apostas/config-consulta, /publico/modalidades e /apostas
  -> GET /compras/meses, /meios-pagamento, /compras/situacoes-compra e /compras
```

`TicketsDataRoutes` mantém as cinco rotas públicas, a coleta do estado, os carregamentos, os avisos e a navegação. `ConfirmedBetsContent`, `PurchasesContent`, `PurchaseDetailContent` e `ManualTicketContent` recebem estado e ações tipadas ligadas ao `TicketsViewModel` atual, sem depender dele. Os filtros de apostas/compras, os diálogos de status/favoritos e seus componentes compartilhados ficam em arquivos próprios. `PurchasePixContent` reúne o painel, o contador e o polling existentes; a paginação permanece no item lazy da lista de compras. A validade e o retorno da consulta manual continuam na rota, inclusive no sucesso sem apostas. O ViewModel, os predicados, as chaves dos efeitos e as regras permanecem inalterados.

As dependências apontam para dentro: UI → UseCase → contrato de Repository. O caminho inverso ocorre apenas pela implementação injetada pelo Hilt no composition root. `verifyCleanArchitecture` impede imports de infraestrutura no Domain/features, acesso de ViewModel a Repository e acesso direto de Repository ao Retrofit.

## Injeção de dependências

- `LoteriasApplication` é anotada com `@HiltAndroidApp`;
- `MainActivity` e `LoteriasMessagingService` são entradas Android anotadas com `@AndroidEntryPoint`; os hosts de testes debug também recebem a integração Hilt;
- `AppInitializationBindings` fornece conectividade, relógio local, dispatchers e estado de sessão em background; módulos Hilt por domínio fornecem os data sources, repositories e casos de uso, preservando seus escopos e usando os tipos efetivos;
- `SessionModule` fornece autenticação, identidade/conteúdo da sessão e limpeza local; `SessionLogoutModule` mantém o transporte de saída da sessão;
- `NetworkApiProvidersModule` em Core Network fornece os providers estáveis; `AppNetworkBindings` fornece relógio do servidor e configuração Android de rede/pins. `BackendClientFactory` cria os clientes; `NetworkClientCoordinator` instala e atualiza as APIs nos mesmos providers, na ordem existente, e invalida os clientes dinâmicos de histórico;
- `AppHostRuntime` resolve o coordenador de rede via `dagger.Lazy` durante a inicialização; dependências estreitas de inicialização/sessão e `AppFeatureGraphDependencies` conectam o host às features. As fachadas inativas `AppContainer` e `ApplicationModule` foram removidas; resolução e instalação são serializadas por `NetworkSecurityPreparation`, sem prometer troca atômica entre todos os providers;
- dependências estáticas são singletons; estado de tela permanece nos ViewModels.

## Segurança

- somente URLs HTTPS são aceitas pelo cliente;
- o `CertificatePinner` usa pins SPKI obtidos de manifesto Remote Config assinado por quórum, cache validado ou fallback local;
- `PinnedHostInterceptor` exige HTTPS e host explicitamente coberto pelos pins nos níveis de aplicação/rede dos cinco clientes Retrofit, inclusive redirects; o fallback atual só cobre o backend produtivo, portanto demais ambientes/provedores ainda exigem provisionamento aprovado antes da homologação;
- o Firebase é apenas canal de distribuição; o backend de negócio não controla a pinagem;
- o repository de pins serializa refreshes, propaga cancelamento e reavalia a validade ao consultar/retornar estado; usa somente a versão autenticada do cache expirado para impedir rollback, sem reautorizar seus pins. Clientes existentes também verificam expiração antes do envio;
- `PrepareNetworkSecurityUseCase` separa o startup da infraestrutura. `NetworkSecurityPreparation`, fornecido pelo composition root, serializa resolução + instalação. `StartupViewModel` o aguarda antes de versão/parâmetros, inclusive em retry; `NetworkSecurityLifecycleObserver` solicita atualização no retorno após STOP sem navegar, com o escopo da Activity. A integração está compilada/testada isoladamente; homologação de sessão longa e OAuth nos APKs permanece pendente;
- manifesto expirado, adulterado, de host não permitido ou em rollback é rejeitado sem bypass de TLS;
- `Authorization` explícito nunca é sobrescrito pelo interceptor;
- logs Retrofit/cURL são opt-in em debug, redigidos e forçados a desligado em release;
- OAuth usa navegador do sistema e PKCE, sem WebView de credenciais;
- o token de acesso fica somente em memória e é limpo no logout;
- nenhuma credencial de repositório privado permanece no Gradle;
- o AAR FilaBRA é fixado por SHA-256 e acessado atrás de uma interface própria.

O modelo de ameaça, formato e runbook operacional estão em [SECURE_PIN_ROTATION.md](SECURE_PIN_ROTATION.md).

## Testes

`modernCheck` agrega lint, testes, APK, cobertura e gates arquiteturais. Os gates de 95% são aplicados a reducers, regras, casos de uso, repositories, mapeadores, parsers e ViewModels. O filtro também possui teste Compose instrumentado para seletores e modais.

O escopo efetivamente medido depende dos filtros de classes em cada módulo. A aprovação de um gate não certifica a cobertura global nem a paridade visual. Em `retail`, o escopo foi ampliado para incluir classes internas de corrotinas, todos os ViewModels e regras; o mínimo é superior a 95% (`0.9501`) e a ausência de dados reprova antes da tarefa JaCoCo. Os demais filtros ainda precisam passar pela mesma auditoria.

O Design System possui testes de invariantes e uma verificação arquitetural que impede cores literais fora de `:core:design-system`. A referência de adoção está em [CAIXA_DS.md](CAIXA_DS.md).

`BetDetailDataRoute` conserva a coleta de estado, o redirecionamento consumido antes da navegação e o retorno antecipado quando os detalhes estão indisponíveis. `BetDetailScreen` reúne a composição pública `BetDetailRoute`, mantendo scaffold, painel fixo e estado restaurável da Teimosinha. `BetDetailReceiptEffect` coleta e abre os comprovantes apenas após a disponibilidade dos detalhes, com o mesmo ViewModel como chave e o IO existente em `BetReceiptFiles`. Editor e confirmação de favoritos ficam em `BetDetailFavorites`; `BetRepeatActionsOverlay` conserva sua coordenação e loading, enquanto `BetDetailDialogs` reúne os diálogos sem alterar a ordem ou os callbacks. ViewModel, regras, APIs públicas, condições e geometria permanecem inalterados.

As três rotas públicas de `BettingCatalogRoute` mantêm a coleta de estado e o efeito de inclusão, com a mesma chave `addedToCart` e sem consumir essa flag. `LotteryModalitiesScreen`, `BetCombosScreen` e `BetComboDetailScreen` recebem estado e callbacks ligados ao ViewModel atual. `BettingCatalogCards` reúne o cartão de combo e o item de modalidade do detalhe; `BettingCatalogComponents` mantém carregamento, erro e contador, e `BettingCatalogColors` consulta as cores de campanha durante a composição. Disponibilidade, linhas ímpares, rodapé fixo, limites, inversão de `surprise`, áreas de toque e APIs públicas permanecem inalterados.


## Revisão de encaminhamentos do APP — 20/10/2026

O inventário atual possui 29 fontes Kotlin em `app/src/main`, além de duas entradas debug. `AppContent` chama `core.queue.android.FilaBraQueueWebView` diretamente; a fachada `FilaBraQueueScreen` foi retirada. As ações de redirecionamento usam `core.navigation.serviceRedirectRoute`, com as mesmas opções de backstack; os dois mapeadores APP de encaminhamento foram retirados. `AppSessionDependencies` conserva sua agregação de Account/background/clock, e os consumidores acessam o foreground/Flow diretamente em `account`.

`AppInitializationBindings`, instalado em SingletonComponent, inclui `AppConfigurationDataModule`; o módulo separado que apenas declarava esse includes foi removido. Providers e escopos não mudaram. As entradas Android, factories de ViewModel, estado de retry/splash, reinicialização de foreground e inicialização única de rede continuam com responsabilidades próprias. Verificações e limites estão na seção de revisão APP de `MIGRATION_VALIDATION.md`.


A matriz completa posterior à limpeza do APP (`modernCheck releaseCheck`) passou em 15min11s, com 2.089 testes unitários, 196 execuções instrumentadas aprovadas, três casos condicionais de perfil de tela e os releases PRD/TQS/Automação gerados com R8. Nenhum limiar foi alterado; 23 configurações de cobertura permaneceram byte a byte iguais. Evidências: `build/verification/app-cleanup-full-matrix-20261020/`.


Os três testes condicionados ao perfil de tela foram posteriormente executados e aprovados, sem skips, em 1080×2408/450dpi/fontScale 1 e barra de status 66 px. A configuração do sistema foi derivada da captura sintética original; fontes, testes e limiares permaneceram inalterados. Perfil e evidências: `docs/SCREEN_PROFILE_VALIDATION.md` e `build/verification/pending-screen-tests-20261020/`.

## Simplificação KISS — 21/10/2026

O estado de conclusão de conta é mantido somente no ViewModel proprietário; a sessão de navegação não espelha loading/erro. A troca de ambiente do host tem uma operação em IO, com reconhecimento da seleção já persistida e ordem cancelamento/limpeza/persistência preservada. Coordenadores leem diretamente o store Redux tipado, mantendo a leitura no mesmo ponto das operações suspensas. Contratos necessários às fronteiras dos módulos permanecem explícitos. Escopo e evidências em [KISS_MODULE_REVIEW.md](KISS_MODULE_REVIEW.md).

## Contrato MVVM e implementações — 21/10/2026

As 14 features de apresentação e seus 41 ViewModels foram conferidos quanto às fronteiras View → ViewModel → UseCase → Repository e, quando necessária, DataSource. Contratos compartilhados evitam camadas vazias. Implementações de Repository/DataSource usam `Impl`, incluindo persistência local e adaptadores de sessão; os bindings Hilt conservam escopos e instâncias compartilhadas.

A validação de cotas expiradas passou de Data para o UseCase de domínio; o Repository coordena binding, fonte e mapeamento. Pós-login recebe o contrato PostLoginAccountDataSource em vez da implementação concreta. Os gates rejeitam atalhos entre camadas e nomes incompatíveis com os papéis. Inventário, detalhes e resultados em [FEATURE_MVVM_CONTRACT.md](FEATURE_MVVM_CONTRACT.md).

## Organização física dos pacotes — 21/10/2026

Os papéis de Domain estão separados em model, repository, usecase, rule e port; Data separa repository, datasource/local, datasource/remote e mapper. Fontes locais foram extraídas de arquivos de Repository, preservando lógica, contratos e persistência. A organização atual e seu inventário estão em [MVVM_PACKAGES.md](MVVM_PACKAGES.md).

## Pacotes do APP/CORE e fronteira Repository — 21/10/2026

APP conserva entradas Android e composição, agrupando ambiente/runtime/tema por responsabilidade. Os demais módulos CORE separam modelos, contratos, casos de uso, rede, autenticação, DS e apresentação nos pacotes correspondentes; bibliotecas pequenas mantêm capacidades coesas. SessionCartRepositoryImpl, SessionBettingRepositoryImpl e SessionBettingParametersRepositoryImpl e seus testes agora pertencem a Data, conforme a fronteira Clean: Domain declara contratos. Regras R8 explícitas acompanham os novos owners sem relaxar flags. Inventário, árvore e validação em [APP_CORE_PACKAGES.md](APP_CORE_PACKAGES.md).

A revisão posterior de ownership moveu os DTOs dos módulos transport para `feature.<feature>.data.dto`, corrigiu os namespaces antigos dos testes e removeu pacotes vazios. Detalhes em [UNUSED_PACKAGES_REVIEW.md](UNUSED_PACKAGES_REVIEW.md).

A validação de 22/10/2026, incluindo os destinos atuais do JaCoCo, resultados e testes condicionais, está em [FEATURE_MVVM_CONTRACT.md](FEATURE_MVVM_CONTRACT.md#validação-das-correções--22102026).

## Verificação automática do contrato - pre-commit de 28/10/2026

O hook nativo e o CI versionado usam `scripts/validate-pre-commit.py` e a tarefa `preCommitCheck`. Os gates de camadas, propriedade de fontes, grafo sem ciclos, App/Feature, Compose e CaixaDS são exigidos junto de Ktlint, Detekt, Lint, testes frescos, cobertura e contratos de plataforma. As fixtures XML operam em cópias temporárias. Divergências entre workspace e index ou falhas mantêm o commit bloqueado.

A última execução completa aprovou 3.510 testes Android/JVM e JaCoCo LINE 95,11%, mas reprovou em Ktlint global; não é aprovação integral. Headers/baselines permanecem intactos. [Novidades e resultados](UPDATES_20261028.md) e [instalação/limites do pre-commit](quality/hooks-and-ci.md) discriminam a evidência e a proteção remota ainda dependente de configuração.

### Reentrada e troca de usuário na tela de login

Uma sessão ativa com identidade autenticada segue diretamente para a Home. Após a perda da sessão em memória, uma credencial biométrica habilitada e disponível inicia a confirmação uma única vez na entrada do app; a renovação e `VerifyPostLoginAccessUseCase` continuam obrigatórios antes de liberar a navegação. Sem credencial disponível, permanece o login normal.

O primeiro nome, obtido do token de acesso depois da validação da autenticação, acompanha a credencial lembrada no armazenamento AES-GCM e é preservado nas rotações de refresh. Não são persistidos tokens de acesso. `BiometricLoginPreferences` expõe somente o nome de apresentação e as capacidades; `LoginViewModel` publica a disponibilidade e o nome no estado Redux. A tela exibe “Entrar como [nome]” e “Trocar usuário” quando aplicável. Credenciais antigas sem nome continuam válidas e usam o texto genérico até o próximo login pelo navegador. Desabilitar a biometria remove a credencial e o nome.

“Trocar usuário” inicia uma nova autorização AppAuth com `prompt=login`, novos state, nonce e PKCE. O login lembrado anterior permanece disponível se a troca for cancelada; uma autorização concluída substitui a credencial conforme a preferência de biometria. A opção não usa o refresh da conta anterior e preserva a verificação de acesso da nova conta.

O login lembrado também é persistido no fluxo OAuth compatível com o legado, quando o endpoint devolve um JWT de acesso com identidade validada e refresh token, mesmo sem `id_token`. O pedido OIDC que exige `openid` continua rejeitando a ausência do token de ID. A gravação depende da escolha explícita de biometria; o contexto da sessão registra esse vínculo para preservar a credencial e o nome nas rotações normais do refresh token. Nenhum token vindo de Intent ou cache é aceito como nova autenticação.
