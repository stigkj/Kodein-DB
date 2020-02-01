package org.kodein.db.impl.data

import org.kodein.db.Value
import org.kodein.db.indexSet
import org.kodein.memory.io.KBuffer
import org.kodein.memory.text.Charset
import org.kodein.memory.text.wrap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("ClassName")
class DataDBTests_11_FindIndexes : DataDBTests() {

    @Test
    fun test00_FindIndexes() {
        val key = ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("aaa"))
        ddb.put(key, Value.ofAscii("ValueA!"), indexSet("Numbers" to Value.ofAscii("forty", "two"), "Symbols" to Value.ofAscii("alpha", "beta")))
        val indexes = ddb.getIndexesOf(key)

        assertEquals(2, indexes.size.toLong())
        assertTrue(indexes.contains("Numbers"))
        assertTrue(indexes.contains("Symbols"))
    }

    @Test
    fun test01_FindNoIndexes() {
        val key = ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("aaa"))
        ddb.put(key, Value.ofAscii("ValueA!"))
        val indexes = ddb.getIndexesOf(key)
        assertTrue(indexes.isEmpty())
    }

    @Test
    fun test02_FindUnknownIndexes() {
        val indexes = ddb.getIndexesOf(ddb.newKey(KBuffer.wrap("Unknown", Charset.ASCII), Value.ofAscii("A")))

        assertTrue(indexes.isEmpty())
    }

}
