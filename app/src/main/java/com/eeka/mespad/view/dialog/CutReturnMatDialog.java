package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
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
import com.eeka.mespad.R;
import com.eeka.mespad.bo.BTReasonBo;
import com.eeka.mespad.bo.DictionaryDataBo;
import com.eeka.mespad.bo.ReturnMaterialInfoBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 裁剪段退/补料弹框
 * Created by Lenovo on 2017/7/4.
 */

public class CutReturnMatDialog extends Dialog implements View.OnClickListener, HttpCallback {

    public static final int TYPE_RETURN = 3;//退料
    public static final int TYPE_ADD = 2;//补料

    private Context mContext;
    private View mView;

    private int mType;
    private ReturnMaterialInfoBo mReturnMaterialInfo;
    private List<BTReasonBo> mList_reason;

    private LinearLayout mLayout_material;

    private boolean isSubmit;

    public CutReturnMatDialog(@NonNull Context context, int type, @NonNull ReturnMaterialInfoBo returnMaterialInfo) {
        super(context);
        mType = type;
        mReturnMaterialInfo = returnMaterialInfo;
        mReturnMaterialInfo.setType(type);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_return_cut, null);
        setContentView(mView);
        setCanceledOnTouchOutside(false);

        initView();
        String code = null;
        if (mType == TYPE_ADD) {
            code = DictionaryDataBo.CODE_BlReason;
        } else if (mType == TYPE_RETURN) {
            code = DictionaryDataBo.CODE_TlReason;
        }
        List<DictionaryDataBo> list = SpUtil.getDictionaryData(code);
        if (list == null || list.size() == 0) {
            LoadingDialog.show(mContext);
            HttpHelper.getDictionaryData(code, this);
        } else {
            initData(list);
        }
    }

    private void initView() {
        TextView tv_title = mView.findViewById(R.id.tv_title);
        if (mType == TYPE_RETURN) {
            tv_title.setText("退料申请");
        } else {
            tv_title.setText("补料申请");
        }

        mLayout_material = mView.findViewById(R.id.layout_returnMaterial_material);

        Button mBtn_save = mView.findViewById(R.id.btn_returnMaterial_save);
        Button mBtn_submit = mView.findViewById(R.id.btn_returnMaterial_submit);
        mBtn_save.setOnClickListener(this);
        mBtn_submit.setOnClickListener(this);
        mView.findViewById(R.id.btn_dlg_close).setOnClickListener(this);

        TextView tv_orderNum = mView.findViewById(R.id.tv_returnMaterial_orderNum);
        tv_orderNum.setText(mReturnMaterialInfo.getOrderNum());

        mLayout_material.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //获取View可见区域的bottom
                Rect rect = new Rect();
                mLayout_material.getWindowVisibleDisplayFrame(rect);
                if (bottom != 0 && oldBottom != 0 && bottom - rect.bottom <= 0) {
                    mView.findViewById(R.id.spaceView).setVisibility(View.VISIBLE);
                    Logger.d("隐藏");
                } else {
                    mView.findViewById(R.id.spaceView).setVisibility(View.GONE);
                    Logger.d("弹出");
                }
            }
        });
    }

    private void initData(List<DictionaryDataBo> list) {
        mList_reason = new ArrayList<>();
        for (DictionaryDataBo dic : list) {
            BTReasonBo reasonBo = new BTReasonBo();
            reasonBo.setREASON_CODE(dic.getVALUE());
            reasonBo.setREASON_DESC(dic.getLABEL());
            mList_reason.add(reasonBo);
        }
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
        isSubmit = false;
        generateData();
    }

    private void submit() {
        isSubmit = true;
        List<ReturnMaterialInfoBo.MaterialInfoBo> infoBos = generateData();
        if (infoBos == null) {
            return;
        }
        if (infoBos.size() == 0) {
            Toast.makeText(mContext, "请选择要退补的物料并输入数量", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject params = new JSONObject();
        params.put("TYPE", mType);
        params.put("SHOP_ORDER", mReturnMaterialInfo.getOrderNum());
        params.put("ITEM_INFOS", JSON.toJSONString(infoBos));

        LoadingDialog.show(mContext);
        HttpHelper.saveMatReturnOrFeeding(params, this);
    }

    private List<ReturnMaterialInfoBo.MaterialInfoBo> generateData() {
        List<ReturnMaterialInfoBo.MaterialInfoBo> list = new ArrayList<>();
        List<ReturnMaterialInfoBo.MaterialInfoBo> list_materialInfo = mReturnMaterialInfo.getMaterialInfoList();
        int childCount = mLayout_material.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = mLayout_material.getChildAt(i);

            EditText et_value = childView.findViewById(R.id.et_returnMaterial_value);
            TextView tv_reason = childView.findViewById(R.id.tv_returnMaterial_reason);

            String reason = tv_reason.getText().toString();
            String value = et_value.getText().toString();
            if (TextUtils.isEmpty(reason) && TextUtils.isEmpty(value)) {
                continue;
            } else if (TextUtils.isEmpty(reason)) {
                Toast.makeText(mContext, "请选择退补料原因", Toast.LENGTH_SHORT).show();
                return null;
            } else if (TextUtils.isEmpty(value)) {
                Toast.makeText(mContext, "请输入退补料数量", Toast.LENGTH_SHORT).show();
                return null;
            }

            ReturnMaterialInfoBo.MaterialInfoBo material = list_materialInfo.get(i);
            material.setReason(reason);
            material.setQTY(value);
            if (isSubmit) {
                list.add(material);
            } else {
                list_materialInfo.set(i, material);
            }
        }
        return list;
    }

    /**
     * 获取物料信息布局
     */
    private View getMaterialInfoView(@NonNull ReturnMaterialInfoBo.MaterialInfoBo materialInfo, final int position) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_return_material, null);
        ImageView iv_materialImg = view.findViewById(R.id.iv_returnMaterial_img);
        TextView tv_num = view.findViewById(R.id.tv_returnMaterial_materialNum);
        TextView tv_unit = view.findViewById(R.id.tv_returnMaterial_unit);
        TextView tv_valueSubTitle = view.findViewById(R.id.tv_returnMaterial_value_subTitle);
        TextView tv_reasonSubTitle = view.findViewById(R.id.tv_returnMaterial_reason_subTitle);
        if (mType == TYPE_RETURN) {
            tv_valueSubTitle.setText("退料");
            tv_reasonSubTitle.setText("退料原因");
        } else {
            tv_valueSubTitle.setText("补料");
            tv_reasonSubTitle.setText("补料原因");
        }

        String unit = materialInfo.getUNIT_LABEL();
        tv_unit.setText(unit);
        if (!"厘米".equals(unit)) {
            tv_unit.setTextColor(mContext.getResources().getColor(R.color.text_red_default));
        }
        tv_num.setText(materialInfo.getITEM());
        EditText et_value = view.findViewById(R.id.et_returnMaterial_value);
        Picasso.with(mContext).load(materialInfo.getPicUrl()).into(iv_materialImg);
        TextView tv_reason = view.findViewById(R.id.tv_returnMaterial_reason);

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
        ppw.show(v, (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
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
            } else if (HttpHelper.getDictionaryData.equals(url)) {
                String jsonArray = resultJSON.getJSONArray("result").toString();
                if (mType == 2) {
                    SpUtil.saveDictionaryData(DictionaryDataBo.CODE_BlReason, jsonArray);
                } else if (mType == 3) {
                    SpUtil.saveDictionaryData(DictionaryDataBo.CODE_TlReason, jsonArray);
                }
                List<DictionaryDataBo> list = JSON.parseArray(jsonArray, DictionaryDataBo.class);
                initData(list);
            }
        } else {
            ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        LoadingDialog.dismiss();
        ErrorDialog.showAlert(mContext, message);
    }

}
