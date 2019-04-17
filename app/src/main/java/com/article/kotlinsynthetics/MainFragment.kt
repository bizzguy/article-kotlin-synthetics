package com.article.kotlinsynthetics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    private var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        countText.text = count.toString()

        button!!.setOnClickListener {
            count++
            countText.text = count.toString()
        }
    }

    companion object {

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

}
