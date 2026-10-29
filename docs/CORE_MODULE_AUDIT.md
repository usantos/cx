# Auditoria dos módulos CORE

Atualização: 20/10/2026, após a remoção de `core:database`. Foram inspecionados os 14 módulos CORE então declarados, seus fontes de produção, consumidores Gradle, testes e responsabilidades de cobertura. Dois módulos foram retirados; restam 12 módulos CORE e 65 módulos no projeto principal.

## Módulos retirados nesta rodada

- `core:datastore`: `UserPreferences.named` apenas encaminhava `Context.getSharedPreferences`; cinco adaptadores Data agora chamam Android diretamente. `EnvironmentPreferences` tinha comportamento real, mas um único consumidor de produção no APP: implementação e teste de falha/retentativa de commit foram movidos para `app/environment`, preservando os corpos. A classe agora é interna ao APP.
- `core:preferences`: biblioteca Android sem fontes próprios, mantida apenas para agregar cobertura. A tarefa raiz `verifyPersistenceCoverage`, definida em `gradle/persistence-coverage.gradle` e exigida por `modernCheck`, conserva as suítes instrumentadas/JVM, seletores de classes e limiar de 95,01%. Foram retiradas apenas entradas do módulo sem testes e da classe de encaminhamento eliminada. Nenhum teste foi excluído.

## Módulos mantidos

| Módulo | Responsabilidade efetiva que justifica sua existência |
|---|---|
| `core:auth` | Coordenação de autenticação, biometria, tokens, concorrência e recuperação de sessão |
| `core:common` | Resultados compartilhados, fila de eventos, dispatchers, conectividade e relógio monotônico de servidor |
| `core:data` | Cache e mapeamento de configuração, tradução de falhas e composição de dados compartilhados |
| `core:design-system` | Componentes Compose, recursos, temas e regras visuais compartilhados |
| `core:domain` | Contratos e políticas compartilhados de configuração, ambiente e resultados de serviços |
| `core:key-management` | Verificação de assinaturas, resolução de conjuntos de chaves e contratos de armazenamento |
| `core:navigation` | Estado e contratos compartilhados de navegação, restauração e continuação de fluxos |
| `core:network` | Clientes/interceptors HTTP, políticas de sessão/segurança/tentativa, transporte e backend mock |
| `core:presentation` | Apresentação compartilhada de falhas, estado de campanhas e efeitos de lifecycle |
| `core:queue` | Integração FilaBRA, WebView, parsing de eventos, broadcasts e apresentação da fila |
| `core:security` | Restauração de componentes Topaz, fonte Firebase de chaves e adaptadores de plataforma |
| `core:testing` | Fake compartilhado de sessão e regra JUnit que configura/restaura o dispatcher Main |

A presença de interfaces, adaptadores ou métodos curtos dentro desses módulos não os torna fachadas redundantes: os módulos mantêm estado, políticas, contratos ou integrações compartilhados. Não foram identificados outros módulos inteiros que apenas encaminhem chamadas no escopo desta revisão.

## Verificação

Arquitetura, módulos, composição e formatação aprovados; compilação TQS Debug/Release e testes unitários do APP PRD/TQS/Automação aprovados, incluindo o teste de ambiente realocado nos três flavors. A execução direcionada reuniu 1.024 testes unitários e 32 instrumentados, sem falhas/erros/ignorados. Persistência: 526/532 linhas (98,87%); Domain: 1.763/1.823 (96,71%). Os dois limiares permanecem em 95,01%.

O dry-run inicial confirmou a resolução do grafo. Depois, a matriz completa `ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain` passou em 20min46s: 3.674 tarefas, 551 executadas, 11 de cache e 3.112 atualizadas. Foram aprovados 2.089 testes unitários e 196 execuções instrumentadas; três dos 199 casos instrumentados foram ignorados por exigir outro perfil físico de tela (um Retail e dois Tickets), sem falhas ou erros. Os releases PRD, TQS e Automação foram gerados com R8. Nenhuma correção foi necessária; 23 configurações de cobertura e 2.242 hashes de fontes/configurações permaneceram inalterados durante a execução.

A revisão independente do patch encerrou sem achados. Evidências da remoção: `build/verification/core-module-facades-20261020/`; evidências da matriz completa: `build/verification/core-removals-full-matrix-20261020/`, incluindo 380 XML de testes, relatórios de cobertura, log e hashes dos três APKs. Não houve commit, push ou alterações no APK nativo instalado.
