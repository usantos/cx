# feature:marketplace-filter

Tela Compose e contrato de eventos do filtro de bolões do Marketplace.

Os componentes públicos são `MarketplaceFilterRoute` e `MarketplaceFilterScreen`. Eles não acessam diretamente rede ou persistência; a configuração deve chegar pelo estado produzido pelo caso de uso/ViewModel.

A feature não declara Activity nem Fragment. A única Activity fica no composition root `:app`; toda UI de feature é Compose.

`betOptions` deve ser produzido a partir de `qtdeMinimaApostaBolaoFiltro` e `qtdeMaximaApostaBolaoFiltro`, recebidos de `/parametros/configuraveis-app`; não existe intervalo padrão fixo na UI.

O task `check` executa testes, lint e uma barreira Jacoco mínima de 95% sobre reducer e mapeadores.

## Auditoria de fidelidade ao legado — 15/10/2026

A captura recebida preserva a estrutura do filtro: carrossel horizontal, lotérica, valores e quantidades de cotas, seis colunas de quantidades de apostas e seletores de dezenas/números. Isso não comprova identidade visual com o aplicativo legado. A comparação direta usa `cx/app/src/main`, incluindo os layouts e o código que escolhe cada layout em execução.

| Elemento | Evidência no legado | Resultado da revisão |
| --- | --- | --- |
| Cartões de modalidade | `res/layout/item_filtra_modalidade.xml:11–16, 52–53, 80–81` | 110dp, margem/raio/elevação de 8dp, trevo e rodapé de 33dp já preservados. |
| Quantidade de apostas | `controllers/FiltraMarketplaceActivity.java:339–342` → `view/config/DezenaConfig.java:21–27` → `res/layout/item_dezena.xml:6–12` | Corrigido de círculos de 29dp/linhas de 45dp para círculos de 45dp/linhas de 53dp; o XML usa 4dp de preenchimento acima e abaixo. A captura mostra a versão anterior menor. |
| Cartela do seletor numérico | `controllers/QueroNaoQueroActivity.java:135` → `res/layout/item_dezena_quero_nao_quero_cartela.xml:11–16` | Mantidos círculos de 29dp/linhas de 45dp. São diferentes da quantidade de apostas no legado. |
| Seleção de quantidade de dezenas | `controllers/QueroNaoQueroActivity.java:159–161` | Tocar novamente na quantidade atual mantém a seleção, como no legado. A exclusão continua disponível no botão da seção. |
| Campo de quantidade mínima de cotas | `res/layout/activity_filtra_marketplace.xml:266–272` | Corrigido início de 10dp para 12dp; rótulo continua em 10dp. |
| Contorno dos campos | `res/drawable/button_white_with_rouded_black_border.xml:8–9` | Corrigido raio de 4dp para 5dp; espessura continua em 1dp. |
| Validação de cotas | `res/layout/activity_filtra_marketplace.xml:333–359` | Corrigidos tamanho de 10sp para 12sp, peso para negrito e margem superior de 3dp para 10dp. |
| Trevo selecionado em campanha | `view/adapter/recyclerview/ListaModalidadesAdapter.java:135–138` | A feature solicita `FILTER_SELECTED_CLOVER` ao DS e usa o recurso retornado. A resolução de campanha, cores e recursos pertence ao DS. |
| Cartões de concursos especiais | `view/adapter/recyclerview/ListaModalidadesAdapter.java:117–126`, `utils/EstiloModalidadeMKP.java` e `model/bo/silce/dto/ModalidadeEnum.java:222–240` | Corrigidos fundo especial selecionado por meio do resolver do DS e rótulos canônicos de duas linhas. O trevo da campanha permanece acima do fundo especial, seguindo a ordem do legado. O texto enviado ao filtro não recebe quebras de linha. |
| Remoção de números selecionados | `res/layout/item_dezena_quero_nao_quero.xml:30–41` e `res/drawable/ic_btn_fechar_qnq.xml` | Substituído o badge aproximado por recurso do DS copiado do legado, com 16dp e sobreposição de 7dp à direita/4dp acima. |

`MarketplaceFilterScreenTest.quantityOfBets_preservesLargerLegacyCellsThanNumberDialog` registra a diferença entre as duas cartelas. Os seis testes Android da feature passaram na validação integrada de 15/10/2026, incluindo essa verificação de dimensões. Os resultados, a captura revisada e as limitações estão em [OCTOBER_ROSE_COMPOSE_VALIDATION_20261015.md](../../docs/OCTOBER_ROSE_COMPOSE_VALIDATION_20261015.md).

Na auditoria anterior ainda era necessária comparação visual pareada, com a mesma densidade, escala de fonte, configuração, modalidade e estado de campanha. O espaçamento adicional de 4dp do texto auxiliar agora é medido pelo DS com a fonte original e verificado contra um TextView nativo. Pontos pendentes de medição: métricas gerais de fontes, estados de erro, busca de lotéricas, posição dos seletores e rodapé. Os eventos parametrizados Mega 30 Anos/Loteca Dia dos Pais no legado dependem também de número de concurso e texto configurável (`EspecialUtils.java:17–32`); a integração atual reconhece nomes recebidos do serviço no resolver do DS, mas não possui essas chaves de configuração históricas. Não declarar paridade de pixels antes dessa verificação.


## Comparação pareada posterior — 15/10/2026

A referência nativa foi capturada com os mesmos dados e dispositivo do Compose. Superfície, campos, botões, cartelas e seleções agora consomem os componentes `CaixaDSMarketplaceFilter*`; os trevos/checks/ícones originais também pertencem ao DS. Foram comparados cinco estados de Filtros com Outubro Rosa ligada/desligada, junto a cinco estados de Bolão em cada campanha. Resultados e limites: [relatório pareado](../../docs/MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md).
