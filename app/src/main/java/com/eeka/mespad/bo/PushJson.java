package com.eeka.mespad.bo;

/**
 * Created by Administrator on 2017/4/13.
 */

public class PushJson {
    public static final String TYPE_EXIT = "type_exit";//退出应用
    public static final String TYPE_LOGIN = "LOGIN";//刷卡上岗消息
    public static final String TYPE_LOGOUT = "LOGOUT";//刷卡离岗消息

    private String content;
    private String type;    // 0 ： 缝制  1 ： 消息提醒  2:错误消息
    private String info;    //显示在状态栏的信息，负责提醒用户用，具体数据视具体情况而定
    private String code;    //代表消息推送成功与否
    private String message; //code值不成功时的错误提示信息

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        if (type == null)
            type = "";
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        if (info == null)
            return "";
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
