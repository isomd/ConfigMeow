package io.github.timemachinelab.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.github.timemachinelab.pojo.dto.ConfigItemDTO;
import io.github.timemachinelab.util.SnowflakeIdUtil;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("config_item")
public class ConfigItemDO {
    @TableId
    private String id;
    private String appName;        // 应用名称
    private String environment;    // 环境(dev/test/prod)
    private String configKey;      // 配置键
    private String configValue;    // 配置值
    private String description;    // 配置描述
    private String dataType;       // 数据类型(string/int/boolean/json)
    private Boolean isActive;      // 是否激活
    private String createdBy;      // 创建人
    private String updatedBy;      // 更新人
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    private Integer version;       // 版本号，用于乐观锁

    public static ConfigItemDO convert2DO(ConfigItemDTO configItemDTO) {
        if (configItemDTO == null) {
            return null;
        }
        return ConfigItemDO.builder()
        .id(String.valueOf(SnowflakeIdUtil.generateId()))
        .appName(configItemDTO.getAppName())
        .environment(configItemDTO.getEnvironment())
        .configKey(configItemDTO.getConfigKey())
        .configValue(configItemDTO.getConfigValue())
        .description(configItemDTO.getDescription())
        .dataType(configItemDTO.getDataType())
        .isActive(configItemDTO.getIsActive())
        .build();
    }
}