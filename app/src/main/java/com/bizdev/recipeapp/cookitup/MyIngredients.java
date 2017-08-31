package com.bizdev.recipeapp.cookitup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyIngredients extends BaseActivity {

    public static final String EXTRA_SEARCH_QUERY = "SELECTED_INGREDIENT";
    ArrayList<String> addedIngredients = new ArrayList<>();
    //user-related
    private String userID;
    private ListView lv;
    //database-related
    private DatabaseReference mDatabase;


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_ingredients);
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        userID = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(userID)
                .child("Added Ingredients")
                .child("Ingredients");
        lv = findViewById(R.id.savedIngredientListView);

        if (mDatabase != null) {
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
                    displayItem(addedIngredients.get(i));
                }
            });
        }
    }

    private void displayItem(String item) {
        Intent intent = new Intent(MyIngredients.this, IngredientPage.class);
        intent.putExtra(EXTRA_SEARCH_QUERY, item);
        startActivity(intent);
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            addedIngredients.add(ds.getKey());
        }
        ListAdapter la = new ArrayAdapter<>(MyIngredients.this,
                android.R.layout.simple_list_item_1, addedIngredients);
        lv.setAdapter(la);
    }
}
