package com.eeka.mespad.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.CommonVPAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.ProcessSheetsBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.CenterLayoutManager;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.view.dialog.CommListDialog;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.HangerSwipeDialog;
import com.eeka.mespad.view.dialog.ProcessSheetsDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 试产
 */
public class PilotProductionActivity extends BaseActivity {

    private TextView mTv_loginUser;
    private EditText mEt_item;
    private TextView mTv_item;
    private TextView mTv_curStep;
    private Button mBtn_searchType;
    private Button mBtn_complete;
    private Button mBtn_done;

    private List<JSONObject> mList_data;
    private ItemAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private CenterLayoutManager mLayoutManager;

    private JSONObject mData;

    private List<JSONObject> mList_CurOperations;

    private ImgAdapter mImgAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_pilotproduction);

        showLoading();
        HttpHelper.initData(this);

        initView();
    }

    @Override
    protected void initView() {
        mTv_loginUser = findViewById(R.id.tv_pilotProd_loginUsers);
        mTv_loginUser.setOnClickListener(this);

        mEt_item = findViewById(R.id.et_pilotProd_item);
        mTv_item = findViewById(R.id.tv_pilotProd_curItem);
        mTv_curStep = findViewById(R.id.tv_pilotProd_curStep);
        mBtn_searchType = findViewById(R.id.btn_pilotProd_searchType);
        mBtn_searchType.setOnClickListener(this);

        mRecyclerView = findViewById(R.id.recyclerView_operation);
        mLayoutManager = new CenterLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mList_data = new ArrayList<>();
        mAdapter = new ItemAdapter(mContext, mList_data, R.layout.item_pilotprod_operation, mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ViewPager vp_sopImg = findViewById(R.id.vp_sopImg);
        mList_CurOperations = new ArrayList<>();
        mImgAdapter = new ImgAdapter(mContext, mList_CurOperations, R.layout.item_pilot_image);
        vp_sopImg.setAdapter(mImgAdapter);

        mBtn_done = findViewById(R.id.btn_pilotProd_done);
        mBtn_done.setOnClickListener(this);

        mBtn_complete = findViewById(R.id.btn_pilotProd_complete);
        mBtn_complete.setOnClickListener(this);

        findViewById(R.id.iv_pilotProd_login).setOnClickListener(this);
        findViewById(R.id.btn_pilotProd_search).setOnClickListener(this);
        findViewById(R.id.btn_pilotProd_feedback).setOnClickListener(this);
        findViewById(R.id.btn_pilotProd_processSheets).setOnClickListener(this);
        findViewById(R.id.btn_pilotProd_video).setOnClickListener(this);

    }

    private class ItemAdapter extends CommonRecyclerAdapter<JSONObject> {

        public ItemAdapter(Context context, List<JSONObject> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, JSONObject item, int position) {
            View rootView = holder.getView(R.id.rootview);
            TextView tv_stepId = holder.getView(R.id.tv_step);
            TextView tv_code = holder.getView(R.id.tv_code);
            TextView tv_desc = holder.getView(R.id.tv_desc);
            TextView tv_resDesc = holder.getView(R.id.tv_resDesc);
            TextView tv_standTime = holder.getView(R.id.tv_standTime);
            String operation = item.getString("operation");

            boolean flag = false;
            for (int i = 0; i < mList_CurOperations.size(); i++) {
                JSONObject object = mList_CurOperations.get(i);
                if (object.getString("operation").equals(operation)) {
                    flag = true;
                    rootView.setBackgroundResource(R.drawable.border_selected);
                    tv_stepId.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tv_code.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tv_desc.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tv_resDesc.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tv_standTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
            if (!flag) {
                if ("Y".equals(item.getString("isWorked"))) {
                    rootView.setBackgroundResource(R.color.divider_gray);
                    tv_stepId.setTextColor(getResources().getColor(R.color.text_gray_default));
                    tv_code.setTextColor(getResources().getColor(R.color.text_gray_default));
                    tv_desc.setTextColor(getResources().getColor(R.color.text_gray_default));
                    tv_resDesc.setTextColor(getResources().getColor(R.color.text_gray_default));
                    tv_standTime.setTextColor(getResources().getColor(R.color.text_gray_default));
                } else {
                    rootView.setBackgroundColor(getResources().getColor(R.color.white));
                    tv_stepId.setTextColor(getResources().getColor(R.color.text_black_default));
                    tv_code.setTextColor(getResources().getColor(R.color.text_black_default));
                    tv_desc.setTextColor(getResources().getColor(R.color.text_black_default));
                    tv_resDesc.setTextColor(getResources().getColor(R.color.text_black_default));
                    tv_standTime.setTextColor(getResources().getColor(R.color.text_black_default));
                }
            }

            tv_stepId.setText(item.getString("stepId"));
            tv_code.setText(operation);
            tv_desc.setText(item.getString("operationDesc"));
            tv_resDesc.setText(item.getString("resourceTypeDesc"));
            tv_standTime.setText(item.getString("operationTime"));

            setWidgetClickListener(holder, position, R.id.rootview);
        }

        @Override
        public void onClick(View v, int position) {
            super.onClick(v, position);
            if (v.getId() == R.id.rootview) {
                mCurClickPosition = position;
                JSONObject json = mList.get(position);
                selectOperation(json.getString("operation"));
            }
        }
    }

    private int mCurClickPosition;

    private void selectOperation(String operation) {
        showLoading();
        HttpHelper.selectOperation(mData.getString("router"), operation, this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_pilotProd_login:
                showLoginDialog();
                break;
            case R.id.tv_pilotProd_loginUsers:
                showLogoutDialog();
                break;
            case R.id.btn_pilotProd_search:
                search();
                break;
            case R.id.btn_pilotProd_complete:
                complete();
                break;
            case R.id.btn_pilotProd_done:
                done();
                break;
            case R.id.btn_pilotProd_feedback:
                String loginUserId = SpUtil.getLoginUserId();
                if (isEmpty(loginUserId)) {
                    toast("请登录");
                    return;
                }
                if ((mData == null)) {
                    toast("请先搜索数据");
                    return;
                }
                startActivity(FeedBackActivity.getIntent(mContext, mData.toJSONString()));
                break;
            case R.id.btn_pilotProd_processSheets:
                showProcessSheets();
                break;
            case R.id.btn_pilotProd_video:
                if (mList_CurOperations == null || mList_CurOperations.size() == 0) {
                    ErrorDialog.showAlert(mContext, "请先选择工序");
                    return;
                }
                new CommListDialog<>(mContext, CommListDialog.TYPE.VIDEO, mList_CurOperations).show();
                break;
            case R.id.btn_pilotProd_searchType:
                showSearchTypeWindow();
                break;
        }
    }

    private void showProcessSheets() {
        if (mData == null) {
            ErrorDialog.showAlert(mContext, "请先搜索数据");
            return;
        }
        String item = mEt_item.getText().toString();
        if (item.length() < 14) {
            showLoading();
            HttpHelper.getProcessSheetsByItem(mData.getString("item"), this);
        } else {
            String url = PadApplication.MTM_URL + mData.getString("orderNo");
            startActivity(WebActivity.getIntent(mContext, url));
        }
    }

    /**
     * 显示搜索类型选择弹框
     */
    private void showSearchTypeWindow() {
        final List<String> list = new ArrayList<>();
        list.add("款号");
        list.add("工单号");
        final ListPopupWindow ppw = new ListPopupWindow(mContext);
        ppw.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, list));
        ppw.setWidth((int) getResources().getDimension(R.dimen.dp_60));
        ppw.setHeight(ListPopupWindow.WRAP_CONTENT);
        ppw.setAnchorView(mBtn_searchType);
        ppw.setModal(true);
        ppw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = list.get(position);
                mBtn_searchType.setText(s);
                mEt_item.setText(null);
                ppw.dismiss();
            }
        });
        ppw.show();
    }

    private void complete() {
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null) {
            showErrorDialog("站位资源获取失败，请联系运维人员查看");
            return;
        }
        if (mData == null) {
            showErrorDialog("请先搜索");
            return;
        }
        showLoading();
        JSONObject json = new JSONObject();
        json.put("site", SpUtil.getSite());
        json.put("item", mData.getString("item"));
        json.put("orderNo", mData.getString("orderNo"));
        json.put("orderType", mData.getString("orderType"));
        json.put("router", mData.getString("router"));
        json.put("lineCateGory", contextInfo.getLINE_CATEGORY());
        json.put("position", contextInfo.getPOSITION());
        json.put("userId", SpUtil.getLoginUserId());
        json.put("trialRouterOperations", mList_CurOperations);
        HttpHelper.trialOperationWork(json, this);
    }

    private void done() {
        String loginUserId = SpUtil.getLoginUserId();
        if (isEmpty(loginUserId)) {
            toast("请登录员工后操作");
            return;
        }
        showLoading();
        HttpHelper.shopOrderDone(mData.getString("orderNo"), loginUserId, this);
    }

    private void search() {
        String loginUserId = SpUtil.getLoginUserId();
        if (isEmpty(loginUserId)) {
            toast("请登录员工后操作");
            return;
        }
        String item = mEt_item.getText().toString();
        if (isEmpty(item)) {
            toast("请输入款号/工单号搜索");
            return;
        }
        String type = "";
        if (item.length() < 14) {
            type = "P";
        } else {
            type = "S";
        }
        showLoading();
        HttpHelper.getTrialRouterInfo(item, type, loginUserId, this);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        dismissLoading();
        if (HttpHelper.isSuccess(resultJSON)) {
            int curPosition;
            if (HttpHelper.queryPositionByPadIp_url.equals(url)) {
                ContextInfoBo contextInfoBo = JSON.parseObject(HttpHelper.getResultStr(resultJSON), ContextInfoBo.class);
                SpUtil.saveContextInfo(contextInfoBo);
                assert contextInfoBo != null;
                List<UserInfoBo> positionUsers = contextInfoBo.getLOGIN_USER_LIST();
                SpUtil.savePositionUsers(positionUsers);
                refreshLoginUser();

                HttpHelper.findProcessWithPadId(this);
            } else if (HttpHelper.findProcessWithPadId_url.equals(url)) {
                PositionInfoBo positionInfo = JSON.parseObject(HttpHelper.getResultStr(resultJSON), PositionInfoBo.class);
                assert positionInfo != null;
                SpUtil.save(SpUtil.KEY_RESOURCE, JSON.toJSONString(positionInfo.getRESR_INFOR()));
                SpUtil.save(SpUtil.KEY_NCIMG_INFO, JSON.toJSONString(positionInfo.getNcImgInfo()));
            } else if (url.equals(HttpHelper.getTrialRouterInfo)) {
                mData = resultJSON.getJSONObject("result");
                if ("P".equals(mData.getString("orderType"))) {
                    mTv_item.setText(mData.getString("item"));
                } else {
                    mTv_item.setText(mData.getString("orderNo"));
                }
                String isShopOrderDone = mData.getString("isShopOrderDone");
                if ("Y".equals(isShopOrderDone)) {
                    mBtn_done.setEnabled(false);
                } else {
                    mBtn_done.setEnabled(true);
                }
                mList_data = mData.getJSONArray("trialRouterOperations").toJavaList(JSONObject.class);
                if (mList_data == null || mList_data.size() == 0) {
                    ErrorDialog.showAlert(mContext, "该款号下无工序数据，请联系运维人员查看");
                    return;
                }
                mList_CurOperations = mData.getJSONArray("currentOperations").toJavaList(JSONObject.class);
                if (mList_CurOperations == null) {
                    mList_CurOperations = new ArrayList<>();
                }
                curPosition = -1;
                if (mList_CurOperations.size() == 0) {
                    if ("Y".equals(mData.getString("isFinish"))) {
                        curPosition = mList_data.size() - 1;
                    } else {
                        mList_CurOperations.add(mList_data.get(0));
                    }
                } else {
                    for (int i = 0; i < mList_data.size(); i++) {
                        JSONObject object = mList_data.get(i);
                        for (int j = 0; j < mList_CurOperations.size(); j++) {
                            JSONObject item = mList_CurOperations.get(j);
                            if (item.getString("operation").equals(object.getString("operation"))) {
                                curPosition = i;
                                break;
                            }
                        }
                    }
                }
                mAdapter.notifyDataSetChanged(mList_data);
                if (curPosition != -1) {
                    mLayoutManager.smoothScrollToPosition(mRecyclerView, null, curPosition);
                }
                mImgAdapter.notifyDataSetChanged(mList_CurOperations);
                setupCurStep();
            } else if (HttpHelper.selectOperation.equals(url)) {
                mList_CurOperations = resultJSON.getJSONArray("result").toJavaList(JSONObject.class);
                mAdapter.notifyDataSetChanged(mList_data);
                mImgAdapter.notifyDataSetChanged(mList_CurOperations);
                mLayoutManager.smoothScrollToPosition(mRecyclerView, null, mCurClickPosition);
                setupCurStep();
            } else if (HttpHelper.trialOperationWork.equals(url)) {
                List<JSONObject> nextOperations = resultJSON.getJSONArray("result").toJavaList(JSONObject.class);
                boolean flag = false;
                curPosition = -1;
                for (int i = 0; i < mList_data.size(); i++) {
                    JSONObject object = mList_data.get(i);
                    if (!flag) {
                        for (int j = 0; j < nextOperations.size(); j++) {
                            JSONObject item = nextOperations.get(j);
                            if (item.getString("operation").equals(object.getString("operation"))) {
                                curPosition = i;
                                flag = true;//只要匹配到，这个循环就可以不用了
                                break;
                            }
                        }
                    }
                    for (int j = 0; j < mList_CurOperations.size(); j++) {
                        JSONObject item = mList_CurOperations.get(j);
                        if (item.getString("operation").equals(object.getString("operation"))) {
                            object.put("isWorked", "Y");
                        }
                    }
                }
                mAdapter.notifyDataSetChanged(mList_data);
                mList_CurOperations = nextOperations;
                if (curPosition != -1)
                    mLayoutManager.smoothScrollToPosition(mRecyclerView, null, curPosition);
                mImgAdapter.notifyDataSetChanged(mList_CurOperations);
                setupCurStep();
            } else if (HttpHelper.shopOrderDone.equals(url)) {
                toast("操作成功");
                mBtn_done.setEnabled(false);
            } else if (HttpHelper.positionLogin_url.equals(url)) {
                toast("用户上岗成功");
                List<UserInfoBo> positionUsers = JSON.parseArray(resultJSON.getJSONArray("result").toString(), UserInfoBo.class);
                SpUtil.savePositionUsers(positionUsers);
                onClockIn(true);
            } else if (HttpHelper.positionLogout_url.equals(url)) {
                toast("用户下岗成功");
                logoutSuccess();
            } else if (HttpHelper.XMII_URL.equals(url)) {
                ProcessSheetsBo processSheets = JSON.parseObject(resultJSON.getString("result"), ProcessSheetsBo.class);
                if (processSheets == null) {
                    ErrorDialog.showAlert(mContext, "根据工单没有查到对应的款号");
                } else {
                    new ProcessSheetsDialog(mContext, processSheets).show();
                }
            }
        } else {
            String message = resultJSON.getString("message");
            if (HttpHelper.positionLogout_url.equals(url) && !isEmpty(message) && message.contains("用户未在站点登录")) {
                toast("用户下线成功");
                logoutSuccess();
            } else {
                if (HttpHelper.XMII_URL.equals(url)) {
                    showErrorDialog(resultJSON.getString("result"));
                } else {
                    showErrorDialog(message);
                }
            }
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        super.onFailure(url, code, message);
        dismissLoading();
    }

    private void setupCurStep() {
        StringBuilder curStep = new StringBuilder();
        for (int j = 0; j < mList_CurOperations.size(); j++) {
            JSONObject item = mList_CurOperations.get(j);
            curStep.append(item.getString("stepId"));
            if (j != mList_CurOperations.size() - 1) {
                curStep.append(",");
            }
        }
        mTv_curStep.setText(curStep.toString());
    }

    private class ImgAdapter extends CommonVPAdapter<JSONObject> {

        public ImgAdapter(Context context, List<JSONObject> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convertView(View view, JSONObject item, final int position) {
            ImageView imageView = view.findViewById(R.id.imageView);
            String sop_url = item.getString("sopUrl");
            String desc = item.getString("operationInstruction");
            TextView textView = view.findViewById(R.id.textView);
            textView.setText(desc);
            if (!isEmpty(sop_url)) {
                Picasso.with(mContext).load(sop_url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> urls = new ArrayList<>();
                        for (JSONObject data : mList_CurOperations) {
                            urls.add(data.getString("sopUrl"));
                        }
                        startActivity(ImageBrowserActivity.getIntent(mContext, urls, position));
                    }
                });
            }
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
                        HttpHelper.positionLogout(mLogoutUserId, PilotProductionActivity.this);
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

}
