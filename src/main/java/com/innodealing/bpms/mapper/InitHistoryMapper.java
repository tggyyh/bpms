package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.BondMatter;
import com.innodealing.bpms.model.CompanyMatter;
import com.innodealing.bpms.model.CustomMatter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InitHistoryMapper {
    public List<CompanyMatter> findCompanyMatter(int id);

    List<CompanyMatter> findBondMatter(int id);

    List<String> findAllBondCodes();

    List<BondMatter> findByBondCode(String code);

    List<Integer> findByKey(String code);

    List<String> findAllCompanys();

    List<CompanyMatter> findByName(String name);
}