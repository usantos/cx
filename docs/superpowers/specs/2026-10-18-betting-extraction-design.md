# Extração da apresentação do simulador de apostas

Separar `BettingGameRoute.kt` em coordenação, componentes por modalidade e formatadores, mantendo o ViewModel, a API pública e o comportamento atual. Referência: fonte local em `build/verification/betting-refactor-20261018/BettingGameRoute.before.kt`, incluindo todas as extrações anteriores.

## Organização e contratos

- A rota mantém os seis estados booleanos locais, chave de descrição Loteca, lifecycle, BackHandler, bloqueio do shake, efeito de inclusão, diálogos e callbacks de navegação. Não mudar remember para rememberSaveable nem consumir eventos de modo diferente.
- `BettingGameActions` conecta métodos do ViewModel atual aos componentes; criado por composição, sem lembrar um ViewModel antigo. O ViewModel e `BettingSportsContent.kt` permanecem intactos.
- `BettingEntryContent.kt`: manual/Surpresinha e contador. Timemania continua usando o componente existente por um slot Compose ligado na rota; Loteca já é chamada na rota pelo componente existente.
- `BettingCloverContent.kt`, `BettingSuperSevenContent.kt`, `BettingLuckyMonthContent.kt`, `BettingLotogolContent.kt`: corpos existentes por modalidade, estado e callbacks.
- `BettingNumberGrid.kt`, `BettingGameHeader.kt`, `BettingGameFooter.kt`, `BettingModeToolbar.kt`, `BettingGameComponents.kt`: visuais comuns e geometria existente, sem mudanças de recursos, cores, textos, medidas, tags ou semântica.
- `BettingGameFormatters.kt`: rótulos de recorrência, título fallback/especial e data do cabeçalho. Mapeamento de arte permanece no cabeçalho, cálculo de altura permanece na grade.

Preservar leitura do estado atual nos handlers, guards e ordem de transições. Limpar/reduzir quantidade da Loteca continua atualizando a chave de descrição. Estados e efeito de shake continuam sendo criados antes dos ramos loading/erro. Complementos, confirmação de saída, favoritos, inclusão/duplicidade e priorização dos diálogos permanecem iguais. Nenhuma alteração de domínio, ViewModel, dependências, gates, recursos ou adaptação KMP.

## Verificação

Salvar hashes e executar JVM/build/gates de referência. Executar35 casos Android controlados antes/depois: smoke8, saída1, recuperação1, diálogos2, opções6, trevos2, Timemania1, origem do prompt2, swipe1, modalidades11. Adicionar testes JVM para os quatro formatadores e observar a inacessibilidade antes da extração. Comparar tokens de assinatura, coordenação, callbacks e todos os corpos extraídos contra a referência. Verificar check/cobertura/lint de betting, compilação release da feature, build/testes PRD do app e nove gates. Revisão independente ao final. Usar apenas AVD dedicado5560; preservar AVD5556 e alterações anteriores. Sem commit.
