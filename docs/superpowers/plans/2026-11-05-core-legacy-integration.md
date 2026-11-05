# Integração do Core no app legado — plano de implementação

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.
> **Execução solicitada:** equipe humana de sete pessoas: você e mais seis devs. Os responsáveis abaixo são pessoas da equipe; este documento não inicia implementação nem delegação a agentes.

**Goal:** permitir que o app legado consuma o Core independente, preservando seus fluxos e adotando as capacidades gradualmente, sem importar funcionalidades do app novo ou criar ciclos.

**Architecture:** importar os módulos necessários do Core a partir de uma versão fixa e criar um módulo Android `:legacy-core-bridge` para composição e adaptação. O app implementa os adaptadores que conhecem suas classes antigas; a ponte recebe interfaces ou callbacks, e o Core permanece sem conhecimento do legado. Uma única implementação é escolhida para cada operação; a propriedade da sessão é fixada durante sua vida.

**Tech Stack:** Kotlin/Java, Gradle, Android, coroutines, Retrofit/OkHttp, AppAuth e persistência segura existentes no Core. Compose é necessário para capacidades visuais selecionadas; Hilt não é imposto ao app legado.

**Spec:** [Core independente das funcionalidades](../../CORE_STANDALONE_INTEGRATION.md), versão de referência `fc28da57e`, e pedido de integração com divisão entre sete devs. As escolhas de ponte e implantação gradual são propostas deste plano.

## Restrições globais

- Direção permitida: `app legado → legacy-core-bridge → core`; dependências diretas do app para contratos Core podem ser explícitas. Core não depende de app, ponte ou `feature:*`.
- A ponte não importa classes do módulo `:app`. Adaptadores que precisam de classes antigas ficam no app e implementam contratos fornecidos à ponte.
- Não copiar funcionalidades, ViewModels ou telas do app novo para satisfazer dependências.
- Preservar os campos JSON, headers, verbos HTTP, semântica de erros, identificadores e contratos de persistência do fluxo adotado.
- Não manter dois renovadores de token, duas sessões ativas para o mesmo fluxo ou duas gravações da mesma operação.
- Flags selecionam implementações; não desativam validação de certificado, assinatura, identidade ou autorização.
- Preservar as telas e a navegação antigas na primeira entrega. Redux/Compose não são pré-requisitos para consumir rede, domínio ou persistência.
- PRD, TQS e Automação precisam de mapeamento explícito para a configuração real do legado. Não escolher endpoints por conveniência ou usar PRD como valor padrão de testes.
- Testes locais e a execução global são na JVM, sem aparelho/emulador. Cenários de sistema não se tornam testes aprovados apenas por terem uma simulação JVM.
- A referência atual usa Gradle 8.14.6, AGP 8.13.0, Kotlin 2.2.21, KSP 2.3.6, JDK/target Java 21 e compileSdk 36. Há bibliotecas com minSdk 23 e outras com 24; importar módulos que exigem 24 requer resolver essa compatibilidade no marco C0, sem `overrideLibrary` para esconder a diferença.
- Não elevar o minSdk do legado silenciosamente: preservar os dispositivos suportados, adaptar os componentes necessários com validação ou registrar a capacidade como adiada até a decisão de produto.
- Não alterar essas versões para “a mais recente” durante a integração. Registrar qualquer alinhamento necessário com a versão efetiva do legado.

## Foco de revisão

| Condição | Comportamento esperado | Tarefa que comprova |
|---|---|---|
| Refresh termina depois de logout ou troca de conta | Não restaura a sessão anterior nem associa uma resposta ao novo usuário | C3 |
| Processo termina entre gravar, verificar e limpar uma migração | Retomada sem perda de dados e sem limpeza prematura da origem | C5 |
| Flag muda durante compra, pagamento ou outra escrita | Não dispara a mesma operação no outro caminho | C2/C11 |
| Ambiente muda com sessão, cliente ou cache existentes | Não mistura tokens, endpoints, pins ou registros entre ambientes | C1/C3/C5 |
| Mesmo SDK já está presente no legado | Uma única versão efetiva, sem classes duplicadas e com regras de release preservadas | C0/C4/C6 |

## Divisão da equipe

| Responsável | Frente e módulos sob sua responsabilidade | Entrega principal | Revisor principal |
|---|---|---|---|
| **Você** | Integração geral, build, inicialização, `navigation` e `module-delivery` quando aplicáveis | Hospedeiro integrado, grafo acíclico, piloto e implantação | Dev 1 + Dev 6 |
| **Dev 1** | `common`, `domain`, `design-system`, `presentation` | Contratos da ponte, adaptação de erros e suporte visual necessário | Você |
| **Dev 2** | `network`, `data` | Cliente HTTP e consulta piloto com paridade de contrato | Dev 4 |
| **Dev 3** | `auth` | Sessão, login, renovação e logout com proprietário único | Dev 4 |
| **Dev 4** | `security`, `key-management` | TLS, chaves, SDKs de segurança e configuração assinada | Dev 2 + Dev 3 |
| **Dev 5** | `persistence`, `queue` | Migração segura de dados e integração de fila separadas | Dev 3; Dev 4 na fila |
| **Dev 6** | `testing`, testes de integração e CI | Suíte JVM, regressão dos três ambientes e evidências | Você + dono da capacidade |

Cada dev entrega os testes da própria alteração. Dev 6 mantém a infraestrutura e coordena a regressão; não concentra a escrita de todos os testes.

## Alcance da primeira entrega

1. Core importado e compilável sem `feature:*` do app novo.
2. Uma consulta de leitura existente do legado funcionando pelo novo caminho: preferencialmente verificação de versão com `IsVersionSupportedUseCase`.
3. Segurança do novo caminho ativa desde a primeira requisição; autenticação inicialmente pode continuar sob o proprietário legado através de um adaptador.
4. Adoção de autenticação, persistência e fila em entregas próprias, depois do piloto.

`design-system`, `presentation`, `navigation` e `module-delivery` têm responsáveis, mas só entram no aplicativo final quando uma capacidade do legado precisar deles. A disponibilidade dos 14 módulos não exige adicionar bibliotecas sem uso ou criar Dynamic Features no legado.

## Arquivos e contratos propostos

Os caminhos abaixo são **novos caminhos propostos no repositório legado**. A árvore do legado não foi inspecionada nesta tarefa. Em C0, você registra os caminhos reais de seu build, Application/DI, rede, autenticação e armazenamento antes de editar esses pontos.

```text
legacy-core-bridge/
  build.gradle
  src/main/AndroidManifest.xml
  src/main/java/br/gov/caixa/loterias/integration/core/
    CoreIntegrationPolicy.kt
    CoreBootstrapInputs.kt
    network/VersionSupportFactory.kt
    auth/LegacyAuthTokenAdapter.kt
    auth/SessionOwnerRouter.kt
    security/CoreSecurityPreparation.kt
    persistence/LegacyPreferenceMigration.kt
    queue/CoreQueueAdapter.kt
    ui/CoreViewHost.kt
    navigation/CoreNavigationAdapter.kt
  src/test/java/br/gov/caixa/loterias/integration/core/
    ... testes correspondentes às capacidades ...
gradle/core-legacy-tests.gradle
.run/Core_Legado_JVM.run.xml
docs/integration/core-legacy-compatibility.md
docs/integration/core-legacy-validation.md
```

Os arquivos que adaptam serviços e classes concretas antigas ficam no pacote de integração **do app**, com os caminhos exatos registrados em C0. Não mover essas classes antigas para a ponte nem conceder à ponte uma dependência do app.

Contratos iniciais, a serem implementados em C1:

```kotlin
enum class CoreCapability { NETWORK, AUTH, PERSISTENCE, QUEUE, UI, NAVIGATION, MODULE_DELIVERY }
enum class SessionOwner { LEGACY, CORE }

interface CoreIntegrationPolicy {
    fun snapshot(): Set<CoreCapability>
}

class LegacyAuthTokenAdapter(
    private val readToken: () -> String?
) : AuthTokenProvider {
    override fun token(): String? = readToken()
}
```

`AuthTokenProvider` é o contrato existente em `core.network.interceptor`. Para renovação, reutilizar `SessionRefreshHandler.bind(authorization: String): SessionRefreshLease?`, sem inventar um segundo mecanismo paralelo de refresh.

`CoreBootstrapInputs` recebe `EffectiveEnvironment`, `AuthTokenProvider`, `SessionRefreshHandler?`, `PublicKeySet` e `PrepareNetworkSecurityUseCase`. Seu construtor não inicia rede. Em C2, `suspend fun createVersionSupport(inputs: CoreBootstrapInputs): AppResult<IsVersionSupportedUseCase>` prepara a segurança antes de entregar o caso de uso. A preparação pode falhar; não retornar um cliente parcialmente pronto.

## Ordem de execução e trabalho paralelo

| Onda | Trabalhos | Condição de saída |
|---|---|---|
| **0 — base real do legado** | Você executa C0; Dev 6 registra a execução de referência; demais devs inventariam seus pontos de integração | Compatibilidade e caminhos registrados; fluxo piloto definido |
| **1 — contratos e infraestrutura** | Dev 1 executa C1; Dev 6 entrega C9A; Devs 2/3/4/5 trabalham contra contratos e simulações acordados | Ponte compila; contratos congelados para o piloto; testes JVM disponíveis |
| **2 — primeiro fluxo completo** | Dev 4 entrega C4; Dev 2 entrega C2; você liga C10 em TQS | Consulta real do legado usa Core, preservando o contrato e a experiência |
| **3 — adoção por capacidade** | C3 autenticação; C5 persistência; C6 fila; C7/C8 somente se necessários | Cada capacidade tem evidência própria e caminho de recuperação |
| **4 — implantação** | Dev 6 conclui C9B; você executa C11 | Três ambientes validados e liberação controlada |

O caminho crítico inicial é **C0 → C1 + C4 → C2 → C10**, com C9A aprovado antes do piloto. C9B é a consolidação posterior e não bloqueia C9A. Autenticação e migração de dados não precisam bloquear a consulta piloto se esta usar a sessão legada através do adaptador.

Dev 5 executa C5 e C6 em sequência; Dev 1 conclui os contratos de C1 antes de C7.
Você deixa C8 para depois do piloto, quando aplicável. A ativação de autenticação
com credenciais lembradas depende também do aceite do armazenamento usado por
essa capacidade; não habilitar C3 com uma persistência ainda sem validação.

Não há prazo fechado antes de C0: conflitos de SDK, build e formatos de armazenamento determinam o esforço real. Ao concluir C0, cada responsável estima suas tarefas e você organiza a capacidade da equipe.

## C0 — compatibilidade e importação mínima — Você

**Arquivos:** criar `docs/integration/core-legacy-compatibility.md`; modificar os arquivos reais de settings, plugins, catálogo de versões e build do app identificados no levantamento.

**Consome:** Core `fc28da57e`, build e aplicativo legado existentes.
**Produz:** inventário de versões/dependências, caminhos reais dos adaptadores, mapeamento PRD/TQS/Automação e decisão de distribuição.

- [ ] Registrar um build e os testes atuais do legado antes da integração, incluindo falhas já existentes, sem considerá-las novas aprovações.
- [ ] Comparar JDK/bytecode, Gradle/AGP/Kotlin/KSP, SDKs, Compose, coroutines, Retrofit/OkHttp, Gson, DI, AppAuth, Topaz e FilaBRA. Registrar também manifests, providers, recursos e regras R8.
- [ ] Verificar colisões com módulos/pacotes `:core:*` já existentes. Preferir módulos Gradle com código-fonte fixado no legado; se houver colisão ou incompatibilidade de distribuição, você define build composto ou bibliotecas versionadas antes de prosseguir. Não sobrescrever o Core antigo nem trocar versões silenciosamente.
- [ ] Importar primeiro `core:common` e `core:domain`, com suas dependências de build necessárias, e comprovar compilação Debug e Release. Validar desugaring/bytecode; não presumir que uma AAR resolve incompatibilidade de toolchain.
- [ ] Registrar o fluxo GET piloto e seus contratos exatos: entrada, saída, headers e tratamento de erro. Usar verificação de versão se o legado já tiver contrato correspondente.
- [ ] Publicar PR próprio com o inventário e a importação mínima; revisão de Dev 1 e Dev 6.

**Aceite:** legado continua compilando; divergências de versões têm resolução explícita; nenhum módulo `feature:*` novo foi importado; não há cópias duplicadas de SDKs.

## C1 — ponte, contratos e seleção de capacidades — Dev 1

**Arquivos:** criar `legacy-core-bridge/build.gradle`, manifest mínimo, `CoreIntegrationPolicy.kt`, `CoreBootstrapInputs.kt`, `auth/LegacyAuthTokenAdapter.kt` e testes correspondentes. Você faz as alterações finais no settings/build do hospedeiro.

**Consome:** inventário C0 e os contratos Core indicados acima.
**Produz:** ponte compilável, política com todas as capacidades inicialmente desabilitadas e entradas explícitas para a composição de rede.

- [ ] Escrever e executar testes que falham sem a ponte: capacidades vazias por padrão; token retornado pelo callback do legado sem cópia persistente; configuração de ambiente entregue sem alteração.
- [ ] Implementar os contratos propostos, sem incluir tipos do app nas assinaturas da ponte e sem efeitos no construtor.
- [ ] Adaptar `AppResult`/erros na fronteira com o legado, preservando mensagens, cancelamentos e distinção entre erro de transporte e de negócio.
- [ ] Testar também duas configurações de ambiente e duas instâncias de sessão: nenhuma reutilização acidental de cliente/token entre elas.
- [ ] Executar testes da ponte e a verificação de grafo. Submeter PR; revisão sua antes de liberar C2/C3/C5.

**Aceite:** `app → ponte → core`, sem seta inversa; testes passam com dependências simuladas; habilitar uma capacidade não habilita as demais.

## C2 — rede, dados e consulta piloto — Dev 2

**Arquivos:** criar `network/VersionSupportFactory.kt` e `VersionSupportContractTest.kt` na ponte; modificar o adaptador da consulta antiga identificado em C0, dentro do app.

**Consome:** C1, preparação segura de C4, `NetworkClientFactory`, `LoteriasApiProvider`, `AppConfigurationRepositoryImpl` e `IsVersionSupported`.
**Produz:** fábrica `createVersionSupport` e substituição selecionável de um único fluxo GET.

- [ ] Com MockWebServer, criar testes de paridade da consulta: método, caminho, headers, codificação, sucesso, erro de negócio, 401 e indisponibilidade. Executar e observar a falha antes de conectar a implementação.
- [ ] Implementar a fábrica: aguardar `PrepareNetworkSecurityUseCase`; somente em sucesso criar cliente/provider/data source/repository/caso de uso. Reutilizar a DI existente ou composição explícita.
- [ ] Ligar uma única chamada antiga ao adaptador. Flag desligada mantém o caminho legado; ligada seleciona Core antes de iniciar a operação.
- [ ] Testar que requisições concorrentes não duplicam Authorization e que mudança de flag durante uma escrita não envia nova requisição por outro cliente. Qualquer ampliação para POST é uma tarefa posterior com reconciliação e idempotência existentes documentadas.
- [ ] Validar a consulta em TQS com o Dev 6, registrar a comparação de contratos e publicar PR revisado pelo Dev 4.

**Aceite:** consulta piloto funcional, sem alteração de UI, sem segunda requisição automática pelo outro caminho e com preparação de segurança obrigatória.

## C3 — autenticação e proprietário de sessão — Dev 3

**Arquivos:** criar `auth/SessionOwnerRouter.kt` e `SessionOwnershipIntegrationTest.kt` na ponte; modificar os adaptadores de login, refresh e logout do app identificados em C0.

**Consome:** C1, rede C2 e segurança C4; armazenamento validado de C5 quando houver credenciais lembradas; gateways públicos de `core:auth`, `SessionTokenStore` e contratos compartilhados de identidade.
**Produz:** proprietário `LEGACY` ou `CORE` fixado ao iniciar a sessão e implementações de token/refresh coerentes com esse proprietário.

- [ ] Criar testes falhando para: chamadas concorrentes com 401; logout durante refresh; troca de conta durante resposta atrasada; sessão antiga após troca de ambiente; recriação do processo preservando o proprietário correto, sem restaurar tokens do outro caminho.
- [ ] Manter as sessões existentes sob o proprietário legado. Adotar Core em um próximo login normal elegível; não copiar tokens para dois armazenamentos nem trocar o proprietário de uma sessão em andamento por mudança de flag.
- [ ] Conectar um único mecanismo de renovação por proprietário, usando os contratos públicos. Não tornar métodos internos de `core:auth` públicos para facilitar os adaptadores.
- [ ] Validar logout, limpeza local, cancelamento do navegador, expiração, revogação e troca de conta, incluindo geração/identidade da sessão.
- [ ] Registrar o procedimento de recuperação: desabilitar novos logins Core não autoriza restaurar tokens antigos. Publicar PR com revisão do Dev 4.

**Aceite:** uma origem de verdade por sessão; refresh antigo não restaura credenciais; nenhuma requisição de uma conta usa token da outra.

## C4 — segurança, chaves e SDK homologado — Dev 4

**Arquivos:** criar `security/CoreSecurityPreparation.kt` e testes de TLS/configuração; integrar os scripts e artefatos necessários de `core:security`/`core:key-management` e os adaptadores do app registrados em C0.

**Consome:** C0/C1, `PublicKeySet` e `PrepareNetworkSecurityUseCase`.
**Produz:** preparação segura e chaves verificadas para C2/C3, sem efeitos de inicialização duplicados.

- [ ] Criar testes falhando para configuração ausente/adulterada, host inesperado, certificado não confiável, rotação inválida e mistura de pins entre ambientes.
- [ ] Integrar a configuração e os artefatos homologados mantendo uma única cópia efetiva dos SDKs. Preservar o hardening e verificar hashes/dependências; não contornar conflito removendo proteção.
- [ ] Conectar a inicialização e o lifecycle uma única vez no hospedeiro. Uma falha de preparação impede a nova operação, sem fallback inseguro.
- [ ] Conferir Manifest, regras R8/consumer rules e compilação Release, além dos testes JVM de contratos.
- [ ] Publicar evidências para o aceite de integração de sistema já usado pelo legado; testes JVM não comprovam hardware biométrico, Keystore físico ou comportamento real do provedor SSO. PR revisado pelos Devs 2 e 3.

**Aceite:** segurança obrigatória antes da rede; configuração isolada por ambiente; Debug e Release compilam com uma única versão dos SDKs.

## C5 — persistência e migração — Dev 5

**Arquivos:** criar `persistence/LegacyPreferenceMigration.kt` e `StorageMigrationIntegrationTest.kt` na ponte; implementar leitura/limpeza da origem nos adaptadores do app registrados em C0.

**Consome:** C1/C4, fábrica de armazenamento seguro e `SecurePreferenceMigration`.
**Produz:** armazenamento com namespace por ambiente/usuário e migração versionada dos registros selecionados.

- [ ] Inventariar os dados a migrar, incluindo nomes de arquivos/chaves, formatos, usuários, ambientes e semântica de confirmação da gravação.
- [ ] Criar testes falhando de interrupção em cada etapa: antes da gravação, depois da gravação, depois da verificação e antes da limpeza; incluir registro corrompido, revisão concorrente e recriação do processo.
- [ ] Integrar a migração preservando a ordem gravar → confirmar/reler → limpar. Não executar gravação dupla contínua nem limpar a origem após uma gravação não confirmada.
- [ ] Testar isolamento entre contas/ambientes e a recuperação de cada formato. Definir separadamente a compatibilidade com o binário anterior: a migração que limpa a origem não é reversível apenas desligando uma flag.
- [ ] Publicar PR e ensaio de migração/recuperação revisados pelo Dev 3 antes de habilitar dados reais.

**Aceite:** migração repetível sem perda de dados; uma única origem ativa por registro; rollback de versão tem procedimento compatível com os dados já migrados.

## C6 — fila — Dev 5

**Arquivos:** criar `queue/CoreQueueAdapter.kt` e `QueueAdmissionIntegrationTest.kt`; modificar somente a entrada/lifecycle da fila no hospedeiro identificados em C0.

**Consome:** C1/C4, `FilaBraQueueGateway` e `QueueListener`.
**Produz:** admissão na fila ligada ao fluxo antigo por callbacks explícitos.

- [ ] Criar testes falhando para callbacks repetidos, passagem atrasada após sair da tela, ausência de internet, abandono e evento de origem inválida.
- [ ] Integrar uma única instância/inscrição por proprietário, com liberação no lifecycle e sem iniciar os caminhos antigo e novo simultaneamente.
- [ ] Preservar os identificadores de conta/fila, regras de origem e a versão homologada do SDK; comprovar ausência de classes duplicadas.
- [ ] Validar os cenários no harness JVM e registrar o aceite de sistema da fila na homologação do legado. Publicar PR separado de C5, revisado pelo Dev 4.

**Aceite:** passagem única, eventos antigos ignorados e fila antiga mantida até a capacidade nova estar habilitada.

## C7 — componentes visuais necessários — Dev 1

**Aplicabilidade:** somente quando uma capacidade adotada precisar de UI do Core. Não é uma migração geral de telas.

**Arquivos:** `ui/CoreViewHost.kt`, `CoreViewHostTest.kt` e o Fragment/Activity de entrada definido em C0.

**Consome:** C1, `core:design-system` e `core:presentation`.
**Produz:** integração pontual de componentes Core em View/Fragment, com estado e ciclo de vida explícitos.

- [ ] Testar recriação, entrada/saída de tela, tema e callbacks antes da integração.
- [ ] Hospedar Compose com a estratégia de descarte ligada ao lifecycle do hospedeiro; não adicionar Hilt Application ou outro controlador de navegação para isso.
- [ ] Validar recursos, tema, fonte, acessibilidade e consumo único de eventos. Evitar mudar o tema global do legado para corrigir um componente.
- [ ] Publicar PR com revisão sua, ou registrar que nenhuma UI Core é necessária nesta entrega e não adicionar dependências visuais sem uso.

**Aceite:** capacidade visual funciona sem reescrever as demais telas e sem reter a Activity/Fragment anterior.

## C8 — navegação e entrega de módulos — Você

**Aplicabilidade:** contratos de navegação somente onde necessários. Entrega dinâmica apenas se o legado já precisar dessa capacidade.

**Arquivos:** `navigation/CoreNavigationAdapter.kt`, testes de rotas e os pontos de navegação antigos registrados em C0.

**Consome:** C1/C7 quando houver UI; `core:navigation` e `core:module-delivery` somente conforme a necessidade efetiva.
**Produz:** tradução de contratos em destinos que o legado realmente oferece.

- [ ] Testar ida/volta, recriação, rota desconhecida e sessão substituída.
- [ ] Conectar os contratos às Activities/Fragments existentes, preservando a pilha e o comportamento de voltar.
- [ ] Não registrar classes de entrada, splits ou `feature:*` do app novo. Se o legado for monolítico, manter entrega local e não ativar Play Feature Delivery apenas porque o módulo existe no Core.
- [ ] Publicar PR revisado pelo Dev 1 e Dev 6, ou registrar a capacidade como não aplicável à primeira entrega.

**Aceite:** nenhuma rota inexistente pode ser solicitada e não há instalação de módulo sem consumidor real.

## C9A e C9B — testes, arquitetura e CI — Dev 6

**Arquivos:** criar `gradle/core-legacy-tests.gradle`, `.run/Core_Legado_JVM.run.xml`, harness de testes do hospedeiro e `docs/integration/core-legacy-validation.md`; modificar o pipeline já existente.

**C9A consome:** execução de referência C0 e contratos C1.
**C9A produz:** tarefa global JVM `runAllTests` e pipeline básico aprovado para a ponte e o piloto.
**C9B consome:** C9A e as capacidades selecionadas de C2 a C8, depois do piloto C10.
**C9B produz:** relatório consolidado por ambiente e aceite de regressão para C11.

**C9A — infraestrutura para o piloto:**

- [ ] Registrar os testes locais existentes e os importados do Core. Criar cenários negativos para ciclo entre módulos e para Core → legado/ponte/feature; o pipeline deve rejeitá-los.
- [ ] Configurar a execução do Core e da ponte com um hospedeiro de teste próprio do legado. Não importar MainActivity nem outras implementações do app novo para fazer os testes passarem.
- [ ] Mapear os nomes reais de variantes de C0 para PRD, TQS e Automação. Falhar quando uma suíte obrigatória não existir, em vez de ignorá-la. Bibliotecas sem flavor executam uma vez.
- [ ] Gerar resultados novos dos módulos já integrados, conferir zero testes inesperadamente ignorados, zero tarefas de instalação/dispositivo e relatórios separados por ambiente. Não ligar a suíte local indiscriminadamente a `:core:*:check`: alguns relatórios específicos da origem ainda possuem dependências de testes instrumentados.
- [ ] Validar execução no Windows e no ambiente de CI suportado, incluindo detecção de Python. Publicar a entrada compartilhada do Studio e o pipeline mínimo de testes e qualidade antes de C10.

**C9B — consolidação das capacidades adotadas:**

- [ ] Executar novamente a matriz completa com os testes de todas as capacidades que serão habilitadas, incluindo as condições de concorrência e recuperação de cada frente.
- [ ] Consolidar ktlint, Detekt, Android Lint, compilação Release e cobertura. Manter na origem a verificação Core/aplicativo acima de 95%; no consumidor, medir o código Core efetivamente exercitado e os adaptadores, com critérios registrados antes da implementação. Não atribuir os 95,29% da origem ao legado nem usar esse número para mascarar sua cobertura.
- [ ] Publicar PR e evidências revisados por você e pelo dono de cada capacidade.

**Aceite:** qualquer dev consegue executar a suíte global do legado pela mesma entrada. Os 5.776 testes do app novo são referência da origem, não uma contagem prometida para outro projeto com funcionalidades diferentes.

## C10 — piloto de leitura em TQS — Você, com Devs 2, 4 e 6

**Arquivos:** inicialização/DI e seletor do fluxo piloto no app; `docs/integration/core-legacy-validation.md`; configuração de capacidades usada pelo hospedeiro.

**Consome:** C2, C4 e C9A.
**Produz:** primeira consulta real selecionável entre legado e Core, com evidência de contrato e recuperação de leitura.

- [ ] Integrar o fluxo GET piloto em TQS; registrar contrato esperado, resultado, latência e erros sem tokens ou dados pessoais nos logs.
- [ ] Validar flags desligadas e ligadas, selecionando o caminho antes da chamada. Nenhuma falha envia automaticamente a consulta pelo segundo caminho.
- [ ] Ensaiar o retorno ao caminho antigo para novas leituras e publicar o PR de integração com a versão Core fixada.

**Aceite:** primeira consulta do legado funciona pelo Core e pelo caminho antigo selecionado explicitamente, sem mudança de UI ou alteração do contrato. Este marco libera o planejamento de adoção visual/navegação que realmente se mostrar necessário.

## C11 — recuperação e implantação por capacidade — Você, com os seis devs

**Arquivos:** seletores e inicialização/DI do app; `docs/integration/core-legacy-validation.md`; configuração de capacidades usada pelo hospedeiro.

**Consome:** piloto C10, regressão C9B e aceite individual de cada capacidade que será habilitada.
**Produz:** adoção gradual com medição, recuperação e versão de Core rastreável.

- [ ] Validar flags desligadas e ligadas. Capturar o caminho escolhido no início de cada operação; uma mudança de flag não cancela, repete ou transfere uma escrita em andamento.
- [ ] Para escritas com resultado desconhecido, consultar/reconciliar pelo identificador original antes de qualquer nova tentativa.
- [ ] Ensaiar rollback por capacidade: rede, sessão e dados têm regras diferentes. Não restaurar token antigo nem depender de origem de dados já limpa.
- [ ] Validar os três ambientes com suas configurações, executar o pipeline e liberar para o público de validação definido pela equipe; expandir após os critérios de erro/latência registrados na referência C0.
- [ ] Encerrar cada frente com PR, evidência e runbook. Remover o caminho antigo somente em tarefa posterior, após a adoção estabilizar e a janela de rollback terminar.

**Aceite:** o legado utiliza a capacidade selecionada sem mudar seus fluxos; não há dependência cíclica, sessão duplicada, envio duplicado ou perda de dados; todas as evidências estão associadas à versão efetivamente integrada.

## Regras de colaboração e PRs

- Você controla a integração dos arquivos compartilhados: settings, plugins, catálogo de versões, Application/DI e pipeline. Os devs submetem mudanças nesses pontos por PR coordenado, sem editar todos os mesmos arquivos ao mesmo tempo.
- Cada PR cobre um comportamento verificável e traz problema, mudança, testes, limitações reais e recuperação. A sequência mínima é: contratos → segurança/rede → piloto → capacidades restantes.
- Dentro de cada tarefa de código: escrever o teste que reproduz o comportamento ausente, observar a falha, implementar a menor mudança, executar os testes e criar o commit. Não gerar novas baselines para obter aprovação.
- Manter um quadro com ID, responsável, revisor, dependências, PR e evidência. Usar o CSV de backlog ao lado deste plano para iniciar a distribuição.
- [Backlog para distribuição](2026-11-05-core-legacy-integration-backlog.csv): responsáveis e dependências já preenchidos, sem datas artificiais antes do levantamento C0.
- Alterações no Core compartilhado passam por revisão do dono do módulo e preservam sua execução independente. Ajustes exclusivos do legado pertencem aos seus adaptadores.

## Definição de concluído

- [ ] Core de origem fixada, sem funcionalidades novas importadas e com dependências compatíveis documentadas.
- [ ] App legado Debug/Release compilando e grafo de produção acíclico; Core sem dependência de app/ponte/features.
- [ ] Fluxo piloto adotado com seleção única de implementação e contrato preservado.
- [ ] Cada capacidade habilitada possui dono de estado, testes, evidência dos três ambientes e recuperação ensaiada.
- [ ] Suíte JVM global, análises de qualidade e cobertura exigida aprovadas; nenhum resultado de outro projeto apresentado como validação do legado.
- [ ] Responsáveis e revisores encerraram os PRs e o runbook identifica o que está ativo, legado ou não aplicável.
