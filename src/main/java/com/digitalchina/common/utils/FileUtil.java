package com.digitalchina.common.utils;
import com.alipay.api.internal.util.StringUtils;

import java.io.File;
/**
 * Created by Cickey on 2017/5/27.
 */
public class FileUtil {
    /**
     * user: Rex
     * date: 2016年12月29日  上午12:25:04
     * @param dir
     * void
     * TODO 判断路径是否存在，如果不存在则创建
     */
    public static void mkdirs(String dir){
        if(StringUtils.isEmpty(dir)){
            return;
        }

        File file = new File(dir);
        if(file.isDirectory()){
            return;
        } else {
            file.mkdirs();
        }
    }
}