package com.eeka.mespad.bo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by Lenovo on 2017/11/29.
 */

public class SaveLabuDataBo {

    /**
     * SHOP_ORDER_BO : ShopOrderBO:8081,LH8000013
     * RESOURCE_BO : ResourceBO:8081,DEFAULT
     * Z_LAYOUT_BO : ZLayoutBO:GENERAL_LAYOUT
     * ITEM_BO : ItemBO:8081,C1AFF542T03,A
     * USER_ID : CHRIS
     * DC_GROUP_BO : DcGroupBO:8081,Z_LAYOUT_DC,A
     * DCS : [{"ROLL":"2","ROLL_DETAIL":[{"PARAMETRIC_MEASURE_BO":"","MEASURED_ATTRIBUTE":"LEFT","MEASURED_VALUE":"5","DATA_TYPE":"T","PARAM_DESC":"LEFT","STATUS":"I"}]}]
     */

    private String SHOP_ORDER_BO;
    private String RESOURCE_BO;
    @JSONField(name = "Z_LAYOUT_BO")
    private String Z_LAYOUT_BO;
    private String ITEM_BO;
    private String USER_ID;
    private String DC_GROUP_BO;
    private List<DCSBean> DCS;

    public String getSHOP_ORDER_BO() {
        return SHOP_ORDER_BO;
    }

    public void setSHOP_ORDER_BO(String SHOP_ORDER_BO) {
        this.SHOP_ORDER_BO = SHOP_ORDER_BO;
    }

    public String getRESOURCE_BO() {
        return RESOURCE_BO;
    }

    public void setRESOURCE_BO(String RESOURCE_BO) {
        this.RESOURCE_BO = RESOURCE_BO;
    }

    public String getZ_LAYOUT_BO() {
        return Z_LAYOUT_BO;
    }

    public void setZ_LAYOUT_BO(String Z_LAYOUT_BO) {
        this.Z_LAYOUT_BO = Z_LAYOUT_BO;
    }

    public String getITEM_BO() {
        return ITEM_BO;
    }

    public void setITEM_BO(String ITEM_BO) {
        this.ITEM_BO = ITEM_BO;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getDC_GROUP_BO() {
        return DC_GROUP_BO;
    }

    public void setDC_GROUP_BO(String DC_GROUP_BO) {
        this.DC_GROUP_BO = DC_GROUP_BO;
    }

    public List<DCSBean> getDCS() {
        return DCS;
    }

    public void setDCS(List<DCSBean> DCS) {
        this.DCS = DCS;
    }

    public static class DCSBean {

        /**
         * MAT_LENGTH : 2
         * DM : 3
         * LAYERS : 2
         * LEFT_QTY : 5
         * INVENTORY : FOW
         */

        private String MAT_LENGTH;
        private String DM;
        private String LAYERS;
        private String LEFT_QTY;
        private String INVENTORY;

        public String getMAT_LENGTH() {
            return MAT_LENGTH;
        }

        public void setMAT_LENGTH(String MAT_LENGTH) {
            this.MAT_LENGTH = MAT_LENGTH;
        }

        public String getDM() {
            return DM;
        }

        public void setDM(String DM) {
            this.DM = DM;
        }

        public String getLAYERS() {
            return LAYERS;
        }

        public void setLAYERS(String LAYERS) {
            this.LAYERS = LAYERS;
        }

        public String getLEFT_QTY() {
            return LEFT_QTY;
        }

        public void setLEFT_QTY(String LEFT_QTY) {
            this.LEFT_QTY = LEFT_QTY;
        }

        public String getINVENTORY() {
            return INVENTORY;
        }

        public void setINVENTORY(String INVENTORY) {
            this.INVENTORY = INVENTORY;
        }
    }
}
