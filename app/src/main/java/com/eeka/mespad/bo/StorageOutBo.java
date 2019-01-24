package com.eeka.mespad.bo;

public class StorageOutBo {

    /**
     * SHOP_ORDER : 000060007228
     * WORK_CENTER : YDHL002
     * ITEM : KV01273
     * RFID : 1090595656
     * SIZE : 40
     * STOR_AREA : A
     * STOR_LOCATION : A-3
     * STOR_QUANTITY : 50
     * OUT_QUANTITY : 20
     * SURPLUS_QUANTITY : 30
     */

    private String SHOP_ORDER;
    private String WORK_CENTER;
    private String ITEM;
    private String RFID;
    private String SIZE;
    private String CLOTH_TYPE;
    private String STOR_AREA;
    private String STOR_LOCATION;
    private String STOR_QUANTITY;
    private String USER_ID;
    private String OUT_QUANTITY;
    private String SURPLUS_QUANTITY;
    private String QUANTITY;

    public String getCLOTH_TYPE() {
        return CLOTH_TYPE;
    }

    public void setCLOTH_TYPE(String CLOTH_TYPE) {
        this.CLOTH_TYPE = CLOTH_TYPE;
    }

    public String getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(String QUANTITY) {
        this.QUANTITY = QUANTITY;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getSHOP_ORDER() {
        return SHOP_ORDER;
    }

    public void setSHOP_ORDER(String SHOP_ORDER) {
        this.SHOP_ORDER = SHOP_ORDER;
    }

    public String getWORK_CENTER() {
        return WORK_CENTER;
    }

    public void setWORK_CENTER(String WORK_CENTER) {
        this.WORK_CENTER = WORK_CENTER;
    }

    public String getITEM() {
        return ITEM;
    }

    public void setITEM(String ITEM) {
        this.ITEM = ITEM;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getSIZE() {
        return SIZE;
    }

    public void setSIZE(String SIZE) {
        this.SIZE = SIZE;
    }

    public String getSTOR_AREA() {
        return STOR_AREA;
    }

    public void setSTOR_AREA(String STOR_AREA) {
        this.STOR_AREA = STOR_AREA;
    }

    public String getSTOR_LOCATION() {
        return STOR_LOCATION;
    }

    public void setSTOR_LOCATION(String STOR_LOCATION) {
        this.STOR_LOCATION = STOR_LOCATION;
    }

    public String getSTOR_QUANTITY() {
        return STOR_QUANTITY;
    }

    public void setSTOR_QUANTITY(String STOR_QUANTITY) {
        this.STOR_QUANTITY = STOR_QUANTITY;
    }

    public String getOUT_QUANTITY() {
        return OUT_QUANTITY;
    }

    public void setOUT_QUANTITY(String OUT_QUANTITY) {
        this.OUT_QUANTITY = OUT_QUANTITY;
    }

    public String getSURPLUS_QUANTITY() {
        return SURPLUS_QUANTITY;
    }

    public void setSURPLUS_QUANTITY(String SURPLUS_QUANTITY) {
        this.SURPLUS_QUANTITY = SURPLUS_QUANTITY;
    }
}
