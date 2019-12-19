package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.CompanyLinkmanMapper;
import com.innodealing.bpms.model.CompanyLinkman;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("companyLinkmanService")
public class CompanyLinkmanService {
    @Autowired
    CompanyLinkmanMapper companyLinkmanMapper;

    public List<CompanyLinkman> findByCompanyId(int companyId){
        return companyLinkmanMapper.findByCompanyId(companyId);
    }

    public List<CompanyLinkman> findByCompanyName(String companyName){
        return companyLinkmanMapper.findByCompanyName(companyName);
    }

    public int insertCompanyLinkman(CompanyLinkman companyLinkman){
        return companyLinkmanMapper.insertCompanyLinkman(companyLinkman);
    }

    public int insertCompanyLinkmans(List<CompanyLinkman> companyLinkmanList){
        return companyLinkmanMapper.insertCompanyLinkmans(companyLinkmanList);
    }

    public int updateCompanyLinkman(CompanyLinkman companyLinkman){
        return companyLinkmanMapper.updateCompanyLinkman(companyLinkman);
    }

    public int deleteCompanyLinkman(int id){
        return companyLinkmanMapper.deleteCompanyLinkman(id);
    }

    public int deleteAllByCompanyName(String companyName){
        return companyLinkmanMapper.deleteAllByCompanyName(companyName);
    }

}
