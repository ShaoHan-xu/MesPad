package com.eeka.mespad.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.BatchCutOrderListBo;
import com.eeka.mespad.bo.BatchCutRecordBo;
import com.eeka.mespad.bo.BatchLabuRecordPrintBo;
import com.eeka.mespad.bo.DictionaryDataBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TopicUtil;
import com.eeka.mespad.utils.UnitUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.widget.MyPopWindow;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 大货裁剪段里面界面/即首页
 */
@SuppressLint("InflateParams")
public class BatchOrderListActivity extends NFCActivity {

    private static final int REQUEST_DETAIL = 0;

    private RelativeLayout mLayout_workCenter;
    private TextView mTv_loginUser;
    private TextView mTv_workCenter;
    private EditText mEt_shopOrder;
    private EditText mEt_item;

    private ItemAdapter mItemAdapter;
    private List<BatchCutOrderListBo> mList_data;
    private List<DictionaryDataBo> mList_type;
    private List<DictionaryDataBo> mList_fzWorkCenter;
    private List<DictionaryDataBo> mList_cjWorkCenter;
    private List<DictionaryDataBo> mList_type_checked;
    private List<DictionaryDataBo> mList_fzWorkCenter_checked;
    private List<DictionaryDataBo> mList_cjWorkCenter_checked;
    private DictionaryDataBo mWorkCenter_fzAll;
    private DictionaryDataBo mWorkCenter_cjAll;

    private RecyclerView.LayoutParams mItemParams;

    private List<String> mTypeCondition;
    private List<String> mFZWorkCenterCondition;
    private List<String> mCJWorkCenterCondition;

    private PositionInfoBo.OPERINFORBean mOperation;

    private int mActionIndex;

    private String mRFID;

    private RelativeLayout mLayout_setSystem;
    private TextView mTv_systemCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_batchcut);

        mOperation = (PositionInfoBo.OPERINFORBean) getIntent().getSerializableExtra("operation");

        initView();
        initData();
        search();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        super.initView();

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(mOperation.getDESCRIPTION());

        TextView tv_version = findViewById(R.id.tv_version);
        if (SystemUtils.isApkInDebug(mContext)) {
            tv_version.setOnClickListener(this);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.app_channel)).append("_");
        sb.append(SystemUtils.getAppVersionName(mContext));
        if (SystemUtils.isApkInDebug(mContext)) {
            sb.append("_debug");
        }
        tv_version.setText(sb.toString());

        if (SystemUtils.isApkInDebug(mContext)) {
            mLayout_setSystem = findViewById(R.id.layout_setSystem);
            mLayout_setSystem.setOnClickListener(this);
            mTv_systemCode = findViewById(R.id.tv_systemCode);
            String systemCode = SpUtil.get(SpUtil.KEY_SYSTEMCODE, null);
            if (!TextUtils.isEmpty(systemCode)) {
                mTv_systemCode.setText(systemCode);
            } else {
                mTv_systemCode.setText(getString(R.string.system_code));
            }
        }

        mItemParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mItemParams.bottomMargin = 12;

        mList_data = new ArrayList<>();
        mItemAdapter = new ItemAdapter(mContext, mList_data, R.layout.item_orderlist_batchcut, null);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_batchCut_orderList);
        recyclerView.setAdapter(mItemAdapter);

        mTv_workCenter = findViewById(R.id.tv_batchCut_workCenter);
        mEt_shopOrder = findViewById(R.id.et_batchCut_shopOrder);
        mEt_item = findViewById(R.id.et_batchCut_item);

        mLayout_workCenter = findViewById(R.id.layout_batchCut_workCenter);

        mTv_loginUser = findViewById(R.id.tv_batchCut_loginUser);
        mTv_loginUser.setOnClickListener(this);
        findViewById(R.id.iv_batchCut_login).setOnClickListener(this);
        mLayout_workCenter.setOnClickListener(this);
        findViewById(R.id.btn_batchCut_search).setOnClickListener(this);
        findViewById(R.id.btn_batchCut_scan).setOnClickListener(this);
        findViewById(R.id.tv_checkUpdate).setOnClickListener(this);

        refreshLoginUser();
    }

    @Override
    protected void initData() {
        super.initData();
        mTopic = TopicUtil.TOPIC_CUT;
        mList_type_checked = new ArrayList<>();
        mList_fzWorkCenter_checked = new ArrayList<>();
        mList_cjWorkCenter_checked = new ArrayList<>();
        mWorkCenter_fzAll = new DictionaryDataBo("所有缝制中心", "*");
        mWorkCenter_cjAll = new DictionaryDataBo("所有裁剪中心", "*");
    }

    private void search() {
        showLoading();
        String shopOrder = mEt_shopOrder.getText().toString();
        String item = mEt_item.getText().toString();
        HttpHelper.getBatchCutOrderList(shopOrder, item, mOperation.getOPERATION(), mTypeCondition, mFZWorkCenterCondition, mCJWorkCenterCondition, this);
    }

    private long mLastMillis;

    @Override
    public void onBackPressed() {
        long curMillis = System.currentTimeMillis();
        if (curMillis - mLastMillis <= 2000) {
            finish();
            System.exit(0);
        } else {
            mLastMillis = curMillis;
            toast("再按一次返回键退出应用");
        }
    }

    /**
     * 收到推送消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushMsgReceive(PushJson push) {
        String type = push.getType();
        if (PushJson.TYPE_RFID.equals(type)) {
            mRFID = push.getContent();
//            mEt_shopOrder.setText(mRFID);
            showLoading();
            HttpHelper.getBatchCardInfo(mRFID, this);
        } else if (PushJson.TYPE_SCAN.equals(type)) {
            String content = push.getContent();
            BatchLabuRecordPrintBo printBo = JSON.parseObject(content, BatchLabuRecordPrintBo.class);
            BatchCutRecordBo data = new BatchCutRecordBo();
            data.setRabNo(printBo.getRabOrder());
            data.setMaterialType(printBo.getMatType());
            Intent intent = BatchCutWorkingActivity.getIntent(mContext, data, mOperation);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_batchCut_login:
                showLoginDialog();
                break;
            case R.id.tv_batchCut_loginUser:
                showLogoutDialog();
                break;
            case R.id.layout_batchCut_workCenter:
                if (mList_cjWorkCenter == null || mList_fzWorkCenter == null) {
                    showLoading();
                    HttpHelper.getLabuWorkCenter(this);
                    return;
                }
                showWorkCenterSelector();
                break;
            case R.id.btn_workCenterSelector_ok:
                getWorkCenterCondition();
                mPPW_workCenter.dismiss();
                break;
            case R.id.btn_batchCut_search:
                search();
                break;
            case R.id.btn_batchCut_scan:
                startScan(true);
                break;
            case R.id.tv_checkUpdate:
                Beta.checkUpgrade();
                break;
            case R.id.tv_version:
                openSystemEnvironment();
                break;
            case R.id.layout_setSystem:
                setSystemCode();
                break;
        }
    }

    /**
     * 设置系统环境
     */
    private void setSystemCode() {
        int checked = -1;
        String s = mTv_systemCode.getText().toString();
        if (!isEmpty(s)) {
            switch (s) {
                case "D":
                    checked = 0;
                    break;
                case "Q":
                    checked = 1;
                    break;
                case "P":
                    checked = 2;
                    break;
                case "LH_P":
                    checked = 3;
                    break;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请选择系统环境");
        final int finalChecked = checked;
        builder.setSingleChoiceItems(R.array.systemCode, checked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which != finalChecked) {
                    dialog.dismiss();
                    if (which == 0) {
                        SpUtil.save(SpUtil.KEY_SYSTEMCODE, "D");
                        PadApplication.BASE_URL = PadApplication.HOST_D + "/eeka-mes/";
                    } else if (which == 1) {
                        SpUtil.save(SpUtil.KEY_SYSTEMCODE, "Q");
                        PadApplication.BASE_URL = PadApplication.HOST_Q + "/eeka-mes/";
                    } else if (which == 2) {
                        SpUtil.save(SpUtil.KEY_SYSTEMCODE, "P");
                        PadApplication.BASE_URL = PadApplication.HOST_P + "/eeka-mes/";
                    } else if (which == 3) {
                        SpUtil.save(SpUtil.KEY_SYSTEMCODE, "LH_P");
                        PadApplication.BASE_URL = PadApplication.HOST_P_LH + "/eeka-mes/";
                    }
                    ErrorDialog.showConfirmAlert(mContext, "系统切换成功，重启应用后生效。", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            System.exit(0);
                        }
                    });
                }
            }
        });

        builder.show();
    }

    private int mClickCount;

    /**
     * 显示系统环境设置的布局
     */
    private void openSystemEnvironment() {
        long curMillis = System.currentTimeMillis();
        if (mLastMillis == 0) {
            mLastMillis = curMillis;
            mClickCount++;
            return;
        }
        if (curMillis - mLastMillis < 1000) {
            mLastMillis = curMillis;
            mClickCount++;
        } else {
            mLastMillis = curMillis;
            mClickCount = 1;
        }
        if (mClickCount == 5) {
            mLayout_setSystem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClockIn(boolean success) {
        if (success) {
            if (mLoginDialog != null)
                mLoginDialog.dismiss();
            refreshLoginUser();
        }
    }

    private void getWorkCenterCondition() {
        StringBuilder stringBuilder = new StringBuilder();

        mTypeCondition = new ArrayList<>();
        int typeSize = mList_type_checked.size();
        for (int i = 0; i < typeSize; i++) {
            DictionaryDataBo item = mList_type_checked.get(i);
            stringBuilder.append(item.getLABEL()).append("、");
            mTypeCondition.add(item.getVALUE());
        }

        mFZWorkCenterCondition = new ArrayList<>();
        int fzSize = mList_fzWorkCenter_checked.size();
        for (int i = 0; i < fzSize; i++) {
            DictionaryDataBo item = mList_fzWorkCenter_checked.get(i);
            stringBuilder.append(item.getLABEL()).append("、");
            mFZWorkCenterCondition.add(item.getVALUE());
        }

        mCJWorkCenterCondition = new ArrayList<>();
        int cjSize = mList_cjWorkCenter_checked.size();
        for (int i = 0; i < cjSize; i++) {
            DictionaryDataBo item = mList_cjWorkCenter_checked.get(i);
            stringBuilder.append(item.getLABEL()).append("、");
            mCJWorkCenterCondition.add(item.getVALUE());
        }

        String s = stringBuilder.toString();
        if (!isEmpty(s)) {
            s = s.substring(0, s.length() - 1);
        }
        mTv_workCenter.setText(s);
    }

    private MyPopWindow mPPW_workCenter;
    private LinearLayout mLayout_fz;
    private LinearLayout mLayout_cj;
    private OnCheckboxListener mCheckboxListener_type;
    private OnCheckboxListener mCheckboxListener_fz;
    private OnCheckboxListener mCheckboxListener_cj;
    private CheckBox mCkb_fzAll;
    private CheckBox mCkb_cjAll;

    private void showWorkCenterSelector() {
        if (mPPW_workCenter == null) {
            mCheckboxListener_type = new OnCheckboxListener(CheckboxListenerType.TYPE);
            mCheckboxListener_fz = new OnCheckboxListener(CheckboxListenerType.FZ);
            mCheckboxListener_cj = new OnCheckboxListener(CheckboxListenerType.CJ);
            View view = LayoutInflater.from(mContext).inflate(R.layout.ppw_workcenter_selector, null);

            RecyclerView mRecycler_type = view.findViewById(R.id.recyclerView_workCenterSelector_type);
            TypeAdapter mTypeAdapter = new TypeAdapter(mContext, mList_type, R.layout.layout_checkbox_orange, null);
            mRecycler_type.setAdapter(mTypeAdapter);

            mCkb_fzAll = view.findViewById(R.id.checkbox_allFZ);
            mCkb_fzAll.setTag(mWorkCenter_fzAll);
            mCkb_cjAll = view.findViewById(R.id.checkbox_allCJ);
            mCkb_cjAll.setTag(mWorkCenter_cjAll);

            mCkb_fzAll.setOnCheckedChangeListener(mCheckboxListener_fz);
            mCkb_cjAll.setOnCheckedChangeListener(mCheckboxListener_cj);

            mLayout_fz = view.findViewById(R.id.layout_workCenterSelector_fz);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = UnitUtil.px2dip(mContext, getResources().getDimension(R.dimen.dp_5));
            for (DictionaryDataBo item : mList_fzWorkCenter) {
                mLayout_fz.addView(getCheckBox(item, true), params);
            }
            mLayout_cj = view.findViewById(R.id.layout_workCenterSelector_cj);
            for (DictionaryDataBo item : mList_cjWorkCenter) {
                mLayout_cj.addView(getCheckBox(item, false), params);
            }

            view.findViewById(R.id.btn_workCenterSelector_ok).setOnClickListener(this);

            mPPW_workCenter = new MyPopWindow(view, (int) getResources().getDimension(R.dimen.dp_200), LinearLayout.LayoutParams.WRAP_CONTENT);
            mPPW_workCenter.setOutsideTouchable(true);
            mPPW_workCenter.setFocusable(true);
        }
        mPPW_workCenter.showAsDropDown(mLayout_workCenter, 0, 0, Gravity.RIGHT);
    }

    private class TypeAdapter extends CommonRecyclerAdapter<DictionaryDataBo> {
        TypeAdapter(Context context, List<DictionaryDataBo> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, DictionaryDataBo item, int position) {
            CheckBox checkBox = holder.getView(R.id.checkbox);
            checkBox.setText(item.getLABEL());
            checkBox.setTag(item);
            checkBox.setOnCheckedChangeListener(mCheckboxListener_type);
        }
    }

    /**
     * 获取工作中心筛选框内的按钮
     *
     * @param isFZ 是否缝制中心
     */
    private View getCheckBox(DictionaryDataBo item, boolean isFZ) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_checkbtn_green_round, null);
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        if (isFZ) {
            checkBox.setOnCheckedChangeListener(mCheckboxListener_fz);
        } else {
            checkBox.setOnCheckedChangeListener(mCheckboxListener_cj);
        }
        checkBox.setTag(item);
        checkBox.setText(item.getLABEL());
        return view;
    }

    private enum CheckboxListenerType {TYPE, FZ, CJ}

    private class OnCheckboxListener implements CompoundButton.OnCheckedChangeListener {

        private CheckboxListenerType mType;

        OnCheckboxListener(CheckboxListenerType type) {
            this.mType = type;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.checkbox_allFZ || buttonView.getId() == R.id.checkbox_allCJ) {
                workCenterSelectAll(mType == CheckboxListenerType.FZ, isChecked);
            } else {
                DictionaryDataBo item = (DictionaryDataBo) buttonView.getTag();
                switch (mType) {
                    case TYPE:
                        if (isChecked) {
                            mList_type_checked.add(item);
                        } else {
                            mList_type_checked.remove(item);
                        }
                        break;
                    case FZ:
                        if (isChecked) {
                            mList_fzWorkCenter_checked.add(item);
                        } else {
                            if (mCkb_fzAll.isChecked()) {
                                //先清除监听，否则会执行全选与取消全选的操作
                                mCkb_fzAll.setOnCheckedChangeListener(null);
                                mCkb_fzAll.setChecked(false);
                                mList_fzWorkCenter_checked.addAll(mList_fzWorkCenter);
                                mList_fzWorkCenter_checked.remove(mWorkCenter_fzAll);
                            }
                            mList_fzWorkCenter_checked.remove(item);
                            mCkb_fzAll.setOnCheckedChangeListener(mCheckboxListener_fz);
                        }
                        break;
                    case CJ:
                        if (isChecked) {
                            mList_cjWorkCenter_checked.add(item);
                        } else {
                            if (mCkb_cjAll.isChecked()) {
                                //先清除监听，否则会执行全选与取消全选的操作
                                mCkb_cjAll.setOnCheckedChangeListener(null);
                                mCkb_cjAll.setChecked(false);
                                mList_cjWorkCenter_checked.addAll(mList_cjWorkCenter);
                                mList_cjWorkCenter_checked.remove(mWorkCenter_cjAll);
                            }
                            mList_cjWorkCenter_checked.remove(item);
                            mCkb_cjAll.setOnCheckedChangeListener(mCheckboxListener_cj);
                        }
                        break;
                }
            }
        }
    }

    private void workCenterSelectAll(boolean isFZ, boolean isSelect) {
        if (isFZ) {
            int childCount = mLayout_fz.getChildCount();
            for (int i = 1; i < childCount; i++) {
                View view = mLayout_fz.getChildAt(i);
                CheckBox checkBox = view.findViewById(R.id.checkbox);
                checkBox.setChecked(isSelect);
            }

            mList_fzWorkCenter_checked.clear();
            if (isSelect) {
                mList_fzWorkCenter_checked.add(mWorkCenter_fzAll);
            }
        } else {
            int childCount = mLayout_cj.getChildCount();
            for (int i = 1; i < childCount; i++) {
                View view = mLayout_cj.getChildAt(i);
                CheckBox checkBox = view.findViewById(R.id.checkbox);
                checkBox.setChecked(isSelect);
            }
            mList_cjWorkCenter_checked.clear();
            if (isSelect) {
                mList_cjWorkCenter_checked.add(mWorkCenter_cjAll);
            }
        }
    }

    private Dialog mLogoutDialog;
    private CommonRecyclerAdapter mLogoutAdapter;
    private String mLogoutUserId;//离岗员工ID

    private void showLogoutDialog() {
        mLogoutDialog = new Dialog(mContext);
        mLogoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_logout, null);
        RecyclerView listView = view.findViewById(R.id.lv_logout);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        listView.setLayoutManager(manager);
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        mLogoutAdapter = new CommonRecyclerAdapter<UserInfoBo>(mContext, loginUsers, R.layout.item_logout, manager) {
            @Override
            public void convert(RecyclerViewHolder holder, final UserInfoBo item, int position) {
                holder.setText(R.id.tv_userName, item.getNAME());
                holder.setText(R.id.tv_userNum, item.getEMPLOYEE_NUMBER());
                holder.getView(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoading();
                        mLogoutUserId = item.getEMPLOYEE_NUMBER();
                        HttpHelper.positionLogout(mLogoutUserId, BatchOrderListActivity.this);
                    }
                });
            }
        };
        listView.setAdapter(mLogoutAdapter);

        mLogoutDialog.setContentView(view);
        mLogoutDialog.show();
        Window window = mLogoutDialog.getWindow();
        assert window != null;
        window.setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }

    private void logoutSuccess() {
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers != null) {
            for (int i = 0; i < loginUsers.size(); i++) {
                UserInfoBo user = loginUsers.get(i);
                if (user.getEMPLOYEE_NUMBER().equals(mLogoutUserId)) {
                    loginUsers.remove(user);
                    break;
                }
            }
            SpUtil.savePositionUsers(loginUsers);
        }
        if (mLogoutAdapter != null) {
            List<UserInfoBo> data = mLogoutAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                UserInfoBo user = data.get(i);
                if (user.getEMPLOYEE_NUMBER().equals(mLogoutUserId)) {
                    mLogoutAdapter.removeData(i);
                    break;
                }
            }
            if (data.size() == 0) {
                if (mLogoutDialog != null) {
                    mLogoutDialog.dismiss();
                }
                mLogoutDialog = null;
            }
        }
        refreshLoginUser();
    }

    private void refreshLoginUser() {
        StringBuilder loginUser = new StringBuilder();
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        for (UserInfoBo user : loginUsers) {
            loginUser.append(user.getNAME()).append("、");
        }
        String userName = loginUser.toString();
        if (userName.endsWith("、")) {
            userName = userName.substring(0, userName.lastIndexOf("、"));
        }
        mTv_loginUser.setText(userName);
    }

    private class ItemAdapter extends CommonRecyclerAdapter<BatchCutOrderListBo> {

        ItemAdapter(Context context, List<BatchCutOrderListBo> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, BatchCutOrderListBo item, int position) {
            View view = holder.getView(R.id.itemView);
            if (position % 2 == 0) {
                mItemParams.rightMargin = 12;
            } else {
                mItemParams.rightMargin = 0;
            }
            view.setLayoutParams(mItemParams);

            holder.setText(R.id.tv_shopOrder, item.getSHOP_ORDER());
            holder.setText(R.id.tv_qty, item.getQTY_ORDERED() + "");
            holder.setText(R.id.tv_marketTime, item.getMARKET_TIME());
            holder.setText(R.id.tv_item, item.getITEM());
            holder.setText(R.id.tv_workCenter, item.getCF_WORKCENTER_DESC());
            holder.setText(R.id.btn_action, item.getOPERATION_DESC());
            holder.setText(R.id.tv_matDesc, item.getITEM_DESC());

            LinearLayout layout_completedQty = holder.getView(R.id.layout_completedQty);
            String operation = mOperation.getOPERATION();
            if ("QRCP001".equals(operation)) {
                layout_completedQty.setVisibility(View.GONE);
            } else {
                layout_completedQty.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_completeQty, item.getDONE_QTY());
            }

            ImageView iv_state = holder.getView(R.id.iv_state);
            if ("IN_QUEUE".equals(item.getSTATUS())) {
                iv_state.setImageResource(R.drawable.ic_state_cut_wait);
            } else if ("IN_WORK".equals(item.getSTATUS())) {
                iv_state.setImageResource(R.drawable.ic_state_cut_working);
            } else if ("DONE".equals(item.getSTATUS())) {
                iv_state.setImageResource(R.drawable.ic_state_cut_done);
            } else {
                iv_state.setImageResource(0);
            }

            setWidgetClickListener(holder, position, R.id.btn_action);
            setWidgetClickListener(holder, position, R.id.iv_state);
        }

        @Override
        public void onClick(View v, int position) {
            super.onClick(v, position);
            BatchCutOrderListBo batchCutOrder = mList_data.get(position);
            if (v.getId() == R.id.btn_action) {
                List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
                if (positionUsers == null || positionUsers.size() == 0) {
                    showErrorDialog("员工未登陆上岗，无法操作");
                    return;
                }
                String operation = mOperation.getOPERATION();
                if ("QRCP001".equals(operation)) {
                    mActionIndex = position;
                    startActivityForResult(ProcessDirectionActivity.getIntent(mContext, batchCutOrder.getSHOP_ORDER(), batchCutOrder.getSHOP_ORDER_REF(), batchCutOrder.getITEM()), REQUEST_DETAIL);
                } else {
                    startActivity(BatchLabuDetailActivity.getIntent(mContext, mOperation, batchCutOrder.getSHOP_ORDER(), batchCutOrder.getSHOP_ORDER_REF(), batchCutOrder.getITEM()));
                }
            } else if (v.getId() == R.id.iv_state) {
                mMatStatusPPWAnchor = v;
                showLoading();
                HttpHelper.getOrderMatTypesStatus(mOperation.getOPERATION(), batchCutOrder.getSHOP_ORDER_REF(), BatchOrderListActivity.this);
            }
        }
    }

    private View mMatStatusPPWAnchor;

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getBatchCutOrderList.equals(url) || HttpHelper.searchBatchRFIDInfo.equals(url)) {
                mList_data = JSON.parseArray(resultJSON.getJSONArray("result").toString(), BatchCutOrderListBo.class);
                mItemAdapter.notifyDataSetChanged(mList_data);
                if (mList_data == null || mList_data.size() == 0) {
                    toast("返回数据为空");
                }
            } else if (HttpHelper.getLabuWorkCenter.equals(url)) {
                JSONObject jsonObject = resultJSON.getJSONObject("result");
                JSONArray cateGoryOptions = jsonObject.getJSONArray("cateGoryOptions");
                if (cateGoryOptions != null) {
                    mList_type = JSON.parseArray(cateGoryOptions.toString(), DictionaryDataBo.class);
                }
                mList_fzWorkCenter = JSON.parseArray(jsonObject.getJSONArray("fzWorkCenterOptions").toString(), DictionaryDataBo.class);
                mList_cjWorkCenter = JSON.parseArray(jsonObject.getJSONArray("cjWorkCenterOptions").toString(), DictionaryDataBo.class);
                showWorkCenterSelector();
            } else if (HttpHelper.positionLogin_url.equals(url)) {
                toast("用户上岗成功");
                List<UserInfoBo> positionUsers = JSON.parseArray(resultJSON.getJSONArray("result").toString(), UserInfoBo.class);
                SpUtil.savePositionUsers(positionUsers);
                onClockIn(true);
            } else if (HttpHelper.positionLogout_url.equals(url)) {
                toast("用户下岗成功");
                logoutSuccess();
            } else if (HttpHelper.getBatchCardInfo.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                String orderType = result.getString("ORDER_TYPE");
                if ("M".equals(orderType)) {
                    List<UserInfoBo> users = SpUtil.getPositionUsers();
                    if (users != null) {
                        for (int i = 0; i < users.size(); i++) {
                            UserInfoBo user = users.get(i);
                            if (user.getCARD_NUMBER().equals(mRFID)) {
                                mLogoutUserId = user.getEMPLOYEE_NUMBER();
                                clockOut(mRFID);
                                return;
                            }
                        }
                    }
                    clockIn(mRFID);
                } else {
                    String ri = result.getString("RI");
                    showLoading();
                    HttpHelper.searchBatchRFIDInfo(mOperation.getOPERATION(), ri, orderType, this);
                }
            } else if (HttpHelper.getOrderMatTypesStatus.equals(url)) {
                JSONArray array = resultJSON.getJSONArray("result");
                if (array == null || array.size() == 0) {
                    toast("面料状态数据为空");
                } else {
                    showMatStatusPPW(array);
                }
            }
        }
    }

    private void showMatStatusPPW(JSONArray array) {
        List<JSONObject> list = JSON.parseArray(array.toString(), JSONObject.class);
        View view = LayoutInflater.from(mContext).inflate(R.layout.ppw_matstatus, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_matStatus);
        recyclerView.setAdapter(new CommonRecyclerAdapter<JSONObject>(mContext, list, R.layout.item_matstatus_ppw, null) {
            @Override
            public void convert(RecyclerViewHolder holder, JSONObject item, int position) {
                String matType = item.getString("MATERIAL_TYPES");
                switch (matType) {
                    case "M":
                        holder.setText(R.id.tv_matType, "面布");
                        break;
                    case "L":
                        holder.setText(R.id.tv_matType, "里布");
                        break;
                    case "N":
                        holder.setText(R.id.tv_matType, "朴布");
                        break;
                }

                View rootView = holder.getView(R.id.rootview);
                TextView tv_status = holder.getView(R.id.tv_matStatus);
                String status = item.getString("STATUS");
                switch (status) {
                    case "IN_WORK":
                        tv_status.setText("作业中");
                        rootView.setBackgroundResource(R.color.status_working);
                        break;
                    case "UN_START":
                        tv_status.setText("未开始");
                        rootView.setBackgroundResource(R.color.status_unStart);
                        break;
                    case "IN_QUEUE":
                        tv_status.setText("排队中");
                        rootView.setBackgroundResource(R.color.status_queue);
                        break;
                    case "DONE":
                        tv_status.setText("已完成");
                        rootView.setBackgroundResource(R.color.status_done);
                        break;
                }
            }
        });

        PopupWindow ppw = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ppw.setContentView(view);
        ppw.setFocusable(true);
        ppw.setBackgroundDrawable(new BitmapDrawable());
        ppw.setOutsideTouchable(true);

        float dimension = getResources().getDimension(R.dimen.dp_50);
        ppw.showAsDropDown(mMatStatusPPWAnchor, (int) (0 - dimension), -mMatStatusPPWAnchor.getHeight());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_DETAIL) {
                String shopOrder = mEt_shopOrder.getText().toString();
                if (!isEmpty(shopOrder)) {
                    mEt_shopOrder.setText(null);
                    search();
                } else {
                    mItemAdapter.removeData(mActionIndex);
                }
            }
        }
    }

    public static Intent getIntent(Context context, PositionInfoBo.OPERINFORBean operation) {
        Intent intent = new Intent(context, BatchOrderListActivity.class);
        intent.putExtra("operation", operation);
        return intent;
    }
}