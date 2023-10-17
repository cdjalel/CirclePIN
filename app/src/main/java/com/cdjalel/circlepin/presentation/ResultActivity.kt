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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

import com.cdjalel.circlepin.presentation.theme.CirclePINTheme

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val result = intent.getBooleanExtra("result", false)

        setContent {
            CirclePINTheme {
                ResultScreen(result)
            }
        }
    }

    companion object {
        fun startResultActivity(
            context: android.content.Context,
            result: Boolean,
        ) {
            val intent = Intent(context, ResultActivity::class.java).apply {
                putExtra("result", result)
            }
            context.startActivity(intent)
        }
    }
}

@Composable
fun ResultScreen(result: Boolean) {
    val context = LocalContext.current

    // Display an error message and a button to go back
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val v: String = if (result) "Valid" else "Invalid"
        Text(
            text = "$v authentication",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(cellSize /2))
        Button(
            modifier = Modifier.size(cellSize),
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
            }
        ) {
            Text(
                text = "⟳",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = digitFontSize,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}