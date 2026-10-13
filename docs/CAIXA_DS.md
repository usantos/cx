# CaixaDS — Design System Compose

## Objetivo

O CaixaDS padroniza a experiência visual e de interação do aplicativo. Ele fica em `:core:design-system`, não conhece regras de negócio e pode ser consumido por todos os módulos de feature. O Material 3 é a base técnica, mas não é o contrato público das features: o contrato é `CaixaDS`.

## Instalação no módulo

```groovy
dependencies {
    implementation project(':core:design-system')
}
```

No único host do aplicativo:

```kotlin
setContent {
    CaixaDSTheme(darkTheme = false) {
        LoteriasApp()
    }
}
```

O CaixaDS suporta modo escuro, mas o host do aplicativo usa explicitamente `darkTheme = false` para preservar a identidade clara do legado. Previews e testes do Design System podem escolher `false` ou `true`.

## Somador do carrinho

`CaixaDSCartSummary` é a faixa legada compartilhada pela Home e pelo carrinho. O host entrega
o total já calculado e a quantidade de apostas do snapshot, nunca `items.size` de cards agrupados.
O componente não consulta nem modifica dados. Um callback opcional permite abrir o carrinho:

```kotlin
CaixaDSCartSummary(
    total = cart.total,
    betCount = cart.betCount,
    onClick = { onOpenCart() }, // omitir dentro da própria tela do carrinho
)
```

Total zero/negativo não compõe a faixa. Caso o layout preserve espaço, reserve no host
`CaixaDSCartSummaryTokens.Height` (36 dp). A Home usa `isCartSummaryVisible(total)` também
para seus insets. O contador mostra `...` acima de 999, conforme a string original; sua semântica
mantém o número completo. Tipografia, dimensões, guia proporcional e cores institucionais são
definidas pelo CaixaDS; o fundo vetorial não depende de XML em runtime.

Há exemplo no catálogo, testes JVM de apresentação e testes Compose. Compilação e regras
aprovadas não substituem captura no dispositivo: ver `CART_SUMMARY_PARITY_AUDIT.md`.

## Ações do carrinho

`CaixaDSCartActions` preserva os dois CTAs do legado, com tamanhos 240 × 56/70 dp, raios 4/5 dp,
intervalo de 14 dp e margem inferior de 15 dp antes do somador. O CTA de pagamento tem duas linhas,
com apenas “Avançar” em negrito. O componente não calcula elegibilidade por valor ou quantidade:

```kotlin
CaixaDSCartActions(
    onContinueBetting = onContinueBetting,
    onPayment = viewModel::requestPayment,
    paymentEnabled = snapshotLoaded && !processing,
)
```

Carrinho vazio não é motivo para desabilitar esse botão: a sequência de gates e a validação
pertencem à feature/Domain/serviço. Tokens públicos: `CaixaDSCartActionTokens`; exemplo no catálogo.
Ver `CART_CHECKOUT_ACTION_AUDIT.md` para fontes e limites de homologação.

## Arquitetura dos tokens

```text
CaixaDSColorTokens (paleta primitiva, uso interno do DS)
             ↓
CaixaDSColors (papéis semânticos claro/escuro)
             ↓
CaixaDS.colors (contrato usado pelas features)
             ↓
Componentes CaixaDS* e telas Compose
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
    color = CaixaDS.colors.brand,
    style = CaixaDS.typography.titleMedium,
)
```

Não use `CaixaDSColorTokens.Blue700` em feature. A paleta primitiva existe para implementar os papéis semânticos dentro do próprio Design System.

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
    modifier = Modifier.padding(CaixaDS.spacing.md),
    verticalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm),
) { /* conteúdo */ }
```

### Tipografia, formas, elevação e tamanhos

- `CaixaDS.typography`: escala Material 3 ajustada, de `displaySmall` a `labelMedium`.
- `CaixaDS.shapes`: cantos de 4, 8, 12, 16 e 28 dp.
- `CaixaDS.radii`: escala de raio para desenhos próprios e formato `pill`.
- `CaixaDS.elevation`: `none`, `low`, `medium` e `high`.
- `CaixaDS.sizes`: ícones, alvo mínimo de toque, altura de controle e dezena.
- `CaixaDSMotion`: durações `Fast`, `Standard` e `Emphasized`, em milissegundos.

A tipografia usa a família CAIXA Std já pertencente ao projeto, com pesos regular, semibold e bold. Os arquivos ficam exclusivamente no CaixaDS e são associados em `CaixaDSFontFamily`/`CaixaDSTypography`.

## Ícones

`CaixaDSIcons.PrizeBetDetail` preserva os caminhos da lupa com sinal de mais do legado (`ic_lupa_detalhe_aposta`), em 23 × 24 dp. Na ação “Ver detalhe da aposta”, use `Icon(CaixaDSIcons.PrizeBetDetail, contentDescription = null, tint = Color.Unspecified)` junto ao rótulo textual; a cor original está no vetor. Não substitua esse asset pela lupa genérica de busca.

### Identificação do tipo da aposta

`CaixaDSBetTypeIcons` contém `Exchange`, `Pool`, `Combo`, `Mirror`, `Surprise` e `Recurring`, convertidos dos seis assets `ic_*_linha`. Os caminhos, viewports, contornos e grupos de recorte do legado foram preservados; a faixa aplica a cor da modalidade com tint.

`CaixaDSBetTypeStrip` reproduz a faixa sem triângulo do `TipoApostaLinhaView`: recebe uma lista de `CaixaDSBetTypeItem`, quebra linhas sem cortar os rótulos e não ocupa espaço quando a lista está vazia. A feature decide quais tipos exibir e sua ordem; o componente não consulta backend nem interpreta regras de aposta.

```kotlin
CaixaDSBetTypeStrip(
    items = listOf(CaixaDSBetTypeItem("Bolão", CaixaDSBetTypeIcons.Pool)),
    backgroundColor = lotteryStyle.dark,
    contentColor = lotteryStyle.darkContent,
)
```

Use `CaixaDSLotteryStyle.darkContent` sobre o fundo escuro da modalidade, e `content` sobre o claro. As cores diferem em modalidades como Super Sete e Dia de Sorte; Timemania mantém seu texto específico também na faixa escura.

Use `CaixaDSIcons`, que dá nome semântico ao recurso visual:

| Ícone | Significado |
|---|---|
| `Back` | voltar na navegação |
| `Close` | fechar |
| `Confirm` | confirmar/concluído |
| `Add` / `Remove` | alterar quantidade |
| `Search` | pesquisar |
| `Information` / `Warning` | feedback e alerta |

```kotlin
CaixaDSIconButton(
    icon = CaixaDSIcons.Search,
    contentDescription = "Buscar concurso",
    onClick = onSearch,
)
```

Features não devem importar `androidx.compose.material.icons` diretamente. Isso mantém o desenho substituível sem alterar consumidores.

## Componentes

### Botões

`CaixaDSButton` oferece as variantes `Primary`, `Secondary`, `Tertiary` e `Danger`, além de estado desabilitado, carregamento e ícone inicial.

```kotlin
CaixaDSButton(
    text = "Finalizar compra",
    onClick = onPay,
    loading = state.isSubmitting,
    enabled = state.canPay,
    modifier = Modifier.fillMaxWidth(),
)
```

### Campos

```kotlin
CaixaDSTextField(
    value = state.contest,
    onValueChange = onContestChanged,
    label = "Concurso",
    isError = state.error != null,
    supportingText = state.error,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
)
```

### Estrutura e feedback

- `CaixaDSTopBar`: título centralizado e navegação acessível.
- `CaixaDSSectionTitle`: título de seção com divisor.
- `CaixaDSCard`: superfície e elevação padronizadas.
- `CaixaDSAlertDialog`: alerta simples com ação de confirmação.
- `CaixaDSLoading`: progresso com mensagem.
- `CaixaDSAuthenticationLoading`: conteúdo visual institucional; fundo e conteúdo aparecem juntos após 500 ms, com fade de 200 ms e rotação de 1.200 ms na curva AccelerateDecelerate do legado. Ícone de 44 dp e texto CAIXA Bold de 17 sp, com padding de fonte legado.
- `CaixaDSAuthenticationLoadingDialog`: versão modal para operações; bloqueia Voltar/toques externos desde a abertura, inclusive durante o atraso, sem escurecimento adicional da janela. Usada no login, retry de indisponibilidade, resgate Mercado Pago e preparação do cadastro Mercado Pago.
- `CaixaDSMessageState`: vazio/erro com ação opcional.
- `CaixaDSNumberBall`: dezena de loteria formatada com dois dígitos.
- `CaixaDSLoteriasLogo`: marca oficial para superfícies institucionais.
- `CaixaDSFingerprintIcon`: ícone Compose de biometria, sem vector drawable XML.

## Acessibilidade

O menu legado usa as extensões `CaixaDSIcons.DrawerResponsible`, `DrawerTerms`, `DrawerQuestions`, `DrawerLogout`, `DrawerChevron` e `DrawerLink`, importadas de `br.gov.caixa.loterias.core.designsystem`. Elas preservam paths e viewports dos vetores antigos; aplique `tint` no `Icon` conforme o contexto (marca nos itens, branco em Sair). Não substitua esses desenhos por ícones Material genéricos. `LegacyDrawerChild`, `LegacyDrawerDivider` e `LegacyDrawerInstitutionText` preservam as cores dos filhos, divisores e painel institucional. Esses ícones ainda precisam de inspeção no catálogo/captura do drawer em runtime.

- O alvo mínimo de interação é `CaixaDS.sizes.touchTarget` (48 dp).
- A cor nunca deve ser o único meio de comunicar um estado.
- Informe `contentDescription` orientada à ação em botões de ícone.
- Use estilos da escala tipográfica para respeitar o tamanho de fonte do sistema.
- Preserve a ordem lógica de leitura e textos explícitos nos estados de erro.
- Valide componentes nos temas claro e escuro e com fonte ampliada.

## Catálogo e previews

`CaixaDSNotificationSwipeRead` e `CaixaDSNotificationSwipeDelete` preservam os ícones legados das
ações laterais de mensagem (26 dp e 29 dp). Use as dimensões `defaultWidth/defaultHeight` e
`Color.Unspecified` no tint para manter o fundo roxo/vermelho e o desenho branco. Não substitua
pelos ícones azuis do cabeçalho. O token `LegacyNotificationUnreadTitle` distingue título não lido
do preto usado após leitura. O catálogo inclui os dois vetores; a comparação no APK está pendente.

`CaixaDSNotificationPlaceholder` é o `ImageVector` decorativo original do popup de notificação,
com sete paths/cores e viewport de 308 × 308. Use `Image(CaixaDSNotificationPlaceholder,
contentDescription = null, modifier = ...)`, preservando proporção e cores, sem `tint` de `Icon`.
Na Home o contêiner tem 200 dp de altura; o bitmap recebido por base64 substitui a ilustração.
O catálogo inclui uma amostra de 96 dp. Não embutir a regra de leitura/link/sessão no Design System.

Abra `CaixaDSCatalog.kt` no Android Studio. Os previews `CaixaDS claro` e `CaixaDS escuro` mostram tipografia, botões, campo e dezenas. Todo componente novo deve ganhar ao menos um estado representativo no catálogo.

## Processo de evolução

1. Verifique se já existe token ou componente equivalente.
2. Modele o nome pelo significado, não pela aparência.
3. Implemente no módulo `:core:design-system` sem dependência de feature.
4. Cubra invariantes testáveis e inclua estados no catálogo.
5. Atualize este documento quando o contrato público mudar.
6. Execute `./gradlew modernCheck` antes de integrar.

APIs `CaixaTheme` e `CaixaColors` existem somente como ponte de compatibilidade e estão marcadas como obsoletas. Código novo deve usar `CaixaDSTheme` e tokens semânticos.

## Tokens de reserva no carrinho

`CaixaDSColorTokens.LegacyReservationWarning` reproduz o amarelo do último minuto; `LegacyReservationExpired` indica textos/contador de reserva vencida em cinza; `LegacyReservationCancelled` destaca a legenda `Cota expirada`. Consuma-os conforme o estado calculado pela feature, sem inferir expiração pela cor. A comparação visual integral desses estados permanece em `CART_EXPIRY_PARITY_AUDIT.md`.

## Diálogo de entrada de texto

`CaixaDSTextInputDialog` reproduz `custom_dialog_edit_text.xml`: largura de 85%,
raio de 8 dp, recuo de 24 dp, título azul de 16 sp, separador e campo sublinhado,
além das ações Cancelar/Salvar de 55 dp. Usa as fontes CAIXA e permite rolagem
quando o teclado ou o tamanho da fonte reduz o espaço disponível.

O componente recebe valor, hint, callbacks, disponibilidade e erro; limite de nome,
validação e persistência pertencem à feature. Durante processamento, edição,
ações e fechamento ficam desabilitados. `CurrentCartRoute` utiliza esse componente
para o nome do carrinho favorito, com limite de 30 caracteres no ViewModel.
