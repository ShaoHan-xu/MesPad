package com.eeka.mespad.bo;

public class PocketSizeBo {
    private String ORDER_NO;
    private String MEASURED_ATTRIBUTE;
    private String VALUE;
    private String PART_NO;
    private String PICTURE_URL;

    public String getPART_NO() {
        return PART_NO;
    }

    public void setPART_NO(String PART_NO) {
        this.PART_NO = PART_NO;
    }

    public String getPICTURE_URL() {
        return PICTURE_URL;
    }

    public void setPICTURE_URL(String PICTURE_URL) {
        this.PICTURE_URL = PICTURE_URL;
    }

    public String getORDER_NO() {
        return ORDER_NO;
    }

    public void setORDER_NO(String ORDER_NO) {
        this.ORDER_NO = ORDER_NO;
    }

    public String getMEASURED_ATTRIBUTE() {
        return MEASURED_ATTRIBUTE;
    }

    public void setMEASURED_ATTRIBUTE(String MEASURED_ATTRIBUTE) {
        this.MEASURED_ATTRIBUTE = MEASURED_ATTRIBUTE;
    }

    public String getVALUE() {
        return VALUE;
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
    }
}
