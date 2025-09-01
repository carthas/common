package com.carthas.common.ext

import com.carthas.common.mvi.CarthasViewModel
import org.koin.core.definition.Definition
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.ScopeDSL
import org.koin.dsl.bind


inline fun <reified VM : CarthasViewModel<*, *, *>> ScopeDSL.scopedViewModel(
    noinline definition: Definition<VM>,
) = scoped(definition = definition) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>> ScopeDSL.scopedViewModelOf(
    crossinline constructor: () -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified P21> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class

inline fun <reified VM : CarthasViewModel<*, *, *>, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified P21, reified P22> ScopeDSL.scopedViewModelOf(
    crossinline constructor: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> VM,
) = scopedOf(constructor) bind CarthasViewModel::class