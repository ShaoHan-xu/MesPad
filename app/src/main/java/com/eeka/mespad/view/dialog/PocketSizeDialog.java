package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.bo.PocketSizeBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 袋口尺寸信息弹框
 */
public class PocketSizeDialog extends BaseDialog {

    private String mShopOrder;
    private String mSFC;
    private String mSize;
    private String mOperation;
    private List<PocketSizeBo> mItems;
    private LinearLayout mLayout_item;

    public PocketSizeDialog(@NonNull Context context, String shopOrder, String sfc, String sizeCode, String operation) {
        super(context);
        mShopOrder = shopOrder;
        mSFC = sfc;
        mSize = sizeCode;
        mOperation = operation;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_pocketsize, null);
        setContentView(view);
        mLayout_item = view.findViewById(R.id.layout_pocketSize);

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initData() {
        LoadingDialog.show(mContext);
        HttpHelper.getCommonInfoByLogicNo("query.cadSizeInfo", mShopOrder, mSFC, mSize, mOperation, null, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    JSONArray result = resultJSON.getJSONArray("result");
                    if (result != null && result.size() != 0) {
                        mItems = JSON.parseArray(result.toString(), PocketSizeBo.class);
                        initView();
                    } else {
                        ErrorDialog.showAlert(mContext, "该工序无袋口尺寸数据", ErrorDialog.TYPE.ERROR, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismiss();
                            }
                        }, false);
                    }
                } else {
                    ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
                }
            }

            @Override
            public void onFailure(String url, int code, String message) {
                LoadingDialog.dismiss();
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    private void initView() {
        for (final PocketSizeBo item : mItems) {
            View child = LayoutInflater.from(mContext).inflate(R.layout.item_pocketsize, null);
            TextView tv_size = child.findViewById(R.id.tv_pocketSize_size);
            TextView tv_code = child.findViewById(R.id.tv_pocketSize_code);
            TextView tv_QKJD = child.findViewById(R.id.tv_pocketSize_QKJD);
            TextView tv_QDC = child.findViewById(R.id.tv_pocketSize_QDC);
            TextView tv_DGK = child.findViewById(R.id.tv_pocketSize_DGK);
            tv_size.setText(item.getVALUE());
            tv_code.setText(item.getPART_NO());
            tv_QKJD.setText(item.getQKJD_VALUE());
            tv_QDC.setText(item.getQDC_VALUE());
            tv_DGK.setText(item.getDGK_VALUE());

            ImageView imageView = child.findViewById(R.id.iv_pocketSize_img);
            Picasso.with(mContext).load(item.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> list = new ArrayList<>();
                    list.add(item.getPICTURE_URL());
                    mContext.startActivity(ImageBrowserActivity.getIntent(mContext, list, 0));
                }
            });

            mLayout_item.addView(child);
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.7), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
        initData();
    }
}
