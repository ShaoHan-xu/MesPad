package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.bo.ProcessSheetsBo;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.UnitUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProcessSheetsDialog extends BaseDialog {

    private ProcessSheetsBo mData;
    private LinearLayout mLayout_pattern;
    private LinearLayout mLayout_sizeCode;

    public ProcessSheetsDialog(@NonNull Context context, ProcessSheetsBo data) {
        super(context);
        mData = data;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_processsheets, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("工艺单");
        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView tv_style = mView.findViewById(R.id.tv_processSheets_style);
        tv_style.setText(mData.getStyleCode());
        TextView tv_craftsman = mView.findViewById(R.id.tv_processSheets_craftsman);
        tv_craftsman.setText(mData.getCraftsman());
        TextView tv_styleName = mView.findViewById(R.id.tv_processSheets_styleName);
        tv_styleName.setText(mData.getStyleName());
        TextView tv_patternNumber = mView.findViewById(R.id.tv_processSheets_patternNumber);
        tv_patternNumber.setText(mData.getPatternNumber());
        TextView tv_season = mView.findViewById(R.id.tv_processSheets_season);
        tv_season.setText(mData.getSeason());
        TextView tv_year = mView.findViewById(R.id.tv_processSheets_year);
        tv_year.setText(mData.getYear());
        TextView tv_designer = mView.findViewById(R.id.tv_processSheets_designer);
        tv_designer.setText(mData.getDesigner());
        TextView tv_band = mView.findViewById(R.id.tv_processSheets_band);
        tv_band.setText(mData.getBand());
        TextView tv_planner = mView.findViewById(R.id.tv_processSheets_planner);
        tv_planner.setText(mData.getPlanner());

        mLayout_pattern = mView.findViewById(R.id.layout_processSheets_pattern);
        mLayout_sizeCode = mView.findViewById(R.id.layout_processSheets_sizeCode);
        initSizeView();

        initTechnology();

        ImageView imageView = mView.findViewById(R.id.iv_processSheets_img);
        Picasso.with(mContext).load(mData.getStyleImageURL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(ImageBrowserActivity.getIntent(mContext, mData.getStyleImageURL()));
            }
        });
    }

    private void initTechnology() {
        LinearLayout layout_technology = mView.findViewById(R.id.layout_processSheets_technology);
        List<ProcessSheetsBo.TechnologyListBean> technologyList = mData.getTechnologyList();
        for (ProcessSheetsBo.TechnologyListBean item : technologyList) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_processsheets_techology, null);
            TextView tv_patternName = view.findViewById(R.id.tv_processSheets_patternName);
            tv_patternName.setText(item.getName());
            TextView tv_desc = view.findViewById(R.id.tv_processSheets_technologyDesc);
            tv_desc.setText(item.getTechnologyRemark());
            layout_technology.addView(view);
        }
    }

    private void initSizeView() {
        List<ProcessSheetsBo.SpecsBean> technologyList = mData.getSpecs();
        for (ProcessSheetsBo.SpecsBean item : technologyList) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_processsheets_size, null);
            TextView tv_itemName = view.findViewById(R.id.tv_processSheets_itemName);
            tv_itemName.setText(item.getSpecName());
            TextView tv_measureMethod = view.findViewById(R.id.tv_processSheets_measureMethod);
            tv_measureMethod.setText(item.getMeasureMethod());
            TextView tv_tolerance = view.findViewById(R.id.tv_processSheets_tolerance);
            tv_tolerance.setText(item.getTolerance());
            TextView tv_patternLooseness = view.findViewById(R.id.tv_processSheets_patternLooseness);
            tv_patternLooseness.setText(item.getPatternLooseness());

            LinearLayout layout_size = view.findViewById(R.id.layout_processSheets_sizeValue);
            List<ProcessSheetsBo.SpecsBean.SizesBean> sizes = item.getSizes();
            if (mLayout_sizeCode.getChildCount() == 0) {
                for (ProcessSheetsBo.SpecsBean.SizesBean size : sizes) {
                    mLayout_sizeCode.addView(getSizeView(size.getSizeCode()));
                }
            }
            for (ProcessSheetsBo.SpecsBean.SizesBean size : sizes) {
                layout_size.addView(getSizeView(size.getSizeValue()));
            }

            mLayout_pattern.addView(view);
        }
    }

    private View getSizeView(String text) {
        View childView = LayoutInflater.from(mContext).inflate(R.layout.item_textview, null);
        TextView tv_size = childView.findViewById(R.id.textView);
        tv_size.setWidth((int) mContext.getResources().getDimension(R.dimen.processSheets_sizeWidth));
        int padding = UnitUtil.dip2px(mContext, 5);
        tv_size.setPadding(padding, padding, padding, padding);
        tv_size.setGravity(Gravity.CENTER);
        tv_size.setText(text);
        return childView;
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.8), (SystemUtils.getScreenHeight(mContext)));
    }
}
