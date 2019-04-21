package com.shareefoo.diaryapp.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.ListDiarysQuery;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shareefoo.diaryapp.app.ClientFactory;
import com.shareefoo.diaryapp.adapters.DiaryAdapter;
import com.shareefoo.diaryapp.R;
import com.shareefoo.diaryapp.data.SPManager;
import com.shareefoo.diaryapp.ui.activities.AddDiaryActivity;
import com.shareefoo.diaryapp.utils.NetworkUtils;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import type.ModelStringFilterInput;
import type.ModeldiaryFilterInput;

public class DiaryListFragment extends Fragment {

    private static final String TAG = DiaryListFragment.class.getSimpleName();

    @BindView(R.id.rv_diaries)
    RecyclerView rvDiaries;

    @BindView(R.id.fab_add_diary)
    FloatingActionButton fabAddDiary;

    @BindView(R.id.tv_no_diaries)
    TextView tvNoDiaries;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<ListDiarysQuery.Item> mDiaries;
    private DiaryAdapter mAdapter;

    private ItemClickListener mCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_list, container, false);
        ButterKnife.bind(this, rootView);

        // Create adapter passing in our data
        mAdapter = new DiaryAdapter(getContext(), new DiaryAdapter.OnDiaryClickListener() {
            @Override
            public void onDiaryClick(ListDiarysQuery.Item item) {
                // Send the event to the host activity
                mCallback.onItemClicked(item);
            }
        });

        // Attach the adapter to the recyclerview to populate items
        rvDiaries.setAdapter(mAdapter);

        // Set layout manager to position the items
        rvDiaries.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the AppSync client
        ClientFactory.init(getContext());

        //
        fabAddDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPetIntent = new Intent(getContext(), AddDiaryActivity.class);
                getActivity().startActivity(addPetIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Query list data
        query();
    }

    public void query() {
        // Check for internet connection
        if (NetworkUtils.IsNetworkAvailable(getContext())) {

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Get user id (sub) from shared preferences
            String userId = SPManager.getInstance(getContext()).getString("user_id", null);

            //
            ModelStringFilterInput modelStringFilterInput = ModelStringFilterInput.builder().eq(userId).build();
            ModeldiaryFilterInput modeldiaryFilterInput = ModeldiaryFilterInput.builder().userId(modelStringFilterInput).build();

            //
            ClientFactory.appSyncClient().query(ListDiarysQuery.builder().filter(modeldiaryFilterInput).build())
                    .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                    .enqueue(diariesCallback);

        } else {
            Toast.makeText(getContext(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }
    }

    private GraphQLCall.Callback<ListDiarysQuery.Data> diariesCallback = new GraphQLCall.Callback<ListDiarysQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListDiarysQuery.Data> response) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });

            if (response.data().listDiarys() != null) {

                Log.i(TAG, "Retrieved list items: " + response.data().listDiarys().items().toString());

                mDiaries = new ArrayList<>(response.data().listDiarys().items());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setItems(mDiaries);
                        mAdapter.notifyDataSetChanged();

                        if (mAdapter.getItemCount() > 0) {
                            tvNoDiaries.setVisibility(View.GONE);
                        } else {
                            tvNoDiaries.setVisibility(View.VISIBLE);
                        }
                    }
                });

            } else {
                Log.e(TAG, "onResponse: Failed to query dairies");
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });

            Log.e(TAG, e.toString());
        }
    };

    // Container Activity must implement this interface
    public interface ItemClickListener {
        public void onItemClicked(ListDiarysQuery.Item item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }
}
