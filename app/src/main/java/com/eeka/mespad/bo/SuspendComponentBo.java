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
    private String SHOP_ORDER;
    private String ITEM;
    private List<COMPONENTSBean> COMPONENTS;

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
