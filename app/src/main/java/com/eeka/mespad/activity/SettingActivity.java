package com.eeka.mespad.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.UpdateManager;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;

/**
 * 设置界面
 * Created by Lenovo on 2017/9/4.
 */

public class SettingActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 1;

    private Switch mDebugSwitch;
    private RelativeLayout mLayout_setSystem;
    private TextView mTv_systemCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setting);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.tv_setLoginUser).setOnClickListener(this);
        findViewById(R.id.tv_checkUpdate).setOnClickListener(this);
        findViewById(R.id.layout_setSystem).setOnClickListener(this);

        mLayout_setSystem = (RelativeLayout) findViewById(R.id.layout_setSystem);
        mLayout_setSystem.setOnClickListener(this);

        mDebugSwitch = (Switch) findViewById(R.id.switch_debug);
        mDebugSwitch.setChecked(SpUtil.isDebugLog());

        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setOnClickListener(this);
        tv_version.setText("版本：" + SystemUtils.getAppVersionName(mContext) + "(" + SystemUtils.getAppVersionCode(mContext) + ")");

        mTv_systemCode = (TextView) findViewById(R.id.tv_setting_system);
        String systemCode = SpUtil.get(SpUtil.KEY_SYSTEMCODE, null);
        if (!TextUtils.isEmpty(systemCode)) {
            if ("D".equals(systemCode)) {
                mTv_systemCode.setText("D系统");
            } else if ("Q".equals(systemCode)) {
                mTv_systemCode.setText("Q系统");
            } else if ("P".equals(systemCode)) {
                mTv_systemCode.setText("P系统");
            }
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_setLoginUser:
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
            case R.id.tv_checkUpdate:
//                showLoading();
//                HttpHelper.getAPKUrl(this);
                Beta.checkUpgrade();
                break;
            case R.id.layout_debugSwitch:
                mDebugSwitch.setChecked(!mDebugSwitch.isChecked());
                SpUtil.setDebugLog(mDebugSwitch.isChecked());
                break;
            case R.id.tv_version:
                openSystemEnvironment();
                break;
            case R.id.layout_setSystem:
                setSystemCode();
                break;
        }
    }

    private int mClickCount;
    private long mLastMillis;

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

    /**
     * 设置系统环境
     */
    private void setSystemCode() {
        int checked = -1;
        String s = mTv_systemCode.getText().toString();
        if (!isEmpty(s)) {
            if (s.contains("D")) {
                checked = 0;
            } else if (s.contains("Q")) {
                checked = 1;
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
                        PadApplication.BASE_URL = PadApplication.BASE_URL_D;
                    } else if (which == 1) {
                        SpUtil.save(SpUtil.KEY_SYSTEMCODE, "Q");
                        PadApplication.BASE_URL = PadApplication.BASE_URL_Q;
                    }
                    ErrorDialog.showConfirmAlert(mContext, "系统切换成功，重启应用后生效。", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PushJson push = new PushJson();
                            push.setType(PushJson.TYPE_EXIT);
                            EventBus.getDefault().post(push);
                            finish();
                        }
                    });
                }
            }
        });

        builder.show();
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.getApkUrl.equals(url)) {
            if (HttpHelper.isSuccess(resultJSON)) {
                UpdateManager.downloadApk(mContext, resultJSON.getString("result"));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                toast("设置成功，正在刷新数据");
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }
        }
    }
}
