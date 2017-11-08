package com.eeka.mespad.bo;

/**
 * 字典实体类
 * Created by xushaohan on 17/11/7.
 */

public class DictionaryDataBo {
    public static final String CODE_STICKY = "ZpType";//获取粘朴数据的keyCode
    public static final String CODE_BIReason = "BlReason";//获取补料数据的code
    public static final String CODE_BTReason = "TlReason";//获取退料数据的code

    private String VALUE;
    private String LABEL;

    public String getVALUE() {
        return VALUE;
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
    }

    public String getLABEL() {
        return LABEL;
    }

    public void setLABEL(String LABEL) {
        this.LABEL = LABEL;
    }
}
