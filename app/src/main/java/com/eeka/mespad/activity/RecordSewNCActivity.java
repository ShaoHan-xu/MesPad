package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.RecordNCBo;
import com.eeka.mespad.bo.SewQCDataBo;
import com.eeka.mespad.bo.UpdateSewNcBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.dragRecyclerViewHelper.RecyclerListAdapter;
import com.eeka.mespad.dragRecyclerViewHelper.SimpleItemTouchHelperCallback;
import com.eeka.mespad.fragment.QCFragment;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.TabViewUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.RepairSelectorDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录缝制质检不良界面
 * Created by Lenovo on 2017/8/11.
 */
public class RecordSewNCActivity extends BaseActivity {

    private static final int REQUEST_REPAIR = 0;

    private static final String KEY_DATA = "key_data";
    private static final String KEY_SFC = "key_sfc";
    private static final String KEY_TYPE = "key_type";

    private TextView mTv_orderNum;
    private LinearLayout mLayout_productComponent;
    private LinearLayout mLayout_designComponent;
    private LinearLayout mLayout_NcProcess;
    private List<RecordNCBo> mList_NcCode;
    private NcAdapter mNcAdapter;
    private JSONArray mList_NcProcess;

    private String mSFCBo;
    private int mProductPosition, mDesignPosition;
    private int mNcCodePosition;

    private UpdateSewNcBo.NcCodeOperationListBean mCurSelecting;
    private List<UpdateSewNcBo.NcCodeOperationListBean> mList_selected;
    private List<SewQCDataBo.DesignComponentBean> mList_component;

    private int mRecordType;

    private ItemTouchHelper mItemTouchHelper;
    private RecyclerListAdapter mSelectedAdapter;

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
        mTv_orderNum = findViewById(R.id.tv_recordSewNC_workNum);
        mLayout_productComponent = findViewById(R.id.layout_recordSewNC_productComponent);
        mLayout_designComponent = findViewById(R.id.layout_recordSewNC_designComponent);
        mLayout_NcProcess = findViewById(R.id.layout_recordSewNC_NcProcess);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_NCType);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        mNcAdapter = new NcAdapter(mContext, mList_NcCode, R.layout.gv_item_recordnc, layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mNcAdapter);

        mList_selected = new ArrayList<>();
        mSelectedAdapter = new RecyclerListAdapter(mList_selected, new RecyclerListAdapter.OnDragStartListener() {
            @Override
            public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
//                开始拖拽了
                mItemTouchHelper.startDrag(viewHolder);
            }
        });
        RecyclerView selected = findViewById(R.id.recyclerView_recordSewNC_selected);
        selected.setHasFixedSize(true);
        selected.setAdapter(mSelectedAdapter);
        selected.setLayoutManager(new LinearLayoutManager(mContext));

        /*
          ★★★★★★★★★★★★★★★★★★★★★★★
          要使用ItemTouchHelper，你需要创建一个ItemTouchHelper.Callback。
          这个接口可以让你监听“move(上下移动)”与 “swipe（左右滑动）”事件。这里还是
          ★控制view被选中
          的状态以及★重写默认动画的地方。

          如果你只是想要一个基本的实现，有一个
          帮助类可以使用：SimpleCallback,但是为了了解其工作机制，我们还是自己实现。
         */
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mSelectedAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
//        将定义好的mItemTouchHelper应用于我们的recyclerView，使得recyclerView获得move和swipe的效果
        mItemTouchHelper.attachToRecyclerView(selected);

        mRecordType = getIntent().getIntExtra(KEY_TYPE, 0);
        Button btn_nextStep = findViewById(R.id.btn_recordSewNC_done);
        btn_nextStep.setOnClickListener(this);
//        if (mRecordType == QCFragment.TYPE_QA) {
//            btn_nextStep.setText("保存");
//        }
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_cancel) {
            finish();
        } else if (v.getId() == R.id.btn_recordSewNC_done) {
            if (mList_selected == null || mList_selected.size() == 0) {
                showErrorDialog("请选择不良代码及对应工序");
                return;
            }
//            if (mRecordType == QCFragment.TYPE_QA) {
            done();
//            } else {
//                startActivityForResult(RepairActivity.getIntent(mContext, mSFCBo, mList_component, mList_selected), REQUEST_REPAIR);
//            }
        }
    }

    private void done() {
        showLoading();
        UpdateSewNcBo data = new UpdateSewNcBo();
        data.setSfcRef(mSFCBo);
        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        if (resource != null) {
            data.setResourceRef(resource.getRESOURCE_BO());
        }
        if (mRecordType == QCFragment.TYPE_QA) {
            UpdateSewNcBo.NcCodeOperationListBean bean = new UpdateSewNcBo.NcCodeOperationListBean();
            String ncCodeBo = "NCCodeBO:" + SpUtil.getSite() + ",NC2QC";
            bean.setNcCodeRef(ncCodeBo);
            bean.setOperation(mList_selected.get(0).getOperation());
            mList_selected.add(bean);
        } else {
            List<UpdateSewNcBo.ReworkOperationListBean> process = new ArrayList<>();
            for (int i = 0; i < mList_selected.size(); i++) {
                UpdateSewNcBo.NcCodeOperationListBean bean = mList_selected.get(i);
                UpdateSewNcBo.ReworkOperationListBean item = new UpdateSewNcBo.ReworkOperationListBean();
                item.setSequence(i + 1);
                item.setReworkOperation(bean.getOperation());
                item.setOperationDesc(bean.getProcessDesc());
                item.setPartId(bean.getPROD_COMPONENT());
                item.setResourceNo(bean.getResourceNo());
                process.add(item);
            }
            data.setReworkOperationList(process);
        }
        data.setNcCodeOperationList(mList_selected);

        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers != null && loginUsers.size() != 0) {
            String userId = loginUsers.get(0).getUSER();
            data.setUserId(userId);
        }
        HttpHelper.recordSewNc(data, this);
    }

    @Override
    protected void initData() {
        super.initData();
        mNcCodePosition = -1;
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
            holder.getView(R.id.btn_recordNc_sub).setVisibility(View.GONE);
            TextView tv_type = holder.getView(R.id.tv_recordNc_type);
            tv_type.setText(item.getDESCRIPTION());

            TextView tv_code = holder.getView(R.id.tv_recordNc_code);
            tv_code.setVisibility(View.VISIBLE);
            tv_code.setText(item.getNC_CODE());

            holder.getView(R.id.layout_recordNc_type).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNcCodePosition = position;
                    SewQCDataBo.DesignComponentBean productComponent = mList_component.get(mProductPosition);
                    SewQCDataBo.DesignComponentBean.DesgComponentsBean desgComponentsBean = productComponent.getDesgComponents().get(mDesignPosition);
                    showLoading();
                    HttpHelper.getProcessWithNcCode(productComponent.getName(), desgComponentsBean.getName(), mSFCBo, item.getNC_CODE_BO(), RecordSewNCActivity.this);
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
                final RecordNCBo recordNCBo = mList_NcCode.get(mNcCodePosition);
                new RepairSelectorDialog(mContext, recordNCBo.getDESCRIPTION(), mList_NcProcess, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        JSONObject item = mList_NcProcess.getJSONObject(position);
                        for (UpdateSewNcBo.NcCodeOperationListBean selected : mList_selected) {
                            if (selected.getOperation().equals(item.getString("OPERATION"))) {
                                ErrorDialog.showAlert(mContext, "该工序已记录过不良，无法多次记录");
                                return;
                            }
                        }
                        mCurSelecting = new UpdateSewNcBo.NcCodeOperationListBean();
                        mCurSelecting.setPROD_COMPONENT(item.getString("PART_ID"));
                        mCurSelecting.setResourceNo(item.getString("RESOURCE"));
                        mCurSelecting.setNC_CODE(recordNCBo.getNC_CODE());
                        mCurSelecting.setNcCodeRef(recordNCBo.getNC_CODE_BO());
                        mCurSelecting.setDESCRIPTION(recordNCBo.getDESCRIPTION());
                        mCurSelecting.setOperation(item.getString("OPERATION"));
                        mCurSelecting.setProcessDesc(item.getString("DESCRIPTION"));

                        mSelectedAdapter.addItem(mCurSelecting);
                    }
                }).show();
            } else if (HttpHelper.recordSewNc.equals(url)) {
                toast("保存成功");
                finish();
            }
        }
    }

    /**
     * @param components 生产部件里面，里面包含设计部件
     * @param type       0=QC,1=QA
     */
    public static Intent getIntent(Context context, int type, String sfc, List<SewQCDataBo.DesignComponentBean> components) {
        Intent intent = new Intent(context, RecordSewNCActivity.class);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_SFC, sfc);
        intent.putExtra(KEY_DATA, (Serializable) components);
        return intent;
    }
}
