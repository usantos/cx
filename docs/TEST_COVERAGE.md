# Testes e cobertura JaCoCo

Execute na raiz do projeto com o JDK do Android Studio:

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' \
./gradlew testAndCoverageReport --continue --max-workers=4 \
  -PhomeV2=false -PmockBackend=false -PnetworkLogging=false
```

A configuração está em `gradle/aggregate-test-reports.gradle`, aplicada pelo build raiz. Seleciona `testPrdDebugUnitTest` no app, `testDebugUnitTest` nas bibliotecas Android e `test` nos módulos JVM, evitando somar variantes equivalentes.

Saídas:

- `build/reports/tests/aggregate/index.html`: resultados das suítes.
- `build/reports/jacoco/aggregate/html/index.html`: cobertura navegável.
- `build/reports/jacoco/aggregate/jacoco.xml`: integração com ferramentas de análise.
- `build/reports/jacoco/aggregate/jacoco.csv`: dados tabulares.

Também é possível executar `jacocoAggregateReport aggregateTestReport`; a tarefa de cobertura exige uma nova execução das suítes selecionadas. `aggregateTestReport` reúne os resultados JUnit disponíveis.

A agregação cobre testes unitários, sem executar testes no celular. Código gerado de recursos, BuildConfig e injeção é excluído; código de negócio, telas e ViewModels permanece no denominador. As verificações de cobertura específicas já existentes continuam independentes, com seus limites preservados.

Com `--continue`, o Gradle executa suítes independentes após uma falha. Os relatórios de cobertura não aprovam execuções parciais: suas dependências exigem os testes concluídos com sucesso.

## Execução local de interfaces

`gradle/local-ui-tests.gradle` configura Robolectric 4.17 com recursos Android e gráficos nativos no SDK 35. As bibliotecas Compose recebem o runner de testes de interface no classpath unitário. O app continua usando a variante PRD Debug, com backend real configurado no build; os testes fornecem repositórios e gateways locais controlados e não realizam apostas, pagamentos ou reservas.

`config/testing/local-ui-suites.txt` lista explicitamente os testes de dispositivo que também podem executar localmente. `scripts/prepare-local-ui-tests.py` gera cópias apenas em `build/`, adaptando runner/configuração e preservando os corpos e asserções. Testes parametrizados mantêm todos os parâmetros; entradas `helper:` copiam seus auxiliares. Recursos de `src/androidTest/res` pertencem somente ao source set de teste local.

Testes que precisam de Chromium real, APK Legado instalado, insets físicos ou injeção de eventos na janela nativa permanecem no dispositivo, com o motivo registrado na lista. `config/testing/local-ui-device-methods.txt` seleciona exceções específicas de método que só executam no runner conectado; os demais métodos da classe continuam locais. Esses limites não excluem classes de produção do JaCoCo. Contratos de callbacks, erro, descarte de WebView e bloqueio de ações possuem casos locais separados; eles não comprovam execução real do Chromium nem do SDK externo ofuscado.

A integração da FilaBRA usa um substituto de transporte com a ABI do SDK somente no source set unitário: o bytecode ofuscado do fornecedor não pode ser instrumentado pelo Robolectric. O gateway, as leases e os callbacks da aplicação executam como código de produção. A validação do SDK real continua sendo feita no dispositivo.

## Qualidade dos testes

As suítes novas verificam resultados, estados e callbacks; os transportes e dados sensíveis são substituídos por fixtures locais. Literais numéricos que representam entradas e resultados esperados possuem supressões `MagicNumber` limitadas ao arquivo de teste e acompanhadas de justificativa. As regras globais dos analisadores e suas baselines não são alteradas para admitir os novos testes. Helpers foram separados quando ultrapassavam os limites de tamanho ou complexidade.

A geração de cópias locais precede a compilação, o KSP e as tarefas de análise/modelo do Lint unitário. Isso permite validar também essas fontes em um checkout sem artefatos anteriores.

## Gate global obrigatório

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' \
./gradlew verifyCoverage --continue \
  -PhomeV2=false -PmockBackend=false -PnetworkLogging=false
```

O gate exige **mais de 95%** das linhas elegíveis: `100 * linhas_cobertas > 95 * linhas_elegíveis`. Exatamente 95% falha, independentemente do arredondamento apresentado. A soma dos contadores dos módulos deve coincidir com o agregado. `summary.json` registra o resultado efetivamente medido, inclusive quando fica abaixo do limite.

Cada execução agregada apaga os dados JaCoCo e os relatórios anteriores e reexecuta as suítes selecionadas. Relatórios dependem do sucesso dos testes; `--continue` executa suítes independentes após uma falha, mas não transforma um resultado parcial em cobertura aprovada.

## Base anterior à padronização dos campos e ao sample — 22/10/2026

A execução completa abaixo terminou com `BUILD SUCCESSFUL` e `QUALITY GATE PASSED`:

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' \
./gradlew qualityGate --continue --console=plain \
  -PhomeV2=false -PmockBackend=false -PnetworkLogging=false
```

- Cobertura global de linhas: **95,22%**, com **39.956 de 41.961** linhas elegíveis cobertas e 2.005 não cobertas, nos **65 módulos** selecionados. A soma dos módulos coincide com o agregado.
- JUnit: **2.959 testes**, em 577 suítes; **zero falhas, erros ou testes ignorados**. Todos os resultados correspondem às variantes selecionadas pelo gate.
- Ktlint, Detekt, Android Lint, testes unitários e gate de cobertura: **aprovados**.
- Verificadores Python: **161 testes aprovados**, incluindo limiar exato, soma ponderada, preservação das asserções e validação do gerador de testes locais.
- Medição inicial desta alteração: 42,39% (17.782/41.949). A pequena diferença no total de linhas decorre das correções de produção descritas abaixo; não foram adicionadas exclusões de classes de produção.

Os novos cenários expuseram duas falhas corrigidas com regressões: o sucesso de `Task<Void>` do Firebase não podia ser inferido de um resultado não nulo; o detalhe de combo precisava carregar os parâmetros atuais antes da inclusão para informar o valor mínimo correto e impedir envio com estado obsoleto após falha.

Evidências geradas pelo build:

- `build/reports/jacoco/aggregate/html/index.html`
- `build/reports/jacoco/aggregate/jacoco.xml`
- `build/reports/jacoco/aggregate/summary.json`
- `build/reports/quality/full-summary.json`

Este resultado mede testes locais JVM/Android com Robolectric e os limites de transporte controlados descritos acima. Não representa validação em dispositivo do Chromium, de SDKs externos ofuscados ou de operações financeiras reais.


## Campos compartilhados e sample do DS — 22/10/2026

A execução final de `qualityGate verifyCaixaDS verifyCleanArchitecture :sample:design-system:assembleDebug`, com `-PhomeV2=false -PmockBackend=false -PnetworkLogging=false`, terminou com `BUILD SUCCESSFUL` e `QUALITY GATE PASSED`.

- Cobertura global LINE: **95,48%** — **42.452/44.460** linhas, 2.008 não cobertas, **66 módulos**. O sample faz parte do denominador e a soma dos módulos coincide com o agregado.
- JUnit: **2.980 testes em 586 suítes**, sem falhas, erros ou testes ignorados; inclui seis cenários do campo unificado, cinco contratos visuais/funcionais da conta e dez testes do catálogo.
- Ktlint, Detekt, Android Lint, testes, cobertura e verificadores de propriedade do DS/arquitetura aprovados. As dez fixtures do verificador de DS rejeitaram acessos diretos e aceitaram os componentes próprios; a fixture foi removida e o estado original restaurado.
- Os 161 testes Python também passaram. Não foram alterados limites, baselines ou exclusões de fontes de produção para esta entrega.

A regressão do campo monetário cobre uma alteração rejeitada pelo consumidor e uma nova tentativa com a mesma entrada. O teste do resumo do carrinho agora espera o valor e a quantidade publicados na interface após a consulta em IO; conserva todas as asserções de exibição e de quantidade de consultas.

O APK independente e as instruções estão em [sample/design-system/README.md](../sample/design-system/README.md). A lista, as três variantes do campo, a edição e o HTML local foram conferidos no emulador API 36. A geração dos relatórios globais continua usando os testes locais controlados; nenhuma aposta, pagamento ou reserva real foi realizada.
