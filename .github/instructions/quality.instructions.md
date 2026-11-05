---
applyTo: "gradle/**,config/**,scripts/**,.github/workflows/**,**/*.gradle,**/*.gradle.kts,gradle.properties,settings.gradle,.editorconfig,**/src/test/**,**/src/androidTest/**"
---
# Build, testes e políticas de qualidade

Consulte [QUALITY_GATE](../../docs/QUALITY_GATE.md) e [hooks-and-ci](../../docs/quality/hooks-and-ci.md). Preserve os gates e a configuração existente.

- O runner oficial é `scripts/validate-pre-commit.py`; ele executa `preCommitCheck` e verifica estabilidade do workspace/index. Não efetuar stage, stash ou formatação automática dentro do hook. Preserve hooks existentes ao instalar.
- Ktlint, Detekt, Android Lint, testes, arquitetura, contratos, IDs, APK e cobertura fazem parte do gate. Não remover dependências, aceitar códigos de erro, usar `-x` ou atualizar baselines para obter aprovação.
- JaCoCo usa dados frescos e contadores agregados: `cobertas * 100 > elegíveis * 95`. Não arredondar para aprovar, reduzir denominador, excluir fontes manuais nem aceitar execução antiga sem validação nativa de entradas e saídas pelo Gradle no modo incremental.
- Não confundir mocks/fixtures de ferramentas com resultados reais do aplicativo. Testes devem exercitar contrato e regressão; não tornar asserts permissivos nem substituir falhas por SKIP.
- Mudanças nos verificadores exigem casos positivos e negativos isolados. Não executar mutações de teste contra fontes do checkout quando existir mecanismo de fixture.
- Execute uma invocação Gradle por vez. Não atualizar toolchain/dependências fora do escopo. Consulte versões no Version Catalog.
- Testes locais, além do pre-commit, devem executar na JVM sem emulador/ADB em TQS, PRD e Automação. Use `./gradlew :app:runAllTests --continue --console=plain`, a tarefa `runAllTests` de qualquer módulo ou a configuração compartilhada `Run All Tests — projeto completo (JVM)`. `allEnvironmentTestReport` continua disponível. Não criar/iniciar AVD para essa execução. O menu nativo `Tests in '<módulo>'` não executa a suíte global. Cenários instrumentados de hardware/sistema permanecem separados e usam aparelho físico, sem substituí-los por SKIP ou afirmar que foram executados pela suíte local.
- No Windows, use `.\gradlew.bat`. Scripts invocados pelo Gradle usam o detector de `gradle/python-runtime.gradle` e `rootProject.pythonCommand()`; não adicionar `commandLine 'python3'` fixo nem depender de aliases da Microsoft Store para resolver Python.
- Uma tarefa global ausente pode indicar checkout desatualizado ou projeto diferente. Confira branch, commit, remoto, pasta aberta e script aplicado antes de recriar a tarefa ou mudar a arquitetura. Nunca usar sincronização ou limpeza como substituto da verificação da versão do código.
- Segredos do CI vêm das configurações autorizadas; não publicar valores em logs/artefatos. Workflow não configura proteção de branch sozinho; hooks locais podem ser desabilitados pelo usuário.
- Ao reportar, diferencie PASSED, FAILED e NOT RUN. Aprovação de um módulo não equivale ao pre-commit completo. Documente limitações existentes sem mascará-las.

Exceção autorizada em 30/10/2026: somente `AndroidGradlePluginVersion`,
`GradleDependency`, `NewerVersionAvailable` e `OldTargetApi` são informativas
para manter AGP 8.13.0/SDK 36. Preserve a lista exata, o bloqueio de erros e
warnings e a tarefa `verifyLintPolicy`. Não estender essa exceção a outras regras.

- Preserve `verifyTrackedSources` e a verificação antecipada do runner: fontes
  Kotlin/Java em `src` não podem depender de arquivos ignorados. A regra de
  metadados do Finder não pode ignorar o pacote `icon`. Valide fontes novas
  também pelo conteúdo preparado no Git, mantendo os controles de capitalização.

O hook local seleciona módulos alterados e consumidores pelo index/grafo Gradle.
Preserve o fallback completo para mudanças compartilhadas/CI, os contratos globais
e JaCoCo global >95%. Somente resultados nativos validados pelo Gradle podem ser
reutilizados; não substitua essa verificação por presença de relatórios antigos.
