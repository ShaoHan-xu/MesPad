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
import com.eeka.mespad.bo.BTReasonBo;
import com.eeka.mespad.bo.SewReturnMatBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 缝制段退补料申请弹框
 * Created by Lenovo on 2017/11/1.
 */

public class SewReturnMatDialog extends Dialog implements View.OnClickListener, HttpCallback {

    public static final int TYPE_RETURN = 3;//退料
    public static final int TYPE_ADD = 2;//补料

    private Context mContext;
    private View mView;
    private TextView mTv_type, mTv_code;
    private EditText mEt_sfc;
    private LinearLayout mLayout_matInfo;

    private String mSFC;
    private String mShopOrder;
    private int mType;
    private List<BTReasonBo> mList_reason;
    private BTReasonBo mBTReason;

    private List<SewReturnMatBo.BomInfoBean> mList_items;

    public SewReturnMatDialog(@NonNull Context context, String sfc) {
        super(context);
        mSFC = sfc;
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_return_sew, null);
        setContentView(mView);
        setCanceledOnTouchOutside(false);

        initView();

        mType = TYPE_RETURN;
        mList_reason = SpUtil.getBTReason(mType);
        if (mList_reason == null || mList_reason.size() == 0) {
            LoadingDialog.show(mContext);
            HttpHelper.getBTReason(mType, this);
        }
    }

    private void initView() {
        mTv_type = (TextView) mView.findViewById(R.id.tv_returnSew_type);
        mTv_code = (TextView) mView.findViewById(R.id.tv_returnSew_code);
        mEt_sfc = (EditText) mView.findViewById(R.id.et_returnSew_sfc);
        mLayout_matInfo = (LinearLayout) mView.findViewById(R.id.layout_returnSew_matInfo);
        mEt_sfc.setText(mSFC);

        mTv_type.setOnClickListener(this);
        mTv_code.setOnClickListener(this);
        mView.findViewById(R.id.btn_done).setOnClickListener(this);
        mView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mView.findViewById(R.id.btn_returnSew_search).setOnClickListener(this);

        if (!TextUtils.isEmpty(mSFC))
            search();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_returnSew_type:
                List<String> list = new ArrayList<>();
                list.add("退料");
                list.add("补料");
                showPopWindow(mTv_type, list);
                break;
            case R.id.tv_returnSew_code:
                showPopWindow(mTv_code, mList_reason);
                break;
            case R.id.btn_returnSew_search:
                SystemUtils.hideKeyboard(mContext, v);
                search();
                break;
            case R.id.btn_done:
                if (mList_items == null || mList_items.size() == 0) {
                    ErrorDialog.showAlert(mContext, "没有bom数据，无法保存");
                    return;
                }
                if (mBTReason == null) {
                    ErrorDialog.showAlert(mContext, "请选择原因");
                    return;
                }
                save();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    private void search() {
        String sfc = mEt_sfc.getText().toString().trim();
        if (TextUtils.isEmpty(sfc)) {
            Toast.makeText(mContext, "请输入工单号进行查询操作", Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.show(mContext);
        HttpHelper.getBomInfo(sfc, this);
    }

    private void save() {
        boolean b = generateData();
        if (!b) {
            Toast.makeText(mContext, "请输入数量后再保存", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject params = new JSONObject();
        params.put("TYPE", mType);
        params.put("SHOP_ORDER", mShopOrder);
        params.put("ITEM_INFOS", JSON.toJSONString(mList_items));

        LoadingDialog.show(mContext);
        HttpHelper.saveMatReturnOrFeeding(params, this);
    }

    private boolean generateData() {
        boolean flag = false;
        int childCount = mLayout_matInfo.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = mLayout_matInfo.getChildAt(i);

            TextView tv_matNo = (TextView) childView.findViewById(R.id.tv_matInfo_matNo);
            EditText et_qty = (EditText) childView.findViewById(R.id.et_matInfo_matQty);
            String qty = et_qty.getText().toString();
            if (TextUtils.isEmpty(qty)) {
                continue;
            }
            flag = true;
            SewReturnMatBo.BomInfoBean mat = mList_items.get(i);
            mat.setITEM(tv_matNo.getText().toString());
            mat.setQTY(qty);
            mat.setREASON_CODE(mBTReason.getREASON_CODE());
            mList_items.set(i, mat);
        }
        return flag;
    }

    private <T> void showPopWindow(View v, final List<T> data) {
        SelectorPopWindow ppw = new SelectorPopWindow<>(mContext, data, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                T t = data.get(position);
                if (t instanceof BTReasonBo) {
                    mBTReason = (BTReasonBo) t;
                    mTv_code.setText(mBTReason.getREASON_DESC());
                } else if (t instanceof String) {
                    String type = (String) t;
                    mTv_type.setText(type);
                    if ("退料".equals(type)) {
                        mType = TYPE_RETURN;
                    } else {
                        mType = TYPE_ADD;
                    }
                    mList_reason = SpUtil.getBTReason(mType);
                    if (mList_reason == null || mList_reason.size() == 0) {
                        LoadingDialog.show(mContext);
                        HttpHelper.getBTReason(mType, SewReturnMatDialog.this);
                    }
                }
            }
        });
        ppw.show(v, (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }

    private void refreshMatInfo() {
        mLayout_matInfo.removeAllViews();
        for (SewReturnMatBo.BomInfoBean item : mList_items) {
            mLayout_matInfo.addView(getMatInfoView(item));
        }
    }

    /**
     * 获取物料信息布局
     */
    private View getMatInfoView(@NonNull SewReturnMatBo.BomInfoBean item) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_returnsew_matinfo, null);
        TextView tv_num = (TextView) view.findViewById(R.id.tv_matInfo_matNo);
        TextView tv_desc = (TextView) view.findViewById(R.id.tv_matInfo_matDesc);
        TextView tv_unit = (TextView) view.findViewById(R.id.tv_matInfo_matUnit);
        EditText et_qty = (EditText) view.findViewById(R.id.et_matInfo_matQty);

        tv_num.setText(item.getITEM());
        tv_desc.setText(item.getDESCRIPTION());
        et_qty.setText(item.getQTY());
        tv_unit.setText(item.getUNIT_OF_MEASURE());
        return view;
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.8), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getBomInfo.equals(url)) {
                String resultStr = HttpHelper.getResultStr(resultJSON);
                if (!TextUtils.isEmpty(resultStr)) {
                    SewReturnMatBo returnMatBo = JSON.parseObject(resultStr, SewReturnMatBo.class);
                    if (returnMatBo != null && returnMatBo.getBomInfo() != null) {
                        mShopOrder = returnMatBo.getShopOrder();
                        mList_items = returnMatBo.getBomInfo();
                        refreshMatInfo();
                    }
                }
            } else if (HttpHelper.cutMaterialReturnOrFeeding.equals(url)) {
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                dismiss();
            } else if (HttpHelper.getBTReason.equals(url)) {
                String resultStr = HttpHelper.getResultStr(resultJSON);
                if (!TextUtils.isEmpty(resultStr)) {
                    String jsonArray = resultJSON.getJSONObject("result").getJSONArray("REASONS").toJSONString();
                    SpUtil.saveBTReasons(mType, jsonArray);
                    mList_reason = JSON.parseArray(jsonArray, BTReasonBo.class);
                }
            }
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
}
