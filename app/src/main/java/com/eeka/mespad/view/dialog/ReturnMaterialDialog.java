package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.ReturnMaterialInfoBo;
import com.eeka.mespad.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 退/补料弹框
 * Created by Lenovo on 2017/7/4.
 */

public class ReturnMaterialDialog extends Dialog implements View.OnClickListener {

    public static final int TYPE_RETURN = 0;//退料
    public static final int TYPE_ADD = 1;//补料

    private Context mContext;
    private View mView;
    private Button mBtn_save, mBtn_submit;

    private int mType;
    private ReturnMaterialInfoBo mReturnMaterialInfo;

    private LinearLayout mLayout_material;

    private OnReturnMaterialCallback mCallback;

    public ReturnMaterialDialog(@NonNull Context context, int type, @NonNull ReturnMaterialInfoBo returnMaterialInfo) {
        super(context);
        mType = type;
        mReturnMaterialInfo = returnMaterialInfo;
        mReturnMaterialInfo.setType(type);
//        mCallback = callback;
        init(context);
    }

    public ReturnMaterialDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected ReturnMaterialDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_return_material, null);
        setContentView(mView);

        initView();
        initData();
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
        for (ReturnMaterialInfoBo.MaterialInfoBo material : infoList) {
            mLayout_material.addView(getMaterialInfoView(material));
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
    }

    private void generateData() {
        List<ReturnMaterialInfoBo.MaterialInfoBo> list_materialInfo = mReturnMaterialInfo.getMaterialInfoList();
        int childCount = mLayout_material.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = mLayout_material.getChildAt(i);

            EditText et_planUse = (EditText) childView.findViewById(R.id.et_returnMaterial_planUse);
            EditText et_realUse = (EditText) childView.findViewById(R.id.et_returnMaterial_realUse);
            EditText et_value = (EditText) childView.findViewById(R.id.et_returnMaterial_value);
            TextView tv_reason = (TextView) childView.findViewById(R.id.tv_returnMaterial_reason);

            String reason = tv_reason.getText().toString();
            String planUse = et_planUse.getText().toString();
            String realUse = et_realUse.getText().toString();
            String value = et_value.getText().toString();

            ReturnMaterialInfoBo.MaterialInfoBo material = list_materialInfo.get(i);
            material.setReason(reason);
            material.setPlanUse(planUse);
            material.setRealUse(realUse);
            material.setValue(value);
            list_materialInfo.set(i, material);
        }
    }

    /**
     * 获取物料信息布局
     *
     * @return
     */
    private View getMaterialInfoView(@NonNull ReturnMaterialInfoBo.MaterialInfoBo materialInfo) {
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

        tv_num.setText(materialInfo.getNum());
        EditText et_planUse = (EditText) view.findViewById(R.id.et_returnMaterial_planUse);
        EditText et_realUse = (EditText) view.findViewById(R.id.et_returnMaterial_realUse);
        EditText et_value = (EditText) view.findViewById(R.id.et_returnMaterial_value);
        Glide.with(mContext).load(materialInfo.getPicUrl()).into(iv_materialImg);
        TextView tv_reason = (TextView) view.findViewById(R.id.tv_returnMaterial_reason);

        tv_reason.setText(materialInfo.getReason());
        et_planUse.setText(materialInfo.getPlanUse());
        et_realUse.setText(materialInfo.getRealUse());
        et_value.setText(materialInfo.getValue());

        tv_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(v);
            }
        });
        return view;
    }

    private void showPopWindow(final View v) {
        final List<String> list = new ArrayList<>();
        list.add("品质问题");
        list.add("颜色差异");
        list.add("尺寸大小差异");
        list.add("面料破损");
        SelectorPopWindow<String> ppw = new SelectorPopWindow<>(mContext, list, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) v).setText(list.get(position));
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

    public interface OnReturnMaterialCallback {
        void returnMaterialCallback(List<ReturnMaterialInfoBo.MaterialInfoBo> data);
    }

}
