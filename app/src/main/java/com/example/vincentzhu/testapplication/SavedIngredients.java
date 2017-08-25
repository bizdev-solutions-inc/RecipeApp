package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.List;

public class SavedIngredients extends BaseActivity {

    //user-related
    private String userID;
    private String key;
    private ListView lv;
    ArrayList<String> str = new ArrayList<String>();
    public static final String EXTRA_SEARCH_QUERY = "SEARCH_QUERY";

    //database-related
    private DatabaseReference mDatabase;


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_saved_ingredients);
        super.onCreate(savedInstanceState);

        userID = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(userID)
                .child("Added Ingredients")
                .child("Ingredients");
        lv = (ListView)findViewById(R.id.savedIngredientListView);

        if(mDatabase!=null) {
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
                    Intent intent = new Intent(SavedIngredients.this, RecipePage.class);
                    intent.putExtra(EXTRA_SEARCH_QUERY, key);
                    startActivity(intent);
                }
            });
        }
    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            str.add(ds.getKey());
        }
        ListAdapter la = new ArrayAdapter<String>(SavedIngredients.this, android.R.layout.simple_list_item_1, str);
        lv.setAdapter(la);
    }
}
