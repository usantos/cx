package br.gov.caixa.loterias.apostas.utils

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import br.gov.caixa.loterias.apostas.R
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {

    fun showSuccess(
        anchorView: View,
        @StringRes messageRes: Int
    ) {
        show(
            anchorView = anchorView,
            message = anchorView.context.getString(messageRes),
            backgroundColor = R.color.snackbar_success
        )
    }

    fun showError(
        anchorView: View,
        @StringRes messageRes: Int
    ) {
        show(
            anchorView = anchorView,
            message = anchorView.context.getString(messageRes),
            backgroundColor = R.color.snackbar_error
        )
    }

    fun showSuccess(
        anchorView: View,
        message: String
    ) {
        show(
            anchorView = anchorView,
            message = message,
            backgroundColor = R.color.snackbar_success
        )
    }

    fun showError(
        anchorView: View,
        message: String
    ) {
        show(
            anchorView = anchorView,
            message = message,
            backgroundColor = R.color.snackbar_error
        )
    }

    private fun show(
        anchorView: View,
        message: String,
        @ColorRes backgroundColor: Int
    ) {
        val context = anchorView.context

        val snackbar = Snackbar.make(
            anchorView,
            message,
            Snackbar.LENGTH_SHORT
        )

        snackbar.setTextColor(
            ContextCompat.getColor(
                context,
                android.R.color.white
            )
        )

        snackbar.view.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE

            setColor(
                ContextCompat.getColor(
                    context,
                    backgroundColor
                )
            )

            cornerRadius = context.dpToPx(16).toFloat()
        }

        val params = snackbar.view.layoutParams

        if (params is ViewGroup.MarginLayoutParams) {
            params.marginStart = context.dpToPx(16)
            params.marginEnd = context.dpToPx(16)
            params.bottomMargin = context.dpToPx(16)

            snackbar.view.layoutParams = params
        }

        snackbar.show()
    }
}
