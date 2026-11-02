# Extração da apresentação de pagamentos

Separar PaymentDataRoute.kt local de 588 linhas em coordenação, métodos, cartões salvos, painel Pix e formatadores, preservando o ViewModel e comportamento. Snapshot/hashes em build/verification/payment-data-refactor-20261018 são a referência; preservar trabalho anterior e deixar mudanças sem commit.

## Organização e contratos

- PaymentDataRoute.kt mantém PaymentVisualAssets público e assinatura/defaults da rota, coleta lifecycle, redirects/resume/notice, estado local/handleBack/BackHandler, efeito pixApproved e todos os diálogos na ordem original. Sem novos owners, chaves de efeito, consumo ou validação.
- PaymentDataContent.kt recebe mode/state/assets/callbacks; contém o Scaffold e precedência loading, recovery/session mismatch, METHODS, CARDS, PIX. PaymentDataActions internal com requestAddCard/requestDelete/checkPixStatus é ligado ao ViewModel atual em cada composição, sem remember; folhas recebem somente os callbacks necessários.
- PaymentMethodsContent.kt contém métodos, expandedProviders com remember original, linhas inline, bandeiras, heading e mapping privado de provider. Mantém filtro de suportados, primeiros dois cartões, Listar Mais/Menos, CTA Pix e dois caminhos de adicionar cartão.
- SavedPaymentCardsContent.kt contém cartões, estilo privado e helpers atuais; LegacyCardProviderLogo internal permanece. Guard privateDataEnabled limita somente o estado vazio, sem novo carregamento/negação de acesso; listas usam chave composta método/id e requestDelete continua passando o cartão original.
- PaymentPixContent.kt contém PixContent, substituindo dependência de ViewModel por onCheckPayment. Preservar remember por expiresAt e por purchaseId, contador1s, polling20s após copiar e enquanto remaining>0, null guard e chamadas ao painel. Não trocar relógio nem corrigir/alterar limites existentes.
- PaymentDataFormatters.kt reúne paymentTopBarTitle/formatPaymentAmount (private→internal pois usados entre arquivos) e secondsUntil internal. Manter spans, locale/espaços, parsing offset/local São Paulo, epochSecond/clamp e fallback0.

Corpos, recursos, strings, semântica, estilos e callbacks são copiados exatamente, salvo substituição necessária de ligação. Sem alterações de ViewModel/estado/restoration/rules, contratos, dependências, recursos, gates ou KMP.

## Verificação

Baseline91 JVM e três gates; quatro novos testes unitários de spans/moeda/datas (observar RED por acesso private antes da extração, GREEN após). Sete novos testes Android pela rota pública: cancelar revisão, CTA/ajuda/adição com limpeza antes de callback, expansão/open/cancelar delete/adição, exclusão Recarga por token, visitante/flag sem carga, recuperação Pix sem repetição de geração e troca do ViewModel/bindings. Fakes locais, nunca serviços reais.

Executar esses sete e os14 existentes de ControlledPixInteractionTest/InstalledSavedCardsPairTest antes/depois (21 casos). Comparação estrutural reverte apenas extração e callbacks; mantém API/coordenador/Scaffold/corpos/remember/efeitos. Proteger todos os Kotlin anteriores por hash, especialmente VM/restoration/rules. Executar feature check/lint/cobertura95.01%, release compile, PRD debug build/unit e nove gates; revisão independente. Restaurar pacotes/display exatos do AVD dedicado5560 e encerrá-lo.
