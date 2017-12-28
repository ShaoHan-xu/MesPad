package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.GetLabuDataBo;
import com.eeka.mespad.bo.LabuDataInfoBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.SaveLabuDataBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.FormatUtil;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录拉布数据界面
 * Created by Lenovo on 2017/11/24.
 */

public class RecordLabuActivity extends BaseActivity {

    private TailorInfoBo mData;
    private LinearLayout mLayout_items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_record_labu);

        mData = (TailorInfoBo) getIntent().getSerializableExtra("data");
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        GetLabuDataBo params = new GetLabuDataBo();
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        if (positionUsers != null && positionUsers.size() != 0) {
            params.setUSER_ID(positionUsers.get(0).getUSER());
        }
        TailorInfoBo.SHOPORDERINFORBean orderInfo = mData.getSHOP_ORDER_INFOR();
        TextView tv_orderNum = (TextView) findViewById(R.id.tv_recordLabu_orderNum);
        tv_orderNum.setText(orderInfo.getSHOP_ORDER());
        params.setSHOP_ORDER_BO(orderInfo.getSHOP_ORDER_BO());
        params.setZ_LAYOUT_BO(orderInfo.getZ_LAYOUT_BO());

        List<TailorInfoBo.LayoutInfoBean> layoutInfos = mData.getLAYOUT_INFOR();
        if (layoutInfos != null && layoutInfos.size() != 0) {
            TailorInfoBo.LayoutInfoBean layoutInfo = layoutInfos.get(0);
            TextView tv_chuangci = (TextView) findViewById(R.id.tv_recordLabu_chuangCi);
            tv_chuangci.setText(layoutInfo.getLAYOUT());
            TextView tv_layoutLength = (TextView) findViewById(R.id.tv_recordLabu_layoutLength);
            tv_layoutLength.setText(layoutInfo.getLENGTH() + layoutInfo.getLENGTH_UNIT());
            ImageView imageView = (ImageView) findViewById(R.id.iv_recordLabu_matImg);
            Picasso.with(mContext).load(layoutInfo.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
        }

        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        TextView tv_caiChuangNum = (TextView) findViewById(R.id.tv_recordLabu_cutNum);
        tv_caiChuangNum.setText(resource.getRESOURCE());
        params.setRESOURCE_BO(resource.getRESOURCE_BO());

        List<TailorInfoBo.MatInfoBean> matInfos = mData.getMAT_INFOR();
        if (matInfos != null && matInfos.size() != 0) {
            TailorInfoBo.MatInfoBean matInfo = matInfos.get(0);
            TextView tv_matNum = (TextView) findViewById(R.id.tv_recordLabu_matNum);
            tv_matNum.setText(matInfo.getMAT_NO());

            params.setITEM_BO(matInfo.getITEM_BO());
        }

        List<TailorInfoBo.CUTSIZESBean> sizeArray = mData.getCUT_SIZES();
        if (sizeArray != null) {
            LinearLayout layout_sizeInfo = (LinearLayout) findViewById(R.id.layout_sizeInfo);
            for (int i = 0; i < sizeArray.size(); i++) {
                layout_sizeInfo.addView(getSizeInfoView(sizeArray.get(i)));
            }
        }

        mLayout_items = (LinearLayout) findViewById(R.id.layout_recordLabu_items);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        showLoading();
        HttpHelper.getLabuData(params, this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_add:
                mLayout_items.addView(getItemView(null, mLayout_items.getChildCount()));
                ((ScrollView) findViewById(R.id.scrollView)).fullScroll(ScrollView.FOCUS_DOWN);
                break;
            case R.id.btn_save:
                done();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    private void done() {
        showLoading();
        String dcGroupBo = null;
        List<SaveLabuDataBo.DCSBean> list_item = new ArrayList<>();
        for (int i = 0; i < mLayout_items.getChildCount(); i++) {
            View view = mLayout_items.getChildAt(i);
            EditText et_juanHao = (EditText) view.findViewById(R.id.et_recordLabu_juanHao);
            String juanHaoStr = et_juanHao.getText().toString();
            for (int j = i + 1; j < mLayout_items.getChildCount(); j++) {
                View view1 = mLayout_items.getChildAt(j);
                EditText et_juanHao1 = (EditText) view1.findViewById(R.id.et_recordLabu_juanHao);
                String juanHaoStr1 = et_juanHao1.getText().toString();
                if (juanHaoStr.equals(juanHaoStr1)) {
                    dismissLoading();
                    ErrorDialog.showAlert(mContext, "无法同时记录两个相同的卷号：" + juanHaoStr);
                    return;
                }
            }

            EditText et_matWidth = (EditText) view.findViewById(R.id.et_recordLabu_matLength);
            EditText et_layers = (EditText) view.findViewById(R.id.et_recordLabu_layers);
            EditText et_leftQty = (EditText) view.findViewById(R.id.et_recordLabu_leftQty);
            TextView tv_duanMa = (TextView) view.findViewById(R.id.tv_recordLabu_dm);

            String matWidthStr = et_matWidth.getText().toString();
            String layersStr = et_layers.getText().toString();
            String leftQtyStr = et_leftQty.getText().toString();
            String dunaMaStr = tv_duanMa.getText().toString();
            //如果新增的行没有填写任何数据，直接跳过，不保存
            if (isEmpty(juanHaoStr) && isEmpty(matWidthStr) && isEmpty(layersStr) && isEmpty(leftQtyStr)) {
                continue;
            }
            if (isEmpty(matWidthStr) || isEmpty(layersStr) || isEmpty(leftQtyStr)) {
                dismissLoading();
                showErrorDialog("卷号 " + juanHaoStr + " 数据输入不完整，无法保存！");
                return;
            }

            SaveLabuDataBo.DCSBean item = new SaveLabuDataBo.DCSBean();
            item.setINVENTORY(juanHaoStr);
            item.setLAYERS(layersStr);
            item.setMAT_LENGTH(matWidthStr);
            item.setLEFT_QTY(leftQtyStr);
            item.setDM(dunaMaStr);
            list_item.add(item);

            if (isEmpty(dcGroupBo)) {
                LabuDataInfoBo.SPREADINGDATABean item1 = (LabuDataInfoBo.SPREADINGDATABean) view.getTag();
                if (!isEmpty(item1.getDC_GROUP_BO())) {
                    dcGroupBo = item1.getDC_GROUP_BO();
                }
            }
        }
        SaveLabuDataBo params = new SaveLabuDataBo();
        params.setDCS(list_item);

        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        if (positionUsers != null && positionUsers.size() != 0) {
            params.setUSER_ID(positionUsers.get(0).getUSER());
        }

        TailorInfoBo.SHOPORDERINFORBean orderInfo = mData.getSHOP_ORDER_INFOR();
        params.setSHOP_ORDER_BO(orderInfo.getSHOP_ORDER_BO());
        params.setZ_LAYOUT_BO(orderInfo.getZ_LAYOUT_BO());

        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        params.setRESOURCE_BO(resource.getRESOURCE_BO());

        List<TailorInfoBo.MatInfoBean> matInfos = mData.getMAT_INFOR();
        if (matInfos != null && matInfos.size() != 0) {
            TailorInfoBo.MatInfoBean matInfo = matInfos.get(0);
            params.setITEM_BO(matInfo.getITEM_BO());
        }
        params.setDC_GROUP_BO(dcGroupBo);
        HttpHelper.saveLabuData(params, this);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getLabuData.equals(url)) {
                LabuDataInfoBo labuDataInfoBos = JSON.parseObject(HttpHelper.getResultStr(resultJSON), LabuDataInfoBo.class);
                List<LabuDataInfoBo.SPREADINGDATABean> list_items = labuDataInfoBos.getSPREADING_DATA();
                mLayout_items.removeAllViews();
                if (list_items != null && list_items.size() != 0) {
                    for (int i = 0; i < list_items.size(); i++) {
                        mLayout_items.addView(getItemView(list_items.get(i), i));
                    }
                    setResult(RESULT_OK);
                }
            } else if (HttpHelper.saveOrUpdateLabuData.equals(url)) {
                toast("保存成功");
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private View getItemView(LabuDataInfoBo.SPREADINGDATABean item, int position) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_recordlabu_item, null);
        EditText et_juanHao = (EditText) view.findViewById(R.id.et_recordLabu_juanHao);
        EditText et_matLength = (EditText) view.findViewById(R.id.et_recordLabu_matLength);
        EditText et_layers = (EditText) view.findViewById(R.id.et_recordLabu_layers);
        EditText et_leftQty = (EditText) view.findViewById(R.id.et_recordLabu_leftQty);
        TextView tv_dm = (TextView) view.findViewById(R.id.tv_recordLabu_dm);
        ImageView iv_del = (ImageView) view.findViewById(R.id.iv_recordLabu_del);
        if (item != null) {
            et_juanHao.setText(item.getINVENTORY());
            et_matLength.setText(item.getMAT_LENGTH());
            et_layers.setText(item.getLAYERS());
            et_leftQty.setText(item.getLEFT_QTY());
            tv_dm.setText(getString(R.string.float_2, getDM(item.getMAT_LENGTH(), item.getLAYERS(), item.getLEFT_QTY())));
        } else {
            iv_del.setVisibility(View.VISIBLE);
            iv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLayout_items.removeViewAt(mLayout_items.indexOfChild(view));
                }
            });
        }
        et_matLength.addTextChangedListener(new TextChangeListener(position));
        et_layers.addTextChangedListener(new TextChangeListener(position));
        et_leftQty.addTextChangedListener(new TextChangeListener(position));
        view.setTag(item);
        return view;
    }

    private void refreshItemView(int position) {
        View view = mLayout_items.getChildAt(position);
        EditText et_matLength = (EditText) view.findViewById(R.id.et_recordLabu_matLength);
        EditText et_layers = (EditText) view.findViewById(R.id.et_recordLabu_layers);
        EditText et_leftQty = (EditText) view.findViewById(R.id.et_recordLabu_leftQty);
        TextView tv_dm = (TextView) view.findViewById(R.id.tv_recordLabu_dm);

        String matWidthStr = et_matLength.getText().toString();
        String layersStr = et_layers.getText().toString();
        String leftQtyStr = et_leftQty.getText().toString();
        if (isEmpty(matWidthStr) || isEmpty(layersStr) || isEmpty(leftQtyStr)) {
            tv_dm.setText("0");
            return;
        }
        tv_dm.setText(getString(R.string.float_2, getDM(matWidthStr, layersStr, leftQtyStr)));
    }

    /**
     * 获取短码数
     */
    private float getDM(String matWidthStr, String layersStr, String leftQtyStr) {
        float matWidth = FormatUtil.strToFloat(matWidthStr);
        float layers = FormatUtil.strToFloat(layersStr);
        float leftQty = FormatUtil.strToFloat(leftQtyStr);
        List<TailorInfoBo.LayoutInfoBean> layoutInfor = mData.getLAYOUT_INFOR();
        if (layoutInfor != null && layoutInfor.size() != 0) {
            String lengthStr = layoutInfor.get(0).getLENGTH();
            float length = FormatUtil.strToFloat(lengthStr);
            return matWidth - length * layers - leftQty;
        }
        return 0;
    }

    private class TextChangeListener implements TextWatcher {

        private int position;

        TextChangeListener(int position) {
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
            refreshItemView(position);
        }
    }

    private View getSizeInfoView(TailorInfoBo.CUTSIZESBean sizeInfo) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sizeinfo, null);
        TextView tv_yardage = (TextView) view.findViewById(R.id.tv_item_yardage);
        TextView tv_count = (TextView) view.findViewById(R.id.tv_item_count);
        tv_yardage.setText(sizeInfo.getSIZE_CODE());
        tv_count.setText(sizeInfo.getSIZE_AMOUNT() + "");
        return view;
    }

    public static Intent getIntent(Context context, TailorInfoBo data) {
        Intent intent = new Intent(context, RecordLabuActivity.class);
        intent.putExtra("data", data);
        return intent;
    }
}
