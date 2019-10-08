package com.eeka.mespad.bo;

public class BatchSplitPackageItemBo {

    /**
     * subPackageQty : 2
     * rfid : 190926092547001
     * processlotRef : PL2801
     */

    private int subPackageQty;
    private String rfid;
    private String processlotRef;

    public int getSubPackageQty() {
        return subPackageQty;
    }

    public void setSubPackageQty(int subPackageQty) {
        this.subPackageQty = subPackageQty;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getProcesslotRef() {
        return processlotRef;
    }

    public void setProcesslotRef(String processlotRef) {
        this.processlotRef = processlotRef;
    }
}
