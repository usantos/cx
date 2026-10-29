# Favorite carts extraction implementation plan

> **For agentic workers:** Use superpowers:executing-plans inline, with one fresh-context read-only review at the end.

**Goal:** Separar coordenação/lista/detalhes/componentes/diálogos/formatadores, preservandoVM/comportamento.
**Architecture:** Duas rotas coordenam estado/efeitos/diálogos; conteúdo/folhas recebem estado ecallbacks atuais. Sete arquivos novos; textosinline movidos literalmente para formatadores.
**Tech Stack:** Kotlin, Compose, coroutines, JUnit, AndroidUI, Gradle/Jacoco.
**Spec:** docs/superpowers/specs/2026-10-18-favorite-carts-extraction-design.md.

## Global Constraints

Checkout codex/migracao-compose e alterações anteriores preservados; sem commit/worktree novo nem mudanças VM/domínio/recursos/dependências/gates. APIs/defaults, onHelp lista semuso, precedência/efeitos/keys/guardas/layout/textos exatos.

## Review Focus

- imported é consumido antes de onCartReady; cartDeleted e BackHandler preservam retorno, sem novo consumo/chaves.
- Flagprivada esconde lista mesmo preenchida, após loading/erro; não muda carregamento doVM. Retry e binds usamVM atual semremember.
- Exclusão de última aposta aindaexcluicarrinho; callbacks/IDs/consentimento/opções especial e -1/cancel/reset preservados.
- Resumo e detalhe diferem emTimemania; número100 vira00, SuperSete mantém colunas, surpresa usa contagens e complementos???, trevos/separadores/nulidade exatos.
- Medição e animação do nome/cartão conservam densidade/cache/tamanhos/tempo; lazykeys e estado de expansão permanecem nos owners originais.

## Task 1 — Referência e caracterização

Consumes: fonte/testes atuais. Produces:snapshot/baseline/REDcontratos.

- [x] BaselineJVM/trêsgates e fullfeaturecheck comtodosAndroid/lint/cobertura95.01%. Expected:pass oufalhaanteriorexplicitamenteidentificada.
- [x] Seis FavoriteCartFormattersTest (labelslista/detalhe, contagens, complementos, números, trevos) comvaloresliterais. Expected:REDunresolvedhelper antes da extração.
- [x] Caracterizar pela APIpública privateflag/importconsume/seleçãoespecial/VMreplacement/detailretry/help/back eúltimaaposta; executar seleçãofavoritos existente+nova contrafonteoriginal. Expected:verde.

## Task 2 — Extração

Consumes:snapshot/testes. Produces:setearquivosdescritosnaspec e rotascoordenadoras.

- [x] Extrair conteúdo/lista/detalhes/folhas/diálogos/formatadores, mantendo corpos/estado/efeitos e callbacks atuais.
- [x] Reverter somente extração/bindings/formatters/visibilidade e comparar todos corpos/APIs. Expected:igualdade, apenasrota alterada entreKotlinanteriores.
- [x] JVM/debug/releasecompile/trêsgates. Expected:GREENdosseiscontratos e suiteanterioraprovada.

## Task 3 — Validação e entrega

Consumes:extração. Produces:arquitetura/evidências/revisão.

- [x] Fullfeaturecheck(todosAndroid/JVM/lint/cobertura95.01%),releasecompile,PRDdebugAPK/appunit e novegatesoffline. Expected:semregressões, semfiltros/gatesalterados.
- [x] Revisãoindependente somenteleitura conformeReviewFocus. Expected:nenhumproblemaimportantependente.
- [x] Atualizararquitetura/registro, verificarhashes/whitespace, restaurarAVDdedicado eencerrá-lo.
