package com.eeka.mespad.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.ComponentBo;
import com.eeka.mespad.http.HttpHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 吊挂作业界面
 * Created by Lenovo on 2017/6/16.
 */

public class SuspendFragment extends BaseFragment {

    private ListView mLv_orderList;
    private OrderAdapter mOrderAdapter;

    private LinearLayout mLayout_component;

    private EditText mEt_search;

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
        mLv_orderList = (ListView) mView.findViewById(R.id.lv_orderList);
        mLayout_component = (LinearLayout) mView.findViewById(R.id.layout_component);

        mEt_search = (EditText) mView.findViewById(R.id.et_searchComponent);
        mView.findViewById(R.id.btn_searchComponent).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        List<String> group = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            group.add("2017008031314");
        }
        mOrderAdapter = new OrderAdapter(mContext, group, R.layout.item_textview);
        mLv_orderList.setAdapter(mOrderAdapter);

        for (int i = 0; i < 4; i++) {
            ComponentBo compon = new ComponentBo();
            compon.setName("腰头");
            compon.setHelpDesc("外协情况");
            mLayout_component.addView(getComponentView(compon));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_searchComponent) {
            String key = mEt_search.getText().toString().trim();
            if (isEmpty(key)) {
                toast("请输入工单号进行搜索");
            } else {

            }
        }
    }

    /**
     * 解绑
     */
    public void unBind() {
        JSONObject json = new JSONObject();

        HttpHelper.hangerUnbind(json, this);
    }

    private class OrderAdapter extends CommonAdapter<String> {

        public OrderAdapter(Context context, List<String> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String item, int position) {

        }
    }

    /**
     * 获取部件布局
     *
     * @param component
     * @return
     */
    private View getComponentView(ComponentBo component) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_component, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        Button btn_component = (Button) view.findViewById(R.id.btn_componentName);
        TextView tv_helpDesc = (TextView) view.findViewById(R.id.tv_helpDesc);
        btn_component.setText(component.getName());
        tv_helpDesc.setText(component.getHelpDesc());
        return view;
    }
}
