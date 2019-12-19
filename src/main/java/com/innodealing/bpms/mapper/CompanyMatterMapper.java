package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.CompanyMatter;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CompanyMatterMapper {
    int insertCompanyMatter(CompanyMatter companyMatter);
    int insertCompanyMatterList(List<CompanyMatter> companyMatterList);
    List<CompanyMatter> findByCompany(ReqData reqData);
    List<CompanyMatter> findByCompanyNames(ReqData reqData);
    int updateStatusByTemplateId(int templateId);
    int deleteByCompanyName(String companyName);
    int deleteLinkMan(CompanyMatter companyMatter);
    int deleteByCompanyIds(int[] ids);
    int deleteByMatterList(@Param("map") Map<String, Object> map);
    int updateStatusByCompanyName(String companyName);
    List<CompanyMatter> findByTemplateId(int templateId);
    int deleteByTemplateId(int templateId);
    int insertByTemplateId(int templateId);
    int insertByCompanyName(String companyName);

    void updateCompanyName(@Param("companyName")String companyName,@Param("sname")String sname);
}
