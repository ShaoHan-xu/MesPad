package com.eeka.mespad.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.RecordNCBo;
import com.eeka.mespad.http.HttpHelper;

import java.util.List;

/**
 * 记录缝制质检部良界面
 * Created by Lenovo on 2017/8/11.
 */
public class RecordSewNCActivity extends BaseActivity {

    private static final String KEY_PRODUCT_COMPONENT = "PROD_COMPONENT";
    private static final String KEY_PROD_COMPONENT_DESC = "PROD_COMPONENT_DESC";
    private static final String KEY_DESIGN_PRODUCT_ID = "DESIGN_PRODUCT_ID";

    private TextView mTv_orderNum;
    private LinearLayout mLayout_productComponent;
    private JSONArray mList_productComponent;
    private LinearLayout mLayout_designComponent;
    private JSONArray mList_designComponent;
    private LinearLayout mLayout_NcProcess;
    private List<RecordNCBo> mList_NcCode;
    private NcAdapter mNcAdapter;
    private JSONArray mList_NcProcess;

    private String mSFCBo;
    private int mProductPosition, mDesignPosition, mNcCodePosition, mNcProcessPosition;

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
        mLayout_productComponent = (LinearLayout) findViewById(R.id.layout_recordSewNC_productComponent);
        mLayout_designComponent = (LinearLayout) findViewById(R.id.layout_recordSewNC_designComponent);
        mLayout_NcProcess = (LinearLayout) findViewById(R.id.layout_recordSewNC_NcProcess);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_NCType);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        mNcAdapter = new NcAdapter(mContext, mList_NcCode, R.layout.gv_item_recordnc, layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mNcAdapter);

        findViewById(R.id.btn_recordSewNC_choseRepairProcess).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mSFCBo = "";
        showLoading();
        HttpHelper.getProductComponentList(mSFCBo, this);
    }

    private class NcAdapter extends CommonRecyclerAdapter<RecordNCBo> {

        NcAdapter(Context context, List<RecordNCBo> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, final RecordNCBo item, final int position) {
            TextView tv_type = holder.getView(R.id.tv_recordNc_type);
            tv_type.setText(item.getDESCRIPTION());
            holder.getView(R.id.btn_recordNc_sub).setVisibility(View.GONE);

            tv_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNcCodePosition = position;
                    JSONObject designComponent = mList_designComponent.getJSONObject(mNcCodePosition);
                    showLoading();
                    HttpHelper.getProcessWithNcCode(designComponent.getString(KEY_DESIGN_PRODUCT_ID), mSFCBo, item.getNC_CODE_BO(), RecordSewNCActivity.this);
                }
            });
        }
    }

    /**
     * 获取导航标签布局
     */
    private <T> View getTabView(T data, final int position) {
        final JSONObject item = (JSONObject) data;
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_tab, null);
        TextView tv_tabName = (TextView) view.findViewById(R.id.textView);
        if (item.containsKey(KEY_PRODUCT_COMPONENT)) {//生产部件
            tv_tabName.setText(item.getString(KEY_PROD_COMPONENT_DESC));
            tv_tabName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProductPosition = position;
                    showLoading();
                    HttpHelper.getDesignComponentList(item.getString(KEY_PRODUCT_COMPONENT), "", RecordSewNCActivity.this);
                }
            });
        } else if (item.containsKey(KEY_DESIGN_PRODUCT_ID)) {//设计部件
            tv_tabName.setText(item.getString(KEY_DESIGN_PRODUCT_ID));
            tv_tabName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDesignPosition = position;
                    showLoading();
                    HttpHelper.getSewNcCodeList(item.getString(KEY_DESIGN_PRODUCT_ID), RecordSewNCActivity.this);
                }
            });
        }
        return view;
    }

    /**
     * 刷新标签视图
     *
     * @param position
     */
    private void refreshTab(ViewGroup parent, int position) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            childAt.setBackgroundResource(R.color.white);
        }
        parent.getChildAt(position).setBackgroundResource(R.color.text_gray_default);
    }

    /**
     * 获取不良工序布局
     *
     * @param item
     * @return
     */
    private View getNcProcessView(JSONObject item) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gv_item_recordnc, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_recordNc_type);
        textView.setText(item.getString("DESCRIPTION"));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getProductComponentList.equals(url)) {
                mList_productComponent = resultJSON.getJSONArray("result");
                mLayout_productComponent.removeAllViews();
                for (int i = 0; i < mList_productComponent.size(); i++) {
                    mLayout_productComponent.addView(getTabView(mList_productComponent.getJSONObject(i), i));
                }
                if (mList_designComponent == null && mList_productComponent != null && mList_productComponent.size() != 0) {
                    JSONObject jsonObject = mList_productComponent.getJSONObject(0);
                    HttpHelper.getDesignComponentList(jsonObject.getString(KEY_PRODUCT_COMPONENT), mSFCBo, this);
                }
            } else if (HttpHelper.getDesignComponentList.equals(url)) {
                mList_designComponent = resultJSON.getJSONArray("result");
                mLayout_designComponent.removeAllViews();
                for (int i = 0; i < mList_designComponent.size(); i++) {
                    mLayout_designComponent.addView(getTabView(mList_designComponent.getJSONObject(i), i));
                }
                if (mList_NcCode == null && mList_designComponent != null && mList_designComponent.size() != 0) {
                    JSONObject jsonObject = mList_designComponent.getJSONObject(0);
                    HttpHelper.getSewNcCodeList(jsonObject.getString(KEY_DESIGN_PRODUCT_ID), this);
                }
            } else if (HttpHelper.getSewNcCodeList.equals(url)) {
                mList_NcCode = JSON.parseArray(resultJSON.getJSONArray("result").toString(), RecordNCBo.class);
                mNcAdapter.notifyDataSetChanged();
            } else if (HttpHelper.getProcessWithNcCode.equals(url)) {
                mList_NcProcess = resultJSON.getJSONArray("result");
                mLayout_NcProcess.removeAllViews();
                for (int i = 0; i < mList_NcProcess.size(); i++) {
                    mLayout_NcProcess.addView(getNcProcessView(mList_NcProcess.getJSONObject(i)));
                }
            }
        }
    }
}
