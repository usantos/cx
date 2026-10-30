# Pares de Bolão/Filtros com o CX

Ferramentas locais de teste; não pertencem à produção. O relatório da rodada é `docs/MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md`.

Pré-requisitos: AVD dedicado `SimloComposeGates` no serial `emulator-5558`, 1280×2856/480dpi/fonte1, SDK/build-tools36, JDK 21 com `JAVA_HOME` configurado e APK CX debug `br.gov.caixa.loterias.apostas.cxv2validation` já instalado com a chave de debug local. O runner é outro APK de instrumentação; não modifica nem exporta Activities do oráculo.

Sempre restrinja **cada comando Gradle que execute testes Android**:

```sh
export ANDROID_SERIAL=emulator-5558
./gradlew modernCheck releaseCheck --offline --continue
```

Sem `ANDROID_SERIAL`, o AGP pode executar testes em todos os dispositivos conectados. Os scripts de captura recusam outro serial.

```sh
bash scripts/visual-parity/build-native-marketplace-runner.sh
python3 scripts/visual-parity/capture-native-marketplace.py --out build/verification/marketplace-paired-20261015/legacy
./gradlew :app:assemblePrdDebug -PmockBackend=true -PmockScenario=MARKETPLACE_PARITY_OCTOBER --offline
adb -s emulator-5558 install -r app/build/outputs/apk/prd/debug/app-prd-debug.apk
python3 scripts/visual-parity/capture-compose-marketplace.py --out build/verification/marketplace-paired-20261015/compose-after
```

Para o par sem campanha, use `--october false` no runner e `-PmockScenario=MARKETPLACE_PARITY` no Compose, gravando em `legacy-normal`/`compose-after-normal`. A fixture JSON é compartilhada e usa sessão sintética no Compose via `visual_test_authenticated=true`, permitida pelo host somente em debug/mock. As consultas externas à área municipal retornam vazio para não repetir códigos.

Os scripts capturam inicial, rolagem, seleção/diálogo, lista/expansão, favoritas e cabeçalho por modalidade. PNG e hierarquia são preservados; o metadata registra o SHA-256 do APK instalado e dos arquivos capturados. Use Python com Pillow/NumPy para `measure-marketplace-pairs.py`; a métrica não reposiciona as imagens. Não usar SSIM como percentual funcional ou como aprovação automática.

Ao terminar, restaure os três APKs debug com `./gradlew :app:assembleDebug --offline` sem propriedades de mock. Não altere limiares de validação para aprovar uma captura.
