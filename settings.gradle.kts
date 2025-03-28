rootProject.name = "TheWorld"

include(":client:commandLine")
include(":server:baseServer")

include(":plugins:test")
include(":connectors:simpleCommandLineConnector")

// 配置插件管理
pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
    }
}

// 配置依赖管理
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        mavenCentral()
    }
}