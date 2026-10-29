# Support extraction implementation plan

> Executar inline com superpowers:executing-plans; análise prévia e revisão final usam perfis do catálogo VoltAgent solicitado pelo usuário.

**Goal:** separar composição, abas, perguntas frequentes, respostas HTML, links, ajuda por modalidade e formatadores, preservando ViewModel e comportamento.

**Architecture:** SupportScreen conserva modos/loading; componentes recebem estado e callbacks atuais. FaqAnswer conserva memória local; extraem-se somente builder e folha do diálogo para separar a integração Android.

**Tech Stack:** Kotlin, Compose, Android, JUnit, Gradle/Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-support-extraction-design.md.

## Global Constraints

Checkout existente e trabalho anterior preservados. Sem commits/worktrees ou instalação global. Somente SupportScreen.kt existente muda na produção, com sete novos arquivos no mesmo pacote. Não alterar ViewModel, reducer, contrato, recursos, dependências ou gates. Nenhum novo comportamento ou endurecimento de URLs.

## Review Focus

- Loading global antes dos ramos e mesmos retornos antecipados; prioridade de erro no conteúdo comum.
- Chaves de seções/perguntas, expansão, animação e lifetime de pendingUrl/cache permanecem iguais.
- Normalização e validação de links originais; confirmação limpa estado antes de Intent/resolveActivity e cancelamento não abre navegador.
- Ações atuais, mesmos títulos, guardas de aceite, fórmula das abas e tags WebView.
- Exceções de cores/drawables da ajuda, formatação de metadados, posição e owner do scroll e distinção entre três renderizadores.

## Task 1 — Referência e caracterização

- [x] Capturar referência e hashes; executar :feature:support:check, compileReleaseKotlin e três gates offline. Esperado: aprovado.
- [x] Aplicar refactoring-specialist em análise somente de leitura dos limites de extração; preservar chaves/capturas e helpers existentes.
- [x] Adicionar três casos Android de comportamento em SupportScreenCoordinationTest, pela tela pública: link pendente preservado na recomposição e limpo ao trocar resposta; abas/aceite com callbacks atuais; loading da ajuda com metadados e retorno. Executar todos os sete Android contra o código original. Esperado: sem falhas.

## Task 2 — Extração

- [x] Criar SupportContent.kt, SupportTabs.kt, SupportFaqContent.kt, SupportAnswerContent.kt, SupportFaqLinks.kt, GameHelpScreen.kt e SupportTextFormatting.kt, conforme as interfaces da especificação.
- [x] Reconstruir corpos originais, conferir hashes e atualizar APP_ARCHITECTURE.md. Esperado: só SupportScreen existente alterado, sem mudanças em recursos/configurações/helpers.

## Task 3 — Validação e entrega

- [x] Check/lint/cobertura configurada, release, sete Android do módulo, JVM do app, APK PRD debug e nove gates offline. Esperado: todos aprovados.
- [x] Aplicar code-reviewer em revisão independente somente de leitura da extração; tratar achados relevantes.
- [x] Conferir whitespace/evidências, restaurar e encerrar o emulador dedicado.
