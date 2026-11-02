# Plano — análise estática e quality gate

Especificação: dois textos fornecidos pelo usuário em 22/10/2026 (Detekt; Lint/Ktlint/JaCoCo/pre-commit).

## Restrições
- Preservar Kotlin 2.4.10, AGP 9.2.1, Gradle 9.4.1, arquitetura e código de produção.
- Configuração compartilhada, versões centralizadas, baselines somente para dívida estática existente.
- Não executar formatação automática durante validação.
- Cobertura LINE global ponderada >=95%, sem excluir código de negócio ou módulos sem testes.
- Dados frescos; uma variante canônica por módulo; distinguir testes JVM e instrumentados.
- Não afirmar aprovação se a cobertura real for inferior à meta; publicar lacuna e relatórios.

## Etapas
1. Inventariar configuração existente e compatibilidade oficial das ferramentas.
2. Integrar Detekt estável e Ktlint com configuração compartilhada e relatórios, validando parsing e violações novas.
3. Centralizar Android Lint sem enfraquecer verificações de release; baseline legado revisável.
4. Ampliar o relatório agregado JaCoCo para verificação global com inventário de módulos e dados atuais.
5. Criar quickQualityCheck e qualityGate com ordem Ktlint → Detekt → Lint → testes → cobertura.
6. Versionar e testar hook/instalador conservador e CI preservando pipelines existentes.
7. Executar ferramentas, controles negativos e testes; documentar comandos, limites e resultado verdadeiro.

## Divisão de arquivos
- Principal: catálogo, Detekt/Ktlint, orquestração Gradle e documentação final.
- Cobertura: gradle/aggregate-test-reports.gradle, verificador e testes associados.
- Lint: gradle/android-lint.gradle e baselines Lint.
- Hooks/CI: scripts/git-hooks, instalador, testes e workflow dedicado.

Somente o agente principal executa Gradle para evitar concorrência sobre builds e dados de cobertura.
