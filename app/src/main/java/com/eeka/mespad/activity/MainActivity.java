package com.eeka.mespad.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.RecordBadBo;
import com.eeka.mespad.bo.ReturnMaterialInfoBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.fragment.CutFragment;
import com.eeka.mespad.fragment.LoginFragment;
import com.eeka.mespad.fragment.SuspendFragment;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.view.dialog.ReturnMaterialDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/6/12.
 */

public class MainActivity extends BaseActivity implements LoginFragment.OnLoginCallback {

    private DrawerLayout mDrawerLayout;

    private FragmentManager mFragmentManager;
    private CutFragment mMainFragment;
    private SuspendFragment mSuspendFragment;

    private LinearLayout mLayout_controlPanel;

    private ReturnMaterialInfoBo mReturnMaterialInfo;//退料
    private ReturnMaterialInfoBo mAddMaterialInfo;//补料
    private List<RecordBadBo> mList_badData;

    private PositionInfoBo mPositionInfo;

    private Dialog mLoginDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mLayout_controlPanel = (LinearLayout) findViewById(R.id.layout_controlPanel);

        findViewById(R.id.tv_caijian).setOnClickListener(this);
        findViewById(R.id.tv_diaogua).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null) {
            showLoading();
            HttpHelper.queryPositionByPadIp(this);
        }
        mFragmentManager = getSupportFragmentManager();
        mMainFragment = new CutFragment();
        mSuspendFragment = new SuspendFragment();

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.layout_content, mMainFragment);
        ft.commit();
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
                    mMainFragment.showCompleteButton();
                    continue;
            }
            mLayout_controlPanel.addView(button);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_caijian:
                changeFragment(0);
                mLayout_controlPanel.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.tv_diaogua:
                changeFragment(1);
                mLayout_controlPanel.setVisibility(View.GONE);
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.btn_materialReturn:
                if (mReturnMaterialInfo == null) {
                    mReturnMaterialInfo = new ReturnMaterialInfoBo();
                    TailorInfoBo materialData = mMainFragment.getMaterialData();
                    if (materialData == null) {
                        toast("获取数据失败，请重新获取数据");
                        return;
                    }
                    mReturnMaterialInfo.setOrderNum(materialData.getSHOP_ORDER_INFOR().getSHOP_ORDER());
                    List<TailorInfoBo.MatInfoBean> itemArray = materialData.getMAT_INFOR();
                    List<ReturnMaterialInfoBo.MaterialInfoBo> materialList = new ArrayList<>();
                    for (TailorInfoBo.MatInfoBean item : itemArray) {
                        ReturnMaterialInfoBo.MaterialInfoBo material = new ReturnMaterialInfoBo.MaterialInfoBo();
                        material.setPicUrl(item.getMAT_URL());
                        material.setNum(item.getMAT_NO());
                        materialList.add(material);
                    }
                    mReturnMaterialInfo.setMaterialInfoList(materialList);
                }
                new ReturnMaterialDialog(mContext, ReturnMaterialDialog.TYPE_RETURN, mReturnMaterialInfo).show();
                break;
            case R.id.btn_materialFeeding:
                if (mAddMaterialInfo == null) {
                    mAddMaterialInfo = new ReturnMaterialInfoBo();
                    TailorInfoBo materialData = mMainFragment.getMaterialData();
                    if (materialData == null) {
                        toast("获取数据失败，请重新获取数据");
                        return;
                    }
                    mAddMaterialInfo.setOrderNum(materialData.getSHOP_ORDER_INFOR().getSHOP_ORDER());
                    List<TailorInfoBo.MatInfoBean> itemArray = materialData.getMAT_INFOR();
                    List<ReturnMaterialInfoBo.MaterialInfoBo> materialList = new ArrayList<>();
                    for (TailorInfoBo.MatInfoBean item : itemArray) {
                        ReturnMaterialInfoBo.MaterialInfoBo material = new ReturnMaterialInfoBo.MaterialInfoBo();
                        material.setPicUrl(item.getMAT_URL());
                        material.setNum(item.getMAT_NO());
                        materialList.add(material);
                    }
                    mAddMaterialInfo.setMaterialInfoList(materialList);
                }
                new ReturnMaterialDialog(mContext, ReturnMaterialDialog.TYPE_ADD, mAddMaterialInfo).show();
                break;
            case R.id.btn_dataCollect:
                mMainFragment.showRecordLabuDialog();
                break;
            case R.id.btn_jobList:
                startActivity(new Intent(mContext, WorkOrderListActivity.class));
                break;
            case R.id.btn_NcRecord:
                startActivityForResult(RecordBadActivity.getIntent(mContext, mMainFragment.getMaterialData(), mList_badData), 0);
                break;
            case R.id.btn_video:
                //自定义播放器，可缓存视频到本地
                startActivity(VideoPlayerActivity.getIntent(mContext, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));

                //系统自带视频播放，无缓存
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"), "video/mp4");
//                startActivity(intent);
                break;
            case R.id.btn_login:
                showLoginDialog();
                break;
            case R.id.btn_logout:
                startActivity(new Intent(mContext, LoginActivity.class));
                SpUtil.saveLoginStatus(false);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                mList_badData = (List<RecordBadBo>) data.getSerializableExtra("badList");
            }
        }
    }

    /**
     * 显示登录弹框
     */
    private void showLoginDialog() {
        mLoginDialog = new Dialog(mContext);
        mLoginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoginDialog.setContentView(R.layout.dlg_login);

        final LoginFragment loginFragment = (LoginFragment) mFragmentManager.findFragmentById(R.id.loginFragment);
        loginFragment.setCallback(this);

        mLoginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mFragmentManager.beginTransaction().remove(loginFragment).commit();
            }
        });
        mLoginDialog.show();
    }

    private void changeFragment(int position) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (position == 0) {
            ft.hide(mSuspendFragment).show(mMainFragment);
        } else if (position == 1) {
            ft.hide(mMainFragment);
            if (mSuspendFragment.isAdded()) {
                ft.show(mSuspendFragment);
            } else {
                ft.add(R.id.layout_content, mSuspendFragment);
            }
        }
        ft.commit();
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.queryPositionByPadIp_url.equals(url)) {
                ContextInfoBo contextInfoBo = JSON.parseObject(resultJSON.getJSONObject("result").toString(), ContextInfoBo.class);
                SpUtil.saveContextInfo(contextInfoBo);
            } else if (HttpHelper.login_url.equals(url)) {
                HttpHelper.findProcessWithPadId("", this);
            } else if (HttpHelper.findProcessWithPadId_url.equals(url)) {
                mPositionInfo = JSON.parseObject(resultJSON.getJSONObject("result").toString(), PositionInfoBo.class);
                if (mPositionInfo.getBUTTON_INFOR() != null) {
                    initButton(mPositionInfo.getBUTTON_INFOR());
                }
            }
            mMainFragment.onSuccess(url, resultJSON);
        } else {
            toast(resultJSON.getString("message"));
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        super.onFailure(url, code, message);
        toast(message);
    }

    @Override
    public void loginCallback(boolean success, UserInfoBo userInfo) {
        if (success) {
            mLoginDialog.dismiss();
            mMainFragment.loginCallback(true, userInfo);
        }
    }
}
