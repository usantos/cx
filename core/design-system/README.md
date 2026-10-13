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

- Não declare cor hexadecimal, espaçamento ou ícone Material diretamente em features.
- Prefira nomes semânticos (`brand`, `error`, `Back`) a detalhes visuais (`Blue700`, `ArrowBack`).
- Todo ícone acionável precisa de descrição de conteúdo; ícones decorativos usam `null`.
- Um novo padrão visual recorrente deve nascer aqui, com preview, documentação e teste quando houver regra verificável.
