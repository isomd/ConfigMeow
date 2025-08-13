package io.github.timemachinelab.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConfigItem {
    private Long id;
    private String appName;        // 应用名称
    private String environment;    // 环境(dev/test/prod)
    private String configKey;      // 配置键
    private String configValue;    // 配置值
    private String description;    // 配置描述
    private String dataType;       // 数据类型(string/int/boolean/json)
    private Boolean isActive;      // 是否激活
    private String createdBy;      // 创建人
    private String updatedBy;      // 更新人
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer version;       // 版本号，用于乐观锁
}