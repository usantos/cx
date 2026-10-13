package br.gov.caixa.loterias.core.preferences

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.domain.cart.LegacyCartNumbers
import java.io.File
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/** Fixtures are isolated from the installed app database, including the corruption fixture. */
class LegacyLocalCartStorageTest {
    private lateinit var context: Context
    private lateinit var directory: File

    @Before fun setUp() {
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        directory = File(target.cacheDir, "cart-snapshot-test-${UUID.randomUUID()}")
        check(directory.mkdirs())
        context = object : ContextWrapper(target) {
            override fun getApplicationContext(): Context = this
            override fun getDatabasePath(name: String) = File(directory, name)
        }
    }

    @After fun tearDown() { check(directory.deleteRecursively()) }
    private fun database() = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("Loterias.db"), null)

    @Test fun absentDatabaseIsNotCreatedAndUnrelatedSchemaIsUntouched() = runBlocking {
        assertTrue(LegacyLocalCartReader.fromContext(context).snapshot().isEmpty())
        assertFalse(context.getDatabasePath("Loterias.db").exists())
        database().use {
            it.version = 49
            it.execSQL("CREATE TABLE unrelated (value TEXT)")
            it.execSQL("INSERT INTO unrelated VALUES ('preserve')")
        }
        assertTrue(LegacyLocalCartReader.fromContext(context).snapshot().isEmpty())
        database().use {
            assertEquals(49, it.version)
            it.rawQuery("SELECT value FROM unrelated", null).use { c ->
                assertTrue(c.moveToFirst()); assertEquals("preserve", c.getString(0))
            }
        }
    }

    @Test fun numericAndColumnBetsSurviveReaderRecreationWithoutChanges() = runBlocking {
        database().use {
            it.version = 49
            it.execSQL("CREATE TABLE tb_identificao_de_uma_aposta_das_8_modalidades (_id INTEGER PRIMARY KEY, modalidade TEXT, id_identificador INTEGER, numeros_selecionados TEXT, trevos_selecionados TEXT, valor REAL)")
            it.execSQL("INSERT INTO tb_identificao_de_uma_aposta_das_8_modalidades VALUES (2, 'super sete', 0, '[[0,1],[],[2],[3],[4],[5],[6]]', NULL, 7.50)")
            it.execSQL("INSERT INTO tb_identificao_de_uma_aposta_das_8_modalidades VALUES (1, '+Milionária', 99, '[1,2,3,4,5,6]', '[1,3]', 6.0)")
        }
        val file = context.getDatabasePath("Loterias.db")
        val before = file.readBytes()
        val snapshot = LegacyLocalCartReader.fromContext(context).snapshot()
        assertEquals(listOf(1L, 2L), snapshot.map { it.localId })
        assertEquals(99L, snapshot[0].serverId); assertNull(snapshot[1].serverId)
        assertEquals(LegacyCartNumbers.Flat(listOf(1,2,3,4,5,6)), snapshot[0].numbers)
        assertEquals(listOf(1,3), snapshot[0].clovers)
        assertEquals(LegacyCartNumbers.Columns(listOf(listOf(0,1), emptyList(), listOf(2), listOf(3), listOf(4), listOf(5), listOf(6))), snapshot[1].numbers)
        assertEquals(0, BigDecimal("7.50").compareTo(snapshot[1].amount))
        assertEquals(snapshot, LegacyLocalCartReader.fromContext(context).snapshot())
        assertArrayEquals(before, file.readBytes())
    }

    @Test fun sportsRelationsAndMonthAreLoadedWithoutErasingOtherTables() = runBlocking {
        database().use {
            it.execSQL("CREATE TABLE tb_identificao_de_uma_aposta_das_8_modalidades (_id INTEGER PRIMARY KEY, modalidade TEXT, id_time_coracao INTEGER, id_mes_sorte INTEGER)")
            it.execSQL("INSERT INTO tb_identificao_de_uma_aposta_das_8_modalidades VALUES (1, 'loteca', 9, 12)")
            it.execSQL("CREATE TABLE tb_parametro_equipe (_id INTEGER, numero INTEGER, nome TEXT, indicadorSelecao INTEGER)")
            it.execSQL("INSERT INTO tb_parametro_equipe VALUES (9, 80, 'Time', 1)")
            it.execSQL("CREATE TABLE tb_equipe (_id INTEGER, numero INTEGER, nome TEXT, indicador_selecao INTEGER, id_parametro_equipe INTEGER, placar TEXT, vitoria INTEGER)")
            it.execSQL("INSERT INTO tb_equipe VALUES (20, 80, 'Time', 1, 9, '2', 1), (21, 81, 'Outro', 0, 0, '0', 0)")
            it.execSQL("CREATE TABLE tb_partida_loteca (_id INTEGER, id_identificao_de_uma_aposta_das_8_modalidades INTEGER, id_equipe_um INTEGER, id_equipe_dois INTEGER, empate INTEGER)")
            it.execSQL("INSERT INTO tb_partida_loteca VALUES (1, 1, 20, 21, 1), (2, 999, 20, 21, 0)")
            it.execSQL("CREATE TABLE tb_partida_lotogol (_id INTEGER, id_identificao_de_uma_aposta_das_8_modalidades INTEGER, id_equipe_um INTEGER, id_equipe_dois INTEGER, index_placar_equipe_um INTEGER, index_placar_equipe_dois INTEGER)")
            it.execSQL("INSERT INTO tb_partida_lotogol VALUES (1, 1, 20, 21, 2, 0)")
            it.execSQL("CREATE TABLE tb_mes_sorte (numero INTEGER PRIMARY KEY, nome TEXT, abreviacao TEXT)")
            it.execSQL("INSERT INTO tb_mes_sorte VALUES (12, 'Dezembro', 'Dez')")
        }
        val snapshot = LegacyLocalCartReader.fromContext(context).snapshot().single()
        assertEquals(80, snapshot.favoriteTeam?.number)
        assertEquals("Dez", snapshot.luckyMonth?.abbreviation)
        assertTrue(snapshot.loteca.single().draw)
        assertTrue(snapshot.loteca.single().first.team.isNationalTeam)
        assertTrue(snapshot.loteca.single().first.parameter!!.isNationalTeam)
        assertTrue(snapshot.loteca.single().first.winner)
        assertEquals(2, snapshot.lotogol.single().firstScoreIndex)
        assertEquals("0", snapshot.lotogol.single().second.score)
        assertEquals(snapshot, LegacyLocalCartReader.fromContext(context).snapshot().single())
    }

    @Test fun corruptDatabaseIsNotDeletedOrReportedAsEmpty() = runBlocking {
        val file = context.getDatabasePath("Loterias.db")
        val invalid = ByteArray(4096) { 65 }
        file.writeBytes(invalid)
        assertTrue(runCatching { LegacyLocalCartReader.fromContext(context).snapshot() }.isFailure)
        assertTrue(file.exists())
        assertArrayEquals(invalid, file.readBytes())
    }
}
