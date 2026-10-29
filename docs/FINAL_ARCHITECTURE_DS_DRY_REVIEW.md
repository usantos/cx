# Revisão final de arquitetura, DS e DRY — 21/10/2026

Revisão de app, 12 módulos core, 52 módulos de features, plugins de convenção, políticas, recursos e cobertura. Foram conferidos proprietários de código, dependências, contratos públicos locais, DS, duplicações equivalentes e persistência. Houve revisão cruzada das alterações; os achados foram corrigidos antes da conclusão.

## Correções

- Tipos e arquivos Kotlin com Legacy foram renomeados em todos os módulos, incluindo testes; APIs, tokens e aliases do DS migraram com seus consumidores. Inventários e seletores acompanham os nomes novos. A política também verifica enums/records Java, nomes Kotlin escapados e fachadas `@file:JvmName`, ignorando exemplos em strings/comentários.
- `CaixaDSContentState` substitui as duas implementações idênticas em Cart/Betting. O teste Compose verifica precedência loading/erro/conteúdo e callback de nova tentativa. Cores repetidas passaram a referenciar tokens existentes; renderização da bola numérica foi compartilhada sem mudar layout, clique ou sobreposição.
- Quatro cópias da leitura recursiva de números JSON passaram a usar uma implementação em core/network. A formatação monetária equivalente foi centralizada em core/presentation, preservando políticas distintas de parsing, nulidade e espaços. Foram adicionados quatro testes de JSON e dois de moeda.
- A entrada HomeRoute recebe diretamente os consumidores do antigo encaminhamento HomeScreen. O reset da sessão usa a operação de limpeza já responsável pelos mesmos campos.
- Foram retirados 135 recursos idênticos do app, com consumidores apontando para os recursos das features, além de duas cópias não utilizadas da fixture de resultado de bilhete. Os inventários de XML foram atualizados; recursos com resolução conflitante foram mantidos.
- Dependências duplicadas foram retiradas, incluindo as fornecidas pelo plugin de convenção. Marketplace declara explicitamente o novo consumo de core/presentation. A seleção JaCoCo de Network usa uma lista compartilhada, sem repetir os padrões por raiz.

A análise dos caminhos de persistência confirmou chaves, tabelas, nomes de fases, campos JSON, fingerprints e hashes de proprietário preservados. Os dados não dependem dos nomes JVM renomeados. Contratos/adaptadores necessários à direção das dependências e variantes visuais com comportamento diferente foram mantidos.

## Validação final

| Verificação | Resultado |
|---|---|
| Políticas de arquitetura/módulos | 74 testes aprovados |
| Guardrails Compose/DS com fixtures negativas | 63 cenários aprovados, arquivos restaurados |
| modernCheck + releaseCheck | BUILD SUCCESSFUL em 30min59s; 3.674 tarefas, 748 executadas, 35 de cache e 2.891 atualizadas |
| Testes unitários da matriz | 2.095 aprovados, zero falhas/erros/skips |
| Instrumentação da matriz | 196 aprovados, três skips por perfil, zero falhas/erros |
| Três casos de coordenadas no perfil correto | Três aprovados, zero falhas/erros/skips |
| Instrumentação adicional do DS | 18 aprovados, zero falhas/erros/skips |
| Compilação AndroidTest do app | PRD, TQS e Automação aprovados, sem instalar o host |
| Releases minificados | PRD, TQS e Automação gerados com R8; applicationIds/hashes conferidos |
| Regras e limiares de cobertura | 49 conjuntos inalterados; nenhuma classe anteriormente selecionada foi perdida após normalização dos nomes |
| Integridade durante a validação | 2.218 hashes de inputs da árvore final inalterados |

São 217 casos instrumentados distintos aprovados entre os dois perfis: 199 da matriz/complemento e 18 do DS. Os três skips permanecem no relatório bruto da matriz, com evidência complementar própria. Persistência mantém 526/532 linhas (98,87%); Core Domain 1.763/1.823 (96,71%); Core Data 2.531/2.662 (95,08%); Core Network 724/731 (99,04%). Os helpers compartilhados integram a cobertura; Network também passou a selecionar cinco DTOs adicionais. Nenhum limiar foi reduzido, teste removido ou expectativa relaxada.

A primeira tentativa de integração detectou a dependência faltante de Marketplace; foi corrigida e a matriz final passou. Chamadas preparatórias interrompidas não contam como validação. O AVD AOSP temporário reproduziu1080×2408,450dpi,fontScale1,0 e barra de status66px. AVD e imagem instalados para a tarefa foram removidos; emulador5560 e quatro AVDs originais foram preservados. Nenhum commit, push ou publicação foi realizado.

## Evidências

`build/verification/final-architecture-ds-dry-20261021/` contém manifest final, log completo, 382 XML da matriz, XML adicionais do DS/perfil de tela, relatórios JaCoCo, comparação dos 49 conjuntos de regras/classes, mapas de renomeação, inventários de recursos e dependências, hashes dos APKs e delta da tarefa. `before.tar.gz` preserva o snapshot inicial; todos os hashes originais foram conferidos antes de compactar a cópia de trabalho. Os relatórios desta revisão são separados das alterações preexistentes do checkout.

Não restaram achados acionáveis conhecidos no escopo revisado. A conclusão está vinculada ao código e aos cenários validados, sem afirmar ausência absoluta de defeitos não detectados.
