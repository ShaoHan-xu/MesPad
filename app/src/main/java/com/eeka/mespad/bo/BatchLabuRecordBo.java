package com.eeka.mespad.bo;

import java.util.List;

public class BatchLabuRecordBo {

    private List<SEGMENTINFOBean> SEGMENT_INFO;
    private List<RABINFOBean> RAB_INFO;
    private String ORDER_NO;
    private String ORDER_SEQ;
    private String SHOP_ORDER;
    private String ITEM;
    private String LAYOUT_NO;
    private String LAYOUT_IMAGE;
    private List<VolumnInfoBean> VOLUMN_INFO;

    public String getLAYOUT_NO() {
        return LAYOUT_NO;
    }

    public void setLAYOUT_NO(String LAYOUT_NO) {
        this.LAYOUT_NO = LAYOUT_NO;
    }

    public String getLAYOUT_IMAGE() {
        return LAYOUT_IMAGE;
    }

    public void setLAYOUT_IMAGE(String LAYOUT_IMAGE) {
        this.LAYOUT_IMAGE = LAYOUT_IMAGE;
    }

    public List<VolumnInfoBean> getVOLUMN_INFO() {
        return VOLUMN_INFO;
    }

    public void setVOLUMN_INFO(List<VolumnInfoBean> VOLUMN_INFO) {
        this.VOLUMN_INFO = VOLUMN_INFO;
    }

    public String getITEM() {
        return ITEM;
    }

    public void setITEM(String ITEM) {
        this.ITEM = ITEM;
    }

    public String getSHOP_ORDER() {
        return SHOP_ORDER;
    }

    public void setSHOP_ORDER(String SHOP_ORDER) {
        this.SHOP_ORDER = SHOP_ORDER;
    }

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
        private int layers;
        private float standLength;
        private float actualLength;
        private List<SizeCodesBean> sizeCodes;

        public int getLayers() {
            return layers;
        }

        public void setLayers(int layers) {
            this.layers = layers;
        }

        public float getActualLength() {
            return actualLength;
        }

        public void setActualLength(float actualLength) {
            this.actualLength = actualLength;
        }

        public int getCutNum() {
            return cutNum;
        }

        public void setCutNum(int cutNum) {
            this.cutNum = cutNum;
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
        private int CUT_NUM;

        public int getCUT_NUM() {
            return CUT_NUM;
        }

        public void setCUT_NUM(int CUT_NUM) {
            this.CUT_NUM = CUT_NUM;
        }

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

    public static class VolumnInfoBean {

        /**
         * bulkRabSegments : [{"cutNum":1,"layers":2}]
         * leftNum : 1.0
         * shortNum : 6.2
         * volumn : 1
         * volumnRef : ZBulkRabVolumBO:8081,ZBulkRabBO:8081,ZLayoutBO:000008002439_MLA0010067W001_1.cut:000008002439,000008002439001,1
         */

        private double leftNum;
        private double shortNum;
        private double length;
        private int volumn;
        private String volumnRef;
        private List<BulkRabSegmentsBean> bulkRabSegments;

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public double getLeftNum() {
            return leftNum;
        }

        public void setLeftNum(double leftNum) {
            this.leftNum = leftNum;
        }

        public double getShortNum() {
            return shortNum;
        }

        public void setShortNum(double shortNum) {
            this.shortNum = shortNum;
        }

        public int getVolumn() {
            return volumn;
        }

        public void setVolumn(int volumn) {
            this.volumn = volumn;
        }

        public String getVolumnRef() {
            return volumnRef;
        }

        public void setVolumnRef(String volumnRef) {
            this.volumnRef = volumnRef;
        }

        public List<BulkRabSegmentsBean> getBulkRabSegments() {
            return bulkRabSegments;
        }

        public void setBulkRabSegments(List<BulkRabSegmentsBean> bulkRabSegments) {
            this.bulkRabSegments = bulkRabSegments;
        }

        public static class BulkRabSegmentsBean {
            /**
             * cutNum : 1
             * layers : 2.0
             */

            private int cutNum;
            private double layers;

            public int getCutNum() {
                return cutNum;
            }

            public void setCutNum(int cutNum) {
                this.cutNum = cutNum;
            }

            public double getLayers() {
                return layers;
            }

            public void setLayers(double layers) {
                this.layers = layers;
            }
        }
    }
}
