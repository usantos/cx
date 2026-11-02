# Refatoração de Compose — plano de implementação

> **Execução:** Método de trabalho: Use execução por etapas verificáveis para execução contínua e uma revisão independente ao final. Grupos de features independentes podem ser executados conforme revisões independentes de escopos separados; Gradle e integrações permanecem centralizados.

**Objetivo:** reduzir acúmulo de responsabilidades de Compose em todas as áreas do app.
**Arquitetura:** Route/Screen/seções da feature/DS; MVVM, Clean e Redux preservados.
**Tecnologias:** Kotlin/Compose atuais; JDK21, AGP8.13.0; nenhuma atualização.
**Especificação:** docs/architecture/COMPOSE_ORGANIZATION.md.

## Restrições globais

Preservar visual, callbacks, IDs, semântica, chaves/escopos de efeitos e estado.
Não alterar arquivos protegidos, baselines, regras de cobertura, negócio ou
transportes. Manter Meus Cartões. Sem staging/commit/push automático. Uma
invocação Gradle por vez. Separações usam tipos e APIs existentes.

## Execução

- [x] Inventariar todos os Composables por módulo e registrar candidatos e justificativas.
- [x] Betting/Cart: separar RapidListRoute da composição, revisar demais Routes e itens de carrinho/favoritos; testes de Rapidao, seleção, swipe e carrinho.
- [x] Tickets/Results: separar listas, seleção, resultado e detalhes por responsabilidade; testes de seleção, conferência, comprovante e recuperação.
- [x] Home/Marketplace/Filter: separar resultado, plataforma/overlays e apresentação do carrossel; testes de resultado, drawer, paginação, quantidade e favoritos.
- [x] Account/Payment: separar formulário, seleção e etapas usando DS existente; testes de formulário, cartões, CVV e callbacks.
- [x] Demais features/App/Core: revisar composição/lifecycle e dividir apenas acúmulos comprovados; preservar arquivos simples.
- [x] Reconciliar inventário de IDs somente quando o arquivo proprietário mudar; preservar IDs, símbolos e escopos.
- [x] Revisão independente do delta desta tarefa; corrigir problemas materiais antes da validação final.
- [x] Executar testes pertinentes, qualidade/contratos globais, cobertura e builds. Atualizar matriz da auditoria e documentação com resultados reais e limites.

## Foco de revisão

Gestures/swipe e semântica continuam no mesmo contêiner; callbacks não duplicam
escrita/navegação; efeitos preservam chaves e dono de lifecycle; branches de erro,
carregamento e estado vazio permanecem alcançáveis; fontes ampliadas e estado de
entrada/scroll sobrevivem como antes. Testes existentes devem continuar exercitando
esses contratos, sem asserts sobre nomes de arquivo ou tamanho de funções.

## Encerramento

Execução concluída sem commit/stage/push. Ver matriz e evidências em
`docs/architecture/COMPOSE_ORGANIZATION.md`. 170 regressões focadas e3.608testes
globais aprovados; cobertura95,27%; qualidade/arquitetura/IDs/Compose e builds
TQS/catálogo aprovados. Headers/baselines e donos de estado preservados.

## Continuação autorizada — fechamento dos acúmulos restantes

A solicitação seguinte exige seguir até não restar acúmulo sem justificativa em
fontes Compose. A referência desta etapa é a árvore após a primeira rodada,
validada por3.608testes e95,27%, não HEAD. As decisões antigas de manutenção
serão reavaliadas; os três candidatos citados na conversa são obrigatórios.

- [x] Refatorar BettingGameRoute, quantidade Rapidao, Loteca e os demais acúmulos
      de composição/toolbar/rodapé/tutoriais de Betting.
- [x] Fechar misturas de responsabilidades em Cart/Tickets/Results, preservando
      listas, medição, filtros e polling/efeitos.
- [x] Fechar Home/Marketplace/Notifications/Support, com seções de drawer,
      cards/listas e diálogos, sem novo tema ou comportamento.
- [x] Fechar DS e restantes App/Core/Account/Payment/Onboarding/RG/Retail.
      Separar integração de composição visual onde necessário; manter coesos
      modelos, desenho vetorial, overloads de uma API e owners de efeitos.
- [x] Repetir inventário de arquivos e funções. Cada fonte sinalizada por
      tamanho, aninhamento ou responsabilidades tem decisão registrada; nenhum
      candidato com acúmulo confirmado fica pendente. Não impor limite numérico
      artificial nem fragmentar unidades claras em wrappers.
- [x] Revalidar IDs, métricas, ordem dos nós, chaves de efeitos e callbacks;
      manter headers/baselines/negócio/Redux e Meus Cartões.
- [x] Revisão independente e testes pertinentes/globais, qualidade, cobertura
      >95%, arquitetura e APKs; documentação atualizada e limitações explícitas.

Sem stage, commit ou push automático; uma invocação Gradle por vez. Nenhuma
supressão, baseline, exclusão ou política pode mascarar os apontamentos.

Fechamento em 02/11/2026: inventário 509→649 Compose, delta de 227 fontes Kotlin,
seis sinais restantes justificados por coesão. Rodada final em 10min59s: 3.608
testes sem falhas/erros/ignorados, LINE 95,29%, qualidade/arquitetura/DS/IDs/
contratos e APKs aprovados. 44 protegidos e fontes revisadas conferidos por hash.
Detalhes e limitações em `docs/architecture/COMPOSE_ORGANIZATION.md`.
