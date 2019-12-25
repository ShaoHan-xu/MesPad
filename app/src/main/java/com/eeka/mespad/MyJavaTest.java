package com.eeka.mespad;

public class MyJavaTest {

    private static String mStr;
    private static Bean mBean;

    public static void main(String[] args) {
//        StringBuilder s = new StringBuilder("1234567890");
//        System.out.println(s.substring(1, s.length()));

        mStr = "123";
        mBean = new Bean();
        mBean.str = "000";
        func(mStr,mBean.str);
        mStr = "abc";
        mBean = null;
        System.out.println(mStr);
    }

    private static void func(final String str,final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(str);
                    System.out.println(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static class Bean {
        String str;
    }
}
