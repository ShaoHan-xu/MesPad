package com.eeka.mespad.bo;

import java.util.List;

/**
 * 开始、完成作业时上传参数的实体类
 * Created by Lenovo on 2017/7/10.
 */

public class StartWorkParamsBo {

    private String PAD_ID;
    private String SHOP_ORDER;
    private String SHOP_ORDER_BO;
    private String PROCESS_LOT;
    private String PROCESS_LOT_BO;
    private String RESOURCE_BO;
    private List<String> OPERATIONS;

    //批量订单字段
    private int LAYERS;

    //定制订单字段
    private int ORDER_QTY;

    public int getORDER_QTY() {
        return ORDER_QTY;
    }

    public void setORDER_QTY(int ORDER_QTY) {
        this.ORDER_QTY = ORDER_QTY;
    }

    public int getLAYERS() {
        return LAYERS;
    }

    public void setLAYERS(int LAYERS) {
        this.LAYERS = LAYERS;
    }

    public String getPAD_ID() {
        return PAD_ID;
    }

    public void setPAD_ID(String PAD_ID) {
        this.PAD_ID = PAD_ID;
    }

    public String getSHOP_ORDER() {
        return SHOP_ORDER;
    }

    public void setSHOP_ORDER(String SHOP_ORDER) {
        this.SHOP_ORDER = SHOP_ORDER;
    }

    public String getSHOP_ORDER_BO() {
        return SHOP_ORDER_BO;
    }

    public void setSHOP_ORDER_BO(String SHOP_ORDER_BO) {
        this.SHOP_ORDER_BO = SHOP_ORDER_BO;
    }

    public String getPROCESS_LOT() {
        return PROCESS_LOT;
    }

    public void setPROCESS_LOT(String PROCESS_LOT) {
        this.PROCESS_LOT = PROCESS_LOT;
    }

    public String getPROCESS_LOT_BO() {
        return PROCESS_LOT_BO;
    }

    public void setPROCESS_LOT_BO(String PROCESS_LOT_BO) {
        this.PROCESS_LOT_BO = PROCESS_LOT_BO;
    }

    public String getRESOURCE_BO() {
        return RESOURCE_BO;
    }

    public void setRESOURCE_BO(String RESOURCE_BO) {
        this.RESOURCE_BO = RESOURCE_BO;
    }

    public List<String> getOPERATIONS() {
        return OPERATIONS;
    }

    public void setOPERATIONS(List<String> OPERATIONS) {
        this.OPERATIONS = OPERATIONS;
    }

}
