package com.shareefoo.diaryapp.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.DeleteDiaryMutation;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.shareefoo.diaryapp.app.ClientFactory;
import com.shareefoo.diaryapp.R;
import com.shareefoo.diaryapp.ui.fragments.DiaryDetailFragment;
import com.shareefoo.diaryapp.utils.NetworkUtils;

import javax.annotation.Nonnull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import type.DeleteDiaryInput;
import type.UpdateDiaryInput;

// TODO: Display Edit/Delete options on Tablet Layout
public class DiaryDetailActivity extends AppCompatActivity {

    private final static String TAG = DiaryDetailActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String mId;
    private String mTitle;
    private String mDesc;

    private ProgressDialog mProgressDialog;

    private final static int REQUEST_UPDATE_DIARY = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);
        ButterKnife.bind(this);

        // Sets the Toolbar to act as the ActionBar
        setSupportActionBar(toolbar);

        mId = getIntent().getStringExtra("id");
        mTitle = getIntent().getStringExtra("title");
        mDesc = getIntent().getStringExtra("desc");

        getSupportActionBar().setTitle(mTitle);

        //
        loadFragment(mTitle, mDesc);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_edit:
                editDiary(mId, mTitle, mDesc);
                return true;

            case R.id.action_delete:
                deleteDiary(mId);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editDiary(String id, String title, String desc) {
        Intent intent = new Intent(this, UpdateDiaryActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("desc", desc);
        startActivityForResult(intent, REQUEST_UPDATE_DIARY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_UPDATE_DIARY) {
            if (resultCode == RESULT_OK) {

                String title = data.getStringExtra("title");
                String desc = data.getStringExtra("desc");

                //
                loadFragment(title, desc);
            }
        }
    }

    private void deleteDiary(String id) {
        // Check for internet connection
        if (NetworkUtils.IsNetworkAvailable(this)) {

            // Show progress dialog
            mProgressDialog = ProgressDialog.show(this, null, getString(R.string.please_wait));

            DeleteDiaryInput input = DeleteDiaryInput.builder()
                    .id(id)
                    .build();

            DeleteDiaryMutation deleteDiaryMutation = DeleteDiaryMutation.builder()
                    .input(input)
                    .build();
            ClientFactory.appSyncClient().mutate(deleteDiaryMutation).enqueue(mutateCallback);

        } else {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }
    }

    // Mutation callback code
    private GraphQLCall.Callback<DeleteDiaryMutation.Data> mutateCallback = new GraphQLCall.Callback<DeleteDiaryMutation.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<DeleteDiaryMutation.Data> response) {
            mProgressDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DiaryDetailActivity.this, "Diary deleted", Toast.LENGTH_SHORT).show();
                    DiaryDetailActivity.this.finish();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull final ApolloException e) {
            mProgressDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("", "Failed to perform DeleteDiaryMutation", e);
                    Toast.makeText(DiaryDetailActivity.this, "Failed to delete diary", Toast.LENGTH_SHORT).show();
                    DiaryDetailActivity.this.finish();
                }
            });
        }
    };

    private void loadFragment(String title, String desc) {
        Fragment fragment = new DiaryDetailFragment();

        // Send data to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("desc", desc);

        fragment.setArguments(bundle);

        // Display the fragment programmatically in the Frame Layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_layout, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

}
