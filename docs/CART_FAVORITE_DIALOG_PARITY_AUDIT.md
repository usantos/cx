# Diálogo de carrinho favorito — migração Compose

## Referência

Fontes consultadas em `/Users/ubiracysantos/Projects/legado/simlo-app-android`:

- `app/src/main/res/layout/custom_dialog_edit_text.xml`: campo limitado a 30 caracteres,
  título, separador, espaçamentos e botões Cancelar/Salvar.
- `app/src/main/res/drawable/rounded_dialog_background.xml`: raio de 8 dp.
- `CarrinhoActivity.setaDialogFavoritar`: título “Carrinho Favorito”, largura de 85%
  e limpeza do campo ao fechar o diálogo.
- `values/colors.xml` e `values/strings.xml`: cores e hint “Nome do carrinho favorito”.

## Implementação

`CaixaDSTextInputDialog` substitui o AlertDialog Material genérico na tela do carrinho.
Preserva fontes, cores, proporção, título, hint e ações da referência, sem layouts XML.
A feature limita o nome a 30 caracteres (antes, 60) e descarta nome/erro ao cancelar.
A marcação de carregamento acontece antes de iniciar a coroutine: cliques repetidos
não validam/salvam o favorito mais de uma vez. Edição é bloqueada durante a operação.
O estado de validação e a persistência continuam no ViewModel/casos de uso.

Diferenças deliberadas: nomes só de espaços continuam rejeitados e o nome enviado
continua normalizado com trim; o legado apenas verifica string não vazia. O diálogo
permite rolagem para telas pequenas/fonte ampliada e desabilita ações enquanto ocupado.

## Limites

Esta etapa cobre a entrada do nome no carrinho atual. O tratamento completo dos erros
remotos de nome/favoritar e os diálogos de favoritos em compras/detalhes permanecem
fora desta revisão. Não comprova equivalência visual integral, integração com backend
real, upload do carrinho local, todos os flavors ou aprovação de modernCheck.

## Verificação — 09/09/2026

- `:feature:cart:testDebugUnitTest`: 69 testes aprovados, incluindo limite, limpeza
  do nome/erro ao cancelar e envio único enquanto a validação aguarda resposta.
- Os nomes dos testes instrumentados de carrinho e dos componentes de carrinho no
  CaixaDS passaram a usar camelCase: espaços nos nomes impediam o D8 de gerar
  os APKs de teste para o minSdk do projeto.
- `verifyCaixaDS`: aprovado após centralizar as cores literais remanescentes da
  notificação da Home e do texto do carrinho; os valores de cor foram preservados.
- `verifyCleanArchitecture`: falhou por referências de infraestrutura nos repositories
  `RemoteLatestNotificationReadRepository`, `RemoteCartRepository` e
  `RemoteRetailRepository`, não alterados nesta revisão. Isso impede afirmar aprovação
  do gate geral da migração.
- Emulador Pixel 9 Pro / Android 16: os dois testes de `CaixaDSTextInputDialog`
  passaram (edição/ações e bloqueio durante carregamento). Os quatro testes existentes
  de ações/resumo do carrinho no CaixaDS também passaram. Na suíte completa do módulo,
  9/10 passaram; `CaixaDSComponentsTest.numberBall_formatsNumberWithTwoDigits` falhou
  em uma expectativa de semântica de texto do componente numérico, não modificado aqui.
- `:feature:cart:connectedDebugAndroidTest`: 4/4 testes aprovados no emulador,
  incluindo entrada com mais de 30 caracteres, cancelamento e reabertura com campo vazio.
- `:feature:home:compileDebugKotlin`: aprovado após a centralização de cores.

Os testes de interface validam ações, semântica e algumas dimensões; ainda não foi
feita captura visual pareada com o APK legado. Nenhuma chamada de produção foi feita.
