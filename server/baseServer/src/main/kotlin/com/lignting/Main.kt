package com.lignting

import com.lignting.core.PackageReader

fun main() {
    val path = "C:\\code\\project\\kotlin\\TheWorld\\plugins\\test\\build\\libs\\test-1.0-SNAPSHOT.jar"
    PackageReader(path).start()
}