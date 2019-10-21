package com.eeka.mespad.bo;

import java.util.List;

public class BatchLabuDetailBo {

    /**
     * ORDER_INFO : [{"SITE":"8081","SHOP_ORDER_BO":"ShopOrderBO:8081,000008000044","SIZE_CODE":"36","SIZE_AMOUNT":1,"SIZE_TOTAL":1,"SIZE_FEN":1,"SIZE_LEFT":0}]
     * RAB_SEQ : 2
     * LAYOUT_INFO : [{"SINGLE_LAYOUT":[{"SITE":"8081","SHOP_ORDER_BO":"ShopOrderBO:8081,000008000044","Z_LAYOUT_BO":"ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044","SIZE_CODE":"36","SIZE_AMOUNT":1,"SIZE_TOTAL":1,"SIZE_FEN":0,"SIZE_LEFT":1},{"SITE":"8081","SHOP_ORDER_BO":"ShopOrderBO:8081,000008000044","Z_LAYOUT_BO":"ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044","SIZE_CODE":"40","SIZE_AMOUNT":1,"SIZE_TOTAL":1,"SIZE_FEN":0,"SIZE_LEFT":1}],"LAY_NO":"1","DISPLAY":true}]
     * RAB_ORDER : 000008000044002
     * ORDER_STATUS : [{"SITE":"8081","STEP_ID":"10","OPERATION":"SP","OPERATION_DESC":"拉布","MATERIAL_TYPES":"M","MATERIAL_TYPES_DESC":"面布","IS_USED":"true","IS_LOCK":"true","STATUS":"IN_QUEUE"}]
     */

    private int RAB_SEQ;
    private String RAB_ORDER;
    private String SHOP_ORDER;
    private String SHOP_ORDER_BO;
    private String MAT_URL;
    private boolean DISPLAY;
    private List<LAYOUTINFOBean.SINGLELAYOUTBean> ORDER_INFO;
    private List<LAYOUTINFOBean> LAYOUT_INFO;
    private List<ORDERSTATUSBean> ORDER_STATUS;

    public boolean isDISPLAY() {
        return DISPLAY;
    }

    public void setDISPLAY(boolean DISPLAY) {
        this.DISPLAY = DISPLAY;
    }

    public String getMAT_URL() {
        return MAT_URL;
    }

    public void setMAT_URL(String MAT_URL) {
        this.MAT_URL = MAT_URL;
    }

    public String getSHOP_ORDER_BO() {
        return SHOP_ORDER_BO;
    }

    public void setSHOP_ORDER_BO(String SHOP_ORDER_BO) {
        this.SHOP_ORDER_BO = SHOP_ORDER_BO;
    }

    public String getSHOP_ORDER() {
        return SHOP_ORDER;
    }

    public void setSHOP_ORDER(String SHOP_ORDER) {
        this.SHOP_ORDER = SHOP_ORDER;
    }

    public int getRAB_SEQ() {
        return RAB_SEQ;
    }

    public void setRAB_SEQ(int RAB_SEQ) {
        this.RAB_SEQ = RAB_SEQ;
    }

    public String getRAB_ORDER() {
        return RAB_ORDER;
    }

    public void setRAB_ORDER(String RAB_ORDER) {
        this.RAB_ORDER = RAB_ORDER;
    }

    public List<LAYOUTINFOBean.SINGLELAYOUTBean> getORDER_INFO() {
        return ORDER_INFO;
    }

    public void setORDER_INFO(List<LAYOUTINFOBean.SINGLELAYOUTBean> ORDER_INFO) {
        this.ORDER_INFO = ORDER_INFO;
    }

    public List<LAYOUTINFOBean> getLAYOUT_INFO() {
        return LAYOUT_INFO;
    }

    public void setLAYOUT_INFO(List<LAYOUTINFOBean> LAYOUT_INFO) {
        this.LAYOUT_INFO = LAYOUT_INFO;
    }

    public List<ORDERSTATUSBean> getORDER_STATUS() {
        return ORDER_STATUS;
    }

    public void setORDER_STATUS(List<ORDERSTATUSBean> ORDER_STATUS) {
        this.ORDER_STATUS = ORDER_STATUS;
    }

    public static class LAYOUTINFOBean {
        /**
         * SINGLE_LAYOUT : [{"SITE":"8081","SHOP_ORDER_BO":"ShopOrderBO:8081,000008000044","Z_LAYOUT_BO":"ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044","SIZE_CODE":"36","SIZE_AMOUNT":1,"SIZE_TOTAL":1,"SIZE_FEN":0,"SIZE_LEFT":1},{"SITE":"8081","SHOP_ORDER_BO":"ShopOrderBO:8081,000008000044","Z_LAYOUT_BO":"ZLayoutBO:000008000044_MRL0180001P821_1.cut:000008000044","SIZE_CODE":"40","SIZE_AMOUNT":1,"SIZE_TOTAL":1,"SIZE_FEN":0,"SIZE_LEFT":1}]
         * LAY_NO : 1
         * DISPLAY : true
         */
        private String LAY_NO;
        private String ITEM;
        private String SHOP_ORDER_BO;
        private String Z_LAYOUT_BO;
        private String PICTURE_URL;
        private boolean LAB_DISPLAY;
        private List<SINGLELAYOUTBean> SINGLE_LAYOUT;
        private List<RABORDERINFOBean> RAB_ORDER_INFO;

        public boolean isLAB_DISPLAY() {
            return LAB_DISPLAY;
        }

        public void setLAB_DISPLAY(boolean LAB_DISPLAY) {
            this.LAB_DISPLAY = LAB_DISPLAY;
        }

        public List<RABORDERINFOBean> getRAB_ORDER_INFO() {
            return RAB_ORDER_INFO;
        }

        public void setRAB_ORDER_INFO(List<RABORDERINFOBean> RAB_ORDER_INFO) {
            this.RAB_ORDER_INFO = RAB_ORDER_INFO;
        }

        public String getPICTURE_URL() {
            return PICTURE_URL;
        }

        public void setPICTURE_URL(String PICTURE_URL) {
            this.PICTURE_URL = PICTURE_URL;
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

        public void setZ_LAYOUT_BO(String z_LAYOUT_BO) {
            Z_LAYOUT_BO = z_LAYOUT_BO;
        }

        public String getITEM() {
            return ITEM;
        }

        public void setITEM(String ITEM) {
            this.ITEM = ITEM;
        }

        public String getLAY_NO() {
            return LAY_NO;
        }

        public void setLAY_NO(String LAY_NO) {
            this.LAY_NO = LAY_NO;
        }

        public List<SINGLELAYOUTBean> getSINGLE_LAYOUT() {
            return SINGLE_LAYOUT;
        }

        public void setSINGLE_LAYOUT(List<SINGLELAYOUTBean> SINGLE_LAYOUT) {
            this.SINGLE_LAYOUT = SINGLE_LAYOUT;
        }

        public static class RABORDERINFOBean {
            private String Z_RAB_BO;
            private String RAB_NO;
            private String STATUS;

            public String getSTATUS() {
                return STATUS;
            }

            public void setSTATUS(String STATUS) {
                this.STATUS = STATUS;
            }

            public String getZ_RAB_BO() {
                return Z_RAB_BO;
            }

            public void setZ_RAB_BO(String z_RAB_BO) {
                Z_RAB_BO = z_RAB_BO;
            }

            public String getRAB_NO() {
                return RAB_NO;
            }

            public void setRAB_NO(String RAB_NO) {
                this.RAB_NO = RAB_NO;
            }
        }

        public static class SINGLELAYOUTBean {
            /**
             * SITE : 8081
             * SIZE_CODE : 36
             * SIZE_AMOUNT : 1
             * SIZE_TOTAL : 1
             * SIZE_FEN : 0
             * SIZE_LEFT : 1
             */

            private String SITE;
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

    public static class ORDERSTATUSBean {
        /**
         * SITE : 8081
         * STEP_ID : 10
         * OPERATION : SP
         * OPERATION_DESC : 拉布
         * MATERIAL_TYPES : M
         * MATERIAL_TYPES_DESC : 面布
         * IS_USED : true
         * IS_LOCK : true
         * STATUS : IN_QUEUE
         */

        private String SITE;
        private String STEP_ID;
        private String OPERATION;
        private String OPERATION_DESC;
        private String MATERIAL_TYPES;
        private String MATERIAL_TYPES_DESC;
        private String IS_USED;
        private String IS_LOCK;
        private String STATUS;

        public String getSITE() {
            return SITE;
        }

        public void setSITE(String SITE) {
            this.SITE = SITE;
        }

        public String getSTEP_ID() {
            return STEP_ID;
        }

        public void setSTEP_ID(String STEP_ID) {
            this.STEP_ID = STEP_ID;
        }

        public String getOPERATION() {
            return OPERATION;
        }

        public void setOPERATION(String OPERATION) {
            this.OPERATION = OPERATION;
        }

        public String getOPERATION_DESC() {
            return OPERATION_DESC;
        }

        public void setOPERATION_DESC(String OPERATION_DESC) {
            this.OPERATION_DESC = OPERATION_DESC;
        }

        public String getMATERIAL_TYPES() {
            return MATERIAL_TYPES;
        }

        public void setMATERIAL_TYPES(String MATERIAL_TYPES) {
            this.MATERIAL_TYPES = MATERIAL_TYPES;
        }

        public String getMATERIAL_TYPES_DESC() {
            return MATERIAL_TYPES_DESC;
        }

        public void setMATERIAL_TYPES_DESC(String MATERIAL_TYPES_DESC) {
            this.MATERIAL_TYPES_DESC = MATERIAL_TYPES_DESC;
        }

        public String getIS_USED() {
            return IS_USED;
        }

        public void setIS_USED(String IS_USED) {
            this.IS_USED = IS_USED;
        }

        public String getIS_LOCK() {
            return IS_LOCK;
        }

        public void setIS_LOCK(String IS_LOCK) {
            this.IS_LOCK = IS_LOCK;
        }

        public String getSTATUS() {
            return STATUS;
        }

        public void setSTATUS(String STATUS) {
            this.STATUS = STATUS;
        }
    }
}
