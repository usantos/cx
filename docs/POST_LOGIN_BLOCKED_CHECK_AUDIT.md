# Verificação pós-login de apostador bloqueado — 07/09/2026

Revisão posterior: o reconhecimento da falha inicial agora delega ao coordenador compartilhado de logout, depois da limpeza local. O host abre EndSession pelo endpoint do flavor, sem reiniciar autorização; detalhes e pendências de homologação em `SSO_END_SESSION_AUDIT.md`.

## Lacuna encontrada

O legado executa, em `TokenActivity`, troca de token, `DadosUsuarioBO.getVerificaUsuarioBloqueado` e somente depois `verificaCadastro`. O primeiro serviço usa `GET usuarios/verificar-apostador-bloqueado`. No app Compose, essa operação existia no contrato Retrofit, mas não possuía consumidor; o login chamava diretamente a verificação de cadastro.

Se a consulta de bloqueio falha, o legado apresenta “Atenção” / “Não foi possível concluir esta operação. Por favor, tente novamente mais tarde.” / “Entendi”, sem avançar para a consulta de cadastro. O reconhecimento inicia logout e volta ao login. Referências: `TokenActivity.java`, `DadosUsuarioBO.java`, `LoginSSOModel.java` e a string `nao_concluiu_operacao_nova` do checkout legado.

## Integração atual

- Domain: `BlockedAccountVerificationRepository`, `VerifyPostLoginAccessUseCase` e `VerifyPostLoginAccess` representam a sequência. O resultado distingue verificação concluída, falha na primeira consulta e falha na consulta de cadastro. Cancelamento se propaga, sem executar a etapa seguinte.
- Data: `RemoteBlockedAccountVerificationRepository` usa o `LegacyBackendApiProvider` existente e atualizado pelo bootstrap/pins. Não cria cliente alternativo nem faz fallback. Payload ausente é permitido nesse endpoint; metadados de erro/redirect não são considerados sucesso.
- Network: a mesma operação passou a retornar `BlockedAccountVerificationEnvelopeDto`, preservando `codigo` e `redirect`, sem imprimir seus valores no `toString`. Não foi criada outra URL ou interface Retrofit.
- DI: Hilt fornece a porta e o caso de uso ao `AppContainer`. Somente o login recebe `verifyPostLoginAccess`; Home/carrinho/entrada em apostas continuam com `verifyAccountAccess`, sem adicionar essa consulta a cada refresh comum.
- Presentation: `LoginViewModel` mantém o loading até concluir as duas verificações. Na falha inicial, não publica autenticação concluída e bloqueia novos inícios/callbacks até “Entendi”. O alerta usa o diálogo CaixaDS. Reconhecer limpa as credenciais locais por `AuthenticationGateway.logout`, preserva a preferência de biometria e não inicia navegador/requisição automaticamente. Se a limpeza falhar, o aviso permanece bloqueante para nova tentativa.

Foi corrigido também o fallback de `RemoteAccountRepository`: cadastro ausente sem redirect e redirect desconhecido não viram mais acesso à Home. Os redirects de cadastro/termos reconhecidos podem continuar sem payload; o caminho normal exige o cadastro. Essa proteção **não** significa que todos os campos e metadados pós-login já tenham sido auditados.

## Mocks e testes

`POST_LOGIN_BLOCKED_CHECK_HTTP_ERROR` retorna HTTP 403 somente no GET de bloqueio. `POST_LOGIN_BLOCKED_CHECK_REDIRECT` retorna 200 com `redirect=login` somente nesse GET. A verificação de cadastro e os demais endpoints continuam disponíveis. São fixtures de backend; não há atalho de startup que simule toda a autorização externa para esses cenários. O teste instrumentado usa um `AuthenticationGateway` falso local para exercitar o alerta sem navegador ou sessão real.

Execução isolada em `build/verification/post-login-blocked-check-20260907/`:

| Escopo de testes executado | Quantidade |
|---|---:|
| Estados/domínio/regras selecionados | 357 |
| Rede e matriz de fixtures | 50 |
| Data, incluindo os testes existentes de repositories | 94 |
| Preferências | 4 |
| Navegação mock do host | 3 |
| Total | 508 |

Todos aprovados. Compilação dos fontes atuais de rede, CaixaDS, fontes selecionados de features, host e composition root concluída. O teste instrumentado do alerta e os demais testes de retorno OAuth foram compilados, **não executados no Android**.

Coberturas parciais de linhas, incluindo classes internas/corrotinas dos escopos indicados:

- Caso de uso/resultados pós-login: 12/12 (100%).
- `LoginViewModel`/estado/mapeamento: 72/73 (98,63%).
- Repositories de conta e consulta de bloqueio: 77/77 (100%).
- DTO da consulta de bloqueio: 5/5 (100%).

O gate Gradle de conta passou a exigir entradas não vazias, mínimo de 0,9501 e incluir classes internas dos outros ViewModels já selecionados. **O gate integral não foi executado**, assim como não foi comprovada cobertura global do app. `./gradlew :core:domain:test :core:network:test :core:data:test :feature:account:testDebugUnitTest --offline` continua impedido antes das tarefas pelo lock da distribuição fora da área permitida. A compilação isolada não executa Hilt/KSP, recursos, APK, R8 ou flavors.

ProGuard: o DTO é coberto pela regra existente de `**Dto`/campos `SerializedName`, e `LegacyBackendApi` permanece retida. Isso é conferência estática, não aprovação de Release minificado.

## Pendências explícitas

- O gateway atual limpa somente a sessão **local**. Logout SSO no navegador, revogação e limpeza coordenada de caches não estão homologados como equivalentes ao `iniciarLogoutSSO` legado. O alerta não deve ser anunciado como conclusão desse logout externo.
- Revisão posterior: `POST_LOGIN_PARTIAL_BLOCK_AUDIT.md` preserva `situacaoApostador` e restaura o aviso de bloqueio parcial, sem homologar toda a sessão. Estado de notificações/carrinho da resposta e envio/migração de apostas locais antigas continuam pendentes; não são resolvidos pela presença do GET.
- A revisão posterior `AUTH_ATTEMPT_LIFECYCLE_AUDIT.md` impede que uma troca antiga republique o token após logout/cancelamento. Ainda falta revisar a validade global da sessão durante falha das verificações, deep links, retomada e morte do processo. Os testes não certificam a segurança de toda a sessão.
- Retorno contextual a compra/resgate, callbacks Android, screenshots pareados, todos os flavors e Release permanecem pendentes.

Esta revisão corrige uma operação ausente no fluxo e dois casos de liberação indevida de Home; não conclui a migração nem certifica conformidade OWASP integral.
