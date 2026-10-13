# Home v2 do CX e pendências de paridade — 13/10/2026

## Referência usada

A referência é a **árvore de trabalho atual** de `/Users/ubiracysantos/Projects/cx`,
commit base `b46cc63` (`Ajustes`), incluindo as alterações locais de `HomeV2View.kt`
e as artes corporativas. Não foi usada uma interpretação independente da imagem.

Arquivos de referência: `view/homev2/HomeV2View.kt`, `HomeV2Formatting.kt`,
`HomeV2ModalityOrder.kt` e `controllers/PrincipalActivity.java` (configuração da
home, ações e carregamento dos resultados).

## Implementação

- `-PhomeV2=true` seleciona `BuildConfig.HOME_V2`, padrão `true`, como no CX.
- `-PhomeV2=false` mantém a home anterior. A opção se aplica a todos os ambientes
  e tipos de build; valores diferentes de `true`/`false` falham na configuração.
- A apresentação do CX foi portada para `HomeV2Screen.kt`: fontes, paleta,
  artes, quatro destaques, indicadores, banner condicionado à venda de bolões,
  oito modalidades iniciais, expansão, arraste com autoscroll, ações de
  acessibilidade, resultados e barra inferior. Não há FrameLayout/ComposeView
  intermediário nem layouts XML de tela.
- A ordem persiste com as mesmas chaves `home_v2_layout/modality_order`, através
  de contrato Domain e implementação em `core:preferences`.
- Concursos/prêmios vêm de `HomeViewModel.parameters`; disponibilidade de bolões
  respeita configuração remota, modalidade disponível e concurso aberto.
- Apostar e comprar bolão usam os mesmos casos de uso e decisões do host:
  validações de entrada, sessão/cadastro, suspensão e navegação não são
  substituídas por callbacks que abrem diretamente uma compra.
- Meus jogos/Favoritas preservam o gate de visitante; Carrinho usa sua rota
  existente; Estatísticas abre Resultados e Promoções abre Notificações, como CX.
- Últimos resultados consulta Mega-Sena, +Milionária e Quina com o caso de uso
  existente. O estado fica separado da consulta de detalhe, elimina chamadas
  duplicadas e cancela respostas antigas quando os parâmetros mudam.
- O retorno à home v2 recarrega parâmetros e carrinho, como o `onResume` do CX.
- Os avisos de carrinho local, pagamento não identificado, notificações,
  redirects, falhas de entrada e loading permanecem compartilhados com a home
  anterior, com a prioridade já implementada.
- Os vetores copiados foram registrados no inventário de desenhos. O gate
  admite `aapt:attr` de fill/stroke apenas em vetores registrados, para preservar
  os gradientes originais; continua rejeitando layouts de tela.

```bash
./gradlew :app:assembleTqsDebug -PhomeV2=true
./gradlew :app:assembleTqsDebug -PhomeV2=false
```

## Execução e limites

O CX foi compilado como `prdDebug`/home v2 e executado no `emulator-5554`, em
pacote separado `br.gov.caixa.loterias.apostas.cxv2validation`. Um manifest de
validação temporário ajustou somente os aliases do launcher ao pacote paralelo;
o checkout CX não foi editado. As tarefas Google Services/upload Crashlytics
foram desabilitadas somente por init script temporário. Essa variante não
homologa Firebase, analytics nem notificações push.

Após a introdução, o CX terminou em `AppIndisponivelActivity`, versão 1.25.0,
**LTR-503**. A captura está em
`build/verification/home-v2-20261013/cx-current.png`. Assim, não foi possível
comparar a home CX com concursos reais equivalentes nesta rodada.

O Compose foi executado no pacote separado
`br.gov.caixa.loterias.apostas.composev2validation.hmp`, com backend mock. A
captura e XML de referência local ficam em `build/verification/home-v2-20261013`.
Mock comprova renderização e interação controladas, não homologação do serviço
real ou igualdade pixel a pixel.

## Verificação

- Builds TQS com `homeV2=true` e `homeV2=false`: compilados.
- Testes JVM do host: 64; testes JVM de Results: 14, incluindo independência do
  detalhe, deduplicação e recarga dos resultados.
- Dois testes Android de Home v2: gate visual de bolão desabilitado, concurso do
  serviço preservado ao apostar, detalhe por modalidade e destinos dos atalhos.
- `verifyLegacyBackendParity` e `verifyCleanArchitecture`: aprovados.
- Revisão de código: identificou cache de resultados sem recarga; corrigido e
  revisado novamente sem novos achados confirmados.

O `modernCheck` **não está verde**. A tentativa completa e a continuação com
`--continue` encontraram:

1. `verifyCaixaDS`: cores literais em sete arquivos anteriores à nova tela:
   HomeScreen, HomeDrawerStyle, ShakeMenuRow, RapidConfigurationRoute,
   BettingSportsContent, ResponsibleGamingRoute e StartupStatusScreen.
2. `verifyComposeMigration`: 17 XMLs anteriores fora do inventário permitido,
   incluindo manifest debug, ícones de drawer/campanha e refresh. Os novos
   desenhos de Home v2 foram registrados e deixaram de constar nessa falha.
3. `feature:betting:jacocoDebugCoverageVerification`: 94,64% de linhas, abaixo
   de 95,01% exigidos.
4. `feature:home:mergeExtDexDebugAndroidTest`: `OutOfMemoryError` no D8.

A continuação global foi interrompida após essas falhas; não significa execução
completa de todas as suítes. Uma repetição dos testes específicos perdeu o foco
porque o CX iniciou uma Activity durante o teste; o log confirmou a interferência.
A validação final repetida com o pacote CX de validação parado passou: 64 testes JVM do host, 14 de Results e 2 testes Android, sem falhas.

## O que falta para 100% do legado em Compose

A existência de telas e endpoints **não significa paridade integral**. A lista
abaixo distingue pendências observadas de homologações ainda necessárias; não
classifica como ausente uma implementação que já existe.

| Prioridade | Área | Trabalho restante | Evidência/critério de fechamento |
|---|---|---|---|
| P0 | Pipeline | Regularizar os XMLs permitidos, mover cores para CaixaDS, atingir cobertura de apostas e resolver memória do dex; concluir `modernCheck` e `releaseCheck` | Falhas observadas nesta rodada; executar a matriz inteira sem exclusões |
| P0 | Serviços e negócio | Confrontar o CX atual com os 99 contratos e verificar cada operação pelo caminho tela → caso de uso → repository → data source, incluindo payload, criptografia, headers, parâmetros, flags, erro e redirect | `LEGACY_BACKEND_BASELINE.tsv` e `BACKEND_MIGRATION.md`; o gate atual verifica método/path, não consumo nem semântica |
| P0 | Home v2 | Capturar CX e Compose com o mesmo backend/dados, conferir geometria, insets, fontes, skeleton, rolagem, drag/autoscroll e restauração da ordem após reiniciar | CX real bloqueado em LTR-503; implementação portada, raster ainda não homologado |
| P0 | Autenticação/conta | Homologar retorno SSO, biometria, bloqueio total/parcial, termos, cadastro, troca de sessão, logout/limpeza, transferência do carrinho e prioridade dos avisos | Auditorias `POST_LOGIN_*`, `LOCAL_SESSION_CLEANUP_AUDIT.md` e `VISUAL_PARITY_MATRIX.md`; faltam pares Android autenticados completos |
| P0 | Pagamento/Pix | Comparar cartões mascarados, formulário/CVV, parcelamento e respostas de negócio; Pix pendente/expirado/aprovado/falha; restauração após morte real do processo sem repetir POST e sem cruzar proprietários | `remaining-financial-parity-2026-10-05.md`, `remaining-pix-process-restoration-2026-10-05.md`; cenários controlados não homologam respostas reais |
| P0 | Resgate | Homologar opções por situação/valor, Mercado Pago/RecargaPay/Pix, token/cadastro/retorno externo, lotérica/agência, cancelamento e comprovantes | `RETAIL_PARITY_AUDIT.md`, `PRIZE_CONNECTIVITY_PARITY_AUDIT.md`, `paridade-producao-final-2026-10-06.md`; finais reais ainda pendentes |
| P1 | Apostas | Pares completos de todas as modalidades e concursos especiais; preços/limites, Super Sete, trevos, espelho Lotomania, Loteca, quantidade, Surpresinha, Teimosinha, Shake, favoritas e duplicidade | `remaining-betting-parity-2026-10-05.md`, `exact-betting-parity-2026-10-05.md`; critérios por tela/estado e resposta equivalente |
| P1 | Bolões/marketplace | Validar flags/modalidades, filtros, lotéricas favoritas, geolocalização/permissões, paginação, limite/quantidade de cotas, expiração e retorno ao carrinho | `POOL_ENTRY_PARITY_AUDIT.md`, `CART_LOCATION_PARITY_AUDIT.md`, inventário visual; comparar caminhos reais com fixture equivalente |
| P1 | Carrinho/compras | Conferir resumo/contagem, mínimo, duplicidade, validação remota, combos, favoritos, redirects, compras em processamento e histórico/filtros/paginação | `CART_*_AUDIT.md`, `PURCHASE_HTTP_ERROR_PARITY_AUDIT.md` e matriz visual |
| P1 | Resultados/bilhetes | Homologar histórico, resultados por modalidade/concurso, leitura QR/barcode/manual, permissões, consultas criptografadas, comprovantes/PDF e compartilhamento | `BACKEND_MIGRATION.md`, `MIGRATION_STATUS.md`, catálogo de telas; comparação com o mesmo concurso/bilhete autorizado |
| P1 | Notificações | Capturar inbox e popup com imagens/texto longo, não lidas, reconhecimento, erro/redirect, prioridade frente a pagamento e retorno de navegador; validar push separadamente | `HOME_NOTIFICATION_POPUP_AUDIT.md`, `NOTIFICATION_INBOX_PARITY_AUDIT.md`; não confundir conteúdo local da central com popup remoto |
| P1 | Conteúdo/entrada | Recapturar onboarding/tutorial, indisponibilidade/retry/offline, atualização obrigatória, FilaBRA, FAQ, termos HTML/CSS/links, repassômetro e jogo responsável | `STARTUP_STATUS_PARITY_AUDIT.md`, `VISUAL_PARITY_MATRIX.md`; falta matriz de estados equivalente |
| P2 | Matriz visual e plataforma | Executar pares de loading/erro/foco/teclado/diálogos, acessibilidade, navegação Voltar, mudanças de rede, morte de processo e minificação nos três flavors | `PRODUCTION_VISUAL_PARITY_20261007.md`, `VISUAL_PARITY_MATRIX.md`; relatórios parciais anteriores não comprovam a árvore inteira atual |

Não foi demonstrado um endpoint faltante pelo gate nesta rodada. Também não foi
feita uma auditoria independente completa das regras de todas as 99 operações.
A homologação deve registrar separadamente: implementado, consumindo serviço,
regra validada e visual pareado. Só promover um fluxo a 100% quando os quatro
critérios estiverem satisfeitos com evidência equivalente ao CX.
