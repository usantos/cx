# Onboarding — separação de responsabilidades

Objetivo: separar coordenação, introdução, sincronização do pager, tutorial, confirmação de idade e componentes de OnboardingRoute.kt sem alterar ViewModel ou comportamento.

## Estrutura e interfaces

- OnboardingRoute.kt conserva assinatura pública, coleta lifecycle, LaunchedEffect(viewModel), quatro efeitos e dispatch das quatro rotas, com os mesmos callbacks/capturas.
- OnboardingIntroduction.kt recebe IntroductionScreen(state: OnboardingUiState, viewModel: OnboardingViewModel), mantém IntroPage/introPages/LegacyIntroPage privados, rememberPagerState no owner atual e seu initialPage/pageCount. Extrair somente a chamada do efeito e o corpo do diálogo; condição ageConfirmationVisible permanece neste local.
- OnboardingPagerSynchronization.kt contém SynchronizeIntroductionPager(pagerState: PagerState, viewModel: OnboardingViewModel): Unit composable. Corpo LaunchedEffect(pagerState) e snapshotFlow/currentPage/distinctUntilChanged/collect(viewModel::pageChanged) literais. Não acrescentar viewModel como chave ou rememberUpdatedState, nem sincronização inversa.
- OnboardingTutorial.kt contém TutorialScreen(checked: Boolean, onToggle: () -> Unit, onStart: () -> Unit, onSkip: () -> Unit), conservando o owner de checkboxInteraction, dimensões, textos, checkbox e cliques.
- OnboardingAge.kt contém AgeScreen(onBack: () -> Unit, onResponsibleGaming: () -> Unit) e AgeConfirmationDialog(onConfirmAdult: () -> Unit, onRejectAdult: () -> Unit). Mesmos textos/ações/defaults do diálogo, sem nova regra de idade ou dismiss. Layout de menor de idade integralmente preservado.
- OnboardingComponents.kt contém LegacyFlatAction e LegacyTutorialButton, com assinaturas/corpos existentes e apenas visibilidade internal para uso entre arquivos. Os estados de interação continuam nos mesmos composables.

## Invariantes

Só o arquivo anterior OnboardingRoute.kt muda em produção. Cinco novos arquivos no mesmo pacote; nenhum novo modelo de domínio, recurso, dependência, API pública ou ViewModel. Confirmação inicial, página inicial, swipe, textos/botões, tipografia e insets não mudam. Não modificar persistência de introdução ou preferência do tutorial; somente iniciar tutorial marcado persiste a preferência, pular não persiste. Preservar as capturas originais dos efeitos, inclusive callbacks coletados até troca de ViewModel.

## Validação

Baseline check/lint/cobertura/compilação release/três gates; seis Android existentes mais três novos de coordenação executados antes/depois. Casos novos: confirmação/página inicial/swipe/completar, rejeição/troca de ViewModel/ações da tela de idade e checkbox/iniciar/pular. Comparar tokens incluindo strings, reconstruir blocos extraídos e conferir hashes da árvore anterior. Final check/cobertura/release/Android, testes JVM app/APK PRD debug e nove gates. Revisão independente read-only com perfil VoltAgent code-reviewer autorizado. Restaurar pacotes/display e encerrar apenas o emulador dedicado.
