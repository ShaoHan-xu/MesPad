package com.eeka.mespad.bo;

import java.util.List;

/**
 * 质检工位成衣尺寸数据实体类
 * Created by Lenovo on 2017/10/10.
 */

public class ClothSizeBo {

    /**
     * DC_GROUP : GARMENT_SIZE_TEST
     * DC_PARRMS : [{"MEASURED_ATTRIBUTE":"KYW","PARAM_DESC":"描述","COLLECTED_VALUE":"1","ALLOW_COLLECTION":"true","DATA_TYPE":"N","UNIT":"cm","VALUE":"94"},{"MEASURED_ATTRIBUTE":"TW","DESCRIPTION":"臀围","COLLECTED_VALUE":"11","ALLOW_COLLECTION":"true","DATA_TYPE":"N","UNIT":"cm","VALUE":"103.5","STANDARD":"11"},{"MEASURED_ATTRIBUTE":"PW","DESCRIPTION":"脾围","COLLECTED_VALUE":"33","ALLOW_COLLECTION":"true","DATA_TYPE":"N","UNIT":"cm","VALUE":"61"},{"MEASURED_ATTRIBUTE":"XTW","COLLECTED_VALUE":"22","ALLOW_COLLECTION":"true","DATA_TYPE":"N","UNIT":"cm","VALUE":"38.5"},{"MEASURED_ATTRIBUTE":"XW","DESCRIPTION":"膝围","ALLOW_COLLECTION":"false","UNIT":"cm","VALUE":"40.39"},{"MEASURED_ATTRIBUTE":"JW","DESCRIPTION":"脚围","ALLOW_COLLECTION":"false","UNIT":"cm","VALUE":"30.50"},{"MEASURED_ATTRIBUTE":"ZD","ALLOW_COLLECTION":"false","UNIT":"cm","VALUE":"27.50"}]
     */

    private String DC_GROUP;
    private List<DCPARRMSBean> DC_PARRMS;

    public String getDC_GROUP() {
        return DC_GROUP;
    }

    public void setDC_GROUP(String DC_GROUP) {
        this.DC_GROUP = DC_GROUP;
    }

    public List<DCPARRMSBean> getDC_PARRMS() {
        return DC_PARRMS;
    }

    public void setDC_PARRMS(List<DCPARRMSBean> DC_PARRMS) {
        this.DC_PARRMS = DC_PARRMS;
    }

    public static class DCPARRMSBean {
        /**
         * MEASURED_ATTRIBUTE : KYW
         * PARAM_DESC : 描述
         * COLLECTED_VALUE : 1
         * ALLOW_COLLECTION : true
         * DATA_TYPE : N
         * UNIT : cm
         * VALUE : 94
         * DESCRIPTION : 臀围
         * STANDARD : 11
         */

        private String MEASURED_ATTRIBUTE;
        private String COLLECTED_VALUE;
        private String ALLOW_COLLECTION;
        private String DATA_TYPE;
        private String UNIT;
        private String VALUE;
        private String DESCRIPTION;
        private String STANDARD;
        private String PARAM_DESC;

        public String getPARAM_DESC() {
            return PARAM_DESC;
        }

        public void setPARAM_DESC(String PARAM_DESC) {
            this.PARAM_DESC = PARAM_DESC;
        }

        public String getMEASURED_ATTRIBUTE() {
            return MEASURED_ATTRIBUTE;
        }

        public void setMEASURED_ATTRIBUTE(String MEASURED_ATTRIBUTE) {
            this.MEASURED_ATTRIBUTE = MEASURED_ATTRIBUTE;
        }

        public String getCOLLECTED_VALUE() {
            return COLLECTED_VALUE;
        }

        public void setCOLLECTED_VALUE(String COLLECTED_VALUE) {
            this.COLLECTED_VALUE = COLLECTED_VALUE;
        }

        public String getALLOW_COLLECTION() {
            return ALLOW_COLLECTION;
        }

        public void setALLOW_COLLECTION(String ALLOW_COLLECTION) {
            this.ALLOW_COLLECTION = ALLOW_COLLECTION;
        }

        public String getDATA_TYPE() {
            return DATA_TYPE;
        }

        public void setDATA_TYPE(String DATA_TYPE) {
            this.DATA_TYPE = DATA_TYPE;
        }

        public String getUNIT() {
            return UNIT;
        }

        public void setUNIT(String UNIT) {
            this.UNIT = UNIT;
        }

        public String getVALUE() {
            return VALUE;
        }

        public void setVALUE(String VALUE) {
            this.VALUE = VALUE;
        }

        public String getDESCRIPTION() {
            return DESCRIPTION;
        }

        public void setDESCRIPTION(String DESCRIPTION) {
            this.DESCRIPTION = DESCRIPTION;
        }

        public String getSTANDARD() {
            return STANDARD;
        }

        public void setSTANDARD(String STANDARD) {
            this.STANDARD = STANDARD;
        }
    }
}
