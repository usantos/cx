# Contrato de identificação Android / Robot

O app usa `Modifier.automationId` para os IDs nomeados e `Modifier.uiId` para as chaves auxiliares compactas, em `core/design-system/.../automation/AutomationIdentity.kt`. As extensões publicam o testTag e habilitam `testTagsAsResourceId` no próprio nó. Isso também atende diálogos em janelas Compose separadas, sem adicionar uma Box à hierarquia ou mudar dimensões, cores, textos ou callbacks.

O valor é literal, não derivado do texto visível, CPF, usuário ou idioma. IDs existentes foram mantidos e exportados. A extensão `legacyAutomationId` dá precedência a esses IDs sobre os novos fallbacks de wrappers, preservando seletores existentes. IDs de componentes em linhas repetidas devem ser consultados dentro do contêiner correto. No Appium use `id=simlo....`, sem prefixar `package:id/`; a sessão desabilita a autocomplementação de ID do UiAutomator2.

## Inventário

`TestData/android-ids.json` registra cada declaração estática, área, arquivo e linha de origem. A contagem abaixo é de declarações, não de elementos simultaneamente exibidos. Ramos exclusivos podem declarar o mesmo ID. IDs legados/dinâmicos mantêm seus próprios valores e estão registrados separadamente em `TestData/legacy-android-ids.json`; não estão somados nessa contagem de novos IDs `simlo.*`. Expressões dinâmicas descrevem a origem no Kotlin e precisam ser resolvidas com a massa/contêiner do caso.

| Área | Declarações estáticas |
| --- | --- |
| account | 69 |
| app | 8 |
| betting | 289 |
| cart | 142 |
| dialog | 2 |
| ds | 146 |
| home | 161 |
| marketplace | 121 |
| marketplace-filter | 39 |
| notifications | 49 |
| onboarding | 46 |
| payment | 134 |
| responsible-gaming | 79 |
| results | 95 |
| retail | 131 |
| support | 31 |
| tickets | 250 |

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

Dois arquivos de header protegidos permanecem intactos. Seus componentes básicos compartilham a infraestrutura do CaixaDS. O `testTag` não substitui `contentDescription`, role, estado ou erro: acessibilidade continua independente dos identificadores de teste.

Não é possível atribuir IDs Compose a elementos pertencentes ao Chrome/SSO, ao diálogo biométrico Android, ao launcher ou ao HTML/SDK de terceiros. Esses alvos precisam de localizadores próprios e contexto/contratos do respectivo dono. Não contornar as proteções dessas telas.

## Atualização do inventário

Ao adicionar ou renomear um ID, atualizar `android-ids.json` e os localizadores/cenários afetados. Arquivos e linhas são relativos ao checkout Android, nunca a caminhos pessoais. Rodar a validação Android e o dry-run do pacote. Não alterar IDs já publicados por renumeração automática.

## Chaves compactas e unicidade

`Modifier.uiId("home.0001")` publica `simlo.home.0001`. O literal é estável; o helper acrescenta apenas o namespace `simlo.`. As chaves não são calculadas por posição de composição, texto, usuário ou idioma. Não renumerar ao reorganizar telas. O inventário inclui `feature`, `source`, `line` e `symbol` para localizar cada elemento, além da chave compacta quando usada.

O gate bloqueia chaves dinâmicas/dados de usuário, namespace incorreto, IDs não registrados e duplicação de declarações distintas. A ação positiva do diálogo compartilhado tem uma exceção explícita para dois ramos mutuamente exclusivos. Repetição de instâncias da mesma linha/componente continua exigindo o escopo do contêiner no Appium.
