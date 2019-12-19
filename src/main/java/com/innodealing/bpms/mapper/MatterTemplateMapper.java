package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.MatterTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MatterTemplateMapper {
    List<MatterTemplate> findMatterAll();
    int isExist(MatterTemplate matterTemplate);
    List<MatterTemplate> findAll();
    List<MatterTemplate> findByType(int type);
    MatterTemplate findById(int id);
    int insertMatterTemplate(MatterTemplate matterTemplate);
    int updateMatterTemplate(MatterTemplate matterTemplate);
    int updateStatusById(int id);
    int updateStatusByIds(int[] ids);
    int deleteById(int id);
    int deleteByIds(int[] ids);
    MatterTemplate findTemplateByProcessId(String pId);
    List<MatterTemplate> findByCompanyId(int companyId);
    MatterTemplate findByTemplateId(int id);
}
