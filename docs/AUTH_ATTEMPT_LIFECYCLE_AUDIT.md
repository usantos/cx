# Ciclo da tentativa de autenticação e logout local — 07/09/2026

Revisão posterior: `SSO_END_SESSION_AUDIT.md` integra a abertura/retorno EndSession ao host Compose e usa o ID token da sessão aceita. As pendências de implementação do launcher abaixo descrevem o estado desta auditoria histórica; homologação SSO, limpeza persistente, processo/Android e flavors continuam pendentes.

## Evidência e relação com o logout SSO

A revisão do logout identificou que `AcessoBO.iniciarLogoutSSO` no legado abre a URL SSO de logout no navegador e limpa registros locais. O app moderno possui `SSO_LOGOUT_URL` nos flavors, mas `AuthenticationGateway.logout` apenas limpa o token local. **O encerramento externo permanece pendente.** Não foi copiado o uso legado de `AuthorizationRequest` apontando para logout como se fosse autorização normal.

Antes de integrar o encerramento externo, foram corrigidos problemas no ciclo atual: `AppAuthAuthenticationGateway` reutilizava uma única `AuthorizationRequest` durante toda a vida do singleton e publicava tokens sem verificar se o usuário já havia saído. Também construía a troca a partir de `response.request`, desserializado do Intent.

Os fontes locais de AppAuth 0.11.1 foram conferidos: `AuthorizationRequest.Builder` gera state, nonce e verificador PKCE na construção; `AuthorizationResponse.createTokenExchangeRequest` usa configuração, client ID, redirect URI, nonce e verificador contidos em `response.request`. Essa leitura orientou a alteração; não é teste runtime do SDK ou do provedor SSO.

## Alteração

- Cada `createAuthorizationIntent` cria outro builder, com novos parâmetros gerados pelo AppAuth. Não há reutilização intencional de state/nonce/verificador entre tentativas.
- `AuthorizationAttemptRegistry` mantém a requisição criada localmente e aceita somente o state exato pendente. Consumir remove a referência uma vez. Callback inexistente, antigo, duplicado ou com state diferente não obtém a requisição de uma tentativa nova.
- O gateway cria `TokenRequest` usando os parâmetros da requisição local consumida e o código de autorização recebido. Não usa o objeto `response.request` como configuração/verificador da troca.
- `SessionTokenStore` associa a publicação do token à identidade da tentativa. Logout, substituição direta, cancelamento ou início de outra tentativa invalidam a anterior. A publicação usa compare-and-set; resposta tardia não restaura credenciais após logout.
- Cancelar uma tentativa não encerra uma sessão anterior já estabelecida. Limpar uma tentativa antiga é uma operação condicionada à identidade, sem cancelar uma tentativa mais nova ou apagar um token já publicado.
- O cancelamento da corrotina registra invalidação da tentativa, o callback verifica a continuidade ativa e o gateway limpa o ticket ao encerrar, inclusive em falha. Isso não cancela ou revoga necessariamente a operação HTTP já recebida pelo servidor.
- O contrato de autenticação passou a exigir `cancelAuthorization`; cancelamento do navegador e falha ao abri-lo chamam essa operação no ViewModel. Nenhum novo login é iniciado automaticamente.

As estruturas auxiliares não carregam credenciais no `toString`; `PendingAuthorization` é explicitamente redigido. O estado continua em memória. Não foram adicionadas persistência de código/verificador ou novas permissões.

## Testes e compilação

Artefatos em `build/verification/auth-attempt-lifecycle-20260907/`:

- 520 testes JVM aprovados: 367 de estados/domínio/regras, 50 de rede, 96 de Data, quatro de preferências e três de navegação mock.
- Oito testes de store/registro cobrem consumo único, state ausente/incorreto, substituição, logout antes/depois do consumo, cancelamento, token vazio, publicação repetida, limpeza de tentativa antiga e disputa logout/publicação com threads.
- Cobertura parcial do store/registro/tickets: 37/37 linhas (100%). Lógica/estado do login: 78/79 (98,73%). **Não representa cobertura de todo `core:auth`, do bridge AppAuth ou global do app.**
- Compilação isolada do gateway AppAuth atual, fontes de estado, features afetadas e host/composition root concluída. Os cinco testes instrumentados existentes de retorno OAuth também compilaram; não foram executados no Android.
- O gate Gradle de estado de autenticação inclui as novas estruturas e classes internas, exige entradas não vazias e mínimo de 0,9501. Sua execução integral continua pendente. `./gradlew :core:auth:testDebugUnitTest :feature:account:testDebugUnitTest --offline` falhou antes das tarefas pelo lock da distribuição fora da área permitida.

## Pendências

- End-session/logout no navegador, tratamento do retorno e falha/cancelamento externo, revogação e limpeza coordenada de caches continuam pendentes. A correção de concorrência não encerra cookies/sessões no provedor.
- Retomada segura da mesma tentativa após morte do processo não foi implementada. Sem a requisição local, o callback é rejeitado; não se recupera o verificador/configuração a partir do Intent como fallback. É uma proteção temporária, não paridade de recuperação concluída.
- Ainda falta validar o protocolo AppAuth em dispositivo, callback real, PKCE/nonce no provedor, TLS/pins do transporte AppAuth, interrupções/ciclo de vida e sessão global durante validação de cadastro.
- APK, Hilt/KSP, R8, todos os flavors, matriz visual e cobertura global não foram homologados. Não declarar conformidade OWASP integral nem conclusão da migração.
