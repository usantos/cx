# Conteúdo da sessão e aviso de pagamento na Home

Registro histórico desta etapa: o popup descrito abaixo como pendente foi integrado depois,
com escopo e limites documentados em `HOME_NOTIFICATION_POPUP_AUDIT.md`.

## O que foi conectado

O retorno válido de cadastro agora preserva `carrinho`, `ultimaNotificacao` e
`pagamentoNaoIdentificado`. `RemotePostLoginAccountRepository` converte o carrinho agrupado
pelo mapeador já utilizado no fluxo de carrinho e publica os três valores juntos por uma
porta de domínio. Redirects não publicam conteúdo de Home; erro ou payload inválido não
substituem a sessão por dados supostamente válidos.

`SessionTokenStore` implementa a porta `AuthenticatedSessionContentRepository` na mesma instância
Hilt que mantém token/identidade. Seu estado imutável usa `MutableStateFlow` com compare-and-set:
publicação e verificação da referência ativa acontecem no mesmo CAS. Um callback antigo não
consegue publicar na sessão seguinte, mesmo copiando o hash do proprietário.

Logout, substituição direta de token e conclusão de outro login removem o conteúdo. Uma tentativa
de autorização em andamento o oculta; cancelamento pode recuperar o conteúdo da sessão anterior
ainda válida, mas não ressuscita sua referência antiga. O ID token de EndSession continua sendo
extraído atomicamente ao limpar o estado. Nenhum token retornado pelo endpoint de cadastro
substitui as credenciais OIDC verificadas.

A Home observa o conteúdo por caso de uso de domínio: restaura total/quantidade do carrinho e
os metadados do aviso. Uma mudança de sessão invalida consultas antigas do resumo do carrinho;
sucesso/falha tardios não repõem o cabeçalho da conta anterior depois dessa mudança observada.
A tela do carrinho continua consultando seu endpoint, sem fallback silencioso para um cache antigo.

## Comportamento confrontado com o legado

Fontes locais conferidas:

- `controllers/TokenActivity.java:146`: aplica carrinho, última notificação e pagamento não identificado.
- `controllers/PrincipalActivity.java:547`: primeiro verifica pagamento não identificado; somente
  no `else` considera a última notificação.
- `utils/DialogUtils.java:556`: diálogo institucional com título padrão “Atenção” e botão personalizado.
- `res/values/strings.xml:1562`: mensagem e rótulo literais do aviso.
- `utils/NotificacaoUtils.java`: `imagem` da notificação é base64, não URL de imagem remota.

`HomePaymentNotice` usa o diálogo existente do CaixaDS, com título “Atenção”, mensagem literal e
botão **“Acompanhe suas compras”**. Após as modalidades carregarem, o aviso aparece antes do
alerta de entrada em aposta, sem sobreposição com loading de preparação. O clique dispensa a
instância do aviso e navega para `tickets/purchases`, sem repetir o cadastro ou acionar pagamento.
Fora do botão, Voltar/toque externo não abrem compras.

O reconhecimento não altera o valor remoto `pagamentoNaoIdentificado`: a notificação simultânea
continua com prioridade inferior. Novos dados de sessão reinicializam o reconhecimento. Logout
remove os metadados observados pela Home.

**A última notificação está preservada, mas seu popup ainda não está integrado.** Faltam o
layout `nova_popup_notificacao`, decodificação/apresentação de base64, link por ação explícita,
fechamento e PUT de leitura com confirmação do backend. Não considerar a presença do DTO/estado
como implementação dessas ações. Datas de visualização/exclusão não são consumidas nesta entrega.

## Mock e testes

O cenário `post_login_payment_not_identified` retorna simultaneamente carrinho, flag de pagamento
e última notificação pelo POST de cadastro. O cenário não fabrica sessão OIDC autenticada: a
publicação na Home exige percorrer o fluxo autenticado ou usar a fixture do teste de UI.

Os testes novos cobrem publicação por identidade, ocultação/cancelamento, outro proprietário,
logout concorrente, observação de limpeza, DTOs, publicação do snapshot, prioridade/reconhecimento
do aviso, resposta antiga de carrinho e o mock. `HomePaymentNoticeTest` verifica texto e clique
em Compose; sua execução no Android ainda é obrigatória.

Execução isolada em `build/verification/post-login-session-content-20260907/`.

- **621 testes JVM aprovados**: 418 de lógica/estado, 63 de network, 108 de data,
  29 de preferences e três de navegação mock. Esta revisão adicionou 12 testes JVM.
- Escopos selecionados: domínio de conta 51/51 linhas; estado de auth/parser 144/144
  (46/46 no store); repositories/mapeador de conta 103/103; DTOs 29/29;
  HomeViewModel/UiState **153/156 (98,08%)**. Não são cobertura global do aplicativo.
- Compilação isolada dos fontes afetados, rede, CaixaDS, host/composition root e
  **29 testes Android**, incluindo o novo teste do aviso. Todos os 29 apenas compilaram;
  nenhum foi executado no dispositivo nesta rodada.
- `git diff --check` aprovado, com avisos históricos de normalização CRLF nos arquivos Gradle.

O gate de Home agora inclui o novo componente e dados de cobertura instrumentada, com dependência
de `connectedDebugAndroidTest`. O gate de network inclui os DTOs de sessão/notificação. Isso amplia
o escopo exigido; não significa que esses gates integrais já passaram.

## Pendências mantidas

- Captura pareada do aviso, navegação/reentrada e prioridade de todos os avisos no APK.
- Integração completa do popup de última notificação e suas falhas de leitura/link/imagem.
- Validação dos demais formatos de carrinho e de todos os estados por modalidade.
- Transferência das apostas locais, confirmação inequívoca do lote e limpeza seletiva; o POST
  de verificação de cadastro continua enviando lista vazia.
- Persistência/reconciliação após morte do processo; o conteúdo OIDC/sessão continua em memória.
- Cobertura global superior a 95%, todos os flavors/Release, Hilt/KSP, D8/R8 e matriz visual.

O comando `./gradlew :core:auth:testDebugUnitTest :core:network:test :feature:home:check --offline`
falhou antes das tarefas pelo lock `gradle-9.4.1-bin.zip.lck (Operation not permitted)`.
Compilação isolada não é substituto para a homologação Android/Gradle.
