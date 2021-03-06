package com.eeka.mespad.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;

/**
 * 设置界面
 * Created by Lenovo on 2017/9/4.
 */

public class SettingActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 1;

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
        findViewById(R.id.tv_checkUpdate).setOnClickListener(this);
        TextView tv_setIP = findViewById(R.id.tv_setLoginUser);
        tv_setIP.setOnClickListener(this);

        TextView tv_version = findViewById(R.id.tv_version);
        if (SystemUtils.isApkInDebug(mContext)) {
            tv_setIP.setVisibility(View.VISIBLE);
            tv_version.setOnClickListener(this);
        }
        StringBuilder sb = new StringBuilder("版本：");
        sb.append(getString(R.string.app_channel)).append("_");
        sb.append(SystemUtils.getAppVersionName(mContext));
        if (SystemUtils.isApkInDebug(mContext)) {
            sb.append("_debug");
        }
        tv_version.setText(sb.toString());

        if (SystemUtils.isApkInDebug(mContext)) {
            mLayout_setSystem = findViewById(R.id.layout_setSystem);
            mLayout_setSystem.setOnClickListener(this);

            mTv_systemCode = findViewById(R.id.tv_setting_system);
            String systemCode = SpUtil.get(SpUtil.KEY_SYSTEMCODE, null);
            if (!TextUtils.isEmpty(systemCode)) {
                mTv_systemCode.setText(systemCode);
            } else {
                mTv_systemCode.setText(getString(R.string.system_code));
            }
        }

        Switch mDebugSwitch = findViewById(R.id.switch_debug);
        mDebugSwitch.setChecked(SpUtil.isDebugLog());
        mDebugSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtil.setDebugLog(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_setLoginUser:
                startActivity(new Intent(mContext, LoginActivity.class));
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
