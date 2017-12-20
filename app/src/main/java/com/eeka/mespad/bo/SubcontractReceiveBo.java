package com.eeka.mespad.bo;

/**
 * Created by Lenovo on 2017/12/16.
 */

public class SubcontractReceiveBo {
    private String rfid;
    private String shopOrder;
    private String sfc;
    private String size;
    private boolean editAble;

    public boolean isEditAble() {
        return editAble;
    }

    public void setEditAble(boolean editAble) {
        this.editAble = editAble;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(String shopOrder) {
        this.shopOrder = shopOrder;
    }

    public String getSfc() {
        return sfc;
    }

    public void setSfc(String sfc) {
        this.sfc = sfc;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
