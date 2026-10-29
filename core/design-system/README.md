# CaixaDS

Design System Compose do Loterias CAIXA. Este módulo é a fonte única de tokens, tema, ícones semânticos e componentes compartilhados do aplicativo.

## Uso rápido

```kotlin
CaixaDSTheme {
    Column(Modifier.padding(CaixaDS.spacing.md)) {
        CaixaDSSectionTitle("Minha aposta")
        CaixaDSButton(
            text = "Continuar",
            onClick = ::continueFlow,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
```

O `CaixaDSTheme` deve envolver o conteúdo no ponto de entrada do app. Nas features, use `CaixaDS.colors`, `CaixaDS.typography`, `CaixaDS.spacing`, `CaixaDS.radii`, `CaixaDS.elevation`, `CaixaDS.sizes` e os componentes `CaixaDS*`.

Consulte a documentação completa em [`docs/CAIXA_DS.md`](../../docs/CAIXA_DS.md). O arquivo `CaixaDSCatalog.kt` contém previews claros e escuros para inspeção no Android Studio.

## Regras

- Não declare cor hexadecimal nem ícone Material diretamente em features. Use tokens de espaçamento nos padrões compartilhados; medidas específicas de composição da tela podem permanecer no consumidor quando necessárias à paridade com o legado.
- Prefira nomes semânticos (`brand`, `error`, `Back`) a detalhes visuais (`Blue700`, `ArrowBack`).
- Todo ícone acionável precisa de descrição de conteúdo; ícones decorativos usam `null`.
- Um novo padrão visual recorrente deve nascer aqui, com preview, documentação e teste quando houver regra verificável.
- Variantes legadas de conta, volante, seletor, suporte e Home v2 estão documentadas em `docs/CAIXA_DS.md`; preserve suas métricas ao reutilizá-las.

## Bolão e Filtros

`CaixaDSMarketplaceComponents` reúne abas/indicador, toolbar, resumo e expansão de bolões, favoritas, degradê e rodapé. `CaixaDSMarketplaceFilterComponents` reúne campos, rótulos, células, seletores, números selecionados e botões. As APIs recebem valores/callbacks e não dependem de domínio ou rede.

Os resolvers de artwork preservam os recursos nativos de modalidades; a campanha tem precedência. Assets verdes normais não são usados por cima de uma campanha Mega-Sena personalizada. A comparação pareada está em `docs/MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md`.

Os modais flutuantes usam `Modifier.caixaDSModalWidth()` com `usePlatformDefaultWidth=false`: margens de 24dp e máximo de 560dp. O modificador atua antes dos mínimos de largura do Material, para respeitar também janelas estreitas. Os controles de aposta podem preservar os insets nativos com `verticalInset = CaixaDSLegacyBettingButtonDefaults.NativeVerticalInset`, sem reduzir a área de toque. Os controles do tutorial de Bolão ficam em `CaixaDSLegacyPoolTutorialComponents.kt`.
