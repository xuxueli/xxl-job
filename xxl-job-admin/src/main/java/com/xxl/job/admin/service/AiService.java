package com.xxl.job.admin.service;

import com.xxl.job.admin.config.AiConfig;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI Code Generation Service
 *
 * @author xxl-job
 */
@Service
public class AiService {
    private static Logger logger = LoggerFactory.getLogger(AiService.class);

    @Resource
    private AiConfig aiConfig;

    /**
     * Generate code using AI service
     *
     * @param prompt User's feature description
     * @param glueType Script type (GLUE_SHELL, GLUE_PYTHON, etc.)
     * @return Generated code
     */
    public Response<String> generateCode(String prompt, String glueType) {
        try {
            // Check if AI is enabled
            if (!aiConfig.isEnabled()) {
                return Response.ofFail(I18nUtil.getString("jobinfo_ai_not_enabled"));
            }

            // Check configuration
            if (!aiConfig.isConfigValid()) {
                return Response.ofFail(I18nUtil.getString("jobinfo_ai_config_missing"));
            }

            // Build system prompt based on glue type
            String systemPrompt = buildSystemPrompt(glueType);
            if (systemPrompt == null) {
                return Response.ofFail(I18nUtil.getString("jobinfo_ai_not_supported"));
            }

            // Call AI service
            String generatedCode = callAiService(systemPrompt, prompt);

            // Clean up markdown code blocks if present
            generatedCode = cleanCodeBlocks(generatedCode);

            logger.info(">>>>>>>>>>> xxl-job ai code generation success, glueType:{}, prompt:{}", glueType, prompt);
            return Response.ofSuccess(generatedCode);

        } catch (Exception e) {
            logger.error(">>>>>>>>>>> xxl-job ai code generation error", e);
            return Response.ofFail(I18nUtil.getString("jobinfo_ai_fail") + ": " + e.getMessage());
        }
    }

    /**
     * Build system prompt based on script type
     */
    private String buildSystemPrompt(String glueType) {
        switch (glueType) {
            case "GLUE_SHELL":
                return getShellSystemPrompt();
            case "GLUE_PYTHON":
            case "GLUE_PYTHON2":
                return getPythonSystemPrompt();
            case "GLUE_NODEJS":
                return getNodejsSystemPrompt();
            case "GLUE_POWERSHELL":
                return getPowerShellSystemPrompt();
            case "GLUE_PHP":
                return getPhpSystemPrompt();
            default:
                return null;
        }
    }

    /**
     * Shell script system prompt
     */
    private String getShellSystemPrompt() {
        return "You are an expert Shell script developer for XXL-JOB distributed task scheduling system.\n\n" +
                "Generate a Shell script with these REQUIRED elements:\n" +
                "1. Shebang: #!/bin/bash\n" +
                "2. Generate TraceId: traceId=\"PEPPA_JOB_$(date +%s%3N)\"\n" +
                "3. Print XXL-JOB parameters:\n" +
                "   - $0: Script location\n" +
                "   - $1: Task parameters (from XXL-JOB console)\n" +
                "   - $2: Shard index (starts from 0)\n" +
                "   - $3: Total shards\n" +
                "4. When making HTTP calls, pass traceId in header: --header \"peppa-trace-id: $traceId\"\n" +
                "5. Use standard output format with timestamp, parameters, and traceId\n" +
                "6. End with: echo \"Good bye!\" and exit 0\n" +
                "7. Add comments explaining key logic\n\n" +
                "Generate clean, production-ready code following these patterns.\n" +
                "Return ONLY the code, no explanations.";
    }

    /**
     * Python script system prompt
     */
    private String getPythonSystemPrompt() {
        return "You are an expert Python developer for XXL-JOB distributed task scheduling system.\n\n" +
                "Generate a Python script with these REQUIRED elements:\n" +
                "1. Shebang: #!/usr/bin/env python3\n" +
                "2. Import necessary modules (sys, datetime, requests, etc.)\n" +
                "3. Generate TraceId: trace_id = f\"PEPPA_JOB_{int(datetime.datetime.now().timestamp() * 1000)}\"\n" +
                "4. Read XXL-JOB parameters from sys.argv:\n" +
                "   - sys.argv[0]: Script location\n" +
                "   - sys.argv[1]: Task parameters\n" +
                "   - sys.argv[2]: Shard index\n" +
                "   - sys.argv[3]: Total shards\n" +
                "5. When making HTTP calls, pass traceId in headers: headers={'peppa-trace-id': trace_id}\n" +
                "6. Use print() for output with timestamp, parameters, and traceId\n" +
                "7. End with: print(\"Good bye!\") and sys.exit(0)\n" +
                "8. Add docstrings and comments explaining key logic\n\n" +
                "Generate clean, production-ready code following these patterns.\n" +
                "Return ONLY the code, no explanations.";
    }

    /**
     * Node.js script system prompt
     */
    private String getNodejsSystemPrompt() {
        return "You are an expert Node.js developer for XXL-JOB distributed task scheduling system.\n\n" +
                "Generate a Node.js script with these REQUIRED elements:\n" +
                "1. Shebang: #!/usr/bin/env node\n" +
                "2. Require necessary modules (axios, etc.)\n" +
                "3. Generate TraceId: const traceId = `PEPPA_JOB_${Date.now()}`;\n" +
                "4. Read XXL-JOB parameters from process.argv:\n" +
                "   - process.argv[0]: Node binary location\n" +
                "   - process.argv[1]: Script location\n" +
                "   - process.argv[2]: Task parameters\n" +
                "   - process.argv[3]: Shard index\n" +
                "   - process.argv[4]: Total shards\n" +
                "5. When making HTTP calls, pass traceId in headers: headers: {'peppa-trace-id': traceId}\n" +
                "6. Use console.log() for output with timestamp, parameters, and traceId\n" +
                "7. End with: console.log('Good bye!'); process.exit(0);\n" +
                "8. Add comments explaining key logic\n\n" +
                "Generate clean, production-ready code following these patterns.\n" +
                "Return ONLY the code, no explanations.";
    }

    /**
     * PowerShell script system prompt
     */
    private String getPowerShellSystemPrompt() {
        return "You are an expert PowerShell developer for XXL-JOB distributed task scheduling system.\n\n" +
                "Generate a PowerShell script with these REQUIRED elements:\n" +
                "1. Generate TraceId: $traceId = \"PEPPA_JOB_\" + [DateTimeOffset]::Now.ToUnixTimeMilliseconds()\n" +
                "2. Read XXL-JOB parameters from $args:\n" +
                "   - $args[0]: Task parameters\n" +
                "   - $args[1]: Shard index\n" +
                "   - $args[2]: Total shards\n" +
                "3. When making HTTP calls, pass traceId in headers: -Headers @{\"peppa-trace-id\"=$traceId}\n" +
                "4. Use Write-Host for output with timestamp, parameters, and traceId\n" +
                "5. End with: Write-Host \"Good bye!\"; exit 0\n" +
                "6. Add comments explaining key logic\n\n" +
                "Generate clean, production-ready code following these patterns.\n" +
                "Return ONLY the code, no explanations.";
    }

    /**
     * PHP script system prompt
     */
    private String getPhpSystemPrompt() {
        return "You are an expert PHP developer for XXL-JOB distributed task scheduling system.\n\n" +
                "Generate a PHP script with these REQUIRED elements:\n" +
                "1. Shebang: #!/usr/bin/env php\n" +
                "2. Start with: <?php\n" +
                "3. Generate TraceId: $traceId = \"PEPPA_JOB_\" . round(microtime(true) * 1000);\n" +
                "4. Read XXL-JOB parameters from $argv:\n" +
                "   - $argv[0]: Script location\n" +
                "   - $argv[1]: Task parameters\n" +
                "   - $argv[2]: Shard index\n" +
                "   - $argv[3]: Total shards\n" +
                "5. When making HTTP calls, pass traceId in headers: ['peppa-trace-id' => $traceId]\n" +
                "6. Use echo for output with timestamp, parameters, and traceId\n" +
                "7. End with: echo \"Good bye!\\n\"; exit(0);\n" +
                "8. Add comments explaining key logic\n\n" +
                "Generate clean, production-ready code following these patterns.\n" +
                "Return ONLY the code, no explanations.";
    }

    /**
     * Call AI service using OpenAI-compatible API
     */
    private String callAiService(String systemPrompt, String userPrompt) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // Set timeout
        restTemplate.getInterceptors().add((request, body, execution) -> {
            return execution.execute(request, body);
        });

        // Build request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + aiConfig.getApiKey());

        // Build request body (OpenAI-compatible format)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Call AI service
        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                aiConfig.getServiceUrl(),
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // Parse response
        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody == null || !responseBody.containsKey("choices")) {
            throw new RuntimeException("Invalid AI service response");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("No response from AI service");
        }

        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        String content = (String) message.get("content");

        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Empty response from AI service");
        }

        return content;
    }

    /**
     * Clean markdown code blocks from generated code
     * Remove ```bash, ```python, etc. markers
     */
    private String cleanCodeBlocks(String code) {
        if (code == null) {
            return null;
        }

        // Remove opening code block markers like ```bash, ```python, ```shell, etc.
        code = code.replaceAll("^```[a-z]*\\s*\\n", "");

        // Remove closing code block markers
        code = code.replaceAll("\\n```\\s*$", "");

        // Trim whitespace
        return code.trim();
    }
}
