# Tutorial de Bolão, modalidades e largura dos modais

Solicitações de 15/10/2026: conferir a tela final do tutorial com o legado, restaurar as medidas das modalidades (em especial o espaço entre as ações do rodapé) e padronizar a largura dos modais flutuantes.

## Evidência e correções

- O runner de teste abre a `TutorialActivity` original do APK CX intacto e captura a página final. A animação `tutorial_bolao_final.json` tem o mesmo SHA-256 nos dois projetos (`53a23acf1d4e52d8e2a4b22457e06dcaf0d98b2eef847400089dd90819d19706`). O legado também posiciona o checkbox junto à borda inferior do telefone ilustrado; essa composição foi preservada. Os controles foram concentrados em `CaixaDSLegacyPoolTutorialComponents`, com drawables originais de checkbox, seleção azul `#007BFF` e o espaço tipográfico original na primeira linha de “Escolher / meu bolão”. Persistência e navegação permanecem na feature.
- O XML `rodape_simula.xml` tem linhas consecutivas de 52dp, sem margem extra. O `MaterialButton` nativo reserva 6dp acima/abaixo do desenho; sua superfície mede 40dp e há 12dp visíveis entre as linhas. O Compose desenhava os 52dp completos. O DS passa a preservar os insets, mantendo a área de toque. A mesma configuração atende seletores de 45dp e contadores de 50dp; o grid de números permanece com suas medidas.
- Os modais flutuantes usam `Modifier.caixaDSModalWidth()`: 24dp de margem lateral e largura máxima de 560dp, pelas restrições reais da janela. O contrato cobre ajuda HTML, alertas, seleção de opções, entrada de texto/valor, favoritas, notificações e filtros de compras/apostas. A tela de seleção numérica que substitui uma Activity e o overlay bloqueante de carregamento continuam em tela cheia.
- O azul solicitado da status bar é preservado. A largura uniforme dos modais é uma alteração de produto solicitada, substituindo as antigas proporções distintas do legado.

## Validação

Os logs e capturas desta rodada estão em `build/verification/pool-tutorial-20261015/`. O AVD próprio `SimloTutorialParity` usa o serial `emulator-5558`; o emulador do usuário não é operado. A comparação do tutorial usa 1280×2856/480dpi/fonte1. Testes históricos de geometria também são executados na configuração 1080×2408/450dpi.

A regressão de largura reproduziu título de 1.136px quando eram esperados 992px na configuração 480dpi. A regressão de botões comparou pixels com um `MaterialButton` real, independentemente do Compose: início do desenho em 18px no nativo e 0px no Compose. O teste de checkbox reproduziu o uso incorreto da cor do botão primário. Os testes não reduzem limiares para aceitar essas diferenças.

| Verificação | Resultado |
|---|---|
| `modernCheck` | PASS: arquitetura, DS, lint dos três flavors, testes e cobertura exigida. |
| `releaseCheck` | PASS: três releases minificados (PRD, TQS e Automação). |
| JVM | 1.699 testes, zero falhas/erros/skips. |
| Android na configuração 1280×2856/480dpi | 187 testes aprovados: preferências 27, apresentação 3, carrinho 54, Home 4, notificações 5, DS 17 e modalidades/tutoriais 77. |
| Android na configuração 1080×2408/450dpi | 11 execuções: 9 aprovadas (insets 4 e grid 5), duas comparações raster de prompt falharam, conforme os limites abaixo. Os quatro testes de inset também fazem parte da rodada de 480dpi. |
| Cobertura exigida | 20 relatórios, mínimo 95,46%; nenhum gate reduzido. |
| Revisão independente | Sem achados pendentes nas alterações, após corrigir e testar o ripple. |

Os relatórios detalhados estão em `jvm-results.json`, `android-480-results.json`, `android-450-summary.json` e `coverage-results.json`. Em 480dpi, as classes específicas de 450dpi são executadas separadamente, não tratadas como aprovadas nessa configuração. A rodada de 450dpi conserva as duas falhas; o conjunto Android completo não é declarado integralmente verde.


## Capturas revisáveis

- [Tutorial pareado: CX à esquerda e Compose à direita](../build/verification/pool-tutorial-20261015/tutorial-final-pair.png).
- [Modal Como jogar com largura padronizada](../build/verification/pool-tutorial-20261015/compose-help-modal.png).
- [Mega-Sena com separação das ações](../build/verification/pool-tutorial-20261015/compose-mega.png), [Dia de Sorte](../build/verification/pool-tutorial-20261015/compose-dia-de-sorte.png) e [Super Sete](../build/verification/pool-tutorial-20261015/compose-super-sete.png).

Na captura final do tutorial, 99,9554% dos pixels RGB do conteúdo são idênticos; erro médio RGB de 0,028 numa escala de 0 a 255. O recorte omite as barras do sistema, preserva as coordenadas e não registra/desloca as imagens. Essa métrica específica não é um percentual de conclusão da migração. Os hashes das imagens/APKs estão nos JSONs de metadados.

## Limites e rodadas intermediárias

A primeira execução ampla usou incorretamente uma configuração única de 1080×2400/450dpi para classes que declaram referências distintas (480dpi e 1080×2408/450dpi). Ela permanece em `android-suites.log`, sem ser tratada como aprovação. A matriz final separa essas referências. O padrão de largura dos modais exige somar cada margem de 24dp arredondada individualmente; as expectativas dos testes foram corrigidas para esse arredondamento físico, preservando a tolerância de 1px.

A revisão independente encontrou o ripple escapando do contorno. Um teste pressionando o botão reproduziu o preenchimento dos espaços; o desenho foi limitado ao retângulo arredondado com inset, sem alterar a área de toque ou a origem do pressionamento. Outro teste fixou a densidade fracionária e reproduziu a diferença de 16/17px. Ambos passaram após a correção.

Duas comparações raster históricas de `BettingPromptOriginTest` continuam divergentes na configuração 1080×2408/450dpi deste emulador: `diaPromptStartsAtTheInstalledSingleMarginRasterOrigin` e `lotofacilPromptStartsAtTheInstalledSingleMarginRasterOrigin` esperam origem horizontal 95px e observam 93px. A origem do prompt não foi modificada nesta rodada, e não se alteraram expectativas/limiares para aprovar esses casos. A equivalência exata desses dois recortes continua pendente; não se afirma 100% global.


A execução conjunta definitiva de `modernCheck releaseCheck` e dos testes Android adicionais terminou em **26m07s**, com 2.220 tarefas e código de saída zero (`integrated-final.log`). O comando usou `ANDROID_SERIAL=emulator-5558`. Apenas as duas classes com referência explícita de 450dpi foram retiradas da execução em 480dpi e executadas em sua matriz separada; as duas falhas raster continuam reportadas.

Os seis APKs finais (três debug e três release) estão sem mock e com `HOME_V2=true`; hashes e flags em `final-apks.json`. O APK com mock usado exclusivamente para as capturas fica separado como `compose-capture.apk`. Os 198 arquivos registrados na atestação de fontes permaneceram idênticos durante a rodada final. As seis capturas, seis hierarquias e o APK de captura tiveram seus hashes conferidos. O buffer de crashes está vazio. A revisão final não encontrou novos problemas nas alterações.

O emulador próprio da rodada foi encerrado após a coleta. O emulador do usuário permaneceu fora dos comandos de teste e captura.
