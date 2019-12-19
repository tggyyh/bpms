package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Company;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CompanyMapper {
    int isExist(Company company);
    List<Company> findAll(ReqData reqData);
    Company findById(int id);
    Company findByName(String name);
    int insertCompany(Company company);
    int updateStatusByIds(int[] ids);
    int updateCompany(Company company);
    List<Company> findLinkMatterCount(int templateId);
    List<Company> findLinkMatterAll(ReqData reqData);
    int updateStatusById(int id);
    List<Company> findCompLinkAll(ReqData reqData);
    void deleteByCompanyName(@Param("companyName") String companyName);
    Company SearchCompanyByName(@Param("companyName") String companyName);
}
