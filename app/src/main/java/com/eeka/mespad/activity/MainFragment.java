package com.eeka.mespad.activity;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.http.HttpHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;

public class MainFragment extends BaseFragment {

    private ImageView mIv_processBmp;

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Integer> mList_data;

    private RadioGroup mRadioGroup;

    private ListView mLv_process;
    private ProcessAdapter mProcessAdapter;

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
        mIv_processBmp = (ImageView) mView.findViewById(R.id.iv_main_processBmp);
        mIv_processBmp.setOnClickListener(this);

        mRadioGroup = (RadioGroup) mView.findViewById(R.id.rg_main_indication);
        mRadioGroup.setOnCheckedChangeListener(new RadioChangedListener());

        mView.findViewById(R.id.iv_materials).setOnClickListener(this);
        mView.findViewById(R.id.btn_returnMaterials).setOnClickListener(this);
        mView.findViewById(R.id.btn_getMaterials).setOnClickListener(this);

        mLv_process = (ListView) mView.findViewById(R.id.lv_processCode);
    }

    protected void initData() {
        mList_data = new ArrayList<>();
        mList_data.add(R.drawable.libai);
        mList_data.add(R.drawable.hanxin);
        mList_data.add(R.drawable.zhaoyun);
        mList_data.add(R.drawable.renzhe);

        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPagerChangedListener());

        for (int i = 0; i < mList_data.size(); i++) {
            RadioButton rb = new RadioButton(mContext);
            rb.setEnabled(false);
            mRadioGroup.addView(rb);
            if (i == 0) {
                rb.setChecked(true);
            }
        }

        List<String> list = new ArrayList<>();
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        mProcessAdapter = new ProcessAdapter(mContext, list, R.layout.lv_item_process);
        mLv_process.setAdapter(mProcessAdapter);

        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_main_processBmp) {
            mIv_processBmp.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.iv_materials) {
            RequestParams params = new RequestParams();
            params.put("gxdm", "SZSFHS001");
            HttpRequest.post(HttpHelper.GETGXDM_URL, params, HttpHelper.getResponseHandler(HttpHelper.GETGXDM_URL, this));
        } else if (v.getId() == R.id.btn_returnMaterials) {
            mHandler.removeMessages(0);
        } else if (v.getId() == R.id.btn_getMaterials) {
            if (!mHandler.hasMessages(0))
                mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private class RadioChangedListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        }
    }

    private class ProcessAdapter extends CommonAdapter<String> {

        public ProcessAdapter(Context context, List<String> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String item, int position) {
            if (position == 0) {
                holder.getView(R.id.btn_item_process_finish).setVisibility(View.VISIBLE);
            }else {
                holder.getView(R.id.btn_item_process_finish).setVisibility(View.GONE);
            }
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
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mList_data == null ? 0 : mList_data.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.vp_item_main_processbmp, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_item_main_processBmp);
            imageView.setImageResource(mList_data.get(position));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setVisibility(View.GONE);
                    mIv_processBmp.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(R.drawable.gif).into(mIv_processBmp);
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
                    if (currentItem >= activity.mList_data.size() - 1) {
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
    }

    @Override
    public void onFailure(String url, int code, String message) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
