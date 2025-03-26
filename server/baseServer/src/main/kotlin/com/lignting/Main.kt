package com.lignting

import com.lignting.core.data.CoreManager

fun main() {
    val pathList = listOf("C:\\code\\project\\kotlin\\TheWorld\\plugins\\test\\build\\libs\\test-1.0-SNAPSHOT.jar")
    val core = CoreManager(pathList)
    println(core.methodList())
    println(core.run("com.lignting.test:author", "lignting"))
}