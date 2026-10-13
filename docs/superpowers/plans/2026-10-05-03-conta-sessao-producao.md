# Cadastro, conta e sessão de produção — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Parear campos, teclado, erros e continuidade de sessão com o original.

**Architecture:** Comparar o binário instalado, corrigir componentes existentes e validar sem substituir contratos por mocks de produção. Plano com execução contínua já autorizada pelo usuário; status por evidência.

**Tech Stack:** Kotlin, Jetpack Compose, widgets Android programáticos, JUnit, AndroidJUnitRunner, ADB.

**Spec:** docs/exact-logged-device-status-2026-10-05.md

**Files:** feature/account, core/auth, app/LoteriasApp.kt.

**Interfaces:** PersonalDataUiState, RegistrationUiState, AuthenticatedSessionIdentity, SavedStateHandle.

## Global Constraints
- Referência principal: APK de produção instalado no XCover7, pacote br.gov.caixa.loterias.apostas, versão1.24.0/230102400. Fonte legado apenas complementa.
- Preservar checkout e alterações anteriores. Sem reset, commit, push ou reinstalação do oficial. Candidato somente pacote paralelo/debug.
- Não confirmar compra, pagamento, resgate, favorita, cadastro ou alteração cadastral em produção; comparar telas de leitura e usar doubles locais para mutações.
- Apenas coordenador usa ADB/Gradle. Cores/fontes no CaixaDS; sem novos layouts XML de tela.
- Aprovação funcional, comparação física e conteúdo equivalente são resultados separados. Não declarar100% com recortes incompletos.

## Review Focus
- Mensagens/literais/HTML e fontes exatamente como o binário.
- Dados distintos não podem passar por pares equivalentes.
- Teclado/foco/scroll não podem esconder ações ou perder seleção.
- Nenhuma restauração pode repetir mutação financeira ou trocar proprietário.
- Gates existentes e caminhos reais de navegação permanecem íntegros.

### Task1: Campos e diálogos
- [x] Conferir recursos compilados de TextInputLayout versus BasicTextField: altura52dp, raio4dp, entrada/rótulo16sp, helper16sp, aviso20dp; reproduzir foco/cursor/erro na candidata. Raster equivalente com o oficial permanece separado abaixo.
- [ ] Medir scroll inferior/IME/checkbox/rodapé contra containers instalados (cadastro40dp, dados100dp), sem gravação.
- [x] Reproduzir diferenças em teste Android, corrigir máscara monetária/seleção e validação CEP inline preservando validadores/callbacks.
- [x] Reproduzir diferenças físicas de Dados Pessoais: ocultar Atualizar com IME mantendo resize/scroll e alinhar recálculo ao fundo cinza/padding12dp do APK; três regressões após RED confirmado, sem gravação.

### Task2: Sessão e restauração
- [x] Rastrear sessão instalada (login/biometria/401/retorno/processo) no DEX/fonte sem acessar credenciais.
- [x] Comparar SessionTokenStore e host antes de decidir persistência; sessão normal também em memória no instalado, sem persistência nova de token.
- [x] Testar proprietário/tarefa/compra em restauração mock e registrar separadamente o contrato instalado e limites de login real. Provas Pix de processo/tarefa estão em `docs/remaining-pix-process-restoration-2026-10-05.md`; o mesmo dono é reinstalado sinteticamente, sem provar autenticação automática de produção.
- [x] Executar :feature:account:testDebugUnitTest, :core:auth:testDebugUnitTest, :app:testAutomacaoDebugUnitTest e Android de conta. Zero falhas confirmadas nos relatórios abaixo.
- [x] Reproduzir retorno do background em regressões sintéticas, corrigir token ausente→login e expirado→Entendi→login, excluindo entrada inicial/login/token/onboarding e autorização pendente.
- [x] Reproduzir perda do aviso em configuração; guardar só marcador UI saveable, restaurar callback pelo snapshot transitório original e rejeitar substituição/login pendente/morte de processo. Nenhuma credencial/identidade serializada.
- [ ] Parear retorno real, biometria/provider e raster de campos/IME com sessão e conteúdo equivalentes no oficial.

## Evidências de execução coordenada

- Conta JVM: **55 testes**, zero failures/errors, em `feature/account/build/test-results/testDebugUnitTest/`.
- XCover7: **11 testes Android passaram**, `/tmp/production-account-final3-green-xcover.log`, `OK (11 tests)`: os oito cenários anteriores (três de máscara/seleção do limite, dois de CEP inline, dois fixtures completos sem gravação e um de helper/mínimo/máximo), mais três regressões de IME/fundo cinza/rótulo de recálculo em uma linha. A seleção 0..9 foi preservada sem envio de edição; digitar 1234 produz uma única emissão R$12,34. O coordenador observou RED para cursor indevidamente movido ao fim e callback duplicado antes da correção.
- RED IME/recálculo: `/tmp/production-account-ime-limit-red-xcover.log`, três falhas esperadas (Atualizar acima do IME, fundo248 em vez de214/215/215, rótulo em duas linhas). Correção restrita à apresentação de PersonalDataRoute, após esse RED. Fixture sintético sem identidade real nem chamadas de escrita/recálculo.
- Sessão JVM/build host: `/tmp/production-session-recreation-green.log`, `BUILD SUCCESSFUL`. XML atual confirma **49 testes core/auth** e **54 testes app**, zero failures/errors. Dentro deles, são **14 regressões novas**: oito core/auth e seis host, incluindo retorno, guard de lease e reconstrução do aviso após configuração.
- RED foreground: `/tmp/production-session-red.log`, seis falhas comportamentais esperadas em oito testes; RED reconstrução: `/tmp/production-session-recreation-red.log`, cinco falhas comportamentais esperadas. Correções aplicadas somente depois desses RED coordenados.
- Revisão estática posterior da entrada monetária: `remember(value)` reinicializa a edição quando o valor externo muda; eventos de seleção/composição sem mudança de texto preservam TextFieldValue sem callback; mudanças reais formatam e só emitem se o valor monetário formatado mudou. Nenhuma alteração de código foi feita durante essa revisão.

**Limite da conclusão:** os resultados comprovam os contratos locais e os cenários sintéticos executados, incluindo as diferenças estruturais comprovadas de IME/recálculo. Containers oficiais cadastro40dp/dados100dp foram lidos do APK; cobertura restante de scroll inferior/checkbox e raster exige conteúdo equivalente. Capturas candidatas e testes host JVM não certificam igualdade raster nem OAuth/biometria reais. Não há declaração de paridade visual100%; não houve cadastro, atualização cadastral, aceite, compra ou persistência de credencial real para obter essas evidências.


## Resultado final 06/10/2026

A regressão final da conta no XCover passou (11 testes Android; 55 JVM sem falhas), cobrindo entrada monetária/cursor, validação inline de CEP e campos/IME/rodapé do limite. O host e core/auth passaram 54 app +49 auth JVM. O APK de produção exibiu o aviso literal de sessão expirada; os redirects e proteção da sessão substituída foram exercitados em doubles. Não houve gravação cadastral nem persistência inventada de token.

Ficam pendentes os pares visuais de todo o cadastro (scroll inferior/checkbox/estados de erro) e login real com provider/biometria, assim como recuperação autenticada da transação após morte do processo. A sessão da conta e as respostas equivalentes não foram reproduzidas nestes testes.

### Revalidação final — 06/10/2026

O log final copiado para `build/verification/production-plans-20261005/logs/production-account-final2-green-xcover.log` confirma `OK (11 tests)` no XCover; os testes JVM finais continuam em55 para conta,49 para auth e54 para o host do app, todos sem falhas. A compilação e os gates gerais também passaram. Nenhum dado foi gravado na conta oficial.

Isso fecha a execução das correções locais de cursor, CEP inline e interação com IME, mas não a comparação completa do formulário. Scroll/checkbox/erros com respostas idênticas e o fluxo real provider/biometria/recuperação continuam pendentes pelos motivos descritos no relatório consolidado.
