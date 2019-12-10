package com.xxl.job.core.util;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.registry.client.util.json.BasicJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author xuxueli 2018-11-25 00:55:31
 */
public class XxlJobRemotingUtil {
    private static Logger logger = LoggerFactory.getLogger(XxlJobRemotingUtil.class);
    public static String XXL_RPC_ACCESS_TOKEN = "XXL-RPC-ACCESS-TOKEN";

    /**
     * post
     *
     * @param url
     * @param accessToken
     * @param requestObj
     * @return
     */
    public static ReturnT<String> postBody(String url, String accessToken, Object requestObj, int timeout) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            // connection
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();

            // connection setting
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout * 1000);
            connection.setConnectTimeout(3 * 1000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");

            if(accessToken!=null && accessToken.trim().length()>0){
                connection.setRequestProperty(XXL_RPC_ACCESS_TOKEN, accessToken);
            }

            // do connection
            connection.connect();

            // write requestBody
            String requestBody = BasicJson.toJson(requestObj);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(requestBody.getBytes("UTF-8"));
            dataOutputStream.flush();
            dataOutputStream.close();

            /*byte[] requestBodyBytes = requestBody.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));
            OutputStream outwritestream = connection.getOutputStream();
            outwritestream.write(requestBodyBytes);
            outwritestream.flush();
            outwritestream.close();*/

            // valid StatusCode
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "xxl-rpc remoting fail, StatusCode("+ statusCode +") invalid. for url : " + url);
            }

            // result
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            String resultJson = result.toString();

            // parse returnT
            try {
                Map<String, Object> resultMap = BasicJson.parseMap(resultJson);

                ReturnT<String> returnT = new ReturnT<String>();
                if (resultMap==null) {
                    returnT.setCode(ReturnT.FAIL_CODE);
                    returnT.setMsg("AdminBizClient Remoting call fail.");
                } else {
                    returnT.setCode(Integer.valueOf(String.valueOf(resultMap.get("code"))));
                    returnT.setMsg(String.valueOf(resultMap.get("msg")));
                    returnT.setContent(String.valueOf(resultMap.get("content")));
                }
                return returnT;
            } catch (Exception e) {
                logger.error("xxl-rpc remoting (url="+url+") response content invalid("+ resultJson +").", e);
                return new ReturnT<String>(ReturnT.FAIL_CODE, "xxl-rpc remoting (url="+url+") response content invalid("+ resultJson +").");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<String>(ReturnT.FAIL_CODE, "xxl-rpc remoting error("+ e.getMessage() +"), for url : " + url);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
    }

}
