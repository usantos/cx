package br.gov.caixa.loterias.core.preferences

import android.content.Context
import br.gov.caixa.loterias.core.domain.home.HomeLayoutPreferences

class SharedPreferencesHomeLayout(context: Context) : HomeLayoutPreferences {
    private val preferences = context.applicationContext.getSharedPreferences("home_v2_layout", Context.MODE_PRIVATE)
    override fun loadOrder(): String? = preferences.getString("modality_order", null)
    override fun saveOrder(order: String) { preferences.edit().putString("modality_order", order).apply() }
}
