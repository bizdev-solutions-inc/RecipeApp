package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class SearchByName extends BaseActivity {

    public static final String EXTRA_SEARCH_QUERY = "SEARCH_QUERY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_by_name);
        super.onCreate(savedInstanceState);
    }

    /**
     * Called when user taps the Search button.
     * Take the search query entered by the user and pass it as an extra to
     * the SearchResults activity and start the SearchResults activity.
     */
    public void goToSearch(View view) {
        Intent intent = new Intent(this, SearchResults.class);
        EditText et_search = (EditText) findViewById(R.id.et_search);
        String query = et_search.getText().toString();
        if (!query.isEmpty()) { // search only if query is not an empty string
            et_search.setText("");
            intent.putExtra(EXTRA_SEARCH_QUERY, query);
            startActivity(intent);
        }
    }
}
