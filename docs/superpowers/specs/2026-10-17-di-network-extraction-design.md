# Separação da injeção por domínio e dos clientes de rede

## Objetivo e escopo

Refatorar `AppContainer.kt` e `di/ApplicationModule.kt`, conforme solicitado, sem modificar o comportamento atual. A árvore de trabalho existente é a referência, inclusive as alterações anteriores de navegação, remoção da CXGuard e correção dos gates.

## Divisão

- `ApplicationModule` mantém apenas dependências comuns da aplicação.
- Módulos Hilt de rede, sessão, configuração, onboarding, conta, notificações, apostas, carrinho, pagamentos, bilhetes, resultados, bolões, jogo responsável, suporte e lotéricas recebem os métodos `@Provides` existentes de seus domínios.
- `BackendClientFactory` cria os mesmos clientes Retrofit/OkHttp com ambiente, Gson, autenticação, refresh, roteamento cloud, opções de debug/mock e pins fornecidos pelo composition root. Não instala clientes nem muda a sessão.
- `NetworkClientCoordinator` instala os clientes nos providers existentes e coordena sua atualização com a mesma `NetworkSecurityPreparation`. O callback de parâmetros continua atualizando o mesmo roteador cloud.
- `NetworkModule` prepara a configuração Android, fallback, repositório de chaves e fábrica. `AppContainer` recebe o coordenador via `dagger.Lazy`, inicializa a rede no mesmo ponto do seu corpo e expõe o mesmo caso de uso `networkSecurity`.

## Contratos preservados

- Todos os métodos `@Provides`, suas assinaturas, corpos, anotações e escopos são transportados sem alteração. Aliases continuam retornando a mesma instância.
- `SingletonComponent`, identidade dos providers, token store e repositories permanecem iguais; não há novas dependências de build nem alterações em `core`/features.
- Inicialização registra o callback de parâmetros e instala o fallback antes de o container ficar disponível.
- Atualização mock instala fallback sem buscar chaves. Atualização real utiliza `refresh()` ou fallback; erros/cancelamentos mantêm a mesma propagação pelo caso de uso existente.
- A ordem de instalação continua: principal, leitura de notificações, logout, cadastro, prêmio, compra, legado, histórico, configuração dinâmica do histórico e pagamentos. Não converter em substituição atômica nem mudar comportamento em falhas parciais.
- Histórico dinâmico utiliza somente hosts do conjunto verificado, limpa seu cache a cada atualização e continua sem o roteador cloud estático.
- Mercado Pago/RecargaPay continuam sem o relógio/Gson e refresh da CAIXA. Pins, signatários, endpoints, logs, redactions e mocks não mudam.
- A fachada pública do container usada pela navegação é preservada.

## Validação

Comparar mecanicamente todos os bindings contra a cópia local anterior à extração; compilar Hilt em todos os flavors; executar os testes JVM existentes e novos testes do coordenador com fábrica/providers reais. Exercitar inicialização, atualização mock/real, invalidação de histórico dinâmico, falha, cancelamento e serialização. Executar os gates de arquitetura, segurança/rede, migração e o pipeline `modernCheck releaseCheck` quando disponíveis. Revisão independente final focada no diff desta tarefa, não no diff inteiro do checkout.

## Decisão de execução

Executar inline na árvore atual, preservando as alterações locais; não criar commits nem integrar em outra branch nesta tarefa. O usuário já autorizou a refatoração e definiu seu limite de comportamento.
