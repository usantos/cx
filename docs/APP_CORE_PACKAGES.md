# Pacotes do APP e CORE

A organização de 21/10/2026 completa a [organização das features](MVVM_PACKAGES.md) e segue a separação explicitada pelo usuário: Presentation contém Screen/ViewModel/UiState/UiEvent; Domain contém regras, UseCases, modelos e contratos de Repository; Data implementa Repository/DataSource e adapta APIs, DTOs e persistência. As bibliotecas de infraestrutura são agrupadas por responsabilidade, sem camadas MVVM vazias.

| Módulo | Organização atual |
|---|---|
| app | composition, navigation, di e initialization existentes; environment/persistence e environment/runtime; runtime/host, runtime/session e runtime/fixture; theme/campaign. |
| core:common | result, coroutines, network, time, event. |
| core:domain | configuration/model, repository e usecase; environment/model e usecase; push/repository e usecase; service/model. |
| core:data | repository, datasource/remote, mapper e di, com a coordenação de campanha transferida para Domain. |
| core:auth | gateway, appauth, biometric, token, refresh, authorization, session, foreground e fixture. |
| core:network | api, provider, client, model, error, interceptor, logging, routing, mapper, adapter, mock e config/di existentes. |
| core:design-system | theme, tokens, artwork, icon, layout, html, preview e components (button, input, dialog, cart, navigation onde há conteúdo). |
| core:navigation | destination, state, action, workflow, policy, configuration, fixture. |
| core:presentation | state, formatting, rule, mapper, time, effect, components, campaign/viewmodel e campaign/theme. |
| core:queue | gateway, parser, presentation/components e integração android existente. |
| core:key-management | model, repository, verification. |
| core:security | authentication, keyset, lifecycle, topaz e inspection por variant; os agrupamentos já eram coesos. |
| core:testing | repository e coroutines para fixtures/testes. |

Foram movidos 209 arquivos de origem e extraídos três arquivos de Core Domain: HomeFeatureConfiguration e os contratos PushRegistrationRepository/PushRegistrationLocalRepository. Corpos e literais de produção foram preservados. MainActivity, LoteriasApplication, serviço push, hosts debug e os três owners públicos registrados mantêm FQNs e caminhos. Namespace, applicationId, manifestos, recursos, URLs, chaves e escopos Hilt não mudaram.

As três implementações SessionCartRepositoryImpl, SessionBettingRepositoryImpl e SessionBettingParametersRepositoryImpl saíram de Domain para os módulos Data de Cart/Betting. Os dois arquivos de testes foram movidos junto, mantendo métodos e asserções. A seleção de sete classes compiladas passa dos gates Domain para o gate Data, sem perda global de classes ou redução de mínimos. O Domain passa a rejeitar RepositoryImpl no gate arquitetural.

Nesta etapa, os contratos de transporte das features conservaram os namespaces existentes. A revisão posterior corrigiu esse ownership para `feature.<feature>.data.dto` e removeu o inventário de compatibilidade; veja [UNUSED_PACKAGES_REVIEW.md](UNUSED_PACKAGES_REVIEW.md). Dez regras R8 explícitas tiveram somente os nomes dos owners atualizados para api/model/error. A substituição inversa reproduz exatamente o arquivo original, comprovando que flags e corpos das regras foram preservados.

O inventário de arquivos/pacotes está em `build/verification/app-core-packages-20261021/inventory-after.json`; o mapa de 212 destinos, símbolos e owners JVM está em `package-moves.json`.

## Validação

A compilação debug do app, AndroidTest dos três flavors e de DS/Auth e os testes dos Repositories migrados passaram em 1min49s (998 tarefas). Os 103 testes de políticas passaram, incluindo proteções RED/GREEN para RepositoryImpl em Domain, gateways Auth na DI do app, coordenação de UseCases em Data e acesso a contratos Repository nos ViewModels do Core. A composição de construtores na DI continua permitida. A revisão independente confirmou equivalência dos corpos e recomposição das três extrações; o achado das regras R8 foi corrigido antes da matriz completa. A posterior transferência da coordenação de campanha foi conferida por comparação dos corpos, políticas arquiteturais e testes existentes; não passou por uma segunda revisão independente.

A comparação inicial dos 49 gates mantém regras, mínimos, raízes de fontes e quatro listas de pré-requisitos, com a transferência de sete classes para Data e nenhuma classe medida perdida globalmente. O fluxo de campanha foi também colocado em Domain: CampaignConfigurationUseCases e CampaignConfigurationUseCasesImpl coordenam segurança e atualização; CampaignViewModel consome somente o contrato de aplicação. Dois testes comportamentais adicionais cobrem validação de favoritos para visitante/autenticado e o relógio padrão do cache. Os gates corrigidos de Data/Domain passaram sem reduzir mínimos: Data 2.568/2.699 (95,15%) e Domain 1.732/1.788 (96,87%). Duas classes da coordenação de campanha saem de Data para Domain, além das sete transferidas no sentido inverso.

A matriz final `modernCheck releaseCheck` passou em 20min18s: 3.676 tarefas (968 executadas, 21 de cache e 2.687 atualizadas). Foram aprovados 2.105 testes unitários e 196 execuções instrumentadas, sem falhas ou erros. Os três skips dependem de outro perfil de tela e já passaram no perfil adequado na rodada KISS anterior; não houve alteração visual nesta reorganização. A comparação dos casos confirma que nenhum teste anterior foi perdido, e dois testes comportamentais foram acrescentados.

Os 2.239 hashes de inputs permaneceram idênticos durante a matriz; 1.889 arquivos Kotlin passaram na verificação de formatação. Os 8.022 literais de produção permanecem iguais ao início da tarefa. A tentativa anterior interrompida após reprovar cobertura foi preservada e não é contabilizada como aprovação.

A comparação final dos 49 gates confirmou regras, mínimos, raízes de fontes e quatro listas de pré-requisitos idênticos. Nenhuma classe antes medida foi perdida globalmente: sete classes de Repository passaram de Domain para Data e duas de coordenação de campanha de Data para Domain. Persistência manteve 526/532 linhas (98,87%) e Network 724/731 (99,04%). Foram arquivados os 20 relatórios de cobertura da matriz.

Os releases PRD/TQS/Automação foram gerados com R8, IDs e hashes conferidos. O AndroidTest dos três flavors foi compilado; o app oficial não foi instalado nem instrumentado nesta tarefa. Os testes instrumentados adicionais de Design System (21) e Auth (13) passaram, sem falhas, erros ou skips, em uma execução de 1min59s. Somados à matriz, foram 230 aprovações instrumentadas nesta rodada; os três casos condicionados ao perfil de tela permanecem registrados separadamente.
