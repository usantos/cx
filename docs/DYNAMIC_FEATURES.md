# Dynamic Features — entrega híbrida das funcionalidades

A expansão aprovada em 02/11/2026 separa os renderizadores Compose das 14 famílias de funcionalidades em módulos de UI, além do FAQ do piloto. Conta, Home e Jogo responsável são entregues na instalação; os demais splits são solicitados ao acessar uma capacidade habilitada. É um único aplicativo e um único App Bundle.

Core, bootstrap, contratos, Domain, Data, DI, ViewModels, estado Redux e coordenação de navegação permanecem na base. Os renderizadores implementam contratos tipados `*UiEntry`; a base só resolve a implementação permitida pelo registro `AppModule`, após confirmar a instalação. Termos, aceite obrigatório e mecanismos de continuidade permanecem acessíveis na base.

## Módulos e configuração

| Chave JSON | Split | Entrega inicial | Habilitado por padrão |
| --- | --- | --- | --- |
| `account` | `account_ui` | Na instalação | Sim |
| `home` | `home_ui` | Na instalação | Sim |
| `onboarding` | `onboarding_ui` | Sob demanda | Sim |
| `betting` | `betting_ui` | Sob demanda | Sim |
| `marketplace` | `marketplace_ui` | Sob demanda | Sim |
| `marketplace_filter` | `marketplace_filter_ui` | Sob demanda | Sim |
| `cart` | `cart_ui` | Sob demanda | Sim |
| `payment` | `payment_ui` | Sob demanda | Sim |
| `tickets` | `tickets_ui` | Sob demanda | Sim |
| `results` | `results_ui` | Sob demanda | Sim |
| `notifications` | `notifications_ui` | Sob demanda | Sim |
| `responsible_gaming` | `responsible_gaming_ui` | Na instalação | Sim |
| `retail` | `retail_ui` | Sob demanda | Sim |
| `support_help` | `support_ui` | Sob demanda | Sim |
| `support_faq` | `faq` | Sob demanda | Não |

O parâmetro `app_modules` do Firebase Remote Config deve ser configurado com o **tipo JSON** no console. Seu documento padrão completo também está disponível em [`config/firebase/app_modules.json`](../config/firebase/app_modules.json):

```json
{
  "schema_version": 1,
  "modules": {
    "account": {
      "enabled": true
    },
    "home": {
      "enabled": true
    },
    "onboarding": {
      "enabled": true
    },
    "betting": {
      "enabled": true
    },
    "marketplace": {
      "enabled": true
    },
    "marketplace_filter": {
      "enabled": true
    },
    "cart": {
      "enabled": true
    },
    "payment": {
      "enabled": true
    },
    "tickets": {
      "enabled": true
    },
    "results": {
      "enabled": true
    },
    "notifications": {
      "enabled": true
    },
    "responsible_gaming": {
      "enabled": true
    },
    "retail": {
      "enabled": true
    },
    "support_help": {
      "enabled": true
    },
    "support_faq": {
      "enabled": false
    }
  }
}
```

`enabled` controla novas entradas e operações da capacidade. O JSON não define classes, URLs, versões nem código executável. Chaves desconhecidas são ignoradas; módulos ausentes usam o padrão local da tabela, permitindo documentos antigos contendo apenas `support_faq`. Uma publicação inválida ou schema incompatível preserva o último documento válido; um cache durável conserva essa decisão após recriar o processo. Sem documento válido, valem os padrões locais.

A configuração acompanha o listener existente do Firebase e uma única recuperação a cada 30 segundos em primeiro plano. Isso não garante prazo de propagação da rede. A edição deste guia não publica valores no Firebase.

## Configuração no Firebase Remote Config

1. Abra o Remote Config do projeto Firebase correspondente ao ambiente do app.
2. Crie ou edite o parâmetro **`app_modules`** e selecione **Tipo de dados: JSON**.
3. Cole o objeto completo acima como valor padrão, sem envolver o documento em aspas e sem escapar suas aspas internas.
4. Ao atualizar um parâmetro existente, preserve as condições configuradas e o valor vigente de `support_faq.enabled`; o arquivo de referência usa `false` para FAQ.
5. Salve e publique as alterações para disponibilizá-las aos aplicativos.

O tipo JSON faz o Firebase validar a sintaxe antes da publicação. No Android,
o SDK continua fornecendo esse conteúdo por `getString("app_modules")`; o
repositório interpreta e valida o contrato tipado, incluindo `schema_version`
e os booleanos `enabled`. A seleção de JSON no console não exige mudança no
código do app. A validação de sintaxe do Firebase não substitui a validação do
schema feita pelo app. Veja a [documentação oficial de parâmetros do Remote
Config](https://firebase.google.com/docs/remote-config/parameters).

## Entrada, instalação e continuidade

`FeatureModulesModule` compõe a instalação compartilhada e o resolvedor de entradas. `FeatureModuleAdmission` verifica disponibilidade antes de criar o proprietário de uma nova jornada. `FeatureModuleViewModel` coordena estados Redux de instalação por módulo; `PlayModuleInstallationRepositoryImpl` observa sessões reais do SDK, rejeita eventos alheios e compartilha pedidos concorrentes do mesmo split. `FeatureModuleContent` resolve a interface tipada e renderiza o conteúdo real, ou o estado de indisponibilidade/entrega/falha do DS.

| Situação | Comportamento |
| --- | --- |
| Nova entrada desabilitada | Empty State com ação de voltar; sem resolver o renderer nem iniciar instalação. |
| Habilitado, split ausente | Solicitar entrega e apresentar estado de preparação/confirmação. |
| Habilitado, split instalado | Resolver a entrada e renderizar a funcionalidade. |
| Falha na entrega ou resolução | Estado recuperável com tentativa explícita. |
| Saída durante download | O observador é removido; a conclusão não navega nem executa negócio. |
| Desativação durante jornada admitida | Preservar formulários, carrinho e operações já iniciadas conforme o proprietário da jornada. Bloquear novas escritas, inclusive as ainda enfileiradas. |
| Autenticação/aceite obrigatório ou operação em continuidade | Usar evidência do proprietário da sessão/operação, sem tratar apenas uma rota recebida como autorização. |

A admissão de novas escritas é revalidada na fila de IO e depois de leituras/validações suspensas, imediatamente antes de cada novo efeito. Desativar uma capacidade durante uma ação em lote impede as próximas escritas, preservando a que já foi enviada. A suspensão protetiva da conta continua disponível, mas captura o vínculo de identidade antes da fila e usa a mesma sessão no GET de perfil e no POST; respostas de uma sessão substituída não atualizam a tela.

Desabilitar um módulo não apaga carrinho/rascunhos, não desinstala um split e não cancela nem repete POST financeiro. Login, carrinho, comprovantes e recuperação continuam sob os proprietários da base. FAQ mantém seu comportamento específico de substituir conteúdo aberto por Empty State ao ser desabilitado, inclusive dentro da navegação de termos.

Os títulos de distribuição pertencem à base. `dynamic_features_keep.xml` preserva a lista exata dessas strings e o R8 preserva as entradas concretas identificadas no registro. Os contratos de plataforma verificam título, modo de entrega, fusing e ausência de componentes/permissões não aprovados em cada manifest.

## Atualizações e distribuição

O Firebase controla acesso e solicitação de instalação; não publica uma versão nova de código. Uma alteração em qualquer módulo exige compilar e distribuir novo AAB. O Google Play decide os APKs/deltas necessários para o aparelho e os splits instalados; não há garantia de baixar apenas o módulo editado. Um APK universal já contém os renderizadores e não comprova entrega sob demanda. Essa comprovação exige AAB e instalação dos splits, em Play ou no modo local do bundletool.

## Build e qualidade

A entrega mantém AGP 8.13.0, Kotlin 2.2.21, SDK 36 e JDK/JVM 21. Os flavors `prd`, `tqs`, `automacao` e os build types correspondem à base. TQS continua sendo o ambiente padrão de desenvolvimento; os exemplos abaixo escolhem variantes explicitamente.

```sh
./gradlew :feature:support:faq:testTqsDebugUnitTest :feature:support:presentation:testDebugUnitTest :feature:support:data:testDebugUnitTest
./gradlew :app:bundleTqsDebug
./gradlew verifyCleanArchitecture verifyAutomationIds verifyComposePlatformResourcePolicy verifyIncrementalScopePolicy
./gradlew quickQualityCheck
./gradlew preCommitCheck --continue --console=plain
```

Execute uma invocação Gradle por vez. O agregado global usa PRD Debug para base e Dynamic Features; bibliotecas usam Debug. Todas as classes e fontes manuais do split entram no denominador JaCoCo, com LINE global estritamente maior que 95%. Lint executa os modelos e análises próprios do módulo dinâmico (produção/testes),
e a aplicação base publica o relatório integrado; Detekt/Ktlint descobrem seus fontes. A única exceção de dependência para aplicação exige plugin dinâmico e registro no `android.dynamicFeatures` da aplicação base exata. Bibliotecas comuns, ciclos reais e compilação de fontes de outro módulo continuam bloqueados.

## Roteiro local de entrega

Use um emulador de teste dedicado e selecione seu serial explicitamente. Preserve dados e instalações existentes; se houver conflito de assinatura/ambiente, registre o bloqueio e escolha um emulador compatível. Os exemplos não limpam dados nem selecionam um Samsung conectado. Não registre configurações locais, keystores ou segredos em logs.

Com o AAB TQS Debug gerado, ajuste o caminho do bundletool oficial e o serial do emulador. O diretório de evidências fica fora do checkout:

```sh
BUNDLETOOL_JAR=/caminho/para/bundletool-all.jar
FAQ_EMULATOR_SERIAL=emulator-5562
FAQ_ARTIFACTS=/Users/ubiracysantos/simlo-artifacts/2026-11-02/dynamic-faq-pilot
mkdir -p "$FAQ_ARTIFACTS"
adb devices -l
java -jar "$BUNDLETOOL_JAR" build-apks --bundle=app/build/outputs/bundle/tqsDebug/app-tqs-debug.aab --output="$FAQ_ARTIFACTS/faq-tqs-local.apks" --local-testing --connected-device --device-id="$FAQ_EMULATOR_SERIAL"
java -jar "$BUNDLETOOL_JAR" install-apks --apks="$FAQ_ARTIFACTS/faq-tqs-local.apks" --device-id="$FAQ_EMULATOR_SERIAL"
```

No modo local, bundletool prepara os APKs para a biblioteca simular a entrega. Use `install-apks` para preservar essa preparação, conforme a [documentação oficial de bundletool](https://developer.android.com/tools/bundletool) e de [entrega sob demanda](https://developer.android.com/guide/playcore/feature-delivery/on-demand). Esse roteiro comprova o mecanismo local somente quando executado e observado; não equivale à distribuição pelo Google Play.

1. Registrar pacote/versão, serial do emulador, AAB e configuração efetiva. Antes de pedir entrega, confirmar ausência de `faq` nos splits instalados.
2. Com `enabled=false`, entrar em Dúvidas e na aba FAQ: conferir Empty State, voltar e ausência de solicitação. Confirmar termos/privacidade/aceite e ajuda por modalidade sem o split.
3. Ativar a configuração no ambiente autorizado e verificar sua ativação no app. Entrar novamente, observar instalação e confirmação quando aplicável, conferir seções, expansão, links e retorno.
4. Registrar presença de `faq` nos splits após a solicitação. Desabilitar com FAQ aberto e conferir Empty State mesmo instalado; reabilitar e conferir nova entrada.
5. Exercitar falha/tentativa, sair durante entrega e recriação do processo, preservando dados. Registrar resultado de cada cenário separadamente.

A configuração versionada `SIMLO_App` mantém a entrada estável `MainActivity` e não desabilita splits no Android Studio. A execução de desenvolvimento pode instalar os módulos selecionados pela IDE; isso não mede o download sob demanda de produção.

A instalação direta de um APK base via ADB não fornece o pacote sob demanda. Um APK universal pode incorporar `faq` por fusing e servir para testes de conteúdo, mas não comprova a solicitação de entrega.

### Teste instrumental com configuração controlada

`SupportDynamicDeliveryUiTest` injeta configuração e conteúdo controlados somente
no teste, obtendo a entrega por `createSupportModuleDelivery` de Support DI,
que encapsula `PlaySupportModuleDelivery` e `SplitInstallManagerFactory` do SDK real com o pacote preparado por bundletool local-testing. Assim exercita a
instalação do split, resolução da entrada concreta, Empty State antes/depois da
instalação e reabilitação sem sessão duplicada. Não publica Remote Config nem
comprova entrega pelos servidores Google Play ou dados reais do serviço FAQ.

No emulador dedicado já instalado pelo procedimento acima, compile o APK de
instrumentação e execute somente esse teste, preservando dados:

```sh
./gradlew :app:assembleTqsDebugAndroidTest
adb -s "$FAQ_EMULATOR_SERIAL" install -r -t app/build/outputs/apk/androidTest/tqs/debug/app-tqs-debug-androidTest.apk
adb -s "$FAQ_EMULATOR_SERIAL" shell am instrument -w -r -e class br.gov.caixa.loterias.apostas.SupportDynamicDeliveryUiTest br.gov.caixa.loterias.apostas.hmp.test/androidx.test.runner.AndroidJUnitRunner
```

O teste exige `faq` inicialmente ausente e falha se o split já estiver instalado;
não remove o módulo nem limpa dados automaticamente. Repetições precisam de uma
instalação base inicial apropriada em ambiente dedicado. O AVD original não
tinha espaço para esta rodada; foi criado `SimloDynamicFaqPilot`, serial
`emulator-5562`, preservando os dados do ambiente anterior. A primeira execução instrumental nesse ambiente passou; o resultado e o limite
da reexecução após alterações posteriores estão discriminados abaixo.

### Compatibilidade dos testes locais

A integração expôs uma limitação do modo Java de recursos do Robolectric ao
carregar o package ID `0x80` do Dynamic Feature. As suítes dinâmicas e sua
adaptação local foram ajustadas para SDK 36 e `ResourcesMode.NATIVE`. Esse ajuste
mantém os testes de conteúdo e o denominador da cobertura. A integração seguinte
identificou que o parser Android espera um APK base, enquanto o artefato do módulo
é um split. `gradle/dynamic-feature-tests.gradle` prepara um APK universal real do
AAB da base para servir exclusivamente como contêiner de recursos dos testes
Debug PRD/TQS/Automação. Bundletool 1.18.3 roda em JavaExec isolado/JDK 21; AAB,
APK set e APK universal são entradas/saídas acompanhadas pelo Gradle. A CLI
recebe explicitamente o `aapt2` do SDK Android configurado, também como entrada.

`DynamicFeatureRobolectricTestRunner` usa esse contêiner e o application ID real
da variante base. As classes executadas continuam no classpath do módulo e
participam do JaCoCo; não há edição manual de recursos/IDs nem exclusão de
produção para contornar o parser. O helper fica em `core/testing/src/testSupport`
e é compilado somente nos testes dinâmicos. O host local de Compose é a Activity
Debug real `HomeDrawerTestActivity`.

`SupportNativeResourcesLoader`, restrito a testes e selecionado por SPI, registra
no SDK 36 os quatro tipos JNI de recursos ausentes no carregador do Robolectric
4.17: `ApkAssets`, `AssetManager`, `StringBlock` e `XmlBlock`. A implementação
acompanha a [correção upstream f027b0b](https://github.com/robolectric/robolectric/commit/f027b0b).
Esse suporte não fornece snapshots nem modifica recursos de produção. A rodada
focada completou as suítes FAQ/Support/host com os resultados abaixo.

## Evidências históricas do piloto FAQ

Os resultados abaixo pertencem somente ao piloto anterior à expansão. Não
comprovam a qualidade ou a entrega física dos demais módulos; a rodada de
expansão possui evidências próprias.

A rodada final de empacotamento e dispositivo gerou `faq-tqs-debug-final.aab`
em `simlo-artifacts/2026-11-02/dynamic-faq-pilot/`, fora do checkout. O pacote
TQS é `br.gov.caixa.loterias.apostas.hmp`, versão `1.24.0`, código `230102400`,
minSdk 29 e targetSdk 36. Builds posteriores exigem nova medição. Os valores de módulo são
somas sem compressão das entradas no ZIP, não tamanhos de download/instalação.

| Evidência | Resultado final e proveniência |
| --- | --- |
| AAB TQS Debug final | 96.706.427 bytes; inspeção em `final-aab-inspection.json` |
| Módulo base | 37 DEX; 170.103.823 bytes sem compressão, incluindo recursos e demais entradas |
| Módulo FAQ | 5 DEX; 90.046 bytes sem compressão, incluindo recursos e demais entradas |
| Definição de `SupportFaqEntryImpl` | Ausente nas definições DEX da base e presente em FAQ |
| Testes do adaptador SDK de entrega | 10 aprovados, incluindo corrida entre conclusão e cancelamento; escopo unitário |
| Configuração opcional | 14 aprovados; escopo parser/repository/SDK de configuração |
| Proteções inventariadas | `protected-final.json`: 45 arquivos conferidos, `changed=[]`; hashes de baselines e headers preservados |
| FAQ após ajuste Robolectric | 15 testes, zero falhas na rodada focada |
| Support Presentation | 46 testes, zero falhas na rodada focada; inclui 5 do ViewModel de entrega |
| Host/navegação App | 16 testes, zero falhas na rodada focada |
| `quickQualityCheck` | PASSED: Ktlint, Detekt e Android Lint; rodada final de build também passou |
| Arquitetura/IDs/XML de plataforma | PASSED na rodada focada; 56 verificações XML isoladas |
| Build final AAB/APK instrumental | `device-build-final.log`: BUILD SUCCESSFUL, 1 min 2 s; qualidade estática, arquitetura e IDs incluídos |
| Entrega local final no emulador | `device-final.log`: OK, 1 teste, 7,264 s; ausente → instalado → Empty State ao desabilitar → reabilitação sem sessão adicional |
| Escopo do dispositivo | Emulador dedicado `emulator-5562`, configuração/conteúdo controlados e SDK real bundletool local-testing |
| Quality gate final | `quality-verified.log`: BUILD SUCCESSFUL em 9 min 21 s; Ktlint, Detekt, Android Lint, testes unitários e cobertura PASSED; `full-summary.json` com `passed=true` |
| Testes na execução global final | 3.715 testes em 768 suítes, zero falhas/erros/skips; contagem dos XML das tarefas selecionadas em `final-test-counts.json` |
| JaCoCo global final | 60.796/63.729 linhas, 2.933 não cobertas, 95,40%; fontes/classes manuais do FAQ incluídas no denominador |
| Contratos na execução final | `verifyCleanArchitecture`, `verifyAutomationIds`, `verifyComposePlatformResourcePolicy` (56 verificações), `verifyIncrementalScopePolicy` e `verifyComposeMigration` aprovados na mesma invocação |
| Release/R8, Google Play, publicação Firebase e Samsung | NOT RUN para este piloto |

A base contém a string de carregamento reflexivo com o nome da entrada; isso
não equivale à definição da classe. A inspeção das tabelas `class_defs` dos DEX
confirma a separação do código concreto. A medição está em
`final-aab-inspection.json`, ao lado dos logs externos. O SHA-256 do AAB final é
`93408ceca4376d7b667efc4dfc2bad12f207bfaf93edf9ddc2be8d5d14e05653`.
Os logs das tentativas anteriores ficam preservados no diretório externo; a
proveniência vigente desta entrega é `quality-verified.log`, `device-final.log`
e `final-aab-inspection.json`. Os tamanhos não permitem atribuir redução sem
comparação equivalente com uma versão anterior. O quality gate aprovado não
representa execução do runner de pre-commit com validação de index/workspace.
Não houve stage, commit ou push, nem exportação de PDF nesta entrega.

Fontes: [especificação do piloto](engineering/specs/2026-11-02-support-dynamic-feature-pilot.md), [arquitetura](APP_ARCHITECTURE.md), [acesso aos dados](DATA_ACCESS_BY_FEATURE.md), [qualidade](QUALITY_GATE.md).


## Evidências da expansão para todas as features

A expansão usa 15 splits concretos: Account, Home e Responsible Gaming são
install-time; as outras 12 capacidades são on-demand. O JSON completo para o
parâmetro `app_modules`, do tipo JSON no console Firebase, está em
[`config/firebase/app_modules.json`](../config/firebase/app_modules.json).
Não foi publicado no Firebase nesta execução. Preserve o valor vigente de FAQ
ao mesclar a configuração: seu default local continua `false`; as novas chaves
têm default `true`. A flag controla admissão de novas jornadas e não desinstala
um split já recebido nem interrompe a conclusão de uma operação admitida.

As evidências desta expansão ficam fora do checkout, em
`simlo-artifacts/2026-11-02/all-feature-delivery/`:

| Verificação | Resultado e escopo |
| --- | --- |
| AAB TQS Debug | `all-features-tqs-debug-final.aab`; SHA-256 `38c719a2680d7f1eb2773d434b8bd375b02439e164d440f78ef3061079b50cc6` |
| AAB TQS Release/R8 | `all-features-tqs-release-final.aab`; 78.639.290 bytes; SHA-256 `d08f188037bf91c488daeac5207108d57cc8afa1f4fa7bb43464f20571bb6213` |
| Separação física | Inspeções Debug e Release dos DEX confirmam as 15 classes concretas somente no split proprietário, ausentes da base |
| Manifestos de entrega | 3 install-time e 12 on-demand, títulos e fusing conferidos nos dois AABs |
| Ofuscação | 4.442 de 4.477 classes do prefixo do projeto renomeadas; as 15 entradas públicas preservadas |
| SDK local-testing | `device-all-features-final.log`: 1 teste aprovado; essenciais presentes inicialmente, 12 opcionais ausentes, instalação pelo SDK e resolução das 15 entradas concretas |
| Disponibilidade e recuperação | `device-all-features-availability.log`: 1 teste aprovado, percorre as 15 chaves; off com Empty State/voltar, on com erro de resolução injetado uma vez, retry, off/on e reentrada; configuração controlada e carregador real |
| Geometria nativa | `device-betting-native-complete.log`: 9 testes instrumentais aprovados; referências nativas com contexto/tema de teste corrigidos, sem alterar a UI de produção |

O AAB Release foi compilado e ofuscado, mas está **sem assinatura de produção**.
Não foi instalado nem certificado como Release. O pacote TQS medido é
`br.gov.caixa.loterias.apostas.hmp`, versão `1.24.0`, código `230102400`,
minSdk 29 e targetSdk 36. O tamanho do AAB não representa o download de cada
usuário e não comprova redução em relação a outra versão.

Os testes físicos usaram somente o emulador dedicado `emulator-5564`. No
local-testing, bundletool funde os três módulos install-time ao APK base;
os demais são entregues pelo SDK/SplitCompat a partir dos arquivos locais.
Isso não certifica distribuição pelos servidores do Google Play, atualização
incremental em produção, publicação Firebase ou execução no Samsung. O teste
de disponibilidade resolve entradas reais, mas não abre todos os fluxos de
negócio; esses comportamentos são cobertos pelas suítes de UI e regressões de
continuidade, sem efetivar apostas, pagamentos ou resgates reais.

Os ajustes posteriores ao gate global de 3.866 testes foram limitados à
visibilidade explícita da dependência AndroidX Core já existente e à
infraestrutura instrumental. O AAB Debug manteve exatamente o mesmo hash.
O registro de qualidade abaixo identifica a rodada final vigente.


A rodada final vigente é `final-quality-seventh.log`: **BUILD SUCCESSFUL** em
14 min 52 s, 3.381 tarefas. Ktlint, Detekt, Android Lint, arquitetura, IDs,
contratos e testes passaram. Os XML das tarefas selecionadas registram
**3.866 testes em 802 suítes**, sem falhas, erros ou skips. A cobertura global
JaCoCo é **95,29%**, com **63.320/66.452 linhas** cobertas (3.132 não cobertas),
sem exclusão de código manual. `final-global-test-counts.json`,
`final-quality-summary.json` e `final-coverage-summary.json` preservam os
resultados externos. A infraestrutura instrumental adicional foi compilada e
executada separadamente, conforme as evidências da tabela.

As duas entradas finais por `MainActivity` via ADB tiveram `Status: ok`,
Activity retomada e buffer de crash vazio no emulador dedicado; isso não é
execução do Android Studio no Windows. Os 45 hashes de headers e baselines
permanecem iguais (`protected-final.json`). Não houve staging, commit, push,
publicação remota ou execução do runner de pre-commit com verificação do index.
O timeout de tela do emulador foi restaurado ao valor original.
