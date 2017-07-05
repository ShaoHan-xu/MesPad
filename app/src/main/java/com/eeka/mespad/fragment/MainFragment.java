package com.eeka.mespad.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.http.HttpHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<TailorInfoBo.OPERINFORBean> mList_processData;

    private RadioGroup mRadioGroup;

    private LinearLayout mLayout_materials;
    private LinearLayout mLayout_sizeInfo;
    private TailorInfoBo mTailorInfo;

    private ListView mLv_process;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_main, null);
        return mView;
    }

    protected void initView() {
        mViewPager = (ViewPager) mView.findViewById(R.id.viewPager_main_processBmp);

        mRadioGroup = (RadioGroup) mView.findViewById(R.id.rg_main_indication);
        mRadioGroup.setOnCheckedChangeListener(new RadioChangedListener());

        mLayout_materials = (LinearLayout) mView.findViewById(R.id.layout_materials1);
        mLayout_sizeInfo = (LinearLayout) mView.findViewById(R.id.layout_sizeInfo);

        mLv_process = (ListView) mView.findViewById(R.id.lv_processList);

    }

    protected void initData() {
        HttpHelper.viewCutPadInfo("", MainFragment.this);

        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPagerChangedListener());

    }

    private void refreshView() {
        mLayout_materials.removeAllViews();
        List<TailorInfoBo.MatInfoBean> itemArray = mTailorInfo.getMAT_INFOR();
        for (int i = 0; i < itemArray.size(); i++) {
            mLayout_materials.addView(getMaterialsView(itemArray.get(i), i));
        }

        mLayout_sizeInfo.removeAllViews();
        List<TailorInfoBo.CUTSIZESBean> sizeArray = mTailorInfo.getCUT_SIZES();
        for (int i = 0; i < sizeArray.size(); i++) {
            mLayout_sizeInfo.addView(getSizeInfoView(sizeArray.get(i)));
        }

        mList_processData = new ArrayList<>();
        mList_processData = mTailorInfo.getOPER_INFOR();
        mViewPagerAdapter.notifyDataSetChanged();
        if (mList_processData.size() > 1) {
            for (int i = 0; i < mList_processData.size(); i++) {
                RadioButton rb = new RadioButton(mContext);
                rb.setEnabled(false);
                mRadioGroup.addView(rb);
                if (i == 0) {
                    rb.setChecked(true);
                }
            }
            mHandler.sendEmptyMessageDelayed(0, 3000);
        }

        mLv_process.setAdapter(new CommonAdapter<TailorInfoBo.OPERINFORBean>(mContext, mList_processData, R.layout.item_textview) {
            @Override
            public void convert(ViewHolder holder, TailorInfoBo.OPERINFORBean item, int position) {
                TextView textView = holder.getView(R.id.text);
                textView.setText(item.getOPERATION_BO());
                textView.setPadding(20, 20, 20, 20);
            }
        });

        TextView tv_orderNum = (TextView) mView.findViewById(R.id.tv_orderNum);
        TextView tv_batchNum = (TextView) mView.findViewById(R.id.tv_batchNum);
        TextView tv_style = (TextView) mView.findViewById(R.id.tv_style);
        TextView tv_qty = (TextView) mView.findViewById(R.id.tv_qty);
        TailorInfoBo.SHOPORDERINFORBean orderInfo = mTailorInfo.getSHOP_ORDER_INFOR();
        tv_orderNum.setText(orderInfo.getSHOP_ORDER());
        tv_batchNum.setText(orderInfo.getPROCESS_LOT());
        tv_style.setText(orderInfo.getITEM());
        tv_qty.setText(orderInfo.getAMOUNT() + "/件");
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeMessages(0);
    }

    @Override
    public void onClick(View v) {

    }

    private View getMaterialsView(final TailorInfoBo.MatInfoBean item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_materials = (ImageView) view.findViewById(R.id.iv_materials);
        TextView tv_materials = (TextView) view.findViewById(R.id.tv_materials);
        Glide.with(mContext).load(item.getMAT_URL()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_materials);
        iv_materials.setTag(position);
        tv_materials.setText(item.getMAT_NO() + "(" + item.getLAYERS() + ")");
        iv_materials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urls = new ArrayList<>();
                List<TailorInfoBo.MatInfoBean> list = mTailorInfo.getMAT_INFOR();
                for (TailorInfoBo.MatInfoBean item : list) {
                    urls.add(item.getMAT_URL());
                }

                startActivity(ImageBrowserActivity.getIntent(mContext, urls, (Integer) v.getTag()));
            }
        });
        return view;
    }

    private View getSizeInfoView(TailorInfoBo.CUTSIZESBean sizeInfo) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sizeinfo, null);
        TextView tv_yardage = (TextView) view.findViewById(R.id.tv_item_yardage);
        TextView tv_count = (TextView) view.findViewById(R.id.tv_item_count);
        tv_yardage.setText(sizeInfo.getSIZE() + "");
        tv_count.setText(sizeInfo.getSIZE_AMOUNT() + "");
        return view;
    }

    private class RadioChangedListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        }
    }

    private class ViewPagerChangedListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            View childAt = mRadioGroup.getChildAt(position);
            if (childAt != null) {
                ((RadioButton) childAt).setChecked(true);
            }

            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 3000);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mList_processData == null ? 0 : mList_processData.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.vp_item_main_processbmp, null);
            final ImageView imageView = (ImageView) view.findViewById(R.id.iv_item_main_processBmp);

            TailorInfoBo.OPERINFORBean operinforBean = mList_processData.get(position);
            Glide.with(mContext).load(operinforBean.getSOP_URL()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> urls = new ArrayList<>();
                    for (TailorInfoBo.OPERINFORBean item : mList_processData) {
                        urls.add(item.getSOP_URL());
                    }
                    startActivity(ImageBrowserActivity.getIntent(mContext, urls, position));
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

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {

        private WeakReference<MainFragment> mWeakActivity;

        public MyHandler(MainFragment activity) {
            this.mWeakActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainFragment activity = mWeakActivity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    int currentItem = activity.mViewPager.getCurrentItem();
                    if (currentItem >= activity.mList_processData.size() - 1) {
                        currentItem = -1;
                    }
                    activity.mViewPager.setCurrentItem(currentItem + 1);
                    sendEmptyMessageDelayed(0, 3000);
                    break;
            }
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        String status = resultJSON.getString("status");
        if ("Y".equals(status)) {
            mTailorInfo = JSONObject.parseObject(resultJSON.getJSONObject("result").toJSONString(), TailorInfoBo.class);
            refreshView();
        } else {
            toast("获取数据失败");
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        toast(message);
    }

    public TailorInfoBo getMaterialData() {
        return mTailorInfo;
    }
}
