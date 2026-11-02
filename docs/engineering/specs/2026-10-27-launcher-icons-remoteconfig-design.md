# Ícones de campanha pelo Remote Config

Pedido direto reiterado pelo usuário: concluir padrão, Outubro Rosa, Setembro Amarelo, Novembro Azul e Agosto Lilás; mostrar as cinco artes lado a lado, testar no Samsung a troca e a volta ao padrão e preservar os quatro atalhos. Sem alteração de headers, baselines ou dados do app.

## Decisões de implementação

A chave STRING `app_launcher_icon` aceita `default`, `outubro_rosa`, `setembro_amarelo`, `novembro_azul`, `agosto_lilas`. Valores ausentes/desconhecidos voltam ao padrão; falhas de rede conservam o valor ativado no Firebase. A seleção é manual no Remote Config, sem calendário embutido.

Os cinco ícones são recursos empacotados. O Outubro Rosa é copiado do legado extraído em `build/verification/experimente-delta-20261025/legacy-res`; as demais campanhas preservam forma, marca e transparência, alterando a paleta. O ícone padrão existente permanece.

O Android usa cinco activity-alias apontando para MainActivity, que permanece habilitada e com o mesmo nome para intents, autenticação e notificações. Somente um alias de launcher fica habilitado. API33+ usa atualização atômica com DONT_KILL_APP; versões anteriores habilitam o destino antes de desabilitar os demais. Os quatro atalhos mantêm IDs, nomes, ícones e destinos; sua propriedade é atualizada para o alias ativo. Falha/limitação de atalhos adia ou desfaz a troca.

A leitura Firebase pertence a Onboarding Data, reutilizando a instância nomeada já usada na atualização obrigatória. Domain tem enum e porta tipados; Presentation usa ViewModel e ReduxKotlin. PackageManager e ShortcutManager ficam no runtime Android do App. A escolha é aplicada uma vez na inicialização, depois da ativação inicial de Remote Config, antes dos fluxos do usuário. Mudanças recebidas durante a sessão valem na inicialização seguinte.

Uma prévia apenas DEBUG fornece o mesmo valor de entrada para testar no Samsung sem publicar configurações globais de produção. Os testes locais dessa prévia e os testes do contrato Firebase são identificados separadamente; não se declara publicação remota sem realizá-la.

## Alternativas e limites

Escolhidos aliases e recursos locais, mecanismo disponível ao launcher Android para esta finalidade. Uma imagem baixada não pode substituir diretamente o recurso de ícone do pacote. Atualização de cache/posição na tela inicial depende do launcher e será conferida no Samsung. Não haverá remoção de atalhos fixados nem limpeza de dados.

## Aceitação

Cinco recursos válidos, parser com fallback, somente um launcher ativo, MainActivity sempre habilitada, troca idempotente/rollback e quatro atalhos corretos. No Samsung: cada campanha, volta ao padrão, captura comparativa, menu de atalhos e destinos abertos com app fechado/aberto. Headers e 37 configurações/baselines conservam hashes.
