package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class SearchResults extends BaseActivity {

    private ArrayList<String> ingredient_list, recipe_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_results);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        recipe_list = (ArrayList<String>) intent.getSerializableExtra("RECIPE_RESULTS");

        ListView lv_search_results = (ListView) findViewById(R.id.lv_search_results);
        lv_search_results.setOnItemClickListener(itemClickListener);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchResults.this,
                android.R.layout.simple_list_item_1, recipe_list);
        lv_search_results.setAdapter(adapter);

//        ingredient_list = (ArrayList<String>) getIntent().getSerializableExtra("ingredientlist");
//
//        mRoot = FirebaseDatabase.getInstance().getReference();
//
//        mRoot.child("Ingredient_Recipes").addListenerForSingleValueEvent (new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                queryData(dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) { }
//        });

    }

    // Create a list-item click-handling object as an anonymous class.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                    displayItem(recipe_list.get(position));
                }
            };

//    public void queryData(DataSnapshot dataSnapshot) {
//
//        HashSet<String> recipes = new HashSet<>(), ingredients = new HashSet<>(ingredient_list);
//
//        for (String currentIngredient : ingredients) {
//
//            try {
//                HashMap<String, Object> current = (HashMap) dataSnapshot.child(currentIngredient).getValue();
//
//                if(recipes.isEmpty())
//                    recipes.addAll(current.keySet());
//                else {
//                    HashSet<String> currentRecipes = new HashSet<>(current.keySet());
//                    recipes.retainAll(currentRecipes);
//                }
//            }
//            catch(Exception e) {
//                Log.d("Ingredient not found: ", currentIngredient);
//            }
//        }
//
//        this.recipe_list = new ArrayList<>(recipes);
//        updateView();
//
//    }



//    private void updateView() {
//        if(recipe_list.isEmpty())
//            return;
//
//        ArrayAdapter<String> adapter = null;
//        ListView listView = null;
//
//        for(String recipe : recipe_list) {
//            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipe_list);
//            listView = (ListView) findViewById(R.id.lv_search_results);
//            listView.setAdapter(adapter);
//        }
//        listView.setOnItemClickListener(itemClickListener);
//    }


    /**
     *
     * @param item
     */
    public void displayItem(String item) {
        Intent intent = new Intent(SearchResults.this, RecipePage.class);
        intent.putExtra("SELECTED_ITEM", item);
        startActivity(intent);
    }

}
