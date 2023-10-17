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
//import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.Button

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

import com.cdjalel.circlepin.presentation.InvalidPinActivity.Companion.startInvalidPinActivity
import com.cdjalel.circlepin.presentation.theme.CirclePINTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CirclePINTheme {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val settings = Settings(context)
    var pin by remember { mutableStateOf(settings.getPIN()) }

    val handleDigitClick: (Int) -> Unit = { digit ->
        when (digit) {
            -1 -> {
                // Handle the OK button click
                if (pin.length % 2 == 0) {
                    // Save the PIN and start MainActivity when OK is pressed
                    settings.savePIN(pin)
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
                else {
                    startInvalidPinActivity(context, pin)
                }
            }
            -2 -> {
                // Handle backspace button click
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1) // Remove the last character from 'pin'
                }
            }
            else -> {
                // Handle numeric button clicks (1-9 and 0)
                pin += digit.toString() // Append the digit to 'pin'
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padSize),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
                .wrapContentWidth()
                .border(borderSize, Color.Gray, shape = RoundedCornerShape(cornerSize))
        ) {
            /* for the case of a password entered with a soft keyboard
                BasicTextField(
                    value = pin,
                    readOnly = true,
                    onValueChange = {
                        // Update the PIN as the user types
                        pin = it
                    },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(padSize)
                )
            */
            Text(
                text = pin,
                style = TextStyle(
                    fontSize = digitFontSize,
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(padSize)
            )
        }

        // Numeric pad
        NumericPad(pin, onPadClick = handleDigitClick)
    }
}
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    CirclePINTheme {
        SettingsScreen()
    }
}

@Composable
fun NumericPad(
    pin: String,
    onPadClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
 ) {
        for (i in 1..3) {
            Row(
                modifier = Modifier.width(cellSize * 3),//fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (j in 1..3) {
                    val digit = i + (j - 1) * 3
                    NumericButton(digit = digit, onClick = { onPadClick(digit) })
                }
            }
        }

        Row(
            modifier = Modifier.width(cellSize * 3),//fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onPadClick(-2) },
                modifier = Modifier.size(cellSize)
            ) {
                Text(text = "⌫")
            }
            NumericButton(digit = 0, onClick = { onPadClick(0) })
            Button(
                onClick = { onPadClick(-1) },
                enabled = pin.length >= 4, // Enable only if 'pin' length is at least 4
                modifier = Modifier.size(cellSize)
            ) {
                Text(text = "⏎")
            }
        }
    }
}

@Composable
fun NumericButton(
    digit: Int,
    text: String? = digit.toString(),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(cellSize)
    ) {
        if (text != null) {
            Text(text = text)
        }
    }
}