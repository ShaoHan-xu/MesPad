package com.eeka.mespad.bo;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * 上传拉布数据参数实体类
 * Created by Lenovo on 2017/7/17.
 */

public class UpdateLabuBo implements Serializable {

    private List<String> OPERATIONS;
    private String RESOURCE_BO;
    private String SHOP_ORDER_BO;
    @JSONField(name = "LAYOUTS")
    private List<Layouts> LAYOUTS;

    public List<String> getOPERATIONS() {
        return OPERATIONS;
    }

    public void setOPERATIONS(List<String> OPERATIONS) {
        this.OPERATIONS = OPERATIONS;
    }

    public String getRESOURCE_BO() {
        return RESOURCE_BO;
    }

    public void setRESOURCE_BO(String RESOURCE_BO) {
        this.RESOURCE_BO = RESOURCE_BO;
    }

    public String getSHOP_ORDER_BO() {
        return SHOP_ORDER_BO;
    }

    public void setSHOP_ORDER_BO(String SHOP_ORDER_BO) {
        this.SHOP_ORDER_BO = SHOP_ORDER_BO;
    }

    public List<Layouts> getLAYOUTS() {
        return LAYOUTS;
    }

    public void setLAYOUTS(List<Layouts> LAYOUTS) {
        this.LAYOUTS = LAYOUTS;
    }

    public static class Layouts {
        @JSONField(name = "Z_LAYOUT_BO")
        private String Z_LAYOUT_BO;
        private String MAT_NO;
        private String PLAN_LAYERS;
        private List<MatItem> DETAILS;

        public String getMAT_NO() {
            return MAT_NO;
        }

        public void setMAT_NO(String MAT_NO) {
            this.MAT_NO = MAT_NO;
        }

        public String getZ_LAYOUT_BO() {
            return Z_LAYOUT_BO;
        }

        public void setZ_LAYOUT_BO(String z_LAYOUT_BO) {
            Z_LAYOUT_BO = z_LAYOUT_BO;
        }

        public String getPLAN_LAYERS() {
            return PLAN_LAYERS;
        }

        public void setPLAN_LAYERS(String PLAN_LAYERS) {
            this.PLAN_LAYERS = PLAN_LAYERS;
        }

        public List<MatItem> getDETAILS() {
            return DETAILS;
        }

        public void setDETAILS(List<MatItem> DETAILS) {
            this.DETAILS = DETAILS;
        }
    }

    public static class MatItem {
        private String MAT_NO;
        private String ITEM_BO;
        private String VOLUME;
        private String LENGTH;
        private String LAYERS;
        private String ODDMENTS;
        private String REMARK;
        private boolean editEnable = true;

        public String getMAT_NO() {
            return MAT_NO;
        }

        public void setMAT_NO(String MAT_NO) {
            this.MAT_NO = MAT_NO;
        }

        public boolean isEditEnable() {
            return editEnable;
        }

        public void setEditEnable(boolean editEnable) {
            this.editEnable = editEnable;
        }

        public String getITEM_BO() {
            return ITEM_BO;
        }

        public void setITEM_BO(String ITEM_BO) {
            this.ITEM_BO = ITEM_BO;
        }

        public String getVOLUME() {
            return VOLUME;
        }

        public void setVOLUME(String VOLUME) {
            this.VOLUME = VOLUME;
        }

        public String getLENGTH() {
            return LENGTH;
        }

        public void setLENGTH(String LENGTH) {
            this.LENGTH = LENGTH;
        }

        public String getLAYERS() {
            return LAYERS;
        }

        public void setLAYERS(String LAYERS) {
            this.LAYERS = LAYERS;
        }

        public String getODDMENTS() {
            return ODDMENTS;
        }

        public void setODDMENTS(String ODDMENTS) {
            this.ODDMENTS = ODDMENTS;
        }

        public String getREMARK() {
            return REMARK;
        }

        public void setREMARK(String REMARK) {
            this.REMARK = REMARK;
        }
    }

}
