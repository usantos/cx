# Acessibilidade integral e auditoria arquitetural

## Intenção e critérios aceitos

O usuário solicita implementar acessibilidade no App e nas 14 features, auditar a arquitetura de todo o projeto e corrigir os desvios encontrados, mantendo MVVM/Clean Architecture/ReduxKotlin, headers e baselines; atualizar documentação e Samsung. Confirmou WCAG2.2 AA e recomendações Android para TalkBack, foco, teclado, texto ampliado e áreas de toque como referências. WCAG2ICT orienta aplicação a software nativo; o resultado não será apresentado como certificação formal sem evidência correspondente.

## Abordagem escolhida pelo executor

Uma base compartilhada no CaixaDS resolve contratos de inputs, seleção, links, erros, dialog/wheel, escala e cores de texto de corpo; correções específicas nas features completam significado contextual e fluxos. Testes cobrem componentes e telas reais, com matriz explícita por feature. Auditoria paralela por donos e revisão independente complementam os gates.

Alternativas consideradas: alterações isoladas em cada tela repetiriam contratos e divergiriam entre features; um wrapper global de semântica esconderia ações/campos e não corrigiria papéis, estados ou regras fora de Domain. A abordagem compartilhada com complementos locais mantém DRY e as fronteiras existentes. Esta é uma decisão de implementação, não um redesenho visual solicitado pelo usuário.

## Escopo e restrições

- Todo o grafo atual de68 módulos: App, Core, todas as camadas das14 features e sample. Baseline verificado anterior:4272 arquivos,43 ViewModels (42 com UiState Redux, AccountStatus somente efeitos).
- Auditar todos os donos de UI inventariados (295 arquivos com Composable nas features), componentes/recursos compartilhados, nativos/WebView/HTML, estados vazios/erro/loading/seleção, navegação e formulários. Inventário de arquivos não equivale a cobertura dinâmica de todos os estados.
- Preservar visual, cores, tipografia e geometria dos headers. CurrentCartScaffold.kt e CaixaDSScreenComponents.kt permanecem byte a byte conforme hashes protegidos; sem mudança nesses arquivos. Ajustes semânticos em consumidores/controles não alteram pintura dos headers. A escala escolhida pelo usuário é respeitada naturalmente, sem fixar fontScale global.
- Não expandir baselines, supressões, exclusões ou reduzir mínimos. Preservar contratos e assertivas anteriores; testes específicos de fonte ampliada verificam adaptação sem enfraquecer verificações de1x.
- Estado de aplicação local aos ViewModels via ReduxKotlin; reducers puros e ações semânticas. Efeitos/usecases/jobs sob lifecycle; validações/preço/expiração de negócio em Domain. UI mantém foco/teclado/URI/Intents/DOM e representações visuais.
- Sem novos módulos, middleware global, backdoor de sessão, API financeira ou biblioteca de widget paralela. Novas dependências apenas de teste se necessárias, pelo Version Catalog/BOM existentes.
- Trabalhar no checkout atual, preservando alterações anteriores; sem commit/push ou limpeza global. Root executa Gradle sequencialmente.
- Samsung recebe somente o mesmo APK principal, sem limpar dados. Medir carrinho atual antes da instalação; o usuário pode adicionar apostas entre rodadas. Nenhuma compra/reserva/pagamento/credencial real em testes.

## Contratos de acessibilidade

1. Nome/papel/estado consistentes para controles; material defaults preservados. Indicadores decorativos sem foco; não adicionar descrições redundantes a todo Text/Image.
2. Um alvo por escolha/ação, controle vinculado ao rótulo, estado selecionado/marcado/expandido, coleções e resultados com contexto (modalidade/time/coluna/acertos).
3. Campos identificados mesmo preenchidos, senha com privacidade, instrução/erro específico associado ao editável; anúncios de mudanças pertinentes sem repetir toda tela ou cronômetros a cada segundo.
4. Operação por TalkBack, teclado/dpad e ações alternativas a gestos; foco/traversal e diálogos com conteúdo rolável/ações alcançáveis.
5. Respeito à fonte do sistema até escala2 nos cenários testados; remover conversões que cancelam ampliação; reflow fora dos headers quando necessário, sem perder conteúdo/ações. Testar1,1.3,2 e viewport estreito/baixo.
6. Alvos Android de48dp quando aplicável; medir alvos reais expandidos, colisões/vizinhança e agrupamentos, sem inferir falha apenas da caixa visual de um ícone pequeno.
7. Contraste de texto de corpo4.5:1 (3:1 para grande) e informação independente da cor. Cores acessíveis locais/de corpo, sem alterar paleta de header ou tokens antigos usados por cabeçalhos.
8. Links anotados com ações semânticas e teclado preservando confirmação/validação existente. WebViews preservam DOM e escala; sem substituir documento por uma descrição única. Conteúdo remoto/SDK externo e autenticação delegada terão fronteiras e limites de homologação explícitos.

## Achados iniciais e responsabilidades

Evidências detalhadas: build/verification/a11y-full-audit-20261027/{ds,features-a,features-b}-audit.md. App/Core/grafo/gates continuam sob inspeção root.

- CaixaDS: fontScale fixo, dp→sp inverso, rótulos/erros de input, escolha/switch/tab sem estado, alvos duplicados, diálogo/wheel em texto ampliado, contraste de corpo e HTML sem links acionáveis.
- Features A: consentimento e termos, combo clicável sem nome, notificações com switch sem rótulo, seleção contextual de jogos/filtros, ampliação; regras de preço/seleção/expiração em Presentation e coordenação de resultados Home na composição.
- Features B: radios/checkboxes de formulário e Pix sem associação, abas, busca, resultados/SuperSete/Loteca/premiação sem associação semântica, FAQ com links apenas por offset; política Pix de Tickets dentro de Screen e respostas obsoletas de Support sem identidade/cancelamento.

## Interfaces compartilhadas

CaixaDS mantém assinaturas existentes sempre que possível. Novo helper público de apresentação converte StringAnnotations URL existentes em LinkAnnotations acionáveis, preservando spans e callbacks de validação/confirmacão da feature (nome exato publicado antes de consumidores alterarem código). Semântica de Material não é apagada com clearAndSetSemantics em campos editáveis.

Movimentos de regras reutilizam políticas Domain existentes, evitando segundo cálculo de preço/expiração. Home passa a possuir pedidos/cache/identidade no ViewModel; expansão puramente visual pode permanecer na View. Support publica resultado somente para requisição/aba/seção atuais e invalida resultados cancelados mesmo quando a implementação remota ignora cancelamento.

## Validação e entrega

Regressões RED antes das mudanças; preservar contratos existentes. Testes semânticos e de interação por feature, testes de escala/contraste compartilhados, verificações ATF quando compatíveis e testes instrumentados controlados para plataformas/HTML. allUnitTests, compilações AndroidTest afetadas, Lint/Detekt/gates, formatação focada, hashes e build PRD. Sem declarar cobertura nova sem medir.

Samsung: baseline de conteúdo/ajustes, atualização principal, navegação pública com serviços assistivos oficiais disponíveis, fonte ampliada e restauração dos ajustes temporários. Autenticação/fluxos financeiros validados com fixtures; não contornar gates. Se ativar TalkBack exigir consentimento do aparelho, solicitar ao usuário depois de o APK estar concreto e revisável. Registrar separado o que foi estático, automatizado, instrumentado, observado no Samsung ou não homologado. Guia CAIXA renderizado e inspecionado, matriz de todas as features e relatório arquitetural com status por achado.

## Fontes primárias

- https://www.w3.org/TR/WCAG22/
- https://www.w3.org/TR/wcag2ict/
- https://developer.android.com/develop/ui/compose/accessibility/semantics
- https://developer.android.com/develop/ui/compose/accessibility/api-defaults
- https://developer.android.com/develop/ui/compose/accessibility/testing

Consultadas em27/10/2026. Referências orientam critérios, não são alegação de conformidade do checkout atual.
