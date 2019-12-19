package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.BankManagerMapper;
import com.innodealing.bpms.model.BankManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service("bankManagerService")
public class BankManagerService {
    @Autowired
    BankManagerMapper bankManagerMapper;

    public List<BankManager> findByBondCode(String bondCode){
        List<BankManager> bankManagerList = bankManagerMapper.findByBondCode(bondCode);
        return bankManagerList;
    }

    public int insertBankManager(BankManager bankManager){
        return bankManagerMapper.insertBankManager(bankManager);
    }

    public int insertBankManagers(List<BankManager> bankManagerList){
        return bankManagerMapper.insertBankManagers(bankManagerList);
    }

    public int updateBankManager(BankManager bankManager){
        return bankManagerMapper.updateBankManager(bankManager);
    }

    public int deleteById(int id){
        return bankManagerMapper.deleteById(id);
    }

    public int deleteByBondCode(String bondCode){
        return bankManagerMapper.deleteByBondCode(bondCode);
    }
}
