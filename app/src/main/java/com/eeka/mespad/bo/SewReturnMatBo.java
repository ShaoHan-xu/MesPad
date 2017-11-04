package com.eeka.mespad.bo;

import java.util.List;

/**
 * Created by Lenovo on 2017/11/2.
 */

public class SewReturnMatBo {

    private String shopOrder;
    private List<BomInfoBean> bomInfo;

    public String getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(String shopOrder) {
        this.shopOrder = shopOrder;
    }

    public List<BomInfoBean> getBomInfo() {
        return bomInfo;
    }

    public void setBomInfo(List<BomInfoBean> BomInfo) {
        this.bomInfo = BomInfo;
    }

    public static class BomInfoBean {
        /**
         * ITEM : FL01010005W0
         * DESCRIPTION : FL01010005W0
         * UNIT_OF_MEASURE : 分米
         */

        private String ITEM;
        private String DESCRIPTION;
        private String UNIT_OF_MEASURE;
        private String REASON_CODE;//退/补料原因代码
        private String QTY;

        public String getREASON_CODE() {
            return REASON_CODE;
        }

        public void setREASON_CODE(String REASON_CODE) {
            this.REASON_CODE = REASON_CODE;
        }

        public String getQTY() {
            return QTY;
        }

        public void setQTY(String QTY) {
            this.QTY = QTY;
        }

        public String getITEM() {
            return ITEM;
        }

        public void setITEM(String ITEM) {
            this.ITEM = ITEM;
        }

        public String getDESCRIPTION() {
            return DESCRIPTION;
        }

        public void setDESCRIPTION(String DESCRIPTION) {
            this.DESCRIPTION = DESCRIPTION;
        }

        public String getUNIT_OF_MEASURE() {
            return UNIT_OF_MEASURE;
        }

        public void setUNIT_OF_MEASURE(String UNIT_OF_MEASURE) {
            this.UNIT_OF_MEASURE = UNIT_OF_MEASURE;
        }
    }
}
