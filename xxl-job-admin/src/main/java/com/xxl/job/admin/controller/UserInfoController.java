package com.xxl.job.admin.controller;

import cn.hutool.extra.validation.ValidationUtil;
import com.xxl.job.admin.common.pojo.dto.PwdDTO;
import com.xxl.job.admin.common.pojo.dto.UserInfoDTO;
import com.xxl.job.admin.common.pojo.dto.UserInfoFilterDTO;
import com.xxl.job.admin.common.pojo.vo.PageVO;
import com.xxl.job.admin.common.pojo.vo.UserInfoVO;
import com.xxl.job.admin.service.UserInfoService;
import com.xxl.job.core.pojo.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户信息控制器
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@Slf4j
@Validated
@Api(tags = "用户信息管理")
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends AbstractController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation("添加用户")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> saveUserInfo(@Validated @RequestBody UserInfoDTO userInfoDTO) {
        log.info("saveUserInfo {}", userInfoDTO.toString());
        userInfoDTO.setCreatedUser(getAccount());
        userInfoService.saveUserInfo(userInfoDTO);
        return ResponseVO.success();
    }

    @ApiOperation("修改用户")
    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> updateUserInfo(@Validated @RequestBody UserInfoDTO userInfoDTO) {
        log.info("updateUserInfo {}", userInfoDTO.toString());
        userInfoDTO.setUpdatedUser(getAccount());
        userInfoService.updateUserInfo(userInfoDTO);
        return ResponseVO.success();
    }

    @ApiOperation("查询用户")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<PageVO<UserInfoVO>> queryUserInfo(@Validated UserInfoFilterDTO filterDTO) {
        log.info("queryUserInfo {}", filterDTO.toString());
        ValidationUtil.validate(filterDTO);
        return ResponseVO.success(userInfoService.page(filterDTO));
    }

    @ApiOperation("删除用户")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "id", dataTypeClass = Long.class, value = "主键ID", required = true),
    })
    public ResponseVO<Void> deleteUserInfo(@PathVariable("id") @NotNull(message = "主键ID 不能为空") Long id) {
        log.info("deleteUserInfo {}", id);
        userInfoService.delete(id);
        return ResponseVO.success();
    }

    @ApiOperation("批量删除用户")
    @DeleteMapping(value = "/batch", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> deleteUserInfos(@RequestBody @Validated List<Long> ids) {
        log.info("deleteUserInfos {}", ids);
        userInfoService.deleteUserInfo(ids);
        return ResponseVO.success();
    }

    @ApiOperation("修改用户密码")
    @PatchMapping(value = "/pwd", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> updatePwd(@RequestBody @Valid PwdDTO pwdDTO) {
        log.info("updatePwd {}", pwdDTO.toString());
        userInfoService.modifyPwd(pwdDTO);
        return ResponseVO.success();
    }

    @ApiOperation("重置用户密码")
    @PatchMapping(value = "/{account}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "account", dataTypeClass = String.class, value = "用户账号", required = true)
    })
    public ResponseVO<String> resetPwd(@PathVariable("account") @NotNull(message = "账号不能为空") String account) {
        log.info("resetPwd {}", account);
        return ResponseVO.success(userInfoService.resetPwd(account));
    }







}
