# Organização dos pacotes MVVM Implementation Plan

> **For agentic workers:** Usar superpowers:executing-plans nesta sessão. Checkout compartilhado existente, sem reset, commits, push ou novo worktree.

**Goal:** Tornar Model, Repository, UseCase e fontes de dados visíveis na árvore de pacotes das features, preservando lógica e cobertura.

**Architecture:** Domain separa model, repository, usecase, rule e port onde existam responsabilidades. Data usa repository, datasource/local, datasource/remote, mapper e di. Presentation conserva seus pacotes existentes. Core Data adota a mesma separação; capacidades pequenas de Core Domain mantêm seus agrupamentos existentes.

**Tech Stack:** Kotlin, Hilt, Gradle, Python.

**Spec:** Pedido do usuário em21/10/2026: criar pacotes para melhorar a organização visual, após padronizar RepositoryImpl/DataSourceImpl.

## Global Constraints

Preservar todos os corpos e literais, persistência, testes, limiares e classes selecionadas na cobertura. Não criar aliases, fachadas ou camadas vazias. Gradle serial e inputs congelados durante builds. Emulador5560 e pacote nativo preservados; sem instrumentação do app com ID padrão.

## Review Focus

Referências implícitas do mesmo pacote; imports wildcard e funções de extensão; contratos em módulos api; implementações privadas extraídas; filtros de cobertura baseados em pacotes.

## Tarefas

- [x] Registrar snapshots e mapear arquivos/declaracões para pacotes com diretórios correspondentes.
- [x] Mover arquivos e separar fontes locais dos arquivos de Repository, preservando corpos; atualizar imports/FQNs, consumidores, Hilt e testes.
- [x] Migrar seletores de cobertura sem perder classes ou reduzir limiares, comparando com a matriz anterior.
- [x] Executar políticas/formatação, compilação/testes e matriz modernCheck/releaseCheck; compilar AndroidTest dos três flavors; obter revisão independente.
- [x] Documentar árvore de pacotes, resultados e limites, preservando evidências anteriores.

## Decisões

A execução continua nesta sessão conforme a autorização do usuário para realizar a organização. Não se solicita aprovação intermediária para mover fontes e atualizar consumidores. Extrações locais exigem que quatro implementações privadas passem a internal, pois são usadas por outro arquivo do mesmo módulo; construtores, operações e escopos não mudam. Novos testes de comportamento não são necessários para uma reorganização sem alteração de lógica; a suíte existente e comparações estruturais cobrem a migração.

## Registro da execução

397 arquivos movidos e dez extraídos,407 destinos. A primeira compilação detectou imports incorretos porque o gerador tratava parâmetros de construtor como declarações de pacote. O gerador passou a considerar parênteses/colchetes e a transformação foi reaplicada exclusivamente sobre os snapshots próprios, preservando o baseline. A compilação corrigida e os AndroidTest dos três flavors passaram em1min43s;98 políticas passaram. A revisão independente confirmou corpos/extrações sem alterações. A comparação dos49 gates não perdeu classes; há apenas uma nova classe de metadados da typealias CartSnapshotRow. Matriz em execução sobre2.236 inputs congelados.

## Conclusão

Matriz completa aprovada em22min07s:2.103 unitários e196 aprovações instrumentadas, três skips por perfil previamente aprovados na rodada KISS. Compilação AndroidTest dos três flavors e releases com R8 aprovados.2.236 inputs inalterados;49 regras/conjuntos de cobertura preservados, sem perda de classes, e contagens de linhas iguais. Documentação e inventário atualizados. Revisão independente sem achados concretos.
