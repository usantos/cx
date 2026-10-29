# Cobertura acima de 95% — plano de implementação

> Execução: acrescentar testes de comportamento e validar o agregado oficial; tarefas independentes podem ser delegadas conforme a skill dispatching-parallel-agents.

**Objetivo:** elevar a cobertura LINE de 94,65% para mais de 95%.

**Abordagem:** testar código existente com Robolectric e repositórios falsos nas fronteiras externas. Preservar integralmente o código de produção e os critérios de medição.

**Restrições:** nenhum header, baseline, exclusão, limite ou entrada do JaCoCo será alterado. Não fazer commit ou publicar. Manter comentários novos em português.

## Tarefas

- [x] Central de notificações: criar testes JVM para estados vazio, anônimo, carregando, erro, abas, ações por ID, cancelamento e confirmação dos três diálogos; usar o ViewModel real na rota e fake somente no repositório.
- [x] Remote Config de atualização obrigatória: testar opções/aplicativo nomeado, configuração, fetch/activate, falha offline, atualização em tempo real, erro e remoção do listener usando shadows do SDK; preservar a implementação.
- [x] Temporizador de bolão: testar renderização com fonte normal/ampliada e atualização do relógio com tempo controlado, incluindo expiração; preservar o componente.
- [x] Rodar as suítes dos módulos alterados sequencialmente, corrigindo somente problemas dos testes novos. Revisar as asserções e medir cobertura sem alterar configuração.
- [x] Rodar `verifyCoverage aggregateTestReport -PnetworkLogging=false --continue` completo; conferir testes, soma dos módulos, cobertura estritamente maior que 95%, hashes protegidos e disponibilizar HTML.

## Foco da revisão

- Confirmações canceladas não alteram dados; confirmação aceita usa IDs reais.
- A conta anônima não mostra dados privados.
- Falhas de carregamento permitem tentar novamente.
- Atualização não relacionada e ativação malsucedida não notificam mudança de force_update.
- Relógio/fontes não dependem de esperas reais e são restaurados após os testes.

## Evidência inicial

3.410 testes aprovados; 53.541/56.569 linhas (94,65%). São necessárias pelo menos 200 linhas adicionais. Relatório anterior e hashes preservados em `build/verification/coverage-over95-20261028/`.

## Progresso

21 testes novos aprovados nas execuções por módulo: 9 de notificações, 9 de Remote Config e 3 de temporizador. Ktlint dos quatro arquivos: aprovado. Revisão independente de notificações/temporizador e revisão manual dos shadows do SDK sem pendências no escopo. Headers, baselines e código de produção preservados por hash. Medição preliminar com entradas oficiais: +241 linhas cobertas, 95,073273%, denominador 56.569 mantido. Validação agregada completa em andamento (`full.log`).

O cenário de recuperação após erro valida retorno ao primeiro plano; o callback do botão Tentar novamente é validado no teste de tela. Não afirma que o botão fica disponível após reconhecer o diálogo.

## Resultado final

`verifyCoverage aggregateTestReport -PnetworkLogging=false --continue`: BUILD SUCCESSFUL (11m15s). 3.431 testes, nenhuma falha/erro/ignorado. Cobertura LINE 53.782/56.569 = 95,07% (COVERAGE PASSED), ganho de 241 linhas. Soma dos 68 módulos conferida. SHA-256 dos headers/baselines, 3.582 arquivos de produção/configuração e entradas do JaCoCo preservados. Evidências e resumo em `build/verification/coverage-over95-20261028/summary-final.json`. Nenhum commit.
