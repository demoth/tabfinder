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
    if (line.isBlank() or fistChars(line, "//"))
        return

    if (firstChar(line) == '}')
        info.currentIndent = max(0, info.currentIndent - info.defaultIndent)

    val lastChar = lastChar(line)
    if (info.continuation) {
        if (indent(line) != info.currentIndent + info.defaultContinuation) {
            println("Bad indent continuation [$info], $line")
            info.notFormatted++
        }
        if (lastChar == ';')
            info.continuation = false

    } else {
        if (indent(line) != info.currentIndent) {
            println("Bad indent: line [$info], $line")
            info.notFormatted++
        }
        if (continuation(lastChar, line))
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
fun fistChars(line: String, prefix: String): Boolean {
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