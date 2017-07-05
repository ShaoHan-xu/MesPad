package com.eeka.mespad.bo;

import java.io.Serializable;

/**
 * Created by Lenovo on 2017/7/4.
 */

public class RecordLabuMaterialInfoBo implements Serializable {
    private String materialNum;
    private String juanHao;//卷号
    private String length;
    private String layers;
    private String left;//余料
    private String remark;

    public String getMaterialNum() {
        return materialNum;
    }

    public void setMaterialNum(String materialNum) {
        this.materialNum = materialNum;
    }

    public String getJuanHao() {
        return juanHao;
    }

    public void setJuanHao(String juanHao) {
        this.juanHao = juanHao;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLayers() {
        return layers;
    }

    public void setLayers(String layers) {
        this.layers = layers;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
