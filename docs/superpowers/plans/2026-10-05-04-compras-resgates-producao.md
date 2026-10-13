# Compras, pagamentos e resgates de produção — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Conferir todos os estados financeiros e corrigir diferenças comprovadas sem efetuar transações.

**Architecture:** Comparar o binário instalado, corrigir componentes existentes e validar sem substituir contratos por mocks de produção. Plano com execução contínua já autorizada pelo usuário; status por evidência.

**Tech Stack:** Kotlin, Jetpack Compose, CaixaDS, JUnit, AndroidJUnitRunner, ADB.

**Spec:** docs/exact-logged-device-status-2026-10-05.md

**Files:** feature/payment, feature/retail, feature/tickets, core/design-system/CaixaDSLegacyPixPanel.kt.

**Interfaces:** PaymentUiState, SavedPixRestoration, purchaseId, AuthenticatedSessionIdentity.

## Global Constraints
- Referência principal: APK de produção instalado no XCover7, pacote br.gov.caixa.loterias.apostas, versão1.24.0/230102400. Fonte legado apenas complementa.
- Preservar checkout e alterações anteriores. Sem reset, commit, push ou reinstalação do oficial. Candidato somente pacote paralelo/debug.
- Não confirmar compra, pagamento, resgate, favorita, cadastro ou alteração cadastral em produção; comparar telas de leitura e usar doubles locais para mutações.
- Apenas coordenador usa ADB/Gradle. Cores/fontes no CaixaDS; sem novos layouts XML de tela.
- Aprovação funcional, comparação física e conteúdo equivalente são resultados separados. Não declarar100% com recortes incompletos.
- Toda interface nova ou alterada neste plano deve ser feita em Jetpack Compose usando o CaixaDS; não adicionar layouts/widgets Android, `AndroidView`, `Dialog` nativo ou `RecyclerView` para renderizar a interface.

## Review Focus
- Mensagens/literais/HTML e fontes exatamente como o binário.
- Dados distintos não podem passar por pares equivalentes.
- Teclado/foco/scroll não podem esconder ações ou perder seleção.
- Nenhuma restauração pode repetir mutação financeira ou trocar proprietário.
- Gates existentes e caminhos reais de navegação permanecem íntegros.

### Task1: Referência de leitura e identidade de dados
- [x] Conferir Meus Cartões salvos, compra finalizada/detalhe e o prêmio R$7 existente sem confirmar ações financeiras.
- [ ] Parear cartão/CVV e formulário Mercado Pago com os mesmos dados permitidos da produção; os testes controlados usam valores fictícios.
- [ ] Parear Pix pendente/expirado/aprovado/falha e resgates finais apenas com registros equivalentes disponíveis; essa conta não oferece Pix no prêmio observado e os outros finais executam operações.
- [x] Conferir recursos instalados e fontes/métricas também quando transação não existe; marcar par oficial indisponível.

### Task2: Correções e recuperação
- [x] Criar RED para diferença concreta de geometria ou callback; corrigir UI sem alterar endpoints/cobrança.
- [x] Executar payment/retail/tickets JVM e testes Android controlled, sem envio real. Rodadas finais: payment17, retail17, tickets15 Android; payment88, retail50 e tickets41 JVM, sem failures/errors.
- [x] Comprovar ausência de segundo POST e restituição do estado/timer após morte de processo, com owner explícito e limites de autenticação registrados.

## Evidência de execução

Auditoria e limites em `docs/financial-production-audit-2026-10-05.md`. APK comprova margem extra16dp e literais compilados do Pix. Coordenador observou RED JVM11/1falha, Pix físico11/3falhas e compra finalizada com ordem/rótulos incorretos. Correções aplicadas após RED: mensagens, recuo do aviso, retorno Pix comum para sistema/toolbar e renderizador restrito de compra numérica. GREEN e comparação das novas capturas dependem de execução pela raiz; tarefa de testes permanece aberta. Par oficial obtido para compra finalizada; Pix pendente/expirado e resgates continuam sem transação equivalente. Morte física/restauração comprovadas apenas no namespace mock atestado em `process-probe/xcover-run-2` e `emulator-run-5`: PID diferente, timer decrescente e POST restaurado0 após inicial1. Owner sintético guardado no mock; autenticação real não é persistida/fabricada por esse teste.

Atualização financeira: pares oficiais obtidos também para Meus Cartões e prêmio7/Lotofácil3797/11acertos com Mercado Pago/Lotérica/Agência; o formulário Mercado Pago foi aberto em leitura, sem clicar Resgatar prêmio. Lotérica/Agência não foram abertas porque seus callbacks geram código antes da navegação. Pix financeiro permanece sem transação equivalente. Cartões3/3 GREEN e retail9/9 GREEN funcional foram informados pelo coordenador, mas o novo raster revelou divergências de cabeçalho/tabela/opções/formulário. RED adicional retail14/4 confirmado em `/tmp/production-prize-layout-red-xcover.log`; após autorização, PrizeRedemptionRoute corrige Concurso, tabela de duas colunas/divisor, fontes herdadas/específicas, borda2dp/ícones exatos, slot20dp, viés vertical0.1 do formulário e rótulo de ação em uma linha. Nenhum contrato/endpoint/callback de ViewModel mudou. Fonte congelada para GREEN físico14 e captura nova; sem alegação de paridade total antes dessa comparação. Feature/tickets transferida à auditoria de conta/coordenador para investigar1px restante; financeiro não altera esse módulo a partir da transferência.

Revisão adicional do resultado de prêmio: o mapper instalado usa descricaoAcerto, quantidadeAcertos como frequência (não quantidade de números acertados), prefixo1º/2ºSorteio na Dupla Sena e normalização da Loteca. RED JVM29/3 seguido GREEN do core/data completo165. RED Android16/2 também comprovou concursos não premiados indevidamente presentes no range e como linhas vazias. A projeção visual agora se restringe aos concursos premiados, sem remover placeholders do modelo usado por outros consumidores. GREEN final dessa projeção e dos helpers nativos ainda deverá ser registrado pelo coordenador.

Revisão final: RED JVM29/3 da descrição/frequência/sorteio e Android16/2 do range/linha vazia confirmados pela raiz antes das respectivas correções. Mapper normaliza descricaoAcerto conforme DEX primário; placeholders permanecem no domínio com marca explícita isAwarded=false e somente projeção exibida filtra. Fonte final usa DS nativo para lineSpacing de resultado e métricas do painelMP, vetor/bitmap originais e ellipsis scoped. GREEN14 anterior já informado pela raiz; final16, gates e raster pós-correção estão sob execução do coordenador. Sem mudanças de endpoints, submits, IDs, autenticação ou transações oficiais.


## Resultado final 06/10/2026

Os cinco gates (`verifyComposeMigration`, `verifyCaixaDS`, `verifyLegacyUiInventory`, `verifyCleanArchitecture`, `verifyLegacyBackendParity`), os 873 testes JVM selecionados, `compileAutomacaoDebugKotlin`, `compileAutomacaoReleaseKotlin` e `assembleAutomacaoDebug` passaram. Payment, Retail e Tickets tiveram GREEN no XCover (17,17,15 testes nesta rodada); a tela de prêmios também passou uma regressão física de posição: término da área roxa em y=880 no perfil1080×2408/450dpi, igual ao PNG instalado. A execução integral do Retail é registrada no log `/tmp/production-retail-final-green-xcover.log`.

Ainda não se certifica igualdade visual total: a comparação de tela completa do prêmio oferece opções 97,95% de pixels dentro de1RGB e formulário Mercado Pago 98,23%; seus toolbars ficam em 95,69%/95,32% dentro de1RGB. Capturas do formulário têm e-mails diferentes (produção versus fixture), portanto não são pares visuais idênticos. O Mercado Pago foi aberto somente em leitura; `Resgatar prêmio` não foi acionado. Lotérica/Agência também não foram avançadas porque geram código.

CVV, mensagens finais Pix, compra em andamento e encerramento dos resgates permanecem sem pareamento de dados equivalente no APK instalado. Os testes de processo e estado financeiro são doubles controlados e não demonstram reautenticação real após morte do processo.

### Fechamento da rodada final — 06/10/2026

Os logs físicos finais copiados em `build/verification/production-plans-20261005/logs/` confirmam `payment 17`, `retail 17` e `tickets 15` testes aprovados no XCover. A cobertura payment reúne os11 casos Pix controlados, cartão/CVV e cartões salvos; não substitui transação equivalente de produção. A compra finalizada passou seu par físico e estados controlados.

A comparação do prêmio R$7 foi recalculada em `prize-final-raster-metrics.json`: opções, corpo97,9504% dentro de1RGB e toolbar95,688%; formulário Mercado Pago, corpo98,2287% e toolbar95,3217%. A posição do término do bloco roxo em y=880 foi aprovada por assertion física. O fixture e o e-mail da produção não são iguais. Mercado Pago permaneceu em leitura; Pix não estava disponível nesse prêmio; Lotérica/Agência não foram avançadas por gerarem código. Os finais dos quatro resgates, o Pix pendente/expirado/aprovado/falha e a paridade do mesmo cartão/CVV seguem sem pares seguros/equivalentes, não sendo certificados por testes controlados.

O relatório `docs/paridade-producao-final-2026-10-06.md` é a matriz consolidada dos itens corrigidos e pendentes. Nenhuma transação oficial foi submetida.

### Continuação Compose + CaixaDS — 06/10/2026

O painel Pix e os diálogos de HTML, favorita e seleção de quantidade/Teimosinha agora renderizam com Compose e tokens/tipografia do CaixaDS. A lista e a roleta de Teimosinha preservam a seleção pendente, o gesto de deslizar, o encaixe da opção central e a confirmação explícita. `:app:compilePrdDebugKotlin` e os testes físicos `BettingNativeOptionsTest` (2) e `BettingWheelSwipeTest` (1) passaram no XCover7. Os testes usam callbacks locais; nenhuma aposta ou operação financeira foi enviada. Ainda falta parear as respostas de negócio com produção e revisar os demais pontos de `AndroidView` existentes fora destes componentes.

Na continuação, a rota principal de aposta, cartões Timemania/Loteca, busca de time, seletores do Rapidão e de apostas favoritas, checkbox/textos auxiliares da conta, ícone do carrinho e tela de termos foram migrados para Compose/DS. O leitor de código agora usa `CameraXViewfinder` Compose, mantendo análise CameraX/ML Kit. `:app:compilePrdDebugKotlin`, `:feature:betting:compileDebugAndroidTestKotlin` e testes JVM de betting/account/design-system/support passaram. O XCover7 voltou a `Dozing` com keyguard e a suíte física mais recente não encontrou hierarquia Compose visível; as telas migradas precisam de recaptura antes de marcar paridade visual como concluída. O único `AndroidView` no código de produto continua na WebView de FilaBRA para executar a página JavaScript do serviço remoto.
