package com.xuxueli.executor.sample.jboot.controller;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/")
public class IndexController extends JbootController {

    public void index() {
        renderText("xxl job executor running.");
    }

}