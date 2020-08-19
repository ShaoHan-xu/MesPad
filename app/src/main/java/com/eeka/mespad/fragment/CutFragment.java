package com.eeka.mespad.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
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
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.activity.MainActivity;
import com.eeka.mespad.activity.RecordCutNCActivity;
import com.eeka.mespad.activity.RecordLabuActivity;
import com.eeka.mespad.activity.WebActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.NcDataBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.RecordNCBo;
import com.eeka.mespad.bo.ReturnMaterialInfoBo;
import com.eeka.mespad.bo.StartWorkParamsBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TabViewUtil;
import com.eeka.mespad.view.dialog.AutoPickDialog;
import com.eeka.mespad.view.dialog.CutRecordQtyDialog;
import com.eeka.mespad.view.dialog.CutReturnMatDialog;
import com.eeka.mespad.view.dialog.MyAlertDialog;
import com.eeka.mespad.view.dialog.PatternDialog;
import com.eeka.mespad.view.dialog.SplitCardDialog;
import com.eeka.mespad.view.dialog.StickyDialog;
import com.eeka.mespad.zxing.EncodingHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 裁剪/拉布界面
 */
public class CutFragment extends BaseFragment {
    private static final int REQUEST_RECORD_NC = 0;
    private static final int REQUEST_RECORD_LABU = 1;
    private ViewPager mVP_process;
    private ViewPager mVP_matInfo;
    private List<TailorInfoBo.OPERINFORBean> mList_padProcess;//工序列表数据

    private LinearLayout mLayout_material1;//排料图
    private LinearLayout mLayout_material2;//粘朴图
    private LinearLayout mLayout_sizeInfo;
    private LinearLayout mLayout_planSize;
    private ScrollView mScrollView_planSize;
    private TailorInfoBo mTailorInfo;//主数据

    private ListView mLv_process;

    private LinearLayout mLayout_processTab;//工序页签
    private LinearLayout mLayout_matTab;//物料图页签
    private TextView mTv_nextProcess;
    private TextView mTv_qualityDesc;
    private TextView mTv_special;
    private Button mBtn_done;
    private TextView mTv_sizeCode;
    private LinearLayout mLayout_ncData;
    private TextView mTv_ncDesc;
    private LinearLayout mLayout_mtmOrderNum;
    private LinearLayout mLayout_sfc;
    private TextView mTv_sfc;
    private TextView mTv_workCenter;

    //套排
    private LinearLayout mLayout_TP;
    private TextView mTv_TP;

    private boolean showDone;
    private String mOrderType;
    private String mRFID;
    private String mRI;

    private List<RecordNCBo> mList_recordNC;//记录不良
    private boolean isRecordLabu;//是否已记录拉布数据

    private boolean isSearchCard = true;//是否按卡号搜索

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_cut, null);
        return mView;
    }

    public void playVideo() {
        if (mTailorInfo.getOPER_INFOR() == null || mTailorInfo.getOPER_INFOR().size() == 0) {
            toast("当前站位无工序");
            return;
        }
        int currentItem = mVP_process.getCurrentItem();
        String videoUrl = mTailorInfo.getOPER_INFOR().get(currentItem).getVIDEO_URL();
        SystemUtils.playVideo(mContext, videoUrl);
    }

    private CardState mCardState;

    private void checkCardState() {
        if (mCardState == null) {
            mCardState = new CardState();
        }
    }

    public void searchOrderByOrderNum(String type, String orderNum) {
        checkCardState();
        if (!orderNum.equals(mCardState.getCardNum())) {
            mCardState.setCardNum(orderNum);
            mCardState.setStarted(false);
            mCardState.setCompleted(false);
        }
        isSearchCard = false;
        showLoading();
        mRFID = orderNum;
        HttpHelper.viewCutPadInfoByShopOrder(type, orderNum, this);
    }

    public void searchOrder(String orderType, String orderNum, String resourceBo, String RI) {
        checkCardState();
        if (!orderNum.equals(mCardState.getCardNum())) {
            mCardState.setCardNum(orderNum);
            mCardState.setStarted(false);
            mCardState.setCompleted(false);
        }
        isSearchCard = true;
        showLoading();
        mOrderType = orderType;
        mRFID = orderNum;
        mRI = RI;
        HttpHelper.viewCutPadInfo(orderType, orderNum, resourceBo, RI, this);
    }

    protected void initView() {
        super.initView();
        mVP_process = mView.findViewById(R.id.vp_main_processDesc);
        mVP_matInfo = mView.findViewById(R.id.vp_main_matInfo);

        mLayout_material1 = mView.findViewById(R.id.layout_material1);
        mLayout_material2 = mView.findViewById(R.id.layout_material2);
        mLayout_sizeInfo = mView.findViewById(R.id.layout_sizeInfo);
        mLayout_planSize = mView.findViewById(R.id.layout_plantSize);
        mScrollView_planSize = mView.findViewById(R.id.scrollView_planSize);
        mLayout_processTab = mView.findViewById(R.id.layout_processTab);
        mLayout_matTab = mView.findViewById(R.id.layout_matTab);
        mTv_nextProcess = mView.findViewById(R.id.tv_nextProcess);
        mTv_qualityDesc = mView.findViewById(R.id.tv_qualityDescribe);
        mTv_special = mView.findViewById(R.id.tv_special);
        mTv_sizeCode = mView.findViewById(R.id.tv_sew_sizeCode);
        mTv_workCenter = mView.findViewById(R.id.tv_cut_workCenter);

        mLayout_TP = mView.findViewById(R.id.layout_cut_TP);
        mTv_TP = mView.findViewById(R.id.tv_cut_TPNum);

        mLv_process = mView.findViewById(R.id.lv_processList);
        mLv_process.setOnItemClickListener(new ProcessClickListener());

        mBtn_done = mView.findViewById(R.id.btn_done);
        if (showDone) {
            mBtn_done.setVisibility(View.VISIBLE);
        }
        mBtn_done.setOnClickListener(this);

        mView.findViewById(R.id.layout_processDescription).setOnClickListener(this);
        mView.findViewById(R.id.layout_special).setOnClickListener(this);

        mLayout_ncData = mView.findViewById(R.id.layout_cut_ncData);
        mTv_ncDesc = mView.findViewById(R.id.tv_cut_ncDesc);

        mLayout_mtmOrderNum = mView.findViewById(R.id.layout_cut_mtmOrderNum);
        mLayout_sfc = mView.findViewById(R.id.layout_cut_sfc);
        mTv_sfc = mView.findViewById(R.id.tv_cut_sfc);
    }

    @SuppressLint("SetTextI18n")
    public void refreshView() {
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
                final int finalI = i;
                mLayout_matTab.addView(TabViewUtil.getTabView(mContext, matInfoBean, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(ImageBrowserActivity.getIntent(mContext, mTailorInfo.getMAT_INFOR(), finalI));
                    }
                }));
            }
            TabViewUtil.refreshTabView(mContext,mLayout_matTab, 0);
        }

        VPAdapter mVPAdapter_matInfo = new VPAdapter<>(itemArray);
        mVP_matInfo.setAdapter(mVPAdapter_matInfo);
        mVP_matInfo.addOnPageChangeListener(new ViewPagerChangedListener(ViewPagerChangedListener.TYPE_MAT));

        //排料图数据
        mLayout_material1.removeAllViews();
        List<TailorInfoBo.LayoutInfoBean> layoutArray = mTailorInfo.getLAYOUT_INFOR();
        if (layoutArray != null) {
            for (int i = 0; i < layoutArray.size(); i++) {
                mLayout_material1.addView(getLayoutView(layoutArray.get(i), i));
                if ("P".equals(mOrderType)) {
                    mLayout_material1.addView(getLayoutBarCodeView(layoutArray.get(i)));
                }
            }
        }

        //排料图尺码信息
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

        int childCount1 = mLayout_planSize.getChildCount();
        for (int i = childCount1 - 1; i > 0; i--) {
            mLayout_planSize.removeViewAt(i);
        }
        List<TailorInfoBo.PlanSizeBean> planSizes = mTailorInfo.getPLAN_SIZES();
        if (planSizes == null || planSizes.size() == 0) {
            mScrollView_planSize.setVisibility(View.GONE);
        } else {
            mScrollView_planSize.setVisibility(View.VISIBLE);
            for (int i = 0; i < planSizes.size(); i++) {
                mLayout_planSize.addView(getSizeInfoView(planSizes.get(i)));
            }
        }

        //粘朴数据
        List<TailorInfoBo.StickyInfo> stickyInfo = mTailorInfo.getSTICKY_INFOR();
        if (stickyInfo == null) {
            stickyInfo = new ArrayList<>();
        }

        //工序数据
        List<TailorInfoBo.OPERINFORBean> list = mTailorInfo.getOPER_INFOR();
        VPAdapter mVPAdapter_process = new VPAdapter<>(list);
        mVP_process.setAdapter(mVPAdapter_process);
        mVP_process.addOnPageChangeListener(new ViewPagerChangedListener(ViewPagerChangedListener.TYPE_PROCESS));

        mLv_process.setAdapter(new CommonAdapter<TailorInfoBo.OPERINFORBean>(mContext, list, R.layout.item_textview) {
            @Override
            public void convert(ViewHolder holder, TailorInfoBo.OPERINFORBean item, int position) {
                TextView textView = holder.getView(R.id.textView);
                textView.setText(item.getDESCRIPTION());
            }
        });
        mLv_process.setItemChecked(0, true);

        mLayout_processTab.removeAllViews();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                TailorInfoBo.OPERINFORBean operaInfoBean = list.get(i);
                String sopUrl = operaInfoBean.getSOP_URL();
                if (!isEmpty(sopUrl)) {
                    String[] split = sopUrl.split(",");
                    for (String url : split) {
                        if (url.startsWith("http")) {
                            TailorInfoBo.StickyInfo stickyInfo1 = new TailorInfoBo.StickyInfo();
                            stickyInfo1.setPICTURE_URL(url);
                            stickyInfo1.setIDENTITY_INFO(operaInfoBean.getOPERATION());
                            stickyInfo.add(stickyInfo1);
                        }
                    }
                }

                final int finalI = i;
                mLayout_processTab.addView(TabViewUtil.getTabView(mContext, operaInfoBean, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mVP_process.setCurrentItem(finalI);
                    }
                }));
            }
            if (list.size() != 0) {
                refreshProcessView(0);
            }
        }
        TailorInfoBo.NextOrderInfo nextOperInfo = mTailorInfo.getNEXT_OPER_INFOR();
        if (nextOperInfo != null) {
            mTv_nextProcess.setText(nextOperInfo.getOPER_DESC());
        }

        mLayout_material2.removeAllViews();
        for (int i = 0; i < stickyInfo.size(); i++) {
            mLayout_material2.addView(getMaterialsView(stickyInfo.get(i), i));
        }

        TextView tv_orderNum = mView.findViewById(R.id.tv_cut_orderNum);
        TextView tv_MTMOrderNum = mView.findViewById(R.id.tv_cut_MTMOrderNum);
        tv_MTMOrderNum.setOnClickListener(this);
        TextView tv_style = mView.findViewById(R.id.tv_sew_style);
        TextView tv_processLot = mView.findViewById(R.id.tv_sew_processLot);
        TextView tv_qty = mView.findViewById(R.id.tv_sew_qty);
        TextView tv_matDesc = mView.findViewById(R.id.tv_cut_matDesc);
        if (!isEmpty(mRI)) {
            tv_processLot.setText(mRI.replace("ProcessLotBO:", ""));
        }

        String sfcBo = mTailorInfo.getSFC_BO();
        if (!isEmpty(sfcBo)) {
            mLayout_sfc.setVisibility(View.VISIBLE);
            mTv_sfc.setText(sfcBo.split(",")[1]);
        } else {
            mLayout_sfc.setVisibility(View.GONE);
        }

        TailorInfoBo.SHOPORDERINFORBean orderInfo = mTailorInfo.getSHOP_ORDER_INFOR();
        mTv_sizeCode.setText(orderInfo.getSIZE_CODE());
        tv_orderNum.setText(orderInfo.getSHOP_ORDER());
        String salesOrder = orderInfo.getSALES_ORDER();
        if (isEmpty(salesOrder)) {
            mLayout_mtmOrderNum.setVisibility(View.GONE);
        } else {
            mLayout_mtmOrderNum.setVisibility(View.VISIBLE);
            tv_MTMOrderNum.setText(salesOrder);
        }

        String tpOrder = orderInfo.getTP_ORDER();
        if (isEmpty(tpOrder)) {
            mLayout_TP.setVisibility(View.GONE);
        } else {
            mLayout_TP.setVisibility(View.VISIBLE);
            mTv_TP.setText(tpOrder);
        }
        tv_matDesc.setText(orderInfo.getITEM_DESC());
        tv_style.setText(orderInfo.getITEM());
        tv_qty.setText(orderInfo.getORDER_QTY() + "/件");
        mTv_special.setText(orderInfo.getSO_REMARK());

        if (!isSearchCard && "P".equals(mOrderType)) {
            mView.findViewById(R.id.layout_cut_layers).setVisibility(View.VISIBLE);
            TextView tv_layers = mView.findViewById(R.id.tv_sew_layers);
            tv_layers.setText(orderInfo.getLAYERS() + "");
        } else {
            mView.findViewById(R.id.layout_cut_layers).setVisibility(View.GONE);
        }

        //配片不良
        NcDataBo nc_data = mTailorInfo.getNC_DATA();
        if (nc_data == null) {
            mLayout_ncData.setVisibility(View.GONE);
        } else {
            mLayout_ncData.setVisibility(View.VISIBLE);
            mTv_ncDesc.setText(nc_data.getNC_DESC());
        }

        mTv_workCenter.setText(orderInfo.getWORK_CENTER_DESC());
    }

    /**
     * 刷新工序相关的界面，包括工序图、工艺说明、品质要求、
     */
    private void refreshProcessView(int position) {
        TailorInfoBo.OPERINFORBean item = mTailorInfo.getOPER_INFOR().get(position);

//        TextView tv_craftDesc = (TextView) mView.findViewById(R.id.tv_craftDescribe);

//        String craftDesc = item.getOPERATION_INSTRUCTION();
//        if (!isEmpty(craftDesc))
//            tv_craftDesc.setText(craftDesc.replace("#line#", "\n"));
        String qualityDesc = item.getQUALITY_REQUIREMENT();
        if (!isEmpty(qualityDesc)) {
            mTv_qualityDesc.setText(qualityDesc.replace("#line#", "\n"));
        } else {
            mTv_qualityDesc.setText(null);
        }

        TabViewUtil.refreshTabView(mContext,mLayout_processTab, position);
    }

    public void markSecondClass() {
        if (mTailorInfo == null) {
            showAlert("请先获取数据");
            return;
        }
        new AlertDialog.Builder(mContext).setMessage("确定标记为二等品吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoading();
                        String sfc_bo = mTailorInfo.getSFC_BO();
                        HttpHelper.markSecondClass(sfc_bo, CutFragment.this);
                    }
                })
                .create()
                .show();
    }

    /**
     * 分包制卡
     */
    public void splitCard(String cardNum) {
        if (mTailorInfo == null) {
            showErrorDialog("请获取主数据后再执行操作");
            return;
        }
        new SplitCardDialog(mContext, cardNum, mRI, mTailorInfo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
                searchOrder(mOrderType, mRFID, resource.getRESOURCE_BO(), mRI);
            }
        }).show();
    }

    public void startWork() {
        if (mTailorInfo == null) {
            toast("请先获取订单数据");
            return;
        }
        showLoading();
        if ("S".equals(mOrderType)) {
            HttpHelper.startCustomWork(getStartAndCompleteParams(), this);
        } else {
            HttpHelper.startBatchWork(getStartAndCompleteParams(), this);
        }
    }

    public void completeWork() {
        if (mTailorInfo == null) {
            toast("请先开始作业");
            return;
        }
        if ("P".equals(mOrderType)) {
            List<TailorInfoBo.OPERINFORBean> operInfo = mTailorInfo.getOPER_INFOR();
            if (operInfo != null) {
                //遍历当前工序，如果有拉布，并且没有记录拉布数据，则必须记录拉布数据才可以完工
                for (TailorInfoBo.OPERINFORBean item : operInfo) {
                    if (!isEmpty(item.getDESCRIPTION()) && item.getDESCRIPTION().contains("拉布")) {
                        if (!isRecordLabu) {
                            showErrorDialog("请记录拉布数据");
                            return;
                        }
                        break;
                    }
                }
            }
        }
        showLoading();
        if ("S".equals(mOrderType)) {
            HttpHelper.completeCustomWork(getStartAndCompleteParams(), this);
        } else {
            HttpHelper.completeBatchWork(getStartAndCompleteParams(), this);
        }
    }

    /**
     * 选择粘朴方式
     */
    public void sticky() {
        if (mTailorInfo == null) {
            toast("请先获取订单数据");
            return;
        }
        TailorInfoBo.SHOPORDERINFORBean orderInfor = mTailorInfo.getSHOP_ORDER_INFOR();
        List<String> process_lot_bo = orderInfor.getPROCESS_LOT_BO();
        if (process_lot_bo != null && process_lot_bo.size() != 0) {
            new StickyDialog(mContext, process_lot_bo.get(0)).show();
        } else {
            toast("批次号为空");
        }
    }

    /**
     * 自动拣选
     */
    public void autoPicking() {
        if (mTailorInfo == null) {
            toast("请先获取订单数据");
            return;
        }
        TailorInfoBo.SHOPORDERINFORBean orderInfor = mTailorInfo.getSHOP_ORDER_INFOR();
        String shopOrder = orderInfor.getSHOP_ORDER();
        String itemCode = orderInfor.getITEM();
        new AutoPickDialog(mContext, shopOrder, itemCode, "20").show();
    }

    private String USER_ID;//避免员工点开始与完成时的用户不一致

    private StartWorkParamsBo getStartAndCompleteParams() {
        TailorInfoBo.SHOPORDERINFORBean orderInfo = mTailorInfo.getSHOP_ORDER_INFOR();
        StartWorkParamsBo params = new StartWorkParamsBo();
        params.setRFID(mRFID);
        if (isEmpty(USER_ID)) {
            List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
            if (positionUsers != null && positionUsers.size() != 0) {
                USER_ID = positionUsers.get(0).getUSER();
            }
        }
        params.setUSER_ID(USER_ID);
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo != null) {
            params.setPOSITION(contextInfo.getPOSITION());
            params.setLINE_CATEGORY(contextInfo.getLINE_CATEGORY());
        }
        params.setPAD_ID(HttpHelper.getPadIp());
        params.setPROCESS_LOTS(orderInfo.getPROCESS_LOT_BO());
        params.setSHOP_ORDER(orderInfo.getSHOP_ORDER());
        params.setSHOP_ORDER_BO(orderInfo.getSHOP_ORDER_BO());
        params.setLAYERS(orderInfo.getLAYERS());
        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        if (resource != null)
            params.setRESOURCE_BO(resource.getRESOURCE_BO());
        params.setORDER_QTY(orderInfo.getORDER_QTY());
        List<String> opList = new ArrayList<>();
        if (mTailorInfo.getOPER_INFOR() != null) {
            for (TailorInfoBo.OPERINFORBean item : mTailorInfo.getOPER_INFOR()) {
                opList.add(item.getOPERATION_BO());
            }
        }
        params.setOPERATIONS(opList);
        return params;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_done) {
            completeWork();
        } else if (v.getId() == R.id.layout_processDescription) {
            String content = mTv_qualityDesc.getText().toString();
            if (!isEmpty(content))
                MyAlertDialog.showAlert(mContext, content);
        } else if (v.getId() == R.id.layout_special) {
            String content = mTv_special.getText().toString();
            if (!isEmpty(content))
                MyAlertDialog.showAlert(mContext, content);
        } else if (v.getId() == R.id.tv_cut_MTMOrderNum) {
            String url = PadApplication.MTM_URL + mTailorInfo.getSHOP_ORDER_INFOR().getSALES_ORDER();
            startActivity(WebActivity.getIntent(mContext, url));
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
     */
    private View getMaterialsView(final Object data, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_material = view.findViewById(R.id.iv_materials);
        TextView tv_material = view.findViewById(R.id.tv_matNum);
        if (data instanceof TailorInfoBo.LayoutInfoBean) {
            TailorInfoBo.LayoutInfoBean item = (TailorInfoBo.LayoutInfoBean) data;
            String picture_url = item.getPICTURE_URL();
            if (!isEmpty(picture_url)) {
                Picasso.with(mContext).load(picture_url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(iv_material);
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
            }
        } else if (data instanceof TailorInfoBo.StickyInfo) {
            TailorInfoBo.StickyInfo item = (TailorInfoBo.StickyInfo) data;
            String picture_url = item.getPICTURE_URL();
            if (!isEmpty(picture_url)) {
                Picasso.with(mContext).load(picture_url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(iv_material);
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
        }
        return view;
    }

    /**
     * 获取排料图
     */
    private View getLayoutView(final TailorInfoBo.LayoutInfoBean item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_material = view.findViewById(R.id.iv_materials);
        TextView tv_material = view.findViewById(R.id.tv_matNum);
        String picture_url = item.getPICTURE_URL();
        if (!isEmpty(picture_url))
            Picasso.with(mContext).load(picture_url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(iv_material);
        iv_material.setTag(position);
        tv_material.setText(item.getLAYOUT());

        iv_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<String> urls = new ArrayList<>();
//                List<TailorInfoBo.LayoutInfoBean> list = mTailorInfo.getLAYOUT_INFOR();
//                for (TailorInfoBo.LayoutInfoBean item : list) {
//                    urls.add(item.getPICTURE_URL());
//                }
                startActivity(ImageBrowserActivity.getIntent(mContext, mTailorInfo.getLAYOUT_INFOR(), (Integer) v.getTag()));
            }
        });
        return view;
    }

    /**
     * 获取排料图一维码
     */
    private View getLayoutBarCodeView(final TailorInfoBo.LayoutInfoBean item) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_imageview, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        try {
            Bitmap barCode = EncodingHandler.createBarCode(item.getLAYOUT(), 400, 200);
            imageView.setImageBitmap(barCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAlertDialog.showBarCode(mContext, item.getLAYOUT());
            }
        });
        return view;
    }

    private <T> View getSizeInfoView(T t) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sizeinfo, null);
        TextView tv_yardage = view.findViewById(R.id.tv_item_yardage);
        TextView tv_count = view.findViewById(R.id.tv_item_count);
        if (t instanceof TailorInfoBo.CUTSIZESBean) {
            TailorInfoBo.CUTSIZESBean data = (TailorInfoBo.CUTSIZESBean) t;
            tv_yardage.setText(data.getSIZE_CODE());
            int layers = mTailorInfo.getSHOP_ORDER_INFOR().getLAYERS();
            tv_count.setText(String.format("%s", data.getSIZE_AMOUNT() * layers));
        } else if (t instanceof TailorInfoBo.PlanSizeBean) {
            TailorInfoBo.PlanSizeBean data = (TailorInfoBo.PlanSizeBean) t;
            tv_yardage.setText(data.getSIZE_CODE());
            tv_count.setText(data.getSIZE_AMOUNT());
        }
        return view;
    }

    /**
     * 显示纸样图案
     */
    public void showPattern() {
        if (mTailorInfo == null) {
            showErrorDialog("请先获取订单数据");
            return;
        }
        new PatternDialog(mContext, mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER()).show();
    }

    public void recordNC() {
        if (mTailorInfo == null) {
            showErrorDialog("请先获取订单数据");
            return;
        }
        startActivityForResult(RecordCutNCActivity.getIntent(mContext, mTailorInfo, mList_recordNC), REQUEST_RECORD_NC);
    }

    /**
     * 记录拉布数据
     */
    public void showRecordLabuDialog() {
        if (mTailorInfo == null) {
            toast("请先获取订单数据");
            return;
        }
        startActivityForResult(RecordLabuActivity.getIntent(mContext, mTailorInfo), REQUEST_RECORD_LABU);
    }

    /**
     * 退补料
     */
    public void returnAndFeedingMat(boolean isReturn) {
        if (mTailorInfo == null) {
            toast("请先获取订单数据");
            return;
        }
        List<TailorInfoBo.MatInfoBean> itemArray = mTailorInfo.getMAT_INFOR();
        List<ReturnMaterialInfoBo.MaterialInfoBo> materialList = new ArrayList<>();
        for (TailorInfoBo.MatInfoBean item : itemArray) {
            ReturnMaterialInfoBo.MaterialInfoBo material = new ReturnMaterialInfoBo.MaterialInfoBo();
            material.setPicUrl(item.getMAT_URL());
            material.setITEM(item.getMAT_NO());
            material.setUNIT_LABEL(item.getUNIT_LABEL());
            materialList.add(material);
        }
        ReturnMaterialInfoBo materialInfoBo = new ReturnMaterialInfoBo();
        materialInfoBo.setOrderNum(mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER());
        materialInfoBo.setMaterialInfoList(materialList);
        if (isReturn) {
            new CutReturnMatDialog(mContext, CutReturnMatDialog.TYPE_RETURN, materialInfoBo).show();
        } else {
            new CutReturnMatDialog(mContext, CutReturnMatDialog.TYPE_ADD, materialInfoBo).show();
        }
    }

    public void showCompleteButton() {
        showDone = true;
        if (mBtn_done != null) {
            mBtn_done.setVisibility(View.VISIBLE);
        }
    }

    private CutRecordQtyDialog mRecordQtyDialog;

    public boolean inputRecordUser(String cardNum) {
        if (mRecordQtyDialog != null) {
            boolean showing = mRecordQtyDialog.isShowing();
            if (showing) {
                mRecordQtyDialog.getUserInfo(cardNum);
            }
            return showing;
        }
        return false;
    }

    /**
     * 裁剪计件
     */
    public void recordQty() {
        if (mTailorInfo == null) {
            showErrorDialog("请先获取订单数据");
            return;
        }
        List<TailorInfoBo.OPERINFORBean> list = mTailorInfo.getOPER_INFOR();
        mRecordQtyDialog = new CutRecordQtyDialog(mContext, mRFID, mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER(), mTailorInfo.getPROCESS_LOT_INFO().getPROCESS_LOT_QTY(), list);
        mRecordQtyDialog.show();
    }

    private class ViewPagerChangedListener implements ViewPager.OnPageChangeListener {

        static final int TYPE_MAT = 0;
        static final int TYPE_PROCESS = 1;
        int TYPE;

        ViewPagerChangedListener(int TYPE) {
            this.TYPE = TYPE;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (TYPE == TYPE_MAT) {
                TabViewUtil.refreshTabView(mContext,mLayout_matTab, position);
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

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_textview, null);
            final TextView textView = view.findViewById(R.id.textView);
            textView.setGravity(Gravity.LEFT);
            Object object = data.get(position);
            if (object instanceof TailorInfoBo.MatInfoBean) {
                TailorInfoBo.MatInfoBean matInfo = (TailorInfoBo.MatInfoBean) object;
                textView.setText(String.format("大类、%s\n小类、%s", matInfo.getGRAND_CATEGORY_DESC(), matInfo.getMID_CATEGORY_DESC()));
            } else if (object instanceof TailorInfoBo.OPERINFORBean) {
                TailorInfoBo.OPERINFORBean operInfo = (TailorInfoBo.OPERINFORBean) object;
                String quality = operInfo.getOPERATION_INSTRUCTION();
                if (!isEmpty(quality))
                    textView.setText(quality.replace("#line#", "\n"));
                else
                    textView.setText(null);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = textView.getText().toString();
                    if (!isEmpty(content))
                        MyAlertDialog.showAlert(mContext, content);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View item = (View) object;
            container.removeView(item);

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
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
            PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
            if ("S".equals(mOrderType)) {
                json.put("RESOURCE_BO", resource.getRESOURCE_BO());
                json.put("OPERATION_BO", operInfo.get(0).getOPERATION_BO());
                HttpHelper.signoffByShopOrder(json, this);
            } else {
                json.put("PROCESS_LOTS", shopOrderInfo.getPROCESS_LOT_BO());
                json.put("RESOURCE_BO", resource.getRESOURCE_BO());
                json.put("OPERATION_BO", operInfo.get(0).getOPERATION_BO());
                HttpHelper.signoffByProcessLot(json, this);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_RECORD_NC) {
                mList_recordNC = (List<RecordNCBo>) data.getSerializableExtra("badList");
            } else if (requestCode == REQUEST_RECORD_LABU) {
                isRecordLabu = true;
            }
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (url.equals(HttpHelper.findProcessWithPadId_url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                if (result != null) {
                    mList_padProcess = JSON.parseArray(result.getJSONArray("OPER_INFOR").toString(), TailorInfoBo.OPERINFORBean.class);
                }
            } else if (url.equals(HttpHelper.viewCutPadInfo_url) || url.equals(HttpHelper.viewCutPadInforByShopOrder)) {
                JSONObject result1 = resultJSON.getJSONObject("result");
                if (result1 != null) {
                    mTailorInfo = JSON.parseObject(result1.toString(), TailorInfoBo.class);
                    if (mTailorInfo.getOPER_INFOR() == null || mTailorInfo.getOPER_INFOR().size() == 0) {
                        mBtn_done.setEnabled(false);
                        ((MainActivity) getActivity()).setButtonState(R.id.btn_start, false);
                        mTailorInfo.setOPER_INFOR(mList_padProcess);
                    } else {
                        if (mCardState.isStarted) {
                            ((MainActivity) getActivity()).setButtonState(R.id.btn_start, false);
                        } else {
                            ((MainActivity) getActivity()).setButtonState(R.id.btn_start, true);
                        }

                        if (mCardState.isCompleted) {
                            mBtn_done.setEnabled(false);
                        } else {
                            mBtn_done.setEnabled(true);
                        }
                    }
                    if (isSearchCard) {
                        mTailorInfo.setOrderType(mOrderType);
                        mTailorInfo.setRFID(mRFID);
                    }
                    SpUtil.saveSalesOrder(mTailorInfo.getSHOP_ORDER_INFOR().getSALES_ORDER());
                    SpUtil.save(SpUtil.KEY_SHOPORDER, mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER());
                    refreshView();

                    //更新订单后需要清空之前的不良记录
                    mList_recordNC = new ArrayList<>();
                    isRecordLabu = false;
                }
            } else if (url.equals(HttpHelper.startBatchWork_url) || url.equals(HttpHelper.startCustomWork_url)) {
//                    mBtn_done.setText("完成");
//                    mBtn_done.setBackgroundResource(R.drawable.btn_primary);
                toast("开始作业");
                mCardState.setStarted(true);
                ((MainActivity) getActivity()).setButtonState(R.id.btn_start, false);
            } else if (url.equals(HttpHelper.completeBatchWork_url) || url.equals(HttpHelper.completeCustomWork_url)) {
//                    mBtn_done.setText("开始");
//                    mBtn_done.setBackgroundResource(R.drawable.btn_green);
                toast("工序已完成");
                USER_ID = null;
                mCardState.setCompleted(true);
                mBtn_done.setEnabled(false);
            } else if (url.equals(HttpHelper.signoffByShopOrder) || url.equals(HttpHelper.signoffByProcessLot)) {
                toast("注销在制品成功，可重新开始");
            } else if (HttpHelper.markSecondClass.equals(url)) {
                toast("标记二等品成功");
            }
        }
    }

    private class CardState {
        private String cardNum;
        private boolean isStarted;
        private boolean isCompleted;

        public String getCardNum() {
            return cardNum;
        }

        public void setCardNum(String cardNum) {
            this.cardNum = cardNum;
        }

        public void setStarted(boolean started) {
            isStarted = started;
        }

        public void setCompleted(boolean completed) {
            isCompleted = completed;
        }
    }
}
