package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by XPS on 8/27/2017.
 */

public class Favorites extends BaseActivity {
    private ArrayList<String> favorite_recipes, favorite_ingredients;
    private DatabaseReference mRoot;
    private DatabaseReference mFavorite;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userid;
    private String key;
    private String activityName;
    private ArrayAdapter<String> adapter;

    public static final String EXTRA_SEARCH_QUERY = "SELECTED_ITEM";
    public static final String EXTRA_GET_ACTIVITY = "GET_ACTIVITY";

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_favorites);
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activityName = this.getLocalClassName();
        favorite_recipes = new ArrayList<String>();
        favorite_ingredients = new ArrayList<String>();

        populateFavoriteRecipes();

        adapter = new ArrayAdapter<>(Favorites.this,
                android.R.layout.simple_list_item_1, favorite_recipes);

        ListView lv_favorite_recipes = findViewById(R.id.lv_favorite_recipes);
        lv_favorite_recipes.setAdapter(adapter);
        lv_favorite_recipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                displayItem(favorite_recipes.get(i));
            }
        });

//        lv_favorite_recipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                key = lv_favorite_recipes.getItemAtPosition(i).toString();
//                Intent intent = new Intent(Favorites.this, RecipePage.class);
//                intent.putExtra(EXTRA_SEARCH_QUERY, key);
//                intent.putExtra(EXTRA_GET_ACTIVITY, activityName);
//                startActivity(intent);
//            }
//        });
    }

    private void displayItem(String item) {
        Intent intent = new Intent(Favorites.this, RecipePage.class);
        intent.putExtra("SELECTED_ITEM", item);
        startActivity(intent);
    }


    private void populateFavoriteRecipes() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userid = user.getUid();
        mRoot = FirebaseDatabase.getInstance().getReference().child("Recipes");
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!favorite_recipes.contains(ds.getKey()) && ds.child("Favorited By").child(userid).exists()) {
                        favorite_recipes.add(ds.getKey());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFavorite = FirebaseDatabase.getInstance().getReference().child(userid).child("Added Recipes").child("Recipes");
        mFavorite.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(!favorite_recipes.contains(ds.getKey()) && ds.child("Favorited By").child(userid).exists()){
                        favorite_recipes.add(ds.getKey());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateFavoriteIngredients() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userid = user.getUid();
        mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("Favorited By").child(userid).exists()) {
                        favorite_ingredients.add(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFavorite = FirebaseDatabase.getInstance().getReference().child(userid).child("Added Ingredients").child("Ingredients");
        mFavorite.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(!favorite_ingredients.contains(ds.getKey()) && ds.child("Favorited By").child(userid).exists()){
                        favorite_ingredients.add(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
