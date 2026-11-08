# Histórico de Minhas Apostas — 90 dias e paginação

## Evidências e causa

- Consulta observada no emulador existente, sem expor credenciais: `mes=90`, `offset=0`, `size=30`, `situacao=1`; resposta HTTP 206. O período já era enviado ao serviço.
- A lista Compose não possuía chamada para buscar a página seguinte. O legado usa `rowCount`, avança o deslocamento e consulta novamente ao alcançar o fim da lista.
- O coordenador ignorava `applyBetFilters` enquanto outra consulta estava em andamento. Teste reproduziu a troca para 90 dias sendo ignorada durante a consulta anterior.

## Correção

- Preservar `rowCount` em `ConfirmedBetsPage.totalCount` quando inteiro não negativo; total ausente permanece desconhecido.
- Paginar com os filtros aplicados, somando os registros recebidos ao deslocamento, deduplicando a lista por id e parando ao alcançar o total ou o fim dos resultados.
- Uma geração por consulta aplicada rejeita páginas e erros de períodos anteriores. Trocar o período inicia uma nova consulta no deslocamento zero.
- Acionar a próxima página ao alcançar o fim da lista. Falha na continuação mantém apostas visíveis e oferece nova tentativa explícita no mesmo deslocamento. A lista volta ao início ao mudar o período.
- Publicados os IDs de carregamento/erro de continuação no inventário e localizadores Robot.
- Preservadas as alterações anteriores de Pix, filtros de situação e contrato.

## Registro de validação

- RED: troca de período durante uma página pendente falhou com o comportamento anterior.
- RED: contratos de paginação ausentes (total, ação e estado) nos testes novos.
- GREEN inicial: 35 testes Data e sete regressões do coordenador.
- RED de interface: falta do acionamento de próxima página e da recuperação de erro, antes de ligar a paginação na tela.
- Revisão independente somente leitura de paginação, concorrência, dados e interface: sem achados.
- PASSOU: Data 35 testes, Presentation 149 testes e Tickets UI PRD 82 testes; total 266, sem falhas, erros ou ignorados.
- PASSOU: `verifyCleanArchitecture`, `verifyAutomationIds`, `:app:assemblePrdDebug`, formatação Kotlin dos módulos alterados e `git diff --check`.
- Instalada atualização PRD Debug no emulador existente `emulator-5554` com `adb install -r`, mantendo dados do aplicativo. Todos os módulos dinâmicos constam como incluídos na versão universal gerada a partir do AAB.
- O aplicativo solicitou autenticação na reabertura; o usuário fez login. Conferência real concluída no emulador: selecionados Últimos 90 dias; observadas consultas `mes=90`, `size=30`, `offset=0`, `30` e `60`, todas com resposta HTTP 206. Após rolar, a lista exibiu apostas mais antigas nas páginas seguintes, sem erro de continuação. A tela foi deixada no início, com Últimos 90 dias selecionado. Não foi contornada autenticação nem realizada ação financeira.
- Suíte global de todos os módulos/ambientes, Lint/cobertura global e certificação Robot em aparelho/farm não executados.
- Não foram efetuadas apostas, pagamentos, resgates ou aceite de contrato durante esta investigação.
