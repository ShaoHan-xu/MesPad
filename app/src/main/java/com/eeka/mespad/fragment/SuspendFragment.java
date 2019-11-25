package com.eeka.mespad.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.CommonVPAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bluetoothPrint.BluetoothHelper;
import com.eeka.mespad.bo.ComponentInfoBo;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.SuspendComponentBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.view.dialog.AutoPickDialog;
import com.eeka.mespad.view.dialog.CreateCardDialog;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.MyAlertDialog;
import com.eeka.mespad.view.dialog.SuspendAlertDialog;
import com.eeka.mespad.view.dialog.WashLabelDialog;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 吊挂作业界面
 * Created by Lenovo on 2017/6/16.
 */

public class SuspendFragment extends BaseFragment {

//    private ListView mLv_orderList;

    private LinearLayout mLayout_component;
    private LinearLayout mLayout_matInfo;

    private ContextInfoBo mContextInfo;
    private SuspendComponentBo mComponent;
    private SuspendComponentBo.COMPONENTSBean mCurComponent;
    private String mCurSFC;
    private String mOperationBo;

    private ViewPager mVP_img;
    private List<String> mList_img;

    private LinearLayout mLayout_mtmOrder;
    private TextView mTv_curSFC;
    private TextView mTv_orderNum;
    private TextView mTv_MTMOrderNum;
    private TextView mTv_orderQty;
    private TextView mTv_finishQty;
    private TextView mTv_itemCode;
    private TextView mTv_matDesc;
    private TextView mTv_size;
    private TextView mTv_processLot;
    private TextView mTv_processLotQty;
    private TextView mTv_realCutQty;
    private HorizontalScrollView mHSV_imgBar;
    private LinearLayout mLayout_imgBar;
    private String mWashLabel;

    private Button mBtn_binding;

    //赋值避免空指针
    private String mShopOrder = "";
    private String mItemCode = "";
    private String mSize = "";

    private WashLabelDialog mWashLabelDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_suspend, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        EventBus.getDefault().register(this);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
//        mLv_orderList = mView.findViewById(R.id.lv_sfcList);
        mLayout_component = mView.findViewById(R.id.layout_component);
        mLayout_matInfo = mView.findViewById(R.id.layout_suspend_matInfo);
        mVP_img = mView.findViewById(R.id.vp_suspend_componentImg);

        mLayout_mtmOrder = mView.findViewById(R.id.layout_suspend_mtmOrder);
        mTv_curSFC = mView.findViewById(R.id.tv_suspend_curSFC);
        mTv_orderNum = mView.findViewById(R.id.tv_suspend_orderNum);
        mTv_MTMOrderNum = mView.findViewById(R.id.tv_suspend_MTMOrderNum);
        mTv_orderQty = mView.findViewById(R.id.tv_suspend_orderQty);
        mTv_finishQty = mView.findViewById(R.id.tv_suspend_finishQty);
        mTv_itemCode = mView.findViewById(R.id.tv_suspend_itemCode);
        mTv_matDesc = mView.findViewById(R.id.tv_suspend_matDesc);
        mTv_size = mView.findViewById(R.id.tv_suspend_size);
        mTv_processLot = mView.findViewById(R.id.tv_suspend_processLot);
        mTv_processLotQty = mView.findViewById(R.id.tv_suspend_processLotQTY);
        mTv_realCutQty = mView.findViewById(R.id.tv_suspend_realCutQty);
        mHSV_imgBar = mView.findViewById(R.id.hsv_suspend_img);
        mLayout_imgBar = mView.findViewById(R.id.layout_suspend_img);

        mBtn_binding = mView.findViewById(R.id.btn_suspend_binding);
        mBtn_binding.setOnClickListener(new View.OnClickListener() {
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
     * 收到推送消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushMsgReceive(String washLabel) {
        mWashLabel = washLabel;
        binding();
    }

    /**
     * 刷新订单信息
     */
    private void refreshOrderInfo() {
        mBtn_binding.setEnabled(true);
        mTv_curSFC.setText(mCurSFC);
        mTv_orderNum.setText(mComponent.getSHOP_ORDER());
        mTv_orderQty.setText(String.format("%s", mComponent.getQTY_ORDERED()));
        mTv_finishQty.setText(mComponent.getQTY_COMPLETE());
        mTv_itemCode.setText(mComponent.getITEM());
        mTv_matDesc.setText(mComponent.getITEM_DESC());
        mTv_size.setText(mComponent.getSFC_SIZE());
        mTv_processLot.setText(mComponent.getPROCESS_LOT());
        mTv_processLotQty.setText(mComponent.getLOT_SFC_QTY());
        mTv_realCutQty.setText(mComponent.getREAL_CUT_NUM());

        String salesOrder = mComponent.getSALES_ORDER();
        if (isEmpty(salesOrder)) {
            mLayout_mtmOrder.setVisibility(View.GONE);
        } else {
            mLayout_mtmOrder.setVisibility(View.VISIBLE);
            mTv_MTMOrderNum.setText(salesOrder);
        }
    }

    /**
     * 设置固定布局：工艺说明、当前工序
     */
    private void setupBaseView(JSONObject json) {
        mOperationBo = json.getString("HANDLE");
        TextView tv_curProcess = mView.findViewById(R.id.tv_suspend_curProcess);
        tv_curProcess.setText(json.getString("OPERATION"));
        final TextView tv_craftDesc = mView.findViewById(R.id.tv_suspend_craftDesc);
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
     * 重置部件布局
     */
    private void resetComponentView() {
        mLayout_matInfo.removeAllViews();
        mLayout_component.removeAllViews();
        List<SuspendComponentBo.COMPONENTSBean> components = mComponent.getCOMPONENTS();
        int flag = 0;
        for (SuspendComponentBo.COMPONENTSBean component : components) {
            mLayout_component.addView(getComponentView(component));
            if ("false".equals(component.getIsBound())) {
                flag++;
            }
        }
        isLastPart = flag <= 1;
        mVP_img.setAdapter(null);
        mList_img = null;
    }

    public void searchOrder(String rfid) {
        if (isAdded())
            showLoading();
        HttpHelper.getSfcComponents(mOperationBo, mContextInfo.getHANDLE(), rfid, this);
    }

    /**
     * 绑定RFID号
     */
    public boolean inputRFID(String rfid) {
        if (mWashLabelDialog != null && mWashLabelDialog.isShowing()) {
            mWashLabelDialog.setWashLabel(rfid);
            return true;
        }
        return false;
    }

    /**
     * 绑定
     */
    public void binding() {
        if (mCurComponent == null) {
            showErrorDialog("请点击选择要绑定的部件");
            return;
        }
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        if (positionUsers == null || positionUsers.size() == 0) {
            showErrorDialog("请员工刷卡登录");
            return;
        }
        //绑定洗水唛，目前只有于都产线需要
        if ("YD".equals(getString(R.string.app_channel))) {
            if ("true".equals(mCurComponent.getIsUnderCarry()) && "true".equals(mCurComponent.getIsMaster()) && isEmpty(mWashLabel)) {
                mWashLabelDialog = new WashLabelDialog(mContext, mComponent.getSFC(), mCurComponent.getComponentName());
                mWashLabelDialog.show();
                return;
            }
        }
        mBtn_binding.setEnabled(false);
        showLoading();
        HttpHelper.hangerBinding(mCurComponent.getComponentId(), mWashLabel, mCurComponent.getIsNeedSubContract(), mCurComponent.getIsMaster(), SuspendFragment.this);
    }

    /**
     * 解绑
     */
    public void unBind() {
        new CreateCardDialog(mContext, mCurSFC).show();
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
            ImageView imageView = view.findViewById(R.id.imageView);
            String url = mList_img.get(position);
            if (!isEmpty(url)) {
                Picasso.with(mContext).load(url).error(R.drawable.ic_error_img).placeholder(R.drawable.loading).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> urls = new ArrayList<>(mList_img);
                        startActivity(ImageBrowserActivity.getIntent(mContext, urls, false));
                    }
                });
            }
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

    private boolean isLastPart;

    /**
     * 获取部件布局
     */
    private View getComponentView(final SuspendComponentBo.COMPONENTSBean component) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_component, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        Button btn_component = view.findViewById(R.id.btn_componentName);
        btn_component.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        ImageView iv_finished = view.findViewById(R.id.iv_part_finished);
        TextView tv_helpDesc = view.findViewById(R.id.tv_helpDesc);
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
                mWashLabel = null;
                HttpHelper.getComponentInfo(mComponent.getSHOP_ORDER(), mCurSFC, component.getComponentId(), SuspendFragment.this);
                List<SuspendComponentBo.COMPONENTSBean> components = mComponent.getCOMPONENTS();
                int childCount = mLayout_component.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = mLayout_component.getChildAt(i);
                    Button button = childAt.findViewById(R.id.btn_componentName);
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
        if (HttpHelper.hangerBindMes.equals(url)) {
            String code = resultJSON.getString("Code");
            if (!"0".equals(code)) {
                showErrorDialog(resultJSON.getString("Message"));
            }
            return;
        }
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
            } else if (HttpHelper.getSuspendUndoList.equals(url)) {
//                mList_sfcList = JSON.parseArray(resultJSON.getJSONArray("result").toString(), String.class);
////                mSFCAdapter.notifyDataSetChanged(mList_sfcList);
////                for (int i = 0; i < mList_sfcList.size(); i++) {
////                    String str = mList_sfcList.get(i);
////                    if (mCurSFC != null && mCurSFC.equals(str)) {
////                        mLv_orderList.setSelection(i);
////                        break;
////                    }
////                }
            } else if (HttpHelper.getSfcComponents.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                mComponent = JSON.parseObject(result.toString(), SuspendComponentBo.class);
                //把主部件放到第一位
                List<SuspendComponentBo.COMPONENTSBean> components = mComponent.getCOMPONENTS();
                for (int i = 0; i < components.size(); i++) {
                    SuspendComponentBo.COMPONENTSBean item = components.get(i);
                    if ("true".equals(item.getIsMaster())) {
                        components.add(0, components.remove(i));
                        break;
                    }
                }
                String shopOrder = mComponent.getSHOP_ORDER();
                String itemCode = mComponent.getITEM();
                String size = mComponent.getSFC_SIZE();
                if (isEmpty(size)) {
                    size = "";
                }
                if (mShopOrder.equals(shopOrder) && mItemCode.equals(itemCode) && mSize.equals(size)) {
                    sureOrderInfoComplete();
                } else {
                    if (mSuspendAlertDialog != null && mSuspendAlertDialog.isShowing()) {
                        //连续刷两次卡的情况，先把之前的弹框消掉
                        mSuspendAlertDialog.dismiss();
                    } else {
                        mSuspendAlertDialog = new SuspendAlertDialog(mContext, shopOrder, itemCode, size, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sureOrderInfoComplete();
                            }
                        });
                        mSuspendAlertDialog.show();
                    }
                }
            } else if (HttpHelper.getComponentInfo.equals(url)) {
                ComponentInfoBo componentInfoBo = JSON.parseObject(HttpHelper.getResultStr(resultJSON), ComponentInfoBo.class);
                mList_img = componentInfoBo.getPICTURE_URL();
                mVP_img.setAdapter(new ImgAdapter(mContext, mList_img, R.layout.item_imageview));
                if (mList_img.size() > 1) {
                    mHSV_imgBar.setVisibility(View.VISIBLE);
                    setImgBar();
                } else {
                    mHSV_imgBar.setVisibility(View.GONE);
                }
                setupMatInfo(componentInfoBo.getMaterialInfo());
            } else if (HttpHelper.hangerBinding.equals(url)) {
                toast("衣架绑定成功");

                //目前在龙华测试
                String channelName = getString(R.string.app_channel);
                if (!"YD".equals(channelName)) {
                    hangerBindMes();
                }
            } else if (HttpHelper.hangerUnbind.equals(url)) {
                toast("衣架解绑成功");
            }
        } else {
            mBtn_binding.setEnabled(true);
        }
    }

    private String mHangerId;

    public void sendHangerId(String hangerId) {
        mHangerId = hangerId;
    }

    private void hangerBindMes() {
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        JSONObject json = new JSONObject();
        json.put("HangerID", mHangerId);
        json.put("Site", SpUtil.getSite());
        json.put("LineID", contextInfo.getLINE_CATEGORY());
        json.put("StationID", contextInfo.getPOSITION());
        json.put("Tag", mComponent.getSFC());
//        if (isLastPart) {
        json.put("ProductTag", mComponent.getSFC());
//        } else {
//            json.put("ProductTag", mComponent.getSFC() + "_" + mCurComponent.getComponentId());
//        }
        json.put("PartID", mCurComponent.getComponentId());
        HttpHelper.hangerBindMes(json, this);
    }

    private void setupMatInfo(List<ComponentInfoBo.MaterialInfoBean> matInfo) {
        mLayout_matInfo.removeAllViews();
        for (ComponentInfoBo.MaterialInfoBean item : matInfo) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_suspend_matinfo, null);
            TextView tv_matCode = view.findViewById(R.id.tv_suspend_matCode);
            TextView tv_matName = view.findViewById(R.id.tv_suspend_matName);
            TextView tv_matUsedQTY = view.findViewById(R.id.tv_suspend_matUsedQTY);
            tv_matCode.setText(item.getMATERIAL_CODE());
            tv_matName.setText(item.getMATERIAL_NAME());
            tv_matUsedQTY.setText(getString(R.string.float_2, item.getQTY()));

            final String url = item.getMAT_URL();
            if (isEmpty(url)) {
                showAlert("该物料无图片地址返回");
            } else {
                tv_matCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> list = new ArrayList<>();
                        list.add(url);
                        startActivity(ImageBrowserActivity.getIntent(mContext, list, 0));
                    }
                });
            }
            mLayout_matInfo.addView(view);
        }
    }

    private SuspendAlertDialog mSuspendAlertDialog;

    /**
     * 设置图片导航
     */
    private void setImgBar() {
        mLayout_imgBar.removeAllViews();
        for (int i = 0; i < mList_img.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_img_100, null);
            ImageView imageView = view.findViewById(R.id.imageView);
            String url = mList_img.get(i);
            if (!isEmpty(url)) {
                Picasso.with(mContext).load(url).error(R.drawable.ic_error_img).placeholder(R.drawable.loading).into(imageView);
                imageView.setTag(i);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        if (mList_img == null) {
                            ErrorDialog.showAlert(mContext, "是否长时间没有操作PAD了？数据已发生变更，请重启应用后再进行查看。");
                        } else {
                            ArrayList<String> urls = new ArrayList<>(mList_img);
                            startActivity(ImageBrowserActivity.getIntent(mContext, urls, position));
                        }
                    }
                });
            }
            mLayout_imgBar.addView(view);
        }
    }

    private void sureOrderInfoComplete() {
        mShopOrder = mComponent.getSHOP_ORDER();
        mItemCode = mComponent.getITEM();
        mSize = mComponent.getSFC_SIZE();
        //返回null时赋值，避免再次刷卡效验数据时空指针错误
        if (isEmpty(mShopOrder)) {
            mShopOrder = "";
        }
        if (isEmpty(mItemCode)) {
            mItemCode = "";
        }
        if (isEmpty(mSize)) {
            mSize = "";
        }
        SpUtil.saveSalesOrder(mComponent.getSALES_ORDER());
        mCurSFC = mComponent.getSFC();
        mCurComponent = null;
        resetComponentView();
        refreshOrderInfo();
        printSFC();
    }

    private Thread mPrintThread;
    private String mPrintData;
    private boolean stopFlag;//线程停止标识,=true表示停止线程
    private boolean printFlag = true;

    /**
     * 打印SFC
     */
    private void printSFC() {
        String pData = mCurSFC.substring(4, mCurSFC.length());
        if (!pData.equals(mPrintData)) {
            mPrintData = pData;
            printFlag = true;
        } else {
            printFlag = false;
        }
        if (mPrintThread == null) {
            mPrintThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!stopFlag) {
                        if (printFlag) {
                            printFlag = false;
                            BluetoothHelper.print(getActivity(), mPrintData);
                        }
                    }
                }
            });
            mPrintThread.start();
        }
    }

    @Override
    public void onDestroy() {
        stopFlag = true;
        super.onDestroy();
    }
}
