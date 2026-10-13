# Limpeza local no logout — 07/09/2026

Revisão posterior: `SESSION_CLEANUP_RECOVERY_AUDIT.md` adiciona marcador durável, serialização do repository e gate Compose de recuperação na entrada. A pendência abaixo sobre ausência de marcador descreve o estado histórico desta revisão; execução Android/morte de processo e demais caches continuam sem homologação completa.

## Lacuna e referência

O logout Compose invalidava o token em memória e passou a abrir EndSession, mas não removia os dados deixados por uma instalação do legado. Foram conferidos, no backup legado:

- `DadosUsuarioBO.limparRegistros` → `salvarUsuario("", "")` → `UsuarioCRUD`, tabela `tb_usuario` (`nome`, `cpf`).
- `DadosBiometriaBO.limparRegistros` → `salvarBiometria("", "", "")` → `BiometriaCRUD`, tabela `tb_biometria` (`refresh`, `nome`, `cpf`). O legado codificava os valores em Base64; isso não foi reutilizado como proteção de credenciais.
- `DBLoteriasHelper.DATABASE_NAME`: `Loterias.db`.
- `DadosUsuarioBO.updateUsuarioLogado`: preferência `USUARIO_KEY`, chave literal `usuariologado` em `strings.xml`. Os ramos de saída/pós-login/autossuspensão a desativam.

## Implementação

`LocalSessionDataRepository` e `ClearLocalSessionDataUseCase` ficam em Domain, sem Android. Hilt fornece `LegacyLocalSessionDataRepository` em `core:preferences`, injetado no coordenador de logout pelo composition root.

| Dado | Ação |
|---|---|
| Access token/tentativa em memória | Invalidado imediatamente pelo gateway, antes de qualquer I/O |
| `USUARIO_KEY.usuariologado` | Grava `false` com confirmação síncrona de persistência, executada no dispatcher de I/O |
| `Loterias.db/tb_usuario` | Remove os registros de identidade |
| `Loterias.db/tb_biometria` | Remove os registros de refresh token/identidade |
| Demais tabelas, versão do banco e preferências | Não são apagadas ou recriadas por essa operação |

O adapter abre somente o banco existente, sem `SQLiteOpenHelper`, upgrade de versão, recriação de schema, `DROP TABLE` ou exclusão do arquivo. Banco/tabelas ausentes são aceitos. As duas tabelas são limpas na mesma transação; falha na segunda desfaz a primeira. A implementação remove registros em vez de reinserir uma linha vazia, preservando a ausência de identidade/credencial sem manter linhas artificiais.

A conexão habilita e verifica `secure_delete` antes da exclusão para reduzir remanescentes em páginas SQLite, conforme a [documentação do SQLite](https://www.sqlite.org/pragma.html#pragma_secure_delete). Isso **não certifica apagamento forense** de armazenamento flash, WAL, backups ou outras cópias; não há `VACUUM` nem remoção manual de arquivos auxiliares.

Preferências e SQLite são armazenamentos distintos, sem transação atômica conjunta. O repository tenta ambos mesmo se um falhar, informa conclusão incompleta e permite repetição idempotente. Cancelamento é propagado. Erros internos não levam conteúdo dos registros para mensagens/logs.

## Apresentação e sequência

1. `LogoutViewModel.start` invalida a autorização em memória. O host remove as seleções transitórias e solicita a limpeza da pilha autenticada.
2. `ClearLocalSessionDataUseCase` roda em `dispatchers.io`, enquanto o CaixaDS apresenta o loading modal. Cliques repetidos não iniciam outra limpeza.
3. Somente depois da conclusão confirmada o ViewModel prepara EndSession, novamente no contexto da apresentação. O modo mock também exige limpeza local e não abre o SSO real.
4. Falha/cancelamento publica `LOCAL_DATA_FAILURE`. O aviso não libera o login por Entendi/Voltar/toque externo: oferece **Tentar novamente**. O retry repete apenas a limpeza, sem novo logout local, login ou request EndSession antecipado.
5. Destruir o host durante a limpeza cancela a continuação; o retorno tardio da operação local não abre o navegador.

O texto de falha de persistência é novo e não é apresentado como literal legado. Reutiliza o diálogo institucional, mas sua aparência/interação ainda exige homologação Android.

## Evidências desta revisão

Artefatos em `build/verification/local-session-cleanup-20260907/`:

- **546 testes JVM aprovados**: 390 de domínio/estado/regras, 50 de rede, 96 de Data, sete de preferências e três de navegação mock. Dez testes novos: dois de domínio, três da orquestração do repository e cinco de estado/cancelamento/retry do logout.
- Cobertura de linhas do logout/estado, incluindo corrotinas: **71/72 (98,61%)**. Domínio de conta selecionado: 21/21. Store/registros de autenticação: 59/59. Lógica do login: 78/79 (98,73%).
- A orquestração pura de `LegacyLocalSessionDataRepository.clear` e suas referências de funções teve 9/9 linhas. Esse relatório **exclui a fábrica Android e o adapter SQLite/SharedPreferences**; não representa cobertura completa do arquivo ou módulo.
- Compilação isolada dos fontes atuais afetados, rede, CaixaDS, gateway e host/composition root aprovada. **13 testes instrumentados apenas compilados**: nove de conta e quatro de armazenamento. Os testes de armazenamento usam diretório e preferências exclusivos de teste; cobrem instalação nova, schema parcial, preservação de outras tabelas/versão/preferências, rollback e falha de preferências.
- O gate Gradle de `core:preferences` agora inclui repository/fábrica/adapter de sessão, mantém a exigência de testes Android e exige mais de 95% (`0.9501`). **Não foi executado com sucesso.**
- `./gradlew :core:preferences:testDebugUnitTest :feature:account:testDebugUnitTest --offline` falhou antes das tarefas pelo lock da distribuição Gradle fora da área gravável. Não houve contorno de permissões. Compilação isolada não valida Hilt/KSP, recursos, D8/R8 nem APKs por flavor.

Nenhum banco real, preferência real ou credencial do usuário foi removido durante o desenvolvimento/verificação deste turno. Foram alterados o código e os testes; os testes Android não foram executados.

## Ainda pendente

- Executar `:core:preferences:connectedDebugAndroidTest`, `:feature:account:connectedDebugAndroidTest` e os respectivos gates de cobertura; inspecionar SQLite/SharedPreferences em migração instalada sobre o APK legado com fixtures autorizadas.
- Persistir/retomar uma limpeza interrompida por morte do processo e integrá-la ao gate de entrada. Hoje a falha bloqueia enquanto o coordenador está vivo; o estado de limpeza não é persistido. O app atual não usa os registros legados como autorização, mas isso não elimina a necessidade de tratar os remanescentes em disco.
- Revisar cookies/cache do WebView e demais dados por usuário, caches de repositories e respostas de negócio em voo. `KeycloakBO.limparSessao` possui ações sobre WebView que não são equivalentes a encerrar cookies do navegador externo.
- Biometria completa/armazenamento seguro de renovação, logout no provedor real, registro da URI de retorno, todos os flavors/Release e matriz visual continuam sem homologação integral.

Esta revisão não conclui a migração completa nem demonstra cobertura global acima de 95% ou certificação OWASP.
