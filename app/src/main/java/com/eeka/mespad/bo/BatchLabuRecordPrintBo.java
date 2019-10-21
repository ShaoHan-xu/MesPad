package com.eeka.mespad.bo;

import java.util.List;

public class BatchLabuRecordPrintBo {

    private List<PostBatchRecordLabuBo.CutSizeBean> sizeList;
    private String item;
    private String shopOrder;
    private String rabOrder;
    private String matType;

    public String getMatType() {
        return matType;
    }

    public void setMatType(String matType) {
        this.matType = matType;
    }

    public List<PostBatchRecordLabuBo.CutSizeBean> getSizeList() {
        return sizeList;
    }

    public void setSizeList(List<PostBatchRecordLabuBo.CutSizeBean> sizeList) {
        this.sizeList = sizeList;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(String shopOrder) {
        this.shopOrder = shopOrder;
    }

    public String getRabOrder() {
        return rabOrder;
    }

    public void setRabOrder(String rabOrder) {
        this.rabOrder = rabOrder;
    }

    public static class SizeBean {
        private String sizeCode;
        private String layer;

        public String getSizeCode() {
            return sizeCode;
        }

        public void setSizeCode(String sizeCode) {
            this.sizeCode = sizeCode;
        }

        public String getLayer() {
            return layer;
        }

        public void setLayer(String layer) {
            this.layer = layer;
        }
    }
}
