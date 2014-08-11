package com.traceandtrust;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Joey on 8/7/2014.
 */
public class CustomListAdapter extends BaseAdapter {

    private ArrayList listData;
    private DrawableBackgroundDownloader help;
    private Context allContext;

    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context context, ArrayList listData, DrawableBackgroundDownloader pics) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        help = pics;
        allContext = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.restaurant_preview_badge, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            holder.title = (TextView) convertView.findViewById(R.id.textView);
            holder.address = (TextView) convertView.findViewById(R.id.textView2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArrayList<String> temp = (ArrayList<String>) listData.get(position);

        Drawable img = null;
        help.loadDrawable(temp.get(0), holder.image, img);
        holder.title.setText(temp.get(1));
        holder.address.setText(temp.get(2));

        return convertView;
    }


    static class ViewHolder {
        TextView title;
        TextView address;
        ImageView image;
    }
}
