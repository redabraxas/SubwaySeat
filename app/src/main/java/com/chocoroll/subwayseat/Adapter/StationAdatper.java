package com.chocoroll.subwayseat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.chocoroll.subwayseat.Model.Station;
import com.chocoroll.subwayseat.R;
import com.chocoroll.subwayseat.SoundSearcher;

import java.util.ArrayList;

/**
 * Created by RA on 2015-05-27.
 */
public class StationAdatper extends ArrayAdapter<Station>  implements Filterable {


    private ArrayList<Station> items;
    private ArrayList<Station> filterd_items;
    private ModelFilter filter;
    private Context context;
    private int textViewResourceId;

    public StationAdatper(Context context, int textViewResourceId, ArrayList<Station> items) {
        super(context, textViewResourceId, items);
        this.items = new ArrayList<Station>();
        this.items.addAll(items);

        this.filterd_items = new ArrayList<Station>();
        this.filterd_items.addAll(items);


        this.textViewResourceId =textViewResourceId;
        this.context = context;

        getFilter();
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(textViewResourceId, null);
        }
        Station p = filterd_items.get(position);
        ViewHolder viewHolder = null;

        if (p != null) {

            viewHolder = new ViewHolder();
            viewHolder.line = (TextView)v.findViewById(R.id.txtLine);
            viewHolder.name=(TextView)v.findViewById(R.id.txtName);
            v.setTag(viewHolder);
            viewHolder.line.setText(p.getLine());
            viewHolder.name.setText(p.getName());

        }
        return v;
    }

    static class ViewHolder {
        protected TextView line;
        protected TextView name;
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new ModelFilter();
        }
        return filter;
    }

    private class ModelFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<Station> filteredItems = new ArrayList<Station>();

                for(int i = 0, l = items.size(); i < l; i++)
                {
                    Station m = items.get(i);
                    if((m.getName().toLowerCase().contains(constraint))|| SoundSearcher.matchString
                            (m.getName(), constraint.toString()))
                        filteredItems.add(m);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = items;
                    result.count = items.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {

            filterd_items = (ArrayList<Station>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = filterd_items.size(); i < l; i++)
                add(filterd_items.get(i));
            notifyDataSetInvalidated();
        }
    }

}