package com.eeka.mespad.bo;

import java.util.List;

/**
 * 退补料信息实体类
 * Created by Lenovo on 2017/6/16.
 */

public class ReturnMaterialInfoBo {
    private String orderNum;
    private int type;//类型：0=退料/1=补料
    private List<MaterialInfoBo> MaterialInfoList;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<MaterialInfoBo> getMaterialInfoList() {
        return MaterialInfoList;
    }

    public void setMaterialInfoList(List<MaterialInfoBo> materialInfoList) {
        MaterialInfoList = materialInfoList;
    }

    public static class MaterialInfoBo {
        private String picUrl;
        private String ITEM;
        private String reason;//退/补料原因
        private String REASON_CODE;//退/补料原因
        private String QTY;//退/补料的值

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getITEM() {
            return ITEM;
        }

        public void setITEM(String ITEM) {
            this.ITEM = ITEM;
        }

        public String getREASON_CODE() {
            return REASON_CODE;
        }

        public void setREASON_CODE(String REASON_CODE) {
            this.REASON_CODE = REASON_CODE;
        }

        public String getQTY() {
            return QTY;
        }

        public void setQTY(String QTY) {
            this.QTY = QTY;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }
}
