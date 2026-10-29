# Responsible gaming extraction implementation plan

> **For agentic workers:** Use superpowers:executing-plans inline, with one fresh-context independent review at the end.

**Goal:** Extrair telas e formatadores sem alterar o ViewModel ou comportamento.
**Architecture:** Rota retém coordenação e estilo herdado; telas reutilizam estado/actions existentes. Corpos extraídos mecanicamente, sem novos owners.
**Tech Stack:** Kotlin, Compose, coroutines, JUnit, Android Compose tests, Gradle.
**Spec:** docs/superpowers/specs/2026-10-18-responsible-gaming-extraction-design.md.

## Global Constraints

Preservar checkout codex/migracao-compose e mudanças anteriores. Sem commit, novos worktrees, dependências, recursos, gates ou KMP. Sete arquivos de produção novos; uma nova classe Android no app, que já possui a infraestrutura. ViewModel/regras existentes byte a byte.

## Review Focus

- assessmentRequired mantém as restrições de fechar e a navegação pelos efeitos, sem introduzir novo carregamento na rota.
- Formulário mantém seleção/voltar/avanço/finalização, estados loading/erro/resultado e privateDataEnabled para pergunta ausente.
- Suspensão mantém ciência/período antes de confirmar, prioridade dos estados e override de onSubmitSuspension versus fallback ao ViewModel.
- Repasses mantêm retry/ajuda/retornos e valores; formatadores conservam truncamento, limites, singular/plural, espaços e ano atual.
- Trocar ViewModel reinicia apenas a coleta keyed por ele; ProvideTextStyle e assinatura/defaults continuam na rota, sem closures lembradas.

## Task 1 — Referência e testes de caracterização

Consumes: rotas, ViewModel e casos de uso atuais. Produces: fonte/hashes de referência e onze cenários Android controlados.

- [x] Salvar fonte/hashes; executar :feature:responsible-gaming:testDebugUnitTest e gates de arquitetura/Compose/DS offline. Expected: sucesso, registrar quantidade.
- [x] Criar ResponsibleGamingCoordinationUiTest.kt com os nove casos da spec via API pública; incluir visitorAssessmentKeepsInitialPublicFormIntroWithoutQuestionReadOrErrorClaim e welcomeIsPublicAndSeguirFinishesWelcomeBeforeFormPromptThenDeclineReturnsHome existentes.
- [x] Executar os onze casos no AVD dedicado antes da extração. Expected: todos passam; corrigir somente premissas do teste se necessário, sem alterar produção.

## Task 2 — Extração

Consumes: snapshot local e testes verdes. Produces: sete arquivos descritos na spec, mantendo as funções e callbacks.

- [x] Extrair os corpos exatos e limpar imports; manter private onde não houver uso entre arquivos e internal somente onde necessário.
- [x] Reconstruir/comparar assinatura, coordenação, estilo e declarações contra o snapshot. Expected: igualdade após reverter somente visibilidade.
- [x] Rodar testes JVM, compile debug/release e três gates offline. Expected: sucesso, ViewModel/regras inalterados por hash.

## Task 3 — Verificação e entrega

Consumes: extração e baseline. Produces: evidências finais, documentação atualizada e implementação sem commit.

- [x] Reexecutar os mesmos onze casos Android e responsible-gaming check/cobertura/lint, compile release, app PRD debug build/unit e nove gates. Expected: todos passam ou divergência preexistente demonstrada no baseline.
- [x] Obter uma revisão independente do escopo local, somente leitura, com Review Focus acima. Expected: nenhum problema importante pendente.
- [x] Atualizar APP_ARCHITECTURE e evidências; conferir hashes/whitespace, restaurar AVD/pacotes/display exatos e encerrá-lo.
