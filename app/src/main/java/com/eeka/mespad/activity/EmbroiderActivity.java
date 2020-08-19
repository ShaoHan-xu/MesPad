package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.EmbroiderBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.TabViewUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 绣花界面
 */
public class EmbroiderActivity extends BaseActivity {

    private String mSFC;
    private String mProcessLotBo;
    private String mTheme;
    private List<EmbroiderBo> mList_data;

    private LinearLayout mLayout_process;
    private TextView mTv_partCode;
    private TextView mTv_partName;
    private TextView mTv_desc;
    private TextView mTv_request;
    private TextView mTv_artist;
    private ImageView mIv_embroider;
    private ImageView mIv_pattern;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_embroider);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mLayout_process = findViewById(R.id.layout_embroider_process);
        mTv_partCode = findViewById(R.id.tv_embroider_part);
        mTv_partName = findViewById(R.id.tv_embroider_partName);
        mTv_desc = findViewById(R.id.tv_embroider_desc);
        mTv_request = findViewById(R.id.tv_embroider_qualityRequest);
        mTv_artist = findViewById(R.id.tv_embroider_artist);

        mIv_embroider = findViewById(R.id.iv_embroider_embroider);
        mIv_pattern = findViewById(R.id.iv_embroider_pattern);
    }

    @Override
    protected void initData() {
        super.initData();

        showLoading();

        Intent intent = getIntent();
        mSFC = intent.getStringExtra("sfc");
        mProcessLotBo = intent.getStringExtra("processLotBo");
        mTheme = intent.getStringExtra("theme");

        HttpHelper.getEmbroiderInfor(mSFC, mProcessLotBo, mTheme, this);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            JSONArray result = resultJSON.getJSONArray("result");
            mList_data = JSON.parseArray(result.toJSONString(), EmbroiderBo.class);

            for (int i = 0; i < mList_data.size(); i++) {
                final EmbroiderBo item = mList_data.get(i);
                final int finalI = i;
                mLayout_process.addView(TabViewUtil.getTabView(mContext, item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TabViewUtil.refreshTabView(mContext, mLayout_process, finalI);
                        setupView(item);
                    }
                }));
            }
            if (mList_data != null && mList_data.size() != 0) {
                setupView(mList_data.get(0));
                TabViewUtil.refreshTabView(mContext, mLayout_process, 0);
            }
        }
    }

    private void setupView(EmbroiderBo item) {
        mTv_partCode.setText(item.getPART_NO());
        mTv_partName.setText(item.getPART_NAME());
        mTv_desc.setText(item.getOPERATION_INSTRUCTION());
        mTv_request.setText(item.getQUALITY_REQUIREMENT());
        mTv_artist.setText(item.getPATT_DESC());

        String image_url = item.getIMAGE_URL();
        if (!isEmpty(image_url))
            Picasso.with(mContext).load(image_url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(mIv_embroider);
        String picture_url = item.getPICTURE_URL();
        if (!isEmpty(picture_url))
            Picasso.with(mContext).load(picture_url).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(mIv_pattern);
    }

    public static Intent getIntent(Context context, String sfc, String processLotBo, String theme) {
        Intent intent = new Intent(context, EmbroiderActivity.class);
        intent.putExtra("sfc", sfc);
        intent.putExtra("processLotBo", processLotBo);
        intent.putExtra("theme", theme);
        return intent;
    }
}
