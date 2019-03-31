package com.article.kotlinsynthetics.d_kotlin_synthetics

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle

import com.article.kotlinsynthetics.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        supportActionBar!!.title = "Kotlin - sythetics"

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitNow()
        }
    }
}
