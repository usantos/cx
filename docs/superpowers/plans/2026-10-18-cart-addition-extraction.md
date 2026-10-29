# Cart Addition Extraction Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Execução inline autorizada, uma revisão independente final.

**Goal:** separar as oito responsabilidades sem alterar ViewModel/comportamento.

**Architecture:** coordenador permanece na rota; sete arquivos no mesmo pacote recebem os corpos existentes, sem novos estados ou efeitos.

**Tech Stack:** Kotlin, Compose, Lifecycle, JUnit, Android UI, Gradle/Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-cart-addition-extraction-design.md.

## Global Constraints

Preservar checkout/trabalho anterior; sem commits/worktrees/configuração global. Só CartAdditionRoute.kt anterior muda em produção, com sete arquivos novos. ViewModel/CartRules/recursos/dependências/gates intactos; cobertura todas classes mínimo95,01% inalterada.

## Review Focus

- ON_RESUME/relógio do servidor/expiração não navegam automaticamente: novo teste lifecycle.
- BackHandler nos dois ramos e callbacks atuais: teste novo de ações/back e troca de VM.
- Combo versus aposta, mínimo positivo, contest>0 e valor999.99: novo teste de troca/layout do valor.
- Help normaliza sem mudar argumento original, desconhecida/null não têm botão: novo teste aliases.
- Geometria/campanhas/insets/defaults de componentes: corpos literais e testes existentes, sem homologação visual física nova.

### Task 1 — Referência

**Files:** novo CartAdditionCoordinationUiTest.kt em feature/cart/src/androidTest/java/br/gov/caixa/loterias/feature/cart.

- [x] Capturar código/hashes; JVM/release/três gates iniciais aprovados.
- [x] Acrescentar três testes; rodar check/lint/cobertura com todos65Android sem filtro contra original. Esperado: aprovados, árvore anterior intacta.

### Task 2 — Extração

**Files:** CartAdditionRoute.kt e sete novos arquivos descritos na spec.

**Interfaces:** assinaturas/defaults originais, somente internal para compartilhados; HeaderValue privado.

- [x] Mover declarações literalmente, conservando coordenador e chamadas.
- [x] Comparar declarações/tokens/hashes e adicionar um parágrafo em APP_ARCHITECTURE.md. Esperado: só CartAdditionRoute.kt anterior alterado.

### Task 3 — Verificação

- [x] Check/lint/cobertura/release/todos65Android do módulo;134JVM app/compilePRDrelease/APKPRDdebug e nove gates aprovados.
- [x] Revisão independente final sem achados relevantes; whitespace/evidências.
- [x] Confirmar pacotes/display originais e encerrar emulador dedicado.
