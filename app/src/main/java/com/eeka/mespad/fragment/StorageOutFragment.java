package com.eeka.mespad.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.DictionaryDataBo;
import com.eeka.mespad.bo.StorageOutBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.callback.IntegerCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.SelectorPopWindow;
import com.eeka.mespad.view.dialog.StorageOutQTYDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 出库
 */
public class StorageOutFragment extends BaseFragment {

    private static final int WHAT_INITDATA = 0;

    private TextView mTv_type;
    private TextView mTv_storageArea;
    private EditText mEt_shopOrder, mEt_item;
    private TextView mTv_autoRefresh;

    private ItemAdapter mItemAdapter;
    private List<StorageOutBo> mList_item;

    private List<DictionaryDataBo> mList_type;
    private Map<String, List<DictionaryDataBo>> mMap_area;
    private DictionaryDataBo mClothType;
    private DictionaryDataBo mArea;

    private int mOutPosition;
    private int mOutQTY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_storageout, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mTv_type = mView.findViewById(R.id.tv_storageOut_setType);
        mTv_storageArea = mView.findViewById(R.id.tv_storageOut_setArea);
        mEt_shopOrder = mView.findViewById(R.id.et_shopOrder);
        mEt_item = mView.findViewById(R.id.et_item);
        mTv_autoRefresh = mView.findViewById(R.id.tv_autoRefresh);

        ListView mLv_items = mView.findViewById(R.id.lv_storageOut_item);
        mItemAdapter = new ItemAdapter(mContext, mList_item, R.layout.item_storageout_item);
        mLv_items.setAdapter(mItemAdapter);

        mTv_type.setOnClickListener(this);
        mTv_autoRefresh.setOnClickListener(this);
        mTv_storageArea.setOnClickListener(this);
        mView.findViewById(R.id.btn_search).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

        mMap_area = new HashMap<>();

        showLoading();
        HttpHelper.getClothType(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_storageOut_setArea:
                if (mClothType == null) {
                    showErrorDialog("请先选择品类");
                    return;
                }
                String typeCode = mClothType.getVALUE();
                if (mMap_area.containsKey(typeCode)) {
                    List<DictionaryDataBo> list = mMap_area.get(typeCode);
                    showSelector(list, mTv_storageArea);
                } else {
                    showLoading();
                    HttpHelper.getStorAreaData(typeCode, this);
                }
                break;
            case R.id.tv_storageOut_setType:
                showSelector(mList_type, mTv_type);
                break;
            case R.id.btn_search:
                search();
                break;
            case R.id.tv_autoRefresh:
                if ("暂停刷新".equals(mTv_autoRefresh.getText().toString())) {
                    mTv_autoRefresh.setText("自动刷新");
                    mHandler.removeMessages(WHAT_INITDATA);
                } else {
                    mTv_autoRefresh.setText("暂停刷新");
                    mHandler.sendEmptyMessage(WHAT_INITDATA);
                }
                break;
        }
    }

    private <T> void showSelector(final List<T> list, final TextView tv_anchorView) {
        SelectorPopWindow ppw = new SelectorPopWindow<>(mContext, list, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果切换品类，库区需要重新选择
                mArea = null;
                mTv_storageArea.setText(null);

                T t = list.get(position);
                DictionaryDataBo item = (DictionaryDataBo) t;
                tv_anchorView.setText(item.getLABEL());

                if (tv_anchorView == mTv_type) {
                    mClothType = item;
                } else if (tv_anchorView == mTv_storageArea) {
                    mArea = item;
                }

                String area = mTv_storageArea.getText().toString();
                if (!isEmpty(area)) {
                    search();
                }
            }
        });
        ppw.setWidth(mTv_storageArea.getWidth());
        ppw.setHeight(ListPopupWindow.WRAP_CONTENT);
        ppw.showAsDropDown(tv_anchorView);
    }

    private void search() {
        if (mClothType == null || mArea == null) {
            ErrorDialog.showAlert(mContext, "请先选择库位和类型");
            return;
        }
        String shopOrder = mEt_shopOrder.getText().toString();
        String item = mEt_item.getText().toString();
        showLoading();
        HttpHelper.getWareHouseInfo(mClothType.getVALUE(), mArea.getVALUE(), shopOrder, item, StorageOutFragment.this);
    }

    private class ItemAdapter extends CommonAdapter<StorageOutBo> {

        ItemAdapter(Context context, List<StorageOutBo> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final StorageOutBo item, final int position) {
            holder.setText(R.id.tv_storageOut_shopOrder, item.getSHOP_ORDER());
            holder.setText(R.id.tv_storageOut_item, item.getITEM());
            holder.setText(R.id.tv_storageOut_workCenter, item.getWORK_CENTER());
            holder.setText(R.id.tv_storageOut_size, item.getSIZE());
            holder.setText(R.id.tv_storageOut_rfid, item.getRFID());
            holder.setText(R.id.tv_storageOut_area, item.getSTOR_AREA());
            holder.setText(R.id.tv_storageOut_location, item.getSTOR_LOCATION());
            holder.setText(R.id.tv_storageOut_inQTY, item.getSTOR_QUANTITY());
            holder.setText(R.id.tv_storageOut_outQTY, item.getOUT_QUANTITY() + "");
            holder.setText(R.id.tv_storageOut_lessQTY, item.getSURPLUS_QUANTITY() + "");

            holder.getView(R.id.btn_storageOut_out).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
                    if (positionUsers == null || positionUsers.size() == 0) {
                        showErrorDialog("请员工上岗在操作");
                        return;
                    }
                    mOutPosition = position;
                    showInputDialog();
                }
            });
        }
    }

    private StorageOutQTYDialog mStorageOutQTYDialog;

    private void showInputDialog() {
        final StorageOutBo outBo = mList_item.get(mOutPosition);
        mStorageOutQTYDialog = new StorageOutQTYDialog(mContext, outBo.getSURPLUS_QUANTITY(), new IntegerCallback() {
            @Override
            public void callback(int value) {
                List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
                if (positionUsers == null || positionUsers.size() == 0) {
                    showErrorDialog("请员工上岗在操作");
                    return;
                }
                outBo.setUSER_ID(positionUsers.get(0).getEMPLOYEE_NUMBER());
                mOutQTY = value;
                outBo.setQUANTITY(mOutQTY + "");
                outBo.setCLOTH_TYPE(mClothType.getVALUE());
                showLoading();
                HttpHelper.storageOut(outBo, StorageOutFragment.this);
            }
        });
        mStorageOutQTYDialog.show();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_INITDATA) {
                search();
            }
        }
    };

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getWareHouseInfo.equals(url)) {
                mList_item = JSON.parseArray(resultJSON.getJSONArray("result").toString(), StorageOutBo.class);
                if (mList_item != null) {
                    for (int i = mList_item.size() - 1; i >= 0; i--) {
                        StorageOutBo item = mList_item.get(i);
                        if ("Y".equals(item.getIS_OUT())) {
                            mList_item.remove(i);
                        }
                    }
                    mItemAdapter.notifyDataSetChanged(mList_item);
                    if (mList_item.size() == 0) {
                        showAlert("该品类在库区内无库存");
                    }
                    mHandler.sendEmptyMessageDelayed(WHAT_INITDATA, 60 * 1000);
                }
            } else if (HttpHelper.storageOut.equals(url)) {
                mStorageOutQTYDialog.dismiss();
                toast("出库成功");
                StorageOutBo outBo = mList_item.get(mOutPosition);
                int less = Integer.valueOf(outBo.getSURPLUS_QUANTITY()) - mOutQTY;
                if (less <= 0) {
                    mList_item.remove(mOutPosition);
                } else {
                    outBo.setSURPLUS_QUANTITY(less + "");
                    int outQTY = Integer.valueOf(outBo.getOUT_QUANTITY()) + mOutQTY;
                    outBo.setOUT_QUANTITY(outQTY + "");
                    mList_item.set(mOutPosition, outBo);
                }
                mItemAdapter.notifyDataSetChanged();
            } else if (HttpHelper.getClothType.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                mList_type = JSON.parseArray(result.getJSONArray("clothTypeoptions").toString(), DictionaryDataBo.class);
            } else if (HttpHelper.getStorAreaData.equals(url)) {
                List<DictionaryDataBo> list = JSON.parseArray(resultJSON.getJSONArray("result").toString(), DictionaryDataBo.class);
                mMap_area.put(mClothType.getVALUE(), list);
                showSelector(list, mTv_storageArea);
            }
        }
    }
}
