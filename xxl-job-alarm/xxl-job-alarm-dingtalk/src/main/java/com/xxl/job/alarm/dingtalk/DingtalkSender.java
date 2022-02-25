package com.xxl.job.alarm.dingtalk;

import com.xxl.job.alarm.util.JSONUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Created on 2022/2/22.
 *
 * <a>https://open.dingtalk.com/document/robots/custom-robot-access</a>
 *
 * @author lan
 */
public final class DingtalkSender {

    private static final Logger logger = LoggerFactory.getLogger(DingtalkSender.class);

    private final static String DEFAULT_MSG_TYPE = "text";


    private final String url;
    private final String keyword;
    private final String secret;

    private final String atMobiles;
    private final String atUserIds;
    private final boolean atAll;

    public DingtalkSender(Properties config) {
        url = config.getProperty(DingtalkConstants.DINGTALK_WEBHOOK);
        keyword = config.getProperty(DingtalkConstants.DINGTALK_KEYWORD);
        secret = config.getProperty(DingtalkConstants.DINGTALK_SECRET);
        atMobiles = config.getProperty(DingtalkConstants.DINGTALK_AT_MOBILES);
        atUserIds = config.getProperty(DingtalkConstants.DINGTALK_AT_USERIDS);
        atAll = Boolean.parseBoolean(config.getProperty(DingtalkConstants.DINGTALK_AT_ALL));
    }

    private static HttpPost constructHttpPost(String url, String msg) {
        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(msg, StandardCharsets.UTF_8);
        post.setEntity(entity);
        post.addHeader("Content-Type", "application/json; charset=utf-8");
        return post;
    }


    private boolean checkSendDingTalkSendMsgResult(String result) {
        if (null == result) {
            logger.info("send ding talk msg error,ding talk server resp is null");
            return false;
        }
        DingTalkSendMsgResponse sendMsgResponse = JSONUtils.readValue(result, DingTalkSendMsgResponse.class);
        if (null == sendMsgResponse) {
            logger.info("send ding talk msg error,resp error");
            return false;
        }
        if (sendMsgResponse.errcode == 0) {
            return true;
        }
        logger.info("alert send ding talk msg error : {}", sendMsgResponse.getErrmsg());
        return false;
    }

    public boolean sendMsg(String msg) {
        msg = constructDingtalkMsg(msg);

        HttpPost httpPost = constructHttpPost(org.apache.commons.lang3.StringUtils.isBlank(secret) ? url : generateSignedUrl(), msg);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String resp;
            try {
                HttpEntity entity = response.getEntity();
                resp = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            return checkSendDingTalkSendMsgResult(resp);
        } catch (IOException e) {
            logger.error("Ding Talk send msg :{} failed", msg, e);
            return false;
        }
    }

    /**
     * {
     *     "at": {
     *         "atMobiles":[
     *             "180xxxxxx"
     *         ],
     *         "atUserIds":[
     *             "user123"
     *         ],
     *         "isAtAll": false
     *     },
     *     "text": {
     *         "content":"我就是我, @XXX 是不一样的烟火"
     *     },
     *     "msgtype":"text"
     * }
     * @param content 消息内容
     * @return 钉钉消息格式
     */
    private String constructDingtalkMsg(String content) {
        Map<String, Object> items = new HashMap<>(4);
        items.put("msgtype", DEFAULT_MSG_TYPE);
        setMsgAt(items);
        setMsgContent(items, content);
        return JSONUtils.writeValueAsString(items);
    }

    private void setMsgContent(Map<String, Object> items, String content) {
        Map<String, Object> text = new HashMap<>(1);
        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isBlank(content)) {
                content = keyword;
            } else if (!content.contains(keyword)) {
                content = content + keyword;
            }
        }
        text.put("content", content);
        items.put("text", text);
    }

    private void setMsgAt(Map<String, Object> items) {
        Map<String, Object> at = new HashMap<>();

        String[] atMobileArray = StringUtils.isNotBlank(atMobiles) ? atMobiles.split(",") : new String[0];
        String[] atUserArray = StringUtils.isNotBlank(atUserIds) ? atUserIds.split(",") : new String[0];

        at.put("atMobiles", atMobileArray);
        at.put("atUserIds", atUserArray);
        at.put("isAtAll", atAll);

        items.put("at", at);

    }

    /**
     * generate sign url
     *
     * @return sign url
     */
    private String generateSignedUrl() {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        String sign = org.apache.commons.lang3.StringUtils.EMPTY;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (Exception e) {
            logger.error("generate sign error, message", e);
        }
        return url + "&timestamp=" + timestamp + "&sign=" + sign;
    }

    static final class DingTalkSendMsgResponse {
        private Integer errcode;
        private String errmsg;

        public DingTalkSendMsgResponse() {
        }

        public Integer getErrcode() {
            return this.errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return this.errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof DingTalkSendMsgResponse)) {
                return false;
            }
            final DingTalkSendMsgResponse other = (DingTalkSendMsgResponse) o;
            final Object this$errcode = this.getErrcode();
            final Object other$errcode = other.getErrcode();
            if (!Objects.equals(this$errcode, other$errcode)) {
                return false;
            }
            final Object this$errmsg = this.getErrmsg();
            final Object other$errmsg = other.getErrmsg();
            return Objects.equals(this$errmsg, other$errmsg);
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $errcode = this.getErrcode();
            result = result * PRIME + ($errcode == null ? 43 : $errcode.hashCode());
            final Object $errmsg = this.getErrmsg();
            result = result * PRIME + ($errmsg == null ? 43 : $errmsg.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "DingTalkSender.DingTalkSendMsgResponse(errcode=" + this.getErrcode() + ", errmsg=" + this.getErrmsg() + ")";
        }
    }
}
