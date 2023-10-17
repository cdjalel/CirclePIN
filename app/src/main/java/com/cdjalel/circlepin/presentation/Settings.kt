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

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Settings(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("CirclePINSettings", Context.MODE_PRIVATE)
    private val pinKey = "PIN"

    // Save the PIN value to SharedPreferences
    fun savePIN(value: String) {
        sharedPreferences.edit {
            putString(pinKey, value)
            apply() // Use apply to save immediately, or use commit to save synchronously
        }
    }

    // Read a string value from SharedPreferences
    fun getPIN(defaultValue: String = "0000"): String {
        return sharedPreferences.getString(pinKey, defaultValue) ?: defaultValue
    }
}

