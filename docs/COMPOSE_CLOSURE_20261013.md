# Fechamento técnico da migração Compose — 13/10/2026

Branch: `codex/migracao-compose`. Referência: árvore atual do projeto CX, base `b46cc63`, incluindo a Home v2 e alterações locais. Este documento atualiza as pendências técnicas de `HOME_V2_CX_PARITY_20261013.md`; os relatórios anteriores são históricos.

## Conversão e comparação

O inventário compara 414 layouts do CX e 102 pontos de montagem de UI com implementações declarativas. O catálogo contém 80 destinos. Os gates rejeitam Java de aplicação, layouts XML de tela e Fragments. XMLs de desenho, manifests, configurações Android e integrações de plataforma continuam sendo recursos necessários; termos HTML são apresentados por WebView dentro do host Compose. Não são telas legadas reutilizadas.

A Home v2 é a apresentação usada no CX, com seus assets e ordem persistida. `-PhomeV2=true` é o padrão; `false` seleciona a apresentação anterior. Parâmetros, concursos, carrinho, bolões, permissões de entrada e resultados continuam nos casos de uso existentes.

## Lacunas fechadas nesta rodada

| Área | Correção e regra preservada |
|---|---|
| Apostas confirmadas | Consulta `apostas/{id}/conferir` antes do detalhe; não usa ID fictício em produção. Serviço, situação atualizada e redirects determinam a continuação. |
| Comprovantes | Hidratação de comprovante comum, prêmio pago e histórico conforme situação/backend; PDFs distintos de aposta e prêmio. |
| Apostas premiadas | Quando faltam dados do prêmio, consulta `apostas/{id}/detalhes-premio`; valor líquido e faixas são remotos. Redirect ou resposta incompleta impede ações sobre dados antigos. |
| Resultados da aposta | Rota própria com ViewModel; `resultados` recebe parâmetros `concursos` repetidos e intervalo completo de Teimosinha. Não abre o fluxo de resgate para mostrar um resultado. Entrada sem contexto volta à lista quando não existe tela anterior; fixture positiva está restrita ao APK mock. |
| Modalidades especiais | Loteca/Lotogol, placares 4 ou mais, colunas Super Sete, segundo sorteio, time/mês, regiões, próximos concursos e títulos especiais preservados. Acertos do Super Sete são calculados por coluna. |
| Autenticação nas consultas | Retorno à lista de apostas após login, sem continuação acidental no resgate. |
| SSO/antifraude | SDK Topaz 4.38.0.1658708 do CX, checksum fixo em Maven local. Envia deviceid normalizado, nivel/app/origem apenas com digest válido. Scope estendido depende de BIOMETRIC_WEAK; sem ela usa padrão do provedor. Permissões runtime por API e negação sem bloquear login. |
| Mock do SDK | Desativa sete componentes Topaz, WorkManagerInitializer e onze componentes de entrega. Backup/restore dos estados PM preserva a troca real→mock→real e evita retomar jobs persistidos. Produção sem marcador não altera estados. |
| Logout | POST `usuarios/sair` vinculado à identidade atual antes da limpeza local; rejeita resposta de sessão antiga e permite recuperar falha de limpeza sem repetir logout remoto confirmado. |
| Push | POST `push/registros` com CPF, token, plataforma android e canal loterias; persiste sucesso somente com confirmação do backend, protege troca de identidade/token e recebe mensagens no canal legado. Solicita POST_NOTIFICATIONS na entrada do login em Android 13+, aguardando a resposta antes do SSO automático e permitindo login mesmo após negação. Mock não solicita permissão nem registra dispositivo real. |
| CaixaDS e recursos | Cores dos consumidores movidas para tokens exatos; desenhos registrados; painter nativo para recursos layer-list/inset; fonte HTML original preservada em raw. |
| Pipeline | Memória e serialização das suítes de dispositivo ajustadas; cobertura de apostas aumentada com cenários de negócio, sem reduzir limiar. Gate de package/caminho detecta arquivos Kotlin em diretório incorreto. |

A auditoria confrontou BOs/controllers do CX com Retrofit, Data, Domain, ViewModels e navegação desses fluxos. As 99 operações de método/path continuam no gate contratual. Ele sozinho não comprova consumo ou equivalência de regras.

`betHistory` tem aba desativada no CX; constantes de buscas de lotéricas sem consumidores ativos não foram classificadas como tela faltante. Contratos genéricos de compra têm consumidores específicos equivalentes. APIs de combos, favoritos e carrinho possuem casos de uso ativos. `resultsRange` permanece compatível, mas o consumidor usa o contrato que repete `concursos`.

## Verificação da árvore final

- `releaseCheck`: aprovado; `assemblePrdRelease`, `assembleTqsRelease` e `assembleAutomacaoRelease` concluídos com minificação e lint vital.
- Testes JVM das áreas principais: Auth 53, Domain 162, Data 184, Network 97, Account 63, Tickets 43 e Results 27, sem falhas. Host: 76 por ambiente (Prd, Tqs e Automacao), sem falhas.
- Cobertura de apostas: 97,22% das linhas do escopo exigido (981 cobertas, 28 não cobertas), superior ao limiar de 95,01%.
- `modernCheck` e `releaseCheck`: aprovados na árvore final com SDK, sem exclusões (16min34s, 2.154 tarefas). Rodada adicional de Android: autenticação 9 e conta 29, sem falhas. Suítes do gate: preferências 27, apresentação 3, carrinho 29, Home 4 e notificações 5, sem falhas.

- APK mock isolado: seis testes Android do host aprovados (Home v2, drawer e isolamento Topaz/WorkManager); valida flags de bolão, concurso do serviço, destinos dos atalhos/menu, metadata e estados reais dos 18 componentes.

- Smoke completo: 80/80 rotas renderizando Compose, sem crash. A entrada direta de resultados foi corrigida e incluída na repetição.
- Pix mock após morte real do processo: aprovado; um POST inicial, zero POSTs no novo processo, PID 31310 → 31438, tarefa 416 preservada no APK final com SDK.
- Extensão SSO/Topaz: compilação TQS e testes JVM de autenticação/host aprovados. Seis testes Android do APK mock aprovados; smoke 80/80 e Pix/proc-morte aprovados com SDK. `modernCheck` e os três builds Release com SDK aprovados.

Nenhum gate ou limiar foi desabilitado.

## Comandos reproduzíveis

```bash
ANDROID_SERIAL=emulator-5558 ./gradlew modernCheck releaseCheck --continue --console=plain
./gradlew :app:assembleTqsDebug -PhomeV2=true
./gradlew :app:assembleTqsDebug -PhomeV2=false
```

As verificações de dispositivo usam AVDs separados com Android 16 (5556 para mock/rotas/Pix; 5558 para os gates finais), pois o emulador compartilhado recebeu uma Activity externa durante a suíte. O teste de favoritos foi alinhado à descrição acessível `Ajuda`, cuja ação no CX abre TermosUsoActivity com o extra de ajuda; as verificações de confirmação/exclusão e retorno permanecem.

A lista de caminhos alterados nesta etapa está em `COMPOSE_CLOSURE_FILES_20261013.txt`; o inventário da conversão original está em `COMPOSE_MIGRATION_CHANGESET_20261013.md`.

## Evidências locais

`build/verification/compose-closure-20261013/` reúne logs dos gates, testes Android, smoke 80/80, APKs mock e atestado SHA-256. `pix-process-sdk-final/result.json` e suas capturas confirmam PID/tarefa e contagem de POSTs. Esses artefatos de build não são versionados; comandos e resultados ficam neste relatório.

## Limite de homologação

A execução real do CX nesta data terminou em indisponibilidade **LTR-503**, versão 1.25.0. Isso impede fechar comparação visual e transacional integral com o mesmo backend. As evidências mock verificam renderização, regras controladas e restauração; não homologam SSO/Firebase, pagamentos ou resgates reais. Não há base para declarar 100% de equivalência visual e de todas as respostas do serviço.

Para essa homologação faltam pares equivalentes de dados/estados, retornos SSO/biometria e digest Topaz real, recebimento FCM real e respostas autorizadas de pagamento/resgate nos três ambientes. A matriz `VISUAL_PARITY_MATRIX.md` mantém os critérios de comparação; pendências de homologação não devem ser confundidas com presença de telas XML ou Fragments no aplicativo ativo.
