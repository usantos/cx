# Migração dos backends

## Garantia de paridade

O catálogo imutável [`LEGACY_BACKEND_BASELINE.tsv`](LEGACY_BACKEND_BASELINE.tsv) registra as 99 combinações de método HTTP e path efetivamente utilizadas pelo aplicativo legado. A tarefa Gradle `verifyLegacyBackendParity` compara esse catálogo com as interfaces Retrofit e falha quando uma operação desaparece. Ela faz parte de `modernCheck` e `releaseCheck`.

Os endpoints foram separados por responsabilidade e host:

- `LoteriasApi`: operações já migradas para DTOs e repositórios específicos de feature;
- `LegacyBackendApi`: operações preservadas durante a migração incremental dos fluxos restantes;
- `BetsHistoryApi`: consulta de apostas cuja URL base pode mudar pelos parâmetros de `/parametros/configuraveis-app`;
- `MercadoPagoApi`: tokenização e identificação do meio de pagamento no host do Mercado Pago;
- `RecargaPayApi`: cadastro/autorização de cartão no host da RecargaPay.

O contrato de compatibilidade não deve ser chamado diretamente por Composables ou ViewModels. Cada tela usa um caso de uso de Domain, implementado por um Repository em Data; à medida que uma feature é concluída, a resposta genérica é convertida para DTOs próprios dessa feature.

A presença no catálogo não prova consumo pela feature. A revisão `POST_LOGIN_BLOCKED_CHECK_AUDIT.md` identificou `GET usuarios/verificar-apostador-bloqueado` sem chamada no login e o integrou antes do POST de cadastro, por repository e caso de uso injetados. Ainda há validações comportamentais pendentes além da contagem de endpoints.

## Configuração dinâmica preservada

`AppParametersDto` lê do mesmo `/parametros/configuraveis-app` usado pelo legado:

- `microServico` e `urlBase`;
- `urlBaseNovaApiPRD` e `urlBaseNovaApiPLT`;
- `grupoCpfNovaApi`;
- `urlBaseCarrinhoPiloto` e `urlBaseCarrinhoProducao`;
- `urlBaseApostador`;
- `qtdeMinimaApostaBolaoFiltro` e `qtdeMaximaApostaBolaoFiltro`.

Qualquer URL recebida remotamente deve continuar passando por validação HTTPS, allowlist de host e pinagem SPKI assinada antes da criação de um cliente dinâmico.

### Roteamento de “Minhas apostas”

O `RetrofitTicketsRemoteDataSource` reproduz a decisão que existia entre SILCE, microserviço e Nova API. Em `prd`, a Nova API usa `urlBaseNovaApiPRD`; nas demais flavors, usa `urlBaseNovaApiPLT`. A ativação respeita `grupoCpfNovaApi`: `0` desativa, `100` ativa para todos e valores intermediários comparam os dois últimos dígitos do CPF. A URL da Nova API recebe o sufixo `/v1`.

Quando a Nova API não é selecionada, `microServico` habilita `urlBase`; em produção somente bases terminadas em `/v1` ou `/v2` são aceitas. URL ausente, HTTP, host sem pin assinado ou configuração inválida faz fallback fechado para o endpoint SILCE já pinado. A consulta dinâmica preserva ainda o nome histórico `comboApostas`, e o detalhe usa `{id}/recuperar` somente quando a rota ativa é a Nova API.

## Release e ofuscação

`prdRelease`, `tqsRelease` e `automacaoRelease` usam R8 full mode, `minifyEnabled`, `shrinkResources` e `proguard-android-optimize.txt`. As regras do aplicativo preservam:

- interfaces e anotações Retrofit;
- campos Gson anotados com `SerializedName`;
- entry points Hilt;
- estado AppAuth;
- Firebase Remote Config/Messaging;
- classes públicas do AAR FilaBRA, que não contém regras consumer próprias;
- métodos `JavascriptInterface` e linhas de stack trace.

Para assinatura local, mantenha o keystore em `~/Caixakeystore` e um arquivo `~/certificado.properties` fora do repositório:

```properties
KEYSTORE_PASSWORD=...
KEY_ALIAS=...
KEY_PASSWORD=...
```

Sem esses arquivos, o Gradle ainda gera um APK release minificado não assinado para validar o R8. A assinatura de produção nunca é armazenada no repositório.

Comandos:

```bash
./gradlew verifyLegacyBackendParity
./gradlew :app:assemblePrdRelease
./gradlew releaseCheck
```
