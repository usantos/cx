# Loterias CAIXA Android

Aplicativo Android modular em Jetpack Compose, estruturado com MVVM, Clean Architecture, ReduxKotlin e injeção de dependências por Hilt.

## Estrutura

```text
app/                 composition root, único host Android e Navigation Compose
core/common/         Result, erros, dispatchers e contratos compartilhados
core/domain/         entidades, contratos de repository e casos de uso
core/data/           data sources, implementations de repository e mapeadores
core/persistence/    armazenamento seguro compartilhado e contratos de persistência
core/network/        Retrofit, OkHttp, DTOs, logs e cURL sanitizado
core/design-system/  CaixaDS: tokens, tema, ícones e componentes
core/presentation/   contratos de UI, reducers e fluxos reutilizáveis
core/*               autenticação, navegação, chaves e integrações isoladas
feature/*/presentation/  telas Compose, ViewModels, UiState, navegação e DI por funcionalidade
feature/*/domain/        regras, modelos e casos de uso independentes
feature/*/data/          repositories, fontes locais/remotas e mapeadores
feature/*/{api,transport}/  contratos compartilhados e DTOs, quando necessários
docs/                arquitetura, bibliotecas, telas, segurança e decisões
```

Não existe módulo `app-compose`: a aplicação Compose definitiva é o próprio `:app`. As features não possuem layouts XML, Fragments ou Activities. `MainActivity` hospeda o produto; `QueueActivity` é uma entrada privada de segurança da fila. O sample independente possui sua própria `CatalogActivity`. Manifestos e adaptadores Android permanecem nos módulos responsáveis pela plataforma.

Cada feature agrupa módulos independentes. O código exibido como `kotlin+java`
no Android Studio pertence a `:feature:<nome>:presentation`; `data` e `domain`
ficam no mesmo nível. Por exemplo, as telas de conta estão em
`feature/account/presentation/src/main/java/br/gov/caixa/loterias/feature/account/presentation/screen`.
`di`, `navigation`, recursos e testes acompanham esse módulo, preservando os
pacotes Kotlin e o namespace Android. As pastas-pai não compilam fontes.

## GitHub Copilot

As regras do projeto inteiro estão em [.github/copilot-instructions.md](.github/copilot-instructions.md), complementadas por [instruções por área](.github/instructions/). Elas cobrem arquitetura, Android/CaixaDS, IDs, segurança, testes, Gradle/CI, documentação e Robot. Consulte o [guia de uso](docs/COPILOT.md). O pacote Robot contém configuração própria para o repositório receptor.

## Configuração local e CI

A versão de build é **AGP 8.13.0 + Gradle 8.14.6 + SDK 36**, compatível com o
Android Studio que suporta AGP 8.13. O plugin Kotlin Android é explícito.

Use somente **JDK 21**: Gradle, toolchains, testes e bytecode de todos os módulos
estão configurados em 21. Não é necessário instalar JDK 17. No Android Studio,
selecione JDK 21 em **Settings → Build, Execution, Deployment → Build Tools →
Gradle → Gradle JDK**; no terminal, configure `JAVA_HOME` para a mesma instalação.
No Windows, por exemplo (ajuste o caminho à instalação):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat --version
```

Antes de compilar em um novo checkout, copie `gradle/build-security.properties.example`
para `.local/build-security.properties` e preencha com os valores públicos autorizados.
O arquivo local é ignorado pelo Git; o CI pode fornecer as mesmas chaves por variáveis
`SIMLO_*`. A configuração atual deste checkout foi preservada nesse arquivo local.
Pins vazios ou inválidos interrompem o build. Chaves privadas não devem ser incluídas.
Veja [rotação segura de pins](docs/SECURE_PIN_ROTATION.md).

## Primeira sincronização, inclusive no Windows

FilaBRA e Topaz são preparados automaticamente por transformações de dependências
no cache do Gradle. O Sync não depende de AARs previamente gerados em `app/build`
ou `build`, nem de uma compilação anterior. As correções TLS e de `SecureRandom`
são preservadas; os AARs originais continuam imutáveis e verificados por SHA-256.
A preparação usa JDK 21. Python continua necessário para os testes e gates existentes.

Para conferir a resolução e a extração usadas no Sync:

```powershell
.\gradlew.bat :app:verifyVendorArtifactsForSync
```

O pre-commit confere também fontes Kotlin/Java ignoradas ou fora do index e
colisões de nomes por maiúsculas. O pacote `icon` do CaixaDS faz parte do Git.
Veja o [sample do design system](sample/design-system/README.md) para compilar
e abrir o catálogo independente.

## Home selecionável

A Home v2 usa a apresentação do projeto CX (`HomeV2View`), portada para o host
Compose e ligada aos casos de uso existentes. A home legada é o padrão;
a v2 é ativada explicitamente com `-PhomeV2=true`.

```bash
./gradlew :app:assembleTqsDebug -PhomeV2=true
./gradlew :app:assembleTqsDebug -PhomeV2=false
```

A mesma propriedade vale para `prd`, `tqs`, `automacao`, debug e release.
`false` mantém a home anterior. A ordem das modalidades é persistida localmente;
concursos, prêmios, disponibilidade de bolões e resultados vêm dos serviços.
Ver [auditoria da Home v2 e pendências de paridade](docs/HOME_V2_CX_PARITY_20261013.md).

## Verificação

```bash
./gradlew modernCheck
```

O comando compila o APK, executa lint, testes, gates de cobertura, regras do CaixaDS, validações de segurança e as restrições de dependência da Clean Architecture.

O gate global de qualidade (`./gradlew qualityGate`) reúne Ktlint, Detekt, Lint,
testes unitários frescos e cobertura LINE global estritamente maior que 95%. Para feedback
estático, use `./gradlew quickQualityCheck`. Baselines, relatórios e instalação
conservadora do pre-commit estão no [guia de qualidade](docs/QUALITY_GATE.md). O hook completo usa `scripts/validate-pre-commit.py` e `preCommitCheck`, incluindo arquitetura, contratos, testes dos scripts e APK; bloqueia qualquer falha ou divergência entre workspace e staging. Instale com `sh scripts/install-git-hooks.sh`.

## Documentação

- [Novidades consolidadas de 28/10/2026](docs/UPDATES_20261028.md)
- [Pre-commit completo e CI](docs/quality/hooks-and-ci.md)
- [Guia técnico e evidências](docs/technical-guide/README.md)

- [Quality gate e cobertura global](docs/QUALITY_GATE.md)
- [Arquitetura](docs/APP_ARCHITECTURE.md)
- [Acesso aos dados por funcionalidade — usuário, apostas e demais áreas](docs/DATA_ACCESS_BY_FEATURE.md)
- [Auditoria arquitetural completa de 28/10/2026](docs/architecture/FULL_AUDIT_20261028.md)
- [Acessibilidade — critérios, implementação e evidências](docs/ACCESSIBILITY.md)
- [Auditoria arquitetural integral de 27/10/2026](docs/architecture/FULL_AUDIT_20261027.md)
- [Bibliotecas e toolchain](docs/LIBRARIES.md)
- [Ambientes e product flavors](docs/ENVIRONMENTS.md)
- [Catálogo de telas](docs/SCREENS.md)
- [CaixaDS](docs/CAIXA_DS.md)
- [Estado da migração](docs/MIGRATION_STATUS.md)
- [Rotação segura de pins TLS](docs/SECURE_PIN_ROTATION.md)
- [Paridade e migração dos backends](docs/BACKEND_MIGRATION.md)
- [Backend mock e catálogo de cenários](docs/MOCK_BACKEND.md)

## Arquivos gerados

`build/`, `tmp/`, caches, configurações locais e `output/` ficam fora do Git. APKs/AABs, PDFs exportados e renderizações de verificação são artefatos locais. Os recursos do app, imagens e manifestos necessários à documentação permanecem nas respectivas pastas de fontes; o guia pode ser regenerado em um checkout novo.

### Automação Robot / certificação

O [pacote independente Robot/Appium](automation/robot-certificacao/README.md) pode ser copiado para o repositório de certificação. Inclui cenários iniciais em português, conexão local/farm parametrizada e [contrato de IDs Android](automation/robot-certificacao/docs/IDS_ANDROID.md). O pre-commit inclui `verifyAutomationIds`; testes reais em dispositivo possuem execução e evidências próprias.
