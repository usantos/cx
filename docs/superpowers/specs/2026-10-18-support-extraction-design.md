# Extração da tela de suporte

Separar composição, abas, FAQ, respostas HTML, links, ajuda por modalidade e formatadores, preservando o ViewModel e todo o comportamento. Continuar inline no checkout existente sem commits, worktrees ou instalação global de agentes.

## Organização

- SupportScreen.kt conserva a assinatura pública, o loading global antes de todos os ramos e os retornos antecipados de termos somente leitura e ajuda por modalidade. O ramo comum chama SupportContent com estado, ação e modifier atuais.
- SupportContent.kt recebe o Scaffold e seu conteúdo intactos: título, customTopBar, rodapé de aceite, abas e prioridade erro/FAQ/HTML.
- SupportTabs.kt recebe SupportTabs, mantendo medição dos textos, fórmula das larguras e ordem das abas.
- SupportFaqContent.kt recebe FaqContent, FaqSections e FaqQuestions, conservando chaves, animação, IDs e FaqAnswer somente quando expandida.
- SupportAnswerContent.kt recebe FaqAnswer e HtmlContent. LocalContext, pendingUrl com remember(answer) e anotações com remember(answer,linkColor) continuam no mesmo owner e ramo. FaqAnswer continua texto simples anotado; HtmlContent continua WebView com HTML original, CSS e links intactos.
- SupportFaqLinks.kt recebe apenas o builder supportAnswerText(answer,linkColor) e a folha SupportAnswerLinkDialog(url,context,onDismiss). O cache chama o builder dentro do remember original; a folha continua dentro de pendingUrl?.let. Confirmar limpa o pendente antes do Intent ACTION_VIEW/Uri.parse/resolveActivity/startActivity; cancelar limpa o pendente. A validação supportAnswerUrl existente fica intacta.
- GameHelpScreen.kt recebe GameHelpScreen e HelpMetadata, mantendo cores/drawables especiais, scroll local, AnnotatedString.fromHtml e as chamadas a gameHelpDate existentes.
- SupportTextFormatting.kt recebe legacyCapitalized e supportRegularStyle, com corpos, locale implícito e estilos intactos.

Sete arquivos novos de produção; somente SupportScreen.kt existente muda na produção. SupportRoute, ViewModel, reducer, contrato, SupportAnswerLinks, GameHelpDate, ReadOnlyTermsScreen, recursos, dependências e gates ficam intactos.

## Verificação

Referência e hashes dos Kotlin e demais fontes/recursos/configurações capturados antes da mudança. Check do módulo, release e três gates antes da extração. Três novos testes de comportamento na tela pública complementam os quatro Android existentes: lifetime/cancelamento de links, ações atuais das abas/aceite e loading/metadados/retorno da ajuda. Executar todos contra a implementação original e depois contra a extração.

Comparar corpos reconstruindo somente os componentes extraídos e os bindings. Check, lint, cobertura configurada, release, todos os Android do suporte, JVM do app, APK PRD debug e nove gates ao final. Aplicar os perfis refactoring-specialist e code-reviewer do catálogo VoltAgent solicitado, em subagentes da sessão; guardar commit e hashes dos perfis. Revisão final somente de leitura. Restaurar pacotes/tela e encerrar apenas o emulador dedicado.
