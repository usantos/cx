# Entrada de bolões pela Home — 07/09/2026

Status: integração em código e testes JVM; homologação visual/interativa e builds dos flavors pendentes.

## Referências e diferenças corrigidas

- `PrincipalActivity` verifica `usuarioEstaSuspenso(..., 2)` antes de `clickButtonBolao`. O CTA Compose navegava diretamente ao Marketplace. Agora `HomeViewModel.startPools` usa `PreparePoolEntryUseCase`, com acesso/suspensão compartilhados com a entrada individual por `verifyBettingAccount`.
- A entrada de bolão não usa a regra de encerramento individual. O objetivo dessa janela é justamente permitir a revenda após encerrar a aposta individual; não se deve bloquear os dois caminhos com a mesma regra temporal.
- `clickButtonBolao` consulta `DadosUsuarioBO.checarTutorial(BOLAO_TUTORIAL)` e mantém os extras de modalidade/tipo. O domínio só possuía gravação da preferência. `OnboardingRepository.isTutorialDismissed` e sua implementação agora leem o mesmo arquivo/chave booleana do legado, com padrão `false`.
- `TutorialActivity.apostar` abre o destino da modalidade e encerra a Activity; `pular` somente encerra. A conclusão do tutorial de bolão estava apontando para o simulador individual. Agora abre `betting/pools` e remove o tutorial da pilha; fechar/pular mantém o retorno, sem abrir o Marketplace.
- O checkbox só grava a dispensa quando marcado, preservando a lógica existente do tutorial. Abrir o tutorial não grava a preferência. O comportamento é compartilhado entre a leitura do caso de uso e a gravação de `SetBettingTutorialDismissed`.

## Fluxo atual

`HomeScreen` envia modalidade e tipo a `HomeViewModel`; o caso de uso valida a seleção e o acesso antes de ler a preferência. Erro/suspensão mantém a Home com alerta; pendência de cadastro, termos ou autoavaliação gera uma decisão tipada para o host. Ações individuais e de bolão compartilham a trava de carregamento/decisão pendente, impedindo navegações concorrentes.

Se autorizado, o host aplica o filtro e abre o Marketplace diretamente ou a rota parametrizada `PoolTutorialRoute`. Ao concluir o tutorial, modalidade/tipo são recuperados dos argumentos da rota e reaplicados ao filtro. Isso não depende apenas da variável em memória que existia antes de abrir o tutorial. A navegação parametrizada também mantém o destino de entrada direta do catálogo sem argumentos, com filtro vazio.

## Limites da evidência

Verificação final: **123 testes aprovados**, incluindo a integração entre checkbox do tutorial e nova entrada, a leitura da preferência histórica e as falhas/decisões da Home. Distribuição: 105 de domínio/ViewModels/formatação/rotas/tutorial, 3 do envelope, 14 de repositories remotos e 1 de preferências com store em memória. Esse último verifica o contrato de leitura/escrita; não é um teste instrumentado de `SharedPreferences` em Android.

Cobertura de linhas: Home com corrotinas/formatação **143/146 (97,95%)**; políticas de entrada individual/bolão e janela de revenda **71/71 (100%)**; construção das rotas de simulador/tutorial **3/3 (100%)**. Os relatórios estão em `build/verification/pool-entry-20260907/`: `feature-home-coverage.xml`, `core-domain-betting-coverage.xml` e `apostas-coverage.xml`. Essas medições não representam cobertura global.

A compilação isolada Kotlin/Compose e dos fontes atuais do host passou, com `retail-ui.jar` e `app-host.jar` no mesmo diretório. Essa compilação usa dependências locais já disponíveis e não executa geração Hilt/KSP, R8 ou empacotamento APK.

Esta revisão cobre o CTA “Compre seu bolão” do carrossel. Não certifica os acessos pelo menu, Combo, Rapidão, Instantânea, reserva/pagamento, nem todas as situações de sessão expirada. Os exemplos mock não substituem a homologação autenticada. Também permanecem pendentes a captura comparativa do tutorial/alerta/loading, restauração em Android runtime, Hilt/KSP, R8 e todos os flavors.
