# Redirecionamentos de compra — 07/09/2026

## Referência e escopo

Fontes confrontadas no checkout legado externo `simlo-app-android-legacy-app-backup-20260903`: `CompraUtil`, `ConfirmarCartaoActivity`, `RedirectNetwork.checkRedirectSucesso`, `RedirectNetwork.trataRedict` e `RedirectEnum`.

No registro de cartão, respostas 200/303 com redirect seguem `checkRedirectSucesso`; durante a consulta, o legado trata os redirects na resposta 303. Os quatro destinos implementados nesse handler são login, termos, cadastro e indisponibilidade. Carrinho e carrinhos favoritos pertencem ao handler de outros erros HTTP; não devem ser acrescentados à lista de destinos do handler de sucesso por mera semelhança de nomes.

O novo transporte reconhece esses quatro comandos também se presentes no envelope 200 da consulta. Essa é uma proteção explícita: um pedido de autenticação/termos não deve virar compra concluída só porque veio com status HTTP 200. O legado encerra a consulta 200 sem inspecionar o corpo; portanto, esse caso não é apresentado como reprodução literal do comportamento antigo.

## Decisões implementadas

| Comando exato do backend | Destino Compose |
|---|---|
| `login` | `account/login?authorize=true`, usando o fluxo existente de autenticação |
| `aceitar-termo-de-uso` | Alerta não cancelável; Entendi abre `support/terms-acceptance` |
| `cadastrar-apostador` | `account/register` |
| `indisponibilidade` | `onboarding/unavailable` |

Texto de `termo_novo`, conferido com `aapt2 dump resources` no APK legado produtivo:

> O termo de Uso foi Atualizado. É necessário aceitar novamente.

O diálogo usa título Atenção e botão Entendi. A conferência do recurso compilado comprova o texto, não a geometria no APK Compose.

`PurchaseRedirect` é um enum do domínio. O repository converte apenas os valores exatos da tabela; URLs, espaços adicionais, caixa diferente, valores desconhecidos e comandos exclusivos do outro handler não se tornam rotas. O comando de continuação `/carrinhos/verifica-compra-processamento` continua no protocolo de polling. O host decide os destinos internos, sem navegação por strings arbitrárias recebidas da rede.

Os redirects conhecidos têm prioridade sobre payload ausente, código de erro e status terminal, como na decisão legada 303. A submissão retorna `CardSubmissionOutcome.RedirectRequired`, não aprovação. A consulta retorna `PurchaseProgress.redirect`; as propriedades de aprovação/recusa não classificam esse resultado como terminal. `CardPurchasePolling.after` exige tratar o redirect antes de avaliar término, evitando interpretar ausência de próximo polling como sucesso nesse caso.

## Retorno e proteção contra reenvio

- Os ViewModels consomem cada redirect uma única vez; uma tentativa de consumir outro destino não altera o estado.
- Enquanto o destino está aberto, submissão, retry, geração de Pix e consultas ficam suspensos nos respectivos fluxos. O estado de pausa pertence ao `UiState`.
- Um efeito Compose compartilhado observa `ON_RESUME` da entrada de navegação; não depende de a composição anterior ter sido descartada. Isso cobre o caminho de retorno rápido durante a transição, cuja execução Android ainda precisa ser testada.
- Após redirect no POST sem ID de compra, o cartão retorna à confirmação e exige nova ação explícita; não há novo POST automático.
- Se o POST trouxe ID positivo, o retorno exige revisão em Minhas compras; CVV é limpo e não há reenvio pela confirmação.
- Se já havia acompanhamento, o retorno permite retry da consulta com o ID atualizado, preservando o valor original. Não há nova submissão do cartão.
- No Pix, o retorno libera a consulta da cobrança existente, sem gerar outra cobrança como efeito da retomada.

Essas decisões de recuperação não reproduzem o botão permanentemente desabilitado que pode resultar do callback legado de redirect. Tampouco comprovam idempotência entre processos: reconciliação após encerramento e todas as ramificações de retorno de login/termos/cadastro continuam pendentes.

## Mocks e testes

`PURCHASE_LOGIN_REQUIRED`, `PURCHASE_TERMS_REQUIRED`, `PURCHASE_REGISTRATION_REQUIRED` e `PURCHASE_UNAVAILABLE` respondem 303 com o respectivo redirect somente nas operações de registro/consulta de compra. Preparação, meios, tokenização, Pix e demais fixtures continuam disponíveis. Todos iniciam na confirmação de cartão para permitir observar o envio e o encaminhamento. O cenário é fixo: uma nova tentativa continuará retornando a mesma precondição.

Os testes cobrem todos os destinos, envelopes 200/303, ID ausente/zero/negativo/positivo, prioridade sobre status/código, rejeição de destinos desconhecidos, consumo único, pausa/retomada, ausência de gravação de compra concluída no redirect, consulta sem reenvio e contrato de rotas. A cadeia Retrofit/interceptor/repository/caso de uso é exercitada para Mercado Pago e RecargaPay, cartão novo e salvo, sem sockets.

## Evidência executada nesta revisão

Saídas atuais em `build/verification/purchase-redirect-parity-20260907/`:

- 352 testes JVM aprovados: 291 de estados/domínio/rotas/regras, 19 de rede, 35 de data, 4 de preferências e 3 de navegação mock do host.
- Compilação isolada concluída dos fontes alterados e do host Compose, incluindo `LoteriasApp`, callbacks obrigatórios, efeito de lifecycle, `AppContainer` e módulo de DI. O compilador apontou a depreciação já existente de `LocalClipboardManager`; não houve erro de compilação.
- Lógica de pagamento: 362/367 linhas (98,64%), incluindo classes internas de corrotinas. Modelos/protocolo selecionados de pagamento: 48/48 (100%). Mapeamento `PaymentRedirectNavigation`: 6/6 (100%).
- Repository/data source de pagamento: 201/204 linhas (98,53%); transporte dedicado de compras: 33/33 (100%). XMLs: `feature-payment-coverage.xml`, `core-domain-payment-coverage.xml`, `apostas-coverage.xml`, `payment-repository-coverage.xml` e `purchase-transport-coverage.xml`.

Esses números são de escopos selecionados, não cobertura global ou cobertura das telas. A execução usa compiladores/dependências locais em cache; não executa o pipeline completo de geração Android.

O comando `./gradlew :core:domain:test :core:data:test :feature:payment:testDebugUnitTest --offline` falhou antes das tarefas: acesso negado pelo sandbox ao lock `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck` (`Operation not permitted`). Não houve tentativa de contornar essa restrição. Nenhum APK atualizado, teste de Hilt/KSP/R8, captura pareada ou homologação de flavor foi produzido nesta revisão.

## Pendências que impedem declarar paridade integral

Revisão posterior: `PURCHASE_HTTP_ERROR_PARITY_AUDIT.md` registra a preservação dos metadados dos outros erros HTTP, o fallback 401 e os destinos de carrinho/carrinhos favoritos exclusivos desse handler. Os alertas específicos de 400/404/500/018003 continuam pendentes.

1. Tratamentos de HTTP 401, 400/404, 500 e código 018003 do outro handler, incluindo carrinho/carrinhos favoritos e mensagens específicas.
2. Comparar o pré-check de conectividade do login e todos os callbacks de autenticação, cadastro e termos. O login existente decide Home/cadastro/termos/autoavaliação após autenticar; não foi alterado para presumir retorno direto ao cartão.
3. Recuperação contextual de indisponibilidade originada no pagamento: a rota global ainda usa a decisão de startup. Não confundir navegação até a tela com paridade do retorno.
4. Validar em Android o retorno rápido, pausa/retomada, diálogo, Voltar, cancelamento e morte do processo; comparar imagens com o mesmo estado do legado.
5. Executar Gradle/Hilt/KSP/APK/R8, todos os flavors/Release, matriz completa e cobertura global acima de 95%. Compilação isolada e cobertura de escopos não substituem esses requisitos.
