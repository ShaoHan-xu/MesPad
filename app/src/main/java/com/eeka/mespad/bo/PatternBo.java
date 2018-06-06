package com.eeka.mespad.bo;

public class PatternBo {

    /**
     * ORDER_NO : MTM18050800003-1
     * OPERATION : XH2
     * PICTURE_URL : http://10.7.123.47:9999/files/order/MTM/180508/MTM18050800003-1/MTM18050800003-1_YQ1005_2.jpg
     */

    private String ORDER_NO;
    private String OPERATION;
    private String PICTURE_URL;

    public String getORDER_NO() {
        return ORDER_NO;
    }

    public void setORDER_NO(String ORDER_NO) {
        this.ORDER_NO = ORDER_NO;
    }

    public String getOPERATION() {
        return OPERATION;
    }

    public void setOPERATION(String OPERATION) {
        this.OPERATION = OPERATION;
    }

    public String getPICTURE_URL() {
        return PICTURE_URL;
    }

    public void setPICTURE_URL(String PICTURE_URL) {
        this.PICTURE_URL = PICTURE_URL;
    }
}
