# Login — extração sem mudança de comportamento

Separar coordenação, autorização, biometria, composição, diálogos e componentes. Manter LoginViewModel e APIs públicas, defaults, resultados, preferências e comportamento atuais.

## Arquivos e interfaces

- LoginRoute.kt: assinatura pública integral; estado lifecycle, LifecycleResumeEffect(viewModel), automaticAuthorizationConsumed rememberSaveable, LaunchedEffect(authorizeOnEnter), LaunchedEffect(state.authenticated), callbacks de navegação e chamada dos helpers/tela/diálogos.
- LoginAuthorization.kt: rememberLoginAuthorization(viewModel: LoginViewModel, authentication: AuthenticationGateway, browserFallbackPending: Boolean, onAuthorizationCancelled: () -> Unit, biometricPrompt: (Activity, Intent, (Intent?) -> Unit) -> (() -> Unit)): LoginAuthorization. Registra launcher browser antes do helper biométrico, mantém decisão beginAuthorization/canUseBiometricLogin, exceções e fallback. LoginAuthorization contém authorize: () -> Unit e promptHost: Activity?. Não memorizar o holder ou callbacks com novas chaves.
- LoginBiometrics.kt: rememberLoginBiometricAuthorization(viewModel, authentication, biometricPrompt): LoginBiometricAuthorization, com challenge rememberSaveable, credentialLauncher, host, DisposableEffect(challenge,host) e cancelamento. Holder contém promptHost: Activity? e launch: () -> Unit. Launch conserva checkNotNull/cast/criação/atribuição/SDK<29 na ordem original. Context.findActivity vira internal para compartilhamento com tela.
- LoginScreen.kt: LoginScreen pública integral, mantendo efeito de barras/insets e retorno AccountTokenScreen antes do conteúdo, layouts e seletor de ambiente.
- LoginDialogs.kt: LoginDialogs(state: LoginUiState, viewModel: LoginViewModel, promptHost: Activity?, biometricEnrollment: (Activity) -> Unit, onSessionInvalidated: () -> Unit). Mesmo if/else: enrollment→disable→blocked→consultation. Mensagens inclusive grafia, ações/defaults e condição de invalidar sessão preservados.
- LoginComponents.kt: LegacyLoginButton internal com assinatura/corpo inalterados. Switch biométrico e demais componentes DS continuam usados na tela.

## Invariantes

Somente LoginRoute.kt anterior muda em produção, cinco novos arquivos no mesmo pacote; nenhum recurso, ViewModel, contrato auth, dependência, build/gate alterado. Helpers sempre chamados incondicionalmente, preservando a duração dos estados/launchers e ordem browser→credential. AutomaticAuthorizationConsumed permanece no coordenador antes do helper. Não adicionar chaves de ViewModel/prompt/callback aos efeitos, rememberUpdatedState, timeout, cache ou novas regras. Fallback continua antes da autorização automática e navegação. DisposableEffect conserva sucesso/falha/throw/cleanup e atualização do challenge antes da conclusão. SDK<29 e >=29 mantêm caminhos próprios. Os holders apenas transportam callbacks/host, sem estado adicional. O valor authorize é criado a cada composição como antes; efeitos mantêm capturas originais até mudanças de suas chaves.

## Validação

Caracterização verde antes/depois: todos34Android existentes de account mais4novos;2LoginEnvironmentPickerUiTest no app. Novos casos: lançamento automático com falha e recomposição/reentrada da flag sem repetição, retry manual com resultado e destino; prompt ativo sobrevive recomposição e cancela ao sair; exception no prompt faz fallback browser uma vez; enrollment tem prioridade sobre aviso de falha bloqueante e ACK mantém invalidação correta. JVM/check/lint/cobertura e release de account antes/depois;134JVM app/APK PRD debug/compilação PRD release e nove gates finais. Comparar corpos/blocos reconstruídos e hashes. Revisão independente VoltAgent code-reviewer. Confirmar pacotes/display originais e parar somente o emulador criado.
