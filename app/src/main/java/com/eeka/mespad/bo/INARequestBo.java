package com.eeka.mespad.bo;

/**
 * INA进出站请求参数实体类
 */
public class INARequestBo {
    private String site;
    private String hangerId;
    private String lineId;
    private String stationId;
    private String inTime;
    private String outTime;
    private String doTime;

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

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getDoTime() {
        return doTime;
    }

    public void setDoTime(String doTime) {
        this.doTime = doTime;
    }
}
