package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.BankManager;

import java.util.List;

public interface BankManagerMapper {
    List<BankManager> findByBondCode(String bondCode);
    int insertBankManager(BankManager bankManager);
    int insertBankManagers(List<BankManager> bankManagerList);
    int updateBankManager(BankManager bankManager);
    int deleteById(int id);
    int deleteByBondCode(String bondCode);
}
