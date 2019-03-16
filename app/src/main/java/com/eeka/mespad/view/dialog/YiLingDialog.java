package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
 * 衣领号弹框
 */
public class YiLingDialog extends BaseDialog {

    private String mShopOrder;
    private String mSFC;
    private String mSize;
    private String mOperation;
    private List<PocketSizeBo> mItems;

    public YiLingDialog(@NonNull Context context, String shopOrder, String sfc, String size, String operation) {
        super(context);
        mShopOrder = shopOrder;
        mSFC = sfc;
        mSize = size;
        mOperation = operation;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_yiling, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("衣领号显示");

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        ErrorDialog.showAlert(mContext, "该工序无衣领数据", ErrorDialog.TYPE.ERROR, new View.OnClickListener() {
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
        final PocketSizeBo item = mItems.get(0);
        TextView tv_zll = mView.findViewById(R.id.tv_yiLing_zll);
        TextView tv_cyl = mView.findViewById(R.id.tv_yiLing_cyl);
        TextView tv_fyl = mView.findViewById(R.id.tv_yiLing_fyl);
        TextView tv_ldh = mView.findViewById(R.id.tv_yiLing_ldh);
        tv_zll.setText(item.getZLL_VALUE());
        tv_cyl.setText(item.getCYL_VALUE());
        tv_fyl.setText(item.getFYL_VALUE());
        tv_ldh.setText(item.getLDH_VALUE());

        ImageView imageView = mView.findViewById(R.id.iv_yiLing_img);
        Picasso.with(mContext).load(item.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = new ArrayList<>();
                list.add(item.getPICTURE_URL());
                mContext.startActivity(ImageBrowserActivity.getIntent(mContext, list, 0));
            }
        });

    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.6), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
        initData();
    }

}
