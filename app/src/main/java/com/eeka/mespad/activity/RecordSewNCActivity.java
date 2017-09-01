package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
import com.eeka.mespad.utils.TabViewUtil;

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
            final SewQCDataBo.DesignComponentBean component = mList_component.get(i);
            final int finalI = i;
            mLayout_productComponent.addView(TabViewUtil.getTabView(mContext, component, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNcCodePosition = -1;
                    mProductPosition = finalI;
                    refreshDesignComponentView(component);
                    TabViewUtil.refreshTabView(mLayout_productComponent, finalI);
                }
            }));
            if (i == 0) {
                refreshDesignComponentView(component);
            }
        }
        if (mList_component.size() != 0) {
            TabViewUtil.refreshTabView(mLayout_productComponent, 0);
        }
    }

    /**
     * 刷新设计部件布局
     */
    private void refreshDesignComponentView(SewQCDataBo.DesignComponentBean component) {
        mLayout_designComponent.removeAllViews();
        List<SewQCDataBo.DesignComponentBean.DesgComponentsBean> desgComponents = component.getDesgComponents();
        if (desgComponents != null && desgComponents.size() != 0) {
            for (int i = 0; i < desgComponents.size(); i++) {
                final SewQCDataBo.DesignComponentBean.DesgComponentsBean bean = desgComponents.get(i);
                final int finalI = i;
                mLayout_designComponent.addView(TabViewUtil.getTabView(mContext, bean, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNcCodePosition = -1;
                        mDesignPosition = finalI;
                        mLayout_NcProcess.removeAllViews();
                        showLoading();
                        HttpHelper.getSewNcCodeList(bean.getName(), RecordSewNCActivity.this);
                        TabViewUtil.refreshTabView(mLayout_designComponent, finalI);
                    }
                }));
                if (i == 0) {
                    mDesignPosition = 0;
                    mLayout_NcProcess.removeAllViews();
                    showLoading();
                    HttpHelper.getSewNcCodeList(bean.getName(), RecordSewNCActivity.this);
                }
            }
            TabViewUtil.refreshTabView(mLayout_designComponent, 0);
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
            } else {
                tv_type.setBackgroundResource(R.color.white);
            }

            tv_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNcCodePosition = position;
                    notifyDataSetChanged();
                    SewQCDataBo.DesignComponentBean.DesgComponentsBean desgComponentsBean = mList_component.get(mProductPosition).getDesgComponents().get(mDesignPosition);
                    showLoading();
                    HttpHelper.getProcessWithNcCode(desgComponentsBean.getName(), mSFCBo, item.getNC_CODE_BO(), RecordSewNCActivity.this);
                }
            });
        }
    }

    /**
     * 获取不良工序布局
     */
    private View getNcProcessView(final JSONObject item, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gv_item_recordnc, null);
        view.findViewById(R.id.btn_recordNc_sub).setVisibility(View.GONE);
        TextView textView = (TextView) view.findViewById(R.id.tv_recordNc_type);
        textView.setBackgroundResource(0);
        textView.setText(item.getString("DESCRIPTION"));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordNCBo recordNCBo = mList_NcCode.get(mNcCodePosition);
                mMap_ncCode.put(recordNCBo, item);
                refreshNcProcessView(position);
                refreshSelectedView();
            }
        });
        return view;
    }

    private void refreshNcProcessView(int position) {
        int childCount = mLayout_NcProcess.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mLayout_NcProcess.getChildAt(i);
            childAt.findViewById(R.id.tv_recordNc_type).setBackgroundResource(R.color.white);
        }
        mLayout_NcProcess.getChildAt(position).findViewById(R.id.tv_recordNc_type).setBackgroundResource(R.color.divider_gray);
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
                mNcAdapter.setData(mList_NcCode);
                mNcAdapter.notifyDataSetChanged();
            } else if (HttpHelper.getProcessWithNcCode.equals(url)) {
                mList_NcProcess = resultJSON.getJSONArray("result");
                mLayout_NcProcess.removeAllViews();
                for (int i = 0; i < mList_NcProcess.size(); i++) {
                    mLayout_NcProcess.addView(getNcProcessView(mList_NcProcess.getJSONObject(i), i));
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
