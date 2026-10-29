package br.gov.caixa.loterias.apostas.navigation.recovery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/** Somente pontos de restauração criptografados e validados podem restaurar rotas após a recriação do hospedeiro. */
@Composable
internal fun rememberRecoveryNavController(): NavHostController {
    var controller: NavHostController? = null
    CompositionLocalProvider(LocalSaveableStateRegistry provides null) {
        controller = rememberNavController()
    }
    return checkNotNull(controller)
}
