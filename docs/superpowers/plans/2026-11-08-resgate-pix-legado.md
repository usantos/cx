# Resgate Pix — paridade com o legado

**Objetivo:** concluir a paridade do fluxo existente de resgate Pix com `ResgatePixActivity`, `EscolhaChavePixResgateFragment` e `ConfirmacaoResgatePixActivity` do checkout local `../simlo-app-android`.

**Decisão:** reutilizar telas Compose, casos de uso e endpoints existentes. Recriar telas ou copiar Activities duplicaria um fluxo já implementado. O escopo é preservar a chave escolhida, iniciar cada entrada com formulário limpo e impedir operações sobre uma etapa inadequada ou ainda em processamento.

**Arquitetura:** estado Redux no módulo de apresentação de Retail; coordenação dos casos de uso em `PrizePixChannel`; transporte e contratos HTTP preservados. A consulta apresenta o favorecido; somente o comando explícito de confirmação efetiva o resgate.

## Execução

- [x] Escrever e executar regressões de CPF/outra chave, confirmação, reentrada, consulta/cancelamento repetidos e falha com nova tentativa.
- [x] Ajustar `PrizeRedemptionChannels.kt`, `PrizeRedemptionViewModel.kt` e `PrizeRedemptionStateReducer.kt` para preservar as regras acima.
- [x] Executar os testes de apresentação, verificadores de arquitetura/IDs e compilação da variante TQS.
- [x] Registrar resultados e limitações sem afirmar execução contra backend ou aparelho.

## Contrato preservado

CPF é a seleção inicial quando disponível. Outra chave exige formato válido e aceite de titularidade. Consultar não efetiva o resgate. Como no legado, a confirmação envia a chave originalmente escolhida, e não o texto de apresentação retornado para o favorecido. Cancelar na confirmação aguarda o serviço antes de retornar às opções. Voltar mantém o comportamento legado de navegação. Sucesso apresenta a orientação e o acesso a Minhas Apostas já existentes.

## Registro

- Comparados os fluxos Java/XML do legado com as telas Compose, o coordenador e os testes existentes.
- Identificadas diferenças: confirmação usava a chave retornada pelo serviço; reentrada preservava outra chave/aceite; consulta e cancelamento aceitavam repetição durante processamento.
- As ferramentas `crapper`, `dryer` e `mutator` não foram encontradas nos scripts do projeto; não há métricas dessas ferramentas nesta entrega.

- Testes antes da implementação: oito cenários executados, seis falharam pelas diferenças de comportamento esperadas.
- Após a implementação, os oito cenários novos passaram. Dois testes antigos foram alinhados à referência do legado: consulta antes de confirmação e envio da chave escolhida, preservando as verificações de processamento e envio único.
- Revisão independente somente leitura: sem achados.
- Preparação local: regenerados apenas artefatos de compilação de Tickets/App para remover duplicação de dex; selecionado Python 3.14 explicitamente e iniciada validação sem reutilizar o processo Gradle do IDE, cujo PATH selecionava Python antigo.

## Validações desta entrega

- PASSOU: `:feature:retail:presentation:testDebugUnitTest` — 114 testes, zero falhas, erros ou ignorados.
- PASSOU: `:feature:retail:retail_ui:testPrdDebugUnitTest` — 45 testes, zero falhas, erros ou ignorados.
- PASSOU: `verifyCleanArchitecture` e `verifyAutomationIds`.
- PASSOU: `python3 scripts/check-kotlin-format.py /Users/ubiracysantos/Projects/cx/feature/retail/presentation` — 77 arquivos, zero violações.
- PASSOU: `git diff --check`.
- Não executados: backend real, aparelho físico, suíte global de todos os módulos/ambientes, Lint e cobertura global. A execução JVM de Retail e a compilação TQS não substituem esses controles.

- PASSOU: `:app:assembleTqsDebug` — compilação concluída. Invocação final executada com `PATH` contendo `/opt/homebrew/bin`, `--no-daemon` e `-PpythonExecutable=/opt/homebrew/bin/python3`.

## Teste de três acionamentos seguidos

- Pedido adicional: simular três cliques em Resgatar para avaliar repetição no backend.
- O aplicativo aberto usa PRD; o comando efetiva resgate Pix. Nenhum acionamento financeiro real foi executado por automação.
- Ajustado o teste de apresentação existente para chamar `confirmPix()` três vezes seguidas antes de avançar o agendamento.
- PASSOU: `:feature:retail:presentation:testDebugUnitTest --tests '*PrizeRedemptionViewModelTest.three rapid Pix*'` — três acionamentos, uma chamada ao caso de uso simulado; chave selecionada preservada; nova tentativa após sucesso não reenvia.
- Esse resultado valida a proteção do cliente, não idempotência de um backend real. Envio de três requisições depende de cenário de homologação sem movimentação real, ainda não informado.
