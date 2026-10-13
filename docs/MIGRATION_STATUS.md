# Estado da migração para Compose

## Leitura correta do status

O corte técnico para Compose está ativo, mas a equivalência integral com o legado ainda não está homologada. Há 78/78 destinos renderizando no smoke mock, zero layout XML e zero Fragment, porém isso não prova igualdade pixel a pixel nem cobre sozinho todas as combinações de respostas, permissões e regras. As afirmações abaixo descrevem implementações já presentes; a validação comparativa tela/estado continua sendo acompanhada em `VISUAL_PARITY_MATRIX.md`.

## Aplicativo ativo

### 09/09/2026 — Diálogo de carrinho favorito

A entrada do nome do carrinho favorito agora usa `CaixaDSTextInputDialog`, reconstruído
a partir do XML/controller no legado em `/Users/ubiracysantos/Projects/legado/simlo-app-android`.
Foram restaurados título, hint, dimensões, fontes, limite de 30 caracteres e limpeza
do nome ao cancelar. Envios repetidos ficam bloqueados enquanto a validação está em andamento.

Validação: **69 testes unitários do carrinho**, **4 testes Android da tela do carrinho**
e **2 testes Android do novo diálogo aprovados**. `verifyCaixaDS` e compilação da Home
aprovados. A suíte completa do CaixaDS teve 9/10 aprovações (falha no teste antigo da bola
numérica); `verifyCleanArchitecture` apontou referências de infraestrutura em três repositories
não alterados nesta etapa. Paridade visual integral e modernCheck não estão aprovados.
Ver [auditoria do diálogo](CART_FAVORITE_DIALOG_PARITY_AUDIT.md).

Os registros abaixo preservam as evidências e limitações das revisões anteriores.

O CTA do carrinho agora usa `CaixaDSCartActions`, com texto misto em duas linhas, alturas,
raios, cores e espaçamentos do legado. Título, fundo branco e aviso de mínimo também foram
revistos. Carrinho vazio, inclusive após refresh de cotas/GPS, percorre conta → localização →
histórico → login → validação do serviço, sem bloqueio local por mínimo/quantidade.
Ver `CART_CHECKOUT_ACTION_AUDIT.md`, que corrige a interpretação histórica de vazio e registra
a pendência do handler completo de erro/redirect do validador.
A revisão aprovou **686 testes JVM** e compilação isolada; lógica/estado do carrinho selecionados
atingiram **323/325 linhas (99,38%)**. Os **42 testes Android apenas compilaram**, incluindo três
novos testes de ações/integração. Não houve execução Android nem novo APK. Gradle continua
falhando antes das tarefas por permissão no lock. Cobertura global, todos os flavors/Release,
matriz visual completa e transferência local no pós-login permanecem pendentes.
Os registros seguintes são históricos e conservam os limites de cada rodada.

O resumo do carrinho agora preserva a lista plana `apostas` para total/quantidade e mantém os
grupos somente para cards e IDs de exclusão. Home usa a contagem do snapshot no pós-login e no
refresh; limpar/favoritar consideram a mesma projeção. `CaixaDSCartSummary` substitui os dois
rodapés divergentes, com fundo diagonal, ícone, dimensões/textos legados, overflow `...` e
visibilidade por total positivo. Ver `CART_SUMMARY_PARITY_AUDIT.md` e o uso em `CAIXA_DS.md`.
A revisão aprovou **682 testes JVM** e compilação isolada, com **326/328 linhas (99,39%)** no
escopo selecionado de lógica/estado do carrinho; cálculo Data, entidade de resumo e regras de
apresentação selecionadas atingiram 100%. Os **39 testes Android apenas compilaram**, incluindo
quatro novos testes do componente e da integração na tela. O gate ampliado do carrinho inclui
todas as classes e execução instrumentada, mas ainda não foi executado. O Gradle falha antes
das tarefas por permissão no lock do wrapper. Upload/limpeza do carrinho local, homologação visual,
Android, todos os flavors/Release e cobertura global continuam pendentes. Os registros abaixo
são históricos e não ampliam o alcance dessa validação.

A central de mensagens recebeu auditoria de comportamento: o legado usa amostras locais, não
o serviço da última notificação autenticada. Foi removido o PUT indevido com IDs de exemplo,
e a injeção agora cria estado por destino. Expansão em Não Lidas preserva o conteúdo até atualizar
o filtro; a leitura explícita remove o item. Switches independentes, ações vazias, prévia/cores e
ícones de swipe foram alinhados às fontes legadas. Ver `NOTIFICATION_INBOX_PARITY_AUDIT.md`.
Esta revisão aprovou 670 testes JVM e compilação isolada, com 99,32% no escopo de lógica/estado
de notificações e 100% nos escopos locais de repository/casos de uso/factory. Os 35 testes Android
apenas compilaram; o gate ampliado da feature ainda depende da execução no dispositivo.
Paridade visual integral, execução Android, todos os flavors/Release e cobertura global continuam
pendentes. Os registros seguintes são históricos e mantêm o escopo das revisões anteriores.

O popup de última notificação está integrado em Compose, com imagem base64/placeholder original,
link sem confirmação de leitura, prioridade do aviso de pagamento e PUT vinculado à sessão.
Falhas agora preservam os avisos/redirects tipados do handler legado: reconhecimento não reabre
o popup nem repete a leitura. Mocks específicos mantêm o pós-login e alteram só o PUT.
Ver `HOME_NOTIFICATION_POPUP_AUDIT.md` para evidências, diferenças defensivas e pendências.
Esta revisão aprovou 643 testes JVM e compilação isolada, com 99,08% no escopo de lógica/estado
da Home e 100% nos escopos selecionados de leitura/mapeamento. Os 32 testes Android apenas
compilaram. Gradle segue impedido pelo lock do wrapper fora da área permitida.
Transferência local, matriz visual, execução Android, todos os flavors/Release e cobertura global
continuam sem homologação. Os registros seguintes descrevem revisões anteriores.

O pós-login agora publica carrinho/última notificação/pagamento não identificado no estado da
mesma sessão, atomicamente com a verificação da identidade. A Home restaura o resumo do carrinho
e apresenta o aviso de pagamento com texto legado e CTA para Minhas compras; logout/troca de
sessão removem os dados, e respostas antigas de resumo são descartadas. A notificação foi
preservada, mas seu popup ainda não está integrado. Envio/limpeza do carrinho local e homologação
visual/flavors/Release continuam pendentes. Ver `POST_LOGIN_CONTENT_PARITY_AUDIT.md`.
Esta rodada aprovou 621 testes JVM e compilação isolada; os escopos de sessão/publicação
atingiram 100% de linhas, e HomeViewModel/UiState 98,08%. Os 29 testes Android apenas compilaram.
O gate ampliado de Home inclui o componente novo e testes instrumentados, ainda sem execução
integral pelo Gradle. Isso não comprova cobertura global ou paridade visual de todas as telas.

Os parágrafos seguintes registram revisões anteriores e seus limites à época.

O coordenador de pós-login agora consome o repository e o cliente dedicado de cadastro pelo Hilt.
A identidade é capturada antes do GET de bloqueio e revalidada após as duas chamadas; falha
antiga não solicita logout da conta que substituiu a sessão. Código negocial/erros de validação
não viram acesso por HTTP 200. A conversão de acesso é compartilhada com as consultas comuns.
**O envio do carrinho local continua vazio**, sem confirmação/limpeza ou restauração de notificações.
Ver `POST_LOGIN_SESSION_INTEGRATION_AUDIT.md`; a equivalência completa permanece pendente.
Esta rodada aprovou 609 testes JVM e compilação isolada, com 100% nos escopos selecionados
do coordenador e repositories/mapeador de cadastro. Os 28 testes Android apenas compilaram.
O gate de Domain agora exige mais de 95% e entradas não vazias, mas sua execução integral
por Gradle, flavors/Release, cobertura global e matriz visual continuam sem aprovação.

A revisão anterior implementou a infraestrutura descrita abaixo, ainda sem o consumo que foi
conectado nesta revisão. Seus resultados de teste são históricos, não validação da árvore atual.

O transporte de verificação de cadastro recebeu cliente dedicado e data source de pós-login,
com identidade capturada por requisição, metadados de erro preservados, corpo one-shot e sem
redirect/retry automático. Providers Hilt, rotação de pins e ProGuard estão conectados.
**O coordenador ainda não chama esse caminho:** envio das apostas locais, confirmação/limpeza
do recibo e aplicação do carrinho/notificações continuam pendentes. Ver
`ACCOUNT_VERIFICATION_TRANSPORT_AUDIT.md`. Isso não conclui a paridade comportamental/visual.
Esta revisão aprovou 602 testes JVM e compilação isolada do host/composition root, com 100%
de linhas nos escopos selecionados de transporte e data source novos. Os 28 testes Android
apenas compilaram; Gradle segue impedido pelo lock, sem aprovação de flavors/Release ou cobertura global.

O pós-login passou a publicar a identidade junto com o token somente após verificações adicionais
de issuer, audience, nonce e validade do ID token recebido diretamente do AppAuth. A chave do
proprietário separa usuário/issuer/cliente/backend; referências antigas não voltam a valer ao
cancelar um novo login. Há provider Hilt para a porta de identidade, mas o envio do lote ainda
não foi conectado. Ver `AUTHENTICATED_SESSION_IDENTITY_AUDIT.md`; homologação do provedor, transporte,
paridade visual, flavors/Release e cobertura global continuam pendentes.
Esta revisão aprovou 592 testes JVM e compilação isolada do gateway/host. Os escopos selecionados
de estado de auth/parser atingiram 135/135 linhas; 28 testes Android somente compilaram.
Isso não comprova cobertura do app inteiro nem homologação do novo perfil OIDC nos flavors.

O carrinho herdado agora possui protocolo local de recibo persistente: reserva única por lote,
confirmação vinculada ao proprietário/UUID e limpeza transacional somente de apostas intactas.
Alterações preservam a aposta e exigem revisão; outro login ou callback antigo não libera limpeza.
O provider Hilt está presente, mas identidade verificada, transporte, retorno e telas de revisão
ainda não estão conectados ao pós-login. Portanto o POST continua sem apostas locais;
ver `LEGACY_CART_TRANSFER_RECEIPT_AUDIT.md`.
Esta rodada aprovou 581 testes JVM, com 98,85% de linhas no escopo de lógica do recibo/fingerprint,
e compilou o provider/host isoladamente. Os 28 testes Android apenas compilaram. SQLite real,
morte do processo, transporte, matriz visual, flavors/Release e cobertura global permanecem pendentes.

O carrinho local recebeu leitor não destrutivo, snapshot tipado por modalidade e mapeador para o
contrato de verificação de cadastro, incluindo matriz do Super Sete, trevos e partidas. O request
deixou de usar `List<Any>` para apostas. **O envio no pós-login continua vazio:** DI/orquestração,
confirmação persistente do lote, resposta perdida e aplicação do carrinho/notificações ainda não
foram conectados. Não considerar essa lacuna resolvida; ver `LEGACY_CART_TRANSFER_AUDIT.md`.
Esta revisão aprovou 570 testes JVM e compilação isolada; 22 testes Android apenas compilaram.
Leitura/conversão pura, mapeador e DTO selecionados atingiram 100% de linhas em seus escopos;
isso não cobre SQLite Android, o aplicativo inteiro, flavors/Release ou paridade visual.

Limpeza interrompida agora tem marcador persistido e recuperação antes de compor entrada normal, fila ou rota visual. O gate usa Splash/CaixaDS, bloqueia falhas e distingue recuperação local de logout SSO confirmado. Sem marcador não apaga dados/sessão; logout e recuperação compartilham repository singleton serializado. A revisão aprovou 556 testes JVM, compilação isolada e 96,15% na lógica da recuperação; 18 testes Android apenas compilaram. Persistência real/morte de processo, transição visual, demais caches, SSO, flavors/Release e cobertura global seguem sem homologação. Ver `SESSION_CLEANUP_RECOVERY_AUDIT.md`.

A saída agora limpa também os registros legados de usuário/biometria e a flag de usuário logado, por porta Domain e repository injetado, sem apagar as demais tabelas/preferências. A limpeza ocorre em I/O, antes de preparar EndSession; falha exige retry explícito e não libera continuação como sucesso. A revisão aprovou 546 testes JVM, compilação isolada e 98,61% no estado/lógica do logout; 13 testes Android somente compilaram. Persistência da limpeza interrompida, execução SQLite/Android, demais caches, SSO real, todos os flavors/Release e cobertura global seguem pendentes. Ver `LOCAL_SESSION_CLEANUP_AUDIT.md`.

O logout agora possui integração AppAuth EndSession pelo endpoint do flavor, compartilhada por menu, autossuspensão e falha de bloqueio pós-login. A sessão local é encerrada antes de abrir o navegador; cancelamento/falha não viram confirmação. O host limpa seleções transitórias e solicita remoção da pilha autenticada. A revisão aprovou 536 testes JVM, compilação isolada e 100% nos escopos de estado do logout (50/50) e store/registros (59/59); oito testes instrumentados compilaram sem execução. A URI de retorno no provedor, navegação/callbacks Android, limpeza persistente, todos os flavors/Release e cobertura global ainda não estão homologados. Ver `SSO_END_SESSION_AUDIT.md`; os registros abaixo são históricos e mantêm o escopo de cada revisão.

O ciclo local de autenticação foi corrigido para impedir publicação tardia após logout/cancelamento e reutilização da mesma requisição OAuth entre logins. O callback consome state pendente uma vez e a troca usa configuração/verificador guardados localmente. A revisão registra 520 testes JVM e compilação isolada do gateway AppAuth/integrações; cobertura parcial do estado de autenticação 37/37 e lógica do login 98,73%. Logout SSO externo, recuperação após morte do processo, protocolo/TLS Android, cobertura global e todos os flavors/Release continuam pendentes; ver `AUTH_ATTEMPT_LIFECYCLE_AUDIT.md`.

O pós-login agora preserva `situacaoApostador.enum` e apresenta o aviso legado de conta habilitada apenas para consultas em BLOQUEADO_PARCIAL. O reconhecimento mantém a sessão/destino e não repete chamadas. A superfície técnica permanece atrás dos avisos pós-login. A revisão registra 512 testes JVM aprovados, compilação isolada e 98,72% no escopo de lógica/estado do login, sem cobertura global ou homologação Android. Capturas, combinações com redirects, biometria, sessão global e todos os flavors/Release continuam pendentes; ver `POST_LOGIN_PARTIAL_BLOCK_AUDIT.md`.

O pós-login passou a executar o GET de verificação de apostador bloqueado antes da consulta de cadastro, por caso de uso e repository injetados. Falha inicial mostra o alerta legado, impede avançar e exige reconhecimento para limpar a sessão local. Cadastro ausente/redirect desconhecido deixaram de liberar Home. A revisão registra 508 testes JVM aprovados e compilação isolada, com cobertura parcial de 98,63% na lógica do login e 100% nos escopos novos de domínio/repos/DTO. Logout SSO externo, bloqueio parcial, sessão global, callbacks/visual, cobertura global e todos os flavors/Release continuam pendentes; ver `POST_LOGIN_BLOCKED_CHECK_AUDIT.md`. Os registros seguintes descrevem revisões anteriores.

O retorno OAuth agora substitui o formulário do login pela superfície azul vazia da `TokenActivity` legada e pelo modal “Carregando…”. O mesmo `AccountTokenScreen` atende à rota de catálogo, eliminando a versão divergente com “Aguarde”. A revisão aprovou 10 testes JVM de login e 11 do CaixaDS, compilação isolada e compilação de três testes instrumentados novos ainda sem execução. O teste de estado preserva loading durante troca do token e validação do cadastro, sem duplicar chamadas. Isso não homologa navegador, retorno contextual, screenshots, flavors ou cobertura global; ver `AUTHENTICATION_RETURN_PARITY_AUDIT.md`.

O loading institucional compartilhado recebeu correção de alpha do fundo, interpolador legado, tipografia e modalidade sem dim adicional. Login, retry de indisponibilidade e operações Mercado Pago usam a mesma janela bloqueante. A revisão do CaixaDS registra 11 testes JVM, compilação isolada e novos testes instrumentados ainda sem execução; não homologa screenshots ou flavors. Ver `LOADING_PARITY_AUDIT.md`. Os registros seguintes são históricos e têm escopos distintos.

Falhas de transporte no POST de resgate e GET do token Mercado Pago preservam agora a distinção timeout/conexão. A apresentação consulta a conectividade no recebimento do erro e no clique para login, usa os textos conferidos no APK legado e mantém o formulário sem repetir operações após reconexão. Foram incluídos quatro mocks e a matriz de fixtures passou a exigir a falha de transporte correta nos endpoints afetados. A suíte isolada ampliada aprovou 468 testes e compilou todos os fontes de rede e os fontes selecionados de Compose/host/integração. Coberturas parciais: lógica de retail 97,75%, pagamento 99,03%, avisos compartilhados 37/37 e repository/data source/mapeador de resgate 234/234. Isso não comprova cobertura global, callbacks Android, paridade visual ou todos os flavors/Release; o Gradle permanece impedido pelo lock. Ver `PRIZE_CONNECTIVITY_PARITY_AUDIT.md`. Os registros abaixo são históricos.

Os erros HTTP da consulta do token Mercado Pago agora preservam avisos, redirects, consentimento de login e novo Sim/Não no código 018003. As decisões gerais ficam em Domain e os avisos em `core:presentation`, compartilhados com o resgate sem acoplar as features. A preparação usa o cliente dedicado, mantém o e-mail selecionado e não repete compra/resgate ao retornar. Consentimentos consecutivos com o mesmo texto têm identidade própria para não serem suprimidos pelo `StateFlow`. A revisão aprovou 441 testes JVM e compilação isolada; lógica de pagamento selecionada 502/507 (99,01%), avisos compartilhados 29/29 e repository/data source de resgate/cadastro 221/221. Isso não prova cobertura global, paridade visual nem Hilt/KSP/flavors/Release. O Gradle permanece impedido pelo lock do wrapper. Ver `REGISTRATION_TOKEN_ERROR_AUDIT.md`. Os registros abaixo são históricos.

O POST de resgate usa agora um cliente transacional dedicado, injetado e atualizado junto com os pins, sem fallback para o cliente geral. Resgate e compra têm corpo não reutilizável, além de redirects/retries de conexão desativados, cobrindo também a possibilidade de follow-up de HTTP 503. A execução isolada aprovou 429 testes JVM e compilou o host/Compose/composition root. A cobertura selecionada de lógica de `retail` é 405/414 (97,83%), repository/data source de resgate 209/209 e parser/DTO/provider/interceptor transacional 40/40. Não são cobertura global nem prova de transporte TLS/runtime. O Gradle continua impedido pelo lock fora da área permitida; erros dos demais endpoints de resgate/token, paridade visual, Hilt/KSP e todos os flavors/Release permanecem pendentes. Ver `PRIZE_TRANSACTION_TRANSPORT_AUDIT.md`. Os registros seguintes são históricos.

Os erros HTTP do POST de resgate preservam consentimento de login no 401, diferença entre 400/404, alertas de CPF/tipo de mensagem no 500 e redirects para destinos tipados. O formulário é pausado durante aviso/navegação, sem novo POST ao retornar. Pagamento e resgate compartilham formatação diagnóstica e efeito de retomada. A revisão final registra 422 testes JVM, compilação isolada, 97,83% no escopo de lógica de `retail` e 100% no arquivo do repository/data source correspondente; não são cobertura global nem homologação Android. Erros dos demais endpoints de resgate/token, callbacks completos, matriz visual e todos os flavors/Release continuam pendentes. Ver `PRIZE_HTTP_ERROR_PARITY_AUDIT.md`.

O código `018003` também foi integrado ao resgate Mercado Pago, preservando o e-mail digitado, consentimento Sim/Não, loading institucional modal e retorno sem repetir o POST. O resultado tipado diferencia cadastro necessário de prêmio resgatado, e novos mocks exercitam token disponível/falha. A revisão registra 411 testes JVM e compilação isolada, 97,57% no escopo de lógica de `retail` e 100% no arquivo do repository/data source de lotéricas/resgates. O gate de `core:data` passou a incluir corrotinas/classes internas e a rejeitar entradas vazias; sua aprovação integral pelo Gradle ainda não foi executada. Outros handlers de resgate/token, runtime/visual, todos os flavors/Release e cobertura global permanecem pendentes. Ver `PRIZE_MP_REGISTRATION_AUDIT.md`. Os registros abaixo são históricos e não homologam a árvore atual.

O código `018003` no pagamento agora solicita consentimento Sim/Não, consulta o perfil/token pelo serviço legado e abre o cadastro Mercado Pago por coordenador injetado. O retorno não reenvia cartão nem aprova compra; mocks de sucesso/falha do token simulam o navegador localmente. Foram aprovados 394 testes JVM e a compilação isolada, com 98,92% de linhas no escopo selecionado da lógica de pagamento, sem comprovar cobertura global. O ramo de resgate, callbacks/visual Android e todos os flavors/Release continuam pendentes. Ver `MERCADO_PAGO_REGISTRATION_AUDIT.md`. Os parágrafos seguintes registram revisões anteriores e seus limites à época; não são homologação da árvore atual.

Os alertas de negócio 400/404/500 agora usam decisões tipadas, diálogo Compose e as ações Apostar mais/Entendi. Foram preservados os três códigos de compra com retorno à Home, o erro específico de CPF 2067, a distinção de tipo 1 no erro 500 e a composição de código/data/hora local com relógio injetado. O aviso não conclui a compra nem dispara outro POST ao ser dispensado. A revisão registra 375 testes JVM e compilação isolada, com 98,77% no escopo de lógica de pagamento e 98,70% no repository/data source. Não representa cobertura global ou homologação visual; `018003`, retornos completos, reconciliação e todos os flavors/Release permanecem pendentes. Ver `PURCHASE_NOTICE_PARITY_AUDIT.md`.

Os erros HTTP da compra agora preservam metadados tipados, com prioridade dos redirects, fallback 401 para login e os destinos de carrinho/carrinhos favoritos exclusivos desse handler. Foram incluídos três mocks de erro de compra, cobertura dos novos modelos e retenção explícita de `PurchaseBackendApi` no ProGuard. A revisão registra 359 testes JVM, compilação isolada, 100% de linhas no transporte selecionado e 98,61% no repository/data source. Os alertas 400/404/500/018003 e a homologação Android/flavors continuam pendentes; ver `PURCHASE_HTTP_ERROR_PARITY_AUDIT.md`. O Gradle segue impedido de abrir o lock fora da área permitida, portanto esses resultados não aprovam Release nem a migração integral.

Os redirects conhecidos de compra (login, termos, cadastro e indisponibilidade) agora atravessam repository/domínio/ViewModels como destinos tipados e chegam ao grafo Compose; o aceite de termos preserva o alerta literal do APK legado. A pausa durante a navegação impede reenvio/consulta enquanto outro destino está aberto; retornar não dispara novo POST. Foram acrescentados quatro cenários mock, testes de cadeia Retrofit e proteção contra classificar redirect como aprovação. Esta revisão registra 352 testes JVM e compilação isolada, com 98,64% de linhas no escopo de lógica de pagamento e 98,53% no repository/data source; não são cobertura global. Erros HTTP específicos, callbacks completos, recuperação contextual e validação Android/visual/flavors permanecem pendentes. Ver `PURCHASE_REDIRECT_PARITY_AUDIT.md`, que detalha também as diferenças deliberadas de segurança em relação ao legado.

A submissão de cartão agora separa erros de preparação de respostas inconclusivas após iniciar o registro. O resultado tipado exige revisão em Minhas compras, com o alerta literal conferido no APK legado; Entendi remove a confirmação da pilha e não reenvia o cartão. Mocks de resposta perdida e fixtures de preparação dos dois processadores exercitam o fluxo. A revisão registra 343 testes JVM e compilação isolada; persistência/reconciliação entre processos, redirects específicos e homologação Android/visual continuam pendentes. Ver `CARD_SUBMISSION_REVIEW_AUDIT.md`.

O acompanhamento do cartão agora distingue término 200 de continuação 303, atualiza ID/periodicidade/limite a cada consulta e mantém a posição acumulada. Retry após falha de consulta acompanha a compra existente sem reenviar o cartão; limite zero não faz consulta extra. A revisão registra 330 testes JVM e compilação isolada. Isso corrige a aplicação indevida da classificação de status como condição de término do cartão; Pix continua tendo regra própria. Redirects, resultado desconhecido no POST inicial, morte do processo e homologação integral continuam pendentes. Ver `CARD_POLLING_PARITY_AUDIT.md`.

O transporte de compras agora lê envelopes HTTP 200/303 pelo cliente dedicado e injetado, com pinagem, autenticação e logs preservados, sem redirects HTTP/retries automáticos. Código de erro, payload ausente e redirect de negócio não viram aprovação. A revisão registra 317 testes JVM e compilação isolada; navegação dos redirects e protocolo completo de polling ainda estão pendentes. Ver `PURCHASE_TRANSPORT_PARITY_AUDIT.md`.

A classificação de situações de compra foi corrigida: status 2 é débito iniciado, não aprovação. Cartão/Pix e mocks agora usam os códigos finais e negativos do legado, sem gravar Pix em andamento como compra concluída. Essa revisão anterior registrou 296 testes JVM e compilação isolada; ver `PAYMENT_STATUS_PARITY_AUDIT.md`. A lacuna de transporte 303 identificada nela recebeu a implementação posterior descrita acima, sem concluir o restante das regras de processamento.

A consulta de localização foi integrada ao pré-pagamento, depois da atualização das cotas e antes da compra repetida/login/validação. Os alertas preservam os textos e Sim/Não/Entendi; concessão/retorno das configurações reconsultam o país, e bloqueio retorna à Home sem apagar o carrinho. A revisão registra 281 testes JVM e compilação isolada, mas permissões/GPS/Geocoder no Android, paridade visual e flavors/Release continuam pendentes. Ver `CART_LOCATION_PARITY_AUDIT.md`.

A regra de compra de mesmo valor em 24 horas passou a consultar o banco legado por repositório injetado e apresentar o alerta Sim/Não no carrinho. Cartão/Pix gravam o histórico nos ramos de conclusão modernos, sem transformar falha de persistência em falha de cobrança. O SQLite nativo, a ordem com localização e os ramos de polling/redirects ainda precisam de homologação; ver `CART_RECENT_PURCHASE_PARITY_AUDIT.md`. Isso não conclui a equivalência do pré-pagamento.

A entrada em pagamento pelo carrinho passou a verificar cadastro/suspensão e sessão por caso de uso injetado. A validação do carrinho é obrigatória, sem fallback de sucesso para dependência ausente. O Sim do alerta de login agora inicia a autorização no navegador sem um segundo clique; cancelamento retorna ao carrinho. Essa revisão anterior registrou 232 testes JVM e compilação isolada. Localização e compra repetida foram implementadas nas revisões posteriores acima; homologação de callbacks Android continua pendente. Escopo e evidências em `CART_CHECKOUT_PARITY_AUDIT.md`.

A lista e a confirmação de inclusão do carrinho usam a hora do servidor nos contadores; a confirmação recalcula também em `ON_RESUME`, sem prolongar a reserva por ticks atrasados. A lista atualiza somente quando há cotas vencidas e antes da validação de pagamento. A revisão mais recente registra 216 testes JVM e 98,85% no escopo de ViewModels/regras de carrinho, incluindo corrotinas. O restante do pré-pagamento e a homologação visual/interativa continuam pendentes; ver `CART_EXPIRY_PARITY_AUDIT.md`.

As telas de indisponibilidade agora consultam uma dependência de conectividade injetada na abertura e antes de cada nova tentativa, inclusive nos fluxos de simulação. A mensagem offline não é inferida de erro TLS/servidor, e atualização obrigatória permanece inalterada. A revisão registra 209 testes JVM e compilação isolada; transições de rede no APK e builds/flavors continuam pendentes. Ver a revisão de conectividade em `STARTUP_STATUS_PARITY_AUDIT.md`.

A recuperação dos parâmetros de simulação agora permanece na Home/carrinho de origem, sem executar a conclusão do onboarding. Foi preservada a diferença legada entre erro de transporte da Home e do carrinho, e respostas vazias não liberam a recuperação. A revisão registra 204 testes JVM; escopos de Home/carrinho acima de 95%, incluindo corrotinas, não representam cobertura global. Ver `SIMULATION_RECOVERY_PARITY_AUDIT.md` para evidências e pendências.

Atualização e indisponibilidade agora compartilham o mesmo componente entre entrada real, mock e catálogo. Foram removidos a versão fixa e os atalhos de conclusão do onboarding; Atualizar abre a loja e Tentar novamente revalida segurança/versão/parâmetros. Essa revisão anterior registrou 164 testes JVM e compilação isolada, sem homologação visual no APK. A recuperação originada na simulação foi tratada na revisão posterior descrita acima. Pendências e escopo em `STARTUP_STATUS_PARITY_AUDIT.md`.

A auditoria de ambientes de 07/09 identificou que o indicador do menu e a troca real são requisitos diferentes. O indicador já usa o endpoint efetivo; a seleção legada de seis ambientes no login/indisponibilidade ainda está pendente (`ENVIRONMENT_PARITY_AUDIT.md`). A mesma revisão corrigiu uma lacuna de pinagem: hosts ausentes do conjunto configurado agora falham fechado nos clientes Retrofit. O fallback contém somente o host produtivo; TQS/automação e provedores de pagamento exigem pins aprovados antes de validar chamadas reais. Os 30 testes focados aprovados não substituem os testes TLS/runtime/flavors; seis testes da suíte ampliada ficaram impedidos pelo sandbox de abrir MockWebServer. Ver `SECURE_PIN_ROTATION.md`.

O aplicativo moderno está diretamente no módulo Gradle `:app`, sob `app/`. `app-compose` era o nome provisório usado para construir o novo grafo em paralelo ao legado; ele foi eliminado e o módulo definitivo assumiu o caminho convencional. O diretório histórico foi retirado do projeto após a migração. O aplicativo ativo possui:

- uma única `MainActivity`, limitada a hospedar Compose e encaminhar o retorno OAuth;
- zero Fragments e zero arquivos de layout XML;
- Navigation Compose com 78 destinos/estados navegáveis catalogados; as quatro telas auxiliares do Rapidão e de números ficam registradas no inventário como componentes internos de pager/modal, sem criar destinos artificiais;
- módulos independentes por feature, ViewModels com `StateFlow`/ações e dependências voltadas para casos de uso de `core:domain`;
- Hilt no composition root, com KSP compatível com o Kotlin integrado do AGP 9.2.1;
- CaixaDS aplicado no host, com tema claro/escuro, tokens, ícones e catálogo Compose;
- namespace e `applicationId` produtivo `br.gov.caixa.loterias.apostas` preservados;
- flavors `prd`, `tqs` e `automacao` restauradas, incluindo endpoints, SSO, Firebase, FilaBRA, nomes e versionamento por ambiente.

O `AndroidManifest.xml` permanece porque é obrigatório na plataforma. AppAuth e o AAR FilaBRA podem incluir componentes Android internos em seus manifests/binários; nenhum deles é Activity de feature escrita pelo aplicativo.

## Baseline e corte

| Item | Legado inventariado | `:app` atual |
|---|---:|---:|
| Java | 1.032 arquivos | 0 |
| Activities/Fragments de feature | 145 | 0 |
| Activity do aplicativo | várias | 1 host |
| Layouts XML | 396 | 0 |
| Destinos catalogados em Compose | 0 | 78 |

`verifyComposeMigration` impede a reintrodução do diretório `app-compose`, de XML de layout/navegação/drawable, de Fragment ou de outra Activity no código-fonte moderno. A allowlist aceita apenas o Manifest, o tema de janela exigido pela pré-splash do Android e as duas declarações de ícone adaptativo; nenhum desses arquivos contém hierarquia de UI. `verifyLegacyUiInventory` preserva a baseline dos 396 XMLs e exige destino para os 95 pontos de montagem de UI inventariados. `verifyCleanArchitecture` valida a direção das dependências e o uso de Hilt. `verifyCaixaDS` impede cores literais nas features e exige o tema no host. `verifyFilaBraAar` garante o SHA-256 do binário homologado.

## Integrações concluídas

| Fluxo | Serviço |
|---|---|
| Verificação de versão | `GET /app/valida-versao/{version}` |
| Parâmetros do aplicativo | `GET /parametros/configuraveis-app` |
| Lista de bolões | `GET /boloes/recuperar-boloes-disponiveis` |
| Detalhe do bolão | `GET /boloes/detalhar-bolao?idBolao=...` |
| Último resultado | `GET /resultados/{modalidade}` |
| Resultado por concurso | `GET /resultados/{modalidade}/{concurso}` |
| Dúvidas e termos | `/duvidas/*` e `/termos-de-uso/*` |
| Autenticação | OIDC Authorization Code + PKCE via AppAuth |
| Verificação pós-login | `POST /logado/usuarios/verificar-cadastro` |
| Cadastro do apostador | `POST /usuarios`, `GET /bairros` |
| Dados pessoais | `GET/PUT /usuarios`, `GET /municipios`, `GET /bairros` |
| Recalcular limite autorizado | `POST /usuarios/recalcular-limite-autorizado` |
| Carrinho | `GET /logado/carrinhos`, inclusão de aposta/cota, exclusão de apostas/combos e limpeza |
| Carrinhos favoritos | consulta, detalhe, exclusão e inclusão em `/logado/carrinhos-favoritos/*` |
| Montagem de aposta | `GET /publico/parametros-simulacao`, `POST /logado/carrinhos/incluir-aposta` |
| Home | `GET /publico/parametros-simulacao`, `GET /parametros/configuraveis-app`, `GET /logado/combos`, `GET /repassometro`, `GET /logado/carrinhos` |
| Rapidão | `GET/POST /logado/rapidoes`, `POST /logado/rapidoes/gerar-apostas` |
| Apostas favoritas | `GET /logado/apostas-favoritas/`, filtro por modalidade, exclusão e inclusão no carrinho |
| Autoavaliação e Jogo Responsável | `/autoavaliacoes/*`, `/repassometro`, `/usuarios/suspender-conta-apostador` |
| Central de notificações | estado de sessão equivalente à lista legada + `PUT /historico-notificacoes/{notificationId}/visualiza` ao marcar como lida |
| Sala de espera | FilaBRA 2.6.5.1 encapsulado por `core:queue` |
| Busca/favoritos de lotéricas | `/lotericas/nome-loterico` e `/boloes/*loterica-favorita*` |
| Resgate de prêmio | detalhes, QR de lotérica, PIX, Mercado Pago e comprovantes em `/apostas/{id}/*` |
| Conferência de bilhetes | câmera CameraX + ML Kit e `/bilhetes/conferir-bilhete*` |
| Filtros de apostas | `GET /apostas/config-consulta`, `GET /publico/modalidades` e `GET /apostas` |
| Histórico de compras | `GET /compras/meses`, `GET /meios-pagamento`, `GET /compras/situacoes-compra` e `GET /compras` |

Em suporte, a navegação Compose preserva a hierarquia do legado: somente `Termo de uso` e `Dúvidas` ficam visíveis na tela combinada; a lista de dúvidas abre uma seção e então consulta/exibe as perguntas, que expandem e recolhem com animação. Medidas, cores e recuos foram derivados de `content_termos_uso.xml`, `item_duvida.xml` e `item_duvida_detalhe.xml` antes da remoção dos layouts.

## Filtro do Marketplace

- `qtdeMinimaApostaBolaoFiltro` e `qtdeMaximaApostaBolaoFiltro` são lidos por `RemoteAppConfigurationRepository` do mesmo caminho definido no legado por `APOSTAS_VERIFICA_APRESENTA_HISTORICO_PATH`.
- Não existe intervalo fixo `1..15` em produção.
- A consulta envia `quantidadeAposta` para `/boloes/recuperar-boloes-disponiveis`.
- A quantidade de dezenas usa modal numérico, com opções dependentes da modalidade, como “Números que quero”.
- O texto da ação é exatamente “Excluir Quantidade”.

## Qualidade

`modernCheck` executa compilação, APK, lint, testes JVM/UI disponíveis, verificações arquiteturais e gates JaCoCo de 95% para a lógica coberta pelos contratos de cada módulo moderno. Os relatórios Android usam o diretório de classes do Kotlin integrado ao AGP 9 (`intermediates/built_in_kotlinc/...`), evitando aprovações vazias pelo caminho antigo. Na última execução integral, `betting` registrou 96,89%, `cart` 98,34%, `marketplace-filter` 98,68% e `preferences` 100%; todos os gates do conjunto passaram. As telas Compose são declarativas; casos de uso, reducers, ViewModels, regras, mapeadores, autenticação de sessão e cliente HTTP são cobertos nos respectivos módulos.

Esses percentuais históricos correspondem aos filtros de classes configurados em cada gate e não comprovam cobertura superior a 95% do aplicativo inteiro. A revisão de 06/09/2026 identificou exclusões de classes internas de corrotinas e entradas obsoletas em `retail`. O gate desse módulo passou a incluir todos os ViewModels, UiStates e arquivos de regras, com classes internas, relatório XML/HTML e falha explícita caso faltem classes ou dados de execução. A medição JVM dos fontes atuais registrou 272/282 linhas (96,45%) nesse escopo, com 22 testes; sete testes do repository de lotéricas/resgate também passaram. A cobertura das telas e a revisão dos filtros dos demais módulos permanecem pendentes. Ver [auditoria de resgate](RETAIL_PARITY_AUDIT.md).

O fluxo de jogo responsável usa estado próprio, sem o `WorkflowViewModel` genérico: recupera as perguntas da autoavaliação, envia todas as respostas, consulta o repassômetro e executa a autoexclusão com o CPF retornado pelo perfil autenticado. Após a confirmação da suspensão, a sessão é encerrada como no fluxo legado.

O fluxo de conta também usa estado e casos de uso próprios. Depois do navegador OIDC, a resposta de `verificar-cadastro` decide entre Home, cadastro, aceite de termos e autoavaliação. Cadastro e dados pessoais não usam exemplos fixos: as claims do token e os serviços de usuário/localidade alimentam os formulários Compose. As confirmações de cadastro/atualização preservam o redirecionamento único após três segundos com `AccountStatusViewModel`; a alteração de dados abre a URL da Conta CAIXA no navegador seguro e o retorno de token mantém a tela de loading institucional.

No cadastro, o estado inicial foi comparado diretamente com `activity_cadastrar.xml`: toolbar, marca Loterias, título com pesos mistos, campos sublinhados, seletor de sexo, ajuda de CEP e consentimentos agora seguem a geometria e os estados do legado. A captura reproduzível `build/visual-audit/legacy-compose-register-v4/report.tsv` registra SSIM `0.889181`, contra `0.834539` antes dessa reconstrução; estados preenchido, erro e teclado continuam pertencendo à homologação combinatória da matriz.

Em Dados Pessoais, a tela Compose recuperou o cabeçalho estendido, campos sublinhados, seletor segmentado, consulta de CEP, controle de notificações, limite diário, diálogos de confirmação/erro e o rodapé fixo do legado. A evidência sem overlay registra SSIM `0.893980` em `build/visual-audit/legacy-compose-personal-data-v2/report.tsv`; o cenário vazio foi capturado à parte para comprovar o alerta “Dados do apostador indisponíveis”.

As telas de confirmação de cadastro e de atualização também usam novamente a ilustração, o fundo claro, o divisor, os pesos tipográficos e as mensagens literais do legado, mantendo o redirecionamento automático único de três segundos. A confirmação de atualização registra SSIM `0.956668` em `build/visual-audit/legacy-compose-account-confirmations-v4/report.tsv`.

O onboarding também não usa mais o `WorkflowViewModel` genérico. A introdução possui `OnboardingViewModel`, `StateFlow` e efeitos; exibe a confirmação “Você tem mais de 18 anos?” da primeira tela legada, envia a resposta negativa à tela de restrição e altera o CTA da terceira página para “Acessar aplicativo”. Ao sair, grava a mesma preferência `INTRODUCAO_KEY` do app anterior. Assim, instalações atualizadas preservam a escolha e novas instalações mostram a introdução apenas na primeira execução.

O estado genérico remanescente também foi eliminado das rotas locais de apostas, pagamento e lotéricas. Cada módulo possui `UiState`, efeitos e `ViewModel` próprios; número e titular do cartão, validade, CVV, aceite do Pix, consulta de localização e e-mail do Mercado Pago são propriedades tipadas e validadas pela feature. `:core:presentation` conserva apenas os metadados imutáveis usados pelo catálogo de destinos.

As antigas Activities separadas de formulário, apresentação do cartão, CVV e confirmação permanecem endereçáveis para compatibilidade e auditoria, mas agora entram em estados da mesma `PaymentCardViewModel`. Prévia/CVV/confirmação recuperam um cartão salvo pelo caso de uso; o caminho normal mantém o processador selecionado, envia o pagamento real e acompanha o status assíncrono. O antigo fluxo de pagamento demonstrativo e seus cartões fixos foram removidos.

A Home possui `HomeViewModel` no módulo `:feature:home`. O carrossel não usa bullets, repete as modalidades de forma infinita e reproduz o `ScaleTransformer` legado: 1,00 no card selecionado e 0,80 nos adjacentes, com interpolação durante o gesto. Concurso, prêmio estimado, sorteio, encerramento, preço mínimo, estado aberto e modalidades com cotas vêm de `/publico/parametros-simulacao`. O card Instantânea mantém conteúdo e navegação próprios; o card Combo respeita `comboApostasHabilitado` e as opções válidas de `/logado/combos`. O CTA “Compre seu bolão” respeita `habilitaRevendaBolaoApp`, só aparece para modalidades presentes em `modalidadesDisponiveisCotas` e abre o Marketplace filtrado por modalidade/tipo de concurso. O acesso rápido carrega `/repassometro` sob demanda, desloca o carrossel em 500 ms e o rodapé usa o carrinho real.

Na lista do Marketplace, cada card mantém o acordeão do legado, sincroniza o favorito entre bolões da mesma lotérica, limita o seletor entre uma cota e a disponibilidade retornada e calcula a primeira/última cota com a regra tarifária original. Tanto o card quanto o detalhe executam `POST /logado/carrinhos/incluir-aposta` com o contrato de cota do legado. A confirmação só é exibida depois que a resposta devolve a reserva do bolão e o contador é calculado a partir de `dataHoraExpiracaoReserva`; falhas e respostas sem reserva permanecem na tela com mensagem e nova tentativa.

Resultados não fabricam mais concurso, dezenas ou faixas de premiação quando o backend não retorna dados. A consulta mostra estado vazio/erro real, valida que o concurso digitado não ultrapasse o último disponível e os controles anterior/próximo consultam o concurso correspondente; “próximo” fica desabilitado no resultado mais recente, como no legado.

O carrinho principal deixou de usar conteúdo demonstrativo: recupera apostas individuais, cotas e combos do serviço autenticado, apresenta o total retornado (ou calculado como fallback) e executa exclusões e limpeza pelos endpoints correspondentes. Falhas de mutação preservam o último carrinho válido na tela.

A montagem de aposta não fixa mais Mega-Sena, intervalo de dezenas, preço ou Teimosinha. O `BettingGameViewModel` seleciona o jogo solicitado nos parâmetros retornados pelo backend, aplica os limites de dezenas e trevos, resolve o preço correspondente, multiplica concursos e Surpresinhas e envia `numberQuantity` mesmo quando os números são gerados pelo servidor.

O Rapidão recuperou o fluxo paginado do legado: boas-vindas, prêmio mínimo, faixa de valor, modalidades, números obrigatórios, números proibidos e resumo. As listas de números são mutuamente exclusivas. Os endpoints declarados no `ServerMethods` antigo, mas antes ausentes do contrato moderno, agora possuem DTO, data source, repository e casos de uso tipados.

“Números que quero” e “Números que não quero” não são destinos independentes: como no legado, são seletores de tela cheia pertencentes ao filtro do Marketplace e às etapas do Rapidão. Fechar ou cancelar preserva a seleção inicial, confirmar aplica a seleção e números presentes no filtro oposto são bloqueados com alerta.

Apostas favoritas individuais também deixaram de usar exemplos locais. O fluxo consulta o agrupamento real, filtra por modalidade, formata dezenas, colunas do Super Sete, time do coração, mês de sorte e trevos, confirma exclusão e aplica as opções de concurso, Teimosinha e Espelho antes de chamar a inclusão no carrinho.

A central de notificações também deixou de usar estado `remember` demonstrativo e o `WorkflowViewModel` genérico. A feature possui `NotificationsViewModel`, `StateFlow`, efeitos de compartilhamento, casos de uso e repositório injetados. Foram restauradas as cinco categorias do legado, a opção mestre, pesquisa, filtro de não lidas, expansão exclusiva com leitura automática, exclusão/leitura individual e em lote com confirmação e ações específicas de lembretes/resultados. Os ícones de mensagem foram convertidos dos vetores XML legados para `CaixaDSIcons`.

Os carrinhos favoritos também usam dados reais. A lista e o detalhe suportam exclusão, remoção da última aposta com exclusão do carrinho e transformação em carrinho atual, incluindo a escolha entre concursos regulares/especiais existente no legado.

O resgate de prêmio parte da aposta efetivamente selecionada. As opções são as retornadas por `detalhes-premio`; o QR de lotérica usa o conteúdo e a validade fornecidos pelo servidor, com contador e renovação; o PIX mantém as duas etapas do legado (consulta do titular e confirmação), cancela a reserva ao voltar da confirmação; Mercado Pago envia o parâmetro `EMAIL`; e os comprovantes são baixados pelos endpoints PDF e salvos via seletor seguro do Android.

A conferência de bilhetes usa CameraX para o preview e ML Kit para QR Code, Code 128 e ITF, preservando a digitação manual como alternativa. O payload lido é normalizado no mesmo formato do legado, criptografado com RSA/PKCS1 antes de `/bilhetes/conferir-bilhete*` e direcionado ao endpoint de código de barras ou QR Code conforme o conteúdo. A entrada manual voltou a ter as máscaras `####-#################-##` e `##.######-#`, campo opcional “Lotérico”, validação mínima histórica e ajuda de Termos de Uso. O resultado da leitura é o retorno real do serviço e não uma aposta demonstrativa. A tela vazia foi comparada com `activity_insercao_manual_cod_barras.xml` em `build/visual-audit/legacy-compose-manual-ticket-v2/report.tsv`, com SSIM `0.968241`.

“Minhas apostas” recupera do backend os períodos, situações, ordenações, tipos de aposta e tipos de concurso antes da primeira consulta, combina-os com as modalidades e preserva os filtros Surpresinha, Teimosinha e Combos do legado. O histórico decide em runtime entre SILCE, microserviço e Nova API com os mesmos parâmetros, flavor e grupo dos dois últimos dígitos do CPF usados anteriormente; clientes dinâmicos só são criados para HTTPS e hosts cobertos pelo manifesto de pins assinado, com fallback fechado para SILCE. O detalhe da aposta usa ainda `{id}/recuperar` quando a Nova API está ativa, e a disponibilidade de resgate é recalculada a partir desse detalhe recuperado. O contrato moderno preserva a aposta completa em vez de achatar somente as dezenas: matriz do Super Sete, trevos da +Milionária, time da Timemania, mês do Dia de Sorte e jogos/placares de Loteca e Lotogol percorrem repository, domínio, detalhe, favorita, repetição e edição. A repetição volta a consultar as modalidades abertas e reproduz a escolha ou a confirmação de conversão entre concurso Normal e Especial. “Minhas compras” recupera os meses antes da lista, busca meios de pagamento e situações apenas quando o filtro é aberto, invalida a situação quando o meio muda e pagina de 10 em 10 registros com as mesmas chaves de query do aplicativo anterior.

O detalhe de compra usa novamente `GET compras/{id}/apostas` como fonte completa da compra e das apostas. O mapeamento não descarta os identificadores de situação e meio de pagamento, E2E do Pix, indicador de cota de bolão ou Surpresinha. Com isso, a tela Compose reproduz a cor do status, o cabeçalho e os ícones do toolbar, o Pix pendente com contador e cópia, os filtros locais combinados, a regra que esconde Favoritar para bolão, o salvamento de carrinho favorito e os alertas de cancelamento, estorno e débito não autorizado.

A confirmação de pagamento normal preserva a semântica do legado: tanto a conclusão do cartão quanto a identificação do Pix mostram “Estamos processando sua compra.”, os quatro avisos originais e a ampulheta circular; “Acompanhe suas compras” abre sempre `tickets/purchases`. Ao tentar sair do Pix ainda válido, o alerta reproduz todas as consequências e a pergunta do texto histórico, mantendo “Sim” na tela e “Voltar ao início” como saída.

A tela de escolha da forma de pagamento também preserva a geometria específica do legado: cards com o inset visual equivalente ao `CardView`, CTA Pix de 52 dp, cabeçalho de processador de 92 dp, estado vazio de 190 dp e logos Mercado Pago/RecargaPay nas respectivas dimensões intrínsecas. O cenário mock `PAYMENT_NO_SAVED_CARDS` permite repetir a comparação sem depender dos cartões do usuário; a captura homologável atual registra SSIM `0.921023` em `build/visual-audit/legacy-compose-payment-content-v11/report.tsv`.

## Assets do legado

Os conjuntos históricos de imagens raster, fontes CAIXA/Futura/Arial, Lottie, JSONs de tutorial e modelos HTML de comprovante permanecem disponíveis. Uma auditoria por SHA-256 confirmou a preservação byte a byte de todos os assets binários de runtime do checkout legado; ficaram de fora somente configuração Google e artes de publicação da loja, que não são recursos de tela. A presença do arquivo não é tratada como prova de que ele já esteja aplicado corretamente em cada tela; esse uso é verificado no inventário visual. Os antigos drawables/font families XML não retornam; sua função é substituída por Compose/CaixaDS e o gate impede XML de UI. Os quatro certificados `.cer` antigos permanecem removidos: a pinagem moderna usa pins SPKI com manifesto assinado, cache validado e fallback local, conforme o runbook de segurança.

```bash
./gradlew modernCheck
```

## Toolchain

| Componente | Versão |
|---|---:|
| compileSdk / targetSdk | 37 |
| minSdk | 24 |
| Android Gradle Plugin | 9.2.1 |
| Gradle | 9.4.1 |
| Kotlin / Compose Compiler | 2.4.10 |
| Java | 17 |

## Inicialização e ícone

- Ícones PNG são usados nos Android antigos e o launcher usa ícone adaptativo a partir do Android 8.
- A pré-splash usa `androidx.core:core-splashscreen:1.2.0`, com o foreground oficial das Loterias e transição para a animação institucional em Compose/Lottie.
- `themes.xml` configura somente a janela criada pelo sistema antes do primeiro frame Compose; não é um layout e não instancia Views.
