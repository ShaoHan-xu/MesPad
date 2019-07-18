package com.eeka.mespad.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bluetoothPrint.BluetoothHelper;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class SplitCardDialog extends BaseDialog {

    private String mCardNum;
    private TailorInfoBo mMainData;
    private String mProcessLotBo;
    private View.OnClickListener mListener;
    private LinearLayout mLayout_items;
    private TextView mTv_qty;
    private TextView mTv_size;

    public SplitCardDialog(@NonNull Context context, String cardNum, String processLotBo, @NonNull TailorInfoBo data, View.OnClickListener listener) {
        super(context);
        mCardNum = cardNum;
        mMainData = data;
        mProcessLotBo = processLotBo;
        mListener = listener;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_splitcard, null);
        setContentView(view);

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        TailorInfoBo.ProcessLotInfo processLotInfo = mMainData.getPROCESS_LOT_INFO();
        TextView tv_cardNum = view.findViewById(R.id.tv_splitCard_cardNum);
        tv_cardNum.setText(mCardNum);
        mTv_size = view.findViewById(R.id.tv_splitCard_size);
        mTv_qty = view.findViewById(R.id.tv_splitCard_qty);
        mTv_size.setText(processLotInfo.getSIZE());
        mTv_qty.setText(processLotInfo.getPROCESS_LOT_QTY());

        mLayout_items = view.findViewById(R.id.layout_splitCard_childItem);
        Button btn_add = view.findViewById(R.id.btn_splitCard_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    private void addItem() {
        final View childView = LayoutInflater.from(mContext).inflate(R.layout.item_splitcardchild, null);
        mLayout_items.addView(childView);

        int No = mMainData.getSUB_PACKAGE_NUM() + mLayout_items.getChildCount();
        TextView tv_No = childView.findViewById(R.id.tv_splitCard_No);
        tv_No.setText(String.format("%d", No));

        Button btn_del = childView.findViewById(R.id.btn_splitCard_delete);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout_items.removeView(childView);
            }
        });

        EditText et_qty = childView.findViewById(R.id.et_splitCard_qty);
        et_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshQTY();
            }
        });
    }

    /**
     * 刷新数量
     */
    private void refreshQTY() {
        TailorInfoBo.ProcessLotInfo processLotInfo = mMainData.getPROCESS_LOT_INFO();
        int count = 0;
        for (int i = 0; i < mLayout_items.getChildCount(); i++) {
            View childView = mLayout_items.getChildAt(i);
            EditText et_qty = childView.findViewById(R.id.et_splitCard_qty);
            String qty = et_qty.getText().toString();
            if (!TextUtils.isEmpty(qty)) {
                count += Integer.valueOf(qty);
            }
        }
        int qty = Integer.valueOf(processLotInfo.getPROCESS_LOT_QTY());
        if (count > qty) {
            ErrorDialog.showAlert(mContext, "分卡数量大于总数量,请注意");
        }
        mTv_qty.setText(String.valueOf(qty - count));
    }

    private void save() {
        List<SplitCardItemBo> list = new ArrayList<>();
        for (int i = 0; i < mLayout_items.getChildCount(); i++) {
            View childItemView = mLayout_items.getChildAt(i);
            EditText et_cardNum = childItemView.findViewById(R.id.et_splitCard_cardNum);
            EditText et_qty = childItemView.findViewById(R.id.et_splitCard_qty);
            String cardNum = et_cardNum.getText().toString();
            if (TextUtils.isEmpty(cardNum)) {
                ErrorDialog.showAlert(mContext, "卡号不能为空");
                return;
            }
            if (cardNum.equals(mCardNum)) {
                ErrorDialog.showAlert(mContext, "分卡不能与母卡卡号相同");
                return;
            }

            for (int j = 0; j < list.size(); j++) {
                SplitCardItemBo item1 = list.get(j);
                if (cardNum.equals(item1.getCardId())) {
                    ErrorDialog.showAlert(mContext, cardNum + "有相同卡号，请效验");
                    return;
                }
            }
            String qty = et_qty.getText().toString();
            if (TextUtils.isEmpty(qty) || "0".equals(qty)) {
                ErrorDialog.showAlert(mContext, "卡号：" + cardNum + "，数量不能为0或者空，请注意!");
                return;
            }

            TextView tv_number = childItemView.findViewById(R.id.tv_splitCard_No);
            String number = tv_number.getText().toString();

            SplitCardItemBo bo = new SplitCardItemBo();
            bo.setCardId(cardNum);
            bo.setSubqty(qty);
            bo.setSize(mTv_size.getText().toString());
            bo.setNumber(number);
            list.add(bo);
        }

        LoadingDialog.show(mContext);
        final SplitCardDataBo data = new SplitCardDataBo();
        data.setPcardId(mCardNum);
        data.setSize(mTv_size.getText().toString());
        data.setLotInfos(list);
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        data.setLineCateGory(contextInfo.getLINE_CATEGORY());
        data.setPosition(contextInfo.getPOSITION());
        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        data.setResourceBo(resource.getRESOURCE_BO());
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        data.setUserId(positionUsers.get(0).getEMPLOYEE_NUMBER());
        data.setProcessLotBo(mProcessLotBo);

        TailorInfoBo.SHOPORDERINFORBean shopOrderInfo = mMainData.getSHOP_ORDER_INFOR();
        data.setItem(shopOrderInfo.getITEM());
        data.setShopOrder(shopOrderInfo.getSHOP_ORDER());
        data.setShopOrderBo(shopOrderInfo.getSHOP_ORDER_BO());
        List<String> opersBo = new ArrayList<>();
        List<TailorInfoBo.OPERINFORBean> oper_infor = mMainData.getOPER_INFOR();
        for (TailorInfoBo.OPERINFORBean oper : oper_infor) {
            opersBo.add(oper.getOPERATION_BO());
        }
        data.setOpersBo(opersBo);
        HttpHelper.splitCard(data, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    Toast.makeText(mContext, "分包成功，正在打印，请稍等", Toast.LENGTH_SHORT).show();
                    BluetoothHelper.printSubCardInfo((Activity) mContext, data);
                    if (mListener != null) {
                        mListener.onClick(null);
                    }
                    dismiss();
                } else {
                    ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
                }
            }

            @Override
            public void onFailure(String url, int code, String message) {
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }

    public class SplitCardDataBo {
        private String pcardId;
        private String size;
        private String processLotBo;
        private String resourceBo;
        private String lineCateGory;
        private String position;
        private String userId;
        private String item;
        private String shopOrder;
        private String shopOrderBo;
        private List<SplitCardItemBo> lotInfos;
        private List<String> opersBo;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getShopOrder() {
            return shopOrder;
        }

        public void setShopOrder(String shopOrder) {
            this.shopOrder = shopOrder;
        }

        public String getPcardId() {
            return pcardId;
        }

        public void setPcardId(String pcardId) {
            this.pcardId = pcardId;
        }

        public String getProcessLotBo() {
            return processLotBo;
        }

        public void setProcessLotBo(String processLotBo) {
            this.processLotBo = processLotBo;
        }

        public String getResourceBo() {
            return resourceBo;
        }

        public void setResourceBo(String resourceBo) {
            this.resourceBo = resourceBo;
        }

        public String getLineCateGory() {
            return lineCateGory;
        }

        public void setLineCateGory(String lineCateGory) {
            this.lineCateGory = lineCateGory;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getShopOrderBo() {
            return shopOrderBo;
        }

        public void setShopOrderBo(String shopOrderBo) {
            this.shopOrderBo = shopOrderBo;
        }

        public List<SplitCardItemBo> getLotInfos() {
            return lotInfos;
        }

        public void setLotInfos(List<SplitCardItemBo> lotInfos) {
            this.lotInfos = lotInfos;
        }

        public List<String> getOpersBo() {
            return opersBo;
        }

        public void setOpersBo(List<String> opersBo) {
            this.opersBo = opersBo;
        }
    }

    public class SplitCardItemBo {
        private String cardId;
        private String subqty;
        private String size;
        private String number;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getSubqty() {
            return subqty;
        }

        public void setSubqty(String subqty) {
            this.subqty = subqty;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }
}
