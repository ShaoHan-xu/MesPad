package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
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
import com.eeka.mespad.bo.SewQCDataBo;
import com.eeka.mespad.bo.UpdateSewNcBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 记录缝制质检部良界面
 * Created by Lenovo on 2017/8/11.
 */
public class RecordSewNCActivity extends BaseActivity {

    private static final int REQUEST_REPAIR = 0;

    private static final String KEY_DATA = "key_data";
    private static final String KEY_SFC = "key_sfc";

    private TextView mTv_orderNum;
    private LinearLayout mLayout_productComponent;
    private LinearLayout mLayout_designComponent;
    private LinearLayout mLayout_NcProcess;
    private List<RecordNCBo> mList_NcCode;
    private NcAdapter mNcAdapter;
    private JSONArray mList_NcProcess;
    private LinearLayout mLayout_selected;

    private String mSFCBo;
    private int mProductPosition, mDesignPosition;
    private int mNcCodePosition;
    private Map<RecordNCBo, JSONObject> mMap_ncCode;

    private List<SewQCDataBo.DesignComponentBean> mList_component;

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
        mLayout_selected = (LinearLayout) findViewById(R.id.layout_recordSewNC_selected);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_NCType);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        mNcAdapter = new NcAdapter(mContext, mList_NcCode, R.layout.gv_item_recordnc, layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mNcAdapter);

        findViewById(R.id.btn_recordSewNC_choseRepairProcess).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_cancel) {
            finish();
        } else if (v.getId() == R.id.btn_recordSewNC_choseRepairProcess) {
            if (mMap_ncCode == null || mMap_ncCode.size() == 0) {
                showErrorDialog("请选择不良代码及对应工序");
                return;
            }
            List<UpdateSewNcBo.NcCodeOperationListBean> list = new ArrayList<>();
            Set<Map.Entry<RecordNCBo, JSONObject>> entries = mMap_ncCode.entrySet();
            for (Map.Entry<RecordNCBo, JSONObject> next : entries) {
                RecordNCBo key = next.getKey();
                JSONObject value = next.getValue();

                UpdateSewNcBo.NcCodeOperationListBean item = new UpdateSewNcBo.NcCodeOperationListBean();
                item.setNcCodeRef(key.getNC_CODE_BO());
                item.setOperation(value.getString("OPERATION"));
                list.add(item);

            }
            startActivityForResult(RepairActivity.getIntent(mContext, mSFCBo, mList_component, list), REQUEST_REPAIR);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mNcCodePosition = -1;
        mMap_ncCode = new LinkedHashMap<>();
        String sfc = getIntent().getStringExtra(KEY_SFC);
        mTv_orderNum.setText(sfc);
        mSFCBo = "SFCBO:" + SpUtil.getSite() + "," + sfc;
        mList_component = (List<SewQCDataBo.DesignComponentBean>) getIntent().getSerializableExtra(KEY_DATA);
        if (mList_component == null) {
            showErrorDialog("数据异常");
            return;
        }

        for (int i = 0; i < mList_component.size(); i++) {
            SewQCDataBo.DesignComponentBean component = mList_component.get(i);
            mLayout_productComponent.addView(getTabView(component, i));
            if (i == 0) {
                refreshDesignComponentView(component, 0);
            }
        }
        if (mList_component.size() != 0) {
            refreshTab(mLayout_productComponent, 0);
        }
    }

    /**
     * 刷新设计部件布局
     */
    private void refreshDesignComponentView(SewQCDataBo.DesignComponentBean component, int position) {
        mLayout_designComponent.removeAllViews();
        List<SewQCDataBo.DesignComponentBean.DesgComponentsBean> desgComponents = component.getDesgComponents();
        if (desgComponents != null && desgComponents.size() != 0) {
            for (int i = 0; i < desgComponents.size(); i++) {
                SewQCDataBo.DesignComponentBean.DesgComponentsBean bean = desgComponents.get(i);
                mLayout_designComponent.addView(getTabView(bean, i));
                if (i == 0) {
                    mDesignPosition = i;
                    showLoading();
                    HttpHelper.getSewNcCodeList(bean.getName(), RecordSewNCActivity.this);
                }
            }
            refreshTab(mLayout_designComponent, position);
        }
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
            if (position == mNcCodePosition) {
                tv_type.setBackgroundResource(R.color.divider_gray);
            }

            tv_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNcCodePosition = position;
                    notifyItemChanged(position);
                    SewQCDataBo.DesignComponentBean.DesgComponentsBean desgComponentsBean = mList_component.get(mProductPosition).getDesgComponents().get(mDesignPosition);
                    showLoading();
                    HttpHelper.getProcessWithNcCode(desgComponentsBean.getName(), mSFCBo, item.getNC_CODE_BO(), RecordSewNCActivity.this);
                }
            });
        }
    }

    /**
     * 获取导航标签布局
     */
    private <T> View getTabView(T data, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_tab, null);
        TextView tv_tabName = (TextView) view.findViewById(R.id.textView);
        tv_tabName.setPadding(10, 10, 10, 10);
        if (data instanceof SewQCDataBo.DesignComponentBean) {//生产部件
            final SewQCDataBo.DesignComponentBean item = (SewQCDataBo.DesignComponentBean) data;
            tv_tabName.setText(item.getDescription());
            tv_tabName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProductPosition = position;
                    refreshDesignComponentView(item, position);
                }
            });
        } else if (data instanceof SewQCDataBo.DesignComponentBean.DesgComponentsBean) {//设计部件
            final SewQCDataBo.DesignComponentBean.DesgComponentsBean item = (SewQCDataBo.DesignComponentBean.DesgComponentsBean) data;
            tv_tabName.setText(item.getDescription());
            tv_tabName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDesignPosition = position;
                    showLoading();
                    HttpHelper.getSewNcCodeList(item.getName(), RecordSewNCActivity.this);
                    refreshTab(mLayout_designComponent, position);
                }
            });
        }
        return view;
    }

    /**
     * 刷新标签视图
     */
    private void refreshTab(ViewGroup parent, int position) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            childAt.findViewById(R.id.textView).setEnabled(true);
        }
        parent.getChildAt(position).findViewById(R.id.textView).setEnabled(false);
    }

    /**
     * 获取不良工序布局
     */
    private View getNcProcessView(final JSONObject item) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gv_item_recordnc, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_recordNc_type);
        textView.setText(item.getString("DESCRIPTION"));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordNCBo recordNCBo = mList_NcCode.get(mNcCodePosition);
                mMap_ncCode.put(recordNCBo, item);
                refreshSelectedView();
            }
        });
        return view;
    }

    /**
     * 刷新已选择不良代码及工序的布局
     */
    private void refreshSelectedView() {
        mLayout_selected.removeAllViews();
        Set<RecordNCBo> recordNCBos = mMap_ncCode.keySet();
        for (final RecordNCBo item : recordNCBos) {
            JSONObject json = mMap_ncCode.get(item);
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sewnc_selected, null);
            TextView tv_code = (TextView) view.findViewById(R.id.tv_recordSewNC_selected_code);
            TextView tv_process = (TextView) view.findViewById(R.id.tv_recordSewNC_selected_process);
            tv_code.setText(item.getDESCRIPTION());
            tv_process.setText(json.getString("DESCRIPTION"));
            mLayout_selected.addView(view);

            view.findViewById(R.id.btn_recordSewNC_delSelected).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap_ncCode.remove(item);
                    refreshSelectedView();
                }
            });
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getSewNcCodeList.equals(url)) {
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

    /**
     * @param components 生产部件里面，里面包含设计部件
     */
    public static Intent getIntent(Context context, String sfc, List<SewQCDataBo.DesignComponentBean> components) {
        Intent intent = new Intent(context, RecordSewNCActivity.class);
        intent.putExtra(KEY_SFC, sfc);
        intent.putExtra(KEY_DATA, (Serializable) components);
        return intent;
    }
}
