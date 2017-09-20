package com.eeka.mespad.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.manager.UpdateManager;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;

/**
 * 设置界面
 * Created by Lenovo on 2017/9/4.
 */

public class SettingActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 1;

    private Switch mDebugSwitch;

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
        findViewById(R.id.layout_debugSwitch).setOnClickListener(this);

        mDebugSwitch = (Switch) findViewById(R.id.switch_debug);
        mDebugSwitch.setChecked(SpUtil.isDebugLog());

        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本：" + SystemUtils.getAppVersionName(mContext) + "(" + SystemUtils.getAppVersionCode(mContext) + ")");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_setLoginUser:
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
            case R.id.tv_checkUpdate:
                UpdateManager.downloadApk(mContext);
                break;
            case R.id.layout_debugSwitch:
                mDebugSwitch.setChecked(!mDebugSwitch.isChecked());
                SpUtil.setDebugLog(mDebugSwitch.isChecked());
                break;
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
