package com.article.kotlinsynthetics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.other_layout.*

class MainFragment : Fragment() {

    internal var counter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        otherCounterText.text = Integer.toString(counter)

        counterText.text = "aaa"

        button.setOnClickListener { v ->
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
