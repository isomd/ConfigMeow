package io.github.timemachinelab.service.impl;

import io.github.timemachinelab.common.ResultCode;
import io.github.timemachinelab.dto.ConfigRequest;
import io.github.timemachinelab.dto.ConfigResponse;
import io.github.timemachinelab.entity.ConfigItem;
import io.github.timemachinelab.repository.ConfigRepository;
import io.github.timemachinelab.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    
    private final ConfigRepository configRepository;
    
    @Override
    @Transactional
    public ConfigResponse createConfig(ConfigRequest request) {
        log.info("创建配置: appName={}, environment={}, configKey={}", 
                request.getAppName(), request.getEnvironment(), request.getConfigKey());
        
        // 检查配置是否已存在
        Optional<ConfigItem> existingConfig = configRepository.findByAppNameAndEnvironmentAndConfigKey(
                request.getAppName(), request.getEnvironment(), request.getConfigKey());
        
        if (existingConfig.isPresent()) {
            throw new RuntimeException("配置已存在: " + request.getConfigKey());
        }
        
        // 创建新配置
        ConfigItem configItem = new ConfigItem();
        BeanUtils.copyProperties(request, configItem);
        configItem.setCreatedBy(request.getOperator());
        configItem.setUpdatedBy(request.getOperator());
        configItem.setCreatedTime(LocalDateTime.now());
        configItem.setUpdatedTime(LocalDateTime.now());
        configItem.setVersion(1);
        
        ConfigItem savedConfig = configRepository.save(configItem);
        return convertToResponse(savedConfig);
    }
    
    @Override
    @Transactional
    public ConfigResponse updateConfig(ConfigRequest request) {
        log.info("更新配置: id={}, configKey={}", request.getId(), request.getConfigKey());
        
        ConfigItem existingConfig = configRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("配置不存在: " + request.getId()));
        
        // 检查是否有其他配置使用相同的key（除了当前配置）
        Optional<ConfigItem> duplicateConfig = configRepository.findByAppNameAndEnvironmentAndConfigKey(
                request.getAppName(), request.getEnvironment(), request.getConfigKey());
        
        if (duplicateConfig.isPresent() && !duplicateConfig.get().getId().equals(request.getId())) {
            throw new RuntimeException("配置键已存在: " + request.getConfigKey());
        }
        
        // 更新配置
        BeanUtils.copyProperties(request, existingConfig, "id", "createdBy", "createdTime", "version");
        existingConfig.setUpdatedBy(request.getOperator());
        existingConfig.setUpdatedTime(LocalDateTime.now());
        existingConfig.setVersion(existingConfig.getVersion() + 1);
        
        ConfigItem updatedConfig = configRepository.update(existingConfig);
        return convertToResponse(updatedConfig);
    }
    
    @Override
    @Transactional
    public void deleteConfig(Long id) {
        log.info("删除配置: id={}", id);
        
        if (!configRepository.findById(id).isPresent()) {
            throw new RuntimeException("配置不存在: " + id);
        }
        
        configRepository.deleteById(id);
    }
    
    @Override
    public ConfigResponse getConfigById(Long id) {
        log.debug("根据ID获取配置: id={}", id);
        
        ConfigItem configItem = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("配置不存在: " + id));
        
        return convertToResponse(configItem);
    }
    
    @Override
    public String getConfigValue(String appName, String environment, String configKey) {
        log.debug("获取配置值: appName={}, environment={}, configKey={}", 
                appName, environment, configKey);
        
        Optional<ConfigItem> configItem = configRepository.findByAppNameAndEnvironmentAndConfigKey(
                appName, environment, configKey);
        
        if (!configItem.isPresent() || !configItem.get().getIsActive()) {
            throw new RuntimeException("配置不存在或未激活: " + configKey);
        }
        
        return configItem.get().getConfigValue();
    }
    
    @Override
    public List<ConfigResponse> getAppConfigs(String appName, String environment) {
        log.debug("获取应用配置: appName={}, environment={}", appName, environment);
        
        List<ConfigItem> configItems = configRepository.findByAppNameAndEnvironmentAndIsActive(
                appName, environment, true);
        
        return configItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getAppConfigsAsMap(String appName, String environment) {
        log.debug("获取应用配置Map: appName={}, environment={}", appName, environment);
        
        List<ConfigItem> configItems = configRepository.findByAppNameAndEnvironmentAndIsActive(
                appName, environment, true);
        
        Map<String, Object> configMap = new HashMap<>();
        for (ConfigItem item : configItems) {
            Object value = convertValueByType(item.getConfigValue(), item.getDataType());
            configMap.put(item.getConfigKey(), value);
        }
        
        return configMap;
    }
    
    @Override
    @Transactional
    public List<ConfigResponse> batchUpdateConfigs(String appName, String environment, 
                                                 Map<String, String> configs, String operator) {
        log.info("批量更新配置: appName={}, environment={}, count={}", 
                appName, environment, configs.size());
        
        List<ConfigResponse> responses = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            String configKey = entry.getKey();
            String configValue = entry.getValue();
            
            Optional<ConfigItem> existingConfig = configRepository.findByAppNameAndEnvironmentAndConfigKey(
                    appName, environment, configKey);
            
            if (existingConfig.isPresent()) {
                // 更新现有配置
                ConfigItem configItem = existingConfig.get();
                configItem.setConfigValue(configValue);
                configItem.setUpdatedBy(operator);
                configItem.setUpdatedTime(LocalDateTime.now());
                configItem.setVersion(configItem.getVersion() + 1);
                
                ConfigItem updatedConfig = configRepository.update(configItem);
                responses.add(convertToResponse(updatedConfig));
            } else {
                // 创建新配置
                ConfigItem configItem = new ConfigItem();
                configItem.setAppName(appName);
                configItem.setEnvironment(environment);
                configItem.setConfigKey(configKey);
                configItem.setConfigValue(configValue);
                configItem.setDataType("string");
                configItem.setIsActive(true);
                configItem.setCreatedBy(operator);
                configItem.setUpdatedBy(operator);
                configItem.setCreatedTime(LocalDateTime.now());
                configItem.setUpdatedTime(LocalDateTime.now());
                configItem.setVersion(1);
                
                ConfigItem savedConfig = configRepository.save(configItem);
                responses.add(convertToResponse(savedConfig));
            }
        }
        
        return responses;
    }
    
    @Override
    @Transactional
    public List<ConfigResponse> copyEnvironmentConfigs(String appName, String sourceEnv, 
                                                     String targetEnv, String operator) {
        log.info("复制环境配置: appName={}, sourceEnv={}, targetEnv={}", 
                appName, sourceEnv, targetEnv);
        
        // 获取源环境的所有配置
        List<ConfigItem> sourceConfigs = configRepository.findByAppNameAndEnvironment(appName, sourceEnv);
        
        if (sourceConfigs.isEmpty()) {
            throw new RuntimeException("源环境没有配置: " + sourceEnv);
        }
        
        List<ConfigResponse> responses = new ArrayList<>();
        
        for (ConfigItem sourceConfig : sourceConfigs) {
            // 检查目标环境是否已存在相同配置
            Optional<ConfigItem> existingConfig = configRepository.findByAppNameAndEnvironmentAndConfigKey(
                    appName, targetEnv, sourceConfig.getConfigKey());
            
            if (!existingConfig.isPresent()) {
                // 创建新配置
                ConfigItem newConfig = new ConfigItem();
                BeanUtils.copyProperties(sourceConfig, newConfig, "id", "environment", "createdTime", "updatedTime", "version");
                newConfig.setEnvironment(targetEnv);
                newConfig.setCreatedBy(operator);
                newConfig.setUpdatedBy(operator);
                newConfig.setCreatedTime(LocalDateTime.now());
                newConfig.setUpdatedTime(LocalDateTime.now());
                newConfig.setVersion(1);
                
                ConfigItem savedConfig = configRepository.save(newConfig);
                responses.add(convertToResponse(savedConfig));
            }
        }
        
        return responses;
    }
    
    @Override
    public Map<String, Object> getConfigsByPage(String appName, String environment, int page, int size) {
        log.debug("分页查询配置: appName={}, environment={}, page={}, size={}", 
                appName, environment, page, size);
        
        // 查询总数
        long total = configRepository.countByAppNameAndEnvironment(appName, environment);
        
        // 分页查询
        List<ConfigItem> configItems = configRepository.findByPage(appName, environment, page, size);
        
        List<ConfigResponse> configs = configItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("configs", configs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (total + size - 1) / size);
        
        return result;
    }
    
    @Override
    public List<ConfigResponse> getConfigHistory(String appName, String environment, String configKey) {
        log.debug("获取配置历史: appName={}, environment={}, configKey={}", 
                appName, environment, configKey);
        
        // 注意：这里需要根据实际的历史表结构来实现
        // 目前简化实现，只返回当前配置
        Optional<ConfigItem> configItem = configRepository.findByAppNameAndEnvironmentAndConfigKey(
                appName, environment, configKey);
        
        if (configItem.isPresent()) {
            return Collections.singletonList(convertToResponse(configItem.get()));
        }
        
        return Collections.emptyList();
    }
    
    /**
     * 将ConfigItem转换为ConfigResponse
     */
    private ConfigResponse convertToResponse(ConfigItem configItem) {
        ConfigResponse response = new ConfigResponse();
        BeanUtils.copyProperties(configItem, response);
        return response;
    }
    
    /**
     * 根据数据类型转换配置值
     */
    private Object convertValueByType(String value, String dataType) {
        if (!StringUtils.hasText(value) || !StringUtils.hasText(dataType)) {
            return value;
        }
        
        try {
            switch (dataType.toLowerCase()) {
                case "int":
                case "integer":
                    return Integer.parseInt(value);
                case "long":
                    return Long.parseLong(value);
                case "double":
                    return Double.parseDouble(value);
                case "float":
                    return Float.parseFloat(value);
                case "boolean":
                    return Boolean.parseBoolean(value);
                case "json":
                    // 这里可以集成JSON解析库，如Jackson
                    return value; // 暂时返回字符串
                default:
                    return value;
            }
        } catch (Exception e) {
            log.warn("配置值类型转换失败: value={}, dataType={}, error={}", value, dataType, e.getMessage());
            return value;
        }
    }
}