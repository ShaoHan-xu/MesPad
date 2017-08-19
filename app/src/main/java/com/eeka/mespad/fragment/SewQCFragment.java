package com.eeka.mespad.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.SuspendComponentBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.UnitUtil;

import java.util.List;

/**
 * 缝制质检
 * Created by Lenovo on 2017/8/8.
 */

public class SewQCFragment extends BaseFragment {

    private LinearLayout mLayout_loginUser;
    private LinearLayout mLayout_sizeInfo;
    private LinearLayout mLayout_productComponent;
    private LinearLayout mLayout_designComponent;
    private LinearLayout mLayout_matInfo;

    private TextView mTv_componentDesc;
    private TextView mTv_SFC;
    private TextView mTv_curProcess;
    private TextView mTv_dayOutput;
    private TextView mTv_monthOutput;
    private TextView mTv_orderNum;
    private TextView mTv_matNum;
    private TextView mTv_matAttr;
    private TextView mTv_size;
    private TextView mTv_special;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_sewqc, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mLayout_loginUser = (LinearLayout) mView.findViewById(R.id.layout_loginUsers);
        mLayout_sizeInfo = (LinearLayout) mView.findViewById(R.id.layout_sewQC_sizeInfo);
        mLayout_productComponent = (LinearLayout) mView.findViewById(R.id.layout_sewQC_productComponent);
        mLayout_designComponent = (LinearLayout) mView.findViewById(R.id.layout_sewQC_designComponent);
        mLayout_matInfo = (LinearLayout) mView.findViewById(R.id.layout_sewQC_matInfo);

        mTv_componentDesc = (TextView) mView.findViewById(R.id.tv_sewQC_componentDesc);
        mTv_SFC = (TextView) mView.findViewById(R.id.tv_sewQC_SFC);
        mTv_curProcess = (TextView) mView.findViewById(R.id.tv_sewQC_curProcess);
        mTv_dayOutput = (TextView) mView.findViewById(R.id.tv_sewQC_dayOutput);
        mTv_monthOutput = (TextView) mView.findViewById(R.id.tv_sewQC_monthOutput);
        mTv_orderNum = (TextView) mView.findViewById(R.id.tv_sewQC_orderNum);
        mTv_matNum = (TextView) mView.findViewById(R.id.tv_sewQC_matNum);
        mTv_matAttr = (TextView) mView.findViewById(R.id.tv_sewQC_matAttr);
        mTv_size = (TextView) mView.findViewById(R.id.tv_sewQC_size);
        mTv_special = (TextView) mView.findViewById(R.id.tv_sewQC_special);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    /**
     * 刷新登录用户、有用户登录或者登出时调用
     */
    public void refreshLoginUsers() {
        mLayout_loginUser.removeAllViews();
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers != null) {
            ScrollView scrollView = (ScrollView) mView.findViewById(R.id.scrollView_loginUsers);
            if (loginUsers.size() >= 3) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UnitUtil.dip2px(mContext, 120));
                scrollView.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                scrollView.setLayoutParams(params);
            }
            for (UserInfoBo userInfo : loginUsers) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loginuser, null);
                TextView tv_userName = (TextView) view.findViewById(R.id.tv_userName);
                TextView tv_userId = (TextView) view.findViewById(R.id.tv_userId);
                tv_userName.setText(userInfo.getUSER());
                tv_userId.setText(userInfo.getEMPLOYEE_NUMBER() + "");
                mLayout_loginUser.addView(view);
            }
        }
    }

    /**
     * 获取导航标签布局
     */
    private <T> View getTabView(T data, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_textview, null);
        TextView tv_tabName = (TextView) view.findViewById(R.id.text);
        tv_tabName.setPadding(10, 10, 10, 10);
        if (data instanceof TailorInfoBo.OPERINFORBean) {
            TailorInfoBo.OPERINFORBean item = (TailorInfoBo.OPERINFORBean) data;
            tv_tabName.setText(item.getDESCRIPTION());
            tv_tabName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else if (data instanceof TailorInfoBo.MatInfoBean) {
            TailorInfoBo.MatInfoBean matInfo = (TailorInfoBo.MatInfoBean) data;
            tv_tabName.setText(matInfo.getMAT_NO());
            tv_tabName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        return view;
    }


    /**
     * 获取尺寸信息布局
     *
     * @param component
     * @return
     */
    private View getSizeInfoView(SuspendComponentBo.COMPONENTSBean component) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sewqc_size, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        TextView tv_sizeAttr = (TextView) view.findViewById(R.id.tv_sewQc_sizeAttr);
        TextView tv_refSize = (TextView) view.findViewById(R.id.tv_sewQc_refSize);
        TextView tv_refTolerance = (TextView) view.findViewById(R.id.tv_sewQc_refTolerance);
        TextView tv_realTolerance = (TextView) view.findViewById(R.id.tv_sewQc_realTolerance);
        EditText et_clothingSize = (EditText) view.findViewById(R.id.et_sewQc_clothingSize);

        return view;
    }

    /**
     * 获取物料属性布局
     *
     * @return
     */
    private View getMatInfo() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_key_value, null);
        TextView tv_key = (TextView) view.findViewById(R.id.tv_key);
        TextView tv_value = (TextView) view.findViewById(R.id.tv_value);
        return view;
    }

}
