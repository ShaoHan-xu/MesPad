package com.eeka.mespad.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eeka.mespad.R;

/**
 * 记录缝制质检部良界面
 * Created by Lenovo on 2017/8/11.
 */
public class RecordSewNCActivity extends BaseActivity {

    private TextView mTv_orderNum;
    private LinearLayout mLayout_productComponent;
    private LinearLayout mLayout_designComponent;
    private LinearLayout mLayout_repiarProcess;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_record_sewnc);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mTv_orderNum = (TextView) findViewById(R.id.tv_recordSewNC_workNum);
        mLayout_designComponent = (LinearLayout) findViewById(R.id.layout_recordSewNC_designComponent);
        mLayout_productComponent = (LinearLayout) findViewById(R.id.layout_recordSewNC_productComponent);
        mLayout_repiarProcess = (LinearLayout) findViewById(R.id.layout_recordSewNC_repairProcess);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_NCType);

        findViewById(R.id.btn_recordSewNC_choseRepairProcess).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }
}
