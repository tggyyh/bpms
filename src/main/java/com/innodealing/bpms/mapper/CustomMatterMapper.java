package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.CustomMatter;

import java.util.List;
import java.util.Map;

public interface CustomMatterMapper {
    List<CustomMatter> findAllCustomMatter();
    List<CustomMatter> findCustomMatterByKey(CustomMatter customMatter);
    List<CustomMatter> findByKey(CustomMatter customMatter);
    CustomMatter findById(int id);
    int insertCustomMatter(CustomMatter customMatter);
    int updateResetRelation(CustomMatter customMatter);
    int updateRelationByIds(Integer[] ids);
    int updateCustomMatter(CustomMatter customMatter);
    int updateStatus(CustomMatter customMatter);
    int deleteById(int id);
    List<CustomMatter> findByKeys(ReqData reqData);
}
