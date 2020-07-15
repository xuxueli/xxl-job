package com.xxl.job.admin.core.id;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xxl.job.admin.core.id.service.MachineService;
import com.xxl.job.admin.core.model.XxlJobMachine;
import com.xxl.job.admin.core.util.MachineUtils;

@Component
@EnableScheduling
public class HeartBeat {

	@Value("${server.port}")
	private String serverPort;
	
	@Autowired
	private MachineService machineService;

	@Scheduled(fixedDelay = 10000)
	public void checkMachineSurvive() {
		String machineIpStr = MachineUtils.getIPAndPort(serverPort);
		XxlJobMachine xxlJobMachine = machineService.selectByMachineIp(machineIpStr);
		if (xxlJobMachine != null) {
			machineService.update(machineIpStr, new Date());
		}
	}
}