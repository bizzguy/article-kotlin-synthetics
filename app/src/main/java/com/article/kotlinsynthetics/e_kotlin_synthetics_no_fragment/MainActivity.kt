package com.article.kotlinsynthetics.e_kotlin_synthetics_no_fragment

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle

import com.article.kotlinsynthetics.R
import kotlinx.android.synthetic.main.main_fragment.*

class MainActivity : AppCompatActivity() {

    var counter = 110

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_fragment)

        supportActionBar!!.title = "Kotlin - synthetics - No Fragment"

        text_counter!!.text = Integer.toString(counter)

        button!!.setOnClickListener {
            counter++
            text_counter!!.text = Integer.toString(counter)
        }

    }



}
