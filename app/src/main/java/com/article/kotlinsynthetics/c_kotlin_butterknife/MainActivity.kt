package com.article.kotlinsynthetics.c_kotlin_butterknife

import android.os.Bundle

import com.article.kotlinsynthetics.R

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        supportActionBar!!.title = "Kotlin - ButterKnife"

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitNow()
        }
    }
}
