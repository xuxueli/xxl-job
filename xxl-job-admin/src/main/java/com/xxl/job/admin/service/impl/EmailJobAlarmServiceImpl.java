package com.xxl.job.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.mail.EmailTemplate;
import com.xxl.job.admin.common.mail.domain.EmailTo;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.common.pojo.vo.JobLogVO;
import com.xxl.job.admin.service.JobAlarmService;
import com.xxl.job.core.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务报警邮箱服务实现类
 *
 * @author Rong.Jia
 * @date 2023/05/14
 */
@Slf4j
@Service
public class EmailJobAlarmServiceImpl implements JobAlarmService {

    @Autowired
    private EmailTemplate emailTemplate;

    @Override
    public Boolean sendAlarm(JobInfoVO info, JobLogVO jobLog) {

        // send monitor email
        if (ObjectUtil.isNotNull(info) && StrUtil.isNotBlank(info.getAlarmEmail())) {

            // alarmContent
            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (ObjectUtil.notEqual(ResponseEnum.SUCCESS.getCode(), jobLog.getTriggerCode())) {
                alarmContent += "<br>TriggerMsg=<br>" + jobLog.getTriggerMessage();
            }
            if (jobLog.getHandleCode() > 0 && ObjectUtil.notEqual(ResponseEnum.SUCCESS.getCode(), jobLog.getHandleCode())) {
                alarmContent += "<br>HandleCode=" + jobLog.getHandleMessage();
            }

            String content = MessageFormat.format(emailJobAlarmTemplate(),
                    info.getJobGroup().getTitle(),
                    info.getId(),
                    info.getName(),
                    alarmContent);

            List<EmailTo> emails = StrUtil.split(info.getAlarmEmail(), StrUtil.C_COMMA)
                            .stream().map(a -> {
                        EmailTo emailTo = new EmailTo();
                        emailTo.setMail(a);
                        return emailTo;
                    }).collect(Collectors.toList());
            try {
                emailTemplate.sendText("任务调度中心监控报警", content, emails);
                return Boolean.TRUE;
            }catch (Exception e) {
                log.error(">>>>>>>>>>> xxl-job, job fail alarm email send error, JobLogId:{}", jobLog.getId(), e);
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    /**
     * 拼接任务报警模板
     *
     * @return {@link String}
     */
    private static String emailJobAlarmTemplate() {
        return "<h5>" + "监控告警明细" + "：</span>" +
                "<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n" +
                "   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
                "      <tr>\n" +
                "         <td width=\"20%\" >" + "执行器" + "</td>\n" +
                "         <td width=\"10%\" >" + "任务ID" + "</td>\n" +
                "         <td width=\"20%\" >" + "任务描述" + "</td>\n" +
                "         <td width=\"10%\" >" + "告警类型" + "</td>\n" +
                "         <td width=\"40%\" >" + "告警内容" + "</td>\n" +
                "      </tr>\n" +
                "   </thead>\n" +
                "   <tbody>\n" +
                "      <tr>\n" +
                "         <td>{0}</td>\n" +
                "         <td>{1}</td>\n" +
                "         <td>{2}</td>\n" +
                "         <td>" + "调度失败" + "</td>\n" +
                "         <td>{3}</td>\n" +
                "      </tr>\n" +
                "   </tbody>\n" +
                "</table>";
    }
}
