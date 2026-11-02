# Refatoração dos seis arquivos Compose remanescentes

> Execução: continuação autorizada pelo usuário após a revisão anterior. Aplicar ub-mode, preservar o comportamento e verificar cada unidade antes de integrar.

**Objetivo:** separar também as seis unidades anteriormente mantidas por coesão, com responsabilidades explícitas e sem fragmentação artificial.

**Arquitetura:** manter MVVM/Clean/ReduxKotlin, os contratos públicos e os proprietários de estado. Helpers de integração ficam em Route; componentes recebem dados/callbacks; a imagem original vira recurso da feature.

**Tecnologias:** Kotlin/Compose existentes, Flow, Activity Result, JDK/JVM21, AGP8.13.0. Sem dependências novas.

**Referência:** `docs/architecture/COMPOSE_ORGANIZATION.md`, seção de fechamento anterior; solicitação atual substitui a decisão de manter esses seis arquivos intactos.

## Restrições e riscos a conferir

Headers HTTP/visuais, 44 protegidos, baselines e exclusões permanecem intactos.
Não mudar Domain/Data/ViewModels/reducers, textos, IDs, layout, streams ou navegabilidade.
Não criar commit/staging. Só o integrador executa Gradle, uma invocação por vez.
Recursos binários devem manter os bytes e a escala da renderização original.

- Callback tardio do SSO deve continuar verificando estado atual e autorização pendente.
- Retorno do navegador/desconexão/troca de usuário preservam flags saveable e cancelamento.
- Recibos conservam geração, claim, recuperação, escrita retida e dismiss de falhas.
- Routes conservam remember/effect keys, back handler, ordem dos diálogos e leituras atuais.
- Arte preserva PNG, dimensão, semântica e ContentScale; diálogo preserva foco/teclado.

## Unidades verificáveis

- [x] Fixar comportamento com testes existentes antes das mudanças. Refatoração parte do GREEN; não criar testes artificiais de tamanho/nome de arquivo nem alegar RED inexistente.
- [x] Betting: `BettingFavoriteDialog.kt` → chrome/título, entrada e ações; `BettingGameRoute.kt` → efeitos/conteúdo/overlays com único owner do estado local. Validar `BettingTutorialBranchesUiTest` e testes de jogo.
- [x] Account: `LoginAuthorization.kt` → autorização/resultado de Activity, recuperação de conexão e efeitos em helpers especializados; manter flags/saveable e cancelamento em um owner. Validar `AccountRouteUiTest`, `LoginNetworkObservationTest`, `LoginActivityReturnTest` e capacidades.
- [x] Retail: `PrizeReceiptManager.kt` → plataforma PDF, diálogos de falhas e efeitos de entrega; `PrizeRedemptionRoute.kt` → ações, redirecionamento e seleção inicial. Validar recibos, canais, Pix e overlays existentes.
- [x] Onboarding: `ResponsibleGamingIcon.kt` → PNG em `drawable-nodpi`; componente apenas compõe a imagem. Validar introdução/status e hash/decodificação da imagem original.
- [x] Integração: revisar IDs/métricas, formatar somente delta, compilar, executar qualidade/arquitetura/DS/contratos, revisão independente e testes globais/cobertura >95% e APKs.
- [x] Documentar a nova organização, retirar a classificação anterior de pendente/coesão mantida, registrar resultados atuais e limites.

## Complemento verificado durante a integração

Leituras delegadas precisam conservar o momento da consulta após a extração.
Corrigidos getters nos callbacks Loteca e nos efeitos addedToCart, betEntry e
initialSelection. Cinco testes novos: quatro falharam na implementação inicial
por diferenças observáveis; todos passaram após a correção (RED19s→GREEN20s).
A revisão independente encerrou dois achados Important envolvendo esses cinco
pontos. Nenhum teste existente teve sua expectativa reduzida.

Fechamento: qualidade, arquitetura, DS, contratos e APK TQS aprovados; 3.613
testes, zero falhas/erros/ignorados e LINE global 95,31%. Foram conferidos os
26 hashes das fontes, 44 protegidos intactos e pixels do PNG no APK. O inventário
contém 660 fontes Compose, com máximo de 199 linhas por arquivo e 97 por função
identificada, acompanhado de revisão sem acúmulo transferido. Sem staging,
commit, push ou dispositivo físico; preCommitCheck completo NOT RUN por fontes
novas ainda não preparadas no Git.
