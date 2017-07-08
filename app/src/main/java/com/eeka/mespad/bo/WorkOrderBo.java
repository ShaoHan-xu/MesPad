package com.eeka.mespad.bo;

/**
 * 作业订单实体类
 * Created by Lenovo on 2017/7/6.
 */

public class WorkOrderBo {

    private String orderNum;
    private String styleNum;
    private String startTime;
    private String endTime;
    private int amount;
    private int returnStatus;//0=未退料，1=已退料
    private int addStatus;//0=未补料，1=已补料

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getStyleNum() {
        return styleNum;
    }

    public void setStyleNum(String styleNum) {
        this.styleNum = styleNum;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(int returnStatus) {
        this.returnStatus = returnStatus;
    }

    public int getAddStatus() {
        return addStatus;
    }

    public void setAddStatus(int addStatus) {
        this.addStatus = addStatus;
    }
}
