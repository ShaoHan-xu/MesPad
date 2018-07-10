package com.eeka.mespad.bo;

public class INAOutRequestBo {
    private String site;
    private String hangerId;
    private String lineId;
    private String stationId;
    private String outTime;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getHangerId() {
        return hangerId;
    }

    public void setHangerId(String hangerId) {
        this.hangerId = hangerId;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }
}
