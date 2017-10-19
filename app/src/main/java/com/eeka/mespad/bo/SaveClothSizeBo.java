package com.eeka.mespad.bo;

import java.util.List;

/**
 * 保存成衣尺寸数据实体类
 * Created by Lenovo on 2017/10/10.
 */

public class SaveClothSizeBo {
    private String SFC;
    private String OPERATION_BO;
    private String RESOURCE_BO;
    private String DC_GROUP;
    private List<Item> DCS;

    public String getSFC() {
        return SFC;
    }

    public void setSFC(String SFC) {
        this.SFC = SFC;
    }

    public String getOPERATION_BO() {
        return OPERATION_BO;
    }

    public void setOPERATION_BO(String OPERATION_BO) {
        this.OPERATION_BO = OPERATION_BO;
    }

    public String getRESOURCE_BO() {
        return RESOURCE_BO;
    }

    public void setRESOURCE_BO(String RESOURCE_BO) {
        this.RESOURCE_BO = RESOURCE_BO;
    }

    public String getDC_GROUP() {
        return DC_GROUP;
    }

    public void setDC_GROUP(String DC_GROUP) {
        this.DC_GROUP = DC_GROUP;
    }

    public List<Item> getDCS() {
        return DCS;
    }

    public void setDCS(List<Item> DCS) {
        this.DCS = DCS;
    }

    public static class Item {
        private String MEASURED_ATTRIBUTE;
        private String VALUE;
        private String DATA_TYPE;
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

        public String getVALUE() {
            return VALUE;
        }

        public void setVALUE(String VALUE) {
            this.VALUE = VALUE;
        }

        public String getDATA_TYPE() {
            return DATA_TYPE;
        }

        public void setDATA_TYPE(String DATA_TYPE) {
            this.DATA_TYPE = DATA_TYPE;
        }
    }

}
