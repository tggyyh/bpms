package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.BondMatter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BondMatterMapper {
    List<BondMatter> findByBondCode(String bondCode);
    List<BondMatter> findByBondCodes(ReqData reqData);
    List<BondMatter> findBondMatterByBondCode(ReqData reqData);
    int insertBondMatter(BondMatter bondMatter);
    int insertBondMatterList(List<BondMatter> bondMatterList);
    int updateStatusByTemplateId(int templateId);
    int deleteByBondCode(String bondCode);
    int deleteByBondIds(int[] ids);
    int deleteLinkMan(BondMatter bondMatter);
    int deleteByMatterList(@Param("map") Map<String, Object> map);
    List<BondMatter> findByTemplateId(int templateId);
    int deleteByTemplateId(int templateId);
    int insertByTemplateId(int templateId);
    int insertByBondCode(String bondCode);
}
