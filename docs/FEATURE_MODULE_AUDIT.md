# Auditoria dos módulos de todas as features

Data: 20/10/2026. Escopo: 52 módulos nas 14 features declaradas em settings: 14 de apresentação, 13 Domain, 13 Data, três API e nove Transport. Foram examinados fontes, declarações próprias, consumidores Gradle/Kotlin, navegação, DI e agregados de testes/cobertura. As revisões independentes cobriram todos os 16 módulos Domain/API e todos os 22 Data/Transport; a revisão principal cobriu os 14 módulos de apresentação e conferiu os candidatos menores.

**Resultado: nenhum módulo inteiro é apenas uma fachada redundante. Nenhum módulo foi removido.** Todos contêm comportamento, contratos ou tipos concretos com consumidores ativos. Não há módulo de feature vazio ou cujo único papel seja reexportar outro módulo. O projeto conserva 52 módulos de features, 12 CORE e o APP (65 ao todo).

O critério é responsabilidade efetiva, não número de arquivos. O antigo Database só encaminhava parâmetros para Android. Home Data, por exemplo, define o arquivo/chave/momento de escrita de uma preferência da feature, implementando sua porta de domínio. Eliminar esse módulo exigiria realocar a implementação e alterar a divisão de camadas, em vez de retirar um encaminhamento equivalente ao antigo Database.

## Apresentação: 14 módulos

| Módulo | Responsabilidade própria | Consumidor efetivo (exemplos) |
|---|---|---|
| `:feature:marketplace-filter` | ViewModel de filtros, validações, projeções e formulário Compose | MarketplaceNavigationGraph/MarketplaceFilterRoute |
| `:feature:marketplace` | Listagem e detalhe de bolões, confirmação e fluxo de filtros | AppNavHost/marketplaceGraph |
| `:feature:support` | Ajuda, perguntas, termos, estado de carregamento e falhas | AppNavHost/supportGraph |
| `:feature:onboarding` | Estado/páginas de introdução, confirmação e tutoriais | AppNavHost/onboardingGraph |
| `:feature:account` | Login, cadastro, dados pessoais, ambiente e coordenação visual de sessão | AppNavHost/accountGraph |
| `:feature:betting` | Fluxos e estado de apostas, favoritos, rápidas, combos, grades e tutoriais | AppNavHost/bettingGraph e bettingTutorialGraph |
| `:feature:cart` | Estado, adição, revisão e restauração do carrinho e carrinhos favoritos | AppNavHost/cartGraph |
| `:feature:payment` | Coordenação visual de métodos, cartões, Pix, progresso e avisos de compra | AppNavHost/paymentGraph |
| `:feature:tickets` | Listagem/detalhe de apostas e compras, filtros, scanner e restauração | AppNavHost/ticketsGraph |
| `:feature:results` | Consulta e apresentação de concursos, premiação e resultados de apostas | AppNavHost/resultsGraph |
| `:feature:responsible-gaming` | Questionário, suspensão, resultados e repasses sociais | AppNavHost/responsibleGamingGraph |
| `:feature:notifications` | Estado, filtros, ações, efeitos e preferências de mensagens | AppNavHost/notificationsGraph |
| `:feature:retail` | Busca/localização de lotéricas, resgate por canal e comprovantes | AppNavHost/retailGraph |
| `:feature:home` | Home, drawer, preferências, campanhas e atalhos de navegação | AppNavHost/homeGraph |

## Domain: 13 módulos

| Módulo | Responsabilidade própria | Consumidor efetivo (exemplos) |
|---|---|---|
| `:feature:account:domain` | Validação de cadastro/dados e coordenação de sessão, pós-login e conteúdo | AccountModule; repositories de Account Data; Core Auth |
| `:feature:betting:domain` | Seleção/limites de jogos, concursos, suspensão, favoritos e cache por identidade | Betting Data/presentation; Cart; Home; Marketplace |
| `:feature:cart:domain` | Regras de carrinho visitante, checkout, geografia, compra recente e migração durável | Cart Data; pós-login de Account; Payment |
| `:feature:home:domain` | Contrato de layout, snapshot e ordenação/toggle de preferências Shake | SharedPreferencesHomeLayout; HomePreferencesModule |
| `:feature:marketplace:domain` | Busca por área, validação/preço do pedido, paginação e contratos de bolões | MarketplaceModule; RemoteMarketplaceRepository |
| `:feature:notifications:domain` | Modelos/contratos de inbox e preferências; registro push protegido contra troca de sessão | Notifications Data/runtime e ViewModel |
| `:feature:onboarding:domain` | Contrato de introdução, decisão de exibição e identificadores/chaves de tutorial | Onboarding Data/startup; Betting Domain/DI |
| `:feature:payment:domain` | Validação de cartão, métodos, polling, identidade de exclusão e dados Mercado Pago | Payment Data/presentation; Retail; Tickets |
| `:feature:responsible-gaming:domain` | Validação/ordenação do questionário, respostas, durações e cálculo de repasses | Data, DI e ViewModel da feature; Home |
| `:feature:results:domain` | Modelos/contratos de resultado e expansão de concursos recorrentes | Results Data/DI; Home; Tickets |
| `:feature:retail:domain` | Validação de busca, IDs, PIX/email e despacho de resgate por canal | Retail Data/DI; Marketplace/filter; Tickets |
| `:feature:support:domain` | Repository, modelos e contratos; conversão de exceções em AppResult nos casos de uso | RemoteSupportRepository; SupportModule/ViewModel |
| `:feature:tickets:domain` | Contratos/modelos de apostas/compras, validação de IDs e normalização de entradas | Tickets Data/DI; Results; Core Navigation |

## API: três módulos

| Módulo | Responsabilidade própria | Consumidor efetivo (exemplos) |
|---|---|---|
| `:feature:cart:api` | Modelos compartilhados de carrinho/favoritos e cálculo de betCount | Account Domain; Betting; Marketplace; Cart; Core Navigation |
| `:feature:account:api` | Identidade referencial de sessão, validação do ownerKey e provider | Core Auth/push; Account/Cart; Betting; Notifications |
| `:feature:marketplace:api` | Filtros normalizados/imutáveis, cópias defensivas, validação/query e localização | Core Domain/Data; Marketplace; marketplace-filter; navegação |

## Data: 13 módulos

| Módulo | Responsabilidade própria | Consumidor efetivo (exemplos) |
|---|---|---|
| `:feature:account:data` | Mapeamento de perfil/cadastro, ownership de sessão, confirmação de transferência e limpeza durável | AccountModule; fluxo pós-login |
| `:feature:betting:data` | Mapeamento de parâmetros/modalidades, requests de favoritos/rápidas e persistência Shake | AppBettingBindings |
| `:feature:cart:data` | SQLite visitante, transações/recibos/fingerprints, compras recentes e mapeamentos de carrinho | CartModule; RemotePostLoginAccountRepository |
| `:feature:home:data` | Arquivo home_v2_layout, chave modality_order e escrita apply | HomePreferencesModule |
| `:feature:marketplace:data` | Filtros/paginação, valores monetários, normalização de bolões e conversão de pedidos | MarketplaceModule |
| `:feature:notifications:data` | Inbox sincronizado, preferências, permissões/canal/banner, Firebase e lifecycle/retry de tokens | NotificationsModule; PushRuntime |
| `:feature:onboarding:data` | Arquivos legados por chave, default de introdução e persistência de tutoriais | OnboardingModule |
| `:feature:payment:data` | Tokenização, mapeamentos Pix/DAM e interpretação de progresso/erros/resultado ambíguo | PaymentModule |
| `:feature:responsible-gaming:data` | Filtragem de perguntas/defaults, repasses e normalização/validação de CPF | ResponsibleGamingModule |
| `:feature:results:data` | Faixas/validação de concursos, roteamento de modalidade e mapeamentos de sorteios/prêmios | ResultsModule |
| `:feature:retail:data` | Normalização de busca/favoritos, payloads de resgate, erros/registro e comprovantes | RetailModule |
| `:feature:support:data` | Defaults/nulls, flatten de perguntas e conversão/trim de modelos remotos | SupportModule |
| `:feature:tickets:data` | Roteamento backend, codificação RSA/QR, recuperação de comprovantes e mapeamentos | TicketsModule |

## Transport: nove módulos

| Módulo | Responsabilidade própria | Consumidor efetivo (exemplos) |
|---|---|---|
| `:feature:account:transport` | DTOs de perfil, sessão, verificação e push com campos serializados/redação de logs | LoteriasApi; Account Data |
| `:feature:betting:transport` | DTOs de parâmetros, submissão, favoritos, rápidas e esportes | LoteriasApi; BettingParametersMapper |
| `:feature:cart:transport` | DTOs de carrinho/favoritos e schema de upload legado | LoteriasApi; payload/upload mappers |
| `:feature:marketplace:transport` | DTOs de página/detalhe/bolões e campos alternativos de cidade/valores | LoteriasApi; RemoteMarketplaceRepository |
| `:feature:retail:transport` | Payload JSON de resgate distinto do payload string de registro Mercado Pago | BackendApi; operações de Retail Data |
| `:feature:responsible-gaming:transport` | DTOs de questionário, respostas, suspensão e repasses | LoteriasApi; repository de jogo responsável |
| `:feature:results:transport` | DTOs de concursos, sorteios, premiação, esportes e ganhadores regionais | LoteriasApi; RemoteResultsRepository |
| `:feature:support:transport` | Campos wire de seções, perguntas e termos | LoteriasApi; RemoteSupportRepository |
| `:feature:payment:transport` | Envelopes distintos de compra, processamento, erro e horário de servidor | PurchaseBackendApi; ServerTimeAdapterFactory |

## Candidatos pequenos e contratos preservados

- Account API tem dois arquivos, mas sua identidade valida o ownerKey, mantém igualdade referencial e oculta informações nos logs. É usada por diferentes camadas/features.
- Cart API e Marketplace API contêm modelos e regras compartilhados. Mover seus fontes para os respectivos Domain introduziria ciclos já evitados por esses contratos mínimos: Account/Cart/Betting e Core Domain/Marketplace/Betting.
- Home Domain mantém ordenação e alteração conjunta de preferências; Home Data conserva arquivo `home_v2_layout`, chave `modality_order`, contexto de aplicação e `apply`.
- Onboarding Data conserva o armazenamento por arquivo/chave legado, o default de introdução e a persistência de tutoriais. O Domain é compartilhado com a lógica de entrada de Betting.
- Support Domain inclui repository/modelos/contratos consumidos por Data e apresentação, além de tratamento de erro via `appResultOf`. Não é só uma reexportação. Support Data faz flatten, defaults, trim e conversão de DTOs.
- Payment Transport e Retail Transport definem envelopes diferentes de sucesso/erro/registro; campos de payload com tipos distintos não são aliases intercambiáveis.
- Existem projeções internas de APIs que encaminham chamadas dentro de módulos Data com outras responsabilidades. Sua existência não torna o módulo inteiro uma fachada. O alias interno de linha do carrinho é um nome para um mapa usado na implementação; não substitui um módulo.

## Testes e cobertura

Nenhuma fonte de produção, teste, registro de módulo, dependência ou configuração de cobertura foi alterada nesta revisão. Os 2.242 hashes de fontes/configurações foram comparados com a árvore da matriz completa anterior; não há adições, remoções ou diferenças. As 23 configurações de cobertura permanecem byte a byte idênticas.

Verificações executadas nesta rodada:

- `python3 -m unittest scripts.test_app_architecture scripts.test_feature_architecture scripts.test_feature_modules`: 68 testes aprovados.
- `./gradlew verifyCleanArchitecture verifyFeatureModules verifyComposeMigration verifyKotlinFormatting --offline --continue --console=plain`: BUILD SUCCESSFUL em 48s; 76 tarefas (72 executadas e quatro atualizadas).

A matriz completa anterior (`modernCheck releaseCheck`) aprovou esta mesma árvore de fontes/configurações: 2.089 unitários, 196 execuções instrumentadas aprovadas e três casos ignorados por perfil físico de tela, além dos releases PRD/TQS/Automação. Não foi repetida nesta auditoria sem mudanças de código. A ausência de testes locais em alguns módulos não significa falta de cobertura: Core Domain, Core Network, Data, apresentação e APP agregam classes, fontes e execução de testes compartilhados. Nenhuma dessas entradas ou dos limiares foi retirada.

Evidências: `build/verification/feature-module-facades-20261020/` contém inventário individual dos 52 módulos com fontes/consumidores Gradle, decisões, revisões, logs e comparação de hashes. A matriz anterior está em `build/verification/core-removals-full-matrix-20261020/`. Apenas este relatório e o registro de validação foram acrescentados/atualizados. Não houve commit, push, alteração de contratos, APK de referência ou redução de limiares.
