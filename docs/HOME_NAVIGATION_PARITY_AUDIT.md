# Navegação de apostas e menu da Home — 07/09/2026

Status: estrutura e ações do menu reconstruídas a partir dos fontes legados; testes JVM/compilação isolada aprovados. A homologação visual/interativa e a matriz integral de flavors continuam pendentes.

## Acessos corrigidos

`PrincipalActivity.buttonHomeBet` verifica suspensão antes de `nextActivity`, inclusive para Combo e Instantânea. `redirecionaRapidao` também verifica suspensão, respeita `IS_RAPIDAO` e consulta `DadosUsuarioBO.checarTutorialRapidao` para escolher configuração ou lista.

O Compose abria Combo e Instantânea diretamente, e o menu enviava sempre o Rapidão à configuração. Agora `PrepareHomeBettingActionUseCase`, fornecido por Hilt, retorna uma decisão tipada depois da verificação compartilhada de conta/suspensão. Combo exige `comboApostasHabilitado`; Rapidão exige `rapidaoHabilitado`. A Instantânea não depende dessas duas flags e mantém o endereço literal do legado: `https://instantanea.caixa.gov.br`.

`BettingTutorial.RAPID` identifica o arquivo/chave booleanos `RAPIDAO_KEY`, lidos pela implementação de preferências existente. Valor `true` leva à lista; `false`/ausente leva à configuração. Não foi inventada uma nova gravação automática: a auditoria encontrou a chamada a `updateTutorialRapidao(true)` comentada no legado. Valores inteiros por CPF presentes nesse mesmo arquivo, usados por `RapidaoSP`, não são alterados pela leitura booleana.

Os cliques de Combo/Instantânea no carrossel e de Rapidão no menu usam `HomeViewModel.startOtherBetting`. A trava de espera/decisão pendente é compartilhada com entrada individual e bolões; erro permite nova tentativa sem navegar. O menu oculta Rapidão quando desabilitado. O host traduz os destinos e só abre o navegador depois da autorização; falha ao abrir o navegador produz aviso dispensável, sem sucesso fictício.

## Verificação

- **137 testes aprovados**: 118 de domínio/estados/rotas/menu/formatação/tutorial, 3 de envelope, 15 de repositories remotos e 1 de preferências com store em memória.
- Home (ViewModel, corrotinas, estado, formatação e modelo do menu): **183/186 linhas, 98,39%**. Modelo/formatação do menu: **28/28**.
- Políticas de entrada de apostas (individual, bolões, Combo/Instantânea/Rapidão e janela de revenda): **94/94, 100%**.
- Construção/seleção das rotas e contratos de confirmação medidos: **38/38, 100%**.
- Compilação isolada Kotlin/Compose da UI e do host concluída sem erros.

Relatórios e artefatos: `build/verification/home-drawer-20260907/` (`feature-home-coverage.xml`, `core-domain-betting-coverage.xml`, `apostas-coverage.xml`, `retail-ui.jar`, `app-host.jar`). Esses escopos não cobrem os composables do drawer nem comprovam cobertura global, Hilt/KSP, R8, empacotamento ou execução dos flavors. A compilação usa fontes atuais e dependências locais; não equivale a uma build integral Gradle.

## Reconstrução do menu

`HomeDrawer` não utiliza mais os grupos “Jogue”, “Acompanhe”, “Sua conta” e “Serviços”. `HomeDrawerMenu`, em `:feature:home`, preserva a ordem e a visibilidade derivadas de `ExpandableListDataSideMenu`/`DrawerEnum`. O host faz a tradução de cada ação para a rota existente, sem remover destinos do catálogo:

1. Minha área, com Dados pessoais, Minhas apostas (condicional), Minhas compras, Apostas favoritas, Carrinhos favoritos e Meus cartões.
2. Resultados; Rapidão (condicional); Conferir bilhetes.
3. Jogo responsável, com Política de jogo responsável, Autoavaliação do apostador e Suspensão temporária.
4. Repasses sociais; Termo de uso; Dúvidas; Sobre a CAIXA Loterias.

`consultaMinhasApostas` é o nome remoto de `ParametrosConfiguraveisDTO.habilitaMenuApostas`; `SplashScreenActivity` o grava como `MOSTRA_MENU_APOSTA`, com fallback `true`. Agora `AppParametersDto` preserva esse nome e `RemoteAppConfigurationRepository` o mapeia para `HomeFeatureConfiguration.myBetsVisible`. A origem continua sendo `/parametros/configuraveis-app`. Os testes cobrem `true`, `false`, `null`, campo ausente, mapa nulo e o nome interno incorreto, que não pode substituir o contrato remoto.

Os dois grupos expandem independentemente e o clique num filho recolhe seu próprio grupo. O conteúdo central é rolável; cabeçalho e rodapé permanecem fora da rolagem. O drawer mantém largura de 300 dp, linhas de grupo de 55 dp, filhos de 58 dp, recuos, cores e ícones originais. Os seis vetores XML necessários foram transcritos para `CaixaDSDrawerIcons`, preservando paths/viewports/strokes, sem reintroduzir XML. O cabeçalho recebe nome da identidade autenticada injetada; o rodapé usa `BuildConfig.VERSION_NAME`, não `v1.0`. O painel institucional recuperou expansão e endereço literal.

Política de Jogo Responsável exige Confirmar/Cancelar antes do navegador. Sair exige Sim/Não antes de encerrar a sessão. A auditoria ampliada corrigiu uma inferência anterior: embora a ação de Sobre a CAIXA estivesse comentada no controller, **`SideMenuExpandleAdapter` instala o listener real**. Essa ação foi restaurada com o texto e a URL literais, sem abrir o navegador antes da confirmação.

## Pendências de homologação

Ainda é necessário capturar o drawer pareado nos estados recolhido, ambos os grupos abertos, filtros de visibilidade, confirmações, nome longo e fonte ampliada. Validar restauração após recriação, cancelamento/retorno de navegador, permissão de câmera e navegação de cada filho no APK. O indicador não produtivo agora usa o endpoint efetivo; seleção persistida no login/indisponibilidade continua pendente, conforme `ENVIRONMENT_PARITY_AUDIT.md`. O texto institucional de cinco linhas precisa de inspeção visual com as fontes reais.

Também continuam pendentes entradas por rotas diretas, todas as situações de sessão expirada/visitante, retorno do navegador, matriz visual completa e builds de todos os flavors. A política implementada aqui não certifica a paridade integral do app.

### Revalidação do menu Compose — 08/10/2026

Foi feita uma nova leitura direta de `activity_principal.xml`, `nav_header_principal.xml`, `nav_bottom_principal.xml`, `list_group_side_menu.xml`, `list_item_side_menu.xml`, `ExpandableListDataSideMenu.java`, `DrawerEnum.java` e `SideMenuExpandleAdapter.java` no projeto legado `/Users/ubiracysantos/Projects/cx/`. A ordem, labels, grupos condicionais, lista de filhos, ícones, fontes/cores declaradas, largura e geometrias-base do Compose correspondem ao contrato dessas fontes. As ações internas têm destinos específicos, e política, sobre a CAIXA e saída mantêm confirmação antes da ação.

Foi acrescentado `HomeDrawerUiTest`, executado no Pixel 9 Pro virtual: expande Minha área e Jogo responsável sem fechar o outro grupo; verifica filho da área; toca a Política e cancela; navega por Meus cartões para `payment/cards`; abre Sair, cancela e verifica que logout não ocorreu. O teste usa Activity hospedeira apenas no source set `debug`. `:app:connectedTqsDebugAndroidTest` e `:app:testTqsDebugUnitTest` passaram.

A certificação de layout permanece pendente: o único device ADB anexado é o Pixel 9 Pro virtual, e o app legado instalado nele (pacote `.legacy`, 1.24.0) abre `AppIndisponivelActivity` com “O aplicativo está temporariamente indisponível”. Assim não foi possível abrir o drawer legado para capturar pares de pixels nem confirmar o raster no aparelho físico/na versão de produção 1.25. A implementação está em Compose e os contratos de toque testados passaram; isso não equivale a declarar igualdade visual de 100%.
