package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.eeka.mespad.R;

/**
 * 图片浏览器
 * Created by Lenovo on 2017/6/16.
 */

public class ImageBrowser extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_imagebrowser);

        String imgUrl = getIntent().getStringExtra("imgUrl");

        PhotoView photoView = (PhotoView) findViewById(R.id.photoView_image);
        photoView.enable();
        Glide.with(this).load(Integer.valueOf(imgUrl)).into(photoView);
//        photoView.setImageResource(Integer.valueOf(imgUrl));
    }

    public static Intent getIntent(Context context, String imgUrl) {
        Intent intent = new Intent(context, ImageBrowser.class);
        intent.putExtra("imgUrl", imgUrl);
        return intent;
    }
}
