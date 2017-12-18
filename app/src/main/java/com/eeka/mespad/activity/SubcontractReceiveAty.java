package com.eeka.mespad.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.bo.SubcontractReceiveBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.view.dialog.ErrorDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 外协收货
 * Created by Lenovo on 2017/12/15.
 */

public class SubcontractReceiveAty extends NFCActivity {

    private ListView mLv_receive;//当前操作的列表
    private ListView mLv_complete;//已保存的列表
    private List<SubcontractReceiveBo> mList_receive;
    private List<List<SubcontractReceiveBo>> mList_complete;
    private ReceAdapter mReceAdapter;
    private CompleteAdapter mCompleteAdapter;

    private EditText mEt_orderNum;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_subcontract_receive);

        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        super.initView();

        mEt_orderNum = (EditText) findViewById(R.id.et_orderNum);
        mLv_receive = (ListView) findViewById(R.id.lv_subcontract_receive);
        mLv_complete = (ListView) findViewById(R.id.lv_subcontract_received);

        mList_receive = new ArrayList<>();
        mReceAdapter = new ReceAdapter(mContext, mList_receive, R.layout.item_subcontract_receive);
        mLv_receive.setAdapter(mReceAdapter);

        mList_complete = new ArrayList<>();
        mCompleteAdapter = new CompleteAdapter(mContext, mList_complete, R.layout.item_subcontract_complete);
        mLv_complete.setAdapter(mCompleteAdapter);

        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        final LinearLayout layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
        final LinearLayout layout_bottom1 = (LinearLayout) findViewById(R.id.layout_bottom1);

        layout_bottom.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //获取View可见区域的bottom
                Rect rect = new Rect();
                layout_bottom.getWindowVisibleDisplayFrame(rect);
                if (bottom != 0 && oldBottom != 0 && bottom - rect.bottom <= 0) {
                    layout_bottom.findViewById(R.id.spaceView).setVisibility(View.VISIBLE);
                    layout_bottom1.findViewById(R.id.spaceView).setVisibility(View.VISIBLE);
                    Logger.d("隐藏");
                } else {
                    layout_bottom.findViewById(R.id.spaceView).setVisibility(View.GONE);
                    layout_bottom1.findViewById(R.id.spaceView).setVisibility(View.GONE);
                    Logger.d("弹出");
                }
            }
        });
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
            case R.id.btn_clear:
                if (mList_receive.size() != 0) {
                    ErrorDialog.showConfirmAlert(mContext, "是否确认清空收货表格内的数据？", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mList_receive.clear();
                            mReceAdapter.notifyDataSetChanged(mList_receive);
                        }
                    });
                }
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

    private void addItem(SubcontractReceiveBo item) {
        if (item == null) {
            item = new SubcontractReceiveBo();
            item.setEditAble(true);
        }
        mList_receive.add(item);
        mReceAdapter.notifyDataSetChanged(mList_receive);
    }

    private void save() {
        StringBuilder sb = new StringBuilder();
        for (SubcontractReceiveBo item : mList_receive) {
            String rfid = item.getRFID();
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

            if (item.isEditAble()) {
                et_rfid.setEnabled(true);
                et_order.setEnabled(true);
                et_sfc.setEnabled(true);
                et_size.setEnabled(true);
            } else {
                et_rfid.setEnabled(false);
                et_order.setEnabled(false);
                et_sfc.setEnabled(false);
                et_size.setEnabled(false);
            }

            et_rfid.addTextChangedListener(new TextChangedListener(TextChangedListener.TYPE_RFID, position));
            et_order.addTextChangedListener(new TextChangedListener(TextChangedListener.TYPE_ORDER, position));
            et_sfc.addTextChangedListener(new TextChangedListener(TextChangedListener.TYPE_SFC, position));
            et_size.addTextChangedListener(new TextChangedListener(TextChangedListener.TYPE_SIZE, position));

            holder.getView(R.id.tv_subcontractReceive_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    SubcontractReceiveBo itemBo = mList_receive.get(position);
                    String rfid = item.getRFID();
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
                    item.setRFID(value);
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
            holder.setText(R.id.tv_subcontractComplete_no, position + "");
            holder.setText(R.id.tv_subcontractComplete_qty, item.size() + "");

            holder.getView(R.id.tv_subcontractComplete_detail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mList_receive.size() == 0) {
                        mList_receive.addAll(item);
                        mReceAdapter.notifyDataSetChanged(mList_receive);
                    } else {
                        ErrorDialog.showConfirmAlert(mContext, "收货表格有未保存的数据会被清空，是否确认查看本单详情？", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mList_receive.clear();
                                mList_receive.addAll(item);
                                mReceAdapter.notifyDataSetChanged(mList_receive);
                            }
                        });
                    }
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

            } else if (HttpHelper.saveSubcontractInfo.equals(url)) {
                toast("保存成功");
                mList_complete.add(mList_receive);
                mCompleteAdapter.notifyDataSetChanged(mList_complete);
                mList_receive.clear();
            }
        }
    }
}
