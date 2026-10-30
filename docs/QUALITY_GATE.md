# Quality gate Android

Integração dos dois requisitos recebidos em 22/10/2026. O gate mantém a arquitetura,
o código de produção e as versões de Kotlin, AGP e Gradle existentes.

[Resultados medidos e cobertura por módulo](QUALITY_GATE_RESULTS_20261022.md).

## Ambiente e ferramentas

| Item | Versão / configuração |
|---|---|
| Gradle | 9.4.1 |
| AGP | 9.2.1 |
| Kotlin / Compose compiler | 2.4.10 |
| JVM do Gradle | 21 (validado com Android Studio JBR 21.0.10) |
| Bytecode / toolchain de todos os módulos | 21 / 21 |
| JVM dos testes locais Android | 21, igual aos compiladores, ao Gradle e ao alvo de produção |
| Detekt CLI | 1.23.8 estável |
| Ktlint CLI | 1.8.0, distribuição completa isolada |
| Android Lint | 9.2.1, fornecido pelo AGP |
| Android SDK / Build Tools no CI | 37 / 36.0.0 ([padrão oficial do AGP 9.2](https://developer.android.com/build/releases/agp-9-2-0-release-notes)) |
| JaCoCo | 0.8.13, preservado |
| Scripts de verificação | Python 3; hooks POSIX sh, macOS/Linux |

Detekt e Ktlint estão no Version Catalog. As tarefas JavaExec compartilham a
configuração na raiz e analisam todos os arquivos Kotlin em `src` dos módulos,
incluindo testes. Não se adiciona o plugin de formatação do Detekt. Não há alteração
no classpath do compilador do aplicativo.

### Limite de compatibilidade do Detekt

A [matriz oficial](https://detekt.dev/docs/introduction/compatibility/) informa que
Detekt estável 1.23.8 foi construído com Kotlin 2.0.21/Gradle 8.12.1/AGP 8.8.1.
As versões com Kotlin 2.4 ainda são alpha. Para preservar a exigência de estabilidade
e não alterar o toolchain, usamos a [integração oficial por JavaExec/CLI](https://detekt.dev/docs/1.23.8/gettingstarted/gradletask/),
isolada dos plugins Kotlin/AGP. O parser analisou os 2.061 fontes atuais sem erro.
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
