# Organização de Compose — revisão de 01/11/2026

## Objetivo e contrato

Revisar App, Core e as 14 features e separar responsabilidades nos pontos em
que coordenação, composição e controles dificultam a manutenção. A solicitação
segue a abordagem Route → Screen → seções da feature → componentes do DS,
explicada e autorizada na conversa. Layout, textos, navegação, efeitos, estados
Redux e resultados observáveis devem permanecer iguais.

Route observa estado com ciclo de vida, conecta callbacks ao ViewModel e integra
plataforma/navegação. Screen recebe estado e callbacks; seções específicas ficam
na própria feature. O DS mantém controles e contratos visuais compartilhados.
ViewModels, Domain e Data conservam responsabilidades existentes. Dividir
arquivos não justifica criar stores, UseCases ou ViewModels adicionais.

## Critérios da revisão

Tamanho é um sinal de investigação, não uma regra de quantidade de linhas.
Extrair responsabilidades e blocos compreensíveis, mantendo juntos os elementos
que mudam juntos. Preservar implementações de gestures, animações, remember,
LaunchedEffect e DisposableEffect e seus escopos/chaves. Não trocar uma função
legível por vários wrappers que não acrescentam responsabilidade.

Headers CurrentCartScaffold.kt/LoteriasCaixaDSScreenComponents.kt e baselines são
protegidos. Manter IDs publicados, sem exclusões de JaCoCo ou mudanças de
política. Preservar os ajustes de Meus Cartões no DS original. A árvore não
possui interface alternativa ou novo módulo de tema.

## Escopo da auditoria

- App, Core Presentation, Navigation, Queue e Design System.
- Account, Betting, Cart, Home, Marketplace, Marketplace Filter, Notifications,
  Onboarding, Payment, Responsible Gaming, Results, Retail, Support e Tickets.

## Resultado da revisão estrutural

O inventário contém **425 fontes originais com declarações Compose**: 420 do
produto e cinco do catálogo. Abrange App, módulos Core e todas as 14 features.
Cada fonte recebeu decisão de manutenção ou extração. Tamanho foi usado para
localizar candidatos; coordenação, composição, controle e formação de texto
foram os critérios efetivos de separação.

| Área | Fontes originais revisadas | Fontes originais refatoradas |
| --- | ---: | ---: |
| `app` | 17 | 0 |
| `core/design-system` | 91 | 4 |
| `core/navigation` | 4 | 0 |
| `core/presentation` | 4 | 0 |
| `core/queue` | 1 | 0 |
| `feature/account` | 25 | 5 |
| `feature/betting` | 58 | 4 |
| `feature/cart` | 29 | 1 |
| `feature/home` | 44 | 2 |
| `feature/marketplace` | 18 | 2 |
| `feature/marketplace-filter` | 8 | 0 |
| `feature/notifications` | 9 | 0 |
| `feature/onboarding` | 16 | 4 |
| `feature/payment` | 18 | 2 |
| `feature/responsible-gaming` | 8 | 3 |
| `feature/results` | 10 | 3 |
| `feature/retail` | 23 | 1 |
| `feature/support` | 9 | 0 |
| `feature/tickets` | 28 | 10 |
| `sample/design-system` | 5 | 0 |

As fontes novas são seções das responsabilidades existentes; não há módulos,
ViewModels, stores, casos de uso ou dependências adicionais.

## Separações aplicadas

- **Betting/Cart:** Routes de Rapidao, cartela e tutorial conectam estado/efeitos;
  Screens e seções recebem valores e callbacks. Swipe, menus e remember de cada
  aposta continuam no card. Medição das linhas individuais e agrupadas do
  carrinho mantém a ordem e quantidade dos filhos; controles e resumo possuem
  arquivos próprios. O Scaffold protegido permanece intacto.
- **Home/Marketplace:** resultado da Home separado em números/complementos e
  premiação; HomeRoute conserva owners de estado e efeitos e delega layout,
  overlays e plataforma. Carrossel, concurso, identidade, quantidade, favorito e
  grades especiais têm arquivos próprios. A formação do título de concurso é
  uma função pura, com as regras de nome especial e locale anteriores.
- **Tickets/Results:** seleção numérica/esportiva, linhas de apostas/compras,
  resultado, premiação, navegação de concurso e rodapé de detalhe são coesos.
  Routes de escolha e resultado confirmado observam estado e coordenam efeitos;
  Screens recebem estado e callbacks. Polling, relógio Pix e entrega de
  comprovantes conservam seus proprietários e chaves.
- **Account/Retail:** cadastro, diálogos de login e busca recebem ações de UI.
  Rotas ligam essas ações aos mesmos métodos do ViewModel. Cadastro separa
  campos e consentimentos; dados pessoais separam campos/preferências/layout;
  busca separa entrada e linha de lotérica. Biometria e abertura de URI ficam
  na integração; a ordem das ações e as guardas de invalidação são mantidas.
- **Payment:** o fluxo de cartões escolhe a etapa e delega formulário,
  conferência/CVV e desfecho. Identidade do processador, ações e entradas da
  lista estão separadas. Os componentes DS de Meus Cartões permanecem e o
  processamento/estado de pagamento não mudou.
- **Onboarding/Responsible Gaming:** introdução recebe ações, com páginas e
  rodapés separados; sincronização do pager mantém a mesma chave. Estados de
  inicialização, perguntas, períodos de suspensão e repasses sociais foram
  divididos por seção. O relógio de repasses conserva o efeito/intervalo.
- **Design System:** diálogo de opções separa medição, cabeçalho, seleção e
  ações; Marketplace separa resumo/expansão e controles numéricos; painel Pix
  separa texto, conteúdo, cópia, cronômetro e ações. Estado de cópia/clipboard e
  efeitos continuam no componente público original. Métricas antes literais
  receberam nomes semânticos com os mesmos valores, sem atualizar baselines.

## Áreas mantidas

Notifications, Support e Marketplace Filter já possuem integração e seções
coesas e não exigiram divisão adicional. App/Core Presentation/Navigation/Queue
conservam coordenadores de sessão, restauração e plataforma: não são telas
monolíticas e separar seus efeitos por quantidade de linhas enfraqueceria o
owner. O catálogo mantém a organização por famílias e os exemplos dos cartões.
As decisões por fonte e os mapas de movimentação estão nas evidências desta
rodada, fora do Git.

## Exemplos de tamanho após separar responsabilidades

| Fonte | Linhas antes | Linhas depois |
| --- | ---: | ---: |
| `RapidListRoute.kt` | 461 | 51 |
| `HomeRoute.kt` | 406 | 194 |
| `MarketplaceCarousel.kt` | 615 | 115 |
| `ConfirmedBetResultRoute.kt` | 348 | 27 |
| `PaymentCardFlowScreen.kt` | 282 | 62 |
| `RegistrationRoute.kt` | 311 | 73 |
| `RetailSearchRoute.kt` | 281 | 27 |
| `LoteriasCaixaDSPixPanel.kt` | 344 | 84 |

A contagem ilustra a distribuição das responsabilidades. Os novos arquivos
conservam a apresentação que antes estava concentrada na fonte original.

## Automação, testes e revisão

IDs, símbolos e localizadores publicados permanecem iguais. Os inventários
registram apenas novos proprietários/linhas das declarações. Mantêm-se número de
ocorrências, contêiner, seleção, ações e semântica; o contrato estático não
comprova sozinho a árvore nativa.

Antes da mudança, a mesma árvore de produção estava validada com 3.608 testes e
LINE global 95,25%. A refatoração usa esse comportamento fixado. Não se criam
RED artificiais sobre quantidade de arquivos ou tamanho de funções. Testes de
introdução e rodapé adaptaram somente a ligação às novas assinaturas; os asserts
comportamentais foram preservados.

Após as extrações, **170 regressões focadas em 13 tarefas passaram**, com zero
falhas, erros ou ignorados: campos/callbacks, cartões/CVV/fonte ampliada, pager,
resultados, escolha/conferência, swipe, medição do carrinho, Home/drawer,
Marketplace, repasses e catálogo. A revisão independente do delta e do
suplemento de título não encontrou achados Critical/P1/P2. É evidência estática
e local; não equivale a certificação de todos os fluxos no Samsung.

## Validação da primeira rodada

A rodada final passou em 11min22s: Ktlint, Detekt, Android Lint, arquitetura,
LoteriasCaixaDS, IDs, contratos Compose e APKs TQS Debug/catálogo DS. Foram **3.608 testes
unitários/locais**, sem falhas, erros ou ignorados. LINE global **95,27%**
(58.430/61.331), sem mudar exclusões ou limiar. São 41 fontes Compose originais
refatoradas; 384 foram mantidas por coesão, integração de plataforma ou proteção.
O delta de 134 fontes Kotlin (incluindo
seções novas e adaptação de três testes) foi revisado e teve hashes conferidos
após os testes, com o suplemento de título aprovado.

Os 44 arquivos protegidos, headers, baselines e configuração de qualidade
mantiveram seus hashes. Nenhuma fonte de ViewModel, reducer, Domain ou Data foi
alterada nesta tarefa. IDs e símbolos permanecem iguais; 431 proprietários do
inventário principal e 12 do legado foram realocados. Os APKs foram inspecionados
para confirmar ausência dos pacotes/flag da interface alternativa anteriormente
retirada. Os ajustes de Meus Cartões e o DS original continuam presentes.

O staging permanece vazio; não houve stage, commit, push, instalação ou operação
financeira. O `preCommitCheck` completo não foi executado porque os fontes novos
ainda não estão preparados no Git: as verificações descritas foram executadas
diretamente, sem desabilitar o gate. Antes de um commit, preparar o conjunto
completo e executar o runner oficial. Validação física no Samsung e certificação
Device Farm não foram realizadas nesta rodada.

Evidências: `simlo-artifacts/2026-11-01/compose-refactor`, fora do Git:
`final-validation.log`, `focused-validation.log`, `final-verification.json`,
`compose-inventory.json`, relatórios dos grupos e `independent-review.md`.


## Fechamento dos acúmulos remanescentes

A continuação revisou a árvore após a primeira rodada: 509 arquivos com
`@Composable`, incluindo as seções já extraídas. O inventário final tem 649
arquivos de composição. O delta desta continuação contém 227 fontes Kotlin
alteradas ou criadas; a contagem não inclui o trabalho anterior como se fosse
novo. As extrações separam conteúdo, controles, diálogos, callbacks e integração
de plataforma, preservando a árvore visual e os contratos públicos.

Betting separa modos de jogo, Loteca, quantidade, tutoriais e diálogos. Cart,
Tickets e Results separam listas, filtros, seleção, resumos e detalhes. Home,
Marketplace, filtros, notificações e ajuda separam seções visuais e ações. Payment
e Retail separam canais, confirmações e resultado do resgate. O DS separa
controles, conteúdo de diálogos, entradas e estratégias de medição. AppSession
separa composição de sessão, conclusão de conta, restauração e callbacks;
WebView e Wheel mantêm seus estados e efeitos nos proprietários originais.

### Critério de encerramento

Não ficam candidatos com mistura de responsabilidades confirmada sem tratamento.
A contagem de linhas é um sinal para revisão, não um limite que obriga criar
wrappers. Na rodada anterior, os seis sinais restantes foram inspecionados explicitamente.
Essas decisões de manutenção foram substituídas pela refatoração adicional de
02/11/2026, descrita adiante:

| Arquivo | Decisão de coesão |
| --- | --- |
| `BettingFavoriteDialog.kt` | Um diálogo com título, campo e ações em funções locais de até 46 linhas. |
| `ResponsibleGamingIcon.kt` | PNG embutido em Base64; a inspeção posterior corrigiu a classificação inicial como vetor. |
| `PrizeReceiptManager.kt` | Uma operação de integração de comprovantes com recuperação e proteção de acesso; sem layout de tela. |
| `LoginAuthorization.kt` | Um protocolo de autorização SSO, com cancelamento e integração de plataforma; sem composição visual acumulada. |
| `BettingGameRoute.kt` | Proprietário de estado local, efeitos e BackHandler; conteúdo por modalidade e diálogos já delegados. |
| `PrizeRedemptionRoute.kt` | Proprietário do ciclo de navegação/resgate; tela, canais, comprovantes e diálogos separados. |

Não foram criados stores, ViewModels ou UseCases para fragmentar UI. Leituras
atuais dos callbacks WebView, chaves de efeitos, identidade de sessão e guardas
continuam no mesmo momento do ciclo de vida. Os componentes de Meus Cartões
permanecem no DS original; a interface alternativa removida não foi reintroduzida.

Evidências desta continuação ficam em
`simlo-artifacts/2026-11-01/compose-final-pass`, fora do Git: inventário,
snapshot anterior, relatórios por grupo, revisão independente e logs de validação.

A validação também substituiu APIs antigas de recursos que o Lint apontou nas
seções extraídas de Betting: o tutorial Super Sete carrega suas três páginas por URI de asset, cujos GIFs
passaram de App para `assets` da feature proprietária com bytes preservados; o trapézio de favoritos usa `ResourcesCompat.getDrawable`.
Não se adicionaram supressões para conservar chamadas antigas.


### Validação do fechamento — 02/11/2026

A execução final passou em 10min59s. Foram **3.608 testes** em
68 tarefas, sem falhas, erros ou ignorados. A cobertura LINE de produção
ficou em **95,29%** (59.037/61.956), estritamente acima de
95%, com as mesmas exclusões e o mesmo limiar. Ktlint, Detekt, Android Lint,
arquitetura, DS, automação e contratos Compose passaram. Os APKs TQS Debug e do
catálogo DS foram gerados; os três GIFs foram conferidos dentro do APK pelos
hashes do início da tarefa. Os 227 hashes das fontes Kotlin revisadas permaneceram
iguais durante essa validação. Não restaram acúmulos de responsabilidades
confirmados e sem tratamento no escopo revisado.

Os 44 arquivos protegidos, incluindo headers e baselines, ficaram intactos.
Não há delta desta continuação em Domain, Data, ViewModels ou reducers. IDs,
chaves e símbolos foram preservados; somente os proprietários/linhas dos
inventários foram realocados. A revisão independente não identificou achados
funcionais Critical/P1/P2 no delta e nos suplementos finais.

A evidência é local, com testes unitários/Robolectric e compilação; não houve
validação física no Samsung, instalação, commit ou push. O staging permanece
vazio. O `preCommitCheck` completo não foi executado porque exige preparar os
fontes novos no Git; as verificações de qualidade, testes, cobertura, contratos
e APKs foram executadas diretamente, sem desabilitar o gate. Ferramentas
crapper/dryer/mutator não estavam disponíveis; não se alegam métricas CRAP,
duplicação ou mutação produzidas por elas.

Os resultados verificáveis estão em `final-verification.json`,
`final-validation-complete.log`, `coverage-final.json`, `review-complete-manifest.json`,
`resource-moves.json` e `independent-review.md` no diretório externo de evidências.


## Refatoração adicional dos seis arquivos — 02/11/2026

Após solicitação expressa para refatorar também as unidades mantidas por coesão,
a organização passou a separar cada responsabilidade local. Os contratos de
estado Redux/Flow, as operações do ViewModel e as regras de Domain/Data continuam
os mesmos; os novos helpers são adaptadores da camada Route ou componentes da
feature. A tabela abaixo substitui a decisão anterior de manutenção.

| Entrada | Antes → depois | Responsabilidades extraídas |
| --- | --- | --- |
| `BettingFavoriteDialog.kt` | 212 → 49 linhas | Título, entrada do nome e ações; chrome e proprietário do foco permanecem no diálogo. |
| `BettingGameRoute.kt` | 137 → 109 linhas | Callbacks de navegação/edição, efeitos e grupos de overlays; sete estados locais permanecem na Route. |
| `LoginAuthorization.kt` | 154 → 47 linhas | Estado local saveable, conexão/retorno, Activity Result, lançamento/repetição e efeitos. |
| `PrizeReceiptManager.kt` | 161 → 94 linhas | Plataforma PDF, recuperação, coleta/conclusão e diálogos de falhas; metadados ficam no owner e bytes no ViewModel. |
| `PrizeRedemptionRoute.kt` | 123 → 74 linhas | Redirecionamento/seleção inicial e bindings das ações da Screen. |
| `ResponsibleGamingIcon.kt` | 208 → 20 linhas | PNG original extraído para `drawable-nodpi`, sem conversão ou recompressão. |

O estado de autorização não é duplicado: `LoginAuthorizationState` delega aos
mesmos cinco `MutableState` salvos e ao callback de retorno associado à Activity.
Os helpers leem esses holders atuais, inclusive em callbacks assíncronos.
Comprovantes conservam getters dos metadados dentro do collector; uma gravação
retida recebe somente o resolver da aplicação, sem capturar a Activity. As chaves
dos efeitos, guards de geração/consumo e ordem dos diálogos são preservadas.

O selo é um PNG de 100×100, com 13.353 bytes. O SHA-256 da imagem extraída é
`9f0b25e034d66350b0980b1c1765ca1c98a17291c0df3682b15c7f346e4708a5`,
idêntico ao Base64 anterior. `ContentScale.Fit`, modificador e descrição acessível
continuam os mesmos. Não se trata de uma nova arte.

O comportamento foi fixado antes da refatoração por 77 testes focados aprovados.
A validação posterior e a revisão independente ficam registradas nas evidências
`simlo-artifacts/2026-11-02/compose-residuals`, fora do Git. Não se usa a quantidade
de linhas como substituto de coesão nem se cria teste artificial de tamanho de
arquivo.


### Leituras de estado após a extração

A inspeção final encontrou uma diferença de timing nas primeiras extrações:
um parâmetro por valor congelava o estado antes da mutação do ViewModel ou antes
de a coroutine começar. O código original consultava o `State` delegado depois.
Os helpers de limpeza/redução da Loteca e os efeitos de confirmação adicionada,
decisão de aposta e seleção inicial agora recebem um getter do mesmo estado
observado pela Route. As chaves dos efeitos continuam sendo os valores lidos na
composição; o corpo consulta o estado atual quando executa. A fonte observada
continua a mesma, sem espelhamento de estado.

Foram adicionados cinco testes em `BettingGameRouteStateTimingTest`,
`BettingGameRouteEffectTimingUiTest` e `PrizeRedemptionEffectTimingUiTest`.
A rodada RED reproduziu quatro falhas de comportamento: rebind extra na redução,
navegação após consumo da confirmação, entrega de decisão já consumida e seleção
inicial após sair da etapa. O teste de limpeza já passava. Após corrigir as
leituras, os cinco passaram em 20 segundos, com as mesmas expectativas. Os testes
usam ViewModels reais e controle de coroutine/composição, sem testes de texto-fonte.
O delta final contém 23 fontes de produção e três arquivos de teste; a revisão
independente conferiu os 26 hashes e encerrou os achados identificados.


### Resultado final desta refatoração

A rodada definitiva passou em **9 minutos**, com **3.613 testes** em
68 tarefas, sem falhas, erros ou ignorados. A cobertura LINE global ficou
em **95,31%** (59.160/62.070 linhas), sem alterar limiar,
exclusões ou baselines. Ktlint, Detekt, Android Lint, arquitetura, DS, IDs e
contratos Compose passaram. Os 26 arquivos revisados conservaram os hashes
durante a validação, e os 44 protegidos ficaram intactos. O APK TQS Debug foi
gerado; o PNG extraído manteve os mesmos pixels após o empacotamento Android.

O inventário atualizado contém **660 arquivos Compose de produção/Core/catálogo**,
com até **199 linhas por arquivo** e **97 linhas por função identificada** pela
varredura lexical. Esses números complementam a revisão de responsabilidades;
não foram adicionados limites artificiais ao gate. Os seis arquivos da tabela
foram efetivamente refatorados; a nova estrutura não foi apenas uma transferência
do bloco completo para outro arquivo.

Identificadores, chaves e símbolos dos 1.777 registros principais e 88 legados
continuam iguais. Somente proprietário/linha foram atualizados nos inventários.
Domain, Data, ViewModels e reducers não receberam alterações nesta continuação.

A validação é local, incluindo Robolectric/Compose; não houve teste físico no
Samsung, instalação, staging, commit ou push. O `preCommitCheck` completo segue
NOT RUN por exigir fontes novas preparadas no Git; as verificações acima foram
executadas diretamente, preservando o gate. Não se alegam métricas de ferramentas
crapper/dryer/mutator indisponíveis.

Evidências atuais: `final-validation-complete.log`, `final-verification.json`,
`coverage-final.json`, `timing-red.log`, `timing-green.log`,
`review-complete-manifest.json` e `independent-review.md`, em
`simlo-artifacts/2026-11-02/compose-residuals`, fora do Git.


## Refatoração autorizada dos headers — 02/11/2026

O usuário autorizou explicitamente refatorar `CurrentCartScaffold.kt` e
`LoteriasCaixaDSScreenComponents.kt`, substituindo a decisão anterior de manter esses
fontes byte a byte. A exceção é estrutural: o contrato visual e funcional continua
protegido, inclusive nos helpers extraídos. Baselines, headers HTTP e os outros
42 arquivos da lista protegida permanecem fora dessa autorização.

`LoteriasCaixaDSScreenComponents.kt` conserva a API pública, seus parâmetros e defaults,
a configuração do TopBar e o cálculo do overlap antes da composição do Scaffold.
`LoteriasCaixaDSScreenChrome.kt` renderiza os slots de barras/conteúdo e contém o footer
e o adaptador `PaddingValues.forToolbarGuideline`. Esse adaptador delega ao
padding original e consulta seu topo quando solicitado: mantém remeasure,
insets, RTL e a diferença entre truncamento/arredondamento com densidade
fracionária. Não há statebag, nova API ou contêiner visual adicional.

`CurrentCartScaffold.kt` continua compondo a tela e o footer. A visibilidade das
ações é calculada uma vez e compartilhada com a largura reservada.
`CurrentCartHeaderActions.kt` contém o mesmo Row, ordem, margem de 16dp, botões de
44dp e ícones de 20dp. `CurrentCartScaffoldContent.kt` mantém as prioridades de
carregamento, erro e conteúdo, com os mesmos callbacks e padding.

Os botões extraídos não tinham IDs explícitos por estarem na exceção dos dois
headers. Agora publicam `simlo.cart.0143` (excluir todas) e `simlo.cart.0144`
(salvar favorito), de forma aditiva. IDs e localizadores anteriores permanecem
iguais; não se ampliou a exceção do validador. Os novos seletores Robot são
`carrinho.excluir_todas` e `carrinho.salvar_favorito`.

Antes das extrações, 10 testes de caracterização passaram e geraram oito PNGs
locais. O teste adicional dos IDs falhou por ausência da tag antes da mudança.
As oito capturas após a extração ficaram idênticas pixel a pixel, cobrindo
header azul, customizado, barras/padding e carrinho autenticado, visitante,
bolão e vazio. Carregamento tem assert semântico, sem comparar spinner animado.
As capturas são evidência local Robolectric, não certificação no Samsung.

Evidências em `simlo-artifacts/2026-11-02/header-refactor`, fora do Git, incluindo
snapshot, logs, comparação visual e revisão independente.


### Resultado da refatoração dos headers

A rodada final passou em **8min26s**, com **3.624 testes** em 68
tarefas, sem falhas, erros ou ignorados. A cobertura LINE global ficou em
**95,32%** (59182/62090 linhas), sem alterar limiar,
exclusões ou baselines. Ktlint, Detekt, Android Lint, arquitetura, DS, IDs e
contratos Compose passaram, assim como os builds TQS Debug e catálogo DS.
Os 16 testes focados e os oito pares PNG também passaram. Cada par tem os mesmos
pixels e bytes PNG antes/depois.

Foram revisadas oito fontes Kotlin: as duas fachadas autorizadas, três helpers
e três arquivos de teste. Os parâmetros/defaults públicos foram comparados ao
snapshot e permaneceram iguais. Os 42 demais arquivos protegidos, todas as
configurações e os verificadores conservaram seus hashes. Nenhuma fonte de
Domain, Data, ViewModel ou reducer foi alterada. Todos os registros anteriores
de IDs e localizadores ficaram idênticos; somente os dois novos IDs/localizadores
foram acrescentados. As instruções Copilot registram a autorização limitada e
mantêm a proteção visual também nos novos helpers.

A conferência de escopo usa o snapshot da árvore existente no início da tarefa.
As deleções de `TicketsDataRoutes.kt` e `PurchaseDetailRows.kt` já estavam no
patch anterior e continuam preservadas; não pertencem a esta mudança.

A evidência é local/Robolectric; não houve teste físico no Samsung, instalação,
staging, commit ou push. `preCommitCheck` completo permanece NOT RUN por exigir
preparar os fontes novos no Git. As verificações citadas foram executadas
diretamente, preservando o gate. Ferramentas crapper/dryer/mutator indisponíveis
não tiveram resultados inferidos.

Resultados atuais: `final-validation.log`, `final-verification.json`,
`coverage-final.json`, `visual-comparison.json`, `review-complete-manifest.json`
e `independent-review.md`, em `simlo-artifacts/2026-11-02/header-refactor`, fora
do Git. As imagens ficam em `png-before` e `png-after`.
