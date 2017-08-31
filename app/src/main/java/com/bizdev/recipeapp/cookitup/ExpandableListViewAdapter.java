package com.bizdev.recipeapp.cookitup;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> groupNames;
    private ArrayList<ArrayList<String>> childNames;

    private Context context;

    ExpandableListViewAdapter(Context context, ArrayList<String> groupNames,
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
        textView.setText(groupNames.get(groupPosition));
        textView.setPadding(0, 8, 0, 8); // left, top, right, bottom
        textView.setTextSize(24);
        textView.setGravity(17); // center
        return textView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        final TextView textView = new TextView(context);
        String text = childNames.get(groupPosition).get(childPosition);
        if (groupPosition == 0 && groupNames.get(0).equals("Ingredients")) {
            textView.setText(WordUtils.capitalize(text));
        } else {
            textView.setText(text);
        }
        textView.setPadding(120, 0, 120, 0); // left, top, right, bottom
        textView.setTextSize(20);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == 0 && groupNames.get(0).equals("Ingredients"))
                {
                    String item = textView.getText().toString().toLowerCase();
                    Intent intent = new Intent(context.getApplicationContext(), IngredientPage.class);
                    intent.putExtra("SELECTED_INGREDIENT", item);
                    //Toast.makeText(context, textView.getText().toString(), Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                }
            }
        });

        return textView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
