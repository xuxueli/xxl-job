<html>
    <body>
        <div id="mailContentContainer" class="qmbox qm_con_body_content qqmail_webmail_only" style="opacity: 1;"><h5></h5>${i18n.jobconf_monitor_detail}：<span></span><table border="1" cellpadding="3" style="border-collapse:collapse; width:80%;">
                <thead style="font-weight: bold;color: #ffffff;background-color: #ff8c00;">      <tr>
                    <td width="20%">${i18n.jobinfo_field_jobgroup}</td>
                    <td width="10%">${i18n.jobinfo_field_id}</td>
                    <td width="20%">${i18n.jobinfo_field_jobdesc}</td>
                    <td width="10%">${i18n.jobconf_monitor_alarm_title}</td>
                    <td width="40%">${i18n.jobconf_monitor_alarm_content}</td>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${jobGroup.title}</td>
                    <td>${jobInfo.id}</td>
                    <td>${jobInfo.jobDesc}</td>
                    <td>${jobLog.triggerMsg}</td>
                    <td>Alarm Job LogId=${jobLog.id}<br>TriggerMsg=<br>${jobLog.triggerMsg}<br>HandleCode=<br>${jobLog.handleMsg!}</td>
                </tr>
                </tbody>
            </table>
            <style type="text/css">.qmbox style, .qmbox script, .qmbox head, .qmbox link, .qmbox meta {display: none !important;}</style>
        </div>
    </body>
</html>
