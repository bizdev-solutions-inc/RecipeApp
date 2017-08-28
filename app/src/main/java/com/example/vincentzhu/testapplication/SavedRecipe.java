package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedRecipe extends BaseActivity {

    private DatabaseReference mDatabase;
    private String userID;
    private String activityName;
    private ListView lv;
    ArrayList<String> addedRecipes = new ArrayList<String>();
    private String key;
    public static final String EXTRA_SEARCH_QUERY = "SELECTED_ITEM";
    public static final String EXTRA_GET_ACTIVITY = "GET_ACTIVITY";

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_saved_recipe);
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        activityName = this.getLocalClassName();
        userID = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(userID)
                .child("Added Recipes")
                .child("Recipes");
        lv = findViewById(R.id.savedRecipeListView);

        if(mDatabase != null) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    key = lv.getItemAtPosition(i).toString();
                    Intent intent = new Intent(SavedRecipe.this, RecipePage.class);
                    intent.putExtra(EXTRA_SEARCH_QUERY, key);
                    intent.putExtra(EXTRA_GET_ACTIVITY, activityName);
                    startActivity(intent);
                }
            });
        }
    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            addedRecipes.add(ds.getKey());
        }
        ListAdapter la = new ArrayAdapter<String>(SavedRecipe.this, android.R.layout.simple_list_item_1, addedRecipes);
        lv.setAdapter(la);
    }
}
