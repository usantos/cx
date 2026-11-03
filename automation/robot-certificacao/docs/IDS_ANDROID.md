# Contrato de identificação Android / Robot

O app usa `Modifier.automationId` para os IDs nomeados e `Modifier.uiId` para as chaves auxiliares compactas, em `core/design-system/.../automation/AutomationIdentity.kt`. As extensões publicam o testTag e habilitam `testTagsAsResourceId` no próprio nó. Isso também atende diálogos em janelas Compose separadas, sem adicionar uma Box à hierarquia ou mudar dimensões, cores, textos ou callbacks.

O valor é literal, não derivado do texto visível, CPF, usuário ou idioma. IDs existentes foram mantidos e exportados. A extensão `legacyAutomationId` dá precedência a esses IDs sobre os novos fallbacks de wrappers, preservando seletores existentes. IDs de componentes em linhas repetidas devem ser consultados dentro do contêiner correto. No Appium use `id=simlo....`, sem prefixar `package:id/`; a sessão desabilita a autocomplementação de ID do UiAutomator2.

## Inventário

`TestData/android-ids.json` registra cada declaração estática, área, arquivo e linha de origem. A contagem abaixo é de declarações, não de elementos simultaneamente exibidos. Ramos exclusivos podem declarar o mesmo ID. IDs legados/dinâmicos mantêm seus próprios valores e estão registrados separadamente em `TestData/legacy-android-ids.json`; não estão somados nessa contagem de novos IDs `simlo.*`. Expressões dinâmicas descrevem a origem no Kotlin e precisam ser resolvidas com a massa/contêiner do caso.

| Área | Declarações estáticas |
| --- | --- |
| account | 69 |
| app | 8 |
| betting | 273 |
| cart | 144 |
| dialog | 2 |
| ds | 147 |
| home | 161 |
| marketplace-filter | 39 |
| modules | 1 |
| notifications | 49 |
| payment | 134 |
| marketplace | 121 |
| responsible-gaming | 79 |
| results | 95 |
| retail | 131 |
| onboarding | 46 |
| support | 32 |
| tickets | 250 |

O inventário acompanha o arquivo físico após a extração das UIs para os splits
`*_ui`. Contratos, ViewModels e efeitos de plataforma permanecem nas bibliotecas
`presentation`; mudar o proprietário do renderizador não altera o ID publicado.
A descoberta confere também `core/presentation`, que fornece o estado de entrega
compartilhado `simlo.modules.0001`, e somente os splits declarados no Gradle.

Os seletores de conteúdo de uma UI sob demanda aparecem após a instalação e a
admissão da capacidade. Durante preparação, falha ou indisponibilidade, use
`modulo.estado` e a ação `modulo.acao` dentro desse contêiner. O contexto diferencia
voltar de tentar novamente; o ID da ação permanece o do Design System. O FAQ
conserva os seletores específicos já publicados.

## IDs dos cenários iniciais

| Elemento | ID |
| --- | --- |
| Experimente | `simlo.account.login.experimente` |
| Entrada/usuário lembrado | `simlo.account.login.entrar` |
| Biometria | `simlo.account.login.biometria` |
| Trocar usuário | `simlo.account.login.trocar_usuario` |
| Menu da Home | `simlo.home.menu` |
| Navegação Apostar | `simlo.home.apostar` |
| Carrinho | `simlo.home.carrinho` |
| Resultados | `simlo.home.resultados` |
| Sair | `simlo.home.sair` |
| Ação positiva de diálogo compartilhado | `simlo.dialog.positivo` |
| Lista de modalidades de resultados | `simlo.results.modalidades` |

## Pre-commit e limites

No checkout Android: `./gradlew verifyAutomationIds`. A tarefa está incluída em `preCommitCheck`. Verifica o contrato JSON, localizadores, declarações estáticas e IDs explícitos nos controles Compose básicos reconhecidos pelo gate. Os testes negativos cobrem controle sem ID, identificador do filho confundido com o do pai e texto/comentário que não deve ser tratado como código.

O gate não promete interpretar todo DSL Kotlin nem inspeciona a árvore nativa. Um componente customizado novo precisa expor o Modifier/ID no alvo interativo e ter um teste/inspeção correspondente. Preservar o ID publicado é parte do contrato com a certificação.

Dois arquivos de header protegidos permanecem intactos. Seus componentes básicos compartilham a infraestrutura do LoteriasCaixaDS. O `testTag` não substitui `contentDescription`, role, estado ou erro: acessibilidade continua independente dos identificadores de teste.

Não é possível atribuir IDs Compose a elementos pertencentes ao Chrome/SSO, ao diálogo biométrico Android, ao launcher ou ao HTML/SDK de terceiros. Esses alvos precisam de localizadores próprios e contexto/contratos do respectivo dono. Não contornar as proteções dessas telas.

## Atualização do inventário

Ao adicionar ou renomear um ID, atualizar `android-ids.json` e os localizadores/cenários afetados. Arquivos e linhas são relativos ao checkout Android, nunca a caminhos pessoais. Rodar a validação Android e o dry-run do pacote. Não alterar IDs já publicados por renumeração automática.

## Chaves compactas e unicidade

`Modifier.uiId("home.0001")` publica `simlo.home.0001`. O literal é estável; o helper acrescenta apenas o namespace `simlo.`. As chaves não são calculadas por posição de composição, texto, usuário ou idioma. Não renumerar ao reorganizar telas. O inventário inclui `feature`, `source`, `line` e `symbol` para localizar cada elemento, além da chave compacta quando usada.

O gate bloqueia chaves dinâmicas/dados de usuário, namespace incorreto, IDs não registrados e duplicação de declarações distintas. A ação positiva do diálogo compartilhado tem uma exceção explícita para dois ramos mutuamente exclusivos. Repetição de instâncias da mesma linha/componente continua exigindo o escopo do contêiner no Appium.

O painel técnico de revisão de rascunho foi retirado em 30/10/2026. As chaves
`bet.0049` a `bet.0064` ficam aposentadas e não devem ser reutilizadas.
O aviso de concurso encerrado usa o diálogo LoteriasCaixaDS compartilhado e a ação
`simlo.dialog.positivo`; não deve haver localizador para “Tentar novamente” nesse
aviso permanente. O retry continua disponível para falhas temporárias de rede.

Em 01/11/2026, 35 declarações do fluxo de cartões foram realocadas para os
componentes de `core/design-system`. Os identificadores `simlo.pay.*` e os
localizadores permaneceram iguais; o inventário registra os novos arquivos.
O DS recebe os callbacks, sem conhecer IDs de compra ou operações de backend.


## Organização de Compose — 01/11/2026

A extração de Screens e seções nas 14 features/Core preserva os IDs e símbolos
publicados. O inventário Android e o legado foram atualizados somente nos
proprietários e linhas das declarações; localizadores continuam os mesmos.
A contagem de ocorrências é conferida pelo validador. Mudança de arquivo não
representa controle novo nem dispensa validar o escopo na árvore nativa.

Na continuação final da mesma revisão, os 1.777 registros principais e os 88
registros legados conservaram identificadores, chaves e símbolos. Em comparação
com a árvore já refatorada da primeira rodada, mudaram somente arquivo/linha
em 619 registros principais e 36 legados. O validador de automação foi executado
novamente; os localizadores Robot não foram renumerados.

Na refatoração adicional de 02/11/2026, as seções do diálogo de favorito foram
separadas por responsabilidade. As chaves `bet.0021` a `bet.0028` e o ID legado
do campo de nome mantêm os mesmos valores e semântica; somente os arquivos e
linhas dos proprietários foram atualizados. Os localizadores Robot continuam
iguais. O contrato de automação foi revalidado após a formatação final.

Na extração autorizada dos headers em 02/11/2026 foram acrescentados dois IDs ao
cabeçalho do carrinho: `simlo.cart.0143` para excluir todas as apostas e
`simlo.cart.0144` para salvar favorito. São aditivos; os IDs anteriores continuam
iguais. Os localizadores `carrinho.excluir_todas` e `carrinho.salvar_favorito`
apontam para esses IDs e respeitam as mesmas condições de visibilidade. Os novos
helpers passam pelo validador normal de controles, sem novas exclusões.


## FAQ em módulo dinâmico — 02/11/2026

O FAQ mantém as chaves `help.0018` e `help.0026` a `help.0031` após a mudança
para `feature/support/faq/src/main`. Termos e a mensagem compartilhada de erro
`help.0025` permanecem na base; o inventário registra os novos proprietários.
Os validadores usam a mesma seleção de fontes e incluem módulos com plugin
`com.android.dynamic-feature` declarados no `settings.gradle` ou
`settings.gradle.kts`. Diretórios temporários não declarados e `build/generated`
não ampliam essa seleção; nenhum ID existente foi renumerado.

O localizador `suporte.modulo.estado` (`id=simlo.help.0050`) identifica o
Empty State de disponibilidade, instalação e falha. Dentro desse contêiner,
`suporte.modulo.acao` usa `id=simlo.ds.0019`, o botão compartilhado do Design
System. Ele oferece “Voltar” durante indisponibilidade/instalação e “Tentar
novamente” após falha. O texto distingue os estados; não há um novo ID por
estado. A confirmação de instalação pertence à interface de plataforma.

Após a instalação habilitada, `suporte.faq.secoes` identifica a lista
`id=simlo.help.0026` e `suporte.faq.perguntas` a lista `id=simlo.help.0029`.
As linhas repetidas `help.0027`, `help.0030` e as respostas `help.0018` exigem
escopo na respectiva lista/pergunta. A presença desses IDs na árvore nativa
e a entrega do split dependem da execução no aplicativo; o gate estático
valida somente declarações e localizadores.
