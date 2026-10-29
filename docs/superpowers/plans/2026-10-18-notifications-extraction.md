# Notifications extraction implementation plan

> **For agentic workers:** Use superpowers:executing-plans inline, with one fresh-context read-only review at the end.

**Goal:** Separar coordenação, mensagens, swipe, preferências, diálogos e mapeadores preservando VM/comportamento.
**Architecture:** Rota mantém coleta/efeito Android e escolha dos destinos. Seis novos arquivos contêm apresentações com estado e callbacks tipados ligados ao VM atual.
**Tech Stack:** Kotlin, Compose, coroutines, Android Intents, JUnit, Gradle/Jacoco.
**Spec:** docs/superpowers/specs/2026-10-18-notifications-extraction-design.md.

## Global Constraints

Checkout codex/migracao-compose e alterações anteriores preservados; sem commit/worktree novo nem mudanças de VM/regras/domínio/recursos/dependências/gates. Manter API, collectAsState, LaunchedEffect(viewModel)/collectLatest, Intents, nomes/keys/estados/layouts.

## Review Focus

- Lista/busca/não lidas preservam projeção e preview duplicado enquanto expandido; ids mantêm chave lazy.
- Swipe com scroll aberto fecha faixa antes de expandir; duas ações precisam consentimento e preservam id/ordem.
- Preferências ALL e por categoria continuam independentes; ajuda e back usam callbacks originais, sem nova carga.
- Confirmação/aviso/erro preservam ordem e dismiss; falha não apaga itens.
- Troca do VM liga callbacks atuais e cancela coletor antigo; compartilhar preserva chooser/MIME/texto exatos sem lançar aplicativos externos nos testes.

## Task 1 — Referência e caracterização

Consumes: fonte/testes atuais. Produces: snapshot/hashes e resultadosbaseline.

- [x] Baseline JVM/três gates e featurecheck com todosAndroid antes de extração. Expected: sucesso ou falha anterior identificada.
- [x] Dois contratos NotificationPresentationMappersTest (ícones de5categorias e TextStyle/defaults). Expected: RED privateacesso antes da extração.
- [x] Adicionar casos de rota pública com VM real/fixture local para busca, swipe close-before-expand, consentimento e troca deVM/compartilhamento; rodar todosAndroid contra produção original. Expected: baselineverde.

## Task 2 — Extração

Consumes: fonte e baseline. Produces: seis arquivos novos e rota coordenadora.

- [x] Extrair mensagens/item/swipe/preferências/diálogos/mapeadores mantendo corpos e callbacks atuais.
- [x] Reconstruir cada declaração/coordenador revertendo bindings/visibilidade. Expected: igualdade e somente rota alterada entreKotlinanteriores.
- [x] JVM/debug/releasecompile e três gates. Expected: contratosGREEN e suiteexistenteaprovada.

## Task 3 — Validar e entregar

Consumes: extração. Produces: evidências e revisão.

- [x] Rodar featurecheck (todosAndroid/JVM/lint/cobertura95.01%), releasecompile, PRDdebugAPK/appunit e nove gates. Expected: semregressões/gatespreservados.
- [x] Revisão independente somente leitura conforme Review Focus. Expected: nenhumachadoimportantependente.
- [x] Atualizar arquitetura/evidências; verificar hashes/whitespace, restaurar AVD e encerrá-lo.
