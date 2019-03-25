package com.footlocker.sample.kotlin6.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.footlocker.sample.kotlin6.R
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    var counter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        counterText!!.text = Integer.toString(counter)

        button!!.setOnClickListener {
            counter++
            counterText!!.text = Integer.toString(counter)
        }

        return view
    }

    companion object {

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

}
