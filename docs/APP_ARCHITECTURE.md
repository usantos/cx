# Arquitetura do Loterias CAIXA

## Visão geral

O aplicativo usa MVVM e Clean Architecture por fatias verticais. O composition root `:app` usa Hilt para montar o grafo e entrega casos de uso aos ViewModels. Features não conhecem Retrofit, DTOs, implementações de repository nem outras features.

```text
:app (Hilt, MainActivity host, NavHost, startup/FilaBRA)
 ├─ :feature:* (Screen/Route Compose, ViewModel, UiState, Actions/Effects)
 │   ├─ :core:design-system
 │   ├─ :core:presentation
 │   └─ :core:domain (UseCases e entidades)
 ├─ :core:data (DataSources, repositories e DTO -> domínio)
 │   ├─ :core:domain
 │   └─ :core:network
 ├─ :core:common (Result, erros, dispatchers, mapper)
 ├─ :core:key-management (manifesto assinado, anti-rollback e fallback de pins TLS)
 ├─ :core:auth (OIDC/AppAuth e token de sessão)
 ├─ :core:preferences (persistência Android compatível com as chaves do legado)
 ├─ :core:queue (porta para FilaBRA)
 └─ :core:navigation (contratos de rota)
```

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

`NotificationViewModelFactory` em `:app` recebe um `Provider<NotificationRepository>` não singleton
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
junto com os pins no `AppContainer`, sem fallback. O coordenador agora consome esse caminho
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

O snapshot de apostas locais pertence a Domain (`LegacyLocalCartRepository`), a leitura SQLite
somente leitura fica em `core:preferences`, e o mapeamento do lote para DTO fica em `core:data`.
`core:network` recebe um contrato tipado sem serializar entidades de domínio nem IDs locais.
`LegacyCartTransferRepository` tem provider singleton Hilt e implementação transacional de recibo
e limpeza seletiva em preferences. O coordenador ainda precisa derivar proprietário verificado,
ligar o transporte/retorno e apresentar revisão/retomada; não há envio no pós-login até essa integração.
O protocolo local está em `LEGACY_CART_TRANSFER_RECEIPT_AUDIT.md` e não copia o `deleteAll()` legado.
Ver `LEGACY_CART_TRANSFER_AUDIT.md` para o contrato, preservação do banco e pendências.

`core:auth` mantém um registro transitório das tentativas OAuth e um ticket de publicação no `SessionTokenStore`. O bridge AppAuth cria a requisição por tentativa e usa os parâmetros locais na troca; o store invalida tickets no logout/cancelamento/substituição para que callbacks antigos não restaurem tokens. O contrato exige cancelamento explícito pela apresentação; ver `AUTH_ATTEMPT_LIFECYCLE_AUDIT.md`.

`AuthenticationGateway` e `EndSessionGateway` são portas servidas pela mesma instância AppAuth singleton no Hilt. O ID token é associado atomicamente à sessão aceita e extraído no logout, sem usar access token como hint. `EndSessionRegistry` guarda requisição/state, enquanto `LogoutViewModel` recebe apenas identidade opaca e resultado tipado. O launcher Compose fica fora do NavHost para sobreviver à limpeza da pilha autenticada. Menu, autossuspensão e falha pós-login compartilham esse coordenador. Falha local, cancelamento externo, resultado não confirmado e simulação são estados distintos. A integração não substitui a homologação do retorno SSO, recuperação persistente ou limpeza coordenada dos demais caches; ver `SSO_END_SESSION_AUDIT.md`.

Entre a invalidação em memória e EndSession, `LogoutViewModel` executa `ClearLocalSessionDataUseCase` no dispatcher I/O. Sua porta `LocalSessionDataRepository` é implementada em `core:preferences` para limpar somente a identidade/biometria e flag de login herdadas, preservando schema e dados não relacionados. Falha em qualquer armazenamento bloqueia a continuação com retry explícito, sem repetir autorização/logout local. Os acessos Android permanecem fora de Domain e das telas; ver `LOCAL_SESSION_CLEANUP_AUDIT.md` para a transação, limites de persistência/cancelamento e validação pendente.

O mesmo repository singleton implementa `LocalSessionCleanupRecoveryRepository`. Um marcador de operação em SharedPreferences é confirmado antes da limpeza e desativado somente depois de concluí-la; um mutex serializa as duas portas no processo. `ResumeLocalSessionCleanupUseCase` retorna NONE_PENDING/RECOVERED, sem Android. `SessionRecoveryViewModel` controla checagem, falha, reconhecimento e liberação. `MainActivity` compõe `SessionRecoveryGate` antes de todos os caminhos de entrada, mantendo o Splash durante a checagem e sem iniciar/repetir SSO. O protocolo e seus limites de durabilidade/homologação estão em `SESSION_CLEANUP_RECOVERY_AUDIT.md`.

`AccountAccess.status` preserva a situação de conta recebida no cadastro, com UNKNOWN separado de ausência/ACTIVE. A situação passa pelo resultado de pós-login sem dependência de DTO nas features. O ViewModel mantém o aviso de bloqueio parcial como decisão a reconhecer antes da navegação, sem transformá-lo em falha da consulta ou logout. A revisão não cria uma política global de autorização baseada apenas nesse campo; ver `POST_LOGIN_PARTIAL_BLOCK_AUDIT.md`.

O pós-login possui `VerifyPostLoginAccessUseCase`, que consulta `BlockedAccountVerificationRepository` antes de `PostLoginAccountRepository`, mantendo a identidade capturada. Hilt injeta o transporte dedicado de cadastro e o provider legado do check de bloqueio. A distinção de etapas é tipada no domínio; o ViewModel decide o aviso e o reconhecimento, sem conhecer endpoint/DTO. Carrinho e entrada em apostas mantêm `VerifyAccountAccessUseCase`, sem esse orquestrador de pós-login. A implementação não implica homologação de logout SSO ou de todos os estados da sessão; ver `POST_LOGIN_SESSION_INTEGRATION_AUDIT.md`.

O cadastro Mercado Pago usa uma porta própria `MercadoPagoRegistrationRepository`; a obtenção do token foi removida de `PrizeRedemptionRepository`, evitando impor cadastro a implementações de resgate. A resposta tipada diferencia token disponível de decisão de serviço, e somente o caso de uso transforma um token válido em URL fixa e codificada. `core:domain/service` contém avisos/redirects independentes de HTTP/Android usados pelo handler geral de resgate e token. As regras de compra assíncrona permanecem separadas, pois seu tratamento 400/404/401 difere do handler geral.

`core:presentation` depende de Domain e compartilha `ServiceNoticeUiState`, textos, ações e formatação diagnóstica. Os nomes `PrizeNotice`/`PrizeRedirect` permanecem como aliases de domínio para consumidores de resgate, sem criar dependência de `feature:payment` para `feature:retail`. O coordenador de cadastro recebe `Clock` e casos de uso por injeção; somente o host traduz destinos tipados em navegação. Falha no token não aprova compra/resgate, não forma URL externa e não repete POST. Um novo 018003 cria outro consentimento com identidade própria; o retorno de navegação apenas libera a pausa do fluxo de origem.

Falhas de transporte do token e do POST de resgate carregam `ServiceConnectionFailure`, não exceções da biblioteca ou textos técnicos. O data source/repository mantém HTTP negocial separado de `IOException`; o mapeador identifica timeout com uma busca limitada nas causas. `ServiceConnectionRules`, na apresentação, combina esse resultado com a informação atual de `NetworkAvailability` injetada. A mesma dependência é consultada no consumo do destino de login, sem bloquear outras navegações nem reexecutar transações após reconexão. Ver `PRIZE_CONNECTIVITY_PARITY_AUDIT.md` para a distinção de conectividade desconhecida e os limites ainda pendentes.

No resultado premiado, `PrizeRedemptionViewModel` recebe `GetBettingParametersUseCase` para o resumo do concurso atual e `PrepareBetEntryUseCase` para iniciar outra aposta. A política de entrada fica em `core:domain`: revalida acesso, suspensão, janela de revenda após encerramento individual e disponibilidade e devolve uma decisão tipada. Somente `:app` traduz essa decisão em navegação e mantém a Home abaixo do simulador. `ApplicationModule` fornece o caso de uso e `AppContainer` o entrega às features; o Compose não acessa repositories nem Retrofit.

O CTA de aposta individual do carrossel também usa essa política por `HomeViewModel`. `BetEntryRequest` mantém modalidade, tipo e número do concurso selecionado durante a revalidação; o acesso pelo resultado histórico continua solicitando apenas a modalidade para obter o concurso atual. A Home publica estado de espera, alerta ou decisão consumível uma vez. O host não reposiciona o carrossel quando a origem já é a Home. `BettingSimulationRoute` transporta tipo e número validados até `BettingGameViewModel`, que exige o mesmo concurso na segunda consulta e nas tentativas seguintes. Rotas genéricas sem esse argumento continuam selecionando o concurso atual; a reedição de aposta histórica não exige o número antigo. Bolões, Combo, Instantânea, Rapidão e demais entradas ainda exigem auditoria própria, assim como a homologação visual/interativa.

O simulador preserva o tipo Normal/Especial no `BetDraft` enviado ao repository. Falha na atualização de parâmetros invalida o jogo anterior para envio, e um concurso encerrado não é enviado ao carrinho. A inclusão é uma operação única por vez; o estado de espera é publicado antes da corrotina, e sucesso desabilita novas inclusões até iniciar outra entrada. O payload mantém o contrato existente do backend; o número da rota protege a seleção local, sem inventar um novo campo na API.

O CTA de bolão do carrossel usa `PreparePoolEntryUseCase`, também injetado por Hilt. A verificação de acesso/suspensão é compartilhada com o caso individual por `verifyBettingAccount`, mas o fechamento individual não bloqueia bolões. A escolha do tutorial consulta `OnboardingRepository.isTutorialDismissed(POOL)`, que lê a chave histórica `BOLAO_TUTORIAL`. A decisão tipada carrega modalidade/tipo e a necessidade de tutorial; `:app` mantém esses dados em `PoolTutorialRoute` e restaura o filtro ao concluir. O tutorial é removido da pilha ao abrir o Marketplace; fechar/pular apenas retorna. Ver `POOL_ENTRY_PARITY_AUDIT.md` para escopo e pendências.

Combo, Instantânea e o atalho de Rapidão da Home usam `PrepareHomeBettingActionUseCase`, com a mesma verificação de conta/suspensão. O domínio resolve a disponibilidade e o destino; somente o host abre o navegador ou navega. O Rapidão lê `RAPIDAO_KEY` para escolher configuração/lista, sem gravar a preferência durante a entrada. A trava de decisão é comum aos CTAs de apostas da Home.

`HomeDrawerMenu`, em `:feature:home`, define identidades estáveis, ordem, filhos e visibilidade do menu legado. Recebe `HomeFeatureConfiguration`; `consultaMinhasApostas` é mapeado em `:core:data`, com fallback `true`, sem interpretação de DTO no Compose. `HomeDrawerNavigation`, em `:app`, traduz ações em destinos e contratos de confirmação externos. Expansão e diálogo pendente são estados de apresentação salvos com `rememberSaveable`. Nome vem de `CurrentUserIdentityProvider` injetado; versão vem de `BuildConfig`. `LegacyTestEnvironment` resolve o indicador não produtivo pelo endpoint efetivo. A seleção persistida de ambientes e a homologação visual/interativa continuam pendentes, conforme `ENVIRONMENT_PARITY_AUDIT.md` e `HOME_NAVIGATION_PARITY_AUDIT.md`.

`ServerTimeSource` é a porta temporal em `core:common`. `MonotonicServerClock` ancora a hora em memória e avança pelo tempo decorrido; o Hilt injeta `SystemClock.elapsedRealtime` no host, sem importar Android no domínio. Antes da primeira sincronização usa a hora local, como o legado. Os clientes Retrofit CAIXA recebem um Gson com `ServerTimeAdapterFactory`, que observa `dataHoraServidor` depois da conversão normal do envelope; os clientes externos de pagamento não recebem esse observador. Respostas com timestamps inválidos, iguais ou anteriores à última referência aceita não substituem essa referência. Esse relógio serve às regras de negócio, não à validação criptográfica ou à expiração do manifesto de pins.

A janela de revenda exige `habilitaRevendaBolaoApp`, modalidade, tipo e número de concurso correspondentes, além dos horários de fechamento individual e da revenda. Ambos os limites são inclusivos. `isBolaoHabilitado` era apenas o nome da preferência local legada, preenchida por `ParametrosConfiguraveisDTO.bolaoHabilitado`, cujo `SerializedName` é `habilitaRevendaBolaoApp`; não é outro parâmetro remoto.

| Camada | Responsabilidade | Não conhece |
|---|---|---|
| Common | resultado, erros, dispatchers e utilidades puras | UI e infraestrutura concreta |
| Domain | entidades, casos de uso, contratos e regras puras | Android, Compose, Retrofit |
| Data | data sources, repositories remotos e mapeamento | telas e navegação |
| Network | Retrofit, OkHttp, envelopes e DTOs | regra de apresentação |
| Auth/Queue | adapters de integrações de plataforma/fornecedor | telas de feature |
| Preferences | implementação local de contratos de persistência | navegação e regra visual |
| Presentation | metadados de tela e componentes de apresentação compartilhados | backend concreto e estado específico de feature |
| Feature | Compose, estado e ViewModel | implementação de rede |
| App | Hilt, startup, segurança de rede e navegação | regra visual ou de negócio |

## Módulos

### Core

- `:core:common`: `AppResult`, erros normalizados, dispatchers e contratos utilitários.
- `:core:domain`: conta/cadastro, filtros, resultados, Marketplace, Jogo Responsável, configuração e suporte.
- `:core:data`: interfaces de data source, adapter Retrofit, implementações de repository e mapeadores.
- `:core:network`: Retrofit/OkHttp, HTTPS, bearer token e contratos JSON.
- `:core:key-management`: validação criptográfica e seleção Remote Config/cache/fallback dos pins SPKI.
- `:core:auth`: OIDC Authorization Code com PKCE usando AppAuth.
- `:core:preferences`: preferências locais Android; mantém as chaves do legado atrás de contratos de `:core:domain`.
- `:core:queue`: adapter do FilaBRA e parser testável dos eventos do console.
- `:core:design-system`: CaixaDS, com tokens semânticos, tema claro/escuro, ícones e componentes institucionais Compose.
- `:core:presentation`: metadados imutáveis dos destinos compartilhados; estados, ações, efeitos e ViewModels permanecem no módulo proprietário da funcionalidade.
- `:core:navigation`: destinos estáveis independentes de Android.

### Features

`home`, `onboarding`, `account`, `betting`, `marketplace-filter`, `marketplace`, `cart`, `payment`, `tickets`, `results`, `responsible-gaming`, `notifications`, `retail` e `support` são módulos Android Library separados.

## Fluxos críticos

```text
HomeScreen
  -> HomeViewModel (StateFlow)
  -> GetBettingParameters / GetHomeFeatureConfiguration / GetBetCombos
  -> GetSocialTransfers / GetCurrentCart
  -> BettingRepository / ResponsibleGamingRepository (interfaces de Domain)
  -> Remote repositories + RetrofitLoteriasRemoteDataSource
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
  -> RetrofitLoteriasRemoteDataSource
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
  -> RetrofitLoteriasRemoteDataSource
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
  -> RetrofitLoteriasRemoteDataSource
  -> GET/POST /autoavaliacoes/*, GET /repassometro e GET/POST /usuarios/*
```

```text
LoginScreen / RegistrationScreen / PersonalDataScreen
  -> LoginViewModel / RegistrationViewModel / PersonalDataViewModel
  -> VerifyAccountAccess / RegisterAccount / GetAccountProfile / UpdatePersonalData
  -> AccountRepository (interface de Domain)
  -> RemoteAccountRepository
  -> AccountRemoteDataSource
  -> RetrofitLoteriasRemoteDataSource
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
  -> RetrofitLoteriasRemoteDataSource
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

As dependências apontam para dentro: UI → UseCase → contrato de Repository. O caminho inverso ocorre apenas pela implementação injetada pelo Hilt no composition root. `verifyCleanArchitecture` impede imports de infraestrutura no Domain/features, acesso de ViewModel a Repository e acesso direto de Repository ao Retrofit.

## Injeção de dependências

- `LoteriasApplication` é anotada com `@HiltAndroidApp`;
- `MainActivity` é o único ponto Android anotado com `@AndroidEntryPoint`;
- `ApplicationModule` fornece data sources, repositories, casos de uso, autenticação e dispatchers;
- `AppContainer` recebe o grafo por construtor e coordena dependências de escopo da aplicação e a troca atômica do cliente após atualizar os pins;
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
