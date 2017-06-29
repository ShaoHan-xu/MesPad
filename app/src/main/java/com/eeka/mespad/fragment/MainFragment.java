package com.eeka.mespad.fragment;

import android.graphics.Color;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.http.HttpHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private ArrayList<String> mList_processData;

    private RadioGroup mRadioGroup;

    private ExpandableListView mElv_process;
    private OrderAdapter mOrderAdapter;

    private LinearLayout mLayout_materials1;
    private LinearLayout mLayout_materials2;
    private LinearLayout mLayout_sizeInfo;
    private TailorInfoBo.TailorResultBean mTailorResult;

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

        mElv_process = (ExpandableListView) mView.findViewById(R.id.elv_processCode);
        mLayout_materials1 = (LinearLayout) mView.findViewById(R.id.layout_materials1);
        mLayout_materials2 = (LinearLayout) mView.findViewById(R.id.layout_materials2);
        mLayout_sizeInfo = (LinearLayout) mView.findViewById(R.id.layout_sizeInfo);

        mElv_process.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                HttpHelper.viewCutPadInfo("", MainFragment.this);
                return false;
            }
        });
    }

    protected void initData() {
        HttpHelper.viewCutPadInfo("", MainFragment.this);

        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPagerChangedListener());

        List<String> group = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            group.add("");
        }
        List<List<String>> child = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<String> list = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                list.add("");
            }
            child.add(list);
        }
        mOrderAdapter = new OrderAdapter(group, child);
        mElv_process.setAdapter(mOrderAdapter);

    }

    private void refreshView() {
        mLayout_materials1.removeAllViews();
        mLayout_materials2.removeAllViews();
        List<TailorInfoBo.TailorResultBean.ItemArrayBean> itemArray = mTailorResult.getItemArray();
        for (int i = 0; i < itemArray.size(); i++) {
            mLayout_materials1.addView(getMaterialsView(itemArray.get(i), i));
            mLayout_materials2.addView(getMaterialsView(itemArray.get(i), i));
        }

        mLayout_sizeInfo.removeAllViews();
        List<TailorInfoBo.TailorResultBean.SizeArrayBean> sizeArray = mTailorResult.getSizeArray();
        for (int i = 0; i < sizeArray.size(); i++) {
            mLayout_sizeInfo.addView(getSizeInfoView(sizeArray.get(i)));
        }

        mList_processData = new ArrayList<>();
        mList_processData.add(mTailorResult.getDrawPicUrl());
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
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeMessages(0);
    }

    @Override
    public void onClick(View v) {

    }

    private View getMaterialsView(final TailorInfoBo.TailorResultBean.ItemArrayBean item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_materials, null);
        view.setTag(position);
        ImageView iv_materials = (ImageView) view.findViewById(R.id.iv_materials);
        TextView tv_materials = (TextView) view.findViewById(R.id.tv_materials);
        Glide.with(mContext).load(item.getMaterialUrl()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_materials);
        iv_materials.setTag(position);
        tv_materials.setText(item.getMaterial());
        iv_materials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urls = new ArrayList<>();
                List<TailorInfoBo.TailorResultBean.ItemArrayBean> list = mTailorResult.getItemArray();
                for (TailorInfoBo.TailorResultBean.ItemArrayBean item : list) {
                    urls.add(item.getMaterialUrl());
                }

                startActivity(ImageBrowserActivity.getIntent(mContext, urls, (Integer) v.getTag()));
            }
        });
        return view;
    }

    private View getSizeInfoView(TailorInfoBo.TailorResultBean.SizeArrayBean sizeInfo) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sizeinfo, null);
        TextView tv_yardage = (TextView) view.findViewById(R.id.tv_item_yardage);
        TextView tv_color = (TextView) view.findViewById(R.id.tv_item_color);
        TextView tv_count = (TextView) view.findViewById(R.id.tv_item_count);
        tv_yardage.setText(sizeInfo.getSize() + "");
        tv_color.setText(sizeInfo.getColor());
        tv_count.setText(sizeInfo.getQty() + "");
        return view;
    }

    private class RadioChangedListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        }
    }

    private class OrderAdapter extends BaseExpandableListAdapter {

        private List<String> mList_group;
        private List<List<String>> mList_child;

        public OrderAdapter(List<String> mList_group, List<List<String>> mList_child) {
            this.mList_group = mList_group;
            this.mList_child = mList_child;
        }

        @Override
        public int getGroupCount() {
            return mList_group == null ? 0 : mList_group.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mList_child == null ? 0 : mList_child.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mList_group.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mList_child.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lv_item_process, null);
//            Button btn_finish = (Button) convertView.findViewById(R.id.btn_item_process_finish);
//            if (groupPosition == 0) {
//                btn_finish.setVisibility(View.VISIBLE);
//            } else {
//                btn_finish.setVisibility(View.INVISIBLE);
//            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lv_item_process, null);
            convertView.setBackgroundColor(Color.parseColor("#C7E6F8"));
//            Button btn_finish = (Button) convertView.findViewById(R.id.btn_item_process_finish);
//            if (childPosition == 0) {
//                btn_finish.setText("开始");
//                btn_finish.setVisibility(View.VISIBLE);
//            } else {
//                btn_finish.setVisibility(View.INVISIBLE);
//            }
            TextView text = (TextView) convertView.findViewById(R.id.tv_item_process_code);
            text.setTextColor(getResources().getColor(R.color.black));
            text.setText("第" + childPosition + "车床");
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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

            Glide.with(mContext).load(mList_processData.get(position)).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ImageBrowserActivity.getIntent(mContext, mList_processData, position));
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
            TailorInfoBo tailorInfoBo = JSONObject.parseObject(resultJSON.toJSONString(), TailorInfoBo.class);
            mTailorResult = tailorInfoBo.getResult();
            refreshView();
        } else {
            toast("获取数据失败");
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        toast(message);
    }

}
