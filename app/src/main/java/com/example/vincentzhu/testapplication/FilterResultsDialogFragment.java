package com.example.vincentzhu.testapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FilterResultsDialogFragment extends DialogFragment {

    private boolean[] filter_options;
    private ArrayList<String> all_ingredients;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userid;
    private DatabaseReference mRoot;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.layout_filter_dialog, null);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.msg_filter_results_by)
                .setView(mView)
                // Add action buttons
                .setPositiveButton(R.string.filter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Filter the results
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel dialog
                    }
                });

        // Set up Recipe Type spinner inside filter dialog
        Spinner sp_type = (Spinner) mView.findViewById(R.id.sp_type);
        ArrayAdapter<CharSequence> sp_type_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.recipe_types, android.R.layout.simple_spinner_item);
        sp_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type.setAdapter(sp_type_adapter);

        // Set up Recipe Cuisine spinner inside filter dialog
        Spinner sp_cuisine = (Spinner) mView.findViewById(R.id.sp_cuisine);
        ArrayAdapter<CharSequence> sp_cuisine_adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.recipe_cuisines,
                        android.R.layout.simple_spinner_item);
        sp_cuisine_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_cuisine.setAdapter(sp_cuisine_adapter);

        all_ingredients = new ArrayList<String>();
        populateIngredients();

        AutoCompleteTextView actv_exclude =
                (AutoCompleteTextView) mView.findViewById(R.id.actv_exclude);
        ArrayAdapter<String> actvAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, all_ingredients);
        actv_exclude.setAdapter(actvAdapter);

        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        // Set soft keyboard to adjust to AutoCompleteTextView
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
    }

    private void populateIngredients() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userid = user.getUid();
//        mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredient_Recipes");
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    all_ingredients.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
