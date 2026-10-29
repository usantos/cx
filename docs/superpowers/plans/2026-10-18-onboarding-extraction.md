# Onboarding Extraction Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Execução inline já autorizada, com uma revisão independente ao final.

**Goal:** separar coordenação, introdução, sincronização do pager, tutorial, idade e componentes, mantendo comportamento e ViewModel.

**Architecture:** OnboardingRoute mantém o coordenador; corpos de UI passam para cinco arquivos no mesmo pacote. Pager permanece na introdução; apenas seu efeito é extraído com as mesmas chaves/capturas.

**Tech Stack:** Kotlin, Compose, JUnit, Android UI, Gradle/Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-onboarding-extraction-design.md.

## Global Constraints

Preservar checkout/trabalho anterior. Sem commits/worktrees ou instalação global de agentes. Somente OnboardingRoute.kt anterior muda em produção; cinco arquivos novos no mesmo pacote. ViewModel/recursos/contratos/gates/dependências permanecem intactos.

## Review Focus

- Callback de cada efeito e reinício somente com viewModel: teste de rejeição e troca de ViewModel.
- InitialPage não zero e swipe atualizam ViewModel e ação final: teste de introdução.
- Diálogo inicia visível, aceita/recusa sem novas regras: testes de introdução/rejeição.
- Checkbox tanto pela linha quanto pelo controle; pular marcado não persiste: teste de tutorial.
- Tipografia/layouts/insets preservados: testes existentes e comparação de corpos; splash não modificado.

### Task 1 — Referência

**Files:** novo androidTest OnboardingCoordinationUiTest.kt; fontes originais intactas.

- [x] Capturar original/hashes, aplicar perfil refactoring-specialist; check/cobertura/release/três gates offline aprovados (18 JVM, 79/79 linhas no escopo configurado).
- [x] Adicionar três casos descritos na especificação; executar :feature:onboarding:connectedDebugAndroidTest sem filtro contra original. Esperado: nove aprovados, nenhum arquivo anterior alterado.

### Task 2 — Extração

**Files:** OnboardingRoute.kt; novos OnboardingIntroduction.kt, OnboardingPagerSynchronization.kt, OnboardingTutorial.kt, OnboardingAge.kt, OnboardingComponents.kt.

**Interfaces:** exatamente as assinaturas da especificação; somente internal quando compartilhadas.

- [x] Mover declarações, extrair efeito e diálogo sem mudar seus owners/chaves/condição.
- [x] Reconstruir corpos e conferir tokens/hashes. Atualizar apenas um parágrafo de APP_ARCHITECTURE.md. Esperado: só OnboardingRoute.kt anterior alterado.

### Task 3 — Verificação e entrega

- [x] Executar check/cobertura/release e nove Android do módulo; 134 JVM app/APK PRD debug e nove gates offline. Esperado: aprovados, cobertura configurada inalterada.
- [x] Revisão independente code-reviewer sem achados relevantes pendentes; evidências e whitespace.
- [x] Confirmar pacotes/display originais, encerrar emulador dedicado e consolidar relatório.
