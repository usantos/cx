# Erros e redirects de resgate — 07/09/2026

## Escopo e referência

Esta revisão liga os erros HTTP de `POST apostas/{id}/resgatar-premio` às decisões do `RedirectNetwork.checkRedirect` legado para Mercado Pago e Pix. Foram confrontados `treatNegotialErros`, `trataErro500`, `trataRedict`, `AlertDialogExperimenteLogarSingleton` e `MensagensNetwork`. Os textos de login, novos termos, falha de requisição e operação não concluída foram conferidos também nos recursos compilados do APK legado.

O escopo não inclui ainda os handlers dos outros endpoints de resgate (detalhe, QR, consulta/cancelamento Pix, comprovantes) nem os do serviço de token de cadastro. Não é homologação integral de resgate ou do aplicativo.

## Decisões preservadas

| Resposta | Decisão Compose |
|---|---|
| Redirect conhecido, mesmo com código/erro HTTP | Prioridade para login, aceite de termos, cadastro do apostador, indisponibilidade, carrinho ou carrinhos favoritos |
| Redirect de login | Abre a rota existente que inicia autenticação, sem o diálogo opcional de 401 |
| Redirect de termos | Atenção / texto literal de novos termos / Entendi → aceite |
| HTTP 401 sem redirect | Atenção / confirmação literal de login / Sim ou Não; Não mantém o resgate |
| HTTP 400, códigos 011010/011011/011012 | Mensagem e Apostar mais/Entendi; Apostar mais retorna à Home com limpeza da pilha acima dela |
| HTTP 404 com esses mesmos códigos | Aviso negocial comum, sem Apostar mais: o handler de resgate é diferente do de compra assíncrona |
| HTTP 500, código 2067 | Mensagem específica de CPF vinculado ao Mercado Pago, sem diagnóstico de código/data |
| HTTP 500, tipo 1 | Mensagem negocial, código e data/hora local; string vazia é diferente de mensagem ausente |
| Demais HTTP 500 | Mensagem genérica, código e data/hora; não exibe o detalhe técnico do backend |
| Outros erros com código 018003 | Cadastro Mercado Pago conforme a revisão anterior; Pix não abre cadastro nem vira sucesso |
| Outros erros | Mensagem negocial/fallback, código e data/hora; HTTP 408 preserva a mensagem de tempo esgotado |
| Redirect `/carrinhos/verifica-compra-processamento` | Encerra loading e mantém o formulário, sem alerta, sucesso, polling ou reenvio |
| Outros valores reconhecidos pelo enum, sem ação em `trataRedict` | Mantém formulário sem navegar; inclui `home`, ativações, modalidades, aposta, validar CPF e selecionar lotérica |

Os resultados de domínio distinguem `Redeemed`, `RegistrationRequired`, `NoticeRequired`, `RedirectRequired` e `Unchanged`. Somente `Redeemed` aciona o estado de sucesso e o aviso de Teimosinha. Nenhum redirect recebido é usado como URL arbitrária; o host mapeia enums para rotas existentes.

## Estado, DI e componentes

`PrizeRedemptionViewModel` recebe o relógio local pelo composition root, cria o aviso uma vez e mantém o formulário/e-mail. A data/hora não é recalculada a cada recomposição. Avisos usam identidade para rejeitar callbacks obsoletos/duplicados; os redirects são consumidos uma vez. Enquanto o aviso está aberto ou outro destino está ativo, nova operação fica bloqueada.

O retorno por `ON_RESUME` apenas libera a pausa de navegação; não conclui resgate, não repete POST e não libera a pausa independente de cadastro Mercado Pago. A tentativa seguinte exige ação explícita. O uso da conta/login existente preserva suas decisões de pós-login; a volta contextual completa ao resgate ainda precisa de homologação.

`LegacyNoticeFormatting` em `core:presentation` passou a centralizar os textos comuns e a formatação mensagem/código/data usados por pagamento e resgate. `NavigationResumeEffect` compartilha a observação de retorno. Não existe dependência direta de `retail` para `payment`. O módulo compartilhado habilita desugaring para uso de `java.time` no minSdk 24; o build Android dessa configuração continua pendente.

O transporte preserva também `tipo` no erro, inclusive quando o JSON fornece o número `1`. O parser permanece limitado e sem corpo bruto/request/cause no texto da exceção. As regras ProGuard existentes para o DTO e os metadados incluem o novo campo.

## Cenários reproduzíveis

Foram adicionados treze mocks específicos: `PRIZE_LOGIN_REQUIRED`, `PRIZE_LOGIN_REDIRECT`, `PRIZE_TERMS_REQUIRED`, `PRIZE_ACCOUNT_REGISTRATION_REQUIRED`, `PRIZE_UNAVAILABLE`, `PRIZE_CART_REDIRECT`, `PRIZE_FAVORITE_CARTS_REDIRECT`, `PRIZE_MORE_BETS_REQUIRED`, `PRIZE_BUSINESS_RULE`, `PRIZE_SERVER_MESSAGE`, `PRIZE_SERVER_FAILURE`, `PRIZE_INVALID_IDENTIFICATION` e `PRIZE_PROCESSING_REDIRECT`.

Todos iniciam na tela Mercado Pago, alteram somente o endpoint de resgate e mantêm detalhe/preparação disponíveis. As mensagens são explicitamente simuladas. Os testes exercitam o transporte Retrofit em memória para os dois canais, a prioridade dos redirects, mensagens, destinos e bloqueio de reenvio; não realizam resgates/autenticações reais.

## Evidência executada

Artefatos em `build/verification/prize-http-errors-20260907/`:

- **422 testes JVM aprovados**: 334 de domínio/estados/rotas/regras, 28 de rede, 53 de data, 4 de preferências e 3 de navegação mock do host. A execução final inclui os vinte redirects reconhecidos sem ação e o estado `Unchanged`.
- Compilação isolada dos fontes alterados de Compose, host e composition root concluída, incluindo o relógio no resgate e o efeito de retomada compartilhado. Permanece a depreciação preexistente de `LocalClipboardManager` no pagamento.
- Lógica selecionada de `retail`, incluindo corrotinas/estados: **405/414 linhas (97,83%)**; regras dos novos avisos: **24/24**. O número não inclui renderização de telas.
- Repository/data source e auxiliares de lotéricas/resgates: **207/207**; domínio compilado de `retail`: **118/118**; parser/metadados/DTO de resgate: **26/26**.
- Formatação compartilhada: **5/5**; mapeamento de destinos de resgate no host: **9/9**. A lógica selecionada de pagamento permanece em **452/457 (98,91%)**; a pequena alteração no denominador corresponde à extração da formatação compartilhada, não à exclusão de corrotinas.
- Relatórios: `feature-retail-coverage.xml`, `retail-repository-coverage.xml`, `retail-domain-coverage.xml`, `prize-transport-coverage.xml`, `core-presentation-coverage.xml`, `apostas-coverage.xml` e `feature-payment-coverage.xml`.

Esses resultados são escopos selecionados, **não cobertura global**. A tentativa `./gradlew :core:domain:test :core:network:test :core:data:test :core:presentation:testDebugUnitTest :feature:retail:testDebugUnitTest --offline` não iniciou as tarefas: `Operation not permitted` no lock `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`. Nenhuma restrição foi contornada. Dependências em cache permitiram compilação isolada, mas não geração Hilt/KSP, APK, R8, flavors ou callbacks Android. Não foi gerada captura pareada atual nesta revisão.

## Limites ainda presentes

A revisão posterior [Transporte transacional](PRIZE_TRANSACTION_TRANSPORT_AUDIT.md) separa o cliente HTTP do POST de resgate e adiciona a proteção de corpo não reutilizável para resgate/compra. Os números acima continuam sendo a evidência desta revisão, não da árvore posterior.

Os erros HTTP da consulta do token receberam tratamento posterior em [Erros da consulta do token](REGISTRATION_TOKEN_ERROR_AUDIT.md), que compartilha as decisões gerais e os avisos sem acoplar as features. Os limites listados abaixo são históricos; os demais endpoints e a homologação integral permanecem pendentes.

- Redirect desconhecido ou vazio em resposta de resgate gera alerta de operação não concluída. Os vinte valores reconhecidos pelo enum que esse handler ignora, incluindo `/carrinhos/verifica-compra-processamento`, mantêm o formulário sem declarar sucesso. O marcador abreviado sem `/carrinhos/` não é aceito como equivalente. Erros malformados/ausentes usam fallback, sem reconstruir todos os textos de falha de parsing legados.
- Corpos HTTP de sucesso que contêm erro/redirect continuam rejeitados, conforme a proteção já registrada na revisão anterior; isso não certifica o comportamento de todos os payloads contraditórios.
- Falhas sem resposta HTTP, mudança de conectividade antes do login e os demais endpoints citados acima ainda precisam de revisão específica. Este trabalho não implementa a navegação de erro do serviço de token de cadastro.
- Permanecem pendentes captura pareada dos avisos/loading, acessibilidade, pilha/retorno no APK, morte do processo, reconciliação, Hilt/KSP, R8, todos os flavors/Release e cobertura global maior que 95%.
