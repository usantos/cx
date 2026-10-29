# Topaz Heartbeat homologado no CX

O aplicativo usa a mesma coordenada `br.com.topaz:heartbeat:4.38.0.1658708`
registrada no `app/build.gradle` do CX. A cópia Maven local em
`third-party/maven/br/com/topaz/heartbeat/4.38.0.1658708/` permite resolver o SDK
sem configurar o repositório privado do fornecedor nem armazenar credenciais.

O AAR foi copiado, sem modificações, do cache Gradle já existente na máquina:
`modules-2/files-2.1/br.com.topaz/heartbeat/4.38.0.1658708/f4617c33d6bd56df7ac35715e251cc997936fe14/`.
SHA-256 do AAR: `82e586ee0417971b52decca1c23af6a933fd4c52a89ddc8b430d7433ccd01aad`.

O POM original não estava presente no cache. O POM local foi reconstruído a
partir do descriptor Gradle cacheado em
`modules-2/metadata-2.107/descriptors/br.com.topaz/heartbeat/4.38.0.1658708/66a1d96f056a9de7623e30ce6b13f78b/descriptor.bin`.
As oito dependências registradas nesse descriptor foram preservadas:

| Grupo | Artefato | Versão |
|---|---|---|
| com.squareup.retrofit2 | converter-gson | 2.9.0 |
| androidx.work | work-runtime | 2.7.1 |
| androidx.concurrent | concurrent-futures | 1.1.0 |
| com.google.android.gms | play-services-mlkit-face-detection | 17.1.0 |
| com.google.android.gms | play-services-mlkit-text-recognition | 18.0.2 |
| androidx.appcompat | appcompat | 1.2.0 |
| androidx.activity | activity | 1.3.1 |
| androidx.core | core | 1.9.0 |

O AAR não traz consumer ProGuard rules. A regra de preservação
`-keep class br.com.topaz.** { *; }` foi portada do CX para conservar o
contrato JNI/reflection no build minificado.

A resolução Gradle pode selecionar versões superiores de dependências já
usadas pelo aplicativo. O AAR de entrada permanece fixo no checksum acima.

## Geração das chaves AES

O SDK original usa `java.util.Random` em `br.com.topaz.n.c.a(int)` para gerar
a chave de sessão AES. Esse gerador tem estado previsível de 48 bits e não
deve gerar material criptográfico. `gradle/harden-topaz.gradle` produz uma
cópia de build com essa referência trocada por `java.security.SecureRandom`.
O original vendorizado permanece intacto. O script recusa outro checksum.

App empacota o AAR corrigido; core/security compila contra seu `classes.jar`
via `compileOnly`, usando também esse JAR nos testes. Isso evita incluir o SDK
original em paralelo e respeita a restrição do AGP a AAR local em bibliotecas.
As dependências transitivas vêm do POM original. O patch preserva
os algoritmos AES/RSA, o comprimento/alfabeto das chaves, as assinaturas,
recursos e bibliotecas nativas. Não representa nova homologação do fornecedor.

`verifyTopazKeyRandom`, incluído em `verifyTopazHeartbeat`, verifica o gerador
efetivo via bytecode, executa 640 controles de formato na JVM e exige que
somente essa classe tenha mudado. A validação de integração depende também
de gerar e analisar novamente o APK release ofuscado.

## Contrato de autorização

A fonte de produção chama `Heartbeat.getInfo(context, 8001)` em cada tentativa
explícita de autorização. A política substitui cada `|` por `-` e envia
`deviceid`, `nivel=10`, `app=br.gov.caixa.loterias.apostas`, `origem=tofd` apenas
quando o digest não é nulo/vazio. Exceções do SDK preservam o fallback do CX,
sem identificador fabricado e sem registrar o digest.

O scope `openid profile offline_access` só é enviado quando
`BiometricManager.canAuthenticate(BIOMETRIC_WEAK)` retorna `BIOMETRIC_SUCCESS`.
PIN e credenciais de dispositivo não substituem essa condição. Caso contrário,
`scope=null` deixa o padrão do provedor, como no CX. A configuração de simulação
não consulta o SDK nem a disponibilidade biométrica. Testes usam fornecedores
injetados com dados sintéticos.

## Manifest e permissões

O manifest original do AAR declara localização fina/aproximada, estado/número de
telefone, contas, contatos (máximo API36), Bluetooth legado (máximo API30),
Bluetooth connect, rede/Wi-Fi, boot, consulta de pacotes e proteção de overlays.
Também declara os serviços/receivers do SDK e duas Activities internas.

A entrada de login solicita somente permissões runtime efetivamente declaradas
no APK e ainda não concedidas. Os guards incluem números de telefone a partir
da API26, Bluetooth connect a partir da API31, notificações a partir da API33,
contatos até a API36 e armazenamento externo somente até a API32 (declaração
explícita preservada do CX, com esse limite moderno). Localização fina e
aproximada são solicitadas juntas na API31+. Permissões normais e acesso
especial ao notification listener não são solicitados como permissões runtime.
Negação permite continuar o login; simulação não abre diálogos de permissão.

## Isolamento dos componentes em simulação

O placeholder `topazComponentsEnabled` mantém os sete componentes do AAR
ativos em produção/release e em debug real, mas desabilita todos em debug mock.
Release fixa o valor `true`, inclusive quando a propriedade Gradle
`mockBackend=true` é fornecida. As declarações do host substituem somente
`android:enabled`; intent-filters e demais contratos do fornecedor permanecem.

| Tipo | Componente |
|---|---|
| Activity | br.com.topaz.heartbeat.calleridwatchlist.CallActionActivity |
| Activity | br.com.topaz.heartbeat.securecall.SecureCallActivity |
| Service | br.com.topaz.heartbeat.notification.NotificationListener |
| Service | br.com.topaz.heartbeat.sosus.ScreenSupervisorIntentService |
| Receiver | br.com.topaz.heartbeat.broadcast.OnBootScheduler |
| Receiver | br.com.topaz.heartbeat.broadcast.CronListener |
| Receiver | br.com.topaz.heartbeat.broadcast.CallListener |

O AAR não declara ContentProvider nem metadata de inicialização AndroidX.
Seus bytecodes não implementam `androidx.startup.Initializer` ou
`ComponentRegistrar`. As dependências disponíveis no cache trazem os providers
compartilhados `androidx.startup.InitializationProvider` (inicializador de
WorkManager) e `com.google.mlkit.common.internal.MlKitInitProvider`; suas
metadatas não registram inicializadores Topaz.

O gate `verifyTopazHeartbeat` lê o manifest real do AAR e exige override de
cada componente no host, além de verificar a configuração debug/release.
`TopazComponentIsolationTest` verifica os estados efetivos no manifest merged
instalado. Essa verificação não executa a coleta do SDK.

## Workers já persistidos e WorkManager

A aplicação não utiliza WorkManager diretamente. A resolução auditada de
`tqsDebugRuntimeClasspath` mantém `androidx.work:work-runtime:2.7.1`, declarado
pelo SDK. O manifest original dessa versão foi conferido no AAR oficial do
Google Maven, além dos defaults `res/values` e `res/values-v23`.

Em debug mock, somente a metadata `androidx.work.WorkManagerInitializer`
recebe o marcador `mock-disabled`; o provider compartilhado
`androidx.startup.InitializationProvider` e seus outros inicializadores
permanecem ativos. Onze componentes de entrega WorkManager ficam desativados:

- SystemAlarmService, SystemJobService e SystemForegroundService;
- ForceStopRunnable$BroadcastReceiver;
- ConstraintProxy$BatteryChargingProxy, $BatteryNotLowProxy,
  $StorageNotLowProxy e $NetworkStateProxy;
- RescheduleReceiver e ConstraintProxyUpdateReceiver;
- DiagnosticsReceiver.

Em release e debug real, os placeholders preservam exatamente os valores
originais: serviços usam `@bool/enable_system_*_service_default`, incluindo
seus overrides por API; ForceStop e Diagnostics usam `true`; constraints e
Reschedule usam `false`; ConstraintProxyUpdate usa o bool de SystemAlarm.
Nenhum serviço originalmente desativado por API é forçado a `true`.

O gate exige os onze overrides, a metadata isolada e os defaults de produção.
O teste instalado verifica a metadata e os estados dos componentes. O pin SDK
`verifyTopazHeartbeat` também é dependência de `releaseCheck`.

## Overrides persistidos no PackageManager

A primeira operação de `LoteriasApplication.onCreate`, antes do bootstrap Hilt
ou do runtime push, aplica a política local de componentes. Em mock ela também
força o override explícito `COMPONENT_ENABLED_STATE_DISABLED` nos 18 componentes,
com `DONT_KILL_APP`, para neutralizar estados que WorkManager tenha persistido
antes da troca de modo. A rotina não chama o SDK.

Antes de mudar qualquer estado, os valores originais dos componentes que serão
alterados são persistidos por commit síncrono em preferências dedicadas
`topaz_mock_component_overrides`. O registro contém somente nomes de componentes
e estados do PackageManager, sem dados pessoais. Falha de persistência impede
qualquer alteração subsequente. Reentrada mock preserva os originais.

Ao voltar a modo real, somente os componentes registrados pela rotina são
restaurados aos seus estados originais, inclusive DEFAULT, DISABLED_USER e
DISABLED_UNTIL_USED. Componentes já desativados antes do mock não são registrados
nem alterados. Depois da restauração completa, o marcador é removido. Sem esse
marcador, produção não altera nenhum estado. Falhas parciais de PM ou de remoção
do marcador preservam a possibilidade de repetir a recuperação.

Os testes da política usam fakes e verificam ida/volta, ordem da persistência,
reentrada, falhas de armazenamento e recuperação de alteração parcial. O teste
instalado verifica os 18 overrides explícitos desativados em mock, sem executar
a coleta do SDK. O gate também exige a lista completa e a ordem do bootstrap.
