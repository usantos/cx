---
applyTo: "core/**,legacy-core-bridge/**,gradle/core-*-integration-coverage.gradle,gradle/verify-feature-modules.gradle,scripts/check-feature-modules.py,scripts/test_feature_modules.py,docs/CORE_STANDALONE_INTEGRATION.md,docs/superpowers/plans/*core-legacy-integration*"
---
# Core independente e integração com o legado

Leia [CORE_STANDALONE_INTEGRATION](../../docs/CORE_STANDALONE_INTEGRATION.md) e
[APP_ARCHITECTURE](../../docs/APP_ARCHITECTURE.md) antes de alterar contratos ou
dependências. `settings.gradle` e os arquivos de build são a fonte dos módulos ativos.

## Dependências e propriedade

- Core pode depender de outros módulos Core e das bibliotecas externas declaradas. Não pode importar tipos de `feature`, app ou ponte de integração, nem declarar dependências de projeto para essas camadas, inclusive em testes.
- Preserve o grafo de produção acíclico. Não resolver um erro de compilação adicionando uma seta de volta para o consumidor. Confira o grafo efetivo do Gradle, incluindo dependências adicionadas por plugins e variantes.
- Os contratos compartilhados estão em `core:domain`, pacote `br.gov.caixa.loterias.core.domain.contract`. Os DTOs dos clientes HTTP compartilhados estão em `core:network`, pacote `br.gov.caixa.loterias.core.network.dto`.
- Use uma definição canônica por tipo. Atualize imports e dependências dos consumidores; não duplicar modelos, criar `typealias` para pacotes antigos ou compilar fontes de outro módulo por `sourceSets`.
- Casos de uso, repositories e regras específicos pertencem à funcionalidade que os possui. Não mover uma implementação inteira para Core apenas para remover uma aresta do grafo.
- Não recriar `feature:account:api` nem os antigos módulos `feature:*:transport` removidos. `feature:cart:api` e `feature:marketplace:api` ainda existem com contratos próprios; consulte settings antes de remover qualquer módulo.
- Domain continua independente de Android, Compose, Retrofit e Redux. Dados de transporte não entram nas assinaturas de Domain ou ViewModels.

## Testes e build

- Testes de métodos internos permanecem no módulo proprietário. Não tornar API interna pública nem conceder dependência reversa para acomodar um teste.
- Testes de um caso de uso da funcionalidade pertencem à funcionalidade e podem usar portas/fakes Core. Declare dependências de teste diretamente quando deixarem de chegar por uma exportação transitiva.
- Preserve `verifyFeatureModules`, seus casos positivos/negativos e a inspeção de todas as configurações Core. Novas regras devem continuar detectando Core → feature e ciclos.
- A composição de cobertura que combina Core, app e funcionalidades pertence aos scripts `gradle/core-*-integration-coverage.gradle`, aplicados pela raiz. Não mover referências ao app/features de volta aos builds Core.
- A compilação independente deve conseguir configurar o conjunto Core sem incluir projetos app ou feature. Isso exige fornecer catálogo, plugins, scripts necessários e artefatos homologados; não significa ausência de dependências externas.
- Para regressão global, use `./gradlew :app:runAllTests --continue --console=plain`; no Windows, `.\gradlew.bat :app:runAllTests --continue --console=plain`. A suíte local usa JVM/Robolectric, sem aparelho ou emulador.
- Execute `verifyFeatureModules` e `verifyCleanArchitecture` nas alterações de dependências. Compilar somente Core não comprova os consumidores; valide também o app e as funcionalidades afetadas.

## Consumidor legado

- Direção permitida: app legado → ponte de integração → Core. Adaptadores que conhecem classes concretas antigas ficam no app e fornecem interfaces/callbacks à ponte; a ponte não depende do app.
- Confira versões, bytecode/JDK, minSdk, bibliotecas, manifests, SDKs e DI do legado antes de importar módulos. Não impor Hilt, Compose, Dynamic Features ou nova navegação sem necessidade da capacidade adotada.
- Preserve uma origem de verdade por sessão e por registro. Uma mudança de flag não troca o proprietário de uma sessão nem repete uma escrita financeira em outro caminho.
- Na migração de armazenamento, confirmar e reler a gravação antes de limpar a origem. Desligar uma flag não recupera automaticamente dados já migrados ou credenciais encerradas.
- O [plano de integração](../../docs/superpowers/plans/2026-11-05-core-legacy-integration.md) e seu backlog descrevem as frentes humanas e os marcos propostos. Caminhos propostos para o legado não são prova de arquivos já existentes ou de integração já executada.
