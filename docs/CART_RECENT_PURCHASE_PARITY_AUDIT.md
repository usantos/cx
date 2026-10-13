# Compra de mesmo valor em 24 horas — 07/09/2026

Status: consulta local, alerta do carrinho e gravação nos fluxos modernos de cartão/Pix implementados; **pré-pagamento completo e persistência Android ainda não homologados**.

## Referências confrontadas

- `CarrinhoActivity.onFluxoLocalizacaoNoBrasil`: consulta antes da confirmação de login/validação; Sim fecha para revisar o carrinho, Não continua.
- `CarrinhoModel.existeCompraMesmoValorEm24horas`: comparação por valor, limite inferior estrito de 24 horas, relógio do dispositivo.
- `CompraModel.salvaUltimaCompra`, `UltimaCompraCRUD`, `TabelaUltimaCompra`, `DBLoteriasHelper`: `Loterias.db`, `tb_ultima_compra`, colunas `id`, `valor`, `data`, data `dd/MM/yyyy HH:mm` no fuso do dispositivo.
- `ConfirmarCartaoActivity.onCompraSuccess` e `CopiaColaPixActivity.finalizaProcessoCompraSucesso`: gravam o valor local antes de limpar o carrinho.
- `CompraUtil` e `CompraPixUtil`: a conclusão do processamento não é sempre sinônimo de aposta efetivada; há respostas/redirects HTTP 303 e esgotamento de tentativas que ainda precisam de auditoria integral nos fluxos modernos.

## Implementação

`RecentPurchaseRepository`, `HasRecentPurchase` e `RecordRecentPurchase` pertencem ao domínio. Recebem relógio explicitamente; não dependem de Android, Retrofit ou Compose. Esse histórico é um aviso local, não autorização de pagamento, controle de duplicidade no servidor ou confirmação de efetivação da aposta.

O adaptador `LegacyRecentPurchaseRepository`, em `core:preferences`, usa o SQLite legado sem alterar sua versão ou remover outras tabelas. Não cria banco durante consulta; ausência de banco/tabela corresponde a histórico vazio. Usa argumentos vinculados, leitura somente leitura e substituição transacional por valor. Assim, uma atualização do aplicativo conserva os registros da instalação anterior. O adaptador mantém a precisão em minutos e o fuso local. A consulta normaliza a escala decimal, seleciona a data válida mais recente e aplica a mesma comparação estrita de 24 horas, inclusive o comportamento após retrocesso do relógio local.

Correções explícitas em relação a defeitos do CRUD legado:

- A exclusão antiga ignorava o ID e não tinha WHERE; a nova substituição não apaga valores não relacionados.
- Uma data malformada não invalida outras datas válidas do mesmo valor.
- Falha de leitura não é tratada como ausência de compra: o carrinho apresenta erro e não valida o pagamento nessa tentativa.

O composition root Hilt fornece o repositório e os casos de uso obrigatórios. O carrinho consulta após verificar conta/atualizar cotas e antes do login/validação. A etapa de localização foi posteriormente inserida antes desse aviso; ver `CART_LOCATION_PARITY_AUDIT.md`, cuja homologação Android segue pendente. O alerta usa o texto literal e `CaixaDSLegacyDialog`. Sim/dispensar mantém o carrinho; Não continua somente a tentativa atual. Uma alteração no carrinho invalida a confirmação anterior e exige nova consulta. Cliques repetidos não repetem a validação.

Cartão captura o total real do carrinho antes de submeter ao processador; erro de consulta ou total não positivo impede a submissão. A gravação ocorre ao entrar na conclusão moderna (aprovação ou encerramento do acompanhamento ainda em processamento), não na edição do cartão e não em rejeições. Pix grava o valor da cobrança ao receber aprovação; gerar código, aguardar ou receber rejeição não grava. Ambos usam o caso de uso injetado. Falha de gravação local não transforma compra aceita em falha nem oferece reenvio da cobrança. Não há PAN, CVV, token, CPF ou código Pix nesse histórico.

As funções puras de apresentação de pagamento foram separadas das rotas em `PaymentRules.kt`, sem mudar seus textos/formatos. A medição de cobertura de pagamento passou a incluir as classes internas de corrotinas, além dos ViewModels/estados/regras.

## Validação e limites

Os testes novos verificam janela temporal (antes, exatamente no limite, depois e relógio retrocedido), valores inválidos, erros/cancelamentos do domínio, formato legado, escala monetária, datas corrompidas, substituição por valor, reconstrução do repositório, ações Sim/Não, convidado, alteração do carrinho, aprovação/rejeição e falha de gravação após pagamento aceito. Os testes JVM de armazenamento usam um store em memória; **não comprovam transação, cursor, abertura do SQLite Android nem atualização de uma instalação real**.

Evidências em `build/verification/cart-recent-purchase-20260907/`:

- 270 testes JVM aprovados: 248 de estados/domínio/rotas/regras, 3 de envelope, 15 de repositories remotos e 4 de persistência com stores em memória.
- Compilação isolada concluída para host, Hilt provider, container, telas de pagamento/carrinho e três testes instrumentados de SQLite.
- Domínio `RecentPurchase.kt`: 14/14 linhas (100%). Carrinho (ViewModels/estados/regras, incluindo corrotinas): 469/474 (98,95%). Pagamento no mesmo escopo: 305/310 (98,39%). Não são cobertura de UI nem cobertura global.
- Persistência nova, incluindo o adaptador Android e sem incluir classes de teste na métrica: 15/40 linhas (37,5%) com execução JVM somente. **O gate de 95% desse escopo não passou**; não foram excluídos os métodos nativos para obter aprovação.

Os três testes `LegacyRecentPurchaseStorageTest` verificam no Android banco ausente, tabela ausente, leitura de registros legados, data nula, recriação, preservação de versão/tabelas/valores e rollback com falha forçada de insert. Usam diretório de cache único de teste, nunca o banco real do aplicativo. Foram compilados, mas não executados. O relatório/gate de `core:preferences` agora depende também de `connectedDebugAndroidTest` e combina `.exec` JVM com `.ec` instrumentado; portanto exige dispositivo/emulador e não pode ser aprovado apenas com stores falsos. Configuração Gradle/Hilt/KSP, APK, R8, flavors, navegador, localização e comparação visual continuam sem validação nesta rodada.

A tentativa `./gradlew :core:preferences:connectedDebugAndroidTest :core:preferences:jacocoDebugCoverageVerification --offline` encerrou antes da configuração: `FileNotFoundException` ao abrir `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`, com `Operation not permitted`. Nenhum teste Android foi executado nessa tentativa e nenhuma permissão/lock foi alterado.

Ainda exigido para homologação:

1. Teste Android com banco legado existente, banco ausente, tabela ausente, erro de leitura/gravação, rollback e preservação das demais tabelas.
2. Captura/gestos do alerta no legado e Compose; voltar, recriação de Activity/processo e troca/expiração de sessão enquanto o alerta está aberto.
3. Homologar no Android a localização/permissão posteriormente inserida antes da consulta, incluindo os ramos de retorno à Home, conforme `CART_LOCATION_PARITY_AUDIT.md`.
4. Auditoria dos redirects HTTP 303, limites de polling e conclusão em processamento de cartão/Pix. O ramo de esgotamento do acompanhamento Pix legado não está coberto pelo fluxo moderno de aprovação simples.
5. Atualização do carrinho local após conclusão e demais entradas de pagamento, incluindo Rapidão, histórico/recuperação de Pix e rotas diretas.
6. Execução em todos os flavors, Release/R8 e cobertura global; esta revisão não certifica equivalência integral.
