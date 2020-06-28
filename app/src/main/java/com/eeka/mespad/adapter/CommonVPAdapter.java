package com.eeka.mespad.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 通用ViewPager adapter
 * Created by Lenovo on 2017/8/10.
 */

public abstract class CommonVPAdapter<T> extends PagerAdapter {

    private Context mContext;
    private List<T> mData;
    private int mLayoutId;

    public CommonVPAdapter(Context context, List<T> data, int layoutId) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, null);
        convertView(view, mData.get(position), position);
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void notifyDataSetChanged(List<T> data) {
        mData = data;
        super.notifyDataSetChanged();
    }

    public abstract void convertView(View view, T item, int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View item = (View) object;
        container.removeView(item);

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
