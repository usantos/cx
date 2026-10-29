# Limpeza de pacotes e ownership dos DTOs — 21/10/2026

A revisão corrige o exemplo de Payment/transport: PurchaseEnvelopeDto, PurchaseErrorEnvelopeDto e PurchaseProcessingDto pertencem agora a `br.gov.caixa.loterias.feature.payment.data.dto`. O mesmo ajuste foi aplicado aos nove módulos de transporte. O caminho físico corresponde ao package.

## Alterações

- 18 arquivos de produção, com 95 DTOs, movidos de `core.network` para `feature.<feature>.data.dto`. Arquivos que agrupam DTOs usam nomes como `AccountDtos.kt`, `BettingParametersDtos.kt` e `PushRegistrationDtos.kt`; nomes das classes e contratos JSON foram preservados.
- 93 arquivos consumidores receberam os imports/referências necessários. Os imports entre contratos de features e os imports antes implícitos de Core Network agora são explícitos.
- 41 arquivos de teste deixaram os namespaces antigos `core.data`, `core.preferences` e `apostas` e foram colocados nos pacotes das features que testam. Corpos, casos e asserções foram preservados. Dois imports de helpers em CartFingerprintTest foram corrigidos após a compilação apontar sua ausência.
- Removido `config/feature-transport-compatibility.json` e o desvio de ownership que permitia o namespace antigo. Os gates verificam ownership também nos testes.
- Core Network pode consumir apenas os contratos DTO realmente declarados nos módulos transport. O gate identifica data classes terminadas em Dto no namespace data.dto da feature; não permite implementações de Repository/DataSource nem DTOs declarados no módulo Data. Os demais módulos Core continuam impedidos de consumir esses contratos de transporte.
- A regra R8 explícita para DTOs foi estendida ao novo namespace, com as mesmas flags. A regra de Core Network permanece para seus DTOs próprios. A verificação confirma preservação explícita dos 95 DTOs, além das regras fornecidas pelo Gson.

## Módulos

O inventário dos 65 módulos e o grafo resolvido pelo Gradle confirmam 64 módulos alcançáveis pelo app em produção e Core Testing usado pelos testes. Nenhum módulo declarado está sem consumidor; nenhum foi removido por ser pequeno.

Os nove módulos transport são contratos HTTP reais compartilhados entre Core Network e Data das features. Incorporá-los a Data criaria ciclos com os clientes de rede. Os três módulos api contêm contratos de domínio usados entre features. Seus nomes refletem essas responsabilidades e foram mantidos.

Foram removidos 510 diretórios vazios, contando os caminhos intermediários que ficaram vazios (485 na primeira limpeza e 25 após mover os testes). Nenhum arquivo ignorado nem artefato de build foi apagado.

## Validação

107 testes de políticas aprovados, com RED/GREEN para ownership de transporte, distinção entre DTO contratual e implementação de Data, e namespaces dos testes. Formatação dos 1.889 arquivos Kotlin aprovada. Testes focados de Network/Core Data/features afetadas e compilação de AndroidTest dos três flavors aprovados após corrigir os dois imports de helpers.

A revisão independente conferiu equivalência dos 59 arquivos movidos após normalizar imports/FQNs, preservação das anotações JSON e referências de configuração/cobertura. A proteção R8 explícita foi atualizada durante a verificação final. A matriz `modernCheck releaseCheck` passou em 22min49s: 3.675 tarefas (993 executadas, 23 de cache e 2.659 atualizadas). Foram aprovados 2.105 testes unitários e 196 execuções instrumentadas, sem falhas ou erros. Os mesmos três casos condicionados a outro perfil de tela tiveram skips nesta matriz; sua aprovação anterior no perfil adequado permanece registrada na rodada KISS, e não houve alteração visual nesta tarefa.

A comparação de identidades dos testes confirma exatamente os mesmos 2.105 casos unitários e 199 instrumentados, sem perdas ou acréscimos. Os 20 relatórios mantêm as mesmas contagens de linhas cobertas/não cobertas. Os 49 gates mantêm regras, mínimos, raízes de fontes, quatro listas de pré-requisitos e exatamente as mesmas classes selecionadas após mapear os namespaces. Nenhuma classe medida perdeu cobertura.

Os corpos dos 59 arquivos movidos e dos 75 consumidores restantes foram comparados com o baseline. Todos os 21.851 literais Kotlin, incluindo testes, foram preservados. Os 2.228 hashes de inputs permaneceram iguais durante a matriz. Manifestos, configurações Gradle e dependências permaneceram iguais. Os APKs PRD/TQS/Automação foram gerados com R8, IDs e hashes conferidos. AndroidTest dos três flavors compilado; o app oficial não foi instalado nem instrumentado.

Evidências: `build/verification/unused-packages-20261021/`. Plano: [2026-10-21-unused-packages-transport.md](superpowers/plans/2026-10-21-unused-packages-transport.md). Nenhum commit ou push.
