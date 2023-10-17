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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color as ComposeColor
val initialColors = listOf(
    ComposeColor(0xffff0000),        // red
    ComposeColor(0xff008000),        // green
    ComposeColor(0xff98fb98),        // palegreen
    ComposeColor(0xff87ceeb),        // skyblue
    ComposeColor(0xffa52a2a),        // brown
    ComposeColor(0xff808080),        // grey
    ComposeColor(0xff0000ff),        // blue
    ComposeColor(0xff800080),        // purple
    ComposeColor(0xffee82ee),        // violet
    ComposeColor(0xffffd700),        // gold
)

fun getColorNames(colors:List<Color>):List<String>{
    val names = colors.map {
        when(it) {
            ComposeColor(0xffff0000)->{ "red" }
            ComposeColor(0xff008000)->{ "green" }
            ComposeColor(0xff98fb98)->{ "palegreen" }
            ComposeColor(0xff87ceeb)->{ "skyblue" }
            ComposeColor(0xffa52a2a)->{ "brown" }
            ComposeColor(0xff808080)->{ "grey" }
            ComposeColor(0xff0000ff)->{ "blue" }
            ComposeColor(0xff800080)->{ "purple" }
            ComposeColor(0xffee82ee)->{ "violet" }
            ComposeColor(0xffffd700)->{ "gold" }
            else -> {"Unknown"}
        }
    }
    return names
}

fun shuffleColors(): List<Color> {
    return initialColors.shuffled()
}