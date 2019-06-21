package com.eeka.mespad.bo;

/**
 * Created by Administrator on 2017/4/13.
 */

public class PushJson {
    public static final String TYPE_EXIT = "EXIT";//退出应用
    public static final String TYPE_FINISH_MAIN = "FINISH_MAIN";//关闭首页
    public static final String TYPE_LOGIN = "LOGIN";//刷卡上岗消息
    public static final String TYPE_LOGOUT = "LOGOUT";//刷卡离岗消息
    public static final String TYPE_RFID = "RFID";//NFC刷卡
    public static final String TYPE_WARNING = "WARNING";//警告
    public static final String TYPE_WARNING_UNDETECT = "WARNING_UNDETECT";//警告
    public static final String TYPE_TOAST = "TOAST";//toast
    public static final String TYPE_ErrDialogDismiss = "ErrDialogDismiss";//错误弹框消失
    public static final String TYPE_HANGERID = "hangerId";//衣架号
    public static final String TYPE_Maintenance = "Maintenance";//保养视频
    public static final String TYPE_ALERT = "ALERT";//提示

    private String content;
    private String type;
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
