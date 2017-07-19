package com.eeka.mespad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xsh on 2016/5/4.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private SparseArray<View> mViews;
    private View mConvertView;

    private RecyclerViewHolder(Context context, View view, ViewGroup parent) {
        super(view);
        mContext = context;
        mConvertView = view;
        mViews = new SparseArray<>();
    }

    public static RecyclerViewHolder get(Context context, int layoutId, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new RecyclerViewHolder(context, view, parent);
    }

    /**
     * 通过id获取控件
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 通过控件Id直接设置文字
     *
     * @param viewId
     * @param text
     * @return
     */
    public RecyclerViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    /**
     * 通过控件id直接设置图片
     *
     * @param viewId
     * @param resId
     * @return
     */
    public RecyclerViewHolder setImageResource(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

}