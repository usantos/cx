# Ação Avançar do carrinho — paridade com o legado

## Evidência que corrige a implementação anterior

O checkout `simlo-app-android-legacy-app-backup-20260903` mantém o botão
`btn_avancar_pagamento` acionável, inclusive sem apostas. `CarrinhoActivity.onClickAvancaPagamento`
não testa carrinho vazio, valor mínimo ou quantidade máxima antes de iniciar o fluxo.

Ordem observada em `onClickAvancaPagamento`, `trataPermissao`, `onFluxoLocalizacaoNoBrasil`
e `validarCarrinho`:

1. Cadastro pendente/suspensão.
2. Atualização de cotas expiradas, quando necessária. A continuação ocorre também quando o
   retorno é vazio; falha de atualização preserva a sequência, como já tratado na revisão anterior.
3. Permissão/localização no Brasil.
4. Histórico de compra de mesmo valor nas últimas 24 horas.
5. Confirmação de login quando não autenticado.
6. `GET logado/carrinhos/validar`; a resposta de sucesso abre a forma de pagamento.

O clique não efetiva uma compra. Não deve chamar `registrar-apostas-async` nem um provedor de
pagamentos. A indicação de valor mínimo é informativa nessa tela; os limites não substituem
localmente o validador remoto.

A migração anterior bloqueava silenciosamente o clique em três pontos: botão desabilitado por
lista de cards vazia, retorno antecipado em `requestPayment`/refresh de cotas e retorno antecipado
de `checkRecentPurchase`. Isso não correspondia à sequência encontrada no legado.

## Alterações

- `CartViewModel` exige snapshot carregado, não lista de cards não vazia. Um carrinho vazio passa
  pelos mesmos gates; isso também vale após atualização de cotas e retorno das configurações de GPS.
- Histórico usa o total do snapshot atual, inclusive zero. Cadastro/suspensão ainda interrompem
  antes de localização; confirmação de compra recente interrompe antes de login/validação.
- Não foi criada aprovação local por valor/quantidade. Erro mantém o carrinho e não navega;
  sucesso da validação é a condição para abrir a próxima tela. Os testes usam portas falsas,
  não executam compras nem alteram a regra do backend.
- Guards contra concorrência, parâmetros pendentes, decisões já apresentadas e navegação pendente
  permanecem. Snapshot ausente/falha inicial continua sem iniciar checkout.

## Layout e CaixaDS

Referências: `activity_carrinho.xml`, `btn_rounded_blue.xml`,
`button_white_blue_border_round.xml`, strings `avancarFormaPagamento`,
`label_lembrar_valor_minimo` e `carrinho_nde_apostas`, além de `ViewUtils.textCaixaSTDBold`
e `setMoedaFormatHtml`.

`CaixaDSCartActions` recebe callbacks e um estado de disponibilidade, sem conhecer Domain,
backend, navegação, carrinho ou autenticação. Substitui o par de botões genéricos que usava
raio de 25 dp e texto “Ir para pagamento”. Tokens estão em `CaixaDSCartActionTokens`.

- Continuar apostando: 240 × 56 dp, borda de 1 dp, raio de 4 dp, fundo branco e texto Bold de 16 sp.
- Pagamento: 240 × 70 dp, raio de 5 dp, azul `#005EA8`, texto de 16 sp em duas linhas:
  “Avançar” em Bold e “para a forma de pagamento” regular.
- 14 dp entre ações e 15 dp antes do slot de somador de 36 dp; assim o CTA fica a 51 dp do fundo,
  como no ConstraintLayout original. O padding não reduz mais a altura útil do botão para 56 dp.
- Título “Carrinho de apostas”, fundo branco e cabeçalho “Confirme suas apostas” em Bold de 14,2 sp.
- Aviso de mínimo recupera a quebra de linha/texto original e valor em negrito, sem substituir
  por uma frase resumida. Exibe somente quando o parâmetro é positivo.
- Foi retirado “Seu carrinho está vazio.”: esse texto adicional não existe na composição legada
  inspecionada. Cabeçalho/aviso e os botões permanecem, conforme o estado do serviço.

O callback de Continuar apostando é preservado; esta revisão não homologa todos os retornos por origem.

## Testes e limites

`CartViewModelTest` foi corrigido onde cristalizava o bloqueio antecipado divergente. Casos novos
verificam a ordem dos gates para vazio/mínimo/quantidade, conta pendente/suspensão, login após
localização/histórico e ausência de snapshot. Rejeição do serviço após esvaziamento não libera
pagamento; sucesso explícito do validador não é substituído por uma regra local inventada.

Dois testes Compose de `CaixaDSCartActions` verificam dimensões, distância, spans, callbacks e
indisponibilidade durante processamento. A integração de carrinho adiciona clique em vazio,
aviso de mínimo e ordem das portas falsas. **São testes compilados, não executados em Android.**

Artefatos: `build/verification/cart-checkout-action-20260907/`. Execução isolada final com código 0:

- **686 testes JVM aprovados:** 462 de lógica/estado, 64 de network, 127 de data, 29 de
  preferences e quatro de host/factory.
- **42 testes Android compilados**, sem execução; três novos cobrem ações e integração no carrinho.
- Cobertura selecionada de `CartRules`/`CartViewModel`/estado: **323/325 linhas (99,38%)**.
  Isso não mede a renderização do CaixaDS nem aprova o gate completo da feature ou do aplicativo.
- Compilação isolada dos fontes atuais de CaixaDS, carrinho/Home/host/DI e dependências selecionadas.

O Gradle foi novamente tentado e falhou antes das tarefas no lock do wrapper por permissão.
Não há novo APK, validação Hilt/KSP/R8, todos os flavors ou recaptura pareada nesta etapa.

Pendências explícitas:

- O validador ainda usa o caminho genérico em `RetrofitLoteriasRemoteDataSource` e descarta o
  envelope no sucesso. Erros são convertidos para `AppError` genérico, perdendo o tratamento
  completo de avisos/redirects que o legado delega a `RedirectNetwork`. É necessária uma revisão
  tipada desse contrato/handler; esta etapa não o homologa.
- Comparar a geometria em dispositivo, fontes ampliadas, insets e todos os estados de botões/alertas.
- Homologar GPS, navegador, retorno contextual e atualização de sessão/carrinho no APK.
- Completar upload/confirmação/limpeza de apostas locais no pós-login, cobertura global acima de
  95%, todos os flavors Release e a matriz completa de telas. O objetivo integral permanece aberto.
