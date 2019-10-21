package com.eeka.mespad.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.PocketSizeBo;
import com.eeka.mespad.bo.ProcessSheetsBo;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.fragment.LoginFragment;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.ToastUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.LoadingDialog;
import com.eeka.mespad.view.dialog.ProcessSheetsDialog;
import com.eeka.mespad.zxing.android.CaptureActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity基类
 * Created by Lenovo on 2017/5/13.
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements View.OnClickListener, HttpCallback, LoginFragment.OnLoginCallback, LoginFragment.OnClockCallback {

    private static final String DECODED_CONTENT_KEY = "codedContent";
    public static final int REQUEST_CODE_SCAN = 999;

    public static int REQUEST_PERMISSION = 998;

    protected Context mContext;

    protected FragmentManager mFragmentManager;
    protected Dialog mLoginDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;
        mFragmentManager = getSupportFragmentManager();

    }

    protected void initView() {
    }

    protected void initData() {
    }

    /**
     * 刷卡获取卡信息
     */
    public void getCardInfo(String orderNum) {
        showLoading();
        HttpHelper.getCardInfo(orderNum, this);
    }

    /**
     * 刷卡上岗
     */
    public void clockIn(String cardNum) {
        HttpHelper.positionLogin(cardNum, this);
    }

    /**
     * 刷卡上岗
     */
    public void clockOut(String cardNum) {
        HttpHelper.positionLogout(cardNum, this);
    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    protected void showLoading() {
        showLoading(getString(R.string.loading), true);
    }

    protected void showLoading(String msg, boolean cancelAble) {
        LoadingDialog.show(mContext, msg);
    }

    protected void dismissLoading() {
        LoadingDialog.dismiss();
    }

    protected void showErrorDialog(String msg) {
        ErrorDialog.showAlert(mContext, msg);
    }

    protected void showAlert(String msg) {
        ErrorDialog.showAlert(mContext, msg, ErrorDialog.TYPE.ALERT, null, false);
    }

    protected void toast(String msg) {
        toast(msg, Toast.LENGTH_LONG);
    }

    protected void toast(String msg, int duration) {
        ToastUtil.showToast(this, msg, duration);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_cutMatInfo:
                String salesOrder = SpUtil.getSalesOrder();
                if (isEmpty(salesOrder)) {
                    ErrorDialog.showAlert(mContext, "找不到该确认单");
                    return;
                }
                HttpHelper.getCutMatInfoPic("query.cutConfirm", salesOrder, this);
                break;
            case R.id.btn_cutBmp:
                List<Integer> bmpRes = new ArrayList<>();
                bmpRes.add(R.drawable.clothingparts1);
                bmpRes.add(R.drawable.clothingparts2);
                bmpRes.add(R.drawable.clothingparts3);
                bmpRes.add(R.drawable.clothingparts4);
                bmpRes.add(R.drawable.clothingparts5);
                bmpRes.add(R.drawable.clothingparts6);
                startActivity(ImageBrowserActivity.getIntent(mContext, bmpRes, true));
                break;
        }
    }

    protected boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void requestPermission(String[] permissions) {
        requestPermissions(permissions, REQUEST_PERMISSION);
    }

    protected boolean allowAllPermission;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            allowAllPermission = false;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allowAllPermission = false;
                    break;
                }
                allowAllPermission = true;
            }
            if (allowAllPermission) {
                if (isScan) {
                    startScan();
                    isScan = false;
                }
            } else {
                Toast.makeText(mContext, "该功能需要授权方可使用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 显示登录弹框
     */
    public void showLoginDialog() {
        mLoginDialog = new Dialog(mContext);
        mLoginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoginDialog.setContentView(R.layout.dlg_login);

        final LoginFragment loginFragment = (LoginFragment) mFragmentManager.findFragmentById(R.id.loginFragment);
        assert loginFragment != null;
        loginFragment.setOnClockCallback(this);

        mLoginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mFragmentManager.beginTransaction().remove(loginFragment).commit();
            }
        });
        mLoginDialog.show();
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        dismissLoading();
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getCommonInfoByLogicNo.equals(url)) {
                JSONArray result = resultJSON.getJSONArray("result");
                if (result != null && result.size() != 0) {
                    List<PocketSizeBo> items = JSON.parseArray(result.toString(), PocketSizeBo.class);
                    String picUrl = items.get(0).getCONFIRM_URL();
                    List<String> list = new ArrayList<>();
                    list.add(picUrl);
                    startActivity(ImageBrowserActivity.getIntent(mContext, list, 0));
                } else {
                    showErrorDialog("找不到该工单的条格面料裁剪确认单");
                }
            }
        } else {
            showErrorDialog(HttpHelper.getMessage(resultJSON));
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        dismissLoading();
        ErrorDialog.showAlert(mContext, message);
    }

    @Override
    public void onLogin(boolean success) {
    }

    @Override
    public void onClockIn(boolean success) {
    }

    private boolean isScan;

    public void startScan() {
        if (!checkPermission(Manifest.permission.CAMERA)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission(Manifest.permission.CAMERA)) {
                isScan = true;
                requestPermission(new String[]{Manifest.permission.CAMERA});
                return;
            }
        }
        Intent intent = new Intent(mContext, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                //返回的文本内容
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                //返回的BitMap图像
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);

                PushJson pushJson = new PushJson();
                pushJson.setType(PushJson.TYPE_SCAN);
                pushJson.setContent(content);
                EventBus.getDefault().post(pushJson);
            }
        }
    }


}
