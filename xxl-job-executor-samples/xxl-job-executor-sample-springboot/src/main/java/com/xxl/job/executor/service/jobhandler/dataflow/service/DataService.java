package com.xxl.job.executor.service.jobhandler.dataflow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.xxl.job.executor.service.jobhandler.dataflow.dto.Foo;

@Service
public class DataService {
	private static Map<Integer, Foo> dataPool = new HashMap<>();
	static {
		dataPool.put(1, new Foo(1, 0));
		dataPool.put(2, new Foo(2, 0));
		dataPool.put(3, new Foo(3, 0));
		dataPool.put(4, new Foo(4, 0));
		dataPool.put(5, new Foo(5, 0));
		dataPool.put(6, new Foo(6, 0));
		dataPool.put(7, new Foo(7, 0));
		dataPool.put(8, new Foo(8, 0));
		dataPool.put(9, new Foo(9, 0));
		dataPool.put(10, new Foo(10, 0));
		dataPool.put(11, new Foo(11, 0));
		dataPool.put(12, new Foo(12, 0));
		dataPool.put(13, new Foo(13, 0));
		dataPool.put(14, new Foo(14, 0));
		dataPool.put(15, new Foo(15, 0));
		dataPool.put(16, new Foo(16, 0));
	}

	public List<Foo> getDataPage(int page, int shardIndex, int shardTotal) {
		Set<Integer> keySet = dataPool.keySet();
		Iterator<Integer> it = keySet.iterator();
		List<Foo> list = new ArrayList<>();
		// 模拟查询分片未处理的数据
		while (it.hasNext()) {
			Integer id = it.next();
			if (id % shardTotal == shardIndex && dataPool.get(id).getStatus() == 0) {
				list.add(dataPool.get(id));
			}
		}
		// 对数据进行分页
		int pageSize = 3;
		int startNo = (page - 1) * pageSize;
		int endNo = page * pageSize;
		List<Foo> result = new ArrayList<>();
		if (startNo > list.size()) {
			return result;
		}
		if (endNo > list.size()) {
			endNo = list.size();
		}
		for (int i = startNo; i < endNo; i++) {
			result.add(new Foo(list.get(i).getId(), list.get(i).getStatus()));
		}
		return result;
	}
	
	public void update(Foo foo) {
		dataPool.get(foo.getId()).setStatus(foo.getStatus());
	}
}
