package org.demoth

import org.junit.Assert.assertEquals
import org.junit.Test

class CodeParsingTest {

    @Test
    fun `test empty`() {
        val code = ""
        assertEquals(FileIndentInfo(lines = 1), testAnalyzeCode(code))
    }

    @Test
    fun `test one line`() {
        val code = "hello world;"
        assertEquals(FileIndentInfo(lines = 1), testAnalyzeCode(code))
    }

    @Test
    fun `test one line not formatted`() {
        val code = "  hello world;"
        assertEquals(FileIndentInfo(lines = 1, notFormatted = 1), testAnalyzeCode(code))
    }

    @Test
    fun `test simple indents`() {
        val code = """
            function {
                do something;
            }
        """.trimIndent()
        assertEquals(FileIndentInfo(0, 0, 3), testAnalyzeCode(code))
    }

    @Test
    fun `test nested indents`() {
        val code = """
            function {
                if it works {
                    do it;
                }
            }
        """.trimIndent()
        assertEquals(FileIndentInfo(0, 0, 5), testAnalyzeCode(code))
    }

    @Test
    fun `test annotation indents`() {
        val code = """
            @Test
            function {
                do the needful;
            }
        """.trimIndent()
        assertEquals(FileIndentInfo(0, 0, 4), testAnalyzeCode(code))
    }

    @Test
    fun `test comments indents`() {
        val code = """
            function {
                // my comment
                do the needful;
            }
        """.trimIndent()
        assertEquals(FileIndentInfo(0, 0, 4), testAnalyzeCode(code))
    }

    @Test
    fun `test if else`() {
        val code = """
            if one thing {
                hello;
            } else {
                world;
            }
        """.trimIndent()
        assertEquals(FileIndentInfo(0, 0, 5), testAnalyzeCode(code))
    }

    @Test
    fun `test one block not formatted`() {
        val code = """
            if whatever {
            fail;
             another fail;
            }
        """.trimIndent()
        assertEquals(FileIndentInfo(2, 0, 4), testAnalyzeCode(code))
    }

    @Test
    fun `test one line not formatted but next is fine`() {
        val code = """
            if whatever {
            fail;
                another fail;
            }
        """.trimIndent()
        assertEquals(FileIndentInfo(1, 0, 4), testAnalyzeCode(code))
    }

    @Test
    fun `test if without curly braces`() {
        val code = """
            if whatever 
                this line is fine;
        """.trimIndent()
        assertEquals(FileIndentInfo(1, 0, 2), testAnalyzeCode(code))
    }

    @Test
    fun `test one-line if no curly braces`() {
        val code = """
            if whatever this line is fine;
        """.trimIndent()
        assertEquals(FileIndentInfo(0, 0, 1), testAnalyzeCode(code))
    }

    @Test
    fun `test indent continuation`() {
        val code = """
            Integer variable = 1004903 +
                    39203948;
        """.trimIndent()
        assertEquals(FileIndentInfo(0, 0, 2), testAnalyzeCode(code))
    }

    private fun testAnalyzeCode(code: String): FileIndentInfo {
        val actualInfo = FileIndentInfo()
        code.split("\n").forEach { analyzeLine(actualInfo, it) }
        return actualInfo
    }
}
