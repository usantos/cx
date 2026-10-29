# Payment terminal extraction implementation plan

> **For agentic workers:** Use superpowers:executing-plans inline, with one fresh-context read-only review at the end.

**Goal:** Separar coordenação/processamento/falha/Pix/componentescartão/formatadores preservandoVM/comportamento.
**Architecture:** Rota mantém coleta/efeito/destinos; cincoarquivosnovos preservam apresentação, callbacks atuais e APIs internas existentes.
**Tech Stack:** Kotlin, Compose, coroutines, JUnit, AndroidUI, Gradle.
**Spec:** docs/superpowers/specs/2026-10-18-payment-terminal-extraction-design.md.

## Global Constraints

Checkout codex/migracao-compose e alteraçõesanteriores preservados; sem commit/worktree novo nem mudanças VM/regras/domínio/recursos/dependências/gates. API/defaults, owners/keys/collect, textos/estilos/card APIs exatos; semnovasoperações financeiras.

## Review Focus

- OrientaçãoPix sóhabilita submit apósaceite; row/checkbox alternam uma vez; back/complete continuam efeitos doVM.
- TrocarVM liga callbacksatuais e cancela coletor antigo; preservar captura original dos callbacks dentro deLaunchedEffect(viewModel).
- Confirmation/success compartilham mesma tela; primary/secondaryopcional/ajuda usam destinos originais sem concluir pagamento.
- Falha primary→payment/method e back/secondary→home, não callbackdebackdoVM.
- Componentescartão mantêm APIs/defaults/renderização,3/4CVV,ProviderHeadercomsemcallback; formatter preserva primeiraocorrência/prioridade/spans/padding.

## Task 1 — Referência e caracterização

- [x] BaselineJVM/trêsgates. Expected:sucesso oufalhaanteriorexplicitada.
- [x] Quatro PaymentTerminalFormattersTest antesextração. Expected:REDprivateaccess dosdoishelpers.
- [x] Seis UIpublicroute/VMreal paraReviewFocus, executar todos24existentes+6novos contraoriginal. Expected:30aprovados.

## Task 2 — Extração

- [x] Extrair cincoarquivosdescritosnaspec e ligar três callbacksPixatuais; coordenaçãofica na rota.
- [x] Reconstituir/corpos/APIs/keys/formatters versus snapshot, revertendobindings/visibilidade. Expected:igualdade e apenasrota alterada entreKotlinanteriores.
- [x] JVM/debug/releasecompile/trêsgates. Expected:99JVMGREEN incluindo4novos.

## Task 3 — Validar e entregar

- [x] FullAndroid30,featurecheck/lint/cobertura95.01%,releasecompile,PRDdebugAPK/appunit e9gatesoffline. Expected:semregressões/gatesintactos.
- [x] Revisãoindependente somenteleitura conformeReviewFocus. Expected:nenhumproblemaimportantependente.
- [x] Atualizararquitetura/evidências/hashes/whitespace, restaurarAVD eencerrá-lo.
