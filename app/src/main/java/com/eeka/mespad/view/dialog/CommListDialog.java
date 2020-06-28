package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

/**
 * 通用列表弹框
 * Created by Lenovo on 2018/2/28.
 */

public class CommListDialog<T> extends BaseDialog {

    public enum TYPE {
        VIDEO
    }

    private TYPE mType;
    private List<T> mList;

    public CommListDialog(@NonNull Context context, TYPE type, List<T> list) {
        super(context);
        mType = type;
        mList = list;
        init();
    }

    @Override
    protected void init() {
        super.init();
        setCanceledOnTouchOutside(true);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_commlist, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        if (mType == TYPE.VIDEO) {
            tv_title.setText("选择播放的工序视频");
        }

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ListView listView = mView.findViewById(R.id.lv_commList);
        listView.setAdapter(new CommonAdapter<T>(mContext, mList, R.layout.item_text_wrap) {
            @Override
            public void convert(ViewHolder holder, T item, int position) {
                TextView textView = holder.getView(R.id.textView);
                if (mType == TYPE.VIDEO) {
                    JSONObject json = (JSONObject) item;
                    textView.setText(json.getString("operationDesc"));
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                T t = mList.get(position);
                if (mType == TYPE.VIDEO) {
                    JSONObject object = (JSONObject) t;
                    String videoUrl = object.getString("videoUrl");
                    if (!isEmpty(videoUrl)) {
                        SystemUtils.playVideo(mContext, videoUrl);
                    } else {
                        ErrorDialog.showAlert(mContext, "该工序无视频");
                    }
                }
            }
        });
    }

    @Override
    public void show() {
        showOri();
    }
}
