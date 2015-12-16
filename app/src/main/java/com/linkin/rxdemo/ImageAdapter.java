package com.linkin.rxdemo;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * @author liangjunfeng
 * @since 2015/12/16 0016 15:01
 */
public class ImageAdapter extends BaseAdapter {

    private List<Bitmap> mBitmaps;

    public ImageAdapter(List<Bitmap> bitmaps) {
        mBitmaps = bitmaps;
    }

    @Override
    public int getCount() {
        return mBitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return mBitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.view_image, null);
        }
        ((ImageView) convertView).setImageBitmap(mBitmaps.get(position));
        return convertView;
    }

}
