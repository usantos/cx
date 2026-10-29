# Acesso aos dados por funcionalidade

Referência do código atual, conferida em 29/10/2026. Veja também os [serviços, endpoints e contratos HTTP por funcionalidade](#serviços-e-endpoints-por-funcionalidade). Este guia descreve classes que já existem; não adiciona uma fachada global nem altera o comportamento do aplicativo.

## O que chamar primeiro

| Necessidade | Entrada recomendada | Diferença importante |
| --- | --- | --- |
| Nome, CPF, e-mail e nascimento da identidade atual | [CurrentUserIdentityProvider](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/port/CurrentUserIdentityProvider.kt)`.currentUser()` | Leitura local do token da sessão; retorna `AuthenticatedUser?`. Não consulta nem atualiza o cadastro. |
| Cadastro completo do usuário | [GetAccountProfileUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/GetAccountProfileUseCase.kt)`()` | Retorna `AppResult<AccountProfile>`; inclui endereço, contato e limites. |
| Apostas já confirmadas | [GetConfirmedBetsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetConfirmedBetsUseCase.kt)`.page(filters)` | Retorna `AppResult<ConfirmedBetsPage>`; conserva os metadados do backend de origem. |
| Apostas de uma compra | [GetPurchaseBetsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchaseBetsUseCase.kt)`(purchaseId)` | Retorna a lista de `ConfirmedBet` da compra, não o carrinho atual. |
| Histórico de compras | [GetPurchasesUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchasesUseCase.kt)`(filters)` | Retorna `Purchase`; o detalhamento é uma consulta separada. |
| Apostas ainda no carrinho | [GetCurrentCartUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/GetCurrentCartUseCase.kt)`()` | Retorna `ShoppingCart`; escolhe o carrinho remoto autenticado ou o local do visitante. |
| Parâmetros de negócio do app | [GetHomeFeatureConfigurationUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt)`()` | Flags da Home, concursos especiais e campanhas de parâmetros do backend; não reúne todos os dados do app. |

## Onde chamar e como receber

A tela Compose chama ações do seu ViewModel e observa `state` com coleta consciente do lifecycle. O ViewModel recebe os contratos de casos de uso por construtor; a Route e os módulos Hilt fazem a composição. O estado e os reducers permanecem em Presentation/ReduxKotlin. Regras de negócio ficam em Domain e acesso a rede/persistência em Data/Transport.

```text
Screen/Route → ViewModel → UseCase → Repository → DataSource → API ou armazenamento
                         ↓
                  modelo de domínio
```

Não instancie `RepositoryImpl`, clientes Retrofit, `SessionTokenStore` ou DTOs na tela para buscar dados. Não use um ViewModel de outra tela como serviço global. Para integrar outra feature, respeite as dependências e contratos `api` existentes; não adicione um atalho entre Presentation e Data. Consulte também [o contrato MVVM](FEATURE_MVVM_CONTRACT.md) e [a arquitetura](APP_ARCHITECTURE.md).

`AppResult<T>` diferencia `Success(value)` de `Failure(error)`. `ServiceResult<T>` também distingue `Rejected(failure)` (decisão de negócio, inclusive redirecionamento) de `Failed(error)` (falha). Não transforme esses resultados em listas vazias ou autorização concedida. A identidade local não valida sozinha expiração, situação da conta ou autorização. Siga os gates de sessão e os casos de uso do fluxo antes de acessar dados privados. Nem todo contrato é `suspend`: leituras locais podem ser síncronas; observações podem retornar `Flow` ou `Closeable`.

Exemplos de chamadas em uma função suspensa de coordenação, com dependências já fornecidas pela composição:

```kotlin
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsPage
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase

suspend fun consultarCadastro(
    consultar: GetAccountProfileUseCase,
): AppResult<AccountProfile> = consultar()

suspend fun consultarApostas(
    consultar: GetConfirmedBetsUseCase,
    filtros: Map<String, String>,
): AppResult<ConfirmedBetsPage> = consultar.page(filtros)
```

Essas funções são exemplos de composição, não novas classes a criar nem rotinas de busca automática de todos os dados. Para estados, erros, filtros e paginação de uma tela existente, reutilize o fluxo do seu ViewModel.

## Usuário, cadastro e sessão — Account

| Necessidade | Classe / chamada | Dados ou comportamento |
| --- | --- | --- |
| Identidade de exibição | [CurrentUserIdentityProvider](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/port/CurrentUserIdentityProvider.kt)`.currentUser()` | `AuthenticatedUser?`: `name`, `cpf`, `email`, `birthDate`. Pode ser `null` no modo visitante ou sem identidade completa no token. |
| Cadastro | [GetAccountProfileUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/GetAccountProfileUseCase.kt)`()` | `AccountProfile`: CPF, nome, e-mail, CEP, gênero, município/UF/cidade, limites diário/mínimo/autorizado, preferência de notificações, nascimento e telefone. |
| Sessão ainda corrente | [AuthenticatedSessionIdentityProvider](../feature/account/api/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/port/AuthenticatedSessionIdentityProvider.kt)`.captureIdentity()` / `.isCurrent(identity)` | Identidade opaca por referência; `ownerKey` não é CPF nem token. Nome lembrado e hash não provam sessão ativa; esse vínculo não é uma verificação isolada de expiração. |
| Snapshot do pós-login | [ObserveAuthenticatedSessionContentUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ObserveAuthenticatedSessionContentUseCase.kt)`()` | `Flow<AuthenticatedSessionContent?>`: carrinho, última notificação, pagamento não identificado e necessidade de revisão do carrinho local. É estado em memória, não recarrega tudo da rede. |
| Verificar acesso durante uso | [VerifyAccountAccessUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/VerifyAccountAccessUseCase.kt)`()` | `AccountAccess`: destino, suspensão temporária, limite e situação. É validação, não substituto da consulta de cadastro. |
| Concluir o pós-login | [VerifyPostLoginAccessUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/VerifyPostLoginAccessUseCase.kt)`()` | Verifica bloqueio antes dos dados pós-login e revalida a identidade capturada; trata `Verified`, `BlockedAccountCheckFailed` e `AccountCheckFailed`. |
| Consultar endereço pelo CEP | [ResolveZipCodeUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ResolveZipCodeUseCase.kt)`(zipCode)` | `AppResult<ZipCodeLocation>`. |
| Recalcular limite autorizado | [RecalculateAuthorizedLimitUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/RecalculateAuthorizedLimitUseCase.kt)`()` | `AppResult<BigDecimal>`; não é um saldo bancário genérico. |
| Alterar cadastro | [UpdatePersonalDataUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/UpdatePersonalDataUseCase.kt)`(update, profile)` / [RegisterAccountUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/RegisterAccountUseCase.kt)`(registration)` | Operações de escrita; não chamar apenas para obter dados. |
| Consultar disponibilidade de biometria | [BiometricLoginPreferences](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/port/BiometricLoginPreferences.kt) | Preferência, disponibilidade, existência de credencial e primeiro nome lembrado; não fornece refresh token e não autentica. |
| Entrar ou sair | [LoginViewModel](../feature/account/presentation/src/main/java/br/gov/caixa/loterias/feature/account/presentation/viewmodel/LoginViewModel.kt) / [LogoutViewModel](../feature/account/presentation/src/main/java/br/gov/caixa/loterias/feature/account/presentation/viewmodel/LogoutViewModel.kt) | Coordenam protocolo e verificações. Para sair, seguir o fluxo de logout; `SessionAuthentication.logout()` sozinho não equivale ao logout completo remoto/SSO. |

Fluxo real de cadastro: [PersonalDataViewModel](../feature/account/presentation/src/main/java/br/gov/caixa/loterias/feature/account/presentation/viewmodel/PersonalDataViewModel.kt) → [GetAccountProfileUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/GetAccountProfileUseCase.kt) → [GetAccountProfile](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/GetAccountProfile.kt) → [AccountRepository](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/repository/AccountRepository.kt). Identidade local: [JwtCurrentUserIdentityProvider](../core/auth/src/main/java/br/gov/caixa/loterias/core/auth/session/JwtCurrentUserIdentityProvider.kt). O armazenamento transitório de autenticação é [SessionTokenStore](../core/auth/src/main/java/br/gov/caixa/loterias/core/auth/session/SessionTokenStore.kt); não deve virar um serviço de dados de telas.

## Apostas confirmadas, compras e conferência — Tickets

As consultas pessoais de histórico e compras pertencem à sessão autenticada. Conferência manual/por leitura de bilhete é um fluxo próprio, inclusive para entrada de visitante nas telas públicas; não use o histórico de conta para conferir um código.

| Necessidade | Chamada | Retorno |
| --- | --- | --- |
| Histórico de apostas | [GetConfirmedBetsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetConfirmedBetsUseCase.kt)`.page(filters)` | `AppResult<ConfirmedBetsPage>`; `invoke(filters)` devolve somente `List<ConfirmedBet>`. |
| Configuração dos filtros do histórico | [GetConfirmedBetsConfigurationUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetConfirmedBetsConfigurationUseCase.kt)`()` | `ConfirmedBetsConfiguration`. |
| Histórico de compras | [GetPurchasesUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchasesUseCase.kt)`(filters)` | `List<Purchase>`. |
| Itens da compra | [GetPurchaseBetsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchaseBetsUseCase.kt)`(purchaseId)` | `List<ConfirmedBet>`. |
| Compra completa | [GetPurchaseDetailsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchaseDetailsUseCase.kt)`(purchase)` | `PurchaseDetails`: compra, apostas e data/hora do servidor quando disponível. |
| Filtros de compras | [GetPurchaseMonthsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchaseMonthsUseCase.kt)`()`; [GetPurchasePaymentMethodsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchasePaymentMethodsUseCase.kt)`()`; [GetPurchaseStatusesUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchaseStatusesUseCase.kt)`(paymentMethodId)` | Meses e opções de meio de pagamento/situação. |
| Detalhe de uma aposta confirmada | [LoadConfirmedBetUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/LoadConfirmedBetUseCase.kt)`(bet)` | `ServiceResult<ConfirmedBet>`; usado para carregamento completo, inclusive tratamento de recibo e decisões de serviço. |
| Recuperar/atualizar aposta | [RecoverConfirmedBetUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/RecoverConfirmedBetUseCase.kt)`(bet)` | `AppResult<ConfirmedBet>`; tem contrato diferente do carregamento completo. |
| Conferir código | [CheckTicketUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/CheckTicketUseCase.kt)`.result(query)` | `CheckedTicketResult`; `invoke(query)` retorna somente a lista de apostas. `prepareScannedQuery(code, orientation)` prepara leitura compatível com orientação/payload. |
| Favoritar uma compra | [SavePurchaseAsFavoriteUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/SavePurchaseAsFavoriteUseCase.kt)`(purchaseId, name, keepSurprises)` | Escrita; preserva a opção de surpresinhas. |

[ConfirmedBet](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/model/ConfirmedBet.kt) inclui ID, modalidade, concurso, data, situação, números, prêmio, metadados de recorrência/bolão e recibo. Não é o modelo [BetDraft](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/model/BetDraft.kt) de uma aposta em edição, nem [CartItem](../feature/cart/api/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/model/CartItem.kt) do carrinho.

A composição existente usa [TicketsViewModel](../feature/tickets/presentation/src/main/java/br/gov/caixa/loterias/feature/tickets/presentation/viewmodel/TicketsViewModel.kt) para listagem, filtros e compras, e [BetDetailViewModel](../feature/tickets/presentation/src/main/java/br/gov/caixa/loterias/feature/tickets/presentation/viewmodel/BetDetailViewModel.kt) para detalhe. [TicketsRepositoryImpl](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/repository/TicketsRepositoryImpl.kt) faz o mapeamento; [TicketsRemoteDataSourceImpl](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsRemoteDataSource.kt) escolhe SILCE ou histórico dinâmico/Nova API conforme os parâmetros e a política vigente. Não fixe esse backend na tela. Preserve o backend devolvido por `page(...)` no fluxo de detalhe.

Os mapas de filtros não são campos arbitrários: a montagem pertence ao fluxo de Tickets. Na Presentation existente, [BetFilters](../feature/tickets/presentation/src/main/java/br/gov/caixa/loterias/feature/tickets/presentation/state/TicketsUiState.kt)`.query()` monta a consulta de apostas, e [PurchaseFilters](../feature/tickets/presentation/src/main/java/br/gov/caixa/loterias/feature/tickets/presentation/state/TicketsUiState.kt)`.query(offset)` monta a de compras. São modelos de Presentation, não dependências a importar em Domain/Data. Consulte os coordenadores de histórico e os modelos de filtros existentes antes de introduzir outra chamada; não envie CPF de outro usuário nem invente chaves de paginação. Uma lista vazia não significa que todos os concursos foram consultados.

## Catálogo, novas apostas, favoritos e Rapidão — Betting

| Dados / fluxo | Classes | Observação |
| --- | --- | --- |
| Modalidades, parâmetros e combos | [GetLotteryModalitiesUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetLotteryModalitiesUseCase.kt), [GetBettingParametersUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetBettingParametersUseCase.kt), [GetBetCombosUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetBetCombosUseCase.kt) | Retornam listas/modelos de domínio em `AppResult`; base do catálogo e da Home. |
| Concursos especiais | [GetBettingSpecialContestsUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetBettingSpecialContests.kt) | Lê `HomeSpecialContests` da configuração compartilhada. |
| Favoritos | [GetFavoriteBetsUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetFavoriteBetsUseCase.kt)`(modalityId)` / `.page(modalityId, offset)` | Apostas favoritas da conta; não confundir com carrinhos favoritos. |
| Configuração do Rapidão | [GetRapidBetConfigurationUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetRapidBetConfigurationUseCase.kt) | `ServiceResult<RapidBetConfiguration>`. |
| Entrada com regras de negócio | [PrepareBetEntryUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/PrepareBetEntryUseCase.kt), [PreparePoolEntryUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/PreparePoolEntryUseCase.kt), [PrepareHomeBettingActionUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/PrepareHomeBettingActionUseCase.kt) | Produzem decisões tipadas; não substituir por navegação direta. |
| Incluir itens no carrinho | [AddBetToCartUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/AddBetToCartUseCase.kt), [AddComboToCartUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/AddComboToCartUseCase.kt), [AddFavoriteBetToCartUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/AddFavoriteBetToCartUseCase.kt) | Escritas; não são consultas de apostas já compradas. |
| Gerar Rapidão e gravar configuração | [GenerateRapidBetsUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GenerateRapidBetsUseCase.kt), [SaveRapidBetConfigurationUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/SaveRapidBetConfigurationUseCase.kt) | Operações com efeitos; `GenerateRapidBetsUseCase(quantity)` retorna `ServiceResult<ShoppingCart>`. |
| Favoritar / excluir | [ValidateFavoriteBetUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/ValidateFavoriteBetUseCase.kt), [SaveFavoriteBetUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/SaveFavoriteBetUseCase.kt), [DeleteFavoriteBetUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/DeleteFavoriteBetUseCase.kt) | Distinguir validação da escrita. |
| Aposta Shake | [GetShakeEnabledUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetShakeEnabledUseCase.kt) e [HomePreferencesUseCases](../feature/home/domain/src/main/kotlin/br/gov/caixa/loterias/feature/home/domain/usecase/HomePreferencesUseCases.kt) | Preferência de uso, avisos e tutorial; não é histórico de apostas. |

Consumidores existentes: [BettingCatalogViewModel](../feature/betting/presentation/src/main/java/br/gov/caixa/loterias/feature/betting/presentation/viewmodel/BettingCatalogViewModel.kt), [BettingGameViewModel](../feature/betting/presentation/src/main/java/br/gov/caixa/loterias/feature/betting/presentation/viewmodel/BettingGameViewModel.kt), [BettingFlowViewModel](../feature/betting/presentation/src/main/java/br/gov/caixa/loterias/feature/betting/presentation/viewmodel/BettingFlowViewModel.kt), [FavoriteBetsViewModel](../feature/betting/presentation/src/main/java/br/gov/caixa/loterias/feature/betting/presentation/viewmodel/FavoriteBetsViewModel.kt), [RapidConfigurationViewModel](../feature/betting/presentation/src/main/java/br/gov/caixa/loterias/feature/betting/presentation/viewmodel/RapidConfigurationViewModel.kt), [RapidListViewModel](../feature/betting/presentation/src/main/java/br/gov/caixa/loterias/feature/betting/presentation/viewmodel/RapidListViewModel.kt).

## Carrinho atual, preservado e favoritos — Cart

| Necessidade | Classes | Observação |
| --- | --- | --- |
| Carrinho atual | [GetCurrentCartUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/GetCurrentCartUseCase.kt) | `AppResult<ShoppingCart>`. [SessionCartRepositoryImpl](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/repository/SessionCartRepositoryImpl.kt) seleciona remoto ou local conforme a autenticação. |
| Revisar apostas preservadas | [GetLocalCartReviewUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/GetLocalCartReviewUseCase.kt)`(identity)` | `LocalCartReviewSnapshot`: apostas locais pendentes, carrinho da conta e revisão; exige identidade ainda corrente. |
| Carrinhos favoritos e seus itens | [GetFavoriteCartsUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/GetFavoriteCartsUseCase.kt); [GetFavoriteCartBetsUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/GetFavoriteCartBetsUseCase.kt)`(cartId)` | `List<FavoriteCart>` e `List<FavoriteCartBet>`. |
| Validar antes de pagar | [ValidateCartUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/ValidateCartUseCase.kt), [VerifyCartPaymentAccountUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/VerifyCartPaymentAccountUseCase.kt), [CheckCheckoutLocationUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/CheckCheckoutLocationUseCase.kt) | Resultados/decisões tipados; visitante não recebe autorização de pagamento. |
| Compra recente | [HasRecentPurchaseUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/HasRecentPurchaseUseCase.kt)`(amount)`; [WasFavoriteCartAddedUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/WasFavoriteCartAddedUseCase.kt)`(cartId)` | Controles locais do fluxo; não substituem o histórico remoto de compras. |
| Alterações | [RemoveCartItemUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/RemoveCartItemUseCase.kt), [ClearCartUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/ClearCartUseCase.kt), [SaveCurrentCartFavoriteUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/SaveCurrentCartFavoriteUseCase.kt), [AddFavoriteCartToCurrentUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/AddFavoriteCartToCurrentUseCase.kt) | Mudam o carrinho ou os favoritos. |
| Excluir revisão preservada | [DiscardLocalCartReviewUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/DiscardLocalCartReviewUseCase.kt) | Altera revisão/carrinho; não chamar para obter dados. |

Consumidores: [CartViewModel](../feature/cart/presentation/src/main/java/br/gov/caixa/loterias/feature/cart/presentation/viewmodel/CartViewModel.kt), [FavoriteCartsViewModel](../feature/cart/presentation/src/main/java/br/gov/caixa/loterias/feature/cart/presentation/viewmodel/FavoriteCartsViewModel.kt) e [LocalCartReviewViewModel](../feature/cart/presentation/src/main/java/br/gov/caixa/loterias/feature/cart/presentation/viewmodel/LocalCartReviewViewModel.kt). Os demais comandos de exclusão, validação de nome e registro estão no catálogo completo abaixo.

## Resultados de sorteios — Results

| Consulta | Chamada | Retorno |
| --- | --- | --- |
| Último resultado | [GetLatestResultUseCase](../feature/results/domain/src/main/kotlin/br/gov/caixa/loterias/feature/results/domain/usecase/GetLatestResultUseCase.kt)`(modality)` | `AppResult<DrawResult>`. |
| Concurso específico | [GetResultByContestUseCase](../feature/results/domain/src/main/kotlin/br/gov/caixa/loterias/feature/results/domain/usecase/GetResultByContestUseCase.kt)`(modality, contest)` | `AppResult<DrawResult>`. |
| Resultados relacionados a uma aposta | [GetConfirmedBetResultsUseCase](../feature/results/domain/src/main/kotlin/br/gov/caixa/loterias/feature/results/domain/usecase/GetConfirmedBetResultsUseCase.kt)`(bet)` | `ServiceResult<List<DrawResult>>`; considera os dados da aposta confirmada. |

Consumidores: [ResultsViewModel](../feature/results/presentation/src/main/java/br/gov/caixa/loterias/feature/results/presentation/viewmodel/ResultsViewModel.kt) e [ConfirmedBetResultsViewModel](../feature/results/presentation/src/main/java/br/gov/caixa/loterias/feature/results/presentation/viewmodel/ConfirmedBetResultsViewModel.kt). Resultados públicos não exigem uma identidade pessoal; detalhes de apostas e prêmios seguem as proteções do seu fluxo.

## Bolões à venda — Marketplace

| Consulta / ação | Chamada | Retorno / requisito |
| --- | --- | --- |
| Buscar bolões | [SearchMarketplaceUseCase](../feature/marketplace/domain/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/usecase/SearchMarketplaceUseCase.kt)`(filter, page)` | `AppResult<MarketplacePage>`; filtro tipado, localização e paginação. |
| Detalhe do bolão | [GetMarketplaceDetailUseCase](../feature/marketplace/domain/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/usecase/GetMarketplaceDetailUseCase.kt)`(poolCode)` | `AppResult<MarketplacePoolDetail>`. |
| Concursos especiais para filtros | [GetMarketplaceSpecialContestsUseCase](../feature/marketplace/api/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/usecase/GetMarketplaceSpecialContestsUseCase.kt)`()` | `AppResult<MarketplaceSpecialContests>`; contrato no módulo `api` de Marketplace. |
| Adicionar cotas | [AddMarketplacePoolToCartUseCase](../feature/marketplace/domain/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/usecase/AddMarketplacePoolToCartUseCase.kt)`(order)` | Escrita; mantém as verificações de acesso do fluxo. |

Consumidores: [MarketplaceViewModel](../feature/marketplace/presentation/src/main/java/br/gov/caixa/loterias/feature/marketplace/presentation/viewmodel/MarketplaceViewModel.kt) e [MarketplaceDetailViewModel](../feature/marketplace/presentation/src/main/java/br/gov/caixa/loterias/feature/marketplace/presentation/viewmodel/MarketplaceDetailViewModel.kt).

## Filtros de bolões — Marketplace Filter

Esta feature tem Presentation e reutiliza os contratos de Marketplace; não possui um novo Repository/Domain próprio.

Use [MarketplaceFilterViewModel](../feature/marketplace-filter/presentation/src/main/java/br/gov/caixa/loterias/feature/marketplacefilter/presentation/viewmodel/MarketplaceFilterViewModel.kt) para o estado da tela; [MarketplaceFilter](../feature/marketplace/api/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/model/MarketplaceFilter.kt) para os valores, [MarketplaceFilterRules](../feature/marketplace/api/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/rule/MarketplaceFilterRules.kt) para as regras, [GetMarketplaceFilterParametersUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt) para limites de quantidade e [GetMarketplaceSpecialContestsUseCase](../feature/marketplace/api/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/usecase/GetMarketplaceSpecialContestsUseCase.kt) para concursos especiais. A busca efetiva continua em `SearchMarketplaceUseCase`. O provider [MarketplaceLocationProvider](../feature/marketplace/api/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/port/MarketplaceLocationProvider.kt) fornece localização conforme seu contrato; não é um histórico de apostas.

## Pagamento e acompanhamento da compra — Payment

| Necessidade | Classes / chamada | Natureza |
| --- | --- | --- |
| Meios de pagamento | [GetPaymentMethodsUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/GetPaymentMethodsUseCase.kt)`()` | Consulta: `List<PaymentMethod>`. |
| Cartões salvos | [GetSavedCardsUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/GetSavedCardsUseCase.kt)`(paymentMethodId)` | Consulta privada: `List<SavedPaymentCard>`. |
| Andamento da compra | [GetPurchaseProgressUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/GetPurchaseProgressUseCase.kt)`(purchaseId)` | Consulta: `PurchaseProgress`; não reenviar o pagamento para consultar status. |
| Validação DAM | [ValidateDamUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/ValidateDamUseCase.kt)`()` | Validação do fluxo, com `DamValidation`. |
| Cobrança Pix | [GeneratePixChargeUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/GeneratePixChargeUseCase.kt)`()` | Gera cobrança (`PixCharge`); tem efeito, não é simples leitura. |
| Cartão / registro externo | [SubmitCardPaymentUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/SubmitCardPaymentUseCase.kt)`(request)`; [PrepareMercadoPagoRegistrationUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/PrepareMercadoPagoRegistrationUseCase.kt)`(email)` | Submissão e preparação de operação externa; seguir decisões/continuações do fluxo. |
| Excluir cartão | [DeleteSavedCardUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/DeleteSavedCardUseCase.kt)`(cardId, paymentMethodId)` | Escrita que retorna a lista atualizada. |

Consumidores: [PaymentViewModel](../feature/payment/presentation/src/main/java/br/gov/caixa/loterias/feature/payment/presentation/viewmodel/PaymentViewModel.kt), [PaymentCardViewModel](../feature/payment/presentation/src/main/java/br/gov/caixa/loterias/feature/payment/presentation/viewmodel/PaymentCardViewModel.kt) e [MercadoPagoRegistrationViewModel](../feature/payment/presentation/src/main/java/br/gov/caixa/loterias/feature/payment/presentation/viewmodel/MercadoPagoRegistrationViewModel.kt). Não iniciar cobranças, envio de cartão ou cadastro externo ao montar um painel de consultas.

## Lotéricas, prêmios e comprovantes — Retail

| Necessidade | Classes | Observação |
| --- | --- | --- |
| Pesquisar lotéricas / favoritas | [SearchRetailersUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/SearchRetailersUseCase.kt)`(query)`; [GetFavoriteRetailersUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/GetFavoriteRetailersUseCase.kt)`()` | `List<Retailer>`. |
| Detalhe do prêmio | [GetPrizeDetailsUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/GetPrizeDetailsUseCase.kt)`(betId)` | `AppResult<PrizeDetails>`; dados vinculados à aposta. |
| Comprovante da aposta / prêmio | [DownloadBetReceiptUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/DownloadBetReceiptUseCase.kt)`(betId)`; [DownloadPrizeReceiptUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/DownloadPrizeReceiptUseCase.kt)`(betId)` | `AppResult<ByteArray>`; a Route coordena arquivo/URI/compartilhamento. |
| Elegibilidade de localização | [CheckRetailLocationUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/CheckRetailLocationUseCase.kt) | Retorna `RetailLocationEligibility`; não contornar a regra de país. |
| Consultar chave Pix | [ConsultPixRedemptionKeyUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/ConsultPixRedemptionKeyUseCase.kt)`(betId, key)` | `AppResult<PixBeneficiary>`; distinto de efetivar o resgate. |
| Alterar favorito / gerar código / resgatar / cancelar | [SetRetailerFavoriteUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/SetRetailerFavoriteUseCase.kt), [GenerateLotteryRedemptionCodeUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/GenerateLotteryRedemptionCodeUseCase.kt), [RedeemPrizeUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/RedeemPrizeUseCase.kt), [CancelPixRedemptionUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/CancelPixRedemptionUseCase.kt) | Operações com efeitos; não executar para coletar dados de leitura. |

Consumidores: [RetailSearchViewModel](../feature/retail/presentation/src/main/java/br/gov/caixa/loterias/feature/retail/presentation/viewmodel/RetailSearchViewModel.kt), [PrizeRedemptionViewModel](../feature/retail/presentation/src/main/java/br/gov/caixa/loterias/feature/retail/presentation/viewmodel/PrizeRedemptionViewModel.kt) e [LocationGateViewModel](../feature/retail/presentation/src/main/java/br/gov/caixa/loterias/feature/retail/presentation/viewmodel/LocationGateViewModel.kt).

## Notificações e preferências — Notifications / Account

| Necessidade | Classe | Observação |
| --- | --- | --- |
| Central remota de notificações | [NotificationInboxActions](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/NotificationInboxActions.kt)`.load(identity) / .markRead(identity, id) / .remove(identity, id) / .clear(identity)` | Usa `NotificationInboxRepository`; consulta e altera o histórico do backend com vínculo à sessão. |
| Mensagens locais | [GetNotificationsUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/GetNotificationsUseCase.kt) | `AppResult<List<AppNotification>>`; repositório local, sem consulta à central remota. |
| Preferências | [GetNotificationPreferencesUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/GetNotificationPreferencesUseCase.kt) | `AppResult<List<NotificationPreference>>`. |
| Quantidade não lida | [GetUnreadNotificationCountUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/GetUnreadNotificationCountUseCase.kt) | Retorna `Int`; sem identidade, sessão substituída ou falha tratada retorna zero. Não use zero como prova de sucesso da API. |
| Última notificação do pós-login | [ObserveAuthenticatedSessionContentUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ObserveAuthenticatedSessionContentUseCase.kt) | A propriedade `latestNotification` é snapshot em memória; não é a caixa completa. |
| Marcar última notificação | [ReadLatestNotificationUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ReadLatestNotificationUseCase.kt) | Usa `AccountNotification` vinculada à sessão; pode produzir decisão de sessão obsoleta. |
| Alterações nas mensagens locais | [MarkNotificationReadUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/MarkNotificationReadUseCase.kt), [MarkAllNotificationsReadUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/MarkAllNotificationsReadUseCase.kt), [DeleteNotificationUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/DeleteNotificationUseCase.kt), [DeleteAllNotificationsUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/DeleteAllNotificationsUseCase.kt) | Escrita. |
| Alterar preferências | [SetNotificationPreferenceUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/SetNotificationPreferenceUseCase.kt)`(category, enabled)` | Escrita; `category` pode ser `null` conforme o contrato. |
| Registrar dispositivo push | [RegisterPushDevice](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/push/usecase/RegisterPushDevice.kt)`(token)` | Operação autenticada e idempotente por titular/token; não é leitura da caixa. |

Consumidores: [NotificationsViewModel](../feature/notifications/presentation/src/main/java/br/gov/caixa/loterias/feature/notifications/presentation/viewmodel/NotificationsViewModel.kt) e [CentralNotificationsViewModel](../feature/notifications/presentation/src/main/java/br/gov/caixa/loterias/feature/notifications/presentation/viewmodel/CentralNotificationsViewModel.kt). `NotificationsViewModel` usa mensagens/preferências locais; `CentralNotificationsViewModel` usa `NotificationInboxActions` para o histórico remoto. `AppNotification` representa mensagens locais, `NotificationInbox` representa a central remota e `AccountNotification` a notificação de sessão.

## Jogo responsável — Responsible Gaming

| Necessidade | Classe | Retorno / efeito |
| --- | --- | --- |
| Perguntas de autoavaliação | [GetSelfAssessmentQuestionsUseCase](../feature/responsible-gaming/domain/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/domain/usecase/GetSelfAssessmentQuestionsUseCase.kt) | `AppResult<List<SelfAssessmentQuestion>>`. |
| Repasses sociais | [GetSocialTransfersUseCase](../feature/responsible-gaming/domain/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/domain/usecase/GetSocialTransfersUseCase.kt) | `AppResult<SocialTransfers>`. |
| Enviar respostas | [SubmitSelfAssessmentUseCase](../feature/responsible-gaming/domain/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/domain/usecase/SubmitSelfAssessmentUseCase.kt) | Escrita autenticada; `answers` tipadas. |
| Suspender conta | [SuspendAccountUseCase](../feature/responsible-gaming/domain/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/domain/usecase/SuspendAccountUseCase.kt)`(periodHours)` | Alteração sensível de conta; não chamar como consulta de situação. |

Consumidor: [ResponsibleGamingViewModel](../feature/responsible-gaming/presentation/src/main/java/br/gov/caixa/loterias/feature/responsiblegaming/presentation/viewmodel/ResponsibleGamingViewModel.kt).

## Ajuda e termos — Support

| Necessidade | Classe | Retorno / efeito |
| --- | --- | --- |
| Seções e perguntas | [GetSupportSectionsUseCase](../feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain/usecase/GetSupportSectionsUseCase.kt); [GetSupportQuestionsUseCase](../feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain/usecase/GetSupportQuestionsUseCase.kt)`(sectionId)` | `List<SupportSection>` / `List<SupportQuestion>`. |
| Texto dos termos | [GetTermsUseCase](../feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain/usecase/GetTermsUseCase.kt) | `AppResult<String>`. |
| Aceitar termos | [AcceptTermsUseCase](../feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain/usecase/AcceptTermsUseCase.kt) | Escrita; carregar o texto não implica aceitar. |

Consumidor: [SupportViewModel](../feature/support/presentation/src/main/java/br/gov/caixa/loterias/feature/support/presentation/viewmodel/SupportViewModel.kt).

## Home, preferências e Aposta Shake — Home

A Home é composta de várias fontes, não de um serviço único de “dados do app”.

| Necessidade | Classe | Fonte / limite |
| --- | --- | --- |
| Banners de parâmetros, flags e concursos | [GetHomeFeatureConfigurationUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt) | Configuração compartilhada do backend. |
| Combos / parâmetros / resultados / repasses | [GetBetCombosUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetBetCombosUseCase.kt), [GetBettingParametersUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetBettingParametersUseCase.kt), [GetLatestResultUseCase](../feature/results/domain/src/main/kotlin/br/gov/caixa/loterias/feature/results/domain/usecase/GetLatestResultUseCase.kt), [GetSocialTransfersUseCase](../feature/responsible-gaming/domain/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/domain/usecase/GetSocialTransfersUseCase.kt) | Contratos das features donas desses dados. |
| Carrinho e última notificação já carregados | [ObserveAuthenticatedSessionContentUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ObserveAuthenticatedSessionContentUseCase.kt) | Snapshot do pós-login, observável e vinculado à sessão. |
| Ordem dos jogos, Shake e tutorial | [HomePreferencesUseCases](../feature/home/domain/src/main/kotlin/br/gov/caixa/loterias/feature/home/domain/usecase/HomePreferencesUseCases.kt)`.load(ownerKey)` | `HomePreferencesSnapshot`; persistência local. `saveOrder`, `toggleShake`, `consumeShakeTooltip` e `completeShakeTutorial` têm efeitos. |
| Ofertas de revenda vigentes | [SelectHomeResaleOffersUseCase](../feature/home/domain/src/main/kotlin/br/gov/caixa/loterias/feature/home/domain/usecase/SelectHomeResaleOffers.kt) | Seleciona uma lista já carregada com o horário do servidor; não busca outra API. |

Consumidores: [HomeViewModel](../feature/home/presentation/src/main/java/br/gov/caixa/loterias/feature/home/presentation/viewmodel/HomeViewModel.kt) e [HomePreferencesViewModel](../feature/home/presentation/src/main/java/br/gov/caixa/loterias/feature/home/presentation/viewmodel/HomePreferencesViewModel.kt). A composição das dependências está em [HomeNavigationDependencies](../feature/home/presentation/src/main/java/br/gov/caixa/loterias/feature/home/navigation/HomeNavigationDependencies.kt).

## Inicialização, tutorial, atualização obrigatória e ícones — Onboarding

| Necessidade | Classes | Forma de acesso |
| --- | --- | --- |
| Introdução / tutoriais | [ShouldShowIntroductionUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/ShouldShowIntroductionUseCase.kt), [GetBettingTutorialDismissedUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/GetBettingTutorialDismissedUseCase.kt) | Leituras locais; `CompleteIntroductionUseCase` e `SetBettingTutorialDismissedUseCase` gravam preferências. |
| Atualização obrigatória ativa | [GetActiveForceUpdateUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/GetActiveForceUpdateUseCase.kt); [RefreshForceUpdateUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/RefreshForceUpdateUseCase.kt); [ObserveForceUpdateUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/ObserveForceUpdateUseCase.kt) | Leitura síncrona, atualização suspensa e listener com `Closeable`. |
| Preset e mudanças do calendário de ícones | [GetLauncherIconPresetUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/GetLauncherIconPresetUseCase.kt); [ObserveLauncherIconCalendarUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/ObserveLauncherIconCalendarUseCase.kt) | Preset ativo e observação. A avaliação por data usa o agendamento/configuração existentes; não reimplementar a troca de aliases em outra tela. |
| Verificação de versão e segurança de rede | [IsVersionSupportedUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt); [PrepareNetworkSecurityUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt) | Gates de startup; falha de preparação não autoriza ignorar pins ou versão mínima. |

Consumidores: [StartupViewModel](../feature/onboarding/presentation/src/main/java/br/gov/caixa/loterias/feature/onboarding/presentation/viewmodel/StartupViewModel.kt), [OnboardingViewModel](../feature/onboarding/presentation/src/main/java/br/gov/caixa/loterias/feature/onboarding/presentation/viewmodel/OnboardingViewModel.kt), [ForceUpdateViewModel](../feature/onboarding/presentation/src/main/java/br/gov/caixa/loterias/feature/onboarding/presentation/viewmodel/ForceUpdateViewModel.kt) e [LauncherIconViewModel](../feature/onboarding/presentation/src/main/java/br/gov/caixa/loterias/feature/onboarding/presentation/viewmodel/LauncherIconViewModel.kt). Calendário do launcher no Firebase é distinto das campanhas dos parâmetros do backend.

## Configuração global, ambiente e infraestrutura — Core / App

| Necessidade | Classe | Observação |
| --- | --- | --- |
| Flags de negócio compartilhadas | [GetHomeFeatureConfigurationUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt)`()` / `.refresh()` | `HomeFeatureConfiguration`: bolões, combos, Rapidão, minhas apostas, campanhas e concursos especiais. |
| Limites dos filtros de bolões | [GetMarketplaceFilterParametersUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt) | `MarketplaceFilterParameters`: mínimo/máximo de quantidade. |
| Campanhas de parâmetros | [CampaignConfigurationUseCases](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/CampaignConfigurationUseCases.kt) | `settings: Flow<CommemorativeCampaignSettings>`; `load()` / `refresh()` atualizam a fonte compartilhada. |
| Versão, URLs e identificadores necessários à composição | [HostFeatureConfiguration](../core/navigation/src/main/java/br/gov/caixa/loterias/core/navigation/configuration/HostFeatureConfiguration.kt) | Configuração imutável fornecida pelo hospedeiro. Não contém os dados pessoais ou todas as respostas do app; não duplicar seu conteúdo em logs/documentos. |
| Ambiente selecionado e efetivo | [EnvironmentSelection](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/environment/usecase/EnvironmentSelection.kt) | `selected`, `effective`, `selectorVisible`. Trocar ambiente é ação explícita do fluxo e afeta a próxima inicialização. |
| Conectividade atual | [NetworkAvailability](../core/common/src/main/java/br/gov/caixa/loterias/core/common/network/NetworkAvailability.kt)`.current()` | `CONNECTED`, `DISCONNECTED` ou `UNKNOWN`. Não comprova que a API responde. |
| Horário de referência do servidor | [ServerTimeSource](../core/common/src/main/java/br/gov/caixa/loterias/core/common/time/ServerTimeSource.kt)`.now()` | Usado nas regras temporais; distinto do relógio local e do fuso do calendário anual. |

[AppConfigurationRepositoryImpl](../core/data/src/main/java/br/gov/caixa/loterias/core/data/repository/AppConfigurationRepositoryImpl.kt) compartilha cache e requisição em andamento dos parâmetros; use `refresh()` quando o contrato exigir atualização explícita. Não espalhe buscas diretas no Firebase/Retrofit para recriar esses dados. Para pins assinados e ícones, siga [rotação de pins](SECURE_PIN_ROTATION.md) e [calendário/launcher](technical-guide/launcher-icons.md).

## Como obter as instâncias na composição

[AccountModule](../feature/account/presentation/src/main/java/br/gov/caixa/loterias/feature/account/di/AccountModule.kt) fornece `GetAccountProfileUseCase`, e [TicketsModule](../feature/tickets/presentation/src/main/java/br/gov/caixa/loterias/feature/tickets/di/TicketsModule.kt) fornece os casos de uso de apostas e compras. Não chame os métodos dos módulos manualmente: deixe o Hilt resolver seus contratos.

Nos grafos já existentes, [AccountNavigationDependencies](../feature/account/presentation/src/main/java/br/gov/caixa/loterias/feature/account/navigation/AccountNavigationDependencies.kt) e [TicketsNavigationDependencies](../feature/tickets/presentation/src/main/java/br/gov/caixa/loterias/feature/tickets/navigation/TicketsNavigationDependencies.kt) fornecem as dependências estreitas usadas na criação dos ViewModels. [AppFeatureGraphDependencies](../app/src/main/java/br/gov/caixa/loterias/apostas/composition/navigation/AppFeatureGraphDependencies.kt) apenas agrega essas interfaces para compor a navegação; não é um serviço global de consultas a ser acessado pelas telas.

Para carregar uma tela já pronta, use suas ações: `PersonalDataViewModel.load()`, `TicketsViewModel.loadBets()` e `TicketsViewModel.loadPurchases()`, observando o `state` correspondente. Para uma nova tela, injete somente os casos de uso necessários no seu próprio ViewModel e preserve os gates de sessão, cancelamento e descarte de respostas antigas.

## Localizar a implementação e manter a documentação

Os links deste guia levam aos contratos ou implementações exatos. Para seguir uma consulta, abra o caso de uso e sua implementação Domain, depois o Repository e o DataSource Data. A composição Hilt fica normalmente em `feature/<funcionalidade>/presentation/.../di/`; o transporte HTTP fica em `feature/<funcionalidade>/transport/` quando esse módulo existe. `core/network` mantém provedores/configuração compartilhados; não é uma fachada única das funcionalidades.

## Serviços e endpoints por funcionalidade

As tabelas abaixo mostram o contrato HTTP declarado e seus consumidores por funcionalidade. As entradas de Domain recomendadas continuam nas seções anteriores; as interfaces Retrofit são detalhes de Data/Transport. Não é necessário chamar todos os endpoints ao abrir o app.

### Base URL, sessão e leitura dos contratos

- **Backend CAIXA**: rotas relativas a `EffectiveEnvironment.apiBaseUrl`, selecionado pelo ambiente. Os providers são montados em [BackendClientFactory](../core/network/src/main/java/br/gov/caixa/loterias/core/network/config/BackendClientFactory.kt); [CloudBackendRouting](../core/network/src/main/java/br/gov/caixa/loterias/core/network/routing/CloudBackendRouting.kt) pode aplicar o roteamento cloud configurado. A rota sozinha não identifica um host fixo de PRD/TQS/Automação.
- **Histórico dinâmico**: `TicketsBetsHistoryApi` usa a base escolhida pela política de histórico; pode chamar `apostas`, `consultar` ou `{id}/recuperar`. Não concatenar essas rotas à base do backend comum sem seguir a política.
- **Mercado Pago**: `PaymentMercadoPagoApi` usa `EffectiveEnvironment.mercadoPagoBaseUrl`. **RecargaPay**: `PaymentRecargaPayApi` usa `EffectiveEnvironment.recargaPayBaseUrl`, por `PaymentApiProvider`.
- `@Path` substitui um segmento da rota; `@Query`/`@QueryMap` geram a query string; `@Body` é o corpo JSON; `@Header` é header HTTP explícito. `@Tag session` é metadado interno Retrofit/OkHttp, não um parâmetro enviado ao servidor. O cliente vinculado usa a credencial da sessão capturada e rejeita respostas de uma sessão substituída.
- As chamadas autenticadas usam a configuração existente dos interceptors/providers. Esta documentação não altera headers. Parâmetros opcionais `null` de query são omitidos pelo Retrofit; defaults/omissão de campos JSON seguem o DTO, mapper e serializador do cliente.
- `ApiEnvelope<T>` contém `payload: T?`, `redirect: String?`, `mensagem: String?`, `dataHoraServidor: String?`. HTTP bem-sucedido não substitui a análise das decisões de negócio. `Unit` indica que o contrato não expõe dados de retorno; `ResponseBody` é conteúdo bruto. Envelopes especializados de cadastro, notificações, compras e prêmios têm contratos próprios.
- Os nomes dos campos JSON tipados estão no [dicionário de DTOs](#dicionário-dos-corpos-e-retornos-tipados). `JsonElement` é contrato aberto: os campos efetivamente montados/lidos estão detalhados em [corpos e retornos dinâmicos](#corpos-e-retornos-dinâmicos). As tabelas registram contratos do cliente, não uma especificação completa do servidor.

### Usuário, cadastro, pós-login e logout — Account

`AccountRemoteDataSourceImpl` consulta cadastro/endereço e realiza alterações. O pós-login usa o cliente dedicado `AccountVerificationBackendClient`: ele decodifica `AccountVerificationEnvelopeDto` de `ResponseBody`, exige HTTP 200 e mantém o vínculo da sessão. A declaração genérica `verifyAccount` não substitui esse fluxo. O logout completo inclui o serviço CAIXA e o SSO descrito adiante; saída de visitante é local.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [AccountLoteriasApi](../feature/account/data/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/datasource/remote/AccountLoteriasApi.kt).`neighborhoodsByZipCode` | `GET bairros` | `@Query("cep") zipCode: String` | `ApiEnvelope<List<NeighborhoodDto>>` |
| [AccountLoteriasApi](../feature/account/data/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/datasource/remote/AccountLoteriasApi.kt).`verifyAccount` | `POST logado/usuarios/verificar-cadastro` | `@Body request: AccountVerificationRequestDto = AccountVerificationRequestDto()` | `ApiEnvelope<AccountSessionDto>` |
| [AccountVerificationBackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/AccountVerificationBackend.kt).`verify` | `POST logado/usuarios/verificar-cadastro` | `@Body request: AccountVerificationRequestDto, @Tag session: AccountVerificationSession` | `Response<ResponseBody>` |
| [AccountLoteriasApi](../feature/account/data/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/datasource/remote/AccountLoteriasApi.kt).`municipalities` | `GET municipios` | `@Query("idUF") stateId: Long` | `ApiEnvelope<List<MunicipalityDto>>` |
| [AccountLoteriasApi](../feature/account/data/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/datasource/remote/AccountLoteriasApi.kt).`registerAccount` | `POST usuarios` | `@Body request: AccountRegistrationDto` | `ApiEnvelope<UserProfileDto>` |
| [AccountLoteriasApi](../feature/account/data/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/datasource/remote/AccountLoteriasApi.kt).`updatePersonalData` | `PUT usuarios` | `@Body request: PersonalDataUpdateDto` | `ApiEnvelope<Unit>` |
| [AccountLoteriasApi](../feature/account/data/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/datasource/remote/AccountLoteriasApi.kt).`recalculateAuthorizedLimit` | `POST usuarios/recalcular-limite-autorizado` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<UserProfileDto>` |
| [AccountLoteriasApi](../feature/account/data/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/datasource/remote/AccountLoteriasApi.kt).`currentUserProfile` | `GET usuarios/recuperar-dados` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<UserProfileDto>` |
| [SessionLogoutApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/SessionLogoutApi.kt).`signOut` | `POST usuarios/sair` | `@Tag session: AccountVerificationSession` | `Response<ResponseBody>` |
| [AccountBackendApi](../feature/account/data/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/datasource/remote/AccountBackendApi.kt).`blockedUserStatus` | `GET usuarios/verificar-apostador-bloqueado` | `Sem parâmetros de rota/query/body.` | `BlockedAccountVerificationEnvelopeDto` |

### Histórico de apostas, compras e conferência — Tickets

`TicketsRemoteDataSourceImpl` escolhe backend e rota conforme `BetHistoryRoutingPolicy`. Recibos históricos, recibos de prêmio e recibos comuns são alternativas conforme a aposta, não três chamadas obrigatórias. `purchaseDetails` combina a compra com `purchaseBets` e preserva a data do servidor. O QR code/código passa pelo preparo e codificação do fluxo de conferência.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`confirmedBets` | `GET apostas` | `@QueryMap filters: Map<String, String>` | `ApiEnvelope<JsonElement>` |
| [TicketsBetsHistoryApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBetsHistoryApi.kt).`confirmedBets` | `GET apostas` | `@QueryMap filters: Map<String, String>` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`confirmedBetsConfiguration` | `GET apostas/config-consulta` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`betReceipt` | `GET apostas/{id}/comprovante` | `@Path("id") betId: Long` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`prizeReceipt` | `GET apostas/{id}/comprovante-premio` | `@Path("id") betId: Long` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`checkBet` | `GET apostas/{id}/conferir` | `@Path("id") betId: Long` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`prizeDetails` | `GET apostas/{id}/detalhes-premio` | `@Path("id") betId: Long` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`checkTicket` | `POST bilhetes/conferir-bilhete` | `@Body body: JsonElement` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`checkTicketQrCode` | `POST bilhetes/conferir-bilhete-qrcode` | `@Body body: JsonElement` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`purchases` | `GET compras` | `@QueryMap filters: Map<String, String>` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`purchaseMonths` | `GET compras/meses` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`purchaseStatuses` | `GET compras/situacoes-compra` | `@Query("meioPagamento") paymentMethodId: Long?` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`purchaseBets` | `GET compras/{id}/apostas` | `@Path("id") purchaseId: Long` | `ApiEnvelope<JsonElement>` |
| [TicketsBetsHistoryApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBetsHistoryApi.kt).`novaApiConfirmedBets` | `GET consultar` | `@QueryMap filters: Map<String, String>` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`historicalPrizeReceipt` | `GET historico-apostas/{id}/comprovante-premio` | `@Path("id") betId: Long` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`savePurchaseAsFavoriteCart` | `POST logado/compras/{purchaseId}/salvar-carrinho-favorito` | `@Path("purchaseId") purchaseId: Long, @Body body: JsonElement` | `ApiEnvelope<JsonElement>` |
| [TicketsBackendApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBackendApi.kt).`paymentMethods` | `GET meios-pagamento` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [TicketsBetsHistoryApi](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsBetsHistoryApi.kt).`recoverBet` | `GET {id}/recuperar` | `@Path("id") betId: Long` | `ApiEnvelope<JsonElement>` |

### Catálogo, apostas, favoritos e Rapidão — Betting

Retornos de inclusão são `CartDto`, convertido em carrinho de domínio. Na mesma rota de inclusão, apostas comuns usam `AddBetRequestDto` e cotas de bolão usam `AddPoolShareRequestDto` em Marketplace. Favoritos e surpresinhas usam corpos montados em Data, detalhados adiante.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`saveFavoriteBet` | `POST logado/apostas-favoritas` | `@Body body: JsonElement` | `Unit` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`groupedFavoriteBets` | `GET logado/apostas-favoritas/` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<GroupedFavoriteBetsDto>` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`favoriteBetsByModality` | `GET logado/apostas-favoritas/modalidade` | `@QueryMap filters: Map<String, String>` | `ApiEnvelope<FavoriteBetPageDto>` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`validateFavoriteBet` | `POST logado/apostas-favoritas/validar` | `@Body body: JsonElement` | `Unit` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`deleteFavoriteBet` | `DELETE logado/apostas-favoritas/{id}` | `@Path("id") id: Long` | `ApiEnvelope<JsonElement>` |
| [BettingLoteriasApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingLoteriasApi.kt).`addBetToCart` | `POST logado/carrinhos/incluir-aposta` | `@Body request: AddBetRequestDto` | `ApiEnvelope<CartDto>` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`addFavoriteBetToCart` | `POST logado/carrinhos/incluir-aposta-favorita` | `@Query("idApostaFavorita") favoriteBetId: Long, @Query("valorTipoConcurso") contestType: Int, @Query("qtdTeimosinhas") recurringDraws: Int, @Query("espelho") mirror: Boolean?` | `ApiEnvelope<CartDto>` |
| [BettingLoteriasApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingLoteriasApi.kt).`addComboToCart` | `POST logado/carrinhos/incluir-combo` | `@Body request: AddComboRequestDto` | `ApiEnvelope<CartDto>` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`addSurpriseBets` | `POST logado/carrinhos/incluir-surpresinha` | `@Body body: JsonElement` | `ApiEnvelope<CartDto>` |
| [BettingLoteriasApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingLoteriasApi.kt).`betCombos` | `GET logado/combos` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<List<BetComboDto>>` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`rapidConfiguration` | `GET logado/rapidoes` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<RapidConfigurationDto>` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`saveRapidConfiguration` | `POST logado/rapidoes` | `@Body body: RapidConfigurationDto` | `ApiEnvelope<RapidConfigurationDto>` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`generateRapidBets` | `POST logado/rapidoes/gerar-apostas` | `@Query("quantidade") quantity: Int` | `ApiEnvelope<CartDto>` |
| [BettingBackendApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingBackendApi.kt).`sportsTeams` | `GET publico/equipes-esportivas/listaEquipes` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<List<SportsTeamShieldDto>>` |
| [BettingLoteriasApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingLoteriasApi.kt).`lotteryModalities` | `GET publico/modalidades` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<List<LotteryModalityDto>>` |
| [BettingLoteriasApi](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/BettingLoteriasApi.kt).`bettingParameters` | `GET publico/parametros-simulacao` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<BettingParametersDto>` |

### Carrinho e carrinhos favoritos — Cart

`SessionCartRepositoryImpl` escolhe carrinho remoto para sessão autenticada e armazenamento local para visitante. Remover/limpar carrinho visitante não chama estas rotas. A revisão de apostas preservadas é local; não existe endpoint próprio da tela. Não confundir `manterSurpresinhas` deste DTO com `mantemSurpresinhas` no favorito de uma compra em Tickets.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`currentCart` | `GET logado/carrinhos` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<CartDto>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`favoriteCarts` | `GET logado/carrinhos-favoritos` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<List<FavoriteCartDto>>` |
| [CartBackendApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartBackendApi.kt).`validateFavoriteCartName` | `POST logado/carrinhos-favoritos/validar` | `@Body body: JsonElement` | `Unit` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`validateFavoriteCartName` | `POST logado/carrinhos-favoritos/validar` | `@Body request: SaveFavoriteCartRequestDto` | `Unit` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`deleteFavoriteCart` | `DELETE logado/carrinhos-favoritos/{cartId}` | `@Path("cartId") cartId: Long` | `ApiEnvelope<List<FavoriteCartDto>>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`favoriteCartBets` | `GET logado/carrinhos-favoritos/{cartId}/apostas` | `@Path("cartId") cartId: Long` | `ApiEnvelope<List<FavoriteCartBetDto>>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`deleteFavoriteCartBet` | `DELETE logado/carrinhos-favoritos/{cartId}/apostas/{betId}` | `@Path("cartId") cartId: Long, @Path("betId") betId: Long` | `ApiEnvelope<List<FavoriteCartBetDto>>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`removeCartBet` | `DELETE logado/carrinhos/apostas/{id}` | `@Path("id") id: Long` | `ApiEnvelope<CartDto>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`removeBoundCartBet` | `DELETE logado/carrinhos/apostas/{id}` | `@Path("id") id: Long, @Tag session: AccountVerificationSession` | `ApiEnvelope<CartDto>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`removeCartCombo` | `DELETE logado/carrinhos/combos/{id}` | `@Path("id") id: Long` | `ApiEnvelope<CartDto>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`addFavoriteCartToCurrent` | `POST logado/carrinhos/incluir-carrinho-favorito/{cartId}` | `@Path("cartId") cartId: Long, @Body request: AddFavoriteCartRequestDto` | `ApiEnvelope<CartDto>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`clearCart` | `POST logado/carrinhos/limpar` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<CartDto>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`saveCurrentCartFavorite` | `POST logado/carrinhos/salvar-carrinho-favorito` | `@Body request: SaveFavoriteCartRequestDto` | `Unit` |
| [CartBackendApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartBackendApi.kt).`validateCart` | `GET logado/carrinhos/validar` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [CartLoteriasApi](../feature/cart/data/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/datasource/remote/CartLoteriasApi.kt).`validateCart` | `GET logado/carrinhos/validar` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |

### Resultados — Results



| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [ResultsBackendApi](../feature/results/data/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/datasource/remote/ResultsBackendApi.kt).`resultsForContests` | `GET resultados` | `@Query("modalidade") modality: String, @Query("concursos") contests: List<Int>` | `ApiEnvelope<List<DrawResultDto>>` |
| [ResultsLoteriasApi](../feature/results/data/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/datasource/remote/ResultsLoteriasApi.kt).`latestResult` | `GET resultados/{modalidade}` | `@Path("modalidade") modality: String` | `ApiEnvelope<DrawResultDto>` |
| [ResultsLoteriasApi](../feature/results/data/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/datasource/remote/ResultsLoteriasApi.kt).`resultByContest` | `GET resultados/{modalidade}/{concurso}` | `@Path("modalidade") modality: String, @Path("concurso") contest: Int` | `ApiEnvelope<DrawResultDto>` |

### Bolões e seus filtros — Marketplace / Marketplace Filter

`MarketplaceRepositoryImpl` monta a paginação e os filtros. `quantidadeAposta` pertence à query de `boloes/recuperar-boloes-disponiveis`; não é parâmetro de inclusão no carrinho. Marketplace Filter reutiliza esse serviço, sem API separada.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [MarketplaceLoteriasApi](../feature/marketplace/data/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/datasource/remote/MarketplaceLoteriasApi.kt).`marketplacePoolDetail` | `GET boloes/detalhar-bolao` | `@Query("idBolao") poolCode: String` | `ApiEnvelope<MarketplacePoolDetailDto>` |
| [MarketplaceLoteriasApi](../feature/marketplace/data/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/datasource/remote/MarketplaceLoteriasApi.kt).`marketplacePools` | `GET boloes/recuperar-boloes-disponiveis` | `@QueryMap filters: Map<String, String>` | `ApiEnvelope<MarketplacePageDto>` |
| [MarketplaceLoteriasApi](../feature/marketplace/data/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/datasource/remote/MarketplaceLoteriasApi.kt).`addPoolShareToCart` | `POST logado/carrinhos/incluir-aposta` | `@Body request: AddPoolShareRequestDto` | `ApiEnvelope<CartDto>` |

### Pagamentos, Pix e cartões — Payment

`PaymentRemoteDataSourceImpl` usa backend CAIXA para Pix, cartões salvos e parâmetros; `CardPaymentTokenizer` usa as APIs externas para tokenização. Compra e acompanhamento usam `PurchaseBackendClient`, com `PurchaseEnvelopeDto` e tratamento de HTTP 303 como processamento. As declarações genéricas equivalentes de `BackendApi` não devem substituir esse cliente.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [PaymentRecargaPayApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentRecargaPayApi.kt).`createCard` | `PUT cards` | `@Header("Authorization") authorization: String, @Header("x-api-version") apiVersion: String = "20220405", @Body body: JsonElement` | `JsonElement` |
| [PaymentRecargaPayApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentRecargaPayApi.kt).`authorizeSavedCard` | `PATCH cards/{customerCardToken}` | `@Path("customerCardToken") customerCardToken: String, @Header("Authorization") authorization: String, @Header("x-api-version") apiVersion: String = "20220405", @Body body: JsonElement` | `JsonElement` |
| [PaymentBackendApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentBackendApi.kt).`generatePixCharge` | `POST logado/carrinhos/gerar-cobranca-pix` | `@Body body: JsonElement` | `ApiEnvelope<JsonElement>` |
| [PurchaseBackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/PurchaseBackendApi.kt).`registerCartBetsAsync` | `POST logado/carrinhos/registrar-apostas-async` | `@Body body: JsonElement` | `Response<PurchaseEnvelopeDto>` |
| [PurchaseBackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/PurchaseBackendApi.kt).`purchaseProcessing` | `GET logado/carrinhos/verifica-compra-processamento` | `@Query("idCompraCarrinho") purchaseId: Long` | `Response<PurchaseEnvelopeDto>` |
| [PaymentBackendApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentBackendApi.kt).`paymentMethods` | `GET meios-pagamento` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [PaymentBackendApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentBackendApi.kt).`paymentMethod` | `GET meios-pagamento/{id}` | `@Path("id") id: Long` | `ApiEnvelope<JsonElement>` |
| [PaymentBackendApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentBackendApi.kt).`validateDam` | `GET parametros/valida-represa` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [PaymentBackendApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentBackendApi.kt).`cards` | `GET usuarios/cartoes` | `@Query("meioPagamento") paymentMethod: String` | `ApiEnvelope<JsonElement>` |
| [PaymentBackendApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentBackendApi.kt).`deleteCard` | `DELETE usuarios/cartoes/{cardId}` | `@Path("cardId") cardId: String, @Query("meioPagamento") paymentMethod: String` | `ApiEnvelope<JsonElement>` |
| [PaymentBackendApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentBackendApi.kt).`recargaPayCustomerToken` | `GET usuarios/gerar-token-apostador-recarga-pay` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<String>` |
| [PaymentLoteriasApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentLoteriasApi.kt).`currentUserProfile` | `GET usuarios/recuperar-dados` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<UserProfileDto>` |
| [PaymentMercadoPagoApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentMercadoPagoApi.kt).`createCardToken` | `POST v1/card_tokens` | `@Query("public_key") publicKey: String, @Body body: JsonElement` | `JsonElement` |
| [PaymentMercadoPagoApi](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/PaymentMercadoPagoApi.kt).`installments` | `GET v1/payment_methods/installments` | `@Query("bin") bin: String, @Query("public_key") publicKey: String` | `JsonElement` |

### Lotéricas, resgates e PDFs — Retail

`PrizeRedemptionOperations` coordena consulta, resgate e cancelamento. Gerar código e cancelar resgate são operações com efeito, embora o contrato use GET. PDFs retornam bytes, não JSON. Os resultados de resgate passam pelas decisões de negócio do envelope especializado.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`retailerRedemptionCode` | `GET apostas/{id}/app/gerar-codigo-resgate-loterica` | `@Path("id") betId: Long` | `ApiEnvelope<JsonElement>` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`cancelPixRedemption` | `GET apostas/{id}/cancelar-resgate-pix` | `@Path("id") betId: Long` | `ApiEnvelope<JsonElement>` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`betReceiptPdf` | `GET apostas/{id}/comprovante-aposta-app-pdf` | `@Path("id") betId: Long` | `ResponseBody` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`prizeReceiptPdf` | `GET apostas/{id}/comprovante-premio-app-pdf` | `@Path("id") betId: Long` | `ResponseBody` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`consultPixKey` | `POST apostas/{id}/consulta-dados-chave` | `@Path("id") betId: Long, @Body body: JsonElement` | `ApiEnvelope<JsonElement>` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`prizeDetails` | `GET apostas/{id}/detalhes-premio` | `@Path("id") betId: Long` | `ApiEnvelope<JsonElement>` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`redeemPrize` | `POST apostas/{id}/resgatar-premio` | `@Path("id") betId: Long, @Body body: JsonElement` | `PrizeRedemptionEnvelopeDto` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`favoriteRetailers` | `GET boloes/consultar-loterica-favorita` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`deleteFavoriteRetailer` | `DELETE boloes/excluir-loterica-favorita/{id}` | `@Path("id") retailerId: Long` | `ApiEnvelope<JsonElement>` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`addFavoriteRetailer` | `POST boloes/incluir-loterica-favorita` | `@Query("nuCd") retailerId: Long` | `ApiEnvelope<JsonElement>` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`retailersByNameOrCode` | `GET lotericas/nome-loterico` | `@Query("nomeCodigo") nameOrCode: String` | `ApiEnvelope<JsonElement>` |
| [RetailBackendApi](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/datasource/remote/RetailBackendApi.kt).`mercadoPagoRegistrationToken` | `GET usuarios/gerar-token-cadastro-mercado-pago` | `@Query("email") email: String` | `MercadoPagoRegistrationEnvelopeDto` |

### Central de notificações e push — Notifications / Account

`NotificationInboxActions.load(identity)` chama `all` e `unread`; alterações usam identidade capturada. O contador e a leitura da última notificação usam `NotificationReadApi`. As variantes de `unread` atendem consumidores distintos. `GetNotificationsUseCase` e preferências locais não consultam esta central. Push envia identificadores de interesse e token FCM; não baixa mensagens por esse POST.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [NotificationInboxApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationInboxApi.kt).`all` | `GET historico-notificacoes` | `@Tag session: AccountVerificationSession` | `Response<NotificationInboxEnvelopeDto>` |
| [NotificationInboxApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationInboxApi.kt).`clear` | `PUT historico-notificacoes/limpa` | `@Tag session: AccountVerificationSession` | `Response<ResponseBody>` |
| [NotificationInboxApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationInboxApi.kt).`unread` | `GET historico-notificacoes/nao-visualizadas` | `@Tag session: AccountVerificationSession` | `Response<NotificationInboxEnvelopeDto>` |
| [NotificationReadApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationReadApi.kt).`unread` | `GET historico-notificacoes/nao-visualizadas` | `@Tag session: AccountVerificationSession` | `Response<UnreadNotificationsEnvelopeDto>` |
| [NotificationInboxApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationInboxApi.kt).`remove` | `PUT historico-notificacoes/{id}/remove` | `@Path("id") id: Long, @Tag session: AccountVerificationSession` | `Response<ResponseBody>` |
| [NotificationInboxApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationInboxApi.kt).`markRead` | `PUT historico-notificacoes/{id}/visualiza` | `@Path("id") id: Long, @Tag session: AccountVerificationSession` | `Response<ResponseBody>` |
| [NotificationReadApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationReadApi.kt).`markRead` | `PUT historico-notificacoes/{notificationId}/visualiza` | `@Path("notificationId") notificationId: Long, @Tag session: AccountVerificationSession` | `Response<NotificationReadEnvelopeDto>` |
| [NotificationReadApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationReadApi.kt).`registerPushDevice` | `POST push/registros` | `@Body body: PushRegistrationRequestDto, @Tag session: AccountVerificationSession` | `PushRegistrationEnvelopeDto` |

### Jogo responsável — Responsible Gaming



| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [ResponsibleGamingLoteriasApi](../feature/responsible-gaming/data/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/datasource/remote/ResponsibleGamingLoteriasApi.kt).`submitSelfAssessment` | `POST autoavaliacoes/grava-resposta` | `@Body answers: List<SelfAssessmentAnswerDto>` | `ApiEnvelope<SelfAssessmentPayloadDto>` |
| [ResponsibleGamingLoteriasApi](../feature/responsible-gaming/data/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/datasource/remote/ResponsibleGamingLoteriasApi.kt).`selfAssessmentQuestions` | `GET autoavaliacoes/recuperar-perguntas` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<SelfAssessmentPayloadDto>` |
| [ResponsibleGamingLoteriasApi](../feature/responsible-gaming/data/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/datasource/remote/ResponsibleGamingLoteriasApi.kt).`socialTransfers` | `GET repassometro` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<SocialTransfersDto>` |
| [ResponsibleGamingLoteriasApi](../feature/responsible-gaming/data/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/datasource/remote/ResponsibleGamingLoteriasApi.kt).`currentUserProfile` | `GET usuarios/recuperar-dados` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<UserProfileDto>` |
| [ResponsibleGamingLoteriasApi](../feature/responsible-gaming/data/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/datasource/remote/ResponsibleGamingLoteriasApi.kt).`suspendAccount` | `POST usuarios/suspender-conta-apostador` | `@Body request: SuspendAccountRequestDto` | `ApiEnvelope<AccountSuspensionDto>` |

### Ajuda e termos — Support



| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [SupportLoteriasApi](../feature/support/data/src/main/kotlin/br/gov/caixa/loterias/feature/support/data/datasource/remote/SupportLoteriasApi.kt).`supportQuestions` | `GET duvidas` | `@Query("secao") sectionId: Long` | `ApiEnvelope<List<SupportSectionDto>>` |
| [SupportLoteriasApi](../feature/support/data/src/main/kotlin/br/gov/caixa/loterias/feature/support/data/datasource/remote/SupportLoteriasApi.kt).`supportSections` | `GET duvidas/secoes` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<List<SupportSectionDto>>` |
| [SupportLoteriasApi](../feature/support/data/src/main/kotlin/br/gov/caixa/loterias/feature/support/data/datasource/remote/SupportLoteriasApi.kt).`acceptCurrentTerms` | `POST termos-de-uso/aceitar-termo-vigente` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<Unit>` |
| [SupportLoteriasApi](../feature/support/data/src/main/kotlin/br/gov/caixa/loterias/feature/support/data/datasource/remote/SupportLoteriasApi.kt).`currentTerms` | `GET termos-de-uso/termo-vigente` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<TermsDto>` |

### Configuração e versão — Core / Home / Onboarding

Os parâmetros alimentam configuração da Home, campanhas e roteamento. O resultado de versão é um booleano no envelope. Firebase Remote Config e preferências locais são integrações separadas, descritas adiante.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [LoteriasApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/LoteriasApi.kt).`isVersionSupported` | `GET app/valida-versao/{version}` | `@Path("version") version: String` | `ApiEnvelope<Boolean>` |
| [LoteriasApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/LoteriasApi.kt).`appParameters` | `GET parametros/configuraveis-app` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<AppParametersMapDto>` |

### Declarações de compatibilidade sem consumidor funcional identificado

Estas rotas permanecem declaradas em `BackendApi` para compatibilidade com o inventário legado. Não foi identificado consumidor de produção fora da declaração para estes métodos. Sua presença não significa que uma funcionalidade atual os execute; por isso não recebem um corpo de requisição presumido. `historico-apostas` aqui não é o mesmo que `historico-apostas/{id}/comprovante-premio`, que é usado por Tickets.

| Serviço / método | HTTP e endpoint relativo | Parâmetros declarados | Retorno do transporte |
| --- | --- | --- | --- |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`saveFavoriteBetOnSilce` | `POST apostas-favoritas/` | `@Body body: JsonElement` | `ApiEnvelope<JsonElement>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`addMultipleBetsToCart` | `POST carrinhos/incluir-apostas` | `@Body body: JsonElement` | `ApiEnvelope<CartDto>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`silceCombos` | `GET combos` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`betHistory` | `GET historico-apostas` | `@QueryMap filters: Map<String, String>` | `ApiEnvelope<JsonElement>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`favoriteBets` | `GET logado/apostas-favoritas/lista-unica` | `@QueryMap filters: Map<String, String>` | `ApiEnvelope<FavoriteBetPageDto>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`addFavoriteBetToCartLegacy` | `POST logado/carrinhos/incluir-aposta-favorita/{favoriteBetId}/{contestType}/{recurringDraws}` | `@Path("favoriteBetId") favoriteBetId: Long, @Path("contestType") contestType: Int, @Path("recurringDraws") recurringDraws: Int` | `ApiEnvelope<CartDto>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`retailers` | `GET lotericas` | `@QueryMap filters: Map<String, String>` | `ApiEnvelope<JsonElement>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`enabledRetailerSearches` | `GET lotericas/buscas-habilitadas` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`retailerByFullCode` | `GET lotericas/codigo-completo` | `@Query("codigoCompleto") fullCode: String` | `ApiEnvelope<JsonElement>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`retailersByFantasyName` | `GET lotericas/nome-fantasia` | `@Query("nomeFantasia") fantasyName: String` | `ApiEnvelope<JsonElement>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`validateAcceptedTerms` | `GET termos-de-uso/valida-termo-aceito` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |
| [BackendApi](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/BackendApi.kt).`federativeUnits` | `GET ufs` | `Sem parâmetros de rota/query/body.` | `ApiEnvelope<JsonElement>` |

### Filtros enviados por QueryMap

| Funcionalidade | Campos enviados e regras |
| --- | --- |
| Histórico de apostas | `mes`, `ano` quando há ano positivo; `offset` começa em `0`; `size` é `32` para mês especial `45`, senão `30`. Situação selecionada `1/2/3` vira `situacao=1/4/6`. `ordenacao`, `tipoAposta`, `tipoConcurso`, `modalidade` só quando positivos. `surpresinha`, `teimosinha`, `combo` recebem string `"3"` quando ativos. Paginação ajusta o deslocamento. |
| Histórico dinâmico | Copia os filtros anteriores, renomeando `combo` para `comboApostas`. A política decide a base e se usa `apostas` ou `consultar`; recuperação `{id}/recuperar` é específica da Nova API. |
| Compras | `mes`, `ano` quando há período; `offset`, `size=10`; `meio` se houver meio de pagamento; `situacao` se selecionada. Filtros de itens da compra são aplicados à lista em memória e não acrescentam parâmetros a `compras/{id}/apostas`. |
| Apostas favoritas por modalidade | `modalidade`, `offset`, `size="8"`. |
| Bolões disponíveis | `tipoConsulta`, `idMunicipio`, `idUf`, `pagina=max(page,0)+1`, `qtdPorPagina="30"`. O domínio usa página iniciada em zero e o serviço recebe página iniciada em um. Filtros opcionais: `quantidadeAposta`, `quantidadeDezena`, `qtdMinimaCota`, `qtdMaximaCota`, `valorMinimoCota`, `valorMaximoCota`, `numerosEscolhidos`, `numerosNaoEscolhidos`, `numeroLoterico`, `idModalidade`, `tipoConcurso`. Listas de números são unidas por `;`; valores ausentes/vazios são omitidos. |

Fontes: [TicketsUiState](../feature/tickets/presentation/src/main/java/br/gov/caixa/loterias/feature/tickets/presentation/state/TicketsUiState.kt), [TicketsRemoteDataSource](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/datasource/remote/TicketsRemoteDataSource.kt), [MarketplaceRepositoryImpl](../feature/marketplace/data/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/repository/MarketplaceRepositoryImpl.kt), [MarketplaceFilterRules](../feature/marketplace/api/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/rule/MarketplaceFilterRules.kt), [FavoriteBetRemoteDataSourceImpl](../feature/betting/data/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/datasource/remote/FavoriteBetRemoteDataSourceImpl.kt).

### Corpos e retornos dinâmicos

Esta seção descreve o que o cliente efetivamente envia ou consome onde a assinatura usa `JsonElement`. Campos adicionais que o servidor possa retornar não constituem contrato de domínio até serem tratados pelos mapeadores.

| Fluxo | Corpo JSON enviado | Dados de retorno consumidos |
| --- | --- | --- |
| Filtros de histórico e compras | Sem body; os filtros de requisição estão na tabela de QueryMap. | `apostas/config-consulta`: `ordenacaoPadrao.valor`, `situacaoPadrao.valor`, `periodoPadrao`, `situacoes`, `ordenacoes`, `periodos`, `listaTipoAposta`, `listaTipoConcurso`; opções usam `valor/descricao`, período usa `mes: {valor, descricao}` e `ano`. `compras/meses` usa `mes` e `ano`; situações/meios retornam opções com identificador e descrição. |
| Consultar lotéricas/favoritas | Busca envia query `nomeCodigo`; inclusão favorita envia `nuCd`; exclusão envia `id` na rota. | `id` ou `codigo`, `numeroFormatado`, `nomeFantasia`, `nome`, `logradouro`, `bairro`, `nomeMunicipio`, `nomeUF` ou `siglaUf` → `Retailer`; a origem da consulta indica se é favorita. |
| Conferir bilhete manual/QR | `{codigo: código preparado/codificado pelo fluxo}`; a rota depende do modo de conferência. | Envelope aberto mapeado para `CheckedTicketResult`: apostas, `situacao`, `modalidade`, `premio.valorLiquido` ou `valorPremio`, concursos premiados/não premiados, faixas com `nome`, `quantidadeAcertos`, `sorteio`, `valorPremioCota` ou `valorLiquido`. |
| Favoritar uma compra | `{nome, mantemSurpresinhas}`. | Envelope aberto; o caso de uso confirma a operação e trata erro de serviço. |
| Validar/salvar aposta favorita | `modalidade: {valor, descricao}`, `nome` limitado a 25 caracteres, `numerosSelecionados` como array ou matriz conforme as colunas; opcionais `trevos: {trevosSelecionados}`, `timeDoCoracao` e `mesDeSorte`. | `Unit` no contrato da feature: não fornece uma aposta salva como retorno. |
| Time e mês da aposta favorita | `timeDoCoracao`: `indicadorSelecao`, `nome`, `numero`, `numeroPais`, `descricaoCurta`, `descricaoLonga`, `pais`, `siglaPais`, `uf`, `nomeClass`; `mesDeSorte`: `numero`, `nome`, `abreviacao`. | Campos enviados somente quando a seleção existir. |
| Surpresinhas | Serialização de `SurpriseBetRequestDto`; campos no dicionário abaixo. | `ApiEnvelope<CartDto>`: carrinho atualizado. |
| Gerar cobrança Pix | `meioPagamento=PaymentMethod.PIX_ID`, `parametrosPagamento=null`, `salvarCarrinhoFavorito=false`, `nomeCarrinhoFavorito=""`. | `pixCopiaECola`, `txId`, `qrcodeArrByte`, `dataHoraCriacao`, `dataHoraExpiracao`, `idCompra`, `valorCobranca` → `PixCharge`. |
| Efetivar compra com cartão | `meioPagamento`; `parametrosPagamento: {TOKEN, SAVED_CARD, PAYMENT_METHOD_ID, SAVE_CARD}`. `SAVED_CARD` é string `"true"` para cartão salvo, senão null; `SAVE_CARD` é booleano convertido para string quando fornecido, senão null. | `PurchaseEnvelopeDto` → decisão de compra/processamento; acompanhamento envia `idCompraCarrinho` na query. Tokenizar cartão não conclui a compra. |
| Mercado Pago: tokenizar | Query `public_key`; cartão novo: `card_number`, `expiration_year`, `expiration_month`; salvo: `card_id`, ano/mês. Ambos: `security_code`, `cardholder.identification: {type:"CPF", number}`; `cardholder.name` somente no novo. | `id` do token e `luhn_validation`; consulta de installments fornece `payment_method_id`. O BIN é obtido do cartão (primeiros 7 dígitos para novo). |
| RecargaPay: criar cartão | `type:"CREDIT"`, `brand`, `number`, `cardHolder: {type:"NATURAL", name, email, document, birthdate, phones}`, `expirationDate: {month, year}`, `securityCode`, `shouldSave`. | `customerCardToken`, `brand`. O token de cliente vem de `usuarios/gerar-token-apostador-recarga-pay`. |
| RecargaPay: autorizar salvo | Path `customerCardToken`; corpo `{securityCode}`. Headers declarados: `Authorization: Bearer <token do cliente>`, `x-api-version: 20220405` (também na criação). | `customerCardToken`, `brand` para a compra CAIXA. |
| Meios de pagamento | Sem body; detalhe envia `id` na rota. | `id`, `nomePagamentoCompra`, `nomePagamentoPremio`, `padrao`, `publicKey` → `PaymentMethod`. |
| Cartões salvos | Query `meioPagamento`; exclusão acrescenta `cardId` na rota. | `idCartao`, `primeirosDigitos`, `ultimosDigitos`, `mesValidade`, `anoValidade`, `tamanhoCodigoSeguranca`, `nomeMetodoPagamento`, `idMetodoPagamento`, `tokenCartao` → `SavedPaymentCard`. |
| Consulta e resgate Pix de prêmio | Path `id`; `{meioPagamento: PrizeRedemptionChannel.PIX.legacyId, chavePix}`. | Consulta: `cpfMascarado`, `nome`, `chave`, `nomeBanco` → `PixBeneficiary`; resgate: `PrizeRedemptionEnvelopeDto` → decisão de negócio. |
| Resgate Mercado Pago | Path `id`; `{meioPagamento: PrizeRedemptionChannel.MERCADO_PAGO.legacyId, parametrosPagamento: {EMAIL: email}}`. | `PrizeRedemptionEnvelopeDto`, com resultado/mensagem/decisão de serviço. Token de cadastro usa query `email`. |
| Gerar código de resgate em lotérica | Path `id`; sem body. | `qrCode`, `codigo`, `nsb`, `validade` → `LotteryRedemptionCode`. |
| Detalhar prêmio | Path `id`; sem body. | `premio.valorLiquido`, `premio.concursos`/`concursosNaoPremiado`, `aposta`, `nsbi`, `meiosResgate`, `mensagemInformativaIR` → `PrizeDetails`; faixas fornecem concurso, acertos, valores e situação. |
| Apostas confirmadas e comprovantes JSON | Filtros de histórico ou `id`, conforme a rota. | `id`, modalidade, concurso, situação, números/colunas, valor de prêmio, datas, flags de bolão/surpresinha/teimosinha/espelho/troca/combo; os mapeadores aceitam variantes como `numerosSelecionados`/`dezenas` e `numeroConcurso`/`concursoInicial`/`concursoAlvo` → `ConfirmedBet`. |
| Compras | Query de compras; detalhe de itens envia `id` na rota. | `id`, `dataInicioCompra`/`dataFinalizacaoCompra`, `situacao`, `meioPagamento`, `valorTotal`, `pixCopiaECola`, `dataHoraExpiracaoPagamento`, `horaFinalizacaoCompra`, `e2eId` → `Purchase`. |

Fontes de montagem e interpretação: [PaymentPayloadMapper](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/mapper/PaymentPayloadMapper.kt), [CardPurchaseRequestMapper](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/mapper/CardPurchaseRequestMapper.kt), [CardPaymentTokenizer](../feature/payment/data/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/datasource/remote/CardPaymentTokenizer.kt), [PrizeRedemptionPayloadMapper](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/mapper/PrizeRedemptionPayloadMapper.kt), [TicketConfirmedBetMapper](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/mapper/TicketConfirmedBetMapper.kt), [TicketPurchaseMapper](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/mapper/TicketPurchaseMapper.kt), [TicketCheckedResultMapper](../feature/tickets/data/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/data/mapper/TicketCheckedResultMapper.kt), [RetailPayloadMapper](../feature/retail/data/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/mapper/RetailPayloadMapper.kt). As respostas abertas de filtros, conferência, lotéricas e validação seguem esses mapeadores e os DataSources vinculados às tabelas; não se deve presumir um DTO estrito a partir de `JsonElement`.

### Autenticação SSO, Firebase e funcionalidades sem REST próprio

| Funcionalidade / serviço | Endereço ou mecanismo | Parâmetros / entrada | Retorno e uso |
| --- | --- | --- | --- |
| Autorizar login — AppAuth | GET no `ssoAuthUrl` do ambiente; navegador/SSO. | `client_id`, `redirect_uri`, `response_type=code`, `state`, `nonce`, PKCE (`code_challenge`, `code_challenge_method`), `scope` quando configurado; adicionais `deviceid`, `nivel`, `app`, `origem`. Reautenticação silenciosa usa `prompt=none`; troca de usuário usa `prompt=login`. | Callback com código ou erro. Não fornece sozinho cadastro/carrinho; depois há troca de código e verificações pós-login. |
| Trocar código / renovar sessão — AppAuth | POST no `ssoTokenUrl`; formulário OAuth gerado pelo SDK. | Código: `grant_type=authorization_code`, `code`, `redirect_uri`, `client_id`, `code_verifier`; renovação: `grant_type=refresh_token`, `refresh_token`, identificação do cliente conforme configuração. | `access_token`, `token_type`, expiração e, conforme resposta/protocolo, `refresh_token`, `id_token`, `scope`. O fluxo valida a identidade antes de persistir. |
| Encerrar sessão SSO — AppAuth | Endpoint `ssoLogoutUrl` do ambiente. | `id_token_hint` quando disponível, `post_logout_redirect_uri`, `state`, `client_id` conforme montagem do pedido. | Callback de encerramento; coordenado com `usuarios/sair` e limpeza local. |
| Ícone por campanha / atualização / pins — Firebase Remote Config | SDK Firebase: fetch/activate e listener de atualização; não há endpoint Retrofit próprio. | Chaves como `app_launcher_icon`, `force_update`, `tls_pinning_keyset`. Calendário anual inclui `default_icon`, `timezone`, `campaigns[]` com `icon/start/end`. | Configuração ativada; calendário decide ícone padrão/campanha; conjunto de pins assinado passa por validação antes de reconfigurar clientes. Recuperação periódica ocorre com app em primeiro plano. |
| Push — Firebase Messaging | SDK FCM obtém/renova token; cadastro no backend está em `push/registros`. | Body `PushRegistrationRequestDto`: `identificadores`, `token`, `plataforma="android"`, `canal="loterias"`. | `PushRegistrationEnvelopeDto`; mensagens chegam pelo serviço Firebase do app. |
| Fila de acesso | SDK de fila configurado pelo host. | Conta/sistema e callbacks do fluxo de fila. | Liberação/erro/status do SDK; não há contrato Retrofit de fila nas tabelas. |
| Home e Aposta Shake | Configuração comum + preferências/sensores locais; carrinho e novas apostas usam Betting/Cart. | Habilitação/preferências e evento do sensor; sem endpoint exclusivo chamado “ApostaShake”. | Estado da funcionalidade e execução do fluxo de aposta correspondente. |
| Marketplace Filter | Estado/regras locais; consulta final usa Marketplace. | Filtros da tabela de QueryMap. | Filtro validado; retorno remoto é `MarketplacePageDto`. |
| Tutorial, biometria e preferências | Armazenamento local e SDK Android; sem REST próprio para os toggles. | Preferências, aceite local do tutorial, credencial protegida e autorização biométrica. | Estado local; autenticar ainda depende do fluxo SSO/backend. |
| Notificações locais | `NotificationRepositoryImpl`, `LocalNotificationMessages`, `LocalNotificationPreferences`. | Leitura, marcação, exclusão e preferências dos casos de uso locais. | `AppNotification`/`NotificationPreference`; não sincroniza a central remota. |
| Carrinho visitante/preservado | Armazenamento local do carrinho; `SessionCartRepositoryImpl`. | Apostas locais e revisão após login. | `ShoppingCart` local; integração pós-login usa os contratos de verificação/validação autenticados. |

Fontes de autenticação: [AuthorizationRequestPolicy](../core/auth/src/main/java/br/gov/caixa/loterias/core/auth/authorization/AuthorizationRequestPolicy.kt), [AppAuthBrowserAuthorization](../core/auth/src/main/java/br/gov/caixa/loterias/core/auth/appauth/AppAuthBrowserAuthorization.kt), [ProcessEnvironmentSettings](../app/src/main/java/br/gov/caixa/loterias/apostas/environment/runtime/ProcessEnvironmentSettings.kt). Os hosts efetivos dependem do ambiente; não copiar URLs fixas de outro ambiente para realizar estas chamadas.

### Dicionário dos corpos e retornos tipados

Nomes à esquerda são os nomes serializados (`@SerializedName` quando existe); à direita, os tipos do cliente Kotlin. `?` indica que o cliente admite ausência/null, não uma promessa de que o servidor sempre o envie. Defaults e formatos específicos estão na declaração vinculada. Tipos aninhados constam em linhas próprias. Este dicionário não contém valores de usuários, credenciais ou dados de cartões.

| DTO / declaração fonte | Campos JSON e tipos |
| --- | --- |
| [AccountNotificationDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `id: Long?`; `titulo: String?`; `conteudo: String?`; `imagem: String?`; `link: String?` |
| [AccountRegistrationDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `cep: String`; `aceitaTermosUso: Boolean`; `aceitaReceberNoticias: Boolean`; `municipioId: MunicipalityIdDto`; `sexo: String`; `apostas: List<Any>` |
| [AccountSessionDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `nome: String?`; `email: String?`; `primeiroNome: String?`; `limiteDiario: BigDecimal?`; `suspensaoTemporariaApostador: Boolean?`; `responderAutoavaliacao: Boolean?`; `situacaoApostador: AccountStatusDto?`; `carrinho: CartDto?`; `ultimaNotificacao: AccountNotificationDto?`; `pagamentoNaoIdentificado: Boolean?` |
| [AccountStatusDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `enum: String?` |
| [AccountSuspensionDto](../feature/responsible-gaming/transport/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/dto/AccountSuspensionDtos.kt) | `apostadorSuspenso: Boolean?`; `prazoHorasDias: String?` |
| [AccountVerificationEnvelopeDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountVerificationEnvelopeDto.kt) | `codigo: String?`; `mensagem: String?`; `tipo: Int?`; `redirect: String?`; `errosValidacao: List<String>?`; `payload: AccountSessionDto?` |
| [AccountVerificationRequestDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `apostas: List<CartUploadDto>` |
| [AccountVerificationSession](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/AccountVerificationBackend.kt) | `boundToken: () -> String?`; `current: () -> Boolean` |
| [AddBetRequestDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `modalidade: String`; `tipoConcurso: DomainCharacterDto`; `numerosSelecionados: JsonElement`; `numerosSelecionadosMaisMilionariaTrevo: List<Int>`; `indicadorSurpresinha: DomainIntegerDto`; `quantidadeTeimosinhas: Int`; `quantidadeApostas: Int`; `quantidadeNumeros: Int`; `quantidadeNumerosMaisMilionariaTrevo: Int`; `surpresinha: Boolean`; `timeDoCoracao: SportsBetTeamDto?`; `partidasLoteca: List<LotecaBetMatchDto>?`; `partidasLotogol: List<LotogolBetMatchDto>?`; `mesDeSorte: LuckyMonthParameterDto?`; `geraEspelho: Boolean?`; `concursoAlvo: Int?`; `valor: BigDecimal?` |
| [AddComboRequestDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `id: Int`; `qtdApostas: Int`; `isSurpresinha: Boolean`; `limparCarrinho: Boolean` |
| [AddFavoriteCartRequestDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `incluirEspecial: Int` |
| [AddPoolShareRequestDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `modalidade: String`; `tipoConcurso: DomainCharacterDto`; `valor: BigDecimal`; `concursoAlvo: Int`; `dataSorteio: String`; `indicadorCotaBolao: Boolean`; `subcanalCarrinhoAposta: Int`; `idBolao: String`; `qtdCotasBolao: Int`; `valorTarifaServico: BigDecimal` |
| [ApiEnvelope](../core/network/src/main/java/br/gov/caixa/loterias/core/network/model/ApiModels.kt) | `payload: T?`; `redirect: String?`; `mensagem: String?`; `dataHoraServidor: String?` |
| [AppParametersDto](../core/network/src/main/java/br/gov/caixa/loterias/core/network/model/ConfigurationApiModels.kt) | `qtdeMinimaApostaBolaoFiltro: Int?`; `qtdeMaximaApostaBolaoFiltro: Int?`; `habilitarConferenciaAutomaticaApostas: Boolean?`; `hostMicroServicoConferenciaAposta: String?`; `hostNovoMicroServicoConferenciaApostaProducao: String?`; `hostNovoMicroServicoConferenciaApostaPiloto: String?`; `grupoCpfsAcessamNovoConferenciaAposta: Int?`; `hostMicroServicoCarrinhoPiloto: String?`; `hostMicroServicoCarrinhoProducao: String?`; `hostMicroServicoApostadorProducao: String?`; `habilitaRevendaBolaoApp: Boolean?`; `comboApostasHabilitado: Boolean?`; `rapidaoHabilitado: Boolean?`; `consultaMinhasApostas: Boolean?`; `vigenciaMesOutubroRosa: Boolean?`; `vigenciaOutubroRosaMegaSena: Boolean?`; `campanhasComemorativas: List<CommemorativeCampaignDto?>?`; `nuvemApikey: String?`; `concursoMega30Anos: Int?`; `concursoLotecaPais: Int?` |
| [AppParametersMapDto](../core/network/src/main/java/br/gov/caixa/loterias/core/network/model/ConfigurationApiModels.kt) | `mapa: AppParametersDto?` |
| [AvailablePoolModalityDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `modalidade: String?`; `modalidadeDetalhada: LotteryModalityDto?`; `tipoConcurso: String?`; `concurso: Int?`; `dataEncerramentoRevenda: String?`; `horaEncerramentoRevenda: String?`; `estimativa: BigDecimal?`; `dataHoraSorteio: String?` |
| [BetComboDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `tipoCombo: BetComboTypeDto?`; `modalidadesCombo: List<BetComboEntryDto>?`; `valor: BigDecimal?` |
| [BetComboEntryDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `modalidade: LotteryModalityDto?`; `tipoConcurso: String?`; `quantidadeApostas: Int?`; `quantidadeTeimosinhas: Int?`; `quantidadeDezenas: Int?`; `quantidadeDezenasTrevos: Int?`; `numero: Int?` |
| [BetComboTypeDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `codigo: Int?`; `nome: String?`; `descricao: String?`; `novo: Boolean?` |
| [BettingParametersDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `valorLimiteDiario: BigDecimal?`; `valorMinimoCarrinho: BigDecimal?`; `quantidadeMaximaApostasCarrinho: Int?`; `parametros: List<LotteryParameterDto>?`; `modalidadesDisponiveisCotas: List<AvailablePoolModalityDto>?` |
| [BlockedAccountVerificationEnvelopeDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `codigo: String?`; `redirect: String?` |
| [CartBetDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `id: Long?`; `modalidade: JsonElement?`; `tipoConcurso: JsonElement?`; `numerosSelecionados: JsonElement?`; `numerosSelecionadosMaisMilionariaTrevo: JsonElement?`; `valor: BigDecimal?`; `quantidadeApostas: Int?`; `quantidadeNumeros: Int?`; `quantidadeNumerosMaisMilionariaTrevo: Int?`; `quantidadeTeimosinhas: Int?`; `concursoAlvo: Int?`; `concursoInicial: Int?`; `indicadorSurpresinha: JsonElement?`; `surpresinha: Boolean?`; `espelho: Boolean?`; `geraEspelho: Boolean?`; `troca: Boolean?`; `combo: Boolean?`; `mesDeSorte: JsonElement?`; `timeDoCoracao: JsonElement?`; `partidasLoteca: JsonElement?`; `partidasLotogol: JsonElement?`; `reservaCotaBolao: CartPoolReservationDto?` |
| [CartCodeDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartUploadDtos.kt) | `valor: T`; `descricao: String?` |
| [CartComboDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `id: Long?`; `tipoCombo: CartComboTypeDto?`; `apostas: List<CartBetDto>?` |
| [CartComboTypeDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `nome: String?`; `descricao: String?` |
| [CartDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `id: Long?`; `valorTotal: BigDecimal?`; `apostasIndividuais: List<CartBetDto>?`; `cotas: List<CartBetDto>?`; `combos: List<CartComboDto>?`; `msgCotaExcluida: Boolean?`; `apostas: List<CartBetDto>?` |
| [CartLotecaDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartUploadDtos.kt) | `equipe1: CartTeamDto`; `equipe2: CartTeamDto`; `empate: Boolean` |
| [CartLotogolDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartUploadDtos.kt) | `equipe1: CartTeamDto`; `equipe2: CartTeamDto`; `indexPlacarEquipe1: Int`; `indexPlacarEquipe2: Int` |
| [CartMonthDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartUploadDtos.kt) | `numero: Int?`; `nome: String?`; `abreviacao: String?` |
| [CartPoolReservationDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `codigoBolaoReserva: String?`; `numeroCotaReservada: Int?`; `vrTotalCota: BigDecimal?`; `qtdCotaTotalBolao: Int?`; `dataHoraExpiracaoReserva: String?` |
| [CartTeamDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartUploadDtos.kt) | `numero: Int?`; `nome: String?`; `numeroPais: Int?`; `pais: String?`; `siglaPais: String?`; `uf: String?`; `nomeClass: String?`; `indicadorSelecao: Boolean?`; `descricaoCurta: String?`; `descricaoLonga: String?`; `parametroEquipe: CartTeamDto?`; `placar: String?`; `vitoria: Boolean?` |
| [CartUploadDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartUploadDtos.kt) | `id: Long?`; `modalidade: String`; `tipoConcurso: CartCodeDto<String?>`; `numerosSelecionados: JsonArray?`; `numerosSelecionadosMaisMilionariaTrevo: List<Int>?`; `indicadorSurpresinha: CartCodeDto<Int>`; `valor: BigDecimal`; `concursoAlvo: Int`; `quantidadeTeimosinhas: Int`; `quantidadeApostas: Int`; `espelho: Boolean`; `geraEspelho: Boolean`; `concursoInicial: Int`; `situacao: CartCodeDto<Long>`; `troca: Boolean`; `dataEfetivacao: String?`; `horaEfetivacao: String?`; `vinculoEspelho: Long`; `quantidadeNumeros: Int`; `quantidadeNumerosMaisMilionariaTrevo: Int`; `surpresinha: Boolean`; `timeDoCoracao: CartTeamDto`; `mesDeSorte: CartMonthDto`; `partidasLoteca: List<CartLotecaDto>`; `partidasLotogol: List<CartLotogolDto>`; `indicadorCotaBolao: Boolean` |
| [CommemorativeCampaignDto](../core/network/src/main/java/br/gov/caixa/loterias/core/network/model/ConfigurationApiModels.kt) | `codigo: String?`; `ativa: Boolean?`; `areas: List<String?>?`; `inicio: String?`; `fim: String?`; `prioridade: Int?` |
| [DomainCharacterDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `valor: String`; `descricao: String` |
| [DomainIntegerDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `valor: Int`; `descricao: String` |
| [DrawContestDto](../feature/results/transport/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/dto/ResultsDtos.kt) | `modalidade: String?`; `numero: Int?`; `dataSorteio: String?`; `tipoConcurso: String?`; `estimativa: String?`; `modalidadeDetalhada: LotteryModalityDto?` |
| [DrawPayoutDto](../feature/results/transport/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/dto/ResultsDtos.kt) | `descricao: String?`; `quantidadeGanhadores: Long?`; `valor: String?` |
| [DrawRegionalWinnerDto](../feature/results/transport/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/dto/ResultsDtos.kt) | `municipioSorteio: MarketplaceCityDto?`; `ufSorteio: MarketplaceStateDto?`; `descricao: String?` |
| [DrawResultDto](../feature/results/transport/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/dto/ResultsDtos.kt) | `municipioSorteio: MarketplaceCityDto?`; `ufSorteio: MarketplaceStateDto?`; `aviso: String?`; `arrecadacaoTotal: String?`; `acumuladoParaProximoConcurso: String?`; `acumulou: Boolean?`; `numerosSorteadosPrimeiroSorteio: JsonElement?`; `numerosSorteadosSegundoSorteio: List<Int>?`; `trevosSorteadosPrimeiroSorteio: List<Int>?`; `concurso: DrawContestDto?`; `premiacoesPrimeiroSorteio: List<DrawPayoutDto>?`; `premiacoesSegundoSorteio: List<DrawPayoutDto>?`; `partidasLoteca: List<DrawSportsMatchDto>?`; `partidasLotogol: List<DrawSportsMatchDto>?`; `premiacaoTimeDoCoracao: DrawSelectionRewardDto?`; `premiacaoMesDeSorte: DrawSelectionRewardDto?`; `ganhadoresPrimeiroSorteio: List<DrawRegionalWinnerDto>?`; `ganhadoresSegundoSorteio: List<DrawRegionalWinnerDto>?`; `proximoConcurso: DrawContestDto?`; `proximoConcursoFinalZero: DrawContestDto?`; `proximoConcursoFinalCinco: DrawContestDto?`; `proximoConcursoEspecial: DrawContestDto?` |
| [DrawSelectionRewardDto](../feature/results/transport/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/dto/ResultsDtos.kt) | `equipe: SportsTeamParameterDto?`; `mesDeSorte: LuckyMonthParameterDto?`; `quantidadeGanhadores: Long?`; `valor: String?` |
| [DrawSportsMatchDto](../feature/results/transport/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/dto/ResultsDtos.kt) | `numero: Int?`; `equipe1: DrawSportsTeamDto?`; `equipe2: DrawSportsTeamDto?`; `empate: Boolean?`; `indexPlacarEquipe1: Int?`; `indexPlacarEquipe2: Int?`; `letraLegenda: String?`; `descricaoLegenda: String?` |
| [DrawSportsTeamDto](../feature/results/transport/src/main/kotlin/br/gov/caixa/loterias/feature/results/data/dto/ResultsDtos.kt) | `parametroEquipe: SportsTeamParameterDto?`; `nome: String?`; `descricaoCurta: String?`; `uf: String?`; `placar: String?`; `vitoria: Boolean?`; `siglaPais: String?`; `indicadorSelecao: Boolean?` |
| [FavoriteBetCloversDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/FavoriteBetDtos.kt) | `trevosSelecionados: JsonElement?` |
| [FavoriteBetDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/FavoriteBetDtos.kt) | `id: Long?`; `modalidade: FavoriteBetEnumDto?`; `nome: String?`; `numerosSelecionados: JsonElement?`; `timeDoCoracao: FavoriteBetNamedParameterDto?`; `mesDeSorte: FavoriteBetNamedParameterDto?`; `trevos: FavoriteBetCloversDto?` |
| [FavoriteBetEnumDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/FavoriteBetDtos.kt) | `valor: Int?`; `descricao: String?` |
| [FavoriteBetNamedParameterDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/FavoriteBetDtos.kt) | `numero: Int?`; `nome: String?`; `uf: String?` |
| [FavoriteBetPageDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/FavoriteBetDtos.kt) | `rowCount: Long?`; `lista: List<FavoriteBetDto>?`; `listaSize: Int?` |
| [FavoriteCartBetDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `id: Long?`; `modalidade: FavoriteCartModalityDto?`; `numerosSelecionados: JsonElement?`; `quantidadeTeimosinhas: Int?`; `espelho: Boolean?`; `indicadorSurpresinha: String?`; `trevos: FavoriteCartCloversDto?`; `quantidadeNumeros: Int?`; `quantidadeTrevos: Int?`; `timeDoCoracao: FavoriteCartTeamDto?`; `mesDeSorte: FavoriteCartMonthDto?` |
| [FavoriteCartCloversDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `trevosSelecionados: JsonElement?` |
| [FavoriteCartDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `id: Long?`; `nome: String?` |
| [FavoriteCartModalityDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `valor: Int?`; `descricao: String?`; `descricaoEspecial: String?` |
| [FavoriteCartMonthDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `nome: String?` |
| [FavoriteCartTeamDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `nome: String?`; `uf: String?` |
| [FederativeUnitDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `id: Long?`; `sigla: String?`; `nome: String?` |
| [GroupedFavoriteBetsDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/FavoriteBetDtos.kt) | `resultado: Map<String, FavoriteBetPageDto>?`; `pageSize: Int?` |
| [LotecaBetMatchDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `equipe1: SportsBetTeamDto`; `equipe2: SportsBetTeamDto`; `empate: Boolean`; `numero: Int`; `descricaoLegenda: String?`; `letraLegenda: String?` |
| [LotogolBetMatchDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `equipe1: SportsBetTeamDto`; `equipe2: SportsBetTeamDto` |
| [LotteryCloverParametersDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `prognosticoMaximoTrevo: Int?`; `quantidadeMinimaTrevo: Int?`; `quantidadeMaximaTrevo: Int?` |
| [LotteryContestDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `modalidade: String?`; `modalidadeDetalhada: LotteryModalityDto?`; `tipoConcurso: String?`; `numero: Int?`; `dataSorteio: String?`; `dataHoraSorteio: String?`; `dataFechamento: String?`; `estimativa: BigDecimal?`; `aberto: Boolean?` |
| [LotteryGameParameterDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `concurso: LotteryContestDto?`; `prognosticoMaximo: Int?`; `quantidadeMinima: Int?`; `quantidadeMaxima: Int?`; `quantidadeSurpresinhas: Int?`; `teimosinhas: List<Int>?`; `valoresAposta: List<LotteryPriceDto>?`; `concursoDisponivelSimulacao: Boolean?`; `concursoCanalAberto: Boolean?`; `trevos: LotteryCloverParametersDto?`; `equipes: List<SportsTeamParameterDto>?`; `partidas: List<SportsMatchParameterDto>?`; `meses: List<LuckyMonthParameterDto>?`; `aceitaEspelho: Boolean?`; `valorApostaMinima: BigDecimal?` |
| [LotteryModalityDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `valor: Int?`; `descricao: String?`; `descricaoEspecial: String?`; `descricaoGestor: String?` |
| [LotteryParameterDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `parametroJogo: LotteryGameParameterDto?`; `proximoConcurso: LotteryGameParameterDto?` |
| [LotteryPriceDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `valor: BigDecimal?`; `numeroPrognosticos: Int?`; `numeroTrevos: Int?`; `quantidadeDuplos: Int?`; `quantidadeTriplos: Int?` |
| [LuckyMonthParameterDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `numero: Int?`; `nome: String?`; `abreviacao: String?` |
| [MarketplaceBetDto](../feature/marketplace/transport/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/dto/MarketplaceDtos.kt) | `indicadorSurpresinha: Boolean?`; `dezenas: JsonElement?`; `trevos: List<Int>?`; `timeDoCoracao: SportsBetTeamDto?`; `mesDeSorte: LuckyMonthParameterDto?`; `partidasLoteca: List<LotecaBetMatchDto>?` |
| [MarketplaceCityDto](../feature/marketplace/transport/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/dto/MarketplaceDtos.kt) | `nome: String?`; `uf: MarketplaceStateDto?` |
| [MarketplaceLotteryDto](../feature/marketplace/transport/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/dto/MarketplaceDtos.kt) | `numeroFormatado: String?` |
| [MarketplacePageDto](../feature/marketplace/transport/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/dto/MarketplaceDtos.kt) | `totalRegistros: Int?`; `paginaAtual: Int?`; `ultimaPagina: Int?`; `cotas: List<MarketplacePoolDto>?` |
| [MarketplacePoolDetailDto](../feature/marketplace/transport/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/dto/MarketplaceDtos.kt) | `qtdCotaTotal: Int?`; `qtdCotaDisponivel: Int?`; `loterica: Long?`; `nomeFantasia: String?`; `municipioCota: MarketplaceCityDto?`; `concurso: Int?`; `tipoConcurso: Int?`; `modalidade: String?`; `dataSorteio: String?`; `diaSorteio: String?`; `horaSorteio: String?`; `vrPremioEstimado: String?`; `qtdApostas: Int?`; `qtdNumeros: Int?`; `vrCotaSemTarifa: String?`; `vrTarifaServicoCota: BigDecimal?`; `vrUltimaCotaComTarifa: String?`; `vrTarifaServicoUltimaCota: String?`; `lotericaFormatada: MarketplaceLotteryDto?`; `apostas: List<MarketplaceBetDto>?`; `vrCotaComTarifa: BigDecimal?`; `vrUltimaCotaSemTarifa: String?` |
| [MarketplacePoolDto](../feature/marketplace/transport/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/dto/MarketplaceDtos.kt) | `codigoBolao: String?`; `modalidade: String?`; `concurso: Int?`; `nomeFantasia: String?`; `dataSorteio: String?`; `valorCota: String?`; `qtdCotaDisponivel: Int?`; `qtdApostas: Int?`; `qtdNumeros: Int?`; `lotericaFavorita: Boolean?`; `loterica: Long?`; `municipio: MarketplaceCityDto?`; `uf: MarketplaceStateDto?`; `qtdCotaTotal: Int?`; `tipoConcurso: Int?`; `vrPremioEstimado: String?`; `vrCotaComTarifa: BigDecimal?`; `valorTarifaServico: BigDecimal?`; `vrUltimaCotaComTarifa: BigDecimal?` |
| [MarketplaceStateDto](../feature/marketplace/transport/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/data/dto/MarketplaceDtos.kt) | `sigla: String?` |
| [MercadoPagoRegistrationEnvelopeDto](../feature/retail/transport/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/dto/PrizeDtos.kt) | `payload: String?`; `codigo: String?`; `redirect: String?` |
| [MunicipalityDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `id: MunicipalityIdDto?`; `nome: String?`; `uf: FederativeUnitDto?` |
| [MunicipalityIdDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `idUF: Long?`; `numero: Long?`; `digitoVerificador: Long?` |
| [NeighborhoodDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `id: Long?`; `nome: String?`; `municipio: MunicipalityDto?` |
| [NotificationInboxEnvelopeDto](../feature/notifications/transport/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/data/dto/NotificationInboxEnvelopeDto.kt) | `payload: JsonElement?`; `codigo: JsonElement?`; `redirect: JsonElement?`; `errosValidacao: JsonElement?` |
| [NotificationReadEnvelopeDto](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationReadApi.kt) | `codigo: String?`; `redirect: JsonElement?`; `errosValidacao: List<JsonElement>?` |
| [PersonalDataUpdateDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `cep: String`; `municipioId: MunicipalityIdDto`; `sexo: String`; `alterandoLoterica: Boolean`; `limiteDiario: BigDecimal`; `aceitaReceberNoticias: Boolean` |
| [PhoneDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `ddd: String?`; `numero: String?` |
| [PrizeRedemptionEnvelopeDto](../feature/retail/transport/src/main/kotlin/br/gov/caixa/loterias/feature/retail/data/dto/PrizeDtos.kt) | `payload: JsonElement?`; `codigo: String?`; `redirect: String?`; `mensagem: String?` |
| [PurchaseEnvelopeDto](../feature/payment/transport/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/dto/PurchaseEnvelopeDto.kt) | `payload: PurchaseProcessingDto?`; `codigo: String?`; `redirect: String?`; `mensagem: String?`; `dataHoraServidor: String?` |
| [PurchaseProcessingDto](../feature/payment/transport/src/main/kotlin/br/gov/caixa/loterias/feature/payment/data/dto/PurchaseProcessingDto.kt) | `id: Long?`; `periodicidade: Long?`; `repeticoes: Int?`; `idSituacaoCompra: Int?` |
| [PushRegistrationEnvelopeDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/PushRegistrationDtos.kt) | `payload: PushRegistrationPayloadDto?`; `codigo: String?`; `redirect: JsonElement?`; `errosValidacao: List<JsonElement>?` |
| [PushRegistrationPayloadDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/PushRegistrationDtos.kt) | `codigo: String?` |
| [PushRegistrationRequestDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/PushRegistrationDtos.kt) | `identificadores: List<Long>`; `token: String`; `plataforma: String`; `canal: String` |
| [RapidConfigurationDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/RapidBettingDtos.kt) | `valorMinimo: BigDecimal?`; `valorMaximo: BigDecimal?`; `valorMinimoPremioPrincipal: BigDecimal?`; `prognosticosObrigatorios: List<Int>?`; `prognosticosProibidos: List<Int>?`; `modalidades: List<LotteryModalityDto>?`; `apostasFavoritas: List<FavoriteCartBetDto>?` |
| [SaveFavoriteCartRequestDto](../feature/cart/transport/src/main/kotlin/br/gov/caixa/loterias/feature/cart/data/dto/CartDtos.kt) | `nome: String`; `manterSurpresinhas: Boolean` |
| [SelfAssessmentAnswerDto](../feature/responsible-gaming/transport/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/dto/ResponsibleGamingDtos.kt) | `idPergunta: Int`; `descricaoResposta: String`; `indicadorResposta: Boolean` |
| [SelfAssessmentPayloadDto](../feature/responsible-gaming/transport/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/dto/ResponsibleGamingDtos.kt) | `retornoPerguntaResposta: List<SelfAssessmentQuestionDto>?` |
| [SelfAssessmentQuestionDto](../feature/responsible-gaming/transport/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/dto/ResponsibleGamingDtos.kt) | `id: Int?`; `descricaoPergunta: String?`; `ordemPergunta: Int?`; `ativo: Boolean?` |
| [SocialTransfersDto](../feature/responsible-gaming/transport/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/dto/ResponsibleGamingDtos.kt) | `arrecadacaoAcumulada: BigDecimal?`; `arrecadacaoUltimaSemana: BigDecimal?`; `dataBase: String?`; `percentualProgramasSociais: BigDecimal?`; `percentualEducacao: BigDecimal?`; `percentualCultura: BigDecimal?`; `percentualEsporte: BigDecimal?`; `percentualSeguranca: BigDecimal?`; `percentualSeguridade: BigDecimal?`; `percentualOutros: BigDecimal?` |
| [SportsBetTeamDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `indicadorSelecao: Boolean`; `nome: String`; `numero: Int`; `numeroPais: Int`; `descricaoCurta: String`; `descricaoLonga: String`; `pais: String`; `siglaPais: String`; `uf: String`; `nomeClass: String`; `vitoria: Boolean?`; `placar: String?`; `parametroEquipe: SportsTeamParameterDto?` |
| [SportsMatchParameterDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `numero: Int?`; `equipe1: SportsTeamParameterDto?`; `equipe2: SportsTeamParameterDto?`; `descricaoLegenda: String?`; `letraLegenda: String?` |
| [SportsShieldTeamDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `id: Long?` |
| [SportsTeamParameterDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `indicadorSelecao: Boolean?`; `nome: String?`; `numero: Int?`; `numeroPais: Int?`; `descricaoCurta: String?`; `descricaoLonga: String?`; `pais: String?`; `siglaPais: String?`; `uf: String?`; `nomeClass: String?` |
| [SportsTeamShieldDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingParametersDtos.kt) | `id: Int?`; `modalidades: List<String>?`; `equipe: SportsShieldTeamDto?`; `imagem: String?` |
| [SupportQuestionDto](../feature/support/transport/src/main/kotlin/br/gov/caixa/loterias/feature/support/data/dto/SupportDtos.kt) | `id: Long?`; `pergunta: String?`; `resposta: String?` |
| [SupportSectionDto](../feature/support/transport/src/main/kotlin/br/gov/caixa/loterias/feature/support/data/dto/SupportDtos.kt) | `id: Long?`; `nome: String?`; `duvidas: List<SupportQuestionDto>?` |
| [SurpriseBetRequestDto](../feature/betting/transport/src/main/kotlin/br/gov/caixa/loterias/feature/betting/data/dto/BettingSubmissionDtos.kt) | `modalidade: String`; `tipoConcurso: DomainCharacterDto`; `indicadorSurpresinha: DomainIntegerDto`; `surpresinha: Boolean`; `concursoAlvo: Int?`; `quantidadeNumeros: Int`; `quantidadeTeimosinhas: Int`; `quantidadeSurpresinhas: Int`; `quantidadeNumerosMaisMilionariaTrevo: Int`; `gerarEspelho: Boolean?`; `quantidadeApostas: Int`; `timeDoCoracao: SportsBetTeamDto?` |
| [SuspendAccountRequestDto](../feature/responsible-gaming/transport/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/data/dto/AccountSuspensionDtos.kt) | `cpfApostador: String`; `periodoSuspensao: Int` |
| [TermsDto](../feature/support/transport/src/main/kotlin/br/gov/caixa/loterias/feature/support/data/dto/SupportDtos.kt) | `conteudo: String?` |
| [UnreadNotificationsEnvelopeDto](../core/network/src/main/java/br/gov/caixa/loterias/core/network/api/NotificationReadApi.kt) | `payload: List<JsonElement>?`; `codigo: String?`; `redirect: JsonElement?`; `errosValidacao: List<JsonElement>?` |
| [UserProfileDto](../feature/account/transport/src/main/kotlin/br/gov/caixa/loterias/feature/account/data/dto/AccountDtos.kt) | `cpf: String?`; `email: String?`; `nome: String?`; `dataNascimento: String?`; `telefone: PhoneDto?`; `cep: String?`; `aceitaReceberNoticias: Boolean?`; `municipioId: MunicipalityIdDto?`; `sexo: String?`; `limiteMinimoDiario: BigDecimal?`; `limiteDiario: BigDecimal?`; `limiteDiarioAutorizado: BigDecimal?` |

### Conferência e atualização deste inventário

O inventário estruturado em [api-contracts.json](technical-guide/api-contracts.json) conserva as variantes de assinatura por rota, inclusive quando duas features compartilham um endpoint com corpos diferentes. São **111 combinações de método e rota declarada**, incluindo compatibilidade; aliases de variável de path podem representar o mesmo recurso HTTP. Isso não equivale a 111 chamadas disparadas pelo app.

Para atualizar o inventário, execute `python3 scripts/documentation/inventory_api.py` e revise estas tabelas contra os DataSources, mapeadores e políticas. O extrator também lê interfaces em arquivos cujo nome não termina em `Api.kt`, como `AccountVerificationBackend.kt`. QueryMaps, JSON dinâmico e uso efetivo de cada declaração exigem conferência do código; exemplos sintéticos do JSON não são fixtures de respostas reais nem contratos completos do servidor.

## Catálogo completo dos contratos de casos de uso

A lista abaixo registra os contratos de casos de uso de Domain/API encontrados no código, inclusive operações que **alteram** dados. A presença no catálogo não autoriza executar um comando para consultar dados. Ao criar/renomear contrato, atualizar também o guia e conferir os links. Este documento é um retrato revisado do código, não um gerador de bindings nem um contrato de API HTTP.

### account

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [ClearLocalSessionDataUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ClearLocalSessionDataUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [GetAccountProfileUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/GetAccountProfileUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [GetUnreadNotificationCountUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/GetUnreadNotificationCountUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [ObserveAuthenticatedSessionContentUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ObserveAuthenticatedSessionContentUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [ReadLatestNotificationUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ReadLatestNotificationUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [RecalculateAuthorizedLimitUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/RecalculateAuthorizedLimitUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [RegisterAccountUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/RegisterAccountUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [ResolveZipCodeUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ResolveZipCodeUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [ResumeLocalSessionCleanupUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/ResumeLocalSessionCleanupUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [SignOutSessionUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/SignOutSessionUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [UpdatePersonalDataUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/UpdatePersonalDataUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [VerifyAccountAccessUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/VerifyAccountAccessUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |
| [VerifyPostLoginAccessUseCase](../feature/account/domain/src/main/kotlin/br/gov/caixa/loterias/feature/account/domain/usecase/VerifyPostLoginAccessUseCase.kt) | `br.gov.caixa.loterias.feature.account.domain.usecase` |

### betting

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [AddBetToCartUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/AddBetToCartUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [AddComboToCartUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/AddComboToCartUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [AddFavoriteBetToCartUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/AddFavoriteBetToCartUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [DeleteFavoriteBetUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/DeleteFavoriteBetUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [GenerateRapidBetsUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GenerateRapidBetsUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [GetBetCombosUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetBetCombosUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [GetBettingParametersUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetBettingParametersUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [GetBettingSpecialContestsUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetBettingSpecialContests.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [GetFavoriteBetsUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetFavoriteBetsUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [GetLotteryModalitiesUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetLotteryModalitiesUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [GetRapidBetConfigurationUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetRapidBetConfigurationUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [GetShakeEnabledUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/GetShakeEnabledUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [PrepareBetEntryUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/PrepareBetEntryUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [PrepareHomeBettingActionUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/PrepareHomeBettingActionUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [PreparePoolEntryUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/PreparePoolEntryUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [SaveFavoriteBetUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/SaveFavoriteBetUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [SaveRapidBetConfigurationUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/SaveRapidBetConfigurationUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |
| [ValidateFavoriteBetUseCase](../feature/betting/domain/src/main/kotlin/br/gov/caixa/loterias/feature/betting/domain/usecase/ValidateFavoriteBetUseCase.kt) | `br.gov.caixa.loterias.feature.betting.domain.usecase` |

### cart

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [AddFavoriteCartToCurrentUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/AddFavoriteCartToCurrentUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [CheckCheckoutLocationUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/CheckCheckoutLocationUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [ClearCartUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/ClearCartUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [DeleteFavoriteCartBetUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/DeleteFavoriteCartBetUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [DeleteFavoriteCartUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/DeleteFavoriteCartUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [DiscardLocalCartReviewUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/DiscardLocalCartReviewUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [GetCurrentCartUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/GetCurrentCartUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [GetFavoriteCartBetsUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/GetFavoriteCartBetsUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [GetFavoriteCartsUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/GetFavoriteCartsUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [GetLocalCartReviewUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/GetLocalCartReviewUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [HasRecentPurchaseUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/HasRecentPurchaseUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [MarkFavoriteCartAddedUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/MarkFavoriteCartAddedUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [RecordCheckoutLocationRequestUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/RecordCheckoutLocationRequestUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [RecordRecentPurchaseUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/RecordRecentPurchaseUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [RemoveCartItemUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/RemoveCartItemUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [RemoveExpiredPoolShareUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/RemoveExpiredPoolShareUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [SaveCurrentCartFavoriteUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/SaveCurrentCartFavoriteUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [ValidateCartUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/ValidateCartUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [ValidateFavoriteCartNameUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/ValidateFavoriteCartNameUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [VerifyCartPaymentAccountUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/VerifyCartPaymentAccountUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |
| [WasFavoriteCartAddedUseCase](../feature/cart/domain/src/main/kotlin/br/gov/caixa/loterias/feature/cart/domain/usecase/WasFavoriteCartAddedUseCase.kt) | `br.gov.caixa.loterias.feature.cart.domain.usecase` |

### home

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [HomePreferencesUseCases](../feature/home/domain/src/main/kotlin/br/gov/caixa/loterias/feature/home/domain/usecase/HomePreferencesUseCases.kt) | `br.gov.caixa.loterias.feature.home.domain.usecase` |
| [SelectHomeResaleOffersUseCase](../feature/home/domain/src/main/kotlin/br/gov/caixa/loterias/feature/home/domain/usecase/SelectHomeResaleOffers.kt) | `br.gov.caixa.loterias.feature.home.domain.usecase` |

### marketplace

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [AddMarketplacePoolToCartUseCase](../feature/marketplace/domain/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/usecase/AddMarketplacePoolToCartUseCase.kt) | `br.gov.caixa.loterias.feature.marketplace.domain.usecase` |
| [GetMarketplaceDetailUseCase](../feature/marketplace/domain/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/usecase/GetMarketplaceDetailUseCase.kt) | `br.gov.caixa.loterias.feature.marketplace.domain.usecase` |
| [GetMarketplaceSpecialContestsUseCase](../feature/marketplace/api/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/usecase/GetMarketplaceSpecialContestsUseCase.kt) | `br.gov.caixa.loterias.feature.marketplace.domain.usecase` |
| [SearchMarketplaceUseCase](../feature/marketplace/domain/src/main/kotlin/br/gov/caixa/loterias/feature/marketplace/domain/usecase/SearchMarketplaceUseCase.kt) | `br.gov.caixa.loterias.feature.marketplace.domain.usecase` |

### notifications

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [DeleteAllNotificationsUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/DeleteAllNotificationsUseCase.kt) | `br.gov.caixa.loterias.feature.notifications.domain.usecase` |
| [DeleteNotificationUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/DeleteNotificationUseCase.kt) | `br.gov.caixa.loterias.feature.notifications.domain.usecase` |
| [GetNotificationPreferencesUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/GetNotificationPreferencesUseCase.kt) | `br.gov.caixa.loterias.feature.notifications.domain.usecase` |
| [GetNotificationsUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/GetNotificationsUseCase.kt) | `br.gov.caixa.loterias.feature.notifications.domain.usecase` |
| [MarkAllNotificationsReadUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/MarkAllNotificationsReadUseCase.kt) | `br.gov.caixa.loterias.feature.notifications.domain.usecase` |
| [MarkNotificationReadUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/MarkNotificationReadUseCase.kt) | `br.gov.caixa.loterias.feature.notifications.domain.usecase` |
| [SetNotificationPreferenceUseCase](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/SetNotificationPreferenceUseCase.kt) | `br.gov.caixa.loterias.feature.notifications.domain.usecase` |
| [NotificationInboxActions](../feature/notifications/domain/src/main/kotlin/br/gov/caixa/loterias/feature/notifications/domain/usecase/NotificationInboxActions.kt) | `br.gov.caixa.loterias.feature.notifications.domain.usecase` |

### onboarding

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [CompleteIntroductionUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/CompleteIntroductionUseCase.kt) | `br.gov.caixa.loterias.feature.onboarding.domain.usecase` |
| [GetActiveForceUpdateUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/GetActiveForceUpdateUseCase.kt) | `br.gov.caixa.loterias.feature.onboarding.domain.usecase` |
| [GetBettingTutorialDismissedUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/GetBettingTutorialDismissedUseCase.kt) | `br.gov.caixa.loterias.feature.onboarding.domain.usecase` |
| [GetLauncherIconPresetUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/GetLauncherIconPresetUseCase.kt) | `br.gov.caixa.loterias.feature.onboarding.domain.usecase` |
| [ObserveForceUpdateUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/ObserveForceUpdateUseCase.kt) | `br.gov.caixa.loterias.feature.onboarding.domain.usecase` |
| [ObserveLauncherIconCalendarUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/ObserveLauncherIconCalendarUseCase.kt) | `br.gov.caixa.loterias.feature.onboarding.domain.usecase` |
| [RefreshForceUpdateUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/RefreshForceUpdateUseCase.kt) | `br.gov.caixa.loterias.feature.onboarding.domain.usecase` |
| [SetBettingTutorialDismissedUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/SetBettingTutorialDismissedUseCase.kt) | `br.gov.caixa.loterias.feature.onboarding.domain.usecase` |
| [ShouldShowIntroductionUseCase](../feature/onboarding/domain/src/main/kotlin/br/gov/caixa/loterias/feature/onboarding/domain/usecase/ShouldShowIntroductionUseCase.kt) | `br.gov.caixa.loterias.feature.onboarding.domain.usecase` |

### payment

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [DeleteSavedCardUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/DeleteSavedCardUseCase.kt) | `br.gov.caixa.loterias.feature.payment.domain.usecase` |
| [GeneratePixChargeUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/GeneratePixChargeUseCase.kt) | `br.gov.caixa.loterias.feature.payment.domain.usecase` |
| [GetPaymentMethodsUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/GetPaymentMethodsUseCase.kt) | `br.gov.caixa.loterias.feature.payment.domain.usecase` |
| [GetPurchaseProgressUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/GetPurchaseProgressUseCase.kt) | `br.gov.caixa.loterias.feature.payment.domain.usecase` |
| [GetSavedCardsUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/GetSavedCardsUseCase.kt) | `br.gov.caixa.loterias.feature.payment.domain.usecase` |
| [PrepareMercadoPagoRegistrationUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/PrepareMercadoPagoRegistrationUseCase.kt) | `br.gov.caixa.loterias.feature.payment.domain.usecase` |
| [SubmitCardPaymentUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/SubmitCardPaymentUseCase.kt) | `br.gov.caixa.loterias.feature.payment.domain.usecase` |
| [ValidateDamUseCase](../feature/payment/domain/src/main/kotlin/br/gov/caixa/loterias/feature/payment/domain/usecase/ValidateDamUseCase.kt) | `br.gov.caixa.loterias.feature.payment.domain.usecase` |

### responsible-gaming

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [GetSelfAssessmentQuestionsUseCase](../feature/responsible-gaming/domain/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/domain/usecase/GetSelfAssessmentQuestionsUseCase.kt) | `br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase` |
| [GetSocialTransfersUseCase](../feature/responsible-gaming/domain/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/domain/usecase/GetSocialTransfersUseCase.kt) | `br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase` |
| [SubmitSelfAssessmentUseCase](../feature/responsible-gaming/domain/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/domain/usecase/SubmitSelfAssessmentUseCase.kt) | `br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase` |
| [SuspendAccountUseCase](../feature/responsible-gaming/domain/src/main/kotlin/br/gov/caixa/loterias/feature/responsiblegaming/domain/usecase/SuspendAccountUseCase.kt) | `br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase` |

### results

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [GetConfirmedBetResultsUseCase](../feature/results/domain/src/main/kotlin/br/gov/caixa/loterias/feature/results/domain/usecase/GetConfirmedBetResultsUseCase.kt) | `br.gov.caixa.loterias.feature.results.domain.usecase` |
| [GetLatestResultUseCase](../feature/results/domain/src/main/kotlin/br/gov/caixa/loterias/feature/results/domain/usecase/GetLatestResultUseCase.kt) | `br.gov.caixa.loterias.feature.results.domain.usecase` |
| [GetResultByContestUseCase](../feature/results/domain/src/main/kotlin/br/gov/caixa/loterias/feature/results/domain/usecase/GetResultByContestUseCase.kt) | `br.gov.caixa.loterias.feature.results.domain.usecase` |

### retail

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [CancelPixRedemptionUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/CancelPixRedemptionUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [CheckRetailLocationUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/CheckRetailLocationUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [ConsultPixRedemptionKeyUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/ConsultPixRedemptionKeyUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [DownloadBetReceiptUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/DownloadBetReceiptUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [DownloadPrizeReceiptUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/DownloadPrizeReceiptUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [GenerateLotteryRedemptionCodeUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/GenerateLotteryRedemptionCodeUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [GetFavoriteRetailersUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/GetFavoriteRetailersUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [GetPrizeDetailsUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/GetPrizeDetailsUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [RedeemPrizeUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/RedeemPrizeUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [SearchRetailersUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/SearchRetailersUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |
| [SetRetailerFavoriteUseCase](../feature/retail/domain/src/main/kotlin/br/gov/caixa/loterias/feature/retail/domain/usecase/SetRetailerFavoriteUseCase.kt) | `br.gov.caixa.loterias.feature.retail.domain.usecase` |

### support

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [AcceptTermsUseCase](../feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain/usecase/AcceptTermsUseCase.kt) | `br.gov.caixa.loterias.feature.support.domain.usecase` |
| [GetSupportQuestionsUseCase](../feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain/usecase/GetSupportQuestionsUseCase.kt) | `br.gov.caixa.loterias.feature.support.domain.usecase` |
| [GetSupportSectionsUseCase](../feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain/usecase/GetSupportSectionsUseCase.kt) | `br.gov.caixa.loterias.feature.support.domain.usecase` |
| [GetTermsUseCase](../feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain/usecase/GetTermsUseCase.kt) | `br.gov.caixa.loterias.feature.support.domain.usecase` |

### tickets

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [CheckTicketUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/CheckTicketUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [GetConfirmedBetsConfigurationUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetConfirmedBetsConfigurationUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [GetConfirmedBetsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetConfirmedBetsUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [GetPurchaseBetsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchaseBetsUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [GetPurchaseDetailsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchaseDetailsUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [GetPurchaseMonthsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchaseMonthsUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [GetPurchasePaymentMethodsUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchasePaymentMethodsUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [GetPurchaseStatusesUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchaseStatusesUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [GetPurchasesUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/GetPurchasesUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [LoadConfirmedBetUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/LoadConfirmedBetUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [RecoverConfirmedBetUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/RecoverConfirmedBetUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |
| [SavePurchaseAsFavoriteUseCase](../feature/tickets/domain/src/main/kotlin/br/gov/caixa/loterias/feature/tickets/domain/usecase/SavePurchaseAsFavoriteUseCase.kt) | `br.gov.caixa.loterias.feature.tickets.domain.usecase` |

### core

| Contrato / classe de entrada | Pacote |
| --- | --- |
| [CampaignConfigurationUseCases](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/CampaignConfigurationUseCases.kt) | `br.gov.caixa.loterias.core.domain.configuration.usecase` |
| [EnvironmentSelection](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/environment/usecase/EnvironmentSelection.kt) | `br.gov.caixa.loterias.core.domain.environment.usecase` |
| [GetHomeFeatureConfigurationUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt) | `br.gov.caixa.loterias.core.domain.configuration.usecase` |
| [GetMarketplaceFilterParametersUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt) | `br.gov.caixa.loterias.core.domain.configuration.usecase` |
| [IsVersionSupportedUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt) | `br.gov.caixa.loterias.core.domain.configuration.usecase` |
| [PrepareNetworkSecurityUseCase](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/configuration/usecase/ConfigurationUseCases.kt) | `br.gov.caixa.loterias.core.domain.configuration.usecase` |
| [RegisterPushDevice](../core/domain/src/main/kotlin/br/gov/caixa/loterias/core/domain/push/usecase/RegisterPushDevice.kt) | `br.gov.caixa.loterias.core.domain.push.usecase` |

Total conferido: **124 contratos/classes de entrada** neste catálogo. Marketplace Filter reutiliza os contratos listados em Marketplace e Core. As portas de identidade, biometria, localização e conectividade estão descritas nas seções correspondentes.
