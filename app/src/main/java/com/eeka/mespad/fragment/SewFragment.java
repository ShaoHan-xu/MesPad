package com.eeka.mespad.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.CommonVPAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.SewDataBo;
import com.eeka.mespad.http.HttpHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

/**
 * 缝制界面
 * Created by Lenovo on 2017/7/26.
 */

public class SewFragment extends BaseFragment {

    private TextView mTv_SFC;//工单号
    private TextView mTv_orderNum;//订单号
    private TextView mTv_style;//风格
    private TextView mTv_orderType;//订单类型，定制/批量
    private TextView mTv_workEfficiency;//效率
    private TextView mTv_craftDesc;//工艺说明
    private TextView mTv_qualityReq;//质量要求
    private TextView mTv_special;//特殊要求

    private LinearLayout mLayout_processTab;
    private LinearLayout mLayout_matInfo;
    private ViewPager mVP_sop;

    private ListView mLv_curProcess;
    private ListView mLv_lastProcess;
    private TextView mTv_lastPosition;

    private SewDataBo mSewData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_sew, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * EventBus推送的消息
     *
     * @param sewData 缝制的数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SewDataBo sewData) {
        mSewData = sewData;
        initData();
    }

    @Override
    protected void initView() {
        super.initView();

        mTv_SFC = (TextView) mView.findViewById(R.id.tv_sew_sfc);
        mTv_orderNum = (TextView) mView.findViewById(R.id.tv_sew_orderNum);
        mTv_orderType = (TextView) mView.findViewById(R.id.tv_sew_orderType);
        mTv_style = (TextView) mView.findViewById(R.id.tv_sew_style);
        mTv_workEfficiency = (TextView) mView.findViewById(R.id.tv_sew_workEfficiency);
        mTv_craftDesc = (TextView) mView.findViewById(R.id.tv_sew_craftDesc);
        mTv_qualityReq = (TextView) mView.findViewById(R.id.tv_sew_qualityReq);
        mTv_special = (TextView) mView.findViewById(R.id.tv_sew_special);
        mTv_lastPosition = (TextView) mView.findViewById(R.id.tv_sew_lastPosition);

        mLayout_processTab = (LinearLayout) mView.findViewById(R.id.layout_sew_processList);
        mLayout_matInfo = (LinearLayout) mView.findViewById(R.id.layout_sew_matInfo);
        mVP_sop = (ViewPager) mView.findViewById(R.id.vp_sew_sop);
        mVP_sop.addOnPageChangeListener(new ViewPagerChangedListener());
        mLv_curProcess = (ListView) mView.findViewById(R.id.lv_sew_curProcess);
        mLv_lastProcess = (ListView) mView.findViewById(R.id.lv_sew_lastProcess);

    }

    public void getData(String orderNum) {
        showLoading();
        HttpHelper.getSewData(orderNum, this);
    }

    @Override
    protected void initData() {
        super.initData();
        if (mSewData == null) {
            toast("数据错误");
            return;
        }
        mTv_SFC.setText(mSewData.getSfc());
        mTv_orderNum.setText(mSewData.getShopOrder());
        mTv_style.setText(mSewData.getItem());
        mTv_special.setText(mSewData.getSoRemark());
        mTv_lastPosition.setText(mSewData.getLastLineCategory() + "," + mSewData.getLastPosition());
        String salesOrder = mSewData.getSalesOrder();
        if (isEmpty(salesOrder)) {
            mTv_orderType.setText("批量订单");
        } else {
            mTv_orderType.setText("定制订单");
        }
        String efficiency = mSewData.getWorkEfficiency();
        if (!isEmpty(efficiency)) {
            try {
                Float aFloat = Float.valueOf(efficiency);
                float v = aFloat * 100;
                BigDecimal bd = new BigDecimal(v);
                float v1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                mTv_workEfficiency.setText("当前效率：" + v1 + "%");
            } catch (Exception e) {
                try {
                    throw new DataFormatException("缝制数据：效率转换异常");
                } catch (DataFormatException e1) {
                    e1.printStackTrace();
                }
            }
        }

        mLayout_matInfo.removeAllViews();
        List<SewDataBo.SewAttr> matInfos = mSewData.getMaterialPictures();
        if (matInfos != null) {
            for (int i = 0; i < matInfos.size(); i++) {
                SewDataBo.SewAttr matInfo = matInfos.get(i);
                mLayout_matInfo.addView(getMatView(matInfo, i));
            }
        }

        List<SewDataBo.SewAttr> lastOperations = mSewData.getLastOperations();
        if (lastOperations != null) {
            mLv_lastProcess.setAdapter(new ProcessListAdapter(mContext, lastOperations, R.layout.item_textview));
        }

        mLayout_processTab.removeAllViews();
        final List<SewDataBo.SewAttr> opeationInfos = mSewData.getCurrentOpeationInfos();
        if (opeationInfos != null) {
            for (int i = 0; i < opeationInfos.size(); i++) {
                SewDataBo.SewAttr opera = opeationInfos.get(i);
                mLayout_processTab.addView(getProcessTabView(opera, i));
            }

            mLv_curProcess.setAdapter(new ProcessListAdapter(mContext, opeationInfos, R.layout.item_textview));

            mVP_sop.setAdapter(new CommonVPAdapter<SewDataBo.SewAttr>(mContext, opeationInfos, R.layout.item_imageview) {
                @Override
                public void convertView(View view, SewDataBo.SewAttr item, final int position) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                    Glide.with(mContext).load(item.getAttributes().getSOP_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> urls = new ArrayList<>();
                            for (SewDataBo.SewAttr data : opeationInfos) {
                                urls.add(data.getAttributes().getMAT_URL());
                            }
                            startActivity(ImageBrowserActivity.getIntent(mContext, urls, position));
                        }
                    });
                }
            });

            if (opeationInfos.size() != 0) {
                refreshProcessView(0);
            }
        }
    }

    /**
     * 工序列表适配器
     */
    private class ProcessListAdapter extends CommonAdapter<SewDataBo.SewAttr> {

        ProcessListAdapter(Context context, List<SewDataBo.SewAttr> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, SewDataBo.SewAttr item, int position) {
            holder.setText(R.id.text, item.getDescription());
        }
    }

    /**
     * 获取物料布局
     *
     * @param item
     * @param position
     * @return
     */
    private View getMatView(SewDataBo.SewAttr item, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_materials);
        Glide.with(this).load(item.getAttributes().getMAT_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
        TextView textView = (TextView) view.findViewById(R.id.tv_matNum);
        textView.setText(item.getDescription());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urls = new ArrayList<>();
                List<SewDataBo.SewAttr> matInfos = mSewData.getMaterialPictures();
                for (SewDataBo.SewAttr matInfo : matInfos) {
                    urls.add(matInfo.getAttributes().getMAT_URL());
                }
                startActivity(ImageBrowserActivity.getIntent(mContext, urls, position));
            }
        });
        return view;
    }

    private class ViewPagerChangedListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            refreshProcessView(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 刷新工序相关的界面，包括工序图、工艺说明、品质要求、
     *
     * @param position
     */
    private void refreshProcessView(int position) {
        SewDataBo.SewAttr item = mSewData.getCurrentOpeationInfos().get(position);
        String craftDesc = item.getAttributes().getOPERATION_INSTRUCTION();
        if (!isEmpty(craftDesc))
            mTv_craftDesc.setText(craftDesc.replace("\\n", "\n"));
        String qualityDesc = item.getAttributes().getQUALITY_REQUIREMENT();
        if (!isEmpty(qualityDesc))
            mTv_qualityReq.setText(qualityDesc.replace("\\n", "\n"));

        refreshMatTab(position);
    }

    /**
     * 刷新物料标签视图
     *
     * @param position
     */
    private void refreshMatTab(int position) {
        int childCount = mLayout_processTab.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mLayout_processTab.getChildAt(i);
            childAt.setBackgroundResource(R.color.white);
        }
        mLayout_processTab.getChildAt(position).setBackgroundResource(R.color.text_gray_default);
    }


    /**
     * 顶部工序导航标签布局
     *
     * @param item
     * @return
     */
    private View getProcessTabView(SewDataBo.SewAttr item, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_textview, null);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(item.getDescription());
        textView.setPadding(20, 20, 20, 20);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVP_sop.setCurrentItem(position);
            }
        });
        return view;
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.findProcessWithPadId_url.equals(url)) {
                if (isAdded()) {
                    showLoading();
                }
                HttpHelper.getSewData("EB20170803", this);
            } else if (HttpHelper.getSewData.equals(url)) {
                mSewData = JSON.parseObject(HttpHelper.getResultStr(resultJSON), SewDataBo.class);
                initData();
            }
        }
    }
}
