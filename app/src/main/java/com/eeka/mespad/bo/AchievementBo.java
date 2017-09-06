package com.eeka.mespad.bo;

import java.util.List;

/**
 * Created by Lenovo on 2017/9/2.
 */

public class AchievementBo {

    /**
     * achievementItemList : [{"id":null,"employeeId":"123","employeeName":"姚淑芳","employeeProportion":null,"amount":"700","saleBillingId":null,"saleNo":null,"saleAmount":null},{"id":null,"employeeId":"234","employeeName":"黄密香","employeeProportion":null,"amount":"200","saleBillingId":null,"saleNo":null,"saleAmount":null},{"id":null,"employeeId":"345","employeeName":"李咏妍","employeeProportion":null,"amount":"200","saleBillingId":null,"saleNo":null,"saleAmount":null},{"id":null,"employeeId":"456","employeeName":"李丽萍","employeeProportion":null,"amount":"200","saleBillingId":null,"saleNo":null,"saleAmount":null}]
     * saleBillingId : null
     * currentTimeMs : 1504255605712
     */

    private String saleBillingId;
    private long currentTimeMs;
    private List<AchievementItemListBean> achievementItemList;

    public Object getSaleBillingId() {
        return saleBillingId;
    }

    public void setSaleBillingId(String saleBillingId) {
        this.saleBillingId = saleBillingId;
    }

    public long getCurrentTimeMs() {
        return currentTimeMs;
    }

    public void setCurrentTimeMs(long currentTimeMs) {
        this.currentTimeMs = currentTimeMs;
    }

    public List<AchievementItemListBean> getAchievementItemList() {
        return achievementItemList;
    }

    public void setAchievementItemList(List<AchievementItemListBean> achievementItemList) {
        this.achievementItemList = achievementItemList;
    }

    public static class AchievementItemListBean {
        /**
         * id : null
         * employeeId : 123
         * employeeName : 姚淑芳
         * employeeProportion : null
         * amount : 700
         * saleBillingId : null
         * saleNo : null
         * saleAmount : null
         */

        private String id;
        private String employeeId;
        private String employeeName;
        private String employeeProportion;
        private String amount;
        private String saleBillingId;
        private String saleNo;
        private String saleAmount;

        public Object getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public Object getEmployeeProportion() {
            return employeeProportion;
        }

        public void setEmployeeProportion(String employeeProportion) {
            this.employeeProportion = employeeProportion;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public Object getSaleBillingId() {
            return saleBillingId;
        }

        public void setSaleBillingId(String saleBillingId) {
            this.saleBillingId = saleBillingId;
        }

        public Object getSaleNo() {
            return saleNo;
        }

        public void setSaleNo(String saleNo) {
            this.saleNo = saleNo;
        }

        public Object getSaleAmount() {
            return saleAmount;
        }

        public void setSaleAmount(String saleAmount) {
            this.saleAmount = saleAmount;
        }
    }
}
