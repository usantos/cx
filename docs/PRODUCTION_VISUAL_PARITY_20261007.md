# Capturas de paridade visual em produção — 2026-10-07

## Referência e variantes

A referência visual desta rodada é o APK Loterias de Produção 1.25 instalado no XCover 7. O legado foi compilado de `/Users/ubiracysantos/Projects/cx/` como `prdDebug`, com pacote de validação `br.gov.caixa.loterias.apostas.cxprodvalidation`. O Compose foi compilado como `prdDebug`, versão `1.25.0`/código `230125000`, com pacote `br.gov.caixa.loterias.apostas.composeprodvalidation`. Ambos usam endpoints de produção; os pacotes separados preservam a instalação e os dados do APK oficial.

O pacote temporário do legado usou configuração Firebase sintética só para viabilizar a compilação isolada; por isso Analytics, Crashlytics, Performance e notificações Firebase não fazem parte desta validação. O arquivo sintético foi removido da árvore de origem após a compilação. A instalação oficial (`br.gov.caixa.loterias.apostas`) não foi substituída nem teve seus dados alterados.

A inicialização do APK oficial foi tentada para ampliar a comparação logada, mas permaneceu em uma tela branca de abertura durante a captura. Por isso, os pares de tela desta rodada usam a variante temporária construída do código legado; não houve sessão logada disponível para validar rotas autenticadas.

O Compose Gradle contém somente as variantes `prd`, `tqs` e `automacao`; não há flavor Beta. Os APKs antigos `.beta` que ainda aparecem no aparelho são instalações residuais e não foram removidos, para preservar seus dados.

## Pares recapturados

As imagens foram capturadas no mesmo aparelho/resolução (1080×2408). Os resultados usam dados públicos da API de produção; nenhuma aposta foi comprada e nenhuma operação de pagamento ou resgate foi executada.

| Tela | Legado CX | Compose Produção | Resultado da revisão |
|---|---|---|---|
| Login / acesso visitante | [captura](</Users/ubiracysantos/Projects/cx/build/parity-production-20261007/cx/current-before-home.png>) | [captura](</Users/ubiracysantos/Projects/simlo-app-android/build/parity-production-20261007/compose/06-launch-after-version.png>) | Mesmo conteúdo e proporções no estado inicial. |
| Tutorial Aposta Shake | [captura](</Users/ubiracysantos/Projects/cx/build/parity-production-20261007/cx/02-experimente.png>) | [captura](</Users/ubiracysantos/Projects/simlo-app-android/build/parity-production-20261007/compose/04-tutorial-01.png>) | Capturado para referência; paridade de todas as páginas do tutorial ainda não foi recertificada nesta rodada. |
| Home com Outubro Rosa | [captura](</Users/ubiracysantos/Projects/cx/build/parity-production-20261007/cx/03-home.png>) | [captura](</Users/ubiracysantos/Projects/simlo-app-android/build/parity-production-20261007/compose/14-home.png>) | Corrigidos o cartão Mega-Sena rosa indevido, o sino de notificações que faltava e a visibilidade das barras do sistema. |
| Simulador Mega-Sena | [captura](</Users/ubiracysantos/Projects/cx/build/parity-production-20261007/cx/06-aposte-entry.png>) | [captura](</Users/ubiracysantos/Projects/simlo-app-android/build/parity-production-20261007/compose/08-bet-entry.png>) | Hierarquia, conteúdo e dimensões principais pareados. Estados de seleção/Surpresinha/Teimosinha e demais modalidades ainda não foram todos recapturados nesta rodada. |
| Seleção de modalidades de resultados | [captura](</Users/ubiracysantos/Projects/cx/build/parity-production-20261007/cx/10-results.png>) | [captura](</Users/ubiracysantos/Projects/simlo-app-android/build/parity-production-20261007/compose/18-results.png>) | Grade, cores, textos e espaçamento visualmente correspondentes. |
| Detalhe do resultado Mega-Sena, concurso 3067 | [captura](</Users/ubiracysantos/Projects/cx/build/parity-production-20261007/cx/11-megasena-results.png>) | [captura](</Users/ubiracysantos/Projects/simlo-app-android/build/parity-production-20261007/compose/19-megasena-results.png>) | Mesmas dezenas, premiação, data e navegação; layout visualmente correspondente. |

## Correções desta rodada

- A Home conserva as cores de modalidade durante Outubro Rosa. A identidade da campanha fica nos atalhos e elementos institucionais, como no APK instalado.
- A Home exibe simultaneamente sino, ícone da campanha e trevo de atalhos.
- A Home repõe as barras de status e navegação com ícones escuros, como as telas legadas.
- A Activity Compose fica travada em retrato. O gradiente da Home agora é desenhado antes dos insets do Scaffold, removendo a faixa `#F7FAFA` que interrompia o fundo antes da barra do sistema; a captura pós-correção está em [home-portrait-insets-fixed.png](</Users/ubiracysantos/Projects/simlo-app-android/build/parity-production-20261007/compose/home-portrait-insets-fixed.png>).
- O padrão da variante Compose `prd` foi atualizado para 1.25.0/código 230125000, alinhado ao APK de referência.

## Limite da homologação

Esta rodada recapturou os estados públicos e acessíveis sem autenticação. A matriz geral ainda não está 100% homologada: cadastro/dados pessoais, compras/cartões, Pix, fluxos de resgate e estados logados precisam de sessões e dados de comparação equivalentes; também faltam os pares completos de todas as modalidades, diálogos e estados de loading/erro/foco. Os fluxos financeiros foram deixados sem confirmação final para não movimentar dinheiro ou resgatar prêmio real. A matriz principal continua em [`VISUAL_PARITY_MATRIX.md`](VISUAL_PARITY_MATRIX.md).
