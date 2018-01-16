package com.eeka.mespad.dragRecyclerViewHelper;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.UpdateSewNcBo;

import java.util.Collections;
import java.util.List;

/**
 * @author Paul Burke (ipaulpro)
 *         ★ RecyclerListAdapter要实现ItemTouchHelperAdapter
 *         重写onItemDismiss（删除条目）和onItemMove（移动条目）
 *         <p>
 *         ★ RecyclerView.ViewHolder实现ItemTouchHelperViewHolder
 *         重写onItemSelected（条目被选中时）和onItemClear（条目被拖拽之后）
 */
public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    private final OnDragStartListener mDragStartListener;
    private List<UpdateSewNcBo.NcCodeOperationListBean> mItems;

    public RecyclerListAdapter(List<UpdateSewNcBo.NcCodeOperationListBean> items, OnDragStartListener dragStartListener) {
        mItems = items;
        mDragStartListener = dragStartListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sewnc_selected, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.tv_code.setText(mItems.get(position).getDESCRIPTION());
        holder.tv_process.setText(mItems.get(position).getProcessDesc());

        holder.tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });

        holder.layout_dragView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onDragStarted(holder);
                }
                return false;
            }
        });
    }

    public void addItem(UpdateSewNcBo.NcCodeOperationListBean item) {
        mItems.add(item);
        notifyItemInserted(mItems.size());
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mItems.size());
    }

    //   当条目被删除时的操作（实现ItemTouchHelperAdapter重写onItemDismiss方法）
    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
//        通知条目已经被删除，与adapter互动
        notifyItemRemoved(position);
    }

    //   当条目被移动时的操作（实现ItemTouchHelperAdapter重写onItemMove方法）
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        TextView tv_code;
        TextView tv_process;
        TextView tv_del;
        LinearLayout layout_dragView;

        ItemViewHolder(View itemView) {
            super(itemView);
            tv_code = (TextView) itemView.findViewById(R.id.tv_recordSewNC_selected_code);
            tv_process = (TextView) itemView.findViewById(R.id.tv_recordSewNC_selected_process);
            tv_del = (TextView) itemView.findViewById(R.id.tv_recordSewNC_delSelected);
            layout_dragView = (LinearLayout) itemView.findViewById(R.id.layout_dragView);
        }

        @Override
        public void onItemSelected() {
//            当选中时，背景色为灰色
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
//            当拖拽完毕后，背景色为透明
            itemView.setBackgroundColor(Color.WHITE);
        }
    }
}
