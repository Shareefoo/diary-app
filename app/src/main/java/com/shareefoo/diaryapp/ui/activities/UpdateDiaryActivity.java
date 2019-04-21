package com.shareefoo.diaryapp.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateDiaryMutation;
import com.amazonaws.amplify.generated.graphql.UpdateDiaryMutation;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.textfield.TextInputEditText;
import com.shareefoo.diaryapp.R;
import com.shareefoo.diaryapp.app.ClientFactory;
import com.shareefoo.diaryapp.data.SPManager;
import com.shareefoo.diaryapp.utils.NetworkUtils;

import javax.annotation.Nonnull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import type.CreateDiaryInput;
import type.UpdateDiaryInput;

// TODO: REFACTOR (Use Dialog / MERGE WITH AddDiaryActivity)
public class UpdateDiaryActivity extends AppCompatActivity {

    private static final String TAG = UpdateDiaryActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_title)
    TextInputEditText etTitle;

    @BindView(R.id.et_desc)
    TextInputEditText etDesc;

    @BindView(R.id.btn_update_diary)
    Button btnUpdateDiary;

    private ProgressDialog mProgressDialog;

    private String mTitle;
    private String mDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_diary);
        ButterKnife.bind(this);

        // Sets the Toolbar to act as the ActionBar
        setSupportActionBar(toolbar);

        etTitle.setText(getIntent().getStringExtra("title"));
        etDesc.setText(getIntent().getStringExtra("desc"));

        String id = getIntent().getStringExtra("id");

        btnUpdateDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDiary(id);
            }
        });
    }

    private void updateDiary(String id) {
        // Check for internet connection
        if (NetworkUtils.IsNetworkAvailable(this)) {

            // Show progress dialog
            mProgressDialog = ProgressDialog.show(this, null, getString(R.string.please_wait));

            // Get user id (sub) from shared preferences
            String userId = SPManager.getInstance(this).getString("user_id", null);

            // Get the user input from fields
            mTitle = etTitle.getText().toString();
            mDesc = etDesc.getText().toString();

            UpdateDiaryInput input = UpdateDiaryInput.builder()
                    .id(id)
                    .title(mTitle)
                    .desc(mDesc)
                    .build();

            UpdateDiaryMutation updateDiaryMutation = UpdateDiaryMutation.builder()
                    .input(input)
                    .build();
            ClientFactory.appSyncClient().mutate(updateDiaryMutation).enqueue(mutateCallback);

        } else {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }
    }

    // Mutation callback code
    private GraphQLCall.Callback<UpdateDiaryMutation.Data> mutateCallback = new GraphQLCall.Callback<UpdateDiaryMutation.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<UpdateDiaryMutation.Data> response) {
            mProgressDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UpdateDiaryActivity.this, "Diary updated", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.putExtra("title", mTitle);
                    intent.putExtra("desc", mDesc);
                    setResult(RESULT_OK, intent);

                    UpdateDiaryActivity.this.finish();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull final ApolloException e) {
            mProgressDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("", "Failed to perform UpdateDiaryMutation", e);
                    Toast.makeText(UpdateDiaryActivity.this, "Failed to update diary", Toast.LENGTH_SHORT).show();
                    UpdateDiaryActivity.this.finish();
                }
            });
        }
    };

}
