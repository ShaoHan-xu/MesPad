package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.SubPackageInfoBo;
import com.eeka.mespad.utils.UnitUtil;

import java.util.List;

/**
 * 分包明细单界面
 */
public class SubPackageDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_subpackagedetail);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("分包单明细");

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String shopOrder = getIntent().getStringExtra("shopOrder");
        TextView tv_shopOrder = findViewById(R.id.tv_shopOrder);
        tv_shopOrder.setText(getString(R.string.shopOrder, shopOrder));

        String styleCode = getIntent().getStringExtra("item");
        TextView tv_item = findViewById(R.id.tv_item);
        tv_item.setText(getString(R.string.item, styleCode));

        SubPackageInfoBo data = (SubPackageInfoBo) getIntent().getSerializableExtra("data");

        TextView tv_workCenter = findViewById(R.id.tv_workCenter);
        tv_workCenter.setText(getString(R.string.workCenter, data.getCF_WORKCENTEER_DESC()));

        LinearLayout layout_detail = findViewById(R.id.layout_labuTable_detail);
        List<SubPackageInfoBo.SUBORDERBean> subOrder = data.getSUB_ORDER();
        if (subOrder != null) {
            LinearLayout.LayoutParams layoutParams
                    = new LinearLayout.LayoutParams(UnitUtil.dip2px(mContext, 50), ViewGroup.LayoutParams.MATCH_PARENT);

            int size = subOrder.size() + 2;//+2是为了显示字段行和"合计"行
            int item2All = 0;
            int item3All = 0;
            int item4All = 0;
            for (int i = 0; i < size; i++) {
                View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_labu_table_item, null);
                TextView tv_item1 = itemView.findViewById(R.id.tv_labuTableItem_size);
                TextView tv_item2 = itemView.findViewById(R.id.tv_labuTableItem_placeOrder);
                TextView tv_item3 = itemView.findViewById(R.id.tv_labuTableItem_yiLabu);
                TextView tv_item4 = itemView.findViewById(R.id.tv_labuTableItem_unLabu);

                if (i == 0) {
                    tv_item1.setText("码数");
                    tv_item2.setText("订单数");
                    tv_item3.setText("已分包数");
                    tv_item4.setText("未分包数");

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UnitUtil.dip2px(mContext, 70), ViewGroup.LayoutParams.MATCH_PARENT);
                    layout_detail.addView(itemView, params);
                    continue;
                } else if (i == size - 1) {
                    tv_item1.setText("汇总");
                    tv_item2.setText(item2All + "");
                    tv_item3.setText(item3All + "");
                    tv_item4.setText(item4All + "");
                } else {
                    SubPackageInfoBo.SUBORDERBean suborderBean = subOrder.get(i - 1);
                    int sizeAmount = suborderBean.getSIZE_AMOUNT();
                    int sizeFen = suborderBean.getSIZE_FEN();
                    int sizeLeft = suborderBean.getSIZE_LEFT();

                    item2All += sizeAmount;
                    item3All += sizeFen;
                    item4All += sizeLeft;

                    tv_item1.setText(suborderBean.getSIZE_CODE());
                    tv_item2.setText(sizeAmount + "");
                    tv_item3.setText(sizeFen + "");
                    tv_item4.setText(sizeLeft + "");
                }

                layout_detail.addView(itemView, layoutParams);
            }
        }

        List<SubPackageInfoBo.SUBDETAILBean> subDetail = data.getSUB_DETAIL();
        if (subDetail != null) {
            //第一次循环，找出最大行数
            int maxLength = 0;
            for (int i = 0; i < subDetail.size(); i++) {
                SubPackageInfoBo.SUBDETAILBean bean = subDetail.get(i);
                int num = bean.getNum();
                if (num > maxLength) {
                    maxLength = num;
                }
            }

            LinearLayout layout_detailList = findViewById(R.id.layout_detailList);
            //第二次循环，渲染数据
            for (int i = 0; i < subDetail.size(); i++) {
                SubPackageInfoBo.SUBDETAILBean bean = subDetail.get(i);

                View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_subpackage_detail_subitem, null);
                TextView tv_sizeCode = itemView.findViewById(R.id.tv_sizeCode);
                tv_sizeCode.setText(bean.getSizeCode());

                LinearLayout layout_subItem = itemView.findViewById(R.id.layout_subItem);

                List<SubPackageInfoBo.SUBDETAILBean.ItemBean> item = bean.getItem();
                if (item != null) {
                    for (int j = 0; j < maxLength; j++) {
                        View subItemView = LayoutInflater.from(mContext).inflate(R.layout.item_subpackage_detail_subitem, null);
                        if (j % 2 == 1){
                            subItemView.setBackgroundColor(getResources().getColor(R.color.bg_gray));
                        }
                        if (j < item.size()) {
                            SubPackageInfoBo.SUBDETAILBean.ItemBean itemBean = item.get(j);
                            TextView tv_packageNum = subItemView.findViewById(R.id.tv_packageNum);
                            TextView tv_qty = subItemView.findViewById(R.id.tv_qty);
                            tv_packageNum.setText(itemBean.getSUB_SEQ() + "");
                            tv_qty.setText(itemBean.getSUB_QTY() + "");
                        }
                        layout_subItem.addView(subItemView);
                    }
                }
                layout_detailList.addView(itemView);
            }
        }
    }

    public static Intent getIntent(Context context, SubPackageInfoBo data, String shopOrder, String item) {
        Intent intent = new Intent(context, SubPackageDetailActivity.class);
        intent.putExtra("data", data);
        intent.putExtra("shopOrder", shopOrder);
        intent.putExtra("item", item);
        return intent;
    }
}
