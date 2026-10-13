package br.gov.caixa.loterias.core.preferences

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.security.MessageDigest

/** Versioned, length-delimited raw graph encoding; independent of Gson, R8 and locale. */
internal fun legacyCartFingerprint(tables: Map<String, List<LegacyCartRow>>, id: Long): String? {
    val root = tables[LEGACY_CART_TABLE].orEmpty().filter { it["_id"]?.toLong() == id }
    if (root.isEmpty()) return null
    check(root.size == 1) { "Identificação local duplicada" }
    fun related(table: String) = tables[table].orEmpty().filter { it[LEGACY_CART_PARENT]?.toLong() == id }
    val loteca = related("tb_partida_loteca")
    val lotogol = related("tb_partida_lotogol")
    val teamIds = (loteca + lotogol).flatMap { listOf(it["id_equipe_um"]?.toLong(), it["id_equipe_dois"]?.toLong()) }.toSet()
    val teams = tables["tb_equipe"].orEmpty().filter { it["_id"]?.toLong() in teamIds }
    val parameterIds = (teams.map { it["id_parametro_equipe"]?.toLong() } + root.single()["id_time_coracao"]?.toLong()).toSet()
    val graph = linkedMapOf(
        LEGACY_CART_TABLE to root,
        "tb_partida_loteca" to loteca,
        "tb_partida_lotogol" to lotogol,
        "tb_equipe" to teams,
        "tb_parametro_equipe" to tables["tb_parametro_equipe"].orEmpty().filter { it["_id"]?.toLong() in parameterIds },
        "tb_mes_sorte" to tables["tb_mes_sorte"].orEmpty().filter { it["numero"]?.toLong() == root.single()["id_mes_sorte"]?.toLong() },
    )
    val bytes = ByteArrayOutputStream()
    DataOutputStream(bytes).use { output ->
        output.writeInt(1) // Encoding version; never silently change it for pending receipts.
        graph.forEach { (table, rows) ->
            output.field(table)
            output.writeInt(rows.size)
            rows.sortedBy { it[if (table == "tb_mes_sorte") "numero" else "_id"] }.forEach { row ->
                output.writeInt(row.size)
                row.toSortedMap().forEach { (key, value) -> output.field(key); output.field(value) }
            }
        }
    }
    return MessageDigest.getInstance("SHA-256").digest(bytes.toByteArray()).joinToString("") {
        (it.toInt() and 255).toString(16).padStart(2, '0')
    }
}

private fun DataOutputStream.field(value: String?) {
    if (value == null) writeInt(-1) else {
        val bytes = value.toByteArray(Charsets.UTF_8)
        writeInt(bytes.size)
        write(bytes)
    }
}
