package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ErrToastDialog extends BaseDialog {

    private List<String> mList_msg;
    private ItemAdapter mAdapter;

    public ErrToastDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public ErrToastDialog(@NonNull Context context, int theme) {
        super(context, theme);
        init();
    }

    @Override
    protected void init() {
        super.init();

        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_errtoast, null);
        setContentView(mView);

        Window window = getWindow();
        window.setGravity(Gravity.TOP);
        //设置不遮挡背景事件
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        RecyclerView mRecyclerView = mView.findViewById(R.id.recyclerView_errToast);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mList_msg = new ArrayList<>();
        mAdapter = new ItemAdapter(mContext, mList_msg, R.layout.item_text_wrap, layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mView.findViewById(R.id.closeAllMsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mList_msg.clear();
            }
        });
    }

    public void addMsg(String msg) {
        if (mList_msg.size() >= 3) {
            mAdapter.removeData(2);
        }
        mAdapter.addData(0, msg);
    }

    private class ItemAdapter extends CommonRecyclerAdapter<String> {

        ItemAdapter(Context context, List<String> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, String item, int position) {
            TextView textView = holder.getView(R.id.textView);
            textView.setTextColor(mContext.getResources().getColor(R.color.text_red_default));
            textView.setText(item);
        }
    }
}
