# Revisão MVVM de 02/11/2026

A revisão manual percorreu os 46 ViewModels das features, os dois ViewModels de Core e as integrações relevantes de estado, efeitos, navegação, Domain e Data. O gate estrutural de arquitetura passou na revisão inicial; isso não substitui os testes de concorrência e reentrada descritos abaixo. As conclusões iniciais foram obtidas por leitura dos fluxos, sem transações remotas nem reprodução no Samsung nesta rodada.

## Contratos corrigidos

| Referência | Área | Contrato |
|---|---|---|
| AO-02 | Cadastro e dados pessoais | A conclusão é reconhecida no store antes do callback de navegação; reentrada com o mesmo ViewModel não reabre a confirmação. O reconhecimento não apaga o sucesso da gravação nem autoriza reenvio. |
| AO-03 | Introdução e tutorial | Complete, Start e Skip disputam uma única conclusão, reivindicada antes de agendar persistência. A preferência é capturada no comando; falha permite nova tentativa explícita. |
| BCP-01 | Cartão e Pix | Sucesso e reconhecimento de navegação são estados distintos. A Route consome a intenção antes de chamar o host; voltar não repete a conclusão nem envia outra compra. |
| BCP-02 | Carrinhos favoritos | Cada expansão tem identidade própria. Nova seleção, cache, recolher e refresh invalidam a anterior; resposta antiga não muda a seleção nem repopula o cache invalidado. |
| OTH-01 | Resgate Pix | A submissão mantém um proprietário independente de loading/etapa. Voltar ou trocar canal não libera outra escrita enquanto a anterior estiver pendente. Não há repetição automática de resgate. |
| OTH-02 | Histórico de compras | Geração e consulta aplicada identificam cada página. Trocar mês/filtros invalida respostas anteriores, inclusive erros e catálogo de meses atrasado. Append exige primeira página bem-sucedida da geração atual. |
| OTH-03 | Repetir aposta | A intenção é reivindicada antes de IO e permanece durante escolha/conversão de concurso e inclusão. Confirmações são consumidas antes da execução. Cancelamento do proprietário é verificado após parâmetros não cooperativos e antes de adicionar ao carrinho. |
| AO-01 | Pós-login | Resultado de sessão substituída é distinto de falha da sessão atual. O resultado antigo não autoriza encerramento da sessão nova; cleanup e reconhecimento de bloqueio devem conservar o vínculo de identidade. |
| AO-04 | Escritas de formulário | Cadastro, atualização e recálculo são vinculados à identidade capturada na criação do formulário. Ausência/substituição de identidade falha fechada; guards da apresentação rejeitam comandos e respostas de sessão antiga. |

AO-01 e AO-04 foram riscos condicionais na revisão: o fluxo normal de troca de conta na mesma Activity já limpa a pilha e cancela os ViewModels anteriores. O isolamento adicional protege proprietários simultâneos e operações pendentes, sem alegar que houve alteração real de outra conta.

Os aliases de modalidade usados em Support foram extraídos para mapper puro, preservando os IDs e o comportamento existente. Não foi criado caso de uso vazio por convenção.

## Fronteiras preservadas

Reducers continuam puros e recebem ações tipadas. A apresentação expõe StateFlow somente leitura e coordena os casos de uso; claims de efeitos ficam privados ao proprietário. Domain não importa Android, Retrofit ou Redux. Data adapta o vínculo da sessão para o transporte existente. Os métodos, caminhos, corpos, parâmetros remotos e headers HTTP permanecem preservados; `@Tag` é metadado local de Retrofit, não um novo parâmetro enviado ao servidor.

Campanha continua como projeção do Flow de configuração. AccountStatus permanece dedicado a efeitos. Estado visual de rolagem, filtro do catálogo e feedback de copiar do Sample não exige ViewModel artificial. DestinationSnapshotViewModel conserva proteção por geração, mas não foi localizado consumidor de produção no workspace desta revisão; referências históricas ao seu uso em carrinho não comprovam uso atual.

## Evidências e limites

Foram acrescentados testes de reentrada de composição, respostas fora de ordem, comandos concorrentes, falha/retry, cancelamento e transporte vinculado com Retrofit/MockWebServer local. As regressões de navegação e concorrência foram executadas em vermelho antes das correções; testes adicionais ampliam a proteção dos contratos. Em AO-01, a primeira massa usava identidade inválida e não comprovava o defeito. Após corrigir a massa, uma cópia externa das fontes, com as três proteções de login retiradas, produziu três falhas de assert; duas regressões adicionais comprovaram o consumo de conclusão após troca de sessão. A cópia não alterou fontes, headers ou index do checkout. A validação seguinte usa novamente as fontes nativas corrigidas.

A validação nativa final executou:

```sh
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew preCommitCheck :app:assembleTqsDebug --continue --console=plain
```

| Verificação | Resultado desta rodada |
|---|---|
| Testes JVM/Robolectric agregados | 3.673 aprovados; zero falhas e zero ignorados |
| JaCoCo LINE global | 95,39%: 60.441 linhas cobertas de 63.364 elegíveis; limiar estrito >95% aprovado |
| Ktlint, Detekt e Android Lint | Aprovados |
| Arquitetura, módulos, IDs, DS e contratos de backend/UI | Aprovados |
| Políticas de segurança, ferramentas, hooks e Android Studio | Aprovadas |
| APK PRD Debug e TQS Debug | Gerados |
| Fontes no Git/index | Bloqueado: 467 fontes ainda não indexadas, incluindo refatorações anteriores |
| Resultado global de preCommitCheck | Código de saída 1, exclusivamente por verifyTrackedSources |

O gate não foi reduzido ou contornado. As fontes não indexadas não foram excluídas nem ignoradas para esconder a falha. Esta tarefa não autorizou staging/commit/push, portanto o index existente foi preservado; a validação não significa que o commit esteja liberado. O SHA-256 de 40 baselines e dos cinco headers visuais protegidos permaneceu idêntico à captura anterior à correção.

Relatórios regeneráveis: `build/reports/tests/aggregate/index.html` e `build/reports/jacoco/aggregate/html/index.html`. Logs, prova negativa, lista de fontes não indexadas, registro de ações e resumo desta sessão foram preservados fora do repositório em `simlo-artifacts/2026-11-02/mvvm-fixes/`. As ferramentas crapper, dryer e mutator não estavam disponíveis; nenhuma execução ou métrica delas foi alegada.

Não houve instalação, publicação Firebase ou operação financeira remota nesta tarefa. Testes JVM/Robolectric e APK Debug não comprovam execução Samsung ou release ofuscada.
