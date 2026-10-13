# Entrada no pagamento — 07/09/2026

Revisão posterior em `CART_CHECKOUT_ACTION_AUDIT.md`: carrinho vazio também percorre os gates
e a validação do serviço; mínimo e quantidade não são bloqueios locais antecipados. O novo
CaixaDSCartActions preserva o CTA/layout legado. Handlers HTTP/redirect completos e captura
Android permanecem pendentes; os registros abaixo são históricos.

Status: conta, localização e histórico de compra repetida integrados ao carrinho; **sequência completa de pré-pagamento ainda não homologada**. Revisões posteriores em `CART_LOCATION_PARITY_AUDIT.md` e `CART_RECENT_PURCHASE_PARITY_AUDIT.md`.

## Ordem encontrada no legado

`CarrinhoActivity.onClickAvancaPagamento` trata cadastro pendente antes da suspensão (`AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso`, ação 2). Depois atualiza cotas vencidas e consulta localização/permissão. Em território brasileiro, verifica compra de mesmo valor nas últimas 24 horas; o diálogo pode interromper a continuação. Somente então confirma login, se necessário, e chama `validarCarrinho` antes de abrir `FormaPagamentoActivity`.

O alerta de suspensão usa `autosuspensao_alerta_nova`; Entendi fecha o alerta e mantém o carrinho. A confirmação de login usa `confirmacao_continuar_experimente`, Sim/Não; Não mantém o carrinho. No legado, Sim chama diretamente `RedirectNetwork.connectKeycloak`.

## Implementado nesta revisão

- `VerifyCartPaymentAccount`, em `core:domain:cart`, retorna decisões tipadas: permitido, login necessário, suspenso ou conclusão de cadastro/termos/autoavaliação. A pendência de cadastro tem precedência sobre suspensão, como no clique legado. Falha do serviço não é transformada em autorização.
- O caso de uso consulta a sessão em cada tentativa por dependência fornecida no composition root Hilt. Sem sessão, não chama o endpoint protegido de verificação. Com sessão, usa `VerifyAccountAccessUseCase`; nenhuma feature passa a interpretar JWT ou conhecer Retrofit.
- `CartViewModel` chama essa verificação antes da atualização de cotas/validação. Cadastro ou suspensão interrompem o fluxo; login necessário é apresentado depois da atualização de cotas, sem chamar validação. Cliques repetidos durante requisição ou decisão pendente são ignorados.
- A UI usa os textos literais de suspensão e confirmação de login, pelo CaixaDS. Entendi/Não preservam o carrinho. Cadastro navega para a rota correspondente, retirando o carrinho da pilha quando o destino é registro; termos/autoavaliação mantêm destinos próprios.
- `ValidateCartUseCase` passou a ser uma dependência obrigatória do ViewModel. Foi removido o fallback de sucesso que existia quando a dependência era nula. Fakes de sucesso permanecem somente na construção explícita dos testes.

## Evidências

`build/verification/cart-account-gate-20260907/`:

- 223 testes JVM aprovados: 204 de estados/domínio/rotas/regras, 3 de envelope, 15 de repositories e 1 de preferências.
- Casos novos cobrem todas as combinações de destino/suspensão, ausência e mudança de sessão, falha do serviço, prioridade do cadastro, bloqueio da validação/atualização quando não permitido, decisão pendente, preservação do carrinho e retry após resolução do acesso.
- Política de conta: 14/14 linhas (100%). Escopo de ViewModels/UiStates/regras de carrinho, incluindo corrotinas: 444/449 linhas (98,89%). Não inclui renderização, Hilt, navegador ou cobertura global.
- Fontes atuais do host, provider Hilt, container e telas compilaram isoladamente com as dependências locais. Não houve geração/validação de novo APK, Hilt/KSP, R8 ou dos flavors nesta revisão.

## Pendências para equivalência integral

1. Homologar a consulta de localização/permissão agora inserida entre atualização das cotas e continuação, incluindo retorno à Home, configurações e callbacks nativos. Ver `CART_LOCATION_PARITY_AUDIT.md`; o gate visual de lotéricas não foi reutilizado.
2. Homologar no Android a busca/gravação local de compra do mesmo valor em 24 horas e o alerta agora implementados. Ver `CART_RECENT_PURCHASE_PARITY_AUDIT.md`; persistência nativa, polling/redirects e validação da ordem com localização ainda pendentes.
3. Homologar callback/cancelamento/retorno no navegador real. A abertura direta foi implementada posteriormente, conforme a revisão abaixo; a autenticação não foi exercitada no APK nesta rodada.
4. Homologar respostas de `validarCarrinho`, limites, localização enviada aos serviços e redirecionamentos de erro. As verificações deste documento não certificam esses ramos nem as outras entradas de pagamento, como Rapidão e rotas diretas.
5. Comparar os alertas/loading no APK legado/Compose, incluindo retorno após cadastro, expiração da sessão, todos os flavors e release com R8.

O fluxo não deve ser declarado integralmente equivalente enquanto essas etapas estiverem pendentes.

## Autorização direta pelo carrinho — revisão posterior

O Sim do alerta navega para `account/login?authorize=true`. A mesma rota de login aceita o parâmetro opcional, falso por padrão, e dispara `AuthenticationGateway.createAuthorizationIntent` pelo launcher AppAuth existente. Não exige um segundo clique no CTA e não troca o mecanismo OAuth/PKCE. A tentativa automática é consumida em estado salvável antes de abrir o navegador, evitando repeti-la em recomposições/rotação. Entrada normal na tela de login permanece manual, com o mesmo layout.

Cancelar a autorização iniciada pelo carrinho (`RESULT_CANCELED` sem dados) encerra a tentativa e retorna à origem. Na entrada normal do login, permanece na tela. Falha ao abrir o navegador apresenta mensagem e permite nova tentativa manual. `LoginViewModel` bloqueia lançamentos concorrentes e callbacks repetidos durante troca de token/verificação ou depois de concluir a autenticação.

O ViewModel foi separado de `LoginRoute.kt`. `VerifyAccountAccessUseCase` passou a ser obrigatório: não há mais fallback de conta liberada quando a dependência é omitida. Cancelamento de corrotina na troca de token ou na consulta do cadastro não é convertido em login bem-sucedido nem deixa o loading ativo.

Evidências em `build/verification/cart-browser-login-20260907/`:

- 232 testes JVM aprovados (213 de estados/domínio/rotas/regras, 3 de envelope, 15 de repositories, 1 de preferências).
- Inclui 9 testes do login: destinos pós-autenticação, falhas, biometria, lançamento único, cancelamento, ausência de navegador, callback duplicado e cancelamento em cada operação assíncrona.
- `LoginViewModel.kt`, incluindo corrotinas/estado: 65/66 linhas (98,48%). Não é cobertura do módulo account inteiro nem de UI/navegador.
- Compilação isolada dos fontes atuais de login, host, container e demais telas de integração concluída. Sem execução de navegador real, APK, Hilt/KSP ou flavors nessa rodada.

Ainda verificar no APK os estados salváveis, retorno após rotação/processo recriado, cancelamento com/sem erro AppAuth, ausência de navegador e a atualização da sessão nas telas preservadas na pilha. Esses testes de lógica não homologam callbacks Android, paridade visual nem as outras etapas de checkout.
