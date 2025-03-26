package com.lignting.core.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface ParameterData<T> {
    val name: String
    fun getValue(text: String): T
}

class StringParameterData(override val name: String) : ParameterData<String> {
    override fun getValue(text: String): String = text
}

class JsonParameterData<T>(override val name: String) : ParameterData<T> {
    private val gson = Gson()
    private val typeToken = object : TypeToken<T>() {}
    override fun getValue(text: String): T =
        gson.fromJson(text, typeToken)
}

//class ServerParameterData(override val name: String) : ParameterData {
//
//}