package com.eeka.mespad.bo;

import java.io.Serializable;
import java.util.List;

/**
 * 记录缝制质检不良数据结构
 * Created by Lenovo on 2017/8/29.
 */

public class UpdateSewNcBo {
    /**
     * sfcRef : SFCBO:TEST,TEST672
     * resourceRef : ResourceBO:TEST,AUTO_001
     * reworkOperationList : [{"reworkOperation":"GC-OP-CAIJIAN","sequence":1,"operationDesc":"GC-OP-CAIJIAN"},{"reworkOperation":"GC-OP-LABU","sequence":2,"operationDesc":"GC-OP-LABU"}]
     * ncCodeOperationList : [{"ncCodeRef":"NCCodeBO:TEST,GC-OP-CAIJIAN","operation":"GC-OP-CAIJIAN"},{"ncCodeRef":"NCCodeBO:TEST,GC-OP-LABU","operation":"GC-OP-LABU"}]
     */
    private String sfcRef;
    private String resourceRef;
    private List<ReworkOperationListBean> reworkOperationList;
    private List<NcCodeOperationListBean> ncCodeOperationList;

    public String getSfcRef() {
        return sfcRef;
    }

    public void setSfcRef(String sfcRef) {
        this.sfcRef = sfcRef;
    }

    public String getResourceRef() {
        return resourceRef;
    }

    public void setResourceRef(String resourceRef) {
        this.resourceRef = resourceRef;
    }

    public List<ReworkOperationListBean> getReworkOperationList() {
        return reworkOperationList;
    }

    public void setReworkOperationList(List<ReworkOperationListBean> reworkOperationList) {
        this.reworkOperationList = reworkOperationList;
    }

    public List<NcCodeOperationListBean> getNcCodeOperationList() {
        return ncCodeOperationList;
    }

    public void setNcCodeOperationList(List<NcCodeOperationListBean> ncCodeOperationList) {
        this.ncCodeOperationList = ncCodeOperationList;
    }

    public static class ReworkOperationListBean {
        /**
         * reworkOperation : GC-OP-CAIJIAN
         * sequence : 1
         * operationDesc : GC-OP-CAIJIAN
         */

        private String reworkOperation;
        private int sequence;
        private String operationDesc;

        public String getReworkOperation() {
            return reworkOperation;
        }

        public void setReworkOperation(String reworkOperation) {
            this.reworkOperation = reworkOperation;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public String getOperationDesc() {
            return operationDesc;
        }

        public void setOperationDesc(String operationDesc) {
            this.operationDesc = operationDesc;
        }
    }

    public static class NcCodeOperationListBean implements Serializable{
        /**
         * ncCodeRef : NCCodeBO:TEST,GC-OP-CAIJIAN
         * operation : GC-OP-CAIJIAN
         */

        private String ncCodeRef;
        private String operation;

        public String getNcCodeRef() {
            return ncCodeRef;
        }

        public void setNcCodeRef(String ncCodeRef) {
            this.ncCodeRef = ncCodeRef;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }
    }
}
