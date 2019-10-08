package com.eeka.mespad.bo;

import java.util.List;

public class ProcessDirectionBo {

    /**
     * SHOP_ORDER : 000008002344
     * ITEM : NV00657
     * materialCutFlows : [{"cutFlowList":[{"isLock":"true","isUsed":"true","materialType":"M","materialTypeDesc":"面布","operation":"CP","operationDesc":"二度裁","site":"8081","stepGroup":"41","stepId":"60"}]}]
     */

    private String SHOP_ORDER;
    private String ALL_CUT;
    private String ITEM;
    private List<CUTFLOWTEMPLETEBean> materialCutFlows;

    //回传参数
    private String shopOrderRef;
    private String isAllCut;//是否尽裁 Y/N

    public String getALL_CUT() {
        return ALL_CUT;
    }

    public void setALL_CUT(String ALL_CUT) {
        this.ALL_CUT = ALL_CUT;
    }

    public String getIsAllCut() {
        return isAllCut;
    }

    public void setIsAllCut(String isAllCut) {
        this.isAllCut = isAllCut;
    }

    public List<CUTFLOWTEMPLETEBean> getMaterialCutFlows() {
        return materialCutFlows;
    }

    public void setMaterialCutFlows(List<CUTFLOWTEMPLETEBean> materialCutFlows) {
        this.materialCutFlows = materialCutFlows;
    }

    public String getShopOrderRef() {
        return shopOrderRef;
    }

    public void setShopOrderRef(String shopOrderRef) {
        this.shopOrderRef = shopOrderRef;
    }

    public String getSHOP_ORDER() {
        return SHOP_ORDER;
    }

    public void setSHOP_ORDER(String SHOP_ORDER) {
        this.SHOP_ORDER = SHOP_ORDER;
    }

    public String getITEM() {
        return ITEM;
    }

    public void setITEM(String ITEM) {
        this.ITEM = ITEM;
    }

    public static class CUTFLOWTEMPLETEBean {

        private String materialType;
        private String materialTypeDesc;
        private List<CutFlowListBean> cutFlowList;

        public String getMaterialType() {
            return materialType;
        }

        public void setMaterialType(String materialType) {
            this.materialType = materialType;
        }

        public String getMaterialTypeDesc() {
            return materialTypeDesc;
        }

        public void setMaterialTypeDesc(String materialTypeDesc) {
            this.materialTypeDesc = materialTypeDesc;
        }

        public List<CutFlowListBean> getCutFlowList() {
            return cutFlowList;
        }

        public void setCutFlowList(List<CutFlowListBean> cutFlowList) {
            this.cutFlowList = cutFlowList;
        }

        public static class CutFlowListBean {
            /**
             * isLock : true
             * isUsed : true
             * materialType : M
             * materialTypeDesc : 面布
             * operation : CP
             * operationDesc : 二度裁
             * site : 8081
             * stepGroup : 41
             * stepId : 60
             */

            private String isLock;
            private String isUsed;
            private String materialType;
            private String materialTypeDesc;
            private String operation;
            private String operationDesc;
            private String site;
            private String stepGroup;
            private String stepId;

            public String getIsLock() {
                return isLock;
            }

            public void setIsLock(String isLock) {
                this.isLock = isLock;
            }

            public String getIsUsed() {
                return isUsed;
            }

            public void setIsUsed(String isUsed) {
                this.isUsed = isUsed;
            }

            public String getMaterialType() {
                return materialType;
            }

            public void setMaterialType(String materialType) {
                this.materialType = materialType;
            }

            public String getMaterialTypeDesc() {
                return materialTypeDesc;
            }

            public void setMaterialTypeDesc(String materialTypeDesc) {
                this.materialTypeDesc = materialTypeDesc;
            }

            public String getOperation() {
                return operation;
            }

            public void setOperation(String operation) {
                this.operation = operation;
            }

            public String getOperationDesc() {
                return operationDesc;
            }

            public void setOperationDesc(String operationDesc) {
                this.operationDesc = operationDesc;
            }

            public String getSite() {
                return site;
            }

            public void setSite(String site) {
                this.site = site;
            }

            public String getStepGroup() {
                return stepGroup;
            }

            public void setStepGroup(String stepGroup) {
                this.stepGroup = stepGroup;
            }

            public String getStepId() {
                return stepId;
            }

            public void setStepId(String stepId) {
                this.stepId = stepId;
            }
        }
    }
}
