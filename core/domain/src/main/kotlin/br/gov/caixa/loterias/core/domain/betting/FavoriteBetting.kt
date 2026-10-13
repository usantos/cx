package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart

data class FavoriteBet(
    val id: Long,
    val modalityId: Int,
    val modalityName: String,
    val name: String,
    val numberColumns: List<List<Int>>,
    val heartTeam: String = "",
    val luckyMonth: String = "",
    val clovers: List<Int> = emptyList(),
) {
    val numbers: List<Int> get() = numberColumns.flatten()
    val isSuperSeven: Boolean get() = numberColumns.size > 1
}

data class FavoriteBetCartOptions(
    val contestType: Int = 1,
    val recurringDraws: Int = 0,
    val mirror: Boolean = false,
)

data class FavoriteBetDraft(
    val modalityId: Int,
    val modalityName: String,
    val name: String,
    val numberColumns: List<List<Int>>,
    val clovers: List<Int> = emptyList(),
    val favoriteTeam: SportsTeam? = null,
    val luckyMonth: LuckyMonth? = null,
)

interface FavoriteBetRepository {
    suspend fun favorites(modalityId: Int? = null, offset: Int = 0): List<FavoriteBet>
    suspend fun delete(favoriteBetId: Long)
    suspend fun addToCart(favoriteBetId: Long, options: FavoriteBetCartOptions): ShoppingCart
    suspend fun validate(draft: FavoriteBetDraft)
    suspend fun save(draft: FavoriteBetDraft)
}

fun interface ValidateFavoriteBetUseCase {
    suspend operator fun invoke(draft: FavoriteBetDraft): AppResult<Unit>
}

class ValidateFavoriteBet(private val repository: FavoriteBetRepository) : ValidateFavoriteBetUseCase {
    override suspend fun invoke(draft: FavoriteBetDraft) = appResultOf {
        draft.validate()
        repository.validate(draft)
    }
}

fun interface SaveFavoriteBetUseCase {
    suspend operator fun invoke(draft: FavoriteBetDraft): AppResult<Unit>
}

class SaveFavoriteBet(private val repository: FavoriteBetRepository) : SaveFavoriteBetUseCase {
    override suspend fun invoke(draft: FavoriteBetDraft) = appResultOf {
        draft.validate()
        repository.save(draft)
    }
}

private fun FavoriteBetDraft.validate() {
    require(modalityId > 0) { "Modalidade inválida" }
    require(name.isNotBlank()) { "Informe um nome para a aposta." }
    require(numberColumns.any { it.isNotEmpty() }) { "A aposta não possui números." }
}

fun interface GetFavoriteBetsUseCase {
    suspend operator fun invoke(modalityId: Int?): AppResult<List<FavoriteBet>>
}

class GetFavoriteBets(private val repository: FavoriteBetRepository) : GetFavoriteBetsUseCase {
    override suspend fun invoke(modalityId: Int?) = appResultOf {
        require(modalityId == null || modalityId > 0) { "Modalidade inválida" }
        repository.favorites(modalityId)
    }
}

fun interface DeleteFavoriteBetUseCase {
    suspend operator fun invoke(favoriteBetId: Long): AppResult<Unit>
}

class DeleteFavoriteBet(private val repository: FavoriteBetRepository) : DeleteFavoriteBetUseCase {
    override suspend fun invoke(favoriteBetId: Long) = appResultOf {
        require(favoriteBetId > 0) { "Aposta favorita inválida" }
        repository.delete(favoriteBetId)
    }
}

fun interface AddFavoriteBetToCartUseCase {
    suspend operator fun invoke(favoriteBetId: Long, options: FavoriteBetCartOptions): AppResult<ShoppingCart>
}

class AddFavoriteBetToCart(private val repository: FavoriteBetRepository) : AddFavoriteBetToCartUseCase {
    override suspend fun invoke(favoriteBetId: Long, options: FavoriteBetCartOptions) = appResultOf {
        require(favoriteBetId > 0) { "Aposta favorita inválida" }
        require(options.contestType in 1..2) { "Tipo de concurso inválido" }
        require(options.recurringDraws >= 0) { "Quantidade de Teimosinha inválida" }
        repository.addToCart(favoriteBetId, options)
    }
}
