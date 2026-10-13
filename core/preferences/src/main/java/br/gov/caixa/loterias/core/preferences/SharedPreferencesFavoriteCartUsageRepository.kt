package br.gov.caixa.loterias.core.preferences

import android.content.Context
import br.gov.caixa.loterias.core.domain.cart.FavoriteCartUsageRepository

internal interface LongSetPreferenceStore {
    fun read(key: String): Set<Long>
    fun write(key: String, value: Set<Long>)
}

private class AndroidLongSetPreferenceStore(context: Context) : LongSetPreferenceStore {
    private val preferences = context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun read(key: String): Set<Long> = preferences.getStringSet(key, emptySet())
        .orEmpty()
        .mapNotNull(String::toLongOrNull)
        .toSet()

    override fun write(key: String, value: Set<Long>) {
        preferences.edit().putStringSet(key, value.map(Long::toString).toSet()).apply()
    }

    private companion object {
        const val FILE_NAME = "favorite_cart_usage"
    }
}

class SharedPreferencesFavoriteCartUsageRepository internal constructor(
    private val store: LongSetPreferenceStore,
) : FavoriteCartUsageRepository {
    override fun wasAddedToCurrentCart(cartId: Long): Boolean = cartId in store.read(ADDED_CART_IDS)

    override fun markAsAddedToCurrentCart(cartId: Long) {
        store.write(ADDED_CART_IDS, store.read(ADDED_CART_IDS) + cartId)
    }

    companion object {
        private const val ADDED_CART_IDS = "added_cart_ids"

        fun fromContext(context: Context): SharedPreferencesFavoriteCartUsageRepository =
            SharedPreferencesFavoriteCartUsageRepository(AndroidLongSetPreferenceStore(context))
    }
}
