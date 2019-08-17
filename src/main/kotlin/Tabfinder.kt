package org.demoth

import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Integer.max

val logger = LoggerFactory.getLogger("Tabfinder")

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: tabfinder <src_folder>")
        return
    }
    var stats = Statistics()
    File(args[0]).walkTopDown().forEach { file ->
        if (file.isFile && file.name.endsWith(".java")) {
            println("$file - ${analyzeFile(file, stats)}")
            stats.files++
            logger.debug("Analysed {} files. Found {} issues", stats.files, stats.notFormatted)
        }
    }
}

data class Statistics(
    var files: Int = 0,
    var lines: Int = 0,
    var notFormatted: Int = 0
)

data class FileIndentInfo(
    var notFormatted: Int = 0,
    var currentIndent: Int = 0,
    var lines: Int = 0,
    var continuation: Boolean = false,
    var multiLineComment: Boolean = false,
    val defaultIndent: Int = 4,
    val defaultContinuation: Int = 8
)

fun analyzeFile(file: File, stats: Statistics): FileIndentInfo {
    val info = FileIndentInfo()
    file.forEachLine { line ->
        analyzeLine(info, line)
    }
    stats.lines += info.lines
    stats.notFormatted += info.notFormatted
    return info
}

fun analyzeLine(info: FileIndentInfo, line: String) {
    info.lines++
    if (line.isBlank() or firstChars(line, "//"))
        return

    // we are inside of the multiline comment: check if it has ended
    if (info.multiLineComment) {
        if (line.contains("*/")) {
            info.multiLineComment = false
            // if it contains some code, this line is not properly formatted
            if (!line.endsWith("*/")) {
                info.notFormatted++
            }
        }
        return
    }

    // check if multiline comment has started
    if (line.contains("/*")) {
        info.multiLineComment = true
        // if it contains some code before the comment, this line is not properly formatted
        if (!firstChars(line, "/*")) {
            info.notFormatted++
        }
        return
    }

    val codeLine: String = line.split("//")[0]

    if (firstChar(codeLine) == '}')
        info.currentIndent = max(0, info.currentIndent - info.defaultIndent)

    val lastChar = lastChar(codeLine)
    if (info.continuation) {
        if (indent(codeLine) != info.currentIndent + info.defaultContinuation) {
            println("Bad indent continuation [$info], $codeLine")
            info.notFormatted++
        }
        if (lastChar == ';')
            info.continuation = false

    } else {
        if (indent(codeLine) != info.currentIndent) {
            println("Bad indent: line [$info], $codeLine")
            info.notFormatted++
        }
        if (continuation(lastChar, codeLine))
            info.continuation = true
    }

    if (lastChar == '{')
        info.currentIndent += info.defaultIndent
}

private fun continuation(lastChar: Char, line: String) =
    lastChar != ';' && lastChar != '{' && lastChar != '}' && firstChar(line) != '@'

private fun lastChar(line: String) = line.last { it != ' ' && it != '\t' }

private fun firstChar(line: String) = line.first { it != ' ' && it != '\t' }

// prefix does not contain spaces
fun firstChars(line: String, prefix: String): Boolean {
    if (prefix.isEmpty())
        return true
    if (line.isEmpty())
        return false
    return line.trim().startsWith(prefix)
}

fun indent(str: String): Int {
    var i = 0
    str.forEach {
        if (it == ' ' || it == '\t')
            i++
        else
            return i
    }
    return i
}