package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.CutRecordQtyBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 裁剪记件弹框
 * Created by xushaohan on 2018/2/24.
 */

public class CutRecordQtyDialog extends Dialog implements View.OnClickListener, HttpCallback {

    private Context mContext;
    private CutRecordQtyBo mData;
    private List<CutRecordQtyBo.RecordQtyItemBo> mList_data;
    private LinearLayout mLayout_list;

    private String mRFID;
    private String mShopOrder;
    private List<TailorInfoBo.OPERINFORBean> mList_process;

    public CutRecordQtyDialog(@NonNull Context context, String rfid, String shopOrder, List<TailorInfoBo.OPERINFORBean> processList) {
        super(context);
        mContext = context;
        mRFID = rfid;
        mShopOrder = shopOrder;
        mList_process = processList;
        init();
    }

    protected void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_cutrecordqty, null);
        setContentView(view);

        view.findViewById(R.id.btn_close).setOnClickListener(this);
        view.findViewById(R.id.btn_add).setOnClickListener(this);
        view.findViewById(R.id.btn_save).setOnClickListener(this);

        mLayout_list = view.findViewById(R.id.layout_cutRecordQty);

    }

    private View getItemView(CutRecordQtyBo.RecordQtyItemBo item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cutrecordqty, null);
        ((TextView) view.findViewById(R.id.tv_cutRecordQty_bedNo)).setText(mData.getWORK_CENTER());
        ((TextView) view.findViewById(R.id.tv_cutRecordQty_deviceNo)).setText(mData.getRESOURCE());
        ((TextView) view.findViewById(R.id.tv_cutRecordQty_orderNo)).setText(mData.getSHOP_ORDER());
        ((TextView) view.findViewById(R.id.tv_cutRecordQty_cutNo)).setText(mData.getWORK_CENTER());
        ((TextView) view.findViewById(R.id.tv_cutRecordQty_matNo)).setText(mData.getMATERIAL());
        ((TextView) view.findViewById(R.id.tv_cutRecordQty_matType)).setText(mData.getLAYOUT_TYPE());

        TextView tv_recordUser = view.findViewById(R.id.tv_cutRecordQty_recordUser);
        tv_recordUser.setText(item.getUSER_NAME());
        EditText et_recordQty = view.findViewById(R.id.et_cutRecordQty_recordQty);
        et_recordQty.setText(item.getRECORD());

        TextView tv_process = view.findViewById(R.id.tv_cutRecordQty_process);
        tv_process.setTag(position);
        tv_process.setText(item.getOPERATION_DESC());
        tv_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(v, (Integer) v.getTag());
            }
        });

        TextView tv_del = view.findViewById(R.id.tv_cutRecordQty_del);
        tv_del.setTag(position);
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem((Integer) v.getTag());
            }
        });

        return view;
    }

    private void addItem() {
        CutRecordQtyBo.RecordQtyItemBo itemBo = new CutRecordQtyBo.RecordQtyItemBo();
        mList_data.add(itemBo);
        mLayout_list.addView(getItemView(itemBo, mList_data.size() - 1));
    }

    private void removeItem(int position) {
        mList_data.remove(position);
        mLayout_list.removeViewAt(position);

        if (position < mList_data.size()) {
            for (int i = position; i < mLayout_list.getChildCount(); i++) {
                View view = mLayout_list.getChildAt(i);
                TextView tv_del = view.findViewById(R.id.tv_cutRecordQty_del);
                tv_del.setTag(i);
                TextView tv_process = view.findViewById(R.id.tv_cutRecordQty_process);
                tv_process.setTag(i);
            }
        }
    }

    private void showPopWindow(final View v, final int index) {
        SelectorPopWindow<TailorInfoBo.OPERINFORBean> ppw = new SelectorPopWindow<>(mContext, mList_process, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TailorInfoBo.OPERINFORBean operation = mList_process.get(position);
                ((TextView) v).setText(operation.getDESCRIPTION());

                CutRecordQtyBo.RecordQtyItemBo itemBo = mList_data.get(index);
                itemBo.setOPERATION(operation.getOPERATION());
                itemBo.setOPERATION_DESC(operation.getDESCRIPTION());
            }
        });
        ppw.show(v, (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_add:
                addItem();
                break;
            case R.id.btn_save:
                save();
                break;
        }
    }

    private void save() {
        int childCount = mLayout_list.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mLayout_list.getChildAt(i);
            CutRecordQtyBo.RecordQtyItemBo itemBo = mList_data.get(i);
            if (TextUtils.isEmpty(itemBo.getOPERATION())) {
                ErrorDialog.showAlert(mContext, "请选择要记录的工序");
                return;
            }
            if (TextUtils.isEmpty(itemBo.getUSER_ID())) {
                ErrorDialog.showAlert(mContext, "请刷卡录入记录人");
                return;
            }
            EditText et_recordQty = view.findViewById(R.id.et_cutRecordQty_recordQty);
            String recordQty = et_recordQty.getText().toString();
            if (TextUtils.isEmpty(recordQty)) {
                ErrorDialog.showAlert(mContext, "请填写记录数据");
                return;
            }
            itemBo.setRECORD(recordQty);
        }
        mData.setRFID(mRFID);
        mData.setRECORD_LIST(mList_data);
        LoadingDialog.show(mContext);
        HttpHelper.saveCutRecordData(mData, this);
    }

    public void getUserInfo(String cardNum) {
        LoadingDialog.show(mContext);
        HttpHelper.getUserInfo(cardNum, this);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout(SystemUtils.getScreenWidth(mContext), SystemUtils.getScreenHeight(mContext));
        
        //防止loading弹框被覆盖
        LoadingDialog.show(mContext);
        HttpHelper.getCutRecordData(mRFID, mShopOrder, this);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getCutRecordData.equals(url)) {
                String resultStr = HttpHelper.getResultStr(resultJSON);
                mData = JSON.parseObject(resultStr, CutRecordQtyBo.class);
                List<CutRecordQtyBo.RecordQtyItemBo> recordList = mData.getRECORD_LIST();
                mList_data = new ArrayList<>();
                if (recordList == null || recordList.size() == 0) {
                    //如果之前未保存过数据，添加一条空数据待用户录入
                    mList_data.add(new CutRecordQtyBo.RecordQtyItemBo());
                } else {
                    mList_data.addAll(recordList);
                }
                mLayout_list.removeAllViews();
                for (int i = 0; i < mList_data.size(); i++) {
                    CutRecordQtyBo.RecordQtyItemBo item = mList_data.get(i);
                    mLayout_list.addView(getItemView(item, i));
                }
            } else if (HttpHelper.saveCutRecordData.equals(url)) {
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                dismiss();
            } else if (HttpHelper.getUserInfo.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                String userId = result.getString("USER_ID");
                String userName = result.getString("USER_NAME");
                for (int i = 0; i < mList_data.size(); i++) {
                    CutRecordQtyBo.RecordQtyItemBo item = mList_data.get(i);
                    if (TextUtils.isEmpty(item.getUSER_ID()) || i == mList_data.size() - 1) {
                        item.setUSER_ID(userId);
                        item.setUSER_NAME(userName);
                        View childAt = mLayout_list.getChildAt(i);
                        TextView tv_user = childAt.findViewById(R.id.tv_cutRecordQty_recordUser);
                        tv_user.setText(userName);
                        break;
                    }
                }
            }
        } else {
            ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
        }
        LoadingDialog.dismiss();
    }

    @Override
    public void onFailure(String url, int code, String message) {
        ErrorDialog.showAlert(mContext, message);
        LoadingDialog.dismiss();
    }
}
