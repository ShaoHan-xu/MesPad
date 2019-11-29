package com.eeka.mespad.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bluetoothPrint.BluetoothHelper;
import com.eeka.mespad.bo.BatchLabuRecordBo;
import com.eeka.mespad.bo.BatchLabuRecordPrintBo;
import com.eeka.mespad.bo.PostBatchRecordLabuBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.FormatUtil;
import com.eeka.mespad.utils.MyInputFilter;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.UnitUtil;
import com.eeka.mespad.view.dialog.BaseDialog;
import com.eeka.mespad.view.dialog.BatchLabuRecordPrintContentDialog;
import com.eeka.mespad.view.dialog.ImageBrowserDialog;
import com.eeka.mespad.view.dialog.LabuCheckDialog;

import java.util.ArrayList;
import java.util.List;

public class BatchLabuRecordActivity extends BaseActivity {

    private PostBatchRecordLabuBo mPostData;

    private BatchLabuRecordBo mData;
    private LinearLayout mLayout_itemTitle;
    private LinearLayout mLayout_itemTotal;
    private LinearLayout mLayout_items;

    private LinearLayout mLayout_cutNum;
    private LinearLayout mLayout_sizeList;

    private Button mBtn_start;

    private boolean editAble;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_batch_labu);

        editAble = getIntent().getBooleanExtra("editAble", false);
        mPostData = (PostBatchRecordLabuBo) getIntent().getSerializableExtra("data");
        mOperationFlag = "BEGIN";

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.btn_back).setOnClickListener(this);

        mLayout_cutNum = findViewById(R.id.layout_layoutDuan);
        mLayout_sizeList = findViewById(R.id.layout_sizeList);

        mLayout_itemTitle = findViewById(R.id.layout_labuRecord_tabTitle);
        mLayout_itemTotal = findViewById(R.id.layout_labuRecord_total);
        mLayout_items = findViewById(R.id.layout_itemList);

        TextView tv_layoutNoTitle = findViewById(R.id.tv_layoutNoTitle);
        tv_layoutNoTitle.setText("排料图" + mPostData.getLayoutNo() + "已拉层数汇总表");

        if (editAble) {
            mBtn_start = findViewById(R.id.btn_start);
            mBtn_start.setOnClickListener(this);
            findViewById(R.id.btn_layoutImg).setOnClickListener(this);
            findViewById(R.id.btn_add).setOnClickListener(this);
            findViewById(R.id.btn_completed).setOnClickListener(this);
        } else {
            mLayout_itemTitle.removeViewAt(mLayout_itemTitle.getChildCount() - 1);
            mLayout_itemTotal.removeViewAt(mLayout_itemTotal.getChildCount() - 1);
            findViewById(R.id.layout_buttonList).setVisibility(View.GONE);
            findViewById(R.id.btn_print).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_print).setOnClickListener(this);
        }
    }

    private void setupView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("拉布单：" + mData.getORDER_NO());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        List<BatchLabuRecordBo.SEGMENTINFOBean> segmentInfo = mData.getSEGMENT_INFO();
        for (int i = 0; i < segmentInfo.size(); i++) {
            BatchLabuRecordBo.SEGMENTINFOBean item = segmentInfo.get(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_labu_layout_length, null);
            TextView tv_duan = view.findViewById(R.id.tv_duan);
            tv_duan.setText(item.getCutNum() + "段");
            TextView tv_length = view.findViewById(R.id.tv_length);
            float standLength = item.getStandLength();
            tv_length.setText(getString(R.string.float_2, standLength));

            float actualLength = item.getActualLength();
            if (actualLength == 0) {
                actualLength = standLength;
            }
            EditText editText = view.findViewById(R.id.et_actualLength);
            editText.setEnabled(editAble);
            editText.setText(getString(R.string.float_2, actualLength));
            editText.setFilters(new InputFilter[]{new MyInputFilter()});

            mLayout_cutNum.addView(view, params);

            TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.textview_common, null);
            textView.setText(item.getCutNum() + "段层数");
            mLayout_itemTitle.addView(textView, 2 + i, params);

            TextView total = (TextView) LayoutInflater.from(mContext).inflate(R.layout.textview_common, null);
            mLayout_itemTotal.addView(total, 2 + i, params);

            //码数选择部分
            TextView tv_sizeDuan = (TextView) LayoutInflater.from(mContext).inflate(R.layout.textview_common, null);
            tv_sizeDuan.setPadding(2, 2, 2, 2);
            tv_sizeDuan.setText(item.getCutNum() + "段");

            LinearLayout sizeLayout = new LinearLayout(mContext);
            sizeLayout.setPadding(8, 8, 0, 0);
            sizeLayout.setId(R.id.linearLayout);
            sizeLayout.setGravity(Gravity.CENTER);
            sizeLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.rightMargin = UnitUtil.px2dip(mContext, getResources().getDimension(R.dimen.dp_5));
            params1.width = (int) getResources().getDimension(R.dimen.dp_40);
            params1.height = (int) getResources().getDimension(R.dimen.dp_17);
            List<BatchLabuRecordBo.SEGMENTINFOBean.SizeCodesBean> sizeCodes = item.getSizeCodes();
            for (BatchLabuRecordBo.SEGMENTINFOBean.SizeCodesBean sizeCodesBean : sizeCodes) {
                View view1 = LayoutInflater.from(mContext).inflate(R.layout.layout_checkbtn_green_round, null);
                CheckBox checkBox = view1.findViewById(R.id.checkbox);
                checkBox.setLayoutParams(params1);
                checkBox.setChecked(true);
                checkBox.setEnabled(editAble);
                checkBox.setText(sizeCodesBean.getSizeCode());
                sizeLayout.addView(view1);
            }

            LinearLayout duanLayout = new LinearLayout(mContext);
            duanLayout.setTag(item.getCutNum());
            duanLayout.setGravity(Gravity.CENTER);
            duanLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            duanLayout.setDividerDrawable(getResources().getDrawable(R.drawable.divider_gray_light));
            duanLayout.setOrientation(LinearLayout.VERTICAL);

            duanLayout.addView(tv_sizeDuan);
            duanLayout.addView(sizeLayout);
            mLayout_sizeList.addView(duanLayout);
        }

        LinearLayout layout_yila = findViewById(R.id.layout_yiLa);
        List<BatchLabuRecordBo.RABINFOBean> rabInfo = mData.getRAB_INFO();
        for (int i = 0; i < rabInfo.size(); i++) {
            BatchLabuRecordBo.RABINFOBean item = rabInfo.get(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout_yila, null);
            TextView tv_cutNum = view.findViewById(R.id.tv_cutNum);
            TextView tv_size = view.findViewById(R.id.tv_size);
            TextView tv_rate = view.findViewById(R.id.tv_rate);
            TextView tv_need = view.findViewById(R.id.tv_need);
            TextView tv_yiLa = view.findViewById(R.id.tv_yiLa);
            TextView tv_unLa = view.findViewById(R.id.tv_unLa);

            int cutNum = item.getCUT_NUM();
            tv_cutNum.setText(cutNum + "");
            tv_size.setText(item.getSIZE_CODE());
            tv_rate.setText(item.getSIZE_AMOUNT() + "");
            tv_need.setText(item.getSIZE_TOTAL() + "");
            tv_yiLa.setText(item.getSIZE_FEN() + "");
            tv_unLa.setText(item.getSIZE_LEFT() + "");

            layout_yila.addView(view, params);
        }

        if (editAble) {
            //初始加一行
            mLayout_items.addView(getItemView(null), mLayout_items.getChildCount() - 1);
        } else {
            List<BatchLabuRecordBo.VolumnInfoBean> volumnInfo = mData.getVOLUMN_INFO();
            double lengthTotal = 0;
            double leftTotal = 0;
            double shortTotal = 0;
            double[] layers = null;
            for (BatchLabuRecordBo.VolumnInfoBean item : volumnInfo) {
                mLayout_items.addView(getItemView(item), mLayout_items.getChildCount() - 1);
                lengthTotal += item.getLength();
                leftTotal += item.getLeftNum();
                shortTotal += item.getShortNum();

                List<BatchLabuRecordBo.VolumnInfoBean.BulkRabSegmentsBean> rabSegments = item.getBulkRabSegments();
                if (layers == null) {
                    layers = new double[rabSegments.size()];
                }
                for (int i = 0; i < rabSegments.size(); i++) {
                    layers[i] += rabSegments.get(i).getLayers();
                }
            }

            TextView tv_lengthTotal = (TextView) mLayout_itemTotal.getChildAt(1);
            tv_lengthTotal.setText(getString(R.string.float_2, lengthTotal));
            TextView tv_leftTotal = (TextView) mLayout_itemTotal.getChildAt(layers.length + 2);
            tv_leftTotal.setText(getString(R.string.float_2, leftTotal));
            TextView tv_shortTotal = (TextView) mLayout_itemTotal.getChildAt(layers.length + 3);
            tv_shortTotal.setText(getString(R.string.float_2, shortTotal));
            for (int i = 0; i < layers.length; i++) {
                TextView tv_layersTotal = (TextView) mLayout_itemTotal.getChildAt(i + 2);
                tv_layersTotal.setText(layers[i] + "");
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();

        getData();
    }

    private void getData() {
        showLoading();
        if (editAble) {
            HttpHelper.getBatchLabuInfo(mPostData.getOperation(), mPostData.getMaterialType(), mPostData.getShopOrderRef(), mPostData.getLayOutRef(), mPostData.getLayOutName(), this);
        } else {
            HttpHelper.getRabHistoryByRabNo(mPostData.getRabOrderNo(), mPostData.getOperation(), mPostData.getMaterialType(), this);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_layoutImg:
                new ImageBrowserDialog(mContext, mPostData.getLayoutImgUrl()).show();
                break;
            case R.id.btn_add:
                mLayout_items.addView(getItemView(null), mLayout_items.getChildCount() - 1);
                break;
            case R.id.btn_completed:
                new LabuCheckDialog(mContext, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completed();
                    }
                }).setParams(0.5f, 0.6f).show();
                break;
            case R.id.btn_start:
                start();
                break;
            case R.id.btn_print:
                print();
                break;
        }
    }

    private String mOperationFlag;

    private void start() {
        String userId = SpUtil.getLoginUserId();
        if (isEmpty(userId)) {
            showErrorDialog("需要员工登录上岗才能操作");
            return;
        }
        String rabRef = "ZBulkRabBO:" + SpUtil.getSite() + "," + mPostData.getLayOutRef() + "," + mData.getORDER_NO();
        showLoading();
        HttpHelper.operationProduce(userId, mOperationFlag, mPostData.getShopOrderRef(), rabRef, mData.getORDER_NO(), mPostData.getOperation(), this);
    }

    private void completed() {
        List<PostBatchRecordLabuBo.CutSizeBean> sizeList = getSelectedSize();
        if (sizeList.size() == 0) {
            showErrorDialog("请选择尺码");
            return;
        }
        mPostData.setCutSizes(sizeList);

        List<PostBatchRecordLabuBo.BulkRabRollsBean.BulkRabSegmentsBean> cutNum = new ArrayList<>();
        List<BatchLabuRecordBo.SEGMENTINFOBean> cut_num = mData.getSEGMENT_INFO();
        for (int i = 0; i < cut_num.size(); i++) {
            LinearLayout childAt = (LinearLayout) mLayout_cutNum.getChildAt(i);
            TextView tv_actualLength = childAt.findViewById(R.id.et_actualLength);

            BatchLabuRecordBo.SEGMENTINFOBean cutnumBean = cut_num.get(i);
            PostBatchRecordLabuBo.BulkRabRollsBean.BulkRabSegmentsBean item = new PostBatchRecordLabuBo.BulkRabRollsBean.BulkRabSegmentsBean();
            item.setCutNum(cutnumBean.getCutNum());
            item.setStandLength(cutnumBean.getStandLength() + "");
            item.setActualLenth(tv_actualLength.getText().toString());

            cutNum.add(item);
        }

        List<PostBatchRecordLabuBo.BulkRabRollsBean> items = new ArrayList<>();
        for (int i = 0; i < mLayout_items.getChildCount() - 1; i++) {
            LinearLayout itemView = (LinearLayout) mLayout_items.getChildAt(i);
            TextView tv_juanHao = itemView.findViewById(R.id.tv_juanHao);
            String volumnStr = tv_juanHao.getText().toString();
            TextView tv_length = itemView.findViewById(R.id.et_length);
            String lengthStr = tv_length.getText().toString();
            if (isEmpty(lengthStr)) {
                showErrorDialog("卷号：" + volumnStr + " ,面料长度不能为空");
                return;
            }

            List<PostBatchRecordLabuBo.BulkRabRollsBean.BulkRabSegmentsBean> cuts = new ArrayList<>();
            for (int j = 0; j < cut_num.size(); j++) {
                BatchLabuRecordBo.SEGMENTINFOBean cutnumBean = cut_num.get(j);
                TextView tv_layer = (TextView) itemView.getChildAt(j + 2);
                String layer = tv_layer.getText().toString();
                if (isEmpty(layer)) {
                    showErrorDialog("卷号：" + volumnStr + " ," + cutnumBean.getCutNum() + "段层数不能为空");
                    return;
                }
                PostBatchRecordLabuBo.BulkRabRollsBean.BulkRabSegmentsBean bean = cutNum.get(j);
                PostBatchRecordLabuBo.BulkRabRollsBean.BulkRabSegmentsBean bean1 = new PostBatchRecordLabuBo.BulkRabRollsBean.BulkRabSegmentsBean();
                bean1.setActualLenth(bean.getActualLenth());
                bean1.setCutNum(bean.getCutNum());
                bean1.setLayers(layer);
                bean1.setStandLength(bean.getStandLength());
                cuts.add(bean1);
            }

            TextView tv_left = itemView.findViewById(R.id.et_left);
            String left = tv_left.getText().toString();
            if (isEmpty(left)) {
                left = "0";
            }
            TextView tv_shortNum = itemView.findViewById(R.id.tv_shortNum);

            PostBatchRecordLabuBo.BulkRabRollsBean item = new PostBatchRecordLabuBo.BulkRabRollsBean();
            item.setLeftNum(left);
            item.setLength(lengthStr);
            item.setVolumn(volumnStr);
            item.setShortNum(tv_shortNum.getText().toString());
            item.setBulkRabSegments(cuts);
            items.add(item);
        }

        mPostData.setBulkRabSegments(cutNum);
        mPostData.setRabOrderNo(mData.getORDER_NO());
        mPostData.setRabSeq(mData.getORDER_SEQ());
        mPostData.setBulkRabRolls(items);

        StringBuilder sb = new StringBuilder();
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        for (UserInfoBo user : positionUsers) {
            sb.append(user.getEMPLOYEE_NUMBER()).append(",");
        }
        showLoading();
        HttpHelper.saveBatchLabuData(sb.substring(0, sb.length() - 1), mPostData, this);
    }

    private List<PostBatchRecordLabuBo.CutSizeBean> getSelectedSize() {
        List<PostBatchRecordLabuBo.CutSizeBean> sizeList = new ArrayList<>();
        for (int i = 0; i < mLayout_sizeList.getChildCount(); i++) {
            View child = mLayout_sizeList.getChildAt(i);
            LinearLayout layout = child.findViewById(R.id.linearLayout);
            for (int j = 0; j < layout.getChildCount(); j++) {
                View childAt = layout.getChildAt(j);
                CheckBox checkBox = childAt.findViewById(R.id.checkbox);
                String size = checkBox.getText().toString();
                if (checkBox.isChecked()) {
                    PostBatchRecordLabuBo.CutSizeBean bean = new PostBatchRecordLabuBo.CutSizeBean();
                    bean.setSizeCode(size);
                    bean.setCutNum((Integer) child.getTag());
                    sizeList.add(bean);
                }
            }
        }
        return sizeList;
    }

    private View getItemView(BatchLabuRecordBo.VolumnInfoBean item) {
        int childCount = mLayout_items.getChildCount();
        List<BatchLabuRecordBo.SEGMENTINFOBean> cutNum = mData.getSEGMENT_INFO();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_labu_record, null);
        TextView tv_juanHao = view.findViewById(R.id.tv_juanHao);
        TextView tv_shortNum = view.findViewById(R.id.tv_shortNum);
        tv_juanHao.setText(childCount + "");
        EditText et_length = view.findViewById(R.id.et_length);
        EditText et_left = view.findViewById(R.id.et_left);
        if (!editAble) {
            view.findViewById(R.id.tv_del).setVisibility(View.GONE);
            tv_juanHao.setText(item.getVolumn() + "");
            tv_shortNum.setText(item.getShortNum() + "");

            et_length.setEnabled(false);
            et_left.setEnabled(false);
            et_left.setText(item.getLeftNum() + "");
            et_length.setText(item.getLength() + "");
        } else {
            et_length.setTag(1);
            et_left.setTag((2 + cutNum.size()));
            et_left.setFilters(new InputFilter[]{new MyInputFilter().setDigits(1)});
            et_length.addTextChangedListener(new InputChangeListener(et_length));
            et_left.addTextChangedListener(new InputChangeListener(et_left));
        }

        LinearLayout layout = view.findViewById(R.id.layout_rootView);
        layout.setTag(childCount - 1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        List<BatchLabuRecordBo.VolumnInfoBean.BulkRabSegmentsBean> rabSegments = null;
        if (item != null) {
            rabSegments = item.getBulkRabSegments();
        }
        for (int i = 0; i < cutNum.size(); i++) {
            EditText editText = (EditText) LayoutInflater.from(mContext).inflate(R.layout.edittext_common, null);
            if (!editAble) {
                editText.setEnabled(false);
                editText.setText(rabSegments.get(i).getLayers() + "");
            }
            editText.setPadding(0, 0, 0, 0);
            editText.setBackgroundResource(0);
            editText.setGravity(Gravity.CENTER);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setSingleLine();
            editText.addTextChangedListener(new InputChangeListener(editText));
            int index = i + 2;
            editText.setTag(index);
            layout.addView(editText, index, params);
        }
        TextView tv_del = view.findViewById(R.id.tv_del);
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout parent = (LinearLayout) v.getParent();
                int index = (int) parent.getTag();
                removeItem(index);
            }
        });
        return view;
    }

    private void removeItem(int index) {
        calculate(0, 0, index);
        mLayout_items.removeViewAt(index);
        for (int i = index; i < mLayout_items.getChildCount() - 1; i++) {
            View childAt = mLayout_items.getChildAt(i);
            int tag = (int) childAt.getTag();
            childAt.setTag(tag - 1);
            TextView tv_juanHao = childAt.findViewById(R.id.tv_juanHao);
            tv_juanHao.setText(tag + "");
        }
    }

    private class InputChangeListener implements TextWatcher {

        private View view;

        InputChangeListener(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            LinearLayout parent = (LinearLayout) view.getParent();
            int verticalIndex = (int) parent.getTag();

            EditText editText = (EditText) view;
            int horizontalIndex = (int) editText.getTag();

            calculate(verticalIndex, horizontalIndex, -1);
        }
    }

    /**
     * @param verticalIndex   有改动的行
     * @param horizontalIndex 有改动的列
     * @param delIndex        删除行（只有删除后重新计算时才传，否则传 -1）
     */
    private void calculate(int verticalIndex, int horizontalIndex, int delIndex) {
        if (delIndex != -1) {
            //删除一行后，重新计算合计
            LinearLayout childAt = (LinearLayout) mLayout_items.getChildAt(delIndex);
            for (int i = 1; i < mLayout_itemTotal.getChildCount() - 1; i++) {
                TextView tv_delValue = (TextView) childAt.getChildAt(i);
                TextView tv_totalText = (TextView) mLayout_itemTotal.getChildAt(i);
                String delValue = tv_delValue.getText().toString();
                String totalValue = tv_totalText.getText().toString();
                int id = tv_delValue.getId();
                if (id == R.id.tv_shortNum || id == R.id.et_left) {
                    float del = 0;
                    float total = 0;
                    if (!isEmpty(delValue)) {
                        del = FormatUtil.strToFloat(delValue);
                    }
                    if (!isEmpty(totalValue)) {
                        total = FormatUtil.strToFloat(totalValue);
                    }
                    tv_totalText.setText(getString(R.string.float_1, total - del));
                } else {
                    int del = 0;
                    int total = 0;
                    if (!isEmpty(delValue)) {
                        del = Integer.parseInt(delValue);
                    }
                    if (!isEmpty(totalValue)) {
                        total = Integer.parseInt(totalValue);
                    }
                    tv_totalText.setText((total - del) + "");
                }
            }
        } else {
            TextView totalText = (TextView) mLayout_itemTotal.getChildAt(horizontalIndex);
            //计算合计
            float total = 0;
            for (int i = 0; i < mLayout_items.getChildCount() - 1; i++) {
                LinearLayout layout = (LinearLayout) mLayout_items.getChildAt(i);
                TextView textView = (TextView) layout.getChildAt(horizontalIndex);
                int id = textView.getId();
                if (id == R.id.tv_shortNum || id == R.id.et_left) {
                    float value = 0;
                    String valueStr = textView.getText().toString();
                    if (!isEmpty(valueStr)) {
                        value = FormatUtil.strToFloat(valueStr);
                    }
                    total += value;
                    totalText.setText(getString(R.string.float_1, total));
                } else {
                    int value = 0;
                    String valueStr = textView.getText().toString();
                    if (!isEmpty(valueStr)) {
                        value = Integer.parseInt(valueStr);
                    }
                    total += value;
                    totalText.setText(((int) total) + "");
                }
            }

            //计算短码数：（1段长*层数+2段长*层数）+余料-总米数=短码数
            LinearLayout parent = (LinearLayout) mLayout_items.getChildAt(verticalIndex);
            float layerLength = 0;
            for (int i = 0; i < mLayout_cutNum.getChildCount(); i++) {
                LinearLayout childAt = (LinearLayout) mLayout_cutNum.getChildAt(i);
                String realLengthText = ((TextView) childAt.findViewById(R.id.et_actualLength)).getText().toString();
                if (isEmpty(realLengthText) || "0".equals(realLengthText)) {
                    showErrorDialog("请输入排料图实际长度");
                    return;
                }
                float realLength = FormatUtil.strToFloat(realLengthText);
                String layerText = ((TextView) parent.getChildAt(2 + i)).getText().toString();
                if (isEmpty(layerText)) {
                    return;
                }
                int layer = Integer.parseInt(layerText);

                layerLength += (realLength * layer);
            }

            String lengthText = ((TextView) parent.getChildAt(1)).getText().toString();
            if (isEmpty(lengthText)) {
                return;
            }
            int length = Integer.parseInt(lengthText);

            String leftText = ((TextView) parent.getChildAt(2 + mLayout_cutNum.getChildCount())).getText().toString();
            if (isEmpty(leftText)) {
                return;
            }
            float left = FormatUtil.strToFloat(leftText);

            float shortNum = layerLength + left - length;
            TextView tv_shortNum = parent.findViewById(R.id.tv_shortNum);
            tv_shortNum.setText(getString(R.string.float_2, shortNum));

            //短码合计
            TextView tv_shortNumAll = (TextView) mLayout_itemTotal.getChildAt(mLayout_itemTotal.getChildCount() - 2);
            float value = 0;
            float shortNumTotal = 0;
            for (int i = 0; i < mLayout_items.getChildCount() - 1; i++) {
                LinearLayout layout = (LinearLayout) mLayout_items.getChildAt(i);
                TextView textView = (TextView) layout.getChildAt(layout.getChildCount() - 2);
                String valueStr = textView.getText().toString();
                if (!isEmpty(valueStr)) {
                    value = FormatUtil.strToFloat(valueStr);
                }
                shortNumTotal += value;
            }
            tv_shortNumAll.setText(getString(R.string.float_2, shortNumTotal));
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getBatchLabuInfo.equals(url) || HttpHelper.getRabHistoryByRabNo.equals(url)) {
                mData = JSON.parseObject(HttpHelper.getResultStr(resultJSON), BatchLabuRecordBo.class);
                if (HttpHelper.getRabHistoryByRabNo.equals(url)) {
                    TextView tv_layoutNoTitle = findViewById(R.id.tv_layoutNoTitle);
                    tv_layoutNoTitle.setText("排料图" + mData.getLAYOUT_NO() + "已拉层数汇总表");
                    mPostData.setLayoutImgUrl(mData.getLAYOUT_IMAGE());
                }
                if (mData != null) {
                    setupView();
                }
            } else if (HttpHelper.saveBatchLabuData.equals(url)) {
                toast("操作成功");

                print();
            } else if (HttpHelper.operationProduce.equals(url)) {
                if ("BEGIN".equals(mOperationFlag) || "RESTART".equals(mOperationFlag)) {
                    mOperationFlag = "PAUSE";
                    mBtn_start.setText("暂停");
                } else {
                    mOperationFlag = "RESTART";
                    mBtn_start.setText("继续");
                }
            }
        }
    }

    private void print() {
        BatchLabuRecordPrintBo printBo = new BatchLabuRecordPrintBo();
        printBo.setShopOrder(mData.getSHOP_ORDER());
        printBo.setRabOrder(mData.getORDER_NO());
        printBo.setLayoutNo(mPostData.getLayoutNo());
        printBo.setItem(mPostData.getItem());
        printBo.setMatType(mPostData.getMaterialType());

        List<BatchLabuRecordBo.SEGMENTINFOBean> segmentInfo = mData.getSEGMENT_INFO();
        List<PostBatchRecordLabuBo.CutSizeBean> sizes;
        if (editAble) {
            sizes = mPostData.getCutSizes();
            for (int i = 0; i < segmentInfo.size(); i++) {
                BatchLabuRecordBo.SEGMENTINFOBean bean1 = segmentInfo.get(i);
                for (int j = 0; j < sizes.size(); j++) {
                    PostBatchRecordLabuBo.CutSizeBean bean = sizes.get(j);
                    if (bean.getCutNum() == bean1.getCutNum()) {
                        TextView totalText = (TextView) mLayout_itemTotal.getChildAt(i + 2);
                        String totalStr = totalText.getText().toString();
                        bean.setLayers(FormatUtil.strToInt(totalStr));
                    }
                }
            }
        } else {
            sizes = new ArrayList<>();
            for (BatchLabuRecordBo.SEGMENTINFOBean item : segmentInfo) {
                for (BatchLabuRecordBo.SEGMENTINFOBean.SizeCodesBean sizeCodesBean : item.getSizeCodes()) {
                    PostBatchRecordLabuBo.CutSizeBean sizeBean = new PostBatchRecordLabuBo.CutSizeBean();
                    sizeBean.setCutNum(item.getCutNum());
                    sizeBean.setLayers(item.getLayers());
                    sizeBean.setSizeCode(sizeCodesBean.getSizeCode());
                    sizes.add(sizeBean);
                }
            }
        }

        printBo.setSizeList(sizes);

        BaseDialog dialog = new BatchLabuRecordPrintContentDialog(mContext, printBo).setParams(0.5f, 0.5f);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                setResult(RESULT_OK);
                finish();
            }
        });
        dialog.show();

        int rateCount = 0;
        List<BatchLabuRecordBo.RABINFOBean> rabInfo = mData.getRAB_INFO();
        for (BatchLabuRecordBo.RABINFOBean item : rabInfo) {
            rateCount += item.getSIZE_AMOUNT();
        }
        for (int i = 0; i < rateCount * 2; i++) {
            BluetoothHelper.printLabuInfo(this, printBo);
        }
    }

    public static Intent getIntent(Context context, PostBatchRecordLabuBo data, boolean editAble) {
        Intent intent = new Intent(context, BatchLabuRecordActivity.class);
        intent.putExtra("data", data);
        intent.putExtra("editAble", editAble);
        return intent;
    }
}
