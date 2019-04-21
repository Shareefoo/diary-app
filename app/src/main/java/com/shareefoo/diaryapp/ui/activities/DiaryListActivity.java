package com.shareefoo.diaryapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.amazonaws.amplify.generated.graphql.ListDiarysQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.shareefoo.diaryapp.R;
import com.shareefoo.diaryapp.ui.fragments.DiaryDetailFragment;
import com.shareefoo.diaryapp.ui.fragments.DiaryListFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiaryListActivity extends AppCompatActivity implements DiaryListFragment.ItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);
        ButterKnife.bind(this);

        // Sets the Toolbar to act as the ActionBar
        setSupportActionBar(toolbar);

        // Check if phone or table mode
        isTablet = findViewById(R.id.container_layout) != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        //
        AWSMobileClient.getInstance().signOut();
        //
        Intent i = new Intent(DiaryListActivity.this, AuthenticationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onItemClicked(ListDiarysQuery.Item item) {
        // The user selected and item of the list view from the DiaryListFragment
        // Do something with the click event

        if (!isTablet) {
            // Open a new activity (Phone Mode)
            Intent intent = new Intent(this, DiaryDetailActivity.class);
            intent.putExtra("id", item.id());
            intent.putExtra("title", item.title());
            intent.putExtra("desc", item.desc());
            startActivity(intent);

        } else {
            // Display the fragment in the same layout (Tablet Mode)
            Fragment fragment = new DiaryDetailFragment();

            // Send data to the fragment
            Bundle bundle = new Bundle();
            bundle.putString("id", item.id());
            bundle.putString("title", item.title());
            bundle.putString("desc", item.desc());

            fragment.setArguments(bundle);

            // Display the fragment programmatically in the Frame Layout
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_layout, fragment);
            fragmentTransaction.commit();
        }
    }

}
