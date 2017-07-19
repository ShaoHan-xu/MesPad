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
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.ReturnMaterialInfoBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.fragment.LoginFragment;
import com.eeka.mespad.fragment.MainFragment;
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
    private MainFragment mMainFragment;
    private SuspendFragment mSuspendFragment;

    private LinearLayout mLayout_controlPanel;

    private ReturnMaterialInfoBo mReturnMaterialInfo;//退料
    private ReturnMaterialInfoBo mAddMaterialInfo;//补料

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
        findViewById(R.id.btn_returnMaterials).setOnClickListener(this);
        findViewById(R.id.btn_getMaterials).setOnClickListener(this);
        findViewById(R.id.btn_recordLabu).setOnClickListener(this);
        findViewById(R.id.btn_orderList).setOnClickListener(this);
        findViewById(R.id.btn_recordBad).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_quit).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        mFragmentManager = getSupportFragmentManager();
        mMainFragment = new MainFragment();
        mSuspendFragment = new SuspendFragment();

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.layout_content, mMainFragment);
        ft.commit();

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
            case R.id.btn_returnMaterials:
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
            case R.id.btn_getMaterials:
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
            case R.id.btn_recordLabu:
                mMainFragment.showRecordLabuDialog();
                break;
            case R.id.btn_orderList:
                startActivity(new Intent(mContext, WorkOrderListActivity.class));
                break;
            case R.id.btn_recordBad:
                startActivity(new Intent(mContext, RecordBadActivity.class));
                break;
            case R.id.btn_login:
                showLoginDialog();
                break;
            case R.id.btn_pause:
                toast("暂停");
                break;
            case R.id.btn_quit:
                startActivity(new Intent(mContext, LoginActivity.class));
                SpUtil.saveLoginStatus(false);
                finish();
                break;
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
    public void loginCallback(boolean success, UserInfoBo userInfo) {
        if (success) {
            mLoginDialog.dismiss();
            HttpHelper.findProcessWithPadId("", mMainFragment);
        }
    }
}
