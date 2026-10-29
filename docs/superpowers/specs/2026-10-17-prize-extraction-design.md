# Extração do fluxo de resgate de prêmio

Separar `PrizeRedemptionRoute.kt` sem alterar assinatura, owners, regras, efeitos, callbacks, conteúdo visual ou tratamento de falhas. A referência é a fonte local salva em `build/verification/prize-refactor-20261017/PrizeRedemptionRoute.before.kt`, incluindo o trabalho anterior da conversa.

## Organização

- A rota pública permanece coordenadora: coleta de estado, retorno de navegação, consumo único de redirects/decisões, seleção inicial, previews controlados, escolha de etapa e avisos.
- Telas de opções, lotérica, agência, Pix, Mercado Pago e sucesso em arquivos próprios. Recebem estado/callbacks; nenhuma tela recebe ViewModel.
- Componentes compartilhados permanecem no mesmo módulo, com visibilidade interna somente quando necessária.
- `rememberPrizeReceiptManager(viewModel)` encapsula o bloco Compose Android de comprovantes: receipt pendente, cache de URIs, CreateDocument, escrita, abertura e coleta do flow. Expõe somente `openOrDownload(fileName, download)` à rota.

## Contratos

Preservar as chaves e a ordem dos efeitos. Aceite de termos exige confirmação; outros redirects continuam consumidos no efeito existente. Seleção inicial depende da etapa OPTIONS e não substitui preview autorizado. BackHandler e destinos de sucesso Pix/Mercado Pago mantêm a distinção atual.

Os comprovantes continuam em memória da composição, sem rememberSaveable, disco adicional, persistência de permissão ou novo owner. Cache por nome exato; recibos já salvos abrem sem novo download/CreateDocument. Cancelamento limpa o pendente. Gravação precede cache e abertura; stream nulo, falhas de escrita e tratamento silencioso de ausência de visualizador mantêm o comportamento existente. Preservar ACTION_VIEW, application/pdf e FLAG_GRANT_READ_URI_PERMISSION.

Preservar layouts, recursos, estilos, textos, cores, QR/contador/haptics e campos. Não migrar para KMP, alterar ViewModel/domínio/dependências/gates nem corrigir comportamentos preexistentes nesta extração.

## Verificação

Caracterizar PDFs com registry/context controlados, dados sintéticos e arquivos temporários, antes da extração. Executar os18 testes Android de resgate existentes e os novos casos de PDFs antes/depois. Comparar estruturalmente todos os corpos extraídos e a coordenação contra a baseline local. Executar check/cobertura/lint do retail, compilação release da feature, build/teste PRD do host e os nove gates estruturais. Uma revisão independente final. Usar somente AVD dedicado5560; preservar AVD5556 e alterações anteriores, sem commit.
