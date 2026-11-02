# LoteriasCaixaDS — Design System Compose

## Objetivo

O LoteriasCaixaDS padroniza a experiência visual e de interação do aplicativo. Ele fica em `:core:design-system`, não conhece regras de negócio e pode ser consumido por todos os módulos de feature. O Material 3 é a base técnica, mas não é o contrato público das features: o contrato é `LoteriasCaixaDS`.

## Instalação no módulo

```groovy
dependencies {
    implementation project(':core:design-system')
}
```

No único host do aplicativo:

```kotlin
setContent {
    LoteriasCaixaDSTheme(darkTheme = false) {
        LoteriasApp()
    }
}
```

O LoteriasCaixaDS suporta modo escuro, mas o host do aplicativo usa explicitamente `darkTheme = false` para preservar a identidade clara do legado. Previews e testes do Design System podem escolher `false` ou `true`.

## Campos de texto compartilhados

`LoteriasCaixaDSTextField`, no pacote `components`, é a entrada pública para campos com estilo. Estado, erros de negócio, consultas de CEP e persistência continuam pertencendo ao consumidor.

| Variante | Apresentação |
|---|---|
| `OUTLINED` (padrão) | Label flutuante Material, mantendo o contrato anterior; aceita erro e texto de suporte. |
| `LABELLED` | Label externo com os tokens de formulário e campo com contorno; usado pelo adaptador `LoteriasCaixaDSLabelledField`. |
| `LEGACY` | Label externo de 16 sp, intervalo de 4 dp, linha de entrada de 52 dp e cores, check e estados de foco/erro do Legado. |

```kotlin
LoteriasCaixaDSTextField(
    value = state.zipCode,
    onValueChange = viewModel::changeZipCode,
    label = stringResource(R.string.zip_code),
    placeholder = stringResource(R.string.numbers_only),
    variant = LoteriasCaixaDSTextFieldVariant.LEGACY,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    isError = state.zipCodeErrorMessage != null,
)
```

A variante `LEGACY` também fornece `showValidation` (check em campos bloqueados) e `currencyInput` (dígitos como centavos BRL, cursor ao final após alteração do texto). Selecionar texto não emite alteração; a tela pode rejeitar um valor e receber a próxima tentativa normalmente. Valores substituídos pelo servidor são refletidos sem notificação artificial.

`LoteriasCaixaDSAccountField` e `LoteriasCaixaDSLabelledField` permanecem adaptadores compatíveis, delegando à mesma base. Edição de dados pessoais e cadastro usam `LoteriasCaixaDSTextField` diretamente; CPF, nome, UF, município e e-mail mantêm seus bloqueios, e CEP/limite preservam os callbacks e a validação da feature.

Campos com decoração própria — pesquisa, cartão, código e chaves de resgate — usam `components.base.LoteriasCaixaDSBasicTextField`. Seus overloads de `String` e `TextFieldValue` preservam as opções da primitiva Compose: seleção, transformação visual, fonte, cursor, teclado, IME, leitura e decoração. A aparência desses blocos não é uniformizada à força. `verifyLoteriasCaixaDS` impede acesso direto à primitiva fora do DS, inclusive por aliases e nomes qualificados.

## Aplicativo de exemplos

O aplicativo independente `:sample:design-system` usa o tema claro da CAIXA. Ele lista componentes por grupo, permite pesquisar por API/pacote/parâmetros e abre detalhes com demonstração, código de uso e assinaturas. Não depende do app de produto, autenticação ou backend. Instruções e limites estão em [sample/design-system/README.md](../sample/design-system/README.md).

## Somador do carrinho

`LoteriasCaixaDSCartSummary` é a faixa legada compartilhada pela Home e pelo carrinho. O host entrega
o total já calculado e a quantidade de apostas do snapshot, nunca `items.size` de cards agrupados.
O componente não consulta nem modifica dados. Um callback opcional permite abrir o carrinho:

```kotlin
LoteriasCaixaDSCartSummary(
    total = cart.total,
    betCount = cart.betCount,
    onClick = { onOpenCart() }, // omitir dentro da própria tela do carrinho
)
```

Total zero/negativo não compõe a faixa. Caso o layout preserve espaço, reserve no host
`LoteriasCaixaDSCartSummaryTokens.Height` (36 dp). A Home usa `isCartSummaryVisible(total)` também
para seus insets. O contador mostra `...` acima de 999, conforme a string original; sua semântica
mantém o número completo. Tipografia, dimensões, guia proporcional e cores institucionais são
definidas pelo LoteriasCaixaDS; o fundo vetorial não depende de XML em runtime.

Há exemplo no catálogo, testes JVM de apresentação e testes Compose. Compilação e regras
aprovadas não substituem captura no dispositivo: ver `CART_SUMMARY_PARITY_AUDIT.md`.

## Ações do carrinho

`LoteriasCaixaDSCartActions` preserva os dois CTAs do legado, com tamanhos 240 × 56/70 dp, raios 4/5 dp,
intervalo de 14 dp e margem inferior de 15 dp antes do somador. O CTA de pagamento tem duas linhas,
com apenas “Avançar” em negrito. O componente não calcula elegibilidade por valor ou quantidade:

```kotlin
LoteriasCaixaDSCartActions(
    onContinueBetting = onContinueBetting,
    onPayment = viewModel::requestPayment,
    paymentEnabled = snapshotLoaded && !processing,
)
```

Carrinho vazio não é motivo para desabilitar esse botão: a sequência de gates e a validação
pertencem à feature/Domain/serviço. Tokens públicos: `LoteriasCaixaDSCartActionTokens`; exemplo no catálogo.
Ver `CART_CHECKOUT_ACTION_AUDIT.md` para fontes e limites de homologação.

## Arquitetura dos tokens

```text
LoteriasCaixaDSColorTokens (paleta primitiva, uso interno do DS)
             ↓
LoteriasCaixaDSColors (papéis semânticos claro/escuro)
             ↓
LoteriasCaixaDS.colors (contrato usado pelas features)
             ↓
Componentes LoteriasCaixaDS* e telas Compose
```

### Cores semânticas

| Token | Uso |
|---|---|
| `brand` / `onBrand` | ação e superfície principal / conteúdo sobre ela |
| `brandStrong` | ênfase institucional forte |
| `accent` / `onAccent` | destaque secundário |
| `actionHighlight` / `onActionHighlight` | CTA quente institucional, como o acesso da tela de login |
| `authenticationBackground` | fundo institucional fixo das telas de autenticação |
| `loadingOverlay` | camada azul translúcida do loading de autenticação |
| `background` | fundo da janela |
| `surface` / `surfaceVariant` | cartões, painéis e agrupamentos |
| `textPrimary` / `textSecondary` | hierarquia de conteúdo |
| `divider` | separadores e contornos discretos |
| `success`, `warning`, `error`, `info` | estados semânticos e seus respectivos `on*` |

```kotlin
Text(
    text = "Prêmio estimado",
    color = LoteriasCaixaDS.colors.brand,
    style = LoteriasCaixaDS.typography.titleMedium,
)
```

Não use `LoteriasCaixaDSColorTokens.Blue700` em feature. A paleta primitiva existe para implementar os papéis semânticos dentro do próprio Design System.

### Espaçamento

| Token | Valor |
|---|---:|
| `none` | 0 dp |
| `xxs` | 2 dp |
| `xs` | 4 dp |
| `sm` | 8 dp |
| `md` | 16 dp |
| `lg` | 24 dp |
| `xl` | 32 dp |
| `xxl` | 48 dp |

```kotlin
Column(
    modifier = Modifier.padding(LoteriasCaixaDS.spacing.md),
    verticalArrangement = Arrangement.spacedBy(LoteriasCaixaDS.spacing.sm),
) { /* conteúdo */ }
```

### Tipografia, formas, elevação e tamanhos

- `LoteriasCaixaDS.typography`: escala Material 3 ajustada, de `displaySmall` a `labelMedium`.
- `LoteriasCaixaDS.shapes`: cantos de 4, 8, 12, 16 e 28 dp.
- `LoteriasCaixaDS.radii`: escala de raio para desenhos próprios e formato `pill`.
- `LoteriasCaixaDS.elevation`: `none`, `low`, `medium` e `high`.
- `LoteriasCaixaDS.sizes`: ícones, alvo mínimo de toque, altura de controle e dezena.
- `LoteriasCaixaDSMotion`: durações `Fast`, `Standard` e `Emphasized`, em milissegundos.

A tipografia usa a família CAIXA Std já pertencente ao projeto, com pesos regular, semibold e bold. Os arquivos ficam exclusivamente no LoteriasCaixaDS e são associados em `LoteriasCaixaDSFontFamily`/`LoteriasCaixaDSTypography`.

## Ícones

`LoteriasCaixaDSIcons.PrizeBetDetail` preserva os caminhos da lupa com sinal de mais do legado (`ic_lupa_detalhe_aposta`), em 23 × 24 dp. Na ação “Ver detalhe da aposta”, use `Icon(LoteriasCaixaDSIcons.PrizeBetDetail, contentDescription = null, tint = Color.Unspecified)` junto ao rótulo textual; a cor original está no vetor. Não substitua esse asset pela lupa genérica de busca.

### Identificação do tipo da aposta

`LoteriasCaixaDSBetTypeIcons` contém `Exchange`, `Pool`, `Combo`, `Mirror`, `Surprise` e `Recurring`, convertidos dos seis assets `ic_*_linha`. Os caminhos, viewports, contornos e grupos de recorte do legado foram preservados; a faixa aplica a cor da modalidade com tint.

`LoteriasCaixaDSBetTypeStrip` reproduz a faixa sem triângulo do `TipoApostaLinhaView`: recebe uma lista de `LoteriasCaixaDSBetTypeItem`, quebra linhas sem cortar os rótulos e não ocupa espaço quando a lista está vazia. A feature decide quais tipos exibir e sua ordem; o componente não consulta backend nem interpreta regras de aposta.

```kotlin
LoteriasCaixaDSBetTypeStrip(
    items = listOf(LoteriasCaixaDSBetTypeItem("Bolão", LoteriasCaixaDSBetTypeIcons.Pool)),
    backgroundColor = lotteryStyle.dark,
    contentColor = lotteryStyle.darkContent,
)
```

Use `LoteriasCaixaDSLotteryStyle.darkContent` sobre o fundo escuro da modalidade, e `content` sobre o claro. As cores diferem em modalidades como Super Sete e Dia de Sorte; Timemania mantém seu texto específico também na faixa escura.

Use `LoteriasCaixaDSIcons`, que dá nome semântico ao recurso visual:

| Ícone | Significado |
|---|---|
| `Back` | voltar na navegação |
| `Close` | fechar |
| `Confirm` | confirmar/concluído |
| `Add` / `Remove` | alterar quantidade |
| `Search` | pesquisar |
| `Information` / `Warning` | feedback e alerta |

```kotlin
LoteriasCaixaDSIconButton(
    icon = LoteriasCaixaDSIcons.Search,
    contentDescription = "Buscar concurso",
    onClick = onSearch,
)
```

Features não devem importar `androidx.compose.material.icons` diretamente. Isso mantém o desenho substituível sem alterar consumidores.

## Componentes

### Botões

`LoteriasCaixaDSButton` oferece as variantes `Primary`, `Secondary`, `Tertiary` e `Danger`, além de estado desabilitado, carregamento e ícone inicial.

```kotlin
LoteriasCaixaDSButton(
    text = "Finalizar compra",
    onClick = onPay,
    loading = state.isSubmitting,
    enabled = state.canPay,
    modifier = Modifier.fillMaxWidth(),
)
```

### Campos

```kotlin
LoteriasCaixaDSTextField(
    value = state.contest,
    onValueChange = onContestChanged,
    label = "Concurso",
    isError = state.error != null,
    supportingText = state.error,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
)
```

### Estrutura e feedback

- `LoteriasCaixaDSSectionTitle`: título de seção com divisor.
- `LoteriasCaixaDSAlertDialog`: alerta simples com ação de confirmação.
- `LoteriasCaixaDSLoading`: progresso com mensagem.
- `LoteriasCaixaDSAuthenticationLoading`: conteúdo visual institucional; fundo e conteúdo aparecem juntos após 500 ms, com fade de 200 ms e rotação de 1.200 ms na curva AccelerateDecelerate do legado. Ícone de 44 dp e texto CAIXA Bold de 17 sp, com padding de fonte legado.
- `LoteriasCaixaDSAuthenticationLoadingDialog`: versão modal para operações; bloqueia Voltar/toques externos desde a abertura, inclusive durante o atraso, sem escurecimento adicional da janela. Usada no login, retry de indisponibilidade, resgate Mercado Pago e preparação do cadastro Mercado Pago.
- `LoteriasCaixaDSNumberBall`: dezena de loteria formatada com dois dígitos.
- `LoteriasCaixaDSLoteriasLogo`: marca oficial para superfícies institucionais.
- `LoteriasCaixaDSFingerprintIcon`: ícone Compose de biometria, sem vector drawable XML.

## Acessibilidade

O Drawer institucional usa `LoteriasCaixaDSDrawerSheet`, `LoteriasCaixaDSDrawerHeader`, `LoteriasCaixaDSDrawerItem` e `LoteriasCaixaDSDrawerDefaults`. O fundo combina `Blue500`, `Blue700` e `Blue900`, com textos claros, subitens translúcidos e rodapé escuro. O cabeçalho reutiliza `LoteriasCaixaDSLoteriasLogo`, a mesma marca do login, e oferece fechamento acessível. Essa apresentação foi solicitada em 15/10/2026 e substitui intencionalmente o visual branco do menu legado, quando não há campanha ativa; as opções, expansões e confirmações continuam com os contratos anteriores. Campanhas recebidas do backend usam `LoteriasCaixaDSCampaignTheme` e alteram a paleta do Drawer sem modificar as ações.

Os itens preservam os desenhos históricos e as extensões `LoteriasCaixaDSIcons.DrawerLogout` e `DrawerChevron`, com tint claro sobre o azul. Os tokens `DrawerChild`, `DrawerDivider` e `DrawerInstitutionText` continuam disponíveis para componentes que precisem da apresentação histórica.

- O alvo mínimo de interação é `LoteriasCaixaDS.sizes.touchTarget` (48 dp).
- A cor nunca deve ser o único meio de comunicar um estado.
- Informe `contentDescription` orientada à ação em botões de ícone.
- Use estilos da escala tipográfica para respeitar o tamanho de fonte do sistema.
- Preserve a ordem lógica de leitura e textos explícitos nos estados de erro.
- Valide componentes nos temas claro e escuro e com fonte ampliada.

## Catálogo e previews

`LoteriasCaixaDSNotificationSwipeRead` e `LoteriasCaixaDSNotificationSwipeDelete` preservam os ícones legados das
ações laterais de mensagem (26 dp e 29 dp). Use as dimensões `defaultWidth/defaultHeight` e
`Color.Unspecified` no tint para manter o fundo roxo/vermelho e o desenho branco. Não substitua
pelos ícones azuis do cabeçalho. O token `NotificationUnreadTitle` distingue título não lido
do preto usado após leitura. O catálogo inclui os dois vetores; a comparação no APK está pendente.

`LoteriasCaixaDSNotificationPlaceholder` é o `ImageVector` decorativo original do popup de notificação,
com sete paths/cores e viewport de 308 × 308. Use `Image(LoteriasCaixaDSNotificationPlaceholder,
contentDescription = null, modifier = ...)`, preservando proporção e cores, sem `tint` de `Icon`.
Na Home o contêiner tem 200 dp de altura; o bitmap recebido por base64 substitui a ilustração.
O catálogo inclui uma amostra de 96 dp. Não embutir a regra de leitura/link/sessão no Design System.

Abra `LoteriasCaixaDSCatalog.kt` no Android Studio. Os previews `LoteriasCaixaDS claro` e `LoteriasCaixaDS escuro` mostram tipografia, botões, campo e dezenas. Todo componente novo deve ganhar ao menos um estado representativo no catálogo.

## Processo de evolução

1. Verifique se já existe token ou componente equivalente.
2. Modele o nome pelo significado, não pela aparência.
3. Implemente no módulo `:core:design-system` sem dependência de feature.
4. Cubra invariantes testáveis e inclua estados no catálogo.
5. Atualize este documento quando o contrato público mudar.
6. Execute `./gradlew modernCheck` antes de integrar.

APIs `CaixaTheme` e `CaixaColors` existem somente como ponte de compatibilidade e estão marcadas como obsoletas. Código novo deve usar `LoteriasCaixaDSTheme` e tokens semânticos.

### Variantes preservadas do CX — consolidação de 14/10/2026

- `LoteriasCaixaDSOptionDialog`: quantidade/Teimosinha; viewport de 170dp, nenhuma escolha inicial, Confirmar desabilitado até tap/drag, medidas nativas e tags originais. O cinza desabilitado reutiliza `AccountHelper` com alpha 0,6.
- `LoteriasCaixaDSBettingButton`: botão do volante/Loteca; mantém fontes CAIXA, borda, ícone, arredondamento inteiro em pixels e variantes de tamanho em dp/sp.
- `LoteriasCaixaDSAccountField`, `LoteriasCaixaDSAccountInlineWarning`, `LoteriasCaixaDSAccountCheckbox`, `LoteriasCaixaDSAccountChoice`, `LoteriasCaixaDSAccountCopy` e `LoteriasCaixaDSAccountSaveFooter`: apresentações compartilhadas de cadastro/dados pessoais. Choice recebe duas opções genéricas; `Gender`, limites, validações e callbacks de negócio permanecem na feature. O campo de moeda preserva centavos, espaços e seleção no fim do texto.
- `LoteriasCaixaDSAccountTextStyle` e `LoteriasCaixaDSAccountBackground`: fonte/métricas e gradiente antes repetidos nos consumidores.
- `LoteriasCaixaDSHomeV2Theme`: encapsula a Typography Material3 e fontScale 1 usados pelo CX; aplicar essa variante conserva a geometria da Home sob o tema do aplicativo.
- `LoteriasCaixaDSSupportTopBar` e `LoteriasCaixaDSHtmlDocument`: barra sólida e renderização somente leitura do HTML original, com JavaScript desabilitado e descarte do WebView. A montagem do documento e o estado do serviço permanecem em suporte.

Os previews estão em `LoteriasCaixaDSParityCatalog.kt`. Os testes existentes das features continuam exercitando os componentes públicos do DS e seus consumidores, sem cópias locais de implementação.

Medidas que posicionam seções de uma tela pertencem à composição dessa tela. Importar um primitivo Material, ou declarar um `dp` de layout, isoladamente não comprova divergência visual. Padrões reutilizados, paletas, fontes e temas devem ser centralizados; variantes legadas não devem ser substituídas por dimensões genéricas. Os controles básicos são consumidos pela API `components.base` do LoteriasCaixaDS. Layouts e blocos específicos continuam nas features, recebendo estado e usando componentes do DS.

`verifyLoteriasCaixaDS` rejeita cores literais, imports diretos de ícones Material, criação de Typography nos consumidores e declarações de componentes com prefixo LoteriasCaixaDS fora do módulo. O gate também rejeita controles Material3 importados/chamados diretamente e WebViews, AndroidView e widgets nativos construídos fora do DS. Tipos de estado de UI e anotações técnicas não são renderizadores e permanecem permitidos. O gate não certifica igualdade de pixels nem audita todos os argumentos de todo primitivo Compose.

## Tokens de reserva no carrinho

`LoteriasCaixaDSColorTokens.ReservationWarning` reproduz o amarelo do último minuto; `ReservationExpired` indica textos/contador de reserva vencida em cinza; `ReservationCancelled` destaca a legenda `Cota expirada`. Consuma-os conforme o estado calculado pela feature, sem inferir expiração pela cor. A comparação visual integral desses estados permanece em `CART_EXPIRY_PARITY_AUDIT.md`.

## Diálogo de entrada de texto

`LoteriasCaixaDSTextInputDialog` reproduz `custom_dialog_edit_text.xml`: largura de 85%,
raio de 8 dp, recuo de 24 dp, título azul de 16 sp, separador e campo sublinhado,
além das ações Cancelar/Salvar de 55 dp. Usa as fontes CAIXA e permite rolagem
quando o teclado ou o tamanho da fonte reduz o espaço disponível.

O componente recebe valor, hint, callbacks, disponibilidade e erro; limite de nome,
validação e persistência pertencem à feature. Durante processamento, edição,
ações e fechamento ficam desabilitados. `CurrentCartRoute` utiliza esse componente
para o nome do carrinho favorito, com limite de 30 caracteres no ViewModel.


### Status bar, tooltip e campanhas (15/10/2026)

`LoteriasCaixaDSStatusBar` centraliza o preenchimento azul CAIXA do inset superior; o host mantém ícones claros e a configuração nativa equivalente antes do primeiro frame. A Home preserva seu cabeçalho e status claros, conforme a regra específica do Legado. `LoteriasCaixaDSDrawerDefaults.itemTextStyle` é compartilhado entre os itens e Aposta Shake. `LoteriasCaixaDSAnchoredTooltip` deve ficar dentro do Box do ícone: calcula a posição abaixo do alvo, limita horizontalmente a bolha à janela e aponta a seta para o centro do ícone. O Drawer usa bolha de 280 dp, cantos de 20 dp e mensagem em duas linhas.

A ativação de campanhas fica no domínio; nomes e paletas ficam no DS. Veja `COMMEMORATIVE_CAMPAIGNS.md` para o contrato legado, a extensão opcional de gestão e as áreas atualmente atendidas. A status bar permanece azul durante as campanhas.


### Escopo completo de Outubro Rosa

O tema é fornecido no host de navegação a partir de `AppConfigurationRepository.campaignSettings`. `loteriasCaixaDSLotteryStyle` é um adaptador Compose sobre o resolvedor puro `resolveLoteriasCaixaDSLotteryStyle`: oferece `light`, `dark`, `content`, `darkContent`, `listText`, `numeral` e `textOnSurface`. O código da feature escolhe o papel, sem interpretar flags ou inventar cores. `loteriasCaixaDSLotteryClover(..., onDark)` e `loteriasCaixaDSCampaignDrawable(..., role)` usam os desenhos originais adequados ao fundo; tipos layer-list usam `loteriasCaixaDSDrawablePainter`. IDs desconhecidos mantêm o fallback normal sem adquirir identidade de Mega-Sena.

`LoteriasCaixaDSOctoberRoseRibbon` e `LoteriasCaixaDSOctoberRoseCampaignPanel` concentram a ação e o painel informativo. A Home não tem faixa permanente. Consulte `OCTOBER_ROSE_LEGACY_SCOPE_20261015.md` para as exceções e todos os consumidores auditados.

## Bolão e Filtros — comparação pareada de 15/10/2026

Os padrões corrigidos estão em `LoteriasCaixaDSMarketplaceComponents`, `LoteriasCaixaDSMarketplaceArtwork`, `LoteriasCaixaDSMarketplaceFilterComponents` e `LoteriasCaixaDSMarketplaceFilterArtwork`. Eles concentram as cores, fontes, geometria compartilhada e assets nativos de Bolão/Filtros; os consumidores fornecem estado e callbacks. Os recursos normais são resolvidos somente quando não sobrepõem uma campanha personalizada de Mega-Sena.

A evidência de 20 pares e os limites da rodada estão em [MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md](MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md).

### Ajustes visuais — 15/10/2026 (tutorial, modalidades e modais)

- `LoteriasCaixaDSModalTokens` e `Modifier.loteriasCaixaDSModalWidth()` definem 24dp de margem lateral e limite de 560dp, pelas restrições reais da janela. Os diálogos flutuantes compartilham esse contrato; telas de seleção em tela cheia e o overlay bloqueante de carregamento conservam sua função.
- `LoteriasCaixaDSBettingButtonDefaults.NativeVerticalInset` preserva os 6dp superiores/inferiores de desenho do MaterialButton legado, mantendo as áreas de toque de 52dp (rodapé), 45dp (seletores) e 50dp (contadores).
- `LoteriasCaixaDSPoolTutorialPrimary` e `LoteriasCaixaDSTutorialPreference` concentram os controles sobre a animação original, incluindo os drawables nativos de checkbox e a cor de seleção `#007BFF`. Estado, navegação e persistência permanecem na feature.


## Nomenclatura e estado compartilhado — 21/10/2026

Os tipos, arquivos Kotlin e APIs visuais do DS usam nomes de responsabilidade, sem o prefixo Legacy. A política de módulos rejeita tipos e arquivos de fonte com esse nome em app, core e features, incluindo testes e fachadas JVM explícitas. Recursos Android e fixtures de referência preservam seus identificadores e valores.

`LoteriasCaixaDSContentState` concentra a apresentação comum de loading, erro com nova tentativa e conteúdo, usada por Cart e Betting. Loading mantém precedência sobre erro; callbacks e espaçamentos originais foram preservados. Tokens semânticos de cores permanecem separados por finalidade, com valores compartilhados referenciando a paleta canônica. Variantes com diferenças de tipografia, raio, sombra, foco ou dimensões continuam com suas responsabilidades próprias.

## Simplificação KISS — 21/10/2026

Callbacks de eventos dos diálogos usam diretamente os parâmetros atuais da composição; rememberUpdatedState permanece reservado aos consumidores que precisam acompanhar callbacks dentro de efeitos ou capturas de longa duração. Primary e Danger compartilham a construção equivalente do Button com suas próprias cores; Secondary e Tertiary mantêm as implementações específicas. Testes conferem callbacks após recomposição, dimensões, cores antes de ripple, clique, loading e disabled. Detalhes em [KISS_MODULE_REVIEW.md](KISS_MODULE_REVIEW.md).


## Três famílias de cabeçalho

- Header Caixa: `LoteriasCaixaDSTopBar`, azul institucional com título16sp e seta12×18dp. Suporte, Jogos e Termo de Uso o reutilizam, sem tamanhos próprios.
- Header Home: `LoteriasCaixaDSHomeTopBar`, branco, com menu, notificações, campanha e trevo; recebe contagens, rotações e callbacks.
- Header customizado de modalidade: `LoteriasCaixaDSModalityTopBar`, com fundo, arte, tinta, título, concurso e sorteio entregues pela feature. Preserva as medidas nativas da simulação e a redução de fonte dos especiais.

A escolha de concurso/campanha e as ações de navegação permanecem nas features. Os três renderizadores pertencem ao DS; os antigos adaptadores de Home/modalidade apenas convertem o estado para propriedades visuais.

## Componentes básicos e WebViews

`components.base` concentra texto, ícones, botões, checkboxes, radio, cards, superfícies, separadores, diálogos, menus, scaffold, drawer, bottom sheet, progresso e pull to refresh. A migração preserva os parâmetros e defaults do renderer Material3 instalado. É permitido criar blocos específicos nas features; não é permitido contornar o DS chamando diretamente esses controles. Primitivos de layout/desenho do Compose continuam disponíveis para composição das telas.

`LoteriasCaixaDSWebPage` recebe URL, configuração explícita de JavaScript/back e um callback de console com texto. JavaScript é desativado por padrão; a fila solicita sua ativação. A criação, cliente e descarte da WebView ficam no DS. A fila mantém validação de URL, parser de eventos, broadcasts, prazo e saída da Activity. `LoteriasCaixaDSHtmlDocument` continua sendo o componente de HTML somente leitura. Os consumidores não recebem WebViews ou clientes nativos.

`LoteriasCaixaDSGateMessage`, `LoteriasCaixaDSStatusAction` e `showLoteriasCaixaDSMessage` concentram apresentações genéricas de status e feedback. Nenhum deles conhece regras da fila, autenticação, apostas ou pagamentos.

As regressões de propriedade são verificadas com `scripts/test-ds-ownership.py`; testes instrumentados comparam controles antes/depois e exercitam callbacks e WebViews locais sem backend financeiro.


### Animações institucionais

`LoteriasCaixaDSAnimation` concentra o renderizador Lottie. Recebe composição, progresso,
escala e substituições opcionais de imagens por camada. `LoteriasCaixaDSAnimationImage`
permite aproveitar uma região normalizada de uma arte do DS sem alterar seus arquivos:
o recorte remove transparência externa, preserva a proporção e ocupa o canvas do
asset original. As curvas de posição e opacidade continuam pertencendo à animação.
A Splash define as regiões da marca e seu ciclo de reprodução na feature; o DS não
conhece sessão, atualização obrigatória ou FilaBRA.

## Cartões de pagamento — componentes compartilhados

As telas de Meus Cartões, cadastro, prévia, CVV e confirmação compõem os
componentes de `core/design-system/components`. A feature fornece valores,
textos localizados, artes existentes e callbacks; não desenha outra versão dos
controles nem implementa processamento no DS.

| Componente | Responsabilidade |
| --- | --- |
| `LoteriasCaixaDSCreditCardFront` / `LoteriasCaixaDSCreditCardContent` | Frente do cartão e campos número/nome/validade; layout legado normal e reorganização em fonte ampliada. |
| `LoteriasCaixaDSCreditCardBack` | Verso, faixas, marca e lupa fornecida como arte. |
| `LoteriasCaixaDSCardSecurityCode` | Entrada numérica de três/quatro posições, sem validar compra ou enviar dados. |
| `LoteriasCaixaDSCardActionButton` | Botão verde de mínimo 50dp, texto completo e centralizado. |
| `LoteriasCaixaDSSavedCreditCard` | Cartão salvo, marca e ações independentes de abrir/excluir. |
| `LoteriasCaixaDSSavedCardSection` | Seção do processador e área de cartões. |
| `LoteriasCaixaDSCardProviderHeader` | Identificação do processador e callback de alteração. |
| `LoteriasCaixaDSCardTextInput` / `LoteriasCaixaDSCardPicker` | Entrada e seleção de dados do formulário. |
| `LoteriasCaixaDSCardHeading` / `LoteriasCaixaDSCardTextStyle` | Título e tipografia compartilhada do fluxo. |

Os dez componentes visuais possuem exemplos executáveis no catálogo do DS.
Os IDs publicados `simlo.pay.*` foram preservados ao mudar o proprietário visual;
continuam sendo usados pelas mesmas ações e pelos localizadores Robot.

## Migração da família para LoteriasCaixaDS

Toda a API do Design System usa o prefixo `LoteriasCaixaDS`: tema, componentes,
ícones, tokens e previews. Por exemplo, use `LoteriasCaixaDSTheme`,
`LoteriasCaixaDSButton`, `LoteriasCaixaDSIcons` e o objeto `LoteriasCaixaDS`.
Helpers seguem o prefixo camelCase `loteriasCaixaDS`. Os módulos
`:core:design-system` e `:sample:design-system` e seus pacotes continuam iguais.

Os recursos próprios usam `loterias_caixa_ds_*`; a migração conserva seus bytes.
IDs de automação, keys de estado, valores de enums e contratos externos são
preservados. Baselines e relatórios históricos conservam nomes antigos; esses
registros não são referências para escrever novos consumidores. O catálogo
reconhece a seleção salva com o identificador anterior ao restaurar a tela.

As constantes de duração são `LoteriasCaixaDSMotion.FAST`, `.STANDARD` e
`.EMPHASIZED`, com os mesmos valores anteriores. Foram removidos dois parâmetros
sem uso: `retainNativeAnimator` de `LoteriasCaixaDSBettingButton` e
`validationEmphasized` de `LoteriasCaixaDSAccountField`. Remova esses argumentos
dos consumidores; eles não modificavam a renderização.

Execute `./gradlew verifyLoteriasCaixaDS` para validar a propriedade visual.
O comando anterior `verifyCaixaDS` permanece como alias da mesma verificação.
A política continua reconhecendo ambos os prefixos para impedir que código
antigo contorne as restrições de uso dos componentes.

Os parâmetros `modifier` de `LoteriasCaixaDSLoading`,
`LoteriasCaixaDSSectionHeader` e `LoteriasCaixaDSChoiceRow` seguem a convenção
Compose: primeiro parâmetro opcional. Prefira argumentos nomeados nos
consumidores. `LoteriasCaixaDSOctoberRoseCampaignPanel` aceita `modifier` na
raiz e conserva `descriptionModifier` para medir/animar somente a descrição.
O offset animado do switch é lido na fase de layout e drawables são carregados
por `ResourcesCompat`, conservando recursos, tema e geometria.

### Validação da migração — 02/11/2026

Ktlint, Detekt, Android Lint, arquitetura e contratos aprovados; 3.625 testes
sem falhas, erros ou ignorados. JaCoCo LINE global de 95,37% (60.171/63.090),
sem novas exclusões. Testes Android de 16 módulos compilados e APKs TQS Debug
mais catálogo gerados. Oito capturas dos headers permanecem idênticas byte a
byte. As 40 baselines não foram alteradas. O precommit completo exige preparar
os fontes novos no Git; nenhuma alteração foi colocada no staging nesta tarefa.

## Sample organizado por tipos

Execute `:sample:design-system` para explorar os 131 componentes do catálogo.
A lista usa 16 tipos por finalidade, independentemente do pacote técnico:
botões e ações, campos, seleção, cartões, navegação, diálogos, feedback,
layout, textos, números/apostas, carrinho, pagamentos/Pix, marcas,
ícones, temas e Web/HTML. Os filtros mostram a quantidade total de cada tipo.
A busca por API, pacote ou parâmetro funciona em conjunto com o tipo selecionado.

Ao abrir um componente, o detalhe apresenta a prévia real interativa, o contador
de ações locais, o exemplo de uso, a descrição dos parâmetros e a assinatura.
“Copiar exemplo” e “Copiar assinatura” enviam exatamente o texto mostrado para
a área de transferência e confirmam com “Copiado”. A nota do exemplo descreve
as variáveis de estado e callbacks do playground; o trecho deve ser adaptado ao
host da feature, dentro do tema do DS. Não é uma aplicação independente nem
inclui o fonte interno do componente. Os exemplos usam tokens do DS.

Voltar pelo header ou pelo Android conserva busca, filtro e posição da lista.
A seleção do componente e a chave estável do tipo ficam no estado salvo da tela.
Não há chamadas de serviços, login, apostas ou pagamentos reais neste catálogo.

A implementação fica separada por responsabilidade:

- `CatalogScreen.kt`: host e estado local de navegação.
- `CatalogTypes.kt` e `CatalogRegistry.kt`: tipos, agrupamento e busca puros.
- `CatalogList.kt` e `CatalogTypeFilters.kt`: lista e seleção do tipo.
- `CatalogDetail.kt` e `CatalogCodePanel.kt`: prévia, documentação e cópia.
- `CatalogEntries*.kt`: metadados e categoria explícita de cada API.

Os IDs existentes de busca, lista, detalhe e componentes continuam iguais.
Filtros usam `simlo.catalog.type.<chave>` (por exemplo, `buttons`, `fields` e
`selection`), com `all` para todos. No detalhe, os botões têm IDs
`simlo.catalog.copy.usage` e `simlo.catalog.copy.signature`; seu escopo é o
componente atualmente aberto, sem incluir dados pessoais no identificador.

Validação desta reorganização em 02/11/2026: 14 testes do Sample e 3.628 testes
globais aprovados, sem falhas, erros ou ignorados. LINE global de 95,38%
(60.249/63.168); Lint, Ktlint, Detekt, arquitetura/IDs e APK aprovados.
Inspeção visual no emulador; sem instalação no Samsung. Baselines e headers
preservados. O precommit completo continua dependente do staging dos fontes novos.

A posição horizontal da faixa de tipos também pertence ao host do Sample.
Ela permanece salva enquanto o detalhe está aberto, junto da busca, seleção e
posição vertical dos componentes. O retorno pela seta ou pelo Android restaura
esse contexto, inclusive após recriar a Activity. A regressão é coberta por
`CatalogReturnNavigationTest`, usando um tipo fora da faixa inicialmente visível.

A confirmação “Copiado” dos botões do catálogo dura 3 segundos e depois restaura
o rótulo original (“Copiar exemplo” ou “Copiar assinatura”). Um novo toque
reinicia o prazo. O temporizador é cancelado ao sair do componente ou trocar
seu código; o conteúdo da área de transferência permanece intacto.
