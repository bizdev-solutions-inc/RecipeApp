package com.example.vincentzhu.testapplication;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by XPS on 8/27/2017.
 */

public class Favorites extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_favorites);
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);






    }




}
