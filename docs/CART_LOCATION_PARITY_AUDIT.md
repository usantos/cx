# Localização no pré-pagamento — 07/09/2026

Status: regra, mensagens, ações Android e ordem de continuação implementadas; **comportamento no APK e equivalência integral ainda não homologados**.

## Legado confrontado

`CarrinhoActivity.onClickAvancaPagamento`, `trataPermissao`, `onRequestPermissionsResult`, `showAlertLocalizacao`, `onFluxoLocalizacaoNoBrasil`; `LocalizacaoUtils`; `SharedPreferencesUtils`; recursos `posso_consultar_sua_localizacao` e `alerta_precisa_estar_no_brasil`.

A sequência é: cadastro/suspensão → atualizar cotas vencidas → localização → compra de mesmo valor em 24h → confirmação de login ou validação do carrinho → pagamento. A localização das lotéricas usa outra tela; não foi reutilizada no carrinho.

## Implementado

- `CheckoutLocationSource` é a porta do domínio para acesso à plataforma. `CheckCheckoutLocation` prioriza provedor desativado, exige ambas as permissões (precisa e aproximada), consulta histórico de solicitações e só libera país BR. Ausência de país e falhas não autorizam pagamento. `RecordCheckoutLocationRequest` grava a tentativa por porta injetada. Não há Android/Retrofit/Compose no domínio.
- `AndroidCheckoutLocationSource`, fornecido por Hilt, usa os providers do Android, permissões e Geocoder. Preserva o arquivo/chave `CONT_PERMISSION_LOCATION` e o limiar legado de duas solicitações. A consulta usa a última posição de melhor precisão, como no legado; sem posição em cache solicita uma localização pontual com limite de 15 segundos e cancelamento, sem listeners permanentes. Coordenadas não são registradas em logs, persistidas nem enviadas a um endpoint novo.
- `CartViewModel` consulta localização depois de atualizar cotas e antes de consultar compras recentes, confirmar login ou validar pagamento. As dependências são obrigatórias. Conta suspensa/cadastro pendente continuam interrompendo antes da localização.
- Permissão ausente, configurações de permissão necessárias ou GPS desativado exibem o texto literal “Podemos consultar a sua localização?”, com Sim/Não. Sim solicita permissões ou abre as configurações correspondentes. Não mostra o alerta de território nacional.
- Fora do Brasil, posição/serviço indisponível, recusa ou falha de abertura do Android mostram o alerta literal que informa a preservação do carrinho. Entendi retorna à Home com limpeza da pilha acima dela. Nenhuma aposta é excluída por essa ação.
- `CartLocationDialogs` usa `CaixaDSLegacyDialog`, não cancelável por toque externo/Voltar como no legado, e launchers de resultado. A ação é consumida antes de abrir a UI Android. Concessão e retorno das configurações reconsultam o país; recusa definitiva abre as configurações do aplicativo. Resultados duplicados ou de outra etapa são ignorados. Se o carrinho ficar vazio durante a ida às configurações, não avança.
- Não há liberação simulada implícita: o provider da aplicação usa a plataforma inclusive quando o backend está em modo mock. Os testes de unidade injetam resultados explicitamente; cenários nativos/mocks de permissão ainda precisam de integração no catálogo.

Correções de defeitos da implementação antiga: não reutilizamos a divergência entre o request code 123 e a constante 400, a interpretação invertida de `shouldShowRequestPermissionRationale` ou listeners de localização sem remoção. O contador histórico é mantido para a decisão inicial; o resultado atual do Android identifica a recusa definitiva após o pedido. Permissão apenas aproximada não foi silenciosamente transformada em autorização, pois o legado exige as duas permissões.

## Evidências

Em `build/verification/cart-checkout-location-20260907/`:

- 281 testes JVM aprovados (259 de estados/domínio/regras/rotas, 3 de envelope, 15 de repositories remotos e 4 de persistência com stores em memória).
- Testes verificam ordem das operações, precedência de provedor/permissão, contador, país BR/externo/ausente, falha e cancelamento, consentimento, lançamento único, concessão, recusa temporária/definitiva, retorno sem alterar configurações, falha de abertura, preservação e esvaziamento do carrinho.
- `CheckoutLocation.kt`: 21/21 linhas (100%). Carrinho: 544/549 (99,09%) no escopo de ViewModels/estados/regras, incluindo corrotinas. Não inclui renderização, launchers ou o adaptador Android.
- Compilação isolada concluída para fontes atuais do host, container, provider Hilt, adaptador Android e telas de integração. Isso não é um build Gradle/Hilt/KSP nem geração de APK.

## Pendências de homologação

1. Rodar no Android com permissão inicial, concessão precisa, concessão somente aproximada, recusa, recusa definitiva, revogação nas configurações e permissão temporária; validar rotação/processo recriado e callbacks de fabricantes.
2. Validar GPS/rede desativados, ausência/retorno de posição, timeout, cancelamento da localização pontual, Geocoder sem implementação/falha e países de fronteira. A posição em cache segue o legado e pode estar desatualizada; este gate local não é prova antifraude de presença física.
3. Capturas pareadas dos dois alertas, carregamento e retorno à Home; confirmar preservação do carrinho e limites após longos períodos nas configurações. Revalidar expiração de conta/cotas durante a espera e todos os caminhos diretos de pagamento, incluindo Rapidão.
4. Validar fluxo de login no navegador e respostas/redirects HTTP 303/polling de cartão/Pix conforme `CART_RECENT_PURCHASE_PARITY_AUDIT.md`.
5. Completar testes do SQLite nativo, mocks de localização, cobertura global, todos os flavors e Release/R8. O impedimento de execução Gradle por permissão no lock do wrapper continua documentado na revisão anterior; os testes nativos não foram executados nesta rodada.

A ordem do fluxo principal agora está representada no código, mas isso não certifica paridade visual/comportamental integral nem autoriza concluir a migração.
