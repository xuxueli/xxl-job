package com.xxl.job.admin.security;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm3.Sm3;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Ice2Faith
 * @date 2024/5/24 23:07
 * @desc
 */
@Component
public class SecurityContext implements InitializingBean  {
    public static final String STORE_PATH="../xxl-job-meta";
    private static SecurityContext instance;

    private PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public static final String LAST_KEY_PATH=SecurityContext.STORE_PATH+"/last.pk";
    private volatile Keypair lastKeyPair;
    private volatile String lastPublicKeySm3;

    public static final String CURR_KEY_PATH=SecurityContext.STORE_PATH+"/curr.pk";
    private volatile Keypair currKeyPair;
    private volatile String currPublicKeySm3;

    {
        currKeyPair = loadStoreKeypair(CURR_KEY_PATH, Sm2.generateKeyPairHex());
        currPublicKeySm3= Sm3.sm3(currKeyPair.getPublicKey());
        saveStoreKeypair(CURR_KEY_PATH,currKeyPair);

        lastKeyPair = loadStoreKeypair(LAST_KEY_PATH, currKeyPair);
        lastPublicKeySm3= Sm3.sm3(lastKeyPair.getPublicKey());
        saveStoreKeypair(LAST_KEY_PATH,lastKeyPair);
    }


    private ScheduledExecutorService pool= Executors.newSingleThreadScheduledExecutor();
    {
        pool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                refreshKeypair();
            }
        },0,30, TimeUnit.MINUTES);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SecurityContext.instance=this;
    }

    public static SecurityContext getInstance(){
        return instance;
    }

    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    public boolean matchPassword(String password,String encoded){
        return passwordEncoder.matches(password,encoded);
    }

    public synchronized Keypair refreshKeypair(){
        lastKeyPair = currKeyPair;
        lastPublicKeySm3=currPublicKeySm3;
        currKeyPair = Sm2.generateKeyPairHex();
        currPublicKeySm3=Sm3.sm3(currKeyPair.getPublicKey());
        saveStoreKeypair(CURR_KEY_PATH,currKeyPair);
        saveStoreKeypair(LAST_KEY_PATH,lastKeyPair);
        return currKeyPair;
    }

    public synchronized Keypair currentKeypair(){
        return currKeyPair;
    }

    public synchronized Keypair findKeypair(String sign){
        if(Objects.equals(sign,currPublicKeySm3)){
            return currKeyPair;
        }
        if(Objects.equals(sign,lastPublicKeySm3)){
            return lastKeyPair;
        }
        return null;
    }

    public static Keypair loadStoreKeypair(String storeFileName,Keypair defVal){

        File file = new File(storeFileName);
        if(file.exists()){
            Keypair pair=new Keypair();
            try(BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"))){
                pair.setPublicKey(reader.readLine().trim());
                pair.setPrivateKey(reader.readLine().trim());
            }catch (Exception e){

            }
            boolean valid=false;
            try{
                String testMsg="123456";
                String enc = Sm2.doEncrypt(testMsg, pair.getPublicKey());
                String dec = Sm2.doDecrypt(enc, pair.getPrivateKey());
                valid=testMsg.equals(dec);
            }catch(Exception e){

            }
            if(!valid){
                return defVal;
            }
            return pair;
        }
        return defVal;
    }

    public static void saveStoreKeypair(String storeFileName,Keypair pair){
        File file = new File(storeFileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try(BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"))){
            writer.write(pair.getPublicKey());
            writer.newLine();
            writer.write(pair.getPrivateKey());
            writer.newLine();
        }catch (Exception e){

        }
    }

}
