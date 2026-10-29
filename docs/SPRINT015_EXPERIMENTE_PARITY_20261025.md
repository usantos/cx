# Comparação Sprint015 × Compose — 25/10/2026

Referência: `/Users/ubiracysantos/Projects/cx`, branch `Sprint015`, commit `5eb10f60d`. O checkout antigo de setembro e o APK de validação instalado em outubro não foram usados como referência das novidades.

## Diferenças implementadas

| Área | Comportamento trazido para o Compose |
| --- | --- |
| Central de notificações | Histórico real do servidor no destino existente; abas Todas e Não lidas com contagem; estados vazios; exclusão individual, exclusão total e marcação de todas como lidas com confirmação. As mensagens locais de exemplo deixam de alimentar esse destino. |
| Segurança das operações de notificações | Requisições vinculadas à sessão capturada; descarte de respostas antigas após troca de usuário; confirmação inválida após troca de sessão; atualização do histórico após falha parcial; rejeição de payload inválido ou IDs duplicados. Sem repetição automática de mutações. |
| Carrinho Loteca | Palpites mandante/empate/visitante preservados por jogo, inclusive duplos e triplos, tanto no carrinho anônimo quanto no remoto; legenda de escolhidos/não escolhidos. |
| Carrinho — apresentação | Títulos das apostas e bolões com a cor de texto do legado. Itens individuais com modalidade nula não aparecem; os totais de contratação continuam preservados. Modalidades futuras não nulas continuam aceitas. |
| Filtro de lotéricas | Todas as sugestões ficam disponíveis em uma lista rolável de 130dp, sem o corte nas seis primeiras. A seleção mantém a lotérica original e seus dados. |
| Lotéricas favoritas | Quebra do nome específica em 27 caracteres, preservando a regra geral dos outros nomes. |
| Dados pessoais | Retorno à tela recarrega dados quando o formulário está limpo. O primeiro resume não duplica a busca. Alterações pendentes, inclusive localização derivada do CEP, não são sobrescritas por respostas atrasadas. |

## Novidades que já estavam presentes

A inspeção do código confirmou que o Compose já continha a Home clássica, campanha e acessos rápidos; informações específicas de modalidades no carrinho (Super Sete, trevos, mês, time e indicadores); ordem de bolões/apostas/combos; valor de reserva; aviso de arredondamento em detalhes de bolão; sincronização de favoritos no retorno; e os fluxos principais de dados pessoais e combinações da Loteca. Esses pontos foram conferidos como existentes, sem reimplementação indiscriminada do diff inteiro.

## Evidência de validação

- 66 testes JVM focados aprovados: rede, repositório e estado de notificações, mapeamento/apresentação do carrinho, marketplace e dados pessoais.
- Central de notificações: 2 testes de interface aprovados no emulador Android 16 e repetidos com sucesso no Samsung XCover7 físico (Android 16).
- Filtros: 8 testes de interface aprovados no emulador, incluindo rolagem e seleção da sétima sugestão.
- Build `PrdDebug` normal aprovado e build de inspeção com pacote separado aprovado, ambos com minSdk 29.
- Verificação de limites de arquitetura dos módulos aprovada. A checagem global do app ainda aponta um registro antigo de `NetworkInspector.kt` em `config/app-compatibility.json` cujo arquivo não existe; as duas dependências diretas novas de data foram corrigidas, movendo o módulo Hilt para a feature. O registro antigo não foi alterado neste escopo.
- Revisão independente do código não encontrou defeitos importantes no escopo implementado. A revisão não substitui testes integrados com os serviços.

Os logs e capturas ficam em `build/verification/experimente-delta-20261025/`.

## Execução do legado e limites

Foi compilado o legado atualizado em um pacote isolado, com ajuste exclusivo de validação para uma string duplicada idêntica, aliases e configuração de pacote. O código-fonte funcional do legado não foi alterado. No emulador, o bootstrap falhou ao conectar ao servidor. Uma instrumentação de inspeção abriu a LoginActivity nativa; o toque real em Experimente terminou em indisponibilidade **LTR-503**. Isso limita a comparação visual com serviços reais; não há alegação de validação integral de autenticação ou transações.

No Samsung XCover7 físico (Android 16), o legado chegou à LoginActivity pelo fluxo normal e o toque real em Experimente carregou a Home clássica com dados e campanha. O sino abriu a Central de notificações e exibiu a confirmação de login para anônimo; foi cancelada. O botão de bolões também exigiu login, cancelado sem autenticação. As capturas `physical-legacy-experimente-current.png` e `physical-legacy-central.png` registram esses estados. Inicialmente ambos os apps foram instalados com pacotes separados. A cópia isolada do Compose permaneceu na Splash; depois de confirmar a mesma assinatura SHA-256 do APK instalado, o pacote normal foi atualizado com `install -r`, sem limpeza de dados, mantendo o APK anterior para recuperação. O resultado desse teste está registrado abaixo.

Não foram feitos login real, compra, reserva ou alteração de notificações do usuário no servidor. As operações autenticadas foram verificadas com testes controlados. Bibliotecas de terceiros não foram alteradas nesta tarefa. Este trabalho funcional não representa uma nova análise MobSF.

## APK de inspeção

`output/validation/simlo-sprint015-compose-inspecao-debug.apk` — build debug com serviços reais, pacote `br.gov.caixa.loterias.apostas.composeupdatedvalidation`, instalado separadamente para preservar dados. Não é um novo release ofuscado. SHA-256: `cfdbcfc920185c4dc50beaca4d3d3ef6bbe53001a7dc9845567dbd19621a0b74`.

Tamanho do APK de inspeção: 143,365,709 bytes (136.72 MiB).

APK normal atualizado: `output/validation/simlo-sprint015-compose-debug.apk`, 143.365.689 bytes, SHA-256 `1eb623fbe20b02ff4ec59b4843948a82c9a331d87d40c108dd70f9bf00763d05`. Build debug sem backend simulado.

## Pendência da execução completa no Compose

A versão atual, tanto no pacote isolado quanto no pacote normal, permaneceu na Splash durante esta inspeção. Não foi observado crash fatal nesse período. Foi reinstalado temporariamente o APK anterior retirado do próprio aparelho (mesma assinatura, `install -r -t`, sem limpeza de dados) para estabelecer uma referência; ele também permaneceu na Splash no intervalo observado. Isso não comprova a causa do bloqueio e não permite atribuí-lo à integração Sprint015.

Assim, os componentes e contratos citados passaram nos testes, mas o caminho completo Compose → Experimente → Home com serviços reais **não está aprovado** nesta execução. Os testes autenticados foram controlados; login real e transações não foram executados. Não foram removidas verificações de segurança nem modificadas bibliotecas de terceiros para atravessar esse bloqueio.

Ao encerrar a inspeção, o APK normal atualizado foi reinstalado com sucesso, mantendo os dados. Os pacotes auxiliares criados nesta tarefa (legado, clone Compose e instrumentação de abertura) foram removidos do aparelho para evitar ícones duplicados. Os APKs e evidências locais foram preservados.

## Ajuste solicitado: entrada na modalidade como no legado

Removido de `PrepareBetEntry` o bloqueio adicional por `open = false` na abertura da simulação. O aviso completo que orienta comprar bolões continua restrito à janela de encerramento individual até encerramento da revenda, com a venda de bolões habilitada, conforme `PrincipalActivity.nextActivity` e `EncerramentoUtil` da Sprint015. Seleção explícita de modalidade/concurso, requisitos de conta e tratamento de falhas foram preservados. A validação de concurso encerrado na inclusão no carrinho continua ativa.

Regressão reproduzida antes da correção em dois testes. Após a correção: 199 testes de core/domain, 5 de betting/domain e 52 de BettingGameViewModel aprovados (256 no total), e build PrdDebug aprovado. Inclui entrada autenticada/anônima na Loteca 1278 fechada fora da janela de bolões, manutenção do aviso na janela correta e bloqueio de inclusão de aposta encerrada no carrinho. Evidências em `build/verification/legacy-bet-entry-20261025/`.
