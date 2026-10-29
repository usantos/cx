# Organização dos pacotes MVVM

A organização física das 14 features foi padronizada em 21/10/2026, preservando os 41 ViewModels e as responsabilidades do [contrato MVVM](FEATURE_MVVM_CONTRACT.md). Os módulos api/domain/data/transport existentes continuam responsáveis pelas mesmas fontes. A estrutura abaixo mostra os papéis possíveis; cada feature mantém somente os pacotes de que precisa.

```text
feature/<feature>/
  domain/.../domain/
    model/              modelos e resultados
    repository/         contratos de Repository
    usecase/            contratos e implementações dos casos de uso
    rule/               políticas e regras puras
    port/               capacidades de sessão, identidade ou localização
  data/.../data/
    repository/         implementações RepositoryImpl
    datasource/
      local/            SQLite, preferências e contratos locais
      remote/           APIs, contratos remotos e DataSourceImpl
      push/             fonte do token Firebase, onde necessária
    mapper/             conversões, parsing e montagem de payloads
  presentation/src/main/.../presentation/
    viewmodel/
    state/
    coordinator/
    mapper/
    route/
    screen/
    components/
  presentation/src/main/.../
    navigation/         integração de destinos da feature
    di/                 composição Hilt e runtimes de aplicação
```

Os contratos dos módulos api usam os mesmos pacotes de papel dos demais contratos da feature, sem mudar de módulo. Marketplace Filter reutiliza domínio existente e não ganhou camadas vazias. Os pacotes pequenos de Core Domain continuam agrupados por capacidade; Core Data passou a separar repository, datasource/remote, mapper e di. Navegação, integrações Android e transporte permanecem em seus agrupamentos específicos.

## Árvore vigente — 22/10/2026

O grafo conserva 65 módulos. Os caminhos acima pertencem aos módulos independentes `feature/<nome>/{presentation,domain,data,api,transport}` declarados em `settings.gradle`; cada feature cria somente as camadas de que precisa. Os módulos API existem em Account/Cart/Marketplace; Transport em nove features. Marketplace Filter possui apenas Presentation. Não há `core:preferences`/`CoreDatastore`: SQLite e preferências ficam em Data do dono; `EnvironmentPreferences` fica em `app/.../environment/persistence`. Core Navigation é Android/Compose, e seus pacotes não representam um domínio JVM puro.

O runtime de push `AndroidPushRuntime` pertence a Notifications `presentation/.../di`; Data fornece token Firebase, canal/mensagens e persistência, sem dependência de `RegisterPushDevice`. Home Domain implementa `SelectHomeResaleOffersUseCase` com `ServerTimeSource` para selecionar as ofertas de revenda elegíveis; o estado e o mapper recebem `resaleOffers`, sem relógio local no mapper. Retail Domain concentra a política de país, enquanto o geocoder Android fica em Data. A apresentação de cadastro Account invalida respostas de consultas de CEP substituídas antes de atualizar endereço ou erro.

A revisão de cobertura identificou arquivos `build/jacoco/testDebugUnitTest.exec` antigos enquanto AGP 9 gravava a execução atual em `build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec`. Os relatórios agora obtêm, de forma tardia, o `destinationFile` da `JacocoTaskExtension` da tarefa `Test` efetiva, inclusive `app:testPrdDebugUnitTest`, sem unir o caminho Android obsoleto. As coletas instrumentadas `.ec`, produtores, seletores de classes, exclusões e mínimos permanecem iguais. Relatórios históricos que leram dados antigos não comprovam cobertura das classes atuais; a nova matriz precisa confirmar os resultados com os destinos corrigidos.

As contagens de movimentos, evidências e validações abaixo são registros da reorganização de 21/10/2026. Não comprovam a execução das alterações de 22/10/2026.

## Histórico da reorganização — 21/10/2026

Foram movidos 397 arquivos e extraídos dez arquivos de cinco originais, resultando em407 destinos. As fontes Onboarding, Session, RecentPurchase, FavoriteCartUsage e CartSnapshot agora têm arquivos próprios; o parser do carrinho local fica em LocalCartMapper. Quatro implementações privadas passaram a internal para serem usadas por outro arquivo do mesmo módulo. Nomes, construtores, corpos, literais e escopos Hilt foram preservados; não há aliases ou fachadas de compatibilidade.

As referências e imports foram atualizados nos consumidores e testes. O mapa de407 destinos e o inventário corrente estão em `build/verification/mvvm-packages-20261021/package-moves.json` e `inventory-after.json`. A revisão independente confirmou a equivalência dos corpos, a recomposição das cinco extrações e a correspondência entre diretórios e pacotes.

## Validação

Gates de arquitetura e formatação,98 testes de políticas, montagem debug e compilação AndroidTest de PRD/TQS/Automação passaram. A comparação inicial das49 configurações de cobertura preserva regras, mínimos, fontes e quatro listas de pré-requisitos; nenhuma classe selecionada perdeu cobertura após mapear sua nova localização. A classe de metadados gerada para a typealias CartSnapshotRow foi acrescentada à seleção de persistência.

A matriz completa `ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain` passou em **22min07s**, com3.676 tarefas (1.144 executadas,20 de cache e2.512 atualizadas). Foram aprovados **2.103 testes unitários** e **196 execuções instrumentadas**, sem falhas ou erros. Há três skips de perfil de tela, os mesmos casos já aprovados no perfil necessário na [rodada KISS anterior](KISS_MODULE_REVIEW.md); eles não foram reexecutados nesta reorganização sem alteração visual.

Os três releases com R8 foram gerados; IDs e hashes dos APKs foram conferidos. A compilação dos testes Android do app nos três flavors já havia passado sobre os mesmos fontes. Os2.236 hashes de inputs ficaram inalterados durante a validação.

A comparação final dos49 gates confirma as mesmas regras/mínimos, fontes e quatro listas de pré-requisitos, sem perder nenhuma classe selecionada após mapear os pacotes. Apenas a classe de metadados da typealias foi acrescentada. As contagens de linhas permaneceram iguais à matriz anterior: persistência526/532 (98,87%), Core Domain1.769/1.829 (96,72%), Core Data2.527/2.658 (95,07%) e Core Network724/731 (99,04%).

A revisão independente terminou sem achados concretos pendentes. Evidências em `build/verification/mvvm-packages-20261021/`: mapa de movimentos/FQNs, snapshots, inventário, revisão, logs, XML,20 relatórios de cobertura, comparação de seletores e metadados dos APKs. Sem commit, push ou publicação.

A organização de APP/CORE e os Repositories de sessão agora em Data estão descritos em [APP_CORE_PACKAGES.md](APP_CORE_PACKAGES.md). As seções datadas anteriores registram a validação daquela etapa.

A revisão posterior de ownership moveu os DTOs dos módulos transport para `feature.<feature>.data.dto`, corrigiu os namespaces antigos dos testes e removeu pacotes vazios. Detalhes em [UNUSED_PACKAGES_REVIEW.md](UNUSED_PACKAGES_REVIEW.md).

A validação de 22/10/2026, incluindo os destinos atuais do JaCoCo, resultados e testes condicionais, está em [FEATURE_MVVM_CONTRACT.md](FEATURE_MVVM_CONTRACT.md#validação-das-correções--22102026).
