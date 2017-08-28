package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchByName extends BaseActivity {

    private DatabaseReference mRoot, mCustom;
    private String searchBy;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ArrayList<String> all_results= new ArrayList<>();
    private AutoCompleteTextView actv;
    private boolean isRecipe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
;
        searchBy = (String) getIntent().getSerializableExtra("SEARCH_NAME");
        setTitle("Search " + searchBy);

        if(searchBy.equals("Recipes"))
            isRecipe = true;

        setContentView(R.layout.activity_search_by_name);
        super.onCreate(savedInstanceState);

        populateResults();

        /**
         * AutocompleteTextView for the searchsing functionality
         */
        actv = findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> actvAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, all_results);
        actv.setAdapter(actvAdapter);
        actv.requestFocus();

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = actv.getText().toString();
                if (!item.isEmpty())
                    showResultPage(item, isRecipe);
            }
        });
    }

    /**
     * Fills the autocomplete textView with results from the database that match the first
     * few characters that are inputted
     */
    private void populateResults(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        mRoot = FirebaseDatabase.getInstance().getReference().child(searchBy);
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    all_results.add(ds.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        mCustom = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Added " + searchBy).child(searchBy);
        mCustom.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                    if(!all_results.contains(ds.getKey()))
                        all_results.add(ds.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    // Shows either the IngredientPage or the RecipePage depending on the selection
    private void showResultPage(String item, boolean isRecipe) {
        Intent intent;
        if(isRecipe) {
            intent = new Intent(SearchByName.this, RecipePage.class);
            intent.putExtra("SELECTED_ITEM", item);
        }
        else {
            intent = new Intent(SearchByName.this, IngredientPage.class);
            intent.putExtra("SELECTED_INGREDIENT", item);
        }

        startActivity(intent);
    }
}
