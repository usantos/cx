# Auditoria de módulos sem uso — 21/10/2026

O grafo configurado pelo Gradle, incluindo dependências de variantes, testes e plugins de convenção, não contém módulos declarados sem uso. Dos 65 módulos registrados, 64 são alcançados pelo app em produção; `:core:testing` é consumido por 22 projetos em configurações de teste. O build incluído `build-logic` fornece os plugins de convenção utilizados pelo projeto. Nenhuma entrada foi removida de `settings.gradle` nesta rodada.

Foram removidas somente as pastas residuais `core/database`, `core/datastore` e `core/preferences`, de módulos já retirados anteriormente. Antes da exclusão, foi confirmado que todos os arquivos estavam em `build/`, sem fontes ou configurações remanescentes. Foram eliminados 2.140 arquivos gerados (27.382.613 bytes). Os inventários e XML históricos de testes/cobertura encontrados foram arquivados nas evidências.

Após a limpeza, os 69 testes das políticas de arquitetura/módulos passaram, assim como `verifyCleanArchitecture`, `verifyFeatureModules`, `verifyComposeMigration` e `verifyKotlinFormatting` (Gradle: 45s, 76 tarefas). Os 2.239 hashes de fontes/configurações e as 23 configurações de cobertura da matriz anterior permanecem idênticos. Nenhum comportamento, teste ou limiar de cobertura foi alterado. A matriz completa `modernCheck releaseCheck` não foi repetida nesta rodada, pois foram excluídos apenas resíduos gerados fora do build ativo.

Evidências: `build/verification/unused-modules-20261021/`, incluindo `manifest.json`, `configured-graph.json`, script temporário de auditoria, inventários e logs de validação.

## Validação completa posterior

A matriz `modernCheck releaseCheck` foi executada após esta auditoria e passou em 10min39s. Relatórios: 2.089 testes unitários aprovados e 196 casos instrumentados aprovados, com três skips conhecidos por perfil de tela e zero falhas/erros. Releases PRD/TQS/Automação e cobertura aprovados, sem alteração de limiares ou fontes. Detalhes: seção de 21/10/2026 em `MIGRATION_VALIDATION.md`; evidências em `build/verification/unused-modules-full-matrix-20261021/`.
