# Aviso de bloqueio parcial no pós-login — 07/09/2026

## Referência legada

`TokenActivity.verificaCadastro` armazena `payload.situacaoApostador` e chama `RedirectNetwork.trataBloqueioParcial`. Para `BLOQUEADO_PARCIAL`, o helper apresenta “Usuário habilitado apenas para consultas.” e encerra a tela técnica ao reconhecer. Não limpa a sessão. O retorno por refresh/biometria em `LoginFragment.onRefreshTokenListener` apresenta o mesmo aviso e segue para a Home após “Entendi”.

`SituacaoApostador` contém um objeto com campo JSON `enum`; `SituacaoApostadorEnum` define ATIVO, BLOQUEADO, BLOQUEADO_TOTAL e BLOQUEADO_PARCIAL. A comparação é pelo enum, não pela descrição ou pelo ID. O texto foi conferido em `usuario_bloqueio_parcial` do checkout legado.

## Alteração

O DTO moderno não lia a situação, e o domínio a descartava. `AccountSessionDto` agora lê `situacaoApostador` por `AccountStatusDto`; `RemoteAccountRepository` transfere a situação usada pelo comportamento legado para `AccountAccess.status`:

| Enum recebido | Domínio |
|---|---|
| ATIVO | ACTIVE |
| BLOQUEADO | BLOCKED |
| BLOQUEADO_TOTAL | TOTALLY_BLOCKED |
| BLOQUEADO_PARCIAL | PARTIALLY_BLOCKED |
| Valor não reconhecido | UNKNOWN |
| Campo/objeto/enum ausente | null, sem inferir ACTIVE |

`LoginViewModel` mantém uma decisão pendente de aviso somente para PARTIALLY_BLOCKED. Ainda não publica a conclusão da navegação, mas preserva o destino calculado de Home/cadastro/termos/autoavaliação. “Entendi” consome a decisão uma única vez e libera esse destino. Não há logout, nova troca de token ou repetição das consultas ao reconhecer; a preferência de biometria é preservada. Isso é diferente do erro da consulta de bloqueio, cujo reconhecimento limpa a sessão local.

Enquanto qualquer aviso pós-login está pendente, a tela mantém a superfície técnica e o loading institucional atrás do diálogo, em vez de voltar a desenhar os botões do formulário. No legado, `AlertDialogUtils.dismiss` ocorre em `TokenActivity.finish`; a manutenção dessa camada antes de reconhecer segue essa referência.

Na revisão dos consumidores de `SituacaoApostador` não foi encontrada regra local que esconda itens de carrinho baseada nesse enum. Não foram inventados filtros, botões ocultos ou novos critérios de autorização de compra. O campo foi preservado e o aviso restaurado; **isso não homologa os controles de autorização e os handlers de bloqueio de todas as operações do backend**. BLOQUEADO, BLOQUEADO_TOTAL e UNKNOWN não são reclassificados como ATIVO, mas ainda precisam de revisão no contexto dos demais fluxos.

## Mocks e validação

`ACCOUNT_PARTIALLY_BLOCKED` responde à verificação de cadastro com a situação aninhada legada. A consulta prévia de bloqueio continua respondendo normalmente, e os outros endpoints não mudam. A fixture é exercitada pelo caminho Retrofit → Data → caso de uso; não constitui um atalho de startup com navegador/SSO integralmente simulado.

Artefatos: `build/verification/post-login-partial-block-20260907/`.

- 512 testes JVM aprovados: 359 de estados/domínio/regras, 50 de rede/fixtures, 96 de Data, quatro de preferências e três de navegação mock.
- Testes novos verificam mapeamento dos enums e ausência/valor desconhecido, preservação com redirects, aviso nos quatro destinos pós-login, reconhecimento único, ausência de logout/repetição e distinção de conta ativa/sem situação.
- O teste Retrofit com mocks verifica a ordem exata `GET usuarios/verificar-apostador-bloqueado` → `POST logado/usuarios/verificar-cadastro`, preservando PARTIALLY_BLOCKED até o resultado do caso de uso, sem conexão real.
- Compilação isolada de fontes atuais de rede, CaixaDS e fontes selecionados de domínio, Data, features, host/DI concluída.
- O novo teste instrumentado verifica texto, ausência do formulário ao fundo e navegação somente após “Entendi”, sem logout. Os cinco testes instrumentados de retorno OAuth foram compilados, **não executados em dispositivo/emulador**.

Coberturas parciais de linhas: domínio pós-login/estado de conta 19/19; lógica/estado do login 77/78 (98,72%); repositories de conta/consulta de bloqueio 84/84; DTOs selecionados de sessão/situação/consulta de bloqueio 17/17. Esses números não representam a cobertura global solicitada nem aprovação do gate integral de conta.

O Gradle continua impedido de abrir o lock da distribuição antes das tarefas. Não houve bypass de permissões. A regra ProGuard existente para `**Dto` e `SerializedName` cobre o novo DTO, mas R8/Release não foram executados nesta revisão.

## Pendências

Capturas pareadas do aviso e das camadas de loading, combinação de bloqueio parcial com redirects, refresh/biometria real, retorno contextual, recriação/morte do processo, sessão global, autorização das operações, todos os flavors e Release continuam pendentes. A ordem de continuação moderna é testada no domínio/estado; a pilha de Activities da referência não foi considerada automaticamente equivalente ao grafo Compose. Não concluir a migração com base nesta revisão.
