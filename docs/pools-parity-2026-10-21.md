# Bolões — validação complementar de 21–22/10/2026

## Referência e segurança da execução

Oráculo principal: APK nativo 1.25.0 instalado como `br.gov.caixa.loterias.apostas.cxv2validation`, confrontado com `/Users/ubiracysantos/Projects/cx`. A cópia `/Projects/legado/simlo-app-android` foi usada inicialmente, mas seus XMLs antigos de detalhe não correspondem ao APK. A decisão final preserva botão de compra de 48 dp, largura disponível menos 24 dp em cada lado, aviso de arredondamento e rodapé de três colunas do APK/CX. Metadados em `build/verification/pools-complete-parity-20261021/oracle.json`.

Nenhuma aposta, pagamento, reserva, alteração de favoritas ou outra mutação foi enviada ao backend real. Regras de inclusão, confirmações e falhas usam use cases controlados. O runner do APK nativo substitui todas as filas de rede: apenas GETs conhecidos recebem fixtures; método diferente de GET ou leitura desconhecida é rejeitado, sem transporte real de fallback. As consultas autenticadas no celular usam o backend real, Home do Legado, sem Chucker ou log de rede.

## Matriz de cenários

| Área | Cenário / contrato | Verificação |
|---|---|---|
| Valores do filtro | Digitação em centavos, valor restaurado, exclusão e conversão para query decimal | Unitários e UI de filtros |
| Lotérica no filtro | Selecionar / limpar; recuperar modalidade anterior e limpar dependentes | Unitários e UI de filtros |
| Modalidades | Disponíveis sem jogo em parâmetros; ranges oficiais; Super Sete/Loteca identificados por ID | Unitários de mapper e validação |
| Limites | Quantidade mínima/máxima, inversão, valores grandes, quero/não quero | Testes de filtros e regras |
| Concursos temporários | Mega 30 anos/Loteca Pais por número disponível e tipo especial; falha opcional sem bloquear | Testes de provider, mapper, lista e detalhe |
| Busca regional | Município → UF → nacional com número da lotérica preservado | Testes de contrato domínio/repositório |
| Paginação e recarga | GET tardio não substitui pesquisa nova; append cancelado não se mistura; cabeçalho antigo cancelado | Testes de coordenadores |
| Sessão | Lista e detalhe aguardam login; continuação e chave da sessão | UI com grafo de navegação de produção |
| Favoritas | Inclusão abre aba após GET; remoção atualiza cartões; GET antigo não restaura removida; loading modal | Use cases controlados e UI |
| Vazios e erros | Ops, card, mensagem nativa; diferença entre pesquisa inicial e filtro aplicado; lotérica vazia | Unitários e UI |
| Detalhe | Carregamento bloqueia interação; primeiro resume sem GET duplicado; erro de atualização preserva conteúdo | Unitários e UI |
| +Milionária | Trevos null distintos de []; até três novas consultas por vida do detalhe | Mapper e ViewModel controlados |
| Cotas esgotadas | Aviso nativo e retorno somente após Entendi | Unitários e UI controlada |
| Quantidade e valores | Limites 1..disponível; última cota + demais no total exibido; corpo por origem lista/detalhe | Unitários de apresentação e repositório |
| Layout | Lista expandida, detalhe, cores das dez modalidades, artes especiais e footer | Métricas APK, capturas e UI |
| Jogos especiais | Time, mês, trevos, Super Sete e partidas Loteca | Capturas do APK e UI controlada |

## Evidência

Resultados finais, contagens, execução no celular e limites estão registrados abaixo após as reexecuções integradas. Logs RED documentam divergências antes de cada correção e não representam o resultado final.

Diretório desta rodada: `build/verification/pools-complete-parity-20261021`. Capturas de detalhe usam dados inteiramente artificiais e incluem auditoria das consultas interceptadas. Capturas com coordenadas inválidas por overflow do driver são descartadas e substituídas por execução com rolagem nativa limitada.

## Diferenças demonstradas e corrigidas

- Máscara monetária nativa em centavos, restauração da seleção anterior ao limpar lotérica, catálogo disponível sem parâmetros de jogo e validação de limites grandes antes da conversão para Int.
- O número da lotérica permanece uma restrição independente da área: município, UF e nacional seguem sua progressão. Não força tipoConsulta 3.
- Leituras antigas são canceladas/invalidadas ao reiniciar a busca. Voltar do filtro sem edição também refaz página zero e limpa cotas selecionadas; a chave do ViewModel inclui o modo de pesquisa aplicado.
- Toda consulta de bolões, inclusive próxima página, apresenta modal bloqueante. Inclusão de favorita recarrega e abre a aba; exclusão remove os cartões correspondentes e não pode ser desfeita por resposta antiga.
- Cards vazios/erro, mensagem de favoritas e mensagem da lotérica sem bolões seguem textos, fontes e margens do CX. Busca inicial e filtro aplicado conservam mensagens distintas.
- +Milionária mantém a diferença entre trevos null e lista vazia. Apenas null na primeira aposta permite até três GETs adicionais por vida do detalhe; atualizações posteriores não reabrem esse orçamento. Falha ao atualizar conserva o detalhe e a quantidade anteriores.
- Cotas esgotadas apresentam o aviso nativo e só retornam à lista ao Entendi. Esse cenário foi executado com respostas controladas.
- O detalhe exibe última cota + demais cotas comuns, enquanto seu request usa última cota × quantidade, conforme ApostaUtils do CX. A lista conserva a própria fórmula de request. Nenhuma inclusão foi enviada ao serviço real.
- Nomes temporários usam o concurso de cada consumidor: available no filtro, próprio bolão na lista/detalhe e simulação no header da lista. O título do header da lista aplica Loteca dos Pais em uma linha; Mega mantém seu título anual nesse consumidor. O detalhe de Mega 30 anos usa duas linhas e o de Loteca dos Pais uma.
- Restauradas as medidas do botão e footer do APK ativo; corrigidos cores dos jogos, inset do cabeçalho, geometria da favorita, trevos, colunas do Super Sete, legenda e proporções dos seletores da Loteca. O anel não marcado usa as mesmas duas camadas do drawable nativo, cobertas por comparação de pixels. O divisor da Loteca é ancorado na base do texto do visitante, evitando que a altura maior do seletor acrescente espaço a cada partida.

As diferenças de layout foram reproduzidas antes da correção. Exemplos: botão mediu 570 px contra 1.136 px esperados na largura do teste; metadados do header tinham inset 20 px contra 12 px; trevo estava 131 px abaixo do label contra 72 px; anel da Loteca apresentou erro médio 61,09 antes de reproduzir a composição das duas camadas. Esses logs RED são históricos.

## Verificação final

Compilação integrada passou (`BUILD SUCCESSFUL`, 967 tarefas). Passaram 361 testes JVM nas seis suítes verificadas, sem falhas, erros ou ignorados. Os 89 testes instrumentados distintos passaram: 69 de Bolões e 20 dos filtros. Os dois cenários de retorno dos filtros foram repetidos dez vezes adicionais (20 execuções), todas aprovadas. Quatorze casos de captura controlada foram reexecutados, abrangendo dez modalidades, especiais, Mega 30 anos, Loteca dos Pais e Outubro Rosa. Após a última correção, os dois casos de Loteca foram capturados novamente e a suíte completa de Bolões foi reexecutada; todos passaram. A remoção posterior do espaço final de 16 dp somente na Loteca passou em nova compilação, 12 testes focados e nas duas capturas normal/Pais.

Passaram `verifyCleanArchitecture` (inclui módulos e política do host), `verifyComposeMigration`, `verifyCaixaDS`, `verifyKotlinFormatting`, `verifyLegacyBackendParity`, `verifyNetworkSecurity` e `git diff --check`. Os dois drawables nativos de rádio e o drawable-oráculo de teste estão registrados no inventário de desenhos conforme a política existente; nenhum layout XML foi adicionado. Foi removido somente um espaço no fim de uma linha de um teste de introdução preexistente, apontado pela verificação de formatação.

A conferência complementar em 450 dpi confirmou no APK nativo fonte de 18sp com 51 pixels e de 14sp com 39 pixels. O detalhe passou a reproduzir essa conversão. Os spans de prognósticos/cotas conservam os tamanhos fracionários nativos; a Lotofácil especial aplica 80% sobre o tamanho XML já convertido. Os círculos numéricos reproduzem o viewport 41, centro, raio e stroke do vetor original. A captura física também revelou dois pontos de integração corrigidos: status bar do detalhe branca com ícones escuros, restaurando azul ao sair; ausência da legenda extra Surpresinha, que o JogoBolaoHolder nativo não apresenta, conservando os números armazenados.

A comparação final também demonstrou intervalo de 392 pixels entre partidas da Loteca, contra 382 pixels no APK em densidade 3. O teste reproduziu a divergência e passou após posicionar o divisor pela base do texto visitante, como no ConstraintLayout nativo, mantendo as dimensões e a composição dos seletores. O respiro final de 16 dp, ausente no APK, foi removido somente da Loteca para alinhar também o fim da rolagem. A captura final confirma o último mandante em y=1910 e visitante em y=2123..2204, mesmos limites do APK, sem corte.

Uma falha intermitente de retorno do filtro foi reproduzida na sexta execução de diagnóstico: rota/flag/revisão corretas e GET de erro concluído, mas o estado voltava a `isLoading=true/errorMessage=null`. A causa era uma atualização concorrente de favoritas/cabeçalho feita sobre estado antigo. Todos os escritores do mesmo MarketplaceUiState agora usam atualização atômica com dados atuais; lançamentos, HTTP e callbacks permanecem fora do CAS. Os testes determinísticos usam latches para forçar a disputa. Com a implementação atômica passaram; uma contraprova temporária do padrão anterior de leitura/cópia/escrita falhou nos dois asserts esperados. Os arquivos de produção foram restaurados byte a byte imediatamente após essa contraprova e recompilados na versão final. A revisão cruzada não apontou novos problemas concretos nesses escritores.

Resultados agregados: `jvm-summary.json`, `ui-summary.json`, `app-build.json`. O build integrado está em `simlo-pools-final-corrected-build.log`; após a correção final da Loteca, `simlo-pools-final-loteca-build.log` confirma nova compilação do app/testes e gates; `simlo-pools-final-loteca-spacing-build.log` registra o último ajuste do espaço final e `simlo-pools-final-loteca-focused-ui.log` sua revalidação de 12 testes. A suíte final de 69 testes de Bolões está em `simlo-pools-final-69-ui.log`; filtros e dez repetições adicionais estão em `simlo-pools-final-filter-stress.log`. Os logs foram copiados para o diretório de evidência. Logs anteriores que falham são históricos e não substituem esses resultados. Capturas e demais evidências ficam em `build/verification/pools-complete-parity-20261021`.

## Celular e limites de cobertura

Versão final instalada por atualização do mesmo pacote no Samsung (1080×2408, 450 dpi, fonte 1,0). Backend real, Home do Legado, sem Chucker, inspetor ou log de rede; as quatro flags foram conferidas no BuildConfig e o SHA-256 do APK está registrado. O fluxo normal de autenticação retomou a consulta sem exigir novo login manual.

Consultas reais verificadas: lista inicial, filtro das três modalidades disponíveis para a conta (Mega-Sena, Quina e Lotofácil), máscara digitada 123 → 1,23 e restauração sem nova escala; detalhe, rolagem de jogos e retorno; quantidade local 1 → 2 com total R$170,10 e retorno a 1; favoritas e consulta dos bolões de uma lotérica favorita em outra UF, sem alterar essa favorita. Na versão final, confirmados novamente leitura, detalhe com status bar branca, ausência da legenda extra, carrinho com total R$0,00 e contador zero e consulta da Lotofácil. A consulta real não apresentou erro nessa versão. Após a última atualização, a lista real voltou a carregar e foi deixada aberta com um bolão expandido, controles locais e botão de inclusão visíveis, sem acionar a inclusão.

Não foram realizadas apostas, pagamentos, reservas, exclusões ou alterações reais de favoritas. Esgotamento, falhas, mutações, confirmações e demais modalidades indisponíveis na resposta real foram exercitados com dados e serviços controlados. A evidência cobre a matriz descrita, o aparelho conectado e os perfis instrumentados; não é certificação universal de todos os aparelhos, combinações ou estados futuros do backend. Modo histórico LEITURA completo não é um destino desse grafo de Bolões e não foi introduzido nesta revisão.
