package br.gov.caixa.loterias.apostas.utils

import android.content.Context
import android.util.TypedValue
import android.view.View

fun Context.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}

fun View.dpToPx(dp: Int): Int {
    return context.dpToPx(dp)
}

fun Int.dp(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        context.resources.displayMetrics
    ).toInt()
}