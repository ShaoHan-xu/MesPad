package com.eeka.mespad.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.bo.SubcontractReceiveBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.service.MQTTService;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.SubcontractDetailDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 外协收货
 * Created by Lenovo on 2017/12/15.
 */

public class SubcontractReceiveAty extends NFCActivity {

    private List<SubcontractReceiveBo> mList_receive;
    private List<List<SubcontractReceiveBo>> mList_complete;
    private ReceAdapter mReceAdapter;
    private CompleteAdapter mCompleteAdapter;

    private EditText mEt_orderNum;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_subcontract_receive);

        initView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MQTTService.actionStart(mContext);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        MQTTService.actionStop(mContext);
        super.onDestroy();
    }

    private long mLastMillis;

    @Override
    public void onBackPressed() {
        long curMillis = System.currentTimeMillis();
        if (curMillis - mLastMillis <= 2000) {
            finish();
            System.exit(0);
        } else {
            mLastMillis = curMillis;
            toast("再按一次返回键退出应用");
        }
    }

    @Override
    protected void initView() {
        super.initView();

        mEt_orderNum = (EditText) findViewById(R.id.et_orderNum);
        ListView mLv_receive = (ListView) findViewById(R.id.lv_subcontract_receive);
        ListView mLv_complete = (ListView) findViewById(R.id.lv_subcontract_received);

        mList_receive = new ArrayList<>();
        mReceAdapter = new ReceAdapter(mContext, mList_receive, R.layout.item_subcontract_receive);
        mLv_receive.setAdapter(mReceAdapter);

        mList_complete = new ArrayList<>();
        mCompleteAdapter = new CompleteAdapter(mContext, mList_complete, R.layout.item_subcontract_complete);
        mLv_complete.setAdapter(mCompleteAdapter);

        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

    }

    /**
     * 收到推送消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushMsgReceive(PushJson push) {
        String type = push.getType();
        if (PushJson.TYPE_RFID.equals(type)) {
            String rfid = push.getContent();
            getRFIDInfo(rfid);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_add:
                addItem(null);
                break;
            case R.id.btn_search:
                String rfid = mEt_orderNum.getText().toString();
                if (isEmpty(rfid)) {
                    toast("请输入RFID卡号进行搜索");
                } else {
                    getRFIDInfo(rfid);
                }
                break;
            case R.id.btn_save:
                save();
                break;
        }
    }

    private void addItem(SubcontractReceiveBo data) {
        if (data == null) {
            data = new SubcontractReceiveBo();
            data.setEditAble(true);
        }
        for (SubcontractReceiveBo item : mList_receive) {
            String rfid = item.getRfid();
            if (isEmpty(rfid) || rfid.equals(data.getRfid())) {
                return;
            }
        }
        mReceAdapter.addData(data);
    }

    private void save() {
        StringBuilder sb = new StringBuilder();
        for (SubcontractReceiveBo item : mList_receive) {
            String rfid = item.getRfid();
            if (!isEmpty(rfid))
                sb.append(rfid).append(",");
        }
        if (!isEmpty(sb.toString())) {
            sb.deleteCharAt(sb.lastIndexOf(","));
            showLoading();
            HttpHelper.saveSubcontractInfo(sb.toString(), this);
        } else {
            showErrorDialog("请录入数据并确认RFID卡号不为空后保存");
        }
    }

    private class ReceAdapter extends CommonAdapter<SubcontractReceiveBo> {

        ReceAdapter(Context context, List<SubcontractReceiveBo> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final SubcontractReceiveBo item, final int position) {
            EditText et_rfid = holder.getView(R.id.et_subcontractReceive_rfid);
            EditText et_order = holder.getView(R.id.et_subcontractReceive_orderNum);
            EditText et_sfc = holder.getView(R.id.et_subcontractReceive_sfc);
            EditText et_size = holder.getView(R.id.et_subcontractReceive_size);
            et_rfid.setText(item.getRfid());
            et_order.setText(item.getShopOrder());
            et_sfc.setText(item.getSfc());
            et_size.setText(item.getSize());

//            if (item.isEditAble()) {
//                et_rfid.setEnabled(true);
//                et_order.setEnabled(true);
//                et_sfc.setEnabled(true);
//                et_size.setEnabled(true);
//            } else {
//                et_rfid.setEnabled(false);
//                et_order.setEnabled(false);
//                et_sfc.setEnabled(false);
//                et_size.setEnabled(false);
//            }
//
//            et_rfid.addTextChangedListener(new TextChangedListener(TextChangedListener.TYPE_RFID, position));
//            et_order.addTextChangedListener(new TextChangedListener(TextChangedListener.TYPE_ORDER, position));
//            et_sfc.addTextChangedListener(new TextChangedListener(TextChangedListener.TYPE_SFC, position));
//            et_size.addTextChangedListener(new TextChangedListener(TextChangedListener.TYPE_SIZE, position));

            holder.getView(R.id.tv_subcontractReceive_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    SubcontractReceiveBo itemBo = mList_receive.get(position);
                    String rfid = item.getRfid();
                    String shopOrder = item.getShopOrder();
                    String sfc = item.getSfc();
                    String size = item.getSize();
                    if (TextUtils.isEmpty(rfid) && TextUtils.isEmpty(shopOrder) && TextUtils.isEmpty(sfc) && TextUtils.isEmpty(size)) {
                        removeData(position);
                    } else {
                        ErrorDialog.showConfirmAlert(mContext, "本行数据未保存，是否确认删除？", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeData(position);
                            }
                        });
                    }
                }
            });
        }
    }

    private class TextChangedListener implements TextWatcher {

        static final int TYPE_RFID = 0;
        static final int TYPE_ORDER = 1;
        static final int TYPE_SFC = 2;
        static final int TYPE_SIZE = 3;

        private int type;
        private int position;

        TextChangedListener(int type, int position) {
            this.type = type;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            SubcontractReceiveBo item = mList_receive.get(position);
            String value = s.toString();
            switch (type) {
                case TYPE_RFID:
                    item.setRfid(value);
                    break;
                case TYPE_ORDER:
                    item.setShopOrder(value);
                    break;
                case TYPE_SFC:
                    item.setSfc(value);
                    break;
                case TYPE_SIZE:
                    item.setSize(value);
                    break;
            }
        }
    }

    private class CompleteAdapter extends CommonAdapter<List<SubcontractReceiveBo>> {

        CompleteAdapter(Context context, List<List<SubcontractReceiveBo>> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final List<SubcontractReceiveBo> item, int position) {
            holder.setText(R.id.tv_subcontractComplete_no, (position + 1) + "");
            holder.setText(R.id.tv_subcontractComplete_qty, item.size() + "");

            holder.getView(R.id.tv_subcontractComplete_detail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SubcontractDetailDialog(mContext, item).show();
                }
            });
        }
    }

    private void getRFIDInfo(String rfid) {
        showLoading();
        HttpHelper.findRfidInfo(rfid, this);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.findRfidInfo.equals(url)) {
                SubcontractReceiveBo item = JSON.parseObject(HttpHelper.getResultStr(resultJSON), SubcontractReceiveBo.class);
                addItem(item);
            } else if (HttpHelper.saveSubcontractInfo.equals(url)) {
                toast("保存成功");

                List<SubcontractReceiveBo> list = new ArrayList<>();
                for (SubcontractReceiveBo item : mList_receive) {
                    list.add(item);
                }
                mList_complete.add(list);
                mCompleteAdapter.notifyDataSetChanged(mList_complete);

                mList_receive.clear();
                mReceAdapter.notifyDataSetChanged(mList_receive);
            }
        }
    }
}
