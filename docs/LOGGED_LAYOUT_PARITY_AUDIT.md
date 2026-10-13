# Revisão de recursos e layouts do fluxo logado — 05/10/2026

Referência: `/Users/ubiracysantos/Projects/legado/simlo-app-android`.
Implementação: checkout atual em `/Users/ubiracysantos/Projects/simlo-app-android`.

A matriz `LOGGED_LAYOUT_RESOURCE_MATRIX.tsv` enumera todas as rotas do arquivo
`COMPOSE_ROUTES.txt`, seus layouts diretamente mapeados em `LEGACY_UI_INVENTORY.tsv`
e as referências de drawable, cor e fonte desses XMLs. Inventário de recursos não
é comprovação de equivalência visual. Includes e estados dinâmicos exigem a inspeção
dos respectivos controladores e capturas.

## Correções verificadas por referência de fonte

| Rotas/componentes | Referências legadas inspecionadas | Correção |
|---|---|---|
| Busca compartilhada | `ic_lupa.xml`, `app_bar_degrade_pesquisa*.xml`, `app_bar_degrade_push.xml` | Geometria original com viewport18×18, em lugar de Material Search com margens internas diferentes. |
| `tickets/purchases` | `list_group_compras.xml`, `ic_estrela.xml` | Estrela vazada original com stroke6 preservado, em lugar da estrela preenchida. |
| `tickets/bet-detail` | `content_detalhe_minhas_apostas_todas.xml`, `icon_editar_aposta.png`, `ic_carrinho2.xml` | Edição com PNG original por densidade; carrinho com paths originais; ambos36dp. |
| `cart/favorite-carts` | `row_carrinho_favorito.xml`, `ic_lupa_carrinho_fav.png`, `icon_carrinho.png`, `ic_icone_lixeira.xml` | Lupa e carrinho originais por densidade; áreas de ação40×70dp com imagem20dp. |
| `betting/favorites` | `linearlayout_favoritas.xml`, `ic_favoritas_carrinho.xml`, `ic_favoritas_lixeira.xml` | Carrinho com seta de inclusão e lixeira próprios da lista, preservando seus paths. |
| `betting/home`, `betting/modalities`, `betting/favorites` | `ModalidadeEnum.java`, trevos originais e temas das modalidades | IDs reais do backend resolvidos pelo CaixaDS; removidas letras substituindo trevos; cabeçalho de favoritas aplica cores e contraste da modalidade, corrigindo Mega-Sena2 interpretada como Lotofácil. Lotogol14 usa PNG original por densidade; textos sobre branco usam tokens de contraste. |
| `payment/pix-copy`, `payment/pix-expired` | `fragment_copia_cola_pix.xml`, botão `icon_atualizar` | Corrigido corte da segunda linha visto na captura: altura mínima48dp expansível pelo conteúdo, texto14sp e lineHeight18sp, seguindo wrap_content do legado. |
| `payment/cards`, cartão apresentado | `row_meu_cartao.xml`, `ic_icone_lixeira.xml` | Exclusão com ícone original, reutilizando CaixaDSLegacyDeleteIcon. |
| `retail/search` | `activity_buscar_lotericas.xml`, `item_buscar_favoritas.xml`, `BuscaLotericaViewHolder.java`, strings | Barra sólida20sp; input40dp e instrução exata; lista código/nome18sp sem cartões/endereço adicionais; coração PNG original24×20dp; divisores2dp; vazio alinhado à esquerda. Busca automática existente preservada. |
| `betting/rapid`, boas-vindas | `fragment_configuracao_rapidao_fragment1.xml`, `icone_rapidao.xml`, strings | Raio original100dp, separador150dp, textos e tamanhos exatos, sem estrela/CTA inventados. |
| `betting/rapid`, prêmio | `ConfiguracaoRapidaoFragment2.java`, `fragment_configuracao_rapidao_fragment2.xml`, `layout_opcao_premio_minimo.xml` | Seletor Compose0–100 milhões e sugestões originais0/20/40/60/80/100Mi; descrição original. |
| `betting/rapid`, valores | `ConfiguracaoRapidaoFragment3.java`, `fragment_configuracao_rapidao_fragment3.xml`, `custom_dialog_valor_aposta.xml` | Máximo antes de mínimo; presets circulares60dp derivados do valor mínimo selecionado e limite diário verificado; opção de valor digitado com máscara monetária por centavos e cursor ao final (MoneyTextWatcher); recuperação quando limites falham; removidos controles±5 inventados. |
| `betting/rapid`, modalidades | `ConfiguracaoRapidaoFragment4.java`, `TipoApostaHolder.java`, `linearlayout_tipos_apostas.xml` | Grade com duas colunas, tiles56dp, trevo18dp e Futura14sp; seleção por cor e borda. |
| `betting/rapid`, números | `ConfiguracaoRapidaoFragment5/6.java`, `fragment_configuracao_rapidao_fragment5.xml`, adapters e drawables circulares | Títulos/descrições exatos; números40dp, célula56dp e intervalo vertical17dp; obrigatórios em verde, proibidos em coral; tamanho natural sem corte de grade fixa900dp. |
| `betting/rapid`, favoritas | `ConfiguracaoRapidaoFragment7.java`, `fragment_configuracao_rapidao_fragment7.xml`, `linearlayout_aposta_favorita.xml`, adapter | Oitava etapa restaurada: carrega favoritas pelo caso de uso existente, seleciona por ID e preserva IDs salvos em falha/retentativa. |
| `betting/rapid`, resumo/navegação | `ConfiguracaoRapidaoActivity.java`, `fragment_configuracao_rapidao_fragment8.xml`, `activity_configuracao_rapidao.xml`, `ic_editar.xml` | Oito páginas; indicador inferior com áreas de toque48dp; Salvar na barra após acessar resumo; cartões100dp separados para mínimo/máximo e ações de edição que retornam à etapa correta. |

| `betting/combo-detail` | `CaixaDSLegacyCard` e captura atual do combo7 | Row e concurso antes se sobrepunham no Box do card; conteúdo agora agrupado verticalmente. |
| `betting/rapid`, indicador | Duas LaunchedEffects antigas e reprodução física de resumo8→valores3 | Observação de settledPage após scroll; navegação programática não realimenta páginas intermediárias. Teste Android cobre toque no centro dos indicadores8→3 e swipe com offset final zero nos dois dispositivos. O indicador distribui oito células pela largura para evitar toque na etapa vizinha. |

## Evidência local

- Fontes CaixaStd Regular/SemiBold/Bold e Futura Book/Bold do CaixaDS são idênticas em bytes às fontes legadas.
- As11 cópias por densidade de edição/lupa de carrinho são idênticas em bytes aos originais. Os corações também foram copiados diretamente do legado.
- Testes de RapidConfigurationViewModel verificam preservação de IDs, seleção/remoção, persistência, falha e retentativa, limite de sessão autoritativo, validação de valor digitado e ajuste de valor máximo baixo.
- Capturas finais em `build/verification/logado-layout-20261005/final-emulator` confirmaram trevos/paleta de Mega-Sena, label Pix completo e busca de lotéricas restaurada. A recaptura do combo7 confirma a correção da sobreposição. A navegação do indicador foi testada nos dois dispositivos e recapturada no XCover7: oito etapas, favoritas legíveis e retorno8→3 centralizado.
- Os oito estados do Rapidão em `final-xcover` foram inspecionados; grade de números e resumo são roláveis, portanto uma captura por página não verifica conteúdo abaixo do primeiro viewport.
- Capturas iniciais do emulador evidenciaram letras substitutas, cores incorretas de favoritas e corte no botão Pix; estas correções compilam, mas dependem de nova captura do APK atualizado para validação visual.
- `verifyCaixaDS` valida o contrato de cores, tema e documentação; não testa posicionamento ou renderização.

## Checklist de validação visual atual

- [x] Inventariar os recursos diretamente mapeados de todas as rotas da matriz.
- [x] Inspecionar as referências de XML/controlador dos componentes corrigidos na tabela.
- [x] Preservar assets originais e fontes, centralizando as conversões/tokens no CaixaDS.
- [x] Compilar módulos afetados e executar testes de domínio/presentation aplicáveis.
- [ ] Comparar capturas atuais de todas as rotas, incluindo os oito estados do Rapidão, no mesmo dispositivo, fonte, dados e insets.
- [ ] Conferir seletor de prêmio após swipe e clique de sugestão, edição monetária com teclado e seleção de favoritas em fluxo real.
- [ ] Homologar limites/seleção de favoritas com conta e backend reais.

Os relatórios existentes em `build/visual-audit` são históricos e não homologam esta
árvore. Capturas em mock provam somente os estados capturados. As demais rotas estão
inventariadas; sua equivalência integral depende da revisão de capturas e dos estados
reais. Não foi realizada interação com dispositivo por este agente.
