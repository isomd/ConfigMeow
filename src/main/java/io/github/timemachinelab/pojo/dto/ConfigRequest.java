package io.github.timemachinelab.pojo.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ConfigRequest {
    @NotNull(message = "ID不能为空")
    private String id;

    @NotBlank(message = "应用名称不能为空")
    private String appName;
    
    @NotBlank(message = "环境不能为空")
    private String environment;
    
    @NotBlank(message = "配置键不能为空")
    private String configKey;
    
    @NotBlank(message = "配置值不能为空")
    private String configValue;
    
    private String description;
    
    private String dataType = "string";
    
    private Boolean isActive = true;
    
    private String operator; // 操作人
}