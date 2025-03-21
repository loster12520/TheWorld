package com.lignting.test.functions

import com.lignting.annotations.Command
import com.lignting.annotations.StringParameter

class Test {
    @Command(
        useSpace = "com.lignting.test",
        name = "test",
    )
    fun test() = "Hello World"

    @Command(
        useSpace = "com.lignting.test",
        name = "author",
    )
    fun author(
        @StringParameter(name = "author")
        author: String
    ) = "author: $author"
}