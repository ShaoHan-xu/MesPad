package com.eeka.mespad.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.RecordNCBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录裁剪不良界面
 * Created by Lenovo on 2017/7/19.
 */

public class RecordCutNCActivity extends BaseActivity {

    private TailorInfoBo mTailorInfo;
    private List<RecordNCBo> mList_badRecord;
    private ItemAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_record_cutnc);

        mTailorInfo = (TailorInfoBo) getIntent().getSerializableExtra("data");
        if (mTailorInfo == null) {
            toast("数据错误");
            return;
        }
        mList_badRecord = (List<RecordNCBo>) getIntent().getSerializableExtra("badList");
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        TextView tv_orderNum = (TextView) findViewById(R.id.tv_recordBad_orderNum);
        tv_orderNum.setText(mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER());

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        mAdapter = new ItemAdapter(mContext, mList_badRecord, R.layout.gv_item_recordnc, layoutManager);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gv_recordBad);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        findViewById(R.id.btn_recordBad_save).setOnClickListener(this);
        findViewById(R.id.btn_recordBad_cancel).setOnClickListener(this);

        if (mList_badRecord == null || mList_badRecord.size() == 0) {
            showLoading();
            HttpHelper.getBadList(this);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_recordBad_save) {
            final List<RecordNCBo> list = new ArrayList<>();
            for (RecordNCBo item : mList_badRecord) {
                if (item.getQTY() > 0) {
                    list.add(item);
                }
            }
            if (list.size() == 0) {
                toast("请选择要记录的不良代码");
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("确认保存所选的不良记录吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    save(list);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else if (v.getId() == R.id.btn_recordBad_cancel) {
            onBackPressed();
        }
    }

    private void save(List<RecordNCBo> list) {
        showLoading();
        JSONObject json = new JSONObject();
        json.put("RFID", mTailorInfo.getRFID());
        json.put("ORDER_TYPE", mTailorInfo.getOrderType());
        json.put("SHOP_ORDER", mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER());
        json.put("SHOP_ORDER_BO", mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER_BO());
        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        if (resource != null)
            json.put("RESOURCE_BO", resource.getRESOURCE_BO());
        json.put("NC_CODES", list);
        json.put("OPERATION", mTailorInfo.getOPER_INFOR().get(0).getOPERATION());
        json.put("OPERATION_BO", mTailorInfo.getOPER_INFOR().get(0).getOPERATION_BO());
        HttpHelper.saveBadRecord(json, this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("badList", (Serializable) mList_badRecord);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class ItemAdapter extends CommonRecyclerAdapter<RecordNCBo> {

        ItemAdapter(Context context, List<RecordNCBo> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, final RecordNCBo item, final int position) {
            TextView tv_count = holder.getView(R.id.tv_recordNc_count);
            final int[] badCount = {item.getQTY()};
            if (badCount[0] == 0) {
                tv_count.setVisibility(View.GONE);
            } else {
                tv_count.setVisibility(View.VISIBLE);
                tv_count.setText(String.valueOf(badCount[0]));
            }

            TextView tv_type = holder.getView(R.id.tv_recordNc_type);
            tv_type.setText(item.getDESCRIPTION());
            tv_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    badCount[0]++;
                    item.setQTY(badCount[0]);
                    notifyItemChanged(position);
                }
            });
            holder.getView(R.id.btn_recordNc_sub).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (badCount[0] > 0) {
                        badCount[0]--;
                        item.setQTY(badCount[0]);
                        notifyItemChanged(position);
                    }
                }
            });
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getBadList.equals(url)) {
                mList_badRecord = JSON.parseArray(resultJSON.getJSONArray("result").toString(), RecordNCBo.class);
                mAdapter.setData(mList_badRecord);
            } else if (HttpHelper.saveBadRecord.equals(url)) {
                toast("保存成功");
                //保存成功后清除记录并关闭界面
                mList_badRecord.clear();
                onBackPressed();
            }
        }
    }

    public static Intent getIntent(Context context, TailorInfoBo data, List<RecordNCBo> badList) {
        Intent intent = new Intent(context, RecordCutNCActivity.class);
        intent.putExtra("data", data);
        intent.putExtra("badList", (Serializable) badList);
        return intent;
    }
}
