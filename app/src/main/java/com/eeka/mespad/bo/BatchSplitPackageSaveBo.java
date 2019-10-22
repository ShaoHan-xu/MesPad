package com.eeka.mespad.bo;

import java.util.List;

public class BatchSplitPackageSaveBo {

    /**
     * subOrder : 000008000044001
     * shopOrder : 000008000044
     * item : KA01117
     * shopOrderRef : ShopOrderBO:8081,000008002271
     * subSeq : 1
     * layOutRef : ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044
     * sizeCode : 36
     * subPackages : [{"subPackageSeq":1,"subPackageQty":1}]
     */

    private String subOrder;
    private String shopOrder;
    private String item;
    private String shopOrderRef;
    private int subSeq;
    private int cutNum;
    private String layOutRef;
    private String sizeCode;
    private String materialType;
    private List<SubPackagesBean> subPackages;

    public int getCutNum() {
        return cutNum;
    }

    public void setCutNum(int cutNum) {
        this.cutNum = cutNum;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getSubOrder() {
        return subOrder;
    }

    public void setSubOrder(String subOrder) {
        this.subOrder = subOrder;
    }

    public String getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(String shopOrder) {
        this.shopOrder = shopOrder;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getShopOrderRef() {
        return shopOrderRef;
    }

    public void setShopOrderRef(String shopOrderRef) {
        this.shopOrderRef = shopOrderRef;
    }

    public int getSubSeq() {
        return subSeq;
    }

    public void setSubSeq(int subSeq) {
        this.subSeq = subSeq;
    }

    public String getLayOutRef() {
        return layOutRef;
    }

    public void setLayOutRef(String layOutRef) {
        this.layOutRef = layOutRef;
    }

    public String getSizeCode() {
        return sizeCode;
    }

    public void setSizeCode(String sizeCode) {
        this.sizeCode = sizeCode;
    }

    public List<SubPackagesBean> getSubPackages() {
        return subPackages;
    }

    public void setSubPackages(List<SubPackagesBean> subPackages) {
        this.subPackages = subPackages;
    }

    public static class SubPackagesBean {
        /**
         * subPackageSeq : 1
         * subPackageQty : 1
         */

        private String subPackageSeq;
        private String subPackageQty;

        public String getSubPackageSeq() {
            return subPackageSeq;
        }

        public void setSubPackageSeq(String subPackageSeq) {
            this.subPackageSeq = subPackageSeq;
        }

        public String getSubPackageQty() {
            return subPackageQty;
        }

        public void setSubPackageQty(String subPackageQty) {
            this.subPackageQty = subPackageQty;
        }
    }
}
