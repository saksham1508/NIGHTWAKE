package com.example.nightwake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

/**
 * On-Screen Virtual Floating Joystick for responsive one-handed portrait gameplay.
 */
@Composable
fun VirtualJoystick(
    size: Dp = 150.dp,
    onMove: (deltaX: Float, deltaY: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(size)
            .pointerInput(Unit) {
                val radiusPx = size.toPx() / 2f
                val maxDrag = radiusPx * 0.75f

                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(radiusPx, radiusPx)
                        val diff = offset - center
                        val dist = diff.getDistance()
                        val clamped = if (dist > maxDrag) {
                            diff * (maxDrag / dist)
                        } else {
                            diff
                        }
                        knobOffset = clamped
                        onMove(clamped.x / maxDrag, clamped.y / maxDrag)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = knobOffset + dragAmount
                        val dist = newOffset.getDistance()
                        val clamped = if (dist > maxDrag) {
                            newOffset * (maxDrag / dist)
                        } else {
                            newOffset
                        }
                        knobOffset = clamped
                        onMove(clamped.x / maxDrag, clamped.y / maxDrag)
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val outerRadius = this.size.width / 2f * 0.85f
            val knobRadius = outerRadius * 0.35f

            // Outer Base Ring
            drawCircle(
                color = Color(0x33FFFFFF),
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = Color(0x66FF3D00),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Inner Thumb Knob
            val knobCenter = center + knobOffset
            drawCircle(
                color = Color(0xCCFF3D00),
                radius = knobRadius,
                center = knobCenter
            )
            drawCircle(
                color = Color.White,
                radius = knobRadius * 0.45f,
                center = knobCenter
            )
        }
    }
}
