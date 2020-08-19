package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bm.library.PhotoView;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.PatternBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.TabViewUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 显示纸样弹框
 */
public class PatternDialog extends BaseDialog {

    private String mShopOrder;
    private LinearLayout mLayout_process;
    private List<PatternBo> mList_data;
    private PhotoView mIv_img;

    public PatternDialog(@NonNull Context context, String shopOrder) {
        super(context);
        this.mShopOrder = shopOrder;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_pattern, null);
        setContentView(view);

        mLayout_process = view.findViewById(R.id.layout_pattern_process);
        mIv_img = view.findViewById(R.id.iv_pattern_img);
        mIv_img.enable();

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIv_img.setImageBitmap(null);
                dismiss();
            }
        });

        LoadingDialog.show(mContext);
        HttpHelper.getPattern(mShopOrder, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                if (HttpHelper.isSuccess(resultJSON)) {
                    JSONArray array = resultJSON.getJSONArray("result");
                    mList_data = JSON.parseArray(array.toString(), PatternBo.class);
                    if (mList_data != null && mList_data.size() != 0) {
                        setupView();
                    }
                } else {
                    ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
                }
                LoadingDialog.dismiss();
            }

            @Override
            public void onFailure(String url, int code, String message) {
                LoadingDialog.dismiss();
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    private void setupView() {
        for (int i = 0; i < mList_data.size(); i++) {
            final int finalI = i;
            mLayout_process.addView(TabViewUtil.getTabView(mContext, mList_data.get(i), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TabViewUtil.refreshTabView(mContext, mLayout_process, finalI);
                    String picture_url = mList_data.get(finalI).getPICTURE_URL();
                    if (!TextUtils.isEmpty(picture_url)) {
                        Picasso.with(mContext).load(picture_url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(mIv_img);
                    }
                }
            }));
        }
        TabViewUtil.refreshTabView(mContext, mLayout_process, 0);
        String picture_url = mList_data.get(0).getPICTURE_URL();
        if (!isEmpty(picture_url))
            Picasso.with(mContext).load(picture_url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(mIv_img);
    }

}
