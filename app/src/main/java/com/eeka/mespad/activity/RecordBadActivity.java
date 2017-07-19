package com.eeka.mespad.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录不良界面
 * Created by Lenovo on 2017/7/19.
 */

public class RecordBadActivity extends BaseActivity {

    private ItemAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_recordbad);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        List<String> list = new ArrayList<>();
        list.add("面料太薄");
        list.add("裁片太长");
        list.add("裁片太短");
        list.add("形状不对");
        list.add("版型不对");
        list.add("大小不符");
        list.add("辅料不够");
        list.add("面料太薄");
        list.add("裁片太长");
        list.add("裁片太短");
        list.add("形状不对");
        list.add("版型不对");
        list.add("大小不符");
        list.add("辅料不够");
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        mAdapter = new ItemAdapter(mContext, list, R.layout.gv_item_recordbad, layoutManager);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gv_recordBad);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private class ItemAdapter extends CommonRecyclerAdapter<String> {

        public ItemAdapter(Context context, List<String> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, String item, int position) {
            holder.setText(R.id.tv_item_recordBad_type, item);
        }
    }
}
