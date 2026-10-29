# Experimente — comparação com o Legado em 21/10/2026

Referência: `/Users/ubiracysantos/Projects/legado/simlo-app-android`. Correções feitas no checkout Compose existente, preservando a reorganização dos módulos.

## Comparação e correções

| Área | Resultado |
| --- | --- |
| Menu lateral | Largura de 300 dp, trevo original, saudação, tipografia, alturas, divisórias, submenu, instituição, versão e saída alinhados à implementação nativa. Captura do aplicativo nativo confirmou fundo branco e acentos rosa durante Outubro Rosa. |
| Aposta Shake | Tooltip manual permanece aberto; somente a apresentação automática expira após cinco segundos. Alterar o switch fecha o tooltip. Preservadas as exceções solicitadas: check branco e fundo rosa quando selecionado em Outubro Rosa, com tooltip mais escuro. |
| Rapidão | Entrada do menu e atalho da Home convergem; visitante recebe confirmação de login, com recusa voltando à Home. Conta autenticada passa pela verificação antes de carregar configuração/lista. Tutorial, sessão expirada, mensagens e redirecionamentos usam o fluxo central; respostas de sessões anteriores são ignoradas. |
| Minha Área, Combo e Bolão | Conferidos os bloqueios de visitante e a continuação para login. |
| Favoritos | Visitante recebe o aviso de login antes do formulário de nome. Recusar preserva os números escolhidos. |
| Carrinho vazio | Avançar segue a sequência do Legado, inclusive localização e solicitação de login. |
| Ajuda | Voltar das perguntas retorna às categorias; voltar das categorias sai da tela. |
| Jogo responsável | Ajuda abre FAQ; voltar na avaliação inicial mantém o comportamento nativo; suspensão apresenta o aviso completo e “Confirmar Suspensão”. |
| Repassômetro | Projeção de 48%, base semanal, data local e atualização a cada segundo reproduzem a regra nativa. |
| Bilhetes | Orientação filtra pelo conteúdo interpretado, conforme o Legado. Botão QR Code solicita retrato. Voltar do resultado não reabre automaticamente o mesmo resultado e permite nova consulta. |
| Visitante e Shake | Conferidos carrinho local, Espelho e limites do gesto; sem nova divergência confirmada no escopo auditado. |

## Validação da primeira rodada

- Compilação integrada de todos os módulos, fontes unitários e instrumentados: `BUILD SUCCESSFUL`.
- 1.759 testes JVM: zero falhas e zero erros.
- 108 testes Python de políticas: todos passaram.
- `verifyFeatureModules`, `verifyCleanArchitecture`, `verifyComposeMigration` e `verifyCaixaDS`: passaram.
- 31 testes de interface do aplicativo no emulador: todos passaram, incluindo comparação das dimensões e métricas tipográficas do menu com o layout nativo instalado.
- Oito testes instrumentados de bilhetes: todos passaram, incluindo retorno do resultado por scanner e consulta manual sem reabertura indevida.
- `git diff --check`: sem problemas de whitespace.
- Revisão independente dos fluxos e da alteração final de campanha: nenhum achado acionável.

Testes automatizados usam respostas controladas para verificar regras, inclusive falhas. O APK de validação foi configurado com backend real, Home do Legado e Chucker desativado. Instalado somente no emulador, em pacote isolado.

Logs locais: `/tmp/simlo-experimente-final-validation.log`, `/tmp/simlo-experimente-python.log`, `/tmp/simlo-experimente-final-ui.log` e `/tmp/simlo-experimente-tickets-ui.log`.

## Limites da primeira rodada

A comparação combina código do Legado, captura do menu nativo e testes instrumentados. Não representa homologação de todos os pixels em todos os aparelhos. Login real, transações financeiras, câmera física e pós-login contra o servidor precisam de homologação integrada. Nenhuma transação financeira foi realizada e o celular físico não foi alterado.

## Validação ampliada solicitada após a primeira rodada

A conferência complementar considera os destinos alcançáveis pelo menu Experimente e as continuações de login. O inventário foi comparado novamente com Activities, Fragments, XMLs, reducers e contratos do Legado.

| Origem/tela | Estados e regras conferidos |
| --- | --- |
| Menu lateral | Grupos, subgrupos, flags de visibilidade, instituição, links com confirmação, saída, campanhas e Shake. |
| Minha área | Dados pessoais, apostas, compras, favoritas, carrinhos favoritos e cartões: formulário inicial, aviso de login, Sim/Não, cancelamento do navegador e sessão já autenticada. Não existe um item separado chamado Extrato nesse menu. |
| Rapidão | Entrada do menu/atalho, verificação de conta, tutorial, sete páginas, resumo ao reabrir, filtros conflitantes, modalidades permitidas, presets de limites, lista, configuração, salvar/voltar e erros do serviço. |
| Volantes | Mega-Sena, Quina, Lotofácil, Dupla Sena, Lotomania/Espelho, +Milionária/trevos, Timemania/time, Dia de Sorte/mês, Super Sete/colunas, Loteca e placares: seleções, limites, mudança Manual/Surpresinha, quantidade, teimosinha, Shake, ajuda e inclusão local. |
| Favoritos e carrinho | Login antes de favoritar, preservação da seleção ao recusar, vazio/itens, inclusão/duplicata, capacidade, Espelho, remoção e continuidade para login. |
| Home e continuações | Primeira carga, retorno sem atualização automática indevida, pull to refresh V2, tutorial/tooltip, Combo retorna à Home após login e Bolão mantém a continuação nativa. |
| Resultados | Lista pública, busca/validação/concurso histórico, falha com recuperação do último concurso, concursos especiais, dois sorteios, premiação, time/mês, trevos, Loteca e Super Sete. |
| Dúvidas e Termo | Categorias, perguntas, conteúdo, links, leitura, retorno e barra de status comparada com captura do aplicativo nativo. |
| Autoavaliação | Introdução, rádios, navegação que limpa resposta, última ação Concluido, payload, resultado afirmativo/negativo, introdução preservada, ajuda/contatos e Voltar. |
| Suspensão | Períodos, ciência, confirmação, falha e sucesso com término no fuso de São Paulo usando o relógio do servidor; saída apenas ao Entendi. Testado com use case controlado, sem suspender a conta real. |
| Repasses sociais | Cálculo, tempo, conteúdo e encaminhamento para FAQ. |
| Bilhetes | Gate de câmera na Home, negação/concessão, scanner/orientação, QR Code, consulta manual, resultado, ajuda, retorno sem reabertura, transição de navegação e Aposte agora com modalidade. |

### Diferenças adicionais corrigidas

- Rapidão: ação Configuração, resumo na reabertura, exclusão de modalidades incompatíveis, seleção vazia preservada, mensagem MA016 e limites de presets.
- Volantes: alternância entre Manual e Surpresinha limpa as escolhas e reinicia quantidades como no reducer nativo.
- Conta: retorno de SSO cancelado recoloca o aviso na tela privada; placeholders iniciais não inventam limites monetários; Combo não abre automaticamente após login.
- Autoavaliação: ação final, limpeza de resposta ao navegar, descrição vazia no contrato, resultado com introdução e ações nativas e folha de ajuda completa.
- Suspensão: diálogo de sucesso com data/hora do servidor no fuso America/Sao_Paulo, inclusive com o aparelho em UTC.
- Resultados: artes especiais originais, time/mês antes da data, trevos individuais, carrossel e cartões da Loteca, células nativas de Super Sete e recuperação de falha histórica.
- Bilhetes: removida ação Salvar inexistente no Legado, restaurada Ajuda, preservada modalidade em Aposte agora e corrigida a resolução do ViewModel durante a saída do resultado.
- Câmera: o leitor só abre após a permissão; recusa mantém a Home e mostra a mensagem nativa.
- Dúvidas/Termo: barra de status branca com ícones escuros como no aplicativo nativo; teste de troca de telas durante a animação de navegação incluído na rodada final.

### Verificação real no celular

O usuário realizou o login manualmente. Foram observados retorno ao aplicativo, tutorial da Aposta Shake por usuário, abertura do menu após Pular, carregamento dos Dados Pessoais e carregamento de Apostas Favoritas sem novo aviso de login ou erro. Nenhum campo foi editado ou salvo. Não foram registrados valores dos dados pessoais neste relatório.

### Limites da evidência

Os caminhos de avaliação, suspensão, mutações e erros de serviço foram exercitados por testes controlados; a conta real foi usada somente para autenticação e consultas de leitura. Não foram realizadas apostas, pagamentos ou suspensão real. Lotogol não está exposto no menu Resultados atual; seu renderer histórico de fluxos autenticados não foi incluído como uma tela pública alcançável. Busca de lotéricas também não apresentou origem pública normal nesse menu.

A homologação real limita-se ao celular conectado e às consultas de leitura descritas. A conferência visual completa em múltiplos aparelhos e a execução financeira real não fazem parte desta evidência. As exceções de Outubro Rosa solicitadas pelo usuário foram preservadas.

### Retorno de login na compilação atualizada

Após instalar a compilação final com backend real, Home do Legado e Chucker desativado, o usuário autenticou novamente no celular. O retorno ao aplicativo foi observado e Dados pessoais carregou cinco campos preenchidos, sem novo aviso de login ou erro. Nenhum dado foi alterado; o aplicativo foi deixado aberto após retornar dessa consulta.

### Resultado final da validação ampliada

- Compilação integrada: `BUILD SUCCESSFUL`, 2.181 tarefas. Recompilação dos testes corrigidos: `BUILD SUCCESSFUL`.
- 1.769 testes JVM em 65 tarefas: zero falhas/erros. 108 testes Python passaram.
- 351 cenários instrumentados distintos passaram, considerando a execução final de cada caso: 279 nas bibliotecas e 72 no aplicativo. Nenhum caso desse conjunto ficou ignorado. Os 55 cenários da Home incluem 20 comparações efetivas com o APK original do Legado, após fornecer o asset de referência que faltava.
- A repetição final incluiu 19 testes de autoavaliação/resultados e 20 de carrinho. Dois testes de carrinho com expectativa antiga de botão desativado foram substituídos por verificações dos gates nativos de localização, histórico e login. A busca de concurso agora verifica a mensagem exata do Legado; Voltar da ajuda é enviado à janela ativa.
- `verifyFeatureModules`, `verifyCleanArchitecture`, `verifyComposeMigration`, `verifyCaixaDS` e cobertura de responsible-gaming passaram. `git diff --check` passou.
- Evidência consolidada e resultados por cenário: `build/verification/experimente-20261021/final-reruns/summary.json`; os logs históricos com falhas anteriores foram preservados e são substituídos pelas reexecuções finais identificadas nesse arquivo.

Esse resultado cobre o inventário e os cenários descritos, sem substituir homologação financeira real ou comparação visual em todos os aparelhos. Nesta rodada, a confirmação MA014 e as combinações inválidas de limites do Rapidão ainda estavam pendentes. A rodada complementar abaixo trata desses pontos.

## Revisão complementar — MA014, Rapidão, Favoritas, Bolões e inclusão pela modalidade

### Inclusão pela modalidade e MA014

A comparação encontrou erros no corpo enviado ao serviço: modalidades comuns carregavam a matriz vazia do Super Sete no lugar dos números marcados; faltavam o concurso alvo e o valor calculado; tipo de concurso e indicador de Surpresinha usavam códigos diferentes dos nativos. A Surpresinha também seguia o endpoint da aposta manual.

O envio foi corrigido para conservar a seleção e o preço completo, limitar as colunas ao Super Sete, usar tipo de concurso 1/2 e indicadores 1/2/3, e enviar Surpresinha ao endpoint próprio com quantidade e campo de espelho nativos. Rejeições HTTP com mensagem deixam de virar o erro genérico de carregamento. A distinção entre time escolhido e aleatório da Timemania é preservada.

A MA014 agora precede a inclusão manual autenticada quando carrinho + aposta ultrapassa estritamente o limite diário dos parâmetros. Igualdade não abre aviso. A opção “Não” preserva a seleção; “Sim” inclui a seleção pendente uma única vez. Visitante e Surpresinha conservam seus caminhos próprios. Sair da sessão cancela a pendência.

### Limites do Rapidão

Reproduzidos os ramos estritos de quatro vezes o mínimo e os ajustes de R$ 5, inclusive quando o Legado mantém mínimo acima do máximo na fronteira exata ou ajusta um extremo para fora do intervalo do editor. Valores sugeridos resultantes continuam visíveis. Entrada personalizada fora dos limites abre Entendi e mantém o editor para correção, sem alterar a configuração. Os estados preservados pelo Legado são encaminhados ao serviço ao salvar, sem reparo local inventado.

### Apostas Favoritas

Conferidos e corrigidos: catálogo completo de modalidades, consulta paginada com size=8, continuidade e respostas antigas, saída ao criar nova favorita, limpeza do filtro após excluir seu último item, sessão expirada e sequência suspensão → leitura do carrinho → limite diário → duplicidade → opções/inclusão. Concurso somente especial inclui diretamente com tipo 2, zero teimosinhas e parâmetro espelho omitido. Modalidade indisponível continua consultável/excluível, mas sua inclusão é bloqueada.

Favoritas esportivas já existentes recebem a regra nativa: Loteca/Lotogol não comparam listas numéricas vazias como duplicadas e usam valorApostaMinima para o limite quando esse dado está presente. O campo passa da resposta para o domínio sem ser perdido.

### Bolões

Resposta de sucesso sem payload equivale à página vazia, permitindo continuar a busca geográfica. Trocar a lotérica limpa modalidade e filtros dependentes. Nomes de cidade/lotérica seguem capitalização nativa; o nome completo da lotérica quebra por palavras, sem corte em 13 caracteres. Rótulos normais/especiais usam as descrições e a campanha configurada. Lista e carrossel respeitam suas diferenças quando há lotérica e uma modalidade selecionada.

### Leitura do carrinho após incluir

Indicador nativo 1 é manual, 2/3 são Surpresinha; o booleano explícito é respeitado. A Surpresinha numérica mantém os números ocultos e mostra o time escolhido, como no Legado.

### Evidência e limites

Requisições de inclusão, rejeições HTTP, exclusões, suspensão e salvamento foram exercitados exclusivamente com callbacks controlados e servidores HTTP locais. Nenhuma aposta, pagamento, reserva real de cota ou alteração da conta foi realizada. O APK de execução usa backend real, Home do Legado e Chucker desativado. A comparação visual foi exercitada no emulador; não representa certificação de todos os aparelhos.

Os logs da rodada ficam em build/verification/experimente-20261021/favorites-pools-final e favorites-pools-final-ui. Compilação final: BUILD SUCCESSFUL (1.236 tarefas). A suíte completa executou 1.813 testes JVM em 65 tarefas, sem falhas ou erros. Os 81 testes de interface desta revisão (33 apostas/Favoritas, 30 Bolões e 18 filtros) e os 108 testes Python passaram, sem cenários ignorados. As verificações de módulos, arquitetura, migração Compose e CaixaDS passaram; git diff --check também passou. A revisão independente apontou as duas integrações adicionais de carrinho e favoritas esportivas descritas acima, ambas corrigidas e cobertas por testes. Os logs com falhas anteriores são históricos; o resumo final é favorites-pools-final/summary.json.

A compilação final foi instalada por atualização do mesmo pacote br.gov.caixa.loterias.apostas no celular conectado e aberta com sucesso. Mantidos backend real, Home do Legado e inspeção de rede/Chucker desativada. Não foi acionada inclusão real no carrinho para validar esta rodada.

## Ajuste solicitado — carrinho vazio

Por solicitação explícita do usuário, o footer vazio oculta Ir para pagamento, o ícone/contador do carrinho e o divisor; Total do carrinho e o valor ficam centralizados. Adicionar nova aposta permanece. Os elementos retornam ao existir item, inclusive item com valor zero. Essa preferência substitui a expectativa anterior dos testes que permitia abrir os gates de checkout com carrinho vazio.

Conferência no APK nativo instalado cxv2validation: os três paths de drawable/ic_cart coincidem com o vetor Compose, área 140 × 140 dp, tint #EEEEEE, margem do texto 16 dp, fonte Caixa Std Semi Bold12sp e texto “Seu carrinho está vazio”. Ícone e tamanho já coincidiam; foram corrigidos família tipográfica, texto e cor #005CA9. O arquivo TTF Semi Bold é idêntico ao nativo por SHA-256.

Compilação do app e dos testes instrumentados: BUILD SUCCESSFUL. 24 testes de interface passaram, incluindo carrinho vazio, ausente, inclusão/remoção do último item, centralização e tipografia; a suíte unitária de cart:presentation também passou. Evidências em build/verification/empty-cart-20261021. Sem apostas ou pagamentos reais.

## Revisão complementar — carrinho com apostas

A referência visual e de comportamento desta rodada foi o APK nativo 1.25.0 instalado como cxv2validation, consultado por recursos XML, fontes e DEX. Fontes anteriores do Legado foram confrontados com esse APK para evitar restaurar regras já substituídas. Por exemplo, o carrinho atual formata o número bruto 100 da Lotomania; não usa a transformação antiga de VolanteUtils.

Corrigidos os espaçamentos e as âncoras independentes de título, exclusão, quantidade, concurso, preço e detalhes de bolões/combos. A quantidade do combo respeita as duas âncoras nativas, inclusive o arredondamento de pixels. Foram ajustados cor do texto do bolão, fonte de detalhes, fonte do prazo, espaçamentos da seta, divisores, margem inferior do combo e deslocamento de 1,5 dp do contador do footer. Combo com uma aposta usa o singular. As artes de exclusão respeitam os tamanhos/paddings e cores de cada tipo de item; a seta foi substituída pelo vetor nativo. Tokens e vetor pertencem ao CaixaDS.

As linhas de partidas esportivas deixaram de ser anexadas à linha do carrinho: o holder nativo atual não as apresenta nesse local. Os dados continuam disponíveis no modelo para seus demais consumidores. Preservados Super Sete por colunas, números ocultos da Surpresinha, complementos de mês/time/trevos, indicadores, ordenação das seções e regras de totalização sem contar duas vezes as projeções do serviço.

Conferida a sequência conta/cadastro/suspensão → cotas expiradas → localização → compra recente de mesmo valor em 24 horas → validação do serviço → pagamento. Os limites continuam submetidos à validação autoritativa do backend. Corrigido o retorno tardio da validação: uma sessão encerrada ou substituída não pode voltar a liberar a navegação ao pagamento.

Corrigida também a falha de limpeza confirmada: como no APK nativo, a falha do serviço limpa o carrinho local e zera os itens/totais exibidos sem fazer nova consulta. A falha continua disponível no estado; exclusão individual com falha preserva a aposta. Cancelamento e resposta de uma sessão encerrada não acionam essa atualização da tela. A limpeza local não representa confirmação de exclusão no servidor; uma consulta posterior pode retornar o carrinho remoto, conforme seu estado.

No sucesso, o carrinho exibido também é zerado caso a resposta ainda contenha uma projeção antiga de apostas. A limpeza bem-sucedida remove a marca local de carrinhos favoritos já importados, permitindo reutilizá-los sem aviso incorreto de duplicidade. Falha e cancelamento preservam essa marca; uma resposta atrasada após cancelamento não pode apagá-la. A ligação entre persistência, caso de uso e injeção de dependências foi coberta por testes locais.

Os contratos HTTP foram exercitados do repositório até o transporte em localhost: consulta e validação por GET, exclusões de aposta/cota e combo por seus respectivos DELETEs, limpeza por POST e substituição dos totais pela resposta. Não houve registro de apostas, pagamentos, reserva real de cotas ou chamada de mutação no backend real. Mantido o ajuste explícito do usuário para o carrinho vazio.

Compilação integrada do app, fontes de testes e módulos: BUILD SUCCESSFUL. A suíte completa executou 1.827 testes JVM, sem falhas, erros ou testes ignorados. As verificações verifyFeatureModules, verifyCleanArchitecture, verifyComposeMigration e verifyCaixaDS passaram. A evidência desta rodada fica em build/verification/filled-cart-20261021. A comparação não substitui homologação financeira nem conferência visual em todos os aparelhos.

Na execução final, os 81 testes de interface do carrinho passaram, sem cenários ignorados. Incluem 12 verificações visuais novas, estados vazio/preenchido, bloqueio durante carregamento/mutação, confirmações, gates de checkout, cotas e favoritos. As artes pequenas são comparadas com rasterização independente dos paths nativos, considerando antialias e capturando o ícone sem a área expandida de toque. O teste físico do botão Adicionar recalcula sua posição após o carregamento, pois o footer preenchido inclui o botão de pagamento. git diff --check passou. Os logs de falhas anteriores são históricos e foram preservados; os resultados finais estão em jvm-summary.json, ui-summary.json e simlo-filled-cart-final-all-green.log.

## Validação no celular com backend real — carrinho

Validado no Samsung SM-G556B, 1080 × 2408, 450 dpi, escala de fonte 1,0. O usuário autenticou manualmente; após a atualização corretiva, a sessão foi retomada pelo fluxo normal de login. Não foram capturados tokens, senhas ou códigos. A consulta final abriu uma Activity e um CartViewModel novos, preservando o mesmo processo autenticado, evitando reaproveitar a primeira tela de visitante. O menu autenticado foi conferido antes e depois da navegação. O carrinho retornado nessa consulta estava vazio, com total R$ 0,00.

Conferidos no celular: mensagem e ícone do estado vazio; total centralizado; ausência do pagamento, divisor/ícone do footer, exclusão e salvamento de favorito; Adicionar nova aposta retornando à Home e mantendo a sessão. A abertura direta do carrinho não possuía uma tela anterior no grafo, deixando Adicionar sem destino. Esse caso agora abre a Home e substitui a entrada direta do carrinho. Os retornos normais de modalidade, bolão, Home e detalhe de compra foram preservados.

A comparação com o APK nativo 1.25.0, configurado com a mesma resolução/densidade no emulador, encontrou uma diferença de largura da mensagem vazia: 369 px no Compose contra 383 px no nativo. O XML nativo converte 12 sp para 34 pixels inteiros antes de renderizar; o Compose usava 33,75 pixels. O tamanho foi ajustado para seguir esse arredondamento, respeitando a escala de fonte. No celular atualizado, a mensagem passou a medir 383 × 47 px, como no nativo. Mantidas as exceções do footer vazio explicitamente solicitadas pelo usuário.

Incluídos testes em 420, 450 e 480 dpi. O oráculo usa TextView e a conversão nativa de dimensão compilada do Android, confrontada com as métricas extraídas do APK, sem inserir layouts XML no projeto Compose. Antes da correção, os casos 420/450 falhavam; após a correção passaram. A regressão de entrada direta também falhou antes da mudança de navegação e passou depois.

Resultado final desta rodada: compilação do app e dos testes passou; 316 testes JVM do app/carrinho e 84 testes de interface passaram, sem cenários ignorados. Sete testes de fonte/footer foram repetidos com o oráculo final e também passaram. As verificações de módulos, arquitetura, migração Compose e CaixaDS passaram, assim como git diff --check. A versão corrigida foi instalada no mesmo pacote do celular, com backend real, Home do Legado e Chucker/inspeção/log de rede desativados. Não foram incluídas/excluídas apostas, efetuados pagamentos ou criadas reservas reais.

Limite desta evidência: a conta estava sem apostas no carrinho. As linhas preenchidas, limites, expiração e falhas financeiras continuam cobertos pelos testes controlados da rodada anterior; não foram homologados com apostas reais nesta conta. Evidências agregadas, capturas do carrinho e logs em build/verification/cart-phone-real-20261021. O emulador foi restaurado para sua resolução/densidade original após a comparação.

## Layout do bolão expandido — 21/10/2026

Comparada a captura do Legado em 1080×2408/450 dpi com a linha expandida da lista. A margem direita fixa de 29dp só reproduzia a distribuição nativa na largura de 426dp: no celular de 384dp ela comprimia o botão de adicionar. O espaço livre agora é dividido entre o intervalo dos controles e a margem direita, mantendo o seletor de 159dp, o botão de 177dp e o arredondamento nativo de 5dp. O bloco de valor passou a alinhar ao centro do resumo completo da lotérica; a descrição de cotas reserva a largura medida desse bloco.

Os três testes novos reproduziram as diferenças antes da correção e passaram na densidade do celular depois dela. A compilação, os 44 testes JVM do marketplace e os gates de arquitetura passaram. A versão foi instalada com backend real, Home do Legado e logs de rede desativados. A consulta real de bolões apresentou erro, inclusive ao tentar novamente; por isso a comparação visual da lista preenchida nesta rodada usou dados controlados no emulador. Não foram realizadas apostas, pagamentos ou reservas reais. Evidências em `build/verification/pool-layout-20261021`.

Na resolução padrão do emulador (1280×2856/480dpi), os 33 testes de interface do marketplace passaram. A execução dos três testes específicos na resolução do celular também passou.

## Consulta real de bolões e autenticação — 21/10/2026

A investigação corrigiu o diagnóstico da rodada anterior: a mensagem de carregamento na abertura direta após reinstalar o app era causada pela ausência de sessão. O provider de localização exige identidade autenticada e município do perfil; sem isso, a busca terminava antes de enviar o GET de bolões. Com autenticação, a resposta real carregou normalmente, sem erro de transporte ou DTO observado.

As entradas diretas `betting/pools` e `betting/pool-view` agora encaminham ao Login CAIXA antes de criar os consumidores de dados privados e guardam a continuação para retornar à lista. Os ViewModels da lista também usam a chave da sessão. Dois testes com o NavHost e o grafo de produção falharam antes da correção e passaram depois: nenhuma leitura privada antes do login, continuação preservada e busca liberada após autenticar. A instrumentação temporária de diagnóstico foi removida.

Na versão final instalada no Samsung, o fluxo de login retornou à lista real. Conferidos a consulta, o filtro da Mega-Sena (concurso 3073), as linhas recolhidas e o bolão expandido. O botão de adicionar permanece em uma linha e separado do seletor; o preço está alinhado ao resumo da lotérica. A tela foi deixada aberta nesse estado. Compilação, gates de arquitetura, 252 testes JVM (app e marketplace) e 35 testes de interface do marketplace passaram. Backend real, Home do Legado e logs/Chucker desativados. Nenhuma inclusão, exclusão, aposta, pagamento, favorito ou reserva real foi executada. Evidências em `build/verification/pools-real-backend-20261021`.


## Bolões — encerramento da validação complementar (21–22/10/2026)

A matriz complementar, diferenças demonstradas, correções e limites estão em [pools-parity-2026-10-21.md](pools-parity-2026-10-21.md). Passaram 361 testes JVM, 69 testes de interface de Bolões e 20 de filtros, além de dez repetições adicionais do retorno dos filtros e 14 casos de captura comparados ao APK nativo. A correção final da Loteca foi novamente compilada e conferida nas capturas normal/Pais. Build e gates de arquitetura, Compose, DS, formatação, contratos e segurança passaram.

Versão final instalada no celular com backend real, Home do Legado e sem Chucker, inspetor ou log de rede. Confirmadas consultas, filtros, detalhes, favoritas somente leitura e lista expandida. Nenhuma aposta, pagamento, reserva ou alteração real de favoritas foi realizada; estados destrutivos/indisponíveis usam serviços controlados. Os resultados valem para a matriz e os perfis descritos no relatório, sem certificação universal de combinações futuras do backend.
