package com.lignting

import com.lignting.core.PackageReader

fun main() {
    val path = "C:\\code\\project\\kotlin\\TheWorld\\plugins\\test\\build\\libs\\test-1.0-SNAPSHOT.jar"
    val commandList = PackageReader(path).init().commandList
    commandList.forEach {
        println(it)
    }
    commandList.get("com.lignting.test", "author").second.invoke(listOf("lignting")).also { println(it) }
}

typealias CommandList = List<Pair<PackageReader.CommandInformation, (List<String>) -> Any?>>

typealias Command = Pair<PackageReader.CommandInformation, (List<String>) -> Any?>

fun CommandList.get(usePlace: String, name: String): Command =
    filter { it.first.useSpace == usePlace && it.first.name == name }.firstOrNull()
        ?: throw RuntimeException("unknown command: $name")
