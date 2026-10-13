# Alertas de negócio da compra — 07/09/2026

## Referência

Foram confrontados `RedirectNetwork.treatNegotialErros`, `trataErro500`, `montarMensgem`, `MensagensNetwork`, `DateUtils.getDateTime` e `MercadoPagoUtil` no checkout legado externo `simlo-app-android-legacy-app-backup-20260903`.

Os textos abaixo foram conferidos nos recursos compilados do APK legado `build/outputs/apk/prd/release/app-prd-release.apk`, usando `aapt2 dump resources`:

- `nao_concluiu_operacao`: Não foi possível concluir esta operação. Por favor, tente novamente.
- `erro_req`: Opa, ocorreu um erro na sua requisição. Por favor, tente novamente dentro de alguns instantes.
- `mercado_pago_digito_verificador_invalido`: O CPF vinculado ao e-mail informado deve ser o mesmo CPF cadastrado nas Loterias CAIXA.

Essa conferência comprova os recursos de texto, não a igualdade visual do diálogo moderno.

## Decisões restauradas

| Resposta HTTP com metadados utilizáveis e sem redirect | Alerta e ação |
|---|---|
| 400/404 com `011010`, `011011`, `011012` | Mensagem recebida; Apostar mais → Home; Entendi dispensa. Sem código/data no texto |
| Demais 400/404, exceto `018003` | Mensagem recebida + código quando presente + data/hora local; Entendi |
| 500 com `2067` | Mensagem específica de CPF do Mercado Pago, sem código/data; Entendi |
| Outros 500 com `tipo == "1"` | Mensagem recebida + código/data; Entendi |
| Demais 500 | `erro_req` + código/data; a mensagem técnica recebida não é exibida |

Redirect continua tendo prioridade. Um código de negócio acompanhado de redirect desconhecido não permite cair no alerta nem navegar para uma URL. `018003` em 400/404 permanece separado para o cadastro externo ainda pendente; em 500, prevalece o tratamento específico de 500 como no legado. Status fora desses ramos não recebem um alerta inventado.

Mensagem vazia de erro 400/404 usa `nao_concluiu_operacao`. Em 500 tipo 1, a regra original distingue mensagem `null` de mensagem vazia: somente `null` usa `erro_req`. Código `null` não acrescenta linha de código; código vazio preserva a linha vazia. Os testes cobrem essas diferenças literais.

## Domínio, MVVM e DI

`PurchaseNotice` e `PurchaseNoticeReason` representam o motivo de negócio sem dependência de Compose/Retrofit. O repository classifica os metadados HTTP; `CardSubmissionOutcome.NoticeRequired` e `PurchaseProgress.notice` transportam a decisão. Um alerta não é aprovação nem situação negativa de Pix, mesmo se houver status terminal conflitante no modelo. O protocolo de polling exige tratar o alerta antes de avaliar término.

`PurchaseNoticeRules` transforma a decisão em `PurchaseNoticeUiState`, com texto e disponibilidade da ação Apostar mais. A data/hora segue `dd/MM/yyyy HH:mm:ss`, com relógio local injetado por Hilt no host, separado do relógio monotônico do servidor usado nas reservas. Ela é capturada quando o aviso chega, não a cada recomposição. Os testes usam instante e fuso fixos.

Os ViewModels bloqueiam submissão/consulta/geração enquanto o alerta está aberto. A ação consome a instância do alerta uma única vez; callback antigo não pode dispensar outro aviso de conteúdo idêntico. A navegação de Apostar mais pausa as ações durante a transição.

## Compose e retorno

`PurchaseNoticeDialog` reutiliza `CaixaDSLegacyDialog`: título Atenção, botão Entendi e, nos três códigos correspondentes, botão positivo Apostar mais e negativo Entendi. O host usa navegação à Home com `popUpTo("home")` não inclusivo e `launchSingleTop`, equivalente à intenção de limpar as telas acima da Home quando ela existe na pilha.

- No cartão, o diálogo aparece sobre a confirmação. Entendi não reenvia o POST nem grava uma compra concluída. Após erro conhecido do POST, uma nova submissão depende de ação explícita do usuário.
- Durante acompanhamento já iniciado, o alerta interrompe o polling sem concluir a compra. Após dispensá-lo, a recuperação oferece apenas nova consulta do ID preservado/atualizado, sem novo POST e com o valor original para histórico.
- No Pix, o alerta interrompe novas consultas e impede regenerar cobrança enquanto aberto. Dispensar não gera outra cobrança nem registra aprovação. A consulta existente pode ser feita novamente após dispensar ou retomar a navegação.

A recuperação de acompanhamento não é descrita como reprodução literal de todos os estados intermediários do legado: preservar a compra existente evita reenviar cartão após falha de consulta. A proteção é do fluxo/VM vivo, não idempotência global; respostas HTTP 500 e tentativas manuais ainda exigem homologação e reconciliação persistente. Corpos ausentes/inválidos continuam no caminho de resposta inconclusiva, não no de erro conhecido dispensável.

## Cenários reproduzíveis

Os cenários `PURCHASE_MORE_BETS_REQUIRED`, `PURCHASE_BUSINESS_RULE`, `PURCHASE_SERVER_MESSAGE`, `PURCHASE_SERVER_FAILURE` e `PURCHASE_INVALID_IDENTIFICATION` iniciam na confirmação de cartão e alteram apenas registro/consulta. Preparação, tokenização e destinos permanecem disponíveis. As mensagens de backend dos mocks são explicitamente simuladas, não textos atribuídos ao backend real.

Os testes exercitam a cadeia Retrofit/interceptor/repository/caso de uso para os dois processadores, cartão novo/salvo e consulta, sem sockets. A geração de screenshots e interação no APK atual continua pendente.

## Evidência executada

Saídas em `build/verification/purchase-notice-parity-20260907/`:

- 375 testes JVM aprovados: 304 de estados/domínio/rotas/regras, 23 de rede, 41 de data, 4 de preferências e 3 de navegação mock do host.
- Compilação isolada concluída dos fontes alterados, integração do host e telas Compose; inclui o novo relógio no módulo de DI. Permanece a depreciação já existente de `LocalClipboardManager`.
- Lógica de pagamento: 403/408 linhas (98,77%), incluindo classes internas de corrotinas e as novas regras/estado de alerta. `PurchaseNoticeRules`: 14/14 (100%).
- Modelos/protocolo selecionados de pagamento: 57/57 (100%); repository/data source: 228/231 (98,70%); transporte dedicado: 51/51 (100%).
- Relatórios: `feature-payment-coverage.xml`, `core-domain-payment-coverage.xml`, `payment-repository-coverage.xml` e `purchase-transport-coverage.xml`.

Os testes verificam recursos literais, data/fuso fixos, código ausente/vazio, mensagens vazias/nulas, prioridade de redirects e códigos, consumo único, callbacks obsoletos, bloqueio durante diálogo/navegação, ausência de gravação no erro e continuidade da consulta sem reenvio. Não são cobertura global nem testes de renderização/acessibilidade.

`./gradlew :core:domain:test :core:data:test :feature:payment:testDebugUnitTest --offline` falhou antes das tarefas por `Operation not permitted` no lock `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`. Nenhuma restrição foi contornada. A compilação isolada com dependências em cache não executa geração Hilt/KSP, APK, R8 ou testes Android; não foi produzido APK/captura pareada atual nesta revisão.

## Ainda não concluído

Revisão posterior: o ramo `018003` do pagamento recebeu consentimento, consulta de token e coordenador de abertura externa em `MERCADO_PAGO_REGISTRATION_AUDIT.md`. A pendência abaixo registra o estado desta auditoria original; resgate e homologação Android do novo fluxo continuam pendentes.

- Cadastro externo Mercado Pago do código `018003`, com consentimento, tokenização e abertura segura do navegador.
- Retornos completos de autenticação/termos/cadastro/indisponibilidade, pré-check de rede, cancelamento e morte do processo.
- Homologação de polling/manual Pix, todos os alertas e combinações de respostas, textos longos, acessibilidade e screenshots pareados.
- Gradle/Hilt/KSP/R8/APKs de todos os flavors/Release, testes Android/TLS, matriz completa e cobertura global superior a 95%.
