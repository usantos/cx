# ADR-001 — Corte modular integral para Compose

## Status

Aceita e aplicada na branch `refatoracao`.

## Contexto

O aplicativo original era um monólito Java com Activities, Fragments, XML, Volley e regras acopladas às telas. A migração precisava preservar o `applicationId`, os serviços existentes e integrações homologadas sem manter controllers legados no grafo executável.

## Decisão

- manter o composition root diretamente em `app/` e retirar o diretório histórico do projeto;
- manter uma única `ComponentActivity` como host;
- usar Compose e Navigation Compose em todas as telas do aplicativo;
- separar cada domínio funcional em módulo próprio;
- aplicar dependências de fora para dentro (`app -> feature/data -> domain`);
- encapsular AppAuth e FilaBRA em adapters;
- rejeitar layouts XML e controllers de feature por uma tarefa Gradle;
- exigir cobertura mínima de 95% para a lógica moderna.

## Consequências

O código histórico continua disponível somente para auditoria e comparação, sem ser compilado. Bibliotecas externas podem conter recursos XML e componentes Android internos, mas não alteram a regra de uma única Activity escrita e declarada pelo aplicativo. Novas features devem fornecer modelo de domínio, porta de repository, adapter de dados, estado/ViewModel e UI Compose no módulo correto.
