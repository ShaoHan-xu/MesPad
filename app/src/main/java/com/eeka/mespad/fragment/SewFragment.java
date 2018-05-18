package com.eeka.mespad.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.eeka.mespad.activity.EmbroiderActivity;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.activity.MainActivity;
import com.eeka.mespad.activity.OutlinePicActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.CommonVPAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.SewDataBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.BitmapUtil;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TabViewUtil;
import com.eeka.mespad.view.dialog.CreateCardDialog;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.LineColorDialog;
import com.eeka.mespad.view.dialog.MyAlertDialog;
import com.eeka.mespad.view.dialog.NCDetailDialog;
import com.eeka.mespad.view.dialog.PocketSizeDialog;
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

    private LinearLayout mLayout_MTMOrderNum;
    private TextView mTv_SFC;//工单号
    private TextView mTv_orderNum;//订单号
    private TextView mTv_MTMOrderNum;//MTM订单号
    private TextView mTv_style;//款号
    private TextView mTv_size;//尺码
    private TextView mTv_matDesc;//物料描述
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
    private TextView mTv_ncDetail;

    private String mRFID;
    private SewDataBo mSewData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        mLayout_MTMOrderNum = mView.findViewById(R.id.layout_sew_salesOrder);
        mTv_SFC = mView.findViewById(R.id.tv_sew_sfc);
        mTv_orderNum = mView.findViewById(R.id.tv_sew_orderNum);
        mTv_MTMOrderNum = mView.findViewById(R.id.tv_sew_MTMOrderNum);
        mTv_matDesc = mView.findViewById(R.id.tv_sew_matDesc);
        mTv_style = mView.findViewById(R.id.tv_sew_style);
        mTv_size = mView.findViewById(R.id.tv_sew_size);
        mTv_workEfficiency = mView.findViewById(R.id.tv_sew_workEfficiency);
        mTv_craftDesc = mView.findViewById(R.id.tv_sew_craftDesc);
        mTv_qualityReq = mView.findViewById(R.id.tv_sew_qualityReq);
        mTv_special = mView.findViewById(R.id.tv_sew_special);
        mTv_lastPosition = mView.findViewById(R.id.tv_sew_lastPosition);
        mTv_ncDetail = mView.findViewById(R.id.tv_sew_ncDetail);
        mTv_ncDetail.setOnClickListener(this);

        mLayout_processTab = mView.findViewById(R.id.layout_sew_processList);
        mLayout_matInfo = mView.findViewById(R.id.layout_sew_matInfo);
        mVP_sop = mView.findViewById(R.id.vp_sew_sop);
        mVP_sop.addOnPageChangeListener(new ViewPagerChangedListener());
        mLv_curProcess = mView.findViewById(R.id.lv_sew_curProcess);
        mLv_nextProcess = mView.findViewById(R.id.lv_sew_nextProcess);

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
        } else if (v.getId() == R.id.tv_sew_ncDetail) {
            new NCDetailDialog(mSewData.getCurrentOpeationInfos(), mContext).show();
        }
        if (!isEmpty(content))
            MyAlertDialog.showAlert(mContext, content);
    }

    public void searchOrder(String rfid) {
        if (isAdded())
            showLoading();
        mRFID = rfid;
        HttpHelper.getSewData(rfid, this);
    }

    public void gotoQC() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        ErrorDialog.showConfirmAlert(mContext, "发现当前实物有不良，需要去质检站？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
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

    /**
     * 厂内外协开始
     */
    public void subStart() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        showLoading();
        HttpHelper.sewSubStart(mRFID, this);
    }

    /**
     * 厂内外协完成
     */
    public void subComplete() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        showLoading();
        HttpHelper.saveSubcontractInfo(mRFID, this);
    }

    /**
     * 显示线迹
     */
    public void showLineColor() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        new LineColorDialog(mContext, mSewData.getSalesOrder()).show();
    }

    /**
     * 绣花信息
     */
    public void showEmbroiderInfo() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        startActivity(EmbroiderActivity.getIntent(mContext, mSewData.getSfc(), null, "UI020"));
    }

    /**
     * 袋口尺寸信息
     */
    public void showPocketSizeInfo() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        new PocketSizeDialog(mContext, mSewData.getShopOrder()).show();
    }

    @Override
    protected void initData() {
        super.initData();
        if (mSewData == null) {
            toast("数据错误");
            return;
        }
        mTv_ncDetail.setVisibility(View.GONE);
        mTv_qualityReq.setText(null);
        mTv_craftDesc.setText(null);
        mTv_SFC.setText(mSewData.getSfc());
        mTv_orderNum.setText(mSewData.getShopOrder());
        mTv_matDesc.setText(mSewData.getItemDesc());
        mTv_style.setText(mSewData.getItem());
        mTv_size.setText(mSewData.getSize());
        mTv_lastPosition.setText(String.format("%s,%s", mSewData.getLastLineCategory(), mSewData.getLastPosition()));
        String remark = mSewData.getSoRemark();
        if (isEmpty(remark)) {
            mTv_special.setText(null);
        } else {
            mTv_special.setText(remark.replace("#line#", "\n"));
        }

        String salesOrder = mSewData.getSalesOrder();
        if (!isEmpty(salesOrder)) {
            mLayout_MTMOrderNum.setVisibility(View.VISIBLE);
            mTv_MTMOrderNum.setText(salesOrder);
        } else {
            mLayout_MTMOrderNum.setVisibility(View.GONE);
        }
        String efficiency = mSewData.getWorkEfficiency();
        mTv_workEfficiency.setText("0%");
        if (!isEmpty(efficiency)) {
            try {
                Float aFloat = Float.valueOf(efficiency);
                float v = aFloat * 100;
                BigDecimal bd = new BigDecimal(v);
                float v1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                mTv_workEfficiency.setText(String.format("%s%%", v1));
            } catch (Exception e) {
                try {
                    throw new DataFormatException("缝制数据：效率转换异常");
                } catch (DataFormatException e1) {
                    e1.printStackTrace();
                }
            }
        }

        for (int i = 0; i < mLayout_matInfo.getChildCount(); i++) {
            View view = mLayout_matInfo.getChildAt(i);
            ImageView imageView = view.findViewById(R.id.iv_materials);
//            BitmapUtil.clearImgMemory(imageView);
            imageView.setImageBitmap(null);
        }
        mLayout_matInfo.removeAllViews();
        List<SewDataBo.SewAttr> matInfos = mSewData.getColorItems();
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
        final List<SewDataBo.SewAttr> curOperation = mSewData.getCurrentOpeationInfos();
        if (mCurProcessAdapter == null) {
            mCurProcessAdapter = new ProcessListAdapter(mContext, curOperation, R.layout.item_textview);
            mLv_curProcess.setAdapter(mCurProcessAdapter);
        } else {
            mCurProcessAdapter.notifyDataSetChanged(curOperation);
        }
        if (curOperation != null) {
            boolean hasNC = false;
            for (int i = 0; i < curOperation.size(); i++) {
                SewDataBo.SewAttr opera = curOperation.get(i);
                String ncDescription = opera.getAttributes().getNC_DESCRIPTION();
                if (!isEmpty(ncDescription)) {
                    hasNC = true;
                }
                final int finalI = i;
                mLayout_processTab.addView(TabViewUtil.getTabView(mContext, opera, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mVP_sop.setCurrentItem(finalI);
                    }
                }));
            }
            if (hasNC) {
                mTv_ncDetail.setVisibility(View.VISIBLE);
            }
            mVP_sop.setAdapter(new CommonVPAdapter<SewDataBo.SewAttr>(mContext, curOperation, R.layout.item_imageview) {
                @Override
                public void convertView(View view, SewDataBo.SewAttr item, final int position) {
                    ImageView imageView = view.findViewById(R.id.imageView);
                    Picasso.with(mContext).load(item.getAttributes().getSOP_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> urls = new ArrayList<>();
                            for (SewDataBo.SewAttr data : curOperation) {
                                urls.add(data.getAttributes().getSOP_URL());
                            }
                            startActivity(ImageBrowserActivity.getIntent(mContext, urls, position));
                        }
                    });
                }
            });

            if (curOperation.size() != 0) {
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
        ImageView imageView = view.findViewById(R.id.iv_materials);
        Picasso.with(mContext).load(item.getAttributes().getMAT_URL()).resize(200, 200).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
        TextView textView = view.findViewById(R.id.tv_matNum);
        textView.setText(item.getDescription());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urls = new ArrayList<>();
                List<SewDataBo.SewAttr> matInfos = mSewData.getColorItems();
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
                SewDataBo sewData = JSON.parseObject(HttpHelper.getResultStr(resultJSON), SewDataBo.class);
                if (mSewData != null) {
                    String sfc = sewData.getSfc();
                    if (!isEmpty(sfc) && !sfc.equals(mSewData.getSfc())) {
                        MainActivity activity = (MainActivity) getActivity();
                        assert activity != null;
                        activity.setButtonState(R.id.btn_subStart, true);
                        activity.setButtonState(R.id.btn_subComplete, true);
                    }
                }
                mSewData = sewData;
                initData();
            } else if (HttpHelper.initNcForQA.equals(url)) {
                toast("操作成功");
            } else if (HttpHelper.sewSubStart.equals(url)) {
                toast("开始绣花工序");
                MainActivity activity = (MainActivity) getActivity();
                assert activity != null;
                activity.setButtonState(R.id.btn_subStart, false);
            } else if (HttpHelper.saveSubcontractInfo.equals(url)) {
                toast("绣花工序完成");
                MainActivity activity = (MainActivity) getActivity();
                assert activity != null;
                activity.setButtonState(R.id.btn_subComplete, false);
            }
        }
    }
}
