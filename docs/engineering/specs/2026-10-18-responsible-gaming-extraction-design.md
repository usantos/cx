# Extração das telas de jogo responsável

Separar a fonte local de 856 linhas de ResponsibleGamingRoute.kt em coordenação, autoavaliação, suspensão, repasses sociais e formatadores, mantendo ViewModel e comportamento. A referência local salva em build/verification/responsible-gaming-refactor-20261018 é a autoridade para comparação; preservar o trabalho anterior sem commit.

## Arquivos e contratos

- ResponsibleGamingRoute.kt conserva a assinatura pública/defaults, collectAsState, LaunchedEffect(viewModel), consumo dos dois efeitos e callbacks. Mantém ProvideTextStyle e o when das quatro rotas, inclusive erro de rota desconhecida e fallback onSubmitSuspension. Não adicionar carregamento, lifecycle collection, novos owners ou chaves de efeito.
- AssessmentStartScreen.kt contém a tela inicial e WhiteOutlineButton privado.
- AssessmentFormScreen.kt contém formulário, questão e resultado. Mantém precedência loading/erro/submitted/privado sem pergunta, botões, respostas, resultado e restrição de fechar.
- SuspensionScreen.kt contém seleção, ciência, confirmação e resultado, preservando exatamente os guards, callbacks e uso de canSubmitSuspension.
- SocialTransfersScreen.kt contém scaffold e conteúdo de repasses, sem mudar cálculo/ano/callbacks.
- SocialTransfersComponents.kt contém blocos e categorias, com o modelo visual local. Componentes chamados de outros arquivos ficam internal; detalhes continuam private.
- ResponsibleGamingComponents.kt contém LoadingState, ErrorState e LegacyTealButton compartilhados.
- ResponsibleGamingFormatters.kt contém SocialAmount e as quatro funções existentes, com os mesmos nomes, assinatura e visibilidade internal; preservar Calendar, locale pt-BR, espaços substituídos, limiares, singular/plural e RoundingMode.DOWN.

Os sete arquivos novos recebem apenas estado/callbacks, nunca ViewModel. Aproveitar ResponsibleGamingAction existente; não criar novos bundles ou wrappers. Apenas mudanças de localização/imports/visibilidade estritamente necessárias; corpos/textos/recursos/layout/semântica inalterados. ResponsibleGamingViewModel, regras, contratos, dependências e gates permanecem intocados. Sem KMP nesta etapa.

## Validação

Salvar hashes de todos os Kotlin existentes e fonte local, executar baseline JVM/gates. Acrescentar nove testes Android pela rota pública no módulo app (infraestrutura de instrumentação já existente), sem alterar dependências: boas-vindas opcional/obrigatória, respostas/navegação anterior/finalização, resultado negativo, erro/retry/flag privada, suspensão com override e default, repasses/ajuda/retorno, troca de ViewModel. Usar somente casos de uso locais. Reexecutar os nove casos e dois testes existentes de acesso visitante/navegação antes/depois. Testes existentes de regras cobrem os formatadores.

Reconstruir estruturalmente o original após reverter apenas extração/visibilidade, verificando API, estilo herdado, efeitos, callbacks e todos os corpos. Rodar responsible-gaming check/lint/cobertura, release compile, app PRD debug build/unit e nove gates offline; revisão independente somente leitura. Restaurar pacotes/display exatos do AVD dedicado 5560 e encerrá-lo.
