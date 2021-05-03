package com.stegano.punch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    val power by lazy { intent.getDoubleExtra("power", 0.0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        scoreLabel.text = power.toString()

        backButton.setOnClickListener {
            finish()
        }
    }
}