package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.BondManager;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BondManagerMapper {
    List<BondManager> findByBondCode(String bondCode);
    List<BondManager> findByBondCodes(ReqData reqData);
    int insertBondManager(List<BondManager> bondManagerList);
    int deleteByBondCode(String bondCode);
}
