package com.carthas.common.ext


inline fun <T> T.letIf(condition: Boolean, lambda: (T) -> T): T =
    if (condition) lambda(this)
    else this

inline fun <T> T.runIf(condition: Boolean, lambda: T.() -> T): T =
    if (condition) lambda()
    else this