# Correção do filtro de Minhas Apostas e do aceite de contrato

## Escopo e causas

- O filtro guardava `TicketFilterOption.valor`, mas `BetFilters.query()` convertia novamente posições `1/2/3` para códigos `1/4/6`. Ao escolher os valores reais `4` (Pagas) e `6` (Prescritas), a consulta omitia `situacao` e retornava resultados sem esse filtro. A consulta deve preservar diretamente o código configurado pelo serviço.
- O rodapé de aceite usava altura fixa e posicionava o botão em `55dp`, sobrepondo o texto da declaração quando a fonte aumentava. Seleção e botão devem ocupar linhas sucessivas; o rodapé cresce com o texto, mantendo IDs e estilo.
- A tela de aceite pode ser a entrada inicial (`MockStartDestination.TERMS_REQUIRED`) e não possuir tela anterior. Nesse caso, apenas `popBackStack()` não oferece destino de retorno. Voltar deve abrir Home sem aceitar o contrato nem alterar a sessão; navegação com uma tela anterior continua retornando a ela.

## Registro de execução

- Preservadas as alterações de resgate Pix já presentes no checkout.
- Comparados `TicketsUiState`, seletor, configuração de filtros e `SituacoesApostaFiltroEnum`/`FiltroApostasAdapter` do legado.
- Antes da correção de filtros: três testes executados, dois falharam exatamente pela omissão de `situacao`.
- Após a correção: 22 testes de `TicketsViewModelTest` passaram, incluindo seleção de Todas/Pagas/Prescritas e limpar.
- Termos: toque normal funciona; teste com largura de 320dp e fonte 1,5 reproduziu falha ao tocar o texto da declaração. Teste de retorno durante leitura pendente passou; o carregamento não foi alterado por hipótese.
- Inspecionada a tela instalada de Minhas Apostas no emulador já aberto; o diálogo de filtros foi aberto e fechado sem aplicar alterações. Nenhum resgate, aposta ou aceite real foi enviado.

## Validações finais

- PASSOU: `:feature:tickets:presentation:testDebugUnitTest` — 142 testes, zero falhas, erros ou ignorados.
- PASSOU: teste de fonte ampliada após o rodapé empilhado.
- RED confirmado: os dois novos testes de retorno na raiz (seta e sistema) falharam antes do ajuste de navegação.
- PASSOU: revisão independente somente leitura de Tickets/Support, sem achados.
- PASSOU: formatação Kotlin dos dois módulos — 157 arquivos, zero violações; `git diff --check`.
- PASSOU: `:feature:support:presentation:testDebugUnitTest` — 49 testes, zero falhas, erros ou ignorados; inclui retorno pela seta/sistema na raiz e toque na declaração com fonte ampliada.
- PASSOU: `:feature:support:support_ui:testPrdDebugUnitTest` — três testes, zero falhas, erros ou ignorados.
- PASSOU: `verifyCleanArchitecture`, `verifyAutomationIds` e `:app:assembleTqsDebug`.
- Total das suites executadas: 194 testes, todos aprovados. Invocações Gradle sequenciais com `PATH=/opt/homebrew/bin:$PATH`, `--no-daemon` e `-PpythonExecutable=/opt/homebrew/bin/python3`.
- Não executados: suíte global de todos os módulos/ambientes, Android Lint/cobertura global, backend real e aceite real de contrato.
- Ferramentas crapper/dryer/mutator não disponíveis; não há resultados dessas ferramentas nesta entrega.
