package com.digitalchina.common.result;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by asus on 2017/1/5.
 */
public class Result {
    private static SerializeConfig config = new SerializeConfig();
    private String status;
    private String message;
    private Object result;
    private Integer code;

    public Result(int code, String status, String message, Object result) {
        this.status = status;
        this.message = message;
        this.result = result;
        this.code = Integer.valueOf(code);
    }

    public Result(int code, String status, String message) {
        this.status = status;
        this.message = message;
        this.code = Integer.valueOf(code);
    }

    public Result() {
    }

    public static String error(Integer code, Object result) {
        return (new Result(code.intValue(), Result.Status.ERROR.name(), (String)null, result)).toString();
    }

    public static String error(Integer code) {
        return (new Result(code.intValue(), Result.Status.ERROR.name(), (String)null, (Object)null)).toString();
    }

    public static String error(String errMsg, Object result) {
        return (new Result(1000, Result.Status.ERROR.name(), errMsg, result)).toString();
    }

    public static String ok(Integer code, Object result) {
        return (new Result(code.intValue(), Result.Status.OK.name(), (String)null, result)).toString();
    }

    public static String ok(Integer code) {
        return (new Result(code.intValue(), Result.Status.OK.name(), (String)null, (Object)null)).toString();
    }

    public static String ok(String msg, Object result) {
        return (new Result(0, Result.Status.OK.name(), msg, result)).toString();
    }

    public static String ok(Object result) {
        return (new Result(0, Result.Status.OK.name(), "success", result)).toString();
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String toString() {
        return JSONObject.toJSONString(this, config, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty});
    }

    private static enum Status {
        OK,
        ERROR;

        private Status() {
        }
    }
}
