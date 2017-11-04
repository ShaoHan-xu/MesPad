package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

/**
 * 选择返修工序弹框
 * Created by Lenovo on 2017/11/4.
 */

public class RepairSelectorDialog extends Dialog {

    private Context mContext;
    private View mView;
    private List<JSONObject> mList_data;
    private String mTitle;

    private AdapterView.OnItemClickListener mListener;

    public RepairSelectorDialog(@NonNull Context context, String title, JSONArray jsonArray, AdapterView.OnItemClickListener listener) {
        super(context);
        mTitle = title;
        mList_data = jsonArray.toJavaList(JSONObject.class);
        mListener = listener;
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_repairselector, null);
        setContentView(mView);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView() {
        TextView tv_title = (TextView) mView.findViewById(R.id.tv_repairProcess_ncTYpe);
        tv_title.setText(mTitle);
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView_ncProcess);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ItemAdapter(mContext, mList_data, R.layout.gv_item_recordnc, layoutManager));

        mView.findViewById(R.id.btn_dlg_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private class ItemAdapter extends CommonRecyclerAdapter<JSONObject> {

        ItemAdapter(Context context, List<JSONObject> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(final RecyclerViewHolder holder, JSONObject item, final int position) {
            holder.getView(R.id.btn_recordNc_sub).setVisibility(View.GONE);
            TextView textView = holder.getView(R.id.tv_recordNc_type);
            textView.setBackgroundResource(0);
            textView.setText(item.getString("DESCRIPTION"));

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(null, v, position, 0);
                    }
                    dismiss();
                }
            });
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.8), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }

}
