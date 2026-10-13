# Experimente — implementação e validação

Implementação no checkout Compose em 05/10/2026, com referência ao código de `/Users/ubiracysantos/Projects/legado/simlo-app-android`.

## Entrega

- Entrada individual e Instantânea para visitante sem consultar a conta autenticada.
- Carrinho visitante persistente em Loterias.db, compatível com o grafo legado e a transferência existente: inclusão, exclusão, limpeza, limites, confirmação de duplicidade, concursos, teimosinha, modalidades especiais e Espelho, inclusive Surpresinha com Espelho.
- IDs locais monotônicos, incluindo reservas de recibos anteriores, para impedir que uma confirmação antiga apague uma nova aposta.
- Pós-login prepara lote persistente, envia pelo contrato existente e limpa somente itens intactos após confirmação válida do carrinho na mesma sessão. Respostas incertas permanecem para revisão, sem reenvio automático.
- Menu privado, Combo, Bolão e Rapidão solicitam login; destino e ação são retomados após a conclusão do fluxo de autenticação.
- Aposta Shake: preferências compatíveis com o legado, ativação/desativação com confirmação, opção de ocultar aviso, tutorial com seis GIFs, tooltip e acelerômetro vinculado ao ciclo de vida. Completa a seleção sem adicionar ao carrinho; pede confirmação para substituir uma aposta completa. Lotogol permanece sem Shake, conforme o legado.
- Regressão encontrada na interface: Mega-Sena carregava colunas vazias do estado Super Sete. O armazenamento agora usa colunas somente na modalidade Super Sete.

## Evidências

- 461 testes JVM, sem falhas: domain 147, data 154, preferences 33, home 26, betting 66, app AutomacaoDebug 35.
- BUILD SUCCESSFUL: APK AutomacaoDebug com mock, verifyCaixaDS, verifyComposeMigration e verifyCleanArchitecture.
- XCover7 RXGL10CDZ0W: oito testes instrumentados de armazenamento passaram. A suíte usa banco temporário isolado e preserva os dados do app instalado.
- Emulador Pixel 9 Pro: login → Experimente → tutorial → Home → simulação Mega-Sena → inclusão → carrinho. Confirmados números 01–06, dois concursos e total R$10,00. Carrinho restaurado após force-stop e reabertura.
- Emulador: Bolão solicitou login com a mensagem do legado; menu Shake e confirmação de ativação exibidos; acelerômetro simulado completou seis números e pediu confirmação na segunda agitação.
- Revisão de código independente dos caminhos alterados não apontou problemas adicionais após as correções.

Logs, capturas e APK: `build/verification/experimente-20261005/`.

## Limites e pendências de homologação

A interface e o gesto físico no XCover7 ainda precisam de validação: o aparelho estava bloqueado. Os aplicativos legados instalados foram preservados; o APK Compose foi instalado em paralelo como `br.gov.caixa.loterias.apostas.hmp.composevalidation` porque a assinatura debug não substitui a assinatura instalada.

O APK `experimente-compose-validacao-mock.apk` usa backend simulado. Não é artefato de produção. Login real, transferência contra servidor, reconciliação de resposta incerta e pagamento precisam de homologação integrada. Os testes automatizados de pós-login verificam o protocolo local, os contratos e as respostas simuladas; não comprovam execução no servidor.

Não foram executados gates globais de cobertura, todos os flavors ou builds Release. Nenhum commit, publicação ou alteração de assinatura foi realizado.
