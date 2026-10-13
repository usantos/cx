package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import java.math.BigDecimal

enum class CartItemKind { INDIVIDUAL_BET, POOL_SHARE, COMBO }

enum class CartItemBadge { EXCHANGE, POOL, COMBO, MIRROR, SURPRISE, RECURRING }

data class CartPoolShare(
    val code: String,
    val reservedShare: Int,
    val totalShares: Int,
    val expiresAt: String,
)

data class CartItem(
    val id: Long,
    val kind: CartItemKind,
    val title: String,
    val description: String,
    val amount: BigDecimal,
    val modalityCode: String = "",
    val contestNumber: Int? = null,
    val numberQuantity: Int = 0,
    val recurringDraws: Int = 1,
    val selectedNumbers: List<List<Int>> = emptyList(),
    val selectedClovers: List<Int> = emptyList(),
    val badges: Set<CartItemBadge> = emptySet(),
    val complementaryLabel: String? = null,
    val poolShare: CartPoolShare? = null,
    val gameRows: List<String> = emptyList(),
    val comboBetCount: Int = 0,
)

data class ShoppingCart(
    val id: Long,
    val total: BigDecimal,
    val items: List<CartItem>,
    val expiredPoolShareRemoved: Boolean = false,
    /** Count from the full snapshot, independent of the number of grouped cards. */
    val totalBetCount: Int? = null,
) {
    val betCount: Int
        get() = totalBetCount ?: items.sumOf { if (it.kind == CartItemKind.COMBO) it.comboBetCount else 1 }
}

data class FavoriteCart(
    val id: Long,
    val name: String,
)

data class FavoriteCartBet(
    val id: Long,
    val modality: String,
    val numbers: List<Int>,
    val clovers: List<Int> = emptyList(),
    val recurringDraws: Int = 0,
    val surprise: Boolean = false,
    val special: Boolean = false,
    val modalityId: Int? = null,
    val numberColumns: List<List<Int>> = emptyList(),
    val numberCount: Int = numbers.size,
    val cloverCount: Int = clovers.size,
    val favoriteTeam: String? = null,
    val luckyMonth: String? = null,
)

interface CartRepository {
    suspend fun current(): ShoppingCart
    suspend fun remove(item: CartItem): ShoppingCart
    suspend fun clear(): ShoppingCart
    suspend fun validate(): CartValidationResult
    suspend fun validateFavoriteName(name: String)
    suspend fun saveFavorite(name: String)
}

interface FavoriteCartRepository {
    suspend fun favorites(): List<FavoriteCart>
    suspend fun bets(cartId: Long): List<FavoriteCartBet>
    suspend fun deleteCart(cartId: Long): List<FavoriteCart>
    suspend fun deleteBet(cartId: Long, betId: Long): List<FavoriteCartBet>
    suspend fun addToCurrentCart(cartId: Long, specialSelection: Int): ShoppingCart
}

/** Persistência local da regra legada que alerta quando um favorito já virou carrinho. */
interface FavoriteCartUsageRepository {
    fun wasAddedToCurrentCart(cartId: Long): Boolean
    fun markAsAddedToCurrentCart(cartId: Long)
}

fun interface GetCurrentCartUseCase {
    suspend operator fun invoke(): AppResult<ShoppingCart>
}

class GetCurrentCart(private val repository: CartRepository) : GetCurrentCartUseCase {
    override suspend fun invoke() = appResultOf { repository.current() }
}

fun interface RemoveCartItemUseCase {
    suspend operator fun invoke(item: CartItem): AppResult<ShoppingCart>
}

class RemoveCartItem(private val repository: CartRepository) : RemoveCartItemUseCase {
    override suspend fun invoke(item: CartItem) = appResultOf {
        require(item.id > 0) { "Item do carrinho inválido" }
        repository.remove(item)
    }
}

fun interface ClearCartUseCase {
    suspend operator fun invoke(): AppResult<ShoppingCart>
}

class ClearCart(private val repository: CartRepository) : ClearCartUseCase {
    override suspend fun invoke() = appResultOf { repository.clear() }
}

sealed interface CartValidationResult {
    data object Accepted : CartValidationResult
    data class Rejected(val failure: ServiceFailure) : CartValidationResult
}

fun interface ValidateCartUseCase {
    suspend operator fun invoke(): AppResult<CartValidationResult>
}

class ValidateCart(private val repository: CartRepository) : ValidateCartUseCase {
    override suspend fun invoke() = appResultOf { repository.validate() }
}

fun interface ValidateFavoriteCartNameUseCase {
    suspend operator fun invoke(name: String): AppResult<Unit>
}

class ValidateFavoriteCartName(private val repository: CartRepository) : ValidateFavoriteCartNameUseCase {
    override suspend fun invoke(name: String) = appResultOf {
        require(name.isNotBlank()) { "Informe o nome do carrinho favorito" }
        repository.validateFavoriteName(name.trim())
    }
}

fun interface SaveCurrentCartFavoriteUseCase {
    suspend operator fun invoke(name: String): AppResult<Unit>
}

class SaveCurrentCartFavorite(private val repository: CartRepository) : SaveCurrentCartFavoriteUseCase {
    override suspend fun invoke(name: String) = appResultOf {
        require(name.isNotBlank()) { "Informe o nome do carrinho favorito" }
        repository.saveFavorite(name.trim())
    }
}

fun interface GetFavoriteCartsUseCase {
    suspend operator fun invoke(): AppResult<List<FavoriteCart>>
}

class GetFavoriteCarts(private val repository: FavoriteCartRepository) : GetFavoriteCartsUseCase {
    override suspend fun invoke() = appResultOf { repository.favorites() }
}

fun interface GetFavoriteCartBetsUseCase {
    suspend operator fun invoke(cartId: Long): AppResult<List<FavoriteCartBet>>
}

class GetFavoriteCartBets(private val repository: FavoriteCartRepository) : GetFavoriteCartBetsUseCase {
    override suspend fun invoke(cartId: Long) = appResultOf {
        require(cartId > 0) { "Carrinho favorito inválido" }
        repository.bets(cartId)
    }
}

fun interface DeleteFavoriteCartUseCase {
    suspend operator fun invoke(cartId: Long): AppResult<List<FavoriteCart>>
}

class DeleteFavoriteCart(private val repository: FavoriteCartRepository) : DeleteFavoriteCartUseCase {
    override suspend fun invoke(cartId: Long) = appResultOf {
        require(cartId > 0) { "Carrinho favorito inválido" }
        repository.deleteCart(cartId)
    }
}

fun interface DeleteFavoriteCartBetUseCase {
    suspend operator fun invoke(cartId: Long, betId: Long): AppResult<List<FavoriteCartBet>>
}

class DeleteFavoriteCartBet(private val repository: FavoriteCartRepository) : DeleteFavoriteCartBetUseCase {
    override suspend fun invoke(cartId: Long, betId: Long) = appResultOf {
        require(cartId > 0 && betId > 0) { "Aposta favorita inválida" }
        repository.deleteBet(cartId, betId)
    }
}

fun interface AddFavoriteCartToCurrentUseCase {
    suspend operator fun invoke(cartId: Long, specialSelection: Int): AppResult<ShoppingCart>
}

class AddFavoriteCartToCurrent(private val repository: FavoriteCartRepository) : AddFavoriteCartToCurrentUseCase {
    override suspend fun invoke(cartId: Long, specialSelection: Int) = appResultOf {
        require(cartId > 0) { "Carrinho favorito inválido" }
        require(specialSelection in 0..2) { "Seleção de concurso inválida" }
        repository.addToCurrentCart(cartId, specialSelection)
    }
}

fun interface WasFavoriteCartAddedUseCase {
    operator fun invoke(cartId: Long): Boolean
}

class WasFavoriteCartAdded(private val repository: FavoriteCartUsageRepository) : WasFavoriteCartAddedUseCase {
    override fun invoke(cartId: Long): Boolean = cartId > 0 && repository.wasAddedToCurrentCart(cartId)
}

fun interface MarkFavoriteCartAddedUseCase {
    operator fun invoke(cartId: Long)
}

class MarkFavoriteCartAdded(private val repository: FavoriteCartUsageRepository) : MarkFavoriteCartAddedUseCase {
    override fun invoke(cartId: Long) {
        if (cartId > 0) repository.markAsAddedToCurrentCart(cartId)
    }
}
