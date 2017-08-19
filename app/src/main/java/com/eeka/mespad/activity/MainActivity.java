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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.CardInfoBo;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.RecordNCBo;
import com.eeka.mespad.bo.ReturnMaterialInfoBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.fragment.CutFragment;
import com.eeka.mespad.fragment.LoginFragment;
import com.eeka.mespad.fragment.SewFragment;
import com.eeka.mespad.fragment.SewQCFragment;
import com.eeka.mespad.fragment.SuspendFragment;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.service.MQTTService;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TopicUtil;
import com.eeka.mespad.view.dialog.ReturnMaterialDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/6/12.
 */

public class MainActivity extends BaseActivity {

    private static final int REQUEST_RECORD_NC = 0;
    private static final int REQUEST_LOGIN = 1;

    private DrawerLayout mDrawerLayout;
    private TextView mTv_startWorkAlert;

    private LoginFragment mLoginFragment;
    private CutFragment mCutFragment;
    private SuspendFragment mSuspendFragment;
    private SewFragment mSewFragment;
    private SewQCFragment mSewQCFragment;

    private LinearLayout mLayout_controlPanel;

    private ReturnMaterialInfoBo mReturnMaterialInfo;//退料
    private ReturnMaterialInfoBo mAddMaterialInfo;//补料
    private List<RecordNCBo> mList_badData;

    private PositionInfoBo mPositionInfo;

    private EditText mEt_orderNum;
    private EditText mEt_position;
    private String mTopic;
    private CardInfoBo mCardInfo;
    private boolean isSearchOrder;

    private boolean isInit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);

        initView();
        mCardInfo = new CardInfoBo();

        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null) {
            isInit = true;
            showLoading("应用初始化中...", true);
            HttpHelper.initData(this);
        } else {
            initData();
        }

        startMQTTService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MQTTService.actionStop(mContext);
    }

    private void startMQTTService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MQTTService.actionStart(mContext);
            }
        }).start();
    }

    @Override
    protected void initView() {
        super.initView();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mLayout_controlPanel = (LinearLayout) findViewById(R.id.layout_controlPanel);
        mTv_startWorkAlert = (TextView) findViewById(R.id.tv_startWorkAlert);
        mTv_startWorkAlert.setOnClickListener(this);

        findViewById(R.id.tv_caijian).setOnClickListener(this);
        findViewById(R.id.tv_diaogua).setOnClickListener(this);
        findViewById(R.id.tv_sew).setOnClickListener(this);
        findViewById(R.id.tv_sewQC).setOnClickListener(this);
        findViewById(R.id.tv_setLoginUser).setOnClickListener(this);

        findViewById(R.id.btn_searchOrder).setOnClickListener(this);
        findViewById(R.id.btn_searchPosition).setOnClickListener(this);

        mEt_orderNum = (EditText) findViewById(R.id.et_orderNum);
        mEt_position = (EditText) findViewById(R.id.et_position);
        mEt_position.setText(HttpHelper.PAD_IP);
    }

    @Override
    protected void initData() {
        super.initData();

        UserInfoBo loginUser = SpUtil.getLoginUser();
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        if (loginUser == null || positionUsers == null || positionUsers.size() == 0) {
            if (mLoginFragment == null) {
                initLoginFragment();
            }
            if (loginUser == null) {
                mLoginFragment.setType(LoginFragment.TYPE_LOGIN);
            } else {
                mLoginFragment.setType(LoginFragment.TYPE_CLOCK);
            }
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.layout_content, mLoginFragment);
            ft.commit();
        } else {
            showLoading();
            HttpHelper.findProcessWithPadId(this);
        }
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
                case "UNBIND":
                    button.setText("解绑");
                    button.setId(R.id.btn_unbind);
                    break;
                case "COMPLETE":
                    if (mCutFragment != null)
                        mCutFragment.showCompleteButton();
                    continue;
            }
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
                } else {
                    mCutFragment.refreshLoginUsers();
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
                } else {
                    mSuspendFragment.refreshLoginUsers();
                }
                if (mSuspendFragment.isAdded()) {
                    ft.show(mSuspendFragment);
                } else {
                    ft.add(R.id.layout_content, mSuspendFragment);
                }
                ft.commit();
                break;
            case TopicUtil.TOPIC_QC:
                if (mSewQCFragment == null)
                    mSewQCFragment = new SewQCFragment();
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
            case R.id.tv_setLoginUser:
                mDrawerLayout.closeDrawer(Gravity.START);
                startActivityForResult(new Intent(mContext, LoginActivity.class), REQUEST_LOGIN);
                break;
            case R.id.btn_materialReturn:
                if (mReturnMaterialInfo == null) {
                    mReturnMaterialInfo = new ReturnMaterialInfoBo();
                    TailorInfoBo materialData = mCutFragment.getMaterialData();
                    if (materialData == null) {
                        toast("获取订单数据失败，请重新获取数据");
                        return;
                    }
                    mReturnMaterialInfo.setOrderNum(materialData.getSHOP_ORDER_INFOR().getSHOP_ORDER());
                    List<TailorInfoBo.MatInfoBean> itemArray = materialData.getMAT_INFOR();
                    List<ReturnMaterialInfoBo.MaterialInfoBo> materialList = new ArrayList<>();
                    for (TailorInfoBo.MatInfoBean item : itemArray) {
                        ReturnMaterialInfoBo.MaterialInfoBo material = new ReturnMaterialInfoBo.MaterialInfoBo();
                        material.setPicUrl(item.getMAT_URL());
                        material.setITEM(item.getMAT_NO());
                        materialList.add(material);
                    }
                    mReturnMaterialInfo.setMaterialInfoList(materialList);
                }
                new ReturnMaterialDialog(mContext, ReturnMaterialDialog.TYPE_RETURN, mReturnMaterialInfo).show();
                break;
            case R.id.btn_materialFeeding:
                if (mAddMaterialInfo == null) {
                    mAddMaterialInfo = new ReturnMaterialInfoBo();
                    TailorInfoBo materialData = mCutFragment.getMaterialData();
                    if (materialData == null) {
                        toast("获取订单数据失败，请重新获取数据");
                        return;
                    }
                    mAddMaterialInfo.setOrderNum(materialData.getSHOP_ORDER_INFOR().getSHOP_ORDER());
                    List<TailorInfoBo.MatInfoBean> itemArray = materialData.getMAT_INFOR();
                    List<ReturnMaterialInfoBo.MaterialInfoBo> materialList = new ArrayList<>();
                    for (TailorInfoBo.MatInfoBean item : itemArray) {
                        ReturnMaterialInfoBo.MaterialInfoBo material = new ReturnMaterialInfoBo.MaterialInfoBo();
                        material.setPicUrl(item.getMAT_URL());
                        material.setITEM(item.getMAT_NO());
                        materialList.add(material);
                    }
                    mAddMaterialInfo.setMaterialInfoList(materialList);
                }
                new ReturnMaterialDialog(mContext, ReturnMaterialDialog.TYPE_ADD, mAddMaterialInfo).show();
                break;
            case R.id.btn_dataCollect:
                mCutFragment.showRecordLabuDialog();
                break;
            case R.id.btn_jobList:
                startActivity(new Intent(mContext, WorkOrderListActivity.class));
                break;
            case R.id.btn_NcRecord:
                startActivityForResult(RecordCutNCActivity.getIntent(mContext, mCutFragment.getMaterialData(), mList_badData), REQUEST_RECORD_NC);
                break;
            case R.id.btn_video:
                //自定义播放器，可缓存视频到本地
                startActivity(VideoPlayerActivity.getIntent(mContext, "http://10.7.121.75/gst/test.MP4"));

                //系统自带视频播放，无缓存
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"), "video/mp4");
//                startActivity(intent);
                break;
            case R.id.btn_login:
            case R.id.tv_startWorkAlert:
                showLoginDialog();
                break;
            case R.id.btn_logout:
                List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
                if (loginUsers != null && loginUsers.size() != 0) {
                    if (loginUsers.size() == 1) {
                        UserInfoBo userInfo = loginUsers.get(0);
                        mLogoutIndex = 0;
                        HttpHelper.positionLogout(userInfo.getCARD_NUMBER(), this);
                    } else {
                        showLogoutDialog();
                    }
                } else {
                    //数据异常的情况
                    logout();
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
                    case TopicUtil.TOPIC_SEW:

                        break;
                    case TopicUtil.TOPIC_SUSPEND:

                        break;
                    case TopicUtil.TOPIC_QC:

                        break;
                    case TopicUtil.TOPIC_PACKING:

                        break;
                }
                break;
            case R.id.btn_searchOrder:
                String position = mEt_position.getText().toString();
                if (!isEmpty(position)) {
                    HttpHelper.PAD_IP = position;
                } else {
                    toast("请输入完整的站位");
                }
                ContextInfoBo contextInfo = SpUtil.getContextInfo();
                if (contextInfo == null) {
                    showLoading();
                    HttpHelper.initData(this);
                } else if (mPositionInfo == null) {
                    isSearchOrder = true;
                    HttpHelper.findProcessWithPadId(this);
                    return;
                }
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
            case R.id.btn_unbind:
                if (mSuspendFragment != null) {
                    mSuspendFragment.unBind();
                }
                break;
        }
    }

    private void searchOrder() {
        String cardNum = mEt_orderNum.getText().toString();
        if (!isEmpty(cardNum)) {
            mCardInfo.setCardNum(cardNum);
            switch (mTopic) {
                case TopicUtil.TOPIC_CUT:
                    showLoading();
                    getCardInfo(cardNum);
                    break;
                case TopicUtil.TOPIC_SEW:
                    mSewFragment.getData(mCardInfo.getCardNum());
                    break;
                case TopicUtil.TOPIC_SUSPEND:
                    mSuspendFragment.searchOrder(cardNum);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_RECORD_NC) {
                mList_badData = (List<RecordNCBo>) data.getSerializableExtra("badList");
            } else if (requestCode == REQUEST_LOGIN) {
                HttpHelper.findProcessWithPadId(this);
            }
        }
    }

    private Dialog mLogoutDialog;
    private CommonRecyclerAdapter mLogoutAdapter;
    private int mLogoutIndex;

    private void showLogoutDialog() {
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers == null) {
            logout();
            return;
        }

        mLogoutDialog = new Dialog(mContext);
        mLogoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_logout, null);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.lv_logout);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        listView.setLayoutManager(manager);
        mLogoutAdapter = new CommonRecyclerAdapter<UserInfoBo>(mContext, loginUsers, R.layout.item_logout, manager) {
            @Override
            public void convert(RecyclerViewHolder holder, final UserInfoBo item, final int position) {
                holder.setText(R.id.tv_userName, item.getUSER());
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
            mTv_startWorkAlert.setVisibility(View.GONE);
            JSONObject result = resultJSON.getJSONObject("result");
            if (HttpHelper.queryPositionByPadIp_url.equals(url)) {
                ContextInfoBo contextInfoBo = JSON.parseObject(result.toString(), ContextInfoBo.class);
                SpUtil.saveContextInfo(contextInfoBo);
                List<UserInfoBo> loginUserList = contextInfoBo.getLOGIN_USER_LIST();
                SpUtil.savePositionUsers(loginUserList);
                initData();
            } else if (HttpHelper.getCardInfo.equals(url)) {
                if (result == null) {
                    toast("数据有误");
                    return;
                }
                String orderType = result.getString("ORDER_TYPE");
                mCardInfo.setCardType(orderType);
                mCardInfo.setValue(resultJSON.getString("RI"));
                switch (mTopic) {
                    case TopicUtil.TOPIC_CUT:
                        if ("P".equalsIgnoreCase(orderType) || "S".equalsIgnoreCase(orderType)) {
                            mCutFragment.getData(orderType, mCardInfo.getCardNum(), mPositionInfo.getRESR_INFOR().getRESOURCE_BO(), mCardInfo.getValue());
                        } else if ("M".equals(orderType)) {
                            clockIn(mCardInfo.getCardNum());
                        }
                        break;
                    case TopicUtil.TOPIC_SUSPEND:

                        break;
                }
            } else if (HttpHelper.findProcessWithPadId_url.equals(url)) {
                mPositionInfo = JSON.parseObject(result.toString(), PositionInfoBo.class);

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
            } else if (HttpHelper.positionLogin_url.equals(url)) {
                toast("用户上岗成功");
                onClockIn(true);
            } else if (HttpHelper.positionLogout_url.equals(url)) {
                toast("用户下岗成功");
                logoutSuccess();
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
            case TopicUtil.TOPIC_SEW:
                mSewFragment.onSuccess(url, resultJSON);
                break;
            case TopicUtil.TOPIC_SUSPEND:
                mSuspendFragment.onSuccess(url, resultJSON);
                break;
            case TopicUtil.TOPIC_QC:
                break;
            case TopicUtil.TOPIC_PACKING:
                break;
        }
    }

    private void logoutSuccess() {
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers != null && loginUsers.size() > mLogoutIndex) {
            loginUsers.remove(mLogoutIndex);
            SpUtil.savePositionUsers(loginUsers);
            if (loginUsers.size() == 0) {
                logout();
                return;
            }
            if (mLogoutAdapter != null) {
                mLogoutAdapter.removeData(mLogoutIndex);
            }
        }
        mCutFragment.refreshLoginUsers();
    }

    public void logout() {
        if (mLoginFragment == null) {
            initLoginFragment();
        }
        mLoginFragment.setType(LoginFragment.TYPE_CLOCK);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isAdded())
                    ft.hide(fragment);
            }
        }
        if (mLoginFragment.isAdded()) {
            ft.show(mLoginFragment);
        } else {
            ft.add(R.id.layout_content, mLoginFragment);
        }
        ft.commit();
        if (mLogoutDialog != null) {
            mLogoutDialog.dismiss();
        }
        mLogoutDialog = null;
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
            mTv_startWorkAlert.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClockIn(boolean success) {
        if (success) {
            mTv_startWorkAlert.setVisibility(View.GONE);
            if (mLoginDialog != null)
                mLoginDialog.dismiss();
            if (mPositionInfo == null) {
                HttpHelper.findProcessWithPadId(this);
            } else {
                changeFragment();
            }
        }
    }
}
