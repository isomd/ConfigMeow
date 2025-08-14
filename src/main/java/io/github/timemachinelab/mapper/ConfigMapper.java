package io.github.timemachinelab.mapper;

import io.github.timemachinelab.pojo.entity.ConfigItemDO;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface ConfigMapper extends BaseMapper<ConfigItemDO> {
    
    /**
     * 插入或更新配置项（基于唯一索引：app_name, environment, config_key）
     * @param configItem 配置项实体
     * @return 影响行数
     */
    int insertOrUpdate(ConfigItemDO configItem);
}
