# Continuação da comparação CX / Compose pelo Experimente

> Continuação posterior: [CaixaDS, gates e validação integrada](DS_CONSOLIDATION_20261014.md). O relatório registra os testes desta árvore, a centralização dos componentes, os dois raster preexistentes ainda pendentes e os limites da homologação. As capturas reais e decisões abaixo preservam o escopo original desta etapa.

Branch `codex/migracao-compose`; base desta continuação `911d5bc31`. Complementa o relatório runtime anterior, preservando seu escopo de 138 arquivos.

## Escopo e evidências

Apps PRD Debug reais, pacotes paralelos, Home v2 do CX (`-PhomeV2=true`), AVD Android16 SimloComposeValidation/emulator-5556. CX somente leitura; emulator-5554 do usuário preservado. Testes controlados em5558. Nenhuma cobrança, resgate ou reserva real; apenas apostas locais criadas pelo teste em Experimente.

O menu Experimente permite comparar os caminhos públicos, volante, preço e carrinho local. Não fornece os dados privados de aposta premiada, comprovante, pagamento ou resgate. Evidência de fonte/teste controlado permanece separada da captura com serviços reais.

## Correções e validação desta continuação

| Área | Correção | Evidência |
|---|---|---|
| RecargaPay | Normaliza nascimento do titular para yyyy-MM-dd conforme CX, preservando dia/calendário ISO | RED/GREEN do corpo enviado; Data197, cobertura95,374% |
| Comprovantes | Disable-Crypto:true nos mesmos GET; Base64 decodificado e PDF binário preservado; inválido falha | Data/Network/Tickets356 JVM +10 Android |
| Teimosinha confirmada | Campos reais quantidadeTeimosinhas/troca/situação determinam original/filha e avisos do primeiro concurso; premiada excluída | Fonte ApostaUtils/controller + regressões controladas |
| Pix de compras | Relógio do servidor preservado pelo envelope; prazo monotônico não é prorrogado por recarga/restauração | Regressões server15:00/exp15:10/aparelho15:15 e expiração |
| Biometria | Fallback SSO único, matrícula, confirmação de desativação, refresh semID vinculado à credencial, rotação posterior preservada | Auth99+Account68 JVM;11+14 Android; revisão/fix/revisão |
| Simulador | Sucesso limpa volante antes de Entendi, mantém destino e recarrega carrinho; edição conserva VM/concurso especial apesar de limpar intenção global | Betting110/97,2576%, UI do volante e5 do consumidor; Mega e+Milionária reais capturadas |
| +Milionária | Nome enum+milionária e margem comum dos trevos conforme CX | Descrição947–1076px coincide com CX,6números/2trevosR$6 |
| Carrinho | Indicadores condicionais/ícones/6dp, Futura Bold herdada do tema CX, centralização arredondada, extras na coluna correta, ação visitante44dp+16dp | PRD final: Teimosinha y982–1038 e largura317px coincidem; título+Milionária1119/trevos1270/toolbarcentro1166.87JVM+53Android antes do ajuste1px;16UI finais passaram; cobertura fresca será registrada no gate integrado |
| Dados pessoais | Recusa de login pelo menu fecha o formulário e volta à Home; callback consumido uma vez | App111 JVM +3 Android e Account68 JVM; par real cx-experimente-personal-decline/compose-task7-personal-decline-home |
| Volantes e Super Sete | Toolbar56dp, fontes/margens, borda3px e distribuição dos pixels residuais conforme widgets nativos; contador e cabeçalhos fixos ao rolar | Betting110 JVM +20 Android,97,2576%; PRD Mega, mês e Super Sete inicial/rolado, dígito9 selecionado, posições conferidas |
| Loteca | Seletor15..26 independente de preços, redução limpa/preserva alvo, aviso uma vez, Limpar Loteca/Lotogol; fonte Material real e escudos35dp, margens/card303px/pitch441px | Betting116 JVM/97,773475%,26 UI +9 visuais e6 UI da correção; PRD confirma seleções/redução/Limpar e card/nomes/UF/títulos nas posições do CX. Completar aleatório também corrigido: prioridade/alvo/preços e aviso único, seleção preserva UF/altura da linha;129 JVM,10 Android completos+1 adicional,97,7033%. PRD15/R4 e16/R8 comprovados; captura final valida contadores e linhas selecionadas |
| Áreas privadas no Experimente | Drawer abre apresentação inicial sob login, ações Home/bottom mantêm Home; Não fecha somente destino aberto. Carga adiada e respostas vinculadas à sessão | PRD sete destinos/Não→Home, welcome→form e bottom capturados;18 Android+16 JVM de lease+6 Loteca. Restauração de outra sessão corrigida:14 Android e revisão aprovados |
| Seletores | Confirmação começa desabilitada; viewport170dp mostra0/2/3 completos; tap/drag escolhem antes do commit, título/botões nativos | Betting108 JVM/97,225%,6 Android, captura real pacote Compose conferido e3concursosR$18 |

A recusa de localização foi comparada: aviso de território nacional → Entendi → Home, mantendo carrinho local, sem transação. Os dois carrinhos de teste foram preparados com Mega3concursosR$18 e+MilionáriaR$6, total2cards/R$24.

No CX PRD, a geração aleatória da Loteca também foi comparada com alvo16: 14partidas,12simples,2duplas eR$8, conforme preços do serviço. O carrinho continuou2cards/R$24; a cartela foi descartada sem inclusão. O Compose PRD completou os alvos15/R$4 e16/R$8. Na captura final, o alvo16 sorteou outra combinação válida da tabela do serviço:13simples e1tripla, comR$6. O preço acompanha a combinação gerada; não se exige a mesma escolha aleatória entre execuções. A revisão e a correção final também conferiram a apresentação após seleção, preservando UF e altura das linhas.

Validação integrada, entradas privadas do menu, complemento aleatório Loteca e métricas de indisponibilidade em andamento; este documento será atualizado após o fechamento dos testes.

O CX apresentou a tela de indisponibilidade `LTR-503` ao retornar à Home após o teste local da Loteca (`cx-loteca-random-discard-home`). Isso registra o estado exibido pelo aplicativo; não é uma confirmação do status HTTP da resposta. As comparações anteriores foram capturadas antes desse estado. Depois de uma nova tentativa, o Compose retornou ao login e a comparação PRD da Loteca foi retomada com sucesso.

## Registro de captura

Capturas/APKs ficam ignorados em `build/verification/compose-cx-runtime-20261013/`. A revisão do pacote da árvore XML identificou capturas inicialmente nomeadas compose-task4-* que eram CX em primeiro plano; elas foram excluídas como evidência Compose. A repetição usa compose-task4-verified-* e valida o pacote esperado antes de tocar/capturar. O controle Confirmar (pai semântico clicável) está desabilitado inicialmente; o Text filho isolado não representa o estado do botão.

## Reprodução dos testes de geometria

Os três testes de `VisitorLoginDialogGeometryUiTest` e os testes que usam `LotecaNativeOracle` ou `StartupUnavailableGeometryTest` consultam recursos, temas e classes do APK CX de validação instalado como oráculo independente. O emulador 5558 desta rodada já tem o pacote `br.gov.caixa.loterias.apostas.cxv2validation`. Em um emulador novo, instalar antes o APK CX correspondente; ele não é uma dependência de execução do Compose.

```sh
adb -s emulator-5558 install -r /caminho/cx-prd-real.apk
adb -s emulator-5558 shell pm path br.gov.caixa.loterias.apostas.cxv2validation
```

O APK local desta rodada está ignorado em `build/verification/compose-cx-runtime-20261013/cx-prd-real.apk`. Não remover a verificação nem substituir o oráculo por valores copiados do Compose.

## Limites da declaração de equivalência

A validação de refresh sem ID token usa a resposta do endpoint de token configurado, transportada por TLS e vinculada à credencial/contexto/identidade; não foi implementada verificação criptográfica independente da assinatura JWT.

Ainda não se pode afirmar100% dos estados reais: faltam dados equivalentes de comprovantes/apostas premiadas, pagamentos/resgates em homologação, entrega FCM e biometria/matrícula em dispositivos apropriados, além de TQS/automação com serviços disponíveis. Experimente não substitui esses estados.

## Decisões tomadas durante a continuação

Registro completo das decisões do plano, na ordem em que foram tomadas. Todas as alterações permanecem reversíveis na branch de migração.

1. Prosseguir sem nova aprovação do plano, pois o usuário autorizou a continuidade até a equivalência. Se incorreto, o custo é desfazer alterações locais.
2. Incluir a correção dos seletores após reproduzir o erro de interação no emulador. Se incorreto, será necessário rever o seletor.
3. Reproduzir a apresentação inicial observada do picker: primeiras três opções inteiras, sem seleção inicial; manter seleção por interação. Se incorreto, será necessário ajustar a apresentação inicial.
4. Incluir sucesso do simulador e apresentação do carrinho, preservando combo/bolão/Rapid sem evidência de comportamento comum. Se incorreto, será necessário rever esses consumidores.
5. Na recusa de login, fechar somente o destino aberto pela política de visitante; preservar a proteção dos dados privados. Se incorreto, será necessário rever a navegação.
6. Usar o fragmento/cartela realmente inflado pelo simulador como referência da geometria do Super Sete. Se incorreto, será necessário refazer a geometria; Domain permanece intacto.
7. Corrigir o controle de palpites ausente na Loteca, preservando endpoints, parâmetros e preços existentes. Se incorreto, será necessário rever a UI da Loteca.
8. Preservar o aviso de aumento da Loteca comprovado na captura real, corrigindo a inferência inicial incompleta da fonte. Se incorreto, será necessário rever o aviso.
9. Resolver a fonte dos indicadores pelo tema efetivo do CX: Futura Bold herdada, em vez de presumir Roboto pela ausência de fontFamily no XML. Se incorreto, será necessário rever a fonte.
10. Abrir a apresentação das áreas protegidas pelo drawer com carga privada adiada; preservar o aviso sobre a Home nas ações diretas. Se incorreto, será necessário rever a origem de navegação e a carga adiada.
11. Oferecer15..26 palpites conforme o controller, mesmo com tabela de preços esparsa; continuar validando valor/combinação pelo backend. Se incorreto, será necessário atualizar o limite com uma futura regra do legado.
12. Manter contador e cabeçalhos do Super Sete fixos, deixando somente a cartela rolar, conforme o CX. Se incorreto, será necessário rever o layout de rolagem.
13. Conferir a tipografia e as medidas do aviso de login por oracle Android nativo; permitir ajuste no componente necessário sem confundir largura semântica preenchida com largura do texto. Se incorreto, será necessário rever a aparência dos diálogos compartilhados.
14. Corrigir na tarefa Loteca a apresentação inicial do seletor compartilhado: nenhuma linha destacada e confirmação cinza antes da escolha, preservando os contratos já testados. Se incorreto, será necessário rever as cores do picker.
15. Conferir a captura do picker de forma síncrona antes de alterar produção; o código existente já prevê o estado visual inicial correto. Caso passe no oracle, corrigir somente a captura/teste. Se incorreto, o custo é uma regressão visual não detectada e nova verificação. Esta decisão condiciona a anterior à reprodução real.
16. Reusar o diálogo legado de85% no aviso de aumento de quantidade, pois a captura real mostrou que o Compose usava um alerta de largura diferente. Preservar mensagem e callback. Se incorreto, será necessário rever a aparência do aviso.
17. Incluir a geração/complementação local da Loteca pelo rodapé, pois o CX permite esse fluxo e o Compose o desativa. Seguir alvo e prioridades do algoritmo nativo sem enviar aposta. Se incorreto, será necessário rever a seleção aleatória e o cálculo correspondente.
18. Reusar o diálogo de85% nas mensagens de texto do volante (saída, sucesso e duplicidade), mantendo os callbacks; preservar o componente de HTML sem evidência específica. Se incorreto, será necessário rever a apresentação dessas mensagens.
19. Corrigir as métricas da tela de indisponibilidade revelada no emulador, seguindo o XML realmente usado pelo CX; preservar o estado, as regras de acesso e a nova tentativa. Se incorreto, será necessário rever a aparência dessa tela.
20. Separar o callback do drawer das ações diretas da Home, mantendo o layout: os dois caminhos têm comportamentos nativos diferentes antes do login. Se incorreto, será necessário rever a origem da navegação.
21. Proteger também os estágios assíncronos de carga com a sessão capturada, bloqueando consultas e publicação antigas depois de trocar de sessão; a UI isolada sozinha não basta. Preservar os demais consumidores e a renovação comum de token. Se incorreto, será necessário rever o ciclo de carga e da sessão.

22. Incluir na correção da Loteca a apresentação das linhas já montadas após seleção, pois completar a cartela revelou que o Compose escondia a UF e reduzia a altura imediatamente, enquanto o CX atualiza somente o estado visual nessa etapa. Preservar o comportamento inicial e de nova vinculação observado no legado. Se incorreto, será necessário rever o ciclo visual das linhas e a rolagem.
