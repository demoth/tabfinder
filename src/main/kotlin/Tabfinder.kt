package org.demoth

import java.io.File
import java.lang.Integer.max

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: tabfinder <src_folder>")
        return
    }

    File(args[0]).walkTopDown().forEach { file ->
        if (file.isFile && file.name.endsWith(".java"))
            println("$file - ${analyzeFile(file)}")
    }
}

data class FileIndentInfo(
    var notFormatted: Int = 0,
    var currentIndent: Int = 0,
    var lines: Int = 0
)

fun analyzeFile(file: File): FileIndentInfo {
    val info = FileIndentInfo()
    file.forEachLine { line ->
        analyzeLine(info, line)
    }
    return info
}

fun analyzeLine(info: FileIndentInfo, line: String) {
    info.lines++
    if (line.isBlank())
        return

    if (firstChar(line) == '}')
        info.currentIndent = max(0, info.currentIndent - 4)

    if (indent(line) != info.currentIndent) {
        println("Format issue: line [$info], $line")
        info.notFormatted++
    }

    if (lastChar(line) == '{')
        info.currentIndent += 4
}

private fun lastChar(line: String) = line.last { it != ' ' && it != '\t' }

private fun firstChar(line: String) = line.first { it != ' ' && it != '\t' }

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