package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.SewQCDataBo;
import com.eeka.mespad.bo.UpdateSewNcBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 选择返修工序界面
 * Created by Lenovo on 2017/8/11.
 */

public class RepairActivity extends BaseActivity {

    private static final String KEY_SFCBO = "key_sfcBo";
    private static final String KEY_DATA = "key_data";
    private static final String KEY_SELECTED = "key_selected";

    private LinearLayout mLayout_component;
    private List<SewQCDataBo.DesignComponentBean> mList_component;
    private RecyclerView mRecyclerView;
    private List<JSONObject> mList_type;
    private NcAdapter mAdapter;

    private String mSFCBO;

    private int mProductIndex;
    private List<JSONObject> mList_selected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_repair);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mLayout_component = (LinearLayout) findViewById(R.id.layout_repair_component);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_repairType);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        mRecyclerView.setLayoutManager(layoutManager);
        mList_type = new ArrayList<>();
        mAdapter = new NcAdapter(mContext, mList_type, R.layout.gv_item_recordnc, layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        findViewById(R.id.btn_done).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mList_selected = new LinkedList<>();
        mSFCBO = getIntent().getStringExtra(KEY_SFCBO);
        mList_component = (List<SewQCDataBo.DesignComponentBean>) getIntent().getSerializableExtra(KEY_DATA);
        if (mList_component != null && mList_component.size() != 0) {
            for (int i = 0; i < mList_component.size(); i++) {
                mLayout_component.addView(getTabView(mList_component.get(i), i));
            }
            refreshTab(0);
        } else {
            showErrorDialog("数据错误");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_done) {
            done();
        } else if (v.getId() == R.id.btn_cancel) {
            finish();
        }
    }

    private void done() {
        UpdateSewNcBo data = new UpdateSewNcBo();
        data.setSfcRef(mSFCBO);
        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        if (resource != null) {
            data.setResourceRef(resource.getRESOURCE_BO());
        }
        List<UpdateSewNcBo.NcCodeOperationListBean> list = (List<UpdateSewNcBo.NcCodeOperationListBean>) getIntent().getSerializableExtra(KEY_SELECTED);
        data.setNcCodeOperationList(list);
        int containsCount = 0;
        List<UpdateSewNcBo.ReworkOperationListBean> process = new ArrayList<>();
        for (int i = 0; i < mList_selected.size(); i++) {
            JSONObject json = mList_selected.get(i);
            UpdateSewNcBo.ReworkOperationListBean item = new UpdateSewNcBo.ReworkOperationListBean();
            item.setSequence(i + 1);
            String operation = json.getString("OPERATION");
            item.setReworkOperation(operation);
            item.setOperationDesc(json.getString("DESCRIPTION"));
            item.setPartId(json.getString("partId"));
            process.add(item);
            if (!isEmpty(operation)) {
                for (UpdateSewNcBo.NcCodeOperationListBean bean : list) {
                    if (operation.equals(bean.getOperation())) {
                        containsCount++;
                    }
                }
            }
        }
        if (containsCount >= list.size()) {
            data.setReworkOperationList(process);
            showLoading();
            HttpHelper.recordSewNc(data, this);
        } else {
            showErrorDialog("返修工序需要包含所有被记录不良的工序");
        }
    }

    /**
     * 获取标签布局
     */
    private View getTabView(final SewQCDataBo.DesignComponentBean component, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_tab, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        TextView tv_tabName = (TextView) view.findViewById(R.id.textView);
        tv_tabName.setGravity(Gravity.CENTER);
        tv_tabName.setText(component.getDescription());
        tv_tabName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTab(position);
            }
        });
        return view;
    }

    private void refreshTab(int position) {
        mProductIndex = position;
        int childCount = mLayout_component.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mLayout_component.getChildAt(i);
            childAt.setBackgroundResource(R.color.white);
        }
        mLayout_component.getChildAt(position).setBackgroundResource(R.color.divider_gray);

        showLoading();
        SewQCDataBo.DesignComponentBean component = mList_component.get(position);
        HttpHelper.getRepairProcess(component.getName(), mSFCBO, RepairActivity.this);
    }

    private class NcAdapter extends CommonRecyclerAdapter<JSONObject> {

        NcAdapter(Context context, List<JSONObject> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, final JSONObject item, final int position) {
            holder.getView(R.id.btn_recordNc_sub).setVisibility(View.GONE);
            TextView tv_type = holder.getView(R.id.tv_recordNc_type);
            TextView tv_index = holder.getView(R.id.tv_recordNc_index);
            tv_index.setVisibility(View.GONE);
            tv_type.setText(item.getString("DESCRIPTION"));
            String itemBo = item.getString("OPERATION_BO");
            if (!isEmpty(itemBo)) {
                for (int i = 0; i < mList_selected.size(); i++) {
                    JSONObject json = mList_selected.get(i);
                    if (itemBo.equals(json.getString("OPERATION_BO"))) {
                        tv_index.setVisibility(View.VISIBLE);
                        tv_index.setText(String.valueOf(i + 1));
                    }
                }
            }

            tv_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemBo = item.getString("OPERATION_BO");
                    if (!isEmpty(itemBo)) {
                        for (JSONObject json : mList_selected) {
                            if (itemBo.equals(json.getString("OPERATION_BO"))) {
                                mList_selected.remove(item);
                                mAdapter.notifyDataSetChanged();
                                return;
                            }
                        }
                        item.put("partId", mList_component.get(mProductIndex).getName());
                        mList_selected.add(item);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getRepairProcess.equals(url)) {
                mList_type = JSON.parseArray(resultJSON.getJSONArray("result").toString(), JSONObject.class);
                mAdapter.setData(mList_type);
                mAdapter.notifyDataSetChanged();
            } else if (HttpHelper.recordSewNc.equals(url)) {
                toast("记录成功");
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }
        }
    }

    public static Intent getIntent(Context context, String sfcBo, List<SewQCDataBo.DesignComponentBean> components, List<UpdateSewNcBo.NcCodeOperationListBean> selected) {
        Intent intent = new Intent(context, RepairActivity.class);
        intent.putExtra(KEY_SFCBO, sfcBo);
        intent.putExtra(KEY_DATA, (Serializable) components);
        intent.putExtra(KEY_SELECTED, (Serializable) selected);
        return intent;
    }
}
