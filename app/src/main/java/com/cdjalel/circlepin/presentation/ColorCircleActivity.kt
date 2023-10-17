/*
 *  Copyright © 2023 Djalel Chefrour, cdjalel@gmail.com
 *
 *  This file is part of a CirclePIN implementation for Android Wear OS
 *  See README.md in the root directory for information about CirclePIN
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  It is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Bilal.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.cdjalel.circlepin.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.times
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.graphics.Paint
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf

import androidx.wear.compose.material.Text

import kotlin.math.cos
import kotlin.math.sin

import com.cdjalel.circlepin.presentation.theme.CirclePINTheme
import com.cdjalel.circlepin.presentation.ColorTableActivity.Companion.startColorTableActivity
import com.cdjalel.circlepin.presentation.ResultActivity.Companion.startResultActivity

class ColorCircleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val position = intent.getIntExtra("position", 0) // Default value is 0
        val matchingColorInitialIndex = intent.getIntExtra("matching", 0)
        val previous = intent.getBooleanExtra("previous", true)

        // TODO assert position overflow within pin

        setContent {
            CirclePINTheme {
                ColorCircle(position, matchingColorInitialIndex, previous)
            }
        }
    }

    companion object {
        fun startColorCircleActivity(
            context: android.content.Context,
            position: Int,
            matchingColorInitialIndex: Int,
            previous: Boolean
        ) {
            val intent = Intent(context, ColorCircleActivity::class.java).apply {
                putExtra("position", position)
                putExtra("matching", matchingColorInitialIndex)
                putExtra("previous", previous)
            }
            context.startActivity(intent)
        }
    }
}


@Composable
fun ColorCircle(position: Int, matchingColorInitialIndex: Int, previous: Boolean) {
    var selectedDigit by remember { mutableIntStateOf(0) }
    val rotationState = remember { mutableFloatStateOf(0f) }

    val context = LocalContext.current
    val colors = shuffleColors()
    val matchingColorIndex = colors.indexOf(initialColors[matchingColorInitialIndex])
    Log.d("ColorCircle", "shuffled colors=${getColorNames(colors)}, matchingIndex=$matchingColorIndex")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Outer circle with digits
        OuterCircle()

        // Middle circle with colors
        MiddleCircle(rotationState, colors)

        // Inner circle (OK button)
        OKButton {
            val rotationStep = 36f
            val shift = rotationState.floatValue / rotationStep
            val rounded = if (shift < 0) {
                (shift - 0.5f).toInt()
            } else {
                (shift + 0.5f).toInt()
            }
            selectedDigit = (matchingColorIndex + rounded + colors.size) % colors.size
            Log.d("OK Button", "shift=$shift, rounded=$rounded, selectedDigit: $selectedDigit")

            val pin = Settings(context).getPIN()
            val current = pin[position+1].digitToInt() == selectedDigit
            val p : String = if (current) "paired" else "not paired"
            Log.d("OK Button", "digits $p")
            if ((position + 2) == pin.length) {
                val login = previous && current
                startResultActivity(context, login)
            }
            else {
                startColorTableActivity(context, position + 2, current)
            }
        }
    }
}

@Composable
fun OuterCircle() {
    val outerCircleRadius = colorCircleRadius
    val density = LocalDensity.current
    val digits = (0..9).toList()

    Box(
        modifier = Modifier
            .size(outerCircleRadius * 2)
            .clip(CircleShape)
            .background(Color.DarkGray)
    ) {
        Canvas(
            modifier = Modifier.size(outerCircleRadius * 2)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val circleRadius = with(density) {
                (outerCircleRadius-(cellSize / 2 - 3 * borderSize )).toPx()
            }
            repeat(10) { i ->
                val angle = Math.toRadians(36.0 * i - 90)
                val textPaint = Paint()
                textPaint.textAlign = Paint.Align.CENTER
                textPaint.textSize = digitFontSize.toPx()
                textPaint.color = Color.White.toArgb()
                val x = centerX + circleRadius * cos(angle)
                val y = centerY + circleRadius * sin(angle) -
                        ((textPaint.descent() + textPaint.ascent()) / 2)
                        // the distance from the baseline to the center.
                drawContext.canvas.nativeCanvas.drawText(
                    digits[i].toString(),
                    x.toFloat(),
                    y.toFloat(),
                    textPaint
                )
            }
        }
    }
}

@Composable
fun MiddleCircle(rotationState: MutableState<Float>, colors: List<Color>) {
    val radius = colorCircleRadius - cellSize * 2/3 - 3 * borderSize
    val focusRequester = remember { FocusRequester() }

    Canvas(
        modifier = Modifier
            .size(radius * 2)
            /*  FIXME: doesn't get rotary events! so we use onRotaryScrollEvent for now
            .pointerInput(Unit) {
                // Handle crown rotation
                     detectTransformGestures { _, _, _, rotation ->
                         Log.d("Rotation", "Rotation: $rotation")
                         rotationState.value += rotation
                     }
            }  */
            .onRotaryScrollEvent {
                // Handle crown rotation
                rotationState.value += it.verticalScrollPixels
                rotationState.value %= 360 // it seems these are not angles
                // val rotation = rotationState.value
                // Log.d("onRotaryScrollEvent", "new rotation value: $rotation")
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        onDraw = {
            // Calculate angle step for dividing the circle into 10 equal portions
            for (i in 0 until 10) {
                val angleStep = 36f
                val startAngle = -90 - angleStep/2 + i * angleStep + rotationState.value
                drawArc(
                    color = colors[i],
                    startAngle = startAngle,
                    sweepAngle = angleStep,
                    useCenter = true,
                )
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun OKButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(cellSize * 3/2)
            .clip(CircleShape)
            .background(Color.DarkGray)
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "OK",
            fontSize = digitFontSize,
            color = Color.White
        )
    }
}