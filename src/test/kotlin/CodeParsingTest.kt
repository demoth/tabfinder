package org.demoth

import org.junit.Assert
import org.junit.Test

class CodeParsingTest {

    @Test
    fun `test empty`() {
        val code = ""
        Assert.assertEquals(FileIndentInfo(lines = 1), testAnalyzeCode(code))
    }

    @Test
    fun `test one line`() {
        val code = "hello world"
        Assert.assertEquals(FileIndentInfo(lines = 1), testAnalyzeCode(code))
    }

    @Test
    fun `test one line not formatted`() {
        val code = "  hello world"
        Assert.assertEquals(FileIndentInfo(lines = 1, notFormatted = 1), testAnalyzeCode(code))
    }

    @Test
    fun `test simple indents`() {
        val code = """
            function {
                do something
            }
        """.trimIndent()
        Assert.assertEquals(FileIndentInfo(0, 0, 3), testAnalyzeCode(code))
    }

    @Test
    fun `test nested indents`() {
        val code = """
            function {
                if it works {
                    do it
                }
            }
        """.trimIndent()
        Assert.assertEquals(FileIndentInfo(0, 0, 5), testAnalyzeCode(code))
    }

    @Test
    fun `test if else`() {
        val code = """
            if one thing {
                hello
            } else {
                world                
            }
        """.trimIndent()
        Assert.assertEquals(FileIndentInfo(0, 0, 5), testAnalyzeCode(code))
    }

    @Test
    fun `test one block not formatted`() {
        val code = """
            if whatever {
            fail
             another fail   
            }
        """.trimIndent()
        Assert.assertEquals(FileIndentInfo(2, 0, 4), testAnalyzeCode(code))
    }

    private fun testAnalyzeCode(code: String): FileIndentInfo {
        val actualInfo = FileIndentInfo()
        code.split("\n").forEach { analyzeLine(actualInfo, it) }
        return actualInfo
    }
}
