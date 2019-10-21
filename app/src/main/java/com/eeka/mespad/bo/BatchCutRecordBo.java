package com.eeka.mespad.bo;

import java.io.Serializable;
import java.util.List;

public class BatchCutRecordBo implements Serializable {

    /**
     * site : 8081
     * rabRef : ZBulkRabBO:8081,ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044,000008000044001
     * shopOrderRef : ShopOrderBO:8081,000008000044
     * layOutRef : ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044
     * operation : DS
     * workNo : 000008000044001
     * workSeq : 1
     * materialType : M
     * cutSizes : [{"sizeCode":"36","sizeTotal":1,"sizeFen":0,"sizeLeft":1},{"sizeCode":"38","sizeTotal":1,"sizeFen":0,"sizeLeft":1}]
     */

    private String site;
    private String rabRef;
    private String rabNo;
    private String shopOrder;
    private String shopOrderRef;
    private String layOutRef;
    private String operation;
    private String operationBo;
    private String workNo;
    private int workSeq;
    private String materialType;
    private String isFinish;
    private String item;
    private String layoutNo;
    private String layoutImg;
    private List<CutSizesBean> cutSizes;

    public String getRabNo() {
        return rabNo;
    }

    public void setRabNo(String rabNo) {
        this.rabNo = rabNo;
    }

    public String getLayoutImg() {
        return layoutImg;
    }

    public void setLayoutImg(String layoutImg) {
        this.layoutImg = layoutImg;
    }

    public String getOperationBo() {
        return operationBo;
    }

    public void setOperationBo(String operationBo) {
        this.operationBo = operationBo;
    }

    public String getLayoutNo() {
        return layoutNo;
    }

    public void setLayoutNo(String layoutNo) {
        this.layoutNo = layoutNo;
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

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getRabRef() {
        return rabRef;
    }

    public void setRabRef(String rabRef) {
        this.rabRef = rabRef;
    }

    public String getShopOrderRef() {
        return shopOrderRef;
    }

    public void setShopOrderRef(String shopOrderRef) {
        this.shopOrderRef = shopOrderRef;
    }

    public String getLayOutRef() {
        return layOutRef;
    }

    public void setLayOutRef(String layOutRef) {
        this.layOutRef = layOutRef;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public int getWorkSeq() {
        return workSeq;
    }

    public void setWorkSeq(int workSeq) {
        this.workSeq = workSeq;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public List<CutSizesBean> getCutSizes() {
        return cutSizes;
    }

    public void setCutSizes(List<CutSizesBean> cutSizes) {
        this.cutSizes = cutSizes;
    }

    public static class CutSizesBean implements Serializable {
        /**
         * sizeCode : 36
         * sizeTotal : 1
         * sizeFen : 0
         * sizeLeft : 1
         */

        private String sizeCode;
        private int sizeTotal;
        private int sizeFen;
        private int sizeLeft;
        private int cutNum;

        public int getCutNum() {
            return cutNum;
        }

        public void setCutNum(int cutNum) {
            this.cutNum = cutNum;
        }

        public String getSizeCode() {
            return sizeCode;
        }

        public void setSizeCode(String sizeCode) {
            this.sizeCode = sizeCode;
        }

        public int getSizeTotal() {
            return sizeTotal;
        }

        public void setSizeTotal(int sizeTotal) {
            this.sizeTotal = sizeTotal;
        }

        public int getSizeFen() {
            return sizeFen;
        }

        public void setSizeFen(int sizeFen) {
            this.sizeFen = sizeFen;
        }

        public int getSizeLeft() {
            return sizeLeft;
        }

        public void setSizeLeft(int sizeLeft) {
            this.sizeLeft = sizeLeft;
        }
    }
}
