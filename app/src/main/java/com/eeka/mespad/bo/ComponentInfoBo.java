package com.eeka.mespad.bo;

import java.util.List;

public class ComponentInfoBo {

    private List<String> PICTURE_URL;
    private List<MaterialInfoBean> MaterialInfo;

    public List<String> getPICTURE_URL() {
        return PICTURE_URL;
    }

    public void setPICTURE_URL(List<String> PICTURE_URL) {
        this.PICTURE_URL = PICTURE_URL;
    }

    public List<MaterialInfoBean> getMaterialInfo() {
        return MaterialInfo;
    }

    public void setMaterialInfo(List<MaterialInfoBean> MaterialInfo) {
        this.MaterialInfo = MaterialInfo;
    }

    public static class MaterialInfoBean {
        /**
         * MATERIAL_CODE : FF0301001Q70100
         * MATERIAL_NAME : Q710暗青紫棉类仿牛仔梭织净面测试
         * QTY : 1.0
         */

        private String MATERIAL_CODE;
        private String MATERIAL_NAME;
        private String MAT_URL;
        private double QTY;

        public String getMAT_URL() {
            return MAT_URL;
        }

        public void setMAT_URL(String MAT_URL) {
            this.MAT_URL = MAT_URL;
        }

        public String getMATERIAL_CODE() {
            return MATERIAL_CODE;
        }

        public void setMATERIAL_CODE(String MATERIAL_CODE) {
            this.MATERIAL_CODE = MATERIAL_CODE;
        }

        public String getMATERIAL_NAME() {
            return MATERIAL_NAME;
        }

        public void setMATERIAL_NAME(String MATERIAL_NAME) {
            this.MATERIAL_NAME = MATERIAL_NAME;
        }

        public double getQTY() {
            return QTY;
        }

        public void setQTY(double QTY) {
            this.QTY = QTY;
        }
    }
}
