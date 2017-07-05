package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.bo.RecordLabuMaterialInfoBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录拉布数据弹框
 * Created by Lenovo on 2017/7/4.
 */

public class RecordLabuDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private View mView;
    private LinearLayout mLayout_materialImg;
    private LinearLayout mLayout_material;

    private ScrollView mScrollView_material;
    private List<RecordLabuMaterialInfoBo> mList_materialInfo;
    private OnRecordLabuCallback mRecordLabuCallback;

    public RecordLabuDialog(@NonNull Context context, OnRecordLabuCallback recordLabuCallback) {
        super(context);
        mRecordLabuCallback = recordLabuCallback;
        init(context);
    }

    public RecordLabuDialog(@NonNull Context context, List<RecordLabuMaterialInfoBo> list_materialInfo, OnRecordLabuCallback recordLabuCallback) {
        super(context);
        mList_materialInfo = list_materialInfo;
        mRecordLabuCallback = recordLabuCallback;
        init(context);
    }

    public RecordLabuDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected RecordLabuDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.dlg_record_labudata, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(mView);

        initView();
        initData();
    }

    private void initView() {
        mLayout_materialImg = (LinearLayout) mView.findViewById(R.id.layout_recordLabu_materialImg);
        mLayout_material = (LinearLayout) mView.findViewById(R.id.layout_recordLabu_material);
        mScrollView_material = (ScrollView) mView.findViewById(R.id.scrollView_recordLabu_material);

        mView.findViewById(R.id.btn_dlg_close).setOnClickListener(this);
        mView.findViewById(R.id.btn_recordLabu_save).setOnClickListener(this);
        mView.findViewById(R.id.btn_recordLabu_saveAndDone).setOnClickListener(this);
        View btn_addMaterial = mView.findViewById(R.id.btn_recordLabu_addMaterial);
        btn_addMaterial.setOnClickListener(this);

        mLayout_materialImg.addView(getMaterialsView(new TailorInfoBo.MatInfoBean(), 0));
        mLayout_materialImg.addView(getMaterialsView(new TailorInfoBo.MatInfoBean(), 1));
        mLayout_materialImg.addView(getMaterialsView(new TailorInfoBo.MatInfoBean(), 2));
    }

    private void initData() {
        if (mList_materialInfo != null) {
            for (RecordLabuMaterialInfoBo materialInfo : mList_materialInfo) {
                mLayout_material.addView(getMaterialInfoView(materialInfo), mLayout_material.getChildCount() - 1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dlg_close:
                dismiss();
                break;
            case R.id.btn_recordLabu_addMaterial:
                mLayout_material.addView(getMaterialInfoView(null), mLayout_material.getChildCount() - 1);
                mScrollView_material.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView_material.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 100);
                break;
            case R.id.btn_recordLabu_save:
                save(false);
                break;
            case R.id.btn_recordLabu_saveAndDone:
                saveAndDone();
                break;
        }
    }

    /**
     * 保存并完工
     */
    private void saveAndDone() {
        if (save(true)) {
            if (mRecordLabuCallback != null) {
                mRecordLabuCallback.recordLabuCallback(mList_materialInfo, true);
            }
            dismiss();
        }
    }

    /**
     * 保存
     *
     * @param withDone true=保存并完工，false=保存
     */
    private boolean save(boolean withDone) {
        mList_materialInfo = new ArrayList<>();
        int childCount = mLayout_material.getChildCount() - 1;
        for (int i = 0; i < childCount; i++) {
            View childView = mLayout_material.getChildAt(i);
            TextView tv_num = (TextView) childView.findViewById(R.id.tv_recordLabu_materialNum);
            EditText et_juanHao = (EditText) childView.findViewById(R.id.et_recordLabu_juanHao);
            EditText et_length = (EditText) childView.findViewById(R.id.et_recordLabu_length);
            EditText et_layers = (EditText) childView.findViewById(R.id.et_recordLabu_layers);
            EditText et_left = (EditText) childView.findViewById(R.id.et_recordLabu_left);
            EditText et_remark = (EditText) childView.findViewById(R.id.et_recordLabu_remark);

            String num = tv_num.getText().toString();
            String juanHap = et_juanHao.getText().toString();
            String layers = et_layers.getText().toString();
            String left = et_left.getText().toString();
            String length = et_length.getText().toString();

            if (!TextUtils.isEmpty(num) && !TextUtils.isEmpty(juanHap) && !TextUtils.isEmpty(layers) && !TextUtils.isEmpty(left) && !TextUtils.isEmpty(length)) {
                String remark = et_remark.getText().toString();
                RecordLabuMaterialInfoBo materialInfo = new RecordLabuMaterialInfoBo();
                materialInfo.setMaterialNum(num);
                materialInfo.setJuanHao(juanHap);
                materialInfo.setLayers(layers);
                materialInfo.setLength(length);
                materialInfo.setLeft(left);
                materialInfo.setRemark(remark);
                mList_materialInfo.add(materialInfo);
            } else {
                Toast.makeText(mContext, "数据未填写完整，无法保存", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!withDone) {
            if (mRecordLabuCallback != null) {
                mRecordLabuCallback.recordLabuCallback(mList_materialInfo, false);
            }
        }
        Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * 获取物料图布局
     *
     * @param item
     * @param position
     * @return
     */
    private View getMaterialsView(final TailorInfoBo.MatInfoBean item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_materials = (ImageView) view.findViewById(R.id.iv_materials);
        TextView tv_materials = (TextView) view.findViewById(R.id.tv_materials);
        Glide.with(mContext).load(item.getMAT_URL()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_materials);
        iv_materials.setTag(position);
        tv_materials.setText(item.getMAT_NO());
        iv_materials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urls = new ArrayList<>();
//                List<TailorInfoBo.TailorResultBean.ItemArrayBean> list = mTailorResult.getItemArray();
//                for (TailorInfoBo.TailorResultBean.ItemArrayBean item : list) {
//                    urls.add(item.getMaterialUrl());
//                }

                mContext.startActivity(ImageBrowserActivity.getIntent(mContext, urls, (Integer) v.getTag()));
            }
        });
        return view;
    }

    /**
     * 获取物料信息布局
     *
     * @return
     */
    private View getMaterialInfoView(RecordLabuMaterialInfoBo materialInfo) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_recordlabu_materialinfo, null);
        TextView tv_num = (TextView) view.findViewById(R.id.tv_recordLabu_materialNum);
        if (materialInfo != null) {
            EditText et_juanHao = (EditText) view.findViewById(R.id.et_recordLabu_juanHao);
            EditText et_length = (EditText) view.findViewById(R.id.et_recordLabu_length);
            EditText et_layers = (EditText) view.findViewById(R.id.et_recordLabu_layers);
            EditText et_left = (EditText) view.findViewById(R.id.et_recordLabu_left);
            EditText et_remark = (EditText) view.findViewById(R.id.et_recordLabu_remark);

            tv_num.setText(materialInfo.getMaterialNum());
            et_juanHao.setText(materialInfo.getJuanHao());
            et_layers.setText(materialInfo.getLayers());
            et_length.setText(materialInfo.getLength());
            et_left.setText(materialInfo.getLeft());
            et_remark.setText(materialInfo.getRemark());
        }

        tv_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(v);
            }
        });

        view.findViewById(R.id.btn_recordLabu_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout_material.removeView(view);
            }
        });
        return view;
    }


    private void showPopWindow(final View v) {
        final List<String> list = new ArrayList<>();
        list.add("M1111111");
        list.add("M2222222");
        list.add("M3333333");
        list.add("M4444444");
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
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.9), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }


    public interface OnRecordLabuCallback {
        void recordLabuCallback(List<RecordLabuMaterialInfoBo> list_materialInfo, boolean done);
    }
}
