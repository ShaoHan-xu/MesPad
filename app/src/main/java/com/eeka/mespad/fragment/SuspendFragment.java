package com.eeka.mespad.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.CommonVPAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.SuspendComponentBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.view.dialog.AutoPickDialog;
import com.eeka.mespad.view.dialog.CreateCardDialog;
import com.eeka.mespad.view.dialog.MyAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 吊挂作业界面
 * Created by Lenovo on 2017/6/16.
 */

public class SuspendFragment extends BaseFragment {

    private ListView mLv_orderList;
    private List<String> mList_sfcList;
    private SFCAdapter mSFCAdapter;

    private LinearLayout mLayout_component;
    private TextView mTv_curSFC;

    private ContextInfoBo mContextInfo;
    private SuspendComponentBo mComponent;
    private SuspendComponentBo.COMPONENTSBean mCurComponent;
    private String mCurSFC;
    private String mOperationBo;

    private ViewPager mVP_img;
    private List<String> mList_img;

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
        mTv_curSFC = (TextView) mView.findViewById(R.id.tv_suspend_curSFC);
        mVP_img = (ViewPager) mView.findViewById(R.id.vp_suspend_componentImg);

        Button btn_binding = (Button) mView.findViewById(R.id.btn_suspend_binding);
        btn_binding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
//        mList_sfcList = new ArrayList<>();
//        mSFCAdapter = new SFCAdapter(mContext, mList_sfcList, R.layout.item_textview);
//        mLv_orderList.setAdapter(mSFCAdapter);

    }

    /**
     * 设置固定布局：工艺说明、当前工序
     */
    private void setupBaseView(JSONObject json) {
//        mOperationBo = json.getString("HANDLE");
        mOperationBo = "OperationBO:8081,SCBD001,A";
//        TextView tv_curProcess = (TextView) mView.findViewById(R.id.tv_suspend_curProcess);
//        tv_curProcess.setText(json.getString("OPERATION"));
        final TextView tv_craftDesc = (TextView) mView.findViewById(R.id.tv_suspend_craftDesc);
        String instruction = json.getString("OPERATION_INSTRUCTION");
        if (isEmpty(instruction)) {
            tv_craftDesc.setText(null);
        } else {
            tv_craftDesc.setText(instruction.replace("#line#", "\n"));
        }

        tv_craftDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = tv_craftDesc.getText().toString();
                if (!isEmpty(content)) {
                    MyAlertDialog.showAlert(mContext, content);
                }
            }
        });
    }

    /**
     * 设置部件布局
     */
    private void setupComponentView() {
        mLayout_component.removeAllViews();
        List<SuspendComponentBo.COMPONENTSBean> components = mComponent.getCOMPONENTS();
        for (SuspendComponentBo.COMPONENTSBean component : components) {
            mLayout_component.addView(getComponentView(component));
        }
        mVP_img.setAdapter(null);
        mList_img = null;
    }

    public void searchOrder(String orderNum) {
        if (isAdded())
            showLoading();
        HttpHelper.getSfcComponents(mOperationBo, mContextInfo.getHANDLE(), orderNum, this);
    }

    /**
     * 绑定
     */
    public void binding() {
        if (mCurComponent != null) {
            showLoading();
            HttpHelper.hangerBinding(mCurComponent.getComponentId(), mCurComponent.getIsNeedSubContract(), SuspendFragment.this);

//            String msg = "确认绑定衣架 " + "mCurSFC" + " 的部件 " + mCurComponent.getComponentName() + " 吗？";
//            ErrorDialog.showConfirmAlert(mContext, msg, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showLoading();
//                    HttpHelper.hangerBinding(mCurComponent.getComponentId(), mCurComponent.getIsNeedSubContract(), SuspendFragment.this);
//                }
//            });
        } else {
            showErrorDialog("没有选择部件，无法执行绑定操作");
        }
    }

    /**
     * 解绑
     */
    public void unBind() {
        CreateCardDialog dialog = new CreateCardDialog(mContext, mCurSFC);
        dialog.show();
    }

    /**
     * 自动拣选
     */
    public void autoPicking() {
        if (mComponent == null) {
            toast("请先获取订单数据");
            return;
        }
        new AutoPickDialog(mContext, mComponent.getSHOP_ORDER(), mComponent.getITEM(), "30").show();
    }

    private class ImgAdapter extends CommonVPAdapter<String> {

        ImgAdapter(Context context, List<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convertView(View view, String item, final int position) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            String url = mList_img.get(position);
            Picasso.with(mContext).load(url).error(R.drawable.ic_error_img).placeholder(R.drawable.loading).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> urls = new ArrayList<>();
                    for (String url : mList_img) {
                        urls.add(url);
                    }
                    startActivity(ImageBrowserActivity.getIntent(mContext, urls, position));
                }
            });

        }
    }

    private class SFCAdapter extends CommonAdapter<String> {

        SFCAdapter(Context context, List<String> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String item, int position) {
            TextView textView = holder.getView(R.id.textView);
            textView.setPadding(0, 15, 0, 15);
            textView.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        Button btn_component = (Button) view.findViewById(R.id.btn_componentName);
        btn_component.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        ImageView iv_finished = (ImageView) view.findViewById(R.id.iv_part_finished);
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
        if ("true".equals(component.getIsBound())) {
            btn_component.setEnabled(false);
            iv_finished.setVisibility(View.VISIBLE);
        } else {
            btn_component.setEnabled(true);
            iv_finished.setVisibility(View.GONE);
        }
        btn_component.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurComponent = component;
//                showLoading();
                HttpHelper.getComponentPic(mCurSFC, component.getComponentId(), SuspendFragment.this);
                List<SuspendComponentBo.COMPONENTSBean> components = mComponent.getCOMPONENTS();
                int childCount = mLayout_component.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = mLayout_component.getChildAt(i);
                    Button button = (Button) childAt.findViewById(R.id.btn_componentName);
                    SuspendComponentBo.COMPONENTSBean componentsBean = components.get(i);
                    if ("true".equals(componentsBean.getIsBound())) {
                        button.setEnabled(false);
                    } else {
                        button.setEnabled(true);
                    }
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
            } else if (HttpHelper.findProcessWithPadId_url.equals(url)) {
                if (isAdded())
                    showLoading();
                mContextInfo = SpUtil.getContextInfo();
                HttpHelper.getSuspendBaseData(mContextInfo.getHANDLE(), this);
            } else if (HttpHelper.getSuspendBaseData.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                setupBaseView(result);
//                HttpHelper.getSuspendUndoList(mOperationBo, mContextInfo.getWORK_CENTER(), this);
            } else if (HttpHelper.getSuspendUndoList.equals(url)) {
                mList_sfcList = JSON.parseArray(resultJSON.getJSONArray("result").toString(), String.class);
                mSFCAdapter.notifyDataSetChanged(mList_sfcList);
                for (int i = 0; i < mList_sfcList.size(); i++) {
                    String str = mList_sfcList.get(i);
                    if (mCurSFC != null && mCurSFC.equals(str)) {
                        mLv_orderList.setSelection(i);
                        break;
                    }
                }
                //默认选中第一个
//                if (mCurComponent == null && mComponent != null) {
//                    List<SuspendComponentBo.COMPONENTSBean> components = mComponent.getCOMPONENTS();
//                    if (components != null && components.size() != 0) {
//                        mCurComponent = components.get(0);
//                        HttpHelper.getComponentPic(mCurSFC, mCurComponent.getComponentId(), this);
//                        View childAt = mLayout_component.getChildAt(0);
//                        Button btn_component = (Button) childAt.findViewById(R.id.btn_componentName);
//                        btn_component.setEnabled(false);
//                    }
//                }
            } else if (HttpHelper.getSfcComponents.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                mComponent = JSON.parseObject(result.toString(), SuspendComponentBo.class);
                mCurSFC = mComponent.getSFC();
                mTv_curSFC.setText(mCurSFC);
                mCurComponent = null;
                setupComponentView();
//                HttpHelper.getSuspendUndoList(mOperationBo, mContextInfo.getWORK_CENTER(), SuspendFragment.this);
            } else if (HttpHelper.getComponentPic.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                mList_img = JSON.parseArray(result.getJSONArray("PICTURE_URL").toString(), String.class);
                mVP_img.setAdapter(new ImgAdapter(mContext, mList_img, R.layout.item_imageview));
            } else if (HttpHelper.hangerBinding.equals(url)) {
                toast("衣架绑定成功");
            } else if (HttpHelper.hangerUnbind.equals(url)) {
                toast("衣架解绑成功");
            }
        }
    }

}
