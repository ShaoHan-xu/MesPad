package com.eeka.mespad.bo;

import java.io.Serializable;
import java.util.List;

/**
 * 缝制质检记录不良数据结构用
 * Created by Lenovo on 2017/8/29.
 */

public class RecordSewNcBo implements Serializable {

    private String PROD_COMPONENT;
    private String PROD_COMPONENT_DESC;

    public String getPROD_COMPONENT() {
        return PROD_COMPONENT;
    }

    public void setPROD_COMPONENT(String PROD_COMPONENT) {
        this.PROD_COMPONENT = PROD_COMPONENT;
    }

    public String getPROD_COMPONENT_DESC() {
        return PROD_COMPONENT_DESC;
    }

    public void setPROD_COMPONENT_DESC(String PROD_COMPONENT_DESC) {
        this.PROD_COMPONENT_DESC = PROD_COMPONENT_DESC;
    }

    public static class SewNcDesgComponentBo implements Serializable {
        private String DESG_COMPONENT;
        private String DESG_COMPONENT_DESC;

        public String getDESG_COMPONENT() {
            return DESG_COMPONENT;
        }

        public void setDESG_COMPONENT(String DESG_COMPONENT) {
            this.DESG_COMPONENT = DESG_COMPONENT;
        }

        public String getDESG_COMPONENT_DESC() {
            return DESG_COMPONENT_DESC;
        }

        public void setDESG_COMPONENT_DESC(String DESG_COMPONENT_DESC) {
            this.DESG_COMPONENT_DESC = DESG_COMPONENT_DESC;
        }
    }

    public static class SewNcProcessBo implements Serializable {
        private String OPERATION;
        private String OPERATION_BO;
        private String DESCRIPTION;

        public String getOPERATION() {
            return OPERATION;
        }

        public void setOPERATION(String OPERATION) {
            this.OPERATION = OPERATION;
        }

        public String getOPERATION_BO() {
            return OPERATION_BO;
        }

        public void setOPERATION_BO(String OPERATION_BO) {
            this.OPERATION_BO = OPERATION_BO;
        }

        public String getDESCRIPTION() {
            return DESCRIPTION;
        }

        public void setDESCRIPTION(String DESCRIPTION) {
            this.DESCRIPTION = DESCRIPTION;
        }
    }
}
