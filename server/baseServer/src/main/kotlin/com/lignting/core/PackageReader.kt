package com.lignting.core

import com.lignting.annotations.Command
import java.io.File
import java.lang.reflect.Method
import java.net.URLClassLoader
import java.util.jar.JarFile

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
            it.entries().asSequence()
                .filter { it.name.endsWith(".class") }
                .map { classLoader.loadClass(it.name.replace(".class", "").replace("/", ".")) }
                .filter { it.packageName.startsWith(basePackageName) }
                .map {
                    it.methods.toList()
                }.toList().flatten()
                .filter { it.isAnnotationPresent(Command::class.java) }
                .map {
                    CommandInformation(
                        it.getAnnotation(Command::class.java).useSpace,
                        it.getAnnotation(Command::class.java).name,
                    )
                }.forEach { println(it) }   // test
        }
    }

    data class CommandInformation(
        val useSpace: String,
        val name: String,
    )
}