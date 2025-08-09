package com.carthas.common

import android.os.Build


fun isMinSDK(sdk: Int): Boolean = Build.VERSION.SDK_INT >= sdk