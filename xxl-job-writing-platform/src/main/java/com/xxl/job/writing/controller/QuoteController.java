package com.xxl.job.writing.controller;

import com.xxl.job.writing.dto.QuoteCreateDTO;
import com.xxl.job.writing.exception.BusinessException;
import com.xxl.job.writing.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 报价控制器
 * 实现报价相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/quote")
@Tag(name = "报价管理", description = "专家报价相关接口")
public class QuoteController {

    /**
     * 专家报价
     * POST /api/quote
     */
    @PostMapping
    @Operation(summary = "专家报价", description = "专家对任务进行报价")
    public Result<Long> createQuote(@Validated @RequestBody QuoteCreateDTO quoteCreateDTO) {
        log.info("专家报价，任务ID: {}, 报价金额: {}", quoteCreateDTO.getTaskId(), quoteCreateDTO.getAmount());
        throw BusinessException.notImplemented("Create quote");
    }

    /**
     * 获取专家报价列表
     * GET /api/quote/expert/{expertId}
     */
    @GetMapping("/expert/{expertId}")
    @Operation(summary = "获取专家报价列表", description = "获取指定专家的所有报价")
    public Result<Object> getExpertQuotes(@PathVariable Long expertId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        log.info("获取专家报价列表，专家ID: {}, 页码: {}, 页大小: {}", expertId, page, size);
        throw BusinessException.notImplemented("Get expert quotes");
    }

    /**
     * 撤销报价
     * DELETE /api/quote/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "撤销报价", description = "专家撤销自己的报价")
    public Result<Void> cancelQuote(@PathVariable Long id) {
        log.info("专家撤销报价，报价ID: {}", id);
        throw BusinessException.notImplemented("Cancel quote");
    }

    /**
     * 获取可报价任务列表
     * GET /api/quote/available-tasks
     */
    @GetMapping("/available-tasks")
    @Operation(summary = "获取可报价任务列表", description = "获取专家可以报价的任务列表")
    public Result<Object> getAvailableTasks(@RequestParam Long expertId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("获取可报价任务列表，专家ID: {}, 页码: {}, 页大小: {}", expertId, page, size);
        throw BusinessException.notImplemented("Get available tasks");
    }

    /**
     * 修改报价
     * PUT /api/quote/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改报价", description = "专家修改自己的报价")
    public Result<Void> updateQuote(@PathVariable Long id, @Validated @RequestBody QuoteCreateDTO quoteCreateDTO) {
        log.info("专家修改报价，报价ID: {}, 新报价金额: {}", id, quoteCreateDTO.getAmount());
        throw BusinessException.notImplemented("Update quote");
    }
}
