package io.github.timemachinelab.pojo.dto;

import javax.validation.constraints.NotBlank;

import io.github.timemachinelab.pojo.entity.ConfigItemDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigItemDTO {
    @NotBlank(message = "应用名称不能为空")
    private String appName;        // 应用名称
    @NotBlank(message = "环境不能为空")
    private String environment;    // 环境(dev/test/prod)
    @NotBlank(message = "配置键不能为空")
    private String configKey;      // 配置键
    private String configValue;    // 配置值
    private String description;    // 配置描述
    private String dataType;       // 数据类型(string/int/boolean/json)
    private Boolean isActive;      // 是否激活

    public static ConfigItemDTO convert2DTO(ConfigItemDO configItemDO) {
        if (configItemDO == null) {
            return null;
        }
        return ConfigItemDTO.builder()
        .appName(configItemDO.getAppName())
        .environment(configItemDO.getEnvironment())
        .configKey(configItemDO.getConfigKey())
        .configValue(configItemDO.getConfigValue())
        .description(configItemDO.getDescription())
        .dataType(configItemDO.getDataType())
        .isActive(configItemDO.getIsActive())
        .build();
    }
}
