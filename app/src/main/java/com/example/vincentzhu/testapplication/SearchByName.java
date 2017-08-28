package com.example.vincentzhu.testapplication;

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
    private ArrayList<String> ingredient_list = new ArrayList<>(),
                                all_ingredients= new ArrayList<>(),
                                all_recipes= new ArrayList<>();
    private AutoCompleteTextView actv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        searchBy = (String) getIntent().getSerializableExtra("SEARCH_NAME");
        setTitle("Search " + searchBy);

        setContentView(R.layout.activity_search_by_name);
        super.onCreate(savedInstanceState);

        populateIngredients();

        /**
         * Autocompletetextview
         */
        actv = findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> actvAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, all_ingredients);
        actv.setAdapter(actvAdapter);
        actv.requestFocus();

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = actv.getText().toString();
                if (!item.isEmpty() && !ingredient_list.contains(item)) {
                    ingredient_list.add(item);
                    updateList();
                    actv.setText("");
                }
            }
        });
    }

    /**
     *
     */
    private void populateIngredients(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredient_Recipes");
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    all_ingredients.add(ds.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        mCustom = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Added Ingredients").child("Ingredients");
        mCustom.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                    if(!all_ingredients.contains(ds.getKey()))
                        all_ingredients.add(ds.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /**
     * Updates the ListView with entries from the ingredients list.
     * Should be called every time an entry is added to or removed from the list.
     * This method uses an ArrayAdapter to retrieve data from the ingredients ArrayList
     * and display each String entry as an item in the ListView.
     */
    private void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ingredient_list);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    /**
     * Called when the user taps the Clear List button.
     * Clears the ingredients list contained in the ListView.
     */
    public void clearList(View view) {
        ingredient_list.clear();
        updateList();
    }
}
