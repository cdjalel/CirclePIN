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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.MaterialTheme

import com.cdjalel.circlepin.presentation.theme.CirclePINTheme
import com.cdjalel.circlepin.presentation.ColorCircleActivity.Companion.startColorCircleActivity

class ColorTableActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val position: Int = intent.getIntExtra("position", 0) // Default value is 0
        val previous = intent.getBooleanExtra("previous", false) // Default value is false

        setContent {
            CirclePINTheme {
                ColorTableScreen(position, previous)
            }
        }
    }

    companion object {
        fun startColorTableActivity(
            context: android.content.Context,
            position: Int,
            previous: Boolean
        ) {
            val intent = Intent(context, ColorTableActivity::class.java).apply {
                putExtra("position", position)
                putExtra("previous", previous)
            }
            context.startActivity(intent)
        }
    }
}

@Composable
fun ColorTableScreen(position: Int, previous: Boolean) {
    val context = LocalContext.current
    val pin = Settings(context).getPIN()
    val colors = remember { shuffleColors() }

    val matchingColorInitialIndex = pin.getOrNull(position)?.toString()?.toIntOrNull()?.let { digit ->
        val initialIndex = initialColors.indexOf(colors[digit])
        if (initialIndex != -1) {
            initialIndex
        } else {
            null
        }
    } ?: return

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            for (i in 0 until 3) {
                Row(
                    modifier = Modifier.width(cellSize * 3),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (j in 0 until 3) {
                        val index = i * 3 + j
                        ColorCell(index+1, colors[index+1])
                    }
                }
            }
            Row(
                modifier = Modifier.width(cellSize * 3),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Spacer(
                    modifier = Modifier
                        .size(cellSize)
                        .background(MaterialTheme.colors.background)
                )
                ColorCell(0, colors[0])
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clickable { startColorCircleActivity(context, position, matchingColorInitialIndex, previous) }
                        .size(cellSize)
                ) {
                    Text(
                        text = "OK",
                        color = ComposeColor.White
                    )
                }
            }
        }
    }
}

@Composable
fun ColorCell(digit: Int, backgroundColor: ComposeColor) {
    Box(modifier = Modifier
        .border(borderSize, ComposeColor.Black)
        .size(cellSize)
        .background(backgroundColor),
        contentAlignment = Alignment.Center,
    )
    {
        Text(
            text = digit.toString(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = digitFontSize,
                color = ComposeColor.White
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ColorTableScreenPreview() {
    CirclePINTheme {
        ColorTableScreen(0, false)
    }
}
