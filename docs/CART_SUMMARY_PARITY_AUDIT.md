# Somador do carrinho — contrato e apresentação Compose

## Referências do legado

Checkout de referência: `simlo-app-android-legacy-app-backup-20260903`.

- `CarrinhoDTO.atualizaValorTotal` e `CarrinhoSingleton.setCarrinho`: somam a lista plana
  `apostas`; uma reserva usa `vrTotalCota`, não `valor`. A atribuição ao singleton recalcula o total.
- `SomadorCarrinhoFragment.atualizaValorTotal`: quantidade é `apostas.size`, não a quantidade
  de cards/combos nem a multiplicação por Teimosinha. Acima de 999 usa `mais_999`, cujo conteúdo
  real em `strings.xml` é `...`.
- `showFragmentSomadorCarrinho`: só apresenta a faixa quando o total é estritamente positivo.
- `CarrinhoActivity.podeHabilitarFavoritar` e `configuraBotoesSuperiores`: lista plana não vazia
  participa da visibilidade; cotas/combos permitem limpar e impedem favoritar. Favoritar exige login.
- `fragment_somador_carrinho.xml`, `ic_background_somador_carrinho.xml`, `ic_somador_carrinho.xml`:
  faixa de 36 dp, texto “Valor atual do carrinho”, valor à esquerda, ícone e contador à direita.
  A região azul-escura possui uma transição diagonal, não uma divisão retangular de largura fixa.

## Separação de responsabilidades

`CartDto.bets` preserva a lista plana `apostas` separadamente de `poolShares`, `individualBets`
e `combos`. São projeções do mesmo snapshot, **não valores adicionais para somar**.

`CartSummary.kt`, em Data, calcula o total da projeção plana sem converter valores para Double.
`RemoteCartRepository.toDomainCart` mantém cotas → apostas individuais → combos para os cards.
IDs de combo e IDs de apostas continuam usando seus endpoints de exclusão próprios; não são
reconstruídos a partir da lista plana. O mesmo mapeador atende ao retorno do pós-login.

`ShoppingCart.totalBetCount` preserva a contagem explícita do snapshot. `betCount` expõe a
quantidade ao consumidor e, para entidades sem esse campo, conta apostas internas dos combos.
`HomeViewModel` usa essa propriedade tanto ao observar o conteúdo autenticado quanto no refresh.
Não há parsing de DTO, acesso à rede ou soma financeira dentro do componente Compose.

Quando `apostas` está presente, inclusive vazia, é a fonte do resumo. Para payloads sem essa
projeção, mantém-se a compatibilidade anterior: total informado ou soma dos cards e quantidade
derivada dos grupos. **Esse fallback não é homologação do contrato de um backend que omita
`apostas`.** Não se deve usar mocks incompletos como prova de equivalência ao legado.

Reservas sem `vrTotalCota` falham no mapeamento em vez de publicar um total silenciosamente
incorreto. Ausência de `valor` numa aposta sem reserva não adiciona valor, como no método legado.
Testes também verificam que uma falha no pós-login não substitui o conteúdo da sessão anterior.

## Componente CaixaDS

`CaixaDSCartSummary` substitui a implementação privada da Home e o rodapé simplificado do carrinho.
Recebe total, contagem e callback opcional; não conhece repository, navegação ou autenticação.

- Fundo convertido diretamente dos três paths do XML, com cores e stroke originais; renderização
  esticada nas dimensões do componente como o background Android.
- Ícone de 28 dp, margem final de 16 dp; contador de 16 dp, a 6 dp do topo, cor `#FDAB01`.
- Valor 16 sp em Caixa STD Bold; descrição 12 sp em Caixa STD Regular; contador 7 sp em Bold.
- Descrição centralizada entre o final do valor e a guia de 80% da largura, conforme ConstraintLayout.
- Overflow visual `...`; a semântica mantém a contagem completa para acessibilidade.
- Total não positivo remove a faixa. No carrinho, o host mantém o slot de 36 dp, como o FrameLayout
  legado quando o Fragment ficava GONE. Na Home, a composição e os insets usam a mesma regra.
- Callback só é informado na Home. O somador da própria tela do carrinho não cria navegação adicional.

Tokens e exemplo executável estão em `CaixaDSCartSummaryTokens` e `CaixaDSCatalog`.
Uso documentado em `CAIXA_DS.md`.

## Validação e limites

Os testes cobrem precisão decimal, lista plana vazia/ausente, cotas, combos, mutações, publicação
pós-login, contador na Home, visibilidade de toolbar, moeda independente do locale e overflow.
Testes Compose do componente e da tela do carrinho verificam conteúdo, ação opcional, atualização,
limpeza e visibilidade. A compilação isolada desses testes **não é execução Android**.

Artefatos desta revisão: `build/verification/cart-summary-component-20260907/`.
Execução isolada final com código 0:

- 682 testes JVM aprovados: 458 de lógica/estado, 64 de network, 127 de data, 29 de
  preferences e quatro de host/factory.
- 39 testes Android compilados, não executados: incluindo dois do novo somador CaixaDS e
  dois da integração do somador/toolbar/limpeza com `CurrentCartRoute` e seu ViewModel.
- JaCoCo selecionado: `CartSummary.kt` 6/6; `ShoppingCart` 8/8; regras do somador CaixaDS 3/3;
  `CartRules`/`CartViewModel`/estado 326/328 (99,39%). São recortes de lógica, não a feature inteira.
- Fontes atuais de rede, mapeador de carrinho, CaixaDS, Home/carrinho Compose, host e DI compilados
  isoladamente. Isso não executa Hilt/KSP, processamento de recursos, D8/R8 ou empacotamento.

Comando Gradle tentado: `./gradlew :core:data:test :feature:cart:check :core:design-system:assembleDebug --offline`.
Falhou antes das tarefas com `Operation not permitted` no `gradle-9.4.1-bin.zip.lck` do wrapper.
Não houve workaround de permissão nem novo APK validado nesta revisão.

O gate de `:feature:cart` passou a incluir todas as classes, inclusive UI e corrotinas,
e exige testes instrumentados além dos JVM, com limite de linhas estritamente acima de 95%.
Um percentual selecionado de lógica não aprova esse gate nem a cobertura global do aplicativo.

Continuam pendentes:

- Capturas pareadas da faixa, fonte ampliada, valores longos, insets e ação na Home expandida.
- Homologação dos demais elementos da tela, incluindo CTA de pagamento, diálogos e estados vazios.
- Payload somente com projeção plana: resumo preservado, mas nenhum card/grupo com ID inventado.
- Transferência de apostas locais no pós-login: o POST ainda envia lista vazia; esta revisão
  **não habilita upload, confirmação do lote nem limpeza de SQLite**.
- Execução Android, Hilt/KSP, empacotamento, R8, todos os flavors Release, matriz visual completa
  e cobertura global. O Gradle continua impedido antes das tarefas pelo lock do wrapper sem permissão.
