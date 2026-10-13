# Recibo durável e limpeza seletiva do carrinho herdado

## Escopo e estado

Implementada a porta `LegacyCartTransferRepository`, seu repository em `core:preferences` e o
provider singleton no Hilt. Ela prepara um lote uma única vez, persiste sua confirmação e limpa
somente apostas confirmadas que não mudaram. **Ainda não está chamada pelo pós-login**: faltam
consumo da identidade verificada do proprietário, validação da resposta, transporte sem reenvio e coordenação
dos estados de revisão/retomada em Compose. A presença do provider não significa integração completa.

Nenhuma aposta de usuário foi enviada ou excluída durante esta revisão. Os testes JVM usam memória;
os testes Android, ainda sem execução, são restritos a diretórios exclusivos de fixtures.

## Protocolo local

| Operação/estado | Resultado e restrição |
|---|---|
| `prepare`, sem recibo e sem apostas | `Empty`; não cria banco ausente |
| `prepare`, com apostas e sem recibo | Persiste `PENDING` em transação antes de devolver `Ready` com lote/UUID |
| `prepare`, com `PENDING` | `RequiresReview`; nunca devolve outra permissão para o mesmo lote |
| `confirmReceived` | Exige mesmo proprietário/UUID; passa a `CONFIRMED`; confirmação repetida é idempotente |
| `prepare`, com `CONFIRMED` do mesmo proprietário | `CleanupPending`; retoma só a etapa local, não um POST |
| `finishConfirmed`, conteúdo intacto | Remove apenas pais e partidas do lote, junto com a limpeza do recibo |
| `finishConfirmed`, conteúdo alterado | Preserva a aposta alterada e grava `CONFLICT`; exige revisão, sem reenvio automático |
| Recibo de outro proprietário, callback antigo ou lote não confirmado | Não permite limpeza nem nova autorização de upload |

O owner key aceito é um hash hexadecimal de 64 caracteres que o futuro orquestrador deve derivar
do **issuer/subject verificados**, com separação de ambiente. Não passar access token, ID token,
CPF ou e-mail. A porta valida o formato, não a autenticidade desse identificador.

O protocolo é conservador: interrupção depois da reserva, mesmo antes do envio efetivo, pode
exigir revisão. Não há timeout que apague o recibo nem retry automático de um resultado incerto.
Não se promete entrega exatamente uma vez: isso requer reconciliação/contrato com o serviço.
Uma resposta com redirect ou erro não deve ser confirmada apenas porque houve HTTP 200.

## Armazenamento e transações

O banco `Loterias.db` mantém a versão e suas tabelas originais. Na primeira reserva não vazia,
são adicionadas exclusivamente:

- `compose_cart_transfer_receipt_v1`: um slot, UUID, owner hash, fase e versão do encoding.
- `compose_cart_transfer_items_v1`: IDs locais e fingerprints das apostas do lote.

Não armazena token, identidade em claro, números apostados ou resposta do backend nessas tabelas.
O Manifest atual desabilita backup do app. Hashes são detecção de mudança, não assinatura do servidor
nem proteção contra adulteração por um atacante que já controla o armazenamento do aplicativo.

Leitura do recibo, comparação, alterações e commit usam uma transação SQLite síncrona no mesmo
handle. Não há suspensão dentro dela. Falha de delete, gravação ou commit não devolve sucesso
parcial. O chamador futuro deve executar a porta em I/O; a implementação não é chamada por tela.

A limpeza usa `_id = ?` no pai e a chave de pai nas tabelas de Loteca/Lotogol. Não remove equipes,
parâmetros/mês compartilhados, carrinhos favoritos, identidade, biometria ou outras tabelas.
Linhas de partidas relacionadas a outros pais são preservadas. Pais já ausentes não justificam
limpeza de outras linhas. Órfãos compartilhados podem permanecer; não foi implementado garbage
collection por falta de prova de propriedade exclusiva.

Não copia `DBLoteriasCrud.deleteAll()`. As duas tabelas novas permanecem vazias após conclusão;
não há `DROP`, reset de versão ou helper destrutivo. O handler de corrupção impede a exclusão
automática do arquivo. Exclusão lógica de linhas não é afirmação de apagamento forense.

## Fingerprint de grafo, versão 1

O SHA-256 inclui a linha completa da aposta e suas partidas, equipes, parâmetros e mês referenciados.
Inclui também colunas não conhecidas pelo modelo de upload: uma mudança nelas preserva a aposta.
Ordem de mapas/linhas não importa; ausência, nulo, vazio e delimitadores são distintos. O encoding
usa comprimentos explícitos e UTF-8, sem reflexão Gson, nomes Kotlin ou dependência de locale/R8.
Há fixture golden para impedir alteração silenciosa do encoding com recibos pendentes.

Uma mudança numa equipe compartilhada preserva todas as apostas do lote que a referenciam.
Uma aposta nova com outro ID não altera a confirmação das anteriores. O schema legado usa
`INTEGER PRIMARY KEY AUTOINCREMENT` para apostas (`DBTabelasLoterias`); ainda é necessário
homologar versões de banco/restauração e fluxos que alterem explicitamente IDs. Reutilização manual
de um ID com conteúdo byte a byte igual não é distinguível pelo fingerprint e não está homologada.

Recibos com versão desconhecida, fase inválida, itens sem cabeçalho, estrutura incompleta ou
fingerprints inválidos falham sem apagar/substituir o estado. Não há migração automática de recibos
de outra versão: uma futura alteração precisa manter compatibilidade ou reconciliação explícita.

## Validação e limites

Execução isolada final de 07/09/2026 em
`build/verification/legacy-cart-transfer-receipt-20260907/`:

- **581 testes JVM aprovados**: 397 de lógica/estado, 52 de network, 100 de data, 29 de
  preferences e três de navegação mock. Esta revisão adicionou 11 testes JVM.
- Lógica do recibo/fingerprint: **86/87 linhas (98,85%)**, incluindo corpos das operações do
  repository; o factory Android e adaptadores SQLite não integram esse percentual parcial.
- Decodificação/consistência já existente: 91/91; mapeador do lote: 39/39. Não são cobertura global.
- Compilação isolada de fontes afetados, network, CaixaDS, host/composition root com o novo provider
  e **28 testes Android**, dos quais seis são os novos testes de transação. Todos os 28 somente
  compilaram, sem execução no dispositivo.
- A fixture golden do fingerprint foi executada na rodada final. `git diff --check` não encontrou
  erros; apenas avisos históricos de normalização CRLF nos arquivos Gradle.
- A árvore ativa segue com uma MainActivity host, zero Fragment/layout XML e quatro XMLs de plataforma.

Os testes JVM exercitam transições, confirmação idempotente, recusa de outro proprietário/callback,
reserva concorrente por duas instâncias, commit/cancelamento/falha de exclusão, preservação de
apostas novas/alteradas e fingerprint de todo o grafo. O armazenamento simulado implementa rollback;
isso valida o protocolo, não o motor SQLite Android.

Seis testes instrumentados novos cobrem banco ausente/corrupto, recriação do repository, recibos,
preservação de dados compartilhados, conflito por alteração de equipe, rollback com trigger de erro,
versão/fase inválida/itens órfãos e concorrência de handles. Precisam **ser executados** em Android.

O gate de preferences passou a incluir repository, estado do recibo, fingerprint, adaptadores SQLite,
fábricas e corpos internos. A cobertura JVM parcial não substitui esse gate nem a meta global >95%.

Tentativa Gradle: `./gradlew :core:preferences:testDebugUnitTest :core:preferences:connectedDebugAndroidTest --offline`.
O wrapper falhou antes das tasks ao abrir `gradle-9.4.1-bin.zip.lck` fora da área permitida
(`Operation not permitted`). Nenhuma permissão foi contornada. Hilt/KSP, SQLite real, APK,
flavors/Release/R8 e paridade visual continuam sem validação desta revisão.

## Próxima integração obrigatória

1. A revisão `AUTHENTICATED_SESSION_IDENTITY_AUDIT.md` fornece owner key/referência de sessão por
   provider Hilt. Falta consumir essa porta no pós-login, preservando a sequência de bloqueio/
   cadastro, a credencial vinculada e os retornos de cadastro/termos.
2. Usar transporte dedicado sem retries/follow-ups automáticos e validar código, redirect e payload
   antes de `confirmReceived`; não presumir que recebimento HTTP confirma todas as apostas.
3. Consumir `Ready` uma vez, apresentar `RequiresReview` sem retry cego e retomar apenas a limpeza
   em `CleanupPending`. Definir reconciliação verificável para conflitos e resposta perdida.
4. Aplicar os dados de carrinho/notificações/sessão retornados, sem sobrescrever a sessão de outro login.
5. Executar testes instrumentados, morte real do processo/WAL, bancos legados anonimizados,
   capturas de cada estado, todos os flavors/Release e cobertura global.
