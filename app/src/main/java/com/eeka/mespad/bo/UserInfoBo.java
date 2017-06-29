package com.eeka.mespad.bo;

/**
 * 用户信息实体类
 * Created by Lenovo on 2017/6/28.
 */

public class UserInfoBo {

    private String userName;
    private String password;
    private String token;

    public UserInfoBo() {
    }

    public UserInfoBo(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
