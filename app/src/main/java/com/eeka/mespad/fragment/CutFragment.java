package com.eeka.mespad.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.StartWorkParamsBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UpdateLabuBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.UnitUtil;
import com.eeka.mespad.view.dialog.RecordLabuDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 裁剪/拉布界面
 */
public class CutFragment extends BaseFragment {

    private ViewPager mVP_process;
    private VPAdapter mVPAdapter_process;
    private ViewPager mVP_matInfo;
    private VPAdapter mVPAdapter_matInfo;
    private List<TailorInfoBo.OPERINFORBean> mList_processData;//工序列表数据

    private LinearLayout mLayout_material1;//排料图
    private LinearLayout mLayout_material2;//粘朴图
    private LinearLayout mLayout_sizeInfo;
    private TailorInfoBo mTailorInfo;//主数据

    private ListView mLv_process;

    private LinearLayout mLayout_processTab;//工序页签
    private LinearLayout mLayout_matTab;//物料图页签
    private TextView mTv_nextProcess;
    private Button mBtn_done;

    private LinearLayout mLayout_loginUser;

    private TailorInfoBo.ResultInfo mResultInfo;

    public UpdateLabuBo mLabuData;//记录拉布数据里面的数据

    private boolean showDone;
    private String mOrderType;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_cut, null);
        return mView;
    }

    public void getData(String orderType, String orderNum, String resourceBo, String processLotBo) {
        showLoading();
        mOrderType = orderType;
        HttpHelper.viewCutPadInfo(orderType, orderNum, resourceBo, processLotBo, this);
    }

    protected void initView() {
        mVP_process = (ViewPager) mView.findViewById(R.id.vp_main_processDesc);
        mVP_matInfo = (ViewPager) mView.findViewById(R.id.vp_main_matInfo);

        mLayout_material1 = (LinearLayout) mView.findViewById(R.id.layout_material1);
        mLayout_material2 = (LinearLayout) mView.findViewById(R.id.layout_material2);
        mLayout_sizeInfo = (LinearLayout) mView.findViewById(R.id.layout_sizeInfo);
        mLayout_processTab = (LinearLayout) mView.findViewById(R.id.layout_processTab);
        mLayout_matTab = (LinearLayout) mView.findViewById(R.id.layout_matTab);
        mTv_nextProcess = (TextView) mView.findViewById(R.id.tv_nextProcess);

        mLayout_loginUser = (LinearLayout) mView.findViewById(R.id.layout_loginUsers);

        mLv_process = (ListView) mView.findViewById(R.id.lv_processList);
        mLv_process.setOnItemClickListener(new ProcessClickListener());

        mBtn_done = (Button) mView.findViewById(R.id.btn_done);
        if (showDone) {
            mBtn_done.setVisibility(View.VISIBLE);
        }
        mBtn_done.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        List<UserInfoBo> userInfo = SpUtil.getPositionUsers();
        if (userInfo != null) {
            refreshLoginUsers();
        }
    }

    public void refreshView() {
        mView.findViewById(R.id.layout_processDescription).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.layout_orderInfo).setVisibility(View.VISIBLE);

        //物料数据
        mLayout_material1.removeAllViews();
        mLayout_matTab.removeAllViews();
        if (mTailorInfo == null) {
            return;
        }
        final List<TailorInfoBo.MatInfoBean> itemArray = mTailorInfo.getMAT_INFOR();
        if (itemArray != null && itemArray.size() != 0) {
            for (int i = 0; i < itemArray.size(); i++) {
                TailorInfoBo.MatInfoBean matInfoBean = itemArray.get(i);
                mLayout_matTab.addView(getTabView(matInfoBean, i));
            }
            refreshTab(mLayout_matTab, 0);
        }

        mVPAdapter_matInfo = new VPAdapter<>(itemArray);
        mVP_matInfo.setAdapter(mVPAdapter_matInfo);
        mVP_matInfo.addOnPageChangeListener(new ViewPagerChangedListener(ViewPagerChangedListener.TYPE_MAT));

        //粘朴数据
        mLayout_material2.removeAllViews();
        List<TailorInfoBo.StickyInfo> stickyInfo = mTailorInfo.getSTICKY_INFOR();
        for (int i = 0; i < stickyInfo.size(); i++) {
            TailorInfoBo.StickyInfo info = stickyInfo.get(i);
            mLayout_material2.addView(getMaterialsView(info, i));
        }

        //排料图数据
        mLayout_material1.removeAllViews();
        List<TailorInfoBo.LayoutInfoBean> layoutArray = mTailorInfo.getLAYOUT_INFOR();
        if (layoutArray != null) {
            for (int i = 0; i < layoutArray.size(); i++) {
                mLayout_material1.addView(getLayoutView(layoutArray.get(i), i));
            }
        }

        int childCount = mLayout_sizeInfo.getChildCount();
        for (int i = childCount - 1; i > 0; i--) {
            mLayout_sizeInfo.removeViewAt(i);
        }
        List<TailorInfoBo.CUTSIZESBean> sizeArray = mTailorInfo.getCUT_SIZES();
        if (sizeArray == null) {
            mLayout_sizeInfo.setVisibility(View.GONE);
        } else {
            mLayout_sizeInfo.setVisibility(View.VISIBLE);
            for (int i = 0; i < sizeArray.size(); i++) {
                mLayout_sizeInfo.addView(getSizeInfoView(sizeArray.get(i)));
            }
        }

        List<TailorInfoBo.OPERINFORBean> list = mTailorInfo.getOPER_INFOR();
        mVPAdapter_process = new VPAdapter<>(list);
        mVP_process.setAdapter(mVPAdapter_process);
        mVP_process.addOnPageChangeListener(new ViewPagerChangedListener(ViewPagerChangedListener.TYPE_PROCESS));

        mLv_process.setAdapter(new CommonAdapter<TailorInfoBo.OPERINFORBean>(mContext, list, R.layout.item_textview) {
            @Override
            public void convert(ViewHolder holder, TailorInfoBo.OPERINFORBean item, int position) {
                TextView textView = holder.getView(R.id.text);
                textView.setText(item.getDESCRIPTION());
                textView.setPadding(10, 10, 10, 10);
            }
        });
        mLv_process.setItemChecked(0, true);

        //工序数据
        mLayout_processTab.removeAllViews();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                mLayout_processTab.addView(getTabView(mList_processData.get(i), i));
            }
            if (list.size() != 0) {
                refreshProcessView(0);
            }
        }
        TailorInfoBo.NextOrderInfo nextOperInfo = mTailorInfo.getNEXT_OPER_INFOR();
        if (nextOperInfo != null) {
            mTv_nextProcess.setText(nextOperInfo.getOPER_DESC());
        }

        TextView tv_orderNum = (TextView) mView.findViewById(R.id.tv_sew_orderNum);
//        TextView tv_batchNum = (TextView) mView.findViewById(R.id.tv_batchNum);
        TextView tv_style = (TextView) mView.findViewById(R.id.tv_sew_style);
        TextView tv_qty = (TextView) mView.findViewById(R.id.tv_sew_qty);
        TextView tv_special = (TextView) mView.findViewById(R.id.tv_special);
        TailorInfoBo.SHOPORDERINFORBean orderInfo = mTailorInfo.getSHOP_ORDER_INFOR();
        tv_orderNum.setText(orderInfo.getSHOP_ORDER());
//        tv_batchNum.setText(orderInfo.getPROCESS_LOT());
        tv_style.setText(orderInfo.getITEM());
        tv_qty.setText(orderInfo.getORDER_QTY() + "/件");
        tv_special.setText(orderInfo.getSO_REMARK());

        refreshLoginUsers();
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
                    mVP_process.setCurrentItem(position);
                }
            });
        } else if (data instanceof TailorInfoBo.MatInfoBean) {
            TailorInfoBo.MatInfoBean matInfo = (TailorInfoBo.MatInfoBean) data;
            tv_tabName.setText(matInfo.getMAT_NO());
            tv_tabName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> urls = new ArrayList<>();
                    for (TailorInfoBo.MatInfoBean mat : mTailorInfo.getMAT_INFOR()) {
                        urls.add(mat.getMAT_URL());
                    }
                    startActivity(ImageBrowserActivity.getIntent(mContext, urls, position));
                }
            });
        }
        return view;
    }

    /**
     * 刷新工序相关的界面，包括工序图、工艺说明、品质要求、
     *
     * @param position
     */
    private void refreshProcessView(int position) {
        TailorInfoBo.OPERINFORBean item = mList_processData.get(position);

//        TextView tv_craftDesc = (TextView) mView.findViewById(R.id.tv_craftDescribe);
        TextView tv_qualityDes = (TextView) mView.findViewById(R.id.tv_qualityDescribe);
//        String craftDesc = item.getOPERATION_INSTRUCTION();
//        if (!isEmpty(craftDesc))
//            tv_craftDesc.setText(craftDesc.replace("\\n", "\n"));
        String qualityDesc = item.getQUALITY_REQUIREMENT();
        if (!isEmpty(qualityDesc))
            tv_qualityDes.setText(qualityDesc.replace("\\n", "\n"));

        refreshTab(mLayout_processTab, position);

    }

    /**
     * 刷新标签视图
     *
     * @param position
     */
    private void refreshTab(ViewGroup parent, int position) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            childAt.setBackgroundResource(R.color.white);
        }
        parent.getChildAt(position).setBackgroundResource(R.color.text_gray_default);
    }

    public void startWork() {
        if (mTailorInfo == null) {
            toast("数据错误");
            return;
        }
        showLoading();
        if ("S".equals(mOrderType)) {
            HttpHelper.startCustomWork(getStartAndCompleteParams(), this);
        } else {
            HttpHelper.startBatchWork(getStartAndCompleteParams(), this);
        }
    }

    private StartWorkParamsBo getStartAndCompleteParams() {
        TailorInfoBo.SHOPORDERINFORBean orderInfo = mTailorInfo.getSHOP_ORDER_INFOR();
        StartWorkParamsBo params = new StartWorkParamsBo();
        params.setPAD_ID(HttpHelper.PAD_IP);
        params.setPROCESS_LOTS(orderInfo.getPROCESS_LOT_BO());
        params.setSHOP_ORDER(orderInfo.getSHOP_ORDER());
        params.setSHOP_ORDER_BO(orderInfo.getSHOP_ORDER_BO());
        params.setLAYERS(orderInfo.getLAYERS());
        params.setRESOURCE_BO(mResultInfo.getRESOURCE_BO());
        params.setORDER_QTY(orderInfo.getORDER_QTY());
        List<String> opList = new ArrayList<>();
        if (mList_processData != null) {
            for (TailorInfoBo.OPERINFORBean item : mList_processData) {
                opList.add(item.getOPERATION_BO());
            }
        }
        params.setOPERATIONS(opList);
        return params;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_done) {
            if (mTailorInfo == null) {
                toast("请先开始作业");
                return;
            }
            if ("P".equals(mOrderType)) {
                if (mLabuData == null || mLabuData.getLAYOUTS() == null || mLabuData.getLAYOUTS().size() == 0) {
                    toast("请先记录拉布数据");
                    showRecordLabuDialog();
                    return;
                }
            }
            showLoading();
            if ("S".equals(mOrderType)) {
                HttpHelper.completeCustomWork(getStartAndCompleteParams(), this);
            } else {
                HttpHelper.completeBatchWork(getStartAndCompleteParams(), this);
            }
        }
    }

    /**
     * 当前工序点击事件
     */
    private class ProcessClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mVP_process.setCurrentItem(position);
        }
    }

    /**
     * 获取物料图
     *
     * @param data
     * @param position
     * @return
     */
    private View getMaterialsView(final Object data, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_material = (ImageView) view.findViewById(R.id.iv_materials);
        TextView tv_material = (TextView) view.findViewById(R.id.tv_matNum);
        if (data instanceof TailorInfoBo.LayoutInfoBean) {
            TailorInfoBo.LayoutInfoBean item = (TailorInfoBo.LayoutInfoBean) data;
            Glide.with(mContext).load(item.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(iv_material);
            iv_material.setTag(position);
            tv_material.setText(item.getLAYOUT());
            iv_material.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> urls = new ArrayList<>();
                    List<TailorInfoBo.LayoutInfoBean> list = mTailorInfo.getLAYOUT_INFOR();
                    for (TailorInfoBo.LayoutInfoBean item : list) {
                        urls.add(item.getPICTURE_URL());
                    }
                    startActivity(ImageBrowserActivity.getIntent(mContext, urls, (Integer) v.getTag()));
                }
            });
        } else if (data instanceof TailorInfoBo.StickyInfo) {
            TailorInfoBo.StickyInfo item = (TailorInfoBo.StickyInfo) data;
            Glide.with(mContext).load(item.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(iv_material);
            iv_material.setTag(position);
            tv_material.setText(item.getIDENTITY_INFO());
            iv_material.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> urls = new ArrayList<>();
                    List<TailorInfoBo.StickyInfo> list = mTailorInfo.getSTICKY_INFOR();
                    for (TailorInfoBo.StickyInfo item : list) {
                        urls.add(item.getPICTURE_URL());
                    }
                    startActivity(ImageBrowserActivity.getIntent(mContext, urls, (Integer) v.getTag()));
                }
            });
        }
        return view;
    }

    /**
     * 获取排料图
     *
     * @param item
     * @param position
     * @return
     */
    private View getLayoutView(final TailorInfoBo.LayoutInfoBean item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_material = (ImageView) view.findViewById(R.id.iv_materials);
        TextView tv_material = (TextView) view.findViewById(R.id.tv_matNum);
        Glide.with(mContext).load(item.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(iv_material);
        iv_material.setTag(position);
        tv_material.setText(item.getLAYOUT());

        iv_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urls = new ArrayList<>();
                List<TailorInfoBo.LayoutInfoBean> list = mTailorInfo.getLAYOUT_INFOR();
                for (TailorInfoBo.LayoutInfoBean item : list) {
                    urls.add(item.getPICTURE_URL());
                }

                startActivity(ImageBrowserActivity.getIntent(mContext, urls, (Integer) v.getTag()));
            }
        });
        return view;
    }

    private View getSizeInfoView(TailorInfoBo.CUTSIZESBean sizeInfo) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sizeinfo, null);
        TextView tv_yardage = (TextView) view.findViewById(R.id.tv_item_yardage);
        TextView tv_count = (TextView) view.findViewById(R.id.tv_item_count);
        tv_yardage.setText(sizeInfo.getSIZE());
        tv_count.setText(sizeInfo.getSIZE_AMOUNT() + "");
        return view;
    }

    private RecordLabuDialog mLabuDialog;

    public void showRecordLabuDialog() {
        mLabuDialog = new RecordLabuDialog(mContext, mTailorInfo, mLabuData, mOrderType, new RecordLabuDialog.OnRecordLabuCallback() {
            @Override
            public void recordLabuCallback(UpdateLabuBo labuData, boolean done) {
                mLabuData = labuData;
                showLoading();
                if (done) {
                    HttpHelper.saveLabuDataAndComplete(labuData, CutFragment.this);
                } else {
                    HttpHelper.saveLabuData(labuData, CutFragment.this);
                }
            }
        });
        mLabuDialog.show();
    }

    public void showCompleteButton() {
        showDone = true;
        if (mBtn_done != null) {
            mBtn_done.setVisibility(View.VISIBLE);
        }
    }

    private class ViewPagerChangedListener implements ViewPager.OnPageChangeListener {

        static final int TYPE_MAT = 0;
        static final int TYPE_PROCESS = 1;
        int TYPE;

        public ViewPagerChangedListener(int TYPE) {
            this.TYPE = TYPE;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (TYPE == TYPE_MAT) {
                refreshTab(mLayout_matTab, position);
            } else {
                refreshProcessView(position);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class VPAdapter<T> extends PagerAdapter {

        private List<T> data;

        VPAdapter(List<T> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_textview, null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            Object object = data.get(position);
            if (object instanceof TailorInfoBo.MatInfoBean) {
                TailorInfoBo.MatInfoBean matInfo = (TailorInfoBo.MatInfoBean) object;
                textView.setText("1、" + matInfo.getGRAND_CATEGORY() + "\n2、" + matInfo.getMID_CATEGORY());
            } else if (object instanceof TailorInfoBo.OPERINFORBean) {
                TailorInfoBo.OPERINFORBean operInfo = (TailorInfoBo.OPERINFORBean) object;
                String quality = operInfo.getOPERATION_INSTRUCTION();
                if (!isEmpty(quality))
                    textView.setText(quality.replace("\\n", "\n"));
            }
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

        private WeakReference<CutFragment> mWeakActivity;

        MyHandler(CutFragment activity) {
            this.mWeakActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CutFragment activity = mWeakActivity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    int currentItem = activity.mVP_process.getCurrentItem();
                    if (currentItem >= activity.mList_processData.size() - 1) {
                        currentItem = -1;
                    }
                    activity.mVP_process.setCurrentItem(currentItem + 1);
                    sendEmptyMessageDelayed(0, 3000);
                    break;
            }
        }
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

    public void signOff() {
        if (mTailorInfo == null) {
            toast("请获取订单信息");
            return;
        }
        TailorInfoBo.SHOPORDERINFORBean shopOrderInfo = mTailorInfo.getSHOP_ORDER_INFOR();
        List<TailorInfoBo.OPERINFORBean> operInfo = mTailorInfo.getOPER_INFOR();
        if (operInfo != null && operInfo.size() != 0) {
            showLoading();
            JSONObject json = new JSONObject();
            json.put("SHOP_ORDER_BO", shopOrderInfo.getSHOP_ORDER_BO());
            if ("S".equals(mOrderType)) {
                json.put("RESOURCE_BO", mTailorInfo.getRESR_INFOR().getRESOURCE_BO());
                json.put("OPERATION_BO", operInfo.get(0).getOPERATION_BO());
                HttpHelper.signoffByShopOrder(json, this);
            } else {
                json.put("PROCESS_LOTS", shopOrderInfo.getPROCESS_LOT_BO());
                json.put("RESOURCE_BO", mTailorInfo.getRESR_INFOR().getRESOURCE_BO());
                json.put("OPERATION_BO", operInfo.get(0).getOPERATION_BO());
                HttpHelper.signoffByProcessLot(json, this);
            }
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            switch (url) {
                case HttpHelper.positionLogin_url:
                    refreshLoginUsers();
                    break;
                case HttpHelper.findProcessWithPadId_url:
                    JSONObject result = resultJSON.getJSONObject("result");
                    mResultInfo = JSON.parseObject(result.getJSONObject("RESR_INFOR").toString(), TailorInfoBo.ResultInfo.class);
                    mList_processData = JSON.parseArray(result.getJSONArray("OPER_INFOR").toString(), TailorInfoBo.OPERINFORBean.class);
                    break;
                case HttpHelper.viewCutPadInfo_url:
                    JSONObject result1 = resultJSON.getJSONObject("result");
                    mTailorInfo = JSON.parseObject(result1.toString(), TailorInfoBo.class);
                    if (mTailorInfo.getOPER_INFOR() == null || mTailorInfo.getOPER_INFOR().size() == 0) {
                        mBtn_done.setEnabled(false);
                        mTailorInfo.setOPER_INFOR(mList_processData);
                    } else {
                        mBtn_done.setEnabled(true);
                        mList_processData = mTailorInfo.getOPER_INFOR();
                    }
                    mTailorInfo.setRESR_INFOR(mResultInfo);
                    refreshView();
                    break;
                case HttpHelper.startBatchWork_url:
                case HttpHelper.startCustomWork_url:
//                    mBtn_done.setText("完成");
//                    mBtn_done.setBackgroundResource(R.drawable.btn_primary);
                    toast("开始作业");
                    break;
                case HttpHelper.completeBatchWork_url:
                case HttpHelper.completeCustomWork_url:
//                    mBtn_done.setText("开始");
//                    mBtn_done.setBackgroundResource(R.drawable.btn_green);
                    toast("工序已完成");
                    break;
                case HttpHelper.saveLabuData:
                    toast("保存成功");
                    break;
                case HttpHelper.saveLabuDataAndComplete:
                    toast("保存成功");
                    if (mLabuDialog != null && mLabuDialog.isShowing()) {
                        mLabuDialog.dismiss();
                    }
                    break;
                case HttpHelper.signoffByShopOrder:
                case HttpHelper.signoffByProcessLot:
                    toast("注销在制品成功，可重新开始");
                    break;
            }
        }
    }

    public TailorInfoBo getMaterialData() {
        return mTailorInfo;
    }
}
