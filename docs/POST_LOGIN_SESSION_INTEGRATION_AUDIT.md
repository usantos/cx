# Integração do cadastro pós-login com sessão capturada

## Implementação ativa

O provider Hilt de `VerifyPostLoginAccessUseCase` agora entrega o coordenador com
`AuthenticatedSessionIdentityProvider` e `PostLoginAccountRepository`. O repository implementado
em data consome `RetrofitPostLoginAccountDataSource` e, portanto, o cliente dedicado documentado
em `ACCOUNT_VERIFICATION_TRANSPORT_AUDIT.md`. Não há fallback para `VerifyAccountAccessUseCase`
genérico no pós-login.

O `LoginViewModel` continua recebendo a porta de domínio existente: nenhuma dependência de
Retrofit, token, DTO ou SQLite foi introduzida na apresentação. Consultas comuns de cadastro
no carrinho e na simulação continuam separadas, sem provocar transferência local implícita.

**A lista enviada ainda é vazia.** Esta integração corrige a sessão/transporte/classificação
do cadastro, não conclui o envio do carrinho legado, a confirmação do lote ou a limpeza seletiva.
Nenhum dado de usuário foi enviado/apagado nos testes; as respostas são fixtures em memória.

## Sequência e regras

1. Captura uma identidade ativa antes de qualquer chamada. Sem identidade ou com referência
   já invalidada, retorna falha de sessão e não consulta bloqueio/cadastro.
2. Executa o GET de apostador bloqueado na ordem legada. Antes de classificar o resultado,
   confere novamente a identidade capturada.
3. Se o GET pertence a uma sessão já substituída, não produz `BlockedAccountCheckFailed`:
   esse resultado autorizaria a apresentação a encerrar a sessão corrente. Retorna falha
   de sessão, sem solicitar logout da nova conta.
4. O POST usa a mesma referência pelo repository/data source e pelo binding `tokenFor(identity)`.
   O cliente não obtém o token genérico de um login posterior.
5. Após a consulta do cadastro, sucesso e falha passam pela revalidação da identidade. Uma
   resposta antiga não libera Home/cadastro/termos/autoavaliação nesse coordenador.
6. Cancelamento de corrotina continua propagado, sem classificação como falha negocial e
   sem execução automática da próxima etapa.
7. `RemotePostLoginAccountRepository` rejeita `codigo` não nulo (inclusive vazio/desconhecido)
   e lista não vazia de `errosValidacao`. HTTP 200 ou um redirect conhecido não eliminam
   esses metadados para fabricar acesso válido. Nenhum código de sucesso textual foi
   comprovado no contrato; sua eventual aceitação requer evidência/homologação.
8. `AccountAccessMapper` é compartilhado com o repository comum: preserva suspensão, limite,
   situações de conta e os três redirects conhecidos; Home/autoavaliação exigem payload.
   Redirect vazio, desconhecido ou URL externa não concede acesso.

O alerta de sessão usa a superfície de erro existente no login, com o texto
“A sessão foi encerrada ou substituída. Entre novamente.”. Esse texto adicional e as interações
de troca de conta ainda precisam de homologação visual/Android; não são uma captura literal do legado.

O GET de bloqueio continua usando o transporte geral. A revalidação descarta seu resultado se
a sessão mudar, mas não cancela uma requisição já transmitida. A etapa de publicação futura de
carrinho/notificação/recibo também precisará de coordenação atômica com a sessão; checagens
isoladas na rede não substituem essa coordenação.

## Evidência legada e pendências do carrinho

`controllers/TokenActivity.java:119` transmite a lista local, e seu ramo sem redirect atualiza
limite, situação, carrinho e última notificação, seguido de `crud.deleteAll()`. O contrato
`UsuarioLogadoResponse` consultado não tem recibo de transferência por aposta. O GsonRequest
desserializa e entrega a resposta, sem transformar esse envelope em confirmação individual.

Não foi copiado o `deleteAll()`. Ainda é obrigatório definir e validar a confirmação do lote
com os campos efetivamente retornados, preservar apostas alteradas/novas e tratar resposta
perdida, cadastros/termos e morte do processo. O payload atual ainda não restaura os campos de
carrinho, última notificação e pagamento não identificado. Não classificar essa lacuna como resolvida.

## Validação

Execução isolada em `build/verification/post-login-session-integration-20260907/`.

- **609 testes JVM aprovados**: 411 de lógica/estado, 60 de network, 106 de data,
  29 de preferences e três de navegação mock. Esta revisão acrescentou sete testes e atualizou
  a integração do cenário BLOQUEADO_PARCIAL para exercitar o cliente dedicado real com mock.
- Domínio selecionado: 37/37 linhas, das quais 22/22 no coordenador. Repositories/mapeador de
  cadastro selecionados: 92/92, incluindo 7/7 no novo repository e 18/18 no mapeador extraído.
  Transporte selecionado: 67/67; data source: 6/6. Não são cobertura global do aplicativo.
- Compilação isolada dos fontes afetados, rede, host/composition root e **28 testes Android**.
  Os testes Android apenas compilaram; não foram executados no dispositivo.
- O teste integrado verificou GET → POST, token/tag de sessão e corpo one-shot, além da situação
  parcialmente bloqueada chegando ao resultado de domínio. Nenhuma requisição saiu para backend real.
- `git diff --check` aprovado, com os avisos históricos de normalização CRLF.

O gate de Domain foi corrigido para fontes Java/Kotlin, relatório XML/HTML, recusa de dados/classes
ausentes e mínimo **0,9501**, alinhado à exigência de cobertura superior a 95%. Usa a versão
JaCoCo 0.8.13 já adotada nos módulos de rede/data. Inclui as classes de domínio e corpos de
corrotinas, sem filtros para esconder os novos casos de uso.

`./gradlew :core:domain:check :core:data:check :app:compileAutomacaoDebugKotlin --offline`
falhou antes das tarefas pelo lock `gradle-9.4.1-bin.zip.lck (Operation not permitted)`.
Logo, a configuração do gate e os relatórios isolados não comprovam aprovação integral de
Domain ou do app por Gradle, Hilt/KSP, D8/R8, APK, flavors/Release ou matriz visual.
