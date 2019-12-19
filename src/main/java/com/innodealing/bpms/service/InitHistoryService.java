package com.innodealing.bpms.service;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.BondMapper;
import com.innodealing.bpms.mapper.InitHistoryMapper;
import com.innodealing.bpms.model.Bond;
import com.innodealing.bpms.model.BondMatter;
import com.innodealing.bpms.model.CompanyMatter;
import com.innodealing.bpms.model.CustomMatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InitHistoryService {
    @Autowired
    private InitHistoryMapper initHistoryMapper;
    @Autowired
    private BondMapper bondMapper;
    public List<CompanyMatter> findCompanyMatter(int id) {
        return initHistoryMapper.findCompanyMatter(id);
    }

    public List<CompanyMatter> findBondMatter(int id) {
        return initHistoryMapper.findBondMatter(id);
    }

    public List<String> findAllBondCodes() {
        return initHistoryMapper.findAllBondCodes();
    }

    public List<Bond> findAllBonds() {
        return bondMapper.findBondExcel(new ReqData());
    }

    public List<BondMatter> findByBondCode(String code) {
        return initHistoryMapper.findByBondCode(code);
    }

    public List<Integer> findByKey(String code) {
        return initHistoryMapper.findByKey(code);
    }

    public List<String> findAllCompanys() {
        return initHistoryMapper.findAllCompanys();
    }

    public List<CompanyMatter> findByName(String name) {
        return initHistoryMapper.findByName(name);
    }
}
