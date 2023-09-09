package com.xxl.job.admin.controller;

import com.xxl.job.admin.common.pojo.dto.LoginDTO;
import com.xxl.job.admin.common.pojo.dto.RegisterDTO;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.core.pojo.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录控制器
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@Slf4j
@Validated
@RestController
@Api(tags = "登录管理")
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @ApiOperation("登录")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> login(@Validated @RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        log.debug("login {}", loginDTO.toString());
        loginService.login(loginDTO, response);
        return ResponseVO.success();
    }

    @ApiOperation("注册")
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        log.debug("register {}", registerDTO.toString());
        loginService.register(registerDTO);
        return ResponseVO.success();
    }

    @ApiOperation("登出")
    @GetMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> logout(HttpServletRequest request) {
        loginService.logout(request);
        return ResponseVO.success();
    }

}
