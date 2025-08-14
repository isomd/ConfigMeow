package io.github.timemachinelab.service.impl;

import io.github.timemachinelab.domain.ConfigDomain;
import io.github.timemachinelab.pojo.dto.ConfigItemDTO;
import io.github.timemachinelab.service.ConfigService;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl implements ConfigService {
    private final ConfigDomain configDomain;

    public ConfigServiceImpl(ConfigDomain configDomain) {
        this.configDomain = configDomain;
    }
    
    @Override
    public Boolean setConfig(ConfigItemDTO configItemDTO) {
        return configDomain.setConfig(configItemDTO);
    }

    @Override
    public ConfigItemDTO getConfigByKey(String appName, String environment, String configKey) {
        return configDomain.getConfigByKey(appName, environment, configKey);
    }

    @Override
    public ConfigItemDTO deleteConfig(String appName, String environment, String configKey) {
        return configDomain.deleteConfig(appName, environment, configKey);
    }
}
