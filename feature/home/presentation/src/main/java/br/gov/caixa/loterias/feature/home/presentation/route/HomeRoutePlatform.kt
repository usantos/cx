package br.gov.caixa.loterias.feature.home.presentation.route

import android.content.ActivityNotFoundException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView

@Composable
internal fun HomeRouteSystemBars(classicHome: Boolean, activity: android.app.Activity?) {
    val window = activity?.window
    val view = LocalView.current
    br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBarColor(
        if (classicHome) {
            br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens.White
        } else {
            br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens.Blue700
        }
    )
    SideEffect { updateHomeSystemBars(window, view) }
}

@Composable
internal fun womenSupportOpener(failure: androidx.compose.runtime.MutableState<Boolean>): () -> Unit {
    val uriHandler = LocalUriHandler.current
    return {
        failure.value = try {
            uriHandler.openUri("https://api.whatsapp.com/send?phone=5561996100180&text=Disque180")
            false
        } catch (_: ActivityNotFoundException) {
            true
        } catch (_: SecurityException) {
            true
        } catch (_: IllegalArgumentException) {
            true
        }
    }
}
