package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.http.WebServiceUtils;
import com.eeka.mespad.utils.PattermUtil;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TopicUtil;

/**
 * 分拣扫码弹框
 */
public class SortingDialog extends BaseDialog implements View.OnClickListener {

    private String mTopic;
    private OnClickListener mListener;

    public SortingDialog(@NonNull Context context, String topic, @NonNull OnClickListener listener) {
        super(context);
        mTopic = topic;
        mListener = listener;
        init();
    }

    private EditText mEt_hangerId;
    private EditText mEt_tag;
    private String mLastNum;
    private String mLastTagNum;

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_sorting, null);
        setContentView(view);

        mEt_hangerId = view.findViewById(R.id.et_sorting_code);
        mEt_hangerId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String orderNum = mEt_hangerId.getText().toString();
                    if (!TextUtils.isEmpty(mLastNum)) {
                        mLastNum = orderNum.replaceFirst(mLastNum, "");
                    } else {
                        mLastNum = orderNum;
                    }
                    mEt_hangerId.setText(mLastNum);
                    reqFocus(mEt_tag);
                    return true;
                }
                return false;
            }
        });

        mEt_tag = view.findViewById(R.id.et_sorting_tag);
        mEt_tag.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String value = mEt_tag.getText().toString();
                    if (!TextUtils.isEmpty(mLastTagNum)) {
                        mLastTagNum = value.replaceFirst(mLastTagNum, "");
                    } else {
                        mLastTagNum = value;
                    }

                    //如果扫描的结果是纯数字，则是扫错了条码
                    if (PattermUtil.isNumeric(mLastTagNum)) {
                        Toast.makeText(mContext, "条码扫描错误，请重新扫码！", Toast.LENGTH_SHORT).show();
                    } else {
                        mEt_tag.setText(mLastTagNum);
                        reqFocus(mEt_hangerId);
                    }
                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.btn_ok).setOnClickListener(this);
        Button btn_jump = view.findViewById(R.id.btn_jump);
        if (TopicUtil.TOPIC_MANUAL.equals(mTopic)) {
            btn_jump.setVisibility(View.VISIBLE);
            btn_jump.setOnClickListener(this);
        }
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEt_hangerId.requestFocus();
                SystemUtils.showSoftInputFromWindow(mContext);
            }
        }, 500);
    }

    private void reqFocus(final View v) {
        String tag = mEt_tag.getText().toString();
        String hangerId = mEt_hangerId.getText().toString();
        if (!isEmpty(tag) && !isEmpty(hangerId)) {
            checkItemSize();
            return;
        }
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.requestFocus();
            }
        }, 50);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                checkItemSize();
                break;
            case R.id.btn_jump:
                dismiss();
                mListener.onClick(null, 0);
                break;
        }
    }

    private void checkItemSize() {
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null) {
            ErrorDialog.showAlert(mContext, "站位数据未获取，请重启应用获取");
        } else {
            mLastNum = mEt_hangerId.getText().toString();
            mLastTagNum = mEt_tag.getText().toString();
            if (TextUtils.isEmpty(mLastNum)) {
                Toast.makeText(mContext, "请输入衣架号继续操作", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(mLastTagNum)) {
                Toast.makeText(mContext, "请输入吊牌号继续操作", Toast.LENGTH_SHORT).show();
                return;
            } else if (mLastNum.length() != 10) {
                Toast.makeText(mContext, "输入的衣架号非10位，请查验", Toast.LENGTH_SHORT).show();
                return;
            }
            LoadingDialog.show(mContext);
            HttpHelper.checkItemAndSize(contextInfo.getLINE_CATEGORY(), contextInfo.getPOSITION(), mLastTagNum, new HttpCallback() {
                @Override
                public void onSuccess(String url, JSONObject resultJSON) {
                    if (HttpHelper.isSuccess(resultJSON)) {
                        sorting();
                    } else {
                        LoadingDialog.dismiss();
                        ErrorDialog.showAlert(mContext, resultJSON.getString("result"));
                    }
                }

                @Override
                public void onFailure(String url, int code, String message) {
                    LoadingDialog.dismiss();
                    ErrorDialog.showAlert(mContext, message);
                }
            });
        }
    }

    private void sorting() {
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        WebServiceUtils.sendProductMessage(contextInfo.getLINE_CATEGORY(), contextInfo.getPOSITION(), mLastNum, new WebServiceUtils.HttpCallBack() {
            @Override
            public void onSuccess(String method, JSONObject result) {
                LoadingDialog.dismiss();
                dismiss();
                Toast.makeText(mContext, result.getString("message"), Toast.LENGTH_LONG).show();
                mListener.onClick(null, 1);
            }

            @Override
            public void onFail(String errMsg) {
                LoadingDialog.dismiss();
                //webservice的接口报错时都会有推送，所以此处不需要显示
//                    ErrorDialog.showAlert(mContext, errMsg);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }

}
