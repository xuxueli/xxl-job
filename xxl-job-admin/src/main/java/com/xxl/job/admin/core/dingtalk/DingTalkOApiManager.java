package com.xxl.job.admin.core.dingtalk;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiChatSendRequest;
import com.dingtalk.api.request.OapiCspaceAddRequest;
import com.dingtalk.api.request.OapiCspaceAddToSingleChatRequest;
import com.dingtalk.api.request.OapiCspaceGetCustomSpaceRequest;
import com.dingtalk.api.request.OapiCspaceGrantCustomSpaceRequest;
import com.dingtalk.api.request.OapiFileUploadSingleRequest;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageSendToConversationRequest;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiChatSendResponse;
import com.dingtalk.api.response.OapiCspaceAddResponse;
import com.dingtalk.api.response.OapiCspaceAddToSingleChatResponse;
import com.dingtalk.api.response.OapiCspaceGetCustomSpaceResponse;
import com.dingtalk.api.response.OapiCspaceGrantCustomSpaceResponse;
import com.dingtalk.api.response.OapiFileUploadSingleResponse;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageSendToConversationResponse;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.FileItem;
import com.taobao.api.internal.util.WebUtils;
import com.xxl.job.admin.configuration.AppForDingTalkConf;
import io.micrometer.core.instrument.util.StringUtils;
import java.io.File;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wachoo
 */
@Component
public class DingTalkOApiManager {

    private static Logger logger = LoggerFactory.getLogger(DingTalkOApiManager.class);

    @Autowired
    private AppForDingTalkConf appForDingTalkConf;

    public DingTalkOApiManager() {
    }

    /**
     * 获取token
     */
    public String getTokenRequest() throws Exception {
        DefaultDingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(appForDingTalkConf.getAppKey());
        request.setAppsecret(appForDingTalkConf.getAppSecret());
        request.setHttpMethod("GET");
        OapiGettokenResponse response = client.execute(request);
        return response.getAccessToken();

    }

    /***
     *上传单个文件
     * @param accessToken
     * @param file
     * @return
     * @throws Exception
     */
    public String fileUploadSingleRequest(String accessToken, File file) throws Exception {
        OapiFileUploadSingleRequest request = new OapiFileUploadSingleRequest();
        request.setFileSize(1000L);
        request.setAgentId(appForDingTalkConf.getAgentId());
        DingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/file/upload/single?" + WebUtils
                        .buildQuery(request.getTextParams(), "utf-8"));
        // 必须重新new一个请求
        request = new OapiFileUploadSingleRequest();
        request.setFile(new FileItem(file));
        OapiFileUploadSingleResponse response = client.execute(request, accessToken);
        if (response.isSuccess()) {
            return response.getMediaId();
        }
        return null;
    }

    /***
     * 发送文本
     * @param accessToken
     * @param chatId
     * @return
     * @throws Exception
     */
    public String chatSendText(String accessToken, String chatId, String content) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/send");
        OapiChatSendRequest request = new OapiChatSendRequest();
        request.setChatid(chatId);

        OapiChatSendRequest.Msg msg = new OapiChatSendRequest.Msg();
        msg.setMsgtype("text");
        OapiChatSendRequest.Text text = new OapiChatSendRequest.Text();

        text.setContent(content);
        msg.setText(text);
        request.setMsg(msg);
        OapiChatSendResponse response = client.execute(request, accessToken);

        if (response.isSuccess()) {
            return response.getMessageId();
        }
        return null;
    }


    /**
     * 发送群文本消息
     * @param content
     * @param mobiles
     * @return
     * @throws Exception
     */
    public Boolean groupTextSend(String accessToken, String secret, String content, String mobiles) throws Exception {
        Long timestamp = System.currentTimeMillis();
        String sign = getSecret(timestamp, secret);
        StringBuilder serverUrl = new StringBuilder("https://oapi.dingtalk.com/robot/send");
        serverUrl.append("?access_token=" + accessToken);
        serverUrl.append("&timestamp=" + timestamp);
        serverUrl.append("&sign=" + sign);
        DingTalkClient client = new DefaultDingTalkClient(serverUrl.toString());
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(content);
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(Arrays.asList(mobiles));
        request.setAt(at);
        OapiRobotSendResponse response = client.execute(request);
        return response.isSuccess();
    }


    /**
     *  获得加密签名
     * @param timestamp
     * @param secret
     * @return
     * @throws Exception
     */
    public String getSecret(Long timestamp, String secret) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
    }

    /***
     * 发送活动卡
     * @param accessToken
     * @param mediaId
     * @param chatId
     * @return
     * @throws Exception
     */
    public String chatSendActionCard(String accessToken, String mediaId, String chatId)
            throws Exception {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/send");
        OapiChatSendRequest request = new OapiChatSendRequest();
        request.setChatid(chatId);

        OapiChatSendRequest.Msg msg = new OapiChatSendRequest.Msg();
        msg.setMsgtype("action_card");
        OapiChatSendRequest.ActionCard actionCard = new OapiChatSendRequest.ActionCard();
        actionCard.setTitle("xxx123411111");
        actionCard.setMarkdown("### 测试123111");
        actionCard.setSingleTitle("测试测试");
        actionCard.setSingleUrl("https://www.baidu.com");
        msg.setActionCard(actionCard);

        request.setMsg(msg);
        OapiChatSendResponse response = client.execute(request, accessToken);

        if (response.isSuccess()) {
            return response.getMessageId();
        }
        return null;
    }

    /***
     * 发送文件
     * @param accessToken
     * @param mediaId
     * @param chatId
     * @return
     * @throws Exception
     */
    public String chatSendFile(String accessToken, String mediaId, String chatId) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/send");
        OapiChatSendRequest request = new OapiChatSendRequest();
        request.setChatid(chatId);

        OapiChatSendRequest.Msg msg = new OapiChatSendRequest.Msg();
        OapiChatSendRequest.File file = new OapiChatSendRequest.File();
        file.setMediaId(mediaId);

        msg.setMsgtype("file");
        msg.setFile(file);
        request.setMsg(msg);
        OapiChatSendResponse response = client.execute(request, accessToken);

        if (response.isSuccess()) {
            return response.getMessageId();
        }
        return null;
    }

    /***
     * 发送消息
     * @param accessToken
     * @param mediaId
     * @param chatId
     * @return
     * @throws Exception
     */
    public String chatMsgSendFile(String accessToken, String mediaId, String chatId)
            throws Exception {
        DingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/message/send_to_conversation");
        OapiMessageSendToConversationRequest req = new OapiMessageSendToConversationRequest();
        req.setSender("15531335899935228");
        req.setCid(chatId);

        OapiMessageSendToConversationRequest.File file = new OapiMessageSendToConversationRequest.File();
        file.setMediaId(mediaId);
        OapiMessageSendToConversationRequest.Msg msg = new OapiMessageSendToConversationRequest.Msg();
        msg.setFile(file);
        msg.setMsgtype("file");
        req.setMsg(msg);
        OapiMessageSendToConversationResponse response = client.execute(req, accessToken);

        if (response.isSuccess()) {
            return response.getErrmsg();
        }
        return null;
    }

    /***
     * 机器人群消息-文件链接
     * @param accessToken
     * @param fileUrl
     * @param title
     * @param text
     * @return
     * @throws Exception
     */

    public String robotSendLink(String accessToken, String fileUrl, String title, String text)
            throws Exception {
        String URL = "https://oapi.dingtalk.com/robot/send?access_token=" + accessToken;
        DingTalkClient client = new DefaultDingTalkClient(URL);
        OapiRobotSendRequest request = new OapiRobotSendRequest();

        request.setMsgtype("link");
        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
        link.setMessageUrl(StringUtils.isBlank(fileUrl) ? "https://www.dingtalk.com/" : fileUrl);
        link.setPicUrl("");
        link.setTitle(title);
        link.setText(StringUtils.isBlank(text) ? title : text);
        request.setLink(link);

        OapiRobotSendResponse response = client.execute(request);

        if (response.isSuccess()) {
            return response.getErrmsg();
        }
        return null;
    }

    /***
     * 机器人群消息-文本
     * @param accessToken
     * @param content
     * @param atList
     * @return
     * @throws Exception
     */
    public String robotSendText(String accessToken, String content, List<String> atList)
            throws Exception {
        String URL = "https://oapi.dingtalk.com/robot/send?access_token=" + accessToken;
        DingTalkClient client = new DefaultDingTalkClient(URL);
        OapiRobotSendRequest request = new OapiRobotSendRequest();

        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(content);
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(atList);
        request.setAt(at);

        OapiRobotSendResponse response = client.execute(request);

        if (response.isSuccess()) {
            return response.getErrmsg();
        }
        return null;
    }

    /***
     * 网盘文件-通知
     * @param accessToken
     * @param mediaId
     * @param fileName
     * @param userId
     * @return
     * @throws Exception
     */
    public Boolean cSpaceAddToSingleChat(String accessToken, String mediaId,
            String fileName, String userId)
            throws Exception {
        OapiCspaceAddToSingleChatRequest request = new OapiCspaceAddToSingleChatRequest();
        request.setAgentId(appForDingTalkConf.getAgentId());
        request.setUserid(userId);
        request.setMediaId(mediaId);
        request.setFileName(fileName);
        DingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/cspace/add_to_single_chat?" + WebUtils
                        .buildQuery(request.getTextParams(), "utf-8"));
        OapiCspaceAddToSingleChatResponse response = client.execute(request, accessToken);
        return response.isSuccess();
    }

    /***
     * 网盘文件-添加
     * @param accessToken
     * @param agentId
     * @param code
     * @param mediaId
     * @param spaceId
     * @param folderId
     * @param name
     * @param overwrite
     * @return
     * @throws Exception
     */
    public Boolean cSpaceAdd(String accessToken, String agentId, String code, String mediaId,
            String spaceId, String folderId, String name, Boolean overwrite)
            throws Exception {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/cspace/add");
        OapiCspaceAddRequest request = new OapiCspaceAddRequest();
        request.setAgentId(agentId);
        request.setCode(code);
        request.setMediaId(mediaId);
        request.setSpaceId(spaceId);
        request.setFolderId(folderId);
        request.setName(name);
        request.setOverwrite(overwrite);
        request.setHttpMethod("GET");
        OapiCspaceAddResponse response = client.execute(request, accessToken);
        return response.isSuccess();
    }

    /***
     * 网盘文件-授权
     * @param accessToken
     * @param agentId
     * @param domain
     * @param type
     * @param userId
     * @param path
     * @param duration
     * @return
     * @throws Exception
     */
    public Boolean grantCustomSpace(String accessToken, String agentId, String domain, String type,
            String userId, String path, Long duration)
            throws Exception {
        DingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/cspace/grant_custom_space");
        OapiCspaceGrantCustomSpaceRequest request = new OapiCspaceGrantCustomSpaceRequest();
        request.setAgentId(agentId);
        request.setDomain(domain);
        request.setType(type);
        request.setUserid(userId);
        request.setPath(path);
        request.setDuration(duration);
        request.setHttpMethod("GET");
        OapiCspaceGrantCustomSpaceResponse response = client.execute(request, accessToken);
        return response.isSuccess();
    }

    /***
     * 网盘空间-ID获取
     * @param accessToken
     * @param agentId
     * @param domain
     * @return
     * @throws Exception
     */
    public String getCustomSpace(String accessToken, String agentId, String domain)
            throws Exception {
        DingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/cspace/get_custom_space");
        OapiCspaceGetCustomSpaceRequest request = new OapiCspaceGetCustomSpaceRequest();
        request.setAgentId(agentId);
        request.setDomain(domain);
        request.setHttpMethod("GET");
        OapiCspaceGetCustomSpaceResponse response = client.execute(request, accessToken);
        if (response.isSuccess()) {
            return response.getSpaceid();
        }
        return null;
    }


}
