package io.github.timemachinelab.domain;

import io.github.timemachinelab.mapper.ConfigMapper;
import io.github.timemachinelab.pojo.dto.ConfigItemDTO;
import io.github.timemachinelab.pojo.entity.ConfigItemDO;

import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Component
public class ConfigDomain {
    
    private final ConfigMapper configMapper;

    public ConfigDomain(ConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    /**
     * 设置配置项（插入或更新）
     * @param configItemDTO 配置项DTO
     * @return 是否成功
     */
    public boolean setConfig(ConfigItemDTO configItemDTO) {
        ConfigItemDO configItem = ConfigItemDO.convert2DO(configItemDTO);
        int result = configMapper.insertOrUpdate(configItem);
        return result > 0;
    }

    /**
     * 根据应用名、环境、配置键获取配置项
     * @param appName 应用名
     * @param environment 环境
     * @param configKey 配置键
     * @return 配置项DTO
     */
    public ConfigItemDTO getConfigByKey(String appName, String environment, String configKey) {
        QueryWrapper<ConfigItemDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_name", appName)
                   .eq("environment", environment)
                   .eq("config_key", configKey)
                   .eq("is_active", true);
        
        ConfigItemDO configItem = configMapper.selectOne(queryWrapper);
        
        if (configItem == null) {
            return null;
        }
        
        return ConfigItemDTO.convert2DTO(configItem);
    }

    /**
     * 根据应用名、环境、配置键删除配置项（物理删除）
     * @param appName 应用名
     * @param environment 环境
     * @param configKey 配置键
     * @return 是否成功
     */
    public ConfigItemDTO deleteConfig(String appName, String environment, String configKey) {
        QueryWrapper<ConfigItemDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_name", appName)
                   .eq("environment", environment)
                   .eq("config_key", configKey);
        
        int result = configMapper.delete(queryWrapper);
        return result > 0 ? ConfigItemDTO.builder().appName(appName).environment(environment).configKey(configKey).build() : null;

    }
}
