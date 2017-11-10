package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.squareup.picasso.Picasso;

/**
 * 查看线稿图界面
 * Created by Lenovo on 2017/11/8.
 */
public class OutlinePicActivity extends BaseActivity {

    public static final String KEY_SFC = "key_sfc";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_outline);

        String sfc = getIntent().getStringExtra(KEY_SFC);
        showLoading();
        HttpHelper.getOutlineInfo(sfc, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                if (HttpHelper.isSuccess(resultJSON)) {
                    JSONArray result = resultJSON.getJSONArray("result");
                    if (result != null && result.size() != 0) {
                        initView(result);
                    } else {
                        toast("工艺图为空");
                        finish();
                    }
                } else {
                    ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
                }
                dismissLoading();
            }

            @Override
            public void onFailure(String url, int code, String message) {
                dismissLoading();
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    protected void initView(@NonNull JSONArray result) {
        for (int i = 0; i < result.size(); i++) {
            JSONObject item = result.getJSONObject(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_imageview, null);
            String pictureUrl = item.getString("PICTURE_URL");
            if (!isEmpty(pictureUrl)) {
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                Picasso.with(mContext).load(pictureUrl).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
            }
            String description = item.getString("DESCRIPTION");
            if (!isEmpty(description)) {
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setVisibility(View.VISIBLE);
                textView.setText(description);
            }
        }
    }

    public static Intent getIntent(Context context, String sfc) {
        Intent intent = new Intent(context, OutlinePicActivity.class);
        intent.putExtra(KEY_SFC, sfc);
        return intent;
    }
}
