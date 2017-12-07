package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;

/**
 * 自动拣选dialog
 * Created by Lenovo on 2017/11/9.
 */
public class AutoPickDialog extends BaseDialog {

    private String mShopOrder;
    private String mItemCode;
    private String mLocationType;

    /**
     * @param locationType 库位类型 20=裁剪段，30=上裁段
     */
    public AutoPickDialog(@NonNull Context context, String shopOrder, String itemCode, String locationType) {
        super(context);
        mShopOrder = shopOrder;
        mItemCode = itemCode;
        mLocationType = locationType;
        init(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_auto_pick, null);
        setContentView(mView);
        setCanceledOnTouchOutside(true);

        TextView tv_shopOrder = (TextView) mView.findViewById(R.id.tv_autoPick_shopOrder);
        TextView tv_lot = (TextView) mView.findViewById(R.id.tv_autoPick_lot);
        tv_shopOrder.setText(mShopOrder);
        tv_lot.setText(mItemCode);

        mView.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialog.show(mContext);
                HttpHelper.autoPicking(mShopOrder, mItemCode, mLocationType, new HttpCallback() {
                    @Override
                    public void onSuccess(String url, JSONObject resultJSON) {
                        LoadingDialog.dismiss();
                        if (HttpHelper.isSuccess(resultJSON)) {
                            Toast.makeText(mContext, "自动拣选调用成功", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
                        }
                    }

                    @Override
                    public void onFailure(String url, int code, String message) {
                        LoadingDialog.dismiss();
                        ErrorDialog.showAlert(mContext, message);
                    }
                });
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }

}
