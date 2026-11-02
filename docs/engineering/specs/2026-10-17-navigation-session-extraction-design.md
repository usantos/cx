# Separação da navegação e sessão do host

## Objetivo

Refatorar `LoteriasApp.kt`, separando o registro dos destinos por fluxo e a coordenação de sessão sem alterar comportamento. A referência é a árvore local de 17/10/2026, incluindo as alterações ainda não commitadas.

## Estrutura

Um único `NavHost` continua responsável pela pilha. Funções de extensão de `NavGraphBuilder` registram Home, onboarding, conta, jogo responsável, apostas, carrinho, pagamento, bilhetes, marketplace, resultados, lotéricas/resgate, notificações e suporte. Não serão introduzidos grafos aninhados: isso mudaria os owners e a semântica dos `popUpTo`.

`AppNavigationState` mantém o contexto de detalhes já existente e os estados transitórios de aposta, revisão do marketplace e modalidade solicitada. `AppSessionState` mantém os estados de retorno de login/cadastro. `rememberAppSessionCoordinator` reúne identidade, logout, expiração em foreground, recuperação, prompt de visitante e callbacks de autenticação. `AppNavigationActions` reúne as transições entre fluxos.

## Contratos preservados

- Strings de rota, argumentos, defaults, ViewModel keys e owners de cada destino.
- `remember` versus `rememberSaveable` e chaves por proprietário da sessão.
- Validação da identidade capturada, descarte de retornos de outra conta e restauração diferida.
- Ordem de navegação após autenticação: Home como base antes do destino privado.
- Cancelamento e recusa de login, retomada de ação da Home e cadastro obrigatório.
- Listeners de lifecycle/destino e descarte do host de resgate.
- A extração não altera serviços, contratos de backend ou configuração de segurança. A remoção da CXGuard foi autorizada separadamente, conforme a ampliação de escopo abaixo.

## Validação

Testes JVM do host antes/depois; testes focados de transição do estado extraído; gates Compose, arquitetura e CaixaDS; compilação do host e dos testes Android. Executar regressões Android de visitante/restauração em AVD dedicado quando a configuração local permitir. Registrar impedimentos reais sem modificar gates ou licenças.

## Ampliação autorizada durante a execução

O usuário solicitou remover a CXGuard. Remover plugin/runtime, repositórios Maven exclusivos, configurações, adapters, telemetria e testes/documentação exclusivos dessa integração. Retornar o minSdk de 26 para 24, cujo aumento era exclusivo da CXGuard. Preservar Chucker e todas as demais integrações existentes. Essa remoção é uma mudança de comportamento explicitamente autorizada, separada da extração de navegação.
