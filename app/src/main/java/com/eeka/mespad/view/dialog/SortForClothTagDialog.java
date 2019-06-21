package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.PattermUtil;
import com.eeka.mespad.utils.SystemUtils;

/**
 * 根据吊牌走分拣系统
 */
public class SortForClothTagDialog extends BaseDialog {

    private EditText mEt_hangerId;
    private EditText mEt_tag;
    private String mLastNum;
    private String mLastTagNum;

    public SortForClothTagDialog(@NonNull Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_sortfortag, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("通过吊牌号走分拣系统");

        mEt_hangerId = mView.findViewById(R.id.et_sortForTag_hangerId);
        mEt_hangerId.requestFocus();
        SystemUtils.showSoftInputFromWindow(mContext);
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

        mEt_tag = mView.findViewById(R.id.et_sortForTag_tag);
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
                        reqFocus(SortForClothTagDialog.this.mEt_hangerId);
                    }
                    return true;
                }
                return false;
            }
        });

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
    }

    private void reqFocus(final View v) {
        String tag = mEt_tag.getText().toString();
        String hangerId = mEt_hangerId.getText().toString();
        if (!isEmpty(tag) && !isEmpty(hangerId)) {
            done();
            return;
        }
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.requestFocus();
            }
        }, 50);
    }

    private void done() {
        if (TextUtils.isEmpty(mLastNum) || mLastNum.length() != 10) {
            ErrorDialog.showAlert(mContext, "衣架号有误，请核对");
            return;
        }

        LoadingDialog.show(mContext);
        HttpHelper.sortForClothTag(mLastTagNum, mLastNum, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
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

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.6), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }
}
