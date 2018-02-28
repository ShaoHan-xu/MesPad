package com.eeka.mespad.bo;

import java.util.List;

/**
 * 裁剪计件实体类
 * Created by Lenovo on 2018/2/27.
 */

public class CutRecordQtyBo {

    /**
     * LAYOUT_TYPE : M
     * OPERATION : SP
     * RECORD_LIST : []
     * LAYOUT : 2017122501_MRL0180001Q710_1.cut
     * MATERIAL : MRL0180001Q710
     * WORK_CENTER : YDPGCJ1
     * RESOURCE : CUT_BED_04
     * SHOP_ORDER : 2017122501
     */

    private String LAYOUT_TYPE;
    private String OPERATION;
    private String LAYOUT;
    private String MATERIAL;
    private String WORK_CENTER;
    private String RESOURCE;
    private String SHOP_ORDER;
    private List<RecordQtyItemBo> RECORD_LIST;

    public String getLAYOUT_TYPE() {
        return LAYOUT_TYPE;
    }

    public void setLAYOUT_TYPE(String LAYOUT_TYPE) {
        this.LAYOUT_TYPE = LAYOUT_TYPE;
    }

    public String getOPERATION() {
        return OPERATION;
    }

    public void setOPERATION(String OPERATION) {
        this.OPERATION = OPERATION;
    }

    public String getLAYOUT() {
        return LAYOUT;
    }

    public void setLAYOUT(String LAYOUT) {
        this.LAYOUT = LAYOUT;
    }

    public String getMATERIAL() {
        return MATERIAL;
    }

    public void setMATERIAL(String MATERIAL) {
        this.MATERIAL = MATERIAL;
    }

    public String getWORK_CENTER() {
        return WORK_CENTER;
    }

    public void setWORK_CENTER(String WORK_CENTER) {
        this.WORK_CENTER = WORK_CENTER;
    }

    public String getRESOURCE() {
        return RESOURCE;
    }

    public void setRESOURCE(String RESOURCE) {
        this.RESOURCE = RESOURCE;
    }

    public String getSHOP_ORDER() {
        return SHOP_ORDER;
    }

    public void setSHOP_ORDER(String SHOP_ORDER) {
        this.SHOP_ORDER = SHOP_ORDER;
    }

    public List<RecordQtyItemBo> getRECORD_LIST() {
        return RECORD_LIST;
    }

    public void setRECORD_LIST(List<RecordQtyItemBo> RECORD_LIST) {
        this.RECORD_LIST = RECORD_LIST;
    }

    public static class RecordQtyItemBo {
        private String USER_ID;
        private String OPERATION;
        private String OPERATION_DESC;
        private String RECORD;

        public String getOPERATION_DESC() {
            return OPERATION_DESC;
        }

        public void setOPERATION_DESC(String OPERATION_DESC) {
            this.OPERATION_DESC = OPERATION_DESC;
        }

        public String getUSER_ID() {
            return USER_ID;
        }

        public void setUSER_ID(String USER_ID) {
            this.USER_ID = USER_ID;
        }

        public String getOPERATION() {
            return OPERATION;
        }

        public void setOPERATION(String OPERATION) {
            this.OPERATION = OPERATION;
        }

        public String getRECORD() {
            return RECORD;
        }

        public void setRECORD(String RECORD) {
            this.RECORD = RECORD;
        }
    }
}
