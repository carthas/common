package com.carthas.common.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp


@Composable
fun VerticalSpace(height: Dp) = Spacer(Modifier.height(height))

@Composable
fun HorizontalSpace(width: Dp) = Spacer(Modifier.width(width))