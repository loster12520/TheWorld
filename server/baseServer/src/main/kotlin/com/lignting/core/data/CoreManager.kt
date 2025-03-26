package com.lignting.core.data

import com.lignting.core.PackageReader

class CoreManager(jarPathList: List<String>) {
    constructor(vararg jarPathList: String) : this(jarPathList.toList())

    private val packageReaderList = jarPathList.map {
        PackageReader(it).also { it.init() }
    }

    private val informations =
        packageReaderList.map { it.commandList }.flatten().map { it.commandInformation.toString() }

    private val map: Map<String, (List<String>) -> Any?> =
        packageReaderList.map { it.commandList }.flatten()
            .associate { it.commandInformation.getSymbol() to it.method }

    fun run(symbol: String, parameterList: List<String>): Any? {
        map[symbol]?.let {
            return it.invoke(parameterList)
        } ?: throw RuntimeException("Method not found")
    }

    fun run(symbol: String, vararg parameterList: String) = run(symbol, parameterList.toList())

    fun run(useSpace: String, name: String, parameterList: List<String>) =
        run("$useSpace:$name", parameterList.toList())

    fun methodList() = informations
}