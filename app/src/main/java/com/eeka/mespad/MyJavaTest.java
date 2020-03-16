package com.eeka.mespad;

import com.eeka.mespad.bo.UserInfoBo;

import java.util.ArrayList;
import java.util.List;

public  class MyJavaTest<T> {

    public static void main(String[] args) {
//        StringBuilder s = new StringBuilder("1234567890");
//        System.out.println(s.substring(1, s.length()));

        MyJavaTest test = new MyJavaTest();

        //测试值传递与引用传递
//        String mStr = "123";
//        Bean mBean = test.new Bean();
//        mBean.str = "000";
//        test.func(mStr, mBean);
//        mStr = "abc";
//        mBean.str = "111";
//        mBean = null;

        //测试泛型转换
        List data = test.getData();
        for (int i = 0; i < data.size(); i++) {
            UserInfoBo user = (UserInfoBo) data.get(i);
            System.out.println(user.getUSER());
        }
    }

    private List<T> getData(){
        List<T> userInfoBos = new ArrayList<>();
        UserInfoBo infoBo = new UserInfoBo("hh","ll");
        userInfoBos.add((T) infoBo);
        return userInfoBos;
    }

    /**
     * 在Java中所有的参数传递，不管基本类型还是引用类型，都是值传递，或者说是副本传递。即方法传进来的参数都会拷贝一份一模一样的数据
     * 只是在传递过程中：
     * <p>
     * 如果是对基本数据类型的数据进行操作，由于原始内容和副本都是存储实际值，并且是在不同的栈区，因此形参的操作，不影响原始内容。
     * <p>
     * 如果是对引用类型的数据进行操作，分两种情况，一种是形参和实参保持指向同一个对象地址，则形参的操作，会影响实参指向的对象的内容。一种是形参被改动指向新的对象地址（如重新赋值引用），则形参的操作，不会影响实参指向的对象的内容。
     */
    private void func(final String str, final Bean bean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(str);
                    System.out.println(bean.str);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class Bean {
        String str;
    }
}
