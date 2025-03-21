package com.lignting.core

interface ParameterData {
    val name: String
}

class StringParameterData(override val name: String) : ParameterData {

}

class JsonParameterData(override val name: String) : ParameterData {

}

class ServerParameterData(override val name: String) : ParameterData {

}