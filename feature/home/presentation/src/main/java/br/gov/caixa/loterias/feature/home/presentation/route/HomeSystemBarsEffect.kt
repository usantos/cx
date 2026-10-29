package br.gov.caixa.loterias.feature.home.presentation.route

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/** Aplica a aparência da janela hospedeira; HomeRoute mantém SideEffect sob seu responsável original. */
internal fun updateHomeSystemBars(window: android.view.Window?, view: android.view.View) {
    window?.let {
        WindowCompat.getInsetsController(it, view).apply {
            isAppearanceLightNavigationBars = true
            show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        }
    }
}

internal tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
