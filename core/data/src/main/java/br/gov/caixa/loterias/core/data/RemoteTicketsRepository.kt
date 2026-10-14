package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.tickets.CheckedTicketResult
import br.gov.caixa.loterias.core.domain.tickets.CheckedTicketAward
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.core.domain.tickets.Purchase
import br.gov.caixa.loterias.core.domain.tickets.PurchaseDetails
import br.gov.caixa.loterias.core.domain.tickets.PurchaseMonth
import br.gov.caixa.loterias.core.domain.tickets.TicketFilterOption
import br.gov.caixa.loterias.core.domain.tickets.TicketPeriod
import br.gov.caixa.loterias.core.domain.tickets.TicketQuery
import br.gov.caixa.loterias.core.domain.tickets.TicketsRepository
import br.gov.caixa.loterias.core.domain.betting.LotecaMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LotogolMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LuckyMonth
import br.gov.caixa.loterias.core.domain.betting.SportsMatch
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import br.gov.caixa.loterias.core.network.AppParametersDto
import br.gov.caixa.loterias.core.network.BetsHistoryApiProvider
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.math.BigDecimal

interface TicketsRemoteDataSource {
    suspend fun confirmedBets(filters: Map<String, String>): JsonElement?
    fun confirmedBetsBackend(): br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend =
        br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend.SILCE
    suspend fun recoverConfirmedBet(betId: Long): JsonElement? = null
    suspend fun checkConfirmedBet(betId: Long): JsonElement? = null
    suspend fun confirmedBetPrizeDetails(betId: Long): JsonElement? = null
    suspend fun confirmedBetReceipt(betId: Long, paid: Boolean, historical: Boolean): JsonElement? = null
    suspend fun purchases(filters: Map<String, String>): JsonElement?
    suspend fun purchaseBets(purchaseId: Long): JsonElement?
    suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) = Unit
    suspend fun checkTicket(code: String, qrCode: Boolean): JsonElement?
    suspend fun confirmedBetsConfiguration(): JsonElement?
    suspend fun purchaseMonths(): JsonElement?
    suspend fun purchasePaymentMethods(): JsonElement?
    suspend fun purchaseStatuses(paymentMethodId: Long?): JsonElement?
}

class RetrofitTicketsRemoteDataSource(
    private val apiProvider: LegacyBackendApiProvider,
    private val betsHistoryApiProvider: BetsHistoryApiProvider? = null,
    private val configurationSource: AppConfigurationRemoteDataSource? = null,
    private val currentUserCpfProvider: CurrentUserCpfProvider = CurrentUserCpfProvider { "" },
    private val production: Boolean = false,
) : TicketsRemoteDataSource {
    @Volatile private var cachedBetHistoryParameters: AppParametersDto? = null
    @Volatile private var activeBetHistoryRoute = BetHistoryRoute(BetHistoryBackend.SILCE)

    override suspend fun confirmedBets(filters: Map<String, String>): JsonElement? {
        val parameters = cachedBetHistoryParameters ?: configurationSource?.appParameters()?.values
            ?.also { cachedBetHistoryParameters = it }
        val route = BetHistoryRoutingPolicy.resolve(
            parameters = parameters,
            cpf = currentUserCpfProvider.currentCpf(),
            production = production,
        )
        val dynamicApi = route.baseUrl?.let { betsHistoryApiProvider?.current(it) }
        return when {
            dynamicApi == null -> {
                activeBetHistoryRoute = BetHistoryRoute(BetHistoryBackend.SILCE)
                apiProvider.current().confirmedBets(filters).payload
            }
            route.backend == BetHistoryBackend.NOVA_API ->
                dynamicApi.novaApiConfirmedBets(filters.forDynamicHistory()).payload.also {
                    activeBetHistoryRoute = route
                }
            else -> dynamicApi.confirmedBets(filters.forDynamicHistory()).payload.also {
                activeBetHistoryRoute = route
            }
        }
    }

    override fun confirmedBetsBackend() =
        br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend.valueOf(activeBetHistoryRoute.backend.name)

    override suspend fun recoverConfirmedBet(betId: Long): JsonElement? {
        val route = activeBetHistoryRoute.takeIf { it.backend == BetHistoryBackend.NOVA_API } ?: return null
        return route.baseUrl?.let { betsHistoryApiProvider?.current(it) }?.recoverBet(betId)?.checkedPayload()
    }

    override suspend fun checkConfirmedBet(betId: Long) = apiProvider.current().checkBet(betId).checkedPayload()
    override suspend fun confirmedBetPrizeDetails(betId: Long) = apiProvider.current().prizeDetails(betId).checkedPayload()

    override suspend fun confirmedBetReceipt(betId: Long, paid: Boolean, historical: Boolean) = when {
        historical -> apiProvider.current().historicalPrizeReceipt(betId)
        paid -> apiProvider.current().prizeReceipt(betId)
        else -> apiProvider.current().betReceipt(betId)
    }.checkedPayload()

    override suspend fun purchases(filters: Map<String, String>) =
        apiProvider.current().purchases(filters).payload

    override suspend fun purchaseBets(purchaseId: Long): JsonElement? {
        val response = apiProvider.current().purchaseBets(purchaseId)
        val serverDateTime = response.serverDateTime ?: return response.payload
        return JsonObject().apply {
            add("payload", response.payload)
            addProperty("dataHoraServidor", serverDateTime)
        }
    }

    override suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) {
        val body = JsonObject().apply {
            addProperty("nome", name)
            addProperty("mantemSurpresinhas", keepSurprises)
        }
        apiProvider.current().savePurchaseAsFavoriteCart(purchaseId, body)
    }

    override suspend fun checkTicket(code: String, qrCode: Boolean): JsonElement? {
        val body = JsonParser.parseString("{\"codigo\":${code.toJsonString()}}")
        return if (qrCode) apiProvider.current().checkTicketQrCode(body).payload
        else apiProvider.current().checkTicket(body).payload
    }

    override suspend fun confirmedBetsConfiguration() = apiProvider.current().confirmedBetsConfiguration().payload
    override suspend fun purchaseMonths() = apiProvider.current().purchaseMonths().payload
    override suspend fun purchasePaymentMethods() = apiProvider.current().paymentMethods().payload
    override suspend fun purchaseStatuses(paymentMethodId: Long?) =
        apiProvider.current().purchaseStatuses(paymentMethodId).payload
}

fun interface CurrentUserCpfProvider {
    fun currentCpf(): String
}

internal enum class BetHistoryBackend { SILCE, MICROSERVICE, NOVA_API }

internal data class BetHistoryRoute(
    val backend: BetHistoryBackend,
    val baseUrl: String? = null,
)

/** Exact migration of the rollout performed by the legacy MinhasApostasActivity. */
internal object BetHistoryRoutingPolicy {
    fun resolve(parameters: AppParametersDto?, cpf: String, production: Boolean): BetHistoryRoute {
        val novaBaseUrl = if (production) {
            parameters?.productionBetsApiBaseUrl
        } else {
            parameters?.pilotBetsApiBaseUrl
        }
        if (!novaBaseUrl.isNullOrBlank() && cpfIsInNovaApiRollout(cpf, parameters?.novaApiCpfRolloutPercentage)) {
            return BetHistoryRoute(BetHistoryBackend.NOVA_API, novaBaseUrl.appendLegacyV1())
        }

        val microserviceUrl = parameters?.betsHistoryBaseUrl?.trim()
        val validForFlavor = !production || microserviceUrl?.endsWith("/v1") == true ||
            microserviceUrl?.endsWith("/v2") == true
        if (parameters?.microserviceEnabled == true && !microserviceUrl.isNullOrBlank() && validForFlavor) {
            return BetHistoryRoute(BetHistoryBackend.MICROSERVICE, microserviceUrl)
        }
        return BetHistoryRoute(BetHistoryBackend.SILCE)
    }

    private fun cpfIsInNovaApiRollout(cpf: String, percentage: Int?): Boolean {
        val rollout = percentage ?: 0
        if (rollout == 0) return false
        if (rollout == 100) return true
        val digits = cpf.filter(Char::isDigit)
        return digits.takeIf { it.length >= 2 }?.takeLast(2)?.toIntOrNull()?.let { it < rollout } == true
    }

    private fun String.appendLegacyV1(): String = if (endsWith('/')) "${this}v1" else "$this/v1"
}

private fun Map<String, String>.forDynamicHistory(): Map<String, String> = buildMap {
    putAll(this@forDynamicHistory)
    remove("combo")?.let { put("comboApostas", it) }
}

class RemoteTicketsRepository(
    private val remote: TicketsRemoteDataSource,
    private val ticketCodeEncoder: TicketCodeEncoder = LegacyRsaTicketCodeEncoder,
) : TicketsRepository {
    override suspend fun confirmedBets(filters: Map<String, String>): List<ConfirmedBet> =
        remote.confirmedBets(filters).toBets()

    override suspend fun confirmedBetsPage(filters: Map<String, String>): br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsPage {
        val bets = remote.confirmedBets(filters).toBets().map { it.copy(backend = remote.confirmedBetsBackend()) }
        return br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsPage(bets, remote.confirmedBetsBackend())
    }

    override suspend fun recoverConfirmedBet(bet: ConfirmedBet): ConfirmedBet =
        remote.recoverConfirmedBet(bet.id).toBets().firstOrNull() ?: bet

    override suspend fun loadConfirmedBet(bet: ConfirmedBet) = serviceResultOf {
        require(bet.id > 0) { "Aposta inválida" }
        val checked = if (bet.statusId in setOf(4L, 101L) || bet.status.equals("Efetivada", true)) {
            val status = requireNotNull(remote.checkConfirmedBet(bet.id)) { "Situação da aposta ausente" }.asJsonObject
            bet.copy(statusId = requireNotNull(status.long("valor")) { "Situação da aposta incompleta" },
                status = status.text("descricao").displayName().ifBlank { bet.status })
        } else bet
        val payload = if (bet.backend == br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend.NOVA_API) {
            remote.recoverConfirmedBet(checked.id)
        } else {
            remote.confirmedBetReceipt(checked.id, paid = checked.statusId == 11L, historical = checked.statusId == 3L)
        }
        val hydrated = requireNotNull(payload.toBets().firstOrNull { it.id == checked.id }) { "Comprovante da aposta ausente" }
        // Installed clients retain list status for Nova API/microservice and teimosinhas;
        // both receipt endpoints have a known incorrect combo flag.
        val keepStatus = bet.backend != br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend.SILCE ||
            hydrated.recurringDraws > 0 || checked !== bet || checked.statusId in setOf(3L, 11L, 100L)
        val receiptRoot = payload?.takeIf(JsonElement::isJsonObject)?.asJsonObject
        val prizeRoot = if (checked.statusId in setOf(100L, 11L) &&
            receiptRoot?.get("premio")?.takeIf(JsonElement::isJsonObject) == null) {
            requireNotNull(remote.confirmedBetPrizeDetails(checked.id)) { "Detalhes do prêmio ausentes" }.asJsonObject
        } else receiptRoot
        val prize = prizeRoot?.get("premio")?.takeIf(JsonElement::isJsonObject)?.asJsonObject
        if (checked.statusId in setOf(100L, 11L)) requireNotNull(prize) { "Dados do prêmio ausentes" }
        val netPrize = prize?.get("valorLiquido")?.takeUnless(JsonElement::isJsonNull)?.let {
            runCatching { it.asBigDecimal }.getOrNull()
        }
        if (checked.statusId in setOf(100L, 11L)) requireNotNull(netPrize) { "Valor líquido do prêmio ausente" }
        val receipt = br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetReceipt(
            nsbi = receiptRoot?.text("nsbi").orEmpty(),
            paymentDate = prize?.text("dataPagamento").orEmpty(),
            paymentTime = prize?.text("horaPagamento").orEmpty(),
            paymentLocation = prize?.text("localPagamento").orEmpty(),
            paymentNsu = prize?.text("nsuPagamento").orEmpty(),
            netPrize = netPrize,
            awards = prizeRoot?.toCheckedTicketResult()?.awards.orEmpty(),
        )
        hydrated.copy(
            receipt = receipt,
            backend = bet.backend,
            combo = bet.combo,
            statusId = if (keepStatus) checked.statusId else hydrated.statusId,
            status = if (keepStatus) checked.status else hydrated.status,
            prize = receipt.netPrize ?: hydrated.prize.takeIf { it.signum() != 0 } ?: checked.prize,
            redeemedPrize = checked.redeemedPrize || checked.statusId in setOf(3L, 11L),
            originalRecurringBet = hydrated.originalRecurringBet || bet.originalRecurringBet,
        )
    }

    override suspend fun purchases(filters: Map<String, String>): List<Purchase> =
        remote.purchases(filters).toPurchases()

    override suspend fun purchaseBets(purchaseId: Long): List<ConfirmedBet> =
        remote.purchaseBets(purchaseId).toBets()

    override suspend fun purchaseDetails(purchase: Purchase): PurchaseDetails {
        val payload = remote.purchaseBets(purchase.id)
        return PurchaseDetails(
            purchase = payload.toPurchases().firstOrNull { it.id == purchase.id } ?: purchase,
            bets = payload.toBets(),
            serverDateTime = payload.findFirstText("dataHoraServidor"),
        )
    }

    override suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) =
        remote.savePurchaseAsFavorite(purchaseId, name, keepSurprises)

    override suspend fun checkTicket(query: TicketQuery): List<ConfirmedBet> {
        val encoded = ticketCodeEncoder.encode(query)
        return remote.checkTicket(encoded.value, encoded.qrCode).toBets()
    }

    override suspend fun checkTicketResult(query: TicketQuery): CheckedTicketResult {
        val encoded = ticketCodeEncoder.encode(query)
        return remote.checkTicket(encoded.value, encoded.qrCode).toCheckedTicketResult()
    }

    override suspend fun confirmedBetsConfiguration(): ConfirmedBetsConfiguration =
        remote.confirmedBetsConfiguration().toConfirmedBetsConfiguration()

    override suspend fun purchaseMonths(): List<PurchaseMonth> = remote.purchaseMonths().toPurchaseMonths()

    override suspend fun purchasePaymentMethods(): List<TicketFilterOption> =
        remote.purchasePaymentMethods().toFilterOptions("nomePagamentoPremio", "nomePagamentoCompra")

    override suspend fun purchaseStatuses(paymentMethodId: Long?): List<TicketFilterOption> =
        remote.purchaseStatuses(paymentMethodId).toFilterOptions("descricao")
}

internal fun JsonElement?.toConfirmedBetsConfiguration(): ConfirmedBetsConfiguration {
    val root = payloadObject() ?: return ConfirmedBetsConfiguration()
    fun options(name: String) = root.get(name).toFilterOptions("descricao", idKey = "valor")
    fun period(element: JsonElement?): TicketPeriod? {
        val value = element?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: return null
        val month = value.getAsJsonObject("mes") ?: return null
        return TicketPeriod(
            month = month.int("valor") ?: return null,
            year = value.int("ano"),
            description = month.text("descricao").ifBlank { month.int("valor")?.toString().orEmpty() },
        )
    }
    return ConfirmedBetsConfiguration(
        defaultOrdering = root.getAsJsonObject("ordenacaoPadrao")?.long("valor"),
        defaultStatus = root.getAsJsonObject("situacaoPadrao")?.long("valor"),
        defaultPeriod = period(root.get("periodoPadrao")),
        statuses = options("situacoes"),
        orderings = options("ordenacoes"),
        periods = root.get("periodos")?.takeIf(JsonElement::isJsonArray)?.asJsonArray
            ?.mapNotNull(::period).orEmpty(),
        betTypes = options("listaTipoAposta"),
        contestTypes = options("listaTipoConcurso"),
    )
}

internal fun JsonElement?.toPurchaseMonths(): List<PurchaseMonth> = payloadArrayObjects().mapNotNull { value ->
    val month = value.getAsJsonObject("mes") ?: return@mapNotNull null
    val number = month.int("valor") ?: return@mapNotNull null
    val year = value.int("ano") ?: return@mapNotNull null
    PurchaseMonth(number, year, "${month.text("descricao")} de $year")
}

internal fun JsonElement?.toFilterOptions(
    vararg labelKeys: String,
    idKey: String = "id",
): List<TicketFilterOption> = payloadArrayObjects().mapNotNull { value ->
    val id = value.long(idKey) ?: return@mapNotNull null
    val label = labelKeys.firstNotNullOfOrNull { key -> value.text(key).takeIf(String::isNotBlank) }
        ?: return@mapNotNull null
    TicketFilterOption(id, label.replace(" - ", "-"))
}.distinctBy(TicketFilterOption::value)

private fun JsonElement?.payloadObject(): JsonObject? = when {
    this == null || isJsonNull -> null
    isJsonObject && asJsonObject.has("payload") -> asJsonObject.get("payload").payloadObject()
    isJsonObject -> asJsonObject
    else -> null
}

private fun JsonElement?.payloadArrayObjects(): List<JsonObject> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.mapNotNull { it.takeIf(JsonElement::isJsonObject)?.asJsonObject }
    isJsonObject && asJsonObject.has("payload") -> asJsonObject.get("payload").payloadArrayObjects()
    else -> emptyList()
}

/** Mirrors ResultadoConsultaBilheteDTO and ViewUtils.getFaixaPremiadoNaoPremiado. */
internal fun JsonElement?.toCheckedTicketResult(): CheckedTicketResult {
    val root = payloadObject() ?: return CheckedTicketResult()
    val prize = root.element("premio")?.takeIf(JsonElement::isJsonObject)?.asJsonObject
    fun JsonObject.optionalDecimal(key: String): BigDecimal? =
        runCatching { element(key)?.asBigDecimal }.getOrNull()
    val rows = buildList {
        prize?.get("concursos").payloadArrayObjects().forEach { contest ->
            val number = contest.childInt("concurso", "numero")
            contest.get("faixas").payloadArrayObjects().forEach { award ->
                add(CheckedTicketAward(number, contest.text("situacao"), award.text("nome"),
                    award.int("quantidadeAcertos"), award.int("sorteio"),
                    award.optionalDecimal("valorPremioCota") ?: award.optionalDecimal("valorLiquido")))
            }
        }
        prize?.get("concursosNaoPremiado").payloadArrayObjects().forEach { contest ->
            add(CheckedTicketAward(contest.childInt("concurso", "numero"), contest.text("situacao")))
        }
    }
    return CheckedTicketResult(
        bets = toBets(), statusId = root.childInt("situacao", "valor") ?: root.int("situacao"),
        modalityId = root.childInt("modalidade", "valor"), modalityName = root.text("modalidade").displayName(),
        netPrize = prize?.optionalDecimal("valorLiquido") ?: root.optionalDecimal("valorPremio"),
        awards = rows,
    )
}

internal fun JsonElement?.toBets(): List<ConfirmedBet> = collectObjects().filter { objectValue ->
    objectValue.long("id") != null && (
        objectValue.has("modalidade") || objectValue.has("numeroConcurso") ||
            objectValue.has("concursoAlvo") || objectValue.has("numerosSelecionados")
        )
}.distinctBy { it.long("id") }.map { value ->
    val numberColumns = value.element("numerosSelecionados").numberRows().ifEmpty {
        value.element("dezenas").numberRows()
    }
    val modalityCode = value.rawText("modalidade")
    ConfirmedBet(
        id = value.long("id") ?: 0,
        modality = value.text("modalidade").displayName(),
        contest = value.int("numeroConcurso") ?: value.int("concursoInicial") ?: value.int("concursoAlvo")
            ?: value.childInt("concursoAlvo", "numero") ?: 0,
        date = value.text("dataEfetivacao").ifBlank { value.text("dataCompra") },
        status = value.text("situacao").displayName(),
        numbers = numberColumns.flatten(),
        prize = value.decimal("valorPremio"),
        originalRecurringBet = !value.bool("troca") && ((value.int("quantidadeTeimosinhas") ?: 0) > 0 ||
            value.bool("apostaOriginal") || value.bool("teimosinha")),
        redeemedPrize = value.bool("houveResgate") || value.text("situacao").contains("RESGAT", true),
        exchangeBetId = value.long("idApostaTroca"),
        modalityId = value.childInt("modalidade", "valor") ?: value.int("idModalidade")
            ?: modalityCode.modalityId(),
        statusId = value.childLong("situacao", "valor") ?: value.long("idSituacao") ?: 0,
        contestTypeId = value.contestTypeId(),
        poolShare = value.bool("indicadorCotaBolao") || value.bool("cotaBolao"),
        surprise = value.surpriseFlag(),
        time = value.text("horaEfetivacao"),
        numberColumns = numberColumns,
        clovers = value.numbers("numerosSelecionadosMaisMilionariaTrevo").ifEmpty {
            value.element("trevos")?.takeIf(JsonElement::isJsonObject)?.asJsonObject
                ?.numbers("trevosSelecionados").orEmpty()
        },
        favoriteTeam = value.element("timeDoCoracao").toSportsTeam(),
        luckyMonth = value.element("mesDeSorte").toLuckyMonth(),
        lotecaSelections = value.element("partidasLoteca").toLotecaSelections(),
        lotogolSelections = value.element("partidasLotogol").toLotogolSelections(),
        recurringDraws = value.int("quantidadeTeimosinhas") ?: 0,
        quantity = value.int("quantidadeApostas") ?: 1,
        mirror = value.bool("espelho") || value.bool("geraEspelho"),
        exchange = value.bool("troca"),
        combo = value.bool("combo"),
    )
}

internal fun JsonElement?.toPurchases(): List<Purchase> = collectObjects().filter { objectValue ->
    objectValue.long("id") != null && objectValue.has("valorTotal") &&
        (objectValue.has("dataInicioCompra") || objectValue.has("situacao"))
}.distinctBy { it.long("id") }.map { value ->
    Purchase(
        id = value.long("id") ?: 0,
        date = value.text("dataInicioCompra").ifBlank { value.text("dataFinalizacaoCompra") },
        status = value.text("situacao").displayName(),
        paymentMethod = value.text("meioPagamento").displayName(),
        total = value.decimal("valorTotal"),
        pixCopyPaste = value.text("pixCopiaECola"),
        paymentExpiresAt = value.text("dataHoraExpiracaoPagamento"),
        statusId = value.childLong("situacao", "valor") ?: value.long("idSituacaoCompra") ?: 0,
        paymentMethodId = value.childLong("meioPagamento", "valor") ?: value.long("idMeioPagamento") ?: 0,
        finalizedTime = value.text("horaFinalizacaoCompra"),
        endToEndId = value.text("e2eId"),
    )
}

private fun JsonElement?.collectObjects(): List<JsonObject> = when {
    this == null || isJsonNull -> emptyList()
    isJsonObject -> listOf(asJsonObject) + asJsonObject.entrySet().flatMap { it.value.collectObjects() }
    isJsonArray -> asJsonArray.flatMap { it.collectObjects() }
    else -> emptyList()
}

private fun JsonObject.element(name: String): JsonElement? = get(name)?.takeUnless(JsonElement::isJsonNull)
private fun JsonObject.long(name: String): Long? = runCatching { element(name)?.asLong }.getOrNull()
private fun JsonObject.int(name: String): Int? = runCatching { element(name)?.asInt }.getOrNull()
private fun JsonObject.bool(name: String): Boolean = runCatching { element(name)?.asBoolean }.getOrNull() ?: false
private fun JsonObject.decimal(name: String): BigDecimal = runCatching { element(name)?.asBigDecimal }.getOrNull() ?: BigDecimal.ZERO
private fun JsonObject.childInt(name: String, child: String): Int? = runCatching { element(name)?.asJsonObject?.int(child) }.getOrNull()
private fun JsonObject.childLong(name: String, child: String): Long? = runCatching { element(name)?.asJsonObject?.long(child) }.getOrNull()

private fun JsonObject.contestTypeId(): Long {
    childLong("tipoConcurso", "valor")?.let { return it }
    long("idTipoConcurso")?.let { return it }
    val raw = element("tipoConcurso")?.let { contestType ->
        when {
            contestType.isJsonPrimitive -> runCatching { contestType.asString }.getOrDefault("")
            contestType.isJsonObject -> contestType.asJsonObject.rawText("valor")
                .ifBlank { contestType.asJsonObject.rawText("descricao") }
            else -> ""
        }
    }.orEmpty()
    return when {
        raw.equals("E", true) || raw.contains("ESPECIAL", true) -> 2
        raw.equals("N", true) || raw.contains("NORMAL", true) -> 1
        else -> 0
    }
}

private fun JsonObject.surpriseFlag(): Boolean {
    if (bool("surpresinha")) return true
    val indicator = text("indicadorSurpresinha")
    return indicator.isNotBlank() && indicator != "0" &&
        !indicator.contains("NAO", ignoreCase = true) && !indicator.contains("NÃO", ignoreCase = true)
}

private fun JsonElement?.findFirstText(name: String): String = when {
    this == null || isJsonNull -> ""
    isJsonObject -> asJsonObject.text(name).ifBlank {
        asJsonObject.entrySet().firstNotNullOfOrNull { (_, value) ->
            value.findFirstText(name).takeIf(String::isNotBlank)
        }.orEmpty()
    }
    isJsonArray -> asJsonArray.firstNotNullOfOrNull { value ->
        value.findFirstText(name).takeIf(String::isNotBlank)
    }.orEmpty()
    else -> ""
}
private fun JsonObject.text(name: String): String {
    val value = element(name) ?: return ""
    return when {
        value.isJsonPrimitive -> runCatching { value.asString }.getOrDefault("")
        value.isJsonObject -> listOf("descricao", "nome", "sigla", "valor")
            .firstNotNullOfOrNull { key -> value.asJsonObject.element(key)?.let { runCatching { it.asString }.getOrNull() } }.orEmpty()
        else -> ""
    }
}
private fun JsonObject.rawText(name: String): String = element(name)?.let { value ->
    when {
        value.isJsonPrimitive -> runCatching { value.asString }.getOrDefault("")
        value.isJsonObject -> value.asJsonObject.text("descricao").ifBlank {
            value.asJsonObject.text("nome")
        }
        else -> ""
    }
}.orEmpty()
private fun JsonObject.numbers(name: String): List<Int> = element(name).flattenNumbers()
private fun JsonElement?.numberRows(): List<List<Int>> = when {
    this == null || isJsonNull -> emptyList()
    isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(listOf(asInt))
    isJsonArray && asJsonArray.any(JsonElement::isJsonArray) -> asJsonArray.map { it.flattenNumbers() }
    isJsonArray -> listOf(flattenNumbers())
    else -> emptyList()
}
private fun JsonElement?.flattenNumbers(): List<Int> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.flatMap { it.flattenNumbers() }
    isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
    else -> emptyList()
}

private fun JsonElement?.toLuckyMonth(): LuckyMonth? {
    val value = this?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: return null
    val number = value.int("numero") ?: return null
    return LuckyMonth(number, value.text("nome"), value.text("abreviacao"))
}

private fun JsonElement?.toSportsTeam(): SportsTeam? {
    val outer = this?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: return null
    val value = outer.element("parametroEquipe")?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: outer
    val number = value.int("numero") ?: outer.int("numero") ?: 0
    val name = value.text("nome").ifBlank { outer.text("nome") }
    if (number <= 0 && name.isBlank()) return null
    return SportsTeam(
        number = number,
        name = name,
        state = value.text("uf").ifBlank { outer.text("uf") },
        shortDescription = value.text("descricaoCurta").ifBlank { outer.text("descricaoCurta") },
        longDescription = value.text("descricaoLonga").ifBlank { outer.text("descricaoLonga") },
        countryNumber = value.int("numeroPais") ?: outer.int("numeroPais") ?: 0,
        country = value.text("pais").ifBlank { outer.text("pais") },
        countryCode = value.text("siglaPais").ifBlank { outer.text("siglaPais") },
        className = value.text("nomeClass").ifBlank { outer.text("nomeClass") },
        selectionIndicator = value.bool("indicadorSelecao") || outer.bool("indicadorSelecao"),
    )
}

private fun JsonElement?.toLotecaSelections(): List<LotecaMatchSelection> = jsonObjects().mapIndexedNotNull { index, value ->
    val homeElement = value.element("equipe1")
    val awayElement = value.element("equipe2")
    val home = homeElement.toSportsTeam() ?: return@mapIndexedNotNull null
    val away = awayElement.toSportsTeam() ?: return@mapIndexedNotNull null
    val match = SportsMatch(
        number = value.int("numero") ?: index + 1,
        home = home,
        away = away,
        legendDescription = value.text("descricaoLegenda"),
        legendLetter = value.text("letraLegenda"),
    )
    LotecaMatchSelection(
        match = match,
        homeWin = homeElement.jsonBool("vitoria"),
        draw = value.bool("empate"),
        awayWin = awayElement.jsonBool("vitoria"),
    )
}

private fun JsonElement?.toLotogolSelections(): List<LotogolMatchSelection> = jsonObjects().mapIndexedNotNull { index, value ->
    val homeElement = value.element("equipe1")
    val awayElement = value.element("equipe2")
    val home = homeElement.toSportsTeam() ?: return@mapIndexedNotNull null
    val away = awayElement.toSportsTeam() ?: return@mapIndexedNotNull null
    val homeScore = value.int("indexPlacarEquipe1") ?: homeElement.jsonScore() ?: return@mapIndexedNotNull null
    val awayScore = value.int("indexPlacarEquipe2") ?: awayElement.jsonScore() ?: return@mapIndexedNotNull null
    LotogolMatchSelection(
        match = SportsMatch(index + 1, home, away),
        homeScore = homeScore,
        awayScore = awayScore,
    )
}

private fun JsonElement?.jsonObjects(): List<JsonObject> =
    this?.takeIf(JsonElement::isJsonArray)?.asJsonArray?.mapNotNull {
        it.takeIf(JsonElement::isJsonObject)?.asJsonObject
    }.orEmpty()

private fun JsonElement?.jsonBool(name: String): Boolean =
    this?.takeIf(JsonElement::isJsonObject)?.asJsonObject?.bool(name) == true

private fun JsonElement?.jsonScore(): Int? = this?.takeIf(JsonElement::isJsonObject)?.asJsonObject
    ?.text("placar")?.filter(Char::isDigit)?.toIntOrNull()

private fun String.modalityId(): Int = when (uppercase().replace('-', '_').replace(' ', '_')) {
    "MEGA_SENA" -> 2
    "QUINA" -> 3
    "SUPER_7", "SUPER_SETE" -> 7
    "LOTOFACIL" -> 8
    "MAIS_MILIONARIA", "+MILIONARIA" -> 9
    "DIA_DE_SORTE" -> 11
    "LOTOGOL" -> 14
    "LOTOMANIA" -> 16
    "DUPLA_SENA" -> 18
    "LOTECA" -> 19
    "TIMEMANIA" -> 20
    else -> 0
}
private fun String.displayName(): String = trim().lowercase().split('_', '-', ' ')
    .filter(String::isNotBlank).joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
private fun String.toJsonString(): String = JsonParser.parseString("{}")
    .asJsonObject.apply { addProperty("value", this@toJsonString) }.get("value").toString()
