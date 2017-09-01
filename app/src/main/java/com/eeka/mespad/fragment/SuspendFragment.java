package com.eeka.mespad.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.SuspendComponentBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 吊挂作业界面
 * Created by Lenovo on 2017/6/16.
 */

public class SuspendFragment extends BaseFragment {

    private ListView mLv_orderList;
    private List<String> mList_sfcs;
    private SFCAdapter mSFCAdapter;

    private LinearLayout mLayout_component;
    private ImageView mIv_component;

    private ContextInfoBo mContextInfo;
    private SuspendComponentBo mComponent;
    private SuspendComponentBo.COMPONENTSBean mCurComponent;
    private String mCurSFC;
    private String mOperationBo;

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
        mLv_orderList = (ListView) mView.findViewById(R.id.lv_sfcList);
        mLayout_component = (LinearLayout) mView.findViewById(R.id.layout_component);
        mIv_component = (ImageView) mView.findViewById(R.id.iv_suspend_componentImg);
        refreshLoginUsers();
    }

    @Override
    protected void initData() {
        super.initData();
        mList_sfcs = new ArrayList<>();
        mSFCAdapter = new SFCAdapter(mContext, mList_sfcs, R.layout.item_textview);
        mLv_orderList.setAdapter(mSFCAdapter);
    }

    /**
     * 设置固定布局：工艺说明、当前工序
     */
    private void setupBaseView(JSONObject json) {
        mOperationBo = json.getString("HANDLE");
        TextView tv_curProcess = (TextView) mView.findViewById(R.id.tv_suspend_curProcess);
        tv_curProcess.setText(json.getString("OPERATION"));
        TextView tv_craftDesc = (TextView) mView.findViewById(R.id.tv_suspend_craftDesc);
        tv_craftDesc.setText(json.getString("OPERATION_INSTRUCTION"));
    }

    /**
     * 设置部件布局
     */
    private void setupComponentView() {
        mLayout_component.removeAllViews();
        List<SuspendComponentBo.COMPONENTSBean> components = mComponent.getCOMPONENTS();
        for (SuspendComponentBo.COMPONENTSBean component : components) {
            mLayout_component.addView(getComponentView(component));
            if (mCurComponent == null) {
                mCurComponent = component;
                HttpHelper.getComponentPic(mCurSFC, mCurComponent.getComponentId(), this);
                View childAt = mLayout_component.getChildAt(0);
                Button btn_component = (Button) childAt.findViewById(R.id.btn_componentName);
                btn_component.setEnabled(false);
            }
        }
    }

    public void searchOrder(String orderNum) {
        showLoading();
        HttpHelper.getSfcComponents(mOperationBo, mContextInfo.getHANDLE(), orderNum, this);
    }

    /**
     * 绑定
     */
    public void binding() {
        if (mCurComponent != null) {
            showLoading();
            HttpHelper.hangerBinding(mCurComponent.getComponentId(), mCurComponent.getIsNeedSubContract(), this);
        } else {
            showErrorDialog("没有选择部件，无法执行绑定操作");
        }
    }

    /**
     * 解绑
     */
    public void unBind() {
        if (mCurComponent != null) {
            JSONObject json = new JSONObject();
            json.put("SFC", mCurSFC);
            json.put("PART_ID", mCurComponent.getComponentId());
            showLoading();
            HttpHelper.hangerUnbind(json, this);
        } else {
            showErrorDialog("没有选择部件，无法执行解绑操作");
        }
    }

    private class SFCAdapter extends CommonAdapter<String> {

        SFCAdapter(Context context, List<String> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String item, int position) {
            TextView textView = holder.getView(R.id.textView);
            textView.setPadding(0, 20, 0, 20);
            textView.setText(item);
            if (item.equals(mCurSFC)) {
                textView.setBackgroundResource(R.color.text_green_default);
            } else {
                textView.setBackgroundResource(R.color.white);
            }
        }
    }

    /**
     * 获取部件布局
     */
    private View getComponentView(final SuspendComponentBo.COMPONENTSBean component) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_component, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        Button btn_component = (Button) view.findViewById(R.id.btn_componentName);
        btn_component.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        TextView tv_helpDesc = (TextView) view.findViewById(R.id.tv_helpDesc);
        btn_component.setText(component.getComponentName());
        String isNeedSubContract = component.getIsNeedSubContract();//是否需要外协
        String isSubContractCompleted = component.getIsSubContractCompleted();//外协是否已完成
        if ("true".equals(isNeedSubContract)) {
            if ("true".equals(isSubContractCompleted)) {
                tv_helpDesc.setText("外协已完成");
            } else {
                tv_helpDesc.setText("外协未完成");
            }
        } else {
            tv_helpDesc.setText("不需要外协");
        }
        btn_component.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurComponent = component;
//                showLoading();
                HttpHelper.getComponentPic(mCurSFC, component.getComponentId(), SuspendFragment.this);
                int childCount = mLayout_component.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = mLayout_component.getChildAt(i);
                    Button button = (Button) childAt.findViewById(R.id.btn_componentName);
                    button.setEnabled(true);
                }
                v.setEnabled(false);
            }
        });
        return view;
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.queryPositionByPadIp_url.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                mContextInfo = JSON.parseObject(result.toString(), ContextInfoBo.class);
                SpUtil.saveContextInfo(mContextInfo);
                List<UserInfoBo> loginUserList = mContextInfo.getLOGIN_USER_LIST();
                SpUtil.savePositionUsers(loginUserList);
            } else if (HttpHelper.findProcessWithPadId_url.equals(url)) {
                if (isAdded())
                    showLoading();
                mContextInfo = SpUtil.getContextInfo();
                HttpHelper.getSuspendBaseData(mContextInfo.getHANDLE(), this);
            } else if (HttpHelper.getSuspendBaseData.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                setupBaseView(result);
                HttpHelper.getSuspendUndoList(mOperationBo, mContextInfo.getWORK_CENTER(), this);
            } else if (HttpHelper.getSuspendUndoList.equals(url)) {
                mList_sfcs = JSON.parseArray(resultJSON.getJSONArray("result").toString(), String.class);
                mSFCAdapter.notifyDataSetChanged(mList_sfcs);
            } else if (HttpHelper.getSfcComponents.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                mComponent = JSON.parseObject(result.toString(), SuspendComponentBo.class);
                mCurSFC = mComponent.getSFC();
                mSFCAdapter.notifyDataSetChanged();
                mCurComponent = null;
                setupComponentView();
                HttpHelper.getSuspendUndoList(mOperationBo, mContextInfo.getWORK_CENTER(), this);
            } else if (HttpHelper.getComponentPic.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                Glide.with(mContext).load(result.getString("PICTURE_URL")).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(mIv_component);
            } else if (HttpHelper.hangerBinding.equals(url)) {
                toast("衣架绑定成功");
            } else if (HttpHelper.hangerUnbind.equals(url)) {
                toast("衣架解绑成功");
            }
        } else {
            String message = resultJSON.getString("message");
            showErrorDialog(message);
        }
    }
}
