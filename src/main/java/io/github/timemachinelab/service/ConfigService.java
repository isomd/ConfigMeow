package io.github.timemachinelab.service;

import io.github.timemachinelab.dto.ConfigRequest;
import io.github.timemachinelab.dto.ConfigResponse;
import java.util.List;
import java.util.Map;

public interface ConfigService {
    
    // 创建配置
    ConfigResponse createConfig(ConfigRequest request);
    
    // 更新配置
    ConfigResponse updateConfig(ConfigRequest request);
    
    // 删除配置
    void deleteConfig(Long id);
    
    // 根据ID获取配置
    ConfigResponse getConfigById(Long id);
    
    // 获取单个配置值
    String getConfigValue(String appName, String environment, String configKey);
    
    // 获取应用的所有配置
    List<ConfigResponse> getAppConfigs(String appName, String environment);
    
    // 获取应用的所有配置（Map格式，便于客户端使用）
    Map<String, Object> getAppConfigsAsMap(String appName, String environment);
    
    // 批量更新配置
    List<ConfigResponse> batchUpdateConfigs(String appName, String environment, 
                                          Map<String, String> configs, String operator);
    
    // 复制环境配置
    List<ConfigResponse> copyEnvironmentConfigs(String appName, String sourceEnv, 
                                              String targetEnv, String operator);
    
    // 分页查询配置
    Map<String, Object> getConfigsByPage(String appName, String environment, 
                                        int page, int size);
    
    // 配置历史版本管理
    List<ConfigResponse> getConfigHistory(String appName, String environment, String configKey);
}