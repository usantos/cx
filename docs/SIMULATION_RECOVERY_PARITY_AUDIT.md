# Recuperação dos parâmetros de simulação — 07/09/2026

Status: comportamento de recuperação ligado à Home e ao carrinho, com testes JVM. Homologação visual/interativa e builds completas ainda pendentes.

## Referência e distinção entre origens

No legado, `PrincipalActivity.callWebservice` abre `AppIndisponivelActivity` com `extra_is_busca_param=true` quando a consulta falha ou a lista de modalidades vem vazia. `CarrinhoActivity.onParametroSimulacaoListener` abre essa tela quando a lista vem vazia, mas continua em `postResume` na falha de transporte. Essa diferença foi preservada, em vez de tratar ambos como uma falha de startup.

Na indisponibilidade com essa origem, `Tentar novamente` consulta `ParametrosSimulacaoModel.buscaParametroSiumulacao`. Lista vazia mantém a tela e apresenta `Não foi possível concluir esta operação. Por favor, tente novamente.`; lista válida atualiza os parâmetros e fecha a indisponibilidade, retornando à origem. Esse ramo não executa a limpeza de login do outro ramo de `AppIndisponivelActivity`.

## Implementação atual

- `HomeViewModel` mantém estado de indisponibilidade próprio. Uma resposta vazia não substitui os últimos parâmetros válidos, nem apaga o resumo do carrinho. Nova tentativa bloqueia chamadas repetidas e entradas de aposta enquanto a recuperação está aberta.
- `CartViewModel` diferencia carga do carrinho de carga dos parâmetros. Lista vazia abre a recuperação; falha inicial de transporte não abre. Nova tentativa vazia/com falha mantém a recuperação, enquanto sucesso válido atualiza limites e retorna ao mesmo carrinho. Pagamento não avança enquanto os parâmetros estão carregando ou a recuperação permanece aberta.
- `SimulationRecoveryPanel`, no host, reutiliza os assets/layout de `StartupStatusPanel` sem criar outro ViewModel de startup. Os callbacks pertencem ao ViewModel da origem; não há navegação para login, gravação da introdução ou limpeza de sessão/carrinho.
- Voltar retorna à origem quando ocioso. Durante a tentativa, mantém o loading não cancelável. O alerta de nova tentativa é dispensável e não fecha a recuperação.
- `drawDateLabel` foi movida sem alteração de comportamento da tela para `CartRules`, permitindo testar a formatação junto das demais regras sem depender de Compose.

## Verificação

Artefatos: `build/verification/simulation-recovery-20260907/`.

- 204 testes JVM aprovados: 185 de estados/domínio/rotas/ações, 3 de envelope, 15 de repositories e 1 de preferências.
- Novos cenários: primeira falha/vazio, vazio novamente, falha na tentativa, recuperação com dados válidos, cliques repetidos, Voltar durante espera/depois da espera, bloqueio de pagamento/entrada, preservação dos parâmetros e do carrinho.
- Home: 196/201 linhas (97,51%) no escopo de ViewModel, formatação e menu. Carrinho: 407/413 linhas (98,55%) nos ViewModels/UiStates e regras, incluindo corrotinas. Esses escopos não incluem UI Compose nem comprovam cobertura global.
- O gate Gradle do carrinho agora inclui classes internas/UiStates, exige cobertura maior que 95% e falha se não houver classes ou dados de execução. A execução pelo Gradle continua pendente.
- Compilação isolada concluída dos fontes atuais de Home, host/navegação, recuperação, tela do carrinho, confirmação de inclusão e respectivos componentes, usando o compilador Kotlin/Compose e dependências locais. Não foi gerado/homologado um novo APK nesta revisão.

## Pendências

1. Executar o APK em cada origem e comparar imagens, transições, Voltar e loading com o legado, inclusive retomada de Activity/processo e estado do carrossel/lista.
2. Restaurar a seleção de ambiente. A consulta de conectividade foi integrada posteriormente por dependência injetada, na abertura e em cada retry, conforme a revisão em `STARTUP_STATUS_PARITY_AUDIT.md`; transições reais de rede ainda precisam de homologação.
3. Completar o mapeamento dos redirecionamentos de `RedirectNetwork` para falhas específicas do servidor (incluindo sessão/autorização). Nesta recuperação, a falha permanece na origem com mensagem genérica; não foi certificado que todos os códigos reproduzam o legado.
4. Homologar a atualização/validade compartilhada dos parâmetros entre telas, em lugar da política de singleton usada pelo legado.
5. Validar Gradle, Hilt/KSP, R8 e todos os flavors. Compilação isolada e testes de lógica não substituem essa validação.
