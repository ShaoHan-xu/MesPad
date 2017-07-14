package com.eeka.mespad.bo;

/**
 * 作业订单实体类
 * Created by Lenovo on 2017/7/6.
 */

public class WorkOrderBo {

    private String shopOrder;
    private String plannedItem;
    private String plannedStartDate;
    private String plannedCompDate;
    private int qtyToBuild;
    private int status;//10:未调度 20:已调度未制卡 30:已制卡未完成 40:生产完成
    private int priority;//

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(String shopOrder) {
        this.shopOrder = shopOrder;
    }

    public String getPlannedItem() {
        return plannedItem;
    }

    public void setPlannedItem(String plannedItem) {
        this.plannedItem = plannedItem;
    }

    public String getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(String plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public String getPlannedCompDate() {
        return plannedCompDate;
    }

    public void setPlannedCompDate(String plannedCompDate) {
        this.plannedCompDate = plannedCompDate;
    }

    public int getQtyToBuild() {
        return qtyToBuild;
    }

    public void setQtyToBuild(int qtyToBuild) {
        this.qtyToBuild = qtyToBuild;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
