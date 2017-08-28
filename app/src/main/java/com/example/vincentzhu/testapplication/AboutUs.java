package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sun.jersey.core.impl.provider.entity.Inflector;

//test
public class AboutUs extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_aboutus);
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        /**
         * Inflector method to convert plural word form to singular.
         * Note that a library is imported in order to use this.
         */
        String plural = "tomatoes";
        Log.i("Test", Inflector.getInstance().singularize(plural));
        plural = "potatoes";
        Log.i("Test", Inflector.getInstance().singularize(plural));
        plural = "juices";
        Log.i("Test", Inflector.getInstance().singularize(plural));
        plural = "cheeses";
        Log.i("Test", Inflector.getInstance().singularize(plural));
    }


}




