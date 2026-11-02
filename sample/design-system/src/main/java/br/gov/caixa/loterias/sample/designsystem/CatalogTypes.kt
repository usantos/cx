package br.gov.caixa.loterias.sample.designsystem

import androidx.annotation.StringRes

/** Tipos estáveis de uso; o pacote técnico não determina onde o componente aparece. */
enum class CatalogType(val key: String, @param:StringRes val title: Int) {
    BUTTONS("buttons", R.string.category_buttons),
    FIELDS("fields", R.string.category_fields),
    SELECTION("selection", R.string.category_input),
    CARDS("cards", R.string.category_cards),
    NAVIGATION("navigation", R.string.category_navigation),
    DIALOGS("dialogs", R.string.category_dialog),
    FEEDBACK("feedback", R.string.category_state),
    LAYOUT("layout", R.string.category_layout),
    TYPOGRAPHY("typography", R.string.category_typography),
    BETTING("betting", R.string.category_betting),
    CART("cart", R.string.category_cart),
    PAYMENTS("payments", R.string.category_payments),
    ARTWORK("artwork", R.string.category_artwork),
    ICONS("icons", R.string.category_icon),
    THEMES("themes", R.string.category_theme),
    WEB("web", R.string.category_web),
}

data class CatalogGroup(val type: CatalogType, val entries: List<CatalogEntry>)
