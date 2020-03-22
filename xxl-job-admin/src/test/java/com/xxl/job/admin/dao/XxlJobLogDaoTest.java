package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobLogDaoTest {

    @Resource
    private XxlJobLogDao xxlJobLogDao;

    @Test
    public void test(){
        XxlJobLog log = new XxlJobLog();
        log.setJobGroup(1L);
        log.setJobId(1L);
        log.setAlarmStatus(1);

        xxlJobLogDao.save(log);
        XxlJobLog dto = xxlJobLogDao.load(log.getId());

        log.setTriggerTime(new Date());
        log.setTriggerCode(1);
        log.setTriggerMsg("1");
        log.setExecutorAddress("1");
        log.setExecutorHandler("1");
        log.setExecutorParam("1");
        long ret1 = xxlJobLogDao.updateTriggerInfo(log);
        dto = xxlJobLogDao.load(log.getId());


        log.setHandleTime(new Date());
        log.setHandleCode(2);
        log.setHandleMsg("2");
        ret1 = xxlJobLogDao.updateHandleInfo(log);
        dto = xxlJobLogDao.load(log.getId());



        xxlJobLogDao.findLogReport(new Date(System.currentTimeMillis() - 60 * 60 * 24 * 1000), new Date());

        Sort sort = Sort.by("id").ascending();
        PageRequest pageRequest = PageRequest.of(0, 10, sort);
        Specification<XxlJobLog> specification = (Specification<XxlJobLog>) (root, query, criteriaBuilder) -> {
            ArrayList<Predicate> list = new ArrayList<>();
            list.add(criteriaBuilder.equal(root.get("jobGroup"), 1));
            list.add(criteriaBuilder.equal(root.get("jobId"), 1));
            list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("triggerTime"), new Date()));
            list.add(criteriaBuilder.not(root.get("id").in(Arrays.asList(1, 2))));
            Predicate[] predicates = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(predicates));
        };
        xxlJobLogDao.findJobLogIds(specification, pageRequest);

        xxlJobLogDao.clearLog(Arrays.asList(-1L));

        xxlJobLogDao.findFailJobLogIds(pageRequest);

        xxlJobLogDao.updateAlarmStatus(log.getId(), log.getAlarmStatus(), 3);

        int ret2 = xxlJobLogDao.delete(log.getJobId());
    }

}
