package com.example.vincentzhu.testapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Vincent on 8/13/2017.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter{

//    String[] groupNames = {"Sport", "Computer", "Food", "Car", "TV"};
//    String[][] childNames = {{"Boxing", "Kick Boxing", "Judo", "Football", "Basketball"},
//            {"Desktop Computer", "Laptop Computer", "Smartphone Computer"},
//            {"Ice Cream", "Banana"}, {"Mercedes Benz"}, {"Samsung TV", "LG TV"}};

    Context context;
    ArrayList<String> par;
    ArrayList<ArrayList<String>> list;
    public ExpandableListViewAdapter(Context context, ArrayList<String> parent, ArrayList<ArrayList<String>> listOfLists){
        this.context = context;
        par = new ArrayList<String>(parent);
        list = new ArrayList<ArrayList<String>>(listOfLists);
    }

    @Override
    public int getGroupCount() {
        return par.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return list.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return par.get(i);
    }

    @Override //i + one for i1
    public Object getChild(int i, int i1) {
        return list.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        TextView txtView = new TextView(context);
        txtView.setText(par.get(i));
        txtView.setPadding(100, 0, 0, 0);
        txtView.setTextColor(Color.BLUE);
        txtView.setTextSize(40);
        return txtView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final TextView txtView = new TextView(context);
        txtView.setText(list.get(i).get(i1));
        txtView.setPadding(100, 0, 0, 0);
        txtView.setTextColor(Color.RED);
        txtView.setTextSize(30);

        txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, txtView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return txtView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}