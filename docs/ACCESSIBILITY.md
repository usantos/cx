# LOTERIAS CAIXA — Acessibilidade do aplicativo Android

**Referência:** WCAG 2.2 nível AA, WCAG2ICT e recomendações Android. **Edição:** 27/10/2026. **Escopo:** App, componentes compartilhados e as 14 features. Critérios confirmados pelo solicitante. Este documento descreve implementação, evidências e limites; não declara certificação de conformidade.


## Resultado histórico da auditoria integral

allUnitTests: **3.325 aprovados / 0 falhos / 0 erros / 0 ignorados**, com XML frescos em 68 módulos. Lint, Detekt, gates de arquitetura/CaixaDS, APK PRD e compilações AndroidTest de App/DS/Retail aprovados. Ktlint: 155 arquivos alterados. Políticas: 184 testes Python + 11 hooks, todos aprovados.

Emulador Android16: 19 casos instrumentados aprovados. Samsung: 26 verificações nas duas rodadas finais, 2 cenários de escala normal/200% e 1 conferência humana com TalkBack aprovados; carrinho 5 apostas/R$22 preservado. Fonte/TalkBack temporários restaurados. A matriz distingue estes escopos de uma certificação de conformidade, cobertura global, release/R8 e homologação autenticada/financeira, que não foram realizadas.


## Rodada complementar no Samsung

A validação autenticada e as correções adicionais em 200% estão detalhadas em [Samsung - validação das 14 features](accessibility/SAMSUNG_VALIDATION_20261027.md). O usuário respondeu a oito roteiros manuais. As respostas de Cart/Results acrescentaram o requisito de leitura integral por extenso e não foram contabilizadas como aprovações. Há retestes do APK final, cenários indisponíveis na sessão e limitações de headers ainda abertos; não se declara homologação integral das 14 features. Os totais acima pertencem à auditoria anterior.

## Contratos de implementação

A View publica nomes, papéis, valores, seleção, erros e ações compatíveis com os dados que apresenta. O ViewModel mantém estado imutável em store ReduxKotlin local e coordena os efeitos. Domain mantém regras de aposta, preço, seleção e expiração; Data implementa suas portas. Semântica de acessibilidade não contém operações de rede, credenciais ou efeitos de domínio.

- **Texto:** respeitar a escala do sistema. Não fixar fontScale em 1 nem converter dp para sp para neutralizar a preferência. Campos e linhas de conteúdo usam altura mínima/adaptação; resultados podem rolar preservando a associação coluna/valor. Headers mantêm estilo e fonte existentes, com escala natural do sistema.
- **Campos:** nome estável também quando preenchidos; erro associado ao campo, mantendo edição, seleção e privacidade de senha. Não usar clearAndSetSemantics para apagar ações de entrada. Loading e disabled conservam estado próprio.
- **Escolhas:** um dono de ação para linha/checkbox/radio; nome, papel e estado no mesmo alvo. Não duplicar callbacks no indicador filho. Tabs publicam seleção. Estado não depende apenas de cor.
- **Resultados e apostas:** anunciar modalidade/contexto, coluna, acerto/erro e títulos das linhas de premiação. Elementos decorativos permanecem sem descrição duplicada.
- **Links:** `AnnotatedString.withCaixaDSAccessibleLinks` aceita tags URL/url preservando intervalos e estilos; publica LinkAnnotation.Clickable e chama o callback existente. Confirmação e validação de destinos continuam na feature. HTML compartilhado rejeita abertura externa de esquemas inseguros.
- **Diálogos e navegação:** ações alcançáveis por rolagem em viewport curto/fonte grande; teclado/Enter acionam uma vez. Indicadores filhos decorativos não mantêm ação/foco próprios quando a linha já é o dono da escolha. A roda tem faixa finita, valor atual, SetProgress, ações anterior/próximo e setas; as cópias visuais de rolagem circular não multiplicam os alvos acessíveis. Cada consumidor informa seu rótulo contextual.
- **WebView:** zoom acompanha a escala do sistema na criação e nas alterações; manter DOM nativo, headings/links e política de origem/JavaScript. Nunca substituir o documento inteiro por uma única descrição.
- **Contraste e toque:** usar cores acessíveis nos papéis de texto do corpo corrigidos; não alterar tokens globais que repintam headers. Avaliar área de toque efetiva, expansão automática Material e sobreposição entre controles; dimensões visuais isoladas não provam falha ou conformidade.

## Matriz de features e regressões

| Feature | Contrato específico corrigido | Evidência automatizada principal |
|---|---|---|
| Account | Consentimento nomeado e link nativo dos termos | AccountAccessibilityRegressionTest |
| Betting | Combo com identidade/preço; seleção Rapid/favoritos e contexto de placar; rodas nomeadas | BettingAccessibilityRegressionTest; testes existentes de seleção e callbacks |
| Cart | Nome de favorito acompanha a escala; expansão com estado; resumo do carrinho cresce para manter valor/rótulo/contagem; headers preservados | CartAccessibilityRegressionTest; testes de favorito/carrinho existentes |
| Home | Texto de resultado escalável; navegação selecionada e notificação expandida | HomeAccessibilityRegressionTest; regressões clássica/V2 existentes |
| Marketplace | Quantidade com ação contextual e dots selecionados | MarketplaceAccessibilityRegressionTest |
| Marketplace Filter | Modalidade com papel/estado e indicador decorativo | MarketplaceFilterAccessibilityRegressionTest |
| Notifications | Preferências com um alvo nomeado, tabs selecionadas e expansão anunciada | NotificationsAccessibilityRegressionTest |
| Onboarding | Preferência do tutorial associada ao checkbox | OnboardingAccessibilityTest |
| Payment | Consentimento Pix em um alvo com papel/estado | PaymentAccessibilityTest |
| Responsible Gaming | Perguntas/períodos nomeados e selecionados; confirmação associada | ResponsibleGamingAccessibilityTest |
| Results | Busca preenchida nomeada; dígitos Super Sete associados às colunas e legíveis em 200% | ResultsAccessibilityTest |
| Retail | Tabs Pix selecionadas; campo preenchido/erro e Enter sem duplicar ação | RetailAccessibilityTest |
| Support | Links FAQ acionáveis preservam confirmação e política; documento escalável | SupportAccessibilityTest; CaixaDSWebFontScaleTest |
| Tickets | NSB/Lotérico nomeados; acerto anunciado; resultado/coluna/premiação associados | TicketsAccessibilityTest |

A inspeção visual no Samsung em 200% também encontrou corte/sobreposição no cartão e no resumo do carrinho da Home. O corpo passou a usar rolagem e slots de ações/rodapé medidos; o rodapé usa altura natural na escala ampliada. O caminho de 1× e os headers são preservados. Testes com a escala real do sistema reproduzem os cenários.

Os testes usam os componentes reais e cenários sintéticos. A matriz não significa que todas as respostas possíveis de backend, telas de terceiros ou operações autenticadas foram percorridas em aparelho. Componentes Material/DS que já possuíam semântica adequada foram mantidos.

## Verificação e rastreabilidade

Relatórios desta edição: `build/verification/a11y-full-audit-20261027/`. O manifesto consolidado atual é `docs/technical-guide/verification.json`. Os XML Gradle são a fonte dos totais de aprovados, falhos, erros e ignorados; a execução instrumentada registra seu próprio resultado, separado de testes JVM/Robolectric. Rodadas RED documentam defeitos reproduzidos antes das correções e não são contabilizadas como aprovação final.

Critérios mínimos de aceitação: fonte normal/200%, viewport curto/estreito; entrada vazia/preenchida/erro/senha/read-only/disabled; seleção e callback único; link com política preservada; navegação de ida/volta e ciclo de vida. TalkBack e teclado verificam ordem, operação e foco no Android; árvores semânticas e testes estáticos não certificam a fala ou a experiência de um leitor de tela.

## Limites de integração

A homologação complementar das 14 features no Samsung com TalkBack e fonte em 200% tem acompanhamento em [Samsung — validação das 14 features](accessibility/SAMSUNG_VALIDATION_20261027.md). Essa nova rodada possui status e evidências próprios; critérios ainda não executados não são somados às aprovações anteriores.

SSO no navegador, biometria, permissões Android, launcher, FilaBRA e documentos remotos possuem conteúdo/fluxos externos. O aplicativo adapta suas fronteiras, sem alegar controle integral desses fornecedores. Nenhuma transação financeira real ou credencial é necessária para os testes controlados. A documentação final distingue aparelho, emulador, testes automatizados e verificações não executadas.

## Referências

- [WCAG 2.2](https://www.w3.org/TR/WCAG22/)
- [WCAG2ICT](https://www.w3.org/TR/wcag2ict/)
- [Android — Semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics)
- [Android — Accessible defaults](https://developer.android.com/develop/ui/compose/accessibility/api-defaults)
- [Android — Testing accessibility](https://developer.android.com/develop/ui/compose/accessibility/testing)

## Leitura agrupada em português - carrinho e resultados

Implementação solicitada durante a rodada: blocos completos com numerais, valores e datas por extenso. PortugueseSpeech em Core Presentation é compartilhado pelos mappers das duas features. Cart mantém números por coluna, trevos, escolhas da Loteca, cotas/expiração, identificadores e conteúdo mascarado. Results agrupa sorteios, faixas, partidas, recompensas, regiões e próximos concursos. Busca, navegação, exclusão, detalhes, legenda e paginação conservam ações próprias.

Código verificado nesta continuação: **3.371 testes consolidados / 0 falhas / 0 erros / 0 ignorados**, 68 módulos com proveniência por log e reexecução dos afetados. Lint, Detekt, montagem PRD e integração App passaram; Ktlint de 60 arquivos passou. Os hashes de 37 configurações/baselines e dois headers permanecem originais. Isso não aprova automaticamente a fala do TalkBack nem fecha a matriz física das 14 features. APK instalado e conferência auditiva pendente estão identificados na matriz Samsung.


### Complemento de leitura no Samsung

Após a atualização de leitura agrupada, o usuário confirmou separadamente Carrinho e Resultados: “Leitura completa e correta”. A confirmação cobre os blocos e ações pedidos, não todas as variantes do backend.

O tutorial de Bolões apresentou falha real: somente Fechar, Pular e Vamos começar eram acessíveis. As instruções em contornos vetoriais passaram a ter descrição equivalente por etapa ativa, também nas sete páginas de +Milionária. Checkbox e rótulo formam um único alvo com estado. A correção mantém as animações, controles, headers e baselines; regressões e reteste físico são discriminados na matriz. Não usar teste JVM como comprovação de fala no aparelho.
