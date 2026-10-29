# Auditoria de modernização — 18/10/2026

Autoridade: SPEC.md fornecida pelo usuário. Estado inicial capturado em build/verification/modernization-spec-20261018/baseline-hashes.json. Esta auditoria distingue migração técnica, refatoração de responsabilidades e homologação externa. Não substitui evidências históricas por afirmações de conclusão.

## Atualização — estrutura APP → features → CORE (19/10/2026)

A auditoria abaixo descreve a fotografia inicial e permanece como histórico. A direção arquitetural atual atende ao diagrama solicitado: app contém Application/MainActivity, navegação raiz e composição; Account, Onboarding, Notifications, Home e as demais features mantêm suas implementações; Network, DataStore, Security, Queue, Auth, Presentation e outros serviços compartilhados pertencem a módulos CORE. Foram realocados 61 arquivos (56 do app para módulos donos e cinco para pacotes de navegação/composição dentro do app). O app tem 62 fontes Kotlin principais e zero ViewModels de negócio. As fachadas públicas necessárias estão inventariadas por FQN e caminho.

A verificação inclui políticas de propriedade e direção de dependências em main/debug/release. Vinte testes de política e doze fixtures reais do Gradle foram executados; cada violação foi rejeitada, os arquivos originais foram restaurados byte a byte e os gates positivos passaram. A equivalência compilada de 1.027 superfícies públicas não apresenta remoções de classe, campo, método, superclasse ou interface. Os 37 arquivos de ViewModel anteriores às features permaneceram byte idênticos. O registro e a matriz final desta continuação estão em `build/verification/app-mvvm-20261019` e na seção final de `MIGRATION_VALIDATION.md`.

A revisão deve considerar a SPEC como autoridade, mas separar implementação local validada de homologação externa. Os casos visuais físicos, credenciais/dispositivo de homologação, provedores privados e assinatura de distribuição continuam pendentes conforme a indisponibilidade já confirmada pelo usuário.

## Inventário real

- 26 módulos Gradle: app, 11 core e14 features, listados em settings.gradle.
- 80 destinos Compose em docs/COMPOSE_ROUTES.txt; 102 entradas de UI legada (incluem diálogos/componentes) em docs/LEGACY_UI_INVENTORY.tsv. Não são102 telas independentes.
- 629 arquivos Kotlin de produção; 1015 Kotlin incluindo testes no baseline.
- Nenhum Java de produção e nenhum XML de layout de tela em app/core/feature. Três Java restantes são testes de filtros de marketplace em core/domain; serão migrados preservando casos/assertivas.
- Vetores/drawables/manifestos XML e recursos de terceiros não são layouts legados e permanecem necessários.
- Features: account, betting, cart, home, marketplace, marketplace-filter, notifications, onboarding, payment, responsible-gaming, results, retail, support, tickets.

## Ferramentas e dependências

Configuração observada: Java/JVM17, Gradle9.4.1, AGP9.2.1, Kotlin/Compose compiler2.4.10, Compose BOM2026.08.00, compileSdk/targetSdk37, minSdk24. Hilt2.60.1 com KSP; coroutines/StateFlow, Retrofit/OkHttp, AppAuth, CameraX/MLKit, SQLite/preferences compatíveis com legado. Dependências majoritariamente em gradle/libs.versions.toml; literais residuais de testes serão centralizados sem alterar versões.

Não há evidência exigindo troca indiscriminada de bibliotecas ou framework DI. Compatibilidade será comprovada por modernCheck/releaseCheck da árvore, não por presumir a versão mais recente.

## Arquitetura existente

:feature:* contém presentation e depende de Domain; :core:domain é JVM sem Android/Compose/Retrofit; :core:data implementa repositories e mappers sobre data sources; network/auth/preferences/queue isolam plataformas. :app é composition root Hilt e navegação. Não será criada estrutura vazia nem módulo por tela.

verifyCleanArchitecture verifica dependências/package/Hilt; verifyComposeMigration preserva exceção debug NetworkInspectorActivity e proíbe Java/layouts/fragments de produção; demais gates cobrem identidade/flavors, 99 operações backend, inventário UI, CaixaDS, FilaBRA, Topaz e rede. Gates e limites não serão enfraquecidos.

## Problemas e ordem de refatoração

| Frente | Arquivos do pedido | Problema | Complexidade | Verificação |
|---|---|---|---|---|
| UI | BettingRoute, PaymentCardFlowRoute, LocationGateRoute, PersonalDataRoute, TicketScannerRoute, HomeRoute | Coordenação, composição, plataforma e helpers no mesmo arquivo | média; Home/câmera alta | testes originais+novos de coordenação, corpos recompostos, UI em emulador |
| Estado | BettingGameViewModel, TicketsViewModel, CartViewModel | Seleção/favoritos/restauração, históricos/Pix, checkout/reserva excessivamente concentrados | alta | testes de estado/cancelamento/ordem/stale response, cobertura incluindo colaboradores |
| Data | RemoteTicketsRepository, RemotePaymentRepository | Adaptadores, mapeamento, roteamento/recibos, tokenização/resposta de compra misturados | alta | contratos de data sources, DTO/JSON/cancelamento/cache/HTTP, cobertura ampliada |
| Design system | CaixaDSLegacyComponents |1007 linhas com ícones, controles, scaffold, barras, diálogos e campos | média | igualdade de declarações/APIs/defaults, UI DS/previews |
| Consolidação | três Java de teste, catalog de versões, documentação | linguagem/documentação/dependências residuais | baixa | mesma contagem de testes, matriz completa, gates |

Mudanças independentes podem ser preparadas em paralelo, com builds/testes centralizados. Cada frente deve permanecer compilável em seu checkpoint.

## Riscos e preservação

Autenticação/SSO, headers, certificados, pinning, Topaz/FilaBRA, serialização, chaves SQLite/preferences, 99 operações e fluxos de compra/resgate não serão redesenhados. Mocks só em testes/previews/ambiente explicitamente habilitado; flags padrão de produção preservadas. Cancelamento nunca deve virar sucesso ou erro comum.

As três ViewModels já usam use cases reais e StateFlow privado; sucesso/vazio/erro derivam dos estados atuais. Fallbacks opcionais existentes precisam ser confrontados com wiring Hilt: auditoria encontrou dependências reais nas rotas que expõem favoritos/compra, sem evidência de execução fictícia em produção. Não remover compatibilidade de testes sem demonstrar alcance real.

Documentos históricos registram paridade visual parcial e pendências de transferência local/pós-login/serviços privados. Esses registros não podem ser apagados por uma extração de arquivos. Conferir código e testes atuais e documentar diferenças restantes em MIGRATION_PROGRESS.md. A inspeção atual de RemotePostLoginAccountRepository e PostLoginCartTransferTest confirma que envio, confirmação, limpeza e revisão por identidade já estão implementados e testados; as pendências de implementação em registros antigos são históricas. A homologação externa sem credenciais/dados/callbacks do provedor não deve ser declarada concluída com testes locais.

## Baseline e critérios

Baseline executada: `./gradlew modernCheck releaseCheck --offline --continue --console=plain`. Logs e XMLs foram preservados. Novas caracterizações passaram contra os originais antes da refatoração. A matriz final com o AVD dedicado passou (`final-matrix-3.log`); a instrumentação complementar e as condições de execução estão detalhadas em MIGRATION_VALIDATION.md.

Aceite de cada frente: APIs públicas/contratos preservados, owners/chaves/ordem de efeitos mantidos, testes de sucesso/falha/borda/cancelamento, compilação/check/cobertura/gates aprovados e relatório da feature atualizado. O denominador de cobertura incluirá classes extraídas, nunca será reduzido para fazer gate passar.

## Resultados da inspeção e decisões

A matriz inicial compilou os três releases minificados, lint, JVM e gates; falhou somente em cinco tarefas de instrumentação por ausência de dispositivo. Foi iniciado um AVD dedicado para executar esses checks. A baseline de caracterização dos módulos envolvidos passou com782testes JVM, mais2testes de coordenação do tutorial e2do cartão contra os originais.

A UI do Design System original passou17testes após corrigir uma assertiva geométrica incompatível com arredondamento em densidade fracionária:14 dp são39px a450dpi. A nova assertiva exige39px exatos, sem tolerância; tokens e componente de produção não foram alterados para satisfazer o teste.

A auditoria encontrou11fluxos de evento com MutableSharedFlow sem replay e consumidor único. Um teste reproduziu NavigateBack perdido com coletor ausente. A correção usa uma fila limitada/cancelável em core:common (sem Android); o estado StateFlow continua separado. A fila existe apenas na vida da ViewModel, não representa persistência após morte de processo ou confirmação transacional de efeitos externos.

MinSdk não é uniforme:app e features que aplicam compose-feature usam24; marketplace-filter e design-system preservam23. Esse limite foi respeitado ao localizar a fila em common.

Foram conferidos21literais de dependências em17build files:catalogação mantém coordenadas, versões e scopes. Foram adicionados somente preview Compose nos módulos de feature que já usam a mesma BOM e um gate de whitespace para .editorconfig. Android Lint e gates arquiteturais existentes fazem a análise estática; nenhum plugin Detekt/ktlint foi introduzido apenas para duplicar infraestrutura durante uma refatoração.

Segurança, rede e persistência continuam sob os gates existentes:nenhuma mudança em manifests, pinning, OIDC, Topaz, FilaBRA, DTOs de integração, chaves de preferências ou identidade do app faz parte desta execução. Nenhum Worker, Service ou Receiver novo foi necessário:as alterações de concorrência são limitadas aos escopos das ViewModels existentes. O relatório final registra os resultados da matriz completa e os limites de homologação externa.

A inspeção de dependências de Views encontrou usos concretos: FilaBRA referencia ConstraintLayout e AppCompat no AAR; o diálogo nativo de opções usa tema AppCompat; o estilo de simulação e os oráculos de geometria usam Material Components. Essas bibliotecas não foram removidas apenas porque as telas próprias são Compose. As contagens de referências dos AARs estão em `platform-dependency-audit.json`. Não foi demonstrada obsolescência de uma dependência resolvida que justificasse sua remoção nesta rodada.

A auditoria final de 681 fontes de produção não encontrou imports externos em Domain, acesso de features a Data/Network ou SharedPreferences, owners Android nas ViewModels, GlobalScope, threads manuais de negócio nas ViewModels ou NotImplementedError. A checagem textual complementa a compilação e os gates; não substitui uma auditoria de segurança independente. Noventa e quatro arquivos protegidos de rede, autenticação, persistência e plataforma mantêm o mesmo hash da baseline (`protected-contracts-equivalence.json`).

A primeira matriz após a extração identificou 83% de cobertura em Tickets, abaixo do mínimo preservado de 95%. Os padrões adicionados também alcançaram `PurchaseFiltersKt` e as classes Actions dos filtros. A correção de validação mantém esse denominador: testes de interação dos três filtros e cobertura Android entram junto à cobertura JVM. O gate passa a exigir execução conectada e dados de ambas as fontes; não basta uma suíte JVM parcial. A execução focada dos 3 novos testes passou e elevou a cobertura para 737/756 linhas (97,49%), sem retirar classes ou reduzir o limite. A matriz completa passou com a suíte Android de Tickets como dependência obrigatória: 738/756 linhas (97,62%).


## Auditoria complementar — sete responsabilidades (18/10/2026)

Pedido posterior do usuário: refatorar FilaBraGate, AppAuthAuthenticationGateway, TicketsPayloadMapper, BettingGameSelection, FavoriteCartsViewModel, PaymentViewModel e RemoteCartRepository. A baseline desta rodada tem 2856 arquivos existentes e snapshots dos sete alvos em build/verification/seven-responsibilities-20261018/. O checkout contém trabalho anterior autorizado; a comparação usa estes snapshots, não HEAD.

- Data: parsing recursivo/filtros/esportes misturados em Tickets; transporte/validação/mapeamento no carrinho. Manter extensões de entrada, prioridades, tipos, escala, ordem, colunas e falhas.
- Betting: regras por modalidade/quantidades/reset misturadas; avisos compartilham duas flags que devem permanecer únicas durante a vida do colaborador.
- Carrinhos favoritos e pagamento: manter Estado/StateFlow, escopo, cache/IDs, guardas de sessão, retornos Job/Unit/Boolean, restauração Pix e protocolo de compra. Separar por operação sem novo owner ou scope.
- Fila: inicialização/segurança, SDK, destinos, WebView/broadcasts e componentes concentrados. Preservar composição, efeitos/chaves, tempo mínimo, retry, flags e ordem de encerramento/disposal.
- Auth: fronteira crítica; quatro contratos, registries/stores e monitor único compartilhados. Mover corpos conservando sincronização da fachada, gateway→store, nonce/PKCE/identidade, cancelamento por ticket e persistência/rollback.

Ordem local: baseline → Data/Betting → ViewModels → Fila/Auth → integração/revisão/documentação. A baseline JVM executou 803 casos sem falhas; Android original executou 110 casos nos três módulos e uma nova caracterização da fila passou antes de sua extração. Dois testes novos de sincronização AppAuth também passaram no original. Evidências e limites finais são registrados em MIGRATION_VALIDATION.md; estes números não representam nova execução da matriz global histórica. Sem redesenho de segurança, novas dependências, commits ou pushes.

## Continuação — dez arquivos por responsabilidade (18/10/2026)

Dez arquivos concentravam estado/carregamento/autenticação, composição de resgate, navegação de apostas e infraestrutura de rede. Inventário inicial: 2.902 arquivos próprios protegidos por hash. Mantidas 80 rotas Compose e nenhum Java próprio; esta rodada não migra novas telas.

| Fachada/arquivo original | Antes | Depois |
|---|---:|---:|
| `RapidConfigurationViewModel.kt` | 245 | 78 |
| `LoginViewModel.kt` | 227 | 65 |
| `ResponsibleGamingViewModel.kt` | 226 | 85 |
| `NotificationsViewModel.kt` | 215 | 71 |
| `TicketsNavigationGraph.kt` | 355 | 32 |
| `PrizeOptionsScreen.kt` | 325 | 87 |
| `PrizePixScreen.kt` | 294 | 110 |
| `NetworkClientFactory.kt` | 247 | 144 |
| `ApiModels.kt` | 737 | 10 |
| `MockBackendInterceptor.kt` | 744 | 54 |

Foram criados 75 arquivos Kotlin de produção, com 34 novas classes/objetos internos; 86 DTOs apenas mudaram de arquivo mantendo as declarações literalmente. Quatro filtros JaCoCo receberam somente inclusões, preservando mínimos/inputs anteriores. Não há atualização de dependências, recursos, manifestos ou flags.

882 testes JVM aprovados (baseline 877; cinco novas caracterizações aprovadas também com as fachadas originais); 65 testes Android aprovados, um teste condicional ignorado, zero falhas/erros; 352 comparações de fonte/API aprovadas. Revisão independente sem achados. Checks/cobertura/lint dos sete módulos envolvidos, lint PrdDebug do app, dez gates e APK PrdDebug aprovados.

Evidências: `build/verification/ten-responsibilities-20261018/`. Serviços privados, hardware biométrico e paridade global continuam sujeitos à homologação externa; nenhuma nova pendência de implementação identificada no escopo.

## Sete ajustes concretos de conformidade — 19/10/2026

Escopo concluído: os sete arquivos identificados na auditoria anterior receberam correções de I/O, cancelamento, lifecycle, geração de QR e separação das regras do carrinho visitante. A gravação do PDF ocorria no callback principal; a geração do QR ocorria na composição; duas capturas de exceções absorviam cancelamento; duas rotas coletavam estado sem lifecycle; o repositório SQLite reunia regras e persistência.

O inventário permanece com 80 destinos Compose e nenhum Java próprio em app/core/feature. Foram introduzidos sete arquivos Kotlin de produção e seis classes/objetos de produção, além dos testes. Não houve migração de novas telas, alteração de dependências, manifestos, recursos, contratos HTTP ou segurança. Evidências: build/verification/seven-compliance-20261018/.

A lista de refinamentos estruturais adicionais é um backlog de manutenção; tamanho de arquivo não constitui violação da SPEC. A decisão sobre mensagens locais legadas e a homologação global das integrações continuam fora desta rodada.

## Links externos e recuperação de comprovantes — 19/10/2026

Auditoria da rodada: ChangeLoginScreen ignorava falhas e anunciava abertura mesmo sem navegador; o atalho Disque180 deixava exceções de abertura escapar; PrizeReceiptManager mantinha bytes, destino e cache somente na composição, perdendo a entrega durante recriação. A recuperação também precisava de ações explícitas para falha de download, gravação e visualizador indisponível, inclusive quando a nova ViewModel retorna a OPTIONS.

As correções preservam assinaturas públicas, StateFlows, URLs, nomes/bytes dos PDFs, MIME e permissões. Domain/Data, protocolos HTTP, autenticação, pinning e demais ViewModels/UiStates permanecem idênticos à baseline desta rodada. Um novo colaborador de produção, PrizeReceiptDelivery, retém a operação no escopo da ViewModel. Não houve migração de novas telas: permanecem 80 destinos Compose, zero Java próprio de produção e 872 arquivos Kotlin de produção contando main/debug/release/prd/tqs/automacao. Arquivos não equivalem a classes; a rodada acrescenta uma classe colaboradora e seu tipo interno Completion.

Recursos locais foram inspecionados sem obter credenciais: não havia dispositivo físico conectado, sessão CAIXA/SSO no navegador ou acessos autorizados disponíveis. O usuário confirmou que conta e aparelho ainda não estão disponíveis. Cinco testes visuais foram executados sem alterar seus oráculos: três aprovados e dois reprovados no perfil Pixel emulado; capturas demonstram deslocamento superior de 80px em relação ao Samsung de referência. A homologação privada e a validação dessas coordenadas no perfil físico continuam pendentes. Resultados globais, comandos e limites atuais estão na última seção de MIGRATION_VALIDATION.md.

## Central de mensagens: origem e separação — 19/10/2026

Comparação direta com o backup Java de 03/09/2026: `MensagensPushActivity.onCreate` chama `MensagemPushModel.getAllMensagensMock()`. Histórico, leitura, exclusão e switches da central são locais; não há contrato remoto completo nessa implementação. O usuário confirmou que o legado é a referência desta etapa. As cinco amostras e seis switches existentes foram preservados, incluindo os IDs locais únicos já adotados pela migração.

O FCM atual somente publica banners do sistema. O GET de não visualizadas alimenta o contador; o PUT de visualização pertence ao popup autenticado. POST de registro de push não é preferência da central. `NotificacaoDTO` não fornece categoria nem data de envio para preencher a central; não se infere esse mapeamento de `tipo` da resposta. Nenhuma API foi criada e nenhum ID sintético foi enviado à rede.

Escopo: `LocalNotificationRepository` coordena colaboradores internos de mensagens e preferências; o catálogo legado está isolado e identificado. Hilt separa caixa local, contador e leitura autenticada. APIs públicas e StateFlows permanecem iguais. A preservação autorizada das amostras não comprova uma central alimentada por dados reais nem conformidade integral com SPEC3.3. Evidências e limites atuais em MIGRATION_VALIDATION.md e docs/NOTIFICATION_INBOX_PARITY_AUDIT.md.

## Checkout do carrinho: separação de responsabilidades — 19/10/2026

CartCheckoutCoordinator tinha 241 linhas com entrada de pagamento, conta/atualização de reservas, localização, consentimento/permissões, compra recente, validação e apresentação de falhas. A inspeção confirmou a ordem conta → atualização de cotas expiradas → localização → compra recente → login/validação, inclusive para carrinho vazio e falha da atualização opcional.

A fachada mantém 102 linhas e delega a seis colaboradores internos: CartCheckoutLocation, CartCheckoutPermissions, CartCheckoutRecentPurchase, CartCheckoutValidation, CartCheckoutNotices e CartCheckoutRedirects. São seis arquivos/classes Kotlin novos, sem novos contratos de Domain, escopos ou StateFlows. CartViewModel e CartUiState mantêm seus fontes e APIs.

O try/catch/finally da localização também envolvia consultas de compra recente e validação; a extração conserva essa fronteira e o caminho diferente de continuação direta após confirmação. Duas caracterizações novas passaram antes e depois: gravação suspensa da permissão e cancelamento suspenso nas duas etapas seguintes, com limpeza e nova tentativa. Dezoito corpos movidos foram comparados literalmente; assinaturas declaradas da fachada, ViewModel e UiState foram comparadas com javap, excluindo bridges sintéticas access$ do compilador. A revisão independente não encontrou achados acionáveis. Resultados finais e limites de validação em MIGRATION_VALIDATION.md.

## Histórico de apostas e compras — 19/10/2026

TicketsHistoryCoordinator reunia em 246 linhas inicialização/configuração de apostas, modalidades, meses de compras, filtros, paginação e retries. A inspeção identificou verificações de sessão antes/depois de catálogos/páginas, leituras de filtros dentro das coroutines e uso do callback fail da própria ViewModel. A ordem e os pontos de leitura/publicação foram preservados.

A fachada mantém 72 linhas e o mesmo construtor/métodos. Seis colaboradores internos separam apostas, compras, paginação, filtros de apostas, filtros de compras e recuperação. Todos recebem o mesmo estado e, quando aplicável, o mesmo scope/dispatchers e callback de falha. ViewModel/UiState e seus StateFlows mantêm os fontes; nenhum contrato de rede, query ou domínio foi alterado.

Três caracterizações adicionais passaram no original e após a extração: expiração de sessão com append suspenso, retry usando catálogos já carregados e filtros atuais, e leitura dos filtros quando a coroutine enfileirada inicia. Foram comparados 29 métodos movidos, com apenas qualificação do novo dono e visibilidade interna necessária. As assinaturas declaradas do coordenador/ViewModel/UiState são iguais. O build de Tickets recebeu somente seis includes de cobertura; os padrões anteriores, entradas Android/JVM e mínimo 0.95 foram mantidos. Resultados finais e limites em MIGRATION_VALIDATION.md.

## Composição da Home clássica — 19/10/2026

HomeLegacyContent reunia em 302 linhas composição, barra superior, carrossel, cartões/ações, atalhos, campanha e resumo do carrinho. A extração conserva 103 linhas no arquivo original e distribui os trechos visuais em seis arquivos com sete funções Composable internas e sem estado próprio. Nenhuma classe de produção foi introduzida; HomeLegacyPresentation e HomeLegacyActions permanecem no mesmo arquivo e com as mesmas assinaturas.

O dono do pager, key(size), seleção, LaunchedEffect(requestedModalityId), constraints e cálculos de largura continua sendo HomeLegacyContent. Foram preservados os BoxScopes, a ordem dos irmãos, modificadores, dimensões, insets, animações e callbacks. HomeRoute/HomeScreen, LotteryCard existente, mapeadores, ViewModels e StateFlows não foram alterados. Não houve nova migração de tela, dependência, Java ou alteração de recurso/manifesto. Evidências e limites na seção desta rodada em MIGRATION_VALIDATION.md.

## Auditoria de ownership e MVVM de todas as features —19/10/2026

Baseline:14features com425arquivos principais em pacotes planos;59arquivos de Domain,80de Data remoto e17de preferências Android. Inspeção independente confirmou dependências cruzadas de domínio que impedem módulos Gradle Domain isolados sem redesenho de contratos. Identificados acessos diretos a preferências em HomeV2Presentation/ShakeMenuRow e dependência de gateways/Intent em Login/Logout ViewModels. Estes pontos integram a refatoração autorizada, além da reorganização dos pacotes.

O gate antigo aceitava um import Android em domain realocado sob feature/src/domain. Fixture negativa reproduziu essa aceitação antes da ampliação; agora a mesma fixture é rejeitada e nove testes da política verificam entradas permitidas/proibidas. A análise deve usar source sets reais, incluindo camadas e host, preservando também todos os checks anteriores e os mínimos de cobertura. Evidências próprias:build/verification/feature-mvvm-20261019/;1602snapshots capturados antes das alterações desta rodada. Implementação concluída; resultados e limites na seção abaixo e em MIGRATION_VALIDATION.md.

## MVVM/Clean por feature — 19/10/2026

A inspeção desta rodada usa o checkout autorizado anterior, incluindo alterações não commitadas. O escopo contém 14 features, 631 movimentos de arquivos e as sete extrações pendentes explicitamente listadas. Foram separadas 19 famílias de estados/efeitos e reclassificados 65 auxiliares conforme seu comportamento real, além de sete divisões de arquivos mistos.

A prova de movimento registra 600 corpos literais iguais, normalizando somente imports/FQNs. Os outros 31 são fronteiras de plataforma/persistência ou extrações explicitamente descritas, com snapshots, testes e revisão própria. O lote de cinco fachadas não Home tem 42 trechos equivalentes e cinco APIs públicas compiladas idênticas. Home tem 14 verificações estáticas de extração/propriedade e comparação exata de 17 PNGs mais medidas antes/depois. Não foram criados novos nós de layout para essas extrações.

As dependências reais de ViewModels em Android/autenticação concreta/repositories foram removidas; as rotas conservam Intents e lançamentos de plataforma. A revisão do carrinho local mantém a consulta sem reenvio e a exclusão condicionada ao recibo/identidade. Preferências da Home saíram dos componentes para casos de uso e ViewModel, com IO ordenado e cancelamento de publicação tardia. Os StateFlows existentes conservam seus donos; o estado de preferências é novo e independente.

Limite arquitetural explícito: as raízes Domain/Data/Platform são de propriedade das features, mas compiladas pelos módulos agregadores existentes. Não se afirma isolamento de binários Domain por feature; os ciclos de contratos preexistentes foram preservados. Home host é de propriedade física da feature e compilada por app. Não há dependência do AAR Home para app, nem compilação duplicada desses fontes.

Foram conferidos 251 recursos/configurações protegidos por hash, além de 23 configurações de cobertura sem remoção de padrões ou redução dos mínimos. As três fixtures de host e a fixture Domain verificam rejeição pelo Gradle real; nove testes da política passam. As revisões independentes do lote e do conjunto não identificaram achados acionáveis. Resultados globais, execução adicional e limites ficam em MIGRATION_VALIDATION.md. Evidências: build/verification/feature-mvvm-20261019/.

## Rodada de módulos independentes por feature — 19/10/2026

Escopo solicitado: arquitetura por feature com MVVM/Clean/SOLID, começando pelos ciclos Account/Cart/Betting e pela compilação agregada. Baseline próprio em `build/verification/feature-modules-20261019/baseline`; checkout anterior preservado, sem reset/clean/stage/commit/push.

| Achado inicial | Correção implementada | Evidência local |
|---|---|---|
| Ciclos reais de Domain entre Account/Cart/Betting e configuração de Marketplace | Contratos mínimos CartAPI/AccountAPI/MarketplaceAPI; Domain compilado independentemente | Grafo configurado acíclico e fixture negativa de ciclo |
| Core compilava Domain/Data/Platform de features por source roots externos | 16 módulos Domain/API e 13 Data com fontes próprios; sem agregação de compilação | `verifyFeatureModules`, rejeição de raiz externa e dono duplicado |
| Composição Home era compilada no app | Home AAR própria, flags/runtime e recursos equivalentes, fachada pública de HomeScreen | 13 testes antes/depois; 18 arquivos de captura byte a byte iguais no primeiro checkpoint |
| Contratos e adapter Retrofit concentravam features em Core/App | Nove módulos de DTOs, 18 contratos HTTP de feature e oito adapters reais; fachada pública de integração | Teste de schema HTTP, teste de troca do cliente durante avaliação do argumento, cobertura Data/Network |
| Bindings e grafos de features concentrados no app | 158 bindings migrados, grafos por feature, portas tipadas, estado compartilhado de navegação separado | Compilação, JVM e caracterização Home/links/navegação aprovados; resultados físicos separados |
| Persistência e auxiliares repetidos sem fronteiras comuns | CoreDatabase/CoreDatastore/CoreTesting reais e cinco plugins de convenção | 28 testes nativos de persistência aprovados; nenhuma migração de esquema/chaves/tecnologia |
| Guards dependiam de raízes antigas e grafo textual parcial | Raízes atuais e grafo configurado real, inclusive convenções/variantes | Dez violações deliberadas rejeitadas e restauração aprovada |

Compatibilidade deliberada: nomes JVM públicos antigos, aliases categorizados, fachadas de construtores/fábricas e contratos HTTP antigos continuam disponíveis. Isso preserva os consumidores existentes e não reintroduz compilação agregada. CoreDomain usa pequenos contratos de identidade/filtros; CoreNavigation usa contratos Domain. Não há dependência Domain de Android/Data/Presentation.

A comparação de bytecode encontrou zero quebras em 1.027 classes/fachadas públicas protegidas; 37/37 declarações de ViewModel e StateFlows e 392/392 declarações de regras Domain foram preservadas. modernCheck e releaseCheck dos três flavors aprovados. A primeira matriz revelou placeholders de teste/Base64 e expansão acidental de cobertura Cart, corrigidos com os contratos/limites anteriores preservados e revisão independente. Os cinco visuais executados tiveram três aprovações e duas divergências físicas anteriores; a repetição da animação produziu oito quadros idênticos ao baseline. Resultados globais/visuais na última seção de MIGRATION_VALIDATION.md. Permanecem os limites já confirmados de conta/aparelho biométrico, integrações privadas, assinatura e perfil físico equivalente; não são substituídos por mocks.

## Fechamento da arquitetura APP — 20/10/2026

A estrutura APP → features → CORE foi conferida contra o grafo de módulos: 68 módulos declarados, 62 fontes Kotlin de produção no APP e nenhum ViewModel de feature no host. `modernCheck`/`releaseCheck` e os gates de arquitetura/formatação passaram. Testes instrumentados de push foram adicionados para conservar a cobertura Data acima do mínimo; nenhum gate foi reduzido. Três dos cinco visuais condicionais passaram, e os dois desvios de 80 px permanecem em aberto até validação no perfil físico.

O relatório independente da arquitetura não encontrou achados acionáveis; os ajustes finais de validação/teste posteriores ao corte foram verificados pela matriz global. OAuth/biometria/SSO/fila/provedores privados, dispositivo físico e assinatura permanecem fora da homologação local porque os recursos não estão disponíveis. `MIGRATION_VALIDATION.md` contém comandos, saídas e evidências completas. Nenhum commit, push ou publicação.
