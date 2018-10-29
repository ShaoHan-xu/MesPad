package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class SplitCardDialog extends BaseDialog {

    private String mCardNum;
    private String mProcessLot;
    private List<TailorInfoBo.CUTSIZESBean> mList_sizes;
    private LinearLayout mLayout_items;

    public SplitCardDialog(@NonNull Context context, String cardNum, String processLot, @NonNull List<TailorInfoBo.CUTSIZESBean> sizes) {
        super(context);
        mCardNum = cardNum;
        mProcessLot = processLot;
        mList_sizes = sizes;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_splitcard, null);
        setContentView(view);

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        mLayout_items = view.findViewById(R.id.layout_splitCard_item);
        for (TailorInfoBo.CUTSIZESBean item : mList_sizes) {
            View viewItem = LayoutInflater.from(mContext).inflate(R.layout.item_splitcard, null);
            mLayout_items.addView(viewItem);
            TextView tv_cardNum = viewItem.findViewById(R.id.tv_splitCard_cardNum);
            TextView tv_size = viewItem.findViewById(R.id.tv_splitCard_size);
            TextView tv_qty = viewItem.findViewById(R.id.tv_splitCard_qty);
            tv_cardNum.setText(mCardNum);
            tv_size.setText(item.getSIZE_CODE());
            tv_qty.setText(item.getQTY());

            final LinearLayout layout_childItemView = viewItem.findViewById(R.id.layout_splitCard_childItem);
            Button btn_add = viewItem.findViewById(R.id.btn_splitCard_add);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View childView = LayoutInflater.from(mContext).inflate(R.layout.item_splitcardchild, null);
                    layout_childItemView.addView(childView);



                    Button btn_del = childView.findViewById(R.id.btn_splitCard_delete);
                    btn_del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout_childItemView.removeView(childView);
                        }
                    });
                }
            });
        }
    }

    private void save() {
        List<SplitCardBo> list = new ArrayList<>();
        for (int i = 0; i < mLayout_items.getChildCount(); i++) {
            View itemView = mLayout_items.getChildAt(i);
            TextView tv_cardNum = itemView.findViewById(R.id.tv_splitCard_cardNum);
            TextView tv_size = itemView.findViewById(R.id.tv_splitCard_size);
            TextView tv_qty = itemView.findViewById(R.id.tv_splitCard_qty);
            SplitCardBo bo = new SplitCardBo();
            bo.setCardId(tv_cardNum.getText().toString());
            bo.setSubqty(tv_qty.getText().toString());
            bo.setSize(tv_size.getText().toString());
            list.add(bo);

            LinearLayout childItemView = itemView.findViewById(R.id.layout_splitCard_childItem);
            for (int j = 0; j < childItemView.getChildCount(); j++) {
                EditText et_cardNum = childItemView.findViewById(R.id.et_splitCard_cardNum);
                EditText et_qty = childItemView.findViewById(R.id.et_splitCard_qty);
                SplitCardBo bo1 = new SplitCardBo();
                bo1.setCardId(et_cardNum.getText().toString());
                bo1.setSubqty(et_qty.getText().toString());
                bo1.setSize(tv_size.getText().toString());
                list.add(bo);
            }
        }

        LoadingDialog.show(mContext);
        HttpHelper.splitCard(mProcessLot, list, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
                }
            }

            @Override
            public void onFailure(String url, int code, String message) {
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }

    public class SplitCardBo {
        private String cardId;
        private String subqty;
        private String size;

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getSubqty() {
            return subqty;
        }

        public void setSubqty(String subqty) {
            this.subqty = subqty;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }
}
