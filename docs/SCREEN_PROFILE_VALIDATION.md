# Execução dos três testes condicionais de tela

Data: 20/10/2026. Os três testes anteriormente ignorados por perfil de tela foram executados em um emulador temporário separado, sem alterar código de produção, expectativas, condições dos testes ou configurações de cobertura.

## Perfil validado

| Item | Configuração |
|---|---|
| Sistema | Android 16/API 36, AOSP ARM64, imagem `system-images;android-36;default;arm64-v8a`, revisão 2 |
| Tela | 1080 × 2408 pixels, portrait |
| Densidade | 450dpi |
| Fonte | fontScale 1,0 |
| Barra de status | 66pixels físicos |
| Recorte superior | Nenhum recorte adicional que aumente o inset |
| Renderizador | SwiftShader |
| Dispositivo temporário | `SimloScreen1080x2408D45020261020`, serial `emulator-5562` |

A imagem inicial com o perfil Pixel 9 Pro mantinha um overlay de câmera com inset superior de 156 px. Mesmo com resolução/densidade corretas, Retail media970px em vez de880px, e Tickets810px em vez de720px: ambos deslocados90px. A posição horizontal da estrela já passava. O perfil sem recorte padrão usava barra de 68 px e Retail media 882 px. A captura sintética original, arquivada antes desta tarefa, tem66px de região superior e limite do resumo em 880 px. A medição independente está em `reference-raster-measurement.json`, com caminho/hash da origem e leitura de pixels, e a imagem foi copiada sem transformação para `reference-synthetic-prize.png`.

A correção foi somente no ambiente: um overlay de recursos do Android no emulador AOSP define `status_bar_height_default` e `status_bar_height_portrait` como66px. O frame `[0,0][1080,66]` foi confirmado no WindowManager. O helper usado é `build/verification/pending-screen-tests-20261020/profile-tool/ReferenceStatusBar.java`; ele registra o perfil no serviço de overlays do sistema, não muda o app nem substitui a imagem capturada pelos testes. As asserções originais continuam sendo executadas sobre `uiAutomation.takeScreenshot()`.

## Resultados

| Teste original | Resultado |
|---|---|
| `ControlledPrizeInteractionTest#installedPrizeSummaryRetainsTheProductionBottomCoordinate` | Aprovado; limite do resumo 880 px e pixel roxo879px |
| `InstalledPurchasePairTest#installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate` | Aprovado; primeira linha roxa720px |
| `InstalledPurchasePairTest#installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition` | Aprovado; centro horizontal da estrela783,5px, tolerância original1px |

Total: três executados, zero falhas, zero erros e zero skips. Retail passou em 7s; Tickets em 7s. As fixtures usam IDs/dados sintéticos e use cases controlados; nenhuma operação financeira ou navegação real foi acionada.

## Repetição do perfil

A pasta de evidências contém `avd-aosp-profile.ini`, `system-image.properties`, `device-profile-final.json`, `window-final.txt` e o helper de perfil. Para repetir, criar AVD ARM64 com a imagem AOSP acima, usar as dimensões/densidade/fonte da tabela e iniciar sem o overlay Pixel. No dispositivo temporário com adbd root, compilar o helper com javac/D8, enviá-lo e executar via app_process; habilitar `com.android.shell:SimloReferenceStatusBar`. Os recursos precisam retornar `66.0px` e o WindowManager precisa confirmar o inset66px antes da execução. O registro conserva as duas dimensões no mesmo overlay; a ferramenta shell de fabricação usada nesta imagem não aceita dimensões diretamente, por isso foi usada a API de perfil no helper.

Comandos finais de testes, usando somente o novo dispositivo:

```sh
ANDROID_SERIAL=emulator-5562 ./gradlew :feature:retail:connectedDebugAndroidTest -Pandroid.injected.device.serial=emulator-5562 -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.feature.retail.ControlledPrizeInteractionTest#installedPrizeSummaryRetainsTheProductionBottomCoordinate --offline --continue --console=plain

ANDROID_SERIAL=emulator-5562 ./gradlew :feature:tickets:connectedDebugAndroidTest -Pandroid.injected.device.serial=emulator-5562 '-Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.feature.tickets.InstalledPurchasePairTest#installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate,br.gov.caixa.loterias.feature.tickets.InstalledPurchasePairTest#installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition' --offline --continue --console=plain
```

## Integridade e encerramento

Os 2.239 hashes de fontes/configurações continuam idênticos à árvore da matriz completa anterior; as 23 configurações de cobertura também estão byte a byte iguais. Nenhum teste/assume/tolerância foi removido ou relaxado e nenhum limiar foi alterado. A matriz global anterior conserva seus três skips brutos no relatório; esta execução complementar fecha os três casos, sem reescrever o histórico ou contar skips como aprovações. Os 196 casos instrumentados aprovados na matriz anterior e estes três casos cobrem os 199 casos distintos entre os dois perfis.

O AVD temporário foi parado e excluído e a imagem AOSP instalada somente para esta tarefa foi desinstalada. O inventário original de quatro AVDs foi preservado; `emulator-5560` continua ativo. Não houve manipulação da instalação nativa de referência, commit, push ou publicação. Evidências completas: `build/verification/pending-screen-tests-20261020/`, incluindo XML finais, logs das tentativas iniciais e finais, perfil e proveniência da referência.


## Reexecução dos três casos condicionais após a matriz — 21/10/2026

Os três casos ignorados na matriz `modernCheck releaseCheck` desta data foram executados no emulador temporário `SimloScreen1080x2408D45020261021`, serial `emulator-5562`, com Android 16/API 36 AOSP ARM64 (revisão 2),1080×2408,450dpi,fontScale1,0 e barra de status66px. Os recursos do sistema e o frame do WindowManager confirmaram o perfil antes da execução.

Resultado: **três testes aprovados, zero falhas, zero erros e zero skips**. Retail `ControlledPrizeInteractionTest#installedPrizeSummaryRetainsTheProductionBottomCoordinate` passou em8s; Tickets `InstalledPurchasePairTest#installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate` e `#installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition` passaram em10s. A seleção usou os mesmos comandos documentados acima, com classe/método e serial explícitos.

Não houve falha a corrigir nem mudança em fontes, testes, condições de execução, expectativas ou tolerâncias. Os2.239 hashes de fontes/configurações e as23 configurações de cobertura permaneceram idênticos à matriz anterior; nenhum limiar foi reduzido. A matriz mantém seus três skips no relatório bruto, e esta execução complementar cobre esses três casos no perfil apropriado:199 casos instrumentados distintos aprovados entre os dois perfis.

O emulador temporário foi encerrado, seu AVD excluído e a imagem instalada exclusivamente para ele removida. O inventário original de quatro AVDs foi restaurado e `emulator-5560` continua em execução. Evidências em `build/verification/pending-screen-tests-20261021/`: XML finais, logs, perfil, frame do WindowManager, helper de configuração, hashes e manifest de resultados/limpeza. Não houve commit, push ou alteração do pacote nativo de referência.

## Complemento da revisão KISS — 21/10/2026

Após `modernCheck releaseCheck`, os mesmos três casos passaram no AVD temporário `SimloKiss1080x2408D45020261021`, serial 5562, com o perfil da tabela: 1080×2408, 450dpi, fontScale 1,0 e barra 66px. Retail: um aprovado em 7s; Tickets: dois aprovados em 9s; zero falhas/erros/skips. As condições, expectativas e tolerâncias não foram alteradas. Os 2.221 hashes dos inputs continuam iguais à matriz desta revisão. O AVD e a imagem temporários foram removidos; os quatro AVDs originais e o emulador 5560 permanecem preservados. Evidências: `build/verification/kiss-review-20261021/screen-profile/`.
