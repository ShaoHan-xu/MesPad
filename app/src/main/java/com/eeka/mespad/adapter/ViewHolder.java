package com.eeka.mespad.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xsh on 2016/8/23.
 */
public class ViewHolder {

	private SparseArray<View> mViews;
	private View mConvertView;

	private ViewHolder(Context context, ViewGroup parent, int layoutId) {
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		mConvertView.setTag(this);
		mViews = new SparseArray<>();

	}

	static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId) {
		if (convertView == null)
			return new ViewHolder(context, parent, layoutId);
		return (ViewHolder) convertView.getTag();
	}

	public View getConvertView() {
		return mConvertView;
	}

	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	// ========================================================
	// =============常用的辅助方法==============================
	// ========================================================

	public void setText(int textViewId, String text) {
		TextView textView = getView(textViewId);
		textView.setText(text);
	}

	public void setImage(int viewId, int resId) {
		ImageView imageView = getView(viewId);
		imageView.setImageResource(resId);
	}
}
