# Atualização e indisponibilidade — 07/09/2026

Status: componentes e ações unificados, testes JVM e compilação isolada aprovados. **Paridade visual no APK e homologação de todos os estados ainda pendentes.**

## Referência

Foram confrontados `AtualizacaoActivity`, `AppIndisponivelActivity`, `RateUtils`, `LoadingViewLoterias`, `activity_atualizacao`, `content_atualizacao_app`, `content_app_indisponivel`, `app_bar_degrade_with_icon` e `dialog_loading` do checkout legado preservado.

## Correções

- `StartupStatusScreen`, em `feature:onboarding`, concentra a apresentação. Entrada real, entrada mock e rotas de catálogo agora usam esse componente por `StartupStatusPanel`; foram removidos o layout duplicado, o desenho aproximado de download e a versão fixa `v1.0.0`.
- Ícones originais `icon_loteria`, `ic_download` e `ic_indisponivel`; mensagem literal por estado (offline, indisponível, atualização), versão de `BuildConfig`, fonte Futura, fundo branco, barra em gradiente, ilustração de 72 dp, divisor de 142 × 1 dp e CTA amarelo retangular de 56 dp. A referência vertical é metade da área útil inteira, não metade do conteúdo abaixo da toolbar. A posição horizontal do título considera o inset da Toolbar legada e ainda requer confirmação por captura.
- Atualizar abre `market://details?id=<package em execução>`; sem handler, tenta Play Store via HTTPS. Não conclui onboarding nem abre Home. Voltar fica bloqueado, conforme `AtualizacaoActivity`. A ausência dos dois handlers mostra erro dispensável.
- Tentar novamente chama `StartupViewModel.retry`: preparação de segurança, verificação da versão e consulta dos parâmetros. O destino só é liberado em `READY`. As rotas de falha foram removidas de `workflowNextDestinations`; o catálogo usa a mesma validação da entrada, sem gravar a conclusão da introdução.
- Durante uma nova tentativa, a tela de erro permanece por baixo do loading institucional. O CTA não aceita repetição e Voltar fica bloqueado enquanto carrega, como o diálogo não cancelável do legado. A primeira carga continua com splash. O texto do loading é `Carregando…`, com atraso/fade do CaixaDS já existente.

## Evidências desta revisão

Artefatos em `build/verification/startup-status-parity-20260907/`:

- 164 testes JVM aprovados: 145 de estados/domínio/rotas/ações, 3 do envelope, 15 de repositories e 1 de preferências.
- Novos testes verificam mensagens/CTAs/versão, precedência da loja nativa, fallback HTTPS, falha sem handlers e ausência de transições genéricas para as duas rotas.
- Cobertura isolada de onboarding: 68/68 linhas (ViewModels, incluindo corrotinas, e conteúdo textual dos estados). `AppStoreNavigation`: 1/1 linha, com ambos os resultados e ambas as tentativas exercitados. Não inclui renderização, intents Android nem navegação Compose.
- Fontes atuais de Compose, host, gate e integração compilados com o compilador/dependências locais. Isso **não substitui** Gradle, geração Hilt/KSP, APK, R8 ou execução nos flavors.

## Pendências explícitas

1. Capturas pareadas nos três estados e na nova tentativa, incluindo tamanho de fonte/acessibilidade, insets, fontes do loading e animação; testar clique/Voltar no APK e loja instalada/ausente.
2. Restaurar a seleção de ambientes na indisponibilidade/login e a recriação coerente do grafo, conforme `ENVIRONMENT_PARITY_AUDIT.md`.
3. O ramo originado na busca de parâmetros de simulação (`extra_is_busca_param`) foi posteriormente ligado à Home e ao carrinho, mantendo a origem e seus dados; ver `SIMULATION_RECOVERY_PARITY_AUDIT.md`. Redirecionamentos de erro específicos, validade compartilhada e homologação interativa continuam pendentes.
4. A reavaliação de conectividade foi posteriormente ligada a todas as instâncias desse componente, incluindo Home/carrinho; ver a revisão abaixo. Permanecem os testes de transição real de rede no APK.
5. Homologar limpeza/retomada de sessão da recuperação e builds/runtime `prd`, `tqs` e `automacao`. Os pins aprovados por ambiente/provedor continuam necessários para chamadas reais.

Nenhum desses resultados certifica todas as telas ou cobertura global superior a 95%.

## Revisão de conectividade — 07/09/2026

`AppIndisponivelActivity.atualizaMsg` consulta `AppUtils.isNetworkAvailable` na criação da tela e a cada clique. O legado usa a presença/disponibilidade/conexão de `NetworkInfo`, sem ping ao backend. A implementação moderna usa o contrato `NetworkAvailability` em `core:common`, fornecido por Hilt e passado explicitamente do `AppContainer` aos componentes. O provider Android consulta `ConnectivityManager.activeNetwork`, sem usar a API obsoleta `NetworkInfo`, sem requisição externa e sem acrescentar permissão além de `ACCESS_NETWORK_STATE`, já presente.

`StartupStatusPanel` lê a condição ao entrar e a relê antes de cada retry. `startupStatusForConnection` mantém a exigência de atualização em qualquer condição; nas telas de indisponibilidade, ausência de rede seleciona a mensagem offline e presença de rede seleciona a mensagem genérica do serviço. A fila, falha de preparação de segurança, entrada real/mock, catálogo, Home e carrinho recebem a mesma dependência.

Conectividade é somente um indício para a apresentação: não certifica acesso ao backend, não classifica erro TLS como offline, não pula o retry e não altera validação de versão, sessão, pinagem ou políticas de hosts. Serviço Android ausente ou falta de permissão produz `UNKNOWN`, preservando a categoria já conhecida, em vez de inventar ausência de internet. Esse tratamento é deliberadamente mais preciso que o `catch(Exception) -> false` legado; ainda exige homologação nos aparelhos suportados.

Evidências em `build/verification/connectivity-status-20260907/`:

- 209 testes JVM aprovados (190 de estados/domínio/ações, 3 de envelope, 15 de repositories e 1 de preferências).
- Testes cobrem rede presente/ausente/desconhecida, releitura após mudança, permissão ausente e todas as nove combinações de tipo de tela/condição de rede; atualização nunca é reclassificada.
- `NetworkAvailability.kt`: 10/10 linhas. `StartupStatusContent.kt`: 10/10 linhas. Onboarding, incluindo ViewModels/corrotinas e conteúdo: 73/73 linhas. Esses escopos não incluem o provider Android nem renderização Compose.
- Compilação isolada dos fontes atuais do host, provider Hilt, container e telas concluída. Sem geração Hilt/KSP, novo APK, execução de R8 ou validação dos flavors nesta rodada.

Pendências específicas: testar modo avião, Wi-Fi, rede móvel, VPN/rede cativa, retorno após configurações do sistema e clique durante transição, comparando layout/mensagem/ações com o APK legado. A existência de uma rede padrão não garante internet utilizável; a resposta real do serviço permanece a autoridade para a operação.

## Comparação visual da indisponibilidade em produção — 07/10/2026

A tela Compose foi comparada no XCover7 com a `AppIndisponivelActivity` do projeto CX no flavor `prd`, versão `1.25.0`. A primeira imagem de referência estava desatualizada; a Activity foi reaberta por instrumentação e recapturada antes de concluir a comparação. A captura atual confirma a correspondência visual da barra de status e navegação, cabeçalho e logo, ilustração, textos e espaçamentos entre linhas, divisor, botão laranja com ícone, versão e código `LTR-503`. O conteúdo da tela Compose foi ajustado para usar o mesmo layout, imagens e fontes Caixa do XML de produção.

Capturas pareadas em `build/parity-production-20261007/unavailable/`:

- `cx-prd-service-unavailable.png`: referência CX 1.25 recapturada diretamente na Activity.
- `compose-prd-service-unavailable.png`: Compose PRD 1.25 no mesmo aparelho e estado.

O pacote oficial instalado também foi aberto para confirmar a versão `1.25.0`, mas a sessão atual ficou na tela de login; por isso, essa sessão não forneceu uma segunda captura da indisponibilidade. A homologação feita aqui é da tela prd do código CX contra a tela Compose. Permanecem pendentes os estados offline e de carregamento/retry, além das verificações funcionais e de transição de conectividade descritas acima. As capturas foram comparadas visualmente; não foi calculada uma métrica de diferença de pixels.
