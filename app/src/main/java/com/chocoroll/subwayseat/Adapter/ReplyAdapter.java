package com.chocoroll.subwayseat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chocoroll.subwayseat.Model.Reply;
import com.chocoroll.subwayseat.R;

import java.util.ArrayList;

/**
 * Created by RA on 2015-06-05.
 */
    public class ReplyAdapter extends ArrayAdapter<Reply> {
    private ArrayList<Reply> items;
    private Context context;

    public ReplyAdapter(Context context, int textViewResourceId, ArrayList<Reply> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.context = context;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.model_reply, null);
    }

    final Reply p = items.get(position);
        if (p != null) {
            ((TextView)  v.findViewById(R.id.answer_date)).setText(p.getDate());
            ((TextView)  v.findViewById(R.id.answer_writer)).setText(p.getWriter());
            ((TextView) v.findViewById(R.id.answer_content)).setText(p.getContent());
        }
    return v;
    }
}
