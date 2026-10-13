package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.retail.RetailRepository
import br.gov.caixa.loterias.core.domain.retail.Retailer
import br.gov.caixa.loterias.core.domain.retail.LotteryRedemptionCode
import br.gov.caixa.loterias.core.domain.retail.PixBeneficiary
import br.gov.caixa.loterias.core.domain.retail.PrizeDetails
import br.gov.caixa.loterias.core.domain.retail.PrizeAward
import br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionChannel
import br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionOption
import br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionRepository
import br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionResult
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import br.gov.caixa.loterias.core.domain.payment.MercadoPagoRegistrationToken
import br.gov.caixa.loterias.core.network.BackendHttpFailure
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.math.BigDecimal

interface RetailRemoteDataSource {
    suspend fun search(query: String): JsonElement?
    suspend fun favorites(): JsonElement?
    suspend fun addFavorite(retailerId: Long)
    suspend fun deleteFavorite(retailerId: Long)
}

interface PrizeRedemptionRemoteDataSource {
    suspend fun prizeDetails(betId: Long): JsonElement?
    suspend fun lotteryCode(betId: Long): JsonElement?
    suspend fun consultPixKey(betId: Long, body: JsonObject): JsonElement?
    suspend fun redeemPrize(betId: Long, body: JsonObject): JsonElement?
    suspend fun cancelPix(betId: Long)
    suspend fun mercadoPagoToken(email: String): String
    suspend fun betReceiptPdf(betId: Long): ByteArray
    suspend fun prizeReceiptPdf(betId: Long): ByteArray
}

class RemoteRetailRepository(
    private val remote: RetailRemoteDataSource,
    private val prizeRemote: PrizeRedemptionRemoteDataSource? = remote as? PrizeRedemptionRemoteDataSource,
) : RetailRepository, PrizeRedemptionRepository, br.gov.caixa.loterias.core.domain.payment.MercadoPagoRegistrationRepository {
    override suspend fun search(query: String): List<Retailer> {
        val favoriteIds = remote.favorites().retailObjects().mapNotNull { it.long("codigo") ?: it.long("id") }.toSet()
        return remote.search(query).retailObjects().map { it.toRetailer(it.long("id") in favoriteIds) }
    }

    override suspend fun favorites(): List<Retailer> = remote.favorites().retailObjects().map { it.toRetailer(true) }

    override suspend fun setFavorite(retailerId: Long, favorite: Boolean) {
        if (favorite) remote.addFavorite(retailerId) else remote.deleteFavorite(retailerId)
    }

    override suspend fun details(betId: Long): PrizeDetails {
        val root = prizeSource().prizeDetails(betId).objectOrEmpty()
        val prize = root.objectValue("premio")
        val bet = root.objectValue("aposta")
        val responseBetId = bet.long("id") ?: betId
        val modalityCode = bet.text("modalidade")
        val modalityId = modalityCode.toPrizeModalityId()
        val modalityName = modalityCode.toPrizeModalityName()
        return PrizeDetails(
            betId = responseBetId,
            netValue = prize.decimal("valorLiquido"),
            nsbi = root.text("nsbi"),
            options = root.arrayObjects("meiosResgate").mapNotNull { option ->
                val channel = PrizeRedemptionChannel.entries.firstOrNull { it.legacyId == option.long("id") }
                    ?: return@mapNotNull null
                // O serviço decide quais canais estão disponíveis. Os textos são contrato
                // do produto e reproduzem a montagem de ResultadoApostaConfirmadaActivity.
                PrizeRedemptionOption(channel, channel.defaultTitle(), channel.defaultDescription())
            },
            incomeTaxMessage = root.text("mensagemInformativaIR"),
            modalityId = modalityId,
            modalityName = modalityName,
            initialContest = (bet.long("concursoInicial") ?: bet.long("concursoAlvo") ?: 0).toInt(),
            targetContest = (bet.long("concursoAlvo") ?: bet.long("concursoInicial") ?: 0).toInt(),
            isPool = bet.text("indicadorCotaBolao").equals("true", ignoreCase = true),
            exchange = bet.text("troca").equals("true", ignoreCase = true),
            combo = bet.text("combo").equals("true", ignoreCase = true),
            mirror = bet.text("espelho").equals("true", ignoreCase = true),
            surprise = bet.text("surpresinha").equals("true", ignoreCase = true),
            recurringDraws = (bet.long("quantidadeTeimosinhas") ?: 0).toInt(),
            awards = prize.arrayObjects("concursos").flatMap { awardedContest ->
                val contest = awardedContest.objectValue("concurso").long("numero")?.toInt()
                    ?: return@flatMap emptyList()
                awardedContest.arrayObjects("faixas").map { range ->
                    PrizeAward(
                        contest = contest,
                        description = range.installedPrizeDescription(modalityCode),
                        // Zero is a supplied quota prize, not an absent value.
                        amount = range.optionalDecimal("valorPremioCota") ?: range.optionalDecimal("valorLiquido"),
                        status = awardedContest.text("situacao"),
                        hitCount = range.long("quantidadeAcertos")?.toInt(),
                        draw = range.long("sorteio")?.toInt(),
                    )
                }
            } + prize.arrayObjects("concursosNaoPremiado").mapNotNull { contest ->
                val number = contest.objectValue("concurso").long("numero")?.toInt() ?: return@mapNotNull null
                PrizeAward(number, description = "", amount = null, status = contest.text("situacao"), isAwarded = false)
            },
        )
    }

    override suspend fun lotteryCode(betId: Long): LotteryRedemptionCode {
        val root = prizeSource().lotteryCode(betId).objectOrEmpty()
        return LotteryRedemptionCode(
            qrCode = root.text("qrCode"),
            code = root.text("codigo"),
            nsb = root.text("nsb"),
            validityMinutes = (root.long("validade") ?: 15).toInt().coerceAtLeast(1),
        )
    }

    override suspend fun consultPixKey(betId: Long, key: String): PixBeneficiary {
        val root = prizeSource().consultPixKey(betId, pixBody(key)).objectOrEmpty()
        return PixBeneficiary(root.text("cpfMascarado"), root.text("nome"), root.text("chave"), root.text("nomeBanco"))
    }

    override suspend fun redeemPix(betId: Long, key: String): PrizeRedemptionResult = try {
        PrizeRedemptionResult.Redeemed(prizeSource().redeemPrize(betId, pixBody(key)).noticeCode())
    } catch (failure: BackendHttpFailure) {
        failure.toServiceFailure(allowRegistration = false).toPrizeResult()
    } catch (failure: java.io.IOException) {
        ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()).toPrizeResult()
    }

    override suspend fun cancelPix(betId: Long) = prizeSource().cancelPix(betId)

    override suspend fun redeemMercadoPago(betId: Long, email: String): PrizeRedemptionResult = try {
        PrizeRedemptionResult.Redeemed(prizeSource().redeemPrize(betId, JsonObject().apply {
            addProperty("meioPagamento", PrizeRedemptionChannel.MERCADO_PAGO.legacyId)
            add("parametrosPagamento", JsonObject().apply { addProperty("EMAIL", email) })
        }).noticeCode())
    } catch (failure: BackendHttpFailure) {
        failure.toServiceFailure(allowRegistration = true).toPrizeResult()
    } catch (failure: java.io.IOException) {
        ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()).toPrizeResult()
    }

    override suspend fun mercadoPagoRegistrationToken(email: String): MercadoPagoRegistrationToken = try {
        MercadoPagoRegistrationToken.Available(prizeSource().mercadoPagoToken(email))
    } catch (failure: BackendHttpFailure) {
        MercadoPagoRegistrationToken.Rejected(failure.toServiceFailure(allowRegistration = true))
    } catch (failure: java.io.IOException) {
        MercadoPagoRegistrationToken.Rejected(ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()))
    }
    override suspend fun betReceiptPdf(betId: Long) = prizeSource().betReceiptPdf(betId)
    override suspend fun prizeReceiptPdf(betId: Long) = prizeSource().prizeReceiptPdf(betId)

    private fun prizeSource() = requireNotNull(prizeRemote) { "Fonte de resgate de prêmio não configurada" }
}

/** Installed ResultadoApostaConfirmadaViewModel#getAcertosNormalized. */
private fun JsonObject.installedPrizeDescription(modalityCode: String): String {
    var description = text("descricaoAcerto").ifBlank { text("nome") }
    // Preserve the installed expression's precedence and literal replacements.
    if (description.contains("1º") || description.contains("2º") && modalityCode == "LOTECA") {
        description = description.replace("1º (", "").replace("2º (", "").replace(")", "")
    }
    if (modalityCode == "DUPLA_SENA") {
        when (long("sorteio")?.toInt()) {
            1 -> description = "1º Sorteio - $description"
            2 -> description = "2º Sorteio - $description"
        }
    }
    val occurrences = long("quantidadeAcertos")?.toInt() ?: 0
    return description + if (occurrences >= 2) " ($occurrences vezes)" else ""
}

private fun ServiceFailure.toPrizeResult(): PrizeRedemptionResult = when (this) {
    is ServiceFailure.ConnectionFailed -> PrizeRedemptionResult.ConnectionFailed(reason)
    ServiceFailure.Unchanged -> PrizeRedemptionResult.Unchanged
    is ServiceFailure.NoticeRequired -> PrizeRedemptionResult.NoticeRequired(notice)
    is ServiceFailure.RedirectRequired -> PrizeRedemptionResult.RedirectRequired(destination)
    is ServiceFailure.RegistrationRequired -> PrizeRedemptionResult.RegistrationRequired(message)
}

private fun pixBody(key: String) = JsonObject().apply {
    addProperty("meioPagamento", PrizeRedemptionChannel.PIX.legacyId)
    addProperty("chavePix", key)
}

private fun PrizeRedemptionChannel.defaultTitle() = when (this) {
    PrizeRedemptionChannel.MERCADO_PAGO -> "Mercado Pago"
    PrizeRedemptionChannel.PIX -> "PIX"
    PrizeRedemptionChannel.LOTTERY -> "Lotérica"
    PrizeRedemptionChannel.AGENCY -> "Agência Caixa"
}

private fun PrizeRedemptionChannel.defaultDescription() = when (this) {
    PrizeRedemptionChannel.MERCADO_PAGO, PrizeRedemptionChannel.PIX -> "Receba na hora"
    PrizeRedemptionChannel.LOTTERY -> "Retire em qualquer lotérica"
    PrizeRedemptionChannel.AGENCY -> "Valores acima da isenção do IR"
}

private fun JsonObject.toRetailer(favorite: Boolean) = Retailer(
    id = long("id") ?: long("codigo") ?: 0,
    code = text("numeroFormatado").ifBlank { text("codigo") },
    fantasyName = text("nomeFantasia"),
    legalName = text("nome"),
    address = text("logradouro"),
    district = text("bairro"),
    city = text("nomeMunicipio"),
    state = text("nomeUF").ifBlank { text("siglaUf") },
    favorite = favorite,
)

private fun JsonElement?.retailObjects(): List<JsonObject> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.flatMap { it.retailObjects() }
    isJsonObject -> {
        val objectValue = asJsonObject
        if ((objectValue.has("id") || objectValue.has("codigo")) && objectValue.has("nomeFantasia")) listOf(objectValue)
        else objectValue.entrySet().flatMap { it.value.retailObjects() }
    }
    else -> emptyList()
}

private fun JsonObject.text(name: String): String = runCatching { get(name)?.takeUnless(JsonElement::isJsonNull)?.asString }.getOrNull().orEmpty()
private fun JsonObject.long(name: String): Long? = runCatching { get(name)?.takeUnless(JsonElement::isJsonNull)?.asLong }.getOrNull()
private fun JsonObject.optionalDecimal(name: String): BigDecimal? = runCatching { get(name)?.takeUnless(JsonElement::isJsonNull)?.asBigDecimal }.getOrNull()
private fun JsonObject.decimal(name: String): BigDecimal = optionalDecimal(name) ?: BigDecimal.ZERO
private fun JsonElement?.objectOrEmpty(): JsonObject = this?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: JsonObject()
private fun JsonObject.objectValue(name: String): JsonObject = get(name)?.objectOrEmpty() ?: JsonObject()
private fun JsonObject.arrayObjects(name: String): List<JsonObject> = get(name)?.takeIf(JsonElement::isJsonArray)?.asJsonArray
    ?.mapNotNull { it.takeIf(JsonElement::isJsonObject)?.asJsonObject }.orEmpty()

private fun String.toPrizeModalityId(): Int = when (uppercase()) {
    "QUINA" -> 3
    "SUPER_7" -> 7
    "LOTOFACIL" -> 8
    "MAIS_MILIONARIA" -> 9
    "DIA_DE_SORTE" -> 11
    "LOTOGOL" -> 14
    "LOTOMANIA" -> 16
    "DUPLA_SENA" -> 18
    "LOTECA" -> 19
    "TIMEMANIA" -> 20
    else -> 2
}

private fun String.toPrizeModalityName(): String = when (uppercase()) {
    "QUINA" -> "Quina"
    "SUPER_7" -> "Super Sete"
    "LOTOFACIL" -> "Lotofácil"
    "MAIS_MILIONARIA" -> "+Milionária"
    "DIA_DE_SORTE" -> "Dia de Sorte"
    "LOTOGOL" -> "Lotogol"
    "LOTOMANIA" -> "Lotomania"
    "DUPLA_SENA" -> "Dupla Sena"
    "LOTECA" -> "Loteca"
    "TIMEMANIA" -> "Timemania"
    else -> "Mega-Sena"
}

private fun JsonElement?.noticeCode(): String? = when {
    this == null || isJsonNull -> null
    isJsonPrimitive -> runCatching { asString }.getOrNull()?.trim()?.takeIf(String::isNotEmpty)
    isJsonObject -> asJsonObject.text("codigo").ifBlank { asJsonObject.text("mensagem") }.takeIf(String::isNotBlank)
    else -> null
}
