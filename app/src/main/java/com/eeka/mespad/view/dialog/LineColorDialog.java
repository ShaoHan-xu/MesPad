package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.LineColorBo;
import com.eeka.mespad.bo.SewDataBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 工位线色弹框
 * Created by Lenovo on 2018/3/31.
 */

public class LineColorDialog extends BaseDialog {

    private List<LineColorBo> mList_data;
    private ItemAdapter mAdapter;
    private String mOrderNum;

    public LineColorDialog(@NonNull Context context, String orderNum) {
        super(context);
        this.mOrderNum = orderNum;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_linecolor, null);
        setContentView(mView);

        final RecyclerView recyclerView = mView.findViewById(R.id.recyclerView_lineColor);

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        LoadingDialog.show(mContext);
        HttpHelper.getStitchInventory(mOrderNum, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    mList_data = JSONObject.parseArray(resultJSON.getJSONArray("result").toString(), LineColorBo.class);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new ItemAdapter(mContext, mList_data, R.layout.item_linecolor, layoutManager);
                    recyclerView.setAdapter(mAdapter);
                } else {
                    ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
                }
            }

            @Override
            public void onFailure(String url, int code, String message) {
                LoadingDialog.dismiss();
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    private class ItemAdapter extends CommonRecyclerAdapter<LineColorBo> {

        ItemAdapter(Context context, List<LineColorBo> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, final LineColorBo item, int position) {
            holder.setText(R.id.tv_lineColor_code, item.getMARTERIAL_CODE());
            holder.setText(R.id.tv_lineColor_desc, item.getMARTERIAL_NAME());
            holder.setText(R.id.tv_lineColor_partName, item.getPOSITION_NAME());

            TextView textView = holder.getView(R.id.tv_lineColor_isExteriorStitch);
            if ("Y".equals(item.getIS_EXTERIOR_STITCH())) {
                textView.setText("外观线迹");
            } else {
                textView.setText("常规用线");
            }

            ImageView imageView = holder.getView(R.id.iv_lineColor_img);
            Picasso.with(mContext).load(item.getMATERAIL_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> urls = new ArrayList<>();
                    urls.add(item.getMATERAIL_URL());
                    mContext.startActivity(ImageBrowserActivity.getIntent(mContext, urls, 0));
                }
            });
        }
    }

}
