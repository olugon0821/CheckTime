package com.example.android_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ScheduleAdapter extends ArrayAdapter<ScheduleItem> {
    private Context mContext;
    private int mResource;

    public ScheduleAdapter(Context context, int resource, ArrayList<ScheduleItem> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        ScheduleItem currentItem = getItem(position);

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewDate = convertView.findViewById(R.id.textViewDate);

        textViewName.setText(currentItem.getName());
        textViewDate.setText(currentItem.getDate());

        return convertView;
    }
}
