# Cart Addition — extração sem mudança de comportamento

Separar coordenação, bolões, apostas individuais, cabeçalho, rodapé, componentes, ajuda e formatadores de CartAdditionRoute.kt. Manter ViewModel e comportamento atuais.

## Arquivos e interfaces

- CartAdditionRoute.kt mantém assinatura/defaults públicos, collectAsStateWithLifecycle, LifecycleEventEffect(ON_RESUME), if por kind e os dois BackHandler nos ramos atuais.
- CartAdditionPoolScreen.kt: LegacyPoolAddition internal, corpo integral e mesmos argumentos/state/callbacks.
- CartAdditionBetScreen.kt: LegacyBetAddition internal, mantendo a condição COMBO, mínimo positivo, insets, estilos e callbacks.
- CartAdditionHeader.kt: PoolHeader internal e HeaderValue privado, mesmos defaults e condições contest>0/onHelp!=null.
- CartAdditionFooter.kt: PoolCartFooter internal, conservando BigDecimal/limite999.99/tamanho14ou9.8sp e ação do carrinho.
- CartAdditionComponents.kt: PoolCartArrow e LegacyHomeButton internal, mesmos Canvas/Path/modifiers/defaults/geometria.
- CartAdditionHelp.kt: cartHelpAction internal, mesma normalização/ids/aliases, callback recebe modalidade original; null/desconhecida continuam sem ação.
- CartAdditionFormatters.kt: formatCurrency(BigDecimal) e String.toDisplayModality internal. Preservar NumberFormat(pt-BR) e normalização literal; não unificar com formatCurrency(Int) existente. formatRemainingTime e drawDateLabel continuam nos arquivos atuais (ViewModel/CartRules), intactos.

## Invariantes

Somente CartAdditionRoute.kt anterior muda em produção, sete novos arquivos no mesmo pacote. Corpos/assinaturas/defaults idênticos, salvo visibilidade de helpers compartilhados. Nenhum ViewModel, regra de reserva, recurso, dependência, gate, callback, estilo/campanha, mensagem ou flag de expiração alterado. Não deslocar/unificar BackHandler ou efeitos. Nenhum novo estado/cache/efeito/wrapper de apresentação.

## Validação

Novos três Android de caracterização antes/depois junto dos62existentes, sem filtro: lifecycle de retomada com ticker suspenso e relógio controlado até expirar, ações e back físico; troca de ViewModel/tipo, combo/mínimo/cabeçalho e fonte do valor na fronteira999.99; aliases/normalização/ids de ajuda, modalidade original e ausência de ação desconhecida/null. Testes JVM/lint/cobertura/check/release do módulo,134JVM app/APKPRDdebug/compilePRDrelease e nove gates. Cobertura inclui todas as classes e Android, mínimo95,01% intacto. Comparar declarações/tokens e hashes, revisão independente final VoltAgent e restaurar/encerrar somente emulador dedicado.
