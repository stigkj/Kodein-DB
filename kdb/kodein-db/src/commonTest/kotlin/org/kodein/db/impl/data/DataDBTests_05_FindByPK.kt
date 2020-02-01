package org.kodein.db.impl.data

import org.kodein.db.Value
import org.kodein.db.indexSet
import org.kodein.db.test.utils.assertBytesEquals
import org.kodein.db.test.utils.byteArray
import org.kodein.memory.io.KBuffer
import org.kodein.memory.text.Charset
import org.kodein.memory.text.wrap
import org.kodein.memory.use
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("ClassName")
class DataDBTests_05_FindByPK : DataDBTests() {

    @Test
    fun test00_FindByPKCompositeKey() {
        ddb.put(ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("aaa", "a")), Value.ofAscii("ValueAa1!"), indexSet("Symbols" to Value.ofAscii("alpha", "beta")))
        ddb.put(ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("aaa", "b")), Value.ofAscii("ValueAb1!"), indexSet("Symbols" to Value.ofAscii("alpha", "beta")))
        ddb.put(ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("bbb")), Value.ofAscii("ValueB1!"), indexSet("Numbers" to Value.ofAscii("forty", "two")))

        ddb.findById(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("aaa")).use {
            assertTrue(it.isValid())
            assertCursorIs(byteArray('o', 0, "Test", 0, "aaa", 0, 'a', 0), byteArray("ValueAa1!"), it)
            assertBytesEquals(it.transientKey(), it.transientSeekKey())
            it.next()
            assertTrue(it.isValid())
            assertCursorIs(byteArray('o', 0, "Test", 0, "aaa", 0, 'b', 0), byteArray("ValueAb1!"), it)
            assertBytesEquals(it.transientKey(), it.transientSeekKey())
            it.next()
            assertFalse(it.isValid())
        }
    }

    @Test
    fun test01_FindByPKReverseCompositeKey() {
        ddb.put(ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("aaa", "a")), Value.ofAscii("ValueAa1!"), indexSet("Symbols" to Value.ofAscii("alpha", "beta")))
        ddb.put(ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("aaa", "b")), Value.ofAscii("ValueAb1!"), indexSet("Symbols" to Value.ofAscii("gamma", "delta")))
        ddb.put(ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("bbb")), Value.ofAscii("ValueB1!"), indexSet("Numbers" to Value.ofAscii("forty", "two")))

        ddb.findById(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("aaa")).use {
            assertTrue(it.isValid())
            it.seekToLast()
            assertTrue(it.isValid())
            assertCursorIs(byteArray('o', 0, "Test", 0, "aaa", 0, 'b', 0), byteArray("ValueAb1!"), it)
            assertBytesEquals(it.transientKey(), it.transientSeekKey())
            it.prev()
            assertTrue(it.isValid())
            assertCursorIs(byteArray('o', 0, "Test", 0, "aaa", 0, 'a', 0), byteArray("ValueAa1!"), it)
            assertBytesEquals(it.transientKey(), it.transientSeekKey())
            it.prev()
            assertFalse(it.isValid())
        }
    }

    @Test
    fun test02_FindByPKUnknownKey() {
        ddb.put( ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("aaa")), Value.ofAscii("ValueA1!"), indexSet("Symbols" to Value.ofAscii("alpha", "beta")))
        ddb.put( ddb.newKey(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("bbb")), Value.ofAscii("ValueB1!"), indexSet("Numbers" to Value.ofAscii("forty", "two")))

        ddb.findById(KBuffer.wrap("Test", Charset.ASCII), Value.ofAscii("ccc")).use {
            assertFalse(it.isValid())
        }
    }
}
