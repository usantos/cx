# Home clássica — conferência com o Legado em 21/10/2026

Referência: `/Users/ubiracysantos/Projects/legado/simlo-app-android/app/src/main` e captura `build/verification/exact-layout-20261005/legacy-home-open.png` do mesmo XCover7 (1080×2408, density450, font scale1).

## Correções demonstradas

- `PrincipalActivity.montarCarrossel`: ScaleTransformer mínimo0,8 nos dois eixos, sem espaçamento adicional entre itens. O card central permanece280dp; vizinhos são224dp.
- `card_modalidade.xml`: nome imediatamente após trapézio85dp; título de resultado centralizado independentemente da seta; margens18dp na divisória e19dp nas linhas; divisória8dp abaixo da legenda.
- Texto XML: o Android lê textSize com `getDimensionPixelSize` (TextView.readTextAppearance). No aparelho13,8sp resulta39px. Uma sonda carregou os recursos do APK original, sem instalar outro app: letterSpacing0, textScaleX1, fontPaddingtrue. A conversão para pixels inteiros foi aplicada aos tamanhos XML; os tamanhos Java `setTextSize` do prêmio e extenso mantêm suas frações. A legenda, que antes tinha638px no original e618px no Compose, passou a ter o mesmo limite de glifos da referência.
- `HomeViewHolder`: estado sem estimativa em um único texto com linhas vazias, número18dp, legenda invisível preservando altura; Loteca usa data de fechamento; Loteca/Lotogol mostram Encerramento; venda finalizada mostra Apostas/Encerradas.
- `LoteriasBaseAppActivity`: barras claras e ícones escuros na Home clássica. O fundo do conteúdo não invade a área branca da navegação. A Home V2 conserva a aparência anterior.
- `content_principal.xml`, `AnimacoesAcessoRapido` e `AnimacaoUtils`: atalhos294dp (199texto+95ícone), início10dp, linhas mínimas60dp, fontes/cópias originais; deslocamento proporcional à janela e ao rodapé invisível, em vez de404dp fixos. Os dois botões desaparecem durante os atalhos; retorno aguarda a sequência de fades2500ms e a translação500ms. O trevo fica desabilitado durante as transições. As animações usam o interpolador cosseno original.
- Cards especiais: Combo233×327dp; Instantânea246×186dp, espaços dos campos invisíveis e apenas INSTANTÂNEA em negrito.
- Repassômetro: projeção48% da arrecadação acumulada mais48% da arrecadação semanal proporcional ao tempo, com a animação original.
- Resultado expandido: formatação/data, números ordenados/100→00, duas extrações da Dupla Sena, colunas Super Sete, trevos, time/mês, faixas/ganhadores/prêmios, regiões e arrecadação. Loteca/Lotogol usam o catálogo real de escudos já existente; falha de rede usa o recurso original, sem interromper cancelamentos.

## Evidências e limites

Captura Compose com os mesmos valores principais da referência: `build/verification/home-legacy-parity-20261021/compose-matched-data.png`. Sonda original: `native-caption-metrics.txt`. Diferença bruta: `reference-diff.png` (inclui barras do sistema e conteúdo dos vizinhos não pareados; não é uma porcentagem de equivalência global).

A comparação confirma os valores e a geometria dos elementos examinados. Não se declara certificação pixel a pixel de todos os estados possíveis do backend. A decoração de Outubro Rosa e o comportamento de Aposta Shake solicitados pelo usuário continuam preservados; o código de campanha não foi localizado nos checkouts de referência, portanto não foi substituído por uma aproximação.

Validação final: Home JVM56, Results Data JVM7, Home Android26 no XCover7; total89 testes, zero falhas/erros/ignorados. assemblePrdDebug concluído com sucesso. Relatório em `build/verification/home-legacy-parity-20261021/test-summary.json`. Configuração de entrega: `homeV2=false`, `mockBackend=false`, `networkLogging=false`.
