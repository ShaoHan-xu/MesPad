package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;

/**
 * 通用列表弹框
 * Created by Lenovo on 2018/2/28.
 */

public class CommListDialog extends BaseDialog {

    public CommListDialog(@NonNull Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        setCanceledOnTouchOutside(true);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_commlist,null);
        setContentView(view);

        ListView listView = (ListView) view.findViewById(R.id.lv_commList);
    }
}
