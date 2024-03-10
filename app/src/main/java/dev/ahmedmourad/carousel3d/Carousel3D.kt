package dev.ahmedmourad.carousel3d

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun Carousel3D(
    count: Int,
    modifier: Modifier = Modifier,
    pivotFractionX: Float = -0.15f,
    contentPadding: PaddingValues = PaddingValues(horizontal = 40.dp),
    itemContent: @Composable (index: Int) -> Unit
) {
    var progressX by remember { mutableFloatStateOf(0f) }
    var progressY by remember { mutableFloatStateOf(0f) }
    CarouselBox(
        pivotFractionX = pivotFractionX,
        modifier = modifier.pointerInput("DragDetector") {
            detectDragGestures { _, dragAmount ->
                progressY += dragAmount.x
                progressX = (progressX - dragAmount.y).coerceIn(-180f, 180f)
            }
        }.padding(contentPadding)
    ) {
        repeat(count) { index ->
            key(index) {
                val rotationY = calculateRotationY(progressY, count, index)
                val rotationX = calculateRotationX(progressX)
                Box(Modifier.wrapContentHeight().graphicsLayer {
                    this.transformOrigin = TransformOrigin(pivotFractionX, 0.5f)
                    this.rotationY = rotationY
                    this.rotationX = rotationX
                }.zIndex(calculateZIndex(rotationX, rotationY))) {
                    itemContent(index)
                }
            }
        }
    }
}

@Composable
private fun CarouselBox(
    pivotFractionX: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val actualConstraints = constraints.copy(
            maxWidth = constraints.maxWidth.div(pivotFractionX.absoluteValue.times(2).plus(2)).roundToInt(),
            minWidth = 0
        )
        val placeables = measurables.map { measurable ->
            measurable.measure(actualConstraints)
        }
        layout(actualConstraints.maxWidth, actualConstraints.maxHeight) {
            placeables.forEach { placeable ->
                placeable.placeRelative(
                    x = actualConstraints.maxWidth.div(2).plus(placeable.width.times(pivotFractionX.absoluteValue).roundToInt()),
                    y = actualConstraints.maxHeight.div(2).minus(placeable.height.div(2))
                )
            }
        }
    }
}
