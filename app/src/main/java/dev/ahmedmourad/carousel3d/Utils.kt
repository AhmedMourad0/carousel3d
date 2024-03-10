package dev.ahmedmourad.carousel3d

import androidx.compose.runtime.Stable
import kotlin.math.absoluteValue

@Stable
fun calculateRotationY(progressY: Float, count: Int, index: Int): Float {
    return progressY.plus((360 / count) * index).mod(360f)
}

@Stable
fun calculateRotationX(progressX: Float): Float {
    return progressX.mod(360f)
}

@Stable
fun calculateZIndex(rotationX: Float, rotationY: Float): Float {
    val yBasedZIndex = if (rotationY in 0f..90f) {
        270f.minus(rotationY).absoluteValue.minus(360f)
    } else {
        270f.minus(rotationY).absoluteValue.unaryMinus()
    }
    return if (rotationX in 90f..270f) {
        yBasedZIndex.unaryMinus()
    } else {
        yBasedZIndex
    }
}
