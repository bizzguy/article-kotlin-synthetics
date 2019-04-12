package com.article.kotlinsynthetics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife

class MainFragment : Fragment() {

    private var counter = 0

    @BindView(R.id.countText)
    @JvmField
    internal var countText: TextView? = null

    @BindView(R.id.button)
    internal var button: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        ButterKnife.bind(activity!!, view)

        button!!.setOnClickListener {
            counter++
            countText!!.setText(counter)
        }

        return view
    }

    companion object {

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

}
