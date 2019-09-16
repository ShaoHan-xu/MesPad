package com.eeka.mespad.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用RecyclerViewAdapter Created by xsh on 2016/5/4.
 */
public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    protected Context mContext;
    protected List<T> mList;
    private int mLayoutId;
    private RecyclerView.LayoutManager mLayoutManager;
    protected Map<View, OnWidgetClickListener> mClickListeners;

    public CommonRecyclerAdapter(Context context, List<T> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutId = layoutId;
        this.mLayoutManager = layoutManager;
        mClickListeners = new HashMap<>();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder holder = RecyclerViewHolder.get(mContext, mLayoutId, parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        convert(holder, mList.get(position), position);
        bindingClickListener(holder, position);
    }

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size();
    }

    public abstract void convert(RecyclerViewHolder holder, T item, int position);

    /**
     * 设置数据
     */
    public void notifyDataSetChanged(List<T> datas) {
        mList = datas;
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void addData(T data) {
        mList.add(data);
        notifyItemInserted(mList.size());
        if (mLayoutManager != null)
            mLayoutManager.scrollToPosition(mList.size() - 1);
    }

    /**
     * 添加数据到指定位置
     *
     * @param data
     * @param index 指定位置
     */
    public void addData(int index, T data) {
        if (index == mList.size()) {
            addData(data);
            return;
        }
        mList.add(index, data);
        notifyItemInserted(index);
        mHandler.sendEmptyMessageDelayed(0, 500);
        if (mLayoutManager != null && index == mList.size() - 1)
            mLayoutManager.scrollToPosition(mList.size() - 1);
    }

    /**
     * 移除数据
     *
     * @param position
     */
    public void removeData(int position) {
        if (position < 0 || position >= mList.size()) {
            throw new IndexOutOfBoundsException("===============adapter removeData IndexOutOfBoundsException==================");
        }
        mList.remove(position);
        notifyItemRemoved(position);
        if (position != mList.size())
            notifyItemRangeChanged(position, mList.size() - position + 1);
    }

    /**
     * 此处统一绑定item内各个单击事件。(调用setWidgetClickListener)<br>
     * 复写onWidgetClick方法、并在其内处理点击事件
     *
     * @param holder
     */
    public void bindingClickListener(RecyclerViewHolder holder, int position) {

    }

    public void onClick(View v, int position) {

    }

    /**
     * 设置列表内控件监听器、在此处设置可避免重复创建监听器
     *
     * @param holder
     * @param position
     * @param viewId
     */
    public void setWidgetClickListener(RecyclerViewHolder holder, final int position, int viewId) {
        View view = holder.getView(viewId);
        OnWidgetClickListener clickListener = mClickListeners.get(view);
        if (clickListener == null) {
            clickListener = new OnWidgetClickListener(position);
            mClickListeners.put(view, clickListener);
        } else {
            clickListener.updateData(position);
        }
        if (view != null)
            view.setOnClickListener(clickListener);
    }

    private class OnWidgetClickListener implements View.OnClickListener {

        private int position;

        public OnWidgetClickListener(final int position) {
            super();
            this.position = position;
        }

        public void updateData(final int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            CommonRecyclerAdapter.this.onClick(v, position);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0)
                notifyDataSetChanged();
        }
    };

}
