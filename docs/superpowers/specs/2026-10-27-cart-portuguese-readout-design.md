# Leitura integral dos blocos do carrinho em português

Requisito explícito do usuário durante a validação: “Cada bloco da modalidade deve ser lido por inteiro e por extenso em portugues”. Aplicar aos blocos do carrinho, preservando visual, headers, conteúdo, ações e baselines.

A apresentação compõe uma descrição por aposta usando os dados existentes: modalidade, concurso, números/colunas, trevos, complementos, marcadores, repetição, seleções Loteca, cotas/expiração ou quantidade do Combo e valor. Numerais e moeda serão falados por extenso em pt-BR. Identificadores preservam zeros e caracteres; números não exibidos da Surpresinha não serão revelados. Não inventar resultados ou detalhes não carregados.

Uma descrição agrupada deve permitir leitura contínua sem fragmentar cada Text. Excluir, detalhes e legenda continuam com foco e callbacks próprios. As seleções somente de leitura da Loteca serão descritas no bloco, mantendo a legenda operável. Não usar clearAndSetSemantics sobre ações de um cartão inteiro.

A escolha técnica é um mapper de apresentação com formatação portuguesa pura compartilhada em Core Presentation, sem dependência nova. A exploração constatou que RuleBasedNumberFormat não integra o SDK público Android do projeto; não usar API oculta ou adicionar ICU4J inteiro. Somente fundir a árvore manteria abreviações/dígitos; escrever números por extenso no visual alteraria o layout. A descrição semântica atende diretamente ao requisito. Estado segue no ViewModel/ReduxKotlin; regras e operações não mudam.

Aceitação: testes dos componentes reais devem falhar sem o bloco agregado e passar com texto completo em português; provar callbacks independentes, valores com centavos, colunas Super Sete, Loteca, Combo/bolão e Surpresinha. Instalar no mesmo app, conferir com TalkBack no Samsung e registrar a resposta humana sem inferir fala de screenshots.

Extensão explícita do usuário: “Mesma coisa nos resultados, onde der pra ler por blocos faca.” Reutilizar a formatação em blocos de concurso, sorteios e premiação da feature Results; manter ações de busca/navegação independentes.
