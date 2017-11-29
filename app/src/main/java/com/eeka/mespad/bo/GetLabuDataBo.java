package com.eeka.mespad.bo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 获取拉布数据参数实体类
 * Created by Lenovo on 2017/11/28.
 */

public class GetLabuDataBo {

    private String RESOURCE_BO;
    @JSONField(name = "Z_LAYOUT_BO")
    private String Z_LAYOUT_BO;
    private String SHOP_ORDER_BO;
    private String ITEM_BO;
    private String USER_ID;

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getRESOURCE_BO() {
        return RESOURCE_BO;
    }

    public void setRESOURCE_BO(String RESOURCE_BO) {
        this.RESOURCE_BO = RESOURCE_BO;
    }

    public String getZ_LAYOUT_BO() {
        return Z_LAYOUT_BO;
    }

    public void setZ_LAYOUT_BO(String z_LAYOUT_BO) {
        Z_LAYOUT_BO = z_LAYOUT_BO;
    }

    public String getSHOP_ORDER_BO() {
        return SHOP_ORDER_BO;
    }

    public void setSHOP_ORDER_BO(String SHOP_ORDER_BO) {
        this.SHOP_ORDER_BO = SHOP_ORDER_BO;
    }

    public String getITEM_BO() {
        return ITEM_BO;
    }

    public void setITEM_BO(String ITEM_BO) {
        this.ITEM_BO = ITEM_BO;
    }
}
