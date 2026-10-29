# Outubro Rosa completo e revisão de Filtros

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans; independent audit/feature tasks use dispatching-parallel-agents.

**Goal:** Aplicar os efeitos legados de Outubro Rosa em todos os destinos afetados, centralizados no DS, remover a faixa inventada da Home e igualar header/status bar.

**Architecture:** Configuração observável no repositório compartilhado alimenta um único provider no host. Resolvedores DS semânticos e recursos legados determinam modalidade e contexto; features apenas compõem esses papéis. Flags geral e Mega-Sena seguem separadas.

**Tech Stack:** Kotlin, Compose, Hilt, Flow, Gradle e testes Android.

**Spec:** Solicitação de 15/10/2026 e `docs/OCTOBER_ROSE_LEGACY_SCOPE_20261015.md`; revisão da captura de Filtros fornecida pelo usuário.

## Global Constraints

- Preservar alterações em andamento; não criar commit, push ou apagar trabalho anterior.
- Não introduzir XML de layout, flags de campanha em telas nem cores de campanha fora do DS.
- Sem faixa rosa na Home. Status/header usam exatamente a mesma cor DS.
- Somente Mega-Sena recebe o estilo de modalidade; a flag geral não colore outras modalidades.
- Não reduzir assertions, limiares de cobertura ou exclusões dos gates.

## Review Focus

- Campanha ativa, desativada e troca durante sessão/navegação.
- Paleta clara/escura, texto em fundo branco e dezenas selecionadas.
- Sorteios especiais mantêm as artes que o legado não altera.
- Filtros: opções45dp, chips29dp, expansão, remoção e envio dos mesmos valores.
- Rotas diretas e conteúdo fora da Home recebem o mesmo estado observado.

## Execução

- [x] Auditar chamadas diretas e os64 consumidores do estilo legado; registrar matriz.
- [x] Publicar estado observável e fornecer tema em todo o host.
- [x] Centralizar papéis de cores/desenhos, assets e testes do DS.
- [x] Integrar todos os consumidores Compose e exceções por contexto.
- [x] Corrigir e verificar divergências comprovadas da tela de Filtros.
- [x] Remover faixa e unificar header/status; verificar Home e Drawer.
- [x] Revisão independente, validação integrada modernCheck/releaseCheck e matriz de UI afetada.
- [x] Atualizar relatório com evidências e limitações reais.

Ruling: executar na árvore em andamento, pois ela contém a migração e solicitações anteriores do usuário. A nova auditoria substitui a limitação anterior de campanhas apenas na Home/Drawer.

Revisão: corrigidos trevos com papel de fundo, aliases1/5 e fallback desconhecido, trevo não selecionado de Filtros e ação de laço na Home V2. O primeiro run integrado foi interrompido para incluir esses achados antes do run final; nenhuma falha/limiar foi excluído.

Fechamento: execução integrada aprovada; 1.691 execuções JVM, 140 casos Android e smoke final 80/80. A correção posterior de dois CTAs da Home V2 foi revalidada nos nove testes do host, matriz dos três flavors e Releases. O diagnóstico de ANR e as limitações de homologação permanecem explícitos em `docs/OCTOBER_ROSE_COMPOSE_VALIDATION_20261015.md`; não se declara paridade pixel a pixel.
