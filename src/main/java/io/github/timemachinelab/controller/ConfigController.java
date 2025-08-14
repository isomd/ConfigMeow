package io.github.timemachinelab.controller;

import io.github.timemachinelab.common.Result;
import io.github.timemachinelab.common.ResultCode;
import io.github.timemachinelab.pojo.dto.ConfigItemDTO;
import io.github.timemachinelab.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/config/api")
@RequiredArgsConstructor
public class ConfigController {
    
    private final ConfigService configService;
    
    /**
     * 设置配置
     */
    @PostMapping("/set")
    public Result<?> setConfig(@Valid @RequestBody ConfigItemDTO configItemDTO) {
        if(!configService.setConfig(configItemDTO)){
            return Result.error(ResultCode.CONFIG_SET_FAILED);
        } else {
            return Result.success();

        }
    }

    /**
     * 根据应用名、环境、配置键获取配置
     */
    @GetMapping("/get")
    public Result<?> getConfigByKey(@RequestParam String appName,
                                                @RequestParam String environment,
                                                @RequestParam String configKey) {
        return Result.success(configService.getConfigByKey(appName, environment, configKey));
    }
    
    /**
     * 删除配置
     */
    @GetMapping("/delete")
    public Result<?> deleteConfig(@RequestParam String appName,
                                                @RequestParam String environment,
                                                @RequestParam String configKey) {
        return Result.success(configService.deleteConfig(appName, environment, configKey));
    }
}
