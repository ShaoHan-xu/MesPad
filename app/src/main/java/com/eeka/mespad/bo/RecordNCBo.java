package com.eeka.mespad.bo;

import java.io.Serializable;

/**
 * 记录不良实体类
 * Created by Lenovo on 2017/7/20.
 */

public class RecordNCBo implements Serializable {
    private String NC_CODE;
    private String NC_CODE_BO;
    private String DESCRIPTION;
    private int QTY;

    public RecordNCBo() {
    }

    public RecordNCBo(String DESCRIPTION, int count) {
        this.DESCRIPTION = DESCRIPTION;
        this.QTY = count;
    }

    public String getNC_CODE() {
        return NC_CODE;
    }

    public void setNC_CODE(String NC_CODE) {
        this.NC_CODE = NC_CODE;
    }

    public String getNC_CODE_BO() {
        return NC_CODE_BO;
    }

    public void setNC_CODE_BO(String NC_CODE_BO) {
        this.NC_CODE_BO = NC_CODE_BO;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public int getQTY() {
        return QTY;
    }

    public void setQTY(int QTY) {
        this.QTY = QTY;
    }

}
