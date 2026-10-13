# Expiração de cotas do carrinho — 07/09/2026

**Correção posterior:** `CART_CHECKOUT_ACTION_AUDIT.md` revê a regra histórica abaixo que
interrompia o checkout após refresh vazio. O legado segue para localização/histórico/login
e validação do serviço mesmo quando o carrinho esvazia; o Compose agora também. A descrição
e os resultados seguintes registram o comportamento da revisão anterior, não essa regra atual.

Status: regra e contadores da lista/confirmação corrigidos; homologação completa de UI, ciclo de vida e pré-pagamento pendente.

## Fontes legadas

`CarrinhoActivity.timerExcluiCotasExpiradas`, `temCotasExpiradas`, `onClickAvancaPagamento`, `DateUtils.diffMillisSecondsTimerZone`, `ApostaCarrinhoHolder.startTimer` e `ContagemRegressiva`.

- O timer do carrinho verifica imediatamente e a cada minuto, mas só atualiza o carrinho se detectar cota expirada.
- O clique em pagamento atualiza primeiro o carrinho quando há cotas vencidas. A falha nessa atualização não encerra o fluxo legado: a sequência de permissão/validação continua.
- A referência temporal é `DataHoraServidorSingleton`, não o relógio de parede isolado do telefone.
- O contador mostra minutos/segundos; o amarelo aparece quando os minutos inteiros ficam abaixo de um. Ao expirar, valor/modalidade/concurso/quantidade/contador ficam cinza e a legenda mostra `Cota expirada` em vermelho escuro.

## Correções atuais

- `CartViewModel` recebe obrigatoriamente `ServerTimeSource`, fornecido pelo `AppContainer` a partir do relógio monotônico sincronizado com respostas CAIXA. A lista lê essa mesma referência a cada segundo. Não altera o relógio do dispositivo.
- `refreshExpiredReservations` consulta apenas quando existem cotas expiradas e a tela não está carregando/recuperando parâmetros. O efeito da tela mantém o intervalo legado de um minuto. Falhas não removem itens localmente; a resposta do backend continua sendo a autoridade sobre exclusão e total.
- `requestPayment` atualiza o carrinho com cotas vencidas antes de validar. Cliques concorrentes ou enquanto a decisão está pendente não repetem a operação. Se a atualização deixar o carrinho vazio, pagamento não é liberado. Se falhar, conserva o carrinho anterior e ainda submete à validação autoritativa, que pode rejeitar a compra. Não foi implementada cobrança local.
- `poolExpiry` exige um instante explícito, mostra `mm:ss`, trata zero segundos como expirado e usa alerta somente abaixo de 60 segundos. Os testes cobrem 60/59 segundos, fração final, datas com offset e datas locais de São Paulo. Datas inválidas permanecem desconhecidas em vez de inventar uma reserva válida.
- Tokens do CaixaDS preservam `jr_amarelo` (#EAAA28), `cor_bola_unselected` (#ADC0C4) e `vermelho_cancelada` (#900000). A linha restaura essas cores, quantidade com dois dígitos e contador separado da legenda de cota expirada. Isso não certifica toda a geometria da linha legada.

## Evidências

`build/verification/cart-expiry-20260907/`:

- 213 testes JVM aprovados: 194 de estados/domínio/rotas/regras, 3 de envelope, 15 de repositories e 1 de preferências.
- Casos novos: consulta periódica antes/depois da expiração segundo o relógio injetado; prevenção de chamadas concorrentes; ordem atualização → validação; carrinho esvaziado; falha de atualização seguida de rejeição do pagamento, preservando os itens anteriores.
- Escopo de ViewModels/UiStates/regras de carrinho, incluindo corrotinas: 429/434 linhas (98,85%). `CartRules`: 42/42; `CartViewModel`: 160/162. Não é cobertura global ou de renderização.
- Compilação isolada Kotlin/Compose de host, container, lista e confirmação de inclusão com os fontes atuais. Sem geração/execução de novo APK, Hilt/KSP ou R8 nesta rodada.

## Ainda falta

1. Homologar no aparelho o contador da confirmação após segundo plano/recriação. A referência temporal e a atualização em `ON_RESUME` foram implementadas na revisão abaixo.
2. Homologar os formatos temporais reais: o legado ignora o sufixo após os primeiros 19 caracteres e aplica São Paulo, enquanto o parser moderno respeita offsets explícitos e aplica São Paulo somente quando não há offset. Não introduzir essa ambiguidade legada silenciosamente no relógio seguro.
3. Completar o fluxo anterior ao pagamento, a notificação de cotas removidas e comparação visual da linha nos estados ativo/alerta/expirado. Cadastro/suspensão foram posteriormente ligados ao carrinho; localização/permissão, compra repetida e outros ramos continuam pendentes em `CART_CHECKOUT_PARITY_AUDIT.md`.
4. Validar pausa/retomada, mudanças do carrinho durante requests, acessibilidade, todos os flavors e R8. As correções presentes não homologam todo o carrinho nem o aplicativo.

## Confirmação de inclusão — revisão posterior

`CartAdditionViewModel` agora também exige `ServerTimeSource`. A reserva usa o timestamp `expiresAt` recebido na navegação; o parâmetro artificial `initialPoolSeconds` foi removido. Cada tick apenas solicita o recálculo entre prazo e hora do servidor: atrasos ou segundo plano não prolongam a reserva. O estado expirado é terminal, de modo que uma leitura posterior mais antiga do relógio não reativa a reserva.

`CartAdditionRoute` chama `refreshReservation` em `ON_RESUME`, permitindo atualizar a tela antes do próximo tick. A aposta individual não consulta o relógio nem inicia esse contador. O host fornece a mesma referência temporal usada na lista, sem fallback local independente nessas duas telas.

Foi corrigida também a seta da confirmação de bolão: conforme `ConfirmadoInclusaoBolaoCarrinhoActivity.onSupportNavigateUp/onBackPressed`, seta e Voltar retornam ao destino anterior. O CTA `Voltar ao início` permanece uma ação separada para a Home. O módulo declara explicitamente `activity-compose` para essa integração.

Evidências em `build/verification/cart-addition-clock-20260907/`:

- 216 testes JVM aprovados: 197 de estados/domínio/rotas/regras, 3 de envelope, 15 de repositories e 1 de preferências.
- Testes novos exercitam tick atrasado em dois minutos, retomada antes do tick pendente, expiração terminal e recriação do ViewModel com o mesmo prazo absoluto. Os testes de duração agora usam a referência injetada, sem um contador fictício de dois ticks.
- Escopo de carrinho permanece 429/434 linhas (98,85%); `CartAdditionViewModel.kt`: 47/48 linhas. Inclui corrotinas, mas não renderização, callbacks Android de ciclo de vida ou navegação em runtime.
- Compilação isolada do host e telas atuais concluída, incluindo `LifecycleEventEffect` e o callback separado de retorno. Ainda não substitui a geração Hilt/KSP, os APKs/R8, os flavors ou capturas pareadas.
