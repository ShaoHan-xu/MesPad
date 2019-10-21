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

    private String ITEM;
    private String MATERIAL_TYPE;
    private String Z_RAB_BO;
    private String LAYOUT_NO;
    private String Z_LAYOUT_BO;
    private String IS_FINISH;
    private String LAYOUT_IMAGE;
    private String SHOP_ORDER;
    private String SHOP_ORDER_BO;

    public String getSHOP_ORDER() {
        return SHOP_ORDER;
    }

    public void setSHOP_ORDER(String SHOP_ORDER) {
        this.SHOP_ORDER = SHOP_ORDER;
    }

    public String getSHOP_ORDER_BO() {
        return SHOP_ORDER_BO;
    }

    public void setSHOP_ORDER_BO(String SHOP_ORDER_BO) {
        this.SHOP_ORDER_BO = SHOP_ORDER_BO;
    }

    public String getLAYOUT_IMAGE() {
        return LAYOUT_IMAGE;
    }

    public void setLAYOUT_IMAGE(String LAYOUT_IMAGE) {
        this.LAYOUT_IMAGE = LAYOUT_IMAGE;
    }

    public String getITEM() {
        return ITEM;
    }

    public void setITEM(String ITEM) {
        this.ITEM = ITEM;
    }

    public String getMATERIAL_TYPE() {
        return MATERIAL_TYPE;
    }

    public void setMATERIAL_TYPE(String MATERIAL_TYPE) {
        this.MATERIAL_TYPE = MATERIAL_TYPE;
    }

    public String getZ_RAB_BO() {
        return Z_RAB_BO;
    }

    public void setZ_RAB_BO(String z_RAB_BO) {
        Z_RAB_BO = z_RAB_BO;
    }

    public String getLAYOUT_NO() {
        return LAYOUT_NO;
    }

    public void setLAYOUT_NO(String LAYOUT_NO) {
        this.LAYOUT_NO = LAYOUT_NO;
    }

    public String getZ_LAYOUT_BO() {
        return Z_LAYOUT_BO;
    }

    public void setZ_LAYOUT_BO(String z_LAYOUT_BO) {
        Z_LAYOUT_BO = z_LAYOUT_BO;
    }

    public String getIS_FINISH() {
        return IS_FINISH;
    }

    public void setIS_FINISH(String IS_FINISH) {
        this.IS_FINISH = IS_FINISH;
    }

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
        private int CUT_NUM;
        private int SIZE_TOTAL;
        private int SIZE_FEN;
        private int SIZE_LEFT;
        private String STATUS;

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
