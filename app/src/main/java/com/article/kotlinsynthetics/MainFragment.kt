package com.article.kotlinsynthetics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.other_layout.*

class MainFragment : Fragment() {

    internal var counter = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        text_counter.text = Integer.toString(counter)

        button.setOnClickListener { v ->
            counter++
            text_counter!!.text = Integer.toString(counter)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("counter", counter)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("counter")
        }
    }

    companion object {

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

}
