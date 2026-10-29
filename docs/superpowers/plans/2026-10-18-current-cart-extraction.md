# Plano de extração do carrinho atual

> Executar inline com `superpowers:executing-plans` e concluir uma revisão independente somente de leitura.

**Objetivo:** separar coordenação, conteúdo, itens, resumo, diálogos e formatadores, preservando o ViewModel e o comportamento.

**Arquitetura:** a rota e o coordenador privado `CartContent` preservam efeitos, ciclo de vida e decisões. Scaffold, conteúdo e componentes recebem estado e callbacks. O resumo e os formatadores saem da apresentação, que conserva os layouts dos itens ativos.

**Tecnologias:** Kotlin, Compose, coroutines, JUnit, testes Android e Gradle/Jacoco.

**Especificação:** `docs/superpowers/specs/2026-10-18-current-cart-extraction-design.md`.

## Restrições

Preservar o checkout e o trabalho anterior. Não criar commits ou worktrees nem modificar ViewModels, regras, recursos, dependências ou gates. Alterar somente os dois arquivos Kotlin existentes previstos e adicionar seis arquivos de produção. Preservar ordem, chaves, owners, capturas, cores, tamanhos e contagens.

## Foco da revisão

- Janela bloqueadora por último e mesma prioridade de carregamento, falha e conteúdo.
- Relógio no mesmo ramo, com `remember(viewModel)`, efeito por `hasExpiringShare`, intervalo de um segundo e captura original.
- Consumo antes dos callbacks de login, cadastro e pagamento; ações vinculadas ao ViewModel atual.
- Resumo por `items.size`, mesmas condições de habilitação e mesmos layouts ativos.
- API, estilos, rótulos, formatação, chaves da lista e identificadores preservados.

## Etapa 1 — Referência e caracterização

- [x] Executar referência: 96 testes JVM, três gates, 59 testes Android, lint e cobertura mínima de 95,01%.
- [x] Adicionar cinco testes de formatadores e confirmar falha inicial por acesso privado.
- [x] Executar três novos testes de coordenação e 16 testes existentes contra a implementação original.

## Etapa 2 — Extração

- [x] Extrair seis arquivos e reduzir rota e apresentação conforme a especificação.
- [x] Comparar todos os corpos e hashes, preservando efeitos e layouts.
- [x] Aprovar 101 testes JVM, compilação debug/release e três gates.

## Etapa 3 — Validação e entrega

- [x] Aprovar check completo: 62 testes Android, JVM, lint, cobertura de todas as classes, compilação release, APK PRD debug, testes JVM do app e nove gates offline.
- [x] Concluir revisão independente sem apontamentos pendentes.
- [x] Finalizar arquitetura, evidências, hashes e whitespace; restaurar e encerrar o emulador dedicado.
