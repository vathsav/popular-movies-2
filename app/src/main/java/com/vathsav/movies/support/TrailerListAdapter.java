package com.vathsav.movies.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vathsav.movies.R;

import java.util.ArrayList;

public class TrailerListAdapter extends BaseAdapter {

    private final LayoutInflater layoutInflater;
    private ArrayList<TrailerItem> listArray = new ArrayList<TrailerItem>();
    private Context context;

    public TrailerListAdapter(Context context, ArrayList<TrailerItem> array) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.listArray = array;
    }

    @Override
    public int getCount() {
        return listArray.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return listArray.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.trailer_item, null);
        }

        final TextView name = (TextView) convertView.findViewById(R.id.trailer_name);

        name.setText(listArray.get(position).getTrailerName());

        final int pos = position;

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + listArray.get(position).getTrailerKey())));
            }
        });

        return convertView;
    }
}
