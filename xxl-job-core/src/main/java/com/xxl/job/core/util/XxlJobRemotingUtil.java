package com.xxl.job.core.util;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.ssl.SslConfig;
import com.xxl.job.core.ssl.SslContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author xuxueli 2018-11-25 00:55:31
 */
public class XxlJobRemotingUtil {
    private static Logger logger = LoggerFactory.getLogger(XxlJobRemotingUtil.class);
    public static final String XXL_JOB_ACCESS_TOKEN = "XXL-JOB-ACCESS-TOKEN";


    private static SSLContext custom_ssl_context = null;
    private static boolean hostnameVerificationDisabled = false;

    public static void init(SslConfig ssl) throws SSLException {
        custom_ssl_context = SslContextUtils.ssl(ssl, false);
        hostnameVerificationDisabled = ssl.isHostnameVerificationDisabled();
    }


    // trust-https start
    private static void trustAllHosts(HttpsURLConnection connection) {
        if (custom_ssl_context != null) {
            logger.debug("use ssl with custom SSLContext");
            connection.setSSLSocketFactory(custom_ssl_context.getSocketFactory());
            // 使用默认的主机名验证策略.
            if (hostnameVerificationDisabled) {
                logger.debug("hostnameVerificationDisabled");
                connection.setHostnameVerifier((hostname, session) -> true);
            }
        } else {
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                SSLSocketFactory newFactory = sc.getSocketFactory();

                connection.setSSLSocketFactory(newFactory);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
    }

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }};
    // trust-https end


    /**
     * post
     *
     * @param url
     * @param accessToken
     * @param timeout
     * @param requestObj
     * @param returnTargClassOfT
     * @return
     */
    public static ReturnT postBody(String url, String accessToken, int timeout, Object requestObj, Class returnTargClassOfT) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            // connection
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();

            // trust-https
            boolean useHttps = url.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                trustAllHosts(https);
            }

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

            if (accessToken != null && accessToken.trim().length() > 0) {
                connection.setRequestProperty(XXL_JOB_ACCESS_TOKEN, accessToken);
            }

            // do connection
            connection.connect();

            // write requestBody
            if (requestObj != null) {
                String requestBody = GsonTool.toJson(requestObj);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(requestBody.getBytes("UTF-8"));
                dataOutputStream.flush();
                dataOutputStream.close();
            }

            /*byte[] requestBodyBytes = requestBody.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));
            OutputStream outwritestream = connection.getOutputStream();
            outwritestream.write(requestBodyBytes);
            outwritestream.flush();
            outwritestream.close();*/

            // valid StatusCode
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "xxl-job remoting fail, StatusCode(" + statusCode + ") invalid. for url : " + url);
            }

            // result
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            String resultJson = result.toString();

            // parse returnT
            try {
                ReturnT returnT = GsonTool.fromJson(resultJson, ReturnT.class, returnTargClassOfT);
                return returnT;
            } catch (Exception e) {
                logger.error("xxl-job remoting (url=" + url + ") response content invalid(" + resultJson + ").", e);
                return new ReturnT<String>(ReturnT.FAIL_CODE, "xxl-job remoting (url=" + url + ") response content invalid(" + resultJson + ").");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<String>(ReturnT.FAIL_CODE, "xxl-job remoting error(" + e.getMessage() + "), for url : " + url);
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
