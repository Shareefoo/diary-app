package com.shareefoo.diaryapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import type.CreateDiaryInput;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateDiaryMutation;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.textfield.TextInputEditText;
import com.shareefoo.diaryapp.app.ClientFactory;
import com.shareefoo.diaryapp.R;
import com.shareefoo.diaryapp.data.SPManager;
import com.shareefoo.diaryapp.utils.NetworkUtils;

import javax.annotation.Nonnull;

public class AddDiaryActivity extends AppCompatActivity {

    private static final String TAG = AddDiaryActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_title)
    TextInputEditText etTitle;

    @BindView(R.id.et_desc)
    TextInputEditText etDesc;

    @BindView(R.id.btn_add_diary)
    Button btnAddDiary;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        ButterKnife.bind(this);

        // Sets the Toolbar to act as the ActionBar
        setSupportActionBar(toolbar);

        btnAddDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewDiary();
            }
        });
    }

    private void addNewDiary() {
        // Check for internet connection
        if (NetworkUtils.IsNetworkAvailable(this)) {

            // Show progress dialog
            mProgressDialog = ProgressDialog.show(this, null, getString(R.string.please_wait));

            // Get user id (sub) from shared preferences
            String userId = SPManager.getInstance(this).getString("user_id", null);

            // Get the user input from fields
            final String title = etTitle.getText().toString();
            final String desc = etDesc.getText().toString();

            CreateDiaryInput input = CreateDiaryInput.builder()
                    .userId(userId)
                    .title(title)
                    .desc(desc)
                    .build();

            CreateDiaryMutation addDiaryMutation = CreateDiaryMutation.builder()
                    .input(input)
                    .build();
            ClientFactory.appSyncClient().mutate(addDiaryMutation).enqueue(mutateCallback);

        } else {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }
    }

    // Mutation callback code
    private GraphQLCall.Callback<CreateDiaryMutation.Data> mutateCallback = new GraphQLCall.Callback<CreateDiaryMutation.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<CreateDiaryMutation.Data> response) {
            mProgressDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddDiaryActivity.this, "Added diary", Toast.LENGTH_SHORT).show();
                    AddDiaryActivity.this.finish();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull final ApolloException e) {
            mProgressDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("", "Failed to perform AddDiaryMutation", e);
                    Toast.makeText(AddDiaryActivity.this, "Failed to add diary", Toast.LENGTH_SHORT).show();
                    AddDiaryActivity.this.finish();
                }
            });
        }
    };

}
