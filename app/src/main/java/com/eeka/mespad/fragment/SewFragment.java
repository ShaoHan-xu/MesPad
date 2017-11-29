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
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.activity.OutlinePicActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.CommonVPAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.SewDataBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TabViewUtil;
import com.eeka.mespad.view.dialog.CreateCardDialog;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.MyAlertDialog;
import com.eeka.mespad.view.dialog.SewReturnMatDialog;
import com.squareup.picasso.Picasso;

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
    private TextView mTv_MTMOrderNum;//MTM订单号
    private TextView mTv_style;//款号
    private TextView mTv_size;//尺码
    private TextView mTv_orderType;//订单类型，定制/批量
    private TextView mTv_workEfficiency;//效率
    private TextView mTv_craftDesc;//工艺说明
    private TextView mTv_qualityReq;//质量要求
    private TextView mTv_special;//特殊要求

    private LinearLayout mLayout_processTab;
    private LinearLayout mLayout_matInfo;
    private ViewPager mVP_sop;

    private ProcessListAdapter mCurProcessAdapter;
    private ProcessListAdapter mNextProcessAdapter;
    private ListView mLv_curProcess;
    private ListView mLv_nextProcess;
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

        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        mTv_SFC = (TextView) mView.findViewById(R.id.tv_sew_sfc);
        mTv_orderNum = (TextView) mView.findViewById(R.id.tv_sew_orderNum);
        mTv_MTMOrderNum = (TextView) mView.findViewById(R.id.tv_sew_MTMOrderNum);
        mTv_orderType = (TextView) mView.findViewById(R.id.tv_sew_orderType);
        mTv_style = (TextView) mView.findViewById(R.id.tv_sew_style);
        mTv_size = (TextView) mView.findViewById(R.id.tv_sew_size);
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
        mLv_nextProcess = (ListView) mView.findViewById(R.id.lv_sew_nextProcess);

        mView.findViewById(R.id.layout_sew_craftDesc).setOnClickListener(this);
        mView.findViewById(R.id.layout_sew_qualityReq).setOnClickListener(this);
        mView.findViewById(R.id.layout_sew_special).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        String content = null;
        if (v.getId() == R.id.layout_sew_craftDesc) {
            content = mTv_craftDesc.getText().toString();
        } else if (v.getId() == R.id.layout_sew_qualityReq) {
            content = mTv_qualityReq.getText().toString();
        } else if (v.getId() == R.id.layout_sew_special) {
            content = mTv_special.getText().toString();
        }
        if (!isEmpty(content))
            MyAlertDialog.showAlert(mContext, content);
    }

    public void searchOrder(String orderNum) {
        if (isAdded())
            showLoading();
        HttpHelper.getSewData(orderNum, this);
    }

    public void gotoQC() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        ErrorDialog.showConfirmAlert(mContext, "发现当前实物有不良，需要去质检站？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
                HttpHelper.initNcForQA(mSewData.getSfc(), resource.getRESOURCE_BO(), SewFragment.this);
            }
        });
    }

    /**
     * 退补料申请
     */
    public void returnOrFeeding() {
        if (mSewData == null)
            new SewReturnMatDialog(mContext, null).show();
        else
            new SewReturnMatDialog(mContext, mSewData.getSfc()).show();
    }

    /**
     * 解绑
     */
    public void unBind() {
        if (mSewData != null) {
            new CreateCardDialog(mContext, mSewData.getSfc()).show();
        } else {
            new CreateCardDialog(mContext, null).show();
        }
    }

    /**
     * 播放视频
     */
    public void playVideo() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        int currentItem = mVP_sop.getCurrentItem();
        List<SewDataBo.SewAttr> infos = mSewData.getCurrentOpeationInfos();
        if (infos == null || infos.size() == 0) {
            toast("当前站位无工序");
            return;
        }
        SewDataBo.SewAttr sewAttr = infos.get(currentItem);
        SystemUtils.playVideo(mContext, sewAttr.getAttributes().getVIDEO_URL());
    }

    /**
     * 查看工艺图
     */
    public void lookOutlinePic() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        startActivity(OutlinePicActivity.getIntent(mContext, mSewData.getSfc()));
    }

    @Override
    protected void initData() {
        super.initData();
        if (mSewData == null) {
            toast("数据错误");
            return;
        }
        mTv_qualityReq.setText(null);
        mTv_craftDesc.setText(null);
        mTv_SFC.setText(mSewData.getSfc());
        mTv_orderNum.setText(mSewData.getShopOrder());
        mTv_style.setText(mSewData.getItem());
        mTv_size.setText(mSewData.getSize());
        String remark = mSewData.getSoRemark();
        if (isEmpty(remark)) {
            mTv_special.setText(null);
        } else {
            mTv_special.setText(remark.replace("#line#", "\n"));
        }
        mTv_lastPosition.setText(mSewData.getLastLineCategory() + "," + mSewData.getLastPosition());
        String salesOrder = mSewData.getSalesOrder();
        mTv_MTMOrderNum.setText(mSewData.getSalesOrder());
        if (isEmpty(salesOrder)) {
            mTv_orderType.setText("批量订单");
        } else {
            mTv_orderType.setText("定制订单");
        }
        String efficiency = mSewData.getWorkEfficiency();
        mTv_workEfficiency.setText("0%");
        if (!isEmpty(efficiency)) {
            try {
                Float aFloat = Float.valueOf(efficiency);
                float v = aFloat * 100;
                BigDecimal bd = new BigDecimal(v);
                float v1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                mTv_workEfficiency.setText(v1 + "%");
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

        List<SewDataBo.SewAttr> nextOperations = mSewData.getNextOperation();
        if (mNextProcessAdapter == null) {
            mNextProcessAdapter = new ProcessListAdapter(mContext, nextOperations, R.layout.item_textview);
            mLv_nextProcess.setAdapter(mNextProcessAdapter);
        } else {
            mNextProcessAdapter.notifyDataSetChanged(nextOperations);
        }

        mLayout_processTab.removeAllViews();
        final List<SewDataBo.SewAttr> opeationInfos = mSewData.getCurrentOpeationInfos();
        if (mCurProcessAdapter == null) {
            mCurProcessAdapter = new ProcessListAdapter(mContext, opeationInfos, R.layout.item_textview);
            mLv_curProcess.setAdapter(mCurProcessAdapter);
        } else {
            mCurProcessAdapter.notifyDataSetChanged(opeationInfos);
        }
        if (opeationInfos != null) {
            for (int i = 0; i < opeationInfos.size(); i++) {
                SewDataBo.SewAttr opera = opeationInfos.get(i);
                final int finalI = i;
                mLayout_processTab.addView(TabViewUtil.getTabView(mContext, opera, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mVP_sop.setCurrentItem(finalI);
                    }
                }));
            }

            mVP_sop.setAdapter(new CommonVPAdapter<SewDataBo.SewAttr>(mContext, opeationInfos, R.layout.item_imageview) {
                @Override
                public void convertView(View view, SewDataBo.SewAttr item, final int position) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                    Picasso.with(mContext).load(item.getAttributes().getSOP_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> urls = new ArrayList<>();
                            for (SewDataBo.SewAttr data : opeationInfos) {
                                urls.add(data.getAttributes().getSOP_URL());
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
            holder.setText(R.id.textView, item.getDescription());
        }
    }

    /**
     * 获取物料布局
     */
    private View getMatView(SewDataBo.SewAttr item, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_materials);
        Picasso.with(mContext).load(item.getAttributes().getMAT_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
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
     */
    private void refreshProcessView(int position) {
        SewDataBo.SewAttr item = mSewData.getCurrentOpeationInfos().get(position);
        String craftDesc = item.getAttributes().getOPERATION_INSTRUCTION();
        if (!isEmpty(craftDesc))
            mTv_craftDesc.setText(craftDesc.replace("#line#", "\n"));
        String qualityDesc = item.getAttributes().getQUALITY_REQUIREMENT();
        if (!isEmpty(qualityDesc))
            mTv_qualityReq.setText(qualityDesc.replace("#line#", "\n"));

        TabViewUtil.refreshTabView(mLayout_processTab, position);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getSewData.equals(url)) {
                mSewData = JSON.parseObject(HttpHelper.getResultStr(resultJSON), SewDataBo.class);
                initData();
            } else if (HttpHelper.initNcForQA.equals(url)) {
                toast("操作成功");
            }
        }
    }
}
