package com.eeka.mespad.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.TopicUtil;
import com.eeka.mespad.utils.UnitUtil;
import com.eeka.mespad.view.dialog.PatternDialog;

import java.util.List;

public class LabuDetailActivity extends BaseActivity {

    private LinearLayout mLayout_buttonList;
    private LinearLayout mLayout_layoutList;

    private LinearLayout.LayoutParams mLayoutParams_right10;
    private LinearLayout.LayoutParams mLayoutParams_weight1_horizontal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_labudetail);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("拉布作业");

        mLayoutParams_right10 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams_right10.rightMargin = UnitUtil.dip2px(mContext, 10);
        mLayoutParams_weight1_horizontal = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mLayoutParams_weight1_horizontal.weight = 1;

        mLayout_buttonList = findViewById(R.id.layout_button);
        initButton();

        RadioGroup rg_tabMenu = findViewById(R.id.tag_menu);
        rg_tabMenu.setOnCheckedChangeListener(new TabMenuCheckedListener());

        mLayout_layoutList = findViewById(R.id.layout_labuDetail_layoutList);

        getTableView(false, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UnitUtil.dip2px(mContext, 140));
        params.bottomMargin = UnitUtil.dip2px(mContext, 10);
        for (int i = 0; i < 5; i++) {
            mLayout_layoutList.addView(getTableView(true, i), params);
        }

        findViewById(R.id.btn_back).setOnClickListener(this);

        setupView();
    }

    @Override
    protected void initData() {
        super.initData();

    }

    private void setupView() {
        ImageView iv_sampleImg = findViewById(R.id.iv_labuDetail_sampleImg);
        ImageView iv_mainMat = findViewById(R.id.iv_labuDetail_mainMat);

        TextView tv_workRequires = findViewById(R.id.tv_labuDetail_workRequires);
        TextView tv_qualityRequires = findViewById(R.id.tv_labuDetail_qualityRequires);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(-1);

        LinearLayout layout_processNameContainer = findViewById(R.id.layout_labuDetail_processNameContainer);
        LinearLayout layout_processIconContainer = findViewById(R.id.layout_labuDetail_processIconContainer);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        for (int i = 0; i < 5; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(params);
            switch (i) {
                case 0:
                    imageView.setImageResource(R.drawable.ic_labu_working);
                    imageView.startAnimation(rotateAnimation);
                    break;
                case 1:
                case 2:
                    imageView.setImageResource(R.drawable.ic_labu_completed);
                    break;
                case 3:
                case 4:
                    imageView.setImageResource(R.drawable.ic_labu_wait);
                    break;
            }
            layout_processIconContainer.addView(imageView);

            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);
            switch (i) {
                case 0:
                    textView.setText("拉布");
                    break;
                case 1:
                    textView.setText("吊纱");
                    break;
                case 2:
                    textView.setText("裁剪");
                    break;
                case 3:
                    textView.setText("分包");
                    break;
                case 4:
                    textView.setText("入库");
                    break;
            }
            layout_processNameContainer.addView(textView);
        }
    }

    private class TabMenuCheckedListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radioBtn_mianBu:

                    break;
                case R.id.radioBtn_liBu:

                    break;
                case R.id.radioBtn_poBu:

                    break;
            }
        }
    }

    private void refreshMatView() {

    }

    private void initButton() {
        mLayout_buttonList.removeAllViews();
        String buttons = SpUtil.get(SpUtil.KEY_BUTTON, null);
        if (!isEmpty(buttons)) {
            List<PositionInfoBo.BUTTONINFORBean> buttonList = JSON.parseArray(buttons, PositionInfoBo.BUTTONINFORBean.class);
            for (PositionInfoBo.BUTTONINFORBean item : buttonList) {
                Button button = (Button) LayoutInflater.from(mContext).inflate(R.layout.layout_button_orange, null);
                switch (item.getBUTTON_ID()) {
                    case "PROCESS_FORM":
                        button.setText("工艺单显示");
                        button.setId(R.id.btn_processSheets);
                        break;
                    case "CUT_MAT_INFO":
                        button.setText("面料裁剪确认单");
                        button.setId(R.id.btn_cutMatInfo);
                        break;
                    case "MATERIALRETURN":
                        button.setText("退料");
                        button.setId(R.id.btn_materialReturn);
                        break;
                    case "MATERIALFEEDING":
                        button.setText("补料");
                        button.setId(R.id.btn_materialFeeding);
                        break;
                    case "VIDEO":
                        button.setText("视频查看");
                        button.setId(R.id.btn_playVideo);
                        break;
                    case "NCRECORD":
                        button.setText("不良录入");
                        button.setId(R.id.btn_NcRecord);
                        break;
                    case "CUT_PICTURE":
                        button.setText("裁剪图片");
                        button.setId(R.id.btn_cutBmp);
                        break;
                    case "XH_MESSAGE":
                        button.setText("绣花信息");
                        button.setId(R.id.btn_embroiderInfo);
                        break;
                    case "PATTERN_MESSAGE":
                        button.setText("显示纸样");
                        button.setId(R.id.btn_pattern);
                        break;
                }
                if (isEmpty(button.getText().toString())) {
                    continue;
                }
                button.setOnClickListener(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                button.setLayoutParams(params);
                mLayout_buttonList.addView(button);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_firstLabu:

                break;
            case R.id.btn_lostLabu:

                break;
            case R.id.btn_addLabu:

                break;
            case R.id.btn_materialReturn:
                returnAndFeedingMat(true);
                break;
            case R.id.btn_materialFeeding:
                returnAndFeedingMat(false);
                break;
            case R.id.btn_playVideo:
                playVideo();
                break;
            case R.id.btn_NcRecord:
                recordNC();
                break;
            case R.id.btn_embroiderInfo:
                String processLotBo = "";
                startActivity(EmbroiderActivity.getIntent(mContext, null, processLotBo, TopicUtil.TOPIC_CUT));
                break;
            case R.id.btn_pattern:
                showPattern(null);
                break;
        }
    }

    /**
     * 显示纸样图案
     */
    public void showPattern(String shopOrder) {
        if (isEmpty(shopOrder)) {
            showErrorDialog("工单号不能为空");
            return;
        }
        new PatternDialog(mContext, shopOrder).show();
    }

    public void recordNC() {
//        if (mTailorInfo == null) {
//            showErrorDialog("请先获取订单数据");
//            return;
//        }
//        startActivityForResult(RecordCutNCActivity.getIntent(mContext, mTailorInfo, mList_recordNC), REQUEST_RECORD_NC);
    }

    public void playVideo() {
//        if (mTailorInfo.getOPER_INFOR() == null || mTailorInfo.getOPER_INFOR().size() == 0) {
//            toast("当前站位无工序");
//            return;
//        }
//        int currentItem = mVP_process.getCurrentItem();
//        String videoUrl = mTailorInfo.getOPER_INFOR().get(currentItem).getVIDEO_URL();
//        SystemUtils.playVideo(mContext, videoUrl);
    }

    /**
     * 退补料
     */
    public void returnAndFeedingMat(boolean isReturn) {
//        if (mTailorInfo == null) {
//            toast("请先获取订单数据");
//            return;
//        }
//        List<TailorInfoBo.MatInfoBean> itemArray = mTailorInfo.getMAT_INFOR();
//        List<ReturnMaterialInfoBo.MaterialInfoBo> materialList = new ArrayList<>();
//        for (TailorInfoBo.MatInfoBean item : itemArray) {
//            ReturnMaterialInfoBo.MaterialInfoBo material = new ReturnMaterialInfoBo.MaterialInfoBo();
//            material.setPicUrl(item.getMAT_URL());
//            material.setITEM(item.getMAT_NO());
//            material.setUNIT_LABEL(item.getUNIT_LABEL());
//            materialList.add(material);
//        }
//        ReturnMaterialInfoBo materialInfoBo = new ReturnMaterialInfoBo();
//        materialInfoBo.setOrderNum(mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER());
//        materialInfoBo.setMaterialInfoList(materialList);
//        if (isReturn) {
//            new CutReturnMatDialog(mContext, CutReturnMatDialog.TYPE_RETURN, materialInfoBo).show();
//        } else {
//            new CutReturnMatDialog(mContext, CutReturnMatDialog.TYPE_ADD, materialInfoBo).show();
//        }
    }


    /**
     * @param isLayout 是否排料图
     */
    private View getTableView(boolean isLayout, int number) {
        View view;
        if (isLayout) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_labu_table, null);
        } else {
            view = findViewById(R.id.layout_labuDetail_table1);
            view.findViewById(R.id.layout_labuTable_toggle).setVisibility(View.GONE);
            TextView tv_shopOrder = view.findViewById(R.id.tv_labuTable_shopOrder);
            tv_shopOrder.setVisibility(View.VISIBLE);
            tv_shopOrder.setText("000000803356");
        }
        TextView tv_number = view.findViewById(R.id.tv_labuTable_number);
        tv_number.setText("" + (number + 1));

        LinearLayout layout_table = view.findViewById(R.id.layout_labuTable_detail);
        for (int i = 0; i < 7; i++) {
            View item = LayoutInflater.from(mContext).inflate(R.layout.layout_labu_table_item, null);
            TextView tv_size = item.findViewById(R.id.tv_labuTableItem_size);
            TextView tv_placeOrder = item.findViewById(R.id.tv_labuTableItem_placeOrder);
            TextView tv_yiLabu = item.findViewById(R.id.tv_labuTableItem_yiLabu);
            TextView tv_unLabu = item.findViewById(R.id.tv_labuTableItem_unLabu);

            if (i == 6) {
                tv_size.setText("合计");
            }

            layout_table.addView(item, mLayoutParams_weight1_horizontal);
        }

        LinearLayout layout_actionList = view.findViewById(R.id.layout_labuTable_action);
        for (int i = 0; i < 3; i++) {
            Button button = (Button) LayoutInflater.from(mContext).inflate(R.layout.layout_button_green, null);
            switch (i) {
                case 0:
                    button.setId(R.id.btn_firstLabu);
                    button.setText("首期拉布单");
                    break;
                case 1:
                    button.setId(R.id.btn_lostLabu);
                    button.setText("大货拉布单");
                    break;
                case 2:
                    button.setId(R.id.btn_addLabu);
                    button.setText("补料拉布单");
                    break;
            }
            button.setOnClickListener(this);
            layout_actionList.addView(button, mLayoutParams_right10);
        }

        CheckBox checkBox = view.findViewById(R.id.ckb_labuTable_toggle);
        checkBox.setTag(number);
        if (mOnCheckedChangeListener == null) {
            mOnCheckedChangeListener = new CheckedChangeListener();
        }
        checkBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
        return view;
    }

    private CheckedChangeListener mOnCheckedChangeListener;

    private class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int index = (int) buttonView.getTag();
            View childAt = mLayout_layoutList.getChildAt(index);
            if (isChecked) {
                childAt.findViewById(R.id.layout_labuTable_detail).setVisibility(View.GONE);
                childAt.findViewById(R.id.layout_labuTable_action).setVisibility(View.VISIBLE);
            } else {
                childAt.findViewById(R.id.layout_labuTable_detail).setVisibility(View.VISIBLE);
                childAt.findViewById(R.id.layout_labuTable_action).setVisibility(View.GONE);
            }
        }
    }
}
