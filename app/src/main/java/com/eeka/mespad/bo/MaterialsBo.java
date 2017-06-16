package com.eeka.mespad.bo;

/**
 * Created by Lenovo on 2017/6/16.
 */

public class MaterialsBo {
    private String picUrl;
    private String name;

    public MaterialsBo(String picUrl, String name) {
        this.picUrl = picUrl;
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
