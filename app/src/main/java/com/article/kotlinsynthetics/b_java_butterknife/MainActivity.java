package com.article.kotlinsynthetics.b_java_butterknife;

import android.os.Bundle;

import com.article.kotlinsynthetics.R;
import com.article.kotlinsynthetics.d_kotlin_synthetics.MainFragment;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getSupportActionBar().setTitle("Java - ButterKnife");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.Companion.newInstance())
                    .commitNow();
        }
    }
}
