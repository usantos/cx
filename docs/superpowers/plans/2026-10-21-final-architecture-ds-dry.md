# Revisão final de arquitetura, DS e DRY

> **For agentic workers:** Execute as tarefas nesta sessão, mantendo revisões independentes por área e Gradle serializado.

**Goal:** Corrigir redundâncias verificadas e retirar Legacy dos nomes de tipos e APIs do DS, preservando comportamento e cobertura.

**Architecture:** Manter proprietários de cada feature e o DS como dependência visual compartilhada. Migrar consumidores sem fachadas de compatibilidade. Centralizar apenas implementações equivalentes.

**Tech Stack:** Kotlin, Compose, Hilt, Gradle, JaCoCo, Android.

**Spec:** Pedido do usuário nesta conversa em 21/10/2026.

## Restrições

Sem redução de limiares, exclusão de testes, mudança de dados persistidos, rotas HTTP, serialização ou aparência. Preservar alterações existentes e emulador5560. Não editar inputs durante Gradle. Sem commit/push.

## Revisão

Verificar colisões dos novos nomes; seletores JaCoCo após renomeação; IDs de recursos/contratos de dados imutáveis; variantes visuais realmente distintas; callbacks e precedência loading/error/conteúdo.

## Tarefas

- [x] Registrar snapshot de fontes e seletores/limiares de cobertura antes da edição.
- [x] Renomear tipos Legacy em todos os módulos, arquivos Kotlin associados e APIs do DS; atualizar consumidores, inventários e seletores mantendo os mesmos alvos.
- [x] Centralizar tokens de cores equivalentes, estado visual compartilhado e seletores repetidos; remover limpeza de navegação e recursos comprovadamente redundantes.
- [x] Acrescentar verificação para impedir nomes Legacy em tipos de módulos; executar políticas e compilação/testes direcionados.
- [x] Executar modernCheck e releaseCheck completos; corrigir falhas com diagnóstico, comparar cobertura e revisar o patch final.
- [x] Registrar evidências e limites da validação; concluir apenas após aprovação dos gates.
