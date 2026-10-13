# Identidade vinculada à sessão para transferência do carrinho

## Estado da implementação

O gateway AppAuth agora verifica a identidade do ID token antes de publicar a sessão. O
`SessionTokenStore` publica token/identidade juntos e oferece uma referência de sessão que não
pode ser recriada apenas copiando seu hash. Há provider Hilt de `AuthenticatedSessionIdentityProvider`
em Domain, servido pelo mesmo singleton de tokens. Não cria outra sessão ou outro armazenamento.

**O envio do carrinho ainda não está conectado.** Esta revisão fornece a identidade exigida pelo
recibo implementado anteriormente; transporte, confirmação do envelope/lote, retomadas e telas de
revisão continuam pendentes. Nenhuma aposta foi transmitida ou apagada nesta revisão.

## Evidência que motivou a alteração

Nos fontes locais de AppAuth **0.11.1**, `IdToken.validate` compara o issuer com o esperado somente
quando existe `configuration.discoveryDoc`. O aplicativo usa configuração manual de endpoints.
O SDK também permite correspondência por `azp` mesmo sem o cliente em `aud`; isso não é suficiente
para a nova identidade. `AuthorizationService` executa a validação antes do callback, e
`DefaultConnectionBuilder` exige HTTPS e desativa redirects da conexão.

A especificação exige issuer exato, cliente presente na audiência, validade temporal e nonce
correspondente quando enviado. O subject é limitado a 255 caracteres ASCII. No Authorization Code
Flow, o token recebido diretamente do Token Endpoint pode usar a autenticação TLS do servidor
no lugar da verificação de assinatura. Essa exceção não vale para tokens vindos do navegador,
Intent ou cache. Fonte: [OpenID Connect Core, validação do ID token](https://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation)
e [claims do ID token](https://openid.net/specs/openid-connect-core-1_0.html#IDToken).

## Validação adicional aplicada

`TokenEndpointIdentity` é interno a auth e é chamado somente sobre `TokenResponse.idToken`
retornado por `performTokenRequest`. Não é um verificador geral de JWT nem valida assinatura RSA.
O token passado pelo Intent não é usado para construir a identidade. O fluxo continua vinculado
ao request/state/nonce/PKCE criados localmente, e o ticket da tentativa continua controlando publicação.

- Exige ID token; ausência não produz nova sessão de produção.
- Exige issuer exato da configuração CAIXA. Por padrão, deriva o realm do endpoint de token
  removendo o sufixo `/protocol/openid-connect/token`; verifica também os endpoints auth/token
  completos contra esse realm. Um caminho diferente não é aceito silenciosamente.
- Aceita apenas o perfil RS256 atual, sem `crit` ou `b64` não suportados. Não busca chaves em
  URLs fornecidas no header. Algoritmo/claims reais do provedor ainda precisam de homologação.
- Exige audiência contendo somente o cliente configurado; se `azp` está presente, exige o mesmo
  cliente. Nenhuma audiência adicional foi aprovada para essa integração.
- Confere nonce, subject não vazio/ASCII, expiração estritamente futura e emissão dentro de
  ±600 segundos, sem overflow. NumericDate é aceito como inteiro decimal não negativo.
- JSON estrito, claims/header sem nomes duplicados no objeto raiz, UTF-8 válido e token limitado
  a 64 KiB. Campos numéricos não aceitam expoentes gigantes, frações ou conversão de strings.
- Falhas retornam mensagem genérica sem causa/claims/token anexados. Isso não é auditoria de todos
  os logs internos do SDK ou do restante do app.

Essas restrições são explícitas; não se deve relaxá-las automaticamente para acomodar uma resposta
desconhecida. Tokens de provider com outro algoritmo/audiências necessitam validação e configuração
específica. O código não demonstra que PRD/TQS/automação já passaram por essa homologação.

## Chave persistente e referência transitória

O owner key usa SHA-256 sobre campos UTF-8 com comprimentos explícitos: versão do formato,
namespace do backend (`BuildConfig.API_BASE_URL`), issuer, client ID e subject. A fixture golden
fixa o formato v1. Não usa CPF, e-mail ou access token como identidade persistente. O hash é um
identificador pseudônimo, não anonimização nem assinatura de autorização.

O mesmo usuário/configuração produz o mesmo owner key entre logins, necessário para reconhecer
recibos locais. Subject, issuer, client ID ou backend distintos produzem outra chave. Alterar
normalização, namespace/URL ou formato requer plano de compatibilidade com recibos pendentes;
a configuração de todos os ambientes legados ainda não foi homologada.

`AuthenticatedSessionIdentity` é uma referência por identidade de objeto, não um `data class`:
construir outro objeto com o mesmo hash não cria uma sessão autorizada.

- `captureIdentity()` não libera referência durante nova tentativa de login.
- `isCurrent()` exige a referência exata e ausência de tentativa em andamento.
- `tokenFor(identity)` lê referência/token no mesmo estado atômico; não substitui token de outro login.
- Iniciar outro login invalida referências antigas. Cancelar pode preservar a sessão anterior,
  mas com nova referência; não reativa operações que haviam capturado a referência antiga.
- Publicação tardia e limpeza de tentativa antiga não substituem a sessão mais recente.
- Logout/clear/replace removem identidade. `replace` e commits internos sem owner key, usados em
  caminhos antigos/testes, não ganham automaticamente permissão para migrar carrinho.

Esses mecanismos não cancelam uma operação já recebida pelo backend. O futuro transporte precisa
vincular token à referência capturada e revalidá-la antes de aplicar respostas; não basta consultar
`isCurrent` e depois usar o `token()` genérico, pois um login pode mudar entre as duas leituras.

## Validação e limites

Rodada final isolada de 07/09/2026, artefatos em
`build/verification/authenticated-session-identity-20260907/`:

- **592 testes JVM aprovados**: 408 de lógica/estado, 52 de network, 100 de data, 29 de preferences
  e três de navegação mock. Foram adicionados 11 testes nesta revisão.
- Estado/registros de auth e parser selecionados: **135/135 linhas** (parser 63/63, store 37/37).
  Porta/referência de identidade em Domain: 3/3. São escopos parciais, não cobertura global.
- Compilação isolada dos fontes afetados, gateway AppAuth, host/composition root, CaixaDS e
  **28 testes Android** existentes; os testes Android apenas compilaram, sem execução.
- A rodada final inclui a fixture golden do owner key e 100 disputas logout/publicação de
  identidade. O primeiro teste de surrogate foi corrigido para representar o escape JSON inválido,
  pois a geração anterior o convertia em `?` antes de formar o token de teste.
- `git diff --check` não encontrou erros, apenas avisos históricos de CRLF do Gradle.

Os testes novos cobrem JSON inválido/duplicado, UTF-8 e surrogate inválidos, algoritmo, audiences,
azp, issuer/configuração, nonce, limites temporais/numéricos, estabilidade/separação do hash,
referência forjada, login cancelado, callbacks antigos, substituição e disputa logout/publicação.

O gate de auth inclui o parser/estado e classes internas, mas não certifica o bridge AppAuth inteiro.
Domain continua sem Android/Gson/Retrofit; auth passou a depender da porta Domain. O provider Hilt
usa o `SessionTokenStore` já singleton, sem uma cópia de identidade fora do estado atômico.

O Gradle foi tentado com `./gradlew :core:auth:testDebugUnitTest :core:domain:test --offline`.
Falhou antes das tasks pelo lock do wrapper fora da área permitida (`Operation not permitted`).
Não houve contorno de permissões. Compilação isolada não executa Hilt/KSP, recursos, APK, R8,
retorno de navegador, todas as variantes ou validação TLS/SSO no dispositivo.

## Pendências para a migração integral

1. Conectar identidade e lote no pós-login com transporte transacional e validação do envelope/
   confirmação das apostas; tratar cadastro/termos, revisão e retomada sem reenvio cego.
2. Revalidar a sessão na aplicação da resposta e transportar a credencial vinculada, não outra
   credencial obtida posteriormente no store genérico.
3. Homologar issuer, audiência, algoritmo e campos reais de cada flavor com o provedor SSO.
4. Auditar callbacks, logs, transporte AppAuth/pinagem e morte do processo. A pinagem Retrofit
   existente não é automaticamente aplicada ao transporte AppAuth.
5. Revisar os consumidores antigos de `JwtCurrentUserIdentityProvider`, que ainda extraem dados
   de perfil do access token; esse provider **não** deve ser usado como owner key do recibo.
6. Executar testes Android, matriz visual completa, todos os flavors/Release e cobertura global
   acima de 95%. Esta revisão não afirma conformidade OWASP integral ou conclusão do app.
