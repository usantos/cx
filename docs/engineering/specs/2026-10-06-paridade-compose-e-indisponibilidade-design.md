# Paridade Compose com a base legado e tela de indisponibilidade

## Objetivo e referência

Concluir os ajustes de paridade da aplicação Compose usando como fonte de comportamento, textos, recursos e regras o código de `/Users/ubiracysantos/Projects/legado/simlo-app-android/`. A versão de produção 1.25 não está acessível pelo ADB nesta sessão; nenhuma equivalência física deve ser declarada sem capturas dessa versão. A branch `/Users/ubiracysantos/Projects/cx/` também permanece uma referência Android tradicional, não o destino da migração.

Todas as telas e interações próprias do aplicativo devem continuar em Jetpack Compose e CaixaDS. O WebView só pode ser apresentado pela API composable acordada para a fila; recursos de plataforma estritamente necessários (câmera, navegador seguro, intents) permanecem encapsulados atrás dos módulos existentes. Nenhum fluxo de compra, pagamento, aposta ou resgate de produção será acionado para gerar evidência.

## Tela e funcionalidade de serviço indisponível

Reusar e completar `StartupStatusScreen`/`StartupStatusDestination`; não criar uma segunda tela concorrente. Preservar a hierarquia, geometria, textos, ícone, versão, toolbar e botão descritos por `content_app_indisponivel.xml`, `activity_app_indisponivel.xml`, `AppIndisponivelActivity` e recursos relacionados.

O estado offline apresenta a mensagem de instabilidade de internet; com conectividade presente, apresenta a mensagem de indisponibilidade do aplicativo. O retry mantém a origem da falha: no caminho de parâmetros, repete a consulta, publica os parâmetros no estado compartilhado e retorna à tela chamadora apenas após sucesso; na validação inicial, consulta compatibilidade de versão e conclui a entrada somente após resposta válida. Loading bloqueia clique repetido e Voltar nos mesmos estados do legado. Erros continuam passando pelo tratamento de redirect/erro do fluxo chamador. Em flavors não produtivos, a seleção de ambiente só será migrada se os ambientes e persistência forem compatíveis com a configuração moderna; mudança de ambiente deve manter a confirmação e o encerramento exigidos pelo legado.

## Sete grupos de paridade

1. **Modalidades e apostas:** reproduzir estados manuais/selecionados, Surpresinha, preços, complementos por modalidade, ajuda e rolagem. Cobrir +Milionária 6–12 números e 2–6 trevos; Super Sete 7–21; espelho da Lotomania; mês do Dia de Sorte; seleção de time da Timemania; partidas da Loteca em concurso aberto. Usar parâmetros e recursos do legado como dados de referência.
2. **Diálogos e carregamento:** cobrir Teimosinha com gesto/deslize e confirmação, ajuda, favorita com teclado, sobrescrita, duplicidade, erros negociais, retry e estado/duração de loading.
3. **Cadastro e dados pessoais:** comparar tela inteira, rolagem, cursor, bordas de foco/erro, teclado/IME, campos inferiores, CEP, checkbox, limites e telas de confirmação. Usar dados sintéticos estruturalmente equivalentes.
4. **Compras e pagamento:** cobrir meios/cartões mascarados, CVV, foco/erro, Pix pendente/expirado/aprovado/falha, confirmações e retorno. Exercitar estados sintéticos via fixtures, sem transmitir pagamento.
5. **Resgates:** cobrir elegibilidade e telas finais de Pix, Mercado Pago, Lotérica e Agência com estado/beneficiário de fixture. Não confirmar resgate real nem gerar código de produção.
6. **Conteúdo e continuidade:** parear concursos, resultados, valores, ajuda/termos e links; validar HTML incluindo tabelas/quebras; confirmar destino da Federal no código/configuração legado; testar restauração de uma operação pendente após recriação/morte do processo com fake repositories.
7. **Validação global e matriz:** atualizar a matriz por tela/estado para separar implementação Compose, testes controlados, comparação estática com XML/recursos legado e captura física. Rodar compilação, testes JVM/Compose e smoke dos destinos alterados. Não rotular como 100% visual enquanto não houver screenshot da versão instalada 1.25 e comparação pós-alteração.

## Critérios de aceitação

- A indisponibilidade é alcançável a partir da inicialização e dos redirects existentes, distingue falta de rede de serviço indisponível e repete a operação correta da origem sem duplicá-la.
- Os sete grupos possuem cobertura identificável no código e testes, sem excluir estados mencionados acima. Cada diferença já conhecida fica registrada com evidência e limitação.
- Componentes e telas editados usam Compose/CaixaDS; não são introduzidos XML de layout ou widgets tradicionais de UI.
- Os fluxos de negócio são executados somente com backends falsos/fixtures durante validação.
- Compilação e testes passam para os módulos afetados. Captura física, se o aparelho permanecer não autorizado, fica registrada como bloqueio de homologação visual, sem mascarar essa pendência como implementação faltante.

## Partição de implementação

Executar primeiro a tela/função de indisponibilidade e depois os sete grupos, cada um como uma etapa própria: modalidades; diálogos/loading; conta; compras/pagamento; resgates; conteúdo/continuidade; matriz/validação final. Cada etapa deve ter um teste RED antes da alteração correspondente e teste GREEN depois. A matriz recebe um status verificável por critério, sem marcar todo o grupo como concluído só pela compilação.
