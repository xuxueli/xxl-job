package com.xxl.job.admin.controller;

import com.xxl.job.admin.service.AdminApiService;
import com.xxl.job.core.pojo.dto.HandleCallbackParam;
import com.xxl.job.core.pojo.dto.RegistryParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理api控制器
 *
 * @author Rong.Jia
 * @date 2023/05/16
 */
@Slf4j
@Validated
@RestController
@Api(tags = "ADMIN端API", hidden = true)
@RequestMapping("/admin")
public class AdminApiController extends AbstractController {

    @Autowired
    private AdminApiService adminApiService;

    @ApiOperation("回调")
    @PostMapping(value = "/callback", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> callback(@Validated @RequestBody List<HandleCallbackParam> handleCallbackParams) {
        log.info("callback {}", handleCallbackParams.toString());
        adminApiService.callback(handleCallbackParams);
        return ResponseVO.success();
    }

    @ApiOperation("注册")
    @PostMapping(value = "/registry", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> registry(@Validated @RequestBody RegistryParam registryParam) {
        log.info("registry {}", registryParam.toString());
        adminApiService.registry(registryParam);
        return ResponseVO.success();
    }

    @ApiOperation("取消注册")
    @PostMapping(value = "/unRegistry", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> unRegistry(@Validated @RequestBody RegistryParam registryParam) {
        log.info("unRegistry {}", registryParam.toString());
        adminApiService.unRegistry(registryParam);
        return ResponseVO.success();
    }













}
