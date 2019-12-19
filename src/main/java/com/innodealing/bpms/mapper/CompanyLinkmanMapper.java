package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.CompanyLinkman;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CompanyLinkmanMapper {
    List<CompanyLinkman> findByCompanyId(int companyId);
    List<CompanyLinkman> findByCompanyName(String companyName);
    int insertCompanyLinkman(CompanyLinkman companyLinkman);
    int insertCompanyLinkmans(List<CompanyLinkman> companyLinkmanList);
    int updateCompanyLinkman(CompanyLinkman companyLinkman);
    int deleteCompanyLinkman(int companyId);
    int deleteAllByCompanyName(String companyName);
}
