# Outubro Rosa e revisão de Filtros — 15/10/2026

> Complemento posterior: a comparação nativa pareada de Bolão/Filtros foi executada em [MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md](MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md). O texto abaixo preserva os limites da rodada anterior.

Continuação na árvore `codex/migracao-compose`, base `2cf348582`, preservando os ajustes anteriores do usuário. Fontes legadas consultadas somente para leitura em `/Users/ubiracysantos/Projects/cx/app/src/main`. A captura de Filtros fornecida pelo usuário representa a versão anterior às correções descritas abaixo.

## Resultado da revisão

A tela de Filtros preservava a estrutura geral, mas não estava integralmente equivalente ao legado. A auditoria corrigiu diferenças comprovadas no código-fonte; a homologação pixel a pixel depende de uma captura pareada do mesmo estado nativo.

| Item | Correção |
|---|---|
| Quantidade de apostas | Círculos de 45dp, em linhas de 53dp, como `item_dezena.xml`. O seletor de números mantém círculos de 29dp e linhas de 45dp; os dois componentes são diferentes no legado. |
| Campos | Raio de 5dp e início do campo de quantidade mínima de cotas em 12dp. |
| Validação de cotas | Texto de 12sp em negrito, com margem superior de 10dp. |
| Texto auxiliar da lotérica | Espaçamento extra de 4dp por linha, calculado pelo DS com as métricas da fonte original; teste compara a distância entre baselines com um TextView nativo. |
| Modalidades e concursos especiais | Trevos originais por superfície e estado, fundo especial selecionado e rótulos canônicos de duas linhas. O texto enviado ao serviço não recebe a quebra de apresentação. |
| Números escolhidos | Recurso original de remoção de 16dp, com a mesma sobreposição. Repetir o toque na quantidade atual mantém a escolha; listas de números continuam permitindo alternar a seleção. |

Referências detalhadas: [README de Filtros](../feature/marketplace-filter/README.md).

## Campanha e Design System

A [auditoria do legado](OCTOBER_ROSE_LEGACY_SCOPE_20261015.md) inventariou os 64 consumidores do estilo compartilhado, incluindo consumidores indiretos e exclusões. Não são 64 telas independentes. As regras continuam sendo ativadas pelo backend: a flag geral `vigenciaMesOutubroRosa` controla Home/Drawer; a customização de Mega-Sena exige também `vigenciaOutubroRosaMegaSena`.

O repositório publica a configuração observável e um provider acima da navegação fornece o tema a todas as rotas. A atualização ao retomar preserva a última configuração válida da sessão em caso de falha. Não há ativação automática apenas por ser outubro.

Paletas, identidade da modalidade, recursos e variantes ficam no DS. As features consomem papéis explícitos para fundo claro/escuro, texto, listas, dezenas, trevos, trapezoides e combos. Foram preservadas as diferenças entre carrinho antigo/atual, lista de apostas confirmadas, prêmio do detalhe de bolão e carrossel filtrado/não filtrado. Modalidades diferentes e IDs desconhecidos não recebem rosa por causa de um fallback verde. Concursos especiais mantêm suas artes próprias.

A Home não tem faixa permanente. O laço abre e fecha o painel informativo sob ação do usuário, com recursos e composição no DS. Header e status bar usam exatamente `Blue700` (`#005CA9`); a barra do sistema mantém ícones claros nas demais rotas. O Drawer conserva a aparência e o logo solicitados pelo usuário.

Os quatro achados da revisão independente foram corrigidos e revistos: trevos distintos conforme a superfície, isolamento dos aliases de Quina/Lotomania e de IDs desconhecidos, trevo não selecionado de Filtros e ação do laço na Home V2. O primeiro run integrado foi interrompido para incorporar esses achados antes da validação final. Os limiares e as dimensões verificadas pelos testes foram preservados.

A conferência visual posterior encontrou também os CTAs do destaque da Home V2 recebendo indevidamente a cor bordô do texto do card. A referência `CX HomeV2View.kt:288,295` conserva ambos com fundo branco e texto azul. As duas superfícies foram restauradas, a revisão independente confirmou a correção e um novo teste raster reproduziu a falha antes do ajuste. O teste raster passou após a correção, e a matriz do app foi revalidada no código final.

## Validação

Execução integrada de `modernCheck releaseCheck :feature:marketplace-filter:connectedDebugAndroidTest` aprovada em **21min29s**, com 2.186 tarefas (664 executadas, 21 de cache, 1.501 atualizadas). Os três Releases minificados, lint dos três flavors, inventários, verificações de arquitetura/DS e gates de cobertura passaram. A matriz JVM soma **1.691 execuções**, zero falhas, erros ou ignorados; inclui a repetição do host por flavor.

Após a correção isolada dos dois CTAs, `releaseCheck` passou novamente em **10min45s**. A matriz final do host passou em **2min20s**: `assembleDebug`, testes JVM e lint de PRD/TQS/Automação, todos os gates globais usados por `modernCheck` e `releaseCheck`. Os módulos de feature/core não receberam novas alterações após a execução integrada; os nove testes Android de Home V2/Drawer foram repetidos. Logs: `release-after-cta-fix.log`, `app-matrix-after-cta-fix.log` e `home-drawer-ui-final.log`.

| Suíte Android | Resultado confirmado |
|---|---|
| Preferências | 27/27 |
| Apresentação | 3/3 |
| Carrinho | 54/54 |
| Home da feature | 4/4 |
| Notificações | 5/5 |
| DS | 13/13 |
| Marketplace | 19/19 |
| Filtros | 6/6 |
| Home V2 e Drawer do host | 9/9 |

Os 13 testes do DS incluem comparação dos trevos com os recursos originais e medição nativa do espaçamento adicional de linhas. Os seis de Filtros incluem as dimensões distintas das duas cartelas, seleção, limpeza e parâmetros de envio. Os nove testes do host passaram após a correção dos CTAs, incluindo o painel sem faixa permanente e as cores dos dois botões. As suítes Android listadas somam 140 casos distintos aprovados. O smoke com `CAMPAIGN_OCTOBER_ROSE` passou em **80/80 rotas**, com processo e hierarquia Compose recém-lida por destino, sem novos ANRs ou crashes do app nos buffers preservados. Esse teste verifica inicialização/renderização básica, não todas as ações e estados de negócio.

Cobertura dos escopos exigidos: conta **97,7551%** (479/490 linhas), apostas **97,7033%** (1.021/1.045), carrinho **96,0601%** (1.853/1.929) e Filtros **97,9798%** (194/198). Os mínimos existentes foram preservados. Esses valores não representam cobertura global de todas as telas.

Na recaptura, a leitura nova da hierarquia expôs uma ocorrência de ANR de inicialização/foco que a reutilização do XML anterior poderia esconder. Foram preservados `current-device.png`, `latest-app-anr.txt`, o dump completo e os logs. A thread principal estava executando composição, com pressão de CPU registrada em 61,21% no AVD; isso não estabelece sozinho a causa. Após encerrar as compilações, a captura com hierarquia nova da Home e a interação com o Drawer passaram; as demais capturas também foram refeitas com sucesso. A tentativa com ANR não é contabilizada como êxito. A execução completa do smoke, sem compilação simultânea, passou em 80/80 rotas e não registrou novo ANR. Essa repetição não estabelece uma causa definitiva nem encerra o diagnóstico de desempenho da ocorrência anterior. O script agora remove o XML anterior antes de cada dump, e `bash -n` passou.

Evidências locais: `build/verification/october-complete-20261015/`, com `integrated.log`, XMLs Android, `jvm-results.json` e `coverage.json`. Os logs iniciais foram preservados. A falha inicial de seleção ambígua no teste de Filtros foi corrigida restringindo o seletor ao diálogo; as dimensões verificadas foram mantidas. O run integrado passou com os seis casos completos de Filtros.

Capturas finais em `captures/`: Home, Drawer, Filtros, marketplace, simulação, carrinho e resultados. A medição dos pixels dominantes de Home e Filtros confirmou status bar e header em **#005CA9**, registrada em `status-header-colors.json` com os hashes das imagens. `runtime-summary.json` registra as 80 rotas e zero novos ANRs/crashes; `source-and-apk-attestation.json` atesta fontes e APKs.

Os três APKs debug gerados estão com `ENABLE_MOCK_BACKEND=false` e `HOME_V2=true`. O APK usado no smoke fica separado como `app-prd-october-mock.apk`. O AVD dedicado da porta 5558 foi encerrado; o emulador 5554 não foi usado.

## O que falta para afirmar 100%

- Capturas pareadas de Filtros e demais estados afetados, com a mesma densidade, fonte, dados e campanha. A tentativa de abrir diretamente a Activity legada de Filtros no AVD foi recusada por ela não ser exportada; não há par nativo desta tela nesta rodada.
- Homologação visual/interativa de erros, lotéricas, seletores, rodapé, retornos e restauração, além do estado inicial apresentado na captura. O mock desta rodada capturou o marketplace em erro e o carrinho vazio; esses registros não homologam cards de bolões/carrinhos/compras carregados com Mega-Sena durante a campanha.
- Diagnóstico definitivo do ANR de inicialização/foco observado durante a compilação concorrente. As capturas e as 80 rotas passaram na repetição sem essa carga, mas isso não certifica desempenho em todos os dispositivos/condições.
- Metadados configuráveis históricos de Mega 30 Anos/Loteca Dia dos Pais. O resolver atual reconhece nomes recebidos, mas o modelo moderno ainda não inclui as chaves de número de concurso/texto usadas pelo legado.
- As pendências anteriores de raster, serviços reais, SSO, pagamentos, resgates e hardware listadas em [DS_CONSOLIDATION_20261014.md](DS_CONSOLIDATION_20261014.md) continuam com seus limites de homologação. Esta revisão não certifica esses fluxos.

A mudança do ícone do launcher é um recurso nativo separado das telas Compose e não foi implementada nesta revisão. A extensão de contrato para Setembro Azul continua opcional e exige publicação no backend, conforme [COMMEMORATIVE_CAMPAIGNS.md](COMMEMORATIVE_CAMPAIGNS.md).

Builds, testes e smoke controlado não determinam um percentual confiável de equivalência visual de todo o app. Não declarar paridade de pixels ou cobertura global com base apenas nesses resultados.
