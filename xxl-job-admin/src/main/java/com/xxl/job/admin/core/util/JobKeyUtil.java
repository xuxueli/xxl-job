package com.xxl.job.admin.core.util;

import com.xxl.job.admin.core.model.XxlJobInfo;

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

}
