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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UpdateLabuBo;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private List<UpdateLabuBo.MatItem> mList_matItem;
    private List<TailorInfoBo.MatInfoBean> mList_matInfo;
    private OnRecordLabuCallback mRecordLabuCallback;
    private TailorInfoBo mTailorInfo;//主数据

    private UpdateLabuBo mLabuData;//拉布主数据
    private boolean isCustom;

    public RecordLabuDialog(@NonNull Context context, @NonNull TailorInfoBo tailorInfo, UpdateLabuBo labuData, String orderType, @NonNull OnRecordLabuCallback recordLabuCallback) {
        super(context);
        mTailorInfo = tailorInfo;
        mLabuData = labuData;
        mRecordLabuCallback = recordLabuCallback;
        if ("S".equals(orderType)) {
            isCustom = true;
        }
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.dlg_record_labudata, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(mView);
        setCanceledOnTouchOutside(false);

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
    }

    private void initData() {
        if (mTailorInfo == null) {
            Toast.makeText(mContext, "数据错误", Toast.LENGTH_SHORT).show();
            return;
        }
        mList_matInfo = mTailorInfo.getMAT_INFOR();
        if (mLabuData != null) {
            List<UpdateLabuBo.Layouts> layouts = mLabuData.getLAYOUTS();
            for (UpdateLabuBo.Layouts layout : layouts) {
                if (mList_matItem == null)
                    mList_matItem = new ArrayList<>();
                mList_matItem.addAll(layout.getDETAILS());
            }
        }
        if (mList_matInfo != null) {
            for (int i = 0; i < mList_matInfo.size(); i++) {
                TailorInfoBo.MatInfoBean matInfo = mList_matInfo.get(i);
                mLayout_materialImg.addView(getMaterialsView(matInfo, i));
            }

            if (mList_matItem == null) {
                mList_matItem = new ArrayList<>();
                for (int i = 0; i < mList_matInfo.size(); i++) {
                    TailorInfoBo.MatInfoBean matInfo = mList_matInfo.get(i);
                    UpdateLabuBo.MatItem matItem = new UpdateLabuBo.MatItem();
                    matItem.setMAT_NO(matInfo.getMAT_NO());
                    if (isCustom) {
                        matItem.setLAYERS("1");
                    } else {
                        matItem.setLAYERS(matInfo.getLAYERS() + "");
                    }
                    mList_matItem.add(matItem);
                }
            }

            for (UpdateLabuBo.MatItem materialInfo : mList_matItem) {
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
                save(true);
                break;
        }
    }

    /**
     * 保存
     *
     * @param withDone true=保存并完工，false=保存
     */
    private void save(boolean withDone) {
        mList_matItem = new ArrayList<>();
        Map<String, Integer> layersMap = new HashMap<>();
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
            String juanHao = et_juanHao.getText().toString();
            String layers = et_layers.getText().toString();
            String left = et_left.getText().toString();
            String length = et_length.getText().toString();

            if (!TextUtils.isEmpty(num) && !TextUtils.isEmpty(juanHao) && !TextUtils.isEmpty(layers) && !TextUtils.isEmpty(left) && !TextUtils.isEmpty(length)) {
                if ("0".equals(layers)) {
                    Toast.makeText(mContext, "物料 " + num + " 拉布层数不能为0", Toast.LENGTH_SHORT).show();
                    return;
                }
                String remark = et_remark.getText().toString();
                UpdateLabuBo.MatItem matItem = new UpdateLabuBo.MatItem();
                matItem.setMAT_NO(num);
                matItem.setVOLUME(juanHao);
                matItem.setLAYERS(layers);
                matItem.setLENGTH(length);
                matItem.setODDMENTS(left);
                matItem.setREMARK(remark);

                Integer itemLayers = Integer.valueOf(layers);
                for (TailorInfoBo.MatInfoBean info : mList_matInfo) {
                    if (num.equals(info.getMAT_NO())) {
                        if (itemLayers > info.getLAYERS()) {
                            Toast.makeText(mContext, "物料 " + num + " 超额拉布", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        matItem.setITEM_BO(info.getITEM_BO());
                    }
                }

                if (!withDone) {
                    matItem.setEditEnable(false);
                }
                mList_matItem.add(matItem);

                if (layersMap.containsKey(num)) {
                    Integer lay = layersMap.get(num);
                    lay += itemLayers;
                    layersMap.put(num, lay);
                } else {
                    layersMap.put(num, itemLayers);
                }
            } else {
                Toast.makeText(mContext, "物料 " + num + " 数据未填写完整，无法保存", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        for (TailorInfoBo.MatInfoBean info : mList_matInfo) {
            String matNo = info.getMAT_NO();
            if (layersMap.containsKey(matNo)) {
                Integer itemLayers = layersMap.get(matNo);
                if (itemLayers > info.getLAYERS()) {
                    Toast.makeText(mContext, "物料 " + matNo + " 超额拉布", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        Map<String, List<UpdateLabuBo.Layouts>> layoutMap = new HashMap<>();
        for (UpdateLabuBo.MatItem matItem : mList_matItem) {
            String matNo = matItem.getMAT_NO();
            if (layoutMap.containsKey(matNo)) {
                List<UpdateLabuBo.Layouts> layoutList = layoutMap.get(matNo);
                for (UpdateLabuBo.Layouts layout : layoutList) {
                    if (matNo.equals(layout.getMAT_NO())) {
                        List<UpdateLabuBo.MatItem> details = layout.getDETAILS();
                        details.add(matItem);
                    }
                }
            } else {
                List<UpdateLabuBo.Layouts> layoutList = new ArrayList<>();
                UpdateLabuBo.Layouts layout = new UpdateLabuBo.Layouts();
                List<UpdateLabuBo.MatItem> list = new ArrayList<>();
                layout.setMAT_NO(matNo);
                layout.setPLAN_LAYERS(matItem.getLAYERS());
                for (TailorInfoBo.MatInfoBean info : mList_matInfo) {
                    if (info.getMAT_NO().equals(matNo)) {
                        layout.setZ_LAYOUT_BO(info.getZ_LAYOUT_BO());
                        list.add(matItem);
                    }
                }
                layout.setDETAILS(list);
                layoutList.add(layout);
                layoutMap.put(matNo, layoutList);
            }
        }

        List<UpdateLabuBo.Layouts> layoutList = new ArrayList<>();
        Set<String> strings = layoutMap.keySet();
        for (String key : strings) {
            layoutList.addAll(layoutMap.get(key));
        }

        mLabuData = new UpdateLabuBo();
        mLabuData.setLAYOUTS(layoutList);
        TailorInfoBo.SHOPORDERINFORBean orderInfo = mTailorInfo.getSHOP_ORDER_INFOR();
        mLabuData.setSHOP_ORDER_BO(orderInfo.getSHOP_ORDER_BO());
        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        if (resource != null)
            mLabuData.setRESOURCE_BO(resource.getRESOURCE_BO());
        List<String> opList = new ArrayList<>();
        for (TailorInfoBo.OPERINFORBean oper : mTailorInfo.getOPER_INFOR()) {
            opList.add(oper.getOPERATION_BO());
        }
        mLabuData.setOPERATIONS(opList);
        mRecordLabuCallback.recordLabuCallback(mLabuData, withDone);
    }

    /**
     * 获取物料图布局
     */
    private View getMaterialsView(final TailorInfoBo.MatInfoBean item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material, null);
        view.setTag(position);
        ImageView iv_materials = (ImageView) view.findViewById(R.id.iv_materials);
        TextView tv_materials = (TextView) view.findViewById(R.id.tv_matNum);
        Glide.with(mContext).load(item.getMAT_URL()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_materials);
        iv_materials.setTag(position);
        tv_materials.setText(item.getMAT_NO());
        iv_materials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urls = new ArrayList<>();
                for (TailorInfoBo.MatInfoBean item : mList_matInfo) {
                    urls.add(item.getMAT_URL());
                }

                mContext.startActivity(ImageBrowserActivity.getIntent(mContext, urls, (Integer) v.getTag()));
            }
        });
        return view;
    }

    /**
     * 获取物料信息布局
     */
    private View getMaterialInfoView(UpdateLabuBo.MatItem materialInfo) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_recordlabu_materialinfo, null);
        TextView tv_num = (TextView) view.findViewById(R.id.tv_recordLabu_materialNum);
        Button btn_del = (Button) view.findViewById(R.id.btn_recordLabu_del);
        if (materialInfo != null) {
            EditText et_juanHao = (EditText) view.findViewById(R.id.et_recordLabu_juanHao);
            EditText et_length = (EditText) view.findViewById(R.id.et_recordLabu_length);
            EditText et_layers = (EditText) view.findViewById(R.id.et_recordLabu_layers);
            EditText et_left = (EditText) view.findViewById(R.id.et_recordLabu_left);
            EditText et_remark = (EditText) view.findViewById(R.id.et_recordLabu_remark);

            if (!materialInfo.isEditEnable()) {
                tv_num.setBackgroundResource(0);
                et_juanHao.setBackgroundResource(0);
                et_length.setBackgroundResource(0);
                et_layers.setBackgroundResource(0);
                et_left.setBackgroundResource(0);
                et_remark.setBackgroundResource(0);

                tv_num.setEnabled(false);
                et_juanHao.setEnabled(false);
                et_length.setEnabled(false);
                et_layers.setEnabled(false);
                et_left.setEnabled(false);
                et_remark.setEnabled(false);
                btn_del.setVisibility(View.INVISIBLE);
            } else {
                if (isCustom) {
                    et_layers.setEnabled(false);
                    et_layers.setBackgroundResource(0);
                }
            }

            tv_num.setText(materialInfo.getMAT_NO());
            et_juanHao.setText(materialInfo.getVOLUME());
            et_layers.setText(materialInfo.getLAYERS());
            et_length.setText(materialInfo.getLENGTH());
            et_left.setText(materialInfo.getODDMENTS());
            et_remark.setText(materialInfo.getREMARK());
        }
        tv_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(v);
            }
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout_material.removeView(view);
            }
        });

        return view;
    }

    private void showPopWindow(final View v) {
        final List<String> list = new ArrayList<>();
        for (TailorInfoBo.MatInfoBean item : mList_matInfo) {
            list.add(item.getMAT_NO());
        }
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
        void recordLabuCallback(UpdateLabuBo labuData, boolean done);
    }
}
