package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class SavedIngredients extends AppCompatActivity {

    ListView lv;
    //ListView Adapter
    ArrayAdapter<String> adapter;
    //search Edit Text
    EditText inputSearch;
    ArrayList<HashMap<String, String>> productList;

    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_ingredients);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        //ListView Data Array Adapter
        String products[] = {"Dell Inspiron", "HTC One X", "HTC Wildfire S", "HTC Sense",
                "HTC Sensation XE", "iPhone 4S", "Samsung Galaxy Note 800", "Samsung Galaxy S3",
                "MacBook Air", "Mac Mini", "MacBook Pro"};

        lv = (ListView)findViewById(R.id.list_view);
        inputSearch = (EditText)findViewById(R.id.inputSearch);

        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, products);
        lv.setAdapter(adapter);

        inputSearch.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SavedIngredients.this.adapter.getFilter().filter(charSequence);
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void afterTextChanged(Editable editable) {

            }
        });
    }


}
