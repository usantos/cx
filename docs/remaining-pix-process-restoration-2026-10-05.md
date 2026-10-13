# Restauração Pix após morte real do processo — 05/10/2026

Este roteiro cobre a lacuna de Android real identificada na validação de compras: restauração do NavHost, do proprietário sintético e do SavedStateHandle depois de encerrar um processo em segundo plano. Não representa pagamento financeiro, homologação remota ou equivalência visual integral com o APK legado.

## O que o código atual faz

`MainActivity.onCreate` usa o extra debug `visual_test_route` para abrir `LoteriasApp`. O extra `visual_test_authenticated=true` instala a sessão sintética somente em debug com backend mock e somente quando não existe identidade. `SessionTokenStore` mantém essa fixture somente em memória; o processo novo precisa reinstalar o mesmo proprietário sintético antes da composição do host.

`LoteriasApp` cria `rememberNavController()` e, dentro da entrada `payment/pix-copy`, obtém `PaymentViewModel` com `createSavedStateHandle()` e o proprietário da sessão atual. `SavedPixRestoration` grava proprietário/tentativa antes do POST, conserva a cobrança concluída e recusa regeneração para resultado incompleto ou proprietário divergente. A simples inspeção desse caminho não comprova que o Android restaure o mesmo estado: o roteiro abaixo deve passar no processo novo.

## Preparação pela raiz

Use somente o pacote paralelo `br.gov.caixa.loterias.apostas.hmp.composevalidation`, nunca o pacote oficial. A raiz deve produzir e instalar um APK debug com `-PmockBackend=true -PmockScenario=PIX_PENDING -PnetworkLogging=true` e sufixo de validação. O script não modifica Gradle, não instala APK, não limpa dados e não usa force-stop. A preparação de uma instalação sintética limpa cabe à raiz, antes do teste; uma sessão real nunca deve estar presente nesse pacote.

A raiz deve escrever um JSON de atestação do build verificado, por exemplo:

```json
{
  "package": "br.gov.caixa.loterias.apostas.hmp.composevalidation",
  "debug": true,
  "mockBackend": true,
  "mockScenario": "PIX_PENDING",
  "networkLogging": true,
  "syntheticSessionOnly": true,
  "apkSha256": "SHA256_DO_APK_PRODUZIDO"
}
```

Essa atestação é uma declaração da raiz sobre a configuração e o isolamento dos dados; não é uma inspeção automática dos campos BuildConfig. O driver confere o hash declarado contra o APK local e contra o APK base instalado, exige DEBUGGABLE e exige que o processo esteja inicialmente ausente. Um build com splits não é aceito. Use Python 3 e Android platform-tools com `logcat --pid`, UI Automator, acesso ao APK instalado e captura PNG.

```sh
python3 scripts/verify-pix-process-restoration.py \
  --serial emulator-5554 \
  --adb /Users/ubiracysantos/Library/Android/sdk/platform-tools/adb \
  --apk /caminho/absoluto/compose-validation-debug.apk \
  --attestation /caminho/absoluto/mock-pix-attestation.json \
  --output /caminho/absoluto/evidencia-pix-processo-vazia
```

## Evidência e critérios

O driver abre `MainActivity` por MAIN/LAUNCHER com flags `0x10200000` na rota Pix autenticada com fixture sintética, aguarda o painel mock, captura o PID e uma imagem e exige um único POST inicial para `logado/carrinhos/gerar-cobranca-pix`. Envia HOME, aguarda a parada da Activity e executa exclusivamente `am kill` no pacote paralelo. Exige ausência do PID antes de reabrir a mesma tarefa com a mesma ação, categoria e flags, reenviando `visual_test_route=payment/pix-copy` e `visual_test_authenticated=true` para recriar o mesmo proprietário mock em memória. Registra o ID da tarefa Android na tela inicial, em segundo plano, depois do kill e no relançamento; todos precisam coincidir. Essas flags não solicitam limpeza da tarefa nem seus SavedStateHandles. O processo restaurado precisa ter PID diferente, exibir o painel Pix pendente e fazer zero novos POSTs de geração durante a observação. Se a entrada fosse nova e não restaurada, a VM geraria outra cobrança; o critério de zero POST deve detectar isso.

Os arquivos são `initial-pix.png`, `restored-pix.png` e `result.json`. Capturas são recusadas se a janela do pacote paralelo não estiver focada. Logcat é consultado somente para os PIDs candidatos e a tag `LoteriasHTTP`; o driver conta linhas de request curl, não linhas de resposta, filtra o período da rodada e nunca grava o log bruto. XML de UI é criado temporariamente em `/data/local/tmp/codex-pix-probe-<identificador-aleatório>.xml`, lido em memória pelo host e removido do aparelho em `finally`. A janela candidata deve estar focada antes e depois da coleta, e somente seus nós são analisados; XML bruto nunca é salvo no host. Nenhum token, perfil de conta ou payload de rede é exportado.

A UI exibe somente os primeiros 19 caracteres do código, seguidos de `...`; o driver compara esse prefixo mock e a ação `Verificar Pagamento`, e registra o timer. Compra 99, transação `MOCK-TX` e código completo são propriedades da fixture no interceptor, não identificadores expostos pela UI. Por isso o teste não afirma que leu o ID completo na tela: painel restaurado mais zero novo POST constitui a evidência de reuso, complementada pelos testes de campos do SavedStateHandle.

Falha ao matar o processo, falta de log de geração, saída para outra rota ou novo POST tornam a rodada vermelha. Uma falha de host deve ser reproduzida e registrada antes de qualquer alteração de produção. Não use `am force-stop`, `am start -S`, CLEAR_TASK ou limpeza de dados no intervalo: essas ações descartam o cenário que se quer verificar.

## Estado desta subtarefa

A quinta rodada, executada pela raiz no emulador Pixel 9 Pro com Android 16, passou: PID inicial **5324**, ausência confirmada após `am kill`, novo PID **5440** e tarefa **2774** preservada nos quatro pontos de coleta. O timer passou de **35:59** para **35:52**, mantendo o painel Pix pendente; houve **um POST inicial e zero POSTs no processo restaurado**. O resultado está em `build/verification/remaining-installed-20261005/process-probe/emulator-run-5/result.json`, com capturas `initial-pix.png` e `restored-pix.png` no mesmo diretório. O APK mock verificado tem SHA-256 `4f2157c4f0ca14774d5f96d41659ae22b93258987978a21fdcf5d61cc9baed09`.

A segunda rodada no XCover físico, executada pela raiz, também passou com o mesmo APK mock atestado: PID inicial **23547**, ausência confirmada após `am kill`, novo PID **23760**, tarefa **705** preservada nos quatro pontos, timer **35:58 → 35:49** e **um POST inicial/zero POSTs restaurados**. Resultado e capturas estão em `build/verification/remaining-installed-20261005/process-probe/xcover-run-2/`.

As duas rodadas comprovam morte real do processo e restauração do Pix pelo host Android no emulador e no aparelho físico, com a mesma tarefa e o mesmo proprietário sintético explicitamente reinstalado. Não comprovam retomada de autenticação real, uma cobrança financeira remota nem leitura visual do ID completo da compra. O único POST inicial foi atendido pelo backend mock; nenhum pagamento financeiro real foi enviado. Nenhuma alteração de produção foi necessária para obter esses resultados. Os testes JVM para tentativa interrompida, registro parcial/inválido e proprietário divergente continuam complementares. Esta subtarefa não executou Gradle/ADB diretamente.

A primeira execução coordenada pela raiz no emulador Android 16 parou na identificação do painel: a captura manual mostrava o Pix, mas `uiautomator dump /dev/tty` retornava apenas a mensagem de destino, sem XML. Isso foi uma falha do coletor, anterior à morte do processo, e não evidência de falha na restauração de produção. O driver foi ajustado para usar o arquivo temporário no aparelho descrito acima; a rodada deve ser repetida desde a preparação inicial.

A segunda rodada identificou outra diferença de coleta no Android 16: `dumpsys window windows` não contém `mCurrentFocus`, enquanto `dumpsys window` completo contém a janela candidata correta. O prefixo esperado coincidia exatamente com o XML. O driver passou a consultar o dump completo, mantendo a exigência do pacote exato em `mCurrentFocus`. O resultado registra somente os booleanos do último diagnóstico de prontidão da UI, sem texto bruto. Essa rodada também parou antes da morte do processo e não identifica defeito em produção.

A terceira rodada matou de fato o PID 4830 e recriou o processo como PID 4940, com um POST inicial e zero POSTs no processo novo. A tela entrou no fallback de recuperação porque a chamada MAIN/LAUNCHER sem extras não reinstalava a identidade sintética em memória. Esse resultado é compatível com a recusa segura de proprietário ausente; não justifica alterar o guard de produção. O driver foi ajustado para recriar o mesmo proprietário sintético pelos extras protegidos, mantendo a restauração real da tarefa e da entrada de navegação. O teste positivo comprova a restauração Pix quando a mesma identidade mock é fornecida ao processo novo; não comprova a retomada de autenticação real de produção. A rodada sem extras permanece evidência complementar de que a ausência de proprietário não reenvia a compra.

Falhas transitórias na coleta UI são registradas separadamente e tentadas novamente até o prazo de prontidão; a remoção do XML temporário continua obrigatória. `last_stage` identifica se a falha ocorreu nos guards, lançamento inicial, coleta inicial, encerramento, relançamento, coleta restaurada ou observação final.

A quarta rodada usou os extras de fixture na volta: PID 5103 morreu e o novo PID 5208 mostrou Pix pendente, mas fez um novo POST (contagem 1/1). O comando inicial dessa rodada não possuía MAIN/LAUNCHER e as mesmas flags do relançamento, e não havia prova do task ID preservado. O driver foi ajustado para usar comandos idênticos nos dois lançamentos e exigir o mesmo task ID nos quatro pontos descritos acima. A quinta rodada passou com essa identidade de tarefa comprovada; portanto a quarta rodada registra a limitação do lançamento do harness, sem sustentar uma afirmação de defeito no SavedState de produção.

A primeira tentativa no XCover parou antes do kill porque o dump Samsung também incluía ActivityRecords antigos em diagnósticos de orientação e cores das barras do sistema. O coletor confundia essas referências com tarefas ativas. O parser passou a considerar somente entradas reais `Hist #...` do pacote candidato e a conferir `topResumedActivity`, quando disponível. Continua exigindo uma única tarefa candidata e o mesmo ID nas quatro fases; não registra o dump bruto. Essa tentativa não exerceu morte do processo no aparelho.

Limite de sessão confirmado por leitura do código atual: `SessionTokenStore` conserva token/identidade em MutableStateFlow somente em memória. A prova sintética reinstala explicitamente o proprietário; não deve ser descrita como persistência automática de autenticação de produção. O comportamento de login/retorno após morte do processo ainda precisa pareamento com a sessão real do legado antes de decidir alterações de persistência.
