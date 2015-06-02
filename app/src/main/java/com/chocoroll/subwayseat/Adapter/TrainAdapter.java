package com.chocoroll.subwayseat.Adapter;

/**
 * Created by RA on 2015-05-29.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chocoroll.subwayseat.Model.Train;
import com.chocoroll.subwayseat.R;

import java.util.ArrayList;

public class TrainAdapter extends ArrayAdapter<Train>  {


    private ArrayList<Train> items;
    private Context context;
    private int textViewResourceId;

    public TrainAdapter(Context context, int textViewResourceId, ArrayList<Train> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.textViewResourceId =textViewResourceId;
        this.context = context;

    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(textViewResourceId, null);
        }
        Train p = items.get(position);

        if (p != null) {


            TextView trainNum = (TextView) v.findViewById(R.id.trainNum);
            TextView arriveTime = (TextView) v.findViewById(R.id.arriveTime);

            trainNum.setText(p.getTrainNum());
            arriveTime.setText(p.getArriveTime());


        }
        return v;
    }


}