# Betting route extraction implementation plan

> Execução inline com `superpowers:executing-plans`; uma revisão independente ao final.

**Goal:** Separar apresentação/formatadores do simulador sem alterar ViewModel ou comportamento.
**Architecture:** Rota coordena e conecta callbacks; componentes consomem estado e ações; Timemania entra por slot do componente já existente. Funções textuais puras ficam em arquivo próprio.
**Tech Stack:** Kotlin, Jetpack Compose, Gradle, JUnit/Compose UI test.
**Spec:** `docs/superpowers/specs/2026-10-18-betting-extraction-design.md`.

## Global Constraints

- Referência local de1095 linhas; preservar checkout já modificado, sem commits.
- API pública, ViewModel, estados, guards, efeitos e corpos de layout permanecem iguais.
- Não mudar dependências, recursos, gates ou componentes esportivos existentes.

## Review Focus

- Voltar do complemento retorna aos números antes de oferecer descarte; loading bloqueia retorno.
- Shake mantém a lista completa de bloqueios; addedToCart continua com o mesmo efeito/chave e reconhecimento.
- Reduzir/limpar Loteca mantém o incremento de descriptionBindingKey e os guards originais.
- Callback referencia o ViewModel atual; slot de Timemania conserva sua posição/condição em manual e Surpresinha.
- Cabeçalhos especiais, aliases fallback, data sem trim, geometria inteira das grades e formatos dos valores permanecem exatos.

## Task 1 — Referência e caracterização

- [x] Salvar fonte, hashes e lista de classes Android.
- [x] Executar baseline JVM/build/gates e35 casos Android controlados.
- [x] Adicionar testes de recorrência, títulos e data; observar falha de acesso antes da extração.

## Task 2 — Extração

- [x] Criar os12 arquivos previstos na spec: ações, entrada, quatro modalidades, grade, cabeçalho, rodapé, toolbar, componentes e formatadores.
- [x] Ligar as15 ações aos métodos originais na rota e o slot de Timemania ao componente existente.
- [x] Preservar estados/efeitos/handlers; conferir corpos e assinatura por reconstrução estrutural.
- [x] Executar JVM/compilação debug/release e gates após a extração.

## Task 3 — Validação e entrega

- [x] Reexecutar os35 casos Android com a mesma configuração.
- [x] Executar betting check/cobertura/lint, release compile, app PRD build/unit e nove gates, offline.
- [x] Obter revisão independente e conferir escopo/whitespace, preservando ViewModel por hash.
- [x] Atualizar arquitetura/evidências e limpar/encerrar somente o AVD dedicado.

## Resultado e limites

Rota1095→240 linhas;12 arquivos de apresentação/ações/formatadores e cinco testes JVM novos. O ViewModel e os componentes esportivos existentes permanecem idênticos por hash. Assinatura, coordenação,15 bindings e19 grupos de declarações comparados contra a referência; revisão independente aprovada sem achados.

Betting check/cobertura/lint, release compile da feature, build PRD debug,268 testes JVM e nove gates passaram. A invocação conjunta com --continue terminou exit1 exclusivamente na suíte Android:33/35 aprovados, sem skips, com os mesmos dois casos de BettingPromptOriginTest falhando antes/depois em1080×2408/450dpi (origem do texto95esperado/93observado). Layout, assertivas e gates foram preservados; corrigir esse desvio visual não faz parte desta extração sem mudança de comportamento. A tentativa inicial480dpi e sua correção de ambiente estão registradas separadamente.

Evidências em `build/verification/betting-refactor-20261018/`, incluindo `final-attestation.json`, fontes, relatórios antes/depois e revisão. Estado original de display/pacotes do AVD dedicado restaurado e AVD encerrado. Alterações sem commit.
