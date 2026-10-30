---
applyTo: "gradle/**,config/**,scripts/**,.github/workflows/**,**/*.gradle,**/*.gradle.kts,gradle.properties,settings.gradle,.editorconfig,**/src/test/**,**/src/androidTest/**"
---
# Build, testes e políticas de qualidade

Consulte [QUALITY_GATE](../../docs/QUALITY_GATE.md) e [hooks-and-ci](../../docs/quality/hooks-and-ci.md). Preserve os gates e a configuração existente.

- O runner oficial é `scripts/validate-pre-commit.py`; ele executa `preCommitCheck` e verifica estabilidade do workspace/index. Não efetuar stage, stash ou formatação automática dentro do hook. Preserve hooks existentes ao instalar.
- Ktlint, Detekt, Android Lint, testes, arquitetura, contratos, IDs, APK e cobertura fazem parte do gate. Não remover dependências, aceitar códigos de erro, usar `-x` ou atualizar baselines para obter aprovação.
- JaCoCo usa dados frescos e contadores agregados: `cobertas * 100 > elegíveis * 95`. Não arredondar para aprovar, reduzir denominador, excluir fontes manuais nem reutilizar execução antiga.
- Não confundir mocks/fixtures de ferramentas com resultados reais do aplicativo. Testes devem exercitar contrato e regressão; não tornar asserts permissivos nem substituir falhas por SKIP.
- Mudanças nos verificadores exigem casos positivos e negativos isolados. Não executar mutações de teste contra fontes do checkout quando existir mecanismo de fixture.
- Execute uma invocação Gradle por vez. Não atualizar toolchain/dependências fora do escopo. Consulte versões no Version Catalog.
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
