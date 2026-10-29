# Arquitetura

## Contrato vigente — auditoria de 28/10/2026

A arquitetura mantém MVVM, Clean Architecture e ReduxKotlin nas 14 features. O inventário atual distingue **68 módulos compiláveis e 17 agrupadores**, **46 ViewModels de features**, **45 bindings Redux** e **72 arquivos de reducers**. Support delega o binding ao store proprietário; AccountStatus coordena somente efeitos. O [relatório completo](docs/architecture/FULL_AUDIT_20261028.md) registra os desvios corrigidos, evidências e limites. As contagens e aprovações das seções datadas abaixo descrevem suas respectivas rodadas históricas.

Preferências de navegação Betting e recuperação Onboarding pertencem a ViewModels com estado Redux, operações Domain e efeitos com lifecycle. DI somente constrói; Data implementa portas. Marketplace consome a porta de município de Account Domain por um adaptador Data, preservando um único GET de perfil e a validação da identidade. Regras de preços e equivalência de favoritos pertencem a Betting Domain.

Actions capturam mensagens antes do dispatch; reducers não consultam catálogo nem locale global. Quando os argumentos dependem do estado, um template imutável carrega o padrão e o locale explícito. Claims/Jobs privados protegem operações antes do agendamento no dispatcher, mesmo quando outro carregamento altera a flag visual. Resultados de navegação são consumidos antes do callback ao host. Esses proprietários operacionais não duplicam o UiState Redux.

Os verificadores complementam a revisão de fontes/consumidores e os testes de concorrência, replay e navegação. O Ktlint global mantém dívida anterior em arquivos fora do escopo e em header protegido; seu resultado é distinto da formatação dos arquivos alterados. Headers, baselines e critérios JaCoCo permanecem preservados.


## Histórico — acessibilidade e auditoria integral de 27/10/2026

A rodada atual mantém MVVM, Clean Architecture e ReduxKotlin nas 14 features. Acessibilidade e matriz de aceitação estão em [ACCESSIBILITY.md](docs/ACCESSIBILITY.md). Os oito desvios confirmados desta revisão e as fronteiras App/Core estão em [FULL_AUDIT_20261027.md](docs/architecture/FULL_AUDIT_20261027.md). As seções de validação das rodadas anteriores abaixo conservam seu escopo histórico; resultados atuais e limites pertencem ao manifesto `docs/technical-guide/verification.json`.

Home passou a possuir resultados/cache/jobs em seu ViewModel/Redux, sem orquestrar ResultsViewModel na View. Preço/elegibilidade e expiração passaram para Domain. Notifications serializa leitura inicial e mutações com releitura. Tickets usa política Pix de Domain. Support rejeita respostas fora da geração ativa. O resumo compartilhado de carrinho usa DestinationSnapshotViewModel de Core Presentation, com reducer tipado e dono fechado ao sair do destino.

A View mantém semântica, plataforma e interação. Campos conservam nome/erro e ações de edição; seleção publica papel/estado; links delegam a política existente; texto acompanha o sistema e WebView adapta zoom mantendo DOM. Headers e baselines continuam protegidos. Não se convertem repositórios, protocolo de SDK ou metadados de navegação em stores artificiais.


Atualização de 27/10/2026: [ReduxKotlin + MVVM nas 14 features](docs/REDUX_MVVM.md), por solicitação do usuário. Stores locais ao ViewModel usam a ponte compartilhada de Core Presentation; reducers puros ficam em presentation/state de cada feature. Efeitos continuam no ViewModel e regras em Domain; Domain/Data não dependem de Redux.

Correção de ARQ-01/ARQ-02: atualização obrigatória pertence a Onboarding Presentation/Domain/Data, criada pela DI da feature. App não possui ViewModel/cliente Remote Config nem dependência de produção direta em Data. A entrega de comprovantes Retail possui coordenador sem plataforma, bytes privados e metadados Redux; URI, launchers e provedor ficam em Route. Escopo e evidências em [docs/architecture/REVIEW_20261027.md](docs/architecture/REVIEW_20261027.md).

As seções datadas abaixo registram refatorações anteriores. Referências históricas a construtores e colaboradores descrevem aquela etapa; o contrato vigente de estado Redux, os donos e a validação atual estão em REDUX_MVVM.md.
Atualização: 22/10/2026. A organização segue APP → features → CORE, com MVVM por feature, Domain independente e Data separado. Cada uma das 14 features possui um módulo `presentation` próprio, no mesmo nível de Data/Domain/API/Transport quando presentes. As pastas-pai são somente agrupadores. Os módulos declaram seus plugins diretamente nos respectivos `build.gradle`; a camada `build-logic` foi retirada. O histórico das extrações permanece nas seções seguintes e em `MIGRATION_*.md`.

## Módulos e direção das dependências

| Fronteira | Responsabilidade |
|---|---|
| `app` | Application/MainActivity, navegação raiz, inicialização e composição das dependências; sem implementar regras, persistência ou clientes |
| `feature/<nome>/presentation` | `presentation/{screen,route,components,viewmodel,state,...}`, `navigation` e `di` da feature |
| `feature/<nome>/domain` | JVM puro: modelos e portas efetivos em `domain`, com implementações de casos de uso em `domain/usecase` e políticas nos seus donos |
| `feature/{account,cart,marketplace}/api` | Contratos mínimos independentes para identidade, valores de carrinho e filtros; cortam os ciclos sem depender das implementações |
| `feature/<nome>/data` | Adaptadores locais/remotos, repositories concretos e mapeadores em `data/{local,remote,repository,mapper}` |
| `feature/<nome>/transport` | DTOs estáveis de nove domínios; apenas Gson/JDK e outros contratos de transporte, sem clientes, Domain ou UI |
| `core:common`, `core:domain` | Primitivas e contratos/configuração realmente compartilhados; sem Android, Compose, Retrofit ou Data |
| `core:network`, `core:auth`, `core:key-management`, `core:queue`, `core:security` | Clientes/providers, sessão, TLS/pinning, fila e integrações Android/Topaz/Firebase/inspeção; configurações e callbacks chegam pela composição |
| Persistência Android | Cada adaptador Data acessa SQLite/SharedPreferences diretamente e conserva esquema, chaves, modo, transações, política de corrupção e `commit`/`apply`. A persistência de ambiente pertence ao APP |
| `core:design-system`, `core:presentation`, `core:navigation` | Identidade visual, componentes compartilhados e contratos/estado de navegação entre features |
| `core:testing` | Fakes e regra de Main dispatcher, exclusivamente em dependências de teste |
| `verifyPersistenceCoverage` (tarefa raiz) | Agrega a cobertura de persistência das suítes reais de Data, com limiar de 95,01%; não exige um módulo Android vazio |

Cada módulo compila somente seus próprios `src/main`. Não há `featureLayerRoots`, inclusão de fontes externos nem compilação agregada de Domain/Data/Home. A agregação de relatórios de cobertura usa classes e execuções de seus donos reais e conserva os mínimos anteriores.

O corte `CartAPI` contém somente valores de carrinho; `AccountAPI` contém identidade e sua porta; `MarketplaceAPI` contém os filtros usados pela configuração global. Account/Betting/Cart Domain e seus adaptadores formam um grafo acíclico. `core:domain` mantém dependências apenas nos pequenos contratos AccountAPI/MarketplaceAPI necessários às interfaces públicas existentes. Não depende de implementação de feature. `core:navigation` é Android/Compose e depende de contratos Domain para preservar o contexto entre destinos.

## Revisão das fronteiras — 22/10/2026

O grafo vigente declara 67 módulos, incluindo o sample independente e `core:persistence`. A árvore vigente é a declarada em `settings.gradle`: as 14 features têm `presentation`; 13 têm `domain` e `data`; Account, Cart e Marketplace têm `api`; nove features têm `transport`. Marketplace Filter reutiliza os contratos existentes. `core:persistence` fornece DataStore, serializer cifrado e Keystore; schemas, migrações e regras de dados permanecem no Data proprietário de cada feature. As preferências de ambiente pertencem ao APP. Não há `core:preferences`, `core:database` ou módulo chamado `core:datastore`. `core:navigation` é uma biblioteca Android/Compose, com contratos e estado entre destinos, e depende dos domínios necessários.

O runtime `AndroidPushRuntime` pertence a `feature/notifications/presentation/.../di`: ali são compostos `RegisterPushDevice`, a coordenação de identidade, o Flow de sessão e os retries em login/resume/rotação de token. Notifications Data fornece token Firebase, mensagens/canal e armazenamento, sem importar ou executar UseCases. A captura da identidade antes da busca assíncrona do token e sua revalidação continuam no coordenador de Domain. Os gates reconhecem também casos de uso em pacotes aninhados, como `core.domain.push.usecase.RegisterPushDevice`, independentemente do sufixo da classe.

Na Home, `SelectHomeResaleOffersUseCase` seleciona em Domain somente as ofertas de revenda elegíveis usando `ServerTimeSource`. O resultado chega ao estado e ao mapper como `resaleOffers`; o mapper não consulta o relógio local. A composição fornece o mesmo relógio sincronizado aos contadores e ao agendamento de atualização após o fechamento inclusivo da revenda. Em Retail, a política de país pertence a Domain e o geocoder Android fornece os fatos por Data. No cadastro Account, alterações do CEP invalidam a consulta anterior; publicação de endereço/erro exige que a requisição ainda corresponda ao CEP e à geração atuais.

A revisão de cobertura identificou arquivos `build/jacoco/testDebugUnitTest.exec` antigos enquanto AGP 9 gravava a execução atual em `build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec`. Os relatórios agora obtêm, de forma tardia, o `destinationFile` da `JacocoTaskExtension` da tarefa `Test` efetiva, inclusive `app:testPrdDebugUnitTest`, sem unir o caminho Android obsoleto. As coletas instrumentadas `.ec`, produtores, seletores de classes, exclusões e mínimos permanecem iguais. Relatórios históricos que leram dados antigos não comprovam cobertura das classes atuais; a nova matriz precisa confirmar os resultados com os destinos corrigidos.

As seções datadas abaixo registram etapas anteriores e seus resultados; nomes e topologias antigos nesses registros não descrevem a árvore vigente. Esta revisão acrescenta testes focados e regressões do gate. As matrizes e percentuais históricos não são resultados desta revisão; a execução atual, seus resultados e os testes condicionais estão registrados em [FEATURE_MVVM_CONTRACT.md](docs/FEATURE_MVVM_CONTRACT.md#validação-das-correções--22102026).

## Compatibilidade pública e transporte

Os contratos públicos inventariados são preservados, exceto as superfícies de compatibilidade explicitamente aposentadas nas limpezas de APP, CORE e features em 20/10/2026, descritas abaixo. Os consumidores das features importam os modelos, portas e DTOs efetivos, e os casos de uso existentes em `domain.usecase`. Os aliases públicos e wrappers que apenas encaminhavam chamadas foram retirados. Os modelos/DTOs ativos mantêm suas classes e payloads Gson; a retirada das fachadas altera intencionalmente seus contratos antigos de Kotlin/JVM.

Os DTOs pertencem aos módulos `feature/*/transport`, no namespace `br.gov.caixa.loterias.feature.<feature>.data.dto`. A exceção que permitia `core.network` nas features e seu inventário de compatibilidade foram removidos em 21/10/2026. Os contratos de acesso remoto ficam nos pacotes `data.datasource.remote` das features. Core Network consome os DTOs de transporte, sem depender das implementações de Data. Projeções internas encaminham para o cliente já selecionado pelo provider, antes da avaliação dos argumentos, preservando rotação de clientes/pins, sessão, headers e defaults. Os contratos Retrofit públicos antigos continuam disponíveis e conservam suas declarações, inclusive argumentos padrão. Não foi criado endpoint novo.

Cada feature fornece sua própria implementação `*RemoteDataSourceImpl` e faz o binding de sua porta na DI da própria feature. A configuração compartilhada usa `AppConfigurationRemoteDataSourceImpl` em `core:data`, ligada pela DI correspondente. Não há adapter agregado cruzando os domínios; os repositories recebem somente as portas que consomem. As implementações de Rapid/FavoriteBet também pertencem a Betting Data, com mapeadores próprios e implementações consumidas diretamente.

## Hilt, navegação e estado

As features possuem seus bindings em `di`, com um único provider por contrato. As fachadas de DI das features no pacote antigo `app.di` foram retiradas; os módulos que já eram instalados foram realocados para a DI da própria feature. Escopos singleton e bindings sem escopo foram conservados. A central de notificações continua criando uma instância local por destino, conforme o legado expressamente autorizado; não há API de histórico inventada. A implementação de cadastro Mercado Pago pertencente a Retail é vinculada em Retail; Payment consome a porta Domain.

Os grafos estão em `feature/*/presentation/src/main/java/br/gov/caixa/loterias/feature/<nome>/navigation` e recebem interfaces pequenas de dependências. Adaptadores de navegação nas próprias features implementam essas interfaces; `AppFeatureGraphDependencies` reúne as 13 dependências na composição raiz. Slots do app conectam permissões/seleção de ambiente/startup pertencentes às features com sua configuração Android. O NavHost raiz e o consumo dos resultados pelo `AppSessionCoordinator` permanecem no app; a verificação de cadastro/estado é da AccountCompletionViewModel na feature Account. `SessionNavigationActions` expõe somente estado e callbacks necessários aos grafos; o LogoutViewModel continua no seu dono original.

`AppNavigationState`, `AppSessionState`, contexto/codec de detalhes e políticas de continuação estão em `core:navigation`. Foram preservados os bytes do codec, rotas/argumentos, chaves de ViewModel/rememberSaveable, owners e ordem das ações. As 43 declarações atuais incluem 42 ViewModels com estado Redux local e AccountStatusViewModel somente com efeitos. A observação pública continua StateFlow. Colaboradores usam os mesmos escopos e callbacks, sem coroutines soltas.

Route coordena ViewModel/lifecycle/permissões; Screen e componentes recebem estado e callbacks. Não se criam reducers, APIs, modelos ou módulos vazios apenas para preencher o desenho. MarketplaceFilter compõe contratos existentes; Home compõe casos de uso de outras features e mantém sua persistência de layout própria. A antiga entrada pública de HomeScreen permanece disponível e delega à composição na própria feature.

## Convenções e verificação

Os módulos Android e JVM aplicam diretamente os plugins necessários em seus próprios `build.gradle`, usando o catálogo existente. A DSL Groovy, versões de toolchain, namespaces, variantes e dependências de execução foram preservados na separação de Presentation. Os arquivos `src/main`, `src/test`, `src/androidTest`, recursos e manifestos pertencem ao novo módulo; não há redirecionamento de fontes para outro dono.

`verifyFeatureModules` exporta o grafo configurado real, incluindo dependências introduzidas por convenções e variantes, em `build/reports/architecture/feature-modules.json`. Rejeita ciclos, dependência de produção no app, dependência externa de Domain, raízes externas e donos sobrepostos. `verifyCleanArchitecture` verifica também os novos módulos Domain/API/Data/Transport e restringe infraestrutura à integração DI/Data. Java, controllers de feature, cores fora do CaixaDS e layouts XML continuam bloqueados. Cópias exatas de desenhos anteriores são registradas individualmente no inventário de recursos, sem exceção geral.

`scripts/test-feature-module-gates.py` injeta dez violações reais, executa os gates e restaura os arquivos byte a byte mesmo em falha. Os mínimos de cobertura anteriores são preservados; novos adaptadores e os wrappers comuns de persistência entram nas fronteiras correspondentes. O padrão amplo HomeDrawer foi substituído pelas três famílias originalmente cobertas, conservando todas as classes/filhos e o mesmo mínimo; a prova do conjunto anterior está nas evidências. No carrinho, `**/presentation/**/*.class` conserva as 61 famílias anteriores e seus filhos/lambdas/Compose; as classes de DI/navegação movidas do app não ampliam por acidente a fronteira histórica de presentation. O mínimo de 95,01% e todos os produtores/assertivas foram mantidos; a prova compara as 51 fontes anteriores e os contadores do relatório.

Evidências da rodada anterior de módulos: `build/verification/feature-modules-20261019`. Incluem snapshot, equivalência das 1.027 superfícies públicas, 392 declarações Domain, 37 ViewModels, contratos HTTP/rotação, grafo real, relatórios e capturas. A validação global local desta rodada foi concluída: `modernCheck` e `releaseCheck` dos três flavors aprovados, com os mínimos anteriores. Dos cinco visuais condicionais executados, três passaram e duas divergências físicas anteriores permanecem; resultados e limites físicos/privados estão em `MIGRATION_VALIDATION.md`. Não se declara homologação integral da SPEC apenas com compilações ou mocks.

## APP → features → CORE — estrutura atual

A composição segue o diagrama solicitado. `app` contém os entry points Android, navegação raiz, composição, inicialização e bindings transversais do host. `di/` fica no nível do host; não há um segundo pacote `composition/di`. As fachadas antigas foram aposentadas; o inventário do APP conserva somente entradas Android efetivas. Login e perfil continuam em Account, que é o dono desses fluxos; Home mantém sua própria feature.

```text
./
  ├── app/               Application · MainActivity · navigation · composition/initialization
  ├── feature/account/   presentation · domain · api · data · transport
  ├── feature/home/      presentation · domain · data
  ├── feature/*/         camadas existentes dos demais fluxos
  └── core/              common · domain · data · auth · network · key-management · queue
                         design-system · presentation · navigation · security · testing
```

O app compõe os entry points das features e as conecta aos serviços CORE; cada feature depende de abstrações compartilhadas conforme o grafo Gradle.

Foram realocados 61 arquivos: 56 saíram fisicamente do app e cinco foram classificados em sua navegação/composição. O app não possui ViewModel própria. O inventário `config/app-compatibility.json` conserva somente o serviço Android de push e duas Activities de debug. As três entradas restantes de `config/host-public-compatibility.json` já pertencem à navegação/composição do próprio APP; as antigas entradas CORE/features foram aposentadas.

| Responsabilidade | Dono real |
|---|---|
| Seleção/troca de ambiente, confirmação de cadastro, permissões de login | Account Presentation/Domain/Data |
| Estado de inicialização e fila | Onboarding Presentation; SDK/WebView/broadcasts em CoreQueue |
| Tema de campanha | CorePresentation; porta em CoreDomain e repository em CoreData |
| Preferências de ambiente | `app/environment/persistence/EnvironmentPreferences`, com recibo de `commit` confirmado |
| Criação/atualização de clientes e preparação TLS | CoreNetwork |
| Observação de sessão em foreground | CoreAuth; app fornece predicado de rotas e navegação |
| Topaz, configuração de chaves Firebase, inspeção debug/release | CoreSecurity |
| Cadastro push, token/mensagem/notification Android e store | Notifications `presentation/di` compõe execução de Core Domain e coordenação de Notifications Domain; token/mensagem/store ficam em Data; APP fornece configuração e entrada Android |
| Localização do checkout | CartData |
| Fixtures de apostas confirmadas | TicketsData, consumidas pelos mesmos ambientes explicitamente simulados |

Screens/componentes recebem estado e callbacks. Account e Onboarding produzem estados imutáveis em ViewModels com StateFlow público e `viewModelScope`; o tema compartilhado possui CampaignViewModel em CorePresentation. Routes integram lifecycle e navegação. A preparação Android permanece em adaptadores, com opções tipadas, sem importar `AppContainer`, `BuildConfig` ou `MainActivity` de fora do app.

A mudança de ambiente preserva o snapshot selecionado/efetivo, a ordem de invalidação da autorização/tokens e a chave original. I/O ocorre no dispatcher injetado; recuperação distingue commit confirmado, commit falho e resultado perdido durante recriação. A conclusão de cadastro confirma identidade antes de publicar e antes de navegar, com acknowledgement explícito. Inicialização/fila invalidam respostas antigas; sair/reentrar reinicia a preparação antes de admitir a fila. Push captura identidade antes de obter o token assíncrono.

`verifyAppArchitecturePolicy` e `verifyCleanArchitecture` rejeitam implementação de negócio no app, imports de implementação do app em features/CORE, CORE→implementação de feature, dependências Android em Domain, ViewModel→plataforma/repository, estado mutável público, trabalho de negócio em composição e providers duplicados nas fachadas. Vinte testes de política e doze violações reais de Gradle foram aprovados, com restauração byte a byte e gates positivos aprovados. Evidências desta rodada: `build/verification/app-mvvm-20261019`; os resultados globais e limites estão na seção correspondente de `MIGRATION_VALIDATION.md`.

O host segue a recomendação oficial de que o módulo de aplicação seja o entry point, agregue as features e normalmente componha a navegação raiz. A implementação também foi comparada com o módulo `app` do Now in Android, que usa esse mesmo papel para `Application`, `Activity`, scaffold e navegação, mantendo as features e o CORE como módulos separados. A orientação oficial não exige uma única topologia: o critério é coesão, baixo acoplamento e fronteiras claras. O binding de configuração remota agora está em Core Data; OAuth e política de autorização são providos por Account. Os consumidores usam esses donos diretamente; as fachadas antigas dos providers foram aposentadas.

### Host APP — fronteiras após a composição tipada (20/10/2026)

`AppNavHost` recebe `AppFeatureGraphDependencies`, um registro tipado das 13 interfaces de navegação. Cada implementação e binding de navegação pertence à feature que declara o contrato. As fachadas inativas `AppContainer` e `ApplicationModule` foram removidas, incluindo seus registros no inventário; seus contratos públicos deixam de existir. `AppInitializationBindings` é o módulo Hilt de conectividade, relógio local, dispatchers e estado de sessão em background. `app` não tem dependências de produção diretas em `feature:*:data`. O gate rejeita a reintrodução das duas fachadas mesmo por uma entrada de compatibilidade.

`HostFeatureConfiguration` concentra valores do host necessários às features (ambiente, endpoints, versão, opções Firebase e flags). `AppHostRuntime`, `AppInitializationDependencies` e `AppSessionDependencies` mantêm inicialização e sessão globais no host; Network, Account e Onboarding executam suas operações nos respectivos owners. Módulos `di/` sob os pacotes das features são donos dos bindings; `app/di` fica restrito a valores de plataforma e composição global. O agregado de implementação dos 13 módulos Data foi removido; dependências `testImplementation` do app são permitidas para testes de contrato e não chegam ao runtime.

Na verificação final, `modernCheck` e `releaseCheck` passaram juntos nos três flavors, assim como os gates Python de APP e Feature e `verifyCleanArchitecture`/`verifyFeatureModules`. O registro de execução e os limites de ABI/homologação permanecem em `MIGRATION_VALIDATION.md` e no plano `docs/superpowers/plans/2026-10-20-app-host-coherence.md`.

## CORE — donos e APIs consolidados (20/10/2026)

Os tipos de CORE usam o pacote do módulo que os compila. `EffectiveEnvironment`, `LegacyTestEnvironment` e `EnvironmentSelection` pertencem a `core.domain.environment`; `ForegroundSessionBackgroundState` a `core.auth.foreground`; `HomeSection` a `core.presentation`; a factory e o coordenador de clientes a `core.network.config`; o store de chaves assinadas e os adaptadores de autorização a `core.security`. As nove entradas de CORE do inventário de pacotes históricos do app foram removidas. Os consumidores usam diretamente esses donos, sem wrappers no pacote `apostas`.

Os aliases de identidade e encerramento de sessão de `core.auth` foram retirados; os consumidores importam os contratos de Account que já eram seus tipos efetivos. `EndSessionGateway` e `JwtCurrentUserIdentityProvider` continuam sendo as pontes Android/JWT de CORE. Os aliases sem uso `CaixaTheme` e `CaixaColors` foram removidos; o design system publica `CaixaDSTheme` e `CaixaDS.colors`. O fake compartilhado de sessão pertence a `core.testing`, usado somente por testes.

A limpeza retira intencionalmente os contratos públicos dessas fachadas, aliases e nomes históricos. Os valores de ambiente, URLs, payloads, chaves de persistência, regras de sessão, ordem de substituição de clientes/pins e escopos Hilt são preservados. A tarefa raiz `verifyPersistenceCoverage` executa a cobertura histórica de persistência; os módulos vazios de persistência foram retirados. Os contratos de transporte ativos continuam preservados nos donos já documentados.

`verifyCleanArchitecture` rejeita tipos de CORE em pacotes de outro módulo ou do app, mesmo se registrados no inventário de compatibilidade, e rejeita novas APIs Kotlin de produção marcadas com `@Deprecated`. Os testes de política e as fixtures Gradle exercitam essas restrições. Os resultados da validação desta limpeza estão em `MIGRATION_VALIDATION.md`.

## Features — declarações efetivas e DI própria (20/10/2026)

As 14 features foram revisadas: Account, Betting, Cart, Home, Marketplace, Marketplace Filter, Notifications, Onboarding, Payment, Responsible Gaming, Results, Retail, Support e Tickets. Foram retirados 395 aliases públicos, 97 wrappers de casos de uso/acesso remoto, três propriedades que republicavam objetos de política, uma extensão de campanha duplicada e uma entrada de Home sem consumidores. Os consumidores importam as declarações reais dos modelos, contratos, DTOs e implementações. A entrada ativa `presentation.screen.HomeScreen` conserva a estrutura de composição existente; a antiga `presentation.host.route.HomeScreen` foi retirada por não ter consumidores. A campanha de Home usa diretamente a conversão compartilhada de Core Presentation. O alias interno da forma de uma linha SQLite continua local ao reader; aliases de utilitários nos testes não são APIs de produção.

As 23 entradas de features em `config/host-public-compatibility.json` foram aposentadas. As fachadas duplicadas de DI, factory de Notifications e store push foram removidas; `AppBettingBindings`, `AppTicketsBindings`, `AppPushBindings` e o adaptador ativo `PushRuntime` pertencem agora ao pacote `di` de suas features. O transporte Android de cadastro push pertence a `Account Data`, implementando a porta compartilhada de CORE. Na organização vigente, `PushRuntime` fornece configuração/Android a `AndroidPushRuntime`, ambos em Notifications `presentation/di`; esse runtime recebe os callbacks de lifecycle e compõe os casos de uso, enquanto token/mensagem/store permanecem em Data.

Os contratos de transporte conservam assinaturas HTTP, nomes de classes DTO e payloads. Seus namespaces agora identificam a feature proprietária em `feature.<feature>.data.dto`, sem aliases para os nomes antigos. Não há alterações de URLs, rotas, preferências, regras de sessão/pagamento, owners de ViewModel, StateFlows, ordenação de callbacks ou escopos Hilt. Referências a classes de fachada em construtores passam a usar suas implementações existentes; a compatibilidade dos tipos aposentados deixa de ser preservada.

O gate de features rejeita aliases públicos de compatibilidade, APIs Kotlin de produção obsoletas, wrappers que apenas delegam ao caso de uso existente e declarações no namespace antigo do app, mesmo se registradas no inventário. `scripts/test-feature-architecture-gate.py` exercita sete violações reais de modo serial e restaura fontes/inventário byte a byte. As evidências e os resultados da matriz global desta rodada ficam em `MIGRATION_VALIDATION.md`.

## Conteúdo esportivo das apostas

`BettingGameSelection` mantém seus métodos e o mesmo `updateState`, delegando a `BettingNumberSelection`, `BettingSuperSevenSelection`, `BettingSportsSelection`, `BettingSelectionQuantities` e `BettingSelectionReset`. Uma única instância de `BettingSelectionWarnings` conserva as duas flags compartilhadas de avisos; mudar quantidade, selecionar números, usar shake ou limpar a seleção não cria outro histórico de avisos. A política Domain existente continua intacta.

`BettingSportsContent.kt` conserva as entradas `BettingLegacyTeams` e `BettingLegacyLoteca`, seus argumentos e defaults. Cada composição liga o ViewModel atual a `BettingSportsCallbacks`, que fornece as mesmas três referências de métodos, sem estado, cache ou escopo adicional. `BettingGameViewModel` e `BettingGameRoute` permanecem intactos.

- `BettingTeamSelection`: filtro, grade e cartão de seleção dos times, recebendo estado e callbacks.
- `BettingLotecaContent`: composição das partidas, contadores, ajuda e quantidade.
- `BettingLotecaOutcomeCard`: cartão das opções por partida e descrição acessível.
- `BettingTeamShield`: decodificação Base64/raw/data URI e fallback visual.
- `BettingSportsMetrics`: altura nativa do texto e alinhamento inteiro original da Loteca.

Os corpos visuais foram movidos literalmente, incluindo dimensões, fontes, distribuição, ordem dos modificadores e chaves de `remember`. A descrição do resultado continua vinculada a `remember(team, descriptionBindingKey)`; não passa a depender de `selected`. Os visuais recebem funções, e as duas entradas mantêm a ligação ao mesmo estado/ViewModel existente. Os testes de geometria continuam com seus oráculos e assertivas originais; diferenças preexistentes entre perfis de densidade estão registradas em MIGRATION_VALIDATION.md.

## Coordenação do resgate de prêmio

`PrizeRedemptionViewModel` mantém o construtor e a fachada pública, o store Redux privado/`StateFlow` público e a fila de comprovantes. Seus colaboradores são internos a `feature:retail` e recebem as mesmas dependências, `viewModelScope` e dispatchers:

- `PrizeRedemptionCoordinator`: detalhes do prêmio, concurso atual e entrada na aposta.
- `PrizeLotteryChannel`, `PrizePixChannel` e `PrizeMercadoPagoChannel`: operações específicas; agência compartilha a obtenção de código com lotérica, preservando a regra de QR distinta.
- `PrizeRedemptionSubmission`: envio e interpretação dos resultados de resgate.
- `PrizeRedemptionTimer`: Job do contador, cancelamento e decrementos existentes.
- `PrizeRedemptionReceipts`: downloads e emissão de bytes/nome na fila original.
- `PrizeRedemptionNotices`: bloqueios de interação, avisos e confirmação de cadastro por identidade.
- `PrizeRedemptionRedirects`: consumo dos destinos, pausa e verificação de conectividade no clique.

Os tipos públicos de estado foram realocados para `PrizeRedemptionUiState.kt` no mesmo package. A fachada mantém seleção de canal e Back/retorno às opções. Nenhum colaborador cria escopo independente; os instantes das guardas e atualizações de estado continuam iguais.

## Coordenação de Home

`HomeViewModel` mantém a fachada pública, o estado privado/read-only e as três revisões `AtomicLong` compartilhadas. Seis colaboradores internos de `feature:home` recebem as mesmas referências, dependências, `viewModelScope` e dispatchers:

- `HomeSessionCoordinator`: observação da sessão, invalidação ordenada das revisões, atualização do conteúdo e refresh de não lidas; confirmações de revisão do carrinho e pagamento.
- `HomeLoadingCoordinator`: parâmetros, configuração, combos, recuperação e finalização do carregamento.
- `HomeNotificationsCoordinator`: badge, leitura, links, avisos e destinos, com versões, identidade e compare-and-set existentes.
- `HomeCartCoordinator`: resumo do carrinho e descarte de resposta anterior à sessão/requisição atual.
- `HomeSocialTransfersCoordinator`: carga única, cache de sucesso e retry após falha.
- `HomeEntryCoordinator`: bloqueio compartilhado, preparação e consumo das três decisões de entrada.

`HomeUiState` e `HomeNotificationRedirect` permanecem públicos no mesmo package, agora em `HomeUiState.kt`. O init continua iniciando o observador antes do load. As versões permanecem na fachada para que observação e respostas assíncronas compartilhem exatamente as mesmas instâncias; nenhum helper cria escopo ou estado independente.

## Coordenação dos detalhes de aposta

`BetDetailViewModel` mantém a fachada pública, init, store Redux privado/`StateFlow` público e a mesma fila de comprovantes. Quatro coordenadores internos de `feature:tickets` compartilham estado, fila quando necessária, `viewModelScope`, dispatchers e use cases existentes:

- `BetDetailDetailsCoordinator`: recuperação/hidratação tipada, falhas de serviço e resultados dos concursos.
- `BetDetailReceiptCoordinator`: seleção do comprovante de aposta/prêmio, download, nomes/bytes e falha de abertura.
- `BetDetailFavoriteCoordinator`: edição, validação, snapshot pendente e consentimento para sobrescrita.
- `BetDetailRepeatCoordinator`: elegibilidade dos dados, opções/conversão de concurso e envio ao carrinho.

`BetDetailMappers` contém os mapeamentos existentes de concursos, favoritos, mensagens e o construtor do draft de repetição, sem recomputar suas regras. Os três tipos públicos foram realocados para `BetDetailUiState.kt`, no mesmo package. Guards e loading permanecem nos mesmos pontos; nenhum novo lock/retry/cancelamento ou escopo foi introduzido.

## Coordenação do marketplace

`MarketplaceViewModel` mantém construtor/defaults, fachada pública, store Redux privado/`StateFlow` público, revisão de retorno dos filhos, roteamento das ações, abas e expansão. Seis colaboradores internos do mesmo package compartilham o estado e as dependências existentes:

- `MarketplaceLoadingCoordinator`: busca, aplicação das páginas e cabeçalho do concurso.
- `MarketplaceSearchPagination`: filtro em uso, próxima área, reset e escolha da próxima página, preservando município→estado→país.
- `MarketplaceFavoriteRetailersCoordinator`: carregamento e confirmação da remoção de lotéricas favoritas.
- `MarketplacePoolFavoriteCoordinator`: confirmação de favorito pela lista de bolões e atualização dos cartões da mesma lotérica.
- `MarketplaceSharesCoordinator`: limites das cotas selecionadas e bloqueio durante a inclusão.
- `MarketplaceCartCoordinator`: montagem do pedido, reserva confirmada, disponibilidade e consumo da confirmação.

Os coordenadores assíncronos recebem o mesmo `viewModelScope` e dispatcher. `updatingFavoriteLotteryIds` e `addingPoolCodes` continuam compartilhados no estado; nenhum lock, retry, cancelamento ou sincronização adicional de listas foi introduzido. O init mantém busca→favoritas→cabeçalho. Retry e retorno de filhos preservam as consultas originais; a busca continua lendo o filtro dentro da coroutine, sem novo descarte de respostas ou deduplicação. `MarketplaceContract.kt` e o efeito Compose em `MarketplacePagination.kt` permanecem idênticos.

## Coordenação do cartão de pagamento

`PaymentCardViewModel` mantém o construtor/defaults, a fachada pública, o store Redux privado/`StateFlow` público, init e apresentação comum das falhas. Seus seis coordenadores são internos a `feature:payment`:

- `PaymentCardFormCoordinator`: normalização do formulário, seleção da marca/CVV e progressão entre etapas, usando as propriedades existentes do estado.
- `PaymentCardSavedCardsCoordinator`: cartão por ID e recuperação do primeiro cartão real nas entradas legadas; `load()` continua retornando `Job`, com guardas dentro da coroutine.
- `PaymentCardSubmissionCoordinator`: guardas do envio, snapshot de seleção/CVV, total do carrinho e interpretação do resultado do POST.
- `PaymentCardMonitoringCoordinator`: início e retry do acompanhamento, protocolo de polling e registro do valor original no histórico local.
- `PaymentCardNoticeCoordinator`: apresentação dos avisos com o mesmo relógio, consumo por identidade e pausa para cadastro/apostar mais.
- `PaymentCardRedirectCoordinator`: redirecionamentos, pausa/retorno e confirmação única da revisão da compra.

A fachada cria uma única `PaymentCardPurchaseContext`, compartilhada pelos coordenadores que leem ou atualizam a referência `PaymentCardTrackedPurchase` (poll/amount). O marcador `reviewHandled` fica no coordenador de redirecionamentos/revisão. `PaymentCardUiState` e `PaymentCardStage` permanecem públicos no mesmo package, agora em `PaymentCardUiState.kt`. Não há estado de UI paralelo ou novo escopo; operações assíncronas usam o mesmo `viewModelScope` e dispatchers. Avisos e redirecionamentos não reapresentam o POST automaticamente; retry do polling retém ID, contagem e valor originais. Nenhuma guarda, timeout, limpeza ou regra de conclusão adicional foi introduzida.

## Coordenação de carrinhos favoritos e pagamento

`FavoriteCartsViewModel` mantém o estado e os caches originais. `FavoriteCartsLoading` concentra carga/expansão, `FavoriteCartsRemoval` as exclusões, `FavoriteCartsImport` a importação e suas decisões, e `FavoriteCartsFailures` a apresentação das falhas. Os três colaboradores assíncronos recebem o mesmo `viewModelScope` e dispatchers; carga, exclusão e importação compartilham `betsCache`, e a importação conserva o mesmo conjunto de IDs já adicionados. Os tipos públicos continuam no mesmo package em `FavoriteCartsUiState.kt`.

`PaymentViewModel` conserva o ownership do estado, restauração Pix, guarda `generatingPix`, `load()` e tratamento comum de erros. `PaymentMethodsLoading`, `PaymentSavedCards`, `PaymentPrePurchase`, `PaymentPixOperations`, `PaymentNotices` e `PaymentRedirects` recebem o estado e dependências existentes. Não há outro StateFlow ou escopo. A ordem de geração/restauração Pix, consulta, prioridade de avisos/redirecionamentos e registro da compra é a original. `PaymentMode` e `PaymentUiState` permanecem públicos no mesmo package, em arquivo próprio.

## Adaptadores de autenticação e fila

`AppAuthAuthenticationGateway` continua implementando as quatro interfaces públicas existentes. `AppAuthSession` reúne as mesmas instâncias de stores, registries, serviço AppAuth e validador de identidade, recebendo `monitor = this` e o callback `logout` da fachada. `AppAuthBrowserAuthorization`, `AppAuthBiometricAuthorization`, `AppAuthBiometricAvailability`, `AppAuthRefresh`, `AppAuthTokenUpdates` e `AppAuthEndSession` separam as operações. As anotações `@Synchronized` continuam nos métodos da fachada; os blocos movidos usam o monitor original, mantendo gateway→store e a validação de ambos os guards no pós-commit. PKCE, nonce, credenciais, tickets, cancelamento e persistência mantêm as regras anteriores.

`FilaBraGate` mantém as duas entradas públicas e os destinos internos. `FilaBraInitialization` conserva os efeitos de splash/segurança; `FilaBraQueueGate` a coordenação da fila e StartupViewModel; `FilaBraMockGate` o fluxo já condicionado à configuração debug; `FilaBraStatusContent` os componentes; `FilaBraQueueWebView` os callbacks/disposal da WebView; `FilaBraBroadcasts` os broadcasts por evento. O estado continua na composição, com as mesmas chaves, owners, tempo mínimo e ordem de efeitos. Flags de ambiente e integração SDK não foram alteradas.

## Mapeamento de carrinho e tickets

`RemoteCartRepository` mantém o contrato/construtor e delega operações a `CartRemoteOperations` e validação a `CartValidationOperations`. `CartPayloadMapper`, `CartItemMapper`, `CartSportsMapper` e `CartJsonValues` preservam montagem, ordem, badges, colunas/zeros, escala decimal, prioridades e campos esportivos. A extensão `toDomainCart` permanece no arquivo original como entrada compatível para os demais repositories.

`TicketsPayloadMapper` conserva as extensões internas consumidas no módulo. `TicketFilterMapper`, `TicketCheckedResultMapper`, `TicketConfirmedBetMapper`, `TicketPurchaseMapper`, `TicketSportsMapper` e `TicketJsonValues` organizam filtros, consulta manual, apostas, compras, esportes e leitura JSON. Helpers realocados têm prefixos específicos; recursão, distinct/ordem, fallbacks e semântica dos payloads não foram redesenhados.

## Data

`RemoteTicketsRepository` delega acesso remoto/cache a `TicketsRemoteDataSource`, decisão de endpoint a `BetHistoryRoutingPolicy`, conversão a `TicketsPayloadMapper` e comprovantes a `ConfirmedBetReceiptLoader`.

`RemotePaymentRepository` delega transporte a `PaymentRemoteDataSource`, tokenização a `CardPaymentTokenizer`, serialização a `CardPurchaseRequestMapper`, payloads a `PaymentPayloadMapper` e protocolo de compra a `PurchaseResponseMapper`. A ordem de preparação/envio, captura de purchaseId, avisos/redirects, HTTP303, erros e cancelamento permanece compatível. Interfaces públicas, DTOs e campos JSON não foram redesenhados.

`RemoteRetailRepository` permanece a fachada dos contratos `RetailRepository`, `PrizeRedemptionRepository` e `MercadoPagoRegistrationRepository`. Cinco colaboradores internos de Data separam suas operações:

- `RetailLotterySearch`: consulta as lotéricas após obter os IDs favoritos, sem cache adicional.
- `RetailFavorites`: lê a lista/IDs favoritos e executa inclusão/remoção com as mesmas prioridades de identificação.
- `PrizeRedemptionOperations`: detalhes, código lotérica/agência, consulta Pix, resgate Pix/Mercado Pago e cancelamento Pix, preservando os tratamentos por canal.
- `MercadoPagoRegistrationLoader`: token de cadastro e interpretação original de falhas HTTP/IO.
- `RedemptionReceiptLoader`: comprovantes de aposta e prêmio, conservando os bytes devolvidos pela fonte.

`RetailPayloadMapper` organiza o parsing recursivo e mapeamento de lotéricas; `PrizeRedemptionPayloadMapper` conserva detalhes, premiações, modalidades, canais, códigos, beneficiário, payloads e resultados; `RetailJsonValues` mantém as leituras tolerantes existentes com nomes internos prefixados. `RetailRemoteDataSource` e `PrizeRedemptionRemoteDataSource` continuam públicos no mesmo package, em arquivos próprios. O construtor/default cast da fachada e a exigência tardia da fonte opcional permanecem: os colaboradores de prêmio recebem `::prizeSource`. Não há cache, scope, fallback de cliente, normalização, retry ou captura de falhas adicional. O adaptador Retrofit e o composition root não mudam.

`RemoteBettingRepository` mantém o contrato público `BettingRepository`, o construtor e a mesma fonte. Quatro colaboradores internos de Data organizam as operações:

- `BettingModalitiesLoader`: catálogo de modalidades.
- `BettingParametersLoader`: obtenção obrigatória dos parâmetros e consulta condicional dos escudos.
- `BettingCombosLoader`: catálogo e inclusão de combos no carrinho.
- `BettingSubmission`: envio do pedido serializado e conversão do carrinho retornado.

`BettingModalityMapper` conserva códigos/IDs/nomes; `BettingParametersMapper` transforma parâmetros/jogos/pools; `BettingComboMapper` mantém valores e entradas dos combos; `BettingSportsMapper` transforma equipes/partidas e os campos esportivos dos pedidos; `BettingRequestMapper` contém o construtor original de AddBetRequestDto e o mês. Gson continua sendo criado por chamada; nenhum campo/ordem/quantidade/coluna/zero/opcional é normalizado ou acrescentado. Consulta de escudos mantém prioridade game?:nextGame, último ID recebido e o runCatching amplo original, inclusive fallback para cancelamento lançado pela fonte. As demais operações propagam suas falhas. Não há cache, scope, validação ou retry novo; RemoteDataSources/Retrofit/DTOs/Hilt continuam intactos.

## Design system e ferramentas

CaixaDS continua utilizando Compose/Material3 com identidade visual existente. As 26 declarações do arquivo legado foram organizadas em scaffold, ícones, seletores, diálogos, barras, botões, conteúdo, grade e resumo. APIs e corpos permanecem idênticos; previews usam somente dados locais inertes.

`gradle/libs.versions.toml` centraliza dependências, sem atualização indiscriminada. `.editorconfig` define formatação; `verifyKotlinFormatting` verifica UTF-8/LF/newline/whitespace, sem reescrever fontes. Android Lint e gates de arquitetura, Compose, inventário, backend, rede, identidade, Topaz, FilaBRA e CaixaDS permanecem ativos. A cobertura mantém mínimos e inclui os colaboradores extraídos; classes anteriormente protegidas não foram excluídas para aprovar a migração. Tickets exige dados JVM e Android reais antes do gate de 95%; os filtros incluídos permanecem no denominador e são exercitados por testes Compose de interação.

## Segurança e validação

Permanecem OIDC/AppAuth, refresh/headers/TLS/pinning, Topaz/FilaBRA, serialização e chaves persistidas. Produção mantém serviços reais; fakes ficam restritos a testes/previews/ambiente debug explicitamente habilitado. Não foram adicionados logs de dados sensíveis.

A validação combina caracterização no original, equivalência de fontes, testes JVM, instrumentação no AVD, lint, gates e builds debug/release dos três flavors. [MIGRATION_VALIDATION.md](MIGRATION_VALIDATION.md) registra comandos, resultados e limites. Validação local não substitui homologação de provedor privado ou comparação visual de todos os estados do aplicativo legado.

## Continuação — dez arquivos por responsabilidade (18/10/2026)

As quatro ViewModels conservam seus construtores, métodos, StateFlows, filas de efeitos e o mesmo `viewModelScope`. Colaboradores recebem explicitamente o store Redux tipado e o escopo original; nenhum estado paralelo ou ciclo de vida adicional foi criado.

- RapidConfiguration: Loading, Amounts, Favorites, Numbers, Submission e Formatting; captura do formulário antes do launch, limites, diferença de cinco reais e seleção oposta preservados.
- Login: AuthorizationCoordinator, BiometricPreferences, Completion, EntryDecisions e ResultMapping; autorização, cancelamento, fallback, matrícula, bloqueio parcial/total e limpeza de sessão preservados.
- ResponsibleGaming: Assessment, Suspension, SocialTransfers e Errors; mantém guards antes/depois da consulta e o mesmo emissor de efeitos na fachada.
- Notifications: Loading, Projection, Mutations, Preferences, Actions e Errors; `projectionRevision`, retenção durante leitura, confirmações e fila de compartilhamento permanecem na mesma fonte de estado.

TicketsNavigationGraph registra, na ordem original, detalhe, histórico, compras, scanner e consulta manual/resultado. Cada bloco de destino permanece literal: mesma criação lazy dos ViewModels, owners/keys, argumentos, `remember`, `LaunchedEffect` e callbacks.

PrizeOptionsScreen conserva composição e usa componentes separados para concurso atual, cabeçalho, premiações e opções. PrizePixDataScreen mantém composição e chama seleção/abas/campo/confirmação/botões; PrizePixActions permanece no mesmo package. Modifiers, métricas, textos, cores, fontes e callbacks são os originais.

NetworkClientFactory é a fachada de defaults públicos; SessionNetworkClients, TransactionNetworkClients, AuthenticatedNetworkClients e PaymentProviderNetworkClients recebem argumentos explícitos. NetworkRetrofitBuilder conserva a mesma mutação do builder e ordem de interceptadores, renovação, bearer, SILCE e pinning. Não há retry, cliente ou fonte adicional.

ApiModels mantém o envelope genérico; os outros 85 DTOs estão agrupados por domínio no mesmo package, com campos/annotations/defaults/ordem e nomes binários idênticos. MockBackendInterceptor conserva interceptação; cenário, opções, resposta, resolução ordenada, roteamento/erros e fixtures por domínio ficam separados. O fixture de Marketplace continua lazy e busca o mesmo resource pelo owner original.

Filtros de cobertura seguem os novos colaboradores: mínimos e entradas obrigatórias anteriores preservados; Notifications continua exigindo JVM e Android sobre todas as classes. Nenhuma dependência ou configuração de produção foi alterada.

## Sete ajustes concretos de conformidade — 19/10/2026

- Domain: VisitorCartPolicy concentra quantidade, disponibilidade, capacidade, preço, duplicidade, espelhamento e remoção agrupada. VisitorCartSelection e VisitorCartEntry transportam apenas valores do domínio, sem Android, linhas SQL ou DTOs remotos.
- Preferences/Data: SQLiteVisitorCartRepository coordena a política e a transação; VisitorCartGraphStorage/VisitorCartGraph mantêm banco, schema, sequência de IDs e escrita do grafo; VisitorCartMapper mantém a projeção de identidade e a apresentação existente.
- Presentation/plataforma: PrizeReceiptSaving e BetReceiptDelivery executam a escrita em IO e retornam ao contexto do chamador para os efeitos. Cancelamento nunca se transforma em erro de visualização nem em publicação tardia. O escopo da composição mantém a propriedade da gravação.
- QR: PrizeQrCodeGenerator executa codificação/pixels em Default; renderPrizeQrCode também cria o bitmap fora da Main. A composição usa um produtor vinculado ao valor para impedir resultados obsoletos. Os mesmos 640 pixels de origem, 200dp de exibição e descrição acessível foram preservados.
- NotificationsRoute e ResponsibleGamingRoute usam collectAsStateWithLifecycle somente para o StateFlow do ViewModel. Eventos, navegação, interfaces e owners de estado não foram alterados.

## Links externos e comprovantes recuperáveis — 19/10/2026

AccountRoute e HomeRoute tratam ActivityNotFoundException, SecurityException e IllegalArgumentException na fronteira de abertura da plataforma. O sucesso só é exibido após a chamada retornar. Estado local salva o resultado/erro; novas tentativas exigem ação do usuário e conservam a URL original. As interfaces e os ViewModels existentes não foram alterados.

PrizeReceiptDelivery é um colaborador interno de Presentation pertencente à PrizeRedemptionViewModel existente. Nesta etapa, somente a gravação do resgate passa do escopo da composição para viewModelScope, sobrevivendo à mudança de configuração. O colaborador mantém bytes pendentes privados e metadados Redux imutáveis com destinos opacos String, conclusão e falhas; não mantém Activity, View, Context ou callbacks de UI. O writer captura apenas ContentResolver obtido de applicationContext e usa a fronteira IO/cancelamento já testada em PrizeReceiptSaving. A composição atual consome a conclusão e abre o visualizador; uma composição descartada não o abre.

rememberSaveable armazena somente filename e URI de destino como strings. A recriação com ViewModel retida reutiliza os bytes e a escrita; uma nova ViewModel pode recuperar exclusivamente comprovante-aposta-{betId}.pdf ou comprovante-premio-{betId}.pdf por meio dos mesmos use cases. Downloads públicos mantêm seu tratamento de erro; o caminho interno de recuperação oferece retry explícito mesmo em OPTIONS. PDFs não são inseridos em Bundle/SavedStateHandle.

Falha de gravação conserva bytes e URI até repetir ou cancelar; falha de recuperação conserva os metadados; falha de abertura conserva o URI salvo. Três diálogos locais do Design System oferecem Tentar novamente e Cancelar. Retry não repete resgate, seletor ou download quando basta gravar/abrir; guards impedem callbacks duplicados e limpeza tardia. Cancelamento de coroutine continua propagado. A restauração de metadados com nova VM foi testada separadamente de ActivityScenario.recreate; isso não constitui um ensaio de encerramento real do processo pelo sistema.

O gate Retail inclui aditivamente PrizeReceiptDelivery e dados JVM+instrumentação Android obrigatória. Todos os padrões de classes anteriores e o mínimo 95,01% foram preservados. Não foram alterados componentes compartilhados do DS, manifestos, recursos, dependências, clientes ou configurações de segurança.

## Verificação global final — 20/10/2026

`modernCheck` e `releaseCheck` passaram para os três flavors. O gate Data mede 2628/2762 linhas (95,148%), acima do mínimo de 95,01%; testes instrumentados do adaptador de notificações exercitam store, mensagens, permissão e coordenação do token. O construtor público de `AndroidPushRuntime` permanece inalterado; a fonte de tokens substituível é interna.

Os cinco visuais condicionais foram executados sem skips: três aprovados e dois mantêm a diferença já conhecida de 80 px no resumo de prêmio e na primeira linha da compra. O app foi restaurado em `PrdDebug`, `verifyCleanArchitecture` e formatação passaram. OAuth, biometria, SSO, fila e provedores privados ainda dependem de conta/aparelho autorizados; assinatura de distribuição e conferência física também permanecem pendentes. Ver `MIGRATION_VALIDATION.md` para os logs e limites completos.

## Origem e composição das notificações — 19/10/2026

`LocalNotificationRepository` conserva o construtor público e as sete operações de Domain. Ele possui `LocalNotificationMessages` e `LocalNotificationPreferences`, ambos internos, com acesso exclusivamente sob o mesmo monitor `synchronized(this)` da fachada. Listas retornadas são snapshots; leitura/exclusão não alteram snapshots anteriores. O catálogo `LegacyNotificationInboxSamples` mantém literalmente os dados locais de demonstração do legado e os IDs únicos preexistentes na migração. Não há persistência nem comunicação remota nessa caixa.

No composition root, `NotificationsModule.notificationRepository` permanece unscoped; `NotificationViewModelFactory` obtém uma instância por destino e a compartilha entre seus sete use cases. `UnreadNotificationsModule` fornece contador/repository unscoped. `LatestNotificationModule` fornece a mesma sessão baseada em SessionTokenStore, datasource/repository de leitura singleton e o caso de uso sem escopo novo. `NotificationsModule` conserva apenas o provider efetivo do repository; os antigos delegadores de compatibilidade foram retirados.

O popup autenticado mantém seu protocolo, identidade de sessão e IDs reais. FCM continua exibindo notificações Android, sem alimentar esta lista. ViewModel, UiState, StateFlow, fila de eventos, interfaces Domain, payloads e caminhos HTTP estão inalterados. A origem local é preservada por orientação expressa do usuário; não se declara um histórico remoto real implementado.

## Coordenação de checkout do carrinho — 19/10/2026

CartCheckoutCoordinator mantém seu construtor e todos os métodos declarados, incluindo os retornos Unit/Boolean. A fachada conserva guardas de entrada, verificação de conta e atualização opcional de cotas expiradas. A mesma CartViewModel continua proprietária do store Redux e do viewModelScope; os colaboradores recebem essas referências, sem criar outra fila, Flow ou coroutine scope.

- CartCheckoutLocation possui a conta pendente, consulta de país, retry, bloqueio e descarte do alerta. O callback suspenso de compra recente/validação permanece dentro do try/catch/finally original; cancelamento bloqueia a continuação, limpa loading/ações e é relançado.
- CartCheckoutPermissions registra o consentimento, publica a ação somente após o registro e mantém consumo único, resultado de permissão, retorno de settings e falha de abertura. Retry/bloqueio chamam a instância de localização existente.
- CartCheckoutRecentPurchase possui o par carrinho/conta capturado para a confirmação; alteração do carrinho reinicia as guardas. A continuação direta conserva sua coroutine original.
- CartCheckoutValidation aplica login/validação e mantém os estados de aceitação/falha, incluindo expirationRemovalInFlight.
- CartCheckoutNotices mantém mapeamento, relógio/rede e dismiss/accept por identidade. CartCheckoutRedirects mantém consumo de pagamento/conta e consumo atômico de redirect pela mesma referência.

As dependências são construídas pela fachada em ordem, com referências de função sem execução no init. Não há ciclo de inicialização. Domain, Data, protocolos, recursos, UI, Hilt, interfaces públicas e StateFlows permanecem iguais. Novos colaboradores estão incluídos pelo padrão de cobertura existente, sem exclusão ou redução de mínimo.

## Histórico Tickets e colaboradores internos — 19/10/2026

TicketsHistoryCoordinator continua sendo a entrada compatível consumida pela TicketsViewModel. A ViewModel é a única proprietária do store Redux/StateFlow e viewModelScope; os colaboradores não criam outro dono de estado, dispatcher ou scope.

- TicketsHistoryBets executa configuração → modalidades → histórico e reaplicação de filtros com as mesmas guardas de sessão e loading.
- TicketsHistoryPurchases inicializa os meses e recarrega compras. Chama o método suspenso de paginação dentro da coroutine original, sem novo launch.
- TicketsHistoryPagination concentra primeira página/append, offset/tamanho 10, mesclagem distinctBy(id), continuidade baseada no tamanho recebido e limpeza original de flags. Mantém as verificações de sessão antes/depois da requisição.
- TicketsHistoryBetFilters mantém seleções, toggles, visibilidade e defaults; limpar chama a mesma operação de reaplicação.
- TicketsHistoryPurchaseFilters mantém meses, métodos, status e catálogos auxiliares; limpar/aplicar chama o mesmo reload. Os pontos existentes de verificação de sessão permanecem iguais, sem redesenho de concorrência nesta extração.
- TicketsHistoryRecovery decide entre inicializar catálogos ausentes ou repetir a página com o estado disponível. Todos os erros continuam usando o callback fail original da ViewModel, com as mesmas mensagens/flags.

A fachada constrói os colaboradores em ordem e injeta referências já existentes, sem callbacks em init ou ciclos. Os filtros de apply/reload/append continuam sendo lidos no momento original dentro da coroutine. Interfaces de Domain, queries, APIs, persistência, UI, navegação e StateFlows são preservados. A configuração de cobertura inclui as seis classes novas de forma aditiva; nenhum padrão ou gate antigo é removido.

## Composição visual da Home clássica — 19/10/2026

HomeLegacyContent mantém Scaffold/BoxWithConstraints, tratamento vazio, key do tamanho da lista, rememberPagerState infinito, modalidade selecionada, LaunchedEffect da modalidade solicitada e cálculos de largura/padding. HomeLegacyPresentation e HomeLegacyActions mantêm a interface declarada. Os novos Composables recebem valores/callbacks e não criam estado, scope, Flow ou acesso a dados.

- HomeLegacyTopBar: menu, contador, faixa de campanha e trevo com a mesma rotação.
- HomeLegacyCarousel: Box com offset e HorizontalPager, mesmas restrições de scroll, página fixa, espaçamento, padding e condição do rodapé.
- HomeLegacyCards: chamada de LotteryCard com escala pela distância e ações bolão/aposta, preservando seleção e habilitação.
- HomeLegacyQuickAccess e HomeLegacyCampaign: transições e callbacks originais.
- HomeLegacyCartSummary: regra de visibilidade e CaixaDSCartSummary original.

Os atalhos, campanha, ações dos cartões e resumo usam o BoxScope original, sem novos nós de layout. A ordem continua atalhos → campanha → Box do carrossel; dentro dele, pager → ações condicionais → resumo. Os tempos/easings/traduções e o proprietário das animações externas permanecem iguais. Não há alteração de ViewModel, StateFlow, navegação, Domain/Data, rede ou persistência.

## Histórico: organização MVVM por feature — 19/10/2026

As 14 features possuem seus pacotes `presentation`, separados em `viewmodel`, `state`, `coordinator`, `mapper`, `route`, `screen`, `components` e `style` onde há responsabilidade correspondente. ViewModels conservam a propriedade de seus StateFlows; coordenadores recebem os mesmos estados, escopos e dispatchers. Componentes visuais recebem valores/callbacks, e mapeadores não coordenam operações de tela. Estados e efeitos foram separados dos arquivos de ViewModel. Sessenta e cinco auxiliares operacionais foram classificados pela implementação, em vez de permanecerem em `state` por causa do nome.

O código de domínio pertence fisicamente a `feature/<nome>/src/domain/kotlin`, dados remotos a `src/data/kotlin` e adaptadores Android de persistência a `src/platform/kotlin`. Os módulos existentes `core:domain`, `core:data` e `core:preferences` compilam essas raízes, respectivamente. Nenhuma dessas camadas é compilada também pelo AAR de apresentação. Não foram criados módulos ou dependências externas.

Existem ciclos de contratos de domínio preexistentes conta↔carrinho e apostas↔carrinho. A agregação conserva o grafo compilável e a visibilidade interna, sem declarar isolamento binário de cada domínio. Configuração compartilhada, service/push, transporte, rede, segurança e implementações de autenticação continuam em core. Cada feature tem somente as camadas de que necessita; filtro de marketplace consome contratos de configuração/marketplace compartilhados, sem classes de domínio artificiais. O adaptador Retrofit compartilhado continua implementando os contratos específicos agora pertencentes às features.

A apresentação Home antes em app pertence a `feature/home/src/host/kotlin`, compilada exclusivamente por app. Isso conserva R, flags e integração com a navegação principal; Home AAR não depende do aplicativo. Composição, callbacks do drawer, vínculos de ações e animações estão separados. Os cartões separam faixa, prêmio, contagem regressiva, modalidades especiais e arte, sem novos pais de layout ou mudança de dimensões/temporização.

`HomePreferencesViewModel` mantém um StateFlow próprio para preferências, independente do HomeViewModel existente. Casos de uso coordenam leitura e gravação; comandos encadeados executam em IO na ordem de submissão. Escritas de Shake mantêm aviso→habilitação e leituras de confirmação; ordem de modalidades mantém o mesmo CSV e normalização. As implementações e chaves de SharedPreferences, inclusive commit/apply, permanecem iguais. O carregamento inicial aguarda a leitura e oferece retry em falhas, e operações nativas já iniciadas podem terminar após cancelamento sem publicar estado tardio. A composição conserva o estado visual de confirmação/tooltip e seu intervalo de cinco segundos.

Conta usa capacidades puras `SessionAuthentication`, `BiometricLoginPreferences`, `EndSessionLifecycle` e identidade em Domain. Gateways Android existentes implementam essas portas. Login/biometria entregam operações de troca à ViewModel; logout entrega a resolução do retorno. As operações executam nos escopos e tratamentos originais, antes da verificação cadastral ou consumo de logout. Intents ficam nas rotas. Os consumidores de identidade usam diretamente os contratos de Account; os antigos aliases de `core.auth` foram retirados. A revisão do carrinho local usa casos de uso de consulta/exclusão, conservando identidade capturada, recibo, guardas e ordem de publicação.

Os FQNs mudaram para refletir os novos pacotes. As assinaturas nas fronteiras UI/Android de login/logout e na injeção da revisão local foram adaptadas com seus consumidores; não se declara compatibilidade binária com os pacotes antigos. As cinco fachadas de extração isolada mantêm a API compilada pública. Payloads, enumerações de transporte, URLs, identificadores, recursos, preferências e StateFlows existentes são preservados.

Os gates usam as raízes reais de compilação e verificam também os fontes relocados. Fixtures negativas exercitam o Gradle real para rejeitar Android em Domain e Activity/Java/cor literal em Home host. Cobertura mantém mínimos e padrões anteriores, adicionando somente colaboradores extraídos. Decisões: `docs/superpowers/specs/2026-10-19-feature-mvvm-design.md`; validação e limites em MIGRATION_VALIDATION.md.


## Histórico: revisão final transversal — 20/10/2026

A revisão completa de APP, CORE e das 14 features eliminou as fachadas restantes de configuração, sessão, rede, preferências de Home, navegação, fila e Firebase. `AppNavHost`, os módulos Hilt efetivos e o adapter de Core Security são consumidos diretamente. `ProcessEnvironmentSettings` é a única instância injetada/singleton de configuração de ambiente no APP, mantendo snapshot, chave, confirmação de persistência e seleção para o próximo lançamento. O registro tipado de grafos é final; não há subclasse pública de compatibilidade. O inventário do APP aceita somente as três entradas Android existentes e o gate rejeita a restauração das superfícies aposentadas mesmo com arquivo renomeado.

Foram retirados os dois casos de uso de ambiente sem consumidor de produção, a interface `Mapper` sem utilização, componentes/helpers visuais sem chamadas e a exposição desnecessária do store de sessão na interface de navegação de Account. O teste de falha de commit foi preservado junto a `EnvironmentPreferences` no módulo Datastore, exercitando a implementação efetiva. Componentes visuais ativos, factories por destino, adapters de sessão/JWT, projeções HTTP com seleção de cliente, contratos de transporte e agregadores de cobertura permanecem por possuírem responsabilidade real.

As dependências herdadas sem uso foram retiradas dos seus donos; Notifications Domain recebe identidade diretamente de Account API. Imports explícitos sem referência no corpo foram removidos, preservando imports de operadores e wildcards. Nenhuma regra, URL, payload, chave, rota, escopo Hilt ou limiar de cobertura foi redesenhado. As seções datadas anteriores registram as extrações daquela rodada; esta revisão e a visão inicial descrevem o estado consolidado. Resultados finais em `MIGRATION_VALIDATION.md`.

## Contrato MVVM e nomes de implementação — 21/10/2026

As 14 features de apresentação usam Model, View e ViewModel, com casos de uso, Repository e DataSource conforme sua responsabilidade. Uma feature pode reutilizar operações e contratos existentes; não é exigido um módulo ou encaminhamento vazio por camada. Implementações de Repository e DataSource terminam em `Impl`, conforme escolha explícita do usuário. Portas ficam sem esse sufixo.

A regra de remoção de cotas expiradas está no domínio de Cart, com acesso remoto pelo Repository. Pós-login depende de um contrato de DataSource, mantendo operação e escopo de injeção. A proteção estrutural e o inventário das responsabilidades estão em [docs/FEATURE_MVVM_CONTRACT.md](docs/FEATURE_MVVM_CONTRACT.md).

## Organização física dos pacotes — 21/10/2026

Os papéis de Domain estão separados em model, repository, usecase, rule e port; Data separa repository, datasource/local, datasource/remote e mapper. Fontes locais foram extraídas de arquivos de Repository, preservando lógica, contratos e persistência. A organização atual e seu inventário estão em [docs/MVVM_PACKAGES.md](docs/MVVM_PACKAGES.md).

## Pacotes do APP/CORE e fronteira Repository — 21/10/2026

APP conserva entradas Android e composição, agrupando ambiente/runtime/tema por responsabilidade. Os demais módulos CORE separam modelos, contratos, casos de uso, rede, autenticação, DS e apresentação nos pacotes correspondentes; bibliotecas pequenas mantêm capacidades coesas. SessionCartRepositoryImpl, SessionBettingRepositoryImpl e SessionBettingParametersRepositoryImpl e seus testes agora pertencem a Data, conforme a fronteira Clean: Domain declara contratos. Regras R8 explícitas acompanham os novos owners sem relaxar flags. Inventário, árvore e validação em [docs/APP_CORE_PACKAGES.md](docs/APP_CORE_PACKAGES.md).

## Ownership de transporte e limpeza de pacotes — 21/10/2026

Os 18 arquivos de DTOs dos nove módulos transport foram colocados em data.dto de suas features; 41 arquivos de teste também passaram aos namespaces das features correspondentes. Pacotes vazios foram removidos. O grafo de 65 módulos não contém módulo órfão: 64 participam do app em produção e Core Testing atende aos testes. A revisão e suas evidências estão em [docs/UNUSED_PACKAGES_REVIEW.md](docs/UNUSED_PACKAGES_REVIEW.md).

## Verificação automática do contrato - pre-commit de 28/10/2026

O hook nativo e o CI versionado usam `scripts/validate-pre-commit.py` e a tarefa `preCommitCheck`. Os gates de camadas, propriedade de fontes, grafo sem ciclos, App/Feature, Compose e CaixaDS são exigidos junto de Ktlint, Detekt, Lint, testes frescos, cobertura e contratos de plataforma. As fixtures XML operam em cópias temporárias. Divergências entre workspace e index ou falhas mantêm o commit bloqueado.

A última execução completa aprovou 3.510 testes Android/JVM e JaCoCo LINE 95,11%, mas reprovou em Ktlint global; não é aprovação integral. Headers/baselines permanecem intactos. [Novidades e resultados](docs/UPDATES_20261028.md) e [instalação/limites do pre-commit](docs/quality/hooks-and-ci.md) discriminam a evidência e a proteção remota ainda dependente de configuração.
