# feature:marketplace-filter

Tela Compose e contrato de eventos do filtro de bolões do Marketplace.

Os componentes públicos são `MarketplaceFilterRoute` e `MarketplaceFilterScreen`. Eles não acessam diretamente rede ou persistência; a configuração deve chegar pelo estado produzido pelo caso de uso/ViewModel.

A feature não declara Activity nem Fragment. A única Activity fica no composition root `:app`; toda UI de feature é Compose.

`betOptions` deve ser produzido a partir de `qtdeMinimaApostaBolaoFiltro` e `qtdeMaximaApostaBolaoFiltro`, recebidos de `/parametros/configuraveis-app`; não existe intervalo padrão fixo na UI.

O task `check` executa testes, lint e uma barreira Jacoco mínima de 95% sobre reducer e mapeadores.
