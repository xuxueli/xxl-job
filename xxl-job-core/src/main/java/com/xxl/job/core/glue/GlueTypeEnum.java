package com.xxl.job.core.glue;

import com.xxl.job.core.util.XxlJobTool;

/**
 * Created by xuxueli on 17/4/26.
 */
public enum GlueTypeEnum {

	BEAN("BEAN", false, null, null),
	GLUE_GROOVY("GLUE(Java)", false, null, null),
	GLUE_SHELL("GLUE(Shell)", true, "bash", ".sh"),
	GLUE_PYTHON("GLUE(Python)", true, "python", ".py"),
	GLUE_PHP("GLUE(PHP)", true, "php", ".php"),
	GLUE_NODEJS("GLUE(Nodejs)", true, "node", ".js"),
	GLUE_POWERSHELL("GLUE(PowerShell)", true, "powershell", ".ps1");

	final String desc;
	final boolean isScript;
	final String cmd;
	final String suffix;

	GlueTypeEnum(String desc, boolean isScript, String cmd, String suffix) {
		this.desc = desc;
		this.isScript = isScript;
		this.cmd = cmd;
		this.suffix = suffix;
	}

	public String getDesc() {
		return desc;
	}

	public boolean isScript() {
		return isScript;
	}

	public String getCmd() {
		return cmd;
	}

	public String getSuffix() {
		return suffix;
	}

	public static GlueTypeEnum match(String name) {
		return XxlJobTool.getEnum(GlueTypeEnum.class, name);
	}

}