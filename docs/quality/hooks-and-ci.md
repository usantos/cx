# Pre-commit completo e CI

O hook local está em `scripts/git-hooks/pre-commit`. Ele chama `scripts/validate-pre-commit.py`, que executa o gate completo e retorna erro diante de qualquer falha. O workflow Android Quality Gate usa o mesmo runner.

## Instalação e uso

```sh
sh scripts/install-git-hooks.sh
# Depois de preparar no staging o conteúdo completo que será validado:
python3 scripts/validate-pre-commit.py
# git commit também chama esse runner automaticamente.
```

O instalador preserva hooks preexistentes e `core.hooksPath`, inclusive global. Uma instalação idêntica não é reescrita; arquivos ausentes impedem a instalação. Não configura um gerenciador de hooks nem substitui hooks de terceiros. O runner versionado é carregado da raiz do checkout atual, inclusive em worktrees.

São necessários Python 3, somente JDK 21 para Gradle/AGP, compiladores e testes (bytecode JVM 21) e o SDK/configuração local do projeto. No macOS, se `JAVA_HOME` estiver ausente, o runner usa o JBR do Android Studio instalado em `/Applications/Android Studio.app`. Um `JAVA_HOME` explícito permanece respeitado. No CI, a instalação JDK 21 do setup-java é encaminhada ao Gradle.

## Conteúdo validado

O commit fica bloqueado se houver alterações rastreadas fora do staging ou arquivos não rastreados/não ignorados. Isso impede que uma correção ainda fora do index esconda um defeito no conteúdo que será commitado. Arquivos locais ignorados e saídas de build continuam permitidos. O script não executa stash, checkout, add, formatação ou geração de baseline.

Ao terminar o gate, o runner verifica novamente workspace e árvore do index. Se arquivos forem editados, adicionados ou preparados durante a validação, o commit é bloqueado e deve ser repetido sobre o conteúdo final. Erros Git, ferramentas ausentes e interrupções também bloqueiam.

## Gate completo

A chamada fixa é:

```sh
./gradlew preCommitCheck --continue --console=plain
```

A suíte é executada novamente e pode levar dezenas de minutos. Nesta validação local, a execução integral levou 47min33s; o tempo depende do equipamento e dos caches.

`preCommitCheck` inclui:

- `qualityGate`: Ktlint global, Detekt, Android Lint, testes unitários frescos de todos os módulos e JaCoCo LINE global **estritamente maior que 95%**;
- `verifyKotlinFormatting`, `verifyCleanArchitecture` e suas políticas de App/Feature, propriedade das fontes e grafo sem ciclos;
- `verifyComposeMigration`, inventário de UI, paridade de backend e CaixaDS;
- identidade/flavors, segurança de rede, FilaBra e Topaz;
- testes Python de arquitetura, cobertura, preparação de UI e assinatura/rotação Remote Config;
- testes do próprio hook com commits reais em repositórios temporários;
- 33 verificações positivas/negativas dos contratos XML, em cópias temporárias sem modificar recursos do checkout;
- relatório agregado de testes e montagem do APK PRD Debug.

`--continue` permite coletar falhas independentes; não transforma falha em sucesso. Dependências de uma etapa que falhou podem ficar sem executar, e a saída continua diferente de zero. O runner não aceita argumentos, atalhos ou exclusões. O `qualityGate` também rejeita `-x` e tarefas de geração de baseline/formatação no mesmo grafo. Nenhum resultado de cobertura anterior substitui a execução atual.

Uma verificação reprovada, inclusive dívida preexistente de Ktlint, impede o commit. O script não equivale a cobertura de 100%: aplica o limiar vigente, sem excluir código para elevá-lo. Instrumentação física, TalkBack e release/R8 conservam verificações próprias; não são presumidos a partir do APK Debug.

## Testes e relatórios

```sh
python3 scripts/test-git-hooks.py
./gradlew verifyComposePlatformResourcePolicy
```

Os testes do hook usam Git real em diretórios descartáveis; somente o processo Gradle é substituído para simular aprovação, falha e mudanças concorrentes. O controle de cobertura baixa usa o verificador JaCoCo real. Cobrem caminhos com espaços, worktrees, instalação repetida, preservação de hooks, staging parcial, alterações durante execução, ferramentas ausentes, sinais e sucesso.

Os relatórios permanecem em `build/reports/quality`, `build/reports/architecture`, `build/reports/tests/aggregate`, `build/reports/jacoco/aggregate` e nos diretórios de cada módulo. O console identifica a tarefa que falhou. As evidências de implementação de 28/10 ficam em `build/verification/pre-commit-20261028/`.

## Limite do hook local e proteção remota

O Git permite ao próprio usuário desabilitar hooks locais. Para impedir integração sem aprovação, o administrador deve tornar **Android Quality Gate** obrigatório nas regras de proteção da branch/fila de merge. O workflow versionado não configura essas regras sozinho. Esta tarefa não alterou proteção remota nem executou CI remoto.

O workflow continua usando os secrets `SIMLO_*` documentados no projeto e publica relatórios mesmo em falha. PRs de forks sem configuração exigida permanecem bloqueados; não usa `pull_request_target` para executar código contribuído.

## Contrato de IDs para Robot/Appium - 29/10/2026

`verifyAutomationIds` passou a integrar `preCommitCheck`. O gate confere declarações estáticas de `automationId`, o inventário distribuído e os localizadores Robot; rejeita controles Compose básicos sem ID explícito. Os testes do gate usam fixtures isoladas e são descobertos por `verifyToolingPolicyTests`. A verificação não inicia Appium nem exige farm/credenciais.

A entrega independente fica em [automation/robot-certificacao](../../automation/robot-certificacao/README.md). O [contrato Android](../../automation/robot-certificacao/docs/IDS_ANDROID.md) distingue IDs por componente, escopo de listas e telas externas. Inspeção da árvore nativa e execução Robot real são etapas de CI/certificação próprias; dry-run não é homologação. Headers e baselines permanecem preservados.
