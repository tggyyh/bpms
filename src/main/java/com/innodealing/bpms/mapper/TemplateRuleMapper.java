package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.TemplateRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TemplateRuleMapper {
    List<TemplateRule> findAll();
    TemplateRule findById(int id);
    List<TemplateRule> findByTemplateId(int templateId);
    int insertTemplateRule(TemplateRule templateRule);
    int insertTemplateRuleList(List<TemplateRule> templateRuleList);
    int updateTemplateRule(TemplateRule templateRule);
    int deleteById(int id);
    int deleteByTemplateId(int templateId);
    int updateTemplateRuleList(List<TemplateRule> templateRuleList);
    int deleteByIds(@Param("map") Map<String, Object> map);
}
