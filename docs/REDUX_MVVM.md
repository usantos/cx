# ReduxKotlin com MVVM em todas as features

## Contrato revisto em 28/10/2026

O inventário atual contém **46 ViewModels de features, 45 com Redux e 72 arquivos de reducers**. Support usa o binding delegado da ponte compartilhada; AccountStatus continua dedicado a efeitos. A auditoria completa, os achados e as evidências estão em [FULL_AUDIT_20261028.md](architecture/FULL_AUDIT_20261028.md). As contagens e aprovações das rodadas datadas abaixo são históricas.

- A Action captura textos e formatos antes do dispatch. Strings e grupos imutáveis têm significado específico para cada evento; não há Action genérica de substituição nem closure de transformação.
- `CapturedTextTemplate` carrega padrão e locale explícito para os argumentos que dependem do estado reduzido. O replay da mesma Action não relê `Locale.getDefault()` ou `ResourceBundle`. A mudança de idioma afeta eventos novos; mensagens já capturadas permanecem parte do evento.
- Mappers de opções/validação de filtro e diálogo de compra que consultam texto são executados antes do dispatch. Reducers recebem seus dados e continuam puros.
- O proprietário do efeito é reivindicado antes de enfileirar a coroutine. Um Job privado não é um segundo UiState: ele impede envio duplicado mesmo quando outro carregamento altera a apresentação. Eventos de conclusão só publicam no dono/geração ainda válidos.
- Resultados de navegação são reconhecidos antes de chamar o host. Reentrar na composição com o mesmo ViewModel não repete uma confirmação consumida.
- Preferências e recuperação de startup têm estado Redux nos ViewModels das features; o grafo apenas compõe, observa e navega. Repositories, SDKs e I/O não são executados pelo reducer ou pela View.


## Histórico — acessibilidade e auditoria integral de 27/10/2026

A rodada atual mantém MVVM, Clean Architecture e ReduxKotlin nas 14 features. Acessibilidade e matriz de aceitação estão em [ACCESSIBILITY.md](ACCESSIBILITY.md). Os oito desvios confirmados desta revisão e as fronteiras App/Core estão em [FULL_AUDIT_20261027.md](architecture/FULL_AUDIT_20261027.md). As seções de validação das rodadas anteriores abaixo conservam seu escopo histórico; resultados atuais e limites pertencem ao manifesto `docs/technical-guide/verification.json`.

Home passou a possuir resultados/cache/jobs em seu ViewModel/Redux, sem orquestrar ResultsViewModel na View. Preço/elegibilidade e expiração passaram para Domain. Notifications serializa leitura inicial e mutações com releitura. Tickets usa política Pix de Domain. Support rejeita respostas fora da geração ativa. O resumo compartilhado de carrinho usa DestinationSnapshotViewModel de Core Presentation, com reducer tipado e dono fechado ao sair do destino.

A View mantém semântica, plataforma e interação. Campos conservam nome/erro e ações de edição; seleção publica papel/estado; links delegam a política existente; texto acompanha o sistema e WebView adapta zoom mantendo DOM. Headers e baselines continuam protegidos. Não se convertem repositórios, protocolo de SDK ou metadados de navegação em stores artificiais.


## Escopo e propriedade

As 14 features usam ReduxKotlin 0.6.2 para o estado de apresentação: Account, Betting, Cart, Home, Marketplace, Marketplace Filter, Notifications, Onboarding, Payment, Responsible Gaming, Results, Retail, Support e Tickets. A captura histórica de 27/10 registrava 43 ViewModels, dos quais 42 possuíam estado gerenciado por Redux; a contagem atual está no início deste documento. Cada ViewModel com UiState possui stores tipados locais ao seu lifecycle. AccountStatusViewModel coordena somente efeitos e não recebe um store artificial. Estados independentes continuam separados; não existe store global de sessão ou navegação.

A arquitetura permanece Feature First, Clean Architecture e MVVM. Domain e Data não dependem de Redux. A View coleta StateFlow somente leitura e envia ações ao ViewModel; os reducers transformam estado e eventos imutáveis. Os casos de uso e os efeitos continuam nos ViewModels e coordenadores.

## Fluxo

```text
View -> ViewModel/coordenador -> dispatch(Action)
                              -> ReduxStateStore -> reducer(estado, action)
                                                 -> StateFlow -> View
ViewModel -> UseCase -> Domain/Data -> resultado -> Action
```

`core:presentation/state/ReduxStateStore.kt` concentra a integração repetida. O helper `reduxStateStore<State, Action>` usa o TypedStore thread-safe oficial. A API expõe somente state, value, dispatch e close. Não oferece setter, update, lambda de transformação ou substituição genérica de estado. A projeção MutableStateFlow é privada à ponte e só a assinatura do Redux a atualiza.

O ViewModel registra o recurso com addCloseable; close é idempotente e remove a assinatura. A Home usa um closeable proprietário que publica o estado terminal de loading antes de fechar a ponte, preservando o resultado do cancelamento anterior. A leitura value, dispatch e close compartilham o mesmo monitor. Consumos que exigem ler, validar identidade e despachar de forma indivisível usam synchronized(store). Isso preserva os antigos contratos de compare-and-set e consumo único sem executar efeitos no reducer. StateFlow é conflado, portanto eventos de navegação e outras entregas únicas permanecem nos canais próprios.

## Responsabilidades por feature

| Feature | Estado e efeitos preservados |
|---|---|
| Account | Login, conta, cadastro, endereço e limites; versões de CEP e identidade impedem publicação obsoleta. AccountStatus permanece somente efeitos. |
| Betting | Jogo, favoritos e Rapidão; seleção, configuração, listas e confirmação durável usam ações semânticas. Randomização ocorre fora dos reducers. |
| Cart | Carrinho, checkout, reserva e compartilhamento; coordenadores compartilham o store do respectivo ViewModel. |
| Home | Conteúdo e preferências; geração de pedidos do carrinho e identidade das notificações permanecem sob controle dos efeitos. |
| Marketplace | Busca, filtros de consulta, detalhe e compra; publicação mantém as verificações de identidade existentes. |
| Marketplace Filter | Parâmetros e seleção; estado inicial vem da fábrica e as alterações passam pelo reducer. |
| Notifications | Lista, seleção e preferências; projeções carregadas preservam versões de requisição. |
| Onboarding | Introdução, tutorial, startup e atualização obrigatória; ForceUpdate usa operações Domain e adaptador Firebase Data. Efeitos de gates não são executados no reducer. |
| Payment | Métodos, Pix, cartão e compra; coordenação de rede e eventos de conclusão permanecem fora dos reducers. |
| Responsible Gaming | Avaliação, suspensão e transferências; ações representam início, resultado e alterações de formulário. |
| Results | Resultados, filtros e apostas confirmadas; estados independentes mantêm stores próprios. |
| Retail | Busca, localização e resgate; entrega de comprovantes usa coordenador sem plataforma, bytes privados e metadados Redux; Uri e provedor permanecem na Route. |
| Support | FAQ, termos, privacidade e ajuda de modalidade; wrapper específico reutiliza a ponte compartilhada. |
| Tickets | Histórico, compras, favoritos, detalhes e comprovantes; consumo único e filtros mantêm os contratos anteriores. |

## Durabilidade, ordenação e concorrência

Em Betting, a fila durável mantém o estado de trabalho separado do estado confirmado. Uma operação captura ações imutáveis, grava o candidato pelo contrato existente e só então publica DraftCommitted no store confirmado. O replay aplica transições locais e não repete operações remotas. RestoreConfirmedDraft/CheckpointRestored representam recuperação ou rollback reais, não uma API genérica para contornar o Redux.

A confirmação de favorito avalia a revisão quando o comando alcança a fila, depois das seleções já enfileiradas. O evento enviado ao reducer carrega o booleano calculado; não contém closure. Relógio, sorteio, I/O, geração de identidade, jobs, cancelamento e eventos de consumo único ficam nos coordenadores/ViewModels. Redux serializa transições, mas não cancela efeitos nem resolve respostas antigas automaticamente. Support agora correlaciona requisições e suas conclusões/falhas/finalizações por geração no reducer; trocar ou fechar aba/seção invalida a geração anterior, inclusive para provedores não cooperativos.

## Dependência e compatibilidade

O Version Catalog fixa 0.6.2. Core Presentation declara api para os artefatos oficiais redux-kotlin-threadsafe-jvm e redux-kotlin-jvm; Support e as demais features reutilizam a mesma ponte. A declaração thread-safe exclui o core multiplataforma transitivo para que os metadados não voltem a selecionar o AAR Android JVM21. Os artefatos JVM oficiais usam classfile61 e preservam o alvo JVM17. Não foi elevada a toolchain nem adotada a linha alpha.

Referência: artefatos oficiais em https://repo.maven.apache.org/maven2/org/reduxkotlin/ e código em https://github.com/reduxkotlin/redux-kotlin . A inspeção binária original está em build/verification/redux-mvvm-20261027/binary-compatibility.json.

## Legado e critérios de arquitetura

No legado local examinado, SimulaViewModel e LotecaViewModel aplicam reducers Java sobre UiState imutável e publicam LiveData. Não foi encontrada dependência org.reduxkotlin nos fontes/Gradle examinados. A migração preserva esse princípio de propriedade e separação; utiliza a biblioteca oficial para store/dispatch e StateFlow para Compose. Evidência: build/verification/redux-mvvm-20261027/legacy-pattern.md.

SOLID: cada reducer transforma dados; a ponte adapta observação e sincronização; ViewModels coordenam efeitos; Domain conserva regras. KISS: stores locais e ações explícitas, sem framework de middleware ou estado global. DRY: uma única ponte compartilhada e uma definição de cada transição, reutilizada pelos coordenadores.

O gate aprova reducers somente em presentation/state/*Reducer.kt dos 14 donos registrados, com pacote correspondente. Rejeita efeitos reconhecíveis, estado mutável e MutableStateFlow nos fontes de apresentação das features. A análise é lexical e não prova pureza transitiva. Flows de repositórios, filas de eventos e estado visual temporário de foco/animação não são substituídos por stores.

Os achados anteriores ARQ-01 e ARQ-02 receberam correções específicas após a expansão Redux: ForceUpdate passou para Onboarding Presentation/Domain/Data; a entrega de comprovantes usa um coordenador proprietário sem Android/Compose, estado Redux de metadados e bytes privados. Detalhes e validação em docs/architecture/REVIEW_20261027.md. Headers, baselines e limites de qualidade permanecem protegidos por hashes.

## Evidências

Plano: docs/superpowers/plans/2026-10-27-redux-all-features.md. Execuções e inventário atuais: build/verification/redux-all-20261027/. Resultados finais e seu escopo são publicados em docs/technical-guide/verification.json após a verificação. O piloto anterior (403 testes Support/App e seis verificações Samsung) permanece histórico em build/verification/redux-mvvm-20261027/, sem ser contabilizado como nova execução.

## Validação da expansão - 27/10/2026

A matriz histórica da expansão aprovou 2.145 testes de Core Presentation, das 14 features Presentation e App PRD, sem falhas, erros ou ignorados. Build PRD, Lint, Detekt, gates arquiteturais e compilação AndroidTest de Retail passaram. Ktlint passou nos 240 arquivos Kotlin alterados. As políticas Python aprovaram 173 testes e outros 11 testes específicos de hooks. Cobertura global e release/R8 não foram reexecutados. Logs: build/verification/redux-all-20261027/verified-final.log e unit-tests-final.json.

Samsung: duas rodadas finais completas com 26 verificações aprovadas, zero falhas ou ignorados, mantendo 4 apostas/R$19 no carrinho. Duas tentativas preliminares tiveram 24 verificações aprovadas e duas expectativas falhas do roteiro ao retornar ao drawer; foram preservadas e documentadas em samsung-harness-note.md. Nenhum código do app foi alterado para ajustar o roteiro.

## Correções arquiteturais posteriores

ForceUpdate possui store Redux próprio e observação fechada no lifecycle, com fábrica da DI de Onboarding; App conserva apenas composição/configuração e intents de loja. A entrega de comprovantes Retail usa outro store local sob o mesmo ViewModel, com ações semânticas e confirmação reivindicada uma única vez. Estado salvo contém somente nome/destino; bytes não entram em Bundle nem no UiState. O fechamento invalida claims e cancela o trabalho antes de encerrar a ponte. Os resultados finais desta etapa estão em docs/technical-guide/verification.json.

Validação das correções: allUnitTests aprovou 3.223 casos nos 68 módulos, sem falhas, erros ou ignorados. Lint, Detekt, gates, APK PRD e compilações AndroidTest passaram; Ktlint dos 24 arquivos Kotlin alterados aprovado. Os 17 testes instrumentados de comprovantes passaram no emulador Android16. No Samsung, o APK foi atualizado sem limpar dados e as duas rodadas finais aprovaram 26 verificações, preservando 5 apostas/R$22 (adição de uma aposta confirmada pelo usuário). Sem nova cobertura global, release/R8 ou alteração da flag Firebase de produção.

## Verificação automática do contrato - pre-commit de 28/10/2026

O hook nativo e o CI versionado usam `scripts/validate-pre-commit.py` e a tarefa `preCommitCheck`. Os gates de camadas, propriedade de fontes, grafo sem ciclos, App/Feature, Compose e CaixaDS são exigidos junto de Ktlint, Detekt, Lint, testes frescos, cobertura e contratos de plataforma. As fixtures XML operam em cópias temporárias. Divergências entre workspace e index ou falhas mantêm o commit bloqueado.

A última execução completa aprovou 3.510 testes Android/JVM e JaCoCo LINE 95,11%, mas reprovou em Ktlint global; não é aprovação integral. Headers/baselines permanecem intactos. [Novidades e resultados](UPDATES_20261028.md) e [instalação/limites do pre-commit](quality/hooks-and-ci.md) discriminam a evidência e a proteção remota ainda dependente de configuração.
