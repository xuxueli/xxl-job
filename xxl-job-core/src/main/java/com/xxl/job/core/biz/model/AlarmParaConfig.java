package com.xxl.job.core.biz.model;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AlarmParaConfig {

    private String env_tag="";
    private DingDingPara dingDingPara;
    private EmailPara emailPara;
    private String rootUrl;

    public String getEnv_tag(){
        return env_tag;
    }
    public void setEnv_tag(String env_tag) {
        this.env_tag=env_tag;
    }

    public DingDingPara getDingDingPara(){
        return this.dingDingPara;
    }

    public void setDingDingPara(DingDingPara dingDingPara) {
        this.dingDingPara = dingDingPara;
    }

    public EmailPara getEmailPara(){
        return this.emailPara;
    }

    public void setEmailPara(EmailPara emailPara){
        this.emailPara=emailPara;
    }

    public void setRootUrl(String rootUrl){
        this.rootUrl=rootUrl;
    }
    public String getRootUrl(){
        return this.rootUrl;
    }

    public AlarmParaConfig(){

    }
    public AlarmParaConfig(String string){
        try{
            //是JSON,解析
            AlarmParaConfig alarmParaConfig=JSONObject.parseObject(string,AlarmParaConfig.class);
            this.setDingDingPara(alarmParaConfig.getDingDingPara());
            this.setEmailPara(alarmParaConfig.getEmailPara());
            this.setEnv_tag(alarmParaConfig.getEnv_tag());
            this.setRootUrl(alarmParaConfig.getRootUrl());
        }
        catch (Exception ignored){

        }
    }

    public static class DingDingPara{
        private List<String> access_token=new ArrayList<>();
        private List<String> atList=new ArrayList<>();

        public List<String> getAccess_token(){
            return access_token;
        }
        public void setAccess_token(List<String> access_token){
            this.access_token=access_token;
        }

        public void setAtList(List<String> atList){
            this.atList=atList;
        }
        public List<String> getAtList(){
            return this.atList;
        }
        public String getAtListString(String splitChar){
            if(null==splitChar){
                splitChar=" ";
            }
            StringBuilder sb=new StringBuilder();
            for (String at:this.atList){
                sb.append("@").append(at).append(splitChar);
            }
            return sb.toString();
        }

        public DingDingPara(){

        }
        public DingDingPara(String string){
            DingDingPara temp = JSONObject.parseObject(string,DingDingPara.class);
            this.access_token=temp.getAccess_token();
            this.atList=temp.getAtList();
        }
    }

    public static class EmailPara{
        private List<String> emails=new ArrayList<>();

        public void setEmails(List<String> emails) {
            this.emails = emails;
        }
        public List<String> getEmails(){
            return this.emails;
        }
        public EmailPara(){

        }
        public EmailPara(String string){
            EmailPara temp=JSONObject.parseObject(string,EmailPara.class);
            this.emails=temp.getEmails();
        }
    }
}
