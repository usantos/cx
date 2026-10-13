# Erros HTTP da compra — 07/09/2026

## Lacuna verificada e transporte

`PurchaseBackendClient` preservava 200/303, mas lançava `retrofit2.HttpException` para qualquer outro status sem interpretar os metadados. Assim, o repository não distinguia sessão expirada ou uma ordem de voltar ao carrinho de uma resposta desconhecida. O POST caía em revisão e a consulta em erro genérico.

Referências: `RedirectNetwork.checkRedirectCompraAsync`, `trataRedict`, `treatNegotialErros`, `trataErro500`, `MensagensNetwork`, `CompraUtil` e `RedirectEnum`, no checkout legado externo `simlo-app-android-legacy-app-backup-20260903`.

O transporte passa a lançar `PurchaseHttpException` com status fornecido pelo Retrofit e `PurchaseErrorEnvelopeDto` contendo apenas `codigo`, `redirect`, `mensagem` e `tipo`. O payload de erro não é interpretado como compra aceita nem usado como identidade de compra: ele pode ter formato diferente do DTO de processamento.

O decoder lê até 65.537 bytes para detectar o limite de 65.536, fecha o corpo mesmo em falha e descarta metadados inválidos/maiores que o limite. O status continua disponível quando o JSON não é utilizável. Falhas de leitura e cancelamentos são propagados; não há fallback de aprovação. Esse limite é do decoder, não do download: o Retrofit pode ter bufferizado a resposta antes.

Texto e causa da exceção não incluem corpo, mensagem do servidor, URL da requisição, cartão ou token. Não há log novo desses dados. Metadados tipados ficam disponíveis para decisões ainda pendentes, sem carregar a resposta HTTP inteira na exceção. Os clientes continuam sem seguir redirects HTTP nem repetir requisições automaticamente, preservando autenticação e pinagem.

## Encaminhamentos implementados

| Contexto | Decisão |
|---|---|
| Erro HTTP com `login`, `aceitar-termo-de-uso`, `cadastrar-apostador` ou `indisponibilidade` | Respectivo destino tipado, antes de avaliar o status |
| Erro HTTP com `carrinho` | `cart/current` |
| Erro HTTP com `carrinhos-favoritos` | `cart/favorite-carts` |
| HTTP 401 sem redirect | Login com autorização automática pelo fluxo existente |
| Redirect desconhecido, URL, caixa/espaços diferentes | Não usar como rota; não ignorar esse valor para aplicar o fallback 401 |
| 200/303 com `carrinho` ou `carrinhos-favoritos` | Continuam recusados; o legado usa esses destinos apenas no handler de outros erros HTTP |

Para 401 com JSON vazio/nulo/inválido, a autenticação pode ser solicitada pelo status. É uma decisão explícita de recuperação: o legado nem sempre seguia esse caminho quando a desserialização retornava `null`. Não há reenvio automático após autenticar.

As ações reutilizam o estado/pausa de `PURCHASE_REDIRECT_PARITY_AUDIT.md`: durante a navegação não se reenvia cartão nem se consulta/gera Pix; a retomada depende do lifecycle. Redirect de consulta preserva o ID acompanhado; voltar e tocar em retry consulta a compra existente. Os testes dos ViewModels agora percorrem também os dois destinos de carrinho.

As ações ainda não implementadas não viram sucesso: no POST exigem revisão; na consulta falham sem reenviar o cartão. Isso é estado intermediário, não reprodução completa dos alertas legados.

## Mocks e Release

- `PURCHASE_HTTP_UNAUTHORIZED`: 401 sem redirect apenas nas operações de registro/consulta.
- `PURCHASE_CART_REDIRECT`: 400 com `carrinho` nessas operações.
- `PURCHASE_FAVORITE_CARTS_REDIRECT`: 404 com `carrinhos-favoritos` nessas operações.

Todos abrem a confirmação de cartão, permitem preparação/tokenização e deixam os serviços dos destinos disponíveis. A cadeia Retrofit/repository/caso de uso é testada com cartão novo/salvo e ambos os processadores, sem sockets. O cenário fixo por build não simula uma sessão real de navegador nem a resolução da precondição no backend.

`PurchaseBackendApi` foi acrescentada à lista explícita de retenção do ProGuard, como as outras interfaces Retrofit. Os DTOs anotados já são abrangidos pela regra `core.network.**Dto`; os novos DTO/exception também foram incluídos no gate de cobertura de rede. Isso não aprova R8/Release sem executar o build minificado.

## Evidência executada

Artefatos em `build/verification/purchase-http-redirect-parity-20260907/`:

- 359 testes JVM aprovados: 291 de estados/domínio/rotas/regras, 22 de rede, 39 de data, 4 de preferências e 3 do host mock.
- Compilação isolada do host Compose e fontes alterados concluída. Permanece o aviso já existente de depreciação de `LocalClipboardManager`.
- Transporte de compras: 51/51 linhas (100%), incluindo `PurchaseErrorEnvelopeDto` e `PurchaseHttpException`; repository/data source: 213/216 (98,61%).
- Lógica de pagamento: 362/367 (98,64%); modelos/protocolo selecionados do domínio: 48/48 (100%); roteamento de redirects: 8/8 (100%). Os ViewModels foram exercitados para todos os seis destinos.
- Relatórios XML: `purchase-transport-coverage.xml`, `payment-repository-coverage.xml`, `feature-payment-coverage.xml`, `core-domain-payment-coverage.xml` e `apostas-coverage.xml`.

A suíte cobre prioridades de status/redirect, metadados independentes do payload, corpos inválidos/limítrofes, fechamento em falha, ausência de retry, exclusividade dos destinos de carrinho no handler de erro e a cadeia real de clientes com mocks. Cobertura medida por linhas nos escopos citados, não cobertura global nem homologação visual.

`./gradlew :core:network:test :core:data:test :feature:payment:testDebugUnitTest --offline` falhou antes das tarefas por `Operation not permitted` ao abrir `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`. Não houve bypass da restrição. A compilação isolada usa dependências locais em cache e não executa geração Android, Hilt/KSP, APK, R8 ou flavors. Nenhum build Release/captura pareada atual foi produzido nesta revisão.

## Próximas decisões obrigatórias, derivadas do legado

Revisão posterior: `PURCHASE_NOTICE_PARITY_AUDIT.md` implementa os alertas tipados 400/404/500, os botões e o diagnóstico temporal. A tabela abaixo registra a lacuna original; `018003`, reconciliação e homologação Android/visual continuam pendentes.

| Condição | Comportamento ainda a implementar/homologar |
|---|---|
| 400/404 e códigos `011010`, `011011`, `011012` | Mensagem de negócio; Apostar mais → Home com limpeza acima dela; Entendi apenas dispensa |
| Outros 400/404 | Mensagem, código quando presente e data/hora local `dd/MM/yyyy HH:mm:ss` |
| 500 e código `2067` | Mensagem específica de identificação do Mercado Pago |
| 500 com `tipo == "1"` | Mensagem recebida + código/data/hora |
| Demais 500 | Mensagem `erro_req` + código/data/hora |
| `018003` nos ramos correspondentes | Confirmação e obtenção do token de cadastro Mercado Pago antes de abrir o navegador |
| Demais erros/cancelamento/reentrada | Revisão/reconciliação e recuperação do acompanhamento, incluindo morte do processo |

`018003` exige revisão específica: `enviaRegistrarMp` faz cast para `JogosConfirmadosPremioMercadoPagoActivity`, mesmo sendo acessível pelo handler de compra. Reproduzir esse cast no cartão seria defeito, não paridade. O fluxo precisa de contrato de domínio/DI próprio para cadastro externo, com consentimento e sem tokens em logs.

Também faltam o pré-check de conectividade do login, retornos completos de autenticação/termos/cadastro/indisponibilidade, screenshots pareados, testes Android/TLS, todos os flavors/Release e cobertura global superior a 95%. A migração integral permanece aberta.
