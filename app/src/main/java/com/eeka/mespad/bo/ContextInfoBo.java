package com.eeka.mespad.bo;

import java.util.List;

/**
 * 应用上下文信息
 * Created by Lenovo on 2017/7/28.
 */

public class ContextInfoBo {

    /**
     * HANDLE : PositionBO:TEST,20,2000
     * LINE_CATEGORY : 2000
     * LOGIN_USER : eeTHAN####ETHAN
     * PAD_IP : 192.168.0.8
     * POSITION : 20
     * POSITION_TYPE : NORMAL
     * RESOURCE : GC_CC_003
     * SITE : TEST
     * STATUS : 0
     * WORK_CENTER : GC-WC-001-01
     */

    private String HANDLE;
    private String LINE_CATEGORY;
    private String LOGIN_USER;
    private String PAD_IP;
    private String POSITION;
    private String POSITION_TYPE;
    private String RESOURCE;
    private String SITE;
    private String STATUS;
    private String WORK_CENTER;
    private List<UserInfoBo> LOGIN_USER_LIST;

    public List<UserInfoBo> getLOGIN_USER_LIST() {
        return LOGIN_USER_LIST;
    }

    public void setLOGIN_USER_LIST(List<UserInfoBo> LOGIN_USER_LIST) {
        this.LOGIN_USER_LIST = LOGIN_USER_LIST;
    }

    public String getHANDLE() {
        return HANDLE;
    }

    public void setHANDLE(String HANDLE) {
        this.HANDLE = HANDLE;
    }

    public String getLINE_CATEGORY() {
        return LINE_CATEGORY;
    }

    public void setLINE_CATEGORY(String LINE_CATEGORY) {
        this.LINE_CATEGORY = LINE_CATEGORY;
    }

    public String getLOGIN_USER() {
        return LOGIN_USER;
    }

    public void setLOGIN_USER(String LOGIN_USER) {
        this.LOGIN_USER = LOGIN_USER;
    }

    public String getPAD_IP() {
        return PAD_IP;
    }

    public void setPAD_IP(String PAD_IP) {
        this.PAD_IP = PAD_IP;
    }

    public String getPOSITION() {
        return POSITION;
    }

    public void setPOSITION(String POSITION) {
        this.POSITION = POSITION;
    }

    public String getPOSITION_TYPE() {
        return POSITION_TYPE;
    }

    public void setPOSITION_TYPE(String POSITION_TYPE) {
        this.POSITION_TYPE = POSITION_TYPE;
    }

    public String getRESOURCE() {
        return RESOURCE;
    }

    public void setRESOURCE(String RESOURCE) {
        this.RESOURCE = RESOURCE;
    }

    public String getSITE() {
        return SITE;
    }

    public void setSITE(String SITE) {
        this.SITE = SITE;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getWORK_CENTER() {
        return WORK_CENTER;
    }

    public void setWORK_CENTER(String WORK_CENTER) {
        this.WORK_CENTER = WORK_CENTER;
    }
}
