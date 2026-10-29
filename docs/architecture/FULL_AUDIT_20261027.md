# LOTERIAS CAIXA — Auditoria arquitetural integral

**Data:** 27/10/2026. **Escopo:** App/Core e as 14 features; módulos de produção, contratos Domain/API, transporte, DI, navegação, ViewModels/coordinators, estado Redux e adaptadores de plataforma. Revisão de fonte e dependências complementada por regressões e gates. Preservar MVVM, Clean Architecture, ReduxKotlin e os hashes de headers/baselines é condição de entrega.


## Resultado verificado

allUnitTests: **3.325 aprovados / 0 falhos / 0 erros / 0 ignorados**, com XML frescos em 68 módulos. Lint, Detekt, gates de arquitetura/CaixaDS, APK PRD e compilações AndroidTest de App/DS/Retail aprovados. Ktlint: 155 arquivos alterados. Políticas: 184 testes Python + 11 hooks, todos aprovados.

Emulador Android16: 19 casos instrumentados aprovados. Samsung: 26 verificações nas duas rodadas finais, 2 cenários de escala normal/200% e 1 conferência humana com TalkBack aprovados; carrinho 5 apostas/R$22 preservado. Fonte/TalkBack temporários restaurados. A matriz distingue estes escopos de uma certificação de conformidade, cobertura global, release/R8 e homologação autenticada/financeira, que não foram realizadas.


## Achados desta rodada

| ID | Desvio confirmado | Correção e contrato |
|---|---|---|
| FULL-01 | Resultados da Home coordenados por estado Compose/ResultsViewModel de outra feature | HomeResultsCoordinator sob HomeViewModel, cache/identidade/erro no Redux da Home; respostas antigas canceladas/rejeitadas; cache preservado ao recolher/expandir. Refresh de expiração pertence ao VM e para quando a tela perde visibilidade. |
| FULL-02 | Preço, seleção elegível e multiplicadores em Betting Presentation; lookup duplicado no carrinho visitante | BetPricing/BetSelectionEligibility em Betting Domain, consumidos pela apresentação e Cart Domain. Preservar UI zero/fallback vs rejeição explícita do visitante para preço ausente; quantidade/recorrência/mirror com contratos próprios. |
| FULL-03 | Interpretação de expiração e decisão de remoção em Cart Presentation | CartReservationExpiry em Cart Domain; relógio/IO/sessão permanecem no coordenador. Preservar São Paulo, ISO/offset, valor malformado, limite inclusivo e distinção existente entre remoção exata e checkout em segundos inteiros. |
| FULL-04 | Leitura inicial e mutations de preferências Notifications concorrentes publicavam snapshot antigo | Fila FIFO reservada antes do IO; inclui leitura inicial e mutação seguida de releitura autoritativa, preservando ordem das ações. |
| FULL-05 | Coordenadores Pix dependiam de regra em Tickets Screen | Purchase.isPendingPix em Tickets Domain, reutilizada pela View/coordenadores. Não alterar estados/códigos terminais. |
| FULL-06 | Respostas antigas de Support podiam substituir aba/seção atual | Geração no estado e mutations correlacionadas de início/conclusão/falha/finally; selecionar/fechar invalida a identidade. Cancelamento complementa a rejeição no reducer, incluindo provedor não cooperativo. |
| FULL-07 | Resumo do carrinho em Core Navigation carregava caso de uso diretamente na View | DestinationSnapshotViewModel compartilhado, store/reducer Redux tipado e dono exclusivo do destino/fonte. Retorno da tela dispara refresh; respostas antigas e callbacks após clear não publicam. |
| FULL-08 | Dois diálogos de armazenamento Onboarding importavam controles Material diretamente | Consumo dos componentes base existentes do CaixaDS, mantendo layout, defaults, callbacks e política de erro. O gate verifyCaixaDS reproduziu a violação antes da troca. |

As correções anteriores ARQ-01/02 (ForceUpdate e entrega de comprovantes) permanecem descritas em `REVIEW_20261027.md`; sua aprovação histórica não é reapresentada como execução desta rodada. Os testes atuais voltam a cobrir seus contratos pertinentes.

## Fronteiras e decisões de arquitetura

Domain/API não depende de Android, Compose, Retrofit ou Data. ViewModels consomem operações de Domain; reducers transformam estado sem IO/relógio/efeitos. Views coordenam interação, launchers e plataforma. Data converte transporte e implementa repositórios. DI pode construir contratos concretos dentro do dono. App compõe features e adaptadores sem possuir ViewModel ou Data de feature.

Core Navigation conserva metadados de navegação; Core Auth conserva repositório de sessão/identidade; cache de dados em Core Data e protocolo transitório de fila não são UiState de feature. Esses estados não recebem Redux artificialmente. Reuso explícito de UI entre Presentation modules não é, sozinho, violação de Clean Architecture. Contratos API são identificados pelo módulo físico, não apenas pelo namespace histórico.

Dependências Gradle são avaliadas por escopo: testImplementation e referências Jacoco não viram arestas de produção. A correção FULL-07 usa Core Presentation e evita ciclo Core → Presentation de feature.

## Controles preventivos

Os gates existentes de dono/camada, pureza de reducers e dependência de ViewModel foram preservados. Novas regressões rejeitam coordinator dependente de Route/Screen/Components (alias, wildcard e acesso qualificado) e carga suspensa de Domain coordenada em Core View. Comentários/strings não geram falso positivo nos novos testes. São guardrails lexicais, não uma prova transitiva de todo código Kotlin futuro.

## Evidências e limites

Inventários e relatórios de auditoria: `build/verification/a11y-full-audit-20261027/{app-core-audit,features-a-audit,features-b-audit,ds-audit}.md`. O manifesto consolidado e o guia técnico publicam os resultados finais de testes, Lint, Detekt, Ktlint alterado, revisão independente e Samsung. Histórico RED e tentativas intermediárias permanecem separados da seleção final.

A revisão cobre contratos e caminhos de produção examinados; não equivale a auditoria de segurança binária de SDK, certificação WCAG, validação de todos os estados de backend ou transações autenticadas reais. Cobertura global histórica e build release/R8 só podem ser chamados atuais se forem reexecutados e registrados. Nenhum achado é encerrado apenas por aumentar baseline, supressão ou limiar.

## Rodada complementar de acessibilidade no Samsung

As correções adicionais e a matriz por cenário estão em [Samsung - validação das 14 features](../accessibility/SAMSUNG_VALIDATION_20261027.md). Os verificadores de propriedade dos módulos, fronteiras de camadas e host/MVVM foram novamente executados nesta rodada e passaram. Os novos componentes adaptam a apresentação e delegam callbacks às rotas/ViewModels existentes; não acrescentam rede, regras de negócio ou efeitos aos reducers. As verificações arquiteturais não substituem os gates de estilo/testes nem aprovam cenários de TalkBack ainda sem conferência humana. Logs atuais: `build/verification/samsung-a11y-14features-20261027/quality-recheck.log`. A auditoria e os totais anteriores permanecem históricos.

A leitura agrupada adicional de Cart/Results usa PortugueseSpeech puro em Core Presentation e mappers locais. As descrições não acionam domínio, rede ou TTS; os callbacks continuam nas rotas/ViewModels existentes. Títulos/normalização de resultados foram centralizados no mapper, com adaptadores de compatibilidade para consumidores existentes. Gates e integração atuais estão em `quality-code-final.json` e `app-speech-verified.log`; a matriz Samsung ainda distingue validação parcial de conformidade integral.


### Complemento: tutorial animado, introdução e câmera

O achado humano no tutorial de Bolões foi rastreado a texto vetorial sem alternativa semântica, não a uma falha do store. As descrições por página ficam no mapper de apresentação de Betting; apenas a página ativa expõe o bloco, e ações ficam separadas. O checkbox tem um único proprietário nomeado/selecionável. Nenhum efeito foi colocado em reducer e as transições continuam nos ViewModels/Redux existentes.

Home passou a delegar a entrada no scanner a Tickets, que já possui a câmera e a alternativa manual. A introdução mede o rodapé e permite rolagem do corpo com fonte ampliada. Contratos, regras de domínio, headers e baselines não foram alterados. Gates completos e reteste físico têm rastreabilidade na matriz Samsung; evidência JVM não substitui a conferência auditiva.
