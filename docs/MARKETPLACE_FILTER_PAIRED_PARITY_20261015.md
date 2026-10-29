# Bolão e Filtros — comparação pareada com o CX

Revisão de 15/10/2026, no checkout `codex/migracao-compose`. Esta rodada substitui a pendência anterior de captura nativa de Filtros e de Bolão carregado.

## Escopo e método

Foram capturados **20 pares**: dez estados com Outubro Rosa e os mesmos dez sem a campanha. Os dois aplicativos usaram o AVD `SimloComposeGates` (`emulator-5558`), Android 16, 1280×2856, 480dpi e fonte 1,0. O APK CX de referência é `br.gov.caixa.loterias.apostas.cxv2validation`.

O oráculo continua usando suas Activities, layouts, holders e drawables originais. Um APK separado de instrumentação, assinado com a mesma chave de debug, abre as Activities internas e fornece dados sintéticos por uma fila Volley de teste. Não foi necessário exportar Activities nem modificar os fontes do CX. O driver prepara parâmetros, flags e carrinho pelo estado/setters reais; não desenha elementos substitutos.

O Compose recebe o mesmo JSON em `core/network/src/main/resources/marketplace-parity-fixture.json`. O conjunto contém quatro modalidades e seis bolões Mega-Sena, concurso 2900, prêmio de R$ 35 milhões, três apostas de seis dezenas, quatro cotas disponíveis de dez e valor de R$ 12,50 por cota. O carrinho contém uma aposta e total de R$ 35,00. Os números de dezenas foram comparados com Mega-Sena explicitamente selecionada nos dois aplicativos.

As consultas de município têm os seis bolões; estado/país retornam lista vazia, evitando repetir os mesmos códigos nas áreas seguintes. Uma captura inicial revelou essa repetição no mock. O teste de contrato reproduziu a falha e a fixture foi corrigida, sem modificar as regras de sessão ou paginação de produção.

A status bar e a toolbar Compose conservam o azul solicitado pelo produto, `#005CA9`. O CX instalado apresenta status bar branca e toolbar `#0066B3`; essa diferença deliberada é preservada. As métricas excluem essas barras e os elementos variáveis do sistema, sem deslocar ou redimensionar as imagens comparadas.

## Diferenças corrigidas e centralização

| Área | Correção |
|---|---|
| Filtros — superfície e campos | Fundo branco, títulos pretos, texto auxiliar/hints `#3A4859`, tipografia e bordas nativas. |
| Filtros — modalidades | Trevos originais de dez modalidades, estado claro/escuro, precedência da campanha e check original de 11×8dp. |
| Filtros — quantidade de apostas | Célula de 49×53dp, botão de 45dp alinhado ao início de cada span, vetor nativo com viewport 41 e texto Caixa Bold 14sp `#404B52`. |
| Filtros — seletores | Plus original de 22dp, sem achatamento pela medição; altura de 27dp e offsets dos ícones conforme o XML. |
| Filtros — dezenas e seleção | Círculos nativos de 29dp, linhas de 45dp, fonte 14sp, seleção azul `#005EA8`, “06” com dois dígitos, exclusão sobreposta e espaçamento do grupo selecionado. |
| Filtros — ações | Botões com cantos de 4/5dp, espaço inferior de 5dp, “Limpar Filtros” e exclusão em maiúsculas/centralizada; fechamento do diálogo com recurso original. |
| Bolão — header e abas | Ícones originais de voltar/filtro/ajuda/busca, posição das ações, cores das abas, indicador triangular e metadados do concurso. |
| Bolão — cabeçalho e cards | Trevos/trapézios originais, recursos de campanha, degradê ancorado no conteúdo da Activity e coração nativo. |
| Bolão — lista | Truncamento nativo da lotérica/cidade, fonte Futura no resumo, duas linhas de cotas/apostas, preço 16sp preto, seta e divisores originais. |
| Bolão — expansão | Textos com spans de 18/16sp, Detalhes, disponibilidade, quantidade, aumentar/diminuir e botão Adicionar ao Carrinho com geometria legada. |
| Bolão — favoritas e fim | Alinhamento/altura dos itens e ações, ícone original de lixeira, rodapé “Fim da lista” e 88dp de conteúdo inferior. |

Os componentes, tokens, fontes e recursos corrigidos pertencem a `:core:design-system`: `CaixaDSMarketplaceComponents`, `CaixaDSMarketplaceArtwork`, `CaixaDSMarketplaceFilterComponents` e `CaixaDSMarketplaceFilterArtwork`. As features conservam estado, regras e callbacks. O inventário explícito de drawables foi atualizado; não houve redução de limiares ou exclusão de verificações.

## Pares e métricas

Artefatos: `build/verification/marketplace-paired-20261015/`. Cada pasta `pairs` contém a imagem nativa à esquerda, Compose à direita e uma diferença RGB amplificada 4×. As imagens integrais estão em `legacy`/`compose-after` e nas variantes `-normal`; JSON/XML registram as hierarquias.

Exemplos revisáveis: [Filtros com seleção](../build/verification/marketplace-paired-20261015/pairs/filter-selection.png), [Bolão expandido sem campanha](../build/verification/marketplace-paired-20261015/pairs-normal/pool-expanded.png).

A métrica é SSIM local de luminância com janela 7×7, sem registro/deslocamento, acompanhada de erro RGB médio e proporção de pixels idênticos. SSIM não representa percentual de conclusão funcional nem certifica igualdade de todos os pixels. Permanecem diferenças de rasterização/antialiasing, sombras e scrollbar que podem ser inspecionadas nas diferenças amplificadas.

| Estado | Outubro Rosa SSIM | Sem campanha SSIM |
|---|---:|---:|
| `filter-initial` | 0.988559 | 0.988552 |
| `filter-bottom` | 0.986356 | 0.986323 |
| `filter-quantity-dialog` | 0.984828 | 0.984828 |
| `filter-quantity-selected` | 0.984927 | 0.984927 |
| `filter-selection` | 0.985805 | 0.985816 |
| `pool-initial` | 0.970270 | 0.970030 |
| `pool-list` | 0.978367 | 0.978262 |
| `pool-expanded` | 0.978012 | 0.977874 |
| `pool-favorites` | 0.993362 | 0.993362 |
| `pool-modality` | 0.977617 | 0.977297 |

## Validação

| Verificação | Resultado final |
|---|---|
| `ANDROID_SERIAL=emulator-5558 ./gradlew modernCheck releaseCheck --offline --continue --no-daemon` | PASS — 12m34s; 2.155 tarefas; três APKs debug e três Releases minificados. |
| Testes JVM dos tasks executados | 1.699 testes; zero falhas, erros ou skips. |
| Android incluído no integrado | Preferências 27, apresentação 3, carrinho 54, Home 4 e notificações 5: 93 testes. |
| Android adicional | DS 13, Bolão 21, Filtros 10 e Home V2/drawer do host 9: 53 testes. |
| Total Android desta rodada final | 146 testes; zero falhas, erros ou skips, restritos ao AVD dedicado. |
| Cobertura dos 20 relatórios dos escopos exigidos | Gates preservados; mínimo observado 95,46%; Bolão 100% e Filtros 97,98% nos respectivos escopos de lógica. Não é cobertura global de UI. |
| Proteção de campanhas | 27 testes JVM do DS aprovados, incluindo regressões contra trevos/trapézios verdes sobre Setembro Azul. |
| Restauração dos APKs e hashes | PASS — três debug restaurados sem mock (`HAPPY_PATH`, `HOME_V2=true`); hashes dos seis APKs registrados em `restored-apks.json`. |

Os 20 PNGs/hierarquias foram conferidos contra os hashes registrados e os dois APKs usados nas capturas. Os 180 arquivos da atestação de fontes continuam idênticos; o buffer de crashes da rodada final está vazio. O PRD debug foi reempacotado na restauração e seu SHA difere do registrado logo após o integrado; os outros cinco APKs mantêm o SHA. Não se afirma identidade binária do PRD debug restaurado. A restauração passou em `restore-debug.log`, sem mock nos três flavors.

Os relatórios detalhados estão em `integrated.log`, `jvm-results.json`, `android-results.json`, `coverage-results.json`, `filter-final.log` e `host-final.log` na pasta de artefatos. A execução Android do host selecionou explicitamente `HomeDrawerUiTest`/`HomeV2UiTest`; os demais testes Android do host não são declarados cobertos.

O primeiro teste de seleção encontrou dois nós “06” após a correção do formato nativo. O clique foi restringido ao diálogo e os dez testes de Filtros foram repetidos com sucesso; a asserção de manutenção da seleção foi preservada. O `check` do módulo também passou novamente. O log intermediário permanece em `android-final.log`.

Uma tentativa integrada foi interrompida para proteger a campanha Setembro Azul e outra por falta do seletor de dispositivo. A rodada definitiva usou `ANDROID_SERIAL=emulator-5558`; os logs cancelados foram preservados, sem tratá-los como aprovação.

A revisão independente preservou os callbacks de autenticação/backend e apontou a ausência de “Fim da lista”; o rodapé foi corrigido e ganhou regressão instrumentada. As regressões de fundo/alinhamento de Filtros e resumo/rodapé de Bolão tiveram falha reproduzida antes das correções.

## O que ainda falta para os 100% da migração

O bloqueio de abrir diretamente Activities não exportadas foi resolvido pelo runner de instrumentação, e Bolão carregado/Filtros deixam de depender de capturas de erro. Esta evidência cobre os estados pareados abaixo, sem certificar todos os demais fluxos da aplicação.

Continuam fora desta rodada as integrações reais de SSO/pagamento/resgate/hardware, os estados privados/transacionais sem pares, os dois casos raster históricos e metadados dinâmicos de especiais como Mega 30/Loteca dos Pais. O estado global segue em `MIGRATION_STATUS.md` e `COMPOSE_COMPLETION_CHECKLIST.md`. Não se declara 100% global nem equivalência binária de pixels. O AVD dedicado foi encerrado após a validação.
