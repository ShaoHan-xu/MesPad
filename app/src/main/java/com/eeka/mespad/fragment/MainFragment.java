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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

    private LinearLayout mLayout_material;//物料图
    private LinearLayout mLayout_material2;//排料图
    private LinearLayout mLayout_sizeInfo;
    private TailorInfoBo mTailorInfo;

    private ListView mLv_process;

    private LinearLayout mLayout_processTab;
    private TextView mTv_nextProcess;
    private Button mBtn_done;

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

        mLayout_material = (LinearLayout) mView.findViewById(R.id.layout_material1);
        mLayout_material2 = (LinearLayout) mView.findViewById(R.id.layout_material2);
        mLayout_sizeInfo = (LinearLayout) mView.findViewById(R.id.layout_sizeInfo);
        mLayout_processTab = (LinearLayout) mView.findViewById(R.id.layout_processTab);
        mTv_nextProcess = (TextView) mView.findViewById(R.id.tv_nextProcess);

        mLv_process = (ListView) mView.findViewById(R.id.lv_processList);
        mLv_process.setOnItemClickListener(new ProcessClickListener());

        mBtn_done = (Button) mView.findViewById(R.id.btn_done);
        mBtn_done.setOnClickListener(this);
    }

    protected void initData() {
        HttpHelper.viewCutPadInfo("", MainFragment.this);

        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPagerChangedListener());

    }

    public void refreshView() {
        mLayout_material.removeAllViews();
        List<TailorInfoBo.MatInfoBean> itemArray = mTailorInfo.getMAT_INFOR();
        if (itemArray != null) {
            for (int i = 0; i < itemArray.size(); i++) {
                mLayout_material.addView(getMaterialsView(itemArray.get(i), i));
            }
        }

        mLayout_material2.removeAllViews();
        List<TailorInfoBo.LayoutInfoBean> layoutArray = mTailorInfo.getLAYOUT_INFOR();
        if (layoutArray != null) {
            for (int i = 0; i < layoutArray.size(); i++) {
                mLayout_material2.addView(getLayoutView(layoutArray.get(i), i));
            }
        }

        int childCount = mLayout_sizeInfo.getChildCount();
        for (int i = childCount - 1; i > 0; i--) {
            mLayout_sizeInfo.removeViewAt(i);
        }
        List<TailorInfoBo.CUTSIZESBean> sizeArray = mTailorInfo.getCUT_SIZES();
        if (sizeArray == null) {
            mLayout_sizeInfo.setVisibility(View.GONE);
        } else {
            mLayout_sizeInfo.setVisibility(View.VISIBLE);
            for (int i = 0; i < sizeArray.size(); i++) {
                mLayout_sizeInfo.addView(getSizeInfoView(sizeArray.get(i)));
            }
        }

//        mRadioGroup.removeAllViews();
        mList_processData = new ArrayList<>();
        mList_processData = mTailorInfo.getOPER_INFOR();
        mViewPagerAdapter.notifyDataSetChanged();
//        if (mList_processData.size() > 1) {
//            for (int i = 0; i < mList_processData.size(); i++) {
//                RadioButton rb = new RadioButton(mContext);
//                rb.setEnabled(false);
//                mRadioGroup.addView(rb);
//                if (i == 0) {
//                    rb.setChecked(true);
//                }
//            }
//            mHandler.sendEmptyMessageDelayed(0, 3000);
//        }

        mLv_process.setAdapter(new CommonAdapter<TailorInfoBo.OPERINFORBean>(mContext, mList_processData, R.layout.item_textview) {
            @Override
            public void convert(ViewHolder holder, TailorInfoBo.OPERINFORBean item, int position) {
                TextView textView = holder.getView(R.id.text);
                textView.setText(item.getDESCRIPTION());
                textView.setPadding(10, 10, 10, 10);
            }
        });

        mLayout_processTab.removeAllViews();
        for (int i = 0; i < mList_processData.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_textview, null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setTag(i);
            TailorInfoBo.OPERINFORBean item1 = mList_processData.get(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem((Integer) v.getTag());
                }
            });
            textView.setText(item1.getDESCRIPTION());
            mLayout_processTab.addView(view);
        }
        if (mList_processData != null && mList_processData.size() != 0) {
            refreshProcessView(0);
        }

        TailorInfoBo.NextOrderInfo nextOperInfo = mTailorInfo.getNEXT_OPER_INFOR();
        if (nextOperInfo != null) {
            mTv_nextProcess.setText(nextOperInfo.getOPER_DESC());
        }

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

    /**
     * 刷新工序相关的界面，包括工序图、工艺说明、品质要求、
     *
     * @param position
     */
    private void refreshProcessView(int position) {
        TailorInfoBo.OPERINFORBean item = mList_processData.get(position);

        TextView tv_craftDesc = (TextView) mView.findViewById(R.id.tv_craftDescribe);
        TextView tv_qualityDes = (TextView) mView.findViewById(R.id.tv_qualityDescribe);
        tv_craftDesc.setText(item.getOPERATION_INSTRUCTION());
        tv_qualityDes.setText(item.getQUALITY_REQUIREMENT());

        int childCount = mLayout_processTab.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mLayout_processTab.getChildAt(i);
            childAt.setBackgroundResource(R.color.white);
        }
        mLayout_processTab.getChildAt(position).setBackgroundResource(R.color.text_gray_default);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeMessages(0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_done) {
            mBtn_done.setText("完成");
            mBtn_done.setBackgroundResource(R.drawable.btn_primary);
        }
    }

    /**
     * 当前工序点击事件
     */
    private class ProcessClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mViewPager.setCurrentItem(position);
        }
    }

    /**
     * 获取物料图
     *
     * @param item
     * @param position
     * @return
     */
    private View getMaterialsView(final TailorInfoBo.MatInfoBean item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_material = (ImageView) view.findViewById(R.id.iv_materials);
        TextView tv_material = (TextView) view.findViewById(R.id.tv_matNum);
        TextView tv_layers = (TextView) view.findViewById(R.id.tv_matLayers);
        Glide.with(mContext).load(item.getMAT_URL()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_material);
        iv_material.setTag(position);
        tv_material.setText(item.getMAT_NO());
        int layers = item.getLAYERS();
        if (layers != 0) {
            tv_layers.setText("(" + layers + ")");
        }

        iv_material.setOnClickListener(new View.OnClickListener() {
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

    /**
     * 获取排料图
     *
     * @param item
     * @param position
     * @return
     */
    private View getLayoutView(final TailorInfoBo.LayoutInfoBean item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_material = (ImageView) view.findViewById(R.id.iv_materials);
        TextView tv_material = (TextView) view.findViewById(R.id.tv_matNum);
        Glide.with(mContext).load(item.getPICTURE_URL()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_material);
        iv_material.setTag(position);
        tv_material.setText(item.getLAYOUT());

        iv_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urls = new ArrayList<>();
                List<TailorInfoBo.LayoutInfoBean> list = mTailorInfo.getLAYOUT_INFOR();
                for (TailorInfoBo.LayoutInfoBean item : list) {
                    urls.add(item.getPICTURE_URL());
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
//            View childAt = mRadioGroup.getChildAt(position);
//            if (childAt != null) {
//                ((RadioButton) childAt).setChecked(true);
//            }
//            mHandler.removeMessages(0);
//            mHandler.sendEmptyMessageDelayed(0, 3000);

            refreshProcessView(position);
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
