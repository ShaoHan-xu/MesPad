package com.eeka.mespad.bo;

import java.util.List;

/**
 * Created by Lenovo on 2017/6/28.
 */

public class TailorInfoBo {

    /**
     * OPER_INFOR : {"SOP_URL":"http://10.8.41.187/裁片.jpg","OPERATION_BO":"OperationBO:TEST,OP002,A"}
     * SHOP_ORDER_INFOR : {"SHOP_ORDER":"MODA-SO-01","PROCESS_LOT":"201708031960292","ITEM":"MODA-MAT-01","ORDER_QTY":100,"LAYERS":30,"MAT_URL":"http://10.8.41.187/50011前袋.jpg","Z_LAYOUT_BO":"1"}
     * IS_CUSTOM : false
     * CUT_SIZES : [{"SIZE":"39","SIZE_AMOUNT":4},{"SIZE":"40","SIZE_AMOUNT":5}]
     */

    private List<OPERINFORBean> OPER_INFOR;
    private SHOPORDERINFORBean SHOP_ORDER_INFOR;
    private boolean IS_CUSTOM;
    private List<CUTSIZESBean> CUT_SIZES;
    private List<MatInfoBean> MAT_INFOR;
    private List<LayoutInfoBean> LAYOUT_INFOR;
    private NextOrderInfo NEXT_OPER_INFOR;
    private ResultInfo RESR_INFOR;

    public ResultInfo getRESR_INFOR() {
        return RESR_INFOR;
    }

    public void setRESR_INFOR(ResultInfo RESR_INFOR) {
        this.RESR_INFOR = RESR_INFOR;
    }

    public NextOrderInfo getNEXT_OPER_INFOR() {
        return NEXT_OPER_INFOR;
    }

    public void setNEXT_OPER_INFOR(NextOrderInfo NEXT_OPER_INFOR) {
        this.NEXT_OPER_INFOR = NEXT_OPER_INFOR;
    }

    public List<LayoutInfoBean> getLAYOUT_INFOR() {
        return LAYOUT_INFOR;
    }

    public void setLAYOUT_INFOR(List<LayoutInfoBean> LAYOUT_INFOR) {
        this.LAYOUT_INFOR = LAYOUT_INFOR;
    }

    public List<MatInfoBean> getMAT_INFOR() {
        return MAT_INFOR;
    }

    public void setMAT_INFOR(List<MatInfoBean> MAT_INFOR) {
        this.MAT_INFOR = MAT_INFOR;
    }

    public List<OPERINFORBean> getOPER_INFOR() {
        return OPER_INFOR;
    }

    public void setOPER_INFOR(List<OPERINFORBean> OPER_INFOR) {
        this.OPER_INFOR = OPER_INFOR;
    }

    public SHOPORDERINFORBean getSHOP_ORDER_INFOR() {
        return SHOP_ORDER_INFOR;
    }

    public void setSHOP_ORDER_INFOR(SHOPORDERINFORBean SHOP_ORDER_INFOR) {
        this.SHOP_ORDER_INFOR = SHOP_ORDER_INFOR;
    }

    public boolean isIS_CUSTOM() {
        return IS_CUSTOM;
    }

    public void setIS_CUSTOM(boolean IS_CUSTOM) {
        this.IS_CUSTOM = IS_CUSTOM;
    }

    public List<CUTSIZESBean> getCUT_SIZES() {
        return CUT_SIZES;
    }

    public void setCUT_SIZES(List<CUTSIZESBean> CUT_SIZES) {
        this.CUT_SIZES = CUT_SIZES;
    }

    public static class MatInfoBean {
        private int LAYERS;
        private String MAT_URL;
        private String MAT_NO;
        private String ITEM_BO;

        public String getITEM_BO() {
            return ITEM_BO;
        }

        public void setITEM_BO(String ITEM_BO) {
            this.ITEM_BO = ITEM_BO;
        }

        public String getMAT_NO() {
            return MAT_NO;
        }

        public void setMAT_NO(String MAT_NO) {
            this.MAT_NO = MAT_NO;
        }

        public int getLAYERS() {
            return LAYERS;
        }

        public void setLAYERS(int LAYERS) {
            this.LAYERS = LAYERS;
        }

        public String getMAT_URL() {
            return MAT_URL;
        }

        public void setMAT_URL(String MAT_URL) {
            this.MAT_URL = MAT_URL;
        }
    }

    public static class OPERINFORBean {
        /**
         * SOP_URL : http://10.8.41.187/裁片.jpg
         * OPERATION_BO : OperationBO:TEST,OP002,A
         */

        private String SOP_URL;
        private String OPERATION_BO;
        private String QUALITY_REQUIREMENT;
        private String OPERATION;
        private String DESCRIPTION;
        private String OPERATION_INSTRUCTION;

        public String getQUALITY_REQUIREMENT() {
            return QUALITY_REQUIREMENT;
        }

        public void setQUALITY_REQUIREMENT(String QUALITY_REQUIREMENT) {
            this.QUALITY_REQUIREMENT = QUALITY_REQUIREMENT;
        }

        public String getOPERATION() {
            return OPERATION;
        }

        public void setOPERATION(String OPERATION) {
            this.OPERATION = OPERATION;
        }

        public String getDESCRIPTION() {
            return DESCRIPTION;
        }

        public void setDESCRIPTION(String DESCRIPTION) {
            this.DESCRIPTION = DESCRIPTION;
        }

        public String getOPERATION_INSTRUCTION() {
            return OPERATION_INSTRUCTION;
        }

        public void setOPERATION_INSTRUCTION(String OPERATION_INSTRUCTION) {
            this.OPERATION_INSTRUCTION = OPERATION_INSTRUCTION;
        }

        public String getSOP_URL() {
            return SOP_URL;
        }

        public void setSOP_URL(String SOP_URL) {
            this.SOP_URL = SOP_URL;
        }

        public String getOPERATION_BO() {
            return OPERATION_BO;
        }

        public void setOPERATION_BO(String OPERATION_BO) {
            this.OPERATION_BO = OPERATION_BO;
        }
    }

    public static class SHOPORDERINFORBean {
        /**
         * SHOP_ORDER : MODA-SO-01
         * PROCESS_LOT : 201708031960292
         * ITEM : MODA-MAT-01
         * ORDER_QTY : 100
         * Z_LAYOUT_BO : 1
         */

        private String SHOP_ORDER;
        private String SHOP_ORDER_BO;
        private List<String> PROCESS_LOT_BO;
        private String ITEM;
        private String SALE_ORDER;
        private int ORDER_QTY;
        private int LAYERS;
        private String Z_LAYOUT_BO;

        public String getSALE_ORDER() {
            return SALE_ORDER;
        }

        public void setSALE_ORDER(String SALE_ORDER) {
            this.SALE_ORDER = SALE_ORDER;
        }

        public int getLAYERS() {
            return LAYERS;
        }

        public void setLAYERS(int LAYERS) {
            this.LAYERS = LAYERS;
        }

        public String getSHOP_ORDER_BO() {
            return SHOP_ORDER_BO;
        }

        public void setSHOP_ORDER_BO(String SHOP_ORDER_BO) {
            this.SHOP_ORDER_BO = SHOP_ORDER_BO;
        }

        public List<String> getPROCESS_LOT_BO() {
            return PROCESS_LOT_BO;
        }

        public void setPROCESS_LOT_BO(List<String> PROCESS_LOT_BO) {
            this.PROCESS_LOT_BO = PROCESS_LOT_BO;
        }

        public String getSHOP_ORDER() {
            return SHOP_ORDER;
        }

        public void setSHOP_ORDER(String SHOP_ORDER) {
            this.SHOP_ORDER = SHOP_ORDER;
        }

        public String getITEM() {
            return ITEM;
        }

        public void setITEM(String ITEM) {
            this.ITEM = ITEM;
        }

        public int getORDER_QTY() {
            return ORDER_QTY;
        }

        public void setORDER_QTY(int ORDER_QTY) {
            this.ORDER_QTY = ORDER_QTY;
        }

        public String getZ_LAYOUT_BO() {
            return Z_LAYOUT_BO;
        }

        public void setZ_LAYOUT_BO(String Z_LAYOUT_BO) {
            this.Z_LAYOUT_BO = Z_LAYOUT_BO;
        }
    }

    public static class CUTSIZESBean {
        /**
         * SIZE : 39
         * SIZE_AMOUNT : 4
         */

        private String SIZE;
        private int SIZE_AMOUNT;

        public String getSIZE() {
            return SIZE;
        }

        public void setSIZE(String SIZE) {
            this.SIZE = SIZE;
        }

        public int getSIZE_AMOUNT() {
            return SIZE_AMOUNT;
        }

        public void setSIZE_AMOUNT(int SIZE_AMOUNT) {
            this.SIZE_AMOUNT = SIZE_AMOUNT;
        }
    }

    public static class LayoutInfoBean {
        private String PICTURE_URL;
        private String LAYOUT;

        public String getLAYOUT() {
            return LAYOUT;
        }

        public void setLAYOUT(String LAYOUT) {
            this.LAYOUT = LAYOUT;
        }

        public String getPICTURE_URL() {
            return PICTURE_URL;
        }

        public void setPICTURE_URL(String PICTURE_URL) {
            this.PICTURE_URL = PICTURE_URL;
        }
    }

    public static class NextOrderInfo {
        private String ROUTER_STEP_BO;
        private String NEXT_STEP_BO;
        private String OPERATION;
        private String OPER_DESC;

        public String getROUTER_STEP_BO() {
            return ROUTER_STEP_BO;
        }

        public void setROUTER_STEP_BO(String ROUTER_STEP_BO) {
            this.ROUTER_STEP_BO = ROUTER_STEP_BO;
        }

        public String getNEXT_STEP_BO() {
            return NEXT_STEP_BO;
        }

        public void setNEXT_STEP_BO(String NEXT_STEP_BO) {
            this.NEXT_STEP_BO = NEXT_STEP_BO;
        }

        public String getOPERATION() {
            return OPERATION;
        }

        public void setOPERATION(String OPERATION) {
            this.OPERATION = OPERATION;
        }

        public String getOPER_DESC() {
            return OPER_DESC;
        }

        public void setOPER_DESC(String OPER_DESC) {
            this.OPER_DESC = OPER_DESC;
        }
    }

    public static class ResultInfo {
        private String RESOURCE_BO;
        private String RESOURCE;

        public String getRESOURCE_BO() {
            return RESOURCE_BO;
        }

        public void setRESOURCE_BO(String RESOURCE_BO) {
            this.RESOURCE_BO = RESOURCE_BO;
        }

        public String getRESOURCE() {
            return RESOURCE;
        }

        public void setRESOURCE(String RESOURCE) {
            this.RESOURCE = RESOURCE;
        }
    }
}
