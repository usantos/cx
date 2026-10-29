package br.gov.caixa.loterias.feature.cart.data.datasource.local

import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser

internal const val CART_DOCUMENT_KEY = "cart-document-v1"

internal fun encodeCartDocument(state: CartDocumentState): String = JsonObject().apply {
    addProperty("schema", 1)
    addProperty("lastId", state.lastId)
    addProperty("legacyReview", state.legacyReview)
    state.legacyDigest?.let { addProperty("legacyDigest", it) }
    add("tables", encodeTables(state.tables))
    state.receipt?.let { add("receipt", encodeReceipt(it)) }
    add("purchases", encodePurchases(state.purchases))
    add("favorites", JsonArray().apply { state.favorites.sorted().forEach(::add) })
}.toString()

private fun encodeTables(tables: Map<String, List<CartSnapshotRow>>) = JsonObject().apply {
    tables.toSortedMap().forEach { (table, rows) ->
        add(table, JsonArray().apply { rows.forEach { add(encodeRow(it)) } })
    }
}

private fun encodeRow(row: CartSnapshotRow) = JsonObject().apply {
    row.toSortedMap().forEach { (key, value) -> addProperty(key, value) }
}

private fun encodeReceipt(receipt: CartReceipt) = JsonObject().apply {
    addProperty("id", receipt.transferId)
    addProperty("owner", receipt.ownerKey)
    addProperty("phase", receipt.phase.name)
    add(
        "items",
        JsonObject().apply {
            receipt.fingerprints.toSortedMap().forEach { (id, hash) -> addProperty(id.toString(), hash) }
        }
    )
}

private fun encodePurchases(purchases: Map<String, List<String>>) = JsonObject().apply {
    purchases.toSortedMap().forEach { (amount, dates) ->
        add(amount, JsonArray().apply { dates.forEach(::add) })
    }
}

internal fun decodeCartDocument(values: Map<String, String>): CartDocumentState {
    val encoded = values[CART_DOCUMENT_KEY] ?: return emptyDocument(values)
    return try {
        decodeState(JsonParser.parseString(encoded).objectValue()).also { it.validate() }
    } catch (failure: JsonParseException) {
        invalidDocument(failure)
    } catch (failure: IllegalArgumentException) {
        invalidDocument(failure)
    } catch (failure: IllegalStateException) {
        invalidDocument(failure)
    } catch (failure: ArithmeticException) {
        invalidDocument(failure)
    }
}

private fun emptyDocument(values: Map<String, String>): CartDocumentState {
    if (values.isNotEmpty()) throw SecureStorageException.InvalidRecord()
    return CartDocumentState()
}

private fun decodeState(root: JsonObject): CartDocumentState {
    require(root.required("schema").longValue() == 1L)
    return CartDocumentState(
        tables = decodeTables(root.required("tables").objectValue()),
        receipt = root.get("receipt")?.let { decodeReceipt(it.objectValue()) },
        lastId = root.required("lastId").longValue(),
        purchases = decodePurchases(root.required("purchases").objectValue()),
        favorites = root.required("favorites").arrayValue().map { it.longValue() }.toSet(),
        legacyReview = root.required("legacyReview").booleanValue(),
        legacyDigest = root.get("legacyDigest")?.stringValue()
    )
}

private fun decodeTables(tables: JsonObject): Map<String, List<CartSnapshotRow>> = tables.entrySet().associate { (table, rows) ->
    table to rows.arrayValue().map { row ->
        row.objectValue().entrySet().associate { (key, value) ->
            key to if (value.isJsonNull) null else value.stringValue()
        }
    }
}

private fun decodeReceipt(receipt: JsonObject) = CartReceipt(
    receipt.required("id").stringValue(),
    receipt.required("owner").stringValue(),
    CartReceiptPhase.valueOf(receipt.required("phase").stringValue()),
    receipt.required("items").objectValue().entrySet().associate { (id, hash) ->
        id.toLong() to hash.stringValue()
    }
)

private fun decodePurchases(purchases: JsonObject): Map<String, List<String>> = purchases.entrySet().associate { (amount, dates) ->
    amount to dates.arrayValue().map { it.stringValue() }
}

private fun JsonObject.required(name: String): JsonElement = checkNotNull(get(name))

private fun JsonElement.objectValue(): JsonObject {
    check(isJsonObject)
    return asJsonObject
}

private fun JsonElement.arrayValue(): JsonArray {
    check(isJsonArray)
    return asJsonArray
}

private fun JsonElement.stringValue(): String {
    check(isJsonPrimitive && asJsonPrimitive.isString)
    return asString
}

private fun JsonElement.booleanValue(): Boolean {
    check(isJsonPrimitive && asJsonPrimitive.isBoolean)
    return asBoolean
}

private fun JsonElement.longValue(): Long {
    check(isJsonPrimitive && asJsonPrimitive.isNumber)
    return asBigDecimal.longValueExact()
}

private fun invalidDocument(cause: Throwable): Nothing = throw SecureStorageException.InvalidRecord(cause)
