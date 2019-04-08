package com.article.kotlinsynthetics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    private int counter = 0;

    private TextView counterText;
    private Button button;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        counterText = view.findViewById(R.id.text_counter);

        button = view.findViewById(R.id.button);

        counterText.setText(Integer.toString(counter));

        button.setOnClickListener(v -> {
            counter++;
            counterText.setText(Integer.toString(counter));
        });

        return view;
    }

}
