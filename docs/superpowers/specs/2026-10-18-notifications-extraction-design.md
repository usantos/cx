# Extração da central de notificações

Separar NotificationsRoute.kt de530linhas mantendo API, ViewModel, regras e comportamento. Preservar checkout e trabalho anterior; sem commits/worktrees, recursos, dependências, contratos, gates, mudanças de lógica ou de compartilhamento.

## Arquivos e contratos

- NotificationsRoute.kt mantém assinatura pública, collectAsState (sem trocar por lifecycle), LocalContext, LaunchedEffect(viewModel), collectLatest dos efeitos e criação/abertura do ACTION_SEND/chooser com texto `${title} <br>${content}`, MIMEtext/plain e título originais. Escolhe as mesmas duas rotas; rota inválida mantém error. Liga callbacks ao VM atual sem remember.
- NotificationMessagesScreen.kt: MessagesScreen internal recebe state/actions/onBack; NotificationMessageActions reúne os9callbacks tipados originais. Scaffold, busca, tabs, bulk actions, loading/empty, LazyColumn e chave id preservados. NotificationTab privado.
- NotificationMessageItem.kt: NotificationItem internal recebe o estado/callbacks originais. Preview continua visível expandido, cores e tamanhos exatos; AnimatedVisibility e botões share/category com mesmas condições e textos.
- NotificationSwipeActions.kt: SwipeableNotificationItem internal e SwipeAction privado. Mesmo BoxWithConstraints/width, rememberScrollState/rememberCoroutineScope locais, horizontalScroll, ordem de ações, e clique com scroll>0 fecha a faixa sem expandir; senão alterna expansão.
- NotificationPreferencesScreen.kt: PreferencesScreen internal recebe state/onTogglePreference:(NotificationCategory?,Boolean)->Unit/onBack/onHelp. PreferenceRow/LegacyNotificationSwitch privados; keys category.name ouALL, callbacks independentes, ajuda, título anotado, animação/estados/semântica exatos.
- NotificationDialogs.kt: NotificationDialogs internal recebe state/onConfirm/onDismiss e mantém ordem confirmação→ação→erro, mensagens e callbacks originais.
- NotificationPresentationMappers.kt: notificationIcon e notificationLegacyTextStyle private→internal, corpos/defaults exatos. NotificationRules e VM permanecem inalterados.

## Verificação

Snapshot/hashes dos Kotlin anteriores; baseline JVM/três gates e check completo do módulo incluindo seus testes Android e gate95.01% com todos os filtros existentes. Dois testes JVM de contratos dos mapeadores observam acesso privateRED e outputs literais/objetos esperados, depoisGREEN. Testes Android adicionais pela rota pública/VM real/localusecases: busca, fechar faixa antes de expandir via gesto real, confirmação/cancelamento em ações individuais, troca de VM mantém callbacks atuais e troca a coleta de compartilhamento, compartilhamento captura Intent localmente sem enviar nada a terceiros. Executar todos os casos Android antes/depois; não modificar os testes anteriores.

Comparação estrutural de todos os corpos/API/efeitos/diálogos/visuais após reverter extração/bindings/visibilidade; nenhum Kotlin anterior além da rota muda. Validar featurecheck/lint/cobertura95.01% (inclui instrumentação de todas classes), releasecompile, PRDdebugAPK/appunit e nove gates offline. Uma revisão independente somente leitura. Restaurar pacotes/display do AVD dedicado e encerrá-lo.
