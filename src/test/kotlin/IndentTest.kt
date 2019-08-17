package org.demoth

import org.junit.Assert.assertEquals
import org.junit.Test

class IndentTest {
    @Test
    fun testIndent() {
        assertEquals(0, indent("1"))
    }

    @Test
    fun testIndent1() {
        assertEquals(1, indent(" 1"))
    }

    @Test
    fun testIndent4() {
        assertEquals(4, indent("    public class Tabfinder {"))
    }

    @Test
    fun testIndentTab() {
        assertEquals(1, indent("\tpublic class Tabfinder {"))
    }

    @Test
    fun testCommentInFirstChars() {
        assertEquals(true, firstChars("\t\t //public class Tabfinder {", "//"))
    }

    @Test
    fun testEmptyLineFirstChars() {
        assertEquals(false, firstChars("\t\t ", "//"))
    }

    @Test
    fun testCommentInFirstCharsNegative() {
        assertEquals(false, firstChars("\t\t p//public class Tabfinder {", "//"))
    }

}