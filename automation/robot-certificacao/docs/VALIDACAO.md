# Verificações desta entrega - 29/10/2026

- Dependências instaladas em ambiente virtual isolado Python 3.12; `pip check` sem incompatibilidades.
- 28 testes Python de configuração, sessão, contratos e relatório aprovados; 6 testes negativos/positivos do gate Android de IDs aprovados.
- Três casos Robot aprovados em **dry-run**, incluindo resolução de Resources/Pages/keywords e Gherkin pt-BR. Não houve execução física/farm desses casos.
- Relatório PDF gerado a partir de output.xml de dry-run: status preservados e identificação explícita de validação offline.
- 2.118 testes Android/JVM de apresentação das 14 features e LoteriasCaixaDS aprovados, sem falhas ou testes ignorados. O teste Compose confirma a propriedade de exportação dos IDs ao UiAutomator.
- Detekt e Android Lint PRD Debug aprovados. APK TQS Debug compilado com IDs. Gates `verifyAutomationIds` e `verifyCleanArchitecture` aprovados. Testes Compose verificam preservação de IDs anteriores e labels de acessibilidade.
- Instalação para validação local Appium impedida por `INSTALL_FAILED_INSUFFICIENT_STORAGE` no emulador Android 36, mesmo após limpeza de caches. Nenhum aplicativo/dado foi removido para liberar espaço. O Samsung não foi alterado.
- Conexão/autenticação Keeggo e integração final RQM aguardam configuração e execução pela equipe responsável. Não foram consideradas aprovadas pelo dry-run.

As evidências e logs completos da execução de desenvolvimento ficam no checkout Android em `build/verification/robot-ids-20261029/`. Não são distribuídos como resultados de certificação. Headers e baselines protegidos foram comparados com os hashes anteriores, sem alterações.

Os detalhes verificáveis estão em `validation.json`. O pre-commit completo (incluindo Ktlint global e JaCoCo) não foi reexecutado nesta entrega; os resultados acima identificam as verificações efetivamente realizadas.


## Atualização do contrato após extração das UIs — 02/11/2026

Foram preservados os 1.780 registros de IDs existentes e as 88 declarações ou
expressões legadas do inventário recebido. Caminhos e linhas agora identificam
os renderizadores físicos nos splits e os componentes compartilhados da base.
O único ID adicionado foi `simlo.modules.0001`, totalizando 1.781 declarações
estáticas e 1.780 valores distintos. O ID de ação compartilhada do Design System
permanece inalterado.

A descoberta passou a conferir também `core/presentation`. Três regressões
falharam antes dessa adaptação; os 17 testes do gate Android de IDs passaram após
a mudança. `scripts/check-automation-ids.py` passou com o inventário atualizado.
No ambiente virtual externo já existente, todas as versões coincidiram com
`requirements.lock.txt`; `Scripts/validar_entrega.py` passou com `pip check`,
28 testes Python e três casos Robot em dry-run. O Python global sem Robot não
foi usado como evidência de aprovação.

Esta atualização valida o contrato estático e o pacote offline. Não comprova a
instalação dos splits, a presença dos IDs na árvore nativa, a entrega Google Play
ou execução no Samsung/farm. A validação integrada Android desta expansão tem
registro separado e não substitui as evidências históricas acima.
