# Logout SSO e sessão local — 07/09/2026

Revisão posterior: `LOCAL_SESSION_CLEANUP_AUDIT.md` integra a limpeza das tabelas legadas de identidade/biometria e flag de login antes de EndSession, em I/O e com retry explícito. O histórico abaixo descreve o escopo original; demais caches, processo, provedor e validação Android/flavors ainda não estão concluídos.

## Referência e decisão

No backup legado, `model/bo/AcessoBO.java`, método `iniciarLogoutSSO`, abre `URL_SSO_LOGOUT` pelo navegador e limpa dados locais de usuário/biometria. A versão Compose apenas limpava o access token. A integração descrita aqui fecha essa lacuna de abertura/retorno do navegador, mas **não homologa o provedor nem conclui a limpeza de todos os dados persistidos**.

Foi usada a operação `EndSessionRequest` do AppAuth 0.11.1, conferida no código-fonte do SDK, sem reaproveitar uma autorização normal para fazer logout. O protocolo [OpenID Connect RP-Initiated Logout](https://openid.net/specs/openid-connect-rpinitiated-1_0.html) recomenda `id_token_hint`, admite `client_id` e permite callback nativo previamente registrado. O `state` correlaciona solicitação e retorno; logout local pode ocorrer antes do redirecionamento. Retorno do navegador não é prova de revogação de todos os tokens/sessões.

## Responsabilidades

- `core:auth/SessionTokenStore`: mantém access token e ID token da mesma tentativa aceita. O logout extrai o ID token e invalida a tentativa atomicamente; um callback tardio não republica credenciais. Substituição direta/clear descartam o ID token. Access token nunca é usado como hint.
- `core:auth/EndSessionGateway`: porta para preparar, abrir, concluir ou descartar a solicitação. `EndSessionLaunch` é uma identidade opaca, sem Intent, URL ou credencial no estado Compose.
- `AppAuthAuthenticationGateway`: implementação das duas portas de autenticação/logout. Hilt fornece **a mesma instância singleton** para ambas. O hint é transitório, consumido na preparação ou descartado ao iniciar outro login. Repetir a limpeza local antes da preparação não perde o hint.
- `EndSessionRegistry`: guarda a requisição original até o primeiro lançamento e depois somente a identidade/state. Callback de handle antigo não consome uma nova solicitação. Retorno sem lançamento, state ausente/incorreto e replay não confirmam o encerramento. Cancelamento é um resultado distinto.
- `feature:account/LogoutViewModel`: encerra localmente antes de preparar o navegador, controla consumo único e apresenta falha local, cancelamento, resultado não confirmado ou simulação. Não restaura tokens, autoriza novamente nem repete a operação automaticamente.
- `LogoutOverlay`: launcher Compose mantido fora do NavHost. Usa loading e diálogos do CaixaDS, bloqueando a interação enquanto aguarda o navegador. `RESULT_OK` sozinho não é interpretado como sucesso; a porta confere o resultado AppAuth/state.
- `LoteriasApp`: conecta saída pelo menu, autossuspensão e reconhecimento da falha de verificação de bloqueio pós-login. Limpa seleções temporárias (compras, apostas, reedição, filtro/lotérica e bilhetes) e solicita remoção inclusiva da pilha do grafo antes de abrir o login sem autorização automática.

## Configuração por flavor

| Configuração | Origem/valor |
|---|---|
| Endpoint produtivo | `prd.SSO_LOGOUT_URL`: `https://login.caixa.gov.br/auth/realms/internet/protocol/openid-connect/logout` |
| Endpoint não produtivo | `tqs`/`automacao.SSO_LOGOUT_URL`: `https://logindes.caixa.gov.br/auth/realms/internet/protocol/openid-connect/logout` |
| Cliente | `SSO_CLIENT_ID`: `cli-mob-lce` |
| Retorno proposto | `SSO_REDIRECT_URI`: `br.gov.caixa.caixaloterias:/oauth2Callback`, injetado explicitamente como `postLogoutRedirectUri` |

O gateway exige endpoint HTTPS. A URL vem do BuildConfig, nunca de mensagem de erro/deep link ou de um payload de negócio. Não há discovery nem alteração de configuração remota nesta revisão. **O cadastro exato da URI como retorno de logout nos provedores PRD/TQS precisa ser confirmado na homologação.** A existência da URI de login não comprova que ela seja aceita como retorno de logout. Se o provedor recusar, a sessão local permanece encerrada e não há fallback que invente confirmação.

## Segurança e diferenças deliberadas

Não se registra o ID token, request ou callback nos logs da aplicação; o hint trafega somente no pedido OIDC ao provedor via SDK/navegador. Não se adiciona persistência de credenciais. O transporte e o estado interno do SDK/navegador ainda exigem auditoria Android própria; a pinagem do Retrofit não é uma prova de proteção do AppAuth.

Os avisos de cancelamento/falha externa são explícitos e distinguem sessão local e SSO. Seus textos são novos para não informar sucesso indevidamente; não são atribuídos ao legado. O visual reutiliza os componentes institucionais, mas capturas pareadas/aceite desses estados ainda são necessários.

Em mock, a ação limpa a sessão local e mostra que nenhuma sessão real do navegador foi encerrada. Não abre o provedor nem simula uma confirmação SSO verdadeira.

## Validação e limites

Resultado da execução isolada, em `build/verification/sso-end-session-20260907/`:

- **536 testes JVM aprovados**: 383 de domínio/estado/regras, 50 de rede, 96 de Data, quatro de preferências e três de navegação mock. São 16 testes novos nesta revisão (três de store, cinco de registro EndSession e oito de ViewModel).
- Cobertura de linhas: logout/estado 50/50 (100%); store/registros de autenticação 59/59 (100%); lógica/estado de login 78/79 (98,73%); domínio pós-login 19/19; repositories de conta selecionados 84/84; DTOs selecionados 17/17. Os relatórios XML têm escopo explícito e não certificam a aplicação inteira ou o bridge AppAuth.
- Compilação isolada dos fontes atuais de rede, CaixaDS, gateway AppAuth, apresentação afetada e composition root/host concluída. Oito testes instrumentados de conta (cinco de retorno e três de status de logout) compilaram, **sem execução Android**. Isso não executa Hilt/KSP, resource merger, D8/R8 ou empacotamento do APK.
- `git diff --check` sem erros; continuam apenas avisos preexistentes de normalização CRLF em arquivos Gradle. A compilação informa deprecações de `LocalClipboardManager` na tela de pagamento e da API v1 de teste Compose, sem erro de compilação.

Os testes unitários de `SessionTokenStoreTest`, `EndSessionRegistryTest` e `LogoutViewModelTest` cobrem correlação, consumo único, disputa logout/publicação, ID token, ordem local→preparação, falhas, cancelamento, limpeza e simulação. Os testes instrumentados de conta verificam delegação após o aviso de bloqueio e os componentes de status de logout; compilação isolada não equivale a execução Android.

Comandos completos previstos:

```sh
./gradlew :core:auth:testDebugUnitTest :feature:account:testDebugUnitTest --offline
./gradlew :core:auth:jacocoDebugCoverageVerification :feature:account:jacocoDebugCoverageVerification
./gradlew :feature:account:connectedDebugAndroidTest
```

O primeiro comando foi tentado e falhou antes das tarefas por falta de permissão no lock `/Users/ubiracysantos/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`. Nenhum lock/permissão foi contornado. Os gates são parciais e não comprovam cobertura global acima de 95%.

Ainda pendem:

- Execução do navegador real em PRD/TQS, configuração do retorno, ausência de navegador, cancelamento, erro do provedor, rotação e recomposição durante a operação.
- Validação da pilha Android: Voltar não deve reabrir tela autenticada nem permitir ação com dados da sessão anterior.
- Morte do processo: o callback sem registro local não confirma logout e não restaura tokens. Retomada persistente e comunicação desse resultado após recriação ainda precisam de implementação/homologação.
- Limpeza coordenada de caches persistidos, biometria/credenciais legadas e tratamento de respostas de negócio em voo; revogação de tokens não foi implementada.
- Hilt/KSP, manifest merger, R8/ProGuard, todos os flavors Debug/Release, cobertura global e matriz visual completa.

Esta revisão não conclui a migração integral nem certifica conformidade OWASP.
