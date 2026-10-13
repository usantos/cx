# Recuperação da limpeza local na entrada — 07/09/2026

## Problema e escopo

A revisão `LOCAL_SESSION_CLEANUP_AUDIT.md` bloqueava a continuação enquanto `LogoutViewModel` estivesse vivo, mas não deixava um registro durável de limpeza incompleta. Após morte do processo, uma exclusão/gravação interrompida não era retomada automaticamente. Esta revisão adiciona o registro e o gate de entrada; **a interrupção real do processo e a persistência Android ainda precisam de homologação**.

## Protocolo local

`LegacyLocalSessionDataRepository` serve às portas Domain `LocalSessionDataRepository` e `LocalSessionCleanupRecoveryRepository`. Hilt fornece a mesma instância singleton para ambas, e um `Mutex` serializa logout/recuperação no processo.

1. Antes de alterar flag de login ou tabelas, grava `SESSION_CLEANUP_STATE.pending = true`, exigindo retorno positivo de `SharedPreferences.commit()`.
2. Executa a limpeza já existente: flag de usuário logado e transação das tabelas `tb_usuario`/`tb_biometria`, sem apagar outros dados/schema.
3. Somente após ambas concluírem grava `pending = false`, novamente verificando o commit.
4. A entrada consulta o marcador por `ResumeLocalSessionCleanupUseCase`. Sem pendência, não abre o banco, não limpa identidade/biometria e não encerra uma sessão em memória. Com pendência, repete a operação idempotente antes de liberar as telas.

Falhas de leitura, tipo inválido, gravação do marcador ou limpeza não viram “sem pendência”. Se a exclusão de uma tabela falhar, a transação faz rollback e o marcador continua pendente. Se a limpeza já tiver terminado mas a gravação final falhar, a próxima recuperação pode repeti-la sem recriar os dados apagados.

O marcador contém somente um booleano, sem credenciais, identificação, URL ou dados de negócio. Não é uma nova fonte de autorização e não substitui verificação de sessão no backend.

## Entrada Compose e estados

`SessionRecoveryViewModel` executa a recuperação no dispatcher I/O. `SessionRecoveryGate`, composto na `MainActivity` antes da seleção de destino, envolve **entrada normal, rota visual de debug e tela da fila**. Não há bypass específico para mock ou visual test.

| Estado | Apresentação/ação |
|---|---|
| CHECKING | Splash existente; conteúdo das telas ainda não é composto |
| FAILED | Splash + diálogo CaixaDS com Tentar novamente; não libera por Voltar/toque externo |
| AWAITING_ACKNOWLEDGMENT | Informa que a limpeza local terminou e que o logout do navegador não foi confirmado; exige Entendi |
| READY | Compõe o fluxo original, sem novo alerta ou loading da recuperação |

Uma recuperação concluída também invalida a autorização em memória, importante para outra Activity no mesmo processo. Se essa invalidação falhar após a limpeza persistida, o ViewModel mantém a informação de recuperação e tenta invalidar novamente após ação explícita, mesmo que o marcador já esteja desativado.

Não há novo login, troca de token, request EndSession nem confirmação implícita de SSO durante a recuperação. O aviso é adicional de segurança, não um texto atribuído ao legado. A aparência usa Splash/CaixaDS existentes, mas ainda requer comparação visual e execução no Android.

## Evidências

Artefatos: `build/verification/session-cleanup-recovery-20260907/`.

- **556 testes JVM aprovados**: 397 de domínio/estado/regras, 50 de rede, 96 de Data, dez de preferências e três de navegação mock. Dez testes novos: dois de domínio, cinco do ViewModel de entrada e três da orquestração persistida/concorrência.
- Estado/lógica da recuperação, incluindo corrotinas: **25/26 linhas (96,15%)**. Logout existente: 71/72 (98,61%). Domínio de conta selecionado: 24/24. Store/registros de autenticação: 59/59. Orquestração de limpeza/recuperação: 18/18.
- O relatório de orquestração não inclui a fábrica Android nem o adapter de SQLite/SharedPreferences. Os gates Gradle incluem esses componentes, mas não foram aprovados nesta execução. **Essas métricas não são cobertura global nem comprovação de persistência no dispositivo.**
- Compilação isolada dos fontes atuais afetados, rede, CaixaDS, gateway, MainActivity e composition root concluída. **18 testes instrumentados somente compilados**: 12 de conta e seis de armazenamento. Os novos cenários cobrem preservação de dados sem marcador, marcador inválido, recuperação após rollback, conteúdo protegido não composto e aviso/acknowledgment.
- `./gradlew :core:preferences:testDebugUnitTest :feature:account:testDebugUnitTest --offline` foi tentado e falhou antes das tarefas por falta de permissão no lock do wrapper Gradle fora da área gravável. Nenhum lock ou permissão foi contornado. Hilt/KSP, merge de recursos, D8/R8 e APKs por flavor não foram validados pela compilação isolada.

## Limites e próximas validações

- A recuperação durável começa com o **commit do marcador**. Se o processo terminar antes dele, não há registro persistido da intenção; a entrada não presume que toda identidade legada deva ser apagada. A invalidação inicial do token continua em memória, sem nova persistência de token.
- O mutex serializa a instância singleton do processo, não constitui protocolo entre processos independentes. O app não adicionou serviços/processos secundários nesta revisão.
- Recriar um repository em teste e compilar testes Android não equivalem a matar/reiniciar o processo real. Executar testes de SharedPreferences/SQLite, falha de disco, rotação, retorno do navegador e morte do processo nos pontos antes/depois de cada commit.
- O marcador trata limpeza local, não uma transação distribuída de logout SSO. Não persiste nem recupera requisições do navegador. O reconhecimento do aviso é estado de apresentação em memória.
- A checagem adiciona I/O local antes de compor os destinos. Homologar duração/transição do Splash e garantir que nenhum frame de conteúdo de sessão anterior apareça, inclusive em rotas de fila/debug.
- Cookies/WebView, demais caches por usuário, respostas em voo, biometria completa, protocolo SSO real, matriz visual completa, todos os flavors/Release e cobertura global continuam pendentes.

Nenhum banco ou preferência real do usuário foi apagado na verificação; os testes Android de armazenamento permanecem sem execução. A migração integral continua em andamento.
