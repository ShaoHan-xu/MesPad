package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.BTReasonBo;

import java.util.List;

/**
 * popupWindow选择器弹框
 * Created by Lenovo on 2017/7/5.
 */

public class SelectorPopWindow<T> extends PopupWindow {

    private Context mContext;
    private View mView;
    private List<T> mList_data;
    private AdapterView.OnItemClickListener mItemClickListener;

    public SelectorPopWindow(Context context, List<T> data, AdapterView.OnItemClickListener itemClickListener) {
        mContext = context;
        mList_data = data;
        mItemClickListener = itemClickListener;
        mView = LayoutInflater.from(context).inflate(R.layout.ppw_selector, null);
        setContentView(mView);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);

        initView();
    }

    private void initView() {
        ListView listView = (ListView) mView.findViewById(R.id.lv_ppw_selector);
        listView.setAdapter(new CommonAdapter<T>(mContext, mList_data, R.layout.item_textview) {
            @Override
            public void convert(ViewHolder holder, T item, int position) {
                if (item instanceof BTReasonBo) {
                    holder.setText(R.id.text, ((BTReasonBo) item).getREASON_DESC());
                } else {
                    holder.setText(R.id.text, item.toString());
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                mItemClickListener.onItemClick(parent, view, position, id);
            }
        });
    }

}
