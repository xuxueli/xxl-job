package com.xxl.job.admin.core.util;

import com.xxl.job.admin.core.model.XxlJobInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * job key util
 *
 * @author xuxueli 2017-12-22 18:48:45
 */
public class JobKeyUtil {

    /**
     * format job key
     *
     * @param xxlJobInfo
     * @return
     */
    public static String formatJobKey(XxlJobInfo xxlJobInfo){
        return String.valueOf(xxlJobInfo.getJobGroup())
                .concat("_").concat(String.valueOf(xxlJobInfo.getId()));
    }

    /**
     * parse jobId from JobKey
     *
     * @param jobKey
     * @return
     */
    public static int parseJobId(String jobKey){
        if (jobKey!=null && jobKey.trim().length()>0) {
            String[] jobKeyArr = jobKey.split("_");
            if (jobKeyArr.length == 2) {
                String jobIdStr = jobKeyArr[1];
                if (StringUtils.isNotBlank(jobIdStr) && StringUtils.isNumeric(jobIdStr)) {
                    int jobId = Integer.valueOf(jobIdStr);
                    return jobId;
                }
            }
        }
        return -1;
    }

}
