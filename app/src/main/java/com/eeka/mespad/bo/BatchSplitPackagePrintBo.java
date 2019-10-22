package com.eeka.mespad.bo;

public class BatchSplitPackagePrintBo {

    /**
     * item : NL00048
     * processLotRef : ProcessLotBO:8081,PL2858-2
     * rfid : 190926174759001
     * shopOrder : 000008002271
     * subPackageQty : 15
     * subPackageSeq : 2
     */

    private String item;
    private String processLotRef;
    private String rfid;
    private String sizeCode;
    private String shopOrder;
    private String workNo;
    private boolean isPrinted;
    private int subPackageQty;
    private int subPackageSeq;

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public boolean isPrinted() {
        return isPrinted;
    }

    public void setPrinted(boolean printed) {
        isPrinted = printed;
    }

    public String getSizeCode() {
        return sizeCode;
    }

    public void setSizeCode(String sizeCode) {
        this.sizeCode = sizeCode;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getProcessLotRef() {
        return processLotRef;
    }

    public void setProcessLotRef(String processLotRef) {
        this.processLotRef = processLotRef;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(String shopOrder) {
        this.shopOrder = shopOrder;
    }

    public int getSubPackageQty() {
        return subPackageQty;
    }

    public void setSubPackageQty(int subPackageQty) {
        this.subPackageQty = subPackageQty;
    }

    public int getSubPackageSeq() {
        return subPackageSeq;
    }

    public void setSubPackageSeq(int subPackageSeq) {
        this.subPackageSeq = subPackageSeq;
    }
}
