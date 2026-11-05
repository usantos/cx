# Core independente das funcionalidades

O grafo de dependências deve seguir `app/feature → core`. Nenhum módulo `core`
pode importar classes de `feature` ou declarar dependências de projeto para
funcionalidades, inclusive nas configurações de teste. As dependências entre
módulos de produção não podem formar ciclos.

## Contratos compartilhados

- `core:domain`, em `br.gov.caixa.loterias.core.domain.contract`: contratos de
  identidade e conteúdo de sessão, autenticação, dados compartilhados entre
  destinos e modelos usados na configuração das funcionalidades.
- `core:network`, em `br.gov.caixa.loterias.core.network.dto`: contratos de
  transporte dos serviços HTTP usados pelo cliente compartilhado. Campos JSON,
  anotações de serialização e comportamento permanecem preservados.
- Implementações de casos de uso e repositórios específicos continuam nos seus
  módulos de funcionalidade. Seus testes pertencem a esses mesmos módulos.

Os contratos foram extraídos fisicamente: não há cópias de modelos, `typealias`
de compatibilidade ou compilação de fontes externas por `sourceSets`. Os
consumidores passaram a importar o contrato canônico do core.

O antigo `feature:account:api` e os dez módulos `feature:*:transport` deixaram de
ser necessários e foram removidos do build. `feature:cart:api` e
`feature:marketplace:api` permanecem porque ainda contêm contratos próprios das
funcionalidades; eles consomem `core:domain`.

## Compilação e cobertura

As dependências e as fontes dos módulos core podem ser configuradas sem incluir
`app` e `feature`. O consumidor precisa fornecer o catálogo de versões, os plugins
e a infraestrutura de build usada pelo conjunto selecionado, incluindo os scripts
e artefatos homologados exigidos por `core:security`.

A composição dos relatórios que combina testes do core, app e funcionalidades
pertence à raiz do aplicativo, nos scripts
`gradle/core-*-integration-coverage.gradle`. Os limites e dados de cobertura não
foram removidos para tornar o core independente.

## Proteção contra regressões

`verifyFeatureModules` verifica os arquivos de build e o grafo efetivamente
configurado pelo Gradle. A verificação rejeita imports de funcionalidades no
core, dependências adicionadas por plugins/configurações de teste e ciclos no
grafo de produção.

```shell
./gradlew verifyFeatureModules verifyCleanArchitecture
./gradlew :app:runAllTests --continue --console=plain
```

O segundo comando continua executando a suíte JVM global em PRD, TQS e Automação,
sem aparelho ou emulador. Bibliotecas sem flavor executam uma vez.

Ao atualizar um checkout com os pacotes anteriores, regenere as saídas de build
para evitar a reutilização de arquivos de classes e DEX antigos.

## Validação em 04/11/2026

- Consumidor isolado com os 14 módulos Core: compilação aprovada sem projetos App
  ou feature, com conferência de todas as dependências de projeto configuradas.
- Grafo configurado do aplicativo: nenhuma dependência Core → feature e nenhum
  ciclo de produção; verificações de arquitetura e fontes rastreadas aprovadas.
- 105 tarefas de compilação de testes JVM aprovadas; as 91 entradas de
  `runAllTests` continuam encaminhando para a suíte global.
- `:app:runAllTests`: 5.776 testes em 1.235 suítes, sem falhas, erros ou ignorados.
  Foram 950 testes por ambiente e 2.926 compartilhados; o App teve 412 por ambiente.
  Nenhum resultado de teste veio do cache e nenhuma tarefa de dispositivo foi usada.
- Ktlint, Detekt e Android Lint aprovados. Cobertura de linhas de 95,29%
  (63.348/66.477), preservando o limite obrigatório acima de 95%.
- 240 testes das ferramentas aprovados. As ocorrências existentes nas baselines
  foram reposicionadas, sem acrescentar exceções.

Foi acrescentado um teste da observação de sessão na funcionalidade. Os testes de
concorrência e publicação do armazenamento permanecem em `core:auth`, com acesso
aos seus métodos internos e sem dependência reversa para a funcionalidade.
