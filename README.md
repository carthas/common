# Carthas Common

[![Maven Central](https://img.shields.io/maven-central/v/com.carthas/common)](https://search.maven.org/artifact/com.carthas/common)
[![License: Apache 2.0](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](https://opensource.org/licenses/MIT)

**Carthas Common** is a collection of utilities designed to streamline development with [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/). It offers shared tools and abstractions to enhance code reuse and maintainability across Android, iOS, desktop, and web platforms.

## ✨ Features

- Cross-platform utilities for Compose Multiplatform projects  
- Modifier.shader implementations for custom SkSL shader application
- Lightweight MVI base classes and fully object-oriented navigation
- Several idiomatic DSL extensions for common CMP use cases
- Supports Android, Desktop, iOS, and Web (Wasm) targets

## 📦 Installation
### Requirements
- In order for your `Screen` implementations to be able to use the `Content` DSL function, you must have [Koin](https://github.com/InsertKoinIO/koin) set up in your application, with [`scoped` definitions](https://insert-koin.io/docs/reference/koin-core/scopes/) for all your `CarthasViewModel` implementations.

Add the following to your `libs.versions.toml` file:

```toml
[versions]
carthas-common = "0.6.0"

[libraries]
carthas-common = { module = "com.carthas:common", version.ref = "carthas-common" }
```

Then, include it in your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.carthas.common)
}
```
