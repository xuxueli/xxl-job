package com.xxl.job.core.util;

import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * http util to send data
 * @author xuxueli
 * @version  2015-11-28 15:30:59
 */
public class XxlJobNetCommUtil {
	private static Logger logger = LoggerFactory.getLogger(XxlJobNetCommUtil.class);

    // hex param key
    public static final String HEX = "hex";


    /**
     * format object to hex-json
     * @param obj
     * @return result
     */
    public static String formatObj2HexJson(Object obj){
    	// obj to json
        String json = JacksonUtil.writeValueAsString(obj);
		int len = ByteHexConverter.getByteLen(json);

		// json to byte[]
		ByteWriteFactory byteWriteFactory = new ByteWriteFactory(4 + len);
		byteWriteFactory.writeInt(len);
		byteWriteFactory.writeString(json, len);
		byte[] bytes = byteWriteFactory.getBytes();

		// byte to hex
        String hex = ByteHexConverter.byte2hex(bytes);
        return hex;
    }

    /**
     * parse hex-json to object
     * @param hex
     * @param clazz
     * @return result
     */
    public static <T> T parseHexJson2Obj(String hex, Class<T> clazz){
    	// hex to byte[]
    	byte[] bytes = ByteHexConverter.hex2Byte(hex);

		// byte[] to json
		ByteReadFactory byteReadFactory = new ByteReadFactory(bytes);
		String json = byteReadFactory.readString(byteReadFactory.readInt());

		// json to obj
        T obj = JacksonUtil.readValue(json, clazz);
        return obj;
    }

    public static void main(String[] args) {
		RequestModel requestModel = new RequestModel();
		requestModel.setJobGroup("group");

		String hex = formatObj2HexJson(requestModel);
		System.out.println(hex);
        System.out.println(parseHexJson2Obj(hex, RequestModel.class));
    }

	/**
	 * http post request
	 * @param reqURL
	 */
	public static ResponseModel postHex(String reqURL, RequestModel requestModel){

		// parse RequestModel to hex-json
		String requestHex = XxlJobNetCommUtil.formatObj2HexJson(requestModel);

        // msg
		String failMsg = null;
		
		// do post
		HttpPost httpPost = null;
		CloseableHttpClient httpClient = null;
		try{
			httpPost = new HttpPost(reqURL);
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			formParams.add(new BasicNameValuePair(XxlJobNetCommUtil.HEX, requestHex));
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));


			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
			httpPost.setConfig(requestConfig);
			
			//httpClient = HttpClients.createDefault();	// default retry 3 times
			httpClient = HttpClients.custom().disableAutomaticRetries().build();

			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200 && null != entity) {
                String responseHex = EntityUtils.toString(entity, "UTF-8");
				logger.info("xxl-job, net comm success, requestHex:{}, responseHex:{}", requestHex, responseHex);
				EntityUtils.consume(entity);

                // i do not know why
                //responseHex = responseHex.replace("\n", "");
                //responseHex = responseHex.replace("\r", "");

				if (responseHex!=null) {
					responseHex = responseHex.trim();
				}

                // parse hex-json to ResponseModel
                ResponseModel responseModel = XxlJobNetCommUtil.parseHexJson2Obj(responseHex, ResponseModel.class);

                if (responseModel!=null) {
                    return responseModel;
                }
			} else {
				failMsg = "http statusCode error, statusCode:" + response.getStatusLine().getStatusCode();
			}
		} catch (Exception e) {
            logger.info("", e);
			/*StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			callback.setMsg(out.toString());*/
			failMsg = e.getMessage();
		} finally{
			if (httpPost!=null) {
				httpPost.releaseConnection();
			}
			if (httpClient!=null) {
				try {
					httpClient.close();
				} catch (IOException e) {
                    logger.info("", e);
				}
			}
		}

		// other, default fail
		ResponseModel callback = new ResponseModel();
		callback.setStatus(ResponseModel.FAIL);
		callback.setMsg(failMsg);
		return callback;
	}
	
	/**
	 * parse address ip:port to url http://.../ 
	 * @param address
	 * @return result
	 */
	public static String addressToUrl(String address){
		return "http://" + address + "/";
	}
	
}
