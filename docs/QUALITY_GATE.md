# Quality gate Android

Integração dos dois requisitos recebidos em 22/10/2026. O gate mantém a arquitetura,
o código de produção; as versões de build vigentes estão na tabela abaixo.

[Resultados medidos e cobertura por módulo](QUALITY_GATE_RESULTS_20261022.md).

## Ambiente e ferramentas

| Item | Versão / configuração |
|---|---|
| Gradle | 8.14.6 |
| AGP | 8.13.0 |
| Kotlin / Compose compiler | 2.4.10 |
| JVM do Gradle | 21 (validado com Android Studio JBR 21.0.10) |
| Bytecode / toolchain de todos os módulos | 21 / 21 |
| JVM dos testes locais Android | 21, igual aos compiladores, ao Gradle e ao alvo de produção |
| Detekt CLI | 1.23.8 estável |
| Ktlint CLI | 1.8.0, distribuição completa isolada |
| Android Lint | 9.2.1, analisador independente do AGP 8.13.0 |
| Android SDK / Build Tools no CI | 36 / 35.0.0 ([compatibilidade AGP 8.13](https://developer.android.com/build/releases/agp-8-13-0-release-notes)) |
| JaCoCo | 0.8.13, preservado |
| Scripts de verificação | Python 3; hooks POSIX sh, macOS/Linux |

Detekt e Ktlint estão no Version Catalog. As tarefas JavaExec compartilham a
configuração na raiz e analisam todos os arquivos Kotlin em `src` dos módulos,
incluindo testes. Não se adiciona o plugin de formatação do Detekt. Não há alteração
no classpath do compilador do aplicativo.

### Limite de compatibilidade do Detekt

A [matriz oficial](https://detekt.dev/docs/introduction/compatibility/) informa que
Detekt estável 1.23.8 foi construído com Kotlin 2.0.21/Gradle 8.12.1/AGP 8.8.1.
A linha do Detekt com suporte ao Kotlin 2.4 ainda é alpha. Para preservar a exigência de estabilidade
e não alterar o toolchain, usamos a [integração oficial por JavaExec/CLI](https://detekt.dev/docs/1.23.8/gettingstarted/gradletask/),
isolada dos plugins Kotlin/AGP. Na adaptação de 30/10/2026, o parser analisou os 2.659 fontes Kotlin sem erro.
Isso valida a análise sintática deste checkout, não a compatibilidade completa de
resolução de tipos Kotlin 2.4 ou de recursos futuros da linguagem.

`SuspendFunWithCoroutineScopeReceiver` existe e está configurada, mas
[exige resolução de tipos](https://detekt.dev/docs/1.23.8/rules/coroutines/), que
esta integração não fornece. Essa regra permanece uma pendência explícita, sem
alegação de proteção ativa. `GlobalCoroutineUsage` funciona sem essa resolução.
Não se usa um classpath misturado de variantes ou um compilador antigo para
produzir uma falsa aprovação semântica. Adotar resolução completa exige revalidar
uma versão estável compatível, ou uma decisão explícita de aceitar uma alpha.

## Comandos

```sh
# macOS, quando Java 21 ainda não está no PATH:
export JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home'

./gradlew ktlintCheck
./gradlew detekt
./gradlew lintQualityCheck
./gradlew quickQualityCheck
./gradlew allUnitTests
./gradlew verifyCoverage
./gradlew qualityGate
./gradlew preCommitCheck

# Instalação explícita, conservadora, sem substituir hooks existentes:
sh scripts/install-git-hooks.sh
```

`quickQualityCheck` executa Ktlint → Detekt → Lint; não executa testes nem cobertura.
`qualityGate` acrescenta os testes unitários frescos e a verificação global de
cobertura. O pre-commit e o CI usam `scripts/validate-pre-commit.py`, que chama `preCommitCheck`: acrescenta os gates de arquitetura/Compose/CaixaDS/contratos, testes dos scripts e do hook, fixtures XML isoladas, relatório agregado e APK PRD Debug. Testes/analisadores comuns são compartilhados no grafo, sem duplicar as mesmas tarefas. Falhas preservam o código de saída e bloqueiam o commit. Workspace e index precisam corresponder e permanecer estáveis durante a validação; nenhum arquivo é preparado automaticamente.

Exclusões de tarefas (`-x`) são rejeitadas no gate e em `lintQualityCheck`,
inclusive exclusões de análises internas do Lint. O resumo registra PASSED, FAILED ou NOT RUN; uma etapa não executada não é
apresentada como aprovada. Cobertura não medida nessa execução não é substituída
por um relatório antigo. O projeto não habilita configuration cache; o resumo usa
um listener de fim do build e não é compatível com esse recurso experimental aqui.

## Baselines e formatação

As validações comuns não modificam baselines nem fontes. Baselines ausentes de
Detekt/Ktlint causam falha explícita, sem criação automática. Lint só recebe um
caminho ausente durante manutenção explícita. Atualizações de baseline e formatação
não podem fazer parte do grafo de um gate.

```sh
# Manutenção explícita: revisar o diff antes de versionar.
./gradlew detektBaseline
./gradlew ktlintBaseline
./gradlew -PupdateLintBaseline=true lintQualityBaseline

# Só quando se desejar formatar e revisar as alterações:
./gradlew ktlintFormat
```

Ktlint retorna 1 ao encontrar violações durante a criação do baseline. Apenas essa
tarefa de manutenção aceita o retorno 1, exige XML gerado válido e finaliza com
`ktlintCheck`, que deve passar. A validação normal nunca ignora o código de saída.

Dívida registrada nesta integração: Detekt **6.204**, Ktlint **43.375** e Lint
**514** ocorrências. Os baselines não corrigem essa dívida. Não se excluem módulos,
testes, Composables, ViewModels, repositórios ou UseCases para esconder violações.
Somente diretórios de build/gerados/KSP/KAPT ficam fora da análise de fontes.

Detekt configura complexidade, tamanho, parâmetros, números mágicos, membros/
parâmetros privados não usados, retornos, throws, catches, exceções genéricas,
exceções engolidas, coroutines, imports e manutenção. O nome real da regra solicitada
como `ComplexMethod` é `CyclomaticComplexMethod`. Compose usa parâmetros default
ignorados no limite de assinatura e propriedades numéricas permitidas para tokens;
Previews podem ser privados. O limite de linha compartilhado é 140. Ktlint usa
`android_studio` e aceita nomes de funções `@Composable`. Ver
[orientação Compose](https://detekt.dev/docs/1.23.8/introduction/compose/).

As declarações Kotlin multilinha mantêm vírgula final por configuração explícita
(`ij_kotlin_allow_trailing_comma=true`). A regra de formatação continua ativa e
exige essa convenção; isso preserva as assinaturas já usadas pelos fontes e
pelos registros de dívida do Detekt, sem regenerar baselines.

Política Lint: erros e warnings falham, verificações de release permanecem ativas,
testes não são excluídos e cada módulo Android é analisado uma vez no gate. O app
usa PRD Debug e as bibliotecas Debug. O gate não substitui `releaseCheck`; o Lint
vital dos builds release permanece configurado. Ver [política Lint](quality/android-lint.md).

## Cobertura global

A descoberta atual seleciona os **68 módulos compiláveis** do checkout, inclusive
módulos sem testes e o sample de Design System; agrupadores Gradle não entram
nessa contagem. O app usa PRD Debug, bibliotecas Android Debug, módulos JVM main. Não são
combinadas as cópias ASM e pré-ASM das mesmas classes. Fontes Kotlin e Java de
produção entram no denominador. Um módulo com fontes e sem classes compiladas
causa falha; interfaces sem linhas executáveis aparecem como N/A.

O gate usa somente testes unitários JVM/Android. Não mescla `.ec` de dispositivos
antigos; linhas de UI/plataforma não exercitadas pelos testes unitários permanecem
não cobertas. Não exige emulador ou celular e não faz operações no backend. Testes
instrumentados continuam disponíveis nas verificações anteriores do projeto.

Apenas R/BuildConfig, factories/injectors/componentes gerados por Dagger/Hilt,
metadados de agregação e META-INF/module-info são excluídos. Não se excluem modelos,
DTOs escritos à mão, estados, mappers, UI ou regras de negócio. O JaCoCo aplica
seus próprios filtros de bytecode sintético.

Antes do conjunto agregado, removem-se os `.exec` selecionados e relatórios antigos.
Os testes selecionados executam de novo, sem restauração do cache; compilação
continua incremental. O destino real de `Test.jacoco.destinationFile` é resolvido
na execução, inclusive os caminhos do AGP 9. Suítes omitidas/skipadas indevidamente
causam falha. A soma dos contadores por módulo deve coincidir com o agregado.

O cálculo compara inteiros: `linhas_cobertas * 100 > linhas_elegíveis * 95`.
Não faz média de percentuais de módulos; exatamente 95% e valores abaixo não aprovam, independentemente do arredondamento.
O limite não é configurável para baixo e não há baseline de cobertura.

## Relatórios e testes da integração

| Conteúdo | Caminho relativo à raiz |
|---|---|
| Detekt HTML/XML/SARIF | `build/reports/detekt/detekt.*` |
| Ktlint HTML/XML/SARIF | `build/reports/ktlint/ktlint.*` |
| Lint HTML/XML/SARIF | `<módulo>/build/reports/lint-results-<variante>.*` |
| JaCoCo global HTML | `build/reports/jacoco/aggregate/html/index.html` |
| JaCoCo contadores estruturados | `build/reports/jacoco/aggregate/summary.json` |
| JaCoCo global XML/CSV | `build/reports/jacoco/aggregate/jacoco.xml`, `jacoco.csv` |
| JaCoCo por módulo | `build/reports/jacoco/modules/<módulo>/jacoco.xml` |
| Resumo do gate | `build/reports/quality/full-summary.json`, `quick-summary.json` |
| Testes unitários | `<módulo>/build/reports/tests/` |

```sh
python3 scripts/test_jacoco_coverage.py
python3 scripts/test-git-hooks.py
# Sem outro Gradle em execução no checkout; cria/remove um fonte de prova:
python3 scripts/test-static-analysis.py
```

Os testes de scripts usam fixtures para sucesso/falha, arredondamento, ponderação,
módulos sem testes, ausência/inconsistência de relatórios, instalação de hooks,
preservação do staging e códigos de saída. O controle estático executa os dois
analisadores reais contra uma nova violação, sem atualizar baselines. Resultados
sintéticos dessas fixtures não representam cobertura do app.

O [workflow e hook](quality/hooks-and-ci.md) usam o mesmo runner e `preCommitCheck`. CI remoto
não foi executado nesta sessão. Configurar os secrets `SIMLO_*` e tornar o check
**Android Quality Gate** obrigatório na proteção de branches exige configuração no
GitHub; um arquivo de workflow sozinho não impõe essa política de merge.

## Arquivos da integração

- `build.gradle`, `gradle/libs.versions.toml`, `.editorconfig` e `README.md`:
  carregamento das políticas, versões dos analisadores, estilo e documentação.
- `gradle/static-analysis.gradle`, `gradle/android-lint.gradle`,
  `gradle/quality-gate.gradle`: políticas compartilhadas e ordem das etapas.
- `gradle/aggregate-test-reports.gradle`: descoberta global, dados frescos e gate
  ponderado, preservando os comandos agregados de relatórios existentes.
- `config/detekt/detekt.yml`, `config/detekt/baseline.xml`,
  `config/ktlint/baseline.xml`, `config/lint/baselines/**/*.xml` (34 arquivos):
  perfil e dívida estática atual, sem supressão de cobertura.
- `scripts/verify-jacoco-coverage.py`, `scripts/test_jacoco_coverage.py`,
  `scripts/test-static-analysis.py`: verificação e controles de regressão.
- `scripts/git-hooks/pre-commit`, `scripts/validate-pre-commit.py`,
  `scripts/install-git-hooks.sh`, `scripts/test-git-hooks.py`: hook, runner, instalação e testes isolados.
- `.github/workflows/android-quality-gate.yml`: novo workflow, sem substituir
  os três pipelines corporativos existentes.
- `docs/QUALITY_GATE.md`, `docs/QUALITY_GATE_RESULTS_20261022.md`,
  `docs/quality/android-lint.md`, `docs/quality/hooks-and-ci.md` e
  `docs/superpowers/plans/2026-10-22-quality-gate.md`: desenho e evidências.

## Contratos XML de plataforma — auditoria de 28/10/2026

`verifyComposeMigration` usa `gradle/compose-platform-resources.gradle` para validar os recursos atuais por nomes e estruturas explícitos: host Compose debug interno, cinco presets adaptativos, fundos e artes vetoriais do DS, aliases gráficos e defaults Remote Config. O manifesto do oráculo de Cart usa a mesma restrição de query CX de Betting/Onboarding. Nenhum inventário/baseline foi ampliado.

`./gradlew verifyComposePlatformResourcePolicy` exercita casos positivos e mutações negativas usando o mesmo validador do gate, na mesma JVM Gradle. As fixtures usam cópias temporárias; não modificam os recursos do checkout e podem participar do gate completo. O relatório fica em `build/reports/architecture/compose-platform-policy.json`. O script `scripts/check-compose-guardrails.py` conserva os cenários de integração com o gate completo, inclusive recursos extras não aprovados.

## Última execução completa documentada

Em 28/10/2026, `preCommitCheck --continue --console=plain -PnetworkLogging=false` terminou com código 1 exclusivamente por Ktlint: 6.897 ocorrências/363 arquivos. Passaram 3.510 testes Android/JVM, 190 de ferramentas Python, 21 do hook e 33 contratos XML; JaCoCo LINE 95,11% (54.340/57.135). A normalização Kotlin conferiu 2.600 arquivos, sem violações. Os 37 arquivos de configuração/baseline e dois headers protegidos permaneceram intactos.

Proveniência: `build/verification/pre-commit-20261028/full-gate.log`, `final-summary.json` e `docs/technical-guide/pre-commit-summary.json`. A documentação usa essa evidência existente e não representa outro teste. Veja [todas as novidades](UPDATES_20261028.md).

## Identificadores de automação e certificação

`./gradlew verifyAutomationIds` verifica o contrato de IDs Compose e os seletores do [pacote Robot independente](../automation/robot-certificacao/README.md), incluindo controles básicos sem ID. É dependência de `preCommitCheck`, sem rede/farm/credenciais. Não substitui inspeção nativa e execução dos cenários em Android; veja o [contrato](../automation/robot-certificacao/docs/IDS_ANDROID.md).

## Correções de execução e estilo — 29/10/2026

Os testes locais Android usam um executor Java 21, pois as dependências de
inspeção de rede do build Debug contêm bytecode dessa versão. A compilação do
aplicativo e dos módulos JVM agora também usa alvo 21. Uma falha de carregamento de
classe no executor deve ser resolvida no runtime de testes, sem excluir testes,
fontes ou dependências da medição.

A normalização Ktlint mantém a convenção de vírgulas finais nas declarações.
Métodos Compose extensos foram separados em helpers locais, preservando a ordem
dos elementos, modificadores, textos, callbacks e IDs. Os adapters de navegação
agrupam dependências de sessão, operação e runtime em construtores injetáveis;
os contratos usados pelos grafos permanecem os mesmos. Os baselines não foram
regenerados.

O modificador do botão de login ocupa a primeira posição opcional, conforme o
contrato Compose. O consumo de um atalho de resultado informa seu uso ao Android
após a navegação; publicar ou apenas receber um atalho não conta como uso.
Testes de regressão cobrem consumo único, pedido substituído e indisponibilidade
do gerenciador de atalhos.

A cobertura inclui cenários de comportamento para o consentimento da Aposta
Shake, trevos e ganhadores regionais na Home, além dos redirecionamentos reais
de apostas e pagamentos, limpeza do backstack e retomada da sessão vinculada
ao titular. Esses testes exercitam interações e destinos observáveis; não
substituem os testes locais existentes nem a certificação em dispositivo.

## Padronização Java 21 — 29/10/2026

Gradle, toolchains Kotlin/Java, compilação dos módulos Android/JVM e execução de
testes usam JDK 21. O alvo de linguagem e o bytecode também são 21, conforme a
orientação final de padronizar tudo nessa versão. `gradle/jvm-toolchains.gradle`
seleciona os compiladores e executores; os módulos declaram compatibilidade 21.
Nos módulos JVM, `options.release=21` mantém a API Java e o bytecode alinhados.
No Android, `compileOptions` define o alvo que o Kotlin integrado do AGP herda;
o D8/R8 converte as classes para DEX. SDK e minSdk permanecem os mesmos.

O CI instala apenas JDK 21 e o hook encaminha `JAVA_HOME_21_X64` ao Gradle quando
presente. O patch Java de FilaBRA e o compilador do runner legado também geram
classes JVM21; os binários originais de fornecedores não são reescritos no Git.
Os registros históricos de execuções anteriores descrevem o alvo vigente na
respectiva data e não representam validação da nova configuração.

Validação desta padronização em 29/10/2026:

- `./gradlew preCommitCheck --continue --console=plain`: aprovado em 21m27s;
  Ktlint, Detekt, Android Lint, arquitetura, contratos, testes e APK PRD Debug
  aprovados. Os 3.563 testes Android/JVM passaram, sem falhas, erros ou skips;
  o hook passou seus 22 testes isolados.
- JaCoCo LINE: **95,16%**, com 58.164/61.122 linhas cobertas. Nenhuma fonte,
  exclusão, limiar ou baseline foi alterado para obter esse resultado.
- `:app:assembleTqsRelease`: aprovado com R8 e Lint vital de release, fornecendo
  somente a instalação JDK 21 via `org.gradle.java.installations.paths`, com
  detecção e download automáticos de outras instalações desativados.
- Conferência adicional da configuração efetiva: 541 tarefas com compiladores,
  executores e alvos 21. Nos outputs compilados de PRD Debug, TQS Release,
  bibliotecas e testes JVM/Debug, 15.991 classes em 183 diretórios usam major
  version 65 (Java 21).
- O runner de paridade legado compilou e gerou seu APK com JDK 21. Headers
  protegidos e arquivos de configuração/baselines conservaram seus hashes.

Os relatórios do gate permanecem em `build/reports/quality/full-summary.json` e
`build/reports/jacoco/aggregate/`. As evidências acima descrevem compilação e
validação local; não substituem a certificação em dispositivo.

## Compatibilidade Android Studio com AGP 8.13 — 30/10/2026

O build usa AGP 8.13.0, Gradle 8.14.6 e Java 21. Os módulos Android aplicam
`org.jetbrains.kotlin.android` explicitamente, pois Kotlin integrado é um recurso
do AGP 9. O compileSdk/targetSdk é 36, dentro da faixa oficialmente suportada;
as bibliotecas cujo metadata AAR exigia SDK 37/AGP 9 foram substituídas por
versões compatíveis, descritas em [Bibliotecas](LIBRARIES.md).

As tarefas de cobertura leem os diretórios Kotlin do plugin explícito do AGP 8,
sem remover classes, fontes, exclusões existentes ou verificações de integridade.
Headers e baselines permanecem imutáveis. Evidências datadas de 29/10 e anteriores
referem-se à configuração anterior e não validam esta adaptação.

A compatibilidade inclui Kotlin/Compose Compiler 2.4.10 e Hilt 2.58. O leitor
`kotlin-metadata-jvm` dos classpaths Hilt/KSP/annotationProcessor é alinhado em
2.3.21 para ler também metadados 2.4 das dependências. A leitura estrita permanece
ativa, e essa regra não altera o classpath de runtime do aplicativo. Os nomes JVM
dos módulos são derivados do caminho único do projeto, sem caracteres inválidos
para factories ou arquivos no Windows. O Gradle rejeita JDK diferente de 21 no
início da configuração, antes de executar a suíte de validação.

Navigation 2.9.8 recebe as transições padrão de entrada/saída/retorno em `None`;
os parâmetros exclusivos da 2.10 foram retirados da chamada de NavHost. O teste
`AppScreenTransitionTest` continua sendo a verificação de opacidade dos primeiros
frames de abertura e retorno.

Para as capturas de pixels JVM, `core/design-system/src/testSupport` contém uma
ponte test-only com PixelCopy e UiAutomation reais. Evita apenas a espera de
FrameCommitCallback que o Robolectric não implementa, equivalente à
[correção AndroidX](https://android.googlesource.com/platform/frameworks/support/+/bcef06da086866f7389fdb8e8d5a80cff2ea2808).
Os imports explícitos e curingas das cópias locais são adaptados, sem modificar
corpos/asserções ou fontes em dispositivo. As comparações de pixels permanecem
ativas. O suporte é ligado somente a sourceSets.test de módulos Compose, sem
participar do APK ou do denominador de produção.

O módulo Marketplace Filter declara minSdk 24, coerente com Core Presentation
e com o mínimo já exigido pelo app host. Não se usa overrideLibrary para ignorar
contratos de plataforma das dependências.

### Resultado da adaptação de 30/10/2026

- `:app:assembleTqsDebug` e `:app:assembleTqsRelease` foram executados com JDK 21/AGP 8.13.2. O Release final, com Gradle 8.14.6, passou em 10min13s, incluindo R8 e Lint Vital. O APK gerado é **não assinado**: não houve instalação nem certificação no Samsung nesta adaptação.
- `verifyCoverage` passou em execução fresca: LINE **95,16%** (58.162/61.120 linhas; 2.958 não cobertas), estritamente acima de 95%. As exclusões e o limiar permanecem idênticos. A redução de duas linhas elegíveis corresponde à retirada dos dois callbacks exclusivos de Navigation 2.10, sem exclusão de código da medição.
- Inspeção efetiva de JDK: 671 tarefas de compilação/teste/alvo verificadas em Java 21; 16.031 classes em 183 diretórios com major version 65 (Java 21).
- `detekt`, `ktlintCheck` e os testes locais de Onboarding Data, Responsible Gaming Data e Support Data passaram na mesma execução final. A leitura do JSON de teste usa bytes UTF-8, disponível no SDK 36.
- Os filtros existentes de métodos que exigem dispositivo só são ligados a módulos que possuem fontes de testes ou suítes compartilhadas. Módulos sem testes continuam no grafo e no denominador de produção, sem desativar tarefas nem tolerar falhas de seleção.
- A execução completa de `preCommitCheck` encontrou pendências de Lint e **não foi aprovada**. O analisador recomenda versões de bibliotecas que exigem AGP 9/SDK 37, incompatíveis com o AGP 8.13 solicitado; também reportou `OldTargetApi`, `ComposableNaming` e `Recycle` (incluindo consultas que já usam `use`). Não foram adicionadas supressões nem alterados baselines para aceitar esses resultados. Os erros de compilação/configuração de testes e de Detekt daquela execução foram corrigidos e validados separadamente; isso não transforma o gate completo em aprovado.

Os relatórios desta máquina estão em `/Users/ubiracysantos/simlo-artifacts/2026-10-30/agp813/`, fora do versionamento. Naquela execução o gate permaneceu bloqueado. A exceção posterior autorizada e sua verificação estão descritas abaixo.

### Versão exata solicitada para Windows

O AGP vigente é **8.13.0**, com Gradle 8.14.6 e JDK/bytecode 21. Os resultados
acima que identificam AGP 8.13.2 são históricos e não comprovam a versão 8.13.0.
O repositório remoto ainda continha AGP 9.2.1 porque o hook bloqueou o commit da
adaptação pelo Lint; um push sem commit não publica arquivos do staging.
No Android Studio do Windows, selecione JDK 21 em **Settings > Build, Execution,
Deployment > Build Tools > Gradle > Gradle JDK** e use o Gradle Wrapper do projeto.

Validação da versão exata: `:app:assembleTqsDebug` com AGP 8.13.0 e JDK 21
passou em 5min19s (878 tarefas). O metadata do APK confirma `androidGradlePluginVersion=8.13.0`.
Isso comprova a compilação Debug nesta máquina, sem alegar aprovação do gate
completo ou certificação de execução no Windows. Headers e baselines preservados.

### Exceção autorizada às recomendações de atualização — 30/10/2026

O usuário autorizou tornar informativas somente `AndroidGradlePluginVersion`,
`GradleDependency`, `NewerVersionAvailable` e `OldTargetApi`, para manter a linha
AGP **8.13.0**/SDK **36**. Essas recomendações continuam nos relatórios. Não há
mudança de baseline, desativação do Lint nem tolerância a falhas das demais regras.
`abortOnError` e `warningsAsErrors` permanecem ativos.

`verifyLintPolicy`, dependência de `lintQualityCheck`, confere a configuração
real de todos os módulos Android e oito fixtures isoladas: a política válida
passa; desligar o bloqueio de erros, relaxar warnings, acrescentar `NewApi` à
exceção, remover uma recomendação da lista, desativar `Recycle`, ignorar `NewApi`
ou ignorar warnings são recusados. A exceção não pode crescer silenciosamente.

O analisador Lint **9.2.1** é selecionado por `android.experimental.lint.version`,
separadamente do plugin de build AGP **8.13.0**. Essa seleção independente segue o
[mecanismo documentado pelo Android](https://developer.android.com/develop/ui/compose/tooling/lint).
Na comparação com os mesmos fontes, eliminou os falsos alertas `Recycle` em
consultas já fechadas por `use`. As verificações de liberação de recursos continuam
ativas. Os wrappers Compose declaram retorno `Unit` explicitamente, e os contratos
de apresentação de estado expõem `Content`, conforme a convenção de nomes de
funções Compose. Chamadas, parâmetros e desenho das telas foram preservados.

Os resumos de execuções anteriores que falharam acima são históricos. A aprovação
integral depende de executar o pre-commit sobre o conteúdo final preparado.

Validação estática após as correções: `verifyLintPolicy ktlintCheck detekt
lintQualityCheck --continue --console=plain` passou em 3min09s (1.632 tarefas).
A política foi conferida em 36 módulos e oito fixtures. Esse resultado não
substitui os testes e a cobertura: o hook executa o `preCommitCheck` completo
e publica o resultado corrente em `build/reports/quality/full-summary.json`.

### Regressão do primeiro Sync com AARs locais — 30/10/2026

O erro `Null extracted folder for artifact` no FilaBRA era causado pela dependência
local apontando para um AAR gerado por uma tarefa em `app/build`. O modelo do IDE
pode resolver dependências sem executar essa tarefa. A situação foi reproduzida
com um buildDir vazio, sem alterar fontes nem caches do checkout.

FilaBRA e Topaz agora são preparados por [transformações de artefatos Gradle](https://docs.gradle.org/current/userguide/artifact_transforms.html),
executadas na resolução de dependências. Entradas SHA-256 e fontes da correção TLS
participam da validação/cache. O compilador JDK 21 produz a mesma classe TLS que
a tarefa anterior. Classes, recursos e JNI do Topaz preservam a referência
anterior; seu verificador também compara o bytecode alterado com a implementação
Python preservada. Não há invocação Python na preparação dos SDKs durante o Sync.

`:app:verifyVendorArtifactsForSync` é obrigatório no `preCommitCheck`. Confere três
artefatos sem tarefas produtoras, extração de dois AARs pelo AGP e identidade entre
o JAR Topaz compilado e o empacotado. Uma configuração destacada, criada somente
na execução da fixture, exige rejeição de entrada adulterada. O arquivo temporário
é removido em `finally`; a fixture não fica visível ao modelo de dependências.

A regressão passou com todos os buildDirs redirecionados para pastas vazias fora
do checkout, executando somente o verificador, em 8 segundos. Nenhum AAR ou classe
precisou ser gerado nessas pastas. Os verificadores TLS e os 640 controles Topaz
também passaram. Isso valida a resolução/extração usada no Sync nesta máquina;
não equivale a ter executado o Android Studio no Windows remoto. AGP 8.13.0,
JDK/bytecode 21, headers, baselines e critérios JaCoCo permanecem preservados.

### Fontes ignoradas e portabilidade Windows — 30/10/2026

A regra genérica `Icon` do template macOS também ignorava o diretório Kotlin
`icon` quando `core.ignorecase=true`. Os 14 arquivos existiam no computador de
desenvolvimento e participavam do build local, mas não chegavam a novos checkouts.
A regra agora usa `Icon[[:cntrl:]]`, preservando o metadado do Finder e deixando
as fontes do pacote versionáveis. Os 14 fontes foram incluídos sem alterar seu
conteúdo nem os headers protegidos.

`scripts/check-tracked-sources.py` descobre as pastas `src` dos projetos Gradle,
compara os fontes Kotlin/Java físicos com `git ls-files --cached` e rejeita fontes
locais invisíveis ao index. A comparação respeita aliases do mesmo arquivo em
sistemas sem distinção de maiúsculas, mas recusa colisões de nomes no index e
no workspace. Caches em `build` e demais diretórios de ferramentas não entram
na descoberta dos módulos. Não se usa `git status` limpo como única prova.

O runner chama o verificador antes de iniciar Gradle, sem gerar bytecode Python
no workspace durante a importação. A tarefa `verifyTrackedSources` também faz
parte do `preCommitCheck` executado diretamente. Fixtures com Git real cobrem
fontes ignoradas, fontes preparadas, controle do Finder, os dois valores de
`core.ignorecase`, aliases, colisões e bloqueio anterior à chamada do Gradle.

O sample foi compilado e seus dez testes passaram numa cópia exportada somente
do index com cache de build desativado (67 tarefas executadas). O APK dessa cópia
foi aberto no emulador Android 36, incluindo o exemplo `CaixaDSFingerprintIcon`.
Essa evidência valida o conteúdo transferido pelo Git, sem alegar execução no
Windows remoto. A aprovação integral continua dependente do pre-commit final.
