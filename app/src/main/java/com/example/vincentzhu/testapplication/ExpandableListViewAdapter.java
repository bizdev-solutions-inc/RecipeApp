package com.example.vincentzhu.testapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

//    String [] groupNames = { "Ingredients", "Instructions" };
//    String [][] childNames = {{"Ingredients go here"}, {"Instructions go here"}};

//    ArrayList<String> groupNames =
//            new ArrayList<String>(Arrays.asList("Ingredients", "Instructions"));
//    ArrayList<ArrayList<String>> childNames = new ArrayList<ArrayList<String>>();
//
//    ArrayList<String> ingredients = new ArrayList<String>(Arrays.asList("Ingredients go here"));
//    ArrayList<String> instructions = new ArrayList<String>(Arrays.asList("Instructions go here"));

    private ArrayList<String> groupNames;
    private ArrayList<ArrayList<String>> childNames;

    Context context;

    public ExpandableListViewAdapter(Context context, ArrayList<String> groupNames,
                                     ArrayList<ArrayList<String>> childNames) {
        this.context = context;
        this.groupNames = groupNames;
        this.childNames = childNames;
    }

    @Override
    public int getGroupCount() {
//        return groupNames.length;
        return groupNames.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        return childNames[groupPosition].length;
        return childNames.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
//        return groupNames[groupPosition];
        return groupNames.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
//        return childNames[groupPosition][childPosition];
        return childNames.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        TextView textView = new TextView(context);
//        textView.setText(groupNames[groupPosition]);
        textView.setText(groupNames.get(groupPosition));
        textView.setPadding(0, 8, 0, 8); // left, top, right, bottom
        textView.setTextSize(24);
        textView.setGravity(17); // center
        return textView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        final TextView textView = new TextView(context);
//        textView.setText(childNames[groupPosition][childPosition]);
        textView.setText(childNames.get(groupPosition).get(childPosition));
        textView.setPadding(160, 8, 0, 8); // left, top, right, bottom
        textView.setTextSize(20);

//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, textView.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });

        return textView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
