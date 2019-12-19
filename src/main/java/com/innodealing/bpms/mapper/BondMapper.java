package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Bond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BondMapper {
    Bond findById(int id);
    int isExist(Bond bond);
    List<Bond> findAllBond();
    List<Bond> findAll(ReqData reqData);
    Bond findByBondId(int id);
    List<Bond> findByCompanyId(int companyId);
    Bond findByBondCode(String bondCode);
    List<Bond> findOtherBond(ReqData reqData);
    int deleteByIds(int[] ids);
    int insertBond(Bond bond);
    int updateBond(Bond bond);
    List<Bond> findLinkMatterCount(int templateId);
    List<Bond> findLinkMatterPage(ReqData reqData);
    List<Bond> findLinkMatterAll(ReqData reqData);
    int updateSetBondExpire(Bond bond);
    int updateResetBondExpire(Bond bond);
    int removeBond(Bond bond);
    List<Bond> findBondInfoByCompany(String companyName);
    Bond findBondByCode(String bondCode);
    int updateBondExpire();
    int findIsExistence(String bondCode);
    Bond findBondManagerByCode(String bondCode);
    List<Bond> findBondExcel(ReqData reqData);
    List<Bond> findBondExpire();
    void updateBondExpireByCode(String code);

    int findCountByCompanyName(String companyName);

    void updateAssociationCompany(@Param("newName")String newName,@Param("sName")String sName);
}
