# Bolão e Filtros — comparação pareada

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:executing-plans. Implementação nesta sessão.

**Goal:** Comparar Bolão e Filtros com o renderer nativo do CX no mesmo AVD/dados, corrigir diferenças comprovadas no DS e validar a árvore integrada.

**Architecture:** Um runner de instrumentação somente de teste inicia as Activities internas do APK CX intacto, preparando sessão/parâmetros e respostas controladas. O Compose recebe os mesmos dados. PNGs e hierarquias novas registram estados inicial, selecionado, expandido/rolado e campanha. Cores, fontes, medidas e recursos compartilhados corrigidos pertencem ao CaixaDS; regras de filtro permanecem na feature.

**Tech Stack:** Android Instrumentation, XML nativo como oráculo, Compose, Kotlin/Java, Gradle, PNG/SSIM e medição de bounds.

**Spec:** Solicitação de comparação pareada de 15/10/2026; fontes em `/Users/ubiracysantos/Projects/cx/app/src/main`; auditoria anterior `docs/OCTOBER_ROSE_COMPOSE_VALIDATION_20261015.md`.

## Global Constraints

- Preservar a árvore em andamento e as alterações do CX; sem commit/push/reset.
- Não alterar layouts, drawables ou controllers do oráculo legado.
- Usar somente o AVD dedicado 5558, mantendo 1280×2856, 480dpi e fontScale 1 nos dois lados.
- Dados sintéticos equivalentes; nenhuma reserva, pagamento ou mutação de conta real.
- Preservar status bar azul, logo e alterações de produto já solicitadas.
- Não reduzir limiares, expectativas ou excluir testes para aprovar gates.

## Review Focus

- A referência deve ser a tela solicitada e conter os mesmos dados, sem erro de rede/ANR disfarçado de comparação.
- Bolão: carrossel, card comum, quantidade, cabeçalho/prêmio e filtros/favoritas.
- Filtros: seleção, moeda, limites de cotas, dezenas/números e aplicação/limpeza.
- Campanha ligada/desligada, fundo claro/escuro e modalidade não Mega.
- Insets, rolagem, fonte e bounds devem ser equivalentes; SSIM isolado não certifica paridade.

## Execução

- [x] Preparar runner de teste CX intacto e dados compartilhados; capturar os pares antes de editar produção.
- [x] Registrar falhas reproduzíveis (medidas/pixels/comportamento) em testes.
- [x] Corrigir os padrões no DS e integrar os consumidores de Bolão/Filtros; verificar testes e recapturar.
- [x] Executar testes Android de ambos módulos e validação integrada `modernCheck releaseCheck`.
- [x] Publicar pares, métricas, resultados e limites; restaurar APK normal e encerrar o AVD dedicado.

A execução permanece no checkout atual porque ele contém a migração autorizada em andamento. A preparação do oráculo fica fora do código de produção, em ferramentas/artefatos de teste. Não há etapa de aprovação adicional: o usuário já pediu a execução e as correções.
