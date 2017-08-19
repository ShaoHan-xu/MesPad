package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.BTReasonBo;
import com.eeka.mespad.bo.ReturnMaterialInfoBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

/**
 * 退/补料弹框
 * Created by Lenovo on 2017/7/4.
 */

public class ReturnMaterialDialog extends Dialog implements View.OnClickListener, HttpCallback {

    public static final int TYPE_RETURN = 3;//退料
    public static final int TYPE_ADD = 2;//补料

    private Context mContext;
    private View mView;
    private Button mBtn_save, mBtn_submit;

    private int mType;
    private ReturnMaterialInfoBo mReturnMaterialInfo;
    private List<BTReasonBo> mList_reason;

    private LinearLayout mLayout_material;

    public ReturnMaterialDialog(@NonNull Context context, int type, @NonNull ReturnMaterialInfoBo returnMaterialInfo) {
        super(context);
        mType = type;
        mReturnMaterialInfo = returnMaterialInfo;
        mReturnMaterialInfo.setType(type);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_return_material, null);
        setContentView(mView);

        initView();

        mList_reason = SpUtil.getBTReason(mType);
        if (mList_reason == null || mList_reason.size() == 0) {
            LoadingDialog.show(mContext);
            HttpHelper.getBTReason(mType, this);
        } else {
            initData();
        }
    }

    private void initView() {
        TextView tv_title = (TextView) mView.findViewById(R.id.tv_dlg_title);
        if (mType == TYPE_RETURN) {
            tv_title.setText("退料申请");
        } else {
            tv_title.setText("补料申请");
        }

        mLayout_material = (LinearLayout) mView.findViewById(R.id.layout_returnMaterial_material);

        mBtn_save = (Button) mView.findViewById(R.id.btn_returnMaterial_save);
        mBtn_submit = (Button) mView.findViewById(R.id.btn_returnMaterial_submit);
        mBtn_save.setOnClickListener(this);
        mBtn_submit.setOnClickListener(this);
        mView.findViewById(R.id.btn_dlg_close).setOnClickListener(this);

        TextView tv_orderNum = (TextView) mView.findViewById(R.id.tv_returnMaterial_orderNum);
        tv_orderNum.setText(mReturnMaterialInfo.getOrderNum());
    }

    private void initData() {
        List<ReturnMaterialInfoBo.MaterialInfoBo> infoList = mReturnMaterialInfo.getMaterialInfoList();
        for (int i = 0; i < infoList.size(); i++) {
            ReturnMaterialInfoBo.MaterialInfoBo material = infoList.get(i);
            mLayout_material.addView(getMaterialInfoView(material, i));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dlg_close:
                dismiss();
                break;
            case R.id.btn_returnMaterial_save:
                save();
                break;
            case R.id.btn_returnMaterial_submit:
                submit();
                break;
        }
    }

    private void save() {
        generateData();
    }

    private void submit() {
        generateData();

        JSONObject params = new JSONObject();
        params.put("TYPE", mType);
        params.put("SHOP_ORDER", mReturnMaterialInfo.getOrderNum());
        params.put("ITEM_INFOS", JSON.toJSONString(mReturnMaterialInfo.getMaterialInfoList()));

        LoadingDialog.show(mContext);
        HttpHelper.cutMaterialReturnOrFeeding(params, this);
    }

    private void generateData() {
        List<ReturnMaterialInfoBo.MaterialInfoBo> list_materialInfo = mReturnMaterialInfo.getMaterialInfoList();
        int childCount = mLayout_material.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = mLayout_material.getChildAt(i);

            EditText et_value = (EditText) childView.findViewById(R.id.et_returnMaterial_value);
            TextView tv_reason = (TextView) childView.findViewById(R.id.tv_returnMaterial_reason);

            String reason = tv_reason.getText().toString();
            String value = et_value.getText().toString();

            ReturnMaterialInfoBo.MaterialInfoBo material = list_materialInfo.get(i);
            material.setReason(reason);
            material.setQTY(value);
            list_materialInfo.set(i, material);
        }

    }

    /**
     * 获取物料信息布局
     */
    private View getMaterialInfoView(@NonNull ReturnMaterialInfoBo.MaterialInfoBo materialInfo, final int position) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_return_material, null);
        ImageView iv_materialImg = (ImageView) view.findViewById(R.id.iv_returnMaterial_img);
        TextView tv_num = (TextView) view.findViewById(R.id.tv_returnMaterial_materialNum);
        TextView tv_valueSubTitle = (TextView) view.findViewById(R.id.tv_returnMaterial_value_subTitle);
        TextView tv_reasonSubTitle = (TextView) view.findViewById(R.id.tv_returnMaterial_reason_subTitle);
        if (mType == TYPE_RETURN) {
            tv_valueSubTitle.setText("退料/米");
            tv_reasonSubTitle.setText("退料原因");
        } else {
            tv_valueSubTitle.setText("补料/米");
            tv_reasonSubTitle.setText("补料原因");
        }

        tv_num.setText(materialInfo.getITEM());
        EditText et_value = (EditText) view.findViewById(R.id.et_returnMaterial_value);
        Glide.with(mContext).load(materialInfo.getPicUrl()).into(iv_materialImg);
        TextView tv_reason = (TextView) view.findViewById(R.id.tv_returnMaterial_reason);

        tv_reason.setText(materialInfo.getReason());
        et_value.setText(materialInfo.getQTY());

        tv_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(v, position);
            }
        });
        return view;
    }

    private void showPopWindow(final View v, final int index) {
        SelectorPopWindow<BTReasonBo> ppw = new SelectorPopWindow<>(mContext, mList_reason, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BTReasonBo reasonBo = mList_reason.get(position);
                ReturnMaterialInfoBo.MaterialInfoBo infoBo = mReturnMaterialInfo.getMaterialInfoList().get(index);
                infoBo.setReason(reasonBo.getREASON_DESC());
                infoBo.setREASON_CODE(reasonBo.getREASON_CODE());
                ((TextView) v).setText(reasonBo.getREASON_DESC());
            }
        });
        ppw.setWidth(v.getWidth());
        ppw.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        ppw.showAsDropDown(v);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.8), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        LoadingDialog.dismiss();
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.cutMaterialReturnOrFeeding.equals(url)) {
                Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                dismiss();
            } else if (HttpHelper.getBTReason.equals(url)) {
                String resultStr = HttpHelper.getResultStr(resultJSON);
                if (!TextUtils.isEmpty(resultStr)) {
                    String jsonArray = resultJSON.getJSONObject("result").getJSONArray("REASONS").toJSONString();
                    SpUtil.saveBTReasons(mType, jsonArray);
                    mList_reason = JSON.parseArray(jsonArray, BTReasonBo.class);
                    initData();
                }
            }
        } else {
            ErrorDialog.showDialog(mContext, resultJSON.getString("message"));
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        LoadingDialog.dismiss();
        ErrorDialog.showDialog(mContext, message);
    }

    public interface OnReturnMaterialCallback {
        void returnMaterialCallback(List<ReturnMaterialInfoBo.MaterialInfoBo> data);
    }

}
