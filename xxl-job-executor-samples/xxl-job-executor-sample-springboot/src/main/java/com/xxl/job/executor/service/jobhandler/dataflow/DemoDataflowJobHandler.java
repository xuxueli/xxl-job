package com.xxl.job.executor.service.jobhandler.dataflow;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IDataflowJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.util.ShardingUtil.ShardingVO;
import com.xxl.job.executor.service.jobhandler.dataflow.dto.Foo;
import com.xxl.job.executor.service.jobhandler.dataflow.service.DataService;

/**
 * 数据流处理Job Demo
 * @author created by liyong on 2019-1-24 16:01:18
 */
@JobHandler(value="demoDataflowJobHandler")
@Component
public class DemoDataflowJobHandler extends IDataflowJobHandler<Foo> {
	@Autowired
	private DataService dataService;
	
	@Override
	public Collection<Foo> fetchData(ShardingVO shardingVO, String param) {
		return dataService.getDataPage(1, shardingVO.getIndex(), shardingVO.getTotal());
	}

	@Override
	public ReturnT<String> processData(ShardingVO shardingVO, String param, Collection<Foo> data) {
		for(Foo foo : data) {
			dataService.update(new Foo(foo.getId(), 1));
		}
		return SUCCESS;
	}
	
}
