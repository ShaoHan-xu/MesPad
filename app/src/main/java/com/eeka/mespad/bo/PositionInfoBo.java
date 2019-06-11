package com.eeka.mespad.bo;

import java.util.List;

/**
 * 站位绑定信息实体类
 * Created by Lenovo on 2017/7/21.
 */

public class PositionInfoBo {

    /**
     * OPER_INFOR : [{"TOPIC":"10","TOPIC_DESC":"CUTTING","OPERATION_BO":"OperationBO:TEST,GC-OP-LABU,A","OPERATION":"GC-OP-LABU","DESCRIPTION":"拉布工序","SOP_URL":"http://10.8.41.187/吊纱.png","QUALITY_REQUIREMENT":"1、详细了解、阅读相关资料，准确掌握生产信息。\\n 2、拿取正确的排料图。\\n 3、确保面料正反面运用正确。\\n 4、做到\u201c3齐一准一平\u201d(上、下口齐、主边齐；接头准确；布面自然平服）。\\n 5、数据录入及时、准确。","OPERATION_INSTRUCTION":"1、在PAD里阅读系统传送的生产工艺、排料方案以及拉布床次、拉布层数等信息资料。 \\n 2、取排料图（定制订单无排料图）。\\n 3、面料正反面确认。 \\n 4、根据拉布作业标准进行拉布作业。\\n 5、将拉布数据录入系统。"},{"TOPIC":"10","TOPIC_DESC":"CUTTING","OPERATION_BO":"OperationBO:TEST,GC-OP-CAIJIAN,A","OPERATION":"GC-OP-CAIJIAN","DESCRIPTION":"裁剪工序","SOP_URL":"http://10.8.41.187/吊纱.png","QUALITY_REQUIREMENT":"裁剪品质要求","OPERATION_INSTRUCTION":"裁剪工艺说明: \\n 1.裁剪操作一。 \\n  2.裁剪操作二 \\n 3.裁剪操作三 \\n"},{"TOPIC":"10","TOPIC_DESC":"CUTTING","OPERATION_BO":"OperationBO:TEST,GC-OP003,A","OPERATION":"GC-OP003","DESCRIPTION":"GC-OP003"},{"TOPIC":"10","TOPIC_DESC":"CUTTING","OPERATION_BO":"OperationBO:TEST,GC-OP002,A","OPERATION":"GC-OP002","DESCRIPTION":"GC-OP002"},{"TOPIC":"10","TOPIC_DESC":"CUTTING","OPERATION_BO":"OperationBO:TEST,GC-OP001,A","OPERATION":"GC-OP001","DESCRIPTION":"GC-OP001"}]
     * BUTTON_INFOR : [{"BUTTON_ID":"COMPLETE","BUTTON_SEQUENCE":1},{"BUTTON_ID":"LOGOUT","BUTTON_SEQUENCE":2}]
     * RESR_INFOR : {"RESOURCE_BO":"ResourceBO:TEST,AUTO_001","RESOURCE":"AUTO_001"}
     */

    private RESRINFORBean RESR_INFOR;
    private List<OPERINFORBean> OPER_INFOR;
    private List<BUTTONINFORBean> BUTTON_INFOR;

    private String PICTURE_IP;//记录不良时上传的图片路径
    private String PICTURE_USER;//记录不良时上传的图片路径
    private String PICTURE_PASSWD;//记录不良时上传的图片路径
    private String PICTURE_REMOTE;//记录不良时上传的图片路径

    public NCImgInfo getNcImgInfo() {
        NCImgInfo ncImgInfo = new NCImgInfo();
        ncImgInfo.PICTURE_IP = PICTURE_IP;
        ncImgInfo.PICTURE_USER = PICTURE_USER;
        ncImgInfo.PICTURE_PASSWD = PICTURE_PASSWD;
        ncImgInfo.PICTURE_REMOTE = PICTURE_REMOTE;
        return ncImgInfo;
    }

    public String getPICTURE_IP() {
        return PICTURE_IP;
    }

    public void setPICTURE_IP(String PICTURE_IP) {
        this.PICTURE_IP = PICTURE_IP;
    }

    public String getPICTURE_USER() {
        return PICTURE_USER;
    }

    public void setPICTURE_USER(String PICTURE_USER) {
        this.PICTURE_USER = PICTURE_USER;
    }

    public String getPICTURE_PASSWD() {
        return PICTURE_PASSWD;
    }

    public void setPICTURE_PASSWD(String PICTURE_PASSWD) {
        this.PICTURE_PASSWD = PICTURE_PASSWD;
    }

    public String getPICTURE_REMOTE() {
        return PICTURE_REMOTE;
    }

    public void setPICTURE_REMOTE(String PICTURE_REMOTE) {
        this.PICTURE_REMOTE = PICTURE_REMOTE;
    }

    public RESRINFORBean getRESR_INFOR() {
        return RESR_INFOR;
    }

    public void setRESR_INFOR(RESRINFORBean RESR_INFOR) {
        this.RESR_INFOR = RESR_INFOR;
    }

    public List<OPERINFORBean> getOPER_INFOR() {
        return OPER_INFOR;
    }

    public void setOPER_INFOR(List<OPERINFORBean> OPER_INFOR) {
        this.OPER_INFOR = OPER_INFOR;
    }

    public List<BUTTONINFORBean> getBUTTON_INFOR() {
        return BUTTON_INFOR;
    }

    public void setBUTTON_INFOR(List<BUTTONINFORBean> BUTTON_INFOR) {
        this.BUTTON_INFOR = BUTTON_INFOR;
    }

    public static class RESRINFORBean {
        /**
         * RESOURCE_BO : ResourceBO:TEST,AUTO_001
         * RESOURCE : AUTO_001
         */

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

    public static class OPERINFORBean {
        /**
         * TOPIC : 10
         * TOPIC_DESC : CUTTING
         * OPERATION_BO : OperationBO:TEST,GC-OP-LABU,A
         * OPERATION : GC-OP-LABU
         * DESCRIPTION : 拉布工序
         * SOP_URL : http://10.8.41.187/吊纱.png
         * QUALITY_REQUIREMENT : 1、详细了解、阅读相关资料，准确掌握生产信息。\n 2、拿取正确的排料图。\n 3、确保面料正反面运用正确。\n 4、做到“3齐一准一平”(上、下口齐、主边齐；接头准确；布面自然平服）。\n 5、数据录入及时、准确。
         * OPERATION_INSTRUCTION : 1、在PAD里阅读系统传送的生产工艺、排料方案以及拉布床次、拉布层数等信息资料。 \n 2、取排料图（定制订单无排料图）。\n 3、面料正反面确认。 \n 4、根据拉布作业标准进行拉布作业。\n 5、将拉布数据录入系统。
         */

        private String TOPIC;
        private String TOPIC_DESC;
        private String OPERATION_BO;
        private String OPERATION;
        private String DESCRIPTION;
        private String SOP_URL;
        private String VIDEO_URL;
        private String QUALITY_REQUIREMENT;
        private String OPERATION_INSTRUCTION;

        public String getVIDEO_URL() {
            return VIDEO_URL;
        }

        public void setVIDEO_URL(String VIDEO_URL) {
            this.VIDEO_URL = VIDEO_URL;
        }

        public String getTOPIC() {
            return TOPIC;
        }

        public void setTOPIC(String TOPIC) {
            this.TOPIC = TOPIC;
        }

        public String getTOPIC_DESC() {
            return TOPIC_DESC;
        }

        public void setTOPIC_DESC(String TOPIC_DESC) {
            this.TOPIC_DESC = TOPIC_DESC;
        }

        public String getOPERATION_BO() {
            return OPERATION_BO;
        }

        public void setOPERATION_BO(String OPERATION_BO) {
            this.OPERATION_BO = OPERATION_BO;
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

    public static class BUTTONINFORBean {
        /**
         * BUTTON_ID : COMPLETE
         * BUTTON_SEQUENCE : 1
         */

        private String BUTTON_ID;
        private int BUTTON_SEQUENCE;

        public String getBUTTON_ID() {
            return BUTTON_ID;
        }

        public void setBUTTON_ID(String BUTTON_ID) {
            this.BUTTON_ID = BUTTON_ID;
        }

        public int getBUTTON_SEQUENCE() {
            return BUTTON_SEQUENCE;
        }

        public void setBUTTON_SEQUENCE(int BUTTON_SEQUENCE) {
            this.BUTTON_SEQUENCE = BUTTON_SEQUENCE;
        }
    }

    public static class NCImgInfo {

        private String PICTURE_IP;//记录不良时上传的图片路径
        private String PICTURE_USER;//记录不良时上传的图片路径
        private String PICTURE_PASSWD;//记录不良时上传的图片路径
        private String PICTURE_REMOTE;//记录不良时上传的图片路径

        public String getPICTURE_IP() {
            return PICTURE_IP;
        }

        public void setPICTURE_IP(String PICTURE_IP) {
            this.PICTURE_IP = PICTURE_IP;
        }

        public String getPICTURE_USER() {
            return PICTURE_USER;
        }

        public void setPICTURE_USER(String PICTURE_USER) {
            this.PICTURE_USER = PICTURE_USER;
        }

        public String getPICTURE_PASSWD() {
            return PICTURE_PASSWD;
        }

        public void setPICTURE_PASSWD(String PICTURE_PASSWD) {
            this.PICTURE_PASSWD = PICTURE_PASSWD;
        }

        public String getPICTURE_REMOTE() {
            return PICTURE_REMOTE;
        }

        public void setPICTURE_REMOTE(String PICTURE_REMOTE) {
            this.PICTURE_REMOTE = PICTURE_REMOTE;
        }
    }
}
