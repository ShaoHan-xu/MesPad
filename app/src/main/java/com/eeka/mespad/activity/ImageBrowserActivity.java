package com.eeka.mespad.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonVPAdapter;
import com.eeka.mespad.bo.TailorInfoBo;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 图片信息显示
 */
public class ImageBrowserActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_imagebrowser);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        List<Object> data = (List<Object>) getIntent().getSerializableExtra("data");
        boolean scrollAble = getIntent().getBooleanExtra("scrollAble", true);
        if (scrollAble) {
            findViewById(R.id.layout_imageBrowser_image).setVisibility(View.VISIBLE);
            ViewPager viewPager = findViewById(R.id.vp_matInfo);
            viewPager.setAdapter(new ViewPagerAdapter(mContext, data, R.layout.item_photoview));

            PagerChangedListener listener = new PagerChangedListener(data);
            viewPager.addOnPageChangeListener(listener);

            int position = getIntent().getIntExtra("position", 0);
            if (position == 0) {
                listener.onPageSelected(0);
            } else {
                viewPager.setCurrentItem(position);
            }
        } else {
            LinearLayout layout_images = findViewById(R.id.layout_imageBrowser_images);
            layout_images.setVisibility(View.VISIBLE);
            for (Object item : data) {
                String url = (String) item;
                if (!isEmpty(url))
                    layout_images.addView(getImageView(url));
            }
        }
    }

    private View getImageView(String url) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photoview, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, MATCH_PARENT);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        final PhotoView imageView = view.findViewById(R.id.imageView);
        imageView.enable();
        Picasso.with(mContext).load(url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
        return view;
    }

    private class ViewPagerAdapter extends CommonVPAdapter<Object> {

        ViewPagerAdapter(Context context, List<Object> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convertView(View view, Object item, int position) {
            final PhotoView imageView = view.findViewById(R.id.imageView);
            imageView.enable();

            if (item instanceof Integer) {
                int bmpUrl = (int) item;
                Picasso.with(mContext).load(bmpUrl).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
            } else {
                String bmpUrl = null;
                if (item instanceof TailorInfoBo.MatInfoBean) {
                    TailorInfoBo.MatInfoBean matInfo = (TailorInfoBo.MatInfoBean) item;
                    bmpUrl = matInfo.getMAT_URL();
                } else if (item instanceof TailorInfoBo.LayoutInfoBean) {
                    TailorInfoBo.LayoutInfoBean layoutInfo = (TailorInfoBo.LayoutInfoBean) item;
                    bmpUrl = layoutInfo.getPICTURE_URL();
                } else if (item instanceof String) {
                    bmpUrl = (String) item;
                }
                if (!isEmpty(bmpUrl)) {
                    Picasso.with(mContext).load(bmpUrl).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
                }
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private class PagerChangedListener implements ViewPager.OnPageChangeListener {

        List<Object> list;

        PagerChangedListener(List<Object> list) {
            this.list = list;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (list == null || list.size() == 0)
                return;
            Object data = list.get(position);
            if (data instanceof TailorInfoBo.MatInfoBean) {
                TailorInfoBo.MatInfoBean matInfo = (TailorInfoBo.MatInfoBean) data;
                refreshMatAttrView(matInfo);
            } else if (data instanceof TailorInfoBo.LayoutInfoBean) {
                TailorInfoBo.LayoutInfoBean layoutInfo = (TailorInfoBo.LayoutInfoBean) data;
                refreshLayAttrView(layoutInfo);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 刷新物料属性布局
     */
    private void refreshMatAttrView(TailorInfoBo.MatInfoBean matInfo) {
        findViewById(R.id.layout_imageBrowser_desc).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_matInfo_matDesc).setVisibility(View.VISIBLE);
        TextView tv_no = findViewById(R.id.tv_matInfo_matNo);
        TextView tv_desc = findViewById(R.id.tv_matInfo_matDesc);
        TextView tv_big = findViewById(R.id.tv_matInfo_matBig);
        TextView tv_bigDesc = findViewById(R.id.tv_matInfo_matBigDesc);
        TextView tv_mid = findViewById(R.id.tv_matInfo_matMid);
        TextView tv_midDesc = findViewById(R.id.tv_matInfo_matMidDesc);

        tv_no.setText(matInfo.getMAT_NO());
        tv_desc.setText(matInfo.getMAT_DESC());
        tv_big.setText(matInfo.getGRAND_CATEGORY());
        tv_bigDesc.setText(matInfo.getGRAND_CATEGORY_DESC());
        tv_mid.setText(matInfo.getMID_CATEGORY());
        tv_midDesc.setText(matInfo.getMID_CATEGORY_DESC());
    }

    /**
     * 刷新排料图属性布局
     */
    private void refreshLayAttrView(TailorInfoBo.LayoutInfoBean layoutInfo) {
        findViewById(R.id.layout_imageBrowser_desc).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_matInfo_layDesc).setVisibility(View.VISIBLE);
        TextView tv_name = findViewById(R.id.tv_matInfo_layName);
        TextView tv_no = findViewById(R.id.tv_matInfo_layNo);
        TextView tv_length = findViewById(R.id.tv_matInfo_layLength);
        TextView tv_width = findViewById(R.id.tv_matInfo_layWidth);
        TextView tv_layers = findViewById(R.id.tv_matInfo_layLayers);
        TextView tv_amount = findViewById(R.id.tv_matInfo_layAmount);
        TextView tv_sizeRatio = findViewById(R.id.tv_matInfo_sizeRatio);

        tv_name.setText(layoutInfo.getLAYOUT());
        tv_no.setText(layoutInfo.getITEM());
        tv_length.setText(layoutInfo.getLENGTH() + layoutInfo.getLENGTH_UNIT());
        tv_width.setText(layoutInfo.getWIDTH() + layoutInfo.getWIDTH_UNIT());
        tv_layers.setText(layoutInfo.getLAYERS());
        tv_amount.setText(layoutInfo.getAMOUNT());
        tv_sizeRatio.setText(layoutInfo.getSIZE_RATIO());
    }

    public static Intent getIntent(Context context, String url) {
        List<String> list = new ArrayList<>();
        list.add(url);
        return getIntent(context, list, 0);
    }

    /**
     * @param data     显示的图片数据
     * @param position 当前显示的图片在列表中的下标
     */
    public static <T> Intent getIntent(Context context, List<T> data, int position) {
        Intent intent = new Intent(context, ImageBrowserActivity.class);
        intent.putExtra("data", (Serializable) data);
        intent.putExtra("position", position);
        return intent;
    }

    /**
     * @param scrollAble 是否为左右滑动的显示方式
     */
    public static <T> Intent getIntent(Context context, List<T> data, boolean scrollAble) {
        Intent intent = new Intent(context, ImageBrowserActivity.class);
        intent.putExtra("data", (Serializable) data);
        intent.putExtra("scrollAble", scrollAble);
        return intent;
    }
}
