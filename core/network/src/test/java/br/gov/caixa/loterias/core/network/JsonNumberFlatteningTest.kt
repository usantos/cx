package br.gov.caixa.loterias.core.network

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.core.network.mapper.flattenNumbers

class JsonNumberFlatteningTest {
    @Test fun `nested arrays retain numeric order and duplicates while ignoring other JSON values`() {
        val value = JsonParser.parseString("""[1,[2,null,[3,2],"4",true,{"number":5}],[],6]""")
        assertEquals(listOf(1, 2, 3, 2, 6), value.flattenNumbers())
    }

    @Test fun `absent and JSON null selections contain no numbers`() {
        assertEquals(emptyList<Int>(), (null as JsonElement?).flattenNumbers())
        assertEquals(emptyList<Int>(), JsonNull.INSTANCE.flattenNumbers())
    }

    @Test fun `numeric primitives retain the existing integer conversion`() {
        assertEquals(listOf(0), JsonPrimitive(0).flattenNumbers())
        assertEquals(listOf(-2), JsonPrimitive(-2).flattenNumbers())
        assertEquals(listOf(3), JsonPrimitive(3.9).flattenNumbers())
    }

    @Test fun `numeric strings booleans and objects are not numeric selections`() {
        assertEquals(emptyList<Int>(), JsonPrimitive("12").flattenNumbers())
        assertEquals(emptyList<Int>(), JsonPrimitive(true).flattenNumbers())
        assertEquals(emptyList<Int>(), JsonParser.parseString("""{"number":12}""").flattenNumbers())
        assertEquals(emptyList<Int>(), JsonParser.parseString("[]").flattenNumbers())
    }
}
