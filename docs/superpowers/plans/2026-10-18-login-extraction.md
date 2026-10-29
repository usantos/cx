# Login Extraction Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Executar inline no checkout autorizado, com uma revisão independente final.

**Goal:** separar coordenação, autorização, biometria, tela, diálogos e componentes, sem mudar ViewModel/comportamento.

**Architecture:** helpers composables incondicionais preservam lifecycle dos launchers/estado salvo e ordem de efeitos; holders transportam apenas callback e host. APIs públicas permanecem.

**Tech Stack:** Kotlin, Compose, Activity Result, JUnit, Android UI, Gradle/Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-login-extraction-design.md.

## Global Constraints

Preservar checkout/trabalho anterior; sem commits/worktrees/instalação global. Só LoginRoute.kt anterior muda em produção, com cinco novos arquivos no mesmo pacote. ViewModel/auth/recursos/dependências/gates intactos. Corpos extraídos literalmente, com adaptações mínimas documentadas.

## Review Focus

- Launchers browser→credential, resultados atuais e SDK<29: testes existentes/corpos preservados; normal path retry manual.
- AutomaticAuthorizationConsumed e chaves de efeitos: teste novo falha automática/reentrada da flag sem nova tentativa.
- Challenge/prompt/cleanup: teste novo recomposição e saída; testes existentes cancel/refresh; novo prompt throw/fallback.
- Prioridade de diálogos e invalidação após ACK: novo enrollment+blocked e testes existentes blocked/consultation.
- Screen/token/cores/tipografia/insets/seletor: corpo igual, testes existentes e LoginEnvironmentPickerUiTest.

### Task 1 — Referência

**Files:** novo feature/account/src/androidTest/java/br/gov/caixa/loterias/feature/account/LoginCoordinationUiTest.kt; produção intacta.

- [x] Capturar original/hashes e aplicar refactoring-specialist localmente; check/lint/cobertura/release/três gates aprovados.
- [x] Acrescentar quatro testes descritos na spec e rodar todos38Android account +2app ambiente contra original. Esperado: zero falhas, hashes anteriores intactos.

### Task 2 — Extração

**Files:** LoginRoute.kt; novos LoginAuthorization.kt, LoginBiometrics.kt, LoginScreen.kt, LoginDialogs.kt e LoginComponents.kt.

**Interfaces:** assinaturas e holders exatamente da spec.

- [x] Extrair preservando ordem/chaves/capturas; APIs públicas e corpos UI iguais.
- [x] Reconstruir blocos e conferir tokens/hashes; adicionar um parágrafo em APP_ARCHITECTURE.md. Esperado: só LoginRoute.kt anterior alterado.

### Task 3 — Verificação

- [x] Check/lint/cobertura/release account,38Android account +2app ambiente;134JVM app/APK PRD debug/compile PRD release e nove gates offline aprovados.
- [x] Revisão independente final sem achados relevantes pendentes; whitespace/evidências.
- [x] Pacotes/display restaurados e emulador dedicado encerrado.
