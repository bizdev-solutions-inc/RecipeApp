package com.bizdev.recipeapp.cookitup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    private boolean[] filterOptionsChecked; // Status of filter options checkboxes
    private final int NUM_OPTIONS = 3;
    private ArrayList<String> all_ingredients;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userid;
    private DatabaseReference mRoot;
    private String recipe_type;
    private String recipe_cuisine;
    private String ingredient_to_exclude;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface FilterResultsDialogListener {
        void onDialogFilterClick(DialogFragment dialog, boolean[] filterOptionsChecked,
                                 String recipe_type,
                                 String recipe_cuisine,
                                 String ingredient_to_exclude);
        void onDialogCancelClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    FilterResultsDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the FilterResultsDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the FilterResultsDialogListener so we can send events to the host
            mListener = (FilterResultsDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
            + " must implement FilterResultsDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        recipe_type = new String();
        recipe_cuisine = new String();
        ingredient_to_exclude = new String();

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
                        mListener.onDialogFilterClick(FilterResultsDialogFragment.this,
                                filterOptionsChecked,
                                recipe_type,
                                recipe_cuisine,
                                ingredient_to_exclude);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel dialog
                        mListener.onDialogCancelClick(FilterResultsDialogFragment.this);
                    }
                });

        setupUI(mView); // Set up UI elements
        setupCheckboxHandlers(mView); // Set up CheckBoxes);

        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        // Set soft keyboard to adjust to AutoCompleteTextView
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
    }

    /**
     * Instantiate the UI elements of the dialog, including spinners and text fields and their
     * item select/click handlers.
     */
    private void setupUI(View mView) {
        // Set up Recipe Type spinner inside filter dialog
        Spinner sp_type = mView.findViewById(R.id.sp_type);
        ArrayAdapter<CharSequence> sp_type_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.recipe_types, android.R.layout.simple_spinner_item);
        sp_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type.setAdapter(sp_type_adapter);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                recipe_type = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Set up Recipe Cuisine spinner inside filter dialog
        Spinner sp_cuisine = mView.findViewById(R.id.sp_cuisine);
        ArrayAdapter<CharSequence> sp_cuisine_adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.recipe_cuisines,
                        android.R.layout.simple_spinner_item);
        sp_cuisine_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_cuisine.setAdapter(sp_cuisine_adapter);
        sp_cuisine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                recipe_cuisine = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Populate list of ingredients for user to select for exclusion
        all_ingredients = new ArrayList<String>();
        populateIngredients();

        // Set up Exclude Ingredient text view
        final AutoCompleteTextView actv_exclude =
                mView.findViewById(R.id.actv_exclude);
        ArrayAdapter<String> actvAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, all_ingredients);
        actv_exclude.setAdapter(actvAdapter);
        actv_exclude.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ingredient_to_exclude = actv_exclude.getText().toString();
            }
        });
    }

    /**
     * Sets the OnCheckedChangeListener for every CheckBox in the dialogue.
     * When a CheckBox is clicked, its checked status is passed as a boolean
     * to the filterOptionsChecked boolean array that keeps track of the
     * CheckBox statuses.
     * @param mView
     */
    private void setupCheckboxHandlers(final View mView) {
        filterOptionsChecked = new boolean[NUM_OPTIONS];

        CheckBox chkbox_type = mView.findViewById(R.id.chkbox_type);
        CheckBox chkbox_cuisine = mView.findViewById(R.id.chkbox_cuisine);
        CheckBox chkbox_exclude = mView.findViewById(R.id.chkbox_exclude);

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener
                = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switch (compoundButton.getId()) {
                    case R.id.chkbox_type:
                        filterOptionsChecked[0] = isChecked;
                        break;
                    case R.id.chkbox_cuisine:
                        filterOptionsChecked[1] = isChecked;
                        break;
                    case R.id.chkbox_exclude:
                        filterOptionsChecked[2] = isChecked;
                        break;
                    default:
                        break;
                }
            }
        };
        // Set the OnCheckedChangeListener to the newly created one
        chkbox_type.setOnCheckedChangeListener(onCheckedChangeListener);
        chkbox_cuisine.setOnCheckedChangeListener(onCheckedChangeListener);
        chkbox_exclude.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    /**
     * Populate array used in AutoCompleteTextView with all available ingredients.
     */
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
