package com.eeka.mespad.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.CardInfoBo;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.fragment.CutFragment;
import com.eeka.mespad.fragment.LoginFragment;
import com.eeka.mespad.fragment.SewFragment;
import com.eeka.mespad.fragment.SewQCFragment;
import com.eeka.mespad.fragment.SuspendFragment;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.service.MQTTService;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TopicUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by Lenovo on 2017/6/12.
 */

public class MainActivity extends NFCActivity {

    public static boolean isReLogin;

    private DrawerLayout mDrawerLayout;
    private LoginFragment mLoginFragment;
    private CutFragment mCutFragment;
    private SuspendFragment mSuspendFragment;
    private SewFragment mSewFragment;
    private SewQCFragment mSewQCFragment;

    private LinearLayout mLayout_controlPanel;

    private PositionInfoBo mPositionInfo;

    private EditText mEt_orderNum;
    private EditText mEt_position;
    private String mTopic;
    private CardInfoBo mCardInfo;
    private boolean isSearchOrder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);

        initView();
        mCardInfo = new CardInfoBo();

        MQTTService.actionStart(mContext);

        initData();

        EventBus.getDefault().register(this);

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isReLogin) {
            isReLogin = false;
            showLoading();
            HttpHelper.initData(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MQTTService.actionStop(mContext);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 收到推送消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushMsgReceive(PushJson push) {
        String type = push.getType();
        if ("LOGIN".equals(type) || "LOGOUT".equals(type)) {
            if ("LOGIN".equals(type)) {
                toast("用户刷卡上岗");
            } else {
                toast("用户刷卡离岗");
            }
            showLoading();
            HttpHelper.getPositionLoginUsers(this);
        } else {
            toast("收到工单消息，正在刷新页面");
            mEt_orderNum.setText(push.getContent());
            searchOrder();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mLayout_controlPanel = (LinearLayout) findViewById(R.id.layout_controlPanel);

        findViewById(R.id.tv_caijian).setOnClickListener(this);
        findViewById(R.id.tv_diaogua).setOnClickListener(this);
        findViewById(R.id.tv_sew).setOnClickListener(this);
        findViewById(R.id.tv_sewQC).setOnClickListener(this);
        findViewById(R.id.tv_setting).setOnClickListener(this);

        findViewById(R.id.btn_searchOrder).setOnClickListener(this);
        findViewById(R.id.btn_searchPosition).setOnClickListener(this);

        mEt_orderNum = (EditText) findViewById(R.id.et_orderNum);
        mEt_position = (EditText) findViewById(R.id.et_position);
        mEt_position.setText(HttpHelper.PAD_IP);
    }

    @Override
    protected void initData() {
        super.initData();

        showLoading();
        UserInfoBo loginUser = SpUtil.getLoginUser();
        HttpHelper.login(loginUser.getUSER(), loginUser.getPassword(), this);
//        HttpHelper.initData(this);
        findViewById(R.id.layout_search).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    private void initLoginFragment() {
        mLoginFragment = new LoginFragment();
        mLoginFragment.setOnLoginCallback(this);
        mLoginFragment.setOnClockCallback(this);
    }

    private void initButton(List<PositionInfoBo.BUTTONINFORBean> buttons) {
        mLayout_controlPanel.removeAllViews();
        for (PositionInfoBo.BUTTONINFORBean item : buttons) {
            if (item.getBUTTON_ID().equals("BINDING")) {
                continue;
            }
            Button button = (Button) LayoutInflater.from(mContext).inflate(R.layout.layout_button, null);
            button.setOnClickListener(this);
            switch (item.getBUTTON_ID().replace(" ", "")) {
                case "MATERIALRETURN":
                    button.setText("退料");
                    button.setId(R.id.btn_materialReturn);
                    break;
                case "MATERIALFEEDING":
                    button.setText("补料");
                    button.setId(R.id.btn_materialFeeding);
                    break;
                case "JOBLIST":
                    button.setText("查看作业清单");
                    button.setId(R.id.btn_jobList);
                    break;
                case "DATACOLLECT":
                    button.setText("数据记录");
                    button.setId(R.id.btn_dataCollect);
                    break;
                case "RESSTATE":
                    button.setText("设备状态更新");
                    button.setId(R.id.btn_resState);
                    break;
                case "SIGNOFF":
                    button.setText("注销在制品");
                    button.setId(R.id.btn_signOff);
                    break;
                case "VIDEO":
                    button.setText("视频查看");
                    button.setId(R.id.btn_video);
                    break;
                case "LOGIN":
                    button.setText("上岗登录");
                    button.setId(R.id.btn_login);
                    break;
                case "LOGOUT":
                    button.setText("离岗登出");
                    button.setId(R.id.btn_logout);
                    break;
                case "NCRECORD":
                    button.setText("不良录入");
                    button.setId(R.id.btn_NcRecord);
                    break;
                case "START":
                    button.setText("开始");
                    button.setId(R.id.btn_start);
                    break;
                case "BINDING":
                    button.setText("绑定");
                    button.setId(R.id.btn_binding);
                    break;
                case "UNBIND":
                    button.setText("制卡/解绑");
                    button.setId(R.id.btn_unbind);
                    break;
                case "COMPLETE":
                    if (mCutFragment != null)
                        mCutFragment.showCompleteButton();
                    continue;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            button.setLayoutParams(params);
            mLayout_controlPanel.addView(button);
        }
    }

    private void changeFragment() {
        if (mPositionInfo == null) {
            toast("请先获取站位数据");
            return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isAdded())
                    ft.hide(fragment);
            }
        }
        switch (mTopic) {
            case TopicUtil.TOPIC_CUT:
                if (mCutFragment == null) {
                    mCutFragment = new CutFragment();
                }
                if (mCutFragment.isAdded()) {
                    ft.show(mCutFragment);
                } else {
                    ft.add(R.id.layout_content, mCutFragment);
                }
                ft.commit();
                break;
            case TopicUtil.TOPIC_SEW:
                if (mSewFragment == null)
                    mSewFragment = new SewFragment();
                if (mSewFragment.isAdded()) {
                    ft.show(mSewFragment);
                } else {
                    ft.add(R.id.layout_content, mSewFragment);
                }
                ft.commit();
                break;
            case TopicUtil.TOPIC_SUSPEND:
                if (mSuspendFragment == null) {
                    mSuspendFragment = new SuspendFragment();
                }
                if (mSuspendFragment.isAdded()) {
                    ft.show(mSuspendFragment);
                } else {
                    ft.add(R.id.layout_content, mSuspendFragment);
                }
                ft.commit();
                break;

            case TopicUtil.TOPIC_QC:
                if (mSewQCFragment == null) {
                    mSewQCFragment = new SewQCFragment();
                }
                if (mSewQCFragment.isAdded()) {
                    ft.show(mSewQCFragment);
                } else {
                    ft.add(R.id.layout_content, mSewQCFragment);
                }
                ft.commit();
                break;
            case TopicUtil.TOPIC_PACKING:

                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        SystemUtils.hideKeyboard(mContext, v);
        switch (v.getId()) {
            case R.id.tv_caijian:
                mTopic = TopicUtil.TOPIC_CUT;
                changeFragment();
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.tv_diaogua:
                mTopic = TopicUtil.TOPIC_SUSPEND;
                changeFragment();
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.tv_sew:
                mTopic = TopicUtil.TOPIC_SEW;
                changeFragment();
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.tv_sewQC:
                mTopic = TopicUtil.TOPIC_QC;
                changeFragment();
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.tv_setting:
                mDrawerLayout.closeDrawer(Gravity.START);
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.btn_materialReturn:
                if (mCutFragment != null) {
                    mCutFragment.returnAndFeedingMat(true);
                }
                break;
            case R.id.btn_materialFeeding:
                if (mCutFragment != null) {
                    mCutFragment.returnAndFeedingMat(false);
                }
                break;
            case R.id.btn_dataCollect:
                if (TopicUtil.TOPIC_CUT.equals(mTopic))
                    mCutFragment.showRecordLabuDialog();
                break;
            case R.id.btn_jobList:
                startActivity(new Intent(mContext, WorkOrderListActivity.class));
                break;
            case R.id.btn_NcRecord:
                if (TopicUtil.TOPIC_CUT.equals(mTopic)) {
                    mCutFragment.recordNC();
                } else if (TopicUtil.TOPIC_QC.equals(mTopic)) {
                    mSewQCFragment.recordNC();
                }
                break;
            case R.id.btn_video:
                if (TopicUtil.TOPIC_SEW.equals(mTopic)) {
                    mSewFragment.playVideo();
                } else if (TopicUtil.TOPIC_CUT.equals(mTopic)){
                    mCutFragment.playVideo();
                }else {
                    toast("该站位无视频");
                }
                break;
            case R.id.btn_login:
            case R.id.tv_startWorkAlert:
                showLoginDialog();
                break;
            case R.id.btn_logout:
                List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
                if (loginUsers != null && loginUsers.size() != 0) {
                    showLogoutDialog();
                } else {
                    showErrorDialog("目前没有人在岗位登录");
                }
                break;
            case R.id.btn_signOff:
                if (isEmpty(mTopic)) {
                    toast("请先获取站位数据");
                    return;
                }
                switch (mTopic) {
                    case TopicUtil.TOPIC_CUT:
                        if (mCutFragment != null) {
                            mCutFragment.signOff();
                        }
                        break;
                }
                break;
            case R.id.btn_searchOrder:
                searchOrder();
                break;
            case R.id.btn_searchPosition:
                String position1 = mEt_position.getText().toString();
                if (!isEmpty(position1)) {
                    HttpHelper.PAD_IP = position1;
                } else {
                    toast("请输入完整的站位");
                }
                showLoading();
                HttpHelper.initData(this);
                break;
            case R.id.btn_start:
                switch (mTopic) {
                    case TopicUtil.TOPIC_CUT:
                        if (mCardInfo != null) {
                            mCutFragment.startWork();
                        }
                        break;
                    case TopicUtil.TOPIC_SEW:
                        break;
                    case TopicUtil.TOPIC_SUSPEND:

                        break;
                }
                break;
            case R.id.btn_binding:
                if (mSuspendFragment != null) {
                    mSuspendFragment.binding();
                }
                break;
            case R.id.btn_unbind:
                if (mSuspendFragment != null) {
                    mSuspendFragment.unBind();
                }
                break;
        }
    }

    /**
     * 测试用
     */
    private void test() {
        for (int i = 0; i < 10; i++) {
            HttpHelper.initData(MainActivity.this);
        }
    }

    private void searchOrder() {
//        test();
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null || mPositionInfo == null) {
            isSearchOrder = true;
            showLoading();
            HttpHelper.initData(this);
            return;
        }
        String cardNum = mEt_orderNum.getText().toString();
        if (isEmpty(cardNum)) {
            toast("请输入RFID卡号");
        } else {
            mCardInfo.setCardNum(cardNum);
            switch (mTopic) {
                case TopicUtil.TOPIC_CUT:
                    getCardInfo(cardNum);
                    break;
                case TopicUtil.TOPIC_SEW:
                    mSewFragment.searchOrder(mCardInfo.getCardNum());
                    break;
                case TopicUtil.TOPIC_SUSPEND:
                    mSuspendFragment.searchOrder(cardNum);
                    break;
                case TopicUtil.TOPIC_QC:
                    mSewQCFragment.searchOrder(cardNum);
                    break;
            }
        }
    }

    private Dialog mLogoutDialog;
    private CommonRecyclerAdapter mLogoutAdapter;
    private int mLogoutIndex;

    private void showLogoutDialog() {
        mLogoutDialog = new Dialog(mContext);
        mLogoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_logout, null);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.lv_logout);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        listView.setLayoutManager(manager);
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        mLogoutAdapter = new CommonRecyclerAdapter<UserInfoBo>(mContext, loginUsers, R.layout.item_logout, manager) {
            @Override
            public void convert(RecyclerViewHolder holder, final UserInfoBo item, final int position) {
                holder.setText(R.id.tv_userName, item.getNAME());
                holder.setText(R.id.tv_userNum, item.getEMPLOYEE_NUMBER());
                holder.getView(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoading();
                        mLogoutIndex = position;
                        HttpHelper.positionLogout(item.getCARD_NUMBER(), MainActivity.this);
                    }
                });
            }
        };
        listView.setAdapter(mLogoutAdapter);

        mLogoutDialog.setContentView(view);
        mLogoutDialog.show();
        Window window = mLogoutDialog.getWindow();
        window.setLayout((int) (SystemUtils.getScreenWidth(this) * 0.5), (int) (SystemUtils.getScreenHeight(this) * 0.5));
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        dismissLoading();
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.login_url.equals(url)) {
                HttpHelper.initData(this);
            } else if (HttpHelper.queryPositionByPadIp_url.equals(url)) {
                ContextInfoBo contextInfoBo = JSON.parseObject(HttpHelper.getResultStr(resultJSON), ContextInfoBo.class);
                SpUtil.saveContextInfo(contextInfoBo);
                List<UserInfoBo> positionUsers = contextInfoBo.getLOGIN_USER_LIST();
                SpUtil.savePositionUsers(positionUsers);

                HttpHelper.findProcessWithPadId(this);
            } else if (HttpHelper.findProcessWithPadId_url.equals(url)) {
                mPositionInfo = JSON.parseObject(HttpHelper.getResultStr(resultJSON), PositionInfoBo.class);
                SpUtil.save(SpUtil.KEY_RESOURCE, JSON.toJSONString(mPositionInfo.getRESR_INFOR()));
                List<PositionInfoBo.OPERINFORBean> operInfo = mPositionInfo.getOPER_INFOR();
                if (operInfo != null && operInfo.size() != 0) {
                    PositionInfoBo.OPERINFORBean bean = operInfo.get(0);
                    mTopic = bean.getTOPIC();
                }
                refreshView(url, resultJSON);

                if (mPositionInfo.getBUTTON_INFOR() != null) {
                    initButton(mPositionInfo.getBUTTON_INFOR());
                }

                if (isSearchOrder) {
                    isSearchOrder = false;
                    searchOrder();
                }
            } else if (HttpHelper.getCardInfo.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                String orderType = result.getString("ORDER_TYPE");
                mCardInfo.setCardType(orderType);
                mCardInfo.setValue(resultJSON.getString("RI"));
                switch (mTopic) {
                    case TopicUtil.TOPIC_CUT:
                        if ("P".equalsIgnoreCase(orderType) || "S".equalsIgnoreCase(orderType)) {
                            mEt_orderNum.setText(mCardInfo.getCardNum());
                            mCutFragment.searchOrder(orderType, mCardInfo.getCardNum(), mPositionInfo.getRESR_INFOR().getRESOURCE_BO(), mCardInfo.getValue());
                        } else if ("M".equals(orderType)) {
                            clockIn(mCardInfo.getCardNum());
                        }
                        break;
                }
            } else if (HttpHelper.positionLogin_url.equals(url)) {
                toast("用户上岗成功");
                List<UserInfoBo> positionUsers = JSON.parseArray(resultJSON.getJSONArray("result").toString(), UserInfoBo.class);
                SpUtil.savePositionUsers(positionUsers);
                onClockIn(true);
            } else if (HttpHelper.positionLogout_url.equals(url)) {
                toast("用户下岗成功");
                logoutSuccess();
            } else if (HttpHelper.getPositionLoginUser_url.equals(url)) {
                List<UserInfoBo> positionUsers = JSON.parseArray(resultJSON.getJSONArray("result").toString(), UserInfoBo.class);
                SpUtil.savePositionUsers(positionUsers);
                refreshLoginUser();
            }
        } else {
            String message = resultJSON.getString("message");
            if (HttpHelper.positionLogout_url.equals(url) && !isEmpty(message) && message.contains("用户未在站点登录")) {
                toast("用户下线成功");
                logoutSuccess();
            } else {
                showErrorDialog(message);
            }
        }
    }

    private void refreshView(String url, JSONObject resultJSON) {
        changeFragment();
        switch (mTopic) {
            case TopicUtil.TOPIC_CUT:
                mCutFragment.onSuccess(url, resultJSON);
                break;
            case TopicUtil.TOPIC_SUSPEND:
                mSuspendFragment.onSuccess(url, resultJSON);
                break;
        }
    }

    /**
     * 刷新登录用户表，用户登录/登出时需要调用
     */
    private void refreshLoginUser() {
        switch (mTopic) {
            case TopicUtil.TOPIC_CUT:
                mCutFragment.refreshLoginUsers();
                break;
            case TopicUtil.TOPIC_SEW:
                mSewFragment.refreshLoginUsers();
                break;
            case TopicUtil.TOPIC_SUSPEND:
                mSuspendFragment.refreshLoginUsers();
                break;
            case TopicUtil.TOPIC_QC:
                mSewQCFragment.refreshLoginUsers();
                break;
        }
    }

    private void logoutSuccess() {
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers != null && loginUsers.size() > mLogoutIndex) {
            loginUsers.remove(mLogoutIndex);
            SpUtil.savePositionUsers(loginUsers);
            if (mLogoutAdapter != null) {
                mLogoutAdapter.removeData(mLogoutIndex);
            }
            if (loginUsers.size() == 0) {
                if (mLogoutDialog != null) {
                    mLogoutDialog.dismiss();
                }
                mLogoutDialog = null;
            }
        }
        refreshLoginUser();
    }

    @Override
    public void onLogin(boolean success) {
        super.onLogin(success);
        if (success) {
            if (mLoginFragment.isAdded()) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.remove(mLoginFragment);
                ft.commit();
            }

            initData();
        }
    }

    @Override
    public void onClockIn(boolean success) {
        if (success) {
            if (mLoginDialog != null)
                mLoginDialog.dismiss();
            if (mPositionInfo == null) {
                HttpHelper.findProcessWithPadId(this);
            } else {
                refreshLoginUser();
            }
        }
    }
}
