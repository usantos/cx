# Retorno da autenticação — 07/09/2026

## Evidência legada

`TokenActivity.onCreate` monta `activity_token.xml` antes de chamar `AlertDialogUtils.show`. O layout não contém marca, botões ou mensagem própria: é uma superfície vazia com `colorAccent` (`#005ca9`). `AlertDialogUtils` delega ao `LoadingViewLoterias`, cujo texto é `Carregando…`. A janela não possui action bar; os insets são tratados pela activity base. O spinner/modal permanece durante as operações seguintes, até ser dispensado pelo fluxo.

## Diferença corrigida

O retorno OAuth moderno continuava desenhando o formulário de login por baixo do overlay. A rota de catálogo `account/token`, por sua vez, tinha um conteúdo separado com `Aguarde` e loading não modal. Isso era diferente do legado, apesar de ambas as telas usarem Compose.

`AccountTokenScreen` agora representa a superfície vazia e o modal CaixaDS institucional. É compartilhado por:

- `LoginScreen`, somente quando `LoginUiState.isLoading` indica troca de token/validação;
- `AccountRoute`, para a referência de catálogo `account/token`.

Não foi criado outro fluxo de autenticação ou uma Activity. A rota de catálogo é apenas uma referência visual, **não** um endpoint OAuth independente. O callback continua pertencendo ao launcher AppAuth e ao `LoginViewModel`; o layout não chama backend, não valida acesso e não dispara navegação.

Enquanto o navegador está pendente, o estado não é confundido com troca de token. Após uma falha, o login volta a ser apresentado com o erro existente. Durante o loading, o formulário deixa a composição e o modal continua bloqueante desde a abertura, inclusive antes do fade de 500 ms. O estilo do loading permanece documentado em `LOADING_PARITY_AUDIT.md`.

## Verificação executada

- 10 testes JVM de `LoginViewModelTest` aprovados, incluindo teste novo com troca de token e consulta de cadastro suspensas separadamente. Verifica loading nas duas etapas, ausência de autorização antecipada, encerramento após ambas e callbacks duplicados sem novas chamadas.
- 11 testes JVM do CaixaDS aprovados novamente.
- Compilação isolada de todos os fontes atuais do CaixaDS, `AccountTokenScreen`, `AccountRoute`, `LoginRoute` e demais consumidores do loading. Artefatos em `build/verification/authentication-return-parity-20260907/`.
- Três testes instrumentados novos de conta compilados: substituição do formulário e restauração após erro, distinção de navegador pendente e compartilhamento da tela de catálogo. **Não executados em dispositivo/emulador.** A feature recebeu dependências e runner de instrumentação, cuja resolução integral pelo Gradle permanece pendente.
- `git diff --check` aprovado, com avisos preexistentes de CRLF em arquivos Gradle.

`./gradlew :feature:account:testDebugUnitTest --offline` falhou antes de executar tarefas: o sandbox não permite abrir o lock da distribuição Gradle. Compilação isolada não substitui APK, geração de recursos, Hilt/KSP, R8, todos os flavors ou testes instrumentados. Os 21 testes desta revisão não representam cobertura global acima de 95%.

## Ainda não homologado

Capturas pareadas de retorno OAuth, barras do sistema, transições com o navegador, falha/cancelamento e recriação no Android continuam pendentes. Esta alteração também **não** resolve por si só o retorno contextual a compra/resgate após autenticação, a verificação de todos os ramos pós-login ou a recuperação após morte do processo. Não considerar a migração integral concluída.
