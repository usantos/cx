package br.gov.caixa.loterias.apostas

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import kotlinx.coroutines.CancellationException

@Composable
internal fun LoginEnvironmentSelector(container: AppContainer, enabled: Boolean) {
    val settings = container.environmentSettings
    if (!settings.selectorVisible) return
    val activity = LocalContext.current.environmentActivity()
    LoginEnvironmentPicker(
        selected = settings.selected,
        enabled = enabled,
        onChange = container::changeEnvironment,
        onClose = {
            activity?.finishAffinity()
            // Hilt services are process singletons; a new process applies the selected snapshot.
            if (activity != null) android.os.Process.killProcess(android.os.Process.myPid())
        },
    )
}

/** Presentation can be exercised without constructing services or performing authentication. */
@Composable
internal fun LoginEnvironmentPicker(
    selected: LegacyTestEnvironment,
    enabled: Boolean,
    onChange: suspend (LegacyTestEnvironment) -> Boolean,
    onClose: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var pendingName by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var persisted by rememberSaveable { mutableStateOf(false) }
    var retry by remember { mutableIntStateOf(0) }
    Box {
        TextButton(onClick = { expanded = true }, enabled = enabled && !saving && pendingName == null,
            modifier = Modifier.height(48.dp).testTag("environment-picker")) {
            Text((pendingName ?: selected.name) + " ▾", color = CaixaDS.colors.onBrand)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LegacyTestEnvironment.entries.forEach { environment ->
                DropdownMenuItem(text = { Text(environment.name) }, onClick = {
                    expanded = false
                    if (environment != selected) pendingName = environment.name
                })
            }
        }
    }
    val pending = pendingName?.let { name -> LegacyTestEnvironment.entries.firstOrNull { it.name == name } }
    LaunchedEffect(selected, pendingName, retry) {
        if (pendingName != null && reconcilePendingEnvironment(pendingName, selected) == null) {
            pendingName = null
            persisted = false
            failed = false
            saving = false
            return@LaunchedEffect
        }
        if (pending == null || persisted) return@LaunchedEffect
        saving = true
        failed = false
        try {
            persisted = onChange(pending)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            failed = true
        } finally {
            saving = false
        }
    }
    // As in CX, save the spinner selection before presenting the closing notice.
    if (pending != null && !saving && (persisted || failed)) {
        CaixaDSLegacyDialog(
            title = "Mudança de Ambiente",
            message = if (failed) "Não foi possível salvar a mudança de ambiente. Tente novamente."
                else "O aplicativo será encerrado para aplicar as mudanças de ambiente.",
            positiveLabel = if (failed) "Tentar novamente" else "Entendi",
            onPositive = {
                if (failed) retry++
                else if (persisted) onClose()
            },
        )
    }
}

private tailrec fun Context.environmentActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.environmentActivity()
    else -> null
}
