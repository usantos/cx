# Persistência criptografada e recuperação após falhas — Android

## Escopo e decisões do usuário

- Dados do usuário autenticado e demais dados locais de propriedade do app devem ser persistidos de forma criptografada com DataStore.
- Aplicar confidencialidade, integridade e disponibilidade (CID).
- Recuperar o fluxo, a tela e os dados de aposta após crash/encerramento do processo.
- Não converter para Compose Multiplatform: o usuário condicionou a migração à compatibilidade total, não comprovada sem SDKs iOS homologados.
- Textos locais do app inteiro devem ser recursos traduzíveis. A proposta de recursos Android existente continua válida.

## Evidências atuais

O projeto usa módulos Android e JVM. Não há DataStore configurado. Há SharedPreferences em onboarding, biometria, sessão/limpeza, configuração de ambiente, Aposta Shake, layout da Home, cadastro de push e uso de favoritos. Dados do carrinho e transferências possuem armazenamento local próprio. O refresh de biometria já usa AES-GCM e Android Keystore; não substituir essa proteção por persistência em claro durante a migração.

A integração FilaBra disponível é `FilaBRAAdapter-2.6.5.1.aar`, com `Activity` e callbacks `onPass`, `onJoin` e `onAbandon`; o contrato examinado não oferece exportação/importação de uma vaga. Xcode está instalado, mas isso não comprova compatibilidade dos SDKs iOS. Nenhuma migração multiplataforma foi aplicada.

## Arquitetura proposta

### 1. Contratos e armazenamento

Criar uma fronteira de persistência segura reutilizável, com interface independente da apresentação. Uma instância de DataStore por arquivo/processo, injetada nos repositórios. As telas não acessam DataStore nem chaves diretamente.

Separar registros de sessão/credenciais, preferências por usuário, carrinho, rascunhos de modalidade e checkpoints de navegação. Cada registro tem versão de esquema e revisão monotônica. Escritas concorrentes usam `updateData`; navegação e rascunho que pertencem ao mesmo passo usam uma atualização atômica conjunta.

A identificação persistida do proprietário é opaca e não usa CPF. O espaço de armazenamento inclui ambiente, conta opaca, tipo de registro e versão. Nunca restaurar dados de uma conta ou ambiente em outro.

### 2. Confidencialidade

Criptografia autenticada AES-GCM antes de serializar dados sensíveis no arquivo DataStore. Gerar IV aleatório por escrita; nunca reutilizar IV com a mesma chave. Proteger chaves de dados com Android Keystore; não embutir chaves no código ou persistir chave junto ao conteúdo cifrado. Vincular a autenticação criptográfica (AAD) ao proprietário opaco, ambiente, tipo de registro e versão.

Persistir somente as credenciais necessárias ao fluxo de autenticação existente. Não persistir senha, CVV, código temporário, conteúdo integral de cartão ou tokens de acesso temporários como parte de um checkpoint de tela. Tokens de renovação mantêm as restrições de biometria existentes e não são restaurados para outra identidade.

Preferências e estados pertencentes ao app devem migrar para DataStore criptografado. Arquivos gerados grandes, como PDFs, não devem ser serializados como grandes valores de preferências: manter conteúdo cifrado em armazenamento privado e seu índice/metadados em DataStore cifrado. Exportação temporária para outro app ocorre apenas na ação explícita de compartilhar, com permissão de leitura restrita e limpeza do temporário.

Excluir arquivos/chaves sensíveis do backup inadequado. Não copiar arquivos privados de SDKs nem alterar persistência de FilaBra/Topaz sem contrato do fornecedor.

### 3. Integridade

Autenticar conteúdo, esquema e proprietário; alterações de ciphertext, IV ou AAD devem rejeitar a leitura. Validar limites, IDs, tipos, números e referências após desserialização; ciphertext autenticado não garante validade atual de negócio.

Não converter falha de autenticação, chave invalidada ou arquivo corrompido em estado vazio seguido de sobrescrita automática. Preservar o arquivo recuperável quando aplicável, mostrar uma mensagem de recuperação e evitar operações de compra durante a falha. Nenhum conteúdo bruto, token ou dados de aposta em logs de diagnóstico.

### 4. Disponibilidade e recuperação

Não depender de `onPause`, `onStop`, `onDestroy` ou capturador global de crash para salvar. O processo pode desaparecer sem callbacks.

Persistir rascunhos e o próximo checkpoint antes de confirmar ao usuário uma alteração relevante de aposta ou uma transição de etapa. Uma escrita só é considerada concluída quando DataStore confirma a transação. Não usar debounce para alterações que possam perder números selecionados ou uma etapa confirmada.

Checkpoint por proprietário/ambiente contém rota estável permitida, argumentos mínimos, versão, revisão, modalidade/concurso, seleção manual, Super Sete, trevos, mês/time, Teimosinha, espelho, quantidades e referências locais/remotas necessárias. Não serializar Activity, Context, WebView, callbacks, ViewModel, pilha arbitrária de objetos ou URLs com credenciais.

Na abertura, carregar o último checkpoint confirmado, validar sessão e elegibilidade e consultar o backend para reconciliar carrinho e operações remotas. Se o login expirou, manter o rascunho cifrado e retomar depois de autenticar a mesma conta. Não restaurar uma tela de sucesso como se o serviço já tivesse confirmado uma operação desconhecida.

Pagamentos, Pix, reservas, apostas e inclusão de carrinho com resultado remoto incerto não são reenviados automaticamente. Persistir uma referência de operação antes da requisição quando o contrato permite e consultar status/idempotência para reconciliar; ausência desse contrato requer recuperação explícita sem duplicar a ação.

## FilaBra: limite de garantia

É possível preservar dados de aposta, carrinho e etapa independentemente da fila. Não é possível garantir, apenas com DataStore, que o servidor mantenha uma vaga ou passe após expiração, revogação ou encerramento do processo.

A retomada deve usar exclusivamente o contrato homologado do SDK/serviço. Nunca inventar um passe local, pular a fila por estado salvo ou usar um token expirado. Se o SDK exigir novo ingresso, preservar o rascunho e restaurá-lo quando houver autorização válida. A garantia de conservar a posição exige confirmação técnica/documentação do fornecedor e suporte do servidor.

## Migração dos dados existentes

1. Inventariar todos os armazenamentos de produção, proprietários, política de conta/ambiente, backups e volume.
2. Criar contratos/implementação segura e testes antes de alterar repositórios existentes.
3. Migrar leitura e escrita por armazenamento: ler original, validar, cifrar e gravar atomicamente; confirmar leitura equivalente antes de remover o original.
4. Tornar a migração reiniciável após falha em qualquer passo. Não apagar dados originais em uma migração interrompida.
5. Migrar sessão/identidade, preferências, carrinho/rascunhos, checkpoints e artefatos temporários. Preservar o comportamento já existente de sessão e biometria.
6. Introduzir a restauração por rotas com allowlist e reconciliar o servidor antes de qualquer efeito remoto.

## Verificação obrigatória

- Arquivos persistidos não contêm valores de identidade, tokens ou números de aposta em claro.
- Reinício recupera o último estado confirmado; testes incluem morte real do processo.
- Isolamento entre duas contas e entre ambientes; troca de usuário não mistura dados.
- IVs diferentes para payloads iguais; adulteração, chave ausente/invalidada e corrupção são rejeitadas.
- Atualizações concorrentes, falta de espaço, falha de escrita e migração interrompida não apagam o carrinho/rascunho anterior.
- Logout, expiração de sessão, fila expirada e retorno de autenticação não descartam nem expõem rascunhos indevidamente.
- Operação remota de resultado incerto é reconciliada sem reaposta, pagamento, Pix ou reserva duplicada.
- Verificações de strings, arquitetura, compilação e testes passam; validação financeira usa serviços controlados e fixtures.

## Critérios de conclusão e dependências

Só declarar o app com persistência criptografada completa após auditar e migrar todos os armazenamentos de propriedade do app. Só declarar restauração de uma tela após um teste de encerramento do processo daquele fluxo, incluindo autenticação e falhas de armazenamento.

Persistência local pode ser entregue no Android independentemente de iOS. Conservar a vaga na FilaBra permanece dependente do contrato do fornecedor/servidor; a restauração dos dados de aposta não depende de afirmar essa garantia externa.

## Fontes técnicas

- DataStore Android: https://developer.android.com/topic/libraries/architecture/datastore
- Suporte multiplataforma de DataStore, consultado para avaliação e sem migração aplicada: https://developer.android.com/kotlin/multiplatform/datastore
- Recursos Compose Multiplatform, apenas avaliação: https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources.html
