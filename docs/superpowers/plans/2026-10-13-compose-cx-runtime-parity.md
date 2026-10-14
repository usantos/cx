# Comparação Android CX / Compose — plano de execução

> Execução nesta sessão com systematic-debugging, testes de regressão e revisão final.

**Objetivo:** corrigir divergências observadas em telas/fluxos com os mesmos serviços e configuração do emulador.
**Arquitetura:** manter Domain/Data e navegação Compose; apresentação reproduz a referência CX. Pacotes paralelos para validação, sem modificar o checkout CX.
**Tecnologias:** Compose, Retrofit, Android 16, ADB, testes Android/JVM.
**Referência:** solicitação do usuário e docs/COMPOSE_CLOSURE_20261013.md.

## Restrições

- Branch codex/migracao-compose; nenhuma alteração de backend ou credenciais.
- Comparação real pública primeiro; fixture não comprova transações reais.
- Registrar captura/configuração/estado; não declarar paridade dos estados não observados.

## Foco de revisão

- Mesmo concurso e modalidade nos dois clientes.
- Tipografia não herda estilos de outro host, alterando altura/rolagem.
- Visitante respeita barreiras do CX e mantém intenção após login.
- Erros/redirects não permitem ações com dados antigos.
- Retorno/back e restauração mantêm o destino correto.

## Tarefas

- [x] Capturar entrada, introdução, login, tutorial e Home real em ambos.
- [x] Comparar resultados públicos, navegação de concurso, carrinho e barreiras de visitante; registrar cada diferença antes de corrigir.
- [x] Em HomeV2UiTest, reproduzir largura/altura de texto contra host Material3 padrão com mesma fixture e verificar falha sob CaixaDS antes de corrigir HomeV2Content.
- [x] Corrigir a fronteira de tema Home v2 em HomeV2Screen.kt; repetir teste e capturas reais.
- [x] Para diferenças adicionais, rastrear controller/BO até caso de uso/serviço, criar regressão, corrigir e repetir o fluxo.
- [x] Executar gates e testes pertinentes, revisar diff, registrar evidências e commits com arquivos alterados.

## Registro

- Inicial: árvore limpa no commit cb342f336; nenhum AGENTS.md aplicável.
- CX e Compose PRD paralelos atingiram introdução/login/Home. Concurso real Mega-Sena 3070, R$ 150 milhões em ambos.
- Home: fontes idênticas por SHA-256; CX usa MaterialTheme padrão, host Compose herda Typography CaixaDS. Larguras diferem e banner desloca grade em 38 px.
- Decisão: continuar execução autorizada pelo usuário nesta sessão, sem nova aprovação do plano.

## Atualização 14/10/2026

- Correções verificadas com RED/GREEN: fronteira MaterialTheme Home v2; resumo duplicado no resultado público; seletor de ambientes não PRD; OAuth sem ID token quando scope não pede openid; renovação regular pré-request/401; persistência biométrica após CAS; flags one-shot na codificação SILCE.
- Login PRD real concluído após correção OAuth. Relógio do AVD voltou a apresentar atraso de ~11 minutos por detecção automática; sincronização de teste sem relaxar validação JWT restaurou o login. Houve ANR durante o seletor de callback; reinício do AVD mantém dados.
- Comparação autenticada: Home, notificação/campanha, Minhas apostas vazia no mesmo período e carrinho remoto. Corrigir footer ausente de Minhas apostas, inset vazio e descrição de período duplicando ano.
- Filtro de bolões ainda continha resumo fixo com zero; substituir por componente compartilhado consumindo GetCurrentCartUseCase.
- Auditoria de foreground: o legado mantém flag de background global e a consome na primeira Activity Base. Minhas apostas/detalhe comum não consomem; preservar pendência e processar entrada posterior em rota Base.
- [x] Verificar a integração final com build/gates, suítes Android dos consumidores e capturas após as últimas correções.
- [x] Relatar estados comparados e pendências de homologação sem inferir paridade integral a partir de métricas parciais.

- APK PRD final: tutorial inicial/final, carrossel, detalhe e filtro capturados; carrinho real mantém 9 itens/R$39. Seleção local de duas cotas recalcula o valor; retorno do carrinho preserva detalhe e redefine quantidade para uma. Não houve reserva/pagamento.
- Gates finais: modernCheck + releaseCheck, BUILD SUCCESSFUL em 12m20s, 2154 tarefas após ajuste final do tutorial.
- Catálogo final repetido: 80/80 rotas, zero falhas, backend controlado no AVD de gates. Relatório e lista completa de 138 arquivos preparados para o commit nesta branch.
