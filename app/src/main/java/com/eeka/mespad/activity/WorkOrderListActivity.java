package com.eeka.mespad.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.RecordLabuMaterialInfoBo;
import com.eeka.mespad.bo.WorkOrderBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.view.dialog.RecordLabuDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 作业订单查看页面
 * Created by Lenovo on 2017/7/6.
 */

public class WorkOrderListActivity extends BaseActivity implements RecordLabuDialog.OnRecordLabuCallback {

    private static final int TYPE_UNDO = 0;//未完成
    private static final int TYPE_DONE = 1;//已完成

    private Button mBtn_undo, mBtn_done, mBtn_search;
    private EditText mEt_search;
    private TextView mTv_statusTag;

    private ListView mListView;
    private OrderAdapter mAdapter;
    private List<WorkOrderBo> mList_done;
    private List<WorkOrderBo> mList_undo;

    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_orderlist);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();

        mEt_search = (EditText) findViewById(R.id.et_orderList_search);
        mBtn_undo = (Button) findViewById(R.id.btn_orderList_undo);
        mBtn_done = (Button) findViewById(R.id.btn_orderList_done);
        mBtn_search = (Button) findViewById(R.id.btn_orderList_search);
        mTv_statusTag = (TextView) findViewById(R.id.tv_orderList_status);

        mListView = (ListView) findViewById(R.id.lv_orderList);

        mBtn_undo.setOnClickListener(this);
        mBtn_done.setOnClickListener(this);
        mBtn_search.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();

        mList_undo = new ArrayList<>();
        mList_done = new ArrayList<>();
        mAdapter = new OrderAdapter(mContext, mList_undo, R.layout.lv_item_orderlist);
        mListView.setAdapter(mAdapter);

        showLoading();
        HttpHelper.getWorkOrderList(this);
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
                mTv_statusTag.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged(mList_undo);
                break;
            case R.id.btn_orderList_done:
                mType = TYPE_DONE;
                mBtn_done.setBackgroundResource(R.color.text_gray_default);
                mBtn_undo.setBackgroundResource(R.color.white);
                mAdapter.notifyDataSetChanged();
                mTv_statusTag.setVisibility(View.INVISIBLE);
                mAdapter.notifyDataSetChanged(mList_done);
                break;
            case R.id.btn_orderList_search:
                search();
                break;
        }
    }

    private void search() {
        SystemUtils.hideKeyboard(mContext, mEt_search);
        String searchKey = mEt_search.getText().toString().toLowerCase();
        List<WorkOrderBo> list = new ArrayList<>();
        for (WorkOrderBo item : mList_done) {
            if (item.getShopOrder().toLowerCase().contains(searchKey)) {
                list.add(item);
            }
        }
        for (WorkOrderBo item : mList_undo) {
            if (item.getShopOrder().toLowerCase().contains(searchKey)) {
                list.add(item);
            }
        }
        if (list.size() == 0) {
            toast("无对应搜索结果");
        } else {
            mAdapter.notifyDataSetChanged(list);
        }

    }

    private class OrderAdapter extends CommonAdapter<WorkOrderBo> {

        public OrderAdapter(Context context, List<WorkOrderBo> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, WorkOrderBo item, int position) {
            holder.setText(R.id.tv_item_orderList_orderNum, item.getShopOrder());
            holder.setText(R.id.tv_item_orderList_styleNum, item.getPlannedItem());
            holder.setText(R.id.tv_item_orderList_startTime, item.getPlannedStartDate());
            holder.setText(R.id.tv_item_orderList_endNum, item.getPlannedCompDate());
            holder.setText(R.id.tv_item_orderList_amount, item.getQtyToBuild() + "");

            TextView tv_status = holder.getView(R.id.tv_item_orderList_status);
            if (mType == TYPE_DONE) {
                tv_status.setText("拉布记录");
                tv_status.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RecordLabuDialog(mContext, null, WorkOrderListActivity.this).show();
                    }
                });
            } else {
                switch (item.getStatus()) {
                    case 10:
                        tv_status.setText("未调度");
                        break;
                    case 20:
                        tv_status.setText("已调度未制卡");
                        break;
                    case 30:
                        tv_status.setText("已制卡未完成");
                        break;
                    case 40:
                        tv_status.setText("生产完成");
                        break;
                }
                tv_status.setTextColor(getResources().getColor(R.color.text_black_default));
                tv_status.setOnClickListener(null);
            }
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        dismissLoading();
        if (HttpHelper.isSuccess(resultJSON)) {
            mList_done = JSON.parseArray(resultJSON.getJSONObject("result").getJSONArray("shopOrderCompInfos").toString(), WorkOrderBo.class);
            mList_undo = JSON.parseArray(resultJSON.getJSONObject("result").getJSONArray("shopOrderWaitInfos").toString(), WorkOrderBo.class);
            if (mType == TYPE_UNDO) {
                mAdapter.notifyDataSetChanged(mList_undo);
            } else {
                mAdapter.notifyDataSetChanged(mList_done);
            }
        } else {
            toast(resultJSON.getString("message"));
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        super.onFailure(url, code, message);
        dismissLoading();
        toast(message);
    }

    @Override
    public void recordLabuCallback(List<RecordLabuMaterialInfoBo> list_materialInfo, boolean done) {

    }
}
