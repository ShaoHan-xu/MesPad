package com.eeka.mespad.bo;

import java.util.List;

public class BatchLabuRecordBo {

    private List<SEGMENTINFOBean> SEGMENT_INFO;
    private List<String> SIZE_CODES;
    private List<RABINFOBean> RAB_INFO;
    private String ORDER_NO;
    private String ORDER_SEQ;

    public String getORDER_NO() {
        return ORDER_NO;
    }

    public void setORDER_NO(String ORDER_NO) {
        this.ORDER_NO = ORDER_NO;
    }

    public String getORDER_SEQ() {
        return ORDER_SEQ;
    }

    public void setORDER_SEQ(String ORDER_SEQ) {
        this.ORDER_SEQ = ORDER_SEQ;
    }

    public List<SEGMENTINFOBean> getSEGMENT_INFO() {
        return SEGMENT_INFO;
    }

    public void setSEGMENT_INFO(List<SEGMENTINFOBean> SEGMENT_INFO) {
        this.SEGMENT_INFO = SEGMENT_INFO;
    }

    public List<String> getSIZE_CODES() {
        return SIZE_CODES;
    }

    public void setSIZE_CODES(List<String> SIZE_CODES) {
        this.SIZE_CODES = SIZE_CODES;
    }

    public List<RABINFOBean> getRAB_INFO() {
        return RAB_INFO;
    }

    public void setRAB_INFO(List<RABINFOBean> RAB_INFO) {
        this.RAB_INFO = RAB_INFO;
    }

    public static class SEGMENTINFOBean {
        /**
         * cutNum : 1
         * lays : 1
         * sizeCodes : [{"sizeAmount":1,"sizeCode":"36"}]
         * standLength : 1
         */

        private int cutNum;
        private int lays;
        private float standLength;
        private List<SizeCodesBean> sizeCodes;

        public int getCutNum() {
            return cutNum;
        }

        public void setCutNum(int cutNum) {
            this.cutNum = cutNum;
        }

        public int getLays() {
            return lays;
        }

        public void setLays(int lays) {
            this.lays = lays;
        }

        public float getStandLength() {
            return standLength;
        }

        public void setStandLength(float standLength) {
            this.standLength = standLength;
        }

        public List<SizeCodesBean> getSizeCodes() {
            return sizeCodes;
        }

        public void setSizeCodes(List<SizeCodesBean> sizeCodes) {
            this.sizeCodes = sizeCodes;
        }

        public static class SizeCodesBean {
            /**
             * sizeAmount : 1
             * sizeCode : 36
             */

            private int sizeAmount;
            private String sizeCode;

            public int getSizeAmount() {
                return sizeAmount;
            }

            public void setSizeAmount(int sizeAmount) {
                this.sizeAmount = sizeAmount;
            }

            public String getSizeCode() {
                return sizeCode;
            }

            public void setSizeCode(String sizeCode) {
                this.sizeCode = sizeCode;
            }
        }
    }

    public static class RABINFOBean {
        /**
         * SITE : 8081
         * SHOP_ORDER_BO : ShopOrderBO:8081,000008000044
         * Z_LAYOUT_BO : ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044
         * SIZE_CODE : 36
         * SIZE_AMOUNT : 1
         * SIZE_TOTAL : 1
         * SIZE_FEN : 0
         * SIZE_LEFT : 1
         */

        private String SITE;
        private String SHOP_ORDER_BO;
        private String Z_LAYOUT_BO;
        private String SIZE_CODE;
        private int SIZE_AMOUNT;
        private int SIZE_TOTAL;
        private int SIZE_FEN;
        private int SIZE_LEFT;

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

        public String getZ_LAYOUT_BO() {
            return Z_LAYOUT_BO;
        }

        public void setZ_LAYOUT_BO(String Z_LAYOUT_BO) {
            this.Z_LAYOUT_BO = Z_LAYOUT_BO;
        }

        public String getSIZE_CODE() {
            return SIZE_CODE;
        }

        public void setSIZE_CODE(String SIZE_CODE) {
            this.SIZE_CODE = SIZE_CODE;
        }

        public int getSIZE_AMOUNT() {
            return SIZE_AMOUNT;
        }

        public void setSIZE_AMOUNT(int SIZE_AMOUNT) {
            this.SIZE_AMOUNT = SIZE_AMOUNT;
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
    }
}
