package com.chocoroll.subwayseat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chocoroll.subwayseat.Can.RelpyDialog;
import com.chocoroll.subwayseat.Model.Post;
import com.chocoroll.subwayseat.R;

import java.util.ArrayList;

/**
 * Created by RA on 2015-05-23.
 */

public class PostAdapter extends ArrayAdapter<Post> {
    private ArrayList<Post> items;
    private Context context;

    public PostAdapter(Context context, int textViewResourceId, ArrayList<Post> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.context = context;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.model_post, null);
        }
        final Post p = items.get(position);
        if (p != null) {
            ((TextView)  v.findViewById(R.id.qna_id)).setText(p.getWriter());
            ((TextView) v.findViewById(R.id.qna_date)).setText(p.getDate());
            ((TextView)  v.findViewById(R.id.qna_content)).setText(p.getContent());
            ((TextView)v.findViewById(R.id.qna_showAnswer)).setText("답변 [ "+p.getAnswerCount()+" ]");
            ((TextView)v.findViewById(R.id.qna_showAnswer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    RelpyDialog dialog = new RelpyDialog(context, p.getNum());
                    dialog.show();
                }
            });
        }
        return v;
    }
}


