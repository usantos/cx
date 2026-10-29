# Consolidação CaixaDS e gates da migração

**Objetivo:** corrigir os gates atuais, centralizar os padrões reutilizados apontados pela auditoria e validar a árvore integrada sem alterar a apresentação do legado.

**Execução:** direta nesta sessão, autorizada pelo usuário. Continuação no checkout `codex/migracao-compose`, preservando a alteração não commitada da Home e os três documentos preexistentes. Não criar outra árvore que perca essas mudanças. Não publicar nem realizar transações reais.

**Arquitetura:** CaixaDS recebe o seletor de opções, botão de volante, campos/rodapé/seleção de conta e tema compatível com Home v2. Features conservam estado de negócio, textos, navegação e composição de tela. Componentes movidos mantêm dimensões, fontes, tags, callbacks e estados; sem substituir variantes legadas por defaults Material.

**Referência:** solicitação do usuário nesta conversa e `COMPOSE_CX_EXPERIMENTE_PARITY_20261014.md`.

## Restrições

- Manter todos os gates, operações, flavors e limiares de cobertura.
- Permitir manifests de oráculo somente em dois caminhos androidTest conhecidos, contendo exclusivamente a query do pacote CX de validação.
- Preservar fonte, alpha, arredondamentos de pixels, teclado/cursor, escolha pendente e confirmação desabilitada dos componentes.
- Usar AVD dedicado `SimloComposeGates`, porta 5558; preservar o emulador 5554.
- Não confundir smoke/mock, build ou SSIM pontual com paridade integral.
- Medidas de composição de tela não serão convertidas mecanicamente em milhares de tokens sem significado; os padrões reutilizados pertencem ao DS.

## Tarefas

### 1. Gates

- [x] Registrar reprodução das falhas atuais e conferir manifests.
- [x] Autorizar estruturalmente apenas as queries dos oráculos de betting/onboarding; rejeitar componentes, permissões e pacotes adicionais.
- [x] Remover cor literal do consumidor ao mover o seletor para o DS, reutilizando a cor exata existente.
- [x] Executar gates; exercitar também rejeição de manifest ampliado e layout XML não autorizado.

### 2. Componentes e tema

- [x] Mover seletor inteiro para `CaixaDSLegacyOptionDialog.kt`, mantendo algoritmo de escolha/scroll e tags.
- [x] Mover botão de volante para `CaixaDSLegacyBettingButton.kt` e trocar consumidores.
- [x] Mover campo monetário, campo de conta, aviso, checkbox, seletor genérico e rodapé para DS; manter `Gender` e as regras/formatação de domínio fora do DS.
- [x] Centralizar estilo/gradiente da conta e tema Home v2, incluindo fontScale e Typography de compatibilidade.
- [x] Acrescentar previews e documentação dos contratos; executar testes existentes de geometria/interação (dois raster preexistentes permanecem pendentes, registrados no relatório).

### 3. Validação e relatório

- [x] Executar `modernCheck releaseCheck --offline --continue` na árvore final, sem exclusões.
- [x] Executar Android de DS, conta, betting, onboarding e Home; instalar oráculo CX no AVD dedicado quando necessário.
- [x] Executar smoke das 80 rotas com backend controlado e registrar variante/flags (79/80 de primeira; favoritos aprovado na repetição dirigida após timeout do Android).
- [x] Revisar diff final e inventariar o que continua como composição específica de tela.
- [x] Atualizar status/checklist/matriz com evidências desta rodada e pendências reais de homologação.

## Foco da revisão

- Picker sem escolha inicial: Confirmar permanece desabilitado, tap e drag preservados.
- Campo de moeda: alteração e seleção do cursor continuam iguais; nenhuma regra de limite passa ao DS.
- Botões: tamanho em dp/sp, fontes, tags e ícones seguem o consumidor original.
- Home: variante Material e fonte fixa preservadas, com exceções solicitadas (concurso fechado e Dia de Sorte branco).
- Gate: manifests de teste permitidos não abrem caminho para layouts, permissões ou Activities.

## Registro de decisões

- Composição específica de tela e controles Material configurados para um único contexto não são automaticamente defeitos do DS; a centralização prioriza os padrões visuais reutilizados identificados. O relatório final deve quantificar o resíduo sem declarar DS integral.
- Extrações preservam corpos e os testes existentes como oráculo; não adicionar testes que apenas repitam os corpos extraídos. Novas regras dos gates serão verificadas com entradas negativas reais.
- Barra de suporte e HTML também centralizados: ambos possuíam mais de um consumidor. A montagem do documento e as decisões de tela continuam na feature.
- A revisão independente identificou que o novo gate não reconhecia `fun <T> CaixaDS...`; regex corrigido antes da conclusão, com fixture negativa prevista.
- Primeira execução integrada atingiu um ANR do SystemUI no AVD dedicado, comprovado por screenshot e foco de janela. Sete testes do carrinho falharam durante a interferência/recuperação. Repetir a suíte e o gate completo após recuperar o AVD; não alterar nem desabilitar asserções por essa interferência.
- Repetição integrada aprovada em 5min17s, 2.154 tarefas; 54 testes do carrinho verdes. A revisão do script também substituiu asserts por verificações explícitas, validadas com PYTHONOPTIMIZE=1 em 13 casos.
- Testes raster de apostas requerem 1080×2408/450dpi/fontScale1. Cinco casos de grade passaram nesse perfil; dois testes de origem do texto mantêm 95px esperados contra 93px observados. O mesmo resultado foi reproduzido com os fontes de apostas da base anterior, depois restaurados byte a byte e conferidos por hash. Preservar a expectativa e registrar a pendência, em vez de alterar layout/teste sem reconciliar o oráculo nativo.
