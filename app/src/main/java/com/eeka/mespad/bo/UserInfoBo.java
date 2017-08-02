package com.eeka.mespad.bo;

/**
 * 用户信息实体类
 * Created by Lenovo on 2017/6/28.
 */

public class UserInfoBo {

    private String EMPLOYEE_NUMBER;
    private String USER;
    private String password;
    private String CARD_NUMBER;
    private String token;

    public UserInfoBo() {
    }

    public UserInfoBo(String userName, String password) {
        this.USER = userName;
        this.password = password;
    }

    public String getCARD_NUMBER() {
        return CARD_NUMBER;
    }

    public void setCARD_NUMBER(String CARD_NUMBER) {
        this.CARD_NUMBER = CARD_NUMBER;
    }

    public String getEMPLOYEE_NUMBER() {
        return EMPLOYEE_NUMBER;
    }

    public void setEMPLOYEE_NUMBER(String EMPLOYEE_NUMBER) {
        this.EMPLOYEE_NUMBER = EMPLOYEE_NUMBER;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
