# Transporte de cadastro vinculado ao login

## Estado e limite da entrega

O transporte dedicado e o data source de pós-login estão implementados e providos por Hilt.
O cliente é configurado na inicialização e substituído junto com os demais clientes quando os
pins verificados são atualizados. Na revisão posterior `POST_LOGIN_SESSION_INTEGRATION_AUDIT.md`,
o coordenador passou a consumir esse caminho por `PostLoginAccountRepository`.
**O request continua com lista vazia**; o transporte não declara resolvida a transferência de
apostas locais, nem homologa telas ou regras completas. Os resultados abaixo são da rodada
anterior de implementação do transporte, não da integração posterior.

Nenhum backend real foi chamado e nenhuma aposta de usuário foi enviada ou excluída nos testes.

## Referência legada e separação de responsabilidades

`controllers/TokenActivity.java:119` lê as apostas locais, converte ID zero em null e chama
`postVerificarCadastro`. No retorno sem redirect, aplica limite/situação/carrinho/notificação
e chama `deleteAll()`. O DTO legado `UsuarioLogadoResponse` contém `codigo`, `mensagem`, `tipo`,
`redirect`, `errosValidacao` e `payload`; o envelope genérico moderno descartava parte disso.

- `core:network/AccountVerificationBackend.kt`: API Retrofit, credencial por requisição,
  envelope, erros HTTP, leitura limitada e provider do cliente.
- `core:data/RetrofitPostLoginAccountDataSource.kt`: mapeia o snapshot tipado para o request;
  não lê/apaga SQLite nem decide que o backend confirmou o lote.
- `AccountVerificationSessionBinding`: porta de composição do data source, implementada no
  `ApplicationModule` por callbacks para `tokenStore.tokenFor(identity)` e `isCurrent(identity)`.
  Ambos fecham sobre a mesma referência capturada, não sobre um token genérico futuro.
- `AppContainer`: configuração inicial e atualização de pins/cliente; nenhuma feature recebe
  Retrofit, tokens ou entidades SQLite.

A consulta comum de cadastro permanece separada. Uma atualização de limite/suspensão no carrinho
não deve disparar transferência destrutiva implícita de apostas locais.

## Garantias implementadas

1. POST em `logado/usuarios/verificar-cadastro` com `AccountVerificationRequestDto` tipado.
2. Contexto de sessão via `@Tag`, sem token no DTO/URL e sem `BearerTokenInterceptor` genérico.
   Tag ausente, sessão inválida ou credencial ausente impedem envio anônimo. Um header de
   autorização fornecido pelo chamador é substituído pelo token da sessão capturada.
3. Revalidação antes/depois de obter o token, no retorno do interceptor e antes/depois da leitura
   da resposta. Resposta de sessão invalidada é fechada e descartada.
4. Redirects HTTP e retries de conexão desativados; corpo one-shot pelo interceptor compartilhado
   impede follow-up automático que reenvie o POST. O cliente não executa retry explícito.
5. Apenas HTTP 200 retorna envelope. Outros status, inclusive 201/202/204/303, são exceções
   tipadas com status e metadados válidos disponíveis; nenhum `Location` é seguido.
6. `@Streaming`, limite de 1 MiB na leitura e JSON estrito/documento único. Corpo ausente, inválido,
   excedente ou `null` não vira envelope de sucesso. O limite ainda exige homologação com o tamanho
   máximo de carrinho suportado pelo serviço. Não há afirmação de validação completa do schema.
7. Código, mensagem, tipo, redirect e erros de validação são preservados; não são convertidos
   em aprovação. Um envelope 200 com erro/redirect/payload ausente continua exigindo decisão do
   coordenador. Os campos de carrinho/notificações dentro do payload ainda precisam ser modelados
   e aplicados: `AccountSessionDto` continua parcial.
8. Pinagem e logs/cURL seguem as opções compartilhadas do Gradle; o cURL mascara autorização
   e corpo. `toString()` do contexto, envelope e request não revela credenciais ou dados do usuário.
9. Provider não configurado falha sem fallback para cliente geral. A interface Retrofit foi
   adicionada ao ProGuard; DTOs seguem a regra existente de retenção de campos serializados.

Essas garantias não desfazem um POST que já chegou ao servidor antes de logout/cancelamento.
O recibo persistente e a reconciliação de resposta perdida continuam necessários. A checagem na
rede também não torna atômica a aplicação futura do resultado em estado/SQLite: o coordenador
precisa revalidar e serializar esse efeito. Não há garantia de entrega exatamente uma vez.

## Validação

Execução isolada em `build/verification/account-verification-transport-20260907/`.

- **602 testes JVM aprovados**: 408 de lógica/estado, 60 de network, 102 de data,
  29 de preferences e três de navegação mock. Dez testes foram acrescentados nesta revisão.
- Novo transporte/contexto/envelope/provider: 63/63 linhas; incluindo request DTO já existente,
  67/67. Data source e binding: 6/6. São escopos selecionados, não cobertura global do aplicativo.
- Compilação isolada dos fontes de rede, data sources selecionados, CaixaDS, host e composition
  root. Os **28 testes Android apenas compilaram**, sem execução no dispositivo.
- Testes exercitam metadados negociais, HTTP não 200, corpo ausente/inválido/excedente, sessão
  invalidada, fechamento de resposta, token ausente/tag ausente, troca de sessão, política do
  builder, one-shot, redaction do cURL, troca do provider e mapeamento do snapshot para o POST.
- Respostas HTTP são fixtures em memória. As asserções de política do OkHttp não substituem
  execução de follow-ups/rede TLS real no Android.
- `git diff --check` aprovado, com os avisos históricos de normalização CRLF nos arquivos Gradle.

O comando `./gradlew :core:network:test :core:data:test --offline` falhou antes das tarefas com
`gradle-9.4.1-bin.zip.lck (Operation not permitted)`. Não foi contornada a permissão. Compilação
isolada não valida Hilt/KSP, recursos, D8/R8, APK, todos os flavors/Release ou paridade visual.

## Próximas integrações obrigatórias

- Capturar a identidade no início do pós-login e preservar a ordem do check de bloqueio.
- Preparar recibo antes do envio; consumir o data source somente nesse fluxo e apenas uma vez.
- Mapear código/redirect/payload para acesso e confirmação inequívoca do lote; não usar HTTP 200
  isoladamente como autorização para limpeza.
- Restaurar carrinho/notificações retornados e tratar retomadas/contextos de cadastro/termos.
- Conectar confirmação/limpeza seletiva, revisão de resposta perdida/conflito e UI Compose.
- Homologar sessão real, TLS, cancelamento, SQLite/morte do processo, limites do contrato,
  comparações visuais, flavors/Release e cobertura global superior a 95%.
