package com.xxl.job.admin.core.id.impl;

import com.xxl.job.admin.core.id.service.MachineService;
import com.xxl.job.admin.core.model.XxlJobMachine;
import com.xxl.job.admin.core.util.MachineUtils;
import com.xxl.job.admin.dao.XxlJobMachineDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class MachineServiceImpl implements MachineService {

    private final Logger logger = LoggerFactory.getLogger(MachineServiceImpl.class);

	@Value("${server.port}")
	private String serverPort;
	
    @Autowired
    private XxlJobMachineDao xxlJobMachineDao;

    @Override
    public void save(XxlJobMachine xxlJobMachine) {
        xxlJobMachineDao.save(xxlJobMachine);
    }

    @Override
    public void update(String machineIp, Date heartLastTime) {
        xxlJobMachineDao.update(machineIp,heartLastTime);
    }

    @Override
    public XxlJobMachine selectByMachineIp(String machineIp) {
        return xxlJobMachineDao.selectByHostIp(machineIp);
    }

    @Override
    public Integer selectMaxMachineId() {
        return xxlJobMachineDao.selectMaxMachineId();
    }

    @Override
    public Integer getInitMachineId() {
    	String ipStr = MachineUtils.getIPAndPort(serverPort);
        XxlJobMachine xxlJobMachine = selectByMachineIp(ipStr);
        Date nowDate = new Date();
        int machineId = -1;
        if(xxlJobMachine != null){
            update(ipStr,nowDate);
            machineId =  xxlJobMachine.getMachineId();
        }else{
            xxlJobMachine = new XxlJobMachine();
            xxlJobMachine.setMachineIp(ipStr);
            xxlJobMachine.setAddTime(nowDate);
            xxlJobMachine.setHeartLastTime(nowDate);
            Random random = new Random();
            boolean flag = false;
            for(int i = 0; i < 100; i++){
                try {
                    Integer value = selectMaxMachineId();
                    machineId = value == null ? 1 : value+1;
                    xxlJobMachine.setMachineId(machineId);
                    save(xxlJobMachine);
                    flag = true;
                    break;
                } catch (DuplicateKeyException e) {
                    try {
                        Thread.sleep(random.nextInt(2000)+1);
                    } catch (InterruptedException interruptedException) {
                        logger.error("sleep error,cause：",interruptedException);
                    }
                    logger.error("retry >>>>>>>>>>>>> ");
                } catch (Exception e){
                    logger.error("save error >>>>>>,system exit,cause：",e);
                }
            }
            if(!flag) {
            	logger.error("多次获取machineId失败，退出程序");
            	System.exit(0);
            }
        }
        return machineId;
    }
}
