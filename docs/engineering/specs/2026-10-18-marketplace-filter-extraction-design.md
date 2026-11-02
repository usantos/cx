# Extração do formulário de filtros do marketplace

Refatorar a fonte local de 652 linhas em MarketplaceFilterScreen.kt, separando coordenação, modalidades, busca de lotéricas e diálogos de números. Manter assinatura/defaults públicos, ViewModel, ações e comportamento. Referência: build/verification/marketplace-filter-refactor-20261018/MarketplaceFilterScreen.before.kt. Preservar trabalho anterior, sem commit.

## Organização

- MarketplaceFilterScreen.kt retém numberDialog com remember sem novas chaves/persistência, showWantedFilters e toda decisão dos diálogos (opções, seleção, indisponíveis, títulos, confirmação e fechamento). Renderiza o formulário por callback de abertura de NumberDialog.
- MarketplaceFilterForm.kt contém Scaffold/Column e ordem das seções, erro configurationError antes de validationMessage, rodapé, canApply, ações Back/Clear/Apply e resumo do carrinho. Preview privado permanece junto do formulário.
- MarketplaceFilterModalities.kt reúne label/LazyRow e ModalityCard privado; preservar destaque Todas, id+contestType e artes especiais/campanha.
- MarketplaceRetailerSearch.kt reúne campo, feedback e instruções, incluindo loading/mensagem/sugestões simultâneos, limite de seis e encaminhamento do Retailer original. O guard lotteryEnabled permanece no formulário.
- MarketplaceFilterFields.kt reúne dois grupos de campos numéricos, seleção da quantidade de apostas e helpers privados. Manter callbacks, formatos, erros e rótulo Palpites para Loteca.
- MarketplaceFilterNumberSelectors.kt reúne os três seletores e divisórias, mantendo guard showWantedFilters, remoção e limpeza.
- MarketplaceNumberDialogs.kt contém enum NumberDialog internal, LegacyNumberSelectionDialog internal e botão privado. Manter current keyed por title/selected, conflito keyed por title, seleção local até Confirmar, quantidade única sem toggle, bloqueio de conflito e confirmação ordenada.

Os seis arquivos novos são apresentação por estado/actions existentes; não criar ViewModel, novos bundles ou wrappers sem função. Visibilidade internal somente para uso entre arquivos. Sem mudanças de domínio, reducer, mapper, configurações, recursos, dependências, gates ou KMP.

## Validação

Baseline JVM/gates e dez casos Android existentes. Acrescentar sete testes públicos que cobrem cancelar/fechar sem gravar rascunho, conflito entre quero/não quero e ordenação, quantidade única, modalidade normal/especial/ocultação Loteca, busca e limite de sugestões/guard, callbacks numéricos e prioridade de erro/ações. Usar estado controlado e reducer existente, sem rede. Executar os mesmos dezessete casos antes/depois.

Comparar helpers/copiar corpos exatos e reconstruir a função pública após reverter somente extração e callback onOpenNumberDialog. Verificar assinatura/defaults, Scaffold e cada seção contra a fonte local. Hashes protegem ViewModel, state/reducer/mapper e todos os Kotlin anteriores. Executar feature check/lint/cobertura, compile release, app PRD debug build/unit e nove gates; revisão independente. Usar AVD dedicado 5560 e restaurar display/pacotes exatos, encerrando-o ao fim.
