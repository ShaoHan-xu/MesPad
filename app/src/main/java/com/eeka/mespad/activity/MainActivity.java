package com.eeka.mespad.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.RecordBadBo;
import com.eeka.mespad.bo.ReturnMaterialInfoBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.fragment.CutFragment;
import com.eeka.mespad.fragment.LoginFragment;
import com.eeka.mespad.fragment.SuspendFragment;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.service.MQTTService;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.view.dialog.ReturnMaterialDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/6/12.
 */

public class MainActivity extends BaseActivity implements LoginFragment.OnLoginCallback {

    private DrawerLayout mDrawerLayout;

    private FragmentManager mFragmentManager;
    private CutFragment mCutFragment;
    private SuspendFragment mSuspendFragment;

    private LinearLayout mLayout_controlPanel;

    private ReturnMaterialInfoBo mReturnMaterialInfo;//退料
    private ReturnMaterialInfoBo mAddMaterialInfo;//补料
    private List<RecordBadBo> mList_badData;

    private PositionInfoBo mPositionInfo;

    private Dialog mLoginDialog;

    private EditText mEt_orderNum;
    private EditText mEt_position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);

        initView();
        initData();

        MQTTService.actionStart(mContext);
    }

    @Override
    protected void initView() {
        super.initView();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mLayout_controlPanel = (LinearLayout) findViewById(R.id.layout_controlPanel);

        findViewById(R.id.tv_caijian).setOnClickListener(this);
        findViewById(R.id.tv_diaogua).setOnClickListener(this);

        findViewById(R.id.btn_searchOrderNum).setOnClickListener(this);
        findViewById(R.id.btn_searchPosition).setOnClickListener(this);

        mEt_orderNum = (EditText) findViewById(R.id.et_orderNum);
        mEt_position = (EditText) findViewById(R.id.et_position);
        CheckBox checkBox = (CheckBox) findViewById(R.id.ckb_custom);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEt_orderNum.setText("GR_SO_MTM_01");
                } else {
                    mEt_orderNum.setText("RFID00000013");
                }
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        mFragmentManager = getSupportFragmentManager();
        mCutFragment = new CutFragment();
        mSuspendFragment = new SuspendFragment();

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.layout_content, mCutFragment);
        ft.commit();

        HttpHelper.findProcessWithPadId(null, this);
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
                    mCutFragment.showCompleteButton();
                    continue;
            }
            mLayout_controlPanel.addView(button);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        SystemUtils.hideKeyboard(mContext, v);
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
                    TailorInfoBo materialData = mCutFragment.getMaterialData();
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
                    TailorInfoBo materialData = mCutFragment.getMaterialData();
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
                mCutFragment.showRecordLabuDialog();
                break;
            case R.id.btn_jobList:
                startActivity(new Intent(mContext, WorkOrderListActivity.class));
                break;
            case R.id.btn_NcRecord:
                startActivityForResult(RecordBadActivity.getIntent(mContext, mCutFragment.getMaterialData(), mList_badData), 0);
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
                    //数据异常的情况，直接退回登录界面
                    logout();
                }
                break;
            case R.id.btn_signOff:
                startActivity(VideoPlayerActivity.getIntent(mContext, "http://10.7.121.75/gst/test.MP4"));
                break;
            case R.id.btn_searchOrderNum:
                if (mPositionInfo == null) {
                    toast("请先获取站位数据");
                    return;
                }
                EditText et_orderNum = (EditText) findViewById(R.id.et_orderNum);
                String searchKey = et_orderNum.getText().toString();
                CheckBox checkBox = (CheckBox) findViewById(R.id.ckb_custom);
                if (!isEmpty(searchKey)) {
                    if (checkBox.isChecked()) {
                        HttpHelper.viewCutPadInfo(null, searchKey, mPositionInfo.getRESR_INFOR().getRESOURCE_BO(), this);
                    } else {
                        HttpHelper.viewCutPadInfo(searchKey, null, mPositionInfo.getRESR_INFOR().getRESOURCE_BO(), this);
                    }
                }
                break;
            case R.id.btn_searchPosition:
                EditText et_position = (EditText) findViewById(R.id.et_position);
                String position = et_position.getText().toString();
                if (!isEmpty(position)) {
                    HttpHelper.findProcessWithPadId(position, this);
                }
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

    private CommonRecyclerAdapter mLogoutAdapter;
    private int mLogoutIndex;

    private void showLogoutDialog() {
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers == null) {
            toast("无登录用户");
            return;
        }

        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        dialog.setContentView(view);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout((int) (SystemUtils.getScreenWidth(this) * 0.5), (int) (SystemUtils.getScreenHeight(this) * 0.5));
    }

    private void changeFragment(int position) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (position == 0) {
            ft.hide(mSuspendFragment).show(mCutFragment);
        } else if (position == 1) {
            ft.hide(mCutFragment);
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
            if (HttpHelper.findProcessWithPadId_url.equals(url)) {
                mPositionInfo = JSON.parseObject(resultJSON.getJSONObject("result").toString(), PositionInfoBo.class);
                if (mPositionInfo.getBUTTON_INFOR() != null) {
                    initButton(mPositionInfo.getBUTTON_INFOR());
                }
                mCutFragment.onSuccess(url, resultJSON);
            } else if (HttpHelper.viewCutPadInfo_url.equals(url)) {
                mCutFragment.onSuccess(url, resultJSON);
            } else if (HttpHelper.positionLogout_url.equals(url)) {
                toast("用户下线成功");
                List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
                if (loginUsers != null && loginUsers.size() > mLogoutIndex) {
                    loginUsers.remove(mLogoutIndex);
                    SpUtil.savePositionUsers(loginUsers);
                    if (loginUsers.size() == 0) {
                        logout();
                    }
                }
                if (mLogoutAdapter != null) {
                    mLogoutAdapter.removeData(mLogoutIndex);
                }

                mCutFragment.refreshLoginUsers();
            }
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
    public void loginCallback(boolean success) {
        if (success) {
            mLoginDialog.dismiss();
            mCutFragment.refreshLoginUsers();
        }
    }
}
