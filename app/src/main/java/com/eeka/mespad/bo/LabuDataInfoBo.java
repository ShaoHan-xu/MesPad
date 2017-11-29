package com.eeka.mespad.bo;

import java.util.List;

/**
 * Created by Lenovo on 2017/11/28.
 */

public class LabuDataInfoBo {

    private List<SPREADINGDATABean> SPREADING_DATA;

    public List<SPREADINGDATABean> getSPREADING_DATA() {
        return SPREADING_DATA;
    }

    public void setSPREADING_DATA(List<SPREADINGDATABean> SPREADING_DATA) {
        this.SPREADING_DATA = SPREADING_DATA;
    }

    public static class SPREADINGDATABean {
        /**
         * LAYOUT : LY002
         * LAYERS : 4
         * INVENTORY : 6
         * ORDER_NO : LH8000014
         * LEFT_QTY : 5
         * ROLL : 1
         * DC_GROUP_BO : DcGroupBO:8081,Z_LAYOUT_DC,A
         */

        private String LAYOUT;
        private String LAYERS;
        private String INVENTORY;
        private String MAT_LENGTH;
        private String ORDER_NO;
        private String LEFT_QTY;
        private String ROLL;
        private String DC_GROUP_BO;

        public String getMAT_LENGTH() {
            return MAT_LENGTH;
        }

        public void setMAT_LENGTH(String MAT_LENGTH) {
            this.MAT_LENGTH = MAT_LENGTH;
        }

        public String getLAYOUT() {
            return LAYOUT;
        }

        public void setLAYOUT(String LAYOUT) {
            this.LAYOUT = LAYOUT;
        }

        public String getLAYERS() {
            return LAYERS;
        }

        public void setLAYERS(String LAYERS) {
            this.LAYERS = LAYERS;
        }

        public String getINVENTORY() {
            return INVENTORY;
        }

        public void setINVENTORY(String INVENTORY) {
            this.INVENTORY = INVENTORY;
        }

        public String getORDER_NO() {
            return ORDER_NO;
        }

        public void setORDER_NO(String ORDER_NO) {
            this.ORDER_NO = ORDER_NO;
        }

        public String getLEFT_QTY() {
            return LEFT_QTY;
        }

        public void setLEFT_QTY(String LEFT_QTY) {
            this.LEFT_QTY = LEFT_QTY;
        }

        public String getROLL() {
            return ROLL;
        }

        public void setROLL(String ROLL) {
            this.ROLL = ROLL;
        }

        public String getDC_GROUP_BO() {
            return DC_GROUP_BO;
        }

        public void setDC_GROUP_BO(String DC_GROUP_BO) {
            this.DC_GROUP_BO = DC_GROUP_BO;
        }
    }
}
