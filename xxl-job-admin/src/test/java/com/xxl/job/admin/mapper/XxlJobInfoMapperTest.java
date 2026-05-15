package com.xxl.job.admin.mapper;

import com.xxl.job.admin.constant.TriggerStatus;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.scheduler.misfire.MisfireStrategyEnum;
import com.xxl.job.admin.scheduler.type.ScheduleTypeEnum;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.DateTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobInfoMapperTest {
	private static Logger logger = LoggerFactory.getLogger(XxlJobInfoMapperTest.class);

	@Resource
	private JobInfoService xxlJobInfoService;

	@Test
	public void pageList(){
		// Service uses pageList with PageModel return type
		// For testing getJobsByGroupId functionality
		List<XxlJobInfo> list2 = xxlJobInfoService.getJobsByGroupId(1);
		logger.info("", list2);
	}

	@Test
	public void save_load(){
		XxlJobInfo info = new XxlJobInfo();
		info.setJobGroup(1);
		info.setJobDesc("desc");
		info.setAuthor("admin");
		info.setAlarmEmail("test@test.com");
		info.setScheduleType(ScheduleTypeEnum.CRON.name());
		info.setScheduleConf("0 0 0 * * ? *");
		info.setMisfireStrategy(MisfireStrategyEnum.DO_NOTHING.name());
		info.setExecutorRouteStrategy("FIRST");
		info.setExecutorHandler("demoJobHandler");
		info.setExecutorParam("");
		info.setExecutorBlockStrategy("SERIAL_EXECUTION");
		info.setGlueType("BEAN");
		info.setGlueRemark("test");
		info.setChildJobId("");

		info.setAddTime(new Date());
		info.setUpdateTime(new Date());

		// Service add method requires userName and groupPermissionCheck
		int count = xxlJobInfoService.add(info, "admin", groupId -> true);

		XxlJobInfo info2 = xxlJobInfoService.getJobInfoById(info.getId());
		if (info2 != null) {
			info2.setJobDesc("desc2");
			info2.setAuthor("admin2");
			info2.setUpdateTime(new Date());
			int item2 = xxlJobInfoService.update(info2, "admin", groupId -> true);
			xxlJobInfoService.remove(List.of(info.getId()), "admin", groupId -> true);
		}

		List<XxlJobInfo> list2 = xxlJobInfoService.getJobsByGroupId(1);
	}

	@Test
	public void scheduleBatchUpdateTest(){

		// Get jobs to update schedule info
		List<XxlJobInfo> list2 = xxlJobInfoService.scheduleJobQuery(DateTool.addHours(new Date(), 1).getTime(), 20);
		int batchSize = 5;

		// update
		list2 = list2.stream().filter(item -> (item.getId()>=4 && item.getId()<=14)).toList();
		list2.forEach(item -> {
			item.setTriggerLastTime(DateTool.addHours(new Date(), -1).getTime());
			item.setTriggerNextTime(DateTool.addHours(new Date(), 1).getTime());
			if (item.getId() == 5) {
				item.setTriggerStatus(TriggerStatus.STOPPED.getValue());
			}
		});

		// batch update
		List<List<XxlJobInfo>> scheduleListBatches = CollectionTool.split(list2, batchSize);
		for (List<XxlJobInfo> scheduleListBatch : scheduleListBatches) {
			int totalAffected = xxlJobInfoService.scheduleBatchUpdate(scheduleListBatch);
			logger.info("scheduleBatchUpdate records:" + totalAffected);
		}
	}

}