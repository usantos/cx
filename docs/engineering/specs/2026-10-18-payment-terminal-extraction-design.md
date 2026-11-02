# Extração da apresentação terminal de pagamento

Separar PaymentRoute.kt de500linhas sem alterar ViewModel/comportamento ou componentes usados por PaymentCardFlowRoute. Mesmo checkout; preservar trabalhoanterior, sem commits/worktrees/recursos/dependências/gates/VM/regras modificados.

## Organização

- PaymentRoute.kt mantém API/defaults/doc de PaymentTerminalRoute, collectAsStateWithLifecycle, LaunchedEffect(viewModel)/effects.collect (não collectLatest nem rememberUpdatedState), seleção confirmation/success→Processing, failure→Failure, pix-guidance→Pix e error original para rota inválida. Callbacks da orientação Pix ligados ao VM atual diretamente.
- PaymentProcessingScreen.kt: ProcessingScreen internal, ProcessingActionButton e LegacyHelpIcon privados; mesmo layout/insets/título fixo/mensagens, estilos/gradientes, ajuda→support/terms, primary→purchaseProcessingDestination e secondaryopcional→home. Não introduzir conclusão, polling ou nova operação.
- PaymentFailureScreen.kt: PaymentFailureScreen internal, mesmo Scaffold/layout/labels e navegação toolbar/secondary→home, primary→payment/method.
- PaymentPixGuidanceScreen.kt: PixGuidanceScreen internal recebe spec/state/onBack/onTogglePixTerms/onSubmit. pixInstructions privado permanece literal. Mesmos scroll/paddings/checkbox/rowtoggle e botãoaceite. Efeitos de back/completed continuam viaVM na rota.
- PaymentCardComponents.kt: ProviderHeader,LegacyPaymentInput,CvvEntry,CreditCardFront,CreditCardBack,LegacySmallHeading internal com APIs/defaults/corpos intactos; dependências existentes de logo/marca/R mantidas.
- PaymentFormatters.kt: legacyPaymentTextStyle e legacyProcessingText private→internal. Corpo/defaults/estilo exatos; prioridade EFETIVADA→não garante→Minhas Compras e somente primeira ocorrência, sem mudar demais spans/textos.

## Verificação

Snapshot/hashes de todosKotlinanteriores. Baseline95JVM/trêsgates;4testesunit (plain/empty,3emphases,prioridade/repetição,estilo/padding) observamREDacessoprivateantesextração. SeisAndroidpublicroute/realVM: Pixaceite row/checkbox/submit/back, trocaVM/cancelcoletor, failuredestinations, confirmationhelp/actions, successsemsecondary, atualizaçãocallbacknavigationsemnovoVM. Executar TODOS24existentes+6novos antes/depois; fixtures locais sem serviços financeiros. Comparar todoscorpos/API/efeitos/cardcomponents/stringsformatados após reverter bindings/visibilidade. VM/regras/outrosKotlininalterados. Featurecheck/lint/coberturaconfigurada95.01%,releasecompile,PRDdebugAPK/appunit e9gatesoffline; uma revisãoindependente. RestaurarAVDdedicado e encerrá-lo.
