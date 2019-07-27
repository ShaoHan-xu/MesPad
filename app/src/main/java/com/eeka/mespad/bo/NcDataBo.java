package com.eeka.mespad.bo;

import java.io.Serializable;

public class NcDataBo implements Serializable {

    private String NC_CODE;
    private String NC_DESC;

    public String getNC_CODE() {
        return NC_CODE;
    }

    public void setNC_CODE(String NC_CODE) {
        this.NC_CODE = NC_CODE;
    }

    public String getNC_DESC() {
        return NC_DESC;
    }

    public void setNC_DESC(String NC_DESC) {
        this.NC_DESC = NC_DESC;
    }
}
