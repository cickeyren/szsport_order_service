package com.digitalchina.sport.order.api.common.config;

import java.util.Map;

/**
 * Created by wanggw on 2017/3/15
 * <p/>
 * Map工具类，用于对Map里面的所有的value去除前后空格
 */
public class MapTrimUtil {


    /**
     * 取出Map里面所有的value前后空格
     *
     * @param params 待处理Map集合
     */
    public static void MapValueTrim(Map<String, Object> params) {

        if (params != null) {
            Object[] keys = params.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                String key_value = params.get(keys[i].toString()) == null ? "" : params.get(keys[i].toString()).toString().trim();
                params.put(keys[i].toString(), key_value);
            }
        }
    }
}
