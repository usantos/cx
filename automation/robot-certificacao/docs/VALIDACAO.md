# Verificações desta entrega - 29/10/2026

- Dependências instaladas em ambiente virtual isolado Python 3.12; `pip check` sem incompatibilidades.
- 28 testes Python de configuração, sessão, contratos e relatório aprovados; 6 testes negativos/positivos do gate Android de IDs aprovados.
- Três casos Robot aprovados em **dry-run**, incluindo resolução de Resources/Pages/keywords e Gherkin pt-BR. Não houve execução física/farm desses casos.
- Relatório PDF gerado a partir de output.xml de dry-run: status preservados e identificação explícita de validação offline.
- 2.118 testes Android/JVM de apresentação das 14 features e CaixaDS aprovados, sem falhas ou testes ignorados. O teste Compose confirma a propriedade de exportação dos IDs ao UiAutomator.
- Detekt e Android Lint PRD Debug aprovados. APK TQS Debug compilado com IDs. Gates `verifyAutomationIds` e `verifyCleanArchitecture` aprovados. Testes Compose verificam preservação de IDs anteriores e labels de acessibilidade.
- Instalação para validação local Appium impedida por `INSTALL_FAILED_INSUFFICIENT_STORAGE` no emulador Android 36, mesmo após limpeza de caches. Nenhum aplicativo/dado foi removido para liberar espaço. O Samsung não foi alterado.
- Conexão/autenticação Keeggo e integração final RQM aguardam configuração e execução pela equipe responsável. Não foram consideradas aprovadas pelo dry-run.

As evidências e logs completos da execução de desenvolvimento ficam no checkout Android em `build/verification/robot-ids-20261029/`. Não são distribuídos como resultados de certificação. Headers e baselines protegidos foram comparados com os hashes anteriores, sem alterações.

Os detalhes verificáveis estão em `validation.json`. O pre-commit completo (incluindo Ktlint global e JaCoCo) não foi reexecutado nesta entrega; os resultados acima identificam as verificações efetivamente realizadas.
