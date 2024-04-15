package com.example.android_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.textViewName = convertView.findViewById(R.id.textViewName);
            holder.textViewDate = convertView.findViewById(R.id.textViewDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScheduleItem currentItem = getItem(position);

        holder.textViewName.setText(currentItem.getName());
        holder.textViewDate.setText(currentItem.getDate());

        return convertView;
    }

    // 항목을 삭제하는 메소드 추가
    public void removeItem(ScheduleItem itemToRemove) {
        // 리스트에서 해당 항목을 삭제
        remove(itemToRemove);
        // 어댑터에게 데이터가 변경되었음을 알림
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView textViewName;
        TextView textViewDate;
    }
}