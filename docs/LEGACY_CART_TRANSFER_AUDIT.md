# Carrinho local legado → verificação de cadastro

## Estado: leitura e contrato implementados; transferência ainda não conectada

Não considerar o pós-login equivalente ao legado: `RetrofitDataSource.verifyAccount()` ainda
constrói `AccountVerificationRequestDto()` sem apostas. Esta revisão prepara e testa o snapshot e
seu mapeamento; não faz POST, não confirma migração e não apaga apostas. A revisão posterior
`LEGACY_CART_TRANSFER_RECEIPT_AUDIT.md` adiciona recibo persistente e limpeza seletiva, com provider
Hilt. A chamada pelo pós-login e a reconciliação de envio inconclusivo ainda não estão conectadas.

## Evidência legada

Referência: backup `simlo-app-android-legacy-app-backup-20260903`, em
`src/main/java/br/gov/caixa/loterias/apostas`:

- `controllers/TokenActivity.java`, `verificaCadastro`: consulta todas as apostas locais,
  normaliza ID de servidor zero para nulo e chama `postVerificarCadastro` com esse lote.
- `model/bo/DadosUsuarioNuvemBO.java` e `DadosUsuarioApostadorBO.java`: enviam `AutenticacaoDTO.apostas`
  a `POST logado/usuarios/verificar-cadastro`.
- `model/dao/DBLoteriasCrud.java`: lê as tabelas de apostas, partidas, equipes, parâmetros e mês.
- `model/bo/silce/dto/IdentificaoDeUmaApostaDas8Modalidades.java`, `ModalidadeEnum.java`,
  `EquipeDTO.java`, `ParametroEquipe.java`, `ParametroMesDeSorte.java`: contrato do lote.
- Sem redirect, o legado aplica dados de sessão/carrinho/notificações e chama `deleteAll()`.
  Esse método apaga também tabelas que não pertencem ao lote. Não foi reproduzido.

## Separação de responsabilidades

| Camada | Implementação | Responsabilidade |
|---|---|---|
| Domain | `LegacyLocalCartRepository`, `LegacyLocalCartBet` | Snapshot tipado, ID local separado do ID remoto, sem Android/Gson |
| Persistência | `LegacyLocalCartReader`, `LegacyCartSnapshot`, `AndroidLegacyCartSnapshotStore` | Leitura do grafo SQLite e conversão, sem helper de upgrade ou escrita |
| Data | `LegacyCartUploadMapper` | Domínio → contrato remoto; exclui metadados locais |
| Network | `LegacyCartUploadDto`, `AccountVerificationRequestDto` | Campos `@SerializedName`; `apostas` deixa de ser `List<Any>` |

O leitor é síncrono na camada de armazenamento; o futuro orquestrador deve chamá-lo em I/O.
Não deve ser invocado diretamente por uma tela Compose.

### Dados preservados

- Modalidades com os nomes de enum enviados pelo legado; tipos/códigos mantêm valor e descrição.
- Dezenas como lista; Super Sete como matriz, preservando zeros, ordem e colunas vazias.
- Trevos nas chaves `numerosSelecionadosMaisMilionariaTrevo` e
  `quantidadeNumerosMaisMilionariaTrevo`.
- Valor, concursos, quantidade de apostas/Teimosinhas, Espelho, Troca, Surpresinha e vínculo do espelho.
- Datas/horas efetivamente armazenadas, time do coração, mês e partidas de Loteca/Lotogol.
- Equipes com campos planos, parâmetro aninhado, placar/vitória e índices de placar de Lotogol.

O JSON não contém `_id`/`idDB`, quantidade local de Surpresinhas, seleção de UI ou recibos de
migração. Parâmetros sem referência usam objetos vazios no upload, como os objetos de DTO
inicializados pelo leitor antigo. Não se afirma igualdade byte a byte com o serializador legado:
campos exclusivamente de UI, como `isSelecionado`, não fazem parte do novo contrato de envio.
O `indicadorCotaBolao` permanece falso, valor inicial do DTO reconstruído pelo banco legado.

### Integridade e preservação

- Banco ausente não é criado; tabelas ausentes não são criadas.
- Abertura `OPEN_READONLY`, sem `SQLiteOpenHelper` nem alteração de schema.
- Handler de corrupção próprio impede a exclusão automática do banco pelo handler padrão Android.
- Apenas seis tabelas de carrinho são consultadas. Identidade e biometria não entram no snapshot.
- `data_version` é comparado antes/depois no handle privado de leitura, com acesso síncrono e
  sem habilitar pool WAL. Mudança rejeita o lote inteiro, sem retry automático. Essa comparação
  só é válida na mesma conexão, conforme a [documentação SQLite](https://www.sqlite.org/pragma.html#pragma_data_version).
- IDs locais inválidos/duplicados, modalidade desconhecida, JSON de formato inválido e referências
  não-zero sem equipe/parâmetro/mês correspondente falham; não retornam sucesso parcial.
- Colunas escalares ausentes/nulas seguem os defaults do leitor atual. A compatibilidade de
  versões antigas de schema ainda precisa ser exercitada com bancos reais anonimizados antes do upload.
- O upload cria suas próprias coleções de números/trevos. Alterar o DTO não modifica o snapshot.
- `toString` do lote, números e request não imprime apostas. Isso não substitui a revisão dos
  interceptadores de logs antes de ligar o envio de dados locais.

## Validação

Execução isolada de 07/09/2026, artefatos em
`build/verification/legacy-cart-snapshot-20260907/`:

- **570 testes JVM aprovados**: 397 de lógica/estado, 52 de network (50 da suíte e dois do
  contrato em execução complementar), 100 de data, 18 de preferences e três de navegação mock.
- **14 testes JVM novos**: cinco do leitor, três da consistência do snapshot, quatro do
  mapeador e dois do contrato de serialização.
- Decodificação e consistência: **91/91 linhas**; mapeador de upload: **39/39**;
  DTOs/request selecionados: **66/66**. Esses números são escopos parciais, não cobertura do app.
- Compilação isolada de network, fontes afetados de domínio/data/preferences, CaixaDS,
  host/DI/autenticação e **22 testes Android**. Os quatro testes Android novos de carrinho
  **somente compilaram**, não foram executados. O adaptador SQLite/factory não faz parte do
  percentual JVM de 91/91 e continua incluído no gate Gradle completo.
- A primeira execução do contrato falhou por resolução de receiver em `JsonArray.apply`,
  que gerava listas vazias. Foi corrigida para usar explicitamente a lista de origem;
  a execução subsequente aprovou também o caso de matriz/colunas e isolamento de mutação.
- `git diff --check` não encontrou erros de whitespace (há avisos históricos de CRLF do Gradle).

Comando Gradle tentado: `./gradlew :core:preferences:testDebugUnitTest :core:data:test :core:network:test --offline`.
Falhou antes das tasks ao abrir `gradle-9.4.1-bin.zip.lck` fora da área permitida (`Operation not permitted`).
Não houve contorno de permissões. Compilação isolada não executa Hilt/KSP, recursos Android,
D8/R8, empacotamento, todos os flavors, testes SQLite reais nem comparação visual.

Os testes JVM cobrem leitura/decodificação, detecção de commit concorrente e contrato JSON completo
dos campos de aposta, complementos e partidas. Os testes Android usam diretórios exclusivos de
teste, incluindo o banco deliberadamente inválido, sem acessar dados da instalação do usuário.

O gate Gradle de preferences inclui leitor/factory/adaptador Android e exige os testes instrumentados.
O de network inclui os novos DTOs; o de data já inclui todos os fontes/classes internas.
As regras R8 existentes de `core.network.**Dto` e campos `@SerializedName` abrangem esses modelos;
a execução Release continua obrigatória e não foi substituída por inspeção das regras.

## Próximos passos obrigatórios

1. Executar os testes SQLite no Android, incluindo bancos das versões legadas e WAL; ampliar
   a matriz de schemas sem aceitar perda silenciosa de dados.
2. Conectar por DI a leitura/mapeamento ao pós-login e cadastro/aceite contextual que retomam a
   verificação. Não inserir efeitos destrutivos em todas as consultas de acesso do carrinho.
3. Validar envelope completo, código, redirect e payload do serviço antes de qualquer confirmação.
4. Persistir estado do lote e tratar resposta perdida/morte do processo sem reenviar cegamente
   apostas com ID nulo. Não presumir idempotência do backend.
5. Confirmar apenas o snapshot reconhecido; preservar apostas novas/alteradas, tabelas não
   relacionadas e dados de outros fluxos. Aplicar também carrinho/notificações da resposta.
6. Homologar os fluxos completos, capturas, flavors, Release/R8 e cobertura global acima de 95%.
