# Favoritas — fonte literal de 2026-10-05

Destino `betting/favorites`; implementação `FavoriteBetsRoute.kt`. Referência atual: `FavoritasActivity`, `FavoritaHolder`, `SelecaoModalidadesRecyclerAdapter`, `ModalidadeHolder`, `AdicionarCarrinhoFavoritasView`; layouts `activity_favoritas`, `app_bar_custom_questions`, `content_favoritas`, `linearlayout_favoritas`, `linearlayout_tipos_apostas_arredondadas`, `dialog_grid`, `dialog_adicionar_carrinho_favoritas`, `super_sete_layout`, `item_dezena_detalhe_super7`; cores/strings/drawables desses layouts.

Correções desta rodada:

- Cabeçalho sólido, ajuda original25dp; textos Caixa com métricas naturais e includeFontPadding; descrição18sp/cinza110404B52, filtro16/cinza9064747A, valor18/cinza13022292E e linhasD0E0E3.
- Estado global vazio: texto literal azul18sp e grid2colunas; células62dp, margens4dp, raio8dp, trevo20dp/start12 e textoFuturaBold16. Loteca excluída exatamente por `criaListTipoAposta`.
- Estado modalidade vazia: mantém descrição/filtro; mensagem literal16sp e CTA337x44/8dp; removidos título/descrição/ação extra “Selecionar outra modalidade” inexistentes no layout.
- FilterDialog trocado de checkbox/lista M3 instantânea para janela90% da tela com cabeçalho literal, grid2colunas e Cancelar/Filtrar. Seleção permanece local até Filtrar, que chama o mesmo `viewModel.selectModality`.
- Linhas: nome16semiBold/start20/top10; números abaixo10dp; controles abaixo6dp, caixas30dp, espaço8dp, desenhos26x24 e17x24. Informações adicionais16sp/cinza110; divisor8dp.
- Banner55dp/top15, trevo28dp/start18/top10, títuloFuturaBold20. Trapezoides originais copiados por modalidade; LayerDrawable original é desenhado pelo Canvas Android, preservando offsets/rotação do XML, sem substituir por retângulo.
- SuperSete deixa de concatenar colunas: cabeçalhos25dp, trilhas41dp, números30dp com cores e borda originais; matriz mantida.
- Exclusão e sucesso usam mensagens literais, incluindo dois-pontos antes do nome e “Aposta favorita inserida com sucesso”.
- Inclusão usa diálogo branco8dp/padding22/18/22/22, título literal16bold, cabeçalhos50dp/borda8dp/margens20, NumberPicker Android ao expandir com margem superior de 20dp; Tipo de Concurso habilita Adicionar após o toque; no caso original de concurso normal único com teimosinhas, esse cabeçalho fica oculto e Adicionar começa habilitado; checkbox espelho com texto literal, CTAs60dp/cancelarblue005CA9/adicionarteal54BBAB/desabilitadoD7E0E2. Seleções usam os mesmos setters; confirmar/cancelar permanecem nos handlers existentes.

ViewModel ajustado apenas no default/seleção recorrente: opções do concurso selecionado, ordem fornecida pelo serviço, primeiro valor real selecionado como no picker original; troca de concurso redefine o valor e seleção inválida retorna à primeira opção real. A inclusão continua ocorrendo apenas no handler explícito Adicionar. Domínio, endpoints, sessão e lote de revisão preservados. Não há ação simulada nem sucesso local novo. Não executados Gradle ou dispositivo nesta revisão. Diff-check e parsing de dez novos desenhos XML apenas verificam integridade estática.

Limitações de certificação: comparação pareada de todos estados ainda pendente; autoSize de TextView, animação dos pickers500ms, altura wrap-content e recorte de textos longos precisam validação com a mesma densidade/fontScale; comportamento no caso retorno de parâmetros especiais/Teimosinhas vazias precisa par e pode exigir ajuste da superfície. A incoerência anterior de default recorrente 0 foi corrigida. Dois testes de contrato cobrem lista sem 0/em ordem remota e troca de concurso, verificando que o valor indexado pelo picker, selecionado no estado e enviado ao carrinho coincidem; incluídos, mas não executados nesta rodada sem Gradle. Não declarar100% com estes checks.

Novos recursos: somente dez `drawable/legacy_favorite_trapeze_*.xml` (layer-list/shape/rotate originais, cores resolvidas literalmente a partir de values/colors.xml); nenhum XML de layout. IDs mapeados conforme estilos legados e DS:3Quina,7SuperSete,8Lotofácil,9Milionária,11DiaSorte,16Lotomania,18Dupla,19Loteca,20Timemania, demaisMegaSena.


## Diálogo de favoritar simulação — captura física v3

Helper separado `BettingFavoriteDialog.kt`, origem `layout_dialog_favoritar.xml`/`DialogUtils.buildDialogFavoritar`: AppCompatEditText com mínimo48dp aplicado após inputType; botões Material com padding horizontal16dp dos recursos mtrl_btn_padding_left/right, preservando buttonBarButtonStyle, fontes, labels e handlers. Par `legacy-simulation-favorite-dialog.png` / `compose-simulation-favorite-dialog-v3.png` do mesmo aparelho. Coordenação informou comparação do recorte raw (90,790)-(990,1545): 679499 de679500 pixels idênticos. O pixel remanescente no canto (90,790) varia somente canal verde224/223 por antialias sobre fundo distinto; conteúdo interno coincidente. Nenhum favorito confirmado no legado e nenhuma transação realizada para a captura. Evidência limitada ao diálogo/estado/escala capturados, sem alegação de igualdade100% do app ou do fluxo completo.
