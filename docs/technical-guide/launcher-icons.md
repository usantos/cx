# Ícones do launcher pelo Remote Config

O parâmetro String `app_launcher_icon` contém um JSON de calendário anual. Os recursos continuam empacotados no APK; o servidor configura apenas as datas e a seleção.

```json
{
  "default_icon": "default",
  "timezone": "America/Sao_Paulo",
  "campaigns": [
    {
      "icon": "agosto_lilas",
      "start": "08-01",
      "end": "08-31"
    },
    {
      "icon": "setembro_amarelo",
      "start": "09-01",
      "end": "09-30"
    },
    {
      "icon": "outubro_rosa",
      "start": "10-01",
      "end": "10-31"
    },
    {
      "icon": "novembro_azul",
      "start": "11-01",
      "end": "11-30"
    }
  ]
}
```

`start` e `end` usam `MM-DD`, com os dois limites inclusivos e repetição todos os anos no fuso `timezone`. Fora dos períodos, o ícone é sempre `default`. Campanha sem uma das datas, com data impossível ou ícone desconhecido é ignorada. JSON inválido ou fuso inválido resulta no padrão; em períodos sobrepostos vence a primeira entrada válida. Um intervalo como `12-20`/`01-10` atravessa a virada do ano. Não há campanha automática fora das entradas válidas recebidas.

O mesmo JSON está em `config/firebase/app_launcher_icon.json` e nos defaults XML. O valor anterior de texto simples, como `outubro_rosa`, deixa de forçar uma campanha sem datas. Ao publicar esta migração, o valor da chave no Firebase deve ser substituído pelo JSON completo.

A seleção é reavaliada ao iniciar e retornar ao app e por um alarme diário do Android, inclusive com o app fechado. O receiver privado reprograma o calendário após reinicialização do telefone ou mudança do relógio/fuso. Usa alarme inexato, sem solicitar permissão de alarme exato: o Android pode adiar a execução durante economia de bateria. Se o usuário forçar a parada do app nas configurações do sistema, a programação retoma na próxima abertura.

Push, configurações de funcionalidades e chaves públicas compartilham a instância Firebase padrão e o mesmo cliente Remote Config. A criação usa um bloqueio comum, inclusive quando os consumidores iniciam simultaneamente. A inicialização do calendário aguarda a ativação inicial do Remote Config. Em segundo plano, aguarda o cache local e os defaults, sem nova busca de rede. Assim os períodos já recebidos continuam funcionando offline. Com o app aberto, um listener próprio observa publicações da chave `app_launcher_icon`, ativa a configuração recebida e informa o ViewModel por um caso de uso de domínio. O Redux incrementa a revisão do calendário; o App aplica o ícone, reassocia os quatro atalhos e reprograma o alarme sem recriar a tela atual. Uma publicação que só muda as datas também reprograma a avaliação, mesmo quando o ícone atual continua igual. Ativações por atualização em tempo real, retomada do app ou busca das chaves públicas notificam os observadores locais do mesmo cliente, mesmo sem um novo evento de `updatedKeys`. Cada observador serializa leitura, deduplicação e entrega; o ViewModel de atualização obrigatória relê o valor atual na thread principal antes de publicar o estado. A observação do calendário começa antes da leitura inicial, evitando perder alterações nesse intervalo. Os listeners são removidos ao encerrar o ViewModel, e callbacks tardios de ativação são ignorados.

Para recuperar publicações não entregues pelo stream, o ViewModel também reutiliza a busca e ativação a cada 30 segundos enquanto a atividade está em RESUMED. `LifecycleResumeEffect` inicia um único ciclo e faz uma verificação imediata ao retomar; `onPauseOrDispose` interrompe novas tentativas periódicas ao sair do primeiro plano. Uma busca já iniciada pode terminar depois da pausa. Não há sobreposição de buscas: uma tentativa em andamento faz o próximo disparo aguardar o ciclo seguinte. Falhas de rede preservam o cache e não encerram os ciclos futuros. A recuperação acrescenta consultas ao Firebase, respeita o throttling do SDK e usa os mesmos eventos de ativação compartilhados; não recria a tela atual.

A entrega em tempo real depende de rede e da API Remote Config Realtime habilitada no projeto Firebase; não há prazo fixo garantido. Falhas de ativação preservam a configuração atual. Com o processo fechado, o alarme usa o calendário já ativado; uma nova publicação ainda será buscada na próxima abertura. A busca ao abrir/retomar usa `fetch(0)`, sem aguardar a configuração de cache de uma hora. A troca nativa continua sujeita à atualização do launcher e à limitação de publicação dos atalhos pelo Android.

O App (minSdk 29) empacota os XMLs adaptativos, fundos e foregrounds por densidade. Cada ícone normal/redondo também declara a camada `monochrome` com o mesmo foreground, para ícones temáticos do Android, sem alterar os pixels dos recursos coloridos. As cópias bitmap legadas normais/redondas ficam em `output/launcher-icons/legacy`, com os hashes preservados.

Os cinco aliases apontam para a mesma MainActivity, que permanece habilitada. Os atalhos conservam os IDs `result_megasena`, `result_lotofacil`, `result_loteca` e `results_all`, os rótulos, os ícones próprios e os intents explícitos para MainActivity. O controlador evita republicar uma seleção já aplicada. Limitação de publicação adia a troca.

## Ambiente do Firebase

A publicação precisa usar o mesmo projeto da variante instalada. O APK PRD consulta `app-loterias-simlo-prd` (número `1090799638737`); TQS e Automação consultam `app-loterias-simlo-nprd` (número `75059847302`, App ID `1:75059847302:android:c963732446d94e71faf107`). Publicar no NPRD não altera o APK PRD. Os pacotes são, respectivamente, `br.gov.caixa.loterias.apostas` e `br.gov.caixa.loterias.apostas.hmp`.

Para validar o fluxo interativo atual, `force_update` deve estar `false`: a tela de atualização obrigatória tem prioridade e antecede a inicialização do gate de ícones. Mantenha o app em primeiro plano e a tela desbloqueada durante publicações consecutivas; ao voltar de segundo plano, a conexão em tempo real pode precisar se restabelecer.

## Validação local

Apenas em builds DEBUG, o extra `launcher_icon_preview` continua aceitando o nome de um ícone para prévia antes do efeito nativo; ele não substitui o calendário JSON. Release ignora esse extra. A prévia não altera o valor ativado no Firebase e não publica configuração na nuvem.

```sh
adb -s RXGL10CDZ0W shell am start -W \
  -n br.gov.caixa.loterias.apostas/.MainActivity \
  --es launcher_icon_preview outubro_rosa
```

Para testar outra inicialização local, encerre o processo e abra novamente com o valor desejado. Para voltar ao padrão, use `default`; sem extra, o app volta a ler a configuração ativada.

O relatório de execução e as capturas desta rodada ficam em `build/verification/launcher-icons-remoteconfig-20261027/`. A matriz diferencia a validação do contrato Firebase em testes da prévia DEBUG exercitada no Samsung. Publicação global no Firebase exige sua própria evidência de publicação e não é inferida dos testes locais.

A validação do calendário anual fica em `build/verification/launcher-calendar-20261028/`, incluindo limites das datas, fuso, retorno ao padrão, agendamento e execução do receiver. A publicação do JSON no Firebase requer acesso ao projeto e não é inferida dos testes locais.

## Executar pelo Android Studio

O `app/build.gradle` define explicitamente o flavor `tqs` e o build type `debug` como padrões do Android Studio (`isDefault = true`), formando `tqsDebug`. A configuração compartilhada `SIMLO App`, em `.run/SIMLO_App.run.xml`, usa **Launch: Specified Activity** com `br.gov.caixa.loterias.apostas.MainActivity`. O Play abre a atividade estável diretamente, independentemente do alias de campanha ativo. Mantenha **Skip Activity validation** e **Clear app storage before deployment** desmarcados.

A configuração também habilita **Always install with package manager**, usando a instalação convencional e desabilitando as otimizações de deploy do Studio. **Skip bytecode verification** permanece desmarcado. No diagnóstico do Samsung, o APK tinha assinatura válida, mas duas instalações foram recusadas após timeout do verificador do Google Play (`INSTALL_FAILED_VERIFICATION_FAILURE`). A instalação convencional do mesmo APK foi concluída e recebeu `ALLOW` do verificador, sem desativar as proteções do aparelho.

Na reprodução de 29/10/2026, a instalação PRD pelo próprio Play do Studio alternou `ALLOW` (verificação 1016) e `REJECT` por timeout (1015 e 1017), mesmo com **Always install with package manager** ativo. Portanto, essa opção não garante a resolução do timeout. O log mostrou `FreecessController` congelando `com.google.android.verifier` entre as tentativas; na tentativa rejeitada, o serviço não respondeu em cinco segundos e o diálogo foi suprimido por o instalador estar em segundo plano. O componente estava em modo de bateria **Otimizado**. Após selecionar **Configurações > Aplicativos > Android Developer Verifier > Bateria > Não restrito**, duas reinstalações consecutivas pelo Play receberam `ALLOW` (1018 e 1019), em 4,182 s e 3,861 s, com intervalo entre elas; a `MainActivity` PRD 1.25.0 voltou ao primeiro plano. O componente passou a constar na lista de execução sem restrição de bateria. Isso mantém a verificação de segurança ativa e evita a suspensão observada nesse diagnóstico. Não desative Play Protect, a verificação por USB ou a verificação de bytecode para mascarar o problema.

Não use **Default Activity** nessa configuração: o Android Studio pode resolver estaticamente `DefaultLauncher`, embora esse alias esteja desativado no aparelho quando uma campanha está ativa, causando `Activity class ... DefaultLauncher does not exist`. Não reative o alias padrão para contornar o erro, pois a seleção do ícone deve continuar seguindo o calendário. Para o NPRD validado neste guia, selecione a variante `tqsDebug` em **Build Variants**; os endpoints de Automação são distintos.

## Release de Automação/NPRD em 28/10/2026

O artefato validado nessa etapa foi `simlo-1.24.0-automacao-nprd-ofuscado-recuperacao-teste-20261028.apk`: release com R8 e sem depuração, assinada com o certificado local de testes. Foi reinstalada no mesmo pacote do Samsung, com o SHA-256 instalado conferido contra o arquivo gerado. Nessa etapa, somente a variante Automação permaneceu instalada entre as variantes do app; os dados locais da instalação PRD removida foram preservados.

A validação passou com 195 testes relacionados de common, segurança, notificações, onboarding e integração do launcher; arquitetura, Detekt, Ktlint dos arquivos alterados, 37 contratos XML e Lint completo de Automação/release. O Lint usa o baseline existente e não apresentou novos erros bloqueantes. Os 39 arquivos protegidos, incluindo headers e baselines, permaneceram idênticos. O Gson está em 2.15.0, a remoção da ferramenta de preview está no manifesto de debug e os dez ícones adaptativos declaram a camada monocromática.

O diagnóstico que motivou a recuperação foi preservado: o servidor já entregava o template 26, mas o cache do Samsung ainda estava no 25, sem busca nova pendente. O processo havia aplicado uma publicação anterior e deixou de receber a seguinte. Esse caso ocorre antes da ativação e da seleção do ícone; a checagem a cada 30 segundos recupera a configuração pelo mesmo caso de uso já utilizado na retomada. O listener continua como caminho imediato.

As regressões verificam inicialização compartilhada do Firebase, ativação por busca das chaves, notificações concorrentes, mudanças durante a leitura inicial, duas alterações sem evento de streaming, pausa/retomada, erro de rede, requisição lenta, inícios duplicados e encerramento do ViewModel. A revisão do ajuste não encontrou bloqueadores concretos.

As evidências locais ficam em `build/verification/launcher-realtime-nprd-20261028/`: `v3-test-summary.json`, `v3-lint-summary.json`, `recuperacao-apk-result.json`, `v3-installed.json` e a sequência física `v3-live-monitor.jsonl`. A prova física final registrou três trocas consecutivas no PID 11928, com o app continuamente em primeiro plano por 5min39s: Outubro Rosa às 22:07:14, padrão às 22:09:52 e Outubro Rosa às 22:12:40. Os quatro atalhos foram reassociados em todos os estados. O template final 31 corresponde ao JSON anual original e mantém force_update=false. O launcher foi aberto para as capturas somente depois do encerramento dessa coleta; publicações feitas com a tela bloqueada não são contadas como prova de entrega em primeiro plano.

## Diagnóstico de LTR-503 no NPRD em 28/10/2026

O código LTR-503 identifica indisponibilidade na inicialização; não corresponde necessariamente ao status HTTP 503. No Samsung, o template Firebase 34 já estava ativado e o armazenamento de configurações assinadas continha o manifesto de pins versão 3. O envelope aceito no aparelho era idêntico ao arquivo publicado, incluindo os hosts de Automação e TQS Externo Esteira. Portanto, a indisponibilidade que persistiu após essa publicação não era causada pela ausência desses pins.

A captura de diagnóstico da variante Automação confirmou HTTP 500 em `simlo.tqs.caixa.gov.br` durante a validação da versão. Uma comparação com o transporte SILCE existente também recebeu HTTP 500 nos parâmetros de inicialização desse host. A variante TQS, no ambiente `EXTERNO_ESTEIRA`, usa `silce.simlo.tqs.caixa.gov.br`: no Samsung, carregou o manifesto versão 3, recebeu HTTP 200, abriu a tela inicial e consultou os resultados de Mega-Sena, Lotofácil e Loteca, com backend simulado desativado. TQS e Automação compartilham o Firebase NPRD e o pacote `.hmp`; a instalação de uma variante substitui a outra. Usar TQS não corrige a indisponibilidade do servidor de Automação.

O APK final dessa validação é `simlo-1.24.0-tqs-nprd-ofuscado-ltr503-teste-20261028.apk`, assinado com o certificado local de testes e instalado no lugar da variante de diagnóstico. A release mantém depuração, backend simulado e logs de rede desativados; o mapping e o DEX confirmam a ofuscação. O SHA-256 instalado corresponde ao arquivo gerado. A tela inicial e a consulta pública da Mega-Sena foram conferidas novamente nessa release no Samsung. O Lint TQS/release passou com o baseline existente, e as verificações selecionadas de onboarding, gerenciamento de chaves e proteção de hosts somaram 94 testes sem falhas. Headers e baselines permaneceram inalterados. Apenas o pacote `.hmp` permanece instalado entre as variantes do app.

As evidências locais estão em `build/verification/launcher-realtime-nprd-20261028/503-pins-publication-confirmed.json`, `503-device-pins-state.json`, `503-backend-comparison.json`, `503-automacao-phone-network-summary.txt`, `503-tqs-phone-network-summary.txt`, `503-tqs-apk-result.json`, `503-tqs-validation-summary.json`, `503-tqs-final-startup.png` e `503-tqs-final-megasena.png`. Esses artefatos de diagnóstico são ignorados pelo Git.

## Fontes e exportações

Os recursos empacotados no APK ficam em `app/src/main/res/`. As três imagens usadas pelo guia técnico ficam em `docs/technical-guide/images/`. `output/launcher-icons/` e as cópias legadas de referência são artefatos locais ignorados; não precisam ser incluídos no commit para compilar o app ou regenerar o guia. A reprodução das artes a partir do material extraído continua exigindo o insumo local descrito em `scripts/assets/generate_launcher_campaign_icons.py`.

## Entrada estável e primeira troca de ícone

Os cinco aliases apontam para `runtime.launcher.LauncherEntryActivity`. Essa
Activity apenas encaminha o Intent à `MainActivity` com `NEW_TASK`/`SINGLE_TOP` e
termina. Flags recebidas que limpem, dupliquem ou ocultem tarefas não são
propagadas. Seu `taskAffinity` é vazio, com `noHistory` e `excludeFromRecents`; a
tarefa da interface usa a entrada estável `MainActivity`. Desabilitar um alias
não encerra a tarefa que contém as telas. O Android pode finalizar uma Activity
aberta pelo componente desabilitado mesmo com `DONT_KILL_APP`, sem matar o processo
ou registrar crash. Os quatro atalhos continuam abrindo `MainActivity` diretamente.

No Android Studio, selecione **SIMLO App**. A configuração tem nome distinto de
`app` para evitar reutilizar uma configuração local antiga em **Default Activity**.
O nome raiz Gradle é fixo em `simlo-app-android`: o módulo compartilhado não depende
de a pasta do checkout se chamar `cx`, `SIMLO` ou outro nome.
O Android Studio carrega `.run/SIMLO_App.run.xml` como configuração nativa
compartilhada ao abrir/sincronizar o projeto, no Windows e no macOS. Não há comando
Python, tarefa de migração ou reescrita automática de `.idea/workspace.xml`.

Em um IDE que já tenha a configuração antiga `app` selecionada, escolha **SIMLO
App** uma vez no seletor ao lado do Play. A seleção é uma preferência local do
Studio, preservada nos próximos usos. Não use a configuração antiga em
**Default Activity**. O projeto não sobrescreve configurações pessoais ou do sample.

`verifyAndroidStudioRunConfiguration` valida diretamente no Gradle o XML e o nome
estável do módulo, com doze fixtures. Rejeita alias de campanha como destino,
modos incorretos, opções que apaguem dados ou dispensem verificação e tarefas
externas antes do Play. O pre-commit mantém essa validação obrigatória.

Referência: [configurações compartilhadas nativas do IDE](https://www.jetbrains.com/help/idea/run-debug-configuration.html).

O teste de regressão confere os cinco aliases, o isolamento da tarefa, os extras
encaminhados e a preservação da sessão nas APIs 32/35. O teste nativo na API 36
reproduziu o encerramento ao desabilitar o alias e confirmou a permanência da
Activity com a entrada isolada. A configuração Firebase e as regras do calendário
continuam responsáveis por escolher o ícone.

`verifyLauncherEntryPolicy`, dependência de `verifyComposeMigration`, valida o
adaptador completo e os atributos exatos do manifesto com oito fixtures positivas
e negativas. A única nova Activity permitida fica nesse caminho de plataforma;
ela não pode receber UI, ViewModel ou lógica de negócio. As demais Activities e
Fragments de features continuam bloqueadas, sem alteração de headers ou baselines.

Validação em 30/10/2026: 3.567 testes JVM aprovados, sem falhas, erros ou skips;
cobertura global 95,16%. Passaram os 214 testes das ferramentas, os 30 testes do
hook e as oito fixtures da entrada de plataforma. No emulador API 36, o APK PRD
Debug foi reinstalado no mesmo pacote, mantendo os dados; abriu pelo alias padrão,
ativou Outubro Rosa e permaneceu na tarefa MainActivity. Duas aberturas seguintes
pela MainActivity retornaram `Status: ok` com o alias padrão desabilitado. A
configuração do Studio é compartilhada por XML, sem migração por script;
essa máquina remota não foi controlada durante a validação.
