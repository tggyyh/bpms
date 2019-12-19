package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.CustomRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomRuleMapper {
    int insertCustomRule(CustomRule customRule);
    int insertCustomRuleList(List<CustomRule> customRuleList);
    int deleteByCustomId(int customId);
    int updateCustomRuleList(List<CustomRule> customRuleList);
    int deleteByIds(@Param("map") Map<String, Object> map);
}
