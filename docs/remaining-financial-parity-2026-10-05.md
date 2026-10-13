# Fechamento de recursos financeiros — 05/10/2026

Referência primária: `build/verification/installed-reference-20261005/legacy-installed.apk`, versão 1.24.0, SHA-256 `2e8ef3bd96c3db48a4391695429bf8531ccae72a821e3683a742bc61eb90dc3e`. A extração aapt2 em `commerce-xml` comprova atributos empacotados; os controladores do checkout legado complementam os estados dinâmicos. Nenhuma operação financeira ou payload pessoal foi utilizado.

## Correções fundamentadas

- Removido espaçador final180dp de `PaymentDataRoute.PixContent`. O APK usa `NestedScrollView fillViewport=true` na Activity, fragmento de altura `wrap_content` e aviso final com margem inferior16dp. `CopiaColaPixFragment` não adiciona rodapé ou padding180dp. A tela conserva o viewport preenchido e rolagem do conteúdo; não cria180dp além do aviso.
- `fragment_copia_cola_pix` conecta Verificar Pagamento com margem32dp à legenda do timer e Minhas Compras com margem16dp à mesma legenda. A versão anterior inseria o segundo botão depois do espaço invisível do primeiro. Agora o segundo ocupa a posição sobreposta original; o slot de Verificar permanece medido durante expiração/processamento. Atenção continua após o slot de Verificar com16dp.
- Botões Pix usam `android.widget.Button` nativo: métricas CAIXA regular/bold, font padding e `setLineSpacing(4*density, 1)`, conforme `lineSpacingExtra=4dp`. `ViewUtils.textCaixaSTDBoldTitle` aplica `AbsoluteSizeSpan(16,true)` em toda a frase; preservado esse tamanho em dp, inclusive com font scale diferente de1. A implementação de span mantém suporte minSdk23. Medição, mínimos e renderização do Button agora são nativos. A cadeia de estilos do APK resolve ButtonNotShadow → Base.Widget.AppCompat.Button → Widget.Material.Button; conservado o estilo nativo e outlineProvider=none. O drawable empacotado btn_rounded_green_outlined tem raio4dp, stroke1dp e nenhum solid; reproduzido por GradientDrawable sem preenchimento inventado, com backgroundTint52BBAA. Callbacks/clipboard/timer/tokens continuam compartilhados.
- Mantida abreviação dos primeiros19caracteres com reticências, explicitamente aplicada em `CopiaColaPixFragment`, além de ellipsize ao fim quando faltar largura.
- `activity_digitar_cvv` empacotada coloca número final e fragmento do verso no mesmo FrameLayout, com margens16dp/8dp. A UI anterior somava a linha do número à altura do cartão; agora número sobreposto e verso começam nos locais originais. CVV permite rolagem como o NestedScrollView do APK. Fonte CAIXA/font padding aplicados ao título12sp e números40sp.

- Telas de prêmio recebem fonte CAIXA com font padding e altura de linha natural, preservando Futura explícito. Lotérica remove alturas de linha18/30sp que não existem no APK (textos14/25sp sem lineSpacing). Título de confirmação da chave inicia16dp, margemStart explícita que substitui a margem geral24dp do XML empacotado.

Árvores adicionais extraídas: `activity_digitar_cvv.txt`, `activity_apresentar_cartao.txt`, `activity_confirmar_cartao.txt`, `btn_rounded_green_outlined.txt`, no diretório `commerce-xml` da referência. Nenhum XML de produção criado.

## Capturas Android controladas

As classes usam DTOs equivalentes em schema/canal/estado, IDs900x, código CONTROLLED não pagável, e-mail `.test`, cartão de número inicial/final000000/0000. Não usam HTTP. O cartão interrompe o teste caso qualquer use case de envio, polling ou conclusão seja invocado. As capturas PNG + metadados vão para `externalCacheDir/codex-financial-parity`; metadados incluem density/fontScale e contadores. Capturas superior/ações complementam telas que excedem o viewport.

| Classe | Estados adicionais |
|---|---|
| `ControlledCardRenderingTest` | VISA prévia, CVV vazio/preenchido, confirmação superior/ação, AMEX4dígitos, processamento da compra |
| `ControlledPixInteractionTest` | Pix pendente prazo15min superior/ações, Pix expirado superior/ações |
| `ControlledPrizeInteractionTest` | escolha Pix/confirmação da chave sem resgatar, MP sem resgatar, Lotérica QR/orientações, Agência sem gerar PDF/resgatar |

Executar pela raiz, que coordena Gradle/aparelho:

```sh
./gradlew :feature:payment:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.feature.payment.ControlledCardRenderingTest,br.gov.caixa.loterias.feature.payment.ControlledPixInteractionTest
./gradlew :feature:retail:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.feature.retail.ControlledPrizeInteractionTest
```

Esta subtarefa não executou Gradle/ADB. Não adicionou comportamento de serviço e não alterou ViewModels/restauração, Gradle, host ou core/data. As mudanças são de geometria comprovada por XML/controlador e instrumentação controlada. Compilação e capturas ainda dependem da execução coordenada da raiz.

## Limites

Ainda faltam pares físicos oficiais com fixtures negociais equivalentes para cartão, Pix e canais de prêmio. Captura controlada prova renderização/interação Android, não identidade visual com o raster oficial. Botões Pix e ProgressBar de consulta agora são nativos. O APK declara ProgressBar padrão match_parent × wrap_content com margem32dp, sem dimensões40dp ou tinta verde. A UI usa ProgressBar Android com medição nativa e colorAccent005CA9 resolvido do AppTheme. Ainda falta confrontar o raster oficial nos mesmos estados. Resgate MP/Lotérica/Agência aguarda pares para resolver eventuais divergências de fonte/métrica após essas correções. Não se declara100% de paridade ou conclusão financeira. Novos aliases de preview não foram habilitados em produção.

## Execução final pela raiz

XCover7: cartão/Pix11/11 passaram em `remaining-native-pix-xcover.log`; resgate6/6 em `remaining-retail-xcover.log`. Capturas/metadados em `payment-native-green/` e `retail-green/` de `build/verification/remaining-installed-20261005/`. JVM payment87/retail50/tickets41 e build/gates passaram na rodada final. Esses resultados exercitam callbacks controlados e não certificam equivalência com transações reais do instalado.
