# Menu principal Compose: fechar paridade com o legado

## Objetivo
Fechar diferenças comprováveis no menu principal Compose comparando-o com a fonte de produção em `/Users/ubiracysantos/Projects/cx/`, e cobrir comportamento/destinos do drawer com testes Compose.

## Referência
`activity_principal.xml`, `nav_header_principal.xml`, `nav_bottom_principal.xml`, `list_group_side_menu.xml`, `list_item_side_menu.xml`, `ExpandableListDataSideMenu.java`, `DrawerEnum.java` e `SideMenuExpandleAdapter.java` do projeto legado. Não declarar homologação de pixels físicos sem captura pareada no mesmo aparelho/estado.

## Tarefas
- [x] Conferir ordem, labels, visibilidade por configuração, ícones, estrutura e destinos do menu Compose contra o legado.
- [x] Adicionar teste instrumentado Compose que expande os dois grupos, navega por “Meus cartões” e verifica cancelar Política/Sair sem executar suas ações.
- [x] Comparar os valores de layout com o XML legado; nenhuma divergência visual pôde ser confirmada por screenshot, então métricas calibradas anteriormente não foram alteradas sem par confiável.
- [x] Rodar testes JVM e instrumentados e atualizar a auditoria com os limites da captura pareada.

## Evidência

`./gradlew :app:connectedTqsDebugAndroidTest :app:testTqsDebugUnitTest --console=plain --quiet` passou no Pixel 9 Pro AVD. O app legado `.legacy` instalado no emulador é 1.24.0 e cai na tela temporária de indisponibilidade, e nenhum aparelho físico aparece em `adb devices`; a captura do drawer de produção 1.25 continua pendente para homologação raster. Ver `docs/HOME_NAVIGATION_PARITY_AUDIT.md`.
