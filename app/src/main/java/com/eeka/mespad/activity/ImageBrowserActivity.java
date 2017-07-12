package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.eeka.mespad.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片浏览器
 * Created by Lenovo on 2017/6/16.
 */

public class ImageBrowserActivity extends BaseActivity {

    private List<String> mList_processData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_imagebrowser);

        mList_processData = getIntent().getStringArrayListExtra("imgUrl");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager_imgBrowser);
        viewPager.setAdapter(new ViewPagerAdapter());

        int position = getIntent().getIntExtra("position", 0);
        viewPager.setCurrentItem(position);

//        PhotoView photoView = (PhotoView) findViewById(R.id.photoView_image);
//        photoView.enable();
//        Glide.with(this).load(Integer.valueOf(imgUrl)).into(photoView);
//        photoView.setImageResource(Integer.valueOf(imgUrl));
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mList_processData == null ? 0 : mList_processData.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.vp_item_main_processbmp, null);
            PhotoView imageView = (PhotoView) view.findViewById(R.id.iv_item_main_processBmp);
            imageView.enable();
            Glide.with(mContext).load(mList_processData.get(position)).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View item = (View) object;
            container.removeView(item);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    public static Intent getIntent(Context context, ArrayList<String> imgUrl, int position) {
        Intent intent = new Intent(context, ImageBrowserActivity.class);
        intent.putStringArrayListExtra("imgUrl", imgUrl);
        intent.putExtra("position", position);
        return intent;
    }


}
