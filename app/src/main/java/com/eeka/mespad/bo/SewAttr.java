package com.eeka.mespad.bo;

public class SewAttr {
    /**
     * attributes : {"MAT_URL":"http://10.8.41.187/ML01.jpg"}
     * description : 裁剪pad查询测试物料清单组件01
     * name : GR_BOM_COMP_01
     */

    private AttributesBean attributes;
    private String description;
    private String name;

    public AttributesBean getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesBean attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class AttributesBean {
        /**
         * MAT_URL : http://10.8.41.187/ML01.jpg
         */
        private String MAT_URL;
        private String SOP_URL;
        private String VIDEO_URL;
        private String QUALITY_REQUIREMENT;//质量要求
        private String OPERATION_INSTRUCTION;//工艺说明
        private String NC_DESCRIPTION;//不合格点
        private String OPERATION_TIME;

        public String getOPERATION_TIME() {
            return OPERATION_TIME;
        }

        public void setOPERATION_TIME(String OPERATION_TIME) {
            this.OPERATION_TIME = OPERATION_TIME;
        }

        public String getNC_DESCRIPTION() {
            return NC_DESCRIPTION;
        }

        public void setNC_DESCRIPTION(String NC_DESCRIPTION) {
            this.NC_DESCRIPTION = NC_DESCRIPTION;
        }

        public String getVIDEO_URL() {
            return VIDEO_URL;
        }

        public void setVIDEO_URL(String VIDEO_URL) {
            this.VIDEO_URL = VIDEO_URL;
        }

        public String getMAT_URL() {
            return MAT_URL;
        }

        public void setMAT_URL(String MAT_URL) {
            this.MAT_URL = MAT_URL;
        }

        public String getSOP_URL() {
            return SOP_URL;
        }

        public void setSOP_URL(String SOP_URL) {
            this.SOP_URL = SOP_URL;
        }

        public String getQUALITY_REQUIREMENT() {
            return QUALITY_REQUIREMENT;
        }

        public void setQUALITY_REQUIREMENT(String QUALITY_REQUIREMENT) {
            this.QUALITY_REQUIREMENT = QUALITY_REQUIREMENT;
        }

        public String getOPERATION_INSTRUCTION() {
            return OPERATION_INSTRUCTION;
        }

        public void setOPERATION_INSTRUCTION(String OPERATION_INSTRUCTION) {
            this.OPERATION_INSTRUCTION = OPERATION_INSTRUCTION;
        }
    }
}
