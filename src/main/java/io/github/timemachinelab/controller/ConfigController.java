package io.github.timemachinelab.controller;

import io.github.timemachinelab.common.Result;
import io.github.timemachinelab.dto.ConfigRequest;
import io.github.timemachinelab.dto.ConfigResponse;
import io.github.timemachinelab.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/config/api")
@RequiredArgsConstructor
public class ConfigController {
    
    private final ConfigService configService;
    
    /**
     * 创建配置
     */
    @PostMapping("/create")
    public Result<ConfigResponse> createConfig(@Valid @RequestBody ConfigRequest request) {
        ConfigResponse response = configService.createConfig(request);
        return Result.success(response);
    }
    
    /**
     * 更新配置
     */
    @PostMapping("/update")
    public Result<ConfigResponse> updateConfig(@Valid @RequestBody ConfigRequest request) {
        ConfigResponse response = configService.updateConfig(request);
        return Result.success(response);
    }
    
    /**
     * 删除配置
     */
    @GetMapping("/delete")
    public Result<Void> deleteConfig(@RequestParam Long id) {
        configService.deleteConfig(id);
        return Result.success();
    }
    
    /**
     * 根据ID获取配置
     */
    @GetMapping("/get")
    public Result<ConfigResponse> getConfig(@RequestParam Long id) {
        ConfigResponse response = configService.getConfigById(id);
        return Result.success(response);
    }
    
    /**
     * 获取单个配置值
     */
    @GetMapping("/value")
    public Result<String> getConfigValue(@RequestParam String appName,
                                       @RequestParam String environment,
                                       @RequestParam String configKey) {
        String value = configService.getConfigValue(appName, environment, configKey);
        return Result.success(value);
    }
    
    /**
     * 获取应用的所有配置
     */
    @GetMapping("/app")
    public Result<List<ConfigResponse>> getAppConfigs(@RequestParam String appName,
                                                    @RequestParam String environment) {
        List<ConfigResponse> configs = configService.getAppConfigs(appName, environment);
        return Result.success(configs);
    }
    
    /**
     * 获取应用配置（Map格式）
     */
    @GetMapping("/app/map")
    public Result<Map<String, Object>> getAppConfigsAsMap(@RequestParam String appName,
                                                        @RequestParam String environment) {
        Map<String, Object> configs = configService.getAppConfigsAsMap(appName, environment);
        return Result.success(configs);
    }
    
    /**
     * 批量更新配置
     */
    @PutMapping("/app/batch")
    public Result<List<ConfigResponse>> batchUpdateConfigs(
            @RequestParam String appName,
            @RequestParam String environment,
            @RequestBody Map<String, String> configs,
            @RequestParam String operator) {
        List<ConfigResponse> responses = configService.batchUpdateConfigs(
            appName, environment, configs, operator);
        return Result.success(responses);
    }
    
    /**
     * 复制环境配置
     */
    @PostMapping("/app/copy")
    public Result<List<ConfigResponse>> copyEnvironmentConfigs(
            @RequestParam String appName,
            @RequestParam String sourceEnv,
            @RequestParam String targetEnv,
            @RequestParam String operator) {
        List<ConfigResponse> responses = configService.copyEnvironmentConfigs(
            appName, sourceEnv, targetEnv, operator);
        return Result.success(responses);
    }
    
    /**
     * 分页查询配置
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> getConfigsByPage(
            @RequestParam String appName,
            @RequestParam String environment,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = configService.getConfigsByPage(
            appName, environment, page, size);
        return Result.success(result);
    }
}
