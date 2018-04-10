package com.eeka.mespad.bo;

public class LineColorBo {
    /**
     * ORDER_NO : aaabbb
     * MARTERIAL_CODE : 111
     * MARTERIAL_NAME : 222
     * POSITION_NAME : 333
     * IS_EXTERIOR_STITCH : Y
     * MATERAIL_URL : aaa
     */

    private String ORDER_NO;
    private String MARTERIAL_CODE;
    private String MARTERIAL_NAME;
    private String POSITION_NAME;
    private String IS_EXTERIOR_STITCH;
    private String MATERAIL_URL;

    public String getORDER_NO() {
        return ORDER_NO;
    }

    public void setORDER_NO(String ORDER_NO) {
        this.ORDER_NO = ORDER_NO;
    }

    public String getMARTERIAL_CODE() {
        return MARTERIAL_CODE;
    }

    public void setMARTERIAL_CODE(String MARTERIAL_CODE) {
        this.MARTERIAL_CODE = MARTERIAL_CODE;
    }

    public String getMARTERIAL_NAME() {
        return MARTERIAL_NAME;
    }

    public void setMARTERIAL_NAME(String MARTERIAL_NAME) {
        this.MARTERIAL_NAME = MARTERIAL_NAME;
    }

    public String getPOSITION_NAME() {
        return POSITION_NAME;
    }

    public void setPOSITION_NAME(String POSITION_NAME) {
        this.POSITION_NAME = POSITION_NAME;
    }

    public String getIS_EXTERIOR_STITCH() {
        return IS_EXTERIOR_STITCH;
    }

    public void setIS_EXTERIOR_STITCH(String IS_EXTERIOR_STITCH) {
        this.IS_EXTERIOR_STITCH = IS_EXTERIOR_STITCH;
    }

    public String getMATERAIL_URL() {
        return MATERAIL_URL;
    }

    public void setMATERAIL_URL(String MATERAIL_URL) {
        this.MATERAIL_URL = MATERAIL_URL;
    }
}
