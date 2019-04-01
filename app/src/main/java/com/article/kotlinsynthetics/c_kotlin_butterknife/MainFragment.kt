package com.article.kotlinsynthetics.c_kotlin_butterknife

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.article.kotlinsynthetics.R
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife

class MainFragment : Fragment() {

    internal var counter = 0

    @BindView(R.id.counterText)
    @JvmField
    var counterText: TextView? = null

    @BindView(R.id.button)
    @JvmField
    var button: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        ButterKnife.bind(this, view)

        counterText!!.text = Integer.toString(counter)

        button!!.setOnClickListener { v ->
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
