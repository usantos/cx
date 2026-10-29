# Revisão KISS de todos os módulos — 21/10/2026

Escopo: app, 12 módulos core e 52 módulos de features, além das configurações de cobertura relacionadas. A revisão buscou complexidade sem responsabilidade própria, preservando fronteiras de arquitetura, persistência, contratos, comportamento visual e limiares de cobertura.

## Simplificações

- A conclusão de conta usa o estado do ViewModel proprietário. Dois espelhos de estado na sessão e o SideEffect que os sincronizava foram removidos. Os testes verificam reset, cancelamento, erro, retry e restauração dos demais estados transitórios.
- A troca de ambiente do host usa uma operação e um contexto de IO. Permanecem o atalho para seleção já persistida, a ordem cancelamento/limpeza/persistência, a propagação de erro de commit e a repetição segura após falha. Três testes instrumentados verificam esses caminhos sem inicializar clientes reais.
- Em 27 coordenadores/projeções, 89 leituras passaram a usar diretamente o mesmo MutableStateFlow, sem getter privado intermediário e sem capturar valores através de suspensão. O Support usa uma única cópia do estado; a escolha do rótulo de modalidade expressa diretamente as mesmas precedências.
- Sete wrappers de callbacks sem efeito de longa duração foram retirados. Os testes verificam que os eventos recebem os callbacks atualizados após recomposição. O conflito de seleção usa Boolean, pois o valor numérico armazenado não era consumido.
- Primary e Danger compartilham a construção equivalente de Button, variando apenas as cores. Secondary e Tertiary mantêm suas implementações específicas. Foram conferidos tamanho, cores, clique, loading e disabled; a cor é medida antes do ripple do clique, com a mesma tolerância.
- Quatro scripts de cobertura usam listas locais de projetos como fonte única dos inputs e pré-requisitos. A comparação resolvida antes/depois da refatoração confirmou igualdade dos 49 conjuntos de regras, classes, fontes e execuções e das quatro listas de tarefas dependentes. Nenhum framework de configuração foi criado.
- A instrumentação de betting passou a declarar o placeholder de autenticação já usado pelas outras features; a ausência foi exposta pela execução do novo teste de callbacks.

Contratos, adaptadores necessários à direção das dependências, variantes com comportamento próprio e identificadores persistidos foram mantidos. Os módulos sem complexidade redundante comprovada não receberam alterações cosméticas. A revisão cruzada não encontrou novos achados acionáveis nas alterações.

## Validação

| Verificação | Resultado |
|---|---|
| modernCheck + releaseCheck | BUILD SUCCESSFUL em 36min11s; 3.674 tarefas, 656 executadas, 27 de cache e 2.991 atualizadas |
| Unitários da matriz | 2.099 aprovados, zero falhas/erros/skips |
| Instrumentação da matriz | 196 aprovados, três skips por perfil, zero falhas/erros |
| Instrumentação adicional | 35 aprovados: DS 21, Betting 1, filtros 8 e host isolado 5; zero falhas/erros/skips |
| Três casos de coordenadas no perfil correto | Três aprovados, zero falhas/erros/skips |
| AndroidTest do app | PRD, TQS e Automação compilados; host executado somente com applicationId isolado |
| Políticas de arquitetura/módulos | 74 testes aprovados |
| Formatação | 1.871 arquivos Kotlin, zero violações |
| Cobertura | 49 conjuntos de regras, fontes e classes preservados; quatro listas de pré-requisitos iguais |
| Integridade | 2.221 hashes de inputs iguais antes/depois da matriz e do complemento |

São 234 casos instrumentados distintos aprovados, incluindo as seis parametrizações de status de bilhete. Os três skips do relatório bruto da matriz têm resultados complementares próprios. O perfil temporário usou Android 16/API 36 AOSP, 1080×2408, 450dpi, fontScale 1,0 e barra de status 66px; as condições e tolerâncias dos testes permaneceram iguais.

Persistência mantém 526/532 linhas cobertas (98,87%); Core Data 2.531/2.662 (95,08%); Core Domain 1.763/1.823 (96,71%); Core Network 724/731 (99,04%). Nenhum limiar foi reduzido. Antes de recompilar, a exportação resolvida dos scripts de cobertura foi exatamente igual. Após os testes, somente os caminhos de coverage.ec de Retail/Tickets mudaram de nome de dispositivo; padrões, regras, fontes, classes e pré-requisitos continuam iguais.

A validação corrigiu um import inválido em teste, a ordem de medição de cor para ocorrer antes do ripple (mesma tolerância) e o placeholder ausente no manifesto AndroidTest de betting. Os logs das tentativas e os resultados finais foram preservados.

O AVD e a imagem instalados para esta tarefa foram removidos. Os quatro AVDs originais e o emulador 5560 foram preservados. Os metadados de instalação do pacote de referência permanecem iguais aos registrados antes da instrumentação isolada. Os arquivos de mapping dos releases foram armazenados temporariamente com compressão para liberar espaço e restaurados com hashes idênticos.

Não restaram achados acionáveis conhecidos no escopo revisado. A conclusão está vinculada aos cenários verificados; não representa garantia absoluta de ausência de defeitos não detectados.

Evidências desta tarefa: `build/verification/kiss-review-20261021/`. O snapshot inicial e o delta separam esta revisão das alterações preexistentes do checkout. Nenhum commit ou push foi realizado.
