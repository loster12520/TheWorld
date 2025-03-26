package com.lignting.core.data

data class CommandData(
    val commandInformation: CommandInformation,
    val method: (List<String>) -> Any?
)

data class CommandInformation(
    val useSpace: String,
    val name: String,
    val parameters: List<ParameterData<*>>,
) {
    fun getSymbol() = "$useSpace:$name"
    override fun toString() = "${getSymbol()} ${parameters.joinToString { it.javaClass.simpleName }}"
}