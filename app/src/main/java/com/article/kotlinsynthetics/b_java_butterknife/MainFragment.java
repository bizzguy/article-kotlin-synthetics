package com.article.kotlinsynthetics.b_java_butterknife;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.article.kotlinsynthetics.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    int counter = 0;

    @BindView(R.id.countText)
    TextView counterText;

    @BindView(R.id.button)
    Button button;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        ButterKnife.bind(this, view);

        counterText.setText(Integer.toString(counter));

        button.setOnClickListener(v -> {
            counter++;
            counterText.setText(Integer.toString(counter));
        });

        return view;
    }

}
