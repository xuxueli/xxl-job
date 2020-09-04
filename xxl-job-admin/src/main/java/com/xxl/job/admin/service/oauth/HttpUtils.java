package com.xxl.job.admin.service.oauth;

import com.xxl.job.core.util.GsonTool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.lang.StringBuilder;
import java.net.URLEncoder; 
/**
 * http工具类
 *
 * @author: wu.xiaoyuan
 * @date: 2018-08-04
 **/
public class HttpUtils {

  /**
   *
   * @param urlPath
   * @param params
   * @return
   */
  public static String doPost(String urlPath, Map<String, String> params, Map<String, String> headers) {
    URL url;
    try {
      url = new URL(urlPath);
      StringBuilder postData = new StringBuilder();
      for (Map.Entry<String,String> param : params.entrySet()) {
        if (postData.length() != 0) {
            postData.append('&');
        }
        postData.append(URLEncoder.encode(param.getKey()));
        postData.append('=');
        postData.append(URLEncoder.encode(param.getValue()));
    }
      URLConnection urlConnection = url.openConnection();
      
      //urlConnection.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
      urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      for (Map.Entry<String,String> header : headers.entrySet()) {
        urlConnection.setRequestProperty(header.getKey(), header.getValue());
      }
      urlConnection.setDoOutput(true);
      HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
      // 建立连接
      httpUrlConnection.connect();
      OutputStream outputStream = httpUrlConnection.getOutputStream();
      //  设置请求体
      //String requestParam = GsonTool.toJson(params);
      outputStream.write(postData.toString().getBytes("UTF-8"));
      outputStream.flush();
      outputStream.close();
      // 读取响应状态，建立输入流，获取响应内容
      int resultCode = httpUrlConnection.getResponseCode();
      if (HttpURLConnection.HTTP_OK == resultCode) {
        // System.out.println("HTTP_OK");
        InputStream in = urlConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuffer temp = new StringBuffer();
        String line = bufferedReader.readLine();
        while (line != null) {
          temp.append(line);
          line = bufferedReader.readLine();
        }
        bufferedReader.close();
        String result = new String(temp.toString().getBytes(), "utf-8");
        if (result != null) {
          System.out.println("**************" + result);
          return result;
        }
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  /**
   *
   * @param urlPath
   * @param params
   * @return
   */
  public static String doGet(String urlPath, Map<String, String> headers) {
    URL url;
    try {
      url = new URL(urlPath);
      URLConnection urlConnection = url.openConnection();
      urlConnection.setRequestProperty("Content-Type", "text/html;charset=UTF-8");      
      for (Map.Entry<String,String> header : headers.entrySet()) {
        urlConnection.setRequestProperty(header.getKey(), header.getValue());
      }
      urlConnection.setDoOutput(true);
      HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
      // 建立连接
      httpUrlConnection.connect();
      OutputStream outputStream = httpUrlConnection.getOutputStream();
      //  设置请求体
      //String requestParam = GsonTool.toJson(params);
      outputStream.flush();
      outputStream.close();
      // 读取响应状态，建立输入流，获取响应内容
      int resultCode = httpUrlConnection.getResponseCode();
      if (HttpURLConnection.HTTP_OK == resultCode) {
        // System.out.println("HTTP_OK");
        InputStream in = urlConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuffer temp = new StringBuffer();
        String line = bufferedReader.readLine();
        while (line != null) {
          temp.append(line);
          line = bufferedReader.readLine();
        }
        bufferedReader.close();
        String result = new String(temp.toString().getBytes(), "utf-8");
        if (result != null) {
          //System.out.println("**************" + result);
          return result;
        }
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}