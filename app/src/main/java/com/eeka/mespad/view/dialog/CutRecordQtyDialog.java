package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.BTReasonBo;
import com.eeka.mespad.bo.CutRecordQtyBo;
import com.eeka.mespad.bo.ReturnMaterialInfoBo;
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

public class CutRecordQtyDialog extends BaseDialog implements View.OnClickListener {

    private CutRecordQtyBo mData;
    private List<CutRecordQtyBo.RecordQtyItemBo> mList_data;
    private ItemAdapter mAdapter;

    private String mRFID;
    private String mShopOrder;
    private List<TailorInfoBo.OPERINFORBean> mList_process;

    public CutRecordQtyDialog(@NonNull Context context, String rfid, String shopOrder, List<TailorInfoBo.OPERINFORBean> processList) {
        super(context);
        mRFID = rfid;
        mShopOrder = shopOrder;
        mList_process = processList;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_cutrecordqty, null);
        setContentView(view);

        view.findViewById(R.id.btn_close).setOnClickListener(this);
        view.findViewById(R.id.btn_add).setOnClickListener(this);
        view.findViewById(R.id.btn_save).setOnClickListener(this);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.recyclerView_cutRecordQty);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        list.setLayoutManager(layoutManager);
        mList_data = new ArrayList<>();
        mAdapter = new ItemAdapter(mContext, mList_data, R.layout.item_cutrecordqty, layoutManager);
        list.setAdapter(mAdapter);

        initData();
    }

    private void initData() {
        LoadingDialog.show(mContext);
        HttpHelper.getCutRecordData(mRFID, mShopOrder, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                if (HttpHelper.isSuccess(resultJSON)) {
                    String resultStr = HttpHelper.getResultStr(resultJSON);
                    mData = JSON.parseObject(resultStr, CutRecordQtyBo.class);
                    List<CutRecordQtyBo.RecordQtyItemBo> recordList = mData.getRECORD_LIST();
                    if (recordList == null || recordList.size() == 0) {
                        //如果之前未保存过数据，添加一条空数据待用户录入
                        mList_data.add(new CutRecordQtyBo.RecordQtyItemBo());
                    } else {
                        mList_data = recordList;
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
                }
                LoadingDialog.dismiss();
            }

            @Override
            public void onFailure(String url, int code, String message) {
                LoadingDialog.dismiss();
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    private class ItemAdapter extends CommonRecyclerAdapter<CutRecordQtyBo.RecordQtyItemBo> {

        ItemAdapter(Context context, List<CutRecordQtyBo.RecordQtyItemBo> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, final CutRecordQtyBo.RecordQtyItemBo item, final int position) {
            holder.setText(R.id.tv_cutRecordQty_bedNo, mData.getWORK_CENTER());
            holder.setText(R.id.tv_cutRecordQty_deviceNo, mData.getRESOURCE());
            holder.setText(R.id.tv_cutRecordQty_orderNo, mData.getSHOP_ORDER());
            holder.setText(R.id.tv_cutRecordQty_cutNo, mData.getLAYOUT());
            holder.setText(R.id.tv_cutRecordQty_matNo, mData.getMATERIAL());
            holder.setText(R.id.tv_cutRecordQty_matType, mData.getLAYOUT_TYPE());

            TextView tv_recordUser = holder.getView(R.id.tv_cutRecordQty_recordUser);
            tv_recordUser.setText(item.getUSER_ID());
            EditText et_recordQty = holder.getView(R.id.et_cutRecordQty_recordQty);
            et_recordQty.setText(item.getRECORD());
            et_recordQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    item.setRECORD(s.toString());
                }
            });

            TextView tv_process = holder.getView(R.id.tv_cutRecordQty_process);
            tv_process.setText(item.getOPERATION_DESC());
            tv_process.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopWindow(v, position);
                }
            });

            holder.getView(R.id.tv_cutRecordQty_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeData(position);
                }
            });
        }
    }

    private void showPopWindow(final View v, final int index) {
        SelectorPopWindow<TailorInfoBo.OPERINFORBean> ppw = new SelectorPopWindow<>(mContext, mList_process, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TailorInfoBo.OPERINFORBean operation = mList_process.get(position);
                ((TextView) v).setText(operation.getDESCRIPTION());

                CutRecordQtyBo.RecordQtyItemBo itemBo = mData.getRECORD_LIST().get(index);
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
                mAdapter.addData(new CutRecordQtyBo.RecordQtyItemBo());
                break;
            case R.id.btn_save:
                save();
                break;
        }
    }

    private void save() {
        List<CutRecordQtyBo.RecordQtyItemBo> recordList = mData.getRECORD_LIST();
        for (CutRecordQtyBo.RecordQtyItemBo item : recordList) {
            if (TextUtils.isEmpty(item.getUSER_ID())) {
                ErrorDialog.showAlert(mContext, "请刷卡录入记录人");
                return;
            } else if (TextUtils.isEmpty(item.getRECORD())) {
                ErrorDialog.showAlert(mContext, "请填写记录数据");
                return;
            }
        }
        LoadingDialog.show(mContext);
        HttpHelper.saveCutRecordData(mData, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                if (HttpHelper.isSuccess(resultJSON)) {
                    Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
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
        });
    }
}
