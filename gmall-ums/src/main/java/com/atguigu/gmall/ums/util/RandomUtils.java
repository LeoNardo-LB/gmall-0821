package com.atguigu.gmall.ums.util;

import java.util.Random;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
public class RandomUtils {

    /**
     * 生成指定位数的随机数字字符串
     * @param length
     * @return
     */
    public static String generateRandomNumberStr(Integer length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int num = new Random().nextInt(10);
            sb.append(num);
        }
        return sb.toString();
    }

    /**
     * 生成指定位数的随机字符串
     * @param length
     * @return
     */
    public static String generateRandomStr(Integer length) {
        StringBuilder sb = new StringBuilder(length);
        String candicator = "123456789~!@#$%^&*()_+-=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(candicator.length());
            sb.append(candicator.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateRandomStr(10));
    }

}
