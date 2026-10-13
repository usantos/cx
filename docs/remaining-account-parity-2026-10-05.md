# Conta e suporte: fechamento de estados restantes — 2026-10-05

Referência primária: APK oficial instalado `build/verification/installed-reference-20261005/legacy-installed.apk`, versão 1.24.0. Dumps `account-states-xml/*-resolved.txt` e `resources.txt` foram lidos diretamente. Também foi executado aapt2 do SDK36.0.0 para `res/color/button_state_color.xml`, `res/color/checkbox_tint.xml` e `res/layout/footer_atualizar_dados_pessoais.xml`. O código legado serve apenas para escolhas runtime. Não houve leitura/transcrição de identidade real para fixtures.

## Diferenças comprovadas corrigidas

- Cadastro: ajuda CEP centralizada no controle de32dp, igual à geometria instalada e ao helper de Dados Pessoais. Label14sp e callback Correios preservados.
- Cadastro: footer disabled segue selector instalado `button_state_color`, CFDFE2, consumindo `CaixaDS.colors.legacyDisabledButton`; enabled permanece0066B3. Dados Pessoais conserva0066B3 nos dois estados porque seu footer não possui esse selector. Footer90dp, margens32dp, top16dp e tipografia16sp/bold mantidos.
- Conta: checkbox desenhado como quadrado preenchido foi substituído pelo widget Android programático CheckBox, sem layout XML, com selector compilado checked006BB6 e unchecked3A4859. Larguras32dp no cadastro e24dp em Dados Pessoais, linhas48dp, texto16sp e margens8dp mantidos. Estado e callbacks permanecem no ViewModel. O drawable/touch do tema ainda precisa comparação física; a mudança não certifica raster do tema.
- Termos de leitura: removido spinner inline duplicado sob modal DS já hospedado por SupportScreen. Conteúdo, erros e callbacks mantidos.

## Mapeamento HTML verificado

`LEGACY_UI_INVENTORY.tsv` associa `support/terms-view` a TermosAceiteActivity: este controlador injeta FonteCaixa (font-face base64). `support/terms-acceptance` corresponde a TermosUsoActivity: recebe HTML sem wrapper. As implementações atuais seguem esse mapeamento. Nenhuma inversão de fonte foi feita a partir do nome read-only/acceptance do estado moderno. O fixture testa o documento entregue à WebView (tag) nos dois casos, usando HTML totalmente local e sem abrir links nem aceitar termos.

## Fixtures Android prontas para coordenação

`AccountRemainingParityCaptureTest`:

- Dados Pessoais completo sintético: topo, CEP focado/IME, limite focado/IME, mínimo e máximo inválidos, retorno válido/scroll inferior, diálogo notificações e falha CEP. GetAccountProfile e ResolveZipCode são implementações locais; qualquer UpdatePersonalData gera erro imediatamente. Exige zero writes e zero saved.
- Cadastro completo sintético: CPF00000000000/nome Pessoa de Teste/e-mail example.invalid, foco CEP, UF/cidade resolvidos localmente, termos desmarcados/marcados e falha CEP. RegisterAccount é guardado com erro imediato; nenhum CTA de gravação é pressionado. Exige zero writes e zero registered.
- Capturas13PNG em `<target externalCacheDir>/remaining-account-parity/`, com metadata density/fontScale. IME depende da configuração do dispositivo; é cenário para pareamento e não validação automática da posição física.

`SupportHtmlParityCaptureTest`:

- Documento read-only e documento de aceite, exigindo wrapper/conteúdo literal conforme controlador mapeado, sem aceite ou links.
- FAQ fechada/expandida, loading e erro locais. Capturas6PNG em `<target externalCacheDir>/remaining-support-parity/`.
- Espera WebView progress100 antes de capturar HTML. Capturas são candidatas; a igualdade requer o mesmo conteúdo no APK oficial e mesmo WebView/fonte/escala/dispositivo.

## Limites explícitos

Cadastro possui container instalado marginBottom40dp, NestedScrollView paddingBottom32dp/clipToPadding=false/fillViewport=true e corpo paddingBottom24dp. Dados Pessoais possui marginBottom100dp. A candidata usa scroll Compose e footer Scaffold: extensão inferior, scrollbar/fade, auto-scroll ao foco e posição de IME precisam captura física pareada. Não foram alteradas por cálculo aproximado a partir de screenshot redimensionado.

Campo52dp/raio4dp, rótulo16sp/bold, helper16sp, warning20dp e texto14sp existentes foram preservados. BasicTextField e TextInputLayout não têm medição nativa idêntica garantida; raster do cursor/seleção/teclado/borda de foco deve ser pareado. Fonte Caixa e DP/SP devem usar density/fontScale reais, evitando trocar dimensões literais por ajustes de1px.

Erros inline do Cadastro e do suporte ainda não têm equivalência comprovada com RedirectNetwork/dialog runtime do APK oficial. Fixture local não prova redirecionamento401, mensagem do servidor ou gravação. Confirmações, login/biometria e provider OAuth continuam fora desta certificação.

Gradle e ADB foram executados pelo agente principal; esta subtarefa leu os logs e artefatos resultantes. `git diff --check -- feature/account feature/support` verifica somente integridade textual. Resultados coordenados estão abaixo; não há declaração100%.

## Primeira execução coordenada

O agente principal executou `AccountRemainingParityCaptureTest` no XCover7: Cadastro passou; Dados Pessoais encontrou a mensagem mínima na árvore semântica, mas abaixo do viewport com IME aberto. O teste passou a executar `performScrollTo` na mensagem mínimo/máximo/helper válido antes de exigir visibilidade e capturar. Isso corrige a preparação explícita do cenário; não altera auto-scroll de produção nem certifica seu comportamento contra o oficial. A segunda execução descrita abaixo validou a preparação corrigida. Log coordenado: `/tmp/remaining-account-xcover.log`.

## Validação final coordenada

- XCover7, `AccountRemainingParityCaptureTest`: **2 testes passaram**, `/tmp/remaining-account-green-xcover.log`, `OK (2 tests)`. Fixtures completas produziram13PNG e13TXT em `build/verification/remaining-installed-20261005/account-green/`.
- XCover7, `SupportHtmlParityCaptureTest`: **3 testes passaram**, `/tmp/remaining-support-xcover.log`, `OK (3 tests)`. Seis capturas em `build/verification/remaining-installed-20261005/support-green/`. HTML entregue ao WebView foi verificado contra o controlador mapeado; nenhum termo foi aceito.
- Build afetado e JVM coordenados: `BUILD SUCCESSFUL` em `/tmp/remaining-final-green-validation.log`. Resultados XML de `feature/account/build/test-results/testDebugUnitTest/` confirmam55 testes, zero failures/errors; suporte confirma14 testes, zero failures/errors.
- Cinco gates executados e aprovados no mesmo log: `verifyComposeMigration`, `verifyCaixaDS`, `verifyLegacyUiInventory`, `verifyCleanArchitecture`, `verifyLegacyBackendParity`. Esses gates não medem igualdade raster nem medição de foco/scroll com o oficial.

Inspeção das capturas candidatas `personal-limit-minimum-ime.png` e `registration-bottom-unchecked.png` confirma que o erro mínimo é visível com teclado decimal aberto, footer Atualizar acima do teclado; cadastro mostra checkbox nativo desmarcado/marcado e CTA disabled claro. Isso confirma o cenário produzido, sem transformar imagens redimensionadas do visualizador em medidas pixel/DP. As capturas do aviso usam scroll explícito do teste; não comprovam autoscroll equivalente ao oficial.

Nenhuma gravação real, cadastro real, aceite real, envio externo ou alteração adicional de produção foi necessário para estes resultados. Ainda faltam os pares oficiais nos mesmos estados/dados sintéticos de campos inferiores, foco/IME, erro, consentimentos e HTML para concluir paridade visual. Viewport/fade/scroll, widgets de campo, tema do checkbox e mensagens/redirects runtime permanecem sujeitos às limitações descritas acima.
