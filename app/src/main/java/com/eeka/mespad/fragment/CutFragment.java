package com.eeka.mespad.fragment;

import android.app.Activity;
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
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.activity.MainActivity;
import com.eeka.mespad.activity.RecordCutNCActivity;
import com.eeka.mespad.activity.RecordLabuActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.ContextInfoBo;
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
    private TailorInfoBo mTailorInfo;//主数据

    private ListView mLv_process;

    private LinearLayout mLayout_processTab;//工序页签
    private LinearLayout mLayout_matTab;//物料图页签
    private TextView mTv_nextProcess;
    private TextView mTv_qualityDesc;
    private TextView mTv_special;
    private Button mBtn_done;

    private boolean showDone;
    private String mOrderType;
    private String mRFID;
    private String mRI;

    private List<RecordNCBo> mList_recordNC;//记录不良
    private boolean isRecordLabu;//是否已记录拉布数据

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
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

    public void searchOrder(String orderType, String orderNum, String resourceBo, String RI) {
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
        mLayout_processTab = mView.findViewById(R.id.layout_processTab);
        mLayout_matTab = mView.findViewById(R.id.layout_matTab);
        mTv_nextProcess = mView.findViewById(R.id.tv_nextProcess);
        mTv_qualityDesc = mView.findViewById(R.id.tv_qualityDescribe);
        mTv_special = mView.findViewById(R.id.tv_special);

        mLv_process = mView.findViewById(R.id.lv_processList);
        mLv_process.setOnItemClickListener(new ProcessClickListener());

        mBtn_done = mView.findViewById(R.id.btn_done);
        if (showDone) {
            mBtn_done.setVisibility(View.VISIBLE);
        }
        mBtn_done.setOnClickListener(this);

        mView.findViewById(R.id.layout_processDescription).setOnClickListener(this);
        mView.findViewById(R.id.layout_special).setOnClickListener(this);
    }

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
            TabViewUtil.refreshTabView(mLayout_matTab, 0);
        }

        VPAdapter mVPAdapter_matInfo = new VPAdapter<>(itemArray);
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
                if ("P".equals(mOrderType)) {
                    mLayout_material1.addView(getLayoutBarCodeView(layoutArray.get(i)));
                }
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

        //工序数据
        mLayout_processTab.removeAllViews();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                final int finalI = i;

                mLayout_processTab.addView(TabViewUtil.getTabView(mContext, mTailorInfo.getOPER_INFOR().get(i), new View.OnClickListener() {
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

        TextView tv_orderNum = mView.findViewById(R.id.tv_cut_orderNum);
        TextView tv_MTMOrderNum = mView.findViewById(R.id.tv_cut_MTMOrderNum);
        TextView tv_style = mView.findViewById(R.id.tv_sew_style);
        TextView tv_processLot = mView.findViewById(R.id.tv_sew_processLot);
        TextView tv_qty = mView.findViewById(R.id.tv_sew_qty);
        TextView tv_matDesc = mView.findViewById(R.id.tv_cut_matDesc);
        TailorInfoBo.SHOPORDERINFORBean orderInfo = mTailorInfo.getSHOP_ORDER_INFOR();
        tv_orderNum.setText(orderInfo.getSHOP_ORDER());
        tv_MTMOrderNum.setText(orderInfo.getSALES_ORDER());
        tv_matDesc.setText(orderInfo.getITEM_DESC());
        tv_style.setText(orderInfo.getITEM());
        tv_processLot.setText(mRI);
        tv_qty.setText(orderInfo.getORDER_QTY() + "/件");
        mTv_special.setText(orderInfo.getSO_REMARK());

        if ("P".equals(mOrderType)) {
            mView.findViewById(R.id.layout_cut_layers).setVisibility(View.VISIBLE);
            TextView tv_layers = mView.findViewById(R.id.tv_sew_layers);
            tv_layers.setText(orderInfo.getLAYERS() + "");
        } else {
            mView.findViewById(R.id.layout_cut_layers).setVisibility(View.GONE);
        }

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

        TabViewUtil.refreshTabView(mLayout_processTab, position);
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

    private StartWorkParamsBo getStartAndCompleteParams() {
        TailorInfoBo.SHOPORDERINFORBean orderInfo = mTailorInfo.getSHOP_ORDER_INFOR();
        StartWorkParamsBo params = new StartWorkParamsBo();
        params.setRFID(mRFID);
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        if (positionUsers != null && positionUsers.size() != 0) {
            params.setUSER_ID(positionUsers.get(0).getUSER());
        }
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
        } else if (v.getId() == R.id.layout_processDescription) {
            String content = mTv_qualityDesc.getText().toString();
            if (!isEmpty(content))
                MyAlertDialog.showAlert(mContext, content);
        } else if (v.getId() == R.id.layout_special) {
            String content = mTv_special.getText().toString();
            if (!isEmpty(content))
                MyAlertDialog.showAlert(mContext, content);
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
            Picasso.with(mContext).load(item.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(iv_material);
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
            Picasso.with(mContext).load(item.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(iv_material);
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
     */
    private View getLayoutView(final TailorInfoBo.LayoutInfoBean item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_material = view.findViewById(R.id.iv_materials);
        TextView tv_material = view.findViewById(R.id.tv_matNum);
        Picasso.with(mContext).load(item.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(iv_material);
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

    private View getSizeInfoView(TailorInfoBo.CUTSIZESBean sizeInfo) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sizeinfo, null);
        TextView tv_yardage = view.findViewById(R.id.tv_item_yardage);
        TextView tv_count = view.findViewById(R.id.tv_item_count);
        tv_yardage.setText(sizeInfo.getSIZE_CODE());
        int layers = mTailorInfo.getSHOP_ORDER_INFOR().getLAYERS();
        tv_count.setText((sizeInfo.getSIZE_AMOUNT() * layers) + "");
        return view;
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
        mRecordQtyDialog = new CutRecordQtyDialog(mContext, mRFID, mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER(), list);
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
                TabViewUtil.refreshTabView(mLayout_matTab, position);
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
                textView.setText(String.format("1、%s\n2、%s", matInfo.getGRAND_CATEGORY_DESC(), matInfo.getMID_CATEGORY_DESC()));
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
            } else if (url.equals(HttpHelper.viewCutPadInfo_url)) {
                JSONObject result1 = resultJSON.getJSONObject("result");
                if (result1 != null) {
                    mTailorInfo = JSON.parseObject(result1.toString(), TailorInfoBo.class);
                    if (mTailorInfo.getOPER_INFOR() == null || mTailorInfo.getOPER_INFOR().size() == 0) {
                        mBtn_done.setEnabled(false);
                        ((MainActivity) getActivity()).setButtonState(R.id.btn_start, false);
                        mTailorInfo.setOPER_INFOR(mList_padProcess);
                    } else {
                        mBtn_done.setEnabled(true);
                        ((MainActivity) getActivity()).setButtonState(R.id.btn_start, true);
                    }
                    mTailorInfo.setOrderType(mOrderType);
                    mTailorInfo.setRFID(mRFID);
                    refreshView();

                    //更新订单后需要清空之前的记录
                    mList_recordNC = new ArrayList<>();
                    isRecordLabu = false;
                }
            } else if (url.equals(HttpHelper.startBatchWork_url) || url.equals(HttpHelper.startCustomWork_url)) {
//                    mBtn_done.setText("完成");
//                    mBtn_done.setBackgroundResource(R.drawable.btn_primary);
                toast("开始作业");
                ((MainActivity) getActivity()).setButtonState(R.id.btn_start, false);
            } else if (url.equals(HttpHelper.completeBatchWork_url) || url.equals(HttpHelper.completeCustomWork_url)) {
//                    mBtn_done.setText("开始");
//                    mBtn_done.setBackgroundResource(R.drawable.btn_green);
                toast("工序已完成");
                mBtn_done.setEnabled(false);
            } else if (url.equals(HttpHelper.signoffByShopOrder) || url.equals(HttpHelper.signoffByProcessLot)) {
                toast("注销在制品成功，可重新开始");
            }
        }
    }
}
