package com.eeka.mespad.bo;

import java.util.List;

/**
 * Created by Lenovo on 2017/8/18.
 */

public class SuspendComponentBo {

    /**
     * SFC : TEST715
     * COMPONENTS : [{"componentId":"HF","isNeedSubContract":"false","isSubContractCompleted":"false","operationRef":"OperationBO:TEST,GR_HF_01,#"},{"componentId":"QF","isNeedSubContract":"true","isSubContractCompleted":"true","operationRef":"OperationBO:TEST,GR_QF_01,#"},{"componentId":"YT","isNeedSubContract":"false","isSubContractCompleted":"false","operationRef":"OperationBO:TEST,GR_YT_01,#"}]
     */

    private String SFC;
    private String PROCESS_LOT;
    private String LOT_SFC_QTY;
    private String REAL_CUT_NUM;
    private String SFC_SIZE;
    private String SHOP_ORDER;
    private String SALES_ORDER;
    private String ITEM;
    private String ITEM_DESC;
    private int QTY_ORDERED;
    private String QTY_COMPLETE;
    private List<COMPONENTSBean> COMPONENTS;

    public String getPROCESS_LOT() {
        return PROCESS_LOT;
    }

    public void setPROCESS_LOT(String PROCESS_LOT) {
        this.PROCESS_LOT = PROCESS_LOT;
    }

    public String getLOT_SFC_QTY() {
        return LOT_SFC_QTY;
    }

    public void setLOT_SFC_QTY(String LOT_SFC_QTY) {
        this.LOT_SFC_QTY = LOT_SFC_QTY;
    }

    public String getREAL_CUT_NUM() {
        return REAL_CUT_NUM;
    }

    public void setREAL_CUT_NUM(String REAL_CUT_NUM) {
        this.REAL_CUT_NUM = REAL_CUT_NUM;
    }

    public String getSFC_SIZE() {
        return SFC_SIZE;
    }

    public void setSFC_SIZE(String SFC_SIZE) {
        this.SFC_SIZE = SFC_SIZE;
    }

    public String getSALES_ORDER() {
        return SALES_ORDER;
    }

    public void setSALES_ORDER(String SALES_ORDER) {
        this.SALES_ORDER = SALES_ORDER;
    }

    public String getITEM_DESC() {
        return ITEM_DESC;
    }

    public void setITEM_DESC(String ITEM_DESC) {
        this.ITEM_DESC = ITEM_DESC;
    }

    public int getQTY_ORDERED() {
        return QTY_ORDERED;
    }

    public void setQTY_ORDERED(int QTY_ORDERED) {
        this.QTY_ORDERED = QTY_ORDERED;
    }

    public String getQTY_COMPLETE() {
        return QTY_COMPLETE;
    }

    public void setQTY_COMPLETE(String QTY_COMPLETE) {
        this.QTY_COMPLETE = QTY_COMPLETE;
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

    public String getSFC() {
        return SFC;
    }

    public void setSFC(String SFC) {
        this.SFC = SFC;
    }

    public List<COMPONENTSBean> getCOMPONENTS() {
        return COMPONENTS;
    }

    public void setCOMPONENTS(List<COMPONENTSBean> COMPONENTS) {
        this.COMPONENTS = COMPONENTS;
    }

    public static class COMPONENTSBean {
        /**
         * componentId : HF
         * componentName : 部件名称
         * isNeedSubContract : false
         * isSubContractCompleted : false
         * operationRef : OperationBO:TEST,GR_HF_01,#
         */

        private String componentId;
        private String componentName;
        private String isNeedSubContract;
        private String isSubContractCompleted;
        private String operationRef;
        private String isBound;
        private String isMaster;//是否主部件，用于判断是否需要绑定洗水唛
        private String isUnderCarry;//是否有下架标识，用于判断是否需要绑定洗水唛

        public String getIsUnderCarry() {
            return isUnderCarry;
        }

        public void setIsUnderCarry(String isUnderCarry) {
            this.isUnderCarry = isUnderCarry;
        }

        public String getIsMaster() {
            return isMaster;
        }

        public void setIsMaster(String isMaster) {
            this.isMaster = isMaster;
        }

        public String getIsBound() {
            return isBound;
        }

        public void setIsBound(String isBound) {
            this.isBound = isBound;
        }

        public String getComponentName() {
            return componentName;
        }

        public void setComponentName(String componentName) {
            this.componentName = componentName;
        }

        public String getComponentId() {
            return componentId;
        }

        public void setComponentId(String componentId) {
            this.componentId = componentId;
        }

        public String getIsNeedSubContract() {
            return isNeedSubContract;
        }

        public void setIsNeedSubContract(String isNeedSubContract) {
            this.isNeedSubContract = isNeedSubContract;
        }

        public String getIsSubContractCompleted() {
            return isSubContractCompleted;
        }

        public void setIsSubContractCompleted(String isSubContractCompleted) {
            this.isSubContractCompleted = isSubContractCompleted;
        }

        public String getOperationRef() {
            return operationRef;
        }

        public void setOperationRef(String operationRef) {
            this.operationRef = operationRef;
        }
    }
}
