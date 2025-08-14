package io.github.timemachinelab.service;

import io.github.timemachinelab.pojo.dto.ConfigItemDTO;

public interface ConfigService {
    // 设置配置
    Boolean setConfig(ConfigItemDTO configItemDTO);

    // 根据应用名、环境、配置键获取配置
    ConfigItemDTO getConfigByKey(String appName, String environment, String configKey);
    
    // 删除配置
    ConfigItemDTO deleteConfig(String appName, String environment, String configKey);
}