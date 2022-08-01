package com.wallstick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Wallstick)
        setContentView(R.layout.activity_category)
    }
}