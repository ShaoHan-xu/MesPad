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
        private String num;
        private String reason;//退/补料原因
        private String planUse;//计划用料/米
        private String realUse;//实际用料/米
        private String value;//退/补料的值
        private int type;//类型，退|补

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getPlanUse() {
            return planUse;
        }

        public void setPlanUse(String planUse) {
            this.planUse = planUse;
        }

        public String getRealUse() {
            return realUse;
        }

        public void setRealUse(String realUse) {
            this.realUse = realUse;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }
}
