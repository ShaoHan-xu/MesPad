package com.eeka.mespad.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.SewDataBo;
import com.eeka.mespad.bo.SewQCDataBo;
import com.eeka.mespad.bo.TailorInfoBo;

/**
 * 统一获取标签栏工具类
 * Created by Lenovo on 2017/9/1.
 */
public class TabViewUtil {

    /**
     * @param listener 该标签被点击的事件
     */
    public static <T> View getTabView(Context context, T data, View.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tab, null);
        TextView tv_tabName = (TextView) view.findViewById(R.id.textView);
        tv_tabName.setOnClickListener(listener);
        String content = null;
        if (data instanceof SewQCDataBo.DesignComponentBean) {//生产部件
            final SewQCDataBo.DesignComponentBean item = (SewQCDataBo.DesignComponentBean) data;
            content = item.getDescription();
        } else if (data instanceof SewQCDataBo.DesignComponentBean.DesgComponentsBean) {//设计部件
            final SewQCDataBo.DesignComponentBean.DesgComponentsBean item = (SewQCDataBo.DesignComponentBean.DesgComponentsBean) data;
            content = item.getDescription();
        } else if (data instanceof TailorInfoBo.OPERINFORBean) {//工序标签
            TailorInfoBo.OPERINFORBean item = (TailorInfoBo.OPERINFORBean) data;
            content = item.getDESCRIPTION();
        } else if (data instanceof TailorInfoBo.MatInfoBean) {//物料标签
            TailorInfoBo.MatInfoBean matInfo = (TailorInfoBo.MatInfoBean) data;
            content = matInfo.getMAT_NO();
        } else if (data instanceof SewDataBo.SewAttr) {//缝制工序标签
            SewDataBo.SewAttr sewData = (SewDataBo.SewAttr) data;
            content = sewData.getDescription();
        }
        if (!TextUtils.isEmpty(content)) {
            int length = content.length();
            if (length > 10) {
                String str1 = content.substring(0, length / 2);
                String str2 = content.substring(length / 2, length);
                content = str1 + "\n" + str2;
            }
        }
        tv_tabName.setText(content);
        return view;
    }

    /**
     * 刷新标签视图
     */
    public static void refreshTabView(ViewGroup parent, int position) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            childAt.setBackgroundResource(R.color.white);
        }
        parent.getChildAt(position).setBackgroundResource(R.color.divider_gray);
    }

}
