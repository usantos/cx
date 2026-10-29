# Prize redemption extraction implementation plan

> Execute inline com `superpowers:executing-plans`; uma revisão independente ao final.

**Goal:** Separar coordenação, telas por canal e PDFs sem alterar o comportamento.
**Architecture:** A rota mantém efeitos/decisões e conecta callbacks às telas. O helper Compose de comprovantes mantém o estado e integração Android existentes.
**Tech Stack:** Kotlin, Jetpack Compose, ActivityResultRegistry, coroutines, Gradle/JUnit.
**Spec:** `docs/superpowers/specs/2026-10-17-prize-extraction-design.md`.

## Global Constraints

- Referência local, preservando o checkout já modificado; sem commits ou mudanças de dependências/gates.
- Assinatura pública, estado não persistido, ordem/chaves dos efeitos, layouts e tratamento de falhas permanecem iguais.
- Nenhuma adaptação KMP nesta etapa.

## Review Focus

- Resultado cancelado ou tardio de CreateDocument não grava nem abre comprovante sem pendente.
- Cache deve funcionar tanto no clique quanto em nova emissão do flow, com os nomes de aposta/prêmio distintos.
- Falha de visualizador continua silenciosa e não invalida cache; falhas de escrita não recebem novo tratamento.
- Confirmação Pix/MP e seleção inicial mantêm guards, consumo único e destinos de retorno distintos.
- Callbacks de campos não mudam a ordem de transições; telas preservam textos/recursos/medidas e efeitos QR/haptics.

## Task 1 — Caracterizar a referência

- [x] Salvar fonte e hashes dos arquivos existentes.
- [x] Executar baseline JVM/gates e testes Android existentes.
- [x] Adicionar testes Android públicos de comprovantes: gravação/abertura/cache, cancelamento, reemissão do flow, ausência de visualizador e comprovante de prêmio.
- [x] Executar esses testes contra a fonte anterior à extração.

## Task 2 — Extrair

- [x] Criar `PrizeOptionsScreen.kt`, `PrizeLotteryScreen.kt`, `PrizeAgencyScreen.kt`, `PrizePixScreen.kt`, `PrizeMercadoPagoScreen.kt`, `PrizeSuccessScreen.kt` e `PrizeRedemptionComponents.kt` com os corpos existentes.
- [x] Substituir dependências de ViewModel em Pix/MP por callbacks tipados, conectados na rota.
- [x] Criar `PrizeReceiptManager.kt`; mover o bloco Compose de PDFs e substituir os dois acessos ao cache por `openOrDownload(String, () -> Unit)`.
- [x] Conferir assinatura, coordenação e corpos por comparação estrutural e executar verificação JVM/compilação.

## Task 3 — Validar e encerrar

- [x] Reexecutar todos os testes Android do retail no AVD dedicado.
- [x] Executar `:feature:retail:check :feature:retail:compileReleaseKotlin :app:assemblePrdDebug :app:testPrdDebugUnitTest` e os nove gates estruturais, offline.
- [x] Obter revisão independente, resolver achados relevantes e conferir escopo/whitespace.
- [x] Atualizar arquitetura/evidências e limpar/encerrar somente o AVD iniciado pela tarefa.

## Resultado e limite conhecido

Rota1338→161 linhas; telas por canal sem ViewModel e helper PDF54 linhas. Comparação estrutural confirmou assinatura/coordenação/efeitos/PDF e oito grupos visuais idênticos após desfazer apenas a extração. Revisão independente aprovada sem achados.

Retail check/cobertura/lint, compilação release da feature, build PRD debug do host,184 testes JVM e nove gates passaram. A execução conjunta com `--continue` terminou com exit1 exclusivamente em `connectedDebugAndroidTest`:22/23 aprovados, sem skips; os cinco casos de PDF passaram antes/depois. A falha visual `installedPrizeSummaryRetainsTheProductionBottomCoordinate` já existia na fonte anterior: esperado880px, observado960px nas duas execuções1080×2408/450dpi. Layout, teste e gates permanecem intactos. Corrigir essa divergência não faz parte desta extração sem mudança de comportamento.

Evidências em `build/verification/prize-refactor-20261017/`, incluindo fontes, relatórios antes/depois, `final-attestation.json` e revisão. AVD5560 dedicado restaurado/encerrado; AVD5556 preservado. Alterações sem commit.
