package io.github.timemachinelab.repository;

import io.github.timemachinelab.entity.ConfigItem;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigRepository {
    
    // 保存配置
    ConfigItem save(ConfigItem configItem);
    
    // 根据ID查找
    Optional<ConfigItem> findById(Long id);
    
    // 根据应用名、环境、配置键查找
    Optional<ConfigItem> findByAppNameAndEnvironmentAndConfigKey(
        String appName, String environment, String configKey);
    
    // 根据应用名和环境查找所有配置
    List<ConfigItem> findByAppNameAndEnvironment(String appName, String environment);
    
    // 根据应用名查找所有配置
    List<ConfigItem> findByAppName(String appName);
    
    // 查找所有激活的配置
    List<ConfigItem> findByAppNameAndEnvironmentAndIsActive(
        String appName, String environment, Boolean isActive);
    
    // 删除配置
    void deleteById(Long id);
    
    // 更新配置
    ConfigItem update(ConfigItem configItem);
    
    // 分页查询
    List<ConfigItem> findByPage(String appName, String environment, int page, int size);
    
    // 统计总数
    long countByAppNameAndEnvironment(String appName, String environment);
}