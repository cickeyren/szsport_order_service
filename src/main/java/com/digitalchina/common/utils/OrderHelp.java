package com.digitalchina.common.utils;

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

    public static void main(String[] args) {
        System.out.println(getOrderNum());
    }
}
