# Navigation and Session Extraction Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans. Execute inline in the current checkout, preserving existing local changes. Request an independent final review of the extraction.

**Goal:** Separate navigation graphs and session coordination without changing their behavior, and remove CXGuard as requested during execution.
**Architecture:** Preserve one flat NavHost and existing destination owners. Extract state holders, session composition, cross-flow actions and graph registration functions.
**Tech Stack:** Kotlin, Compose, Navigation Compose, JUnit, Android instrumentation.
**Spec:** `docs/superpowers/specs/2026-10-17-navigation-session-extraction-design.md`.

## Global Constraints

- Preserve local modifications; use the saved pre-extraction source as the comparison baseline.
- Preserve routes, argument defaults, ViewModel keys, owners and navigation options.
- Preserve saveable state and identity/lease checks; no backend changes. Remove the CXGuard integration as explicitly requested by the user.
- Work in the existing `codex/migracao-compose` checkout because it contains the current behavior.

## Review Focus

- Reauthentication with the same owner restores details; another owner discards them.
- Required registration retains the pending continuation until Home is allowed.
- Home actions resume once after parameters are loaded.
- Logout and foreground expiry clear state and reset the stack in the existing order.
- Extracted registrations keep all routes, argument defaults and destination ViewModel owners.

## Task 1: Extract state and session coordination

**Files:** `AppNavigationState.kt`, `AppSessionState.kt`, `AppSessionCoordinator.kt`, `AppSessionStateTest.kt`, `LoteriasApp.kt`.

**Interfaces:** `AppNavigationState` exposes existing detail context/transient state. `AppSessionState.authenticated(destination, owner, details)` returns the target and preserves pending continuations; `clearForVisitor(details)` clears the existing visitor-entry fields. Coordinator exposes the same callbacks and the current private session lease/key.

- [x] Run existing host JVM tests and architecture gates as baseline.
- [x] Write transition tests for same/different owner restoration, required registration, Home actions and visitor reset; run before extracting implementation.
- [x] Extract state with the existing remember/saveable semantics and session effects/callbacks.
- [x] Run host JVM tests and compile.

## Task 2: Extract graph registration and cross-flow actions

**Files:** `*NavigationGraph.kt`, `AppNavigationActions.kt`, `LoteriasApp.kt`.

**Interfaces:** `NavGraphBuilder.<flow>Graph(...)` registers existing destinations directly in the root graph, receiving only the dependencies/state/callbacks it needs.

- [x] Move destination bodies mechanically, retaining route/argument/ViewModel contracts.
- [x] Move destination helpers with their owning flow and remove unused imports.
- [x] Compare extracted bodies to the saved baseline and run host JVM tests/gates.
- [x] Compile the configured debug flavor and instrumentation tests; run navigation/visitor regressions on a dedicated emulator if available.
- [x] Obtain independent final review, address behavioral findings, and record verification results.

## Execution notes

The initial baseline failed because the CXGuard license path was absent, then because the offline runtime was not cached. Supplying the existing local license and downloading the runtime allowed source compilation, but the SDK's ASM transform failed before unit tests. The user subsequently explicitly requested removing CXGuard. Its plugin/runtime, exclusive repositories/configuration/adapters/hooks, telemetry and exclusive scripts/tests/report were removed; the SDK-only minSdk increase was reverted to 24. Chucker and unrelated local changes remain. Verification after removal uses ordinary Gradle commands without license properties.

The existing `verifyComposeMigration` failure on the debug `NetworkInspectorActivity` manifest was reproduced before the extraction. No gate or threshold was relaxed.

The independent static review approved the extraction/removal with no findings: all 50 registration signatures match, 48 bodies match after field qualification, and the remaining Home/Login changes preserve their original behavior. Host JVM tests passed in Prd/Tqs/Automacao: 117 per environment, zero failures/errors/skips. Architecture and CaixaDS gates passed.


## Validação final — 17/10/2026

- `modernCheck releaseCheck --offline --continue --console=plain`: 2.155 tarefas; única falha em `verifyComposeMigration`, na restrição do manifest debug com `NetworkInspectorActivity`, reproduzida antes da extração. Nenhum gate foi relaxado.
- Relatórios JVM das tarefas executadas/reutilizadas: 1.735 testes, zero falhas/erros/ignorados; inclui 117 testes do host por ambiente.
- Android: 117 testes aprovados nos escopos desta execução: host 24, preferências 27, apresentação 3, carrinho 54, Home 4 e notificações 5. O relatório antigo de tickets presente no mesmo AVD não entra na contagem.
- Smoke do APK mock: 15/15 rotas representativas dos 14 grupos renderizaram, com processo vivo e sem crash do app. O APK utilizado foi inspecionado com `apkanalyzer`: `ENABLE_MOCK_BACKEND=true`, cenário `HAPPY_PATH`.
- PRD, TQS e Automação: lint Debug e os três Releases minificados aprovados. `releaseCheck verifyCleanArchitecture verifyCaixaDS --offline --console=plain` também passou isoladamente no código final (874 tarefas, 21s).
- Seis APKs atuais (três Debug e três Release) inspecionados sem entradas/descritores CXGuard. Hashes dos fontes permaneceram iguais durante a verificação; `git diff --check` passou.
- Revisão independente aprovada sem achados. Rotas, argumentos, keys/owners dos ViewModels, callbacks e semântica de estado conferidos contra a cópia local anterior à extração.
- Validação Android no AVD separado `SimloComposeValidation`, porta 5560. O APK mock instalado por esta tarefa foi removido e esse emulador foi encerrado. O emulador já existente na porta 5556 foi preservado.

Evidências locais: `build/verification/navigation-refactor-20261017/`, incluindo logs, fontes anteriores, resumos JVM/Android, resultados do smoke, atestado dos fontes e hashes dos APKs. Alterações mantidas na árvore atual, sem integrar em outra branch.

## Correção posterior do gate — 17/10/2026

A pedido do usuário, `verifyComposeMigration` passou a reconhecer estritamente as duas Activities do manifest debug existente: host Compose interno e inspetor com seu único filtro MAIN/LAUNCHER. Todos os atributos, filhos e quantidades são validados; permissões, componentes e filtros adicionais continuam proibidos. O manifest e a implementação do inspetor foram preservados byte a byte.

Após corrigir essa primeira falha, o gate revelou outro XML de plataforma existente fora da lista: `receipt_file_paths.xml`. A exceção valida somente esse arquivo com um único `cache-path`, nome `comprovantes` e caminho `receipts/`; não libera XMLs genéricos nem caminhos amplos.

`python3 scripts/check-compose-guardrails.py`: 63 verificações aprovadas, incluindo 59 rejeições esperadas e quatro casos válidos. Fixtures restauradas; hashes dos arquivos preservados conferidos. Revisão independente aprovada sem achados. Evidências: `build/verification/debug-manifest-gate/`. Esta validação posterior não representa nova execução completa de `modernCheck` ou `releaseCheck`.

Execução final sem fixtures: os nove gates raiz de `modernCheck` passaram juntos (`verifyComposeMigration`, inventários/paridade, arquitetura, CaixaDS, FilaBRA, Topaz, segurança de rede e identidade/flavors): nove tarefas executadas, build aprovado em 11s. `git diff --check` aprovado.
