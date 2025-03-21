package com.lignting.annotations

@Target(AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Parameters

@Parameters
annotation class StringParameter(val name: String = "")

@Parameters
annotation class JsonParameter(val name: String = "")

@Parameters
annotation class Server(val name: String = "")