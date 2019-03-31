package com.article.kotlinsynthetics.d_kotlin_synthetics

import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.article.kotlinsynthetics.R

import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    var counter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        counterText!!.text = Integer.toString(counter)

        button!!.setOnClickListener {
            counter++
            counterText!!.text = Integer.toString(counter)
        }
    }

    companion object {

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

}
