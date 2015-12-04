package com.xxl.controller;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.quartz.DynamicSchedulerUtil;

@Controller
@RequestMapping("/job")
public class IndexController {

	
	@RequestMapping("/index")
	public String index(Model model) {
		List<Map<String, Object>> jobList = DynamicSchedulerUtil.getJobList();
		model.addAttribute("jobList", jobList);
		return "job/index";
	}
	
	@RequestMapping("/help")
	public String help(Model model) {
		return "job/help";
	}
	
	private int simpleParam = 0;
	private ThreadLocal<Integer> tlParam;
	
	@RequestMapping("/beat")
	@ResponseBody
	public String beat() {
		if (tlParam == null) {
			tlParam = new ThreadLocal<Integer>();
		}
		if (tlParam.get() == null) {
			tlParam.set(5000);
		}
		simpleParam++;
		tlParam.set(tlParam.get() + 1);
		
		long start = System.currentTimeMillis();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		return MessageFormat.format("cost:{0}, hashCode:{1}, simpleParam:{2}, tlParam:{3}", 
				(end - start), this.hashCode(), simpleParam, tlParam.get());
	}
	
	
	public static void main(String[] args) {
		Runnable runa = new Runnable() {
			private int simInt = 0;
			private ThreadLocal<Integer> tlParam = new ThreadLocal<Integer>();
			@Override
			public void run() {
				while (true) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if (tlParam.get() == null) {
						tlParam.set(0);
					}
					simInt++;
					tlParam.set(tlParam.get()+1);
					System.out.println(Thread.currentThread().hashCode() + ":simInt:" + simInt);
					System.out.println(Thread.currentThread().hashCode() + ":tlParam:" + tlParam.get());
				}
			}
		};
		
		Thread t1 = new Thread(runa);
		Thread t2 = new Thread(runa);
		t1.start();
		t2.start();
	}
}
