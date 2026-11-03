# Implementação da entrega híbrida de todas as features

**Objetivo:** disponibilizar conteúdo real de 15 capacidades em módulos Android de entrega, controlados pelo JSON `app_modules`, com continuidade e recuperação de dados preservadas.

**Desenho aprovado:** [entrega de todas as features](../specs/2026-11-02-all-feature-delivery-design.md), alternativa A. A aprovação do usuário abrange toda a implementação; não pedir novamente a escolha de entrega.

**Arquitetura:** host, contratos, estado, ViewModels/Redux, Domain/Data/DI e efeitos de plataforma permanecem na base. Renderizadores e recursos exclusivos saem para os splits. Account, Home e Responsible Gaming são install-time; demais UIs são on-demand. Core e caminhos obrigatórios ficam disponíveis.

**Stack:** AGP 8.13.0, Kotlin 2.2.21, JDK/JVM 21, SDK 36, Compose/ReduxKotlin/Hilt, Play Feature Delivery 2.1.0, Firebase Remote Config. Preservar flavors e todas as políticas existentes.

## Restrições e revisão

- Não alterar headers HTTP/visuais, baselines nem limiar JaCoCo global >95%; nenhum código manual excluído.
- Preservar o piloto não commitado e seus 45 hashes protegidos. Não executar stage/commit/push ou publicação Firebase/Play sem pedido específico.
- Não efetivar pagamentos/apostas/resgates reais, nem apagar dados ou instalar variantes extras no Samsung.
- Toda capacidade precisa de renderizador real extraído: chave JSON, módulo vazio e wrapper que mantém toda UI base não contam como migração.
- Nova escrita após off é bloqueada; escritas já iniciadas e suas leituras de conclusão mantêm identidade, idempotência e proprietário. Não persistir CVV/senha para restaurar uma UI.
- Testar publicação inválida seguida de reinício, navegação cruzada, Home/Results, Marketplace/Filter, Retail/Payment/Tickets e callbacks atrasados de identidade anterior.

## Unidades de implementação

### 1. Registro e configuração durável

- [x] Escrever regressões para 15 chaves, defaults novos=true/FAQ=false, JSON legado somente FAQ, off explícito, chave desconhecida, schema inválido e cache válido após reinício.
- [x] Observar RED comportamental com contratos/stubs compiláveis; implementar modelos `AppModule`/`AppModulesConfiguration`, parser e cache em Data, mantendo SDK/defaults compartilhados.
- [x] Manter uma decisão local por capacidade; nome de classe, URL, versão e modo de entrega não vêm do Firebase.
- [x] Expor leitura/refresh e política de admissão por contratos de Domain; preservar stream Redux somente Presentation.
- [x] Validar GREEN e contratos de arquitetura antes da integração de novas UIs.

### 2. Entrega e apresentação compartilhadas

- [x] Extrair o instalador do FAQ para adaptador reutilizável com módulo conhecido, sessões/listeners correlacionados, cancelamento e reentrada idempotentes. Regressões para dois módulos simultâneos e estados cruzados.
- [x] Criar contrato marcador de entrada de UI e carregamento tipado; ambiente Compose da aplicação fornece somente configuração/entrega/carregamento de UI, sem localizar serviços de negócio.
- [x] Generalizar ViewModel/Redux e UI DS de preparação, erro, Empty State, confirmação e retry. Nenhum header protegido editado.
- [x] Introduzir admissão/continuidade local por jornada e identidade. Criar proprietários base fora da substituição visual; flag não recria ViewModel, cancela compra nem repete efeito.
- [x] Migrar FAQ para o mecanismo compartilhado e manter as regressões do piloto, inclusive aba Termos e conclusão de instalação tardia.


### 3. Empacotamento e ferramentas

- [x] Registrar projetos/splits com nomes únicos e modo híbrido, títulos/keep exatos e entradas concretas protegidas por R8.
- [x] Preparar APK universal real de recursos uma vez por base/variante. Compartilhar runner/SPI de compatibilidade nativa somente em testes, mantendo classes/cobertura no proprietário.
- [x] Oferecer classes reais dos splits aos testes de integração da base via artefatos de teste, sem dependência de código produção base→split ou código manual agregado na base.
- [x] Adaptar runners/hosts/configuração dos testes de UI, preservando corpos/asserts e execução instrumental original.
- [x] Generalizar manifestos/JSON/IDs/Lint/grafo com fixtures positivas e negativas. Preservar regra estrita dynamic→base e variantes de cobertura.

### 4. Famílias de UI

Para cada linha: fixar comportamento por testes existentes e regressões novas, criar contratos tipados na base, quebrar imports concretos cruzados, mover conteúdo/recursos exclusivos, integrar chamadas base e validar compilação/testes/AAB. Não declarar toda a família pronta por extrair uma única tela.

- [x] Account: login, cadastro, token e dados pessoais; SSO/biometria/sessão/conclusão na base; install-time.
- [x] Home: superfícies atual/V2 e componentes exclusivos, mantendo shell de navegação/recuperação e contratos Results/Betting; install-time.
- [x] Responsible Gaming: apresentação informativa/avaliação/suspensão, operações/identidade e caminhos obrigatórios protegidos; install-time.
- [x] Onboarding: introdução/tutoriais opcionais; boot, fila, update, maioridade e recuperação obrigatória na base.
- [x] Betting: todas as modalidades, cartelas, rapidão, favoritos, combos e tutoriais; preservar rascunhos e continuação pós-login.
- [x] Cart: listas/linhas/agrupamentos, inclusão, favoritos e revisão; proteger fechamento dos headers por wrapper novo na base e preservar transferência anônimo→logado.
- [x] Payment: métodos, cartões, CVV, Pix, processamento e resultados; proteger envio/monitoramento e recuperação de compras.
- [x] Tickets: compras/apostas/comprovantes, detalhes, scanner e conferência; preservar rotas de continuidade de pagamentos/resgates.
- [x] Marketplace e Marketplace Filter: contratos base entre ambos, entrega independente e manutenção dos filtros.
- [x] Results: modalidades, concursos, detalhes e apostas confirmadas; estado consumido pela Home continua base.
- [x] Notifications: UI de central/mensagens/preferências; serviço e recebimento push continuam base.
- [x] Retail: busca/localização e todas as apresentações de resgate; operações/recibos/cadastro Payment continuam base.
- [x] Support Help e FAQ: ajuda opcional e FAQ em seus splits, sem transferir aceite/termos obrigatórios.

### 5. Aceitação integrada

- [x] Confirmar títulos, nomes efetivos e entrega install-time/on-demand de cada módulo no AAB; definições concretas ausentes da base e presentes no módulo correto antes de fusing.
- [x] Em emulador dedicado, provar essenciais instalados inicialmente e opcionais ausentes; instalar cada opcional. Percorrer as 15 chaves com off/on, retry e reentrada controlados; navegação cruzada coberta nas suítes de integração, sem simular certificação de backend.
- [x] Cobrir flag off durante seleção, transferência de carrinho, submissão/monitoramento Pix/cartão, resgate e exibição de comprovante sem operações financeiras reais.
- [x] Compilar APK/AAB e validar segunda entrada MainActivity estável via ADB no emulador dedicado; AAB Release/R8 compilado e 15 entradas preservadas. O IDE Windows e a distribuição Release não foram executados nesta máquina.
- [x] Executar qualidade, arquitetura, IDs, todos os testes e JaCoCo global >95%. Repetir somente etapas afetadas por novas alterações/falhas; uma invocação Gradle por vez.
- [x] Conferir 45 hashes e ausência de artefatos/cache no diff; atualizar documentação com JSON completo, escopo real, comandos, tamanhos e limitações.

Logs e artefatos de execução ficam fora do checkout em `simlo-artifacts/2026-11-02/all-feature-delivery`. Ferramentas opcionais indisponíveis não terão métricas inventadas. Não confundir testes controlados/local-testing com publicação Google Play/Firebase ou validação Samsung.


## Fechamento verificado

Rodada `final-quality-seventh.log`: BUILD SUCCESSFUL em 14 min 52 s;
3.866 testes, 802 suítes, zero falhas/erros/skips; JaCoCo global 95,29%.
AABs Debug e Release/R8 com entradas concretas dos 15 splits verificadas.
Emulador dedicado: instalação SDK de 12 opcionais, disponibilidade/retry/reentrada
nas 15 chaves, 9 testes de geometria nativa e duas entradas MainActivity.
Os testes controlados não certificam backend, Firebase, Play, Samsung ou IDE
Windows. Release sem assinatura de produção. Detalhes e proveniência no
[guia de entrega](../../DYNAMIC_FEATURES.md). Nenhum staging/commit/push.
