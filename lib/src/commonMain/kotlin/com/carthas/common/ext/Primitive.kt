package com.carthas.common.ext


fun Any?.isNull() = this == null
fun Any?.isNotNull() = this != null

infix fun <T> T?.orElse(that: T): T = this ?: that
inline infix fun <T> T?.orElse(that: () -> T) = this ?: that()