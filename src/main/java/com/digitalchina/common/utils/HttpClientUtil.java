package com.digitalchina.common.utils;


import com.digitalchina.common.MD5Utils;
import com.digitalchina.common.ServiceRuntimeException;
import com.digitalchina.sport.order.api.common.Constants;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;


/**
 * Http请求辅助类
 * 本工具类只适合非文件类型的一般请求
 * @author zhang
 *
 */
@SuppressWarnings("deprecation")
public class HttpClientUtil {

	private static Logger logger = Logger.getLogger(HttpClientUtil.class);
	
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 120000;
    private static CloseableHttpClient httpClient = null;
    public static final String DEFAULT_ENCODING="utf-8";
    public static final int DEFAULT_MAX_CONNECTIONS=100;
    public static final String DEFAULT_CONTENT_TYPE="application/json";

    static {
       init();
    }
    
    public static void init(){
    	 connMgr = new PoolingHttpClientConnectionManager();  //设置连接池
         connMgr.setMaxTotal(DEFAULT_MAX_CONNECTIONS);        // 设置连接池大小
         connMgr.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS);

         RequestConfig.Builder configBuilder = RequestConfig.custom();
         configBuilder.setConnectTimeout(MAX_TIMEOUT);   // 设置连接超时
         configBuilder.setSocketTimeout(MAX_TIMEOUT);    // 设置读取超时
         configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);  // 设置从连接池获取连接实例的超时
         configBuilder.setStaleConnectionCheckEnabled(true);      // 在提交请求之前 测试连接是否可用
         requestConfig = configBuilder.build();
    }

    /**
     * 生产HttpClient实例
     * 公开，静态的工厂方法，需要使用时才去创建该单体
     * 
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }
        return httpClient;
    }

    /**
     * POST请求
     * 1. 接收json参数
     * 2. 可传入header参数
     * @param apiUrl
     * @param jsonStr
     * @return
     */
    public static String doPost(String apiUrl, String jsonStr,Map<String,Object> header,String encoding) {
        String result = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setConfig(requestConfig);
        
        if(encoding==null || "".equals(encoding.trim())){
        	encoding=DEFAULT_ENCODING;
        }
        
        /**
         * 1.设置请求体
         */
        StringEntity reqEntity = new StringEntity(jsonStr,encoding);//解决中文乱码问题
        reqEntity.setContentEncoding(encoding);
        
        if(header.get("content-type")==null || "".equals(header.get("content-type"))){
        	reqEntity.setContentType(DEFAULT_CONTENT_TYPE);
        }
        
        httpPost.setEntity(reqEntity);
        

        /**
         * 2. 设置header参数
         */
        httpPost.setHeader("Accept", DEFAULT_CONTENT_TYPE);
        if(header!=null){
            Iterator<String> iter=header.keySet().iterator();
            while(iter.hasNext()){
                String key=iter.next();
                httpPost.setHeader(key,header.get(key)==null?"":header.get(key).toString());
            }
        }

        try {
            
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            
            logger.info(String.format("调用http接口 :%s,返回状态码 :%s", apiUrl,statusCode));
            
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, encoding);
        } catch (IOException e) {
            logger.error("调用接口异常: " + e);
            throw new ServiceRuntimeException(Constants.RTN_MESSAGE_ERROR);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if(httpPost!=null){
            	httpPost.releaseConnection();
            }
        }
        return result;
    }

    /**
     *
     * @param url
     * @param httpConnectionTimeout
     * @param headers
     * @param encoding
     * @return
     */
    public static String doGet(String url, int httpConnectionTimeout, Header[] headers, String encoding) throws  Exception {
        CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();
        HttpGet httpget = null;
        CloseableHttpResponse response = null;
         httpget = new HttpGet(url);
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(httpConnectionTimeout)
                    .setConnectTimeout(httpConnectionTimeout)
                    .build();

            httpget.setConfig(requestConfig);


            //设置http header信息
            if(headers != null && headers.length != 0){
                httpget.setHeaders(headers);
            }

            response = httpClient.execute(httpget);
            
            if(encoding==null || "".equals(encoding.trim())){
            	encoding=DEFAULT_ENCODING;
            }
            String result = EntityUtils.toString(response.getEntity(), encoding);
            response.close();
            return result;

    }
    /**
     * 调用接口获取所有商户的信息
     * @return
     */
    public static List<Map<String,Object>> getListResultByURLAndKey(String url,String interfaceName,String resultKey) {
        List<Map<String,Object>> resultList = null;
        String result = null;
        try {
            result = HttpClientUtil.doGet(url, 30000, null, null);
            Gson gson = new Gson();
            Map<String,Object> gsonMap =  gson.fromJson(result,Map.class);
            if(null != gsonMap && gsonMap.containsKey("code")) {
                if(Constants.RTN_CODE_SUCCESS.equals((String)gsonMap.get("code"))) {
                    Map<String,Object> resultMap = (Map<String,Object>)gsonMap.get("result");
                    resultList = (List<Map<String, Object>>) resultMap.get(resultKey);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("=========调用远程"+interfaceName+"接口时发生错误===========",e);
        }
        return resultList != null? resultList:new ArrayList<Map<String,Object>>();
    }

    /**
     * 返回类型map
     * @return
     */
    public static Map<String,Object> getMapResultByURLAndKey(String url, String interfaceName) {
        Map<String,Object> resultMap = new HashMap<String, Object>();
        String result = null;
        try {
            result = HttpClientUtil.doGet(url, 30000, null, null);
            Gson gson = new Gson();
            Map<String,Object> gsonMap =  gson.fromJson(result,Map.class);
            if(null != gsonMap && gsonMap.containsKey("code")) {
                if(Constants.RTN_CODE_SUCCESS.equals((String)gsonMap.get("code"))) {
                    resultMap = (Map<String,Object>)gsonMap.get("result");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("=========调用远程"+interfaceName+"接口时发生错误===========",e);
        }
        //System.out.print("resultMap="+resultMap);
        return resultMap;
    }
    /**
     * 模拟请求
     *
     * @param url       资源地址
     * @param map   参数列表
     * @param encoding  编码
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String send(String url, Map<String,String> map,String encoding) throws ParseException, IOException{
        String body = "";

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        List<BasicNameValuePair> nvps = new ArrayList<>();
        if(map!=null){
            for (Map.Entry<String,String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        //设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

        System.out.println("请求地址："+url);
        System.out.println("请求参数："+nvps.toString());

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }
    public static String doPost(String url,Map<String,String> map,String charset){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
            }
            if(list.size() > 0){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
    /**
     * 返回类型map
     * @return
     */
    public static Map<String,Object> getRetMapByPost(String url, String interfaceName,Map<String,String> map) {

        Map<String,Object> resultMap = new HashMap<String, Object>();
        String result = null;
        try {
            result = HttpClientUtil.doPost(url, map, "utf-8");
            Gson gson = new Gson();
            Map<String,Object> gsonMap =  gson.fromJson(result,Map.class);
            if(null != gsonMap && gsonMap.containsKey("status")) {
                String status = gsonMap.get("status").toString();

                status = status.substring(0, status.length() - 2);
                if(Constants.RTN_SIGN_SUCCESS.equals(status)) {
                    resultMap = gsonMap;
                    logger.info("=========调用远程"+interfaceName+"接口时成功===========");
                }else if(Constants.RTN_SIGN_FAIL.equals(status)) {
                    resultMap = gsonMap;
                    logger.error("=========调用远程"+interfaceName+"接口时发生错误,sign校验失败===========");
                }else if(Constants.RTN_SIGN_NULL.equals(status)) {
                    resultMap = gsonMap;
                    logger.error("=========调用远程"+interfaceName+"接口时发生错误,必填项有空值===========");
                }else if(Constants.RTN_SIGN_OTHER.equals(status)) {
                    resultMap = gsonMap;
                    logger.error("=========调用远程"+interfaceName+"接口时发生错误,其他错误===========");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("=========调用远程"+interfaceName+"接口时发生错误===========",e);
        }
        //System.out.print("resultMap="+resultMap);
        return resultMap;
    }

    public static void main(String[] args){
        String url = "http://suzhou.24parking.com.cn/index.php/Mobile/Order/getParkingInfo";
        Map<String,String> reMap = new HashMap<String, String>();//作为返回的参数
        String userId="1b5002f407634ad7b23de84cd4181843";
        String carNumber = "苏E12345";
        reMap.put("uid",userId);
        reMap.put("carno",carNumber);
        String number = "004";
        reMap.put("number",number);
        reMap.put("title","篮球馆");
        reMap.put("category","普通票");
        reMap.put("ordernumber","10000220170329173937696749927");
        reMap.put("type","0");
        String sign = MD5Utils.getPwd(userId+number+carNumber);
        reMap.put("sign",sign);
        String mark = "1A 2017-03-29 17:00:00-18:00:00,18:15:00-19:15:00";
        reMap.put("mark",mark);

        System.out.println(getRetMapByPost(url,"getParkingInfo",reMap));
    }

}
