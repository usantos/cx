# Rotação segura de pins TLS

## Decisão

Os valores distribuídos são pins SPKI no formato aceito pelo OkHttp (`sha256/<base64>`). O backend de negócio não publica nem controla esses pins.

O Firebase Remote Config é somente o canal de distribuição. Cada configuração é um manifesto versionado, com validade curta e assinatura ECDSA P-256 de, por padrão, dois entre três signatários offline. O aplicativo só aplica o conteúdo depois de validar o quórum com chaves públicas de configuração incorporadas no build.

```text
cerimônia offline (2 de 3) -> manifesto assinado -> Firebase Remote Config
                                                        |
                                                        v
app -> valida assinatura + versão + validade + host allowlist -> OkHttp CertificatePinner
 |                              |
 |                              +-> inválido: mantém o conjunto atual
 +-> Remote Config indisponível: cache assinado -> fallback local
```

Essa separação evita que o comprometimento isolado do projeto Firebase autorize novos pins. Remote Config não deve conter segredos: pins, chaves públicas de assinatura e identificadores Firebase são material público.

## Âncoras de confiança e limite da solução

É necessária uma publicação inicial do app contendo as chaves públicas dos signatários de configuração. Depois disso, os pins TLS podem ser rotacionados sem nova publicação.

A troca das próprias âncoras de assinatura continua exigindo atualização do app. Com quórum 2-de-3, a perda ou o comprometimento de uma chave de assinatura não interrompe a operação; o comprometimento de duas chaves exige resposta emergencial e uma nova versão do app. As chaves privadas nunca devem ficar no repositório, Firebase, Gradle, estação de desenvolvimento ou backend de negócio: use HSM/KMS e aprovação por pessoas distintas.

O mecanismo é inspirado nas propriedades de metadados assinados, expiração, versionamento e limiar de confiança do TUF; não é uma implementação completa do TUF.

## Configuração do build

As chaves públicas são SPKI DER codificadas em Base64URL sem padding. Configure somente material público:

```bash
./gradlew :app:assembleDebug \
  -PkeySetTrustedSigners='root-a=<SPKI_BASE64URL>,root-b=<SPKI_BASE64URL>,root-c=<SPKI_BASE64URL>' \
  -PkeySetSignatureThreshold=2
```

Sem signatários configurados, o aplicativo desabilita a atualização remota e só permite chamadas reais dos clientes Retrofit para hosts presentes no fallback local. O pipeline de produção deve fornecer os três signatários públicos e o limiar 2.

**Pendência operacional identificada em 07/09:** o fallback atual de `AppContainer` contém somente `mobileloterias.caixa.gov.br`. Ele não permite homologar TQS, automação, Mercado Pago ou RecargaPay sem material adicional de pinagem. Esses hosts devem constar de um manifesto assinado aprovado ou de um fallback homologado; não copie o pin de produção para eles nem adicione trust-all para liberar testes. Nenhum novo pin foi inventado nesta revisão.

Para exportar uma chave pública de configuração já criada em ambiente controlado:

```bash
openssl pkey -in signer-private.pem -pubout -outform DER \
  | openssl base64 -A | tr '+/' '-_' | tr -d '='
```

Esse comando é apenas para a cerimônia controlada. Não copie a chave privada para o projeto ou CI.

## Formato do Remote Config

Parâmetro: `tls_pinning_keyset_v1`.

O valor é um envelope JSON. `payload` contém o JSON compacto do conjunto de pins em Base64URL sem padding. Cada assinatura é calculada sobre os bytes decodificados de `payload`, usando `SHA256withECDSA`:

```json
{
  "payload": "<BASE64URL_DO_PAYLOAD>",
  "signatures": [
    { "keyId": "root-a", "value": "<ASSINATURA_BASE64URL>" },
    { "keyId": "root-b", "value": "<ASSINATURA_BASE64URL>" }
  ]
}
```

Conteúdo decodificado do `payload`:

```json
{
  "version": 42,
  "issuedAtEpochSeconds": 1788390000,
  "expiresAtEpochSeconds": 1796166000,
  "hosts": [
    {
      "hostname": "mobileloterias.caixa.gov.br",
      "pins": [
        "sha256/<PIN_ATUAL>",
        "sha256/<PIN_NOVO>"
      ]
    }
  ]
}
```

Regras aplicadas no dispositivo:

- pelo menos o quórum de assinaturas válidas e com `keyId` distinto;
- versão positiva e estritamente maior que a versão ativa nas atualizações, evitando rollback e substituição ambígua;
- emissão não futura, expiração obrigatória e validade máxima de 180 dias;
- somente hosts da allowlist incorporada no app;
- regras wildcard da allowlist, quando necessárias para hosts dinâmicos CAIXA, são explícitas (`*.caixa.gov.br`), aceitam apenas subdomínios e nunca o domínio raiz ou sufixos parecidos;
- no máximo 10 hosts e 5 pins SHA-256 distintos por host;
- manifesto limitado a 64 KiB;
- cache aceito somente após nova verificação criptográfica.

## Runbook de rotação

1. Gere a nova chave/certificado TLS e obtenha o pin SPKI.
2. Antes de trocar o certificado no servidor, publique uma versão maior contendo os pins atual e novo.
3. Aguarde a janela de propagação definida pela operação e monitore a adoção/erros.
4. Ative o novo certificado no endpoint.
5. Mantenha a sobreposição até que clientes tenham atualizado o manifesto.
6. Publique outra versão maior removendo o pin antigo somente após a janela segura.

Nunca publique diretamente um conjunto que contenha apenas um pin ainda não implantado. A assinatura protege autenticidade; o processo de sobreposição protege disponibilidade.

Para extrair o pin apresentado pelo endpoint e conferir os dados do certificado:

```bash
openssl s_client -connect mobileloterias.caixa.gov.br:443 \
  -servername mobileloterias.caixa.gov.br </dev/null 2>/dev/null \
  | openssl x509 -pubkey -noout \
  | openssl pkey -pubin -outform DER \
  | openssl dgst -sha256 -binary \
  | openssl base64 -A
```

O resultado deve ser verificado por um segundo canal e comparado ao artefato aprovado, não apenas copiado de uma conexão de rede potencialmente comprometida.

## Falhas e recuperação

| Situação | Comportamento |
|---|---|
| Remote Config offline/timeout | reavalia o conjunto ativo; se expirado, seleciona fallback local |
| JSON ou assinatura inválida | rejeita e mantém conjunto ativo somente enquanto válido |
| manifesto remoto expirado ou futuro | rejeita; conjunto ativo também é reavaliado antes do retorno |
| conjunto ativo expira durante o uso | repository passa ao fallback; clientes antigos bloqueiam novos envios até serem recriados |
| versão inferior | rejeita como rollback |
| cache adulterado | assinatura falha e o fallback local é usado |
| pins ativos não correspondem ao servidor | TLS falha fechado; não existe bypass/trust-all |
| host ausente do conjunto ativo ou com lista de pins vazia | `PinnedHostInterceptor` bloqueia antes de enviar dados da aplicação |
| redirecionamento para HTTP ou host sem pins | o interceptor de rede revalida o destino e bloqueia o envio |

Expiração e falha fechada podem afetar disponibilidade; alertas operacionais devem anteceder a expiração. Após limpar os dados ou reinstalar, o app volta ao fallback local e busca novamente o manifesto assinado.

### Validade durante a sessão e concorrência — 07/09/2026

`PublicKeySetRepository.current()` e todos os resultados de `refresh()` reavaliam a janela temporal pelo mesmo relógio do verificador. Ao vencer a validade, o conjunto remoto/cache deixa de ser retornado e o fallback passa a ser selecionado. A expiração também é verificada depois da gravação, evitando usar um manifesto que expirou enquanto o armazenamento estava ocupado.

O maior número de versão autenticado é mantido independentemente do conjunto em uso. Um cache assinado expirado pode fornecer **somente a versão** para esse limite, nunca pins autorizados: `authenticatedVersion` valida assinatura, formato, hosts e pins, mas não devolve o conjunto ao consumidor. Isso preserva o limite após recriar o repository com o mesmo cache. Versão inferior é rejeitada; a mesma versão não pode substituir o conteúdo nem estender a validade; uma versão maior válida pode restaurar o conjunto remoto. A ausência/limpeza do cache ou falha de persistência continua limitando a proteção entre processos — não há promessa de watermark durável após perda de armazenamento.

Atualizações remotas são serializadas por `Mutex`, impedindo que uma resposta concorrente antiga sobrescreva memória/cache mais novos. `CancellationException` é propagada na consulta e na gravação; cancelamento não é tratado como sucesso/offline e o mutex é liberado.

`PinnedHostInterceptor` verifica a expiração antes de cada envio nos níveis de aplicação/rede. Uma instância que já observou expiração não é reativada por atraso posterior do relógio. O certificado e os pins continuam sendo validados pelo TLS/OkHttp. Este mecanismo não cancela uma resposta cujo envio já ocorreu antes da expiração.

Validação desta revisão: **39 testes focados aprovados**, incluindo oito novos testes de repository e um de expiração do cliente. Fontes atuais de `network`/`key-management` compilados. Relatórios em `build/verification/pin-expiry-20260907/`: repository/verificador **118/119 linhas (99,16%)**, incluindo classes internas de corrotinas; factory/guard/adaptador temporal **123/126 (97,62%)**, com guard **13/13**. Esses escopos não comprovam cobertura global nem substituem os seis testes de sockets impedidos pelo sandbox na execução anterior.

### Preparação na entrada, retomada e nova tentativa

`PrepareNetworkSecurityUseCase` é uma porta em `core:domain`. O composition root fornece `AppContainer.networkSecurity`, implementado por `NetworkSecurityPreparation`: um mutex engloba **resolução e instalação dos clientes**, impedindo que uma chamada mais antiga instale clientes depois de uma atualização nova. Falhas são `AppResult.Failure`; cancelamento continua sendo propagado.

`FilaBraAppGate` aguarda essa preparação antes de iniciar o fluxo normal. Falha deixa de prender o app indefinidamente na splash: usa o componente de indisponibilidade com “Tentar novamente”. `StartupViewModel` recebe a porta explicitamente e executa segurança → versão → parâmetros em cada validação/nova tentativa. Falha de segurança impede consultas de negócio; cliques repetidos não iniciam validações paralelas. Cancelamento libera o estado CHECKING para uma nova tentativa.

`MainActivity` registra `NetworkSecurityLifecycleObserver` com seu `lifecycleScope` e dispatcher IO injetado. O primeiro START não duplica a preparação inicial. Depois de STOP, o próximo START solicita nova preparação, inclusive no retorno do navegador. A operação não navega nem recria a tela. Instâncias expiradas continuam falhando fechado até a instalação verificada; falha de atualização não desativa a pinagem. Destruir a Activity cancela seu escopo. A retomada não repete automaticamente POSTs ou pagamentos que falharam, evitando duplicações.

Os testes de `StartupViewModel`, do coordenador e do observador exercitam ordem, espera, chamadas repetidas, falha, cancelamento, retorno e escopo destruído. A compilação isolada inclui os fontes reais de `MainActivity` e `FilaBraGate`. Evidências atuais ficam em `build/verification/startup-security-recovery-20260907/`: **152 testes aprovados** (133 de domínio/estados/rotas/startup, 3 de envelope, 15 de repositories, 1 de preferências); Startup **29/29 linhas**, preparação **6/6** e observador **7/7**, incluindo classes internas nos escopos medidos. Ainda é necessário executar o APK e homologar sessão longa, expiração/rotação reais, retorno OAuth e recuperação de erro em todos os flavors. Não há certificação de paridade visual ou segurança integral somente com esses testes JVM.

## Retrofit, logs e cURL

Os clientes transacionais de compra e resgate preservam a mesma configuração de pinagem, autenticação, logs e mocks da fábrica comum. Ambos desabilitam redirects/retries de conexão e usam corpo não reutilizável para evitar follow-ups automáticos do POST, inclusive em HTTP 503 com `Retry-After: 0`. O provider dedicado de resgate é substituído na atualização dos pins, sem repetir a transação. Testes, escopo e limitações de runtime estão em [Transporte transacional](PRIZE_TRANSACTION_TRANSPORT_AUDIT.md). Os números da auditoria de hosts abaixo são históricos, anteriores a essa inclusão.

### Cobertura de hosts — auditoria de 07/09/2026

Instalar apenas `CertificatePinner` não bloqueia um host sem entradas correspondentes: esse comportamento está explícito no [código oficial do OkHttp](https://github.com/square/okhttp/blob/master/okhttp/src/commonJvmAndroid/kotlin/okhttp3/CertificatePinner.kt). O teste local reproduz a diferença entre host conhecido com certificado incompatível (falha) e host desconhecido no pinner isolado (aceito sem verificação adicional).

Os cinco contratos de `NetworkClientFactory` agora instalam `PinnedHostInterceptor` nos níveis de aplicação e rede quando recebem `PublicKeySet`. A checagem exige HTTPS e hostname exato com pins não vazios; a validação do certificado/hostname continua em TLS/`CertificatePinner`. Wildcard da allowlist do verificador não significa wildcard de pins. A instalação como interceptor de rede protege também os destinos de redirects. As mensagens não incluem URL, query, token ou corpo.

Mocks respondem antes do guard, sem conexão real. Com mock desabilitado, host não coberto falha antes de DNS, conforme teste. A factory mantém o parâmetro opcional para consumidores isolados; `AppContainer` fornece um conjunto não nulo aos cinco clientes. O mecanismo não cobre navegação do navegador nem transporte interno do AppAuth/Firebase, que precisam de auditoria própria.

Evidência: `build/verification/pinned-hosts-20260907/`. Fontes de network/key-management compilados; **30 testes focados aprovados**, incluindo cinco novos testes do guard. Cobertura: **116/119 linhas (97,48%)**, limitada a `NetworkClientFactory` (102/105), `PinnedHostInterceptor` (6/6) e `ServerTimeAdapterFactory` (8/8). A execução ampliada de 46 testes teve 40 aprovados e seis impedidos por `SocketException: Operation not permitted` ao abrir MockWebServer no sandbox. Os testes não foram removidos nem relaxados. Handshakes TLS, redirects reais, release e homologação dos provedores continuam pendentes; essa medição não certifica segurança OWASP nem cobertura global.

### Habilitação dos logs

Todas as APIs de negócio usam Retrofit e OkHttp. Em debug, os logs podem ser habilitados explicitamente:

```bash
./gradlew :app:assembleDebug -PnetworkLogging=true
```

O padrão é `false`, e builds `release` forçam `ENABLE_NETWORK_LOGGING=false` mesmo quando a propriedade é informada. O interceptor registra método, URL, headers e status, mas mascara credenciais e parâmetros sensíveis e nunca registra o corpo. Assim, o cURL representa a estrutura da requisição sem expor token, cookie, CPF, senha, cartão ou payload.

## Controles OWASP adotados

- TLS de plataforma mais pinagem SPKI; não há `TrustManager` permissivo ou `HostnameVerifier` customizado.
- pins primário e de backup com rotação antecipada.
- assinatura de configuração independente do canal de entrega.
- allowlist de hostname, limites de tamanho e validação estrita de entrada.
- anti-rollback, expiração e cache autenticado.
- segredo e PII redigidos dos logs; logs de rede impossíveis em release.
- testes unitários e gate Jacoco superior a 95% nos componentes de segurança.

Referências: [OWASP MASVS-NETWORK](https://mas.owasp.org/MASVS/08-MASVS-NETWORK/), [Android Network Security Configuration](https://developer.android.com/privacy-and-security/security-config), [Android SSL security](https://developer.android.com/privacy-and-security/security-ssl), [Firebase Remote Config](https://firebase.google.com/docs/remote-config/) e [TUF — survivable key compromise](https://theupdateframework.io/papers/survivable-key-compromise-ccs2010.pdf).
