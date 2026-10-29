# Home V2 — separação de responsabilidades

Separar HomeV2Screen.kt mantendo o comportamento atual e o ViewModel intacto. A entrada HomeV2Content conserva assinatura, temas, remember(preferences) e rememberUpdatedState(actions). HomeV2Presentation mantém Actions, a ordem persistida e o getter dinâmico de ações; Home continua como API delegadora para compatibilidade com os testes e consumidores existentes.

## Arquivos

- HomeV2Screen.kt: entrada e temas.
- HomeV2Presentation.kt: contrato de ações, identidade da apresentação e persistência da ordem.
- HomeV2Composition.kt: composição e estados de scroll, expansão, campanha, viewport e altura da navegação.
- HomeV2Reordering.kt: gesto completo, limites, overlay, auto scroll e acessibilidade.
- HomeV2Carousel.kt: pager, hero e indicadores.
- HomeV2Cards.kt: cartões de modalidade/resultado, bolão e atalhos.
- HomeV2Navigation.kt: cabeçalho, navegação inferior e itens.
- HomeV2Loading.kt: shimmer e seus blocos.
- HomeV2VisualMappers.kt: modalidades, identificação, textos, cores e ícones.

## Invariantes

Usar extensões com receiver HomeV2Presentation para preservar o getter de Actions. Não converter referências de métodos em lambdas ou capturar um snapshot de Actions. Todos os corpos extraídos permanecem iguais salvo receiver/visibilidade/indentação. Nenhuma alteração em ViewModels, contratos externos, recursos, dependências ou gates.

Os estados e chaves de Home e ReorderableModalities conservam seus owners. O pager fica no ramo não carregando e condicionado à lista não vazia. A ordem personalizada afeta somente a grade; carrossel, bolão e resultados mantêm ordem canônica. Não corrigir capturas preexistentes de pointerInput(Unit), não alterar coordenadas/haptic/auto scroll nem persistência no fim do gesto. Cores e ícones continuam lendo CompositionLocals durante a composição.

## Evidências

134 JVM e APK/gates iniciais; testes Android existentes e novos de callbacks, reordenação e carregamento executados antes e depois. Comparação dos corpos e hashes da árvore anterior. Lint, compilação release, APK PRD debug e nove gates finais. Uma revisão independente com perfil VoltAgent code-reviewer fixado no commit autorizado. Restaurar e encerrar somente o emulador criado nesta tarefa.
