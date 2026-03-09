package com.example.altintakipandroid.ui.markets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.zIndex
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Row slides left when user swipes left, revealing [backgroundContent] on the right.
 * [onRevealActionClick] is called when the user taps the revealed area; call the received [close] to animate the row closed.
 */
@Composable
fun SwipeToRevealRow(
    revealWidth: androidx.compose.ui.unit.Dp = 80.dp,
    onRevealActionClick: ((close: () -> Unit) -> Unit)? = null,
    modifier: Modifier = Modifier,
    backgroundContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val revealWidthPx = with(density) { revealWidth.toPx() }
    val scope = rememberCoroutineScope()
    val animatable = remember { Animatable(0f) }

    Box(
        modifier = modifier
            .clipToBounds()
    ) {
        // Fixed-width strip on the right (revealed when user swipes row left)
        Box(
            modifier = Modifier
                .zIndex(0f)
                .align(Alignment.CenterEnd)
                .width(revealWidth)
                .fillMaxHeight()
                .then(
                    if (onRevealActionClick != null) Modifier.clickable {
                        onRevealActionClick.invoke {
                            scope.launch {
                                animatable.animateTo(
                                    0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            backgroundContent()
        }
        // Row content: swiping left moves this left, revealing the star on the right
        Box(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxSize()
                .offset { IntOffset((-animatable.value).roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val current = animatable.value
                                val threshold = revealWidthPx * 0.4f
                                val target = if (current >= threshold) revealWidthPx else 0f
                                animatable.animateTo(
                                    target,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                // Swipe left → content moves left → star revealed on the right
                                val newOffset = (animatable.value + dragAmount).coerceIn(0f, revealWidthPx)
                                animatable.snapTo(newOffset)
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}
