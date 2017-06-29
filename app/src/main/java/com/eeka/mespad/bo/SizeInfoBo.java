package com.eeka.mespad.bo;

/**
 * Created by Lenovo on 2017/6/26.
 */

public class SizeInfoBo {

    private String yardage;
    private String color;
    private int count;

    public SizeInfoBo() {
    }

    public SizeInfoBo(String yardage, String color, int count) {
        this.yardage = yardage;
        this.color = color;
        this.count = count;
    }

    public String getYardage() {
        return yardage;
    }

    public void setYardage(String yardage) {
        this.yardage = yardage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
