# TheWorld 插件开发指南

## 目录
- [简介](#简介)
- [插件开发环境设置](#插件开发环境设置)
- [创建插件项目](#创建插件项目)
- [编写插件](#编写插件)
- [构建插件](#构建插件)
- [使用插件](#使用插件)
- [示例](#示例)

## 简介

TheWorld 是一个基于 Kotlin 开发的插件化系统，允许通过 JAR 包动态扩展功能。本指南将帮助您了解如何开发和使用 TheWorld 插件。

## 插件开发环境设置

1. 确保您已安装以下工具：
   - JDK 8 或更高版本
   - Kotlin 编译器
   - Gradle 构建工具

2. 在您的项目中添加必要的依赖：
   ```kotlin
   dependencies {
       implementation("com.lignting:baseServer:1.0-SNAPSHOT")
       implementation("com.lignting:annotations:1.0-SNAPSHOT")
   }
   ```

## 创建插件项目

1. 创建一个新的 Kotlin 项目
2. 在 `build.gradle.kts` 中添加以下配置：
   ```kotlin
   plugins {
       kotlin("jvm") version "1.8.0"
   }

   group = "com.your.plugin"
   version = "1.0-SNAPSHOT"

   dependencies {
       implementation("com.lignting:baseServer:1.0-SNAPSHOT")
       implementation("com.lignting:annotations:1.0-SNAPSHOT")
   }

   tasks.jar {
       manifest {
           attributes(
               "Plugins-Base-Class" to "com.your.plugin.YourBaseClass"
           )
       }
   }
   ```

## 编写插件

### 1. 创建基础类

首先，创建一个基础类作为插件的入口点：

```kotlin
package com.your.plugin

class YourBaseClass {
    // 这里可以添加插件的初始化逻辑
}
```

### 2. 创建命令

在包级别直接定义命令函数：

```kotlin
package com.your.plugin

import com.lignting.annotations.Command
import com.lignting.annotations.StringParameter
import com.lignting.annotations.JsonParameter

@Command(useSpace = "com.your.plugin", name = "hello")
fun hello(@StringParameter(name = "name") name: String): String {
    return "Hello, $name!"
}

@Command(useSpace = "com.your.plugin", name = "process")
fun process(@JsonParameter(name = "data") data: Map<String, Any>): String {
    return "Processed: $data"
}
```

### 3. 注解说明

#### @Command 注解
用于标记一个函数作为命令：
- `useSpace`: 命令的命名空间
- `name`: 命令的名称

#### @StringParameter 注解
用于标记字符串参数：
- `name`: 参数的名称

#### @JsonParameter 注解
用于标记 JSON 参数：
- `name`: 参数的名称

## 构建插件

1. 使用 Gradle 构建插件：
   ```bash
   ./gradlew build
   ```

2. 构建完成后，插件 JAR 文件将位于：
   ```
   build/libs/your-plugin-1.0-SNAPSHOT.jar
   ```

## 使用插件

1. 将构建好的 JAR 文件复制到服务器的插件目录：
   ```
   plugins/your-plugin-1.0-SNAPSHOT.jar
   ```

2. 启动服务器，插件将被自动加载

3. 使用命令行客户端执行命令：
   ```bash
   # 执行 hello 命令
   your-client com.your.plugin hello "World"

   # 执行 process 命令
   your-client com.your.plugin process '{"key": "value"}'
   ```

## 示例

### 完整插件示例

```kotlin
package com.example.plugin

import com.lignting.annotations.Command
import com.lignting.annotations.StringParameter
import com.lignting.annotations.JsonParameter

// 基础类
class ExampleBaseClass {
    init {
        println("Example plugin initialized!")
    }
}

// 命令定义
@Command(useSpace = "com.example.plugin", name = "greet")
fun greet(@StringParameter(name = "name") name: String): String {
    return "Hello, $name! Welcome to TheWorld!"
}

@Command(useSpace = "com.example.plugin", name = "calculate")
fun calculate(@JsonParameter(name = "numbers") numbers: List<Int>): Int {
    return numbers.sum()
}
```

### 使用示例

```bash
# 执行 greet 命令
your-client com.example.plugin greet "Alice"

# 执行 calculate 命令
your-client com.example.plugin calculate '[1, 2, 3, 4, 5]'
```

## 注意事项

1. 确保插件的基础类名称与 `build.gradle.kts` 中配置的 `Plugins-Base-Class` 匹配
2. 命令的命名空间（useSpace）应该是唯一的，建议使用插件的包名
3. 参数注解（@StringParameter 和 @JsonParameter）的 name 属性是必需的
4. 命令函数的参数数量必须与调用时提供的参数数量匹配
5. 确保所有依赖都正确配置在 `build.gradle.kts` 中
6. 推荐在包级别直接定义命令函数，而不是在类中定义命令方法

## 调试提示

1. 在开发过程中，可以使用 `println` 或日志系统来输出调试信息
2. 如果插件加载失败，检查：
   - JAR 文件的 MANIFEST.MF 是否正确配置
   - 基础类是否存在且可访问
   - 所有依赖是否正确配置
3. 如果命令执行失败，检查：
   - 命令注解配置是否正确
   - 参数类型是否匹配
   - 参数数量是否正确 