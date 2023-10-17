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
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text

import com.cdjalel.circlepin.presentation.theme.CirclePINTheme

class InvalidPinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var input = intent.getStringExtra("input")
        if (input == null) input = ""

        setContent {
            CirclePINTheme {
                InvalidPinScreen(input)
            }
        }
    }

    companion object {
        fun startInvalidPinActivity(
            context: android.content.Context,
            input: String,
        ) {
            val intent = Intent(context, InvalidPinActivity::class.java).apply {
                putExtra("input", input)
            }
            context.startActivity(intent)
        }
    }
}

@Composable
fun InvalidPinScreen(input: String) {
    val context = LocalContext.current

    // Display an error message and a button to go back
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Invalid PIN: '$input'\n It must have an even number of digits",
            style = TextStyle(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.size(36.dp),
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
            }
        ) {
            Text(text = "⇦")
        }
    }
}