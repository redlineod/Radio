package com.example.Radio;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by SC on 17.06.2015.
 */
public class MyArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private LayoutInflater lInflater;

    public MyArrayAdapter(Context context, String[] values){
        super(context, R.layout.list_stations, values);
        this.context = context;
        this.values = values;
        this.lInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = lInflater.inflate(R.layout.list_stations, parent, false);
        }
        if (position % 2 == 0){
            view.setBackgroundColor(Color.BLACK);
        }else
            view.setBackgroundColor(Color.WHITE);
        ((TextView)view.findViewById(R.id.textViewStations)).setText(values[position]);
        return view;
    }
}
