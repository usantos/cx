# Central de notificações e preferências — auditoria do legado

## Descoberta que muda a integração

O checkout `simlo-app-android-legacy-app-backup-20260903` diferencia dois fluxos:

1. **Última notificação autenticada na Home:** chega no cadastro e usa o PUT de leitura.
   Sua migração está em `HOME_NOTIFICATION_POPUP_AUDIT.md`.
2. **Central de mensagens:** `MensagensPushActivity.onCreate` chama
   `MensagemPushModel.getAllMensagensMock()`. As cinco mensagens, datas e categorias são locais.
   Leitura/exclusão/ações em lote alteram listas; não chamam `DadosUsuarioBO` nem Retrofit/Volley.

Antes desta revisão, `ApplicationModule` conectava `LocalNotificationRepository` ao PUT de
leitura. Isso enviava IDs sintéticos 1–5, inclusive IDs inexistentes pedidos por uma ação,
como se fossem identificadores de notificações remotas. **Esse espelhamento foi removido.**
O repository local não recebe callback de rede. O endpoint legado foi preservado no catálogo;
a leitura autenticada continua atendida pelo cliente dedicado do popup, não pela lista de exemplo.

Não classificar as cinco amostras como mensagens reais recebidas por push. Não há no modelo
legado inspecionado um GET da central ou API de inscrição por categoria para migrar.
Adicionar um serviço novo exigiria contrato explícito, não inferência a partir dos rótulos da UI.

## Escopo de estado e injeção

`NotificationViewModelFactory` pertence ao composition root `:app`, com `Provider<NotificationRepository>`
e dispatchers injetados. Cada criação de ViewModel obtém **um** repository novo e o compartilha entre
os sete casos de uso desse destino. O binding de `LocalNotificationRepository` não é singleton.

Leitura/exclusão e switches não vazam para outra instância da tela. A instância existente no back
stack mantém seu estado; sair e criar outro destino restaura os dados iniciais, aproximando a
criação de uma nova Activity legada. O teste da factory verifica compartilhamento interno e isolamento
entre destinos. Hilt/KSP e reentrada real no APK ainda precisam de execução.

## Comportamento restaurado

Referências: `MensagensPushActivity`, `MensagempushAdapter`, `MensagemPushViewHolder`,
`PreferenciasNotificacoesActivity`, `PreferenciasNotificacoesModel` e `PreferenciaNotificacaoHolder`.

- Abrir uma mensagem marca-a como lida e expande seu conteúdo, fechando o outro item aberto.
- Em **Não Lidas**, abrir não remove imediatamente o item: o adapter legado altera o objeto,
  mas não recalcula a lista nesse clique. `readWhileVisible` mantém esses itens até atualizar a
  projeção. Assim o usuário consegue ler o conteúdo expandido.
- Trocar filtro/limpar pesquisa recalcula os itens; a ação explícita de marcar como lida remove o
  item da lista Não Lidas. Leitura em lote recalcula tudo; exclusão limpa seleção e retenção do item.
- Resposta atrasada de leitura não reinsere item apagado nem mantém leitura numa projeção que foi
  substituída. Um snapshot antigo de item já lido não dispara nova leitura ao reabrir.
- Ações em lote continuam disponíveis e pedem confirmação mesmo com lista vazia/toda lida, como
  os listeners legados. Textos Sim/Não, filtros, vazios e compartilhamento permanecem literais.
- O CTA de categoria continua mostrando `Realiza ação da categoria`: esse é o comportamento
  real do holder legado. Não foi conectado a uma aposta ou resultado inventado.
- Os seis switches de preferências começam desligados e são independentes/localmente visuais.
  O holder legado não implementa listener de inscrição nem propagação do switch “Todas”. A regra
  de mestre que alterava as cinco categorias foi removida; nenhuma preferência remota é alegada.

## Ajustes visuais em Compose/CaixaDS

- Ícones de deslizar agora usam as geometrias/cores originais de `ic_carta_msglidas.xml` e
  `ic_lixeira_push.xml`, em `CaixaDSNotificationSwipeRead/Delete`, sem drawable XML em runtime.
  Quatro pares path/cor foram comparados integralmente; dimensões 26 dp e 29 dp, respectivamente.
- Título lido fica preto, não lido usa `#48586C`; data volta a 14 sp e prévia a 16 sp.
- A prévia permanece no cabeçalho durante a expansão, conforme `bind()` efetivamente chamado
  pelo adapter. A função alternativa `setExpanded()` do holder não é chamada nesse fluxo.
- Tocar no cabeçalho com ações de swipe expostas fecha a superfície, sem expandir simultaneamente.
- Os ícones e o novo token estão documentados/catalogados no CaixaDS.

Esses ajustes não são prova de igualdade pixel a pixel. Captura pareada deve cobrir todas as
categorias, expansão, swipe, fonte ampliada, confirmação, filtros e preferências.

## Limites mantidos explícitos

- O modelo legado atribui um ID repetido às primeiras amostras e possui acesso por posição da
  lista original em ações filtradas. A migração mantém chaves locais únicas e ações por ID;
  não reproduz exclusão/leitura do item errado nem chamadas de rede com esses IDs.
- A pesquisa Compose recalcula a partir da fonte, evitando a perda cumulativa de itens do
  `TextWatcher` legado. Isso é uma correção explícita, não igualdade de todos os defeitos antigos.
- O estado de switches fica estável enquanto o destino vive; não reproduz reset acidental de
  `ViewHolder` reciclado sem listener. Não representa preferências persistentes de push.
- Animação/snap do swipe, retorno por back stack, fonte ampliada e screenshots ainda não foram
  homologados no aplicativo atual. Há divergências visuais anteriores na matriz que seguem abertas.
- O gate de notificações agora inclui todas as classes da feature e cobertura instrumentada,
  com entradas não vazias e limite estritamente maior que 95%. Sua execução integral está pendente.
- Esta etapa não resolve transferência do carrinho local, cobertura global, todos os flavors/Release
  ou a homologação da matriz completa do aplicativo.

## Validação

Resultados da compilação/JUnit/JaCoCo isolados:
`build/verification/notification-inbox-parity-20260907/`.

Execução final com código 0:

- **670 testes JVM aprovados**: 453 de lógica/estado, 64 de network, 120 de data,
  29 de preferences e quatro de host/navegação/factory. O total inclui regressões de outras features.
- Escopo selecionado de lógica/estado de notificações, incluindo corrotinas: **146/147 linhas
  (99,32%)**. Repository local: **92/92**; contratos/casos de uso: **31/31**; factory: **8/8**.
  Não são cobertura global nem cobertura executada de todos os composables.
- Fontes atuais de rede, CaixaDS, notificações, host e composition root compilaram isoladamente.
- **35 testes Android somente compilados**, três novos da central/preferências; nenhum executado
  no dispositivo nesta revisão.
- Comparação dos quatro pares path/cor dos ícones de swipe aprovada contra os XMLs legados.
- `git diff --check` aprovado, com avisos históricos de normalização CRLF nos arquivos Gradle.

Os novos testes Compose cobrem expansão em Não Lidas, prévia/corpo, ações em lote vazias,
switches independentes e ajuda. **Compilar os testes não equivale a executá-los no Android.**

`./gradlew :core:data:test :feature:notifications:check --offline` falhou antes das tarefas pelo
lock `gradle-9.4.1-bin.zip.lck (Operation not permitted)`. A restrição não foi contornada;
compilação isolada não valida recursos, Hilt/KSP, D8/R8, APK nem Release.
