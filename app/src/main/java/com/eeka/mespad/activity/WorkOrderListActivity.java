package com.eeka.mespad.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.WorkOrderBo;

import java.util.ArrayList;
import java.util.List;

/**
 * 作业订单查看页面
 * Created by Lenovo on 2017/7/6.
 */

public class WorkOrderListActivity extends BaseActivity {

    private static final int TYPE_UNDO = 0;//未完成
    private static final int TYPE_DONE = 1;//已完成

    private Button mBtn_undo, mBtn_done, mBtn_search;
    private EditText mEt_search;

    private ListView mListView;
    private OrderAdapter mAdapter;
    private List<WorkOrderBo> mList_data;

    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_orderlist);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        mEt_search = (EditText) findViewById(R.id.et_orderList_search);
        mBtn_undo = (Button) findViewById(R.id.btn_orderList_undo);
        mBtn_done = (Button) findViewById(R.id.btn_orderList_done);
        mBtn_search = (Button) findViewById(R.id.btn_orderList_search);

        mListView = (ListView) findViewById(R.id.lv_orderList);

        mBtn_undo.setOnClickListener(this);
        mBtn_done.setOnClickListener(this);
        mBtn_search.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);

        mList_data = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            WorkOrderBo order = new WorkOrderBo();
            order.setOrderNum("orderNum=====" + i);
            order.setStyleNum("styleNum=====" + i);
            order.setStartTime("217-07-06 09:06");
            order.setEndTime("217-07-06 10:06");
            order.setAmount((int) (i * 1.8));
            order.setAddStatus(i % 2);
            order.setReturnStatus(i % 2);
            mList_data.add(order);
        }
        mAdapter = new OrderAdapter(mContext, mList_data, R.layout.lv_item_orderlist);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_orderList_undo:
                mType = TYPE_UNDO;
                mBtn_undo.setBackgroundResource(R.color.text_gray_default);
                mBtn_done.setBackgroundResource(R.color.white);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_orderList_done:
                mType = TYPE_DONE;
                mBtn_done.setBackgroundResource(R.color.text_gray_default);
                mBtn_undo.setBackgroundResource(R.color.white);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_orderList_search:
                search();
                break;
        }
    }

    private void search() {
        String searchKey = mEt_search.getText().toString();

    }

    private class OrderAdapter extends CommonAdapter<WorkOrderBo> {

        public OrderAdapter(Context context, List<WorkOrderBo> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, WorkOrderBo item, int position) {
            holder.setText(R.id.tv_item_orderList_orderNum, item.getOrderNum());
            holder.setText(R.id.tv_item_orderList_styleNum, item.getStyleNum());
            holder.setText(R.id.tv_item_orderList_startTime, item.getStartTime());
            holder.setText(R.id.tv_item_orderList_endNum, item.getEndTime());
            holder.setText(R.id.tv_item_orderList_amount, item.getAmount() + "");

            TextView tv_return = holder.getView(R.id.tv_item_orderList_returnMat);
            TextView tv_addMat = holder.getView(R.id.tv_item_orderList_addMat);
            TextView tv_labuRecord = holder.getView(R.id.tv_item_orderList_labuRecord);
            if (mType == TYPE_DONE) {
                tv_addMat.setVisibility(View.VISIBLE);
                tv_return.setVisibility(View.VISIBLE);
                tv_labuRecord.setVisibility(View.VISIBLE);

                int addStatus = item.getAddStatus();
                if (addStatus == 1) {
                    tv_addMat.setText("已补料");
                } else {
                    tv_addMat.setText("补料申请");
                }

                int returnStatus = item.getReturnStatus();
                if (returnStatus == 1) {
                    tv_return.setText("已退料");
                } else {
                    tv_return.setText("退料申请");
                }
            } else {
                tv_addMat.setVisibility(View.INVISIBLE);
                tv_return.setVisibility(View.INVISIBLE);
                tv_labuRecord.setVisibility(View.INVISIBLE);
            }

        }
    }
}
