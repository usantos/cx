package br.gov.caixa.loterias.core.presentation.delivery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import br.gov.caixa.loterias.core.presentation.effect.ScreenForegroundEffect

/** Um único refresh de configuração por aplicação em primeiro plano, e não um por renderer. */
@Composable
fun FeatureModulesProvider(dependencies: FeatureModuleUiDependencies, content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    val refresh = remember(dependencies.configuration, scope) { FeatureModuleForegroundRefresh(scope, dependencies.configuration) }
    DisposableEffect(refresh) { onDispose(refresh::close) }
    ScreenForegroundEffect(refresh::foreground)
    CompositionLocalProvider(LocalFeatureModules provides dependencies, content = content)
}
