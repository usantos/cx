# Plano de separação da injeção e dos clientes de rede

**Goal:** Separar os módulos Hilt por domínio e a criação/atualização dos clientes, mantendo o comportamento.
**Spec:** `docs/superpowers/specs/2026-10-17-di-network-extraction-design.md`.
**Execution:** Inline, com `superpowers:executing-plans`.

## Global Constraints

- Baseline: cópias locais em `build/verification/di-network-refactor-20261017/`.
- Preservar assinaturas, escopos, corpos dos bindings e a fachada do container.
- Manter ordem e condições de criação/instalação dos clientes, pins e serialização.
- Preservar alterações locais; sem novos SDKs, commits, push ou alteração dos gates.

## Review Focus

- A construção lazy não cria ciclos Hilt nem muda o momento da instalação inicial.
- Falhas durante refresh não substituem a geração anterior; falhas parciais de instalação mantêm a ordem existente.
- Histórico dinâmico não conserva clientes/pins da geração anterior.
- Clientes de pagamento não recebem os interceptors/relógio dos clientes CAIXA.
- Aliases dos repositories e sessão mantêm suas instâncias e seus escopos.

## Task 1: Baseline e módulos por domínio

**Files:** `di/ApplicationModule.kt`, `di/*Module.kt`.
**Interfaces:** Mesmos métodos `@Provides` e `SingletonComponent`; sem nova API de feature.

- [x] Executar testes do host e gates como baseline.
- [x] Distribuir mecanicamente os bindings e remover imports não utilizados.
- [x] Comparar métodos completos, anotações/assinaturas/corpos e garantir ausência de duplicatas/perdas.
- [x] Compilar o host e o grafo Hilt.

## Task 2: Fábrica e coordenador de rede

**Files:** `BackendClientFactory.kt`, `NetworkClientCoordinator.kt`, `di/NetworkModule.kt`, `AppContainer.kt`, `NetworkClientCoordinatorTest.kt`.
**Interfaces:** `NetworkClientCoordinator.initialize(): Unit`, `networkSecurity: PrepareNetworkSecurityUseCase`; fábrica tipada cria os clientes existentes com `PublicKeySet`.

- [x] Escrever testes de inicialização, rotação/cache, mock, falha, cancelamento e serialização; observar falha antes da extração.
- [x] Extrair a fábrica e o coordenador, mantendo o código/ordem e a integração lazy do container.
- [x] Executar os testes novos e a suíte JVM do host.

## Task 3: Verificação integrada e revisão

- [x] Executar a matriz JVM/Hilt, gates e `modernCheck releaseCheck --offline --continue`.
- [x] Obter revisão independente sobre o diff contra a baseline local e corrigir achados relevantes.
- [x] Conferir a inicialização do Hilt em APK mock no AVD dedicado.
- [x] Conferir arquivos preservados, whitespace e registrar evidências/limites.

## Evidências da execução

- Baseline do host/gates aprovada antes da extração; 175 bindings distribuídos em 16 módulos, preservando métodos/anotações/imports. `ApplicationModule`: 925 → 29 linhas. `AppContainer`: 462 → 231 linhas.
- Comparação com a cópia local anterior à extração: 175 bindings, 11 chamadas da fábrica, corpo do repositório de pins e 111 propriedades públicas do construtor preservados.
- Três flavors: 127 testes JVM do host por ambiente, incluindo dez novos testes do coordenador em cada ambiente, sem falhas/erros/ignorados.
- `modernCheck releaseCheck --offline --continue --console=plain`, com `ANDROID_SERIAL=emulator-5560`: aprovado em 12m50s, 2.155 tarefas. Relatórios das suítes executadas/reutilizadas: 1.765 casos JVM e 93 Android, sem falhas/erros/ignorados. Lint Debug e três Releases minificados aprovados.
- A primeira tentativa integrada iniciou instrumentação no emulador já aberto (5556), foi interrompida e não integra o resultado acima. A execução final usou somente o AVD dedicado `SimloComposeValidation`, porta 5560.
- Revisão independente aprovada sem achados. Implementação concorrente preexistente dos providers foi preservada; testes/revisão não homologam TLS/OAuth contra os serviços reais.
- APK mock construído em diretório isolado por init script temporário, com `ENABLE_MOCK_BACKEND=true` e `HAPPY_PATH` conferidos no dex. Hashes dos seis APKs normais permaneceram iguais.
- Verificação de inicialização/rotas no APK mock: 15/15 aprovadas, sem crash do app. A aplicação instalada por esta tarefa (ausente antes da instalação) foi removida do AVD dedicado, que foi encerrado. Fontes de produção e seis APKs normais mantiveram seus hashes; `git diff --check` aprovado.

Fontes anteriores, comparação dos contratos, logs, relatórios e hashes: `build/verification/di-network-refactor-20261017/`. Alterações mantidas na árvore atual, sem commits ou integração de branch.
