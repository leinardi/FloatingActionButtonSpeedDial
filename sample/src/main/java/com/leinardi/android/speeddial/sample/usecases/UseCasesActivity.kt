/*
 * Copyright 2020 Roberto Leinardi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leinardi.android.speeddial.sample.usecases

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.leinardi.android.speeddial.SpeedDialView
import com.leinardi.android.speeddial.sample.R

class UseCasesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_use_cases)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
        val speedDialView = findViewById<SpeedDialView>(R.id.speedDial)
        speedDialView.inflate(R.menu.menu_use_cases)
        speedDialView.open()
        speedDialView.setOnActionSelectedListener {
            startActivity(Intent(this@UseCasesActivity, UseCase1Activity::class.java))
            true
        }
    }
}
