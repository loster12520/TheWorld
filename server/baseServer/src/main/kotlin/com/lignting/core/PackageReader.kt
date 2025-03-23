package com.lignting.core

import java.lang.reflect.Modifier
import com.lignting.annotations.Command
import com.lignting.annotations.JsonParameter
import com.lignting.annotations.Server
import com.lignting.annotations.StringParameter
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

class PackageReader(private val file: File) {

    constructor(path: String) : this(File(path))

    private val classLoader = URLClassLoader(arrayOf(file.toURI().toURL()), Thread.currentThread().contextClassLoader)
    val commandList = mutableListOf<Pair<CommandInformation, (List<String>) -> Any?>>()


    fun init(): PackageReader {
        classLoader.use {
            JarFile(file).use { jarFile ->
                val baseClassName =
                    jarFile.manifest.mainAttributes
                        .filter { it.key.toString() == "Plugins-Base-Class" }.takeIf {
                            it.count() == 1
                        }?.map { it.value }?.first() ?: throw RuntimeException("Base classes formatting error")
                val baseClass = classLoader.loadClass(baseClassName.toString())
                val basePackageName = baseClass.`package`.name

                jarFile.entries().asSequence()
                    .filter { it.name.endsWith(".class") }
                    .map { classLoader.loadClass(it.name.replace(".class", "").replace("/", ".")).kotlin }
                    .filter { it.qualifiedName?.startsWith("$basePackageName.") == true }
                    .flatMap { it.java.declaredMethods.asSequence() }
                    .map { it.kotlinFunction }
                    .filterNotNull()
                    .filter { it.hasAnnotation<Command>() }
                    .map { kFunction ->
                        val commandInformation = CommandInformation(
                            kFunction.findAnnotation<Command>()!!.useSpace,
                            kFunction.findAnnotation<Command>()!!.name,
                            kFunction.parameters.mapNotNull {
                                if (it.hasAnnotation<StringParameter>())
                                    StringParameterData(
                                        it.findAnnotation<StringParameter>()?.name
                                            ?: throw RuntimeException("Parameter ${it.name} is required")
                                    )
                                else if (it.hasAnnotation<JsonParameter>())
                                    JsonParameterData(
                                        it.findAnnotation<StringParameter>()?.name
                                            ?: throw RuntimeException("Parameter ${it.name} is required")
                                    )
//                            else if (it.hasAnnotation<Server>())
//                                ServerParameterData(
//                                    it.findAnnotation<StringParameter>()?.name
//                                        ?: throw RuntimeException("Parameter ${it.name} is required")
//                                )
                                else
                                    null
                            }
                        )
                        commandInformation to { input: List<String> ->
                            if (commandInformation.parameters.size != input.size) throw RuntimeException("not allowed size")
                            val parameters = commandInformation.parameters.zip(input)
                                .map { it.first.getValue(it.second) }.toTypedArray()
                            kFunction.javaMethod?.let {
                                if (Modifier.isStatic(it.modifiers))
                                    it.invoke(null, *parameters)
                                else
                                    it.invoke(it.declaringClass.getDeclaredConstructor().newInstance(), *parameters)
                            } ?: throw RuntimeException("Can't invoke ${kFunction.name}")
                        }
                    }.also { commandList += it }
            }
        }
        return this
    }

    data class CommandInformation(
        val useSpace: String,
        val name: String,
        val parameters: List<ParameterData<*>>,
    ) {
        override fun toString(): String {
            return "$useSpace: $name <${parameters.joinToString { "${it.name}: ${it::class.simpleName}" }}>"
        }
    }
}