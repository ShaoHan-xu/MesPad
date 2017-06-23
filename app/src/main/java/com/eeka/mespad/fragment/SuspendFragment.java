package com.eeka.mespad.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 吊挂作业界面
 * Created by Lenovo on 2017/6/16.
 */

public class SuspendFragment extends BaseFragment {

    private ExpandableListView mElv_process;
    private OrderAdapter mOrderAdapter;

    private ListView mLv_searchResult;
    private SearchAdapter mSearchAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_suspend, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mElv_process = (ExpandableListView) mView.findViewById(R.id.elv_orderList);
        mLv_searchResult = (ListView) mView.findViewById(R.id.lv_suspend_caiPianSearchResult);

    }

    @Override
    protected void initData() {
        super.initData();
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

        mSearchAdapter = new SearchAdapter(mContext, group, R.layout.lv_item_search_caipian);
        mLv_searchResult.setAdapter(mSearchAdapter);

    }

    private class SearchAdapter extends CommonAdapter<String> {

        public SearchAdapter(Context context, List<String> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String item, int position) {

        }
    }

    private class OrderAdapter extends BaseExpandableListAdapter {

        private List<String> list_group;
        private List<List<String>> list_child;

        public OrderAdapter(List<String> list_group, List<List<String>> list_child) {
            this.list_group = list_group;
            this.list_child = list_child;
        }

        @Override
        public int getGroupCount() {
            return list_group == null ? 0 : list_group.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return list_child == null ? 0 : list_child.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return list_group.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return list_child.get(groupPosition).get(childPosition);
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
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lv_item_process, null);
            convertView.setBackgroundColor(Color.parseColor("#C7E6F8"));
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

}
