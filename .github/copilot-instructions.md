# GitHub Copilot — Loterias CAIXA Android

Responda em português. Leia o código e os guias abaixo antes de propor mudanças. Comentários novos e documentação ficam em português; preserve nomes de APIs e contratos externos.

## Estrutura e arquitetura obrigatória

Projeto Gradle modular Kotlin/Jetpack Compose, MVVM, Clean Architecture, ReduxKotlin e Hilt. `settings.gradle` é a fonte dos módulos; `:app` é o host. Não existe `app-compose`.
- App: composição, inicialização e navegação; não implementar regras de negócio ou clientes de serviços no host.
- Presentation: Route integra plataforma; Screen recebe estado/callbacks; ViewModel coordena casos de uso e efeitos. Estado de negócio de UI usa a ponte Redux existente; reducers puros, sem I/O, relógio global ou navegação.
- Domain: regras, entidades, portas e casos de uso; sem Android, Compose, Retrofit ou Redux.
- Data: repositories, fontes e mapeadores; transporte/DTO não vaza para Presentation/Domain. DI liga contratos a implementações. Dependências entre features passam pelos contratos existentes, sem ciclos ou módulos vazios.
- Use CaixaDS e preserve acessibilidade e IDs estáveis. Leia as instruções específicas em `.github/instructions/` antes de editar a respectiva área, inclusive quando o cliente não as anexar automaticamente.

## Restrições de toda alteração

Não alterar headers visuais protegidos, headers HTTP/autenticação nem baselines. Não reduzir gates, adicionar supressões/exclusões para mascarar falhas, excluir código do JaCoCo ou diminuir o limiar: LINE global estritamente maior que 95%.
Headers visuais protegidos: `CurrentCartScaffold.kt` e `CaixaDSScreenComponents.kt`. Não modificar esses arquivos para facilitar mudanças de UI ou testes.
Não versionar credenciais, tokens, dados pessoais, keystores, configurações locais, APKs, caches, resultados de execução ou PDFs exportados. Não desligar TLS/pinning/verificação de instalação. Não efetivar apostas/pagamentos reais em testes.
Preserve alterações existentes; não usar reset/clean abrangente, apagar dados de dispositivos ou instalar variantes extras sem necessidade autorizada. TQS Debug é a variante de desenvolvimento padrão; PRD e Automação exigem escolha explícita. Não confundir os backends TQS/Automação, que compartilham application ID.

## Validação e conclusão

Execute testes pertinentes durante a implementação, uma invocação Gradle por vez. JDK 21 para Gradle, toolchain JVM 17 e SDK conforme o projeto. Configuração pública autorizada em `.local/build-security.properties`, baseada no exemplo em `gradle/`.
- Feedback estático: `./gradlew quickQualityCheck`.
- Contratos de arquitetura/IDs: `./gradlew verifyCleanArchitecture verifyAutomationIds`.
- APK de desenvolvimento: `./gradlew :app:assembleTqsDebug`.
- Gate completo: `./gradlew preCommitCheck --continue --console=plain`.
- Instalar hook: `sh scripts/install-git-hooks.sh`. Antes do commit, com workspace/index completos e correspondentes: `python3 scripts/validate-pre-commit.py`.
Não preparar arquivos nem criar commit automaticamente. Não usar `--no-verify`, `-x`, atualizar baseline ou aceitar falha como sucesso. Informe comandos executados, resultados e limites; dry-run, build Debug e relatório antigo não comprovam certificação/release/cobertura atual.

## Fontes de consulta

- [Arquitetura](../docs/APP_ARCHITECTURE.md), [Redux/MVVM](../docs/REDUX_MVVM.md).
- [Classes, serviços, endpoints, parâmetros e retornos por funcionalidade](../docs/DATA_ACCESS_BY_FEATURE.md).
- [Qualidade](../docs/QUALITY_GATE.md), [hook e CI](../docs/quality/hooks-and-ci.md).
- [Ambientes](../docs/ENVIRONMENTS.md), [TLS](../docs/SECURE_PIN_ROTATION.md).
- [Documentação técnica/PDF](../docs/technical-guide/README.md), [Robot](../automation/robot-certificacao/README.md).
Atualize a documentação afetada junto com o código e preserve a proveniência das evidências.
