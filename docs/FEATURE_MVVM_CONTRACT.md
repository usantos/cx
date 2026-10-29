# MVVM das features

## Contrato vigente — auditoria de 28/10/2026

A arquitetura mantém MVVM, Clean Architecture e ReduxKotlin nas 14 features. O inventário atual distingue **68 módulos compiláveis e 17 agrupadores**, **46 ViewModels de features**, **45 bindings Redux** e **72 arquivos de reducers**. Support delega o binding ao store proprietário; AccountStatus coordena somente efeitos. O [relatório completo](architecture/FULL_AUDIT_20261028.md) registra os desvios corrigidos, evidências e limites. As contagens e aprovações das seções datadas abaixo descrevem suas respectivas rodadas históricas.

Preferências de navegação Betting e recuperação Onboarding pertencem a ViewModels com estado Redux, operações Domain e efeitos com lifecycle. DI somente constrói; Data implementa portas. Marketplace consome a porta de município de Account Domain por um adaptador Data, preservando um único GET de perfil e a validação da identidade. Regras de preços e equivalência de favoritos pertencem a Betting Domain.

Actions capturam mensagens antes do dispatch; reducers não consultam catálogo nem locale global. Quando os argumentos dependem do estado, um template imutável carrega o padrão e o locale explícito. Claims/Jobs privados protegem operações antes do agendamento no dispatcher, mesmo quando outro carregamento altera a flag visual. Resultados de navegação são consumidos antes do callback ao host. Esses proprietários operacionais não duplicam o UiState Redux.

Os verificadores complementam a revisão de fontes/consumidores e os testes de concorrência, replay e navegação. O Ktlint global mantém dívida anterior em arquivos fora do escopo e em header protegido; seu resultado é distinto da formatação dos arquivos alterados. Headers, baselines e critérios JaCoCo permanecem preservados.


## Histórico — acessibilidade e auditoria integral de 27/10/2026

A rodada atual mantém MVVM, Clean Architecture e ReduxKotlin nas 14 features. Acessibilidade e matriz de aceitação estão em [ACCESSIBILITY.md](ACCESSIBILITY.md). Os oito desvios confirmados desta revisão e as fronteiras App/Core estão em [FULL_AUDIT_20261027.md](architecture/FULL_AUDIT_20261027.md). As seções de validação das rodadas anteriores abaixo conservam seu escopo histórico; resultados atuais e limites pertencem ao manifesto `docs/technical-guide/verification.json`.

Home passou a possuir resultados/cache/jobs em seu ViewModel/Redux, sem orquestrar ResultsViewModel na View. Preço/elegibilidade e expiração passaram para Domain. Notifications serializa leitura inicial e mutações com releitura. Tickets usa política Pix de Domain. Support rejeita respostas fora da geração ativa. O resumo compartilhado de carrinho usa DestinationSnapshotViewModel de Core Presentation, com reducer tipado e dono fechado ao sair do destino.

A View mantém semântica, plataforma e interação. Campos conservam nome/erro e ações de edição; seleção publica papel/estado; links delegam a política existente; texto acompanha o sistema e WebView adapta zoom mantendo DOM. Headers e baselines continuam protegidos. Não se convertem repositórios, protocolo de SDK ou metadados de navegação em stores artificiais.


Atualização de 27/10/2026: [ReduxKotlin + MVVM nas 14 features](REDUX_MVVM.md), por solicitação do usuário. Stores locais ao ViewModel usam a ponte compartilhada de Core Presentation; reducers puros ficam em presentation/state de cada feature. Efeitos continuam no ViewModel e regras em Domain; Domain/Data não dependem de Redux.

Correção de ARQ-01/ARQ-02: atualização obrigatória pertence a Onboarding Presentation/Domain/Data, criada pela DI da feature. App não possui ViewModel/cliente Remote Config nem dependência de produção direta em Data. A entrega de comprovantes Retail possui coordenador sem plataforma, bytes privados e metadados Redux; URI, launchers e provedor ficam em Route. Escopo e evidências em [architecture/REVIEW_20261027.md](architecture/REVIEW_20261027.md).

Todas as 14 features de apresentação seguem MVVM. A revisão de 21/10/2026 identificou 41 ViewModels e conferiu as responsabilidades abaixo, incluindo dependências compartilhadas e implementações de acesso a dados.

| Papel | Responsabilidade |
|---|---|
| Model | Modelos de domínio e estados de apresentação. DTOs e detalhes de armazenamento permanecem em Data/transport. |
| View | Compose apresenta estado e encaminha ações ao ViewModel. Foco, teclado, animações, permissões e seleção visual temporária podem ficar na View. |
| ViewModel | Mantém estado, ações e trabalho associado ao ciclo de vida. Usa casos de uso para operações de aplicação; não acessa Repository, DataSource ou implementações de plataforma. |
| UseCase | Executa validação e regras de aplicação no domínio, consumindo contratos de dados quando necessários. Uma ação puramente visual não exige um UseCase. |
| Repository | O contrato define operações de domínio; a implementação de I/O em Data coordena fontes, binding de sessão e mapeamento de dados. Implementações que compõem repositories de sessão ficam em Data e preservam os contratos no domínio. |
| DataSource | Contrato e implementação em Data isolam uma fonte remota/local quando isso tem responsabilidade própria. HTTP, Android e SDKs ficam nas implementações. Um armazenamento local simples pode ser implementado diretamente pelo Repository, sem um encaminhamento vazio. |

Data não importa nem executa UseCases; limita-se aos contratos e adaptadores de dados. Hilt/DI e os adaptadores de composição da navegação ligam contratos e implementações. Eles não transferem regras de negócio para a View. Por escolha explícita do usuário, implementações de Repository e DataSource usam o sufixo `Impl`, como `SupportRepositoryImpl`, `SupportRemoteDataSourceImpl` e `OnboardingRepositoryImpl`. Qualificadores distinguem responsabilidades locais e de sessão. Contratos ficam sem `Impl`; não foram criadas fachadas adicionais.

## Fronteiras vigentes — 22/10/2026

O grafo vigente declara 67 módulos, incluindo o sample independente e `core:persistence`. Os módulos das features são `feature/<nome>/{presentation,domain,data,api,transport}` conforme `settings.gradle`. Presentation existe nas 14 features; Marketplace Filter reutiliza domínio existente. API existe em Account/Cart/Marketplace e Transport em nove features. `core:persistence` implementa os contratos de persistência segura de `core:domain`; schemas e migrações continuam no Data da feature. `EnvironmentPreferences` pertence ao APP. Não há `core:preferences`, `core:database` ou módulo chamado `core:datastore`. `core:navigation` é Android/Compose e usa contratos Domain.

`AndroidPushRuntime`, em Notifications `presentation/di`, executa `RegisterPushDevice` como composição do runtime de aplicação. Coordena o Flow da sessão, lifecycle e token, preservando captura/revalidação de identidade e retry em login/resume/rotação. Data oferece apenas token Firebase, mensagens/canal e store. A exceção de composição não permite que Views ou Data chamem UseCases. Presentation fora de DI/navegação também não resolve `Geocoder` nem lê localização com `getLastKnownLocation`, `getCurrentLocation` ou `getFromLocation`; permissões, estado de providers e abertura de configurações continuam na View. O gate reconhece `.domain.<capacidade>.usecase`, como `core.domain.push.usecase.RegisterPushDevice`, com imports diretos, alias, wildcard e nomes qualificados, mesmo sem sufixo `UseCase`.

Na Home, `SelectHomeResaleOffersUseCase` seleciona em Domain as ofertas de revenda elegíveis usando `ServerTimeSource`; Presentation conserva o resultado em `resaleOffers` e o entrega ao mapper, sem relógio local. A política de país para localização Retail pertence a Domain; Data traduz os fatos do geocoder Android. Na edição de CEP, Account descarta a consulta substituída e publica endereço/erro somente para a geração e o valor ainda atuais.

A revisão de cobertura identificou arquivos `build/jacoco/testDebugUnitTest.exec` antigos enquanto AGP 9 gravava a execução atual em `build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec`. Os relatórios agora obtêm, de forma tardia, o `destinationFile` da `JacocoTaskExtension` da tarefa `Test` efetiva, inclusive `app:testPrdDebugUnitTest`, sem unir o caminho Android obsoleto. As coletas instrumentadas `.ec`, produtores, seletores de classes, exclusões e mínimos permanecem iguais. Relatórios históricos que leram dados antigos não comprovam cobertura das classes atuais; os resultados abaixo usam os destinos corrigidos.

Os registros de testes, matrizes e percentuais nas seções históricas abaixo pertencem à revisão de 21/10/2026. Os resultados da revisão de 22/10/2026 estão registrados a seguir.

## Validação das correções — 22/10/2026

Foram corrigidas a política territorial de Retail (Domain e adapter Data), a seleção de revenda da Home com `ServerTimeSource`, as respostas obsoletas de CEP e a composição do runtime de push fora de Data. Seleção, contadores e agendamento de atualização da Home usam o mesmo relógio; o agendamento respeita o fechamento inclusivo. O gate de migração aceita o manifesto de Account exclusivamente com `ACCESS_NETWORK_STATE`, sem componentes ou permissões adicionais.

A validação ocorreu em duas etapas para reutilizar a execução de interface já aprovada, sem repetir testes instrumentados para alterações posteriores restritas a testes HTTP e verificadores:

- Matriz completa `modernCheck releaseCheck verifyFeatureModules`, no emulador `SimloAppMvvm20261019`: 289 casos instrumentados aprovados, nenhum erro/falha e 25 condicionais ignorados. Os ignorados dependem do APK nativo de referência ou de outro perfil de tela; não foram contados como aprovados. Esta etapa detectou duas pendências restantes: cobertura de Core Data e o manifesto no gate de migração, corrigidas antes da passagem final.
- Passagem final `modernCheck releaseCheck verifyFeatureModules -x connectedDebugAndroidTest --continue --console=plain`: **BUILD SUCCESSFUL**, em 1min39s, 3.378 tarefas (175 executadas e 3.203 atualizadas). A instrumentação da etapa anterior foi preservada; os hashes dos 3.270 arquivos de fontes/recursos de produção e instrumentação permaneceram iguais até a conclusão.

Os XML das suítes unitárias registram 1.927 casos na seleção canônica (um flavor PRD do APP por módulo) e mais 424 execuções nos flavors TQS/Automação: **2.351 execuções unitárias sem falhas, erros ou skips**. Os 54 testes isolados do verificador de features passaram. Lint, arquitetura, DS, formatação, cobertura e os três releases com R8 passaram na passagem final.

Core Data atingiu **2.656 de 2.777 linhas, 95,64%**, acima do mínimo de 95,01%, com dados atuais e sem reduzir regras/exclusões. Os testes adicionais verificam endpoints/payloads de resultados, catálogos opcionais, remoção de carrinho com a identidade capturada e preservação de erros HTTP na validação/gravação de favoritos. Os erros de lint foram corrigidos com a permissão no módulo proprietário e a API pública `Settings.Global.ANIMATOR_DURATION_SCALE`; não foi usada supressão para ocultá-los.

Não foram realizadas apostas, pagamentos ou reservas reais. Contratos HTTP adicionais usam MockWebServer local.

## Conferência por feature — inventário de 21/10/2026

Cada linha tem Views/Models e os ViewModels indicados. Os contratos abaixo são exemplos dos caminhos verificados; uma implementação pode atender mais de um contrato.

| Feature | ViewModels | Operações e acesso a dados |
|---|---:|---|
| Account | 8 | Casos de uso de cadastro, sessão e conta; AccountRepository/AccountRepositoryImpl, AccountRemoteDataSource/AccountRemoteDataSourceImpl e adaptadores de sessão/notificações. |
| Betting | 6 | Casos de uso de apostas, favoritos e Rapidão; BettingRepository/BettingRepositoryImpl, BettingRemoteDataSource/BettingRemoteDataSourceImpl e fontes especializadas. |
| Cart | 4 | Casos de uso de carrinho, favoritos e expiração; CartRepository/CartRepositoryImpl, ExpiredPoolShareRepository/ExpiredPoolShareRepositoryImpl e os contratos atendidos por CartRemoteDataSourceImpl. |
| Home | 2 | HomePreferencesUseCases, porta HomeLayoutRepository/HomeLayoutRepositoryImpl e ShakeSettingsRepository compartilhado. Conteúdo e ações usam operações dos domínios correspondentes. |
| Marketplace | 2 | Casos de uso de busca, detalhe e compra; MarketplaceRepository/MarketplaceRepositoryImpl e MarketplaceRemoteDataSource/MarketplaceRemoteDataSourceImpl. |
| Marketplace Filter | 1 | Reutiliza GetMarketplaceFilterParametersUseCase, GetBettingParametersUseCase e SearchRetailersUseCase. Não possui fonte de dados própria que justifique outro Repository/DataSource. |
| Notifications | 1 | Casos de uso de listagem, leitura, exclusão e preferências; NotificationRepository/NotificationRepositoryImpl. Integração de push usa infraestrutura compartilhada. |
| Onboarding | 4 | Casos de uso de introdução, tutorial e startup; OnboardingRepository/OnboardingRepositoryImpl e capacidades compartilhadas de configuração/fila. |
| Payment | 4 | Casos de uso de métodos, Pix, cartões e compra; PaymentRepository/PaymentRepositoryImpl e PaymentRemoteDataSource/PaymentRemoteDataSourceImpl. |
| Responsible Gaming | 1 | Casos de uso de avaliação, suspensão e transferências; ResponsibleGamingRepository/ResponsibleGamingRepositoryImpl e ResponsibleGamingRemoteDataSource/ResponsibleGamingRemoteDataSourceImpl. |
| Results | 2 | Casos de uso de resultados/concursos; ResultsRepository/ResultsRepositoryImpl e ResultsRemoteDataSource/ResultsRemoteDataSourceImpl. |
| Retail | 3 | Casos de uso de busca, localização e resgate; RetailRepository e PrizeRedemptionRepository implementados por RetailRepositoryImpl; fontes de busca/resgate atendidas por RetailRemoteDataSourceImpl. |
| Support | 1 | Casos de uso de FAQ, termos e aceite; SupportRepository/SupportRepositoryImpl e SupportRemoteDataSource/SupportRemoteDataSourceImpl. |
| Tickets | 2 | Casos de uso de bilhetes, compras e comprovantes; TicketsRepository/TicketsRepositoryImpl e TicketsRemoteDataSource/TicketsRemoteDataSourceImpl. |

O inventário com nomes e caminhos está em `build/verification/feature-mvvm-contract-20261021/inventory-after.json`. Os módulos domain, api, data e transport são partes dessas features; não precisam de Views ou ViewModels próprios.

## Histórico: correções realizadas — 21/10/2026

A remoção automática de cotas vencidas implementava um UseCase dentro de Data e validava a reserva ali. Agora `RemoveExpiredPoolShare`, no domínio, valida tipo e presença da reserva e mantém o mesmo `AppResult`. `ExpiredPoolShareRepository` define o acesso; `ExpiredPoolShareRepositoryImpl` usa a identidade recebida para o binding, chama `BoundCartRemovalRemoteDataSource` e converte a resposta.

O contrato público `RemoveExpiredPoolShareUseCase` e o FQN de `BoundCartRemovalRemoteDataSource` foram preservados. A ordem validação → binding da identidade → remoção → conversão, a mensagem de validação, os erros e o cancelamento permanecem iguais. Não houve alteração de endpoint, payload, chave persistida ou configuração visual.

O pós-login também passou a expor `PostLoginAccountDataSource` como contrato. `PostLoginAccountDataSourceImpl` o implementa e `PostLoginAccountRepositoryImpl` recebe a interface; o binding Hilt preserva o mesmo escopo Singleton. Um teste verifica a identidade encaminhada e a rejeição de uma sessão obsoleta antes do acesso à fonte.

A padronização abrangeu 60 implementações e 10 contratos relacionados, com atualização dos consumidores, Hilt, testes e seletores JaCoCo. A comparação dos 8.022 literais Kotlin de produção confirmou que mensagens, payloads e chaves persistidas foram preservados.

## Histórico: proteção e validação — 21/10/2026

O gate rejeita Repository/DataSource em presentation; UseCases ou suas implementações nas Views; Compose/plataforma em ViewModels; dependências de Presentation/UseCase em Data; e DataSource em Domain. Reconhece aliases de ViewModel/Composable, implementações de UseCase em Domain e imports qualificados/wildcard. As regras são verificações estruturais e são complementadas pela revisão dos fluxos e testes de comportamento.

Os gates também rejeitam implementações de Repository/DataSource sem `Impl` e contratos com esse sufixo. As políticas do app reconhecem os novos nomes para impedir a duplicação de bindings. Com casos negativos observados antes das correções, 42 testes do gate de features e 98 testes de políticas no total passaram. Dois testes novos de domínio verificam identidade/reserva válida e rejeição de itens inválidos antes do Repository; o teste de integração verifica também falha no binding sem chamada remota. As asserções anteriores de identidade, reserva e cancelamento foram mantidas.

A validação focada de Account/Cart/Core, os gates de arquitetura/formatação e a montagem debug passaram. A matriz completa `modernCheck releaseCheck --offline --continue --console=plain` passou em **25min33s**, com 3.676 tarefas (1.055 executadas, 20 de cache e 2.601 atualizadas). Foram aprovados **2.103 testes unitários** e **196 execuções instrumentadas**, sem falhas ou erros. Os três skips são testes condicionados a outro perfil de tela, já aprovados na [rodada KISS anterior](KISS_MODULE_REVIEW.md); não foram reexecutados nesta rodada, que não altera UI. Os XML desta matriz preservam esses skips explicitamente.

A compilação AndroidTest de PRD, TQS e Automação passou em 25s. Os três releases com R8 foram gerados, com applicationIds e hashes dos APKs conferidos. Os 2.226 hashes de inputs permaneceram inalterados durante toda a validação.

Os 49 conjuntos de regras e mínimos JaCoCo, suas raízes de fontes e quatro listas de pré-requisitos foram preservados. A normalização dos nomes confirma que nenhuma classe selecionada antes da padronização perdeu cobertura; o novo contrato de pós-login foi incluído. Na separação de Cart, as classes da regra foram substituídas pelo UseCase em Domain e pelo Repository em Data, ambos nos gates existentes. Cobertura de linhas: persistência 526/532 (98,87%), Core Domain 1.769/1.829 (96,72%), Core Data 2.527/2.658 (95,07%) e Core Network 724/731 (99,04%).

A revisão independente não deixou achados concretos pendentes. Evidências em `build/verification/feature-mvvm-contract-20261021/`: inventário, mapa de renomeações, snapshots, logs RED/GREEN, XML de testes, 20 relatórios de cobertura, comparações dos seletores e metadados dos APKs. O log anterior à escolha de `Impl` foi interrompido e não é contabilizado como aprovação. Sem commit, push ou publicação.

## Organização física dos pacotes — 21/10/2026

Os papéis de Domain estão separados em model, repository, usecase, rule e port; Data separa repository, datasource/local, datasource/remote e mapper. Fontes locais foram extraídas de arquivos de Repository, preservando lógica, contratos e persistência. A organização atual e seu inventário estão em [MVVM_PACKAGES.md](MVVM_PACKAGES.md).

A organização de APP/CORE e a migração dos três RepositoryImpl de sessão para Data estão em [APP_CORE_PACKAGES.md](APP_CORE_PACKAGES.md).

A revisão posterior de ownership moveu os DTOs dos módulos transport para `feature.<feature>.data.dto`, corrigiu os namespaces antigos dos testes e removeu pacotes vazios. Detalhes em [UNUSED_PACKAGES_REVIEW.md](UNUSED_PACKAGES_REVIEW.md).

## Verificação automática do contrato - pre-commit de 28/10/2026

O hook nativo e o CI versionado usam `scripts/validate-pre-commit.py` e a tarefa `preCommitCheck`. Os gates de camadas, propriedade de fontes, grafo sem ciclos, App/Feature, Compose e CaixaDS são exigidos junto de Ktlint, Detekt, Lint, testes frescos, cobertura e contratos de plataforma. As fixtures XML operam em cópias temporárias. Divergências entre workspace e index ou falhas mantêm o commit bloqueado.

A última execução completa aprovou 3.510 testes Android/JVM e JaCoCo LINE 95,11%, mas reprovou em Ktlint global; não é aprovação integral. Headers/baselines permanecem intactos. [Novidades e resultados](UPDATES_20261028.md) e [instalação/limites do pre-commit](quality/hooks-and-ci.md) discriminam a evidência e a proteção remota ainda dependente de configuração.
