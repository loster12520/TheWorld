package com.lignting.test.functions

import com.lignting.annotations.Command

@Command(
    useSpace = "com.lignting.test",
    name = "test",
)
fun test() = "Hello World"