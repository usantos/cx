# Limpeza de pacotes e ownership de transporte Implementation Plan

> **For agentic workers:** Executar inline com superpowers:executing-plans; checkout compartilhado existente, sem reset, staging, commits ou push.

**Goal:** Remover pacotes/módulos sem uso comprovado e corrigir os namespaces dos DTOs de transporte das features, conforme o exemplo do usuário.

**Architecture:** DTOs pertencem ao pacote feature.<feature>.data.dto nos nove módulos transport existentes. Os módulos transport contêm contratos HTTP usados por Core Network e Data, sem dependências cíclicas; Domain e Presentation não recebem DTOs. Eliminar a exceção que permite namespace Core em features. Remover somente diretórios comprovadamente vazios, sem apagar artefatos de build ou arquivos ignorados.

**Tech Stack:** Kotlin, Gson, Gradle, Python.

**Spec:** Pedido do usuário de 21/10/2026 nesta conversa e screenshot de Payment/transport com DTOs em core.network e pacotes vazios.

## Global Constraints

Preservar corpos, literais, serialização, persistência, testes e mínimos de cobertura. Atualizar imports explícitos/implícitos e referências de R8. Manter owners Android registrados. Gradle serial, sem editar inputs durante builds. Não instalar/instrumentar o app oficial nem modificar o emulador do usuário.

## Review Focus

Imports implícitos de testes/Core Network; DTOs referenciados por outros arquivos transport; nomes reflexivos/Gson/R8; seleção JaCoCo após mudança de pacote; distinguir módulo sem consumidor de módulo de testes ou contratos usado transitivamente.

## Tarefas

- [x] Inventariar módulos, consumidores, arquivos e diretórios vazios; registrar baseline e grafo.
- [x] Adicionar regressão arquitetural que rejeita DTO de feature em Core mesmo com inventário antigo (RED); mover os 18 arquivos para data.dto, dar nomes Dtos aos arquivos que agrupam DTOs e retirar inventário/exceção (GREEN).
- [x] Atualizar consumidores e imports implícitos; remover diretórios vazios; verificar equivalência de corpos/literais e grafo sem módulos órfãos.
- [x] Executar políticas, compilação, modernCheck/releaseCheck e comparação dos 49 gates sem reduzir mínimos; documentar resultados e revisão.

## Decisões

Manter os nove módulos transport e os três api: são contratos com consumidores reais, não fachadas. Nenhum módulo será removido apenas por ser pequeno. O namespace antigo de transporte não representa API pública publicada e será migrado em todos os consumidores locais, conforme autorização explícita do usuário para renomear.

## Extensão de ownership dos testes

A auditoria encontrou 41 testes de features em core.data/core.preferences/apostas; foram movidos para os pacotes das próprias features. O gate passa a verificar ownership também nos testes (RED/GREEN). Core Network pode consumir somente data classes terminadas em Dto declaradas no módulo transport e no namespace data.dto da feature; referências a implementações de Data continuam proibidas, inclusive classes que tentam usar esse pacote. 107 políticas aprovadas. Grafo estático: 65 módulos, nenhum órfão, Core Testing usado pelos testes. Removidos 485 diretórios vazios antes da segunda migração; limpeza final será registrada após os builds.

## Resultado final

Matriz modernCheck/releaseCheck aprovada em 22min49s: 3.675 tarefas (993 executadas, 23 de cache, 2.659 atualizadas), 2.105 unitários e 196 aprovações instrumentadas, sem falhas/erros. Mesmos três skips de perfil, com aprovação anterior na rodada KISS. Identidades dos 2.105 casos unitários e 199 instrumentados preservadas. 49 gates com regras/mínimos/fontes/pré-requisitos idênticos e mesmas classes após mapeamento. Os 20 relatórios mantêm as contagens de cobertura por linha. 107 políticas e formatação de 1.889 fontes Kotlin aprovadas.

59 corpos movidos e 75 consumidores equivalentes; 21.851 literais Kotlin e 2.228 hashes de inputs preservados. Manifestos/builds/dependências iguais. Releases dos três flavors com IDs/hashes conferidos. 510 diretórios vazios removidos, nenhum módulo sem consumidor. Revisão independente sem achados importantes restantes; preservação explícita R8 e imports dos helpers de Cart corrigidos na verificação final. Relatório: docs/UNUSED_PACKAGES_REVIEW.md. Nenhum commit ou push.
