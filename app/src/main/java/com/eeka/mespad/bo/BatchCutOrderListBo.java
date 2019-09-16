package com.eeka.mespad.bo;

public class BatchCutOrderListBo {

    /**
     * SHOP_ORDER_REF : ShopOrderBO:8081,000008002345
     * SHOP_ORDER : 000008002345
     * QTY_ORDERED : 280
     * ITEM : KF01031
     * CF_WORKCENTER_DESC : 于都海螺1
     * MARKET_TIME : 2019-08-03
     */

    private String SHOP_ORDER_REF;
    private String SHOP_ORDER;
    private int QTY_ORDERED;
    private String ITEM;
    private String CF_WORKCENTER_DESC;
    private String MARKET_TIME;
    private String STATUS;
    private String OPERATION_DESC;

    public String getOPERATION_DESC() {
        return OPERATION_DESC;
    }

    public void setOPERATION_DESC(String OPERATION_DESC) {
        this.OPERATION_DESC = OPERATION_DESC;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getSHOP_ORDER_REF() {
        return SHOP_ORDER_REF;
    }

    public void setSHOP_ORDER_REF(String SHOP_ORDER_REF) {
        this.SHOP_ORDER_REF = SHOP_ORDER_REF;
    }

    public String getSHOP_ORDER() {
        return SHOP_ORDER;
    }

    public void setSHOP_ORDER(String SHOP_ORDER) {
        this.SHOP_ORDER = SHOP_ORDER;
    }

    public int getQTY_ORDERED() {
        return QTY_ORDERED;
    }

    public void setQTY_ORDERED(int QTY_ORDERED) {
        this.QTY_ORDERED = QTY_ORDERED;
    }

    public String getITEM() {
        return ITEM;
    }

    public void setITEM(String ITEM) {
        this.ITEM = ITEM;
    }

    public String getCF_WORKCENTER_DESC() {
        return CF_WORKCENTER_DESC;
    }

    public void setCF_WORKCENTER_DESC(String CF_WORKCENTER_DESC) {
        this.CF_WORKCENTER_DESC = CF_WORKCENTER_DESC;
    }

    public String getMARKET_TIME() {
        return MARKET_TIME;
    }

    public void setMARKET_TIME(String MARKET_TIME) {
        this.MARKET_TIME = MARKET_TIME;
    }
}
