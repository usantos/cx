# Execução dos planos de paridade com produção

Solicitação: fazer todas as pendências, separadas por planos.

- [Layout e diálogos de produção](2026-10-05-01-layout-producao.md)
- [Modalidades e conteúdo de produção](2026-10-05-02-modalidades-conteudo-producao.md)
- [Cadastro, conta e sessão de produção](2026-10-05-03-conta-sessao-producao.md)
- [Compras, pagamentos e resgates de produção](2026-10-05-04-compras-resgates-producao.md)

## Registro de execução

- Referência preservada: versão1.24.0/230102400, SHA2e8ef3bd96c3db48a4391695429bf8531ccae72a821e3683a742bc61eb90dc3e.
- Evidências desta rodada: build/verification/production-plans-20261005.
- Ruling: execução contínua já autorizada pelo usuário — planos não aguardam nova aprovação. Sem commits pois o checkout contém migração anterior não registrada; preservar alterações e documentar validação, sem misturar escopos no Git.
- Ruling: pacote oficial de produção usado para leitura; finais de transações ficam em doubles locais — a autorização para paridade não exige comprar ou resgatar dinheiro real.
- Pre-flight: DS compartilhado é propriedade do coordenador; áreas account, betting, payment/retail/tickets têm ownership separado. Apenas coordenador executa Gradle/ADB.

## Resultados confirmados durante execução

- Modalidades: rodada final17/17 Android; somadas as quatro regressões anteriores de diálogo/roda/carregamento sem alterações,21 cenários únicos. Prompts Dia/Lotofácil coincidem integralmente no recorte específico depois da correção do arredondamento da margem. Matriz completa permanece no plano02.
- Conta: rodada final11/11 Android e55 JVM; teclado não traz Atualizar acima do IME, solicitação de limite usa fundo/cor/padding observados e uma linha no perfil450dpi.
- Sessão:49 JVM auth e54 host, incluindo retorno do segundo plano e reconstrução do aviso sem gravar credenciais ou invalidar sessão substituta. O aviso de expiração também foi observado em produção nesta rodada.
- Financeiro: Pix11 + cartão3 + cartões salvos3 Android verdes. Compra finalizada e seis estados financeiros:15/15 Android após corrigir ordem dos dados, faixa da modalidade, estrela, ícones e a origem do conteúdo. O APK trunca a Guideline56dp e arredonda a toolbar; correção restrita ao detalhe da compra, sem offset constante.
- A conta de produção possui um prêmio consultável deR$7. Capturadas as opções de Mercado Pago/Lotérica/Agência e o formulário Mercado Pago sem resgatar. Pix não era oferecido nessa aposta. Lotérica/Agência geram código ao avançar e não foram acionadas.

Os registros acima descrevem a evidência mais recente; textos históricos com GREEN pendente nos relatórios são preservados como sequência RED→correção→GREEN. Conclusões finais e artefato serão registrados após a última compilação.

## Critérios que ainda impedem certificar100%

- Raster integral de todos os diálogos, rolagens e estados de foco/erro/carregamento; os recortes certificados não representam todas as telas.
- Seleção da Loteca em concurso aberto e pareamento completo do espelho Lotomania e dos demais complementos/quantidades. Federal não aparece nas dez modalidades oferecidas pelo app instalado.
- Cadastro completo e retorno real por provider/biometria, sem usar gravações de cadastro como comparação visual.
- Compra Pix pendente/expirada e finais de resgate com registros equivalentes disponíveis na conta. O prêmio observado oferece Mercado Pago/Lotérica/Agência; avançar Lotérica/Agência gera código e confirmar Mercado Pago efetua resgate.
- Ajuda/termos completos e todos os resultados/concursos pareados com a navegação real. Recursos compilados conferidos não equivalem a certificação de todo HTML renderizado.

Esses itens permanecem abertos nos planos. Testes controlados e consulta do APK cobrem implementação e contratos; não substituem a comparação dos estados de produção indisponíveis. Nenhuma compra, cobrança, resgate ou atualização cadastral real foi efetuada.

## Fechamento de execução — 06/10/2026

Última bateria XCover: conta11, modalidades21, pagamento17, compra15 e prêmio17 testes Android, todos GREEN (81 execuções aprovadas nas cinco suítes). JVM final:873 testes e zero falhas/erros. `verifyComposeMigration`, `verifyCaixaDS`, `verifyLegacyUiInventory`, `verifyCleanArchitecture`, `verifyLegacyBackendParity`, `compileAutomacaoDebugKotlin`, `compileAutomacaoReleaseKotlin`, `assembleAutomacaoDebug` e montagem dos APKs Android de teste passaram. Logs foram copiados para `build/verification/production-plans-20261005/logs/`.

Métricas físicas recalculadas para os pares de modalidades e prêmio foram adicionadas à mesma pasta. Os resultados comprovam as correções específicas e a execução selecionada; não comprovam igualdade visual completa nem estados transacionais ausentes. O relatório `docs/paridade-producao-final-2026-10-06.md` enumera por item a alteração, o status e a evidência exata que falta.

Ao encerrar, o XCover voltou à Home do pacote oficial e `stay_on_while_plugged_in` foi restaurado para `0`. A versão de validação permaneceu como pacote paralelo. Nenhum pagamento, compra, resgate ou atualização cadastral de produção foi efetuado.
