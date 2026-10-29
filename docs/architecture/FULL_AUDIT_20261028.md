# LOTERIAS CAIXA — Auditoria arquitetural integral de 28/10/2026

**Estado: auditoria concluída, desvios arquiteturais corrigidos e verificados; Ktlint global com dívida preexistente.** A revisão independente e os testes focados encerraram os desvios arquiteturais descritos abaixo. O Ktlint global conserva dívida preexistente, inclusive em header protegido; nenhuma aprovação integral de `qualityGate` é presumida.

## Escopo e critérios

A revisão percorreu App, Core e as 14 features, cobrindo módulos físicos de produção, dependências Gradle, contratos Domain/API, transporte, Data, DI, navegação, ViewModels, coordenadores, Actions e reducers. A inspeção de fontes e dos consumidores complementou os gates existentes: uma regra lexical aprovada não prova que todo comportamento respeita a arquitetura.

O contrato preservado é Feature First, MVVM, Clean Architecture e ReduxKotlin. Domain possui entidades, regras, operações e portas sem Android, Compose, Retrofit ou adaptadores concretos. Data implementa portas e converte transporte. ViewModels e coordenadores possuem efeitos, jobs e resultados; reducers transformam estado e Actions imutáveis. A View coleta estado e entrega interações; DI constrói os objetos, sem implementar operações dentro de lambdas de provider.

O checkout já continha alterações extensas antes da rodada. O snapshot e os inventários iniciais ficam no diretório de evidências; a auditoria não pressupõe que o diff inteiro tenha sido produzido nesta sessão. Headers, baselines, exclusões e limiares de qualidade permanecem protegidos. Não se cria Redux artificial para caches de repository, protocolo de SDK ou metadados de navegação.

## Inventário observado

O grafo configurado em `build/reports/architecture/feature-modules.json` contém **85 projetos**: **68 módulos com `build.gradle` próprio** e **17 agrupadores**. Os agrupadores não são contados como módulos compiláveis. O sample independente de Design System faz parte dos 68 módulos. As 14 features são Account, Betting, Cart, Home, Marketplace, Marketplace Filter, Notifications, Onboarding, Payment, Responsible Gaming, Results, Retail, Support e Tickets.

O inventário reproduzível desta rodada está em `build/verification/architecture-audit-20261028/inventory.json`. Ele discrimina módulos, dependências configuradas e declarações atuais, fontes principais por camada, nomes/caminhos dos ViewModels, binding Redux direto ou delegado e arquivos de reducers. A captura contém **1.814 fontes Kotlin/Java de produção**, incluindo **456 fontes Domain/API** de Core/Feature, **46 ViewModels de feature** e **45 bindings Redux**. Support usa o binding delegado de `SupportStateStore`; `AccountStatusViewModel` coordena efeitos e não recebe store artificial.

A contagem física de `*Reducer.kt` é **72 arquivos**, contra 71 no snapshot inicial. Foram adicionados `BettingNavigationPreferencesReducer.kt` e `StartupRecoveryNavigationReducer.kt`; `BettingGameActionsReducer.kt`, que declarava Actions, passou a `BettingGameAction.kt`. Portanto, 71 + 2 − 1 = 72: a mudança de nome não removeu um reducer. As contagens iniciais de ViewModels/bindings eram 44/43 e cresceram para 46/45.

O inventário diferencia as dependências configuradas das declarações atuais dos builds. O gate `verifyFeatureModules` reconstrói o grafo de produção, verifica propriedade física das fontes e rejeita ciclos. Dependências de testes e referências JaCoCo não são interpretadas como arestas de produção.

## Achados e correções desta rodada

| ID | Desvio confirmado | Correção implementada e contrato preservado | Estado |
|---|---|---|---|
| ARQ28-01 | Betting Navigation consultava repositories de tutorial/shake e executava operações dentro de coroutines Compose. | Duas operações Domain e `BettingNavigationPreferencesViewModel`, com estado/reducer Redux, passam a possuir leitura, gravação, falha e nova tentativa. Navigation compõe e traduz resultados em destinos; não executa persistência. | Corrigido; regressões focadas aprovadas. |
| ARQ28-02 | A recuperação de startup em Onboarding Navigation consultava diretamente a decisão de introdução. | `StartupRecoveryNavigationViewModel` possui a operação, geração de requisição e estado Redux de espera, erro e destino; resposta desativada/antiga não publica navegação. | Corrigido; regressões focadas aprovadas. |
| ARQ28-03 | `MarketplaceModule` implementava `GetMarketplaceSpecialContestsUseCase` em uma lambda da DI Presentation. | `GetMarketplaceSpecialContests` pertence a Marketplace Domain; DI somente o constrói. O helper comum propaga cancelamento e o caso de uso preserva `AppError.Unexpected` para as falhas do contrato anterior, inclusive `IOException`. | Corrigido; regressões focadas aprovadas. |
| ARQ28-04 | A DI Marketplace lia Account Data e interpretava DTO de perfil para fornecer localização. | A porta estreita `AccountMunicipalityRepository` pertence a Account Domain; Account Data executa um único GET de perfil e converte os IDs. Marketplace Data captura/revalida a identidade e converte a porta em `MarketplaceLocationProvider`. Município/perfil ausente continua rejeitado; campos ausentes do identificador mantêm fallback zero. Nenhuma consulta adicional ao catálogo de municípios foi introduzida. | Corrigido; regressões focadas aprovadas. |
| ARQ28-05 | Cinco operações reivindicavam exclusividade somente depois de alcançar o dispatcher I/O; flags compartilhadas de loading podiam liberar trabalho pendente em Registration/Betting Catalog. | A reivindicação acontece antes do agendamento; o Job proprietário protege a operação até sua conclusão, inclusive quando outro carregamento muda a flag visual. Revisões adicionais de PersonalData, Responsible Gaming e cancelamento de Combo estão discriminadas abaixo. | Corrigido; revisões adicionais reproduzidas e testadas. |
| ARQ28-06 | `addedToCart` de Combo permanecia ativo após a navegação e podia reproduzir a confirmação no retorno/recomposição. | `consumeCartAddition()` reivindica e limpa a confirmação antes de chamar o callback de navegação. O efeito tem o ViewModel como chave e não entrega novamente uma confirmação consumida. | Corrigido; regressão Compose compartilhada executada em JVM/Robolectric. |
| ARQ28-07 | Preço e detecção de duplicidade de favoritos pertenciam a Betting Presentation. | `FavoriteBetCartRules` pertence a Betting Domain. Preserva comparação de prognósticos, distinção de colunas do Super Sete, trevos, tipo de item e fallback de preços. Loteca/Lotogol usam o mínimo autoritativo e não comparam seleções numéricas vazias como duplicatas. | Corrigido; testes Domain aprovados. |
| ARQ28-08 | A revisão identificou 73 chamadas de localização em 20 reducers de sete features, além de dependências transitivas em helpers/mappers. | Actions semânticas capturam os textos antes do dispatch. Mensagens dependentes de ramo carregam seus dados; `CapturedTextTemplate` conserva padrão e locale explícito para formatar depois sem consultar o ambiente do processo. O replay não relê catálogo, locale, relógio ou plataforma. | Corrigido; regressões de replay/localização aprovadas. |
| ARQ28-09 | Isenções lexicais amplas de Navigation/DI e chamadas transitivas de localização permitiam desvios passarem pelos gates. | Regressões dos verificadores cobrem operações executadas em Views de navegação, implementações operacionais dentro da DI e localização nos reducers/helpers reconhecidos. Comentários, strings e composição autorizada continuam distintos de execução de efeitos. O gate Compose também foi reconciliado com o host debug interno, presets/calendário, artes e aliases gráficos e query de Cart, com contratos explícitos e fixtures de rejeição, sem ampliar inventário/baseline. | Corrigido; 78 testes do verificador de features e 33 fixtures XML aprovados. |
| ARQ28-10 | Documentos atuais conservavam inventários e estados de aprovação de rodadas anteriores. | Relatório, inventário, visão arquitetural, contrato MVVM/Redux e guia técnico separam histórico, resultados atuais e limites da validação. | Atualizado com proveniência por execução. |

### Revisões adicionais concluídas

A revisão independente encontrou três interleavings adicionais. `final-review-red.log` registra as falhas reais antes das correções; os testes subsequentes verificam exclusividade, recuperação e descarte do proprietário.

| Revisão | Cenário a verificar | Expectativa de aceitação | Resultado |
|---|---|---|---|
| PersonalData — reload | Um reload não deve liberar uma atualização ainda pendente por compartilhar a mesma flag visual. | Job proprietário protege o UPDATE e preserva o draft/sucesso contra GET tardio. | Regressão aprovada. |
| Responsible Gaming — GET/POST | GET do questionário e POST da avaliação não devem liberar envios duplicados ou sobrescrever respostas atuais. | GET serializado, geração invalidada no POST e Job proprietário reivindicado antes do I/O; resultados antigos rejeitados. | Regressão aprovada. |
| Betting Catalog — cancelamento | `CancellationException` durante inclusão, com ViewModel ainda ativo, não pode deixar `loading=true` para sempre. | Estado recuperável com owner ativo, sem repetir POST automaticamente; descarte silencioso e intenção durável preservada para conciliação. | Regressões de owner ativo e descarte aprovadas. |

## Evidências disponíveis e seleção de resultados

O diretório raiz é `build/verification/architecture-audit-20261028/`. Logs intermediários e RED permanecem como evidência de detecção; eles não são substituídos por um resultado de sucesso posterior sem identificação do respectivo comando/escopo.

| Evidência | O que comprova nesta captura | Limite |
|---|---|---|
| `workspace-before.json`, `git-status-before.txt`, `protected-before.json` | Estado inicial e referências de integridade antes das correções. | Comparação final em `protected-final.json`: 37 configurações/baselines e dois headers intactos. |
| `baseline-quality.log` | Android Lint passou com os baselines existentes; verificações arquiteturais iniciais passaram apesar dos desvios encontrados manualmente. | O comando agregado falhou por Detekt/Ktlint. Lint usa baselines históricos; não representa ausência absoluta de avisos suprimidos. |
| `detekt-before.xml` | 13 ocorrências em nove arquivos no ponto inicial desta rodada. | Ocorrências corrigidas com tratamento explícito de falhas, constantes e simplificação; nenhum baseline/suppression ampliado. Resultado global final discriminado abaixo. |
| `ktlint-before.xml` | 7.017 ocorrências distribuídas por 368 arquivos no ponto inicial. | O conjunto inclui 26 ocorrências no arquivo de header protegido `CurrentCartScaffold.kt`. Não se declara Ktlint global aprovado nem se oculta esse limite com exclusões ou alteração de baseline. |
| `ownership-red.log`, `onboarding-navigation-red.log` | As APIs novas necessárias à separação de camadas estavam ausentes antes da implementação. | São RED de compilação; não equivalem a regressão funcional executada. |
| `functional-red.log`, `operation-ownership-red.log` | Falhas funcionais demonstraram duplicidade de operações e replay de localização dependente do ambiente. | `final-review-red.log` e `shake-reload-red.log` registram as revisões posteriores; resultados finais usam XML novo. |
| `policy-red.log`, `policy-green.log` | Regressões e avanço dos gates lexicais; a captura GREEN registra 78 testes Python aprovados. | Guardrails lexicais não demonstram pureza transitiva universal de Kotlin. |
| `ownership-and-claims-green-tests.json` | 336 testes focados, sem falha/erro/ignorado na seleção registrada. | É uma seleção focada, anterior à suíte global. |
| `functional-green-tests.json` | 99 testes focados, sem falha/erro/ignorado na seleção registrada. | Não é validação global nem execução instrumentada das 14 features. |
| `scoped-format.log`, `scoped-format-final.log`, `scoped-ktlint-final.json` | Formatação do conjunto autorizado, preservando os arquivos protegidos. | Formatação parcial não substitui Ktlint global. |

Nenhuma passagem histórica de 27/10 é contada como execução nova. [FULL_AUDIT_20261027.md](FULL_AUDIT_20261027.md) e os demais relatórios anteriores conservam seu escopo e suas evidências.

## Fronteiras e exceções preservadas

Os nomes históricos dos pacotes não definem sozinhos o módulo proprietário. DTOs com namespace `feature.*.data.dto` usados por Core Network estão nos módulos transport; modelos com namespace `.domain` podem pertencer às APIs mínimas que interrompem ciclos. A revisão de Domain/API não encontrou dependência de Android, Compose, Retrofit, OkHttp, SDK concreto ou DTO de persistência/transporte nas fontes percorridas.

Os repositories de seleção entre carrinho remoto/visitante e os caches de parâmetros continuam em Data conforme o contrato existente. O scope compartilhado de configuração atende consumidores concorrentes e seu cancelamento independente é testado; ele não é UiState. Contratos de sessão, leases e adaptadores de plataforma mantêm seus donos. A extração de localização Marketplace não usa `AccountRepository.profile()`, pois essa operação faria uma consulta adicional de municípios e alteraria o tratamento de município ausente.

## Validação global e limites

Execução final: `final-quality-verified.log`. **3510 testes aprovados**, zero falhas, erros ou ignorados; 68 módulos selecionados, incluindo tarefas sem fontes de teste identificadas no JSON. JaCoCo LINE **95.11% (54340/57135 linhas)**, acima de 95%, sem mudar exclusões ou limiares. HTML de testes em `build/reports/tests/aggregate/index.html` e cobertura em `build/reports/jacoco/aggregate/html/index.html`.

Detekt e Android Lint passaram na execução global. Após a correção do verificador Compose desatualizado, `final-architecture-and-build.log` aprovou propriedade de fontes/grafo acíclico, políticas App/Feature, Compose, CaixaDS, contratos de identidade/rede/SDKs e montagem PRD Debug. A suíte global e a cobertura precedem apenas alterações nos verificadores/documentação, sem modificar fontes ou testes do aplicativo. O grafo configurado final corresponde às declarações atuais dos 68 módulos. As regressões Python somam **190 testes aprovados** e os contratos XML de plataforma passaram em **33 verificações positivas/negativas**. A formatação dos **117 arquivos Kotlin alterados** está sem violações no relatório global final.

**`qualityGate` global permanece reprovado exclusivamente por Ktlint:** 6897 ocorrências em 363 arquivos, contra 7.017/368 na captura inicial. Existem 26 ocorrências em `CurrentCartScaffold.kt`, header protegido. Esse débito não foi ocultado por baseline, exclusão ou relaxamento de regra. Os hashes finais confirmam **37 configurações/baselines e dois headers intactos** (`protected-final.json`).

Os manifestos vigentes em `docs/technical-guide/` registram proveniência, inventário e limites; `source-fingerprint-final.json` e `apk-final.json` identificam as fontes e o APK compilado. A saída não zero do comando global é preservada: Ktlint e o verificador Compose antigo falharam; a falha Compose foi corrigida e revalidada no comando complementar, sem recontar os testes globais. O qualityGate de formatação/Detekt/Lint/testes/cobertura permanece reprovado somente por Ktlint.

A primeira tentativa global (`final-quality.log`) foi interrompida após o Lint apontar `EmptySuperCall` nos dois ViewModels novos. As chamadas vazias a `super.onCleared()` foram removidas; `deactivate()` continua responsável por cancelar trabalhos e fechar o store. O log foi preservado como intermediário e não é contado como execução aprovada.

A auditoria não realizou instalação no Samsung, nova homologação TalkBack, instrumentação física, release/R8 ou CI remoto. Os testes de navegação desta rodada usam Compose em JVM/Robolectric; casos financeiros e de sessão usam fakes. As confirmações físicas anteriores conservam suas datas e limites nos manifestos históricos. A inspeção e os gates reduzem os desvios observáveis no escopo descrito, sem certificar ausência universal de falhas.

## Complemento posterior: automação do pre-commit

Após as correções desta auditoria, `preCommitCheck` passou a exigir os gates arquiteturais e demais verificações por um runner compartilhado entre hook local e CI. A validação integral posterior está em `build/verification/pre-commit-20261028/full-gate.log`: 3.510 testes Android/JVM, 190 de ferramentas, 21 de hooks e 33 cenários XML aprovados; cobertura 95,11%; falha exclusivamente de Ktlint global. A automatização preservou as fontes do aplicativo, headers e baselines. As fixtures XML agora usam cópias temporárias, sem alterar recursos do checkout.

O [resumo consolidado](../UPDATES_20261028.md) e o [guia do pre-commit](../quality/hooks-and-ci.md) registram instalação, bloqueios por workspace/index e limites remotos. Esta atualização documental mantém os logs anteriores e não representa nova homologação Samsung, Firebase ou CI.
