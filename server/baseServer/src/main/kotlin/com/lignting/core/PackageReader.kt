package com.lignting.core

import com.lignting.annotations.Command
import com.lignting.annotations.JsonParameter
import com.lignting.annotations.Server
import com.lignting.annotations.StringParameter
import java.io.File
import java.lang.reflect.Method
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.kotlinFunction

class PackageReader(private val file: File) {

    constructor(path: String) : this(File(path))

    fun start() {
        val jar = JarFile(file)
        val classLoader = URLClassLoader(arrayOf(file.toURI().toURL()), Thread.currentThread().contextClassLoader)
        jar.use {
            val baseClassName =
                it.manifest.mainAttributes
                    .filter { it.key.toString() == "Plugins-Base-Class" }.takeIf {
                        it.count() == 1
                    }?.map { it.value }?.first() ?: throw RuntimeException("Base classes formatting error")
            val baseClass = classLoader.loadClass(baseClassName.toString())
            val basePackageName = baseClass.`package`.name

            jar.entries().asSequence()
                .filter { it.name.endsWith(".class") }
                .map { classLoader.loadClass(it.name.replace(".class", "").replace("/", ".")).kotlin }
                .filter { it.qualifiedName?.startsWith("$basePackageName.") == true }
                .flatMap { it.java.declaredMethods.asSequence() }
                .map { it.kotlinFunction }
                .filter { it != null }.map { it!! }
                .filter { it.hasAnnotation<Command>() }
                .map {
                    CommandInformation(
                        it.findAnnotation<Command>()!!.useSpace,
                        it.findAnnotation<Command>()!!.name,
                        it.parameters.map {
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
                            else if (it.hasAnnotation<Server>())
                                ServerParameterData(
                                    it.findAnnotation<StringParameter>()?.name
                                        ?: throw RuntimeException("Parameter ${it.name} is required")
                                )
                            else
                                null
                        }.filter { it != null }.map { it!! }
                    )
                }
                .forEach {
                    println(it)
                }
        }
    }

    data class CommandInformation(
        val useSpace: String,
        val name: String,
        val parameters: List<ParameterData>
    ) {
        override fun toString(): String {
            return "$useSpace: $name <${parameters.map { "${it.name}: ${it::class.simpleName}" }.joinToString()}>"
        }
    }
}