package com.eeka.mespad.bo;

import java.util.List;

public class BatchCutWorkingBo {

    /**
     * RAB_INFO : [{"SITE":"8081","SHOP_ORDER_BO":"ShopOrderBO:8081,000008000044","Z_RAB_BO":"ZBulkRabBO:8081,ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044,000008000044001","MATERIAL_TYPE":"M","SIZE_CODE":"40","SIZE_TOTAL":1,"SIZE_FEN":0,"SIZE_LEFT":1,"STATUS":"UNDONE"},{"SITE":"8081","SHOP_ORDER_BO":"ShopOrderBO:8081,000008000044","Z_RAB_BO":"ZBulkRabBO:8081,ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044,000008000044001","MATERIAL_TYPE":"M","SIZE_CODE":"36","SIZE_TOTAL":1,"SIZE_FEN":0,"SIZE_LEFT":1,"STATUS":"UNDONE"}]
     * ORDER_NO : null002
     * ORDER_SEQ : 2
     */

    private String ORDER_NO;
    private int ORDER_SEQ;
    private List<RABINFOBean> RAB_INFO;

    public String getORDER_NO() {
        return ORDER_NO;
    }

    public void setORDER_NO(String ORDER_NO) {
        this.ORDER_NO = ORDER_NO;
    }

    public int getORDER_SEQ() {
        return ORDER_SEQ;
    }

    public void setORDER_SEQ(int ORDER_SEQ) {
        this.ORDER_SEQ = ORDER_SEQ;
    }

    public List<RABINFOBean> getRAB_INFO() {
        return RAB_INFO;
    }

    public void setRAB_INFO(List<RABINFOBean> RAB_INFO) {
        this.RAB_INFO = RAB_INFO;
    }

    public static class RABINFOBean {
        /**
         * SITE : 8081
         * SHOP_ORDER_BO : ShopOrderBO:8081,000008000044
         * Z_RAB_BO : ZBulkRabBO:8081,ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044,000008000044001
         * MATERIAL_TYPE : M
         * SIZE_CODE : 40
         * SIZE_TOTAL : 1
         * SIZE_FEN : 0
         * SIZE_LEFT : 1
         * STATUS : UNDONE
         */

        private String SITE;
        private String SHOP_ORDER_BO;
        private String Z_RAB_BO;
        private String MATERIAL_TYPE;
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

        public String getZ_RAB_BO() {
            return Z_RAB_BO;
        }

        public void setZ_RAB_BO(String Z_RAB_BO) {
            this.Z_RAB_BO = Z_RAB_BO;
        }

        public String getMATERIAL_TYPE() {
            return MATERIAL_TYPE;
        }

        public void setMATERIAL_TYPE(String MATERIAL_TYPE) {
            this.MATERIAL_TYPE = MATERIAL_TYPE;
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
