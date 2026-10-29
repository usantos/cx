# Results extraction implementation plan

> Executar inline com superpowers:executing-plans e uma revisão independente somente de leitura ao final.

**Goal:** separar coordenação, modalidades, pesquisa, detalhes, premiações e mapeadores sem alterar ViewModel ou comportamento.

**Architecture:** rota pública intacta; estado local da pesquisa permanece no detalhe; componentes recebem valores e callbacks atuais. Extrair os corpos existentes sem redesenhar a tela.

**Tech Stack:** Kotlin, Compose, JUnit, Android UI, Gradle/Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-results-extraction-design.md.

## Global Constraints

Preservar trabalho anterior e continuar inline, sem commits/worktrees. Somente ResultsRoute.kt existente muda na produção; criar seis arquivos no mesmo pacote. Não modificar ViewModels, recursos, regras, dependências ou gates.

## Review Focus

- Efeito showDetail e retorno condicionado à seleção: mesmas chaves, captura e ordem.
- Pesquisa: visibilidade e foco persistem com o mesmo owner; abertura limpa o campo; loading fecha a pesquisa; validação não fecha nem inicia consulta.
- Callbacks usam o ViewModel atual durante troca na mesma composição.
- Anterior/seguinte mantêm limites e bloqueio durante loading; erro precede resultado armazenado.
- Mesmas modalidades e ordem visual, números ordenados com exceção do Super Sete, premiações com zero ganhadores sem valor e suplemento sem resumo da home.

## Task 1 — Referência e caracterização

- [x] Capturar hashes e referência; executar :feature:results:check, compileReleaseKotlin e três gates offline. Esperado: suíte e cobertura configurada aprovadas.
- [x] Adicionar ResultsCoordinationUiTest no androidTest do app, sem dependências novas. Testar seleção/retorno, pesquisa inválida e consulta assíncrona, troca de ViewModel e callbacks. Executar com PublicResultsParityUiTest contra o código original. Esperado: todos aprovados.

## Task 2 — Extração

- [x] Extrair ResultModalities.kt, ResultContestSearch.kt, ResultDetailScreen.kt, ResultContent.kt, ResultPayouts.kt e ResultMappers.kt, mantendo assinaturas públicas e corpos conforme especificação.
- [x] Comparar corpos e hashes; somente um arquivo Kotlin existente de produção pode mudar. Atualizar APP_ARCHITECTURE.md.

## Task 3 — Validação final

- [x] Executar check do módulo, compilação release, testes JVM do app, APK PRD debug, nove gates offline e os mesmos testes Android de caracterização/paridade. Esperado: verde, gates e cobertura configurada intactos.
- [x] Revisão independente somente de leitura; resolver achados relevantes com verificação.
- [x] Conferir whitespace e evidências, restaurar pacotes/tela e encerrar o emulador dedicado.
