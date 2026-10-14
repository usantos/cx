# Comparação runtime CX / Compose — 14/10/2026

Branch `codex/migracao-compose`, base desta rodada `cb342f336`. Este relatório complementa `COMPOSE_CLOSURE_20261013.md`; conversão de telas e homologação real têm evidências distintas.

## Execução real

CX e Compose PRD Debug foram instalados com pacotes paralelos no AVD Android 16 `SimloComposeValidation` (emulator-5556). O emulador original do usuário não foi usado. O checkout CX permaneceu sem edição. A Home é a implementação v2 do CX, selecionável por `-PhomeV2=true` (padrão).

Foram observados em ambos: entrada/idade/introdução, login, tutorial Shake, Home com o mesmo concurso e prêmio e resultado público da Mega-Sena. Após login manual do usuário no CX, o Compose concluiu SSO usando a sessão do navegador; abriu Home/campanha, Minhas apostas e carrinho remoto.

Pares públicos após correção, sem barra de status: Home SSIM **0,996831**; resultado público **0,993978**. São medidas desses estados capturados, não certificação de todas as telas. Nas consultas autenticadas, Minhas apostas retornou o mesmo período sem apostas; o Compose carregou o carrinho remoto, cujo total/quantidade coincidiam com o resumo observado no CX. O novo login permitiu capturar ambos os carrinhos com os mesmos itens, total e quantidade; a comparação revelou o layout mais recente do CX, que foi alinhado no Compose. A captura final confirmou 9 itens, R$ 39,00 e as mesmas posições verticais dos títulos das apostas individuais.

O backend apresentou timeout `-502` e indisponibilidade `LTR-503`. O AVD também sofreu atraso de relógio de aproximadamente 11 minutos, reaplicado pela detecção automática. A sincronização de teste, com detecção automática desabilitada apenas nesse AVD, recuperou o login sem relaxar validação JWT. Houve ANR durante o seletor de callback; o AVD foi reiniciado preservando seus dados. Após reinício com 4 GB de RAM e 2 cores, o usuário concluiu novo login no Compose; o CX reaproveitou a sessão do navegador. Foram capturados novamente Home, Minhas apostas, carrinho, tutorial e filtro de bolões.

## Correções de apresentação e consumo

| Área | Mudança |
|---|---|
| Home v2 | Isola a Typography Material3 usada no CX; evita herdar métricas CaixaDS que deslocavam o banner/grade. |
| Resultado público | Não repete o resumo do concurso que o controller CX não apresenta nessa tela; mantém faixas, regiões, segundo sorteio e arrecadação. |
| Ambientes de teste | Seletor CX no login não PRD, preferência persistida e reinício após aviso. API, SSO, pagamentos, fila, namespace e pins usam um snapshot consistente. PRD ignora seleção de teste; automação conserva seus endpoints. |
| Login OAuth | Sem biometria o CX não solicita openid e pode receber access token sem ID token. Compose aceita esse contrato apenas na resposta fresca do endpoint configurado, com vínculo à tentativa, issuer, cliente, owner e validade. ID presente inválido nunca permite fallback. |
| Sessão regular | Preserva refresh/contexto em memória; preflight estrito abaixo de 30 segundos e repetição autenticada após HTTP401. Singleflight/CAS preservam identidade e conteúdo e rejeitam resposta de login anterior. |
| Biometria | Após rotação aceita, atualiza apenas credencial existente, habilitada e da mesma identidade/nonce, sob locks Gateway → Store. Falha de persistência não desfaz a sessão. |
| Transações SILCE | Codificação preserva flags one-shot/duplex. Respostas 503/timeouts não geram reenvio automático de transação. Renovação autenticada é separada. |
| Carrinho atual | Reproduz o controller CX mais recente: toolbar sólida, itens compactos, dezenas em linhas de seis, textos, rótulos e rodapé branco. Habilitação de pagamento, favoritos com combos e contagem dos cards seguem esse controller. |
| Reserva de cota | Contador detecta expiração e remove sem confirmação, uma tentativa por reserva/id/expiração. Troca de sessão cancela jobs; respostas tardias são descartadas. DELETE recebe a identidade capturada via tag, renova somente esse login após 401 e impede reenvio automático após 503. |
| Tutorial de bolões | Imagem centerInside, proporções e controles do XML CX; botão não cobre o texto; Pular alcança a última página sem a animação cancelar a navegação. Preferência e acessibilidade do checkbox preservadas. TextAppearance de 16sp, Futura Bold na entrada e fonte regular no retorno; alturas do botão comparadas ao TextView nativo: 159px inicial e 225px final. |
| Busca de bolões | Página backend começa em 1, com 30 registros. Município → UF → nacional; consulta por lotérica usa tipo 3. Áreas vazias e respostas curtas continuam automaticamente, inclusive com filtro aplicado. Localização vem do perfil da sessão atual. |
| Lista e carrossel de bolões | Cabeçalho usa o concurso/prêmio dos parâmetros reais; primeiros quatro bolões da mesma resposta vão ao carrossel e os seguintes à lista. Quantidades, detalhes, favorito e reserva preservam os casos de uso; paginação é disparada pela rolagem como no CX. |
| Detalhe do bolão | Cabeçalho, formatação BRL, lotérica, jogos e painel fixo seguem XML/controller do CX. O total usa `vrUltimaCotaComTarifa + vrCotaComTarifa × (quantidade − 1)`, inclusive diferenças de arredondamento. Favorito é apresentado quando há contexto de origem e só muda após confirmação e sucesso do serviço. |
| Retorno após alteração | Mudança confirmada no carrinho, favorito ou reserva invalida a lista; a revisão permanece no ViewModel e evita repetir consultas. Confirmação de reserva fecha o detalhe e devolve o resultado à lista. Voltar do carrinho mantém o detalhe e recarrega disponibilidade, redefinindo quantidade para 1 e preservando favorito. Adicionar nova aposta retorna à origem; entrada por detalhe de compra abre Home. |
| Modalidades especiais do detalhe | Contrato preserva colunas do Super Sete, time da Timemania, mês do Dia de Sorte, partidas/seleções/legendas da Loteca e trevos da +Milionária. Campos opcionais não quebram payloads anteriores; endpoint permanece o mesmo. |
| Mutações em andamento | Favorito, reserva, exclusão e operações do carrinho usam loading não cancelável até o serviço responder; navegação não pode descartar o consumidor e perder a atualização confirmada. |
| Parâmetros de aposta | Cache monotônico de 180 segundos, compartilhado entre consumidores, isolado por sessão e namespace/baseURL. Sem fallback vencido; resultado de sessão substituída é descartado. |
| Retorno ao Marketplace | Resumo real do carrinho recarrega ao retornar a RESUMED; toolbar respeita o override CAIXA bold 16sp aplicado pelo controller CX, além da cor e margens. |
| Minhas apostas | Rodapé compartilhado recebe GetCurrentCartUseCase e navega ao carrinho; inset vazio de 8 dp; descrição do período não duplica ano já enviado pelo serviço. Filtro/Ajuda e mês/ano da consulta preservados. |
| Filtro de bolões | Usa total/quantidade reais; restaura filtro aplicado e nome da lotérica, limpa para a modalidade de entrada, preserva seleção ao trocar lotérica e reproduz campos/fonte/prefixo R$ do CX. Voltar sem Aplicar reinicia os constraints conforme o resultado nativo. Contexto da lotérica favorita retorna à própria lista, separado da busca geral. |
| Restauração de filtros | Baseline, lotérica aplicada e filtro da lotérica favorita fazem parte do snapshot privado por owner; codec v2 aceita snapshot v1 e os campos permanecem ocultos até autenticação da mesma identidade. |
| Segundo plano | Flag no processo, preservada nas telas non-Base e em recriação de Activity; consumida na próxima tela Base. Mapa inclui templates reais e visitantes. Resgate genérico acompanha o estágio efetivamente exibido; histórico inativo não é inferido por status. |
| Sessão inválida | Reinicia a entrada com tarefa limpa, reexecutando fila/startup/versão, em vez de apenas navegar para Login. |

## Verificação

`ANDROID_SERIAL=emulator-5558 ./gradlew modernCheck releaseCheck --offline` passou: BUILD SUCCESSFUL em 31m39s, 2154 tarefas. O teste de navegação com backend controlado passou em **80/80 rotas, zero falhas** e foi repetido com o código final após o ajuste do tutorial, novamente **80/80, zero falhas**. A suíte Android do host (Home, resultados públicos, seletor de ambientes e atualização do resumo do carrinho) passou em **8 testes, zero falhas**. Após essas capturas foram acrescentadas regressões para carrinho/tutorial, regionalização/cache e remoção vinculada à sessão. A execução integrada posterior passou em 15m49s. Após o último ajuste do tutorial, os gates completos passaram novamente em **12m20s, 2154 tarefas**, incluindo os 44 testes Android do carrinho. Uma tentativa intermediária detectou cobertura reduzida; os cenários adicionais recuperaram o limite sem alterar gates. A captura real identificou a tipografia inicial do botão do tutorial; o ajuste passou em 4 testes UI, 108 testes JVM e cobertura. As capturas da APK integrada confirmaram Home, carrinho R$39/9 itens, carrossel, detalhe e filtro com serviços reais. A última APK acrescenta a correção do tutorial; a captura real confirmou a primeira página em negrito e a última em duas linhas, além de novo retorno à lista autenticada. O detalhe consultado ofereceu 10 cotas: selecionar duas recalculou R$226,80 para R$453,60 sem reservar; abrir/voltar do carrinho recarregou o detalhe e redefiniu para uma cota. O carrinho permaneceu com 9 itens e R$39,00. Evidências locais ficam em `build/verification/compose-cx-runtime-20261013/`; screenshots da conta e APKs não são versionados. Carrinho atual passou em 44 testes UI, sem falhas, e 96,16% de cobertura (mínimo preservado de 95,01%). Filtro passou em 31 testes JVM, 30 testes Domain de marketplace e 4 testes UI. Probes temporárias de diagnóstico foram removidas. Não se registram senhas, tokens ou identificadores da conta neste relatório.

## O que falta para atestar 100% em runtime

- Comparar estados não disponíveis nesta conta: apostas confirmadas/premiadas, Teimosinha, comprovantes e histórico de compras com dados equivalentes.
- Homologar biometria, rotação real de sessão, callback de Conta CAIXA e FCM em dispositivo/serviços adequados.
- Homologar respostas de pagamentos/resgates em ambiente de teste disponível, incluindo pendência, aprovação, falha, expiração e cancelamento. Nenhuma cobrança ou resgate real foi efetuado nesta rodada.
- Validar os três ambientes com serviços disponíveis; a execução PRD não comprova TQS/automação.

Não há evidência suficiente para declarar equivalência integral de todos os fluxos reais. Testes controlados, catálogo e gates comprovam os contratos exercitados, mas não substituem esses estados de homologação.
