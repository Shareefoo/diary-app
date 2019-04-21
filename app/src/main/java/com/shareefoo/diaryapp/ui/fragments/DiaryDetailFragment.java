package com.shareefoo.diaryapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shareefoo.diaryapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiaryDetailFragment extends Fragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_desc)
    TextView tvDesc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Check if there's data sent to the fragment and it's not null
        if (getArguments() != null) {
            String title = getArguments().getString("title");
            String desc = getArguments().getString("desc");

            tvTitle.setText(title);
            tvDesc.setText(desc);
        }

        return rootView;
    }
}
