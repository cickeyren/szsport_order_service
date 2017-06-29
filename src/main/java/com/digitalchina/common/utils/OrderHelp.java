package com.digitalchina.common.utils;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by yang_ on 2017/5/16.
 */
public class OrderHelp {
    public static String getUUID() {
        return UUIDUtil.generateUUID();
    }

    //订单流水
    public static String getOrderNum() {
        return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + (new Random().nextInt(90) + 10);
    }

    //培训订单流水号
    public static String getCurriculumOrderNum() {
        return new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date()) + getRandomNum(3);
    }

    public static String getRandomNum(int length){
        int randomNum = new Random().nextInt(1000);
        return StringUtils.leftPad(randomNum+"", length, "0");
    }

    public static void main(String[] args) {
        System.out.println(getOrderNum());
    }
}
