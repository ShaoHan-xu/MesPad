package com.eeka.mespad.bo;

import java.io.Serializable;
import java.util.List;

public class SubPackageInfoBo implements Serializable {

    private List<SUBDETAILBean> SUB_DETAIL;
    private List<SUBORDERBean> SUB_ORDER;
    private String CF_WORKCENTER;
    private String CF_WORKCENTEER_DESC;

    public String getCF_WORKCENTER() {
        return CF_WORKCENTER;
    }

    public void setCF_WORKCENTER(String CF_WORKCENTER) {
        this.CF_WORKCENTER = CF_WORKCENTER;
    }

    public String getCF_WORKCENTEER_DESC() {
        return CF_WORKCENTEER_DESC;
    }

    public void setCF_WORKCENTEER_DESC(String CF_WORKCENTEER_DESC) {
        this.CF_WORKCENTEER_DESC = CF_WORKCENTEER_DESC;
    }

    public List<SUBDETAILBean> getSUB_DETAIL() {
        return SUB_DETAIL;
    }

    public void setSUB_DETAIL(List<SUBDETAILBean> SUB_DETAIL) {
        this.SUB_DETAIL = SUB_DETAIL;
    }

    public List<SUBORDERBean> getSUB_ORDER() {
        return SUB_ORDER;
    }

    public void setSUB_ORDER(List<SUBORDERBean> SUB_ORDER) {
        this.SUB_ORDER = SUB_ORDER;
    }

    public static class SUBDETAILBean implements Serializable {
        /**
         * item : [{"SUB_QTY":2,"SUB_SEQ":2}]
         * num : 8
         * sizeCode : 38
         */

        private int num;
        private String sizeCode;
        private List<ItemBean> item;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getSizeCode() {
            return sizeCode;
        }

        public void setSizeCode(String sizeCode) {
            this.sizeCode = sizeCode;
        }

        public List<ItemBean> getItem() {
            return item;
        }

        public void setItem(List<ItemBean> item) {
            this.item = item;
        }

        public static class ItemBean implements Serializable {
            /**
             * SUB_QTY : 2
             * SUB_SEQ : 2
             */

            private int SUB_QTY;
            private int SUB_SEQ;

            public int getSUB_QTY() {
                return SUB_QTY;
            }

            public void setSUB_QTY(int SUB_QTY) {
                this.SUB_QTY = SUB_QTY;
            }

            public int getSUB_SEQ() {
                return SUB_SEQ;
            }

            public void setSUB_SEQ(int SUB_SEQ) {
                this.SUB_SEQ = SUB_SEQ;
            }
        }
    }

    public static class SUBORDERBean implements Serializable {
        /**
         * SITE : 8081
         * SHOP_ORDER_BO : ShopOrderBO:8081,000008002441
         * MATERIAL_TYPE : M
         * SIZE_AMOUNT : 2
         * SIZE_CODE : 36
         * SIZE_TOTAL : 40
         * SIZE_FEN : 0
         * SIZE_LEFT : 40
         * STATUS : UNDONE
         */

        private String SITE;
        private String SHOP_ORDER_BO;
        private String MATERIAL_TYPE;
        private int SIZE_AMOUNT;
        private String SIZE_CODE;
        private int SIZE_TOTAL;
        private int SIZE_FEN;
        private int SIZE_LEFT;
        private String STATUS;

        public String getSITE() {
            return SITE;
        }

        public void setSITE(String SITE) {
            this.SITE = SITE;
        }

        public String getSHOP_ORDER_BO() {
            return SHOP_ORDER_BO;
        }

        public void setSHOP_ORDER_BO(String SHOP_ORDER_BO) {
            this.SHOP_ORDER_BO = SHOP_ORDER_BO;
        }

        public String getMATERIAL_TYPE() {
            return MATERIAL_TYPE;
        }

        public void setMATERIAL_TYPE(String MATERIAL_TYPE) {
            this.MATERIAL_TYPE = MATERIAL_TYPE;
        }

        public int getSIZE_AMOUNT() {
            return SIZE_AMOUNT;
        }

        public void setSIZE_AMOUNT(int SIZE_AMOUNT) {
            this.SIZE_AMOUNT = SIZE_AMOUNT;
        }

        public String getSIZE_CODE() {
            return SIZE_CODE;
        }

        public void setSIZE_CODE(String SIZE_CODE) {
            this.SIZE_CODE = SIZE_CODE;
        }

        public int getSIZE_TOTAL() {
            return SIZE_TOTAL;
        }

        public void setSIZE_TOTAL(int SIZE_TOTAL) {
            this.SIZE_TOTAL = SIZE_TOTAL;
        }

        public int getSIZE_FEN() {
            return SIZE_FEN;
        }

        public void setSIZE_FEN(int SIZE_FEN) {
            this.SIZE_FEN = SIZE_FEN;
        }

        public int getSIZE_LEFT() {
            return SIZE_LEFT;
        }

        public void setSIZE_LEFT(int SIZE_LEFT) {
            this.SIZE_LEFT = SIZE_LEFT;
        }

        public String getSTATUS() {
            return STATUS;
        }

        public void setSTATUS(String STATUS) {
            this.STATUS = STATUS;
        }
    }
}
