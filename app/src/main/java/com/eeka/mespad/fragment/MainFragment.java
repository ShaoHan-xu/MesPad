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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowser;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.MaterialsBo;
import com.eeka.mespad.http.HttpHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;

public class MainFragment extends BaseFragment {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Integer> mList_processData;

    private RadioGroup mRadioGroup;

    private ExpandableListView mElv_process;
    private ProcessAdapter mProcessAdapter;

    private LinearLayout mLayout_materials1;
    private LinearLayout mLayout_materials2;
    private GridView mGv_materials;
    private List<MaterialsBo> mList_materialsData;

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
        mGv_materials = (GridView) mView.findViewById(R.id.gv_info);
    }

    protected void initData() {
        mList_processData = new ArrayList<>();
        mList_processData.add(R.drawable.libai);
        mList_processData.add(R.drawable.hanxin);
        mList_processData.add(R.drawable.zhaoyun);
        mList_processData.add(R.drawable.renzhe);

        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPagerChangedListener());

        for (int i = 0; i < mList_processData.size(); i++) {
            RadioButton rb = new RadioButton(mContext);
            rb.setEnabled(false);
            mRadioGroup.addView(rb);
            if (i == 0) {
                rb.setChecked(true);
            }
        }

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
        mProcessAdapter = new ProcessAdapter(group, child);
        mElv_process.setAdapter(mProcessAdapter);

        mList_materialsData = new ArrayList<>();
        mList_materialsData.add(new MaterialsBo("" + R.drawable.materials1, "面料1"));
        mList_materialsData.add(new MaterialsBo("" + R.drawable.materials2, "面料2"));
        mList_materialsData.add(new MaterialsBo("" + R.drawable.materials3, "面料3"));
        mList_materialsData.add(new MaterialsBo("" + R.drawable.hanxin, "面料4"));
        mList_materialsData.add(new MaterialsBo("" + R.drawable.libai, "面料5"));
        mList_materialsData.add(new MaterialsBo("" + R.drawable.zhaoyun, "面料6"));
        mList_materialsData.add(new MaterialsBo("" + R.drawable.materials1, "面料7"));
        mList_materialsData.add(new MaterialsBo("" + R.drawable.materials1, "面料8"));
        mList_materialsData.add(new MaterialsBo("" + R.drawable.materials1, "面料9"));
        mGv_materials.setAdapter(new CommonAdapter<MaterialsBo>(mContext, mList_materialsData, R.layout.item_textview) {
            @Override
            public void convert(ViewHolder holder, MaterialsBo item, int position) {
                if (position % 3 == 0) {
                    holder.setText(R.id.text, "38");
                } else if (position % 3 == 1) {
                    holder.setText(R.id.text, "红");
                } else if (position % 3 == 2) {
                    holder.setText(R.id.text, "18");
                }
            }
        });

        for (MaterialsBo item : mList_materialsData) {
            mLayout_materials1.addView(getMaterialsView(item));
            mLayout_materials2.addView(getMaterialsView(item));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeMessages(0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_materials) {
            RequestParams params = new RequestParams();
            params.put("gxdm", "SZSFHS001");
            HttpRequest.post(HttpHelper.GETGXDM_URL, params, HttpHelper.getResponseHandler(HttpHelper.GETGXDM_URL, this));
        }
    }

    private View getMaterialsView(final MaterialsBo item) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_materials, null);
        ImageView iv_materials = (ImageView) view.findViewById(R.id.iv_materials);
        TextView tv_materials = (TextView) view.findViewById(R.id.tv_materials);
        iv_materials.setImageResource(Integer.valueOf(item.getPicUrl()));
        tv_materials.setText(item.getName());
        iv_materials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ImageBrowser.getIntent(mContext, item.getPicUrl()));
            }
        });
        return view;
    }

    private class RadioChangedListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        }
    }

    private class ProcessAdapter extends BaseExpandableListAdapter {

        private List<String> mList_group;
        private List<List<String>> mList_child;

        public ProcessAdapter(List<String> mList_group, List<List<String>> mList_child) {
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
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_item_main_processBmp);
            imageView.setImageResource(mList_processData.get(position));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ImageBrowser.getIntent(mContext, mList_processData.get(position) + ""));
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
    }

    @Override
    public void onFailure(String url, int code, String message) {

    }

}
