package com.xxl.job.executor.factory.handler;

import com.xxl.job.executor.context.XxlJobHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 'glue' 处理程序
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Getter
@AllArgsConstructor
public class GlueJobHandler extends JobHandler {

	private JobHandler jobHandler;
	private long glueUpdateTime;

	@Override
	public void execute(Object param) throws Exception {
		XxlJobHelper.log("----------- glue.version "+ glueUpdateTime +" -----------");
		jobHandler.execute(param);
	}

	@Override
	public void init() throws Exception {
		this.jobHandler.init();
	}

	@Override
	public void destroy() throws Exception {
		this.jobHandler.destroy();
	}
}
