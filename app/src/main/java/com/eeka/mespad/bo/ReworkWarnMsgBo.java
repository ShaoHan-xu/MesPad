package com.eeka.mespad.bo;

public class ReworkWarnMsgBo {
    private String OPER_DESC;
    private String NC_DESC;
    private String USER_ID;
    private String USER_NAME;
    private String HANGER_ID;
    private String SFC;
    private String LINE_CATEGORY;

    public String getLINE_CATEGORY() {
        return LINE_CATEGORY;
    }

    public void setLINE_CATEGORY(String LINE_CATEGORY) {
        this.LINE_CATEGORY = LINE_CATEGORY;
    }

    public String getHANGER_ID() {
        return HANGER_ID;
    }

    public void setHANGER_ID(String HANGER_ID) {
        this.HANGER_ID = HANGER_ID;
    }

    public String getSFC() {
        return SFC;
    }

    public void setSFC(String SFC) {
        this.SFC = SFC;
    }

    public String getOPER_DESC() {
        return OPER_DESC;
    }

    public void setOPER_DESC(String OPER_DESC) {
        this.OPER_DESC = OPER_DESC;
    }

    public String getNC_DESC() {
        return NC_DESC;
    }

    public void setNC_DESC(String NC_DESC) {
        this.NC_DESC = NC_DESC;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }
}
