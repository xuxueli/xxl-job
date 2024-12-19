package com.xxl.job.admin.core.conf;

import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * @author Ice2Faith
 * @date 2024/12/13 14:09
 */
@Configuration
public class XxlJobMailConfig implements ApplicationRunner {
    public static final String FILE_NAME="email.properties";
    public static final String MAIL_CONFIG_FILE=SecurityContext.STORE_PATH+"/"+FILE_NAME;
    public static final Path CONFIG_PATH=Paths.get(MAIL_CONFIG_FILE);

    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private JavaMailSenderImpl javaMailSender;


    @Autowired
    private XxlJobAdminConfig xxlJobAdminConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        loadConfig();

        storeConfig();

        startListening();
    }

    public void startListening() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            CONFIG_PATH.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            System.out.println("listening email config file change ...");

            while (true) {
                WatchKey key;
                key = watchService.take(); // 阻塞直到接收到事件
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    if(kind != StandardWatchEventKinds.ENTRY_MODIFY){
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    if (fileName.endsWith(FILE_NAME)) {
                        System.out.println("email config file changed, reload ...");
                        try {
                            loadConfig();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
                key.reset(); // 重置键以继续接收事件
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() throws Exception {
        Properties properties=new Properties();
        File file = getConfigFile();
        if(!file.exists()){
            return;
        }
        FileInputStream fis=new FileInputStream(file);
        properties.load(fis);
        fis.close();
        loadConfig(properties);
    }

    public void loadConfig(Properties properties){
        String host = properties.getProperty("spring.mail.host");
        if(host==null || host.isEmpty()){
            return;
        }
        mailProperties.setHost(host);
        int port=25;
        try{
            port=Integer.parseInt(properties.getProperty("spring.mail.port","25"));
        }catch(Exception e){

        }
        mailProperties.setPort(port);
        String username = properties.getProperty("spring.mail.username");
        if(username!=null && !username.isEmpty()) {
            mailProperties.setUsername(username);
        }
        String password = properties.getProperty("spring.mail.password");
        if(password!=null && !password.isEmpty()) {
            mailProperties.setPassword(password);
        }
        String from = properties.getProperty("spring.mail.from");
        if(from!=null && !from.isEmpty()){
            xxlJobAdminConfig.setEmailFrom(from);
        }

        Enumeration<?> enumeration = properties.propertyNames();
        while(enumeration.hasMoreElements()){
            String key = (String)enumeration.nextElement();
            if(key.startsWith("spring.mail.properties.")){
                String name=key.substring("spring.mail.properties.".length());
                if(!name.isEmpty()) {
                    String prop = (String) properties.get(key);
                    mailProperties.getProperties().put(name, prop);
                }
            }
        }

        applyProperties(mailProperties,javaMailSender);
    }

    public Map<String,String> getConfig(){
        Map<String,String> map=new LinkedHashMap<>();
        map.put("spring.mail.host",mailProperties.getHost());
        map.put("spring.mail.port", String.valueOf(mailProperties.getPort()));
        map.put("spring.mail.username",mailProperties.getUsername());
        map.put("spring.mail.from",xxlJobAdminConfig.getEmailFrom());
        map.put("spring.mail.password",mailProperties.getPassword());
        for (Map.Entry<String, String> entry : mailProperties.getProperties().entrySet()) {
            if(entry.getValue()==null){
                continue;
            }
            map.put("spring.mail.properties."+entry.getKey(),String.valueOf(entry.getValue()));
        }

        return map;
    }

    public void storeConfig() throws Exception {
        Map<String, String> map = getConfig();
        Properties properties=new Properties();
        properties.putAll(map);

        File file = getConfigFile();
        FileOutputStream fos = new FileOutputStream(file);
        properties.store(fos,null);
        fos.close();
    }

    public File getConfigFile(){
        File file = new File(MAIL_CONFIG_FILE);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        return file;
    }

    public void applyProperties(MailProperties properties, JavaMailSenderImpl sender) {
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) {
            sender.setPort(properties.getPort());
        }

        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        if (properties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(properties.getDefaultEncoding().name());
        }

        if (!properties.getProperties().isEmpty()) {
            sender.setJavaMailProperties(this.asProperties(properties.getProperties()));
        }

    }

    public Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    public void sendTest() throws Exception {
        String personal = I18nUtil.getString("admin_name_full");
        MimeMessage mimeMessage = XxlJobAdminConfig.getAdminConfig().getMailSender().createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(XxlJobAdminConfig.getAdminConfig().getEmailFrom(), personal);
        helper.setTo("3071381796@qq.com");
        helper.setSubject("alarm test");
        helper.setText("alarm test mail", true);

        XxlJobAdminConfig.getAdminConfig().getMailSender().send(mimeMessage);
    }
}
