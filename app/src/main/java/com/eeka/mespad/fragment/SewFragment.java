package com.eeka.mespad.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.EmbroiderActivity;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.activity.MainActivity;
import com.eeka.mespad.activity.OutlinePicActivity;
import com.eeka.mespad.activity.WebActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.CommonVPAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.INARequestBo;
import com.eeka.mespad.bo.NcDataBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.SewAttr;
import com.eeka.mespad.bo.SewDataBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.http.WebServiceUtils;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TabViewUtil;
import com.eeka.mespad.utils.TopicUtil;
import com.eeka.mespad.view.dialog.CreateCardDialog;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.LineColorDialog;
import com.eeka.mespad.view.dialog.MyAlertDialog;
import com.eeka.mespad.view.dialog.NCDetailDialog;
import com.eeka.mespad.view.dialog.OfflineDialog;
import com.eeka.mespad.view.dialog.PocketSizeDialog;
import com.eeka.mespad.view.dialog.ProductOnOffDialog;
import com.eeka.mespad.view.dialog.QCSizeDialog;
import com.eeka.mespad.view.dialog.ReplaceRFIDDialog;
import com.eeka.mespad.view.dialog.ReworkListDialog;
import com.eeka.mespad.view.dialog.SewReturnMatDialog;
import com.eeka.mespad.view.dialog.SortForClothTagDialog;
import com.eeka.mespad.view.dialog.SortingDialog;
import com.eeka.mespad.view.dialog.YaotouSizeDialog;
import com.eeka.mespad.view.dialog.YiLingDialog;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 缝制界面
 * Created by Lenovo on 2017/7/26.
 */
@SuppressLint("InflateParams")
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
    private TextView mTv_secondClass;

    private LinearLayout mLayout_ncData;
    private TextView mTv_ncData;

    private String mRFID;
    private SewDataBo mSewData;
    private List<SewAttr> mList_lastOperation;

    private MainActivity mActivity;

    private String mTopic;
    private WebServiceCallback mWebServiceCallback;

    private SortingDialog mSortingDialog;
    private OfflineDialog mOfflineDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_sew, null);
        Bundle bundle = getArguments();
        assert bundle != null;
        mTopic = bundle.getString("topic");
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = (MainActivity) getActivity();
        mWebServiceCallback = new WebServiceCallback();
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
        mTv_secondClass = mView.findViewById(R.id.tv_sew_secondClass);

        mLayout_processTab = mView.findViewById(R.id.layout_sew_processList);
        mLayout_matInfo = mView.findViewById(R.id.layout_sew_matInfo);
        mVP_sop = mView.findViewById(R.id.vp_sew_sop);
        mVP_sop.addOnPageChangeListener(new ViewPagerChangedListener());
        mLv_curProcess = mView.findViewById(R.id.lv_sew_curProcess);
        mLv_nextProcess = mView.findViewById(R.id.lv_sew_nextProcess);

        mTv_MTMOrderNum.setOnClickListener(this);
        mView.findViewById(R.id.layout_sew_craftDesc).setOnClickListener(this);
        mView.findViewById(R.id.layout_sew_qualityReq).setOnClickListener(this);
        mView.findViewById(R.id.layout_sew_special).setOnClickListener(this);

        mLayout_ncData = mView.findViewById(R.id.layout_sew_ncData);
        mTv_ncData = mView.findViewById(R.id.tv_sew_ncData);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        String content = null;
        switch (v.getId()) {
            case R.id.layout_sew_craftDesc:
                content = mTv_craftDesc.getText().toString();
                break;
            case R.id.layout_sew_qualityReq:
                content = mTv_qualityReq.getText().toString();
                break;
            case R.id.layout_sew_special:
                content = mTv_special.getText().toString();
                break;
            case R.id.tv_sew_ncDetail:
                new NCDetailDialog(mSewData.getCurrentOpeationInfos(), mContext).show();
                break;
            case R.id.tv_sew_MTMOrderNum:
                String url = PadApplication.MTM_URL + mSewData.getSalesOrder();
                startActivity(WebActivity.getIntent(mContext, url));
                break;
        }
        if (!isEmpty(content))
            MyAlertDialog.showAlert(mContext, content);
    }

    private boolean isNewOrder;//是否新订单，用于判断按钮状态是否需要重置

    public void searchOrder(String rfid) {
        if (isAdded())
            showLoading();
        mRFID = rfid;
        HttpHelper.getSewData(rfid, this);
    }

    /**
     * 衣领号
     */
    public void showYiLingDialog() {
        if (mSewData == null) {
            showErrorDialog("请先获取衣架数据");
            return;
        }
        List<SewAttr> infos = mSewData.getCurrentOpeationInfos();
        if (infos != null && infos.size() != 0) {
            SewAttr attr = infos.get(mVP_sop.getCurrentItem());
            new YiLingDialog(mContext, mSewData.getShopOrder(), mSewData.getSfc(), mSewData.getSize(), attr.getName()).show();
        } else {
            showErrorDialog("当前衣架无工序，无法执行该操作");
        }
    }

    /**
     * 腰头尺寸
     */
    public void yaotouSize() {
        if (mSewData == null) {
            showErrorDialog("请先获取衣架数据");
            return;
        }
        List<SewAttr> infos = mSewData.getCurrentOpeationInfos();
        if (infos != null && infos.size() != 0) {
            SewAttr attr = infos.get(mVP_sop.getCurrentItem());
            new YaotouSizeDialog(mContext, mSewData.getShopOrder(), mSewData.getSfc(), mSewData.getSize(), attr.getName()).show();
        } else {
            showErrorDialog("当前衣架无工序，无法执行该操作");
        }
    }

    /**
     * 质检尺寸
     */
    public void qcSize() {
        if (mSewData == null) {
            showErrorDialog("请先获取衣架数据");
            return;
        }
        new QCSizeDialog(mContext, mSewData.getShopOrder(), mSewData.getSize()).show();
    }

    /**
     * 分拣
     */
    public void sorting() {
        if (mSewData == null) {
            showErrorDialog("请先获取衣架数据");
            return;
        }
        showSortingDialog();
    }

    /**
     * 线下分拣
     */
    public void offlineSort() {
        ErrorDialog.showConfirmAlert(mContext, "确定进行线下分拣操作吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpHelper.offlineSort(SewFragment.this);
            }
        });
    }

    private void showSortingDialog() {
        if (mSewData == null) {
            showErrorDialog("请先获取衣架数据");
            return;
        }
        if (mSortingDialog != null && mSortingDialog.isShowing()) {
            mSortingDialog.dismiss();
        }
        mSortingDialog = new SortingDialog(mContext, mTopic, mSewData.getSfc(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    jumpSorting = true;
                    manualComplete(mRFID);
                } else if (which == 1) {
                    mActivity.setButtonState(R.id.btn_offlineSort, false);
                    mActivity.setButtonState(R.id.btn_sorting, false);
                }
            }
        });
        mSortingDialog.show();
    }

    /**
     * 手工开始
     */
    public void manualStart(String rfid) {
        mRFID = rfid;
        if (isEmpty(mRFID)) {
            showErrorDialog("请输入条码获取数据");
            return;
        }
        INARequestBo bo = getINARequestParams();
        if (bo == null) {
            return;
        }
        showLoading();
        WebServiceUtils.inaIn(bo, mWebServiceCallback);
    }

    private void inaDoing() {
        INARequestBo bo = getINARequestParams();
        if (bo == null) {
            return;
        }
        isNewOrder = false;
        showLoading();
        WebServiceUtils.inaDoing(bo, mWebServiceCallback);
    }

    private boolean jumpSorting;

    /**
     * 手工完成
     */
    public void manualComplete(String rfid) {
        mRFID = rfid;
        if (isEmpty(mRFID)) {
            showErrorDialog("请输入条码获取数据");
            return;
        }
        if (!jumpSorting && mSewData != null) {
            List<SewAttr> opeationInfos = mSewData.getCurrentOpeationInfos();
            if (opeationInfos != null && opeationInfos.size() != 0) {
                for (SewAttr item : opeationInfos) {
                    if ("XQTBZ018".equals(item.getName())) {
                        showSortingDialog();
                        return;
                    }
                }
            }
        }
        INARequestBo bo = getINARequestParams();
        if (bo == null) {
            return;
        }
        showLoading();
        WebServiceUtils.inaOut(bo, mWebServiceCallback);
    }

    private class WebServiceCallback implements WebServiceUtils.HttpCallBack {

        @Override
        public void onSuccess(String method, JSONObject result) {
            dismissLoading();
            if (WebServiceUtils.INA_IN.equals(method)) {
                inaDoing();
            } else if (WebServiceUtils.INA_OUT.equals(method)) {
                jumpSorting = false;
                String nextLine = result.getString("nextLineId");
                String nextPosition = result.getString("nextStationId");
                String msg = "操作成功，下一个站位" + nextLine + "线" + nextPosition + "站位";
                ErrorDialog.showAlert(mContext, msg, ErrorDialog.TYPE.ALERT, null, true);
                mActivity.setButtonState(R.id.btn_manualStart, false);
                mActivity.setButtonState(R.id.btn_manualComplete, false);
                mActivity.setButtonState(R.id.btn_offlineSort, false);
                mActivity.setButtonState(R.id.btn_sorting, false);
            } else if (WebServiceUtils.INA_DOING.equals(method)) {
                mActivity.setButtonState(R.id.btn_manualStart, false);
                if (manualAlert) {
                    toast("开始手工作业");
                }
                manualAlert = true;
            }
        }

        @Override
        public void onFail(String errMsg) {
            dismissLoading();
            //webservice的接口报错时都会有推送，所以此处不需要显示
//            showErrorDialog(errMsg);
        }
    }

    private INARequestBo getINARequestParams() {
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null) {
            ErrorDialog.showAlert(mContext, "站位数据为空，请重启应用获取");
            return null;
        }
        INARequestBo bo = new INARequestBo();
        bo.setSite(SpUtil.getSite());
        bo.setLineId(contextInfo.getLINE_CATEGORY());
        bo.setStationId(contextInfo.getPOSITION());
        bo.setInTime(System.currentTimeMillis() + "");
        bo.setOutTime(System.currentTimeMillis() + "");
        bo.setDoTime(System.currentTimeMillis() + "");
        bo.setHangerId(mRFID);
        return bo;
    }

    private ProductOnOffDialog mProductOnOffDialog;
    private boolean manualAlert;//用于判断是否提示“手工作业开始”，因为成衣上架成功后需要调用手工开始的接口，此时不需要提示

    /**
     * 成衣上架
     */
    public void productOn() {
        if (isEmpty(mRFID)) {
            showErrorDialog("衣架号不能为空");
            return;
        }
        mProductOnOffDialog = new ProductOnOffDialog(mContext, mRFID, null, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualAlert = false;
                manualStart(mRFID);
            }
        });
        mProductOnOffDialog.show();
    }

    /**
     * 输入RFID号做成衣上架用
     */
    public boolean inputRFID(String rfid) {
        if (mProductOnOffDialog != null && mProductOnOffDialog.isShowing()) {
            mProductOnOffDialog.setWashLabel(rfid);
            return true;
        }
        return false;
    }

    /**
     * 通过吊牌走分拣系统
     */
    public void sortForClothTag() {
        new SortForClothTagDialog(mContext).show();
    }

    /**
     * 更换分拣衣架
     */
    public void replaceRFID() {
        new ReplaceRFIDDialog(mContext).show();
    }

    /**
     * 显示返修工序
     */
    public void showReworkList() {
        if (mSewData == null) {
            showErrorDialog("请先获取衣架数据");
            return;
        }
        new ReworkListDialog(mContext, mSewData.getSfc()).show();
    }

    /**
     * 成衣下架
     */
    public void productOff() {
        if (mSewData == null) {
            showErrorDialog("请先获取衣架数据");
            return;
        }
        new ProductOnOffDialog(mContext, mRFID, mSewData.getSfc(), true, null).show();
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
                HttpHelper.initNcForQA(mSewData.getSfc(), resource.getRESOURCE_BO(), "NC2QC", SewFragment.this);
            }
        });
    }

    public void gotoQA() {
        if (mSewData == null) {
            toast("请先获取缝制数据");
            return;
        }
        ErrorDialog.showConfirmAlert(mContext, "发现当前实物有不良，需要去质检站？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
                HttpHelper.initNcForQA(mSewData.getSfc(), resource.getRESOURCE_BO(), "NC2QA", SewFragment.this);
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
        List<SewAttr> infos = mSewData.getCurrentOpeationInfos();
        if (infos == null || infos.size() == 0) {
            toast("当前站位无工序");
            return;
        }
        SewAttr sewAttr = infos.get(currentItem);
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
        List<SewAttr> infos = mSewData.getCurrentOpeationInfos();
        if (infos != null && infos.size() != 0) {
            SewAttr attr = infos.get(mVP_sop.getCurrentItem());
            new PocketSizeDialog(mContext, mSewData.getShopOrder(), mSewData.getSfc(), mSewData.getSize(), attr.getName()).show();
        } else {
            showErrorDialog("当前衣架无工序，无法执行该操作");
        }
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
        if (isEmpty(mSewData.getLastLineCategory())) {
            mTv_lastPosition.setText("无");
        } else {
            mTv_lastPosition.setText(String.format("%s-%s", mSewData.getLastLineCategory(), mSewData.getLastPosition()));
        }
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
                e.printStackTrace();
            }
        }

        for (int i = 0; i < mLayout_matInfo.getChildCount(); i++) {
            View view = mLayout_matInfo.getChildAt(i);
            ImageView imageView = view.findViewById(R.id.iv_materials);
//            BitmapUtil.clearImgMemory(imageView);
            imageView.setImageBitmap(null);
        }
        mLayout_matInfo.removeAllViews();
        List<SewAttr> matInfos = mSewData.getColorItems();
        if (matInfos != null) {
            for (int i = 0; i < matInfos.size(); i++) {
                SewAttr matInfo = matInfos.get(i);
                mLayout_matInfo.addView(getMatView(matInfo, i));
            }
        }

        List<SewAttr> nextOperations = mSewData.getNextOperation();
        if (mNextProcessAdapter == null) {
            mNextProcessAdapter = new ProcessListAdapter(mContext, nextOperations, R.layout.item_textview);
            mLv_nextProcess.setAdapter(mNextProcessAdapter);
        } else {
            mNextProcessAdapter.notifyDataSetChanged(nextOperations);
        }

        mLayout_processTab.removeAllViews();
        final List<SewAttr> curOperation = mSewData.getCurrentOpeationInfos();
        if (mCurProcessAdapter == null) {
            mCurProcessAdapter = new ProcessListAdapter(mContext, curOperation, R.layout.item_textview);
            mLv_curProcess.setAdapter(mCurProcessAdapter);
        } else {
            mCurProcessAdapter.notifyDataSetChanged(curOperation);
        }
        if (curOperation != null) {
            ErrorDialog.dismiss();
            if (mList_lastOperation != null) {
                boolean needAlert = false;
                //比较当前款工序与上一款工序是否一致，不一致则提醒员工注意
                if (curOperation.size() == mList_lastOperation.size()) {
                    for (SewAttr item1 : curOperation) {
                        boolean flag = true;
                        for (SewAttr item2 : mList_lastOperation) {
                            if (item1.getName().equals(item2.getName())) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            needAlert = true;
                            break;
                        }
                    }
                } else {
                    needAlert = true;
                }
                if (needAlert) {
                    ErrorDialog.showAlert(mContext, "当前要做工序与上个要做工序不同，请注意", ErrorDialog.TYPE.ALERT, null, false);
                    SystemUtils.startSystemRingtoneAlert(mContext);
                }
            }
            mList_lastOperation = curOperation;

            boolean hasNC = false;
            for (int i = 0; i < curOperation.size(); i++) {
                SewAttr opera = curOperation.get(i);
                if ("TQTXJ002".equals(opera.getName())) {
                    if (mOfflineDialog != null) {
                        mOfflineDialog.dismiss();
                        mOfflineDialog = null;
                    }
                    mOfflineDialog = new OfflineDialog(mContext, mSewData.getSfc(), mRFID, mSewData.getShopOrder(), opera.getName(), opera.getDescription());
                    mOfflineDialog.show();
                }
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
            mVP_sop.setAdapter(new CommonVPAdapter<SewAttr>(mContext, curOperation, R.layout.item_imageview) {
                @Override
                public void convertView(View view, SewAttr item, final int position) {
                    ImageView imageView = view.findViewById(R.id.imageView);
                    String sop_url = item.getAttributes().getSOP_URL();
                    if (!isEmpty(sop_url)) {
                        Picasso.with(mContext).load(sop_url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<String> urls = new ArrayList<>();
                                for (SewAttr data : curOperation) {
                                    urls.add(data.getAttributes().getSOP_URL());
                                }
                                startActivity(ImageBrowserActivity.getIntent(mContext, urls, position));
                            }
                        });
                    }
                }
            });

            if (curOperation.size() != 0) {
                refreshProcessView(0);
            }
        }

        //包装主题，去分拣
        if (TopicUtil.TOPIC_PACKING.equals(mTopic)) {
            showSortingDialog();
        }

        //拓展数据
        SewDataBo.ExtInfoMap extInfoMap = mSewData.getExtInfoMap();
        if (extInfoMap != null) {
            //不良描述
            NcDataBo ncData = extInfoMap.getNC_DATA();
            if (ncData != null) {
                mLayout_ncData.setVisibility(View.VISIBLE);
                mTv_ncData.setText(ncData.getNC_DESC());
            } else {
                mLayout_ncData.setVisibility(View.GONE);
            }

            String secondClass = extInfoMap.getSecondClass();
            if (isEmpty(secondClass)) {
                mTv_secondClass.setVisibility(View.GONE);
            } else {
                mTv_secondClass.setVisibility(View.VISIBLE);
            }
        } else {
            mLayout_ncData.setVisibility(View.GONE);
            mTv_secondClass.setVisibility(View.GONE);
        }
    }

    /**
     * 工序列表适配器
     */
    private class ProcessListAdapter extends CommonAdapter<SewAttr> {

        ProcessListAdapter(Context context, List<SewAttr> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, SewAttr item, int position) {
            String operationTime = item.getAttributes().getOPERATION_TIME();

            try {
                float aFloat = Float.parseFloat(operationTime);
                aFloat = aFloat / 60;
                operationTime = getString(R.string.float_2, aFloat) + "分";
            } catch (Exception e) {
                e.printStackTrace();
                operationTime += "秒";
            }

            holder.setText(R.id.textView, item.getDescription() + "\n工时：" + operationTime);
        }
    }

    /**
     * 获取物料布局
     */
    private View getMatView(SewAttr item, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        ImageView imageView = view.findViewById(R.id.iv_materials);
        String mat_url = item.getAttributes().getMAT_URL();
        if (!isEmpty(mat_url))
            Picasso.with(mContext).load(mat_url).resize(200, 200).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
        TextView textView = view.findViewById(R.id.tv_matNum);
        textView.setText(item.getDescription());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urls = new ArrayList<>();
                List<SewAttr> matInfos = mSewData.getColorItems();
                for (SewAttr matInfo : matInfos) {
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
        SewAttr item = mSewData.getCurrentOpeationInfos().get(position);
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
                assert mSewData != null;
                String sfc = mSewData.getSfc();
                if (!isEmpty(sfc)) {
                    mActivity.setButtonState(R.id.btn_subStart, true);
                    mActivity.setButtonState(R.id.btn_subComplete, true);
                }
                if (isNewOrder) {
                    mActivity.setButtonState(R.id.btn_manualStart, true);
                    mActivity.setButtonState(R.id.btn_manualComplete, true);
                    mActivity.setButtonState(R.id.btn_offlineSort, true);
                    mActivity.setButtonState(R.id.btn_sorting, true);
                }
                SpUtil.saveSalesOrder(mSewData.getSalesOrder());
                SpUtil.save(SpUtil.KEY_SHOPORDER, mSewData.getShopOrder());
                isNewOrder = true;
                initData();
            } else if (HttpHelper.initNcForQA.equals(url)) {
                toast("操作成功");
            } else if (HttpHelper.sewSubStart.equals(url)) {
                toast("开始绣花工序");
                mActivity.setButtonState(R.id.btn_subStart, false);
            } else if (HttpHelper.saveSubcontractInfo.equals(url)) {
                toast("绣花工序完成");
                mActivity.setButtonState(R.id.btn_subComplete, false);
            } else if (HttpHelper.offlineSort.equals(url)) {
                mActivity.setButtonState(R.id.btn_offlineSort, false);
                mActivity.setButtonState(R.id.btn_sorting, false);
                toast(resultJSON.getString("result"));
            }
        }
    }
}
